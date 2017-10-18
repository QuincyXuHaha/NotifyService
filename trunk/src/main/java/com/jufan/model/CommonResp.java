package com.jufan.model;

import java.io.Serializable;

/**
 *
 * 通用RESTResponse
 *
 * @author 李尧
 * @since  0.3.0
 */
public class CommonResp implements Serializable {

    private static final long serialVersionUID = 2186182369734130490L;

    // 0 成功， 1 失败
    private Integer code;

    private String msg;

    public CommonResp(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
