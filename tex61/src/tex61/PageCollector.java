package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
 *  @author Conrad Shiao, cs61b-cz
 */
class PageCollector extends PageAssembler {

    /** A new PageCollector that stores lines in OUT. */
    PageCollector(List<String> out) {
        _myList = out;
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        _myList.add(line);
    }

    /** Lines found in this PageCollector. */
    private List<String> _myList;

}
