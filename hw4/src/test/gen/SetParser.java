package gen;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SetParser {
    private SetLexer lex;

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
        private final SetLexer.Token token;
        private TerminalContext(SetLexer.Token token) {
            super(token.text);
            this.token = token;
            isTerminal = true;
        }
        public SetLexer.Token getToken() {
            return token;
        }
    }

    Set<String> createFullSet() {
        return createRange('a', 'z');
    }

    Set<String> createRange(char from, char to) {
        HashSet<String> set = new HashSet<>();
        for (char c = from; c <= to; c++) {
            set.add(Character.toString(c));
        }
        return set;
    }


    public static class ExprContext extends Tree {
        public Set<String> set;
        public SetObjContext s;


        private ExprContext() {
            super("expr");
        }
    }

    public static class ExprFlexContext extends Tree {
        public SetObjContext s;


        private ExprFlexContext() {
            super("exprFlex");
        }
    }

    public static class SetObjContext extends Tree {
        public Set<String> set;
        public SetDefContext e;
        public ExprContext e1;


        private SetObjContext() {
            super("setObj");
        }
    }

    public static class SetDefContext extends Tree {
        public Set<String> set;
        public SetListContext e;
        public TerminalContext l1;
        public TerminalContext l2;


        private SetDefContext() {
            super("setDef");
        }
    }

    public static class SetListContext extends Tree {
        public Set<String> set;
        public TerminalContext e1;
        public SetListFlexContext e2;


        private SetListContext() {
            super("setList");
        }
    }

    public static class SetListFlexContext extends Tree {
        public Set<String> set;
        public TerminalContext e1;
        public SetListFlexContext e2;


        private SetListFlexContext() {
            super("setListFlex");
        }
    }


    private TerminalContext DOLLAR() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case DOLLAR:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ DOLLAR ] expected", lex.lastPos());
        }
    }

    private TerminalContext PERCENT() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case PERCENT:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ PERCENT ] expected", lex.lastPos());
        }
    }

    private TerminalContext PLUS() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case PLUS:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ PLUS ] expected", lex.lastPos());
        }
    }

    private TerminalContext MINUS() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case MINUS:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ MINUS ] expected", lex.lastPos());
        }
    }

    private TerminalContext LB() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case LB:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ LB ] expected", lex.lastPos());
        }
    }

    private TerminalContext RB() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case RB:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ RB ] expected", lex.lastPos());
        }
    }

    private TerminalContext LS() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case LS:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ LS ] expected", lex.lastPos());
        }
    }

    private TerminalContext RS() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case RS:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ RS ] expected", lex.lastPos());
        }
    }

    private TerminalContext COMMA() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case COMMA:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ COMMA ] expected", lex.lastPos());
        }
    }

    private TerminalContext LETTER() throws SetLexer.ParseException {
        switch (lex.curToken().type) {
            case LETTER:
                SetLexer.Token token = lex.curToken();
                lex.nextToken();
                return new TerminalContext(token);
            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ LETTER ] expected", lex.lastPos());
        }
    }


    private ExprContext expr() throws SetLexer.ParseException {
        ExprContext result = new ExprContext();
        System.out.println("expr");

        switch (lex.curToken().type) {
            case DOLLAR:
            case LB:
            case LETTER:
            case LS: {
                SetObjContext s = setObj();
                ExprFlexContext c2 = exprFlex(s.set);
                result.set = s.set;
                result.add(s, c2);
                result.s = s;
                return result;
            }

            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ DOLLAR, LB, LETTER, LS ] expected", lex.lastPos());
        }
    }

    private ExprFlexContext exprFlex(Set<String> parentSet) throws SetLexer.ParseException {
        ExprFlexContext result = new ExprFlexContext();


        switch (lex.curToken().type) {
            case PLUS: {
                System.out.println("+");
                TerminalContext c1 = PLUS();
                SetObjContext s = setObj();
                parentSet.addAll(s.set);
                ExprFlexContext c3 = exprFlex(parentSet);
                result.add(c1, s, c3);
                result.s = s;
                return result;
            }
            case MINUS: {
                System.out.println("-");
                TerminalContext c1 = MINUS();
                SetObjContext s = setObj();
                parentSet.removeAll(s.set);
                ExprFlexContext c3 = exprFlex(parentSet);
                result.add(c1, s, c3);
                result.s = s;
                return result;
            }
            case EOF:
            case RB: {
                return result;
            }

            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ EOF, MINUS, PLUS, RB ] expected", lex.lastPos());
        }
    }

    private SetObjContext setObj() throws SetLexer.ParseException {
        SetObjContext result = new SetObjContext();


        switch (lex.curToken().type) {
            case LB: {
                TerminalContext c1 = LB();
                ExprContext e1 = expr();
                TerminalContext c3 = RB();
                result.set = e1.set;
                result.add(c1, e1, c3);
                result.e1 = e1;
                return result;
            }
            case DOLLAR:
            case LETTER:
            case LS: {
                SetDefContext e = setDef();
                result.set = e.set;
                result.add(e);
                result.e = e;
                return result;
            }

            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ DOLLAR, LB, LETTER, LS ] expected", lex.lastPos());
        }
    }

    private SetDefContext setDef() throws SetLexer.ParseException {
        SetDefContext result = new SetDefContext();


        switch (lex.curToken().type) {
            case LS: {
                TerminalContext c1 = LS();
                SetListContext e = setList();
                TerminalContext c3 = RS();
                result.set = e.set;
                result.add(c1, e, c3);
                result.e = e;
                return result;
            }
            case LETTER: {
                TerminalContext l1 = LETTER();
                TerminalContext c2 = PERCENT();
                TerminalContext l2 = LETTER();
                result.set = createRange(l1.getText().charAt(0), l2.getText().charAt(0));
                System.out.println(result.set);
                result.add(l1, c2, l2);
                result.l1 = l1;
                result.l2 = l2;
                return result;
            }
            case DOLLAR: {
                TerminalContext c1 = DOLLAR();
                result.set = createFullSet();
                System.out.println(result.set);
                result.add(c1);
                return result;
            }

            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ DOLLAR, LETTER, LS ] expected", lex.lastPos());
        }
    }

    private SetListContext setList() throws SetLexer.ParseException {
        SetListContext result = new SetListContext();


        switch (lex.curToken().type) {
            case LETTER: {
                TerminalContext e1 = LETTER();
                SetListFlexContext e2 = setListFlex();
                result.set = e2.set;
                result.set.add(e1.getText());
                ;
                result.add(e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }

            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ LETTER ] expected", lex.lastPos());
        }
    }

    private SetListFlexContext setListFlex() throws SetLexer.ParseException {
        SetListFlexContext result = new SetListFlexContext();


        switch (lex.curToken().type) {
            case COMMA: {
                TerminalContext c1 = COMMA();
                TerminalContext e1 = LETTER();
                SetListFlexContext e2 = setListFlex();
                result.set = e2.set;
                result.set.add(e1.getText());
                ;
                result.add(c1, e1, e2);
                result.e1 = e1;
                result.e2 = e2;
                return result;
            }
            case RS: {
                result.set = new HashSet<>();
                return result;
            }

            default:
                throw new SetLexer.ParseException("Invalid token " + lex.curToken().type + ". Token types [ COMMA, RS ] expected", lex.lastPos());
        }
    }

    public ExprContext parse(String s) throws SetLexer.ParseException {
        return parse(new StringReader(s));
    }

    public ExprContext parse(Reader r) throws SetLexer.ParseException {
        lex = new SetLexer(r);
        lex.nextToken();
        ExprContext t = expr();
        if (lex.curToken().type != SetLexer.TokenType.EOF) {
            throw new SetLexer.ParseException("Got EOF before actual end of string", lex.lastPos());
        }
        return t;
    }
}
