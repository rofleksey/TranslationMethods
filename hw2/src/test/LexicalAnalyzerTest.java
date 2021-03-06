

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LexicalAnalyzerTest {

    @Test
    public void oneCharTest() {
        test("a", Arrays.asList(Token.STR), Arrays.asList("a"));
    }

    @Test
    public void multipleCharsTest() {
        test("ora", Arrays.asList(Token.STR, Token.STR, Token.STR), Arrays.asList("o", "r", "a"));
    }

    @Test
    public void or1Test() {
        test("a|b", Arrays.asList(Token.STR, Token.OR, Token.STR), Arrays.asList("a", "b"));
    }

    @Test
    public void star1Test() {
        test("jo*", Arrays.asList(Token.STR, Token.STR, Token.STAR), Arrays.asList("j", "o"));
    }

    @Test
    public void group1Test() {
        test("(a)(b)", Arrays.asList(Token.LB, Token.STR, Token.RB, Token.LB, Token.STR, Token.RB), Arrays.asList("a", "b"));
    }

    @Test
    public void simple() {
        test("((a)*|b)*|c", Arrays.asList(Token.LB, Token.LB, Token.STR, Token.RB, Token.STAR, Token.OR, Token.STR, Token.RB, Token.STAR, Token.OR, Token.STR),
                Arrays.asList("a", "b", "c"));
    }

    @Test
    public void exampleTest() {
        test("((abc*b|a)*ab(aa|b*)b)*", Arrays.asList(Token.LB, Token.LB, Token.STR, Token.STR, Token.STR, Token.STAR, Token.STR, Token.OR,
                Token.STR, Token.RB, Token.STAR, Token.STR,Token.STR, Token.LB, Token.STR, Token.STR, Token.OR, Token.STR, Token.STAR, Token.RB, Token.STR, Token.RB, Token.STAR),
                Arrays.asList("a", "b", "c", "b", "a", "a", "b", "a", "a", "b", "b"));
    }

    private void test(String input, List<Token> answer, List<String> strings) {
        try {
            LexicalAnalyzer lex = new LexicalAnalyzer(input);
            int ii = 0;
            for (Token t : answer) {
                assertEquals(t, lex.nextToken());
                if (lex.curToken() == Token.STR) {
                    assertEquals(strings.get(ii++), lex.curStr());
                }
            }
            assertEquals(Token.END, lex.nextToken());
        } catch (ParseException e) {
            fail(e.getMessage());
        }
    }
}