package gr.hua.coach.UI;

public class UIProvider {
    public IUI get() {
        return new CLI();
    }
}
