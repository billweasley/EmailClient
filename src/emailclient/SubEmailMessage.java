package emailclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 *
 * @author haoxuan
 */
public class SubEmailMessage {

    private final String type;

    private final String encoding;

    private String subEmailMessage = "";
    private static final String CRLF = "\r\n";

    public SubEmailMessage(File file) {
        this(encodeAttach(file), getMessageType(file).toString(), file.getName(), EncodingType.BASE64.toString());
    }

    public SubEmailMessage(String partBody, String type, String fileName, String encoding) {
        this.type = type;
        this.encoding = encoding;
        subEmailMessage = ("--" + EmailMessage.BOUNDARY + CRLF);
        if (fileName != null) {
            subEmailMessage += "Content-Type: " + type + "; " + "name=" + fileName + CRLF;
            subEmailMessage += "Content-Disposition: attachment; " + "filename=" + fileName + CRLF;
        } else {
            subEmailMessage += "Content-Type: " + type + CRLF;
            subEmailMessage += "Content-Disposition: attachment; " + CRLF;
        }
        subEmailMessage += "Content-Transfer-Encoding: " + encoding + CRLF;
        subEmailMessage += CRLF;
        subEmailMessage += (partBody + CRLF);
    }

    public SubEmailMessage(String main, String type, String encoding) {
        this.type = type;
        this.encoding = encoding;
        subEmailMessage = (main + CRLF);
    }

    public String getType() {
        return type;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getSubEmailMessage() {
        return subEmailMessage;
    }

    public static String encodeAttach(File file) {
        byte[] data = null;
        try {
            try (InputStream isr = new FileInputStream(file)) {
                data = new byte[isr.available()];
                isr.read(data);
            }
        } catch (FileNotFoundException ex) {
            return (file.getName() + "File Not Found");
        } catch (IOException ex) {
            return ("Failure on encoding: " + file.getName());
        }
        return Base64.getMimeEncoder().encodeToString(data);
        // return new BASE64Encoder().encode(data);
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
