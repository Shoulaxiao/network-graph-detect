package com.shoulaxiao.service.impl;

import com.shoulaxiao.common.Symbol;
import com.shoulaxiao.dao.EdgeDOMapper;
import com.shoulaxiao.model.vo.EdgeVO;
import com.shoulaxiao.model.vo.NodeVO;
import com.shoulaxiao.service.LearnDataService;
import com.shoulaxiao.util.mapper.EdgeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 读取网络数据类
 * @author: shoulaxiao
 * @create: 2020-05-12 18:48
 **/
@Service
public class LearnDataServiceImpl implements LearnDataService {

    @Resource
    private EdgeDOMapper edgeDOMapper;


    @Override
    public void readNodeData(InputStream inputStream,int networkGraph) throws IOException {

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

                    EdgeVO edgeVO = new EdgeVO(new NodeVO(nodes[0]), new NodeVO(nodes[1]),networkGraph);
                    edgeVOList.add(edgeVO);

                } else {
                    nodes = line.split(Symbol.BLANK_SPANCE);

                    EdgeVO edgeVO = new EdgeVO(new NodeVO(nodes[0]), new NodeVO(nodes[1]),networkGraph);
                    edgeVOList.add(edgeVO);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            br.close();
            bufferedReader.close();
        }
    }
}
