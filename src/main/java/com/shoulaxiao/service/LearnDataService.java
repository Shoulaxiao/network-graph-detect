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


    /**
     * 读取网络节点响亮数据
     * @param inputStream 节点向量数据
     * @param networkGraph 网络数据源
     */
    void readNodeVectorData(InputStream inputStream, int networkGraph) throws IOException;


    /**
     * 读取标准社区划分
     * @param inputStream
     * @param i
     */
    void readStandardDivision(InputStream inputStream, int i) throws IOException;
}
