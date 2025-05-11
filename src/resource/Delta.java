package resource;

public class Delta {
    private State initialState;
    private char input;
    private State endState;
    
    public Delta() {
    }
    public Delta(State initialState, char input, State endState) {
        this.initialState = initialState;
        this.input = input;
        this.endState = endState;
    }

    public boolean isInputEmpty() {
        return this.input == '\u0000';
    }

    public char getInputEpsilon() {
        return isInputEmpty() ? 'ε' : input;
    }

    public static boolean compareInputEpsilon(char firstInput, char secondInput) {
        if (firstInput == 'ε') {
            firstInput = '\u0000';
        }
        if (secondInput == 'ε') {
            secondInput = '\u0000';
        }
        return firstInput == secondInput;
    }
    
    public State getInitialState() {
        return initialState;
    }
    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }
    public char getInput() {
        return input;
    }
    public void setInput(char input) {
        this.input = input;
    }
    public State getEndState() {
        return endState;
    }
    public void setEndState(State endState) {
        this.endState = endState;
    }

    public static boolean isSameDelta(Delta firstDelta, Delta secondDelta) {
        return State.isSameState(firstDelta.initialState, secondDelta.initialState)
            && firstDelta.input == secondDelta.input
            && State.isSameState(firstDelta.endState, secondDelta.endState);
    }

    @Override
    public String toString() {
        return "D(" + initialState.getName() + ", " + input + ") = " + endState.getName();
    }

    public String toStringEpsilon() {
        return "D(" + initialState.getName() + ", " + getInputEpsilon() + ") = " + endState.getName();
    }
}
