package conversion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

class FormatVisitorTest {

    private String getFileContents(String name) throws IOException {
        return Files.lines(Paths.get(new File(getClass().getResource(name).getPath()).getCanonicalPath())).collect(Collectors.joining("\n")).trim();
    }

    @Test
    void test() throws URISyntaxException, IOException {
        String input = getFileContents("app.c");
        String expected = getFileContents("result.c");
        Assertions.assertEquals(expected, Formatter.format(input));
    }
}