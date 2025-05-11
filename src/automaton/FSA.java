package automaton;

public interface FSA {
    public boolean accept(String input);
    public boolean acceptAll(String... input);
    public boolean reject(String input);
    public boolean rejectAll(String... input);
}
