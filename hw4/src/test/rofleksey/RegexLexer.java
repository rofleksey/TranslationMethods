package rofleksey;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.function.Predicate;

public class RegexLexer {
    public enum TokenType {
        LB, RB, OR, STAR, C,
        EOF
    }

    private Token EOF_TOKEN;
    private final Reader reader;
    private Token curToken;
    private StringBuilder tokenTextBuilder;
    private int curPos, curRead;
    private char curChar;
    private Trie trie;
    private Automata dfa;

    private void init() {
        trie = new Trie();
        dfa = new Automata();
        EOF_TOKEN = new Token(TokenType.EOF, "<EOF>");
        tokenTextBuilder = new StringBuilder();

        trie.add("(", TokenType.LB);
        trie.add(")", TokenType.RB);
        trie.add("|", TokenType.OR);
        trie.add("*", TokenType.STAR);
        AutomataState state0 = new AutomataState(TokenType.C);
        AutomataArrow arrow0 = new AutomataArrow(Character::isLetter, state0);
        dfa.getRoot().addArrow(arrow0);

    }

    public RegexLexer(String s) throws ParseException {
        this(new StringReader(s));
    }


    public RegexLexer(Reader r) throws ParseException {
        reader = r;
        init();
        nextChar();
    }

    private void nextChar() throws ParseException {
        if (curRead == -1) {
            return;
        }
        curPos++;
        try {
            curRead = reader.read();
            curChar = (char) curRead;
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), curPos);
        }
    }

    private boolean isSkipChar() {
        return curRead != -1 && Character.isWhitespace(curChar);
    }

    private void append() {
        if (curRead != -1) {
            tokenTextBuilder.append(curChar);
        }
    }

    Token nextToken() throws ParseException {
        if (curRead != -1) {
            while (isSkipChar()) {
                nextChar();
            }
            if (curRead == -1) {
                return curToken = EOF_TOKEN;
            }
            trie.reset();
            dfa.reset();
            if (tokenTextBuilder.length() > 0) {
                tokenTextBuilder.delete(0, tokenTextBuilder.length());
            }
            append();
            while (true) {
                dfa.move(curChar);
                if (trie.move(curChar)) {
                    nextChar();
                    if (trie.isTerminated()) {
                        return curToken = new Token(trie.getToken(), tokenTextBuilder.toString());
                    } else {
                        append();
                        if (curRead == -1) {
                            throw new ParseException("Unexpected end of input", curPos);
                        }
                    }
                } else if (dfa.isAlive()) {
                    nextChar();
                    while (true) {
                        if (curRead == -1 || isSkipChar()) {
                            if (dfa.isTerminated()) {
                                return curToken = new Token(dfa.getToken(), tokenTextBuilder.toString());
                            } else {
                                throw new ParseException("Unexpected end of input", curPos);
                            }
                        } else {
                            if (dfa.canMove(curChar)) {
                                dfa.move(curChar);
                                append();
                                nextChar();
                            } else {
                                if (dfa.isTerminated()) {
                                    return curToken = new Token(dfa.getToken(), tokenTextBuilder.toString());
                                } else {
                                    throw new ParseException("Unexpected end of input", curPos);
                                }
                            }
                        }
                    }
                } else {
                    throw new ParseException("Invalid input", curPos);
                }
            }
        }
        return curToken = EOF_TOKEN;
    }

    public int curPos() {
        return curPos;
    }

    public Token curToken() {
        return curToken;
    }

    private static class AutomataArrow {
        private final Predicate<Character> predicate;
        private final AutomataState next;

        AutomataArrow(Predicate<Character> predicate, AutomataState next) {
            this.predicate = predicate;
            this.next = next;
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

    private static class AutomataState {
        private ArrayList<AutomataArrow> arrows = new ArrayList<>();
        private TokenType termination;

        AutomataState(TokenType termination) {
            this.termination = termination;
        }

        void addArrow(AutomataArrow arrow) {
            arrows.add(arrow);
        }

        boolean isTerminated() {
            return termination != null;
        }

        TokenType getToken() {
            return termination;
        }

        AutomataState move(char c) {
            for (int i = 0; i < arrows.size(); i++) {
                if (arrows.get(i).predicate.test(c)) {
                    return arrows.get(i).next;
                }
            }
            return null;
        }

        boolean canMove(char c) {
            for (int i = 0; i < arrows.size(); i++) {
                if (arrows.get(i).predicate.test(c)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class Automata {
        private AutomataState root, cur;
        private boolean alive;

        Automata() {
            root = new AutomataState(null);
        }

        AutomataState getRoot() {
            return root;
        }

        void move(char c) {
            if (alive) {
                AutomataState state = cur.move(c);
                if (state != null) {
                    cur = state;
                } else {
                    alive = false;
                }
            }
        }

        boolean canMove(char c) {
            return cur.canMove(c);
        }

        boolean isAlive() {
            return alive;
        }

        boolean isTerminated() {
            return cur.isTerminated();
        }

        TokenType getToken() {
            return cur.getToken();
        }

        void reset() {
            alive = true;
            cur = root;
        }
    }

    private static class TrieElement {
        private TreeMap<Character, TrieElement> map = new TreeMap<>();
        private TokenType termination;

        TrieElement add(char c) {
            TrieElement element = new TrieElement();
            map.put(c, element);
            return element;
        }

        boolean isTerminated() {
            return termination != null;
        }

        TrieElement move(char c) {
            return map.get(c);
        }

        TokenType getToken() {
            return termination;
        }

        void terminate(TokenType t) {
            termination = t;
        }
    }

    private static class Trie {
        private TrieElement root, cur;

        Trie() {
            root = new TrieElement();
            reset();
        }

        void add(String s, TokenType t) {
            for (int i = 0; i < s.length(); i++) {
                cur = cur.add(s.charAt(i));
            }
            cur.terminate(t);
            reset();
        }

        boolean move(char c) {
            TrieElement el = cur.move(c);
            if (el != null) {
                cur = el;
                return true;
            }
            return false;
        }

        boolean isTerminated() {
            return cur.isTerminated();
        }

        TokenType getToken() {
            return cur.getToken();
        }

        void reset() {
            cur = root;
        }
    }

    public static class ParseException extends Exception {
        ParseException(String cause, int at) {
            super("Parse error: " + cause + " at pos=" + (at + 1));
        }
    }
}
