// FrameReceiver.java - full implementation
// DO NOT CHANGE THIS CLASS

import java.io.*;

/**
 * This class implements the receiver side of the physical layer.
 * <P>
 * The source code supplied is already complete.  It does NOT
 * need to be changed and will NOT be submitted for assessment.
 * You may, if you wish, alter the methods in this class for the
 * purpose of debugging your implementation of MessageReceiver,
 * but be careful not to change the interface.
 * <P>
 * In this implementation of the class, frames are read from
 * standard input (normally the keyboard).  Each line of input is
 * treated as a separate frame.
 */

public class FrameReceiver
{
    // Fields ----------------------------------------------------------

    private TerminalStream terminal;  // terminal stream manager

    // Constructor -----------------------------------------------------

    /**
     * Create and initialize new FrameReceiver.
     * @throws ProtocolException if error detected
     */

    public FrameReceiver() throws ProtocolException
    {
        // Create terminal stream manager

        this.terminal = new TerminalStream("FrameReceiver");
        terminal.printlnDiag("physical layer ready");
    }

    // Methods ---------------------------------------------------------

    /**
     * Receive a single frame.
     * <P>
     * @return a string containing the frame received, or null if the
     * end of the input stream has been reached.
     * <P>
     * If a message is split across several frames this method must be
     * called separately for each frame in turn.
     * <P>
     * Note there could be noise present between frames so the string returned
     * may include a few random characters before and after the frame itself.
     * These extra characters aren't part of the frame and would normally be
     * disregarded.
     * <P>
     * @throws ProtocolException in the event of an error
     */

    public String receiveFrame() throws ProtocolException
    {
        // Prompt for next frame

        terminal.printlnDiag("    receiveFrame starting");
        terminal.printDiag("    enter frame > ");

        // Read frame
        // (terminal.readLine handles stop string)

        String frame = terminal.readLine();

        // Report outcome and return frame
        // End of stream signalled by readLine returning null

        if (frame == null)
            terminal.printlnDiag("    receiveFrame returning null (end of input stream)");
        else
            terminal.printlnDiag("    receiveFrame returning \"" + frame + "\"");
        return frame;
    }

} // end of class FrameReceiver

