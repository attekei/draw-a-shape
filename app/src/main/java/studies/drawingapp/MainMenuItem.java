package studies.drawingapp;

public abstract class MainMenuItem {
    public MainMenuItem(String text) {
        this.text = text;
    }

    public String text;
    abstract public void action();

    public String toString() { return text; }
}