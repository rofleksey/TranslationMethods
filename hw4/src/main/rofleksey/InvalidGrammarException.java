package rofleksey;

public class InvalidGrammarException extends Exception {
    InvalidGrammarException(String message, ParserRule rule, Branch branch, PRuleItem it) {
        super(message + " at rule '" + rule + "', branch #" + rule.branches.indexOf(branch) + ", token #" + branch.items.indexOf(it));
    }
}
