package converting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

// from https://gist.github.com/Niyush-04/2b8ffc39817c1dd42c03ac242146bb09
/*

# NFA to DFA Conversion

![Circuit Diagram](https://drive.google.com/uc?export=view&id=1siiQXGW3UOfNkeuxMGxC_kZwA8imqmWa)

## Output of the Above Code

- **NFA to DFA Conversion**
  - Enter the number of states: `3`  
    *(Here total state is 3, i.e., q0, q1, q2)*  
  - Enter number of final states: `1`  
    *(Only one final state in the NFA)*  
  - Enter final states: `2`  
    *(Here q2 is a final state, hence "2")*  
  - Enter the number of rules according to NFA: `5`  
    *(Rules are all the transitions for the inputs)*  

### Transition Rules:
- `0 0 0` *(q0 → 0 → q0)*  
- `0 1 0` *(q0 → 1 → q0)*  
- `0 1 1` *(q0 → 1 → q1)*  
- `1 0 2` *(q1 → 0 → q2)*  
- `1 1 2` *(q1 → 1 → q2)*  

### Input Initial State:
- Enter initial state: `0`

### Distinct States Table:

| **STATE**       | **0**         | **1**           |
|------------------|---------------|-----------------|
| q0              | q0            | q0, q1          |
| q0, q1          | q0, q2        | q0, q1, q2      |
| q0, q2          | q0            | q0, q1          |
| q0, q1, q2      | q0, q2        | q0, q1, q2      |

---

### Test Strings:

- Enter string: `100011`  
  **Output:** String Accepted

---

## Credit
Original C code and concept by [Kipawa](https://gist.github.com/kipawa/10fec56cab1f8d0c33a9)

*/
public class NFAtoDFA {
    static int[][][] nfa;  // NFA transition table
    static int[] dfaStates = new int[10000];  // Array to store DFA states
    static boolean[] isDfaState = new boolean[10000];  // Tracks visited DFA states
    static int[] finalStates;  // Array of final states
    static Map<Integer, Integer[]> dfaTransitions = new HashMap<>();  // DFA transitions map
    static int stateCount = 0;  // Total DFA states count

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("***** NFA to DFA Conversion *****");

        // Input NFA details (states, final states, transition rules)
        int n = sc.nextInt();
        nfa = new int[n][2][n];  // Transition table for NFA

        // Input final states and transitions for NFA
        int fCount = sc.nextInt();
        finalStates = new int[fCount];
        for (int i = 0; i < fCount; i++) finalStates[i] = sc.nextInt();
        int rules = sc.nextInt();
        for (int i = 0; i < rules; i++) {
            int from = sc.nextInt();
            int input = sc.nextInt();
            int to = sc.nextInt();
            nfa[from][input][to] = 1;  // Define transition rule
        }

        // Initial state for the NFA
        int initialState = sc.nextInt();
        int initialStateMask = (int) Math.pow(2, initialState);

        // Construct DFA from NFA
        constructDfaTransitions(initialStateMask, n);

        // Print DFA states and transitions
        System.out.println("\nThe total number of distinct states are:");
        for (int i = 0; i < stateCount; i++) {
            int state = dfaStates[i];
            String stateString = printState(state, n);
            Integer[] transitions = dfaTransitions.get(state);
            String transition0 = printState(transitions[0], n);
            String transition1 = printState(transitions[1], n);
            System.out.println(String.format("%-15s %-15s %-15s", stateString, transition0, transition1));
        }

        // Test a string on the DFA
        String str = sc.next();
        if (isStringAccepted(str, initialStateMask, n)) {
            System.out.println("String Accepted");
        } else {
            System.out.println("String Rejected");
        }
        sc.close();
    }

    // Converts the NFA to DFA by calculating the transition states
    static void constructDfaTransitions(int initialStateMask, int n) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(initialStateMask);
        isDfaState[initialStateMask] = true;
        dfaStates[stateCount++] = initialStateMask;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            Integer[] transitions = new Integer[2];
            Arrays.fill(transitions, 0);

            // Calculate transitions for input 0 and 1
            for (int input = 0; input < 2; input++) {
                for (int i = 0; i < n; i++) {
                    if ((current & (1 << i)) != 0) {
                        for (int j = 0; j < n; j++) {
                            if (nfa[i][input][j] == 1) {
                                transitions[input] |= (1 << j);  // Set transition bit
                            }
                        }
                    }
                }

                // Add unvisited states to the queue
                if (!isDfaState[transitions[input]]) {
                    isDfaState[transitions[input]] = true;
                    queue.add(transitions[input]);
                    dfaStates[stateCount++] = transitions[input];
                }
            }
            dfaTransitions.put(current, transitions);
        }
    }

    // Converts state to string format (e.g., q0, q1, ...)
    static String printState(int state, int n) {
        if (state == 0) return "φ";  // Empty state
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if ((state & (1 << i)) != 0) sb.append("q").append(i).append(",");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "φ";
    }

    // Checks if the string is accepted by the DFA
    static boolean isStringAccepted(String str, int initialStateMask, int n) {
        int current = initialStateMask;
        for (char c : str.toCharArray()) {
            int input = c - '0';
            Integer[] transitions = dfaTransitions.get(current);
            if (transitions == null || transitions[input] == 0) return false;
            current = transitions[input];
        }

        // Check if current state is a final state
        for (int finalState : finalStates) {
            if ((current & (1 << finalState)) != 0) return true;
        }
        return false;
    }
}
