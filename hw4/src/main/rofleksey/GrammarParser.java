package rofleksey;

import grammar.RoflBaseListener;
import grammar.RoflLexer;
import grammar.RoflParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GrammarParser {
    private ParserRuleContext root;
    private RoflParser parser;

    private GrammarParser(RoflParser parser) {
        this.parser = parser;
    }

    private Grammar parse() {
        if (root == null) {
            root = parser.main();
        }
        //System.out.println(root.toStringTree(parser));
        Grammar grammar = new Grammar();
        ParseTreeWalker walker = new ParseTreeWalker();
        GrammarListener listener = new GrammarListener(grammar);
        walker.walk(listener, root);
        grammar.prepare();
        return grammar; //TODO: return grammar
    }

    private class GrammarListener extends RoflBaseListener {
        private ParserRule lastRule;
        private Branch lastBranch;
        private final Grammar grammar;

        private GrammarListener(Grammar g) {
            grammar = g;
        }

        @Override
        public void enterPRule(RoflParser.PRuleContext ctx) {
            lastRule = new ParserRule(ctx.name.getText());
            grammar.pRules.add(lastRule);
        }

        @Override
        public void enterPField(RoflParser.PFieldContext ctx) {
            lastRule.fields.add(new Pair<>(ctx.type.getText(), ctx.name.getText()));
        }

        @Override
        public void enterBranch(RoflParser.BranchContext ctx) {
            lastBranch = new Branch();
            lastRule.branches.add(lastBranch);
            if (ctx.code != null) {
                lastBranch.code = ctx.code.getText();
            }
        }

        @Override
        public void enterPItem(RoflParser.PItemContext ctx) {
            PRuleItem item;
            if (ctx.name != null) {
                item = new PRuleItem(ctx.name.getText(), ctx.content.getText());
            } else if (ctx.content != null) {
                item = new PRuleItem(ctx.content.getText());
            } else {
                item = new PRuleItem(Grammar.EPSILON);
            }
            lastBranch.items.add(item);
        }

        @Override
        public void enterLRule(RoflParser.LRuleContext ctx) {
            grammar.lRules.add(new LRuleItem(ctx.name.getText(), ctx.content.getText(), ctx.special));
        }
    }

    public static Grammar fromString(String text) throws IOException {
        text = text + "\n";
        RoflParser parser = getParser(new ByteArrayInputStream(text.getBytes()));
        GrammarParser gParser = new GrammarParser(parser);
        return gParser.parse();
    }

    public static Grammar fromFile(String path) throws IOException {
        try (InputStream is = Files.newInputStream(Paths.get(path))) {
            RoflParser parser = getParser(is);
            GrammarParser gParser = new GrammarParser(parser);
            return gParser.parse();
        }
    }

    private static RoflParser getParser(InputStream input) throws IOException {
        ANTLRInputStream is = new ANTLRInputStream(input);
        RoflLexer lexer = new RoflLexer(is);
        TokenStream ts = new CommonTokenStream(lexer);
        return new RoflParser(ts);
    }
}
