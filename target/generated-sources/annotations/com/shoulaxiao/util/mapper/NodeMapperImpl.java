package com.shoulaxiao.util.mapper;

import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.model.vo.NodeVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-05-13T12:42:29+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_241 (Oracle Corporation)"
)
public class NodeMapperImpl implements NodeMapper {

    @Override
    public NodeDO vo2do(NodeVO nodeVO) {
        if ( nodeVO == null ) {
            return null;
        }

        NodeDO nodeDO = new NodeDO();

        return nodeDO;
    }

    @Override
    public NodeVO do2vo(NodeDO nodeDO) {
        if ( nodeDO == null ) {
            return null;
        }

        NodeVO nodeVO = new NodeVO();

        return nodeVO;
    }
}
