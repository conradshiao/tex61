package tex61;

import java.io.PrintWriter;

/** A PageAssembler that sends lines immediately to a PrintWriter, with
 *  terminating newlines.
 *  @author Conrad Shiao, cs61b-cz
 */
class PagePrinter extends PageAssembler {

    /** Typewriter that this PagePrinter uses. */
    private PrintWriter _out;

    /** A new PagePrinter that sends lines to OUT. */
    PagePrinter(PrintWriter out) {
        setTextHeight(Defaults.TEXT_HEIGHT);
        _out = out;
    }

    /** Print LINE to my output. */
    @Override
    void write(String line) {
        _out.println(line);
    }
}
