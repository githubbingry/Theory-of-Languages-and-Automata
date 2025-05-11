package automaton;

import java.util.HashMap;
import java.util.Map;

import resource.Delta;
import resource.DeltaTable;
import resource.State;

public class DFA extends DeltaTable implements FSA{
    public DFA(DeltaTable deltaTable) {
        this(deltaTable.tableDeltas);
    }

    public DFA(DeltaTable deltaTable, boolean doSort) {
        this(deltaTable.tableDeltas, doSort);
    }

    public DFA(Delta[] tableDeltas) {
        super(tableDeltas);
    }

    public DFA(Delta[] tableDeltas, boolean doSort) {
        super(tableDeltas, doSort);
    }

    public boolean isValidDFA() {
        boolean valid = true;
        valid &= super.tableDeltas.length == super.sigma.length * super.states.length;
        for (int i = 0; i < super.sigma.length; i++) {
            valid &= super.sigma[i] != '\u0000'; // no epsilon
        }
        for (int i = 0; i < super.states.length; i++) {
            for (int j = 0; j < super.sigma.length; j++) {
                State[] temp = super.getStatesFrom(super.states[i], super.sigma[j]);
                valid &= temp.length == 1 ;
            }
        }
        return valid;
    }

    @Override
    public boolean accept(String input) {
        State temp = super.getStateFrom(super.initState, input);
        return temp == null ? false : temp.isFinal();
    }
    
    @Override
    public boolean acceptAll(String... input) {
        boolean acc = true;
        for (int i = 0; i < input.length; i++) {
            acc &= accept(input[i]);
        }
        return acc;
    }

    @Override
    public boolean reject(String input) {
        return !accept(input);
    }

    @Override
    public boolean rejectAll(String... input) {
        boolean acc = true;
        for (int i = 0; i < input.length; i++) {
            acc &= reject(input[i]);
        }
        return acc;
    }

    // dfa.generateRandomInputs()
    // 

    public String getGrammar() {
        /*
        Grammar
        S->0A|1C
        A->0C|1B|ε
        B->0A|1C|ε
        C->0C|1C
         */
        String s = "";
        for (int i = 0; i < super.states.length; i++) {
            s += super.states[i].getName() + "->";
            for (int j = 0; j < super.sigma.length; j++) {
                State temp = super.getStateFrom(super.states[i], super.sigma[j]);
                s += ""+super.sigma[j] + temp.getName() + "|";
            }
            if (super.states[i].isFinal()) {
                s += "ε";
            } else {
                s = s.substring(0, s.length()-1);
            }
            s += "\n";
        }
        return s;
    }

    public Map<State, String> getEquation() {
        /*
        Equation
        S = ε
        A = S0 + B0
        B = A1
        C = S1 + A0 + B1 + C0 + C1
        */
        Map<State, String> mss = new HashMap<>();
        for (int i = 0; i < super.states.length; i++) {
            mss.put(super.states[i], "");
        }
        for (int i = 0; i < super.tableDeltas.length; i++) {
            String temp = mss.get(super.tableDeltas[i].getEndState());
            temp +=  (temp.equals("") ? "" : "+") + super.tableDeltas[i].getInitialState().getName() + super.tableDeltas[i].getInput();
            mss.replace(super.tableDeltas[i].getEndState(), temp);
        }
        return mss;
    }

    public String getRegEx() {
        return null;
    }
}
