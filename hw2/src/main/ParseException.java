public class ParseException extends Exception {
    ParseException(String cause, int at) {
        super("Parse error: " + cause + " at pos=" + (at + 1));
    }
}
