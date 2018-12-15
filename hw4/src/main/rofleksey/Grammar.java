package rofleksey;

import java.util.*;

public class Grammar {
    public static final String EPSILON = "ϵ", EOF = "$";
    ArrayList<ParserRule> pRules = new ArrayList<>();
    ArrayList<LRuleItem> lRules = new ArrayList<>();
    private HashMap<String, String> options = new HashMap<>();
    HashMap<String, ParserRule> ruleNames;

    Grammar() {
        addOption("headers", "\n");
        addOption("members", "\n");
    }

    void addOption(String name, String content) {
        options.put(name, content);
    }

    boolean hasOption(String name) {
        return options.containsKey(name);
    }

    String getOption(String name) {
        return options.get(name);
    }

    @Override
    public String toString() {
        return pRules.toString();
    }

    private boolean calcNullablesHelper(Map<String, Boolean> result, String key, Set<String> path) {
        if (path.contains(key)) {
            return false;
        }
        path.add(key);
        if (result.containsKey(key)) {
            return result.get(key);
        }
        ParserRule rule = ruleNames.get(key);
        for (Branch branch : rule.branches) {
            boolean broken = false;
            for (PRuleItem it : branch.items) {
                if (!calcNullablesHelper(result, it.content, path)) {
                    broken = true;
                    break;
                }
            }
            if (!broken) {
                result.put(key, true);
                return true;
            }
        }
        result.put(key, false);
        return false;
    }

    private Map<String, Boolean> calcNullables() {
        HashMap<String, Boolean> result = new HashMap<>();
        for (ParserRule rule : pRules) {
            for (Branch branch : rule.branches) {
                for (PRuleItem it : branch.items) {
                    if (!ruleNames.containsKey(it.content)) {
                        result.put(it.content, false);
                    }
                }
            }
        }
        if (result.containsKey(EPSILON)) {
            result.put(EPSILON, true);
        }
        ruleNames.keySet().forEach(s -> calcNullablesHelper(result, s, new HashSet<>()));
        System.out.println(result);
        return result;
    }

    private ArrayList<String> arrayOf(String s) {
        ArrayList<String> arr = new ArrayList<>();
        arr.add(s);
        return arr;
    }

    private ArrayList<String> calcFIRSTHelper(Map<String, ArrayList<String>> result, Map<String, Boolean> nulls, String key, Set<String> path, RemoteBoolean finished) {
        if (!ruleNames.containsKey(key)) {
            return result.get(key);
        }
        if (path.contains(key)) {
            return result.get(key);
        }
        path.add(key);
        if (!result.containsKey(key)) {
            result.put(key, new ArrayList<>());
            finished.set(false);
        }
        ParserRule rule = ruleNames.get(key);
        for (Branch branch : rule.branches) {
            for (PRuleItem it : branch.items) {
                ArrayList<String> temp = calcFIRSTHelper(result, nulls, it.content, path, finished);
                for (String s : temp) {
                    ArrayList<String> arr = result.get(key);
                    if (!arr.contains(s)) { //TODO: O(n)???
                        arr.add(s);
                        finished.set(false);
                    }
                }
                if (!nulls.get(it.content)) {
                    break;
                }
            }
        }
        return result.get(key);
    }

    private Map<String, ArrayList<String>> calcFIRST(Map<String, Boolean> nulls) {
        Map<String, ArrayList<String>> result = new HashMap<>();
        for (ParserRule rule : pRules) {
            for (Branch branch : rule.branches) {
                for (PRuleItem it : branch.items) {
                    if (!ruleNames.containsKey(it.content)) {
                        result.put(it.content, arrayOf(it.content));
                    }
                }
            }
        }
        RemoteBoolean finished = new RemoteBoolean(false);
        while (!finished.get()) {
            finished.set(true);
            ruleNames.keySet().forEach(s -> calcFIRSTHelper(result, nulls, s, new HashSet<>(), finished));
        }
        System.out.println(result);
        return result;
    }

    private void calcFOLLOWHelper(Map<String, ArrayList<String>> result, Map<String, Boolean> nulls, Map<String, ArrayList<String>> first, String key, RemoteBoolean finished) {
        ParserRule rule = ruleNames.get(key);
        for (Branch branch : rule.branches) {
            for (int i = 0; i < branch.items.size(); i++) {
                // A -> a B b
                String mid = branch.items.get(i).content;
                ArrayList<String> followMid = result.get(mid);
                if (ruleNames.containsKey(mid)) {
                    boolean broken = false;
                    for (int j = i + 1; j < branch.items.size(); j++) {
                        PRuleItem curIt = branch.items.get(j);
                        ArrayList<String> ff = first.get(curIt.content);
                        for (String s : ff) {
                            if (!EPSILON.equals(s) && !followMid.contains(s)) { //TODO: O(n)???
                                followMid.add(s);
                                finished.set(false);
                            }
                        }
                        if (!nulls.containsKey(curIt.content) || !nulls.get(curIt.content)) {
                            broken = true;
                            break;
                        }
                    }
                    if (!broken) {
                        for (String s : result.get(key)) {
                            if (!followMid.contains(s)) {//TODO: O(n)???
                                followMid.add(s);
                                finished.set(false);
                            }
                        }
                    }
                }
            }
        }
    }

    private Map<String, ArrayList<String>> calcFOLLOW(Map<String, Boolean> nulls, Map<String, ArrayList<String>> first) {
        Map<String, ArrayList<String>> result = new HashMap<>();
        RemoteBoolean finished = new RemoteBoolean(false);
        result.put(pRules.get(0).name, arrayOf(EOF));
        for (int i = 1; i < pRules.size(); i++) {
            result.put(pRules.get(i).name, new ArrayList<>());
        }
        while (!finished.get()) {
            finished.set(true);
            ruleNames.keySet().forEach(s -> calcFOLLOWHelper(result, nulls, first, s, finished));
        }
        System.out.println(ruleNames.keySet());
        ruleNames.keySet().forEach(s -> Collections.sort(result.get(s)));
        System.out.println(result);
        return result;
    }

    LL1Table genLL1Table() {
        Map<String, Boolean> a = calcNullables();
        Map<String, ArrayList<String>> b = calcFIRST(a);
        Map<String, ArrayList<String>> c = calcFOLLOW(a, b);
        return genLL1Table(a, b, c);
    }

    private LL1Table genLL1Table(Map<String, Boolean> nulls, Map<String, ArrayList<String>> firsts, Map<String, ArrayList<String>> follows) {
        LL1Table table = new LL1Table(pRules);
        for (ParserRule rule : pRules) {
            for (Branch branch : rule.branches) {
                boolean broken = false;
                for (PRuleItem it : branch.items) {
                    ArrayList<String> first = firsts.get(it.content);
                    for (String s : first) {
                        if (!EPSILON.equals(s)) {
                            table.put(rule.name, s, branch);
                        }
                    }
                    if (!nulls.containsKey(it.content) || !nulls.get(it.content)) {
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    ArrayList<String> follow = follows.get(rule.name);
                    for (String s : follow) {
                        table.put(rule.name, s, branch);
                    }
                }
            }
        }
        System.out.println(table);
        return table;
    }

    void prepare() {
        ruleNames = new HashMap<>(pRules.size() + 1, 1);
        for (ParserRule r : pRules) {
            ruleNames.put(r.name, r);
        }
    }
}
