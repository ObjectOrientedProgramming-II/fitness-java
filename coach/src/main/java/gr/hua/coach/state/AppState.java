package gr.hua.coach.state;

public class AppState {

    /*
    Basically ena FSM
    Declare States
    Declare Transitions of states
    */
    public enum State {
        EMPTY,           
        HAS_DATA,        
        COMPUTING,       
        ERROR            
    }
    
    private State current = State.EMPTY;
    private StateListener listener;
    
    public interface StateListener {
        void onStateChange(State newState);
    }
    
    public void setListener(StateListener listener) {
        this.listener = listener;
    }
    
    public State getCurrent() {
        return current;
    }
    
    public void transitionTo(State newState) {
        if (current != newState) {
            current = newState;
            if (listener != null) {
                listener.onStateChange(newState);
            }
        }
    }
    
    public boolean canLoadFiles() {
        return current != State.COMPUTING;
    }
    
    public boolean canCompute() {
        return current == State.HAS_DATA;
    }
    
    public boolean canClear() {
        return current != State.COMPUTING && current != State.EMPTY;
    }
}