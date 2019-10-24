// MessageReceiver.java - PARTIAL IMPLEMENTATION

/**
 * This class implements the receiver side of the data link layer.
 * <P>
 * The source code supplied here only contains a partial implementation.
 * Your completed version must be submitted for assessment.
 * <P>
 * You only need to finish the implementation of the receiveMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes.
 * Only this file will be processed when your work is marked.
 *
 * @author David W. Arnold
 * @version 18th Oct 2019
 */

public class MessageReceiver
{
    // Fields ----------------------------------------------------------

    private int mtu;                      // maximum transfer unit (frame length limit)
    private FrameReceiver physicalLayer;  // physical layer object
    private TerminalStream terminal;      // terminal stream manager

    // DO NOT ADD ANY MORE INSTANCE VARIABLES
    // but it's okay to define constants here

    private final String startFrame = "<";
    private final String frameType = "D";
    private final String frameTypeEnd = "E";
    private final int segLenLen = 2;
    private final String fieldDelimiter = "-";
    private final int checksumLen = 2;
    private final String endFrame = ">";

    // Constructor -----------------------------------------------------

    /**
     * MessageReceiver constructor - DO NOT ALTER ANY PART OF THIS
     * Create and initialize new MessageReceiver.
     * @param mtu the maximum transfer unit (MTU)
     * (the length of a frame must not exceed the MTU)
     * @throws ProtocolException if error detected
     */

    public MessageReceiver(int mtu) throws ProtocolException
    {
        // Initialize fields
        // Create physical layer and terminal stream manager

        this.mtu = mtu;
        this.physicalLayer = new FrameReceiver();
        this.terminal = new TerminalStream("MessageReceiver");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
    }

    // Methods ---------------------------------------------------------

    /**
     * Receive a single message - THIS IS THE ONLY METHOD YOU NEED TO MODIFY
     * @return the message received, or null if the end of the input
     * stream has been reached.  See receiveFrame documentation for
     * further explanation of how the end of the input stream is
     * detected and handled.
     * @throws ProtocolException immediately without attempting to
     * receive any further frames if any error is detected, such as
     * a corrupt frame, even if the end of the input stream has also
     * been reached (signalling an error takes precedence over
     * signalling the end of the input stream)
     */

    public String receiveMessage() throws ProtocolException
    {
        String message = "";    // whole of message as a single string
                                // initialise to empty string

        // Report action to terminal
        // Note the terminal messages aren't part of the protocol,
        // they're just included to help with testing and debugging

        terminal.printlnDiag("  receiveMessage starting");

        // YOUR CODE SHOULD START HERE ---------------------------------
        // No changes are needed to the statements above

        int prefixLen = startFrame.length() + frameType.length() + fieldDelimiter.length() + 2 + fieldDelimiter.length();
        int suffixLen = fieldDelimiter.length() + 2 + endFrame.length();
        if ((mtu - prefixLen - suffixLen) < 1) {
            throw new ProtocolException("In order to receive a frame, the MTU needs to be greater than : " + (prefixLen + suffixLen));
        }

        // The following block of statements shows how the frame receiver
        // is invoked.  At the moment it just sets the message equal to
        // the first frame.  This is of course incorrect!  receiveMessage
        // should invoke receiveFrame separately for each frame of the
        // message in turn until the final frame in that message has been
        // obtained.  The message segments should be extracted and joined
        // together to recreate the original message string.  One whole
        // message should is processed by a single execution of receiveMessage
        // and returned as a single string.
        // 
        // See the coursework specification and other class documentation
        // for further info.

        boolean receiving = true;
        while (receiving) {
            String frame = physicalLayer.receiveFrame();
            String prefix = frame.substring(0, prefixLen);
            String suffix = frame.substring(frame.length() - suffixLen);
            String frmType = getFrmType(frame);
            String segLen = getSegLen(prefix, prefixLen);
            String messSeg = getMessage(frame, prefixLen, suffixLen);
            boolean sl = segLenCorrect(segLen, messSeg);
            boolean ch = checkSumCorrect(frmType, segLen, messSeg, getChecksum(suffix, suffixLen));
            if (!sl || !ch) {
                if (!sl && !ch) {
                    throw new ProtocolException("Segment length and checksum are both incorrect.");
                } else if (!sl) {
                    throw new ProtocolException("Segment length is incorrect.");
                } else {
                    throw new ProtocolException("Checksum is incorrect.");
                }
            } else {
                message += messSeg;
                if (frmType.equals(frameTypeEnd)) {
                    receiving = false;
                }
            }
        }



        // YOUR CODE SHOULD FINISH HERE --------------------------------
        // No changes are needed to the statements below

        // Return message

        if (message == null)
            terminal.printlnDiag("  receiveMessage returning null (end of input stream)");
        else
            terminal.printlnDiag("  receiveMessage returning \"" + message + "\"");
        return message;

    } // end of method receiveMessage

    // You may add private methods if you wish

    private String getFrmType(String frame)
    {
        return frame.substring(startFrame.length(), startFrame.length() + frameType.length());
    }

    private String getMessage(String frame, int prefixLen, int suffixLen)
    {
        return frame.substring(prefixLen, frame.length() - suffixLen);
    }

    private String getSegLen(String prefix, int prefixLen)
    {
        return prefix.substring(prefixLen - segLenLen - fieldDelimiter.length(), prefixLen - fieldDelimiter.length());
    }

    private String getChecksum(String suffix, int suffixLen)
    {
        return suffix.substring(suffixLen - checksumLen - fieldDelimiter.length(), suffixLen - fieldDelimiter.length());
    }

    private boolean segLenCorrect(String segLen, String messSeg)
    {
        return Integer.parseUnsignedInt(segLen) == messSeg.length();
    }

    private boolean checkSumCorrect(String frmType, String segLen, String messSeg, String checksum)
    {
        String arithSum = frmType + fieldDelimiter + segLen + fieldDelimiter + messSeg + fieldDelimiter;
        return genChecksum(arithSum).equals(checksum);
    }

    private String genChecksum(String string)
    {
        int total = 0;
        for (char character : string.toCharArray()) {
            total += character;
        }
        return Integer.toString(total).substring(Integer.toString(total).length() - 2);
    }

} // end of class MessageReceiver

