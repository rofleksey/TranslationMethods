package rofleksey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void testArithmetic() throws ArithmeticLexer.ParseException {
        ArithmeticParser parser = new ArithmeticParser();

        String test = "5+6*7+(8+9)*10+2*(3+4)+10";
        int expected = 5 + 6 * 7 + (8 + 9) * 10 + 2 * (3 + 4) + 10;
        ArithmeticParser.EContext tree = parser.parse(test);
        Assertions.assertEquals(expected, tree.num);

        test = "5";
        expected = 5;
        tree = parser.parse(test);
        Assertions.assertEquals(expected, tree.num);

        test = "4+5";
        expected = 4 + 5;
        tree = parser.parse(test);
        Assertions.assertEquals(expected, tree.num);
    }

    @Test
    void testRegex() throws RegexLexer.ParseException {
        RegexParser parser = new RegexParser();
        String test = "a|b(cd|e*)*f|get";
        RegexParser.RContext tree = parser.parse(test);
        Assertions.assertEquals(2, tree.stars);
        Assertions.assertEquals(3, tree.ors);
    }
}