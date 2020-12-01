package mua;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

class Environment {
    static Scanner in = new Scanner(System.in);
    static LinkedList<LinkedList<Command>> contextCommandStack = new LinkedList<>();
    static LinkedList<HashMap<String, String>> contextNameMap = new LinkedList<>();
    static LinkedList<LinkedList<String>> contextInputPool = new LinkedList<>();
    static LinkedList<LinkedList<String>> contextParameterQ = new LinkedList<>();
    static LinkedList<String> contextReturnVal = new LinkedList<>();

    public static void init() {
        contextReturnVal.push("");
        contextParameterQ.add(new LinkedList<>());
        contextCommandStack.add(new LinkedList<>());
        contextNameMap.add(new HashMap<>());
        contextInputPool.add(new LinkedList<>());
    }

    private Environment() {
    }

    public static void run() {
        String s = next();
        boolean flag = false;
        Command c = Command.get(s);
        if (c != null) {
            push2CurCommandStack(c);
            flag = true;
        } else {
            if (s.charAt(0) == ':') {
                push2CurCommandStack(Command.THING);
                contextParameterQ.peek().addFirst("\"" + s.substring(1));
                flag = true;
            } else if (s.charAt(0) == '\"') {
                contextParameterQ.peek().addFirst(s);
            } else if (s.charAt(0) == '[') {
                addList2ParameterQ(s);
            } else if (s.charAt(0) == '(') {
                contextParameterQ.peek().addFirst("\"" + executeInfixStatement(readExpressionWithBracket(s)));
            } else if (s.matches("(-)?[0-9]\\d*\\.?\\d*")) {
                contextParameterQ.peek().addFirst("\"" + s);
            } else if (s.matches("(true)|(false)")) {
                contextParameterQ.peek().addFirst("\"" + s);
            } else {
                push2CurCommandStack(Command.FUNC);
                contextParameterQ.peek().addFirst("\"" + getNameContent(s));
                flag = true;
            }
        }
        if (!contextCommandStack.peek().isEmpty() && flag) {
            String res = contextCommandStack.peek().getLast().apply();
            contextCommandStack.peek().removeLast();
            if (!contextCommandStack.peek().isEmpty()) {
                contextParameterQ.peek().addFirst("\"" + res);
            } else
                contextReturnVal.set(0, res);
        }
    }

    public static boolean hasNextInPool() {
        return !contextInputPool.peek().isEmpty();
    }

    public static String next() {
        if (contextInputPool.peek().isEmpty()) {
            return in.next();
        } else
            return contextInputPool.peek().pop();
    }

    public static void allocateNewCommandStack() {
        contextCommandStack.push(new LinkedList<>());
    }

    public static void push2CurCommandStack(Command c) {
        contextCommandStack.peek().add(c);
    }

    public static void removeCurCommandStack() {
        contextCommandStack.pop();
    }

    public static void allocateNewInputPool() {
        contextInputPool.push(new LinkedList<>());
    }

    public static void push2CurInputPool(String element) {
        contextInputPool.peek().add(element);
    }

    public static void clearCurInputPool() {
        contextInputPool.peek().clear();
    }

    public static void removeCurInputPool() {
        contextInputPool.pop();
    }

    public static void removeCurNameMap() {
        contextNameMap.pop();
    }

    public static void allocateNewParameterQ() {
        contextParameterQ.push(new LinkedList<>());
    }

    public static void removeCurParameterQ() {
        contextParameterQ.pop();
    }

    public static String nextParameter() {
        while (contextParameterQ.peek().isEmpty()) {
            Environment.run();
        }
        return contextParameterQ.peek().pop().substring(1);
    }

    public static int countBracket(String s) {
        int bracketsRemainUnclosed = 0;
        for (char c : s.toCharArray()) {
            if (c == '[') {
                bracketsRemainUnclosed++;
            } else if (c == ']') {
                bracketsRemainUnclosed--;
            }
        }
        return bracketsRemainUnclosed;
    }

    public static String getNameContent(String key) {
        String res;
        if ((res = contextNameMap.peek().get(key)) != null || (res = contextNameMap.getLast().get(key)) != null) {
            return res;
        } else {
            throw new InvalidName();
        }
    }

    public static void addList2ParameterQ(String s) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(s);
        int bracketsRemainUnclosed = countBracket(s);

        while (bracketsRemainUnclosed > 0) {
            s = next();
            buffer.append(" " + s);
            bracketsRemainUnclosed += countBracket(s);
        }
        contextParameterQ.peek().addFirst("\"" + buffer.toString());
    }

    public static String readExpressionWithBracket(String s) {
        StringBuffer expression = new StringBuffer();
        int noUnclosedBrackets = 0;

        do {
            if (noUnclosedBrackets != 0)
                s = Environment.next();
            for (char c : s.toCharArray()) {
                if (c == '(')
                    noUnclosedBrackets++;
                if (c == ')')
                    noUnclosedBrackets--;
            }

            if (expression.length() != 0)
                expression.append(" " + s);
            else
                expression.append(s);
        } while (noUnclosedBrackets > 0);

        return expression.toString();
    }

    public static String executeInfixStatement(String expression) {
        LinkedList<ALUOperator> operator = new LinkedList<>();
        LinkedList<Double> operand = new LinkedList<>();
        LinkedList<CommandInfo> prefixOp = new LinkedList<>();
        LinkedList<StackInfo> inStack = new LinkedList<>();

        boolean isNeg = false;
        char prevC = '=';
        operator.push(ALUOperator.EQUAL);
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (ALUOperator.get(c) != null) {
                ALUOperator o = ALUOperator.get(c);
                if (o == ALUOperator.MINUS && ALUOperator.get(prevC) != null
                        && ALUOperator.get(prevC) != ALUOperator.RBRACKET) {
                    prevC = c;
                    isNeg = true;
                    continue;
                } else if (o == ALUOperator.PLUS && ALUOperator.get(prevC) != null
                        && ALUOperator.get(prevC) != ALUOperator.RBRACKET) {
                    prevC = c;
                    isNeg = false;
                    continue;
                } else {
                    while (operator.peek().getPrio(true) >= o.getPrio(false)
                            && (inStack.isEmpty() || operator.size() > inStack.peek().nOperator)) {
                        ALUOperator tmpO = operator.pop();
                        if (tmpO != ALUOperator.LBRACKET) {
                            operand.push(tmpO.eval(operand.pop(), operand.pop()));
                        } else
                            break;
                    }
                    if (o != ALUOperator.RBRACKET)
                        operator.push(o);
                }
            } else if (c == ':') {
                int j = i + 1;
                while (j < expression.length() && expression.substring(j, j + 1).matches("[a-zA-Z0-9_]")) {
                    j++;
                }
                String name = expression.substring(i + 1, j);
                String nameContent = getNameContent(name);
                operand.push(Double.parseDouble(nameContent));
                i = j - 1;
            } else if (Character.isLetter(c)) {
                int j = i + 1;
                while (j < expression.length() && Character.isLetter(expression.charAt(j))) {
                    j++;
                }

                String command = expression.substring(i, j);
                Command cmd = Command.get(command);
                if (cmd == null) {
                    contextParameterQ.peek().addFirst("\"" + getNameContent(command));
                    cmd = Command.FUNC;
                }
                inStack.push(new StackInfo(operator.size(), operand.size()));
                prefixOp.push(new CommandInfo(cmd,
                        cmd == Command.FUNC
                                ? MuaList.parseMuaList(MuaList.parseMuaList(getNameContent(command)).get(0)).size()
                                : cmd.getOpNeed()));

                i = j - 1;
            } else if (Character.isDigit(c)) {
                int j = i + 1;
                while (j < expression.length() && expression.substring(j, j + 1).matches("[0-9\\.]")) {
                    j++;
                }
                double res = Double.parseDouble(expression.substring(i, j));
                res = isNeg ? -res : res;
                operand.push(res);
                i = j - 1;
            }
            prevC = c;
            isNeg = false;

            if (!prefixOp.isEmpty() && operand.size() == inStack.peek().nOperand + prefixOp.peek().opNeed
                    && operator.size() == inStack.peek().nOperator) {
                inStack.pop();
                for (int cnt = 0; cnt < prefixOp.peek().opNeed; cnt++) {
                    Environment.push2CurInputPool(operand.pop().toString());
                }
                operand.push(Double.parseDouble(prefixOp.pop().cmd.apply()));
            }
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

class InvalidFunction extends RuntimeException {
    private static final long serialVersionUID = 4684561830530738704L;
}