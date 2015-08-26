package tex61;

import java.util.ArrayList;

import static tex61.Defaults.*;
import static tex61.FormatException.reportError;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Conrad Shiao, cs61b-cz
 */
class LineAssembler {

    /** A new, empty line assembler with default settings of all
     *  parameters depending on ENDNOTEMODE, sending finished
     *  lines to PAGES. */
    LineAssembler(PageAssembler pages, boolean endnoteMode) {
        _pages = pages;
        if (endnoteMode) {
            _textWidth = ENDNOTE_TEXT_WIDTH;
            _indentation = ENDNOTE_INDENTATION;
            _parskip = ENDNOTE_PARAGRAPH_SKIP;
            _paragraphIndentation = ENDNOTE_PARAGRAPH_INDENTATION;
        }
    }

    /** Return the string representation of my current LINE, _line. */
    String toString(ArrayList<String> line) {
        String result = "";
        if (line.isEmpty()) {
            return result;
        }
        for (String elem : line) {
            result = result + elem + " ";
        }
        if (result.length() > 0) {
           result = result.substring(0, result.length() - 1); // cut out last blank
        }
        return result;
    }

    /** Return getter for my current _line. */
    public ArrayList<String> getLine() {
        return _line;
    }

    /** Return getter for my current word. */
    public String getCurrentWord() {
        return _currentWord;
    }

    /** Add TEXT to the word currently being built. */
    void addText(String text) {
        int indent = _indentation;
        if (_firstLine) {
            indent += _paragraphIndentation;
        }
        if ((toString(_line).length() + text.length()
                + indent + _currentWord.length() + 1 > _textWidth)
                && _filling) {
            outputLine(false);
        }
        _currentWord += text;
    }

    /** Finish the current word, if any, and add to words being accumulated. */
    void finishWord() {
        if (!_currentWord.isEmpty()) {
        //if (!_currentWord.equals("")) {
            addWord(_currentWord);
            _currentWord = "";
        }
    }

    /** Add WORD to the formatted text. */
    void addWord(String word) {
        int indent = _indentation;
        if (_firstLine) {
            indent += _paragraphIndentation;
        }
        if ((toString(_line).length() + word.length()
                + indent + 1 > _textWidth)
                && _filling) {
            outputLine(false);
        }
        _line.add(word);
    }


    /** Add LINE to our output, with no preceding paragraph skip.  There must
     *  not be an unfinished line pending. */
    void addLine(String line) {
        if (_firstLine) {
            for (int x = 0; x < _parskip; x++) {
                _pages.addLine("");
            }
        }
        _pages.addLine(line);
        _line.clear();
        _firstLine = false;
    }

    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        _indentation = val;
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0
     *  if I am a Line Assembler for the main text. If I am a Line
     *  Assembler for the endnote, I can additionally take on negative values
     *  (as my default for endnotes is of value -4). */
    void setParIndentation(int val) {
        if (val < 0 && Math.abs(val) > _indentation) {
            reportError("using parindent with value %d with the"
                    + " current settings in this document will shift"
                    + "foramt of text off current page.");
            System.exit(1);
        } else {
            _paragraphIndentation = val;
        }
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        _textWidth = val;
    }

    /** Iff ON, set fill mode. */
    void setFill(boolean on) {
        _filling = on;
    }

    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean on) {
        _justifying = on;
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        _parskip = val;
    }

    /** Set page height to VAL > 0. */
    void setTextHeight(int val) {
        _pages.setTextHeight(val);
    }

    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  adds a new complete line to the finished line queue and clears
     *  the line accumulator. */
    void newLine() {
        finishWord();
        if (!_filling && !_line.isEmpty()) {
            outputLine(false);
        }
    }

    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {
        finishWord();
        outputLine(true);
        _firstLine = true;
    }

    /** Transfer contents of _words to _pages, adding INDENT characters of
     *  indentation, and a total of SPACES spaces between words, evenly
     *  distributed.  Assumes _words is not empty.  Clears _words and _chars. */
    private void emitLine(int indent, int spaces) {
        String result = "";
        int prevSpacing = 0;
        int k = 1;
        for (; k < _line.size(); k++) {
            double temp = _line.size() - 1;
            int totalBlanks = (int) Math.floor(0.5 + spaces * k
                    / temp);
            result += _line.get(k - 1);
            int justify = totalBlanks - prevSpacing;
            for (int i = 0; i < justify; i++) {
                result += " ";
            }
            prevSpacing = totalBlanks;
        }
        result += _line.get(k - 1);
        for (int x = 0; x < indent; x++) {
            result = " " + result;
        }
        addLine(result);
    }

    /** If the line accumulator is non-empty, justify its current
     *  contents, if needed, add a new complete line to _pages,
     *  and clear the line accumulator. LASTLINE indicates the last line
     *  of a paragraph. */
    private void outputLine(boolean lastLine) {
        if (!_line.isEmpty()) {
            int indent = _indentation;
            if (_firstLine) {
                indent += _paragraphIndentation;
            }
            int spaces = _line.size() - 1;
            int totalSpaces = spaces;
            if (_filling && _justifying && !lastLine) {
                totalSpaces += _textWidth - toString(_line).length()
                        - indent;
                int check = 3 * spaces;
                if (totalSpaces >= check) {
                    totalSpaces = check;
                }
            }
            emitLine(indent, totalSpaces);
        }
    }

    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pages;

    /** Setting of current indentation. */
    private int _indentation = INDENTATION;
    /** Setting of current paragraph indentation. */
    private int _paragraphIndentation = PARAGRAPH_INDENTATION;
    /** Setting of the current text width. */
    private int _textWidth = TEXT_WIDTH;
    /** Setting of blank lines in front of each subsequent paragraph. */
    private int _parskip = PARAGRAPH_SKIP;
    /** True iff both filling is true and we are justifying. */
    private boolean _justifying = true;
    /** True iff we are currently filling lines. */
    private boolean _filling = true;

    /** Current word I am looking at. */
    private String _currentWord = "";

    /** True iff I am currently at the first line of a paragraph. */
    private boolean _firstLine = true;

    /** Current list of words being accumulated in my current line. */
    private ArrayList<String> _line = new ArrayList<String>();
}
