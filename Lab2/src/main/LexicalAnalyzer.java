import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LexicalAnalyzer {
    private final InputStream is;
    private Token curToken;
    private int curPos, curChar;
    private String curStr;

    public LexicalAnalyzer(String s) throws ParseException {
        this.is = new ByteArrayInputStream(s.getBytes());
        nextChar();
    }


    public LexicalAnalyzer(InputStream is) throws ParseException {
        this.is = is;
        nextChar();
    }

    private void nextChar() throws ParseException {
        curPos++;
        try {
            curChar = is.read();
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), curPos);
        }
    }

    Token nextToken() throws ParseException {
        while (Character.isWhitespace(curChar)) {
            nextChar();
        }
        switch (curChar) {
            case '|':
                nextChar();
                curToken = Token.OR;
                break;
            case '(':
                nextChar();
                curToken = Token.LB;
                break;
            case ')':
                nextChar();
                curToken = Token.RB;
                break;
            case '*':
                nextChar();
                curToken = Token.STAR;
                break;
            case -1:
                curToken = Token.END;
                break;
            default:
                if (Character.isLetterOrDigit(curChar())) {
                    StringBuilder builder = new StringBuilder(Character.toString(curChar()));
                    nextChar();
                    while (Character.isLetterOrDigit(curChar())) {
                        builder.append(curChar());
                        nextChar();
                    }
                    curToken = Token.STR;
                    curStr = builder.toString();
                } else {
                    throw new ParseException("Illegal character '" + (char) curChar + "'", curPos);
                }
                break;
        }
        return curToken;
    }

    private char curChar() {
        return (char) curChar;
    }

    int curPos() {
        return curPos;
    }

    Token curToken() {
        return curToken;
    }

    String curStr() {
        return curStr;
    }
}
