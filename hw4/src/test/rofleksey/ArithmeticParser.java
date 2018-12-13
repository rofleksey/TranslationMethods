package rofleksey;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

public class ArithmeticParser {
    private ArithmeticLexer lex;

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
        private final ArithmeticLexer.Token token;

        private TerminalContext(ArithmeticLexer.Token token) {
            super(token.text);
            this.token = token;
        }

        public ArithmeticLexer.Token getToken() {
            return token;
        }
    }

    public static class EContext extends Tree {
        public int num;
        public TContext e1;
        public E1Context e2;


        private EContext(Tree... children) {
            super("E", children);
        }

        private void calc11() {
            num = e1.num + e2.num;
        }

    }

    public static class E1Context extends Tree {
        public int num;
        public TContext e1;
        public E1Context e2;


        private E1Context(Tree... children) {
            super("E1", children);
        }

        private void calc12() {
            num = e1.num + e2.num;
        }

        private void calc13() {
            num = 0;
        }

    }

    public static class TContext extends Tree {
        public int num;
        public FContext e1;
        public T1Context e2;


        private TContext(Tree... children) {
            super("T", children);
        }

        private void calc14() {
            num = e1.num * e2.num;
        }

    }

    public static class T1Context extends Tree {
        public int num;
        public FContext e1;
        public T1Context e2;


        private T1Context(Tree... children) {
            super("T1", children);
        }

        private void calc15() {
            num = e1.num * e2.num;
        }

        private void calc16() {
            num = 1;
        }

    }

    public static class FContext extends Tree {
        public int num;
        public TerminalContext e;
        public EContext e1;


        private FContext(Tree... children) {
            super("F", children);
        }

        private void calc17() {
            num = e.getToken().toInt();
        }

        private void calc18() {
            num = e1.num;
        }

    }


    private TerminalContext NUM() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case NUM:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ NUM ] expected", lex.curPos());
        }
    }

    private TerminalContext MULT() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case MULT:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ MULT ] expected", lex.curPos());
        }
    }

    private TerminalContext PLUS() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case PLUS:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ PLUS ] expected", lex.curPos());
        }
    }

    private TerminalContext LB() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case LB:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ LB ] expected", lex.curPos());
        }
    }

    private TerminalContext RB() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case RB:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ RB ] expected", lex.curPos());
        }
    }


    private EContext E() throws ArithmeticLexer.ParseException {
        EContext result;
        switch (lex.curToken().type) {
            case LB:
            case NUM: {
                TContext c1 = T();
                E1Context c2 = E1();
                result = new EContext(c1, c2);
                result.e1 = c1;
                result.e2 = c2;
                result.calc11();
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ LB, NUM ] expected", lex.curPos());
        }
    }

    private E1Context E1() throws ArithmeticLexer.ParseException {
        E1Context result;
        switch (lex.curToken().type) {
            case PLUS: {
                TerminalContext c1 = PLUS();
                TContext c2 = T();
                E1Context c3 = E1();
                result = new E1Context(c1, c2, c3);
                result.e1 = c2;
                result.e2 = c3;
                result.calc12();
                return result;
            }
            case EOF:
            case RB: {
                result = new E1Context();
                result.calc13();
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ EOF, PLUS, RB ] expected", lex.curPos());
        }
    }

    private TContext T() throws ArithmeticLexer.ParseException {
        TContext result;
        switch (lex.curToken().type) {
            case LB:
            case NUM: {
                FContext c1 = F();
                T1Context c2 = T1();
                result = new TContext(c1, c2);
                result.e1 = c1;
                result.e2 = c2;
                result.calc14();
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ LB, NUM ] expected", lex.curPos());
        }
    }

    private T1Context T1() throws ArithmeticLexer.ParseException {
        T1Context result;
        switch (lex.curToken().type) {
            case EOF:
            case PLUS:
            case RB: {
                result = new T1Context();
                result.calc16();
                return result;
            }
            case MULT: {
                TerminalContext c1 = MULT();
                FContext c2 = F();
                T1Context c3 = T1();
                result = new T1Context(c1, c2, c3);
                result.e1 = c2;
                result.e2 = c3;
                result.calc15();
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ EOF, MULT, PLUS, RB ] expected", lex.curPos());
        }
    }

    private FContext F() throws ArithmeticLexer.ParseException {
        FContext result;
        switch (lex.curToken().type) {
            case NUM: {
                TerminalContext c1 = NUM();
                result = new FContext(c1);
                result.e = c1;
                result.calc17();
                return result;
            }
            case LB: {
                TerminalContext c1 = LB();
                EContext c2 = E();
                TerminalContext c3 = RB();
                result = new FContext(c1, c2, c3);
                result.e1 = c2;
                result.calc18();
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ LB, NUM ] expected", lex.curPos());
        }
    }

    public EContext parse(String s) throws ArithmeticLexer.ParseException {
        return parse(new StringReader(s));
    }

    public EContext parse(Reader r) throws ArithmeticLexer.ParseException {
        lex = new ArithmeticLexer(r);
        lex.nextToken();
        EContext t = E();
        if (lex.curToken().type != ArithmeticLexer.TokenType.EOF) {
            throw new ArithmeticLexer.ParseException("Unexpected end of input", lex.curPos());
        }
        return t;
    }
}
