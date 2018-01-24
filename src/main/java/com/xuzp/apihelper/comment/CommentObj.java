package com.xuzp.apihelper.comment;

/**
 * @author za-xuzhiping
 * @Date 2018/1/22
 * @Time 17:22
 */
public class CommentObj {

    private String shortComment = "";

    private String wholeComment = "";

    public CommentObj(){

    }

    public CommentObj(String shortComment, String wholeComment) {
        this.shortComment = shortComment;
        this.wholeComment = wholeComment;
    }

    public String getShortComment() {
        return shortComment;
    }

    public void setShortComment(String shortComment) {
        this.shortComment = shortComment;
    }

    public String getWholeComment() {
        return wholeComment;
    }

    public void setWholeComment(String wholeComment) {
        this.wholeComment = wholeComment;
    }

    @Override
    public String toString(){
        return String.format("[short: %s, whole: %s]", shortComment, wholeComment);
    }
}
