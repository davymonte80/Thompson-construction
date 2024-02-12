import java.util.Stack;

class State {
    char symbol;
    State next1;
    State next2;
    boolean nullable;
    int id;

    public State(char symbol, State next1, State next2) {
        this.symbol = symbol;
        this.next1 = next1;
        this.next2 = next2;
        this.nullable = false;
    }

    public State(char symbol) {
        this.symbol = symbol;
        this.next1 = null;
        this.next2 = null;
        this.nullable = false;
    }

    public State() {
        this.symbol = '\0';
        this.next1 = null;
        this.next2 = null;
        this.nullable = false;
    }
}

public class ThompsonConstruction {

    private static int stateId = 0;

    private static State createState() {
        State newState = new State();
        newState.id = stateId++;
        return newState;
    }

    public static State thompson(String regex) {
        Stack<State> stack = new Stack<>();

        try {
            for (int i = 0; i < regex.length(); i++) {
                char c = regex.charAt(i);

                if (c == '.') {
                    State s1 = stack.pop();
                    State s2 = stack.pop();
                    s2.next1 = s1;
                    s2.next2 = null;
                    stack.push(s2);
                } else if (c == '|') {
                    State s1 = stack.pop();
                    State s2 = stack.pop();
                    State newStart = createState();
                    newStart.next1 = s2;
                    newStart.next2 = s1;
                    stack.push(newStart);
                } else if (c == '*') {
                    State s = stack.pop();
                    State newStart = createState();
                    newStart.next1 = s;
                    newStart.next2 = null;
                    s.next2 = newStart;
                    stack.push(newStart);
                } else {
                    State newState = createState();
                    newState.symbol = c;
                    stack.push(newState);
                }
            }
        } catch (Exception e) {
            System.err.println("Invalid regular expression: " + regex);
            return null;
        }

        State acceptState = createState();
        acceptState.nullable = true;

        State startState = stack.pop();
        acceptState.next1 = startState;
        acceptState.next2 = null;

        return acceptState;
    }

    public static void main(String[] args) {
        String regex = "a.b|c*";
        State nfa = thompson(regex);
        if (nfa != null) {
            System.out.println("NFA constructed from the regular expression '" + regex + "':");
            printNFA(nfa);
        }
    }

    private static void printNFA(State state) {
        if (state == null)
            return;
        System.out.println("State " + state.id + " (Nullable: " + state.nullable + ")");

        if (state.next1 != null) {
            System.out.println("    Next1: " + state.next1.id);
            printNFA(state.next1);
        }

        if (state.next2 != null) {
            System.out.println("    Next2: " + state.next2.id);
            printNFA(state.next2);
        }
    }
}