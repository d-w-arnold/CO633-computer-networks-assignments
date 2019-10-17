// FrameSender.java - full implementation
// DO NOT CHANGE THIS CLASS

/**
 * This class implements the sender side of the physical layer.
 * <P>
 * The source code supplied is already complete.  It does NOT
 * need to be changed and will NOT be submitted for assessment.
 * You may, if you wish, alter the methods in this class for the
 * purpose of debugging your implementation of MessageSender,
 * but be careful not to change the interface.
 */

public class FrameSender
{
    // Fields ----------------------------------------------------------

    private TerminalStream terminal;  // terminal stream manager

    // Constructor -----------------------------------------------------

    /**
     * Create and initialize new FrameSender.
     * @throws ProtocolException if error detected
     */

    public FrameSender() throws ProtocolException
    {
        // Create terminal stream manager

        this.terminal = new TerminalStream("FrameSender");
        terminal.printlnDiag("physical layer ready");
    }

    // Methods ---------------------------------------------------------

    /**
     * Send a single frame.
     * If a message is split across several frames this method must be
     * called separately for each frame in turn.
     * @param frame the frame to be sent.  There should be no extraneous
     * leading or trailing characters
     * @throws ProtocolException in the event of an error
     */

    public void sendFrame(String frame) throws ProtocolException
    {
        // If debug mode enabled then output full diagnostic message
        // If debug mode disabled then just output raw frame

        terminal.printlnDiagOrRaw("    sendFrame called (frame = \"" + frame + "\")", frame);
    }

} // end of class FrameSender

