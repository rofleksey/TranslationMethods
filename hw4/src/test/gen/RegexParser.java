package gen;


import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

public class RegexParser {
    private RegexLexer lex;

    public static class Tree {
        private final String name;
        private final ArrayList<Tree> children;
        private Object userData;
        boolean isTerminal;

        private Tree(String name) {
            this.name = name;
            children = new ArrayList<>();
        }

        void add(Tree... trees) {
            children.addAll(Arrays.asList(trees));
        }
        private void getText(StringBuilder builder) {
            if (isTerminal) {
                builder.append(name);
            } else {
                for (Tree t : children) {
                    t.getText(builder);
                }
            }
        }
        public String getText() {
            if (isTerminal) {
                return name;
            } else {
                StringBuilder builder = new StringBuilder();
                getText(builder);
                return builder.toString();
            }
        }
        public boolean isTerminal() {
            return isTerminal;
        }
        public String getName() {
            return name;
        }
        public ArrayList<Tree> getChildren() {
            return children;
        }
        public void setUserData(Object o) {
            userData = o;
        }
        public Object getUserData() {
            return userData;
        }
        @Override
        public String toString() {
            return children.size() == 0 ? name : "<" + name + ": " + children + ">";
        }
    }

    public static class TerminalContext extends Tree {
        private final RegexLexer.Token token;
        private TerminalContext(RegexLexer.Token token) {
            super(token.text);
            this.token = token;
            isTerminal = true;
        }
        public RegexLexer.Token getToken() {
            return token;
        }
    }



    public static class RContext extends Tree {
        public int ors;
        public int stars;
        public DContext e1;
        public RFlexContext e2;


        private RContext() {
            super("R");
        }
    }

    public static class DContext extends Tree {
        public int ors;
        public int stars;
        public SContext e1;
        public DFlexContext e2;


        private DContext() {
            super("D");
        }
    }

    public static class SContext extends Tree {
        public int ors;
        public int stars;
        public GContext e1;
        public SFlexContext e2;


        private SContext() {
            super("S");
        }
    }

    public static class GContext extends Tree {
        public int ors;
        public int stars;
        public RContext e1;


        private GContext() {
            super("G");
        }
    }

    public static class RFlexContext extends Tree {
        public int ors;
        public int stars;
        public DContext e1;
        public RFlexContext e2;


        private RFlexContext() {
            super("RFlex");
        }
    }

    public static class DFlexContext extends Tree {
        public int ors;
        public int stars;
        public SContext e1;
        public DFlexContext e2;


        private DFlexContext() {
            super("DFlex");
        }
    }

    public static class SFlexContext extends Tree {
        public int ors;
        public int stars;


        private SFlexContext() {
            super("SFlex");
        }
    }


    private TerminalContext lb() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case LB:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ LB ] expected", lex.lastPos());
        }
    }

    private TerminalContext rb() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case RB:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ RB ] expected", lex.lastPos());
        }
    }

    private TerminalContext or() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case OR:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ OR ] expected", lex.lastPos());
        }
    }

    private TerminalContext star() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case STAR:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ STAR ] expected", lex.lastPos());
        }
    }

    private TerminalContext c() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case C:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ C ] expected", lex.lastPos());
        }
    }


    private RContext R() throws RegexLexer.ParseException {
        RContext result = new RContext();


        switch (lex.curToken().type) {
            case C:
            case LB: {
                DContext e1 = D();
                RFlexContext e2 = RFlex();
                result.ors = e1.ors + e2.ors;
                result.stars = e1.stars + e2.stars;
                ;
                result.add(e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ C, LB ] expected", lex.lastPos());
        }
    }

    private DContext D() throws RegexLexer.ParseException {
        DContext result = new DContext();


        switch (lex.curToken().type) {
            case C:
            case LB: {
                SContext e1 = S();
                DFlexContext e2 = DFlex();
                result.ors = e1.ors + e2.ors;
                result.stars = e1.stars + e2.stars;
                ;
                result.add(e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ C, LB ] expected", lex.lastPos());
        }
    }

    private SContext S() throws RegexLexer.ParseException {
        SContext result = new SContext();


        switch (lex.curToken().type) {
            case C:
            case LB: {
                GContext e1 = G();
                SFlexContext e2 = SFlex();
                result.ors = e1.ors + e2.ors;
                result.stars = e1.stars + e2.stars;
                ;
                result.add(e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ C, LB ] expected", lex.lastPos());
        }
    }

    private GContext G() throws RegexLexer.ParseException {
        GContext result = new GContext();


        switch (lex.curToken().type) {
            case LB: {
                TerminalContext c1 = lb();
                RContext e1 = R();
                TerminalContext c3 = rb();
                result.ors = e1.ors;
                result.stars = e1.stars;
                ;
                result.add(c1, e1, c3);
                result.e1 = e1;
                return result;
            }
            case C: {
                TerminalContext c1 = c();
                result.add(c1);
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ C, LB ] expected", lex.lastPos());
        }
    }

    private RFlexContext RFlex() throws RegexLexer.ParseException {
        RFlexContext result = new RFlexContext();


        switch (lex.curToken().type) {
            case OR: {
                TerminalContext c1 = or();
                DContext e1 = D();
                RFlexContext e2 = RFlex();
                result.ors = e1.ors + e2.ors + 1;
                result.stars = e1.stars + e2.stars;
                ;
                result.add(c1, e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }
            case EOF:
            case RB: {
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ EOF, OR, RB ] expected", lex.lastPos());
        }
    }

    private DFlexContext DFlex() throws RegexLexer.ParseException {
        DFlexContext result = new DFlexContext();


        switch (lex.curToken().type) {
            case EOF:
            case OR:
            case RB: {
                return result;
            }
            case C:
            case LB: {
                SContext e1 = S();
                DFlexContext e2 = DFlex();
                result.ors = e1.ors + e2.ors;
                result.stars = e1.stars + e2.stars;
                ;
                result.add(e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ EOF, C, LB, OR, RB ] expected", lex.lastPos());
        }
    }

    private SFlexContext SFlex() throws RegexLexer.ParseException {
        SFlexContext result = new SFlexContext();


        switch (lex.curToken().type) {
            case STAR: {
                TerminalContext c1 = star();
                result.stars = 1;
                ;
                result.add(c1);
                return result;
            }
            case EOF:
            case C:
            case LB:
            case OR:
            case RB: {
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ EOF, C, LB, OR, RB, STAR ] expected", lex.lastPos());
        }
    }

    public RContext parse(String s) throws RegexLexer.ParseException {
        return parse(new StringReader(s));
    }

    public RContext parse(Reader r) throws RegexLexer.ParseException {
        lex = new RegexLexer(r);
        lex.nextToken();
        RContext t = R();
        if (lex.curToken().type != RegexLexer.TokenType.EOF) {
            throw new RegexLexer.ParseException("Got EOF before actual end of string", lex.lastPos());
        }
        return t;
    }
}
