import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static guru.nidi.graphviz.model.Factory.*;

public class Visualizer {
    static int MAX_ID = 0;

    public static void save(Tree root, String where) throws IOException {
        MutableNode main = mutNode(root.text);
        for(Tree t : root.children) {
            traverse(main, t);
        }
        MutableGraph g = mutGraph().add(main);
        Graphviz.useEngine(Collections.singletonList(new GraphvizJdkEngine()));
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(where));
    }

    private static void traverse(MutableNode parent, Tree child) {
        MutableNode me = mutNode(Integer.toString(MAX_ID++)).add("label", child.text);
        if(child.children.isEmpty()) {
            me.attrs().add(Style.FILLED, Shape.RECTANGLE);
        }
        parent.addLink(me);
        for(Tree t : child.children) {
            traverse(me, t);
        }
    }
}
