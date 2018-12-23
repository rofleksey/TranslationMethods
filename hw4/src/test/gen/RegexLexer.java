package gen;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexLexer {
    public enum TokenType {
        LB, RB, OR, STAR, C, 
        EOF
    }
    private Token EOF_TOKEN;
    private final String input;
    private Token curToken;
    private int curPos = -1, lastPos = 0;
    private char curChar;
    private boolean reachedEOF = false;
    private ArrayList<TokenMatcher> matchers;

    private void init() {
        matchers = new ArrayList<>();
        EOF_TOKEN = new Token(TokenType.EOF, "<EOF>");

        matchers.add(new TokenMatcher(TokenType.LB, "(", false));
        matchers.add(new TokenMatcher(TokenType.RB, ")", false));
        matchers.add(new TokenMatcher(TokenType.OR, "|", false));
        matchers.add(new TokenMatcher(TokenType.STAR, "*", false));
        matchers.add(new TokenMatcher(TokenType.C, "[a-zA-Z]", true));

        matchers.forEach(TokenMatcher::prepare);
    }

    public RegexLexer(String s) throws ParseException {
        input = s;
        init();
        nextChar();
    }

    private void nextChar() throws ParseException {
        if (reachedEOF) {
            return;
        }
        if (curPos + 1 < input.length()) {
            curChar = input.charAt(curPos + 1);
            curPos++;
        } else {
            reachedEOF = true;
        }
    }

    private boolean isSkipChar() {
        return !reachedEOF && Character.isWhitespace(curChar);
    }

    Token nextToken() throws ParseException {
        if (!reachedEOF) {
            while (isSkipChar()) {
                nextChar();
            }
            if (reachedEOF) {
                return curToken = EOF_TOKEN;
            }
            lastPos = curPos;
            matchers.forEach(m -> m.from(curPos));
            for (TokenMatcher m : matchers) {
                if (m.match()) {
                    curPos += m.getLen() - 1;
                    nextChar();
                    return curToken = new Token(m.type, m.getMatch());
                }
            }
            throw new ParseException("Invalid input", curPos);
        }
        return curToken = EOF_TOKEN;
    }

    public int curPos() {
        return curPos;
    }

    public int lastPos() {
        return lastPos;
    }

    public Token curToken() {
        return curToken;
    }

    private class TokenMatcher {
        final TokenType type;
        private final Pattern pattern;
        private Matcher matcher;
        String last;

        TokenMatcher(TokenType type, String pattern, boolean special) {
            this.type = type;
            this.pattern = Pattern.compile(special ? pattern : Pattern.quote(pattern));
        }

        void prepare() {
            matcher = pattern.matcher(input);
        }

        void from(int ind) {
            matcher.region(ind, input.length());
        }

        boolean match() {
            if (matcher.lookingAt()) {
                last = matcher.group();
                return true;
            }
            return false;
        }

        int getLen() {
            return last.length();
        }

        String getMatch() {
            return last;
        }
    }

    public static class Token {
        public final TokenType type;
        public final String text;

        Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }

        int toInt() {
            return Integer.valueOf(text);
        }

        @Override
        public String toString() {
            return "{text = '" + text + "', type = " + type + "}";
        }
    }

    public static class ParseException extends Exception {
        ParseException(String cause, int at) {
            super("Parse error: " + cause + " at pos=" + (at + 1));
        }
        ParseException(TokenType type, String expected, RegexLexer lex) {
            this("Invalid token " + type + ". Token types [ " + expected + " ] expected", type != TokenType.EOF ? lex.lastPos() : lex.curPos() + 1);
        }
    }
}
