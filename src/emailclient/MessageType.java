/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailclient;

/**
 *
 * @author haoxuan
 */
public enum MessageType {
    TXT("text/plain"), HTML("application/html"), XHTML("application/xhtml+xml"), GIF("image/gif"), JPG("image/jpeg"),
    PNG("image/png"), MPEG("video/mpeg"), GENER("application/octet-stream"), PDF("application/pdf"), WORD("application/msword"), RFC("message/rfc822"),
    MUTI("mutipart/mixed"), MUTA("mutipart/alternative");
    private String typeName;

    @Override
    public String toString() {
        return typeName;
    }

    private MessageType(String typeName) {
        this.typeName = typeName;
    }
}
