import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Tree {
    static final String EPSILON = "ϵ";
    String text;
    List<Tree> children;

    Tree(String text, Tree... children) {
        this.text = text;
        this.children = Arrays.asList(children);
    }

    Tree(String text) {
        this.text = text;
        this.children = Collections.emptyList();
    }

    @Override
    public String toString() {
        if (children.isEmpty()) {
            return text;
        } else {
            return children.stream().map(Tree::toString).collect(Collectors.joining(""));
        }
    }

    static Tree epsilon() {
        return new Tree("");
    }
}
