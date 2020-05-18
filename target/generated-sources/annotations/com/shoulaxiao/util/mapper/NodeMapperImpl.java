package com.shoulaxiao.util.mapper;

import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.model.vo.NodeVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-05-16T22:22:37+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_241 (Oracle Corporation)"
)
@Component
public class NodeMapperImpl implements NodeMapper {

    @Override
    public NodeDO vo2do(NodeVO nodeVO) {
        if ( nodeVO == null ) {
            return null;
        }

        NodeDO nodeDO = new NodeDO();

        nodeDO.setId( nodeVO.getId() );
        nodeDO.setNodeCode( nodeVO.getNodeCode() );
        nodeDO.setDensityValue( nodeVO.getDensityValue() );
        nodeDO.setVectorValue( nodeVO.getVectorValue() );
        nodeDO.setBelongGraph( nodeVO.getBelongGraph() );

        return nodeDO;
    }

    @Override
    public NodeVO do2vo(NodeDO nodeDO) {
        if ( nodeDO == null ) {
            return null;
        }

        NodeVO nodeVO = new NodeVO();

        nodeVO.setId( nodeDO.getId() );
        nodeVO.setNodeCode( nodeDO.getNodeCode() );
        nodeVO.setDensityValue( nodeDO.getDensityValue() );
        nodeVO.setVectorValue( nodeDO.getVectorValue() );
        nodeVO.setBelongGraph( nodeDO.getBelongGraph() );

        return nodeVO;
    }

    @Override
    public List<NodeDO> vo2dos(List<NodeVO> nodeVOS) {
        if ( nodeVOS == null ) {
            return null;
        }

        List<NodeDO> list = new ArrayList<NodeDO>( nodeVOS.size() );
        for ( NodeVO nodeVO : nodeVOS ) {
            list.add( vo2do( nodeVO ) );
        }

        return list;
    }

    @Override
    public List<NodeVO> do2vos(List<NodeDO> selectByGraph) {
        if ( selectByGraph == null ) {
            return null;
        }

        List<NodeVO> list = new ArrayList<NodeVO>( selectByGraph.size() );
        for ( NodeDO nodeDO : selectByGraph ) {
            list.add( do2vo( nodeDO ) );
        }

        return list;
    }
}
