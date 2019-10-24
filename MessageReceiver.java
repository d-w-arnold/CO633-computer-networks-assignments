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

        // TODO: Message segment must not exceed 99 irrespective of MTU.

        int prefixLen = startFrame.length() + frameType.length() + fieldDelimiter.length() + segLenLen + fieldDelimiter.length();
        int suffixLen = fieldDelimiter.length() + checksumLen + endFrame.length();
        if ((mtu - prefixLen - suffixLen) < 0) {
            throw new ProtocolException("MTU not large enough for receiving a frame with an empty message segment");
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
            int frameLen = frame.length();
            if (frameLen < (prefixLen + suffixLen)) { // If the length of the frame is too short
                throw new ProtocolException("Frame not large enough for receiving a frame with an empty message segment");
            }
            if (frameLen > mtu) { // If the length of the frame is too long
                throw new ProtocolException("Frame length exceeds MTU");
            }
            String prefix = frame.substring(0, prefixLen);
            String suffix = frame.substring(frameLen - suffixLen);
            if (!correctFrameFormat(frame, prefix, prefixLen, suffix, suffixLen)) { // Check formatting of frame
                throw new ProtocolException("Invalid frame format");
            }
            String frmType = getFrmType(prefix);
            String segLen = getSegLen(prefix, prefixLen);
            String messSeg = getMessSeg(frame, prefixLen, suffixLen);
            boolean sl = segLenCorrect(segLen, messSeg);
            boolean ch = checkSumCorrect(frmType, messSeg, getChecksum(suffix, suffixLen));
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

    private boolean correctFrameFormat(String frame, String prefix, int prefixLen, String suffix, int suffixLen)
    {
        if (!correctPrefixFormat(prefix, prefixLen)) { // Check prefix is correct
            return false;
        } else if (!correctSuffixFormat(suffix, suffixLen)) { // Check suffix is correct
            return false;
        }
        return true;
    }

    private boolean correctPrefixFormat(String prefix, int prefixLen)
    {
        String frmType = getFrmType(prefix);
        String firstDelimiter = prefix.substring(prefix.length() - segLenLen - (2 * fieldDelimiter.length()), prefix.length() - segLenLen - fieldDelimiter.length());
        String secondDelimiter = prefix.substring(prefix.length() - fieldDelimiter.length());
        if (prefix.length() != prefixLen) { // Correct length
            return false;
        } else if (!prefix.substring(0, startFrame.length()).equals(startFrame)) { // <
            return false;
        } else if (!frmType.equals(frameType) && !frmType.equals(frameTypeEnd)) { // E or D
            return false;
        } else if (!firstDelimiter.equals(fieldDelimiter) || !secondDelimiter.equals(fieldDelimiter)) { // -??-
            return false;
        }
        return true;
    }

    private boolean correctSuffixFormat(String suffix, int suffixLen)
    {
        String delimiter = suffix.substring(0, fieldDelimiter.length());
        String endF = suffix.substring(suffix.length() - endFrame.length());
        if (suffix.length() != suffixLen) { // Correct length
            return false;
        } else if (!delimiter.equals(fieldDelimiter) || !endF.equals(endFrame)) { // -??>
            return false;
        }
        return true;
    }

    private String getFrmType(String prefix)
    {
        return prefix.substring(startFrame.length(), startFrame.length() + frameType.length());
    }

    private String getMessSeg(String frame, int prefixLen, int suffixLen)
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

    private boolean segLenCorrect(String segLen, String messSeg) throws ProtocolException
    {
        int messSegLen;
        try {
            messSegLen = Integer.parseUnsignedInt(segLen);
        } catch (Exception e) {
            throw new ProtocolException("Segment length not 2-digit decimal");
        }
        return messSegLen == messSeg.length();
    }

    private boolean checkSumCorrect(String frmType, String messSeg, String checksum) throws ProtocolException
    {
        int ch;
        try {
            ch = Integer.parseUnsignedInt(checksum);
        } catch (Exception e) {
            throw new ProtocolException("Checksum not 2-digit decimal");
        }
        String arithSum = frmType + fieldDelimiter + genSegLength(messSeg) + fieldDelimiter + messSeg + fieldDelimiter;
        return Integer.parseUnsignedInt(genChecksum(arithSum)) == ch;
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

} // end of class MessageReceiver

