package emailclient;

/**
 * ***********************************
 * Filename: EmailMessage.java ***********************************
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
    public static final String BOUNDARY = "#frontier#";

    public String getSender() {
        return Sender;
    }

    public String[] getRecipients() {
        return Recipients;
    }

    public String[] getCcs() {
        return Ccs;
    }

    public String getCcList() {
        return CcList;
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
    public EmailMessage(String from, String to, String cc, String subject, SubEmailMessage mainText, List<SubEmailMessage> attechments,
            String localServer) throws UnknownHostException {
        /* Remove whitespace */
        Sender = from.trim();
        RecipientList = to.trim();
        CcList = cc.trim();

        Recipients = RecipientList.split(";");
        Ccs = CcList.split(";");

        Headers = "From: " + Sender + CRLF;

        Headers += "To: ";
        for (String rec : Recipients) {
            Headers += (rec + ",");
        }
        Headers = Headers.substring(0, Headers.length() - 1);
        Headers += CRLF;

        if (!CcList.equals("")) {
            Headers += "Cc: ";
            for (String rec : Ccs) {
                Headers += (rec + ",");
            }
            Headers = Headers.substring(0, Headers.length() - 1);
            Headers += CRLF;
        }

        Headers += ("Subject: " + subject.trim() + CRLF);

        /*
		 * A close approximation of the required format. Unfortunately only GMT.
         */
        SimpleDateFormat format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        Headers += ("Date: " + dateString + CRLF);

        if (!attechments.isEmpty()) {
            Headers += ("MIME-Version: 1.0" + CRLF);
            Headers += ("Content-Type: " + MessageType.MUTI.toString() + "; ");
            Headers += ("boundary=" + BOUNDARY + CRLF);
        } else {
            Headers += ("Content-Type: " + mainText.getType() + CRLF);
        }

        /*
         * Get message. We must escape the message to make sure that there are
         * no single periods on a line. This would mess up sending the mail.
         */
        Body = "";
        for (SubEmailMessage sem : attechments) {
            Body += sem.getSubEmailMessage();
        }

        if (attechments != null && (!(attechments.isEmpty() || mainText.getSubEmailMessage().equals("")))) {
            Body += ("--" + BOUNDARY + CRLF);
            if (mainText.getType() != null) {
                Body += "Content-Type: " + mainText.getType() + ";" + CRLF;
                if (mainText.getEncoding() != null) {
                    Body += "Content-Transfer-Encoding: " + mainText.getEncoding() + CRLF;
                }
                Body += CRLF;
            } else {
                Body += "Content-Type: " + MessageType.TXT.toString() + ";" + CRLF;
                Body += "Content-Transfer-Encoding: " + EncodingType.ASCII_7.toString() + CRLF + CRLF;
            }
            Body += (escapeMessage(mainText.getSubEmailMessage()) + "--" + BOUNDARY + "--");
        } else {
            Body += (escapeMessage(mainText.getSubEmailMessage()));
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

        if (Recipients.equals("")) {
            return false;
        }

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
