/**
 * ***********************************
 * Filename: SMTPConnect.java 
 * Names: Haoxuan WANG,Yuan GAO
 * Student-IDs: 201219597, 201218960
 * Date: 21/Oct/2016 .
 * ***********************************
 **/
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Open an SMTP connection to mailserver and send one mail.
 *
 */
public class SMTPConnect {

    /* The socket to the server */
    private Socket connection;

    /* Streams for reading from and writing to socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;
    
    /* Port for SMTP */
    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnect object. Create the socket and the 
       associated streams. Initialise SMTP connection. */
    public SMTPConnect(EmailMessage mailmessage) throws IOException {
        connection = new Socket(mailmessage.getDestHost(), SMTP_PORT);
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());
        /* Read a line from server and check that the reply code is 220.
	   If not, throw an IOException. */
        if (!fromServer.readLine().startsWith("220")) {
            throw new IOException("220 reply not received from server.");
        }

        /* SMTP handshake. We need the name of the local machine.
	   Send the appropriate SMTP handshake command. */
        String localhost = InetAddress.getLocalHost().getHostName();
        try {
            sendCommand("HELO " + localhost + CRLF, 250);
            isConnected = true;
        } catch (IOException ex) {
            System.out.println("SMTP Handshaking error.");
        }

    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    /*
     * [Alter] our send() method now support mutiple recipients and mutiple Carbon Copy (CC)
     */
    public void send(EmailMessage mailmessage) throws IOException {

        String cammand;
        /* Send all the necessary commands to send a message. Call
	   sendCommand() to do the dirty work. Do _not_ catch the
	   exception thrown from sendCommand(). */
        cammand = "MAIL FROM: <" + mailmessage.getSender() + ">" + CRLF;
        sendCommand(cammand, 250);
        
        for (String recipent : mailmessage.getRecipients()) {
            cammand = "RCPT TO: <" + recipent + ">" + CRLF;
            sendCommand(cammand, 250);
        }

        if (mailmessage.getCcs() == null || !mailmessage.getCcList().equals("")) {
            for (String recipent : mailmessage.getCcs()) {
                cammand = "RCPT TO: <" + recipent + ">" + CRLF;
                sendCommand(cammand, 250);
            }
        }
        cammand = "DATA" + CRLF;
        sendCommand(cammand, 354);
        cammand = mailmessage.getHeaders() + CRLF + mailmessage.getBody() + CRLF + "." + CRLF;

        sendCommand(cammand, 250);

    }

    /* Close SMTP connection. First, terminate on SMTP level, then
       close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT" + CRLF, 221);  //?221
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
       what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {

        toServer.writeBytes(command);
        /* Write command to server and read reply from server. */
        String msg = fromServer.readLine();
        System.out.println(msg);
        /* Check that the server's reply code is the same as the parameter
	   rc. If not, throw an IOException. */
        if (!msg.startsWith(String.valueOf(rc))) {
            throw new IOException(command + " executed error:" + rc + " reply not received from server.");
        }
    }

    /* Destructor. Closes the connection if something bad happens. */
    @Override
    protected void finalize() throws Throwable {
        if (isConnected) {
            close();
        }
        super.finalize();
    }
}
