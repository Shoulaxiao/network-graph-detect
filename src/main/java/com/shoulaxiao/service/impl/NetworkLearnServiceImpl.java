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
import com.shoulaxiao.util.SingleResult;
import com.shoulaxiao.util.mapper.EdgeMapper;
import com.shoulaxiao.util.mapper.NodeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
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
import java.io.File;
import java.io.IOException;
import java.util.*;
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
                target_[i][1] = 1.0;
            }
        }
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 15));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));
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
        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet) {
            final MLData output = network.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) + "," + pair.getInput().getData(2)
                    + ":actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }
        //保存训练好的神经网络模型网络
        System.out.println("Saving network");
        EncogDirectoryPersistence.saveObject(new File("trainModel"), network);
        Encog.getInstance().shutdown();
        return new SingleResult(null, true, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Override
    public SingleResult networkModelTest(int netGraph) {
        System.out.println("loading network...");
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("trainModel"));

        List<NodeVO> inputs_node = nodeMapper.do2vos(nodeDOMapper.selectByGraph(netGraph));
        Map<String, NodeVO> nodeVOMap = inputs_node.stream().collect(Collectors.toMap(NodeVO::getNodeCode, Function.identity()));
        List<EdgeVO> inputs_edges = edgeMapper.do2vos(edgeDOMapper.selectByGraph(netGraph));
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
                target_[i][1] = 1.0;
            }
        }

//        MLDataSet testSet = new BasicMLDataSet(input);
//        double e=network.calculateError(testSet);

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
        //划分的最后结果
        List<List<EdgeVO>> culster = new ArrayList<>();
        //加载边分类模型
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("trainModel"));

        //终止条件：所有的边都遍历
        while (judgeBreak(edges)) {
            List<EdgeVO> clus = new ArrayList<>();
            //找到连接两个节点密度最大的边
            EdgeVO startEdge = findDensityMaxEdeg(edges);
            clus.add(startEdge);
            //设置已经遍历过
            startEdge.setVisited(true);

            // 找到邻居边
            Queue<EdgeVO> neighborEdges = findNeighborEdges(startEdge, edges);

            EdgeVO edge = neighborEdges.peek();
            edge.setVisited(true);
            double[][] input = {{calculateCosSimilarity(edge.getStartNode().getVectorValue(), edge.getEndNode().getVectorValue()), edge.getStartNode().getDensityValue(), edge.getEndNode().getDensityValue()}};
            double[][] output = {{0.0, 0.0}};//无意义
            MLDataSet records = new BasicMLDataSet(input, output);

            MLData rearlyOutput = network.compute(records.get(0).getInput());
            //边分类的预测值
            double out = rearlyOutput.getData(0);
            if (out > threshold) {
                clus.add(edge);
                neighborEdges.offer(edge);
            } else {
                culster.add(clus);
                break;
            }
        }

        return null;
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
    private Queue<EdgeVO> findNeighborEdges(EdgeVO edgeVO, List<EdgeVO> edges) {
        Queue<EdgeVO> neihbors = new LinkedList<EdgeVO>();
        String startNode = edgeVO.getStartNode().getNodeCode();
        String endNode = edgeVO.getEndNode().getNodeCode();
        for (EdgeVO edgeVO1 : edges) {
            if (edgeVO1.getStartNode().getNodeCode().equals(startNode) || edgeVO1.getStartNode().getNodeCode().equals(endNode) ||
                    edgeVO1.getEndNode().getNodeCode().equals(startNode) || edgeVO1.getEndNode().getNodeCode().equals(endNode)
            ) {
                neihbors.offer(edgeVO1);
            }
        }
        return neihbors;
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
