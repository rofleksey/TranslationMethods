package rofleksey;

import org.junit.jupiter.api.Assertions;
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
        LL1Table table = g.genLL1Table();
        System.out.println(table.isLL1());
        Assertions.assertTrue(table.isLL1());
        System.out.println();
    }

    @Test
    void testFF() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("ff.rofl"));
        logGrammar(g);
    }

    @Test
    void testArithmetic() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("arithmetic.rofl"));
        logGrammar(g);
    }

    @Test
    void testSets() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("sets.rofl"));
        logGrammar(g);
    }

    @Test
    void testRegex() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("regex.rofl"));
        logGrammar(g);
    }
}