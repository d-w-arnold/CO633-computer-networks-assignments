// MessageSender.java - PARTIAL IMPLEMENTATION

import java.util.ArrayList;

/**
 * This class implements the sender side of the data link layer.
 * <P>
 * The source code supplied here only contains a partial implementation. 
 * Your completed version must be submitted for assessment.
 * <P>
 * You only need to finish the implementation of the sendMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes. 
 * Only this file will be processed when your work is marked.
 *
 * @author David W. Arnold
 * @version 17th Oct 2019
 */

public class MessageSender
{
    // Fields ----------------------------------------------------------

    private int mtu;                    // maximum transfer unit (frame length limit)
    private FrameSender physicalLayer;  // physical layer object
    private TerminalStream terminal;    // terminal stream manager

    // DO NOT ADD ANY MORE INSTANCE VARIABLES
    // but it's okay to define constants here

    private final String startFrame = "<";
    private final String frameType = "D";
    private final String frameTypeEnd = "E";
    private final String fieldDelimiter = "-";
    private final String endFrame = ">";

    // Constructor -----------------------------------------------------

    /**
     * MessageSender constructor - DO NOT ALTER ANY PART OF THIS
     * Create and initialize new MessageSender.
     * @param mtu the maximum transfer unit (MTU)
     * (the length of a frame must not exceed the MTU)
     * @throws ProtocolException if error detected
     */

    public MessageSender(int mtu) throws ProtocolException
    {
        // Initialize fields
        // Create physical layer and terminal stream manager

        this.mtu = mtu;
        this.physicalLayer = new FrameSender();
        this.terminal = new TerminalStream("MessageSender");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
    }

    // Methods ---------------------------------------------------------

    /**
     * Send a single message - THIS IS THE ONLY METHOD YOU NEED TO MODIFY
     * @param message the message to be sent.  The message can be any
     * length and may be empty but the string reference should not
     * be null.
     * @throws ProtocolException immediately without attempting to
     * send any further frames if, and only if, the physical layer
     * throws an exception or the given message can't be sent
     * without breaking the rules of the protocol (including the MTU)
     */

    public void sendMessage(String message) throws ProtocolException
    {
        // Report action to terminal
        // Note the terminal messages aren't part of the protocol,
        // they're just included to help with testing and debugging

        terminal.printlnDiag("  sendMessage starting (message = \"" + message + "\")");

        // YOUR CODE SHOULD START HERE ---------------------------------
        // No changes are needed to the statements above

        ArrayList<String> frames = new ArrayList<>();
        int prefixLen = startFrame.length() + frameType.length() + fieldDelimiter.length() + 2 + fieldDelimiter.length();
        int suffixLen = fieldDelimiter.length() + 2 + endFrame.length();
        int maxMessSegLen = mtu - prefixLen - suffixLen;
        while (message.length() > maxMessSegLen) {
            String tmp = message.substring(0, maxMessSegLen);
            frames.add(createFrame(tmp, frameType));
            message = message.substring(maxMessSegLen);
        }
        frames.add(createFrame(message, frameTypeEnd));

        // The following statement shows how the frame sender is invoked.
        // At the moment it just passes a fixed string.
        // sendMessage should split large messages into several smaller
        // segments.  Each segment must be encoded as a frame in the
        // format specified.  sendFrame will need to be called separately
        // for each frame in turn.  See the coursework specification
        // and other class documentation for further info.

        for (String frame : frames) {
            physicalLayer.sendFrame(frame);
        }



        // YOUR CODE SHOULD FINISH HERE --------------------------------
        // No changes are needed to the statements below

        // Report completion of task

        terminal.printlnDiag("  sendMessage finished");

    } // end of method sendMessage

    // You may add private methods if you wish

    private String createFrame(String message, String frmType)
    {
        String arithSum = frmType + fieldDelimiter + genSegLength(message) + fieldDelimiter + message + fieldDelimiter;
        return startFrame + arithSum + genChecksum(arithSum) + endFrame;
    }

    private String genSegLength(String seg)
    {
        int len = seg.length();
        String segLength = Integer.toString(len);
        if (len < 10) {
            segLength = "0" + segLength;
        }
        return segLength;
    }

    private String genChecksum(String string)
    {
        int total = 0;
        for (char character : string.toCharArray()) {
            total += character;
        }
        return Integer.toString(total).substring(Integer.toString(total).length() - 2);
    }

} // end of class MessageSender

