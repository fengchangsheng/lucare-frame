package com.lucare.invoke;

/**
 * Created by Lucare.Feng on 2016/3/26.
 */
public class InvokeHolder {

    private String id;
    private int status = 200;
    private String desc;
    private Object result;

    public InvokeHolder(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.status = 500;
        this.desc = desc;
    }

    public void setDesc(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isOk() {
        return this.status == 200 && this.desc == null;
    }
}
