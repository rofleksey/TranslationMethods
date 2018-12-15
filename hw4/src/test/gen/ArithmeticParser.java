package gen;


import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ArithmeticParser {
    private ArithmeticLexer lex;

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
        private final ArithmeticLexer.Token token;
        private TerminalContext(ArithmeticLexer.Token token) {
            super(token.text);
            this.token = token;
            isTerminal = true;
        }
        public ArithmeticLexer.Token getToken() {
            return token;
        }
    }


    public static class EContext extends Tree {
        public int num;
        public TContext e1;
        public E1Context e2;


        private EContext() {
            super("E");
        }
    }

    public static class E1Context extends Tree {
        public int num;
        public TContext e1;
        public E1Context e2;


        private E1Context() {
            super("E1");
        }
    }

    public static class TContext extends Tree {
        public int num;
        public FContext e1;
        public T1Context e2;


        private TContext() {
            super("T");
        }
    }

    public static class T1Context extends Tree {
        public int num;
        public FContext e1;
        public T1Context e2;


        private T1Context() {
            super("T1");
        }
    }

    public static class FContext extends Tree {
        public int num;
        public TerminalContext e;
        public EContext e1;


        private FContext() {
            super("F");
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
        EContext result = new EContext();


        switch (lex.curToken().type) {
            case LB:
            case NUM: {
                TContext e1 = T();
                E1Context e2 = E1();
                result.num = e1.num + e2.num;
                result.add(e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ LB, NUM ] expected", lex.curPos());
        }
    }

    private E1Context E1() throws ArithmeticLexer.ParseException {
        E1Context result = new E1Context();


        switch (lex.curToken().type) {
            case PLUS: {
                TerminalContext c1 = PLUS();
                TContext e1 = T();
                E1Context e2 = E1();
                result.num = e1.num + e2.num;
                result.add(c1, e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }
            case EOF:
            case RB: {
                result.num = 0;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ EOF, PLUS, RB ] expected", lex.curPos());
        }
    }

    private TContext T() throws ArithmeticLexer.ParseException {
        TContext result = new TContext();


        switch (lex.curToken().type) {
            case LB:
            case NUM: {
                FContext e1 = F();
                T1Context e2 = T1();
                result.num = e1.num * e2.num;
                result.add(e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ LB, NUM ] expected", lex.curPos());
        }
    }

    private T1Context T1() throws ArithmeticLexer.ParseException {
        T1Context result = new T1Context();


        switch (lex.curToken().type) {
            case MULT: {
                TerminalContext c1 = MULT();
                FContext e1 = F();
                T1Context e2 = T1();
                result.num = e1.num * e2.num;
                result.add(c1, e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }
            case EOF:
            case PLUS:
            case RB: {
                result.num = 1;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException("Invalid character. Token types [ EOF, MULT, PLUS, RB ] expected", lex.curPos());
        }
    }

    private FContext F() throws ArithmeticLexer.ParseException {
        FContext result = new FContext();


        switch (lex.curToken().type) {
            case NUM: {
                TerminalContext e = NUM();
                result.num = e.getToken().toInt();
                result.add(e);
                result.e = e;
                return result;
            }
            case LB: {
                TerminalContext c1 = LB();
                EContext e1 = E();
                TerminalContext c3 = RB();
                result.num = e1.num;
                result.add(c1, e1, c3);
                result.e1 = e1;
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
