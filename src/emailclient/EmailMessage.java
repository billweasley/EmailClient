package emailclient;

/**
 * ***********************************
 * Filename: EmailMessage.java * ***********************************
 */
import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.*;

public class EmailMessage {

    /* SMTP-sender of the message (in this case, contents of From-header. */
    private String Sender;
    /* SMTP-recipient, or contents of To-header. */
    private String RecipientList;
    private String[] Recipients;

    private String CcList;
    private String[] Ccs;

    /* Target MX-host */
    private String DestHost;
    private InetAddress DestAddr;

    /* The headers and the body of the message. */
    private String Headers;
    private String Body;

    /* To make it look nicer */
    private static final String CRLF = "\r\n";

    public String getSender() {
        return Sender;
    }

    public String[] getRecipients() {
        return Recipients;
    }

    public String[] getCcs() {
        return Ccs;
    }

    public String getDestHost() {
        return DestHost;
    }

    public InetAddress getDestAddr() {
        return DestAddr;
    }

    public String getHeaders() {
        return Headers;
    }

    public String getBody() {
        return Body;
    }

    /*
	 * Create the message object by inserting the required headers from RFC 822
	 * (From, To, Date).
     */
    public EmailMessage(String from, String to, String cc, String subject, String mainText, List<SubEmailMessage> attechments,
            String localServer) throws UnknownHostException {
        /* Remove whitespace */
        Sender = from.trim();
        RecipientList = to.trim();
        Recipients = RecipientList.split(";");
        CcList = cc.trim();
        Ccs = CcList.split(";");

        Headers = "From: " + Sender + CRLF;
        Headers += "To: ";
        for (String rec : Recipients) {
            Headers += (rec + ",");
        }
        Headers += CRLF;

        Headers += "Cc: ";
        for (String rec : Ccs) {
            Headers += (rec + ",");
        }
        Headers += CRLF;

        Headers += ("Subject: " + subject.trim() + CRLF);
        /*
		 * A close approximation of the required format. Unfortunately only GMT.
         */
        SimpleDateFormat format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        Headers += "Date: " + dateString + CRLF;

        String MIME = "MIME-Version: 1.0";
        String boundary = "----=frontier";

        if (attechments != null) {
            Headers += (MIME + CRLF);
            Headers += "Content-Type: " + MessageType.MUTI + "; \n\tboundary= \"" + boundary + "\"" + CRLF + CRLF;
        } else {
            Headers += "Content-Type: " + MessageType.TXT + CRLF + CRLF;
        }

        /*
		 * Get message. We must escape the message to make sure that there are
		 * no single periods on a line. This would mess up sending the mail.
         */
        if (attechments != null || EmailClient.isHTML) {
            Body = "--" + boundary + CRLF;
        }
        if (EmailClient.isHTML & EmailClient.recordedWebpageContentType != null) {
            Body += "Content-Type: " + EmailClient.recordedWebpageContentType + ";" + CRLF;
        } else {
            Body += "Content-Type: " + MessageType.TXT + ";" + CRLF;
            Body += "Content-Transfer-Encoding: " + EncodingType.ASCII_7;
         }
            Body = (escapeMessage(mainText) + CRLF);

            for (SubEmailMessage sem : attechments) {
                Body += sem.getSubEmailMessage();
            }
            /*
		 * Take the name of the local mailserver and map it into an InetAddress
             */
            DestHost = localServer;
            try {
                DestAddr = InetAddress.getByName(DestHost);
            } catch (UnknownHostException e) {
                System.out.println("Unknown host: " + DestHost);
                System.out.println(e);
                throw e;
            }
        }
        /*
	 * Check whether the message is valid. In other words, check that both
	 * sender and recipient contain only one @-sign.
         */
    public boolean isValid() {
        int fromat;
        int toat;
        if (!CcList.equals("")) {
            for (String string : Ccs) {
                toat = string.indexOf('@');
                if (toat < 1 || (string.length() - toat) <= 1) {
                    System.out.println(string + " Cc address is invalid");
                    return false;
                }
                if (toat != string.lastIndexOf('@')) {
                    System.out.println(string + " Cc address is invalid");
                    return false;
                }
            }
        }
        for (String string : Recipients) {
            toat = string.indexOf('@');
            if (toat < 1 || (string.length() - toat) <= 1) {
                System.out.println(string + " Recipient address is invalid");
                return false;
            }
            if (toat != string.lastIndexOf('@')) {
                System.out.println(string + " Recipient address is invalid");
                return false;
            }
        }

        fromat = Sender.indexOf('@');
        if (fromat < 1 || (Sender.length() - fromat) <= 1) {
            System.out.println(Sender + " Sender address is invalid");
            return false;
        }
        if (fromat != Sender.lastIndexOf('@')) {
            System.out.println(Sender + " Sender address is invalid");
            return false;
        }
        return true;
    }

    /* For printing the message. */
    @Override
    public String toString() {
        String res;

        res = "Sender: " + Sender + '\n';

        res += "To: ";
        for (String rec : Recipients) {
            res += (rec + ",");
        }
        res += '\n';

        res += "Cc: ";
        for (String rec : Ccs) {
            res += (rec + ",");
        }
        res += '\n';

        res += "MX-host: " + DestHost + ", address: " + DestAddr + '\n';
        res += "Message:" + '\n';
        res += Headers + CRLF;
        res += Body;

        return res;
    }

    /*
	 * Escape the message by doubling all periods at the beginning of a line.
     */
    private String escapeMessage(String body) {
        String escapedBody = "";
        String token;
        StringTokenizer parser = new StringTokenizer(body, "\n", true);

        while (parser.hasMoreTokens()) {
            token = parser.nextToken();
            if (token.startsWith(".")) {
                token = "." + token;
            }
            escapedBody += token;
        }
        return escapedBody;
    }
}
