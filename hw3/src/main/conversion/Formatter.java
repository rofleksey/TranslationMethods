package conversion;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import parser.CLexer;
import parser.CParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Formatter {

    public static String format(String statement) throws IOException {
        CParser parser = getParser(statement);
        ParseTree a = parser.mainRule();
        //System.out.println(a.toStringTree(parser));
        FormatVisitor visitor = new FormatVisitor();
        visitor.visit(a);
        return visitor.result().trim();
    }

    private static CParser getParser(String statement) throws IOException {
        ANTLRInputStream is = new ANTLRInputStream(new ByteArrayInputStream(statement.getBytes()));
        CLexer lexer = new CLexer(is);
        TokenStream ts = new CommonTokenStream(lexer);
        return new CParser(ts);
    }
}
