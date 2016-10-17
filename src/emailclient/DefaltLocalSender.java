/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailclient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haoxuan
 */
public class DefaltLocalSender {

    private String prefix = System.getProperty("user.name");
    private String hostName;

    public DefaltLocalSender() {

        try {
            this.hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return prefix + "@" + hostName;
    }

}
