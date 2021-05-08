package com.atguigu.springboot.util;

import java.io.Serializable;

/**
 * @author cjhirom
 * @create 2021-05-06 9:58
 */
public class Result implements Serializable {
    //使用code来表示更好，如返回一个404表示资源未找到；返回500表示服务器内部错误
    private boolean status ; //响应状态  true  false，即发起的请求成功还是失败

    private String msg ;  // 响应信息，即true代表什么，false代表什么
    private Object data ;  //处理成功的响应数据，即可能返回的是对象或int值，如查询返回的结果是bean对象，插入返回的结果是条目数

    public static Result ok(boolean status, String msg, Object data){
        Result result = new Result();
        result.setStatus(status);
        result.setMsg(msg);
        result.setData(data);
        return  result ;
    }

    public static Result error(boolean status, String msg){
        Result result = new Result();
        result.setStatus(false);
        result.setMsg(msg);
        result.setData(null);
        return  result ;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
