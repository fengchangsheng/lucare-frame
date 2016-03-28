package com.lucare.tsession;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class TSession extends TCache {
    private String sid;
    private int userid;
    private long loginTime;
    private String userAgent;

    public TSession() {
    }

    public TSession(String sid, int userid, long loginTime, String userAgent) {
        this.sid = sid;
        this.userid = userid;
        this.loginTime = loginTime;
        this.userAgent = userAgent;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getUserid() {
        return this.userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public long getLoginTime() {
        return this.loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
