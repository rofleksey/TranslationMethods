package rofleksey;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

class GrammarParserTest {

    private String getFileContents(String name) throws IOException {
        return Files.lines(Paths.get(new File(getClass().getResource(name).getPath()).getCanonicalPath())).collect(Collectors.joining("\n")).trim();
    }

    private void logGrammar(Grammar g) {
        g.prepare();
        System.out.println(g);
        System.out.println(g.genLL1Table().isLL1());
    }

    @Test
    void testArithmetic() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("arithmetic.rofl"));
        logGrammar(g);
    }

    @Test
    void testRegex() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("regex.rofl"));
        logGrammar(g);
    }
}