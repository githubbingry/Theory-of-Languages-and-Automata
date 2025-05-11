package resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeltaTable {
    private ArrayList<Delta> listDeltas;
    private Set<State> stateSet;
    private Set<Character> sigmaSet;
    private Set<Character> sigmaEpsilonSet;
    private List<State> listFinalStates;
    
    public State[] states;
    public Character[] sigma;
    public Character[] sigmaEpsilon;
    public Delta[] tableDeltas;
    public State initState;
    public State[] finalStates;

    public DeltaTable(Delta[] tableDeltas) {
        this.reset(tableDeltas);
    }

    public DeltaTable(Delta[] tableDeltas, boolean doSort) {
        this.reset(tableDeltas);
        this.sort();
    }

    private boolean[] indexDeltaDuplicate(Delta[] tableDeltas) {
        boolean[] temp = new boolean[tableDeltas.length];
        for (int i = 0; i < tableDeltas.length; i++) {
            for (int j = i+1; j < tableDeltas.length; j++) {
                temp[i] |=  Delta.isSameDelta(tableDeltas[i], tableDeltas[j]);
            }
        }
        return temp;
    }

    public void reset(Delta... tableDeltas) {
        this.listDeltas = new ArrayList<>();
        boolean[] validDelta = indexDeltaDuplicate(tableDeltas);
        for (int i = 0; i < tableDeltas.length; i++) {
            if (!validDelta[i]) {
                this.listDeltas.add(tableDeltas[i]);
            }
        }
        this.tableDeltas = this.listDeltas.toArray(Delta[]::new);
        
        this.stateSet = new HashSet<>();
        this.sigmaSet = new HashSet<>();
        this.sigmaEpsilonSet = new HashSet<>();
        this.listFinalStates = new ArrayList<>();
        for (int i = 0; i < tableDeltas.length; i++) {
            this.stateSet.add(tableDeltas[i].getInitialState());
            this.stateSet.add(tableDeltas[i].getEndState());
            this.sigmaSet.add(tableDeltas[i].getInput());
            this.sigmaEpsilonSet.add(tableDeltas[i].getInputEpsilon());
            if (tableDeltas[i].getInitialState().isStart()) {
                this.initState = tableDeltas[i].getInitialState();
            } else if (tableDeltas[i].getInitialState().isFinal()) {
                this.listFinalStates.add(tableDeltas[i].getInitialState());
            }
        }
        
        this.states = this.stateSet.toArray(State[]::new);
        this.sigma = this.sigmaSet.toArray(Character[]::new);
        this.sigmaEpsilon = this.sigmaEpsilonSet.toArray(Character[]::new);
        this.finalStates = this.listFinalStates.toArray(State[]::new);
    }

    public void add(Delta newDelta) {
        this.listDeltas.add(newDelta);
        this.reset(this.listDeltas.toArray(Delta[]::new));
    }

    public void add(Delta[] newDeltaTables) {
        Delta[] combinedDeltaTables = new Delta[this.tableDeltas.length + newDeltaTables.length];
        System.arraycopy(this.tableDeltas, 0, combinedDeltaTables, 0, this.tableDeltas.length);
        System.arraycopy(newDeltaTables, 0, combinedDeltaTables, this.tableDeltas.length, newDeltaTables.length);
        this.reset(combinedDeltaTables);
    }

    public void remove(Delta removedDelta) {
        for (int i = 0; i < this.listDeltas.size(); i++) {
            if (Delta.isSameDelta(this.listDeltas.get(i), removedDelta)) {
                this.listDeltas.remove(i);
            }
        }
        this.reset(this.listDeltas.toArray(Delta[]::new));
    }

    public void sort() {
        Collections.sort(this.listDeltas, (d1, d2) -> d1.getInitialState().getName().compareTo(d2.getInitialState().getName()));
        Collections.sort(this.listDeltas, (d1, d2) -> Boolean.compare(d2.getInitialState().isStart(), d1.getInitialState().isStart()));
        this.tableDeltas = this.listDeltas.toArray(Delta[]::new);
        
        Arrays.sort(this.states, (s1, s2) -> s1.getName().compareTo(s2.getName()));
        Arrays.sort(this.states, (s1, s2) -> Boolean.compare(s2.isStart(), s1.isStart()));

        Arrays.sort(this.sigma);
    }
    
    public State getStateFrom(State initialState, char input) {
        if (initialState == null) return null;
        for (int i = 0; i < this.tableDeltas.length; i++) {
            if (State.isSameState(tableDeltas[i].getInitialState(), initialState) && Delta.compareInputEpsilon(tableDeltas[i].getInput(), input)) {
                return tableDeltas[i].getEndState();
            }
        }
        return null;
    }

    public State getStateFrom(State initialState, String input) {
        if (initialState == null) return null;
        char[] c = input.toCharArray();
        State initState = getStateFrom(initialState, c[0]);
        for (int i = 1; i < c.length; i++) {
            initState = getStateFrom(initState, c[i]);
        }
        return initState;
    }

    public State[] getStatesFrom(State initialState, char input) {
        if (initialState == null) return null;
        List<State> resultState = new ArrayList<>();
        for (int i = 0; i < tableDeltas.length; i++) {
            if (State.isSameState(tableDeltas[i].getInitialState(), initialState) && Delta.compareInputEpsilon(tableDeltas[i].getInput(), input)) {
                resultState.add(tableDeltas[i].getEndState());
            }
        }
        return resultState.toArray(State[]::new);
    }

    public State[] getStatesFrom(State[] initialState, char input) {
        if (initialState == null) return null;
        Set<State> resultState = new HashSet<>();
        for (int i = 0; i < tableDeltas.length; i++) {
            for (int j = 0; j < initialState.length; j++) {
                if (State.isSameState(tableDeltas[i].getInitialState(), initialState[j]) && Delta.compareInputEpsilon(tableDeltas[i].getInput(), input)) {
                    resultState.add(tableDeltas[i].getEndState());
                }
            }
        }
        return resultState.toArray(State[]::new);
    }

    public State[] getStatesFrom(State initialState, String input) {
        if (initialState == null) return null;
        char[] c = input.toCharArray();
        State[] initStates = getStatesFrom(initialState, c[0]);
        for (int i = 1; i < input.length(); i++) {
            initStates = getStatesFrom(initStates, c[i]);
        }
        return initStates;
    }

    public String toStringEpsilon() {
        String s = "";
        for (int i = 0; i < tableDeltas.length; i++) {
            if (tableDeltas[i].getInitialState().isStart()) {
                s += "S";
            } else if (tableDeltas[i].getInitialState().isFinal()) {
                s += "F";
            }
            s += tableDeltas[i].toStringEpsilon() + "\n";
        }
        return s;
    }

    public String toTableStringEpsilon() {
        String colseparator = "\t|";
        String header = "Delta" + colseparator;
        String rowseparator = "--------";
        for (int i = 0; i < sigmaEpsilon.length; i++) {
            header += sigmaEpsilon[i] + colseparator;
            rowseparator += "--------";
        }
        header += "\n";
        rowseparator += "+\n";
        String content = "";
        for (int i = 0; i < states.length; i++) {
            content += states[i].getName()+colseparator;
            for (int j = 0; j < sigmaEpsilon.length; j++) {
                State[] temp = getStatesFrom(states[i], sigmaEpsilon[j]);
                if (temp.length == 0) {
                    content += "[],";
                } else {
                    for (int k = 0; k < temp.length; k++) {
                        content += temp[k].getName()+",";
                    }
                }
                content = content.substring(0, content.length()-1) + colseparator;
            }
            content += "\n";
        }
        return header + rowseparator + content;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < tableDeltas.length; i++) {
            if (tableDeltas[i].getInitialState().isStart()) {
                s += "S";
            } else if (tableDeltas[i].getInitialState().isFinal()) {
                s += "F";
            }
            s += tableDeltas[i].toString() + "\n";
        }
        return s;
    }

    public String toTableString() {
        String colseparator = "\t|";
        String header = "Delta" + colseparator;
        String rowseparator = "--------";
        for (int i = 0; i < sigma.length; i++) {
            header += sigma[i] + colseparator;
            rowseparator += "--------";
        }
        header += "\n";
        rowseparator += "+\n";
        String content = "";
        for (int i = 0; i < states.length; i++) {
            content += states[i].getName()+colseparator;
            for (int j = 0; j < sigma.length; j++) {
                State[] temp = getStatesFrom(states[i], sigma[j]);
                for (int k = 0; k < temp.length; k++) {
                    content += temp[k].getName()+",";
                }
                content = content.substring(0, content.length()-1) + colseparator;
            }
            content += "\n";
        }
        return header + rowseparator + content;
    }
}
