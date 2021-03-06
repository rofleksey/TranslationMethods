import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParserTest {

    @Test
    public void oneCharTest() {
        test("a");
    }

    @Test
    public void multipleCharsTest() {
        test("ora123ari");
    }

    @Test
    public void or1Test() {
        test("a|b");
    }

    @Test
    public void star1Test() {
        test("jo1taro*");
    }

    @Test
    public void group1Test() {
        test("(a)(b)");
    }

    @Test
    public void simple() {
        test("((a)*|b)*|c");
    }

    @Test
    public void exampleTest() {
        test("((abc*b|a)*ab(aa|b*)b)*");
    }

    private void test(String what) {
        Parser parser = new Parser();
        try {
            Tree t = parser.parse(what);
            assertEquals(what, t.toString());
        } catch (ParseException e) {
            fail(e.getMessage());
        }
    }
}