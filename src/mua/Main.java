package mua;

public class Main {
    public static void main(String[] args) {
        Environment.init();
        while (Environment.in.hasNext()) {
            try {
                Environment.run();
            } catch (InvalidCommand | InvalidName e) {
                e.printStackTrace();
            }
        }
        Environment.in.close();
    }
}