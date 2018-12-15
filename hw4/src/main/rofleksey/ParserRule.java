package rofleksey;

import java.util.ArrayList;

public class ParserRule {
    String name, contextName;
    private String initCode;
    ArrayList<Pair<String, String>> localFields = new ArrayList<>(), funcFields = new ArrayList<>();
    ArrayList<Branch> branches = new ArrayList<>();

    ParserRule(String name) {
        this.name = name;
        contextName = name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    void setInitCode(String code) {
        initCode = code;
    }

    boolean hasInitCode() {
        return initCode != null;
    }

    String getInitCode() {
        return initCode == null ? null : initCode.substring(1, initCode.length() - 1).replace("$", "result");
    }

    @Override
    public String toString() {
        return "<name=" + name + ", localFields=" + localFields + ", branches=" + branches + ">";
    }
}
