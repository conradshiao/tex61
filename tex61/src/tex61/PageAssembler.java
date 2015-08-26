package tex61;


/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Conrad Shiao, cs61b-cz
 */
abstract class PageAssembler {

    /** Create a new PageAssembler that sends its output to OUT.
     *  Initially, its text height is unlimited. It prepends a form
     *  feed character to the first line of each page except the first. */
    PageAssembler() {
    }

    /** Add LINE to the current page, starting a new page with it if
     *  the previous page is full. A null LINE indicates a skipped line,
     *  and has no effect at the top of a page. */
    void addLine(String line) {
        if (_numberOfLines != _textHeight && _numberOfLines != 0) {
            write(line);
            _numberOfLines++;
        } else {
            if (!line.equals("")) {
                if (_numberOfLines == _textHeight) {
                    line = "\f" + line;
                    _numberOfLines = 1;
                } else {
                    _numberOfLines++;
                }
                write(line);
            }
        }
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        _textHeight = val;
    }

    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);

    /** Setting for my text height. */
    private int _textHeight = Integer.MAX_VALUE;

    /** The current number of lines assembled. */
    private int _numberOfLines;
}
