package com.shoulaxiao.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.shoulaxiao.common.Symbol;
import com.shoulaxiao.dao.EdgeDOMapper;
import com.shoulaxiao.dao.NodeDOMapper;
import com.shoulaxiao.model.EdgeDO;
import com.shoulaxiao.model.EdgeDOExample;
import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.model.NodeDOExample;
import com.shoulaxiao.model.vo.EdgeVO;
import com.shoulaxiao.model.vo.NodeVO;
import com.shoulaxiao.service.LearnDataService;
import com.shoulaxiao.util.mapper.EdgeMapper;
import com.shoulaxiao.util.mapper.NodeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 读取网络数据类
 * @author: shoulaxiao
 * @create: 2020-05-12 18:48
 **/
@Service
public class LearnDataServiceImpl implements LearnDataService {

    private static Logger logger = LoggerFactory.getLogger(LearnDataServiceImpl.class);

    @Resource
    private EdgeDOMapper edgeDao;

    @Resource
    private EdgeMapper edgeMapper;

    @Resource
    private NodeMapper nodeMapper;

    @Resource
    private NodeDOMapper nodeDao;

    @Override
    public void readNodeData(InputStream inputStream, int networkGraph) throws IOException {

        //需要保存的边
        List<EdgeVO> edgeVOList = new ArrayList<>();


        InputStreamReader bufferedReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        BufferedReader br = new BufferedReader(bufferedReader);

        String line;
        String[] nodes;

        try {
            while ((line = br.readLine()) != null) {
                //如果文件数据是以逗号隔开
                if (line.contains(Symbol.COMMA)) {
                    nodes = line.split(Symbol.COMMA);
                } else {
                    nodes = line.split(Symbol.BLANK_SPANCE);
                }

                EdgeDOExample query = buildFindRequest(nodes, networkGraph);

                List<EdgeDO> responseDB = edgeDao.selectByExample(query);
                if (CollectionUtils.isEmpty(responseDB)) {
                    EdgeVO edgeVO = new EdgeVO(new NodeVO(nodes[0]), new NodeVO(nodes[1]), networkGraph);
                    edgeVOList.add(edgeVO);

                }
                //保存节点
                findNeedInsetNodes(nodes, networkGraph);
            }
            if (CollectionUtils.isNotEmpty(edgeVOList)){
                edgeDao.insertByBatch(edgeMapper.vo2dos(edgeVOList));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("读取数据库时发生错误:{}", e.getMessage(), e);
        } finally {
            br.close();
            bufferedReader.close();
        }
    }

    @Override
    public void readNodeVectorData(InputStream inputStream, int networkGraph) throws IOException {

        InputStreamReader bufferedReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        BufferedReader br = new BufferedReader(bufferedReader);

        String line;
        String[] record;

        try {
            while ((line=br.readLine())!=null){
                record=line.split(Symbol.BLANK_SPANCE);

                NodeDOExample example=new NodeDOExample();
                NodeDOExample.Criteria criteria=example.createCriteria();
                criteria.andBelongGraphEqualTo(networkGraph);
                criteria.andNodeCodeEqualTo(record[0]);

                List<NodeDO> result=nodeDao.selectByExample(example);

                if (CollectionUtils.isNotEmpty(result)){
                    NodeDO nodeDO=result.get(0);
                    nodeDO.setVectorValue(getVectorValue(record));

                    //更新操作
                    nodeDao.updateByPrimaryKey(nodeDO);
                }
            }
        }catch (Exception e){
            logger.error("读取节点向量文件出错:{}",e.getMessage(),e);
        }finally {
            br.close();
            bufferedReader.close();
        }
    }

    @Override
    public void readStandardDivision(InputStream inputStream, int networkGraph) throws IOException {
        InputStreamReader bufferedReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        BufferedReader br = new BufferedReader(bufferedReader);

        String line;
        List<EdgeDO> edgeDOList=edgeDao.selectByGraph(networkGraph);

        Map<String,EdgeVO> edgeDOMap=edgeMapper.do2vos(edgeDOList).stream().collect(Collectors.toMap(EdgeVO::getBizKey,Function.identity()));

        while ((line=br.readLine())!=null){
           String[] communities=line.split(Symbol.SEMICOLON);
           for (int i=0;i<communities.length;i++){

               for (Map.Entry<String,EdgeVO> edgeVOEntry:edgeDOMap.entrySet()){
                   EdgeVO edgeVO=edgeVOEntry.getValue();
                   if (belongSameCommunity(communities[i],edgeVOEntry.getKey())){
                       edgeVO.setClassification(1.0);
                   }else {
                       edgeVO.setClassification(0.0);
                   }
                   edgeDao.updateByPrimaryKey(edgeMapper.vo2do(edgeVO));
               }
           }
        }
    }


    /**
     * 是否属于同一个社区
     * @param community
     * @param bizKey
     * @return
     */
    private boolean belongSameCommunity(String community,String bizKey){
        String[] nodes=bizKey.split(Symbol.HORIZONTAL_LINE);
        if (community.contains(nodes[0])&&community.contains(nodes[1])){
            return true;
        }
        return false;
    }

    /**
     * 得到节点向量
     * @param record
     * @return
     */
    private String getVectorValue(String[] record) {
        List<String> vector=Lists.newArrayList();

        for (int i=1;i<record.length;i++){
            vector.add(record[i]);
        }

        return JSON.toJSONString(vector);
    }


    private void findNeedInsetNodes(String[] nodes, int networkGraph) {

        List<NodeVO> needInsertResult = new ArrayList<>();

        List<String> stringList = new ArrayList<>();
        stringList.add(nodes[0]);
        stringList.add(nodes[1]);
        Map<String,Object> request=new HashMap<>();

        request.put("list",stringList);
        request.put("graph",networkGraph);
        Map<String, NodeDO> nodeDOMap = nodeDao.selectByNodeCodes(request).stream().collect(Collectors.toMap(NodeDO::getNodeCode, Function.identity()));
        if (!nodeDOMap.containsKey(nodes[0])) {
            needInsertResult.add(new NodeVO(nodes[0], networkGraph));
        }

        if (!nodeDOMap.containsKey(nodes[1])) {
            needInsertResult.add(new NodeVO(nodes[1], networkGraph));
        }
        if (CollectionUtils.isNotEmpty(needInsertResult)){
            nodeDao.insertByBatch(nodeMapper.vo2dos(needInsertResult));
        }
    }

    private EdgeDOExample buildFindRequest(String[] nodes, int networkGraph) {

        EdgeDOExample query = new EdgeDOExample();

        EdgeDOExample.Criteria criteria = query.createCriteria();
        criteria.andStartNodeEqualTo(nodes[0]);
        criteria.andEndNodeEqualTo(nodes[1]);
        criteria.andBelongGraphEqualTo(networkGraph);
        return query;
    }


}
