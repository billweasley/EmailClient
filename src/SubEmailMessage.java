/**
 * ***********************************
 * Filename: SubSMTPConnect.java 
 * Names: Haoxuan WANG,Yuan GAO
 * Student-IDs: 201219597, 201218960
 * Date: 21/Oct/2016 .
 * ***********************************
 **/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/** [Alter] and [Add]
 *  Construct mutiple partitions of a EmailMessage object from attachments(files)
 *  and the body message
 *  The MIME requires a email to be sperated to different parts
 *  The EmailClient object will integrate a list of SubEmilMessage object to form its body.
 */
public class SubEmailMessage {
    
    /* MIME message type the possible value is listed in "MessageType" enum */
    private final String type;
    /* Encoding of message partition, the possible value is listed in "EncodingType" enum */
    private final String encoding;
    
    /* Variable to record result */
    private String subEmailMessage = "";
    private static final String CRLF = "\r\n";

    /* [Add] Constructor for files(attachments), the default value is using BASE64 encoding*/
    public SubEmailMessage(File file) {
        this(encodeAttach(file), getMessageType(file).toString(), file.getName(), EncodingType.BASE64.toString());
    }
    
    /* [Add] Constructor for files(attachments) in details, this one allow pass in the parameters manually */
    public SubEmailMessage(String partBody, String type, String fileName, String encoding) {
        this.type = type;
        this.encoding = encoding;
        /* Build for the corresonding partition of the mail message*/
        /* The delimiter*/
        subEmailMessage = ("--" + EmailMessage.BOUNDARY + CRLF);
        
        /* Partition header with or without filename */
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
    /* [Add] Constructor for main message, which is the characters in the UI panel that user inputted or got from a website */
    public SubEmailMessage(String main, String type, String encoding) {
        this.type = type;
        this.encoding = encoding;
        subEmailMessage = (main + CRLF);
    }
    /* [Add] Get Methods*/
    public String getType() {
        return type;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getSubEmailMessage() {
        return subEmailMessage;
    }
     /* [Add] This method is using for encode a file using base64, convert it to the base64 encoded Strings */
    public static String encodeAttach(File file) {
        /* read in */
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
        /* Convert */
        return Base64.getMimeEncoder().encodeToString(data);
    }
    /* [Add] Determine the type should be used of a attachment, using extension name of the file */
    public static MessageType getMessageType(File file) {
        /*Abstract extension name*/
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
