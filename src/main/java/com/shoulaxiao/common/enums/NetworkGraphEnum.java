package com.shoulaxiao.common.enums;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-13 12:44
 **/
public enum  NetworkGraphEnum {

    KARATE_GRAPH(1,"karate数据图")
    ;


    private Integer code;
    private String desc;

    NetworkGraphEnum(Integer code,String desc){
        this.code=code;
        this.desc=desc;
    }


    public static NetworkGraphEnum getEnumByCode(Integer code){
     for (NetworkGraphEnum graphEnum:NetworkGraphEnum.values()){
         if (graphEnum.code.equals(code)){
             return graphEnum;
         }
     }
     return null;
    }

    public Integer getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }

}
