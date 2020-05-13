package com.shoulaxiao.util;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:22
 **/
public class SingleResult<T> extends BaseResult {
    private static final long serialVersionUID = 4101179590769575819L;
    private T data;

    public SingleResult() {
    }

    public SingleResult(T data) {
        this.data = data;
    }

    public SingleResult(T data, String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
        this.data = data;
    }

    public SingleResult(T data, boolean success, String errorCode, String errorMessage) {
        super(success, errorCode, errorMessage);
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        String str = super.toString();
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(",").append(this.data);
        return sb.toString();
    }
}