package com.xuzp.apihelper.comment;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */

public class CommentPair {
    private String key;
    private CommentObj comment;

    public CommentPair() {

    }

    public CommentPair(String key, CommentObj comment) {
        this.key = key;
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CommentObj getComment() {
        return comment;
    }

    public void setComment(CommentObj comment) {
        this.comment = comment;
    }
}