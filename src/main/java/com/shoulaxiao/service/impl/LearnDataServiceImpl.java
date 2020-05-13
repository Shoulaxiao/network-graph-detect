package com.shoulaxiao.service.impl;

import com.google.common.collect.Lists;
import com.shoulaxiao.common.Symbol;
import com.shoulaxiao.dao.EdgeDOMapper;
import com.shoulaxiao.dao.NodeDOMapper;
import com.shoulaxiao.model.EdgeDO;
import com.shoulaxiao.model.EdgeDOExample;
import com.shoulaxiao.model.NodeDO;
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

        //需要插入的节点
        List<NodeVO> nodeVOList = Lists.newArrayList();

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

                    //保存节点
                    nodeVOList.addAll(Lists.newArrayList(findNeedInsetNodes(nodes, networkGraph)));
                }
            }
            edgeDao.insertByBatch(edgeMapper.vo2dos(edgeVOList));
            nodeDao.insertByBatch(nodeMapper.vo2dos(nodeVOList));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("读取数据库时发生错误:{}", e.getMessage(), e);
        } finally {
            br.close();
            bufferedReader.close();
        }
    }

    private List<NodeVO> findNeedInsetNodes(String[] nodes, int networkGraph) {

        List<NodeVO> needInsertResult = new ArrayList<>();

        List<String> request = new ArrayList<>();
        request.add(nodes[0]);
        request.add(nodes[1]);
        Map<String, NodeDO> nodeDOMap = nodeDao.selectByNodeCodes(request).stream().collect(Collectors.toMap(NodeDO::getNodeCode, Function.identity()));
        if (!nodeDOMap.containsKey(nodes[0])) {
            needInsertResult.add(new NodeVO(nodes[0], networkGraph));
        }

        if (!nodeDOMap.containsKey(nodes[1])) {
            needInsertResult.add(new NodeVO(nodes[1], networkGraph));
        }
        return needInsertResult;
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
