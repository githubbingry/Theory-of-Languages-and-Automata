package automaton;

import resource.Delta;
import resource.DeltaTable;
import resource.State;

public class NFA extends DeltaTable{
    public NFA(DeltaTable deltaTable) {
        this(deltaTable.tableDeltas);
    }

    public NFA(DeltaTable deltaTable, boolean doSort) {
        this(deltaTable.tableDeltas, doSort);
    }

    public NFA(Delta[] tableDeltas) {
        super(tableDeltas);
    }

    public NFA(Delta[] tableDeltas, boolean doSort) {
        super(tableDeltas, doSort);
    }

    public static NFA constructFrom(String input, boolean doSort) {
        // halo
        // S -> H -> A -> L -> O
        State[] s = new State[input.length()+1];
        Delta[] d = new Delta[input.length()];
        s[0] = new State("S", true, false);
        for (int i = 1; i < s.length; i++) {
            s[i] = new State(""+(char)(input.charAt(i-1)-'a'+'A')+i, false, i == s.length-1 ? true : false);
        }
        for (int i = 0; i < d.length; i++) {
            d[i] = new Delta(s[i], input.charAt(i), s[i+1]);
        }
        return new NFA(d, doSort);
    }
    
    public static NFA constructFrom(String input) {
        return NFA.constructFrom(input, false);
    }

    public DFA toDFA(boolean doSort) {
        State deadState = new State("DEAD", false, false);
        for (int i = 0; i < super.sigma.length; i++) {
            super.add(new Delta(deadState, super.sigma[i], deadState));
        }
        for (int i = 0; i < super.states.length; i++) {
            for (int j = 0; j < super.sigma.length; j++) {
                if (super.getStateFrom(super.states[i], super.sigma[j]) == null) {
                    super.add(new Delta(super.states[i], super.sigma[j], deadState));
                }
            }
        }
        return new DFA(super.tableDeltas, doSort);
    }

    public DFA toDFA() {
        return this.toDFA(false);
    }
    // nfa.toDFA()

}
