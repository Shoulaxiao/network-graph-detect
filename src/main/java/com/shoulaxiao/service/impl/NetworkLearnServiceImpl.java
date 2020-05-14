package com.shoulaxiao.service.impl;

import com.alibaba.druid.support.json.JSONParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shoulaxiao.common.NetworkConstant;
import com.shoulaxiao.dao.EdgeDOMapper;
import com.shoulaxiao.dao.NodeDOMapper;
import com.shoulaxiao.model.EdgeDO;
import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.model.vo.EdgeVO;
import com.shoulaxiao.service.LearnDataService;
import com.shoulaxiao.service.NetworkLearnService;
import com.shoulaxiao.util.SingleResult;
import com.shoulaxiao.util.mapper.EdgeMapper;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.RegEx;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:27
 **/
@Service
public class NetworkLearnServiceImpl implements NetworkLearnService {


    @Resource
    private LearnDataService learnDataService;

    @Resource
    private NodeDOMapper nodeDOMapper;

    @Resource
    private EdgeDOMapper edgeDOMapper;

    @Resource
    private EdgeMapper edgeMapper;


    @Override
    public SingleResult networkLearn(CommonsMultipartFile[] files, int networkGraph) throws IOException {
        //数据读取
        for (CommonsMultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            if (fileName.contains(NetworkConstant.NODE_VECTER_SUFFIX)) {
                learnDataService.readNodeVectorData(multipartFile.getInputStream(), networkGraph);
            }
            if (fileName.contains(NetworkConstant.NODE_DATA_SUFFIX)) {
                learnDataService.readNodeData(multipartFile.getInputStream(), networkGraph);
            }

            if (fileName.contains(NetworkConstant.STANDART_FILE)) {
                learnDataService.readStandardDivision(multipartFile.getInputStream(), networkGraph);
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
}
