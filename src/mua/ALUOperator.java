package mua;

import java.util.Stack;

public enum ALUOperator {
    PLUS (3, 2, '+') { double eval(double x, double y) { return x+y;} },
    MINUS (3, 2, '-'){ double eval(double x, double y) { return y-x;} },
    MULTIPLE (5, 4, '*') { double eval(double x, double y) { return x*y;} },
    DIVIDE (5, 4, '/') { double eval(double x, double y) { return y/x;} },
    MODULE (5, 4, '%') { double eval(double x, double y) { return y%x;} },
    LBRACKET (1, 6, '(') { double eval(double x, double y) { return 0;} },
    RBRACKET (6, 1, ')') { double eval(double x, double y) { return 0;} },
    EQUAL (0, 0, '=') { double eval(double x, double y) { return 0;} };

    abstract double eval(double x, double y);
    private final int inPrio;
    private final int outPrio;
    private final char op;

    ALUOperator(int inPrio, int outPrio, char op) {
        this.inPrio = inPrio;
        this.outPrio = outPrio;
        this.op = op;
    }
    int getPrio(boolean isInStack) {
        return isInStack ? inPrio : outPrio;
    }

    public static ALUOperator get(char op) {
        for(ALUOperator a: values()) {
            if(a.op == op) return a;
        }
        return null;
    }

    public static Double calculate(String expression, Stack<ALUOperator> operator, Stack<Double> operand) {
        int i;
        char c;
        char prevC = '=';
        boolean isNeg = false;
        for(i=0; i<expression.length(); i++) {
            c = expression.charAt(i);
            if(get(c) != null) {
                ALUOperator o = get(c);
                if(o == MINUS && get(prevC) != null && get(prevC) != RBRACKET) {
                    prevC = c;
                    isNeg = true;
                    continue;
                }
                else if(o == PLUS && get(prevC) != null && get(prevC) != RBRACKET) {
                    prevC = c;
                    isNeg = false;
                    continue;
                }
                else {
                    while(operator.peek().getPrio(true) >= o.getPrio(false)) {
                        ALUOperator tmpO = operator.pop();
                        if(tmpO != LBRACKET) {
                            operand.push(tmpO.eval(operand.pop(), operand.pop()));
                        }
                        else break;
                    }
                    if(o != RBRACKET) operator.push(o);
                }
            }
            else if(c == ':') {
                int j = i+1;
                while(j < expression.length() && !expression.substring(j,j+1).matches("[a-zA-Z0-9_]")) {
                    j++;
                }
                String name = expression.substring(i,j);
                String nameContent = Environment.nameMap.get(name).toString();
                operand.push(Double.parseDouble(nameContent));
                i = j-1;

            }
            else {
                int j = i+1;
                while(j < expression.length() && !expression.substring(j,j+1).matches("[0-9\\.]")) {
                    j++;
                }
                double res = Double.parseDouble(expression.substring(i, j));
                res = isNeg ? -res: res;
                operand.push(res);
                i = j - 1;
            }
            prevC = c;
            isNeg = false;
        }

        while(operator.size() > 1) {
            operand.push(operator.pop().eval(operand.pop(), operand.pop()));
        }

        return operand.peek();
    }
}
