package rofleksey;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

public class RegexParser {
    private RegexLexer lex;

    public static class Tree {
        private final String name;
        private final Tree[] children;
        private Object userData;

        private Tree(String name, Tree... children) {
            this.name = name;
            this.children = children;
        }

        public String getName() {
            return name;
        }

        public Tree[] getChildren() {
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
            return children.length == 0 ? name : "<" + name + ": " + Arrays.toString(children) + ">";
        }
    }

    public static class TerminalContext extends Tree {
        private final RegexLexer.Token token;

        private TerminalContext(RegexLexer.Token token) {
            super(token.text);
            this.token = token;
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


        private RContext(Tree... children) {
            super("R", children);
        }

        private void calc0() {
            ors = e1.ors + e2.ors;
            stars = e1.stars + e2.stars;
            ;
        }

    }

    public static class DContext extends Tree {
        public int ors;
        public int stars;
        public SContext e1;
        public DFlexContext e2;


        private DContext(Tree... children) {
            super("D", children);
        }

        private void calc1() {
            ors = e1.ors + e2.ors;
            stars = e1.stars + e2.stars;
            ;
        }

    }

    public static class SContext extends Tree {
        public int ors;
        public int stars;
        public GContext e1;
        public SFlexContext e2;


        private SContext(Tree... children) {
            super("S", children);
        }

        private void calc2() {
            ors = e1.ors + e2.ors;
            stars = e1.stars + e2.stars;
            ;
        }

    }

    public static class GContext extends Tree {
        public int ors;
        public int stars;
        public RContext e1;


        private GContext(Tree... children) {
            super("G", children);
        }

        private void calc4() {
            ors = e1.ors;
            stars = e1.stars;
            ;
        }

    }

    public static class RFlexContext extends Tree {
        public int ors;
        public int stars;
        public DContext e1;
        public RFlexContext e2;


        private RFlexContext(Tree... children) {
            super("RFlex", children);
        }

        private void calc5() {
            ors = e1.ors + e2.ors + 1;
            stars = e1.stars + e2.stars;
            ;
        }

    }

    public static class DFlexContext extends Tree {
        public int ors;
        public int stars;
        public SContext e1;
        public DFlexContext e2;


        private DFlexContext(Tree... children) {
            super("DFlex", children);
        }

        private void calc7() {
            ors = e1.ors + e2.ors;
            stars = e1.stars + e2.stars;
            ;
        }

    }

    public static class SFlexContext extends Tree {
        public int ors;
        public int stars;


        private SFlexContext(Tree... children) {
            super("SFlex", children);
        }

        private void calc9() {
            stars = 1;
            ;
        }

    }


    private TerminalContext lb() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case LB:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ LB ] expected", lex.curPos());
        }
    }

    private TerminalContext rb() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case RB:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ RB ] expected", lex.curPos());
        }
    }

    private TerminalContext or() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case OR:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ OR ] expected", lex.curPos());
        }
    }

    private TerminalContext star() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case STAR:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ STAR ] expected", lex.curPos());
        }
    }

    private TerminalContext c() throws RegexLexer.ParseException {
        switch (lex.curToken().type) {
            case C:
                RegexLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ C ] expected", lex.curPos());
        }
    }


    private RContext R() throws RegexLexer.ParseException {
        RContext result;
        switch (lex.curToken().type) {
            case C:
            case LB: {
                DContext c1 = D();
                RFlexContext c2 = RFlex();
                result = new RContext(c1, c2);
                result.e1 = c1;
                result.e2 = c2;
                result.calc0();
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ C, LB ] expected", lex.curPos());
        }
    }

    private DContext D() throws RegexLexer.ParseException {
        DContext result;
        switch (lex.curToken().type) {
            case C:
            case LB: {
                SContext c1 = S();
                DFlexContext c2 = DFlex();
                result = new DContext(c1, c2);
                result.e1 = c1;
                result.e2 = c2;
                result.calc1();
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ C, LB ] expected", lex.curPos());
        }
    }

    private SContext S() throws RegexLexer.ParseException {
        SContext result;
        switch (lex.curToken().type) {
            case C:
            case LB: {
                GContext c1 = G();
                SFlexContext c2 = SFlex();
                result = new SContext(c1, c2);
                result.e1 = c1;
                result.e2 = c2;
                result.calc2();
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ C, LB ] expected", lex.curPos());
        }
    }

    private GContext G() throws RegexLexer.ParseException {
        GContext result;
        switch (lex.curToken().type) {
            case LB: {
                TerminalContext c1 = lb();
                RContext c2 = R();
                TerminalContext c3 = rb();
                result = new GContext(c1, c2, c3);
                result.e1 = c2;
                result.calc4();
                return result;
            }
            case C: {
                TerminalContext c1 = c();
                result = new GContext(c1);
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ C, LB ] expected", lex.curPos());
        }
    }

    private RFlexContext RFlex() throws RegexLexer.ParseException {
        RFlexContext result;
        switch (lex.curToken().type) {
            case OR: {
                TerminalContext c1 = or();
                DContext c2 = D();
                RFlexContext c3 = RFlex();
                result = new RFlexContext(c1, c2, c3);
                result.e1 = c2;
                result.e2 = c3;
                result.calc5();
                return result;
            }
            case EOF:
            case RB: {
                result = new RFlexContext();
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ EOF, OR, RB ] expected", lex.curPos());
        }
    }

    private DFlexContext DFlex() throws RegexLexer.ParseException {
        DFlexContext result;
        switch (lex.curToken().type) {
            case EOF:
            case OR:
            case RB: {
                result = new DFlexContext();
                return result;
            }
            case C:
            case LB: {
                SContext c1 = S();
                DFlexContext c2 = DFlex();
                result = new DFlexContext(c1, c2);
                result.e1 = c1;
                result.e2 = c2;
                result.calc7();
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ EOF, C, LB, OR, RB ] expected", lex.curPos());
        }
    }

    private SFlexContext SFlex() throws RegexLexer.ParseException {
        SFlexContext result;
        switch (lex.curToken().type) {
            case STAR: {
                TerminalContext c1 = star();
                result = new SFlexContext(c1);
                result.calc9();
                return result;
            }
            case EOF:
            case C:
            case LB:
            case OR:
            case RB: {
                result = new SFlexContext();
                return result;
            }

            default:
                throw new RegexLexer.ParseException("Invalid character. Token types [ EOF, C, LB, OR, RB, STAR ] expected", lex.curPos());
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
            throw new RegexLexer.ParseException("Unexpected end of input", lex.curPos());
        }
        return t;
    }
}
