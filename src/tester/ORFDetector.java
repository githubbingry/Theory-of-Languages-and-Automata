package tester;

import automaton.DFA;
import resource.Delta;
import resource.State;

public class ORFDetector {
    public static void main(String[] args) {
        // 1. Definisikan state-state untuk DFA pendeteksi ORF
        State q0 = new State("q0", true, false);  // State awal
        State q1 = new State("q1", false, false); // State setelah membaca 'A'
        State q2 = new State("q2", false, false); // State setelah membaca 'T'
        State q3 = new State("q3", false, false); // State setelah membaca 'G'
        State q4 = new State("q4", false, false); // State setelah membaca 'T'
        State q5 = new State("q5", false, false); // State setelah membaca 'A' 'G' dari q4
        State q6 = new State("q6", false, true); // State setelah membaca 'A' 'G' dari q5, TAA, TGA

        
        // 2. Buat Delta untuk DFA pendeteksi start codon 'ATG'
        Delta[] orfTransitions = {
            // Start Codon ATG
            new Delta(q0, 'A', q1),  // q0 → q1 (A)
            new Delta(q1, 'T', q2),  // q1 → q2 (AT)
            new Delta(q2, 'G', q3),  // q2 → q3 (ATG)
            
            // Stop Codon: TAA, TAG, TGA
            new Delta(q3, 'T', q4),  // q3 → q4 (T)
            new Delta(q4, 'A', q5),  // q4 → q5 (TA/TG)
            new Delta(q4, 'G', q5),  // q4 → q5 (TG)
            new Delta(q5, 'A', q6),  // q5 → q6 (TAA/TGA)
            new Delta(q5, 'G', q6),  // q5 → q6 (TAG)
            
            // Transisi Reset (jika input tidak sesuai pola)
            new Delta(q0, 'C', q0), new Delta(q0, 'T', q0), new Delta(q0, 'G', q0),
            new Delta(q1, 'A', q1), new Delta(q1, 'C', q0), new Delta(q1, 'G', q0),
            new Delta(q2, 'A', q1), new Delta(q2, 'T', q0), new Delta(q2, 'C', q0),
            new Delta(q3, 'A', q3), new Delta(q3, 'C', q3), new Delta(q3, 'G', q3),
            new Delta(q4, 'C', q3), new Delta(q4, 'T', q3),
            new Delta(q5, 'C', q3), new Delta(q5, 'T', q4),

            // Transisi loop karena sudah diterima
            new Delta(q6, 'A', q6), new Delta(q6, 'C', q6), new Delta(q6, 'T', q6), new Delta(q6, 'G', q6)
        };
        
        // 3. Buat DFA dari Delta yang telah didefinisikan
        DFA orfDFA = new DFA(orfTransitions, true);
        
        // 4. Validasi apakah DFA yang dibuat valid
        System.out.println("DFA valid? " + orfDFA.isValidDFA());
        System.out.print(orfDFA.toTableStringEpsilon());
        
        // 5. Test beberapa sequence DNA
        String[] testSequences = {
            "ATGTA",
            "ATGTG",
            "ATGTAA",
            "ATATGTAG",
            "ATGTGAA",
            "ATTTG",
            "AAATGG",
            "ATGCATT",
            "ATGCATGAATGTAG",
            "ATGTGTGA",

        };
        
        System.out.println("\nTesting ORF Detection:");
        for (String seq : testSequences) {
            boolean accepted = orfDFA.accept(seq);
            System.out.printf("Sequence '%s': %s%n",
                seq,
                accepted ? "ORF DITEMUKAN" : "BUKAN ORF");
        }
    }
}