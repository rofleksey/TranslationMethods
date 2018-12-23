package rofleksey;

import gen.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultTest {

    private void testArithmeticImpl(String test, int expected) throws ArithmeticLexer.ParseException {
        ArithmeticParser parser = new ArithmeticParser();
        ArithmeticParser.ExprContext tree = parser.parse(test);
        assertEquals(expected, tree.num);
    }

    @Test
    void testArithmetic1() throws ArithmeticLexer.ParseException {
        testArithmeticImpl("5",
                5);
    }

    @Test
    void testArithmetic2() throws ArithmeticLexer.ParseException {
        testArithmeticImpl("4+5",
                4 + 5);
    }

    @Test
    void testArithmetic3() throws ArithmeticLexer.ParseException {
        testArithmeticImpl("4*5",
                4 * 5);
    }

    @Test
    void testArithmetic4() throws ArithmeticLexer.ParseException {
        testArithmeticImpl("5+6*7+(8+9)*10+2*(3+4)+10",
                5 + 6 * 7 + (8 + 9) * 10 + 2 * (3 + 4) + 10);
    }

    @Test
    void testArithmetic5() throws ArithmeticLexer.ParseException {
        testArithmeticImpl("5+(6^3*7)^2+(8+9)*10^3+2*(3+4)+10+255^0",
                2303174);
    }

    @Test
    void testArithmetic6() throws ArithmeticLexer.ParseException {
        testArithmeticImpl("2**3**2",
                512);
    }

    @Test
    void testArithmetic7() throws ArithmeticLexer.ParseException {
        testArithmeticImpl("(2**3)**2",
                64);
    }

    @Test
    void testArithmeticFail1() throws ArithmeticLexer.ParseException {
        ArithmeticLexer.ParseException exception = assertThrows(ArithmeticLexer.ParseException.class, () -> {
            testArithmeticImpl("5+", 0);
        });
        assertEquals("Parse error: Invalid token EOF. Token types [ LB, MINUS, NUM ] expected at pos=3", exception.getMessage());
    }

    @Test
    void testArithmeticFail2() throws ArithmeticLexer.ParseException {
        ArithmeticLexer.ParseException exception = assertThrows(ArithmeticLexer.ParseException.class, () -> {
            testArithmeticImpl("5*5+()", 0);
        });
        assertEquals("Parse error: Invalid token RB. Token types [ LB, MINUS, NUM ] expected at pos=6", exception.getMessage());
    }

    @Test
    void testArithmeticFail3() throws ArithmeticLexer.ParseException {
        ArithmeticLexer.ParseException exception = assertThrows(ArithmeticLexer.ParseException.class, () -> {
            testArithmeticImpl("5*5+(1+2))))", 0);
        });
        assertEquals("Parse error: Got EOF before actual end of string at pos=10", exception.getMessage());
    }

    @Test
    void testArithmeticFail4() throws ArithmeticLexer.ParseException {
        ArithmeticLexer.ParseException exception = assertThrows(ArithmeticLexer.ParseException.class, () -> {
            testArithmeticImpl("5+5a", 0);
        });
        assertEquals("Parse error: Invalid input at pos=4", exception.getMessage());
    }

    private void testRegexImpl(String test, int stars, int ors) throws RegexLexer.ParseException {
        RegexParser parser = new RegexParser();
        RegexParser.RContext tree = parser.parse(test);
        assertEquals(stars, tree.stars);
        assertEquals(ors, tree.ors);
    }

    @Test
    void testRegex1() throws RegexLexer.ParseException {
        testRegexImpl("a|b(cd|e*)*f|get", 2, 3);
    }

    @Test
    void testRegex2() throws RegexLexer.ParseException {
        testRegexImpl("word*", 1, 0);
    }

    @Test
    void testRegexFail1() throws RegexLexer.ParseException {
        RegexLexer.ParseException exception = assertThrows(RegexLexer.ParseException.class, () -> {
            testRegexImpl("*", 0, 0);
        });
        assertEquals("Parse error: Invalid token STAR. Token types [ C, LB ] expected at pos=1", exception.getMessage());
    }

    @Test
    void testRegexFail2() throws RegexLexer.ParseException {
        RegexLexer.ParseException exception = assertThrows(RegexLexer.ParseException.class, () -> {
            testRegexImpl("a|b|", 0, 0);
        });
        assertEquals("Parse error: Invalid token EOF. Token types [ C, LB ] expected at pos=5", exception.getMessage());
    }

    @Test
    void testRegexFail3() throws RegexLexer.ParseException {
        RegexLexer.ParseException exception = assertThrows(RegexLexer.ParseException.class, () -> {
            testRegexImpl("word?", 0, 0);
        });
        assertEquals("Parse error: Invalid input at pos=5", exception.getMessage());
    }

    @Test
    void testRegexFail4() throws RegexLexer.ParseException {
        RegexLexer.ParseException exception = assertThrows(RegexLexer.ParseException.class, () -> {
            testRegexImpl("w|1", 0, 0);
        });
        assertEquals("Parse error: Invalid input at pos=3", exception.getMessage());
    }

    private void testSetsImpl(String test, String expected) throws SetLexer.ParseException {
        SetParser parser = new SetParser();
        SetParser.ExprContext tree = parser.parse(test);
        assertEquals(expected, tree.set.toString());
    }

    @Test
    void testSets1() throws SetLexer.ParseException {
        testSetsImpl("$all-a%y+($all-($all-<<a, d, e>>) + <<e>>)+x%x", "[a, d, e, x, z]");
    }

    @Test
    void testSets2() throws SetLexer.ParseException {
        testSetsImpl("a%f+f%x+<<y, z>>-$all", "[]");
    }

    @Test
    void testSetsFail1() throws SetLexer.ParseException {
        SetLexer.ParseException exception = assertThrows(SetLexer.ParseException.class, () -> {
            testSetsImpl("<<>>", "");
        });
        assertEquals("Parse error: Invalid token RS. Token types [ LETTER ] expected at pos=3", exception.getMessage());
    }

    @Test
    void testSetsFail2() throws SetLexer.ParseException {
        SetLexer.ParseException exception = assertThrows(SetLexer.ParseException.class, () -> {
            testSetsImpl("<>", "");
        });
        assertEquals("Parse error: Invalid input at pos=1", exception.getMessage());
    }

    @Test
    void testSetsFail3() throws SetLexer.ParseException {
        SetLexer.ParseException exception = assertThrows(SetLexer.ParseException.class, () -> {
            testSetsImpl("a%", "");
        });
        assertEquals("Parse error: Invalid token EOF. Token types [ LETTER ] expected at pos=3", exception.getMessage());
    }

    @Test
    void testSetsFail4() throws SetLexer.ParseException {
        SetLexer.ParseException exception = assertThrows(SetLexer.ParseException.class, () -> {
            testSetsImpl("<<a,>>+<<z>>", "");
        });
        assertEquals("Parse error: Invalid token RS. Token types [ LETTER ] expected at pos=5", exception.getMessage());
    }

    @Test
    void testSetsFail5() throws SetLexer.ParseException {
        SetLexer.ParseException exception = assertThrows(SetLexer.ParseException.class, () -> {
            testSetsImpl("<<a>>$+<<b>>", "");
        });
        assertEquals("Parse error: Invalid input at pos=6", exception.getMessage());
    }

    @Test
    void testSetsFail6() throws SetLexer.ParseException {
        SetLexer.ParseException exception = assertThrows(SetLexer.ParseException.class, () -> {
            testSetsImpl("<<a>>$all+<<b>>", "");
        });
        assertEquals("Parse error: Invalid token DOLLAR. Token types [ EOF, MINUS, PLUS, RB ] expected at pos=6", exception.getMessage());
    }

    @Test
    void testSetsFail7() throws SetLexer.ParseException {
        SetLexer.ParseException exception = assertThrows(SetLexer.ParseException.class, () -> {
            testSetsImpl("<<A1>>", "");
        });
        assertEquals("Parse error: Invalid input at pos=3", exception.getMessage());
    }
}