package rofleksey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ParserGenerator {
    static final String PARSER_INDENT = "    ";
    private final Grammar grammar;
    private final LL1Table table;
    private final String className;
    private final String pkg;
    private final Path outputDir;
    private boolean inited;
    private String lexerTemplate, parserTemplate, builderTemplate, contextTemplate, terminalTemplate;

    ParserGenerator(Grammar grammar, LL1Table table, String className, String pkg, Path outputDir) {
        this.grammar = grammar;
        this.table = table;
        this.className = className;
        this.pkg = pkg;
        this.outputDir = outputDir;
    }

    private String getFileContents(String name) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(new File(getClass().getResource(name).getPath()).getCanonicalPath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }

    private void saveStringToFile(Path outputDir, String fileName, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir.toString(), fileName))) {
            writer.write(content);
        }
    }

    private void init() throws IOException {
        if (inited) {
            return;
        }
        lexerTemplate = getFileContents("LexerTemplate");
        parserTemplate = getFileContents("ParserTemplate");
        builderTemplate = getFileContents("BuilderTemplate");
        terminalTemplate = getFileContents("TerminalTemplate");
        contextTemplate = getFileContents("ContextTemplate");
        inited = true;
    }

    public void generate() throws IOException {
        init();
        generateLexer();
        generateParser();
    }

    private void generateLexer() throws IOException {
        String s = lexerTemplate.replace("%classname", className);

        if (pkg != null) {
            s = s.replace("%package", "package " + pkg + ";");
        }

        StringBuilder tokensList = new StringBuilder();
        for (LRuleItem rule : grammar.lRules) {
            tokensList.append(rule.upperCaseName).append(", ");
        }
        s = s.replace("%tokensList", tokensList.toString());

        StringBuilder data = new StringBuilder();
        for (LRuleItem rule : grammar.lRules) {
            data.append(rule.toLexerData());
        }
        s = s.replace("%data", data);

        saveStringToFile(outputDir, className + "Lexer.java", s);
        //System.out.println(s);
    }

    private void indent(StringBuilder builder, int tabs) {
        for (int i = 0; i < tabs; i++) {
            builder.append(PARSER_INDENT);
        }
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void generateParser() throws IOException {
        String s = parserTemplate.replace("%classname", className);

        if (pkg != null) {
            s = s.replace("%package", "package " + pkg + ";");
        }

        {
            StringBuilder contexts = new StringBuilder();
            for (ParserRule rule : grammar.pRules) {
                String curContext = contextTemplate.replace("%name", rule.name);
                curContext = curContext.replace("%upName", rule.contextName);
                StringBuilder curFields = new StringBuilder();
                for (Pair<String, String> field : rule.fields) {
                    indent(curFields, 2);
                    curFields.append("public ").append(field.getA()).append(" ").append(field.getB()).append(";\n");
                }
                ArrayList<Pair<String, String>> inlineFields = new ArrayList<>();
                for (Branch branch : rule.branches) {
                    for (PRuleItem item : branch.items) {
                        if (item.name != null) {
                            if (grammar.ruleNames.containsKey(item.content)) {
                                inlineFields.add(new Pair<>(capitalize(item.content) + "Context", item.name));
                            } else {
                                inlineFields.add(new Pair<>("TerminalContext", item.name));
                            }
                        }
                    }
                }
                for (Pair<String, String> field : inlineFields) {
                    indent(curFields, 2);
                    curFields.append("public ").append(field.getA()).append(" ").append(field.getB()).append(";\n");
                }
                curContext = curContext.replace("%fields", curFields.toString());
                StringBuilder curCalcs = new StringBuilder();
                for (Branch branch : rule.branches) {
                    if (branch.code != null) {
                        indent(curCalcs, 2);
                        curCalcs.append("private void calc").append(branch.num).append("() {\n");
                        indent(curCalcs, 3);
                        curCalcs.append(branch.getNormalCode()).append(";\n");
                        indent(curCalcs, 2);
                        curCalcs.append("}\n");
                    }
                }
                curContext = curContext.replace("%calc", curCalcs.toString());
                contexts.append(curContext);
            }
            s = s.replace("%contexts", contexts.toString());
        }

        {
            StringBuilder terminals = new StringBuilder();
            for (LRuleItem rule : grammar.lRules) {
                terminals.append(terminalTemplate.replace("%classname", className)
                        .replace("%name", rule.name)
                        .replace("%upName", rule.upperCaseName));
            }
            s = s.replace("%terminals", terminals.toString());
        }

        {
            StringBuilder builders = new StringBuilder();
            for (ParserRule rule : grammar.pRules) {
                String curBuilder = builderTemplate.replace("%classname", className)
                        .replace("%name", rule.name)
                        .replace("%upName", rule.contextName);
                Map<String, ArrayList<Branch>> row = table.getX(rule.name);
                int numOfBranches = row.values().stream().mapToInt(ArrayList::size).sum();
                Map<Branch, ArrayList<String>> inverse = new HashMap<>(numOfBranches + 1, 1);
                for (Map.Entry<String, ArrayList<Branch>> entry : row.entrySet()) {
                    for (Branch b : entry.getValue()) {
                        if (!inverse.containsKey(b)) {
                            inverse.put(b, new ArrayList<>());
                        }
                        inverse.get(b).add(entry.getKey().equals("$") ? "EOF" : entry.getKey());
                    }
                }
                inverse.keySet().forEach(k -> Collections.sort(inverse.get(k)));
                StringBuilder cases = new StringBuilder();
                ArrayList<String> expected = new ArrayList<>();
                for (Map.Entry<Branch, ArrayList<String>> entry : inverse.entrySet()) {
                    int ii = 0;
                    for (String name : entry.getValue()) {
                        indent(cases, 3);
                        cases.append("case ").append(name.toUpperCase()).append(":");
                        if (++ii == entry.getValue().size()) {
                            cases.append(" {");
                        }
                        cases.append("\n");
                        expected.add(name);
                    }
                    int cI = 1;
                    Map<String, Integer> fieldMap = new HashMap<>(entry.getKey().items.size() + 1, 1);
                    for (PRuleItem item : entry.getKey().items) {
                        if (!item.content.equals(Grammar.EPSILON)) {
                            indent(cases, 4);
                            if (grammar.ruleNames.containsKey(item.content)) {
                                cases.append(capitalize(item.content)).append("Context c").append(cI).append(" = ").append(item.content).append("();\n");
                            } else {
                                cases.append("TerminalContext c").append(cI).append(" = ").append(item.content).append("();\n");
                            }
                            if (item.name != null) {
                                fieldMap.put(item.name, cI);
                            }
                            cI++;
                        }
                    }
                    indent(cases, 4);
                    cases.append("result = new ").append(rule.contextName).append("Context(");
                    if (cI > 1) {
                        cases.append("c1");
                        for (int i = 2; i < cI; i++) {
                            cases.append(", ").append("c").append(Integer.toString(i));
                        }
                    }
                    cases.append(");\n");
                    for (Map.Entry<String, Integer> ff : fieldMap.entrySet()) {
                        indent(cases, 4);
                        cases.append("result.").append(ff.getKey()).append(" = c").append(Integer.toString(ff.getValue())).append(";\n");
                    }
                    if (entry.getKey().code != null) {
                        indent(cases, 4);
                        cases.append("result.calc").append(Integer.toString(entry.getKey().num)).append("();\n");
                    }
                    indent(cases, 4);
                    cases.append("return result;\n");
                    indent(cases, 3);
                    cases.append("}\n");
                }
                curBuilder = curBuilder.replace("%cases", cases.toString());
                StringJoiner joiner = new StringJoiner(", ");
                Collections.sort(expected);
                for (String ss : expected) {
                    joiner.add(ss.toUpperCase());
                }
                curBuilder = curBuilder.replace("%expectedTokens", joiner.toString());
                builders.append(curBuilder);
            }
            s = s.replace("%builders", builders.toString());
        }

        {
            s = s.replace("%mainRule", grammar.pRules.get(0).contextName)
                    .replace("%mainToken", grammar.pRules.get(0).name);
        }
        System.out.println(Paths.get(".").toFile().getCanonicalPath());
        saveStringToFile(outputDir, className + "Parser.java", s);
    }
}
