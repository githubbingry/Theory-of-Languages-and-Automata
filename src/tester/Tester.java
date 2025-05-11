package tester;

import java.util.List;
import java.util.Map;

import automaton.DFA;
import automaton.NFA;
import resource.Delta;
import resource.DeltaTable;
import resource.State;

public class Tester {
    public static void main(String[] args) {
        // test1();
        // test2();
        // test3();
        // test4();
        test5();
    }

    static void test5() {
        
    } 

    // test nfa
    static void test4() {
        NFA nfa = NFA.constructFrom("bodoh", true);
        System.out.println(nfa.toTableString());

        DFA dfa = nfa.toDFA();
        System.out.println(dfa.toTableString());
        System.out.println(dfa.accept("bodoh"));
        System.out.println(dfa.accept("abodoh"));
    }

    // test duplicate
    static void test3() {
        State S = new State("S", true, false);
        
        State A = new State("A", false, true);
        State B = new State("B", false, true);
        State C = new State("C", false, false);

        DeltaTable dt = new DeltaTable(new Delta[]{
            new Delta(S, '0', A),   new Delta(S, '1', C),
            new Delta(A, '0', C),   new Delta(A, '1', B),
            new Delta(A, '0', C),   new Delta(A, '1', B),
            new Delta(B, '0', A),   new Delta(B, '1', C),
            new Delta(B, '0', A),   new Delta(B, '1', C),
            new Delta(B, '0', A),   new Delta(B, '1', C),
            new Delta(C, '0', C),   new Delta(C, '1', C),
            new Delta(C, '0', C),   new Delta(C, '1', C),
            new Delta(C, '0', C),   new Delta(C, '1', C),
            new Delta(C, '0', C),   new Delta(C, '1', C),
        });

        DFA dfa = new DFA(dt, true); // regex : (01)*+(01)*0
                                                    // 0(10)*+0(10)*1
        /*
        Grammar
        S->0A|1C
        A->0C|1B|ε
        B->0A|1C|ε
        C->0C|1C

        Equation
        S = ε
        A = S0 + B0
        B = A1
        C = S1 + A0 + B1 + C0 + C1

        Arden's Theorem:
        Let P and Q be two regular expressions.
        If P does not contain null string, then R = Q + RP has a unique solution that is R = QP*

        Note : regex doesnt include dead/useless state

        Solve :
        from equation, C is useless cuz u cant go to final state, so only S, A, B matters, and if u subs:
        A = 0 + A10
        and by Arden's Theorem it become:
        A = 0(10)*
        subs A to B:
        B = 0(10)*1
        cuz both A and B are final state, so the regex of the DFA is:
        0(10)*+0(10)*1
        */

        System.out.println(dfa.isValidDFA());
        System.out.println(dfa.toTableStringEpsilon());

        System.out.println(dfa.acceptAll("0", "01", "0101", "0101010"));
        System.out.println(dfa.rejectAll("1", "011", "1010"));
        System.out.println(dfa.reject("001"));
        System.out.println(dfa.reject("01001"));

        System.out.println(dfa.getGrammar());
        Map<State, String> mss = dfa.getEquation();
        System.out.println(mss.toString());
    }

    // test dfa
    static void test2() {
        /*
        alternate 0 n 1 : https://www.geeksforgeeks.org/dfa-of-alternate-0s-and-1s/
        regex : (01)^++(10)^++(01)*0+(10)*1
        ∈ (no input, 0 and 1)
        010101….. (string that starts with 0 followed by 1 and so on). regex : (01)*+(01)*0
        101010….. (string that starts with 1 followed by 0 and so on). regex : (10)*+(10)*1
        

        Q = {S, A, B, C, P, Q, R}
        Sigma = {0, 1}
        S = S
        F = {A, B, P, Q}

        D   0   1
        S   A   P
        A   C   B
        B   A   C
        C   C   C
        P   Q   R
        Q   R   P
        R   R   R
        */

        State S = new State("S", true, false);
        
        State A = new State("A", false, true);
        State B = new State("B", false, true);
        State C = new State("C", false, false);
        
        State P = new State("P", false, true);
        State Q = new State("Q", false, true);
        State R = new State("R", false, false);

        DeltaTable dt = new DeltaTable(new Delta[]{
            new Delta(S, '0', A),   new Delta(S, '1', P),
            new Delta(A, '0', C),   new Delta(A, '1', B),
            new Delta(B, '0', A),   new Delta(B, '1', C),
            new Delta(C, '0', C),   new Delta(C, '1', C),
            new Delta(P, '0', Q),   new Delta(P, '1', R),
            new Delta(Q, '0', R),   new Delta(Q, '1', P),
            new Delta(R, '0', R),   new Delta(R, '1', R),
        });

        DFA dfa = new DFA(dt, true);

        System.out.println(dfa.accept("010101"));
        System.out.println(dfa.accept("10101"));
        System.out.println(dfa.accept("0010"));

        System.out.println(dfa.toStringEpsilon());
        System.out.println(dfa.toTableStringEpsilon());

        System.out.println(dfa.isValidDFA());
    }

    //test resource
    static void test1() {
        /*
        Q = {S, A, B}
        Sigma = {a, b}
        S = S
        F = B
        D   a   b
        S   A   B
        A   B   A
        B   S   S

        D(S, a) = A
        D(S, b) = B
        D(A, a) = B
        D(A, b) = A
        D(B, a) = S
        D(B, b) = S
        
        (((ab*a|b){1})(a|b){0,1})+
        */

        State S = new State("S", true, false);
        State A = new State("A", false, false);
        State B = new State("B", false, true);

        DeltaTable dt = new DeltaTable(new Delta[]{
            new Delta(S, 'a', A),
            new Delta(A, 'a', B),
            new Delta(S, 'b', B),
            new Delta(B, 'a', S),
            new Delta(A, 'b', A),
            new Delta(B, 'b', S),
        });

        System.out.println(dt.getStateFrom(A, 'a'));
        System.out.println(dt.getStateFrom(S, "abab"));

        dt.sort();
        System.out.println(dt.toString());
        System.out.println(dt.toTableString());
        System.out.println(List.of(dt.states).toString());
        System.out.println(List.of(dt.sigma).toString());

        dt.add(new Delta[]{
            new Delta(S, 'a', B),
            new Delta(A, '\u0000', S),
        });
        dt.remove(new Delta(A, 'b', A));
        dt.sort();
        System.out.println(dt.toStringEpsilon());
        System.out.println(dt.toTableStringEpsilon());

        System.out.println(List.of(dt.getStatesFrom(S, 'a')).toString());
        System.out.println(List.of(
            dt.getStatesFrom(new State[]{S, B}, 'a')
        ).toString());
        System.out.println(List.of(dt.getStatesFrom(S, "aaab")).toString());
    }
}
