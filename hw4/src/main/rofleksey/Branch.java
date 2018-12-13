package rofleksey;

import java.util.ArrayList;

public class Branch {
    private static int globalNum = 0;
    String code;
    int num = globalNum++;
    ArrayList<PRuleItem> items = new ArrayList<>();

    String getNormalCode() {
        return code.substring(1, code.length() - 1);
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
