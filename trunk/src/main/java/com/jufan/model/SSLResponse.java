package com.jufan.model;

import org.apache.http.HttpResponse;

/**
 * @author 李尧
 * date: 2017/8/18
 */
public class SSLResponse {

    private String reqPacket;

    private HttpResponse response;

    public SSLResponse(String reqPacket, HttpResponse response) {
        this.reqPacket = reqPacket;
        this.response = response;
    }

    public String getReqPacket() {
        return reqPacket;
    }

    public SSLResponse setReqPacket(String reqPacket) {
        this.reqPacket = reqPacket;
        return this;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public SSLResponse setResponse(HttpResponse response) {
        this.response = response;
        return this;
    }
}
