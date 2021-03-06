package tex61;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;

import java.io.Reader;

import static tex61.FormatException.reportError;
import static java.lang.Integer.*;

/** Reads commands and text from an input source and send the results
 *  to a designated Controller. This essentially breaks the input down
 *  into "tokens"---commands and pieces of text.
 *  @author Conrad Shiao, cs61b-cz
 */
class InputParser {

    /** Matches text between { } in a command, including the last
     *  }, but not the opening {.  When matched, group 1 is the matched
     *  text.  Always matches at least one character against a non-empty
     *  string or input source. If it matches and group 1 is null, the
     *  argument was not well-formed (the final } was missing or the
     *  argument list was nested too deeply). */
    private static final Pattern BALANCED_TEXT =
        Pattern.compile("(?s)((?:\\\\.|[^\\\\{}]"
                        + "|[{](?:\\\\.|[^\\\\{}])*[}])*)"
                        + "\\}"
                        + "|.");

    /** Matches input to the text formatter.  Always matches something
     *  in a non-empty string or input source.  After matching, one or
     *  more of the groups described by *_TOKEN declarations will
     *  be non-null.  See these declarations for descriptions of what
     *  this pattern matches.  To test whether .group(*_TOKEN) is null
     *  quickly, check for .end(*_TOKEN) > -1).  */
    private static final Pattern INPUT_PATTERN =
        Pattern.compile("(?s)(\\p{Blank}+)"
                        + "|(\\r?\\n((?:\\r?\\n)+)?)"
                        + "|\\\\([\\p{Blank}{}\\\\])"
                        + "|\\\\(\\p{Alpha}+)([{]?)"
                        + "|((?:[^\\p{Blank}\\r\\n\\\\{}]+))"
                        + "|(.)");

    /** Symbolic names for the groups in INPUT_PATTERN. */
    private static final int
        /** Blank or tab. */
        BLANK_TOKEN = 1,
        /** End of line or paragraph. */
        EOL_TOKEN = 2,
        /** End of paragraph (>1 newline). EOL_TOKEN group will also
         *  be present. */
        EOP_TOKEN = 3,
        /** \{, \}, \\, or \ .  .group(ESCAPED_CHAR_TOKEN) will be the
         *  character after the backslash. */
        ESCAPED_CHAR_TOKEN = 4,
        /** Command (\<alphabetic characters>).  .group(COMMAND_TOKEN)
         *  will be the characters after the backslash.  */
        COMMAND_TOKEN = 5,
        /** A '{' immediately following a command. When this group is present,
         *  .group(COMMAND_TOKEN) will also be present. */
        COMMAND_ARG_TOKEN = 6,
        /** Segment of other text (none of the above, not including
         *  any of the special characters \, {, or }). */
        TEXT_TOKEN = 7,
        /** A character that should not be here. */
        ERROR_TOKEN = 8;

    /** A new InputParser taking input from READER and sending tokens to
     *  OUT. */
    InputParser(Reader reader, Controller out) {
        _input = new Scanner(reader);
        _out = out;
    }

    /** A new InputParser whose input is TEXT and that sends tokens to
     *  OUT. */
    InputParser(String text, Controller out) {
        _input = new Scanner(text);
        _out = out;
    }

    /** Break all input source text into tokens, and send them to our
     *  output controller.  Finishes by calling .close on the controller.
     */
    void process() {
        try {
            while (_input.findWithinHorizon(INPUT_PATTERN, 0) != null) {
                MatchResult mat = _input.match();
                if (mat.end(BLANK_TOKEN) > -1) {
                    _out.endWord();
                }
                if (mat.end(EOL_TOKEN) > -1) {
                    _out.addNewline();
                }
                if (mat.end(EOP_TOKEN) > -1) {
                    _out.endParagraph();
                }
                if (mat.end(ESCAPED_CHAR_TOKEN) > -1) {
                    _out.addText(mat.group(ESCAPED_CHAR_TOKEN));
                }
                if (mat.end(COMMAND_TOKEN) > -1) {
                    boolean check = mat.group(COMMAND_ARG_TOKEN).equals("");
                    if (mat.end(COMMAND_ARG_TOKEN) > -1 && !check) {
                        if (_input.findWithinHorizon(BALANCED_TEXT, 0)
                                == null) {
                            reportError("Command Argument was not well-formed");
                            System.exit(1);
                        } else {
                            MatchResult arg = _input.match();
                            String argumentCommand = arg.group(1);
                            processCommand(mat.group(COMMAND_TOKEN),
                                    argumentCommand);
                        }
                    } else {
                        processCommand(mat.group(COMMAND_TOKEN), null);
                    }
                }
                if (mat.end(TEXT_TOKEN) > -1) {
                    _out.addText(mat.group(TEXT_TOKEN));
                }
                if (mat.end(ERROR_TOKEN) > -1) {
                    reportError("Erroneous token: %s", mat.group(ERROR_TOKEN));
                    System.exit(1);
                }
            }
            _out.close();
        } catch (FormatException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /** Process \COMMAND{ARG} or (if ARG is null) \COMMAND.  Call the
     *  appropriate methods in our Controller (_out). */
    private void processCommand(String command, String arg) {
        try {
            switch (command) {
            case "indent":
                _out.setIndentation(parseInt(arg));
                break;
            case "parindent":
                _out.setParIndentation(parseInt(arg));
                break;
            case "textwidth":
                _out.setTextWidth(parseInt(arg));
                break;
            case "textheight":
                _out.setTextHeight(parseInt(arg));
                break;
            case "parskip":
                _out.setParSkip(parseInt(arg));
                break;
            case "nofill":
                _out.setFill(false);
                break;
            case "fill":
                _out.setFill(true);
                break;
            case "justify":
                _out.setJustify(true);
                break;
            case "nojustify":
                _out.setJustify(false);
                break;
            case "endnote":
                _out.formatEndnote(arg);
                break;
            default:
                reportError("unknown command: %s", command);
                System.exit(1);
                break;
            }
        } catch (NumberFormatException e) {
            reportError(e.getMessage());
            System.exit(1);
        }
    }

    /** My input source. */
    private final Scanner _input;
    /** The Controller to which I send input tokens. */
    private Controller _out;

}
