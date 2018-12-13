package rofleksey;

import java.util.ArrayList;

public class LRuleItem {
    static final String INDENT = "        ";
    static final String TRIE_TEMPLATE = INDENT + "trie.add(\"%s\", %s);\n";
    final String name, content, escapedContent, upperCaseName;
    final boolean special;

    LRuleItem(String name, String content, boolean special) {
        this.name = name;
        this.upperCaseName = name.toUpperCase();
        this.content = content;
        this.special = special;
        if (special) {
            escapedContent = content;
        } else {
            escapedContent = content.substring(1, content.length() - 1);
        }
    }

    static class LBuilder {
        private static int stateNum = 0;
        private static int arrowNum = 0;
        private StringBuilder builder;
        private ArrayList<String> stateDefs, arrowDefs;

        LBuilder() {
            stateDefs = new ArrayList<>();
            arrowDefs = new ArrayList<>();
            builder = new StringBuilder();
        }

        private String getStateDef() {
            String def = "state" + (stateNum++);
            stateDefs.add(def);
            return def;
        }

        private String getArrowDef() {
            String def = "arrow" + (arrowNum++);
            arrowDefs.add(def);
            return def;
        }

        LBuilder stateDef(String type) {
            builder.append(INDENT).append("AutomataState ").append(getStateDef()).append(" = new AutomataState(").append(type).append(");\n");
            return this;
        }

        LBuilder stateArrow(String arg1, int to) {
            builder.append(INDENT).append("AutomataArrow ").append(getArrowDef()).append(" = new AutomataArrow(").append(arg1).append(", ").append(stateDefs.get(to)).append(");\n");
            return this;
        }

        LBuilder addArrow(int what, int owner) {
            builder.append(INDENT).append(stateDefs.get(owner)).append(".addArrow(").append(arrowDefs.get(what)).append(");\n");
            return this;
        }

        LBuilder addToRoot(int num) {
            builder.append(INDENT).append("dfa.getRoot().addArrow(").append(arrowDefs.get(num)).append(");\n");
            return this;
        }

        String build() {
            return builder.toString();
        }
    }

    public String toLexerData() {
        if (special) {
            switch (content) {
                case "singleLetter":
                    return new LBuilder().stateDef("TokenType." + upperCaseName)
                            .stateArrow("Character::isLetter", 0)
                            .addToRoot(0).build();

                case "word":
                    return new LBuilder().stateDef("TokenType." + upperCaseName)
                            .stateArrow("Character::isLetter", 0)
                            .addArrow(0, 0)
                            .addToRoot(0).build();

                case "number":
                    return new LBuilder().stateDef("TokenType." + upperCaseName)
                            .stateArrow("Character::isDigit", 0)
                            .addArrow(0, 0)
                            .addToRoot(0).build();

                case "numberWithMinus":
                    return new LBuilder().stateDef("TokenType." + upperCaseName)
                            .stateDef("null") // minus
                            .stateArrow("it -> it == '-'", 1)
                            .stateArrow("Character::isDigit", 0)
                            .addArrow(1, 0)
                            .addArrow(1, 1)
                            .addToRoot(0)
                            .addToRoot(1).build();

                default:
                    return INDENT + "//// CODE NOT IMPLEMENTED FOR TYPE " + content + " !!!\n";
            }
        } else {
            return String.format(TRIE_TEMPLATE, escapedContent, "TokenType." + upperCaseName);
        }
    }

    @Override
    public String toString() {
        return special ? "@" + content : content;
    }
}
