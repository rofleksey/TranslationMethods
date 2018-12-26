package rofleksey;

import java.util.ArrayList;

public class Branch {
    private static int globalNum = 0;
    private String initCode;
    ParserRule parent;
    int num = globalNum++;
    ArrayList<PRuleItem> items = new ArrayList<>();

    Branch(ParserRule parent) {
        this.parent = parent;
    }

    void setInitCode(String code) {
        initCode = code;
    }

    boolean hasCode() {
        return initCode != null;
    }

    String getInitCode() {
        return initCode == null ? null : initCode.substring(1, initCode.length() - 1).replace("$", "result");
    }

    static boolean hasCommonPrefix(Branch a, Branch b) {
        if (Math.min(a.items.size(), b.items.size()) > 0) {
            if (a.items.get(0).content.equals(b.items.get(0).content)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return num;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
