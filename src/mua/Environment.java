package mua;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

class Environment {
    static HashMap<String, Object> nameMap = new HashMap<>();
    static LinkedList<String> paramQueue = new LinkedList<>();
    static Stack<Command> cmdStack = new Stack<>();
    static Scanner in = new Scanner(System.in);
    static LinkedList<String> inputPool = new LinkedList<>();

    static String nextParameter() {
        while(paramQueue.isEmpty()) {
            Environment.run();
        }
        return paramQueue.pop().substring(1);
    }
    private Environment() {}
    public static void run() {
        String s = next();
        // String cmd = next();
        boolean flag = false;
        // paramQueue.addLast(cmd);
        // String s = paramQueue.removeFirst();
        Command c = Command.get(s);
        if(c != null) {
            cmdStack.push(c);
            flag = true;
        }
        else {
            if(s.charAt(0) == ':') {
                cmdStack.push(Command.THING);
                paramQueue.addFirst("\"" + s.substring(1));
                flag = true;
            }
            else if(s.charAt(0) == '\"') {
                paramQueue.addFirst(s);
            }
            else if(s.charAt(0) == '[') {
                addList2Queue(s);
            }
            else if(s.charAt(0) == '(') {
                paramQueue.addFirst("\"" + executeInfixStatement(readExpressionWithBracket(s)));
            }
            else if(s.matches("(-)?[0-9]\\d*\\.?\\d*")) {
                paramQueue.addFirst("\"" + s);
            }
            else if(s.matches("(true)|(false)")) {
                paramQueue.addFirst("\"" + s);
            }
            else {
                throw new InvalidCommand();
            }
        }
        if(!cmdStack.empty() && flag) {
            String res = cmdStack.peek().apply();
            cmdStack.pop();
            if(!cmdStack.isEmpty()) {
                paramQueue.addFirst("\"" + res);
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
    public static void addList2Queue(String s) {
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
        paramQueue.addFirst("\"" + buffer.toString());
    }
    public static String readExpressionWithBracket(String s) {
        StringBuffer expression = new StringBuffer();
        int noUnclosedBrackets = 0;

        do {
            if(noUnclosedBrackets != 0) s = Environment.next();
            for(char c: s.toCharArray()) {
                if(c == '(') noUnclosedBrackets++;
                if(c == ')') noUnclosedBrackets--;
            }

            if(expression.length() != 0) expression.append(" " + s);
            else expression.append(s);
        }while(noUnclosedBrackets > 0);

        return expression.toString();
    }
    public static String executeInfixStatement(String expression) {
        Stack<ALUOperator> operator = new Stack<>();
        Stack<Double> operand = new Stack<>();
        Stack<Command> prefixOp = new Stack<>();
        Stack<StackInfo> inStack = new Stack<>();

        boolean isNeg = false;
        char prevC = '=';
        operator.push(ALUOperator.EQUAL);
        for(int i=0;i<expression.length(); i++) {
            char c = expression.charAt(i);
            if(ALUOperator.get(c) != null) {
                ALUOperator o = ALUOperator.get(c);
                if(o == ALUOperator.MINUS && ALUOperator.get(prevC) != null && ALUOperator.get(prevC) != ALUOperator.RBRACKET) {
                    prevC = c;
                    isNeg = true;
                    continue;
                }
                else if(o == ALUOperator.PLUS && ALUOperator.get(prevC) != null && ALUOperator.get(prevC) != ALUOperator.RBRACKET) {
                    prevC = c;
                    isNeg = false;
                    continue;
                }
                else {
                    while(operator.peek().getPrio(true) >= o.getPrio(false) && (inStack.isEmpty() || operator.size() > inStack.peek().nOperator)) {
                        ALUOperator tmpO = operator.pop();
                        if(tmpO != ALUOperator.LBRACKET) {
                            operand.push(tmpO.eval(operand.pop(), operand.pop()));
                        }
                        else break;
                    }
                    if(o != ALUOperator.RBRACKET) operator.push(o);
                }
            }
            else if(c == ':') {
                int j = i+1;
                while(j < expression.length() && expression.substring(j,j+1).matches("[a-zA-Z0-9_]")) {
                    j++;
                }
                String name = expression.substring(i+1,j);
                String nameContent = Environment.nameMap.get(name).toString();
                operand.push(Double.parseDouble(nameContent));
                i = j-1;
            }
            else if(Character.isLetter(c)) {
                int j = i+1;
                while(j < expression.length() && Character.isLetter(expression.charAt(j))) {
                    j++;
                }
                
                String command = expression.substring(i, j);
                Command cmd = Command.get(command);
                inStack.push(new StackInfo(operator.size(), operand.size()));
                prefixOp.push(cmd);

                i = j - 1;
            }
            else if(Character.isDigit(c)) {
                int j = i+1;
                while(j < expression.length() && expression.substring(j,j+1).matches("[0-9\\.]")) {
                    j++;
                }
                double res = Double.parseDouble(expression.substring(i, j));
                res = isNeg ? -res: res;
                operand.push(res);
                i = j - 1;

                if(!prefixOp.isEmpty() && operand.size() == inStack.peek().nOperand+2 && operator.size() == inStack.peek().nOperator) {
                    inStack.pop();
                    Double rVal = operand.pop();
                    Double lVal = operand.pop();

                    Environment.inputPool.push(lVal.toString());
                    Environment.inputPool.push(rVal.toString());
                    operand.push(Double.parseDouble(prefixOp.pop().apply()));
                }
            }
            prevC = c;
            isNeg = false;
        }
        
        return operand.pop().toString();
    }
}

class InvalidName extends RuntimeException {
    private static final long serialVersionUID = -8786244584248138482L;
}
class InvalidCommand extends RuntimeException {
    private static final long serialVersionUID = 4684561830530738704L;  
}