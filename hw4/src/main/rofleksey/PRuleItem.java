package rofleksey;

public class PRuleItem {
    final String name, content;

    PRuleItem(String content) {
        this.name = null;
        this.content = content;
    }

    PRuleItem(String name, String content) {
        this.name = name;
        this.content = content;
    }

    /*@Override
    public int hashCode() {
        return Objects.hash(name, content);
    }

    @Override
    public boolean equals(Object o1) {
        if(this == o1) {
            return true;
        }
        if(o1 == null || getClass() != o1.getClass()) {
            return false;
        }
        PRuleItem o = (PRuleItem) o1;
        return Objects.equals(name, o.name) && Objects.equals(content, o.content);
    }*/

    @Override
    public String toString() {
        return content;
    }
}
