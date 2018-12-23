package gen;


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


    public static class ExprContext extends Tree {
        public int num;
        public ExprFlexContext e;
        public MultContext t;


        private ExprContext() {
            super("expr");
        }
    }

    public static class ExprFlexContext extends Tree {
        public int num;
        public ExprFlexContext e;
        public MultContext t;


        private ExprFlexContext() {
            super("exprFlex");
        }
    }

    public static class MultContext extends Tree {
        public int num;
        public MultFlexContext m;
        public PowerContext p;


        private MultContext() {
            super("mult");
        }
    }

    public static class MultFlexContext extends Tree {
        public int num;
        public MultFlexContext m;
        public PowerContext p;


        private MultFlexContext() {
            super("multFlex");
        }
    }

    public static class PowerContext extends Tree {
        public int num;
        public PowerFlexContext f;
        public PrimaryContext p;


        private PowerContext() {
            super("power");
        }
    }

    public static class PowerFlexContext extends Tree {
        public int num;
        public PowerContext f;


        private PowerFlexContext() {
            super("powerFlex");
        }
    }

    public static class PrimaryContext extends Tree {
        public int num;
        public ExprContext e;
        public TerminalContext n;
        public PrimaryContext p;


        private PrimaryContext() {
            super("primary");
        }
    }


    private TerminalContext NUM() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case NUM:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "NUM", lex);
        }
    }

    private TerminalContext POW() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case POW:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "POW", lex);
        }
    }

    private TerminalContext MULTIPLY() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case MULTIPLY:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "MULTIPLY", lex);
        }
    }

    private TerminalContext PLUS() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case PLUS:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "PLUS", lex);
        }
    }

    private TerminalContext MINUS() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case MINUS:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "MINUS", lex);
        }
    }

    private TerminalContext LB() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case LB:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "LB", lex);
        }
    }

    private TerminalContext RB() throws ArithmeticLexer.ParseException {
        switch (lex.curToken().type) {
            case RB:
                ArithmeticLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "RB", lex);
        }
    }


    private ExprContext expr() throws ArithmeticLexer.ParseException {
        ExprContext result = new ExprContext();


        switch (lex.curToken().type) {
            case LB:
            case MINUS:
            case NUM: {
                MultContext t = mult();
                ExprFlexContext e = exprFlex(t.num);
                result.num = e.num;
                result.add(t, e);
                result.t = t;
                result.e = e;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "LB, MINUS, NUM", lex);
        }
    }

    private ExprFlexContext exprFlex(int acc) throws ArithmeticLexer.ParseException {
        ExprFlexContext result = new ExprFlexContext();


        switch (lex.curToken().type) {
            case PLUS: {
                TerminalContext c1 = PLUS();
                MultContext t = mult();
                int temp = acc + t.num;
                ExprFlexContext e = exprFlex(temp);
                result.num = e.num;
                result.add(c1, t, e);
                result.t = t;
                result.e = e;
                return result;
            }
            case EOF:
            case RB: {
                result.num = acc;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "EOF, PLUS, RB", lex);
        }
    }

    private MultContext mult() throws ArithmeticLexer.ParseException {
        MultContext result = new MultContext();


        switch (lex.curToken().type) {
            case LB:
            case MINUS:
            case NUM: {
                PowerContext p = power();
                MultFlexContext m = multFlex(p.num);
                result.num = m.num;
                result.add(p, m);
                result.p = p;
                result.m = m;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "LB, MINUS, NUM", lex);
        }
    }

    private MultFlexContext multFlex(int acc) throws ArithmeticLexer.ParseException {
        MultFlexContext result = new MultFlexContext();


        switch (lex.curToken().type) {
            case MULTIPLY: {
                TerminalContext c1 = MULTIPLY();
                PowerContext p = power();
                int temp = acc * p.num;
                MultFlexContext m = multFlex(temp);
                result.num = m.num;
                result.add(c1, p, m);
                result.p = p;
                result.m = m;
                return result;
            }
            case EOF:
            case PLUS:
            case RB: {
                result.num = acc;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "EOF, MULTIPLY, PLUS, RB", lex);
        }
    }

    private PowerContext power() throws ArithmeticLexer.ParseException {
        PowerContext result = new PowerContext();


        switch (lex.curToken().type) {
            case LB:
            case MINUS:
            case NUM: {
                PrimaryContext p = primary();
                PowerFlexContext f = powerFlex(p.num);
                result.num = f.num;
                result.add(p, f);
                result.p = p;
                result.f = f;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "LB, MINUS, NUM", lex);
        }
    }

    private PowerFlexContext powerFlex(int acc) throws ArithmeticLexer.ParseException {
        PowerFlexContext result = new PowerFlexContext();


        switch (lex.curToken().type) {
            case EOF:
            case MULTIPLY:
            case PLUS:
            case RB: {
                result.num = acc;
                return result;
            }
            case POW: {
                TerminalContext c1 = POW();
                PowerContext f = power();
                result.num = (int) Math.pow(acc, f.num);
                result.add(c1, f);
                result.f = f;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "EOF, MULTIPLY, PLUS, POW, RB", lex);
        }
    }

    private PrimaryContext primary() throws ArithmeticLexer.ParseException {
        PrimaryContext result = new PrimaryContext();


        switch (lex.curToken().type) {
            case LB: {
                TerminalContext c1 = LB();
                ExprContext e = expr();
                TerminalContext c3 = RB();
                result.num = e.num;
                result.add(c1, e, c3);
                result.e = e;
                return result;
            }
            case NUM: {
                TerminalContext n = NUM();
                result.num = n.getToken().toInt();
                result.add(n);
                result.n = n;
                return result;
            }
            case MINUS: {
                TerminalContext c1 = MINUS();
                PrimaryContext p = primary();
                result.num = -p.num;
                result.add(c1, p);
                result.p = p;
                return result;
            }

            default:
                throw new ArithmeticLexer.ParseException(lex.curToken().type, "LB, MINUS, NUM", lex);
        }
    }

    public ExprContext parse(String s) throws ArithmeticLexer.ParseException {
        lex = new ArithmeticLexer(s);
        lex.nextToken();
        ExprContext t = expr();
        if (lex.curToken().type != ArithmeticLexer.TokenType.EOF) {
            throw new ArithmeticLexer.ParseException("Got EOF before actual end of string", lex.lastPos());
        }
        return t;
    }
}
