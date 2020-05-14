package com.shoulaxiao.service.impl;

import com.shoulaxiao.common.NetworkConstant;
import com.shoulaxiao.dao.EdgeDOMapper;
import com.shoulaxiao.dao.NodeDOMapper;
import com.shoulaxiao.model.EdgeDO;
import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.service.LearnDataService;
import com.shoulaxiao.service.NetworkLearnService;
import com.shoulaxiao.util.SingleResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

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


    @Override
    public SingleResult networkLearn(CommonsMultipartFile[] files) throws IOException {
        //数据读取
        for (CommonsMultipartFile multipartFile:files){
            String fileName=multipartFile.getOriginalFilename();
            if (fileName.contains(NetworkConstant.NODE_VECTER_SUFFIX)){
               learnDataService.readNodeVectorData(multipartFile.getInputStream(),1);
            }
            if(fileName.contains(NetworkConstant.NODE_DATA_SUFFIX)){
                learnDataService.readNodeData(multipartFile.getInputStream(),1);
            }

            if (fileName.contains(NetworkConstant.STANDART_FILE)){
                learnDataService.readStandardDivision(multipartFile.getInputStream(),1);
            }
        }


        //计算节点的度
        List<NodeDO> graphNodes=nodeDOMapper.selectByGraph(1);
        List<EdgeDO> grapEdges=edgeDOMapper.selectByGraph(1);

        return null;
    }
}
