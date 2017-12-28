package com.xuzp.apihelper.comment;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */

public class CommentPair {
    private String key;
    private String commnent;

    public CommentPair(){

    }

    public CommentPair(String key, String comment) {
        this.key = key;
        this.commnent = comment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCommnent() {
        return commnent;
    }

    public void setCommnent(String commnent) {
        this.commnent = commnent;
    }
}