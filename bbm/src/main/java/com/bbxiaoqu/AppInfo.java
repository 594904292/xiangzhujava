package com.bbxiaoqu;

/**
 * Created by dzy on 2016/9/22.
 */

public class AppInfo {

    private String xmpphost;

    public String getXmpphost() {
        return xmpphost;
    }

    public void setXmpphost(String xmpphost) {
        this.xmpphost = xmpphost;
    }

    public int getXmppport() {
        return xmppport;
    }

    public void setXmppport(int xmppport) {
        this.xmppport = xmppport;
    }

    public String getXmppdomain() {
        return xmppdomain;
    }

    public void setXmppdomain(String xmppdomain) {
        this.xmppdomain = xmppdomain;
    }

    private int xmppport;
    private String xmppdomain;
}
