import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Parser {
    private LexicalAnalyzer lex;

    private Tree C() throws ParseException {
        Tree t = new Tree("C", new Tree(lex.curStr()));
        lex.nextToken();
        return t;
    }

    private Tree D() throws ParseException {
        return new Tree("D", S(), DFlex());
    }

    private Tree DFlex() throws ParseException {
        switch (lex.curToken()) {
            /*
            case STR:
                return new Tree("D'", C(), SFlex(), DFlex());
            case LB:
                lex.nextToken();
                Tree sub = Ro();
                if (lex.curToken() != Token.RB) {
                    throw new ParseException("\" expected", lex.curPos());
                }
                lex.nextToken();
                return new Tree("D'", new Tree("("), sub, new Tree(")"), SFlex(), DFlex());
            default:
                return new Tree("D'", Tree.epsilon());*/
            case STR:
            case LB:
                return new Tree("D'", S(), DFlex());
            default:
                return new Tree("D'", Tree.epsilon());
        }
    }

    private Tree G() throws ParseException {
        switch (lex.curToken()) {
            case LB:
                lex.nextToken();
                Tree sub = Ro();
                if (lex.curToken() != Token.RB) {
                    throw new ParseException(") expected", lex.curPos());
                }
                lex.nextToken();
                return new Tree("G", new Tree("("), sub, new Tree(")"));
            case STR:
                return new Tree("G", C());
            default:
                throw new ParseException("Invalid character", lex.curPos());
        }
    }

    private Tree Ro() throws ParseException { // теперь можно пойти домой
        return new Tree("R", D(), RoFlex());
    }

    private Tree RoFlex() throws ParseException { // и поесть супа
        switch (lex.curToken()) {
            case OR:
                lex.nextToken();
                return new Tree("R'", new Tree("|"), D(), RoFlex());
            default:
                return new Tree("R'", Tree.epsilon());
        }
    }

    private Tree S() throws ParseException {
        return new Tree("S", G(), SFlex());
    }

    private Tree SFlex() throws ParseException {
        switch (lex.curToken()) {
            case STAR:
                lex.nextToken();
                return new Tree("S'", new Tree("*"));
            default:
                return new Tree("S'", Tree.epsilon());
        }
    }

    public Tree parse(String s) throws ParseException {
        return parse(new ByteArrayInputStream(s.getBytes()));
    }

    public Tree parse(InputStream is) throws ParseException {
        lex = new LexicalAnalyzer(is);
        lex.nextToken();
        Tree t = Ro();
        if(lex.curToken() != Token.END) {
            throw new ParseException("Unexpected end of input", lex.curPos());
        }
        return t;
    }
}
