package rofleksey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LL1Table {
    private Map<String, Map<String, ArrayList<Branch>>> map;

    LL1Table(ArrayList<ParserRule> rules) {
        map = new HashMap<>(rules.size() + 1, 1);
        for (ParserRule rule : rules) {
            map.put(rule.name, new HashMap<>());
        }
    }

    Map<String, ArrayList<Branch>> getX(String x) {
        return map.get(x);
    }

    private ArrayList<Branch> getY(Map<String, ArrayList<Branch>> xMap, String y) {
        ArrayList<Branch> yArr;
        if (!xMap.containsKey(y)) {
            yArr = new ArrayList<>();
            xMap.put(y, yArr);
        } else {
            yArr = xMap.get(y);
        }
        return yArr;
    }

    ArrayList<Branch> get(String x, String y) {
        return getY(getX(x), y);
    }

    void put(String x, String y, Branch val) {
        Map<String, ArrayList<Branch>> xMap = getX(x);
        ArrayList<Branch> yArr = getY(xMap, y);
        if (!yArr.contains(val)) {
            yArr.add(val);
        }
    }

    boolean isLL1() {
        for (Map.Entry<String, Map<String, ArrayList<Branch>>> entryX : map.entrySet()) {
            for (Map.Entry<String, ArrayList<Branch>> entryY : entryX.getValue().entrySet()) {
                if (entryY.getValue().size() > 1) {
                    System.out.println("Conflicts: " + entryY.getValue().toString());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
