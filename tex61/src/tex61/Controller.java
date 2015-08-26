package tex61;

import java.io.PrintWriter;
import java.util.ArrayList;

import static tex61.FormatException.reportError;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Conrad Shiao, cs61b-cz
 */
class Controller {

    /** My LineAssembler for the main text. */
    private LineAssembler _mainLineAssembler;

    /** My pagePrinter. */
    private PagePrinter _pagePrinter;

    /** My LineAssembler for the endnotes. */
    private LineAssembler _endnoteLineAssembler;

    /** My PageCollector for endnotes, after being formatted. */
    private PageCollector _pageCollector;

    /** Current lines of my endnote. Sent eventually to _pageCollector. */
    private ArrayList<String> _endnoteLines = new ArrayList<String>();

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _pagePrinter = new PagePrinter(out);
        _mainLineAssembler = new LineAssembler(_pagePrinter, false);
        _pageCollector = new PageCollector(_endnoteLines);
        _endnoteLineAssembler = new LineAssembler(_pageCollector, true);
    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        if (_endnoteMode) {
            _endnoteLineAssembler.addText(text);
        } else {
            _mainLineAssembler.addText(text);
        }
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        if (_endnoteMode) {
            _endnoteLineAssembler.finishWord();
        } else {
            _mainLineAssembler.finishWord();
        }
    }

    /** Finish any current word of formatted text and process an end-of-line
     *  according to the current formatting parameters. */
    void addNewline() {
        if (_endnoteMode) {
            _endnoteLineAssembler.newLine();
        } else {
            _mainLineAssembler.newLine();
        }
    }

    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        if (_endnoteMode) {
            _endnoteLineAssembler.endParagraph();
        } else {
            _mainLineAssembler.endParagraph();
        }
    }

    /** If valid, process TEXT into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote(String text) {
        setEndnoteMode();
        _refNum++;
        _mainLineAssembler.addText(String.format("[%d]", _refNum));
        _endnoteLineAssembler.addText(String.format("[%d] ", _refNum));
        InputParser parsingEndnotes = new InputParser(text, this);
        parsingEndnotes.process();
        _endnoteLineAssembler.endParagraph();
        setNormalMode();
    }

    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        if (!_endnoteMode) {
            if (val <= 0) {
                reportError("textheight cannot take on non-positive "
                        + "argument value of: %d", val);
                System.exit(1);
            } else {
                _mainLineAssembler.setTextHeight(val);
            }
        }
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        if (val < 0) {
            reportError("textwidth cannot take on the negative"
                    + " value of: %d", val);
            System.exit(1);
        } else if (_endnoteMode) {
            _endnoteLineAssembler.setTextWidth(val);
        } else {
            _mainLineAssembler.setTextWidth(val);
        }
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) {
        if (val < 0) {
            reportError("indent cannot take on the negative argument"
                    + " value of %d", val);
            new FormatException("indent is wrong");
            System.exit(1);
        } else if (_endnoteMode) {
            _endnoteLineAssembler.setIndentation(val);
        } else {
            _mainLineAssembler.setIndentation(val);
        }
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        if (_endnoteMode) {
            _endnoteLineAssembler.setParIndentation(val);
        } else {
            _mainLineAssembler.setParIndentation(val);
        }
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        if (val < 0) {
            reportError("parskip cannot take on negative argument"
                    + " value of %d", val);
            System.exit(1);
        } else if (_endnoteMode) {
            _endnoteLineAssembler.setParSkip(val);
        } else {
            _mainLineAssembler.setParSkip(val);
        }
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        if (_endnoteMode) {
            _endnoteLineAssembler.setFill(on);
        } else {
            _mainLineAssembler.setFill(on);
        }
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        if (_endnoteMode) {
            _endnoteLineAssembler.setJustify(on);
        } else {
            _mainLineAssembler.setJustify(on);
        }
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. */
    void close() {
        if (_endnoteMode) {
            setNormalMode();
        } else {
            endParagraph();
            writeEndnotes();
        }
    }

    /** Start directing all formatted text to the endnote assembler. */
    private void setEndnoteMode() {
        _endnoteMode = true;
    }

    /** Return to directing all formatted text to _mainText. */
    private void setNormalMode() {
        _endnoteMode = false;
    }

    /** Write all accumulated endnotes to _mainText. */
    private void writeEndnotes() {
        for (String elem : _endnoteLines) {
            _pagePrinter.addLine(elem);
        }
    }

    /** True iff we are currently processing an endnote. */
    private boolean _endnoteMode;
    /** Number of next endnote. */
    private int _refNum;
}
