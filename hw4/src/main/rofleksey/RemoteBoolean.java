package rofleksey;

public class RemoteBoolean {
    private boolean b;

    public RemoteBoolean(boolean b) {
        set(b);
    }

    public void set(boolean b) {
        this.b = b;
    }

    public boolean get() {
        return b;
    }
}
