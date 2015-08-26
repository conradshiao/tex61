package tex61;

import java.util.ArrayList;

public class Proj1Tests {
    
    /** public static void main(String[] args) {
        ArrayList<String> _line = new ArrayList<String>();
        _line.add("conrad");
        _line.add("is");
        _line.add("the");
        _line.add("best");
        String temp = emitLine(_line, 4, 7);
        System.out.println(temp);
    } */
    
    /** Transfer contents of _words to _pages, adding INDENT characters of
     *  indentation, and a total of SPACES spaces between words, evenly
     *  distributed.  Assumes _words is not empty.  Clears _words and _chars. */
    static private String emitLine(ArrayList<String> _line, int indent, int spaces) {
        String result = "";
        int prevSpacing = 0;
        int k = 1;
        for (; k < _line.size(); k++) {
            double temp = _line.size() - 1;
            int totalBlanks = (int) Math.floor(0.5 + spaces * k
                    / temp);
            System.out.println("spaces is: " + _line.size());
            System.out.println("total Blanks has value of: " + totalBlanks);
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
        return result;
    }

}
