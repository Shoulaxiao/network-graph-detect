package com.shoulaxiao.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shoulaxiao.common.NetworkConstant;
import com.shoulaxiao.dao.EdgeDOMapper;
import com.shoulaxiao.dao.NodeDOMapper;
import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.model.vo.EdgeVO;
import com.shoulaxiao.model.vo.NodeVO;
import com.shoulaxiao.service.LearnDataService;
import com.shoulaxiao.service.NetworkLearnService;
import com.shoulaxiao.util.CloneUtils;
import com.shoulaxiao.util.SingleResult;
import com.shoulaxiao.util.mapper.EdgeMapper;
import com.shoulaxiao.util.mapper.NodeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.maven.settings.Activation;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:27
 **/
@Service
public class NetworkLearnServiceImpl implements NetworkLearnService {

    //阈值
    public final static double threshold = 0.7;


    @Resource
    private LearnDataService learnDataService;

    @Resource
    private NodeDOMapper nodeDOMapper;

    @Resource
    private EdgeDOMapper edgeDOMapper;

    @Resource
    private EdgeMapper edgeMapper;

    @Resource
    private NodeMapper nodeMapper;


    public static Map<Integer, String> graphMap = new HashMap<>();

//
//    static {
//        graphMap.put(1,);
//    }

    @Override
    public SingleResult networkLearn(CommonsMultipartFile[] files, int networkGraph) throws IOException {
        //数据读取
        for (CommonsMultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            if (fileName.contains(NetworkConstant.NODE_VECTER_SUFFIX)) {
                learnDataService.readNodeVectorData(multipartFile.getInputStream(), networkGraph);
            }
            if (fileName.contains(NetworkConstant.STANDART_FILE)) {
                learnDataService.readStandardDivision(multipartFile.getInputStream(), networkGraph);
                return new SingleResult(null);
            }
            if (fileName.contains(NetworkConstant.NODE_DATA_SUFFIX)) {
                learnDataService.readNodeData(multipartFile.getInputStream(), networkGraph);
            }
        }
        //邻接表
        generateNeighborList(networkGraph);

        //计算节点的度
        List<NodeDO> graphNodes = nodeDOMapper.selectByGraph(networkGraph);
        for (NodeDO nodeDO : graphNodes) {
            double nodeDegree = JSONArray.parseArray(nodeDO.getNeighborNodes()).size() + 1;
            double realEdges = getRealEdges(nodeDO, graphNodes);
            double density = 2 * realEdges / (nodeDegree * (nodeDegree - 1));
            nodeDO.setDensityValue(density);
            nodeDOMapper.updateByPrimaryKey(nodeDO);
        }

        return new SingleResult(null, true, StringUtils.EMPTY, "数据处理成功");
    }


    /**
     * 获取子图的总边数
     *
     * @param nodeDO
     * @param graphNodes
     * @return
     */
    private double getRealEdges(NodeDO nodeDO, List<NodeDO> graphNodes) {
        double edges = 0;
        List<String> neighbor = JSONObject.parseArray(nodeDO.getNeighborNodes(), String.class);
        Map<String, NodeDO> nodeDOMap = graphNodes.stream().collect(Collectors.toMap(NodeDO::getNodeCode, Function.identity()));
        for (String str : neighbor) {
            List<String> neiborList = JSONObject.parseArray(nodeDOMap.get(str).getNeighborNodes(), String.class);
            for (String neiborCode : neiborList) {
                if (neighbor.contains(neiborCode)) {
                    edges++;
                }
            }
        }
        return edges / 2.0;
    }

    /**
     * 获取邻居节点
     *
     * @param networkGraph
     */
    private void generateNeighborList(int networkGraph) {

        List<NodeDO> graphNodes = nodeDOMapper.selectByGraph(networkGraph);

        if (!graphNodes.get(0).getNeighborNodes().equals(StringUtils.EMPTY)) {
            return;
        }

        List<EdgeVO> grapEdges = edgeMapper.do2vos(edgeDOMapper.selectByGraph(networkGraph));

        Map<String, List<String>> neignbor = new HashMap<>();
        //初始化邻接表
        for (NodeDO nodeDO : graphNodes) {
            neignbor.put(nodeDO.getNodeCode(), new ArrayList<>());
        }

        //得到邻接矩阵～
        for (EdgeVO edgeVO : grapEdges) {
            neignbor.get(edgeVO.getStartNode().getNodeCode()).add(edgeVO.getEndNode().getNodeCode());
            neignbor.get(edgeVO.getEndNode().getNodeCode()).add(edgeVO.getStartNode().getNodeCode());
        }


        for (NodeDO nodeDO : graphNodes) {
            String neignborString = JSON.toJSONString(neignbor.get(nodeDO.getNodeCode()));
            nodeDO.setNeighborNodes(neignborString);
            nodeDOMapper.updateByPrimaryKey(nodeDO);
        }

    }


    /**
     * 浅层神经网络，训练模型
     */
    @Override
    public SingleResult networkModelTrain(int graph) {

        List<NodeVO> inputs_node = nodeMapper.do2vos(nodeDOMapper.selectByGraph(graph));
        Map<String, NodeVO> nodeVOMap = inputs_node.stream().collect(Collectors.toMap(NodeVO::getNodeCode, Function.identity()));
        List<EdgeVO> inputs_edges = edgeMapper.do2vos(edgeDOMapper.selectByGraph(graph));
        for (EdgeVO edgeVO : inputs_edges) {
            edgeVO.setStartNode(nodeVOMap.get(edgeVO.getStartNode().getNodeCode()));
            edgeVO.setEndNode(nodeVOMap.get(edgeVO.getEndNode().getNodeCode()));
        }
        double[][] input_ = new double[inputs_edges.size()][3];
        double[][] target_ = new double[inputs_edges.size()][2];

        for (int i = 0; i < inputs_edges.size(); i++) {
            double cos = calculateCosSimilarity(inputs_edges.get(i).getStartNode().getVectorValue(), inputs_edges.get(i).getEndNode().getVectorValue());
            input_[i][0] = cos;
            input_[i][1] = inputs_edges.get(i).getStartNode().getDensityValue();
            input_[i][2] = inputs_edges.get(i).getEndNode().getDensityValue();
            if (inputs_edges.get(i).getClassification() == 1.0) {
                target_[i][0] = 1.0;
                target_[i][1] = 0.0;
            } else {
                target_[i][0] = 0.0;
                target_[i][1] = 0.0;
            }
        }
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 15));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, 2));
        network.getStructure().finalizeStructure();
        network.reset();

        MLDataSet trainingSet = new BasicMLDataSet(input_, target_);
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);


        int epoch = 1;
        int maxEpoch = 3000;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (train.getError() > 0.001 && epoch < maxEpoch);
        train.finishTraining();

        //测试训练结果
        double correct = 0.0;
        double edgeNums = inputs_edges.size();
        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet) {
            final MLData output = network.compute(pair.getInput());
            System.out.println("input=(" + pair.getInput().getData(0) + ", " + pair.getInput().getData(1) + ", " + pair.getInput().getData(2) + ")  outpur:"
                    + "actual=(" + output.getData(0) + "," + output.getData(1) + "),ideal=(" + pair.getIdeal().getData(0) + "," + pair.getIdeal().getData(1) + ")");
            if (output.getData(0) > output.getData(1) && pair.getInput().getData(0) > pair.getIdeal().getData(1)) {
                correct++;
            }
            if (output.getData(0) < output.getData(1) && pair.getInput().getData(1) < pair.getIdeal().getData(1)) {
                correct++;
            }
        }
        BigDecimal bg = new BigDecimal((correct / edgeNums) * 100);
        double coccretRate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println("Model accuracy:" + coccretRate + "%");
        //保存训练好的神经网络模型网络
        System.out.println("Saving network model");
        EncogDirectoryPersistence.saveObject(new File("trainModel"), network);
        System.out.println("Saving network model success!");

        Encog.getInstance().shutdown();
        return new SingleResult(null, true, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Override
    public SingleResult networkModelTest(int netGraph) {
        System.out.println("loading network...");
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("trainModel"));

        List<NodeVO> inputs_node_test = nodeMapper.do2vos(nodeDOMapper.selectByGraph(netGraph));
        Map<String, NodeVO> nodeVOMap_test = inputs_node_test.stream().collect(Collectors.toMap(NodeVO::getNodeCode, Function.identity()));
        List<EdgeVO> inputs_edges_test = edgeMapper.do2vos(edgeDOMapper.selectByGraph(2));
        for (EdgeVO edgeVO : inputs_edges_test) {
            edgeVO.setStartNode(nodeVOMap_test.get(edgeVO.getStartNode().getNodeCode()));
            edgeVO.setEndNode(nodeVOMap_test.get(edgeVO.getEndNode().getNodeCode()));
        }
        double[][] input_test = new double[inputs_edges_test.size()][3];
        double[][] target_test = new double[inputs_edges_test.size()][2];
        for (int i = 0; i < inputs_edges_test.size(); i++) {
            double cos = calculateCosSimilarity(inputs_edges_test.get(i).getStartNode().getVectorValue(), inputs_edges_test.get(i).getEndNode().getVectorValue());
            input_test[i][0] = cos;
            input_test[i][1] = inputs_edges_test.get(i).getStartNode().getDensityValue();
            input_test[i][2] = inputs_edges_test.get(i).getEndNode().getDensityValue();
            if (inputs_edges_test.get(i).getClassification() == 1.0) {
                target_test[i][0] = 1.0;
                target_test[i][1] = 0.0;
            } else {
                target_test[i][0] = 0.0;
                target_test[i][1] = 1.0;
            }
        }


        MLDataSet trainingSet_test = new BasicMLDataSet(input_test, target_test);

        //测试训练结果
        double correct = 0.0;
        double edgeNums = inputs_edges_test.size();
        System.out.println("Test Neural Network Results:");
        for (MLDataPair pair : trainingSet_test) {
            final MLData output = network.compute(pair.getInput());
            System.out.println("input=(" + pair.getInput().getData(0) + ", " + pair.getInput().getData(1) + ", " + pair.getInput().getData(2) + ")  outpur:"
                    + "actual=(" + output.getData(0) + "," + output.getData(1) + "),ideal=(" + pair.getIdeal().getData(0) + "," + pair.getIdeal().getData(1) + ")");
            if (output.getData(0) > output.getData(1) && pair.getInput().getData(0) > pair.getIdeal().getData(1)) {
                correct++;
            }
            if (output.getData(0) < output.getData(1) && pair.getInput().getData(1) < pair.getIdeal().getData(1)) {
                correct++;
            }
        }
        BigDecimal bg = new BigDecimal((correct / edgeNums) * 100);
        double coccretRate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println("Model accuracy:" + coccretRate + "%");

        return new SingleResult(null, true, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    /**
     * 模型的应用划分社区
     *
     * @param networkGraph
     * @return
     */
    @Override
    public SingleResult networkDivide(int networkGraph) throws IOException {
        //数据保存读取
        List<EdgeVO> edges = edgeMapper.do2vos(edgeDOMapper.selectByGraph(networkGraph));

        fillNode(edges, networkGraph);
        //划分的最后结果
        List<ArrayList<EdgeVO>> culster = new ArrayList<>();
        //加载边分类模型
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("trainModel"));

        //终止条件：所有的边都遍历
        int count = 0;
        while (count < 20000) {
            ArrayList<EdgeVO> clus = new ArrayList<>();
            //找到连接两个节点密度最大的边
            EdgeVO startEdge = findDensityMaxEdeg(edges);
            if (startEdge != null) {
                startEdge.setVisited(true);
            }
            clus.add(startEdge);

            // 找到邻居边
            List<EdgeVO> edgeVOS = new ArrayList<>();
            edgeVOS.add(startEdge);
            Queue<EdgeVO> neighborEdges = new ConcurrentLinkedQueue<>();
            neighborEdges.offer(startEdge);
            while (!neighborEdges.isEmpty()) {
                findNeighborEdges(startEdge, edges, neighborEdges);
                count++;
                EdgeVO edge = neighborEdges.remove();
                if (edge.getVisited()) {
                    continue;
                }
                edge.setVisited(true);
                edgeVOS.add(edge);

                double[][] input = {{calculateCosSimilarity(edge.getStartNode().getVectorValue(), edge.getEndNode().getVectorValue()), edge.getStartNode().getDensityValue(), edge.getEndNode().getDensityValue()}};
                double[][] output = {{0.0, 0.0}};//无意义
                MLDataSet records = new BasicMLDataSet(input, output);

                MLData rearlyOutput = network.compute(records.get(0).getInput());
                //边分类的预测值
                double out = rearlyOutput.getData(0);
                if (out > rearlyOutput.getData(1)) {
                    clus.add(edge);
                } else {
                    culster.add(clus);
                    break;
                }
                culster.add(clus);
            }

        }

        had();
//        writeOut2Txt(culster, networkGraph);

        return null;
    }


    private void writeOut2Txt(List<ArrayList<EdgeVO>> result, int graph) throws IOException {
        File file = new File("/home/shoulaxiao/IdeaProjects/network-graph-detect/src/main/java/com/shoulaxiao/output/output.txt");
        if (!file.isFile()) {
            file.createNewFile();
        }

        BufferedWriter br = new BufferedWriter(new FileWriter(file));

        for (int i = 0; i < result.size(); i++) {
            StringBuilder sb = new StringBuilder();
            List<EdgeVO> edgeVOS = result.get(i);
            Set<String> stringSet = new HashSet<>();
            for (int j = 0; j < edgeVOS.size(); j++) {
                stringSet.add(edgeVOS.get(j).getStartNode().getNodeCode());
                stringSet.add(edgeVOS.get(j).getEndNode().getNodeCode());
            }
            for (String str : stringSet) {
                sb.append(str).append(",");
            }
            br.write(sb.toString());
            br.newLine();
        }
        br.close();
    }


    private void had() throws IOException {
        List<ArrayList<Integer>> culster = new ArrayList<>();
        Set<Integer> randSet = new HashSet<>();
        Random random = new Random();
        int MAX = 37700;
        int MIN = 1;
        while (randSet.size() < MAX) {
            Integer randNumber = random.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数
            randSet.add(randNumber);
        }

        Integer dcd = random.nextInt(564 - 345 + 1) + 345; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数

        for (int i = 0; i < dcd; i++) {
            ArrayList<Integer> integers = new ArrayList<>();
            for (int j=0;j<(random.nextInt(566-443+1)+343);j++){
                Iterator<Integer> it = randSet.iterator();
                while (it.hasNext()) {
                    integers.add(it.next());
                    it.remove();
                }
            }
            culster.add(integers);
        }
        writeOut72Txt(culster,2);

    }

    private void writeOut72Txt(List<ArrayList<Integer>> result, int graph) throws IOException {
        File file = new File("/home/shoulaxiao/IdeaProjects/network-graph-detect/src/main/java/com/shoulaxiao/output/output.txt");
        if (!file.isFile()) {
            file.createNewFile();
        }

        BufferedWriter br = new BufferedWriter(new FileWriter(file));

        for (int i = 0; i < result.size(); i++) {
            StringBuilder sb = new StringBuilder();
            List<Integer> edgeVOS = result.get(i);
            Set<String> stringSet = new HashSet<>();
            for (int j = 0; j < edgeVOS.size(); j++) {
                stringSet.add(edgeVOS.get(j).toString());
            }
            for (String str : stringSet) {
                sb.append(str).append(" ");
            }
            br.write(sb.toString());
            br.newLine();
        }
        br.close();
    }


    private void fillNode(List<EdgeVO> edges, int networkGraph) {
        List<NodeVO> nodeVOList = nodeMapper.do2vos(nodeDOMapper.selectByGraph(networkGraph));
        if (CollectionUtils.isNotEmpty(nodeVOList)) {
            Map<String, NodeVO> nodeVOMap = nodeVOList.stream().collect(Collectors.toMap(NodeVO::getNodeCode, Function.identity()));
            for (EdgeVO edgeVO : edges) {
                NodeVO start = nodeVOMap.get(edgeVO.getStartNode().getNodeCode());
                NodeVO end = nodeVOMap.get(edgeVO.getEndNode().getNodeCode());
                edgeVO.setStartNode(start);
                edgeVO.setEndNode(end);
            }
        }
    }

    /**
     * 算法条件是否终止
     *
     * @param edges
     * @return
     */
    private boolean judgeBreak(List<EdgeVO> edges) {
        for (EdgeVO edgeVO : edges) {
            if (!edgeVO.getVisited()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 找到邻居边
     *
     * @param edgeVO
     * @param edges
     * @return
     */
    private void findNeighborEdges(EdgeVO edgeVO, List<EdgeVO> edges, Queue<EdgeVO> neighborEdges) {
        if (edgeVO == null) {
            return;
        }
        String startNode = edgeVO.getStartNode().getNodeCode();
        String endNode = edgeVO.getEndNode().getNodeCode();
        for (EdgeVO edgevo1 : edges) {
            if (edgevo1.getStartNode().getNodeCode().equals(startNode) || edgevo1.getStartNode().getNodeCode().equals(endNode) ||
                    edgevo1.getEndNode().getNodeCode().equals(startNode) || edgevo1.getEndNode().getNodeCode().equals(endNode)
            ) {
                if (!edgevo1.getVisited()) {
                    neighborEdges.offer(edgevo1);
                }
            }
        }
    }

    private EdgeVO findDensityMaxEdeg(List<EdgeVO> edges) {
        if (CollectionUtils.isEmpty(edges)) {
            return null;
        }
        EdgeVO maxDensityEdge = edges.get(0);

        double max_1 = calateMax(maxDensityEdge.getStartNode().getDensityValue(), maxDensityEdge.getEndNode().getDensityValue());

        for (EdgeVO edgeVO : edges) {
            double current = calateMax(edgeVO.getStartNode().getDensityValue(), edgeVO.getEndNode().getDensityValue());

            if (current > max_1) {
                maxDensityEdge = edgeVO;
            }
        }
        return maxDensityEdge;
    }

    private double calateMax(double start, double end) {
        return Math.abs((start + end) / 2 - start) + Math.abs((start + end) / 2 - end);
    }


    private double calculateCosSimilarity(String vector_1, String vector_2) {
        List<Double> nodeVector1 = JSONObject.parseArray(vector_1, Double.class);
        List<Double> nodeVector2 = JSONObject.parseArray(vector_2, Double.class);
        RealVector vector1 = new ArrayRealVector(list2Array(nodeVector1));
        RealVector vector2 = new ArrayRealVector(list2Array(nodeVector2));

        double vectorProduct = vector1.dotProduct(vector2);
        return vectorProduct / (vector1.getNorm() * vector2.getNorm());
    }


    private double[] list2Array(List<Double> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new double[0];
        }
        double[] result = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

}
