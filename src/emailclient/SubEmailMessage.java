/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Encoder;

/**
 *
 * @author haoxuan
 */
public class SubEmailMessage {

    private final String type;

    private final String encoding;

    private String subEmailMessage;
    public static final String boundary = "----=frontier";
    private static final String CRLF = "\r\n";

    public SubEmailMessage(File file) {
        this(encodeAttach(file), getMessageType(file).toString(), file.getName(), EncodingType.BASE64.toString());
    }

    public SubEmailMessage(String partBody, String type, String fileName, String encoding) {
        this.type = type;
        this.encoding = encoding;
        subEmailMessage = ("--" + boundary + CRLF);
        if (fileName != null) {
            subEmailMessage += "Content-Type: " + type + ";name=\"" + fileName + "\"" + CRLF;
        } else {
            subEmailMessage += "Content-Type: " + type + CRLF;
        }
        subEmailMessage += "Content-Transfer-Encoding: " + encoding + CRLF;

        subEmailMessage += CRLF;
        subEmailMessage += (partBody + CRLF);
    }

    public String getType() {
        return type;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getSubEmailMessage() {
        return subEmailMessage + "--" + boundary + "--";
    }

    public static String encodeAttach(File file) {
        byte[] data = null;
        try {
            InputStream isr = new FileInputStream(file.getAbsolutePath());
            data = new byte[isr.available()];
            isr.read(data);
            isr.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return (file.getName() + "File Not Found");
        } catch (IOException ex) {
            ex.printStackTrace();
            return ("Failure on encoding: " + file.getName());
        }
        return new BASE64Encoder().encode(data);
    }

    public static MessageType getMessageType(File file) {
        String affix = "";
        String filename = file.getName();
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(".");
            if (dot > -1 && dot < filename.length() - 1) {
                affix = filename.substring(dot + 1);
            }
        }
        switch (affix.toLowerCase()) {
            case "txt":
                return MessageType.TXT;
            case "html":
                return MessageType.HTML;
            case "htm":
                return MessageType.HTML;
            case "xml":
                return MessageType.XHTML;
            case "xhtml":
                return MessageType.XHTML;
            case "gif":
                return MessageType.GIF;
            case "jpg":
                return MessageType.JPG;
            case "jpeg":
                return MessageType.JPG;
            case "png":
                return MessageType.PNG;
            case "mpg":
                return MessageType.MPEG;
            case "mpeg":
                return MessageType.MPEG;
            case "pdf":
                return MessageType.PDF;
            case "doc":
                return MessageType.WORD;
            case "docx":
                return MessageType.WORD;
            default:
                if (file.canExecute()) {
                    return MessageType.GENER;
                } else {
                    return MessageType.MUTI;
                }
        }
    }
}
