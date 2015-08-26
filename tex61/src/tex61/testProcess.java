package tex61;

import java.io.PrintWriter;

public class testProcess {

    public static void main(String[] args) {
        String input = "conrad is cool     i wonder if this works";
        PrintWriter output = new PrintWriter(System.out);
        Controller cntrl = new Controller(output);
        InputParser src = new InputParser(input, cntrl);
        src.process();
        output.close();
    }
}
