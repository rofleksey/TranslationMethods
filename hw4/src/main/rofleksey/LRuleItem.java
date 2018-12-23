package rofleksey;

public class LRuleItem {
    static final String INDENT = "        ";
    final String name, content, escapedContent, upperCaseName;
    final boolean special;

    LRuleItem(String name, String content, boolean special) {
        this.name = name;
        this.upperCaseName = name.toUpperCase();
        this.special = special;
        this.content = special ? content.replace("\\", "\\\\") : content;
        escapedContent = this.content.substring(1, this.content.length() - 1);
    }

    public String toLexerData() {
        return INDENT + "matchers.add(new TokenMatcher(TokenType." + upperCaseName + ", \"" + escapedContent + "\", " + special + "));\n";
    }

    @Override
    public String toString() {
        return special ? "@" + content : content;
    }
}
