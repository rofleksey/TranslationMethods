package rofleksey;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

class ParserGeneratorTest {
    private String getFileContents(String name) throws IOException {
        return Files.lines(Paths.get(new File(getClass().getResource(name).getPath()).getCanonicalPath())).collect(Collectors.joining("\n")).trim();
    }

    @Test
    void textArithmetic() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("arithmetic.rofl"));
        LL1Table table = g.genLL1Table();
        System.out.println("LL1: " + table.isLL1());
        ParserGenerator generator = new ParserGenerator(g, table, "Arithmetic", "rofleksey", Paths.get("./src/test/rofleksey/"));
        System.out.println();
        generator.generate();
    }

    @Test
    void textRegex() throws IOException {
        Grammar g = GrammarParser.fromString(getFileContents("regex.rofl"));
        LL1Table table = g.genLL1Table();
        System.out.println("LL1: " + table.isLL1());
        ParserGenerator generator = new ParserGenerator(g, table, "Regex", "rofleksey", Paths.get("./src/test/rofleksey/"));
        System.out.println();
        generator.generate();
    }
}