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
import java.util.Map;
import java.util.TreeMap;

public class GrammarParser {
    private ParserRuleContext root;
    private RoflParser parser;
    private TreeMap<String, StringBuilder> optionsBuilder;

    private GrammarParser(RoflParser parser) {
        this.parser = parser;
    }

    private Grammar parse() {
        if (root == null) {
            root = parser.main();
        }
        //System.out.println(root.toStringTree(parser));
        Grammar grammar = new Grammar();
        optionsBuilder = new TreeMap<>();
        ParseTreeWalker walker = new ParseTreeWalker();
        GrammarListener listener = new GrammarListener(grammar);
        walker.walk(listener, root);
        listener.finalizeOptions();
        grammar.prepare();
        return grammar; //TODO: return grammar
    }


    private class GrammarListener extends RoflBaseListener {
        private ParserRule lastRule;
        private Branch lastBranch;
        private PRuleItem lastItem;
        private final Grammar grammar;

        private GrammarListener(Grammar g) {
            grammar = g;
        }

        private void finalizeOptions() {
            for (Map.Entry<String, StringBuilder> op : optionsBuilder.entrySet()) {
                grammar.addOption(op.getKey(), op.getValue().toString());
            }
            optionsBuilder.clear();
        }

        @Override
        public void enterOptionsRule(RoflParser.OptionsRuleContext ctx) {
            String name = ctx.name.getText();
            String content = ctx.content.getText().substring(1, ctx.content.getText().length() - 1);
            if (optionsBuilder.containsKey(name)) {
                optionsBuilder.get(name).append("\n").append(content);
            } else {
                optionsBuilder.put(name, new StringBuilder(content));
            }
        }

        @Override
        public void enterPRule(RoflParser.PRuleContext ctx) {
            lastRule = new ParserRule(ctx.name.getText());
            if (ctx.initCode != null) {
                lastRule.setInitCode(ctx.initCode.getText());
            }
            grammar.pRules.add(lastRule);
        }

        @Override
        public void enterLocalField(RoflParser.LocalFieldContext ctx) {
            lastRule.localFields.add(new Pair<>(ctx.type.getText(), ctx.name.getText()));
        }

        @Override
        public void enterBranch(RoflParser.BranchContext ctx) {
            lastBranch = new Branch(lastRule);
            lastRule.branches.add(lastBranch);
            if (ctx.code != null) {
                lastBranch.setInitCode(ctx.code.getText());
            }
        }

        @Override
        public void enterPItem(RoflParser.PItemContext ctx) {
            if (ctx.name != null) {
                lastItem = new PRuleItem(ctx.name.getText(), ctx.content.getText());
            } else if (ctx.content != null) {
                lastItem = new PRuleItem(ctx.content.getText());
            } else {
                lastItem = new PRuleItem(Grammar.EPSILON);
            }
            if (ctx.code != null) {
                lastItem.setCode(ctx.code.getText());
            }
            lastBranch.items.add(lastItem);
        }

        @Override
        public void enterArgList(RoflParser.ArgListContext ctx) {
            lastItem.arguments.add(ctx.name.getText());
        }

        @Override
        public void enterFuncField(RoflParser.FuncFieldContext ctx) {
            lastRule.funcFields.add(new Pair<>(ctx.type.getText(), ctx.name.getText()));
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
