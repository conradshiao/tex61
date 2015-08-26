package tex61;

import static org.junit.Assert.*;
import java.util.ArrayList;

import org.junit.Test;

/** Accumulated tests for LineAssembler.java
 * @author Conrad Shiao
 */

public class LineAssemblerTest {


    private void setupAssembler() {
        setupCollector();
        setupPageCollector();
        _lineAssembler = new LineAssembler(_pageCollector, true);
    }

    private void setupCollector() {
        _outList = new ArrayList<String>();
    }

    private void setupPageCollector() {
        _pageCollector = new PageCollector(_outList);
    }

    private void setupTestLines() {
        _testLines = new ArrayList<String>();
        _testLines.add("4567");
        _testLines.add("4567");
        _testLines.add("4567");
    }

    @Test
    public void testAddLines() {
        setupAssembler();
        setupTestLines();
        for (int i = 0; i <= 2; i++) {
            _lineAssembler.addText("4567");
            _lineAssembler.finishWord();
            _lineAssembler.addLine(_lineAssembler.toString(
                    _lineAssembler.getLine()));
            _lineAssembler.getLine().clear();
        }
        assertEquals("current line has wrong contents", _outList, _testLines);
    }

    @Test
    public void testFinishWord() {
        setupAssembler();
        for (int k = 4; k <= 7; k++)  {
            _lineAssembler.addText("" + k);
        }
        _lineAssembler.finishWord();
        assertEquals("current word has wrong contents", "4567",
                _lineAssembler.getLine().get(0));
    }

    @Test
    public void testAddText() {
        setupAssembler();
        for (int j = 4; j <= 7; j++) {
            _lineAssembler.addText("" + j);
        }
        assertEquals("current word has wrong contents", "4567",
                _lineAssembler.getCurrentWord());
    }

    /** What will be printed out. */
    private ArrayList<String> _outList;
    /** What will be tested. */
    private ArrayList<String> _testLines;
    /** The Line Assembler used. */
    private LineAssembler _lineAssembler;
    /** Page Collector where everything is stored. */
    private PageAssembler _pageCollector;
}
