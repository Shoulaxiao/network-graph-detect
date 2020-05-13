package com.shoulaxiao.service.impl;

import com.shoulaxiao.common.NetworkConstant;
import com.shoulaxiao.service.LearnDataService;
import com.shoulaxiao.service.NetworkLearnService;
import com.shoulaxiao.util.SingleResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:27
 **/
@Service
public class NetworkLearnServiceImpl implements NetworkLearnService {


    @Resource
     private LearnDataService learnDataService;


    @Override
    public SingleResult networkLearn(CommonsMultipartFile[] files) throws IOException {

        for (CommonsMultipartFile multipartFile:files){
            String fileName=multipartFile.getOriginalFilename();
            if (fileName.contains(NetworkConstant.NODE_VECTER_SUFFIX)){
                 learnDataService.readNodeData(multipartFile.getInputStream());
            }
        }
        return null;
    }
}
