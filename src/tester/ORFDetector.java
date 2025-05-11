package tester;

import automaton.DFA;
import resource.Delta;
import resource.State;

public class ORFDetector {
    public static void main(String[] args) {
        // 1. Definisikan state-state untuk DFA pendeteksi ORF
        State q0 = new State("q0", true, false);  // State awal
        State q1 = new State("q1", false, false); // State setelah membaca 'A'
        State q2 = new State("q2", false, false); // State setelah membaca 'AT'
        State q3 = new State("q3", false, true);  // State akhir (menerima 'ATG')
        
        // 2. Buat Delta untuk DFA pendeteksi start codon 'ATG'
        Delta[] orfTransitions = {
            // Delta untuk pola 'ATG'
            new Delta(q0, 'A', q1),
            new Delta(q1, 'T', q2),
            new Delta(q2, 'G', q3),
            
            // Delta untuk karakter lainnya (reset state)
            new Delta(q0, 'T', q0),
            new Delta(q0, 'C', q0),
            new Delta(q0, 'G', q0),
            
            new Delta(q1, 'A', q1),
            new Delta(q1, 'C', q0),
            new Delta(q1, 'G', q0),
            
            new Delta(q2, 'A', q1),
            new Delta(q2, 'T', q0),
            new Delta(q2, 'C', q0),

            // Delta untuk final state (ke diri sendiri karena sudah terdeteksi)
            new Delta(q3, 'A', q3),
            new Delta(q3, 'T', q3),
            new Delta(q3, 'C', q3),
            new Delta(q3, 'G', q3),
        };
        
        // 3. Buat DFA dari Delta yang telah didefinisikan
        DFA orfDFA = new DFA(orfTransitions);
        
        // 4. Validasi apakah DFA yang dibuat valid
        System.out.println("DFA valid? " + orfDFA.isValidDFA());
        System.out.print(orfDFA.toTableStringEpsilon());
        
        // 5. Test beberapa sequence DNA
        String[] testSequences = {
            "ATG",      // ORF valid
            "ATGCGT",    // Dimulai dengan ORF valid
            "TAGATG",    // ORF di tengah
            "ATTTG",     // Tidak ada ORF
            "AAATGG"     // Tidak ada ORF
        };
        
        System.out.println("\nTesting ORF Detection:");
        for (String seq : testSequences) {
            boolean accepted = orfDFA.accept(seq);
            System.out.printf("Sequence '%s': %s%n", 
                seq, 
                accepted ? "ORF DITEMUKAN" : "BUKAN ORF");
        }
        
        // 6. Deteksi ORF dalam sequence panjang
        String longDnaSequence = "ATGCATGAATGTAG";
        System.out.println("\nScanning long sequence: " + longDnaSequence);
        
        // Scan setiap 3 nukleotida (codon)
        for (int i = 0; i <= longDnaSequence.length() - 3; i++) {
            String codon = longDnaSequence.substring(i, i + 3);
            if (orfDFA.accept(codon)) {
                System.out.printf("ORF ditemukan pada posisi %d-%d: %s%n", 
                    i, i+2, codon);
            }
        }
    }
}