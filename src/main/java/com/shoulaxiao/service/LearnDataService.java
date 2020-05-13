package com.shoulaxiao.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:31
 **/
public interface LearnDataService {

    /**
     * 读取网络节点原始数据
     * @param inputStream
     * @param networkGraph 网络数据源
     */
    void readNodeData(InputStream inputStream,int networkGraph) throws IOException;


}
