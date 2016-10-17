/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailclient;

import java.net.InetAddress;

/**
 *
 * @author haoxuan
 */
public class DefaltLocalSender {

    private String prefix = System.getProperty("user.name");
    private String hostName;

    public DefaltLocalSender(InetAddress ia) {
        this.hostName = ia.getHostName();
    }

    @Override
    public String toString() {
        return prefix + "@" + hostName;
    }

}
