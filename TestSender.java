/**
 * This is a test harness for the MessageSender class.
 * <p>
 * The source code supplied is already complete.  It does NOT need
 * to be changed and will NOT be submitted for assessment.  You may,
 * if you wish, alter this class for the purpose of debugging your
 * implementation of MessageSender, but the interface to MessageSender
 * must NOT be changed.
 * <p>
 * <B>Running the test harness under BlueJ</B>
 * <p>
 * Invoke one of the runTest methods by right clicking on the TestSender
 * class icon in the main BlueJ window.  These are static methods so you
 * don't need to create an object first.  Several versions of runTest
 * are available.  The easiest to use is runTest(), which sets all
 * options to sensible default values.  Other versions allow one or more
 * of the following options to be set:
 * <p>
 * mtu    - Specifies the maximum transfer unit (MTU) which is the
 * maximum frame length permitted by the data link protocol.
 * Versions of runTest() without this parameter use the default
 * value defined in the class constant <I>defaultMtu</I>.
 * <p>
 * debug  - If enabled then diagnostic messages are displayed to assist
 * with testing and debugging.<BR>
 * Note that control codes and foreign characters aren't handled
 * reliably by the System.in/out streams.  The debug mode converts
 * them to hex notation just for the purpose of display so their
 * presence can be seen even if the actual characters can't.
 * For example, if a character has a numeric value of 1234 in hex
 * then it will be expanded to the format \\u1234.
 * This is a limitation of the simple test harness provided.
 * It's not related to the functioning of send/receiveMessage.<BR>
 * If debug mode is disabled then all diagnostic messages except
 * fatal error messages are suppressed and text is passed directly
 * to the output stream without expanding non-printable characters.
 * This is intended for use when input/output is redirected
 * from/to a file when run from a command line.<BR>
 * This option is of limited use when running under BlueJ.<BR>
 * Versions of runTest() without this parameter default to false.
 * <p>
 * <B>Running the test harness from a command line</B>
 * <p>
 * This test harness can also be executed directly from the command line
 * (e.g. in a DOS prompt or Linux/Unix shell window), viz:
 * <p>
 * java TestSender options
 * <p>
 * The following command line arguments are supported (see BlueJ section
 * above for full explanations):
 * <p>
 * -mN  Sets mtu to N.  Defaults to class constant defaultMtu if omitted.<BR>
 * -d1  Enable debug mode (default).<BR>
 * -d0  Disable debug mode.
 * <p>
 * <B>Test harness operation</B>
 * <p>
 * When the test harness starts, it reads message strings from standard
 * input (normally the keyboard).  Under BlueJ this appears in the
 * terminal window.  Each line of input is treated as a separate message.
 * <p>
 * The sendMessage method of the MessageSender class is invoked
 * automatically for each message in turn.  If sendMessage is working
 * correctly, the resulting sequence of message segment frames will be
 * reported to the terminal window by the sendFrame method of the
 * physical layer FrameSender class.
 * <p>
 * The program loops until the user enter a string matching that defined
 * in class constant <I>stop</I> or the physical end of the input stream
 * is reached when input is redirected from a file.
 * <p>
 * <B>Additional notes when running from a command line</B>
 * <p>
 * Note that Java's standard keyboard input and screen output packages
 * are designed to handle plain text (i.e. printable characters like
 * those appearing on the keyboard).  Message strings containing certain
 * control codes may therefore not be handled correctly.  For example,
 * redirecting input from a binary file (e.g. an MP3 file) is unlikely
 * to produce correct results.  This is a limitation of the test harness
 * and physical layer implementations, not the MessageSender class.
 * <p>
 * The test harness will also terminate prematurely if input has been
 * redirected from a file containing a line that matches the value of
 * class constant <I>stop</I>.  This restriction can be overcome by
 * setting <I>stop</I> to null.
 */
public class TestSender
{
    // Default value for MTU, leaves room for 10 message chars
    private static final int defaultMtu = 20;
    // User enters this to quit (null = disable)
    private static final String stop = ".";

    /**
     * Main method used when the program is executed from a command line.
     *
     * @param args the command line arguments
     * @throws Exception if unexpected error occurs
     */
    public static void main(String[] args) throws Exception
    {
        // Set options to default values
        int mtu = defaultMtu;              // maximum transfer unit (frame length limit)
        boolean debug = true;              // enable by default

        // Parse command line options
        // Loop for each command line argument in turn
        for (int i = 0; i < args.length; i++) {
            // If debug option found, set accordingly
            if (args[i].equalsIgnoreCase("-d1")) {
                debug = true;
            } else if (args[i].equalsIgnoreCase("-d0")) {
                debug = false;
            }

            // If MTU option found, decode the value
            // Abort program if bad
            else if (args[i].toLowerCase().startsWith("-m")) {
                try {
                    mtu = Integer.parseInt(args[i].substring(2).trim());
                } catch (Exception e) {
                    usageErrorExit("Bad or missing MTU value on command line");
                }
            }

            // Abort program if unrecognised argument found
            else {
                usageErrorExit("Unrecognised command line option " + args[i]);
            }
        }

        // Run test with options specified
        runTest(mtu, debug);
    }

    /**
     * Run test (version with all options)
     *
     * @param mtu   the maximum frame length permitted by the data link protocol
     * @param debug true = enable debug mode, false = disable
     * @throws Exception if unexpected error occurs
     */
    public static void runTest(int mtu, boolean debug) throws Exception
    {
        // Create terminal stream manager
        // Enable debug mode
        // Set field width to length of longest class name
        // Set stop string for message input stream
        TerminalStream terminal = new TerminalStream("TestSender");
        terminal.setDebug(debug);
        terminal.setClassWidth("MessageSender".length());
        terminal.setStop(stop);

        // Announce start of test
        // Create data link layer message sender (which also creates physical layer)
        terminal.printlnDiag("test rig starting (mtu = " + mtu + ", debug = " + debug + ")");
        MessageSender dataLinkLayer = new MessageSender(mtu);

        // Give instructions on how to stop the test
        terminal.printlnDiag("message entry loop starting");
        terminal.printlnDiag("enter one message per line (no \"quotes\" required)");
        if (stop != null)
            terminal.printlnDiag("enter \"" + stop + "\" to stop test");

        // Message processing loop
        // Repeats until end of input stream reached
        while (true) {
            // Read next message
            // (terminal.readLine handles stop string)
            // Break out of loop if stop entered or end of stream reached
            terminal.printlnDiag();
            terminal.printlnDiag("(Example message to send: hello )");
            terminal.printDiag("enter message > ");
            String message = terminal.readLine();
            if (message == null) {
                terminal.printlnDiag("end of input stream reached");
                break;
            }

            // Pass message to data link message sender
            // Trap any exception so can report before terminating program
            try {
                terminal.printlnDiag("calling sendMessage...");
                dataLinkLayer.sendMessage(message);
                terminal.printlnDiag("sendMessage returned normally");
            } catch (Exception e) {
                terminal.printlnError("sendMessage threw an exception \"" + e.getMessage() + "\"");
                break;
            }
        }

        // Test ended normally
        terminal.printlnDiag();
        terminal.printlnDiag("test rig finished");
    }

    /**
     * Run test (version using sensible defaults for most options)
     *
     * @param mtu the maximum frame length permitted by the data link protocol
     * @throws Exception if unexpected error occurs
     */
    public static void runTest(int mtu) throws Exception
    {
        runTest(mtu, true);
    }

    /**
     * Run test (version using sensible defaults for all options)
     *
     * @throws Exception if unexpected error occurs
     */
    public static void runTest() throws Exception
    {
        runTest(defaultMtu, true);
    }

    /**
     * Handle program usage errors.
     * Forces exit.
     *
     * @param errorMessage message describing error
     */
    private static void usageErrorExit(String errorMessage)
    {
        // Display error message
        // Give usage info
        // Force exit
        System.err.println("!!! Program aborted : " + errorMessage);
        System.err.println("Usage  : java TestSender options");
        System.err.println("Options: -mN  set mtu to N (defaults to " + defaultMtu + ")");
        System.err.println("         -d1  enable debug mode (show diagnostics, default)");
        System.err.println("         -d0  disable debug mode (suppress diagnostics)");
        System.exit(1);
    }
}
