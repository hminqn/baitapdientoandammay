package vn.cmc;

public final class App {
    private App() { }

    public static String greeting() {
        return "Hello Jenkins!";
    }

    public static void main(String[] args) {
        System.out.println(greeting());
    }
}
