package com.shoulaxiao.service;

import com.shoulaxiao.util.SingleResult;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:26
 **/
public interface NetworkLearnService {

    SingleResult networkLearn(CommonsMultipartFile[] files,int networkGraph) throws IOException;

    SingleResult networkModelTrain(int netGraph);

    SingleResult networkModelTest(int netGraph);

}
