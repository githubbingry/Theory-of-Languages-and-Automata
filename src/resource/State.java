package resource;

public class State {
    private String name;
    private boolean isStart;
    private boolean isFinal;

    public State() {
    }
    public State(String name, boolean isStart, boolean isFinal) {
        this.name = name;
        this.isStart = isStart;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isFinal() {
        return isFinal;
    }
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
    public boolean isStart() {
        return isStart;
    }
    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public static boolean isSameState(State firstState, State secondState) {
        return firstState.getName().equals(secondState.getName())
            && firstState.isStart() == secondState.isStart()
            && firstState.isFinal() == secondState.isFinal();
    }

    @Override
    public String toString() {
        return "State["+ name
            + (isStart ? ", init" : "")
            + (isFinal ? ", final" : "")
            + "]";
    }
}