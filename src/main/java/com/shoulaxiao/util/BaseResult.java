package com.shoulaxiao.util;

import java.io.Serializable;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:21
 **/
public class BaseResult implements Serializable {
    private static final long serialVersionUID = -7691308396055689995L;
    private boolean success;
    private String errorCode;
    private String errorMessage;

    public BaseResult() {
        this.success = false;
        this.success = true;
    }

    public BaseResult(String errorCode, String errorMessage) {
        this(false, errorCode, errorMessage);
    }

    public BaseResult(boolean success, String errorCode, String errorMessage) {
        this.success = false;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.success).append(",");
        sb.append(this.errorCode).append(",");
        sb.append(this.errorMessage);
        return sb.toString();
    }
}
