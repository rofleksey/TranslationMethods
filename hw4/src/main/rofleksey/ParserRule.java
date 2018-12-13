package rofleksey;

import java.util.ArrayList;

public class ParserRule {
    String name, contextName;
    ArrayList<Pair<String, String>> fields = new ArrayList<>();
    ArrayList<Branch> branches = new ArrayList<>();

    ParserRule(String name) {
        this.name = name;
        contextName = name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override
    public String toString() {
        return "<name=" + name + ", fields=" + fields + ", branches=" + branches + ">";
    }
}
