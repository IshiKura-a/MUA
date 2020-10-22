package mua;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

class Environment {
    static HashMap<String, Object> nameMap = new HashMap<>();
    static LinkedList<String> cmdQueue = new LinkedList<>();
    static Stack<Command> cmdStack = new Stack<>();
    static Scanner in = new Scanner(System.in);
    static LinkedList<String> inputPool = new LinkedList<>();

    static String nextParameter() {
        while(cmdQueue.isEmpty()) {
            Environment.run();
        }
        return cmdQueue.pop().substring(1);
    }
    private Environment() {}
    public static void run() {
        String cmd = next();
        boolean flag = false;
        cmdQueue.addLast(cmd);
        String s = cmdQueue.removeFirst();
        Command c = Command.get(s);
        if(c != null) {
            cmdStack.push(c);
            flag = true;
        }
        else {
            if(s.charAt(0) == ':') {
                cmdStack.push(Command.THING);
                cmdQueue.addFirst("\"" + s.substring(1));
                flag = true;
            }
            else if(s.charAt(0) == '\"') {
                cmdQueue.addFirst(s);
            }
            else if(s.charAt(0) == '[') {
                StringBuffer buffer = new StringBuffer();
                buffer.append(s);
                int bracketsRemainUnclosed = 1;
                if(s.charAt(s.length()-1) == ']') bracketsRemainUnclosed--;
                while(bracketsRemainUnclosed > 0) {
                    s = next();
                    buffer.append(" " + s);
                    if(s.charAt(0) == '[') bracketsRemainUnclosed++;
                    if(s.charAt(s.length()-1) == ']') bracketsRemainUnclosed--;
                }
                cmdQueue.addFirst("\"" + buffer.toString());
            }
            else if(s.matches("(-)?[0-9]\\d*\\.?\\d*")) {
                cmdQueue.addFirst("\"" + s);
            }
            else if(s.matches("(true)|(false)")) {
                cmdQueue.addFirst("\"" + s);
            }
            else {
                throw new InvalidCommand();
            }
        }
        if(!cmdStack.empty() && flag) {
            String res = cmdStack.peek().apply();
            cmdStack.pop();
            if(!cmdStack.isEmpty()) {
                cmdQueue.addFirst("\"" + res);
            }
        }
    }
    public static boolean hasNext() {
        return !inputPool.isEmpty();
    }
    public static String next() {
        if(inputPool.isEmpty()) {
            return in.next();
        }
        else {
            return inputPool.pop();
        }
    }
    public static void append(String element) {
        inputPool.add(element);
    }
}

class InvalidName extends RuntimeException {
    private static final long serialVersionUID = -8786244584248138482L;
}
class InvalidCommand extends RuntimeException {
    private static final long serialVersionUID = 4684561830530738704L;  
}