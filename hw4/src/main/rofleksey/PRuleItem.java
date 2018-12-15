package rofleksey;

import java.util.ArrayList;

public class PRuleItem {
    final String name, content;
    ArrayList<String> arguments = new ArrayList<>();
    private String code;

    PRuleItem(String content) {
        this.name = null;
        this.content = content;
    }

    PRuleItem(String name, String content) {
        this.name = name;
        this.content = content;
    }

    void setCode(String code) {
        this.code = code;
    }

    boolean hasCode() {
        return code != null;
    }

    String getCode() {
        return code == null ? null : code.substring(1, code.length() - 1).replace("$", "result");
    }

    @Override
    public String toString() {
        return content;
    }
}
