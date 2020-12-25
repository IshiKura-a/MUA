package mua;

import java.util.Map.Entry;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;

public enum Command {
    MAKE("make", 2) {
        @Override
        public String apply() {
            String key = Environment.nextParameter();
            String value = Environment.nextParameter();
            if (key.matches("^[_a-zA-Z]\\w*")) {
                Environment.contextNameMap.peek().put(key, value);
                return value;
            } else {
                throw new InvalidName();
            }
        }
    },
    THING("thing", 1) {
        @Override
        public String apply() {
            String name = Environment.nextParameter();
            return Environment.getNameContent(name);
        }
    },
    READ("read", 0) {
        @Override
        public String apply() {
            return Environment.in.next();
        }
    },
    PRINT("print", 1) {
        @Override
        public String apply() {
            String value = Environment.nextParameter();
            if (value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']')
                System.out.println(value.substring(1, value.length() - 1));
            else
                System.out.println(value);
            return value;
        }
    },
    ADD("add", 2) {
        @Override
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal + rVal);
        }
    },
    SUB("sub", 2) {
        @Override
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal - rVal);
        }
    },
    MUL("mul", 2) {
        @Override
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal * rVal);
        }
    },
    DIV("div", 2) {
        @Override
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal / rVal);
        }
    },
    MOD("mod", 2) {
        @Override
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal % rVal);
        }
    },
    ERASE("erase", 1) {
        @Override
        public String apply() {
            String key = Environment.nextParameter();
            String res = null;
            if ((res = Environment.contextNameMap.peek().remove(key)) != null) {
                return res;
            } else if ((res = Environment.contextNameMap.getLast().remove(key)) != null) {
                return res;
            } else
                throw new InvalidName();

        }
    },
    ISNAME("isname", 1) {
        @Override
        public String apply() {
            String key = Environment.nextParameter();
            Boolean res = Environment.contextNameMap.peek().containsKey(key)
                    || Environment.contextNameMap.getLast().containsKey(key);
            return res.toString();
        }
    },
    READLIST("readlist", 0) {
        @Override
        public String apply() {
            MuaList list = new MuaList();
            list.readlist();
            return list.toString();
        }
    },
    RUN("run", 1) {
        @Override
        public String apply() {
            MuaList list = MuaList.parseMuaList(Environment.nextParameter());

            String res = "";
            Environment.allocateNewInputPool();
            Environment.allocateNewCommandStack();
            Environment.contextReturnVal.push("");
            for (String s : list.toArray(new String[1])) {
                Environment.push2CurInputPool(s);
            }
            while (Environment.hasNextInPool()) {
                Environment.run();
            }
            res = Environment.contextReturnVal.pop();
            Environment.removeCurCommandStack();
            Environment.removeCurInputPool();
            return res;
        }
    },
    EQ("eq", 2) {
        @Override
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();
            try {
                double lNumber = Double.parseDouble(lVal);
                double rNumber = Double.parseDouble(rVal);
                return Boolean.toString(lNumber == rNumber);
            } catch (NumberFormatException e) {
                return lVal.compareTo(rVal) == 0 ? TRUESTRING : FALSESTRING;
            }
        }
    },
    GT("gt", 2) {
        @Override
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();

            try {
                double lNumber = Double.parseDouble(lVal);
                double rNumber = Double.parseDouble(rVal);
                return Boolean.toString(lNumber > rNumber);
            } catch (NumberFormatException e) {
                return lVal.compareTo(rVal) > 0 ? TRUESTRING : FALSESTRING;
            }

        }
    },
    LT("lt", 2) {
        @Override
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();

            try {
                double lNumber = Double.parseDouble(lVal);
                double rNumber = Double.parseDouble(rVal);
                return Boolean.toString(lNumber < rNumber);
            } catch (NumberFormatException e) {
                return lVal.compareTo(rVal) < 0 ? TRUESTRING : FALSESTRING;
            }
        }
    },
    AND("and", 2) {
        @Override
        public String apply() {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            boolean rVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(lVal && rVal);
        }
    },
    OR("or", 2) {
        @Override
        public String apply() {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            boolean rVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(lVal || rVal);
        }
    },
    NOT("not", 1) {
        @Override
        public String apply() {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(!lVal);
        }
    },
    ISNUMBER("isnumber", 1) {
        @Override
        public String apply() {
            try {
                Double.parseDouble(Environment.nextParameter());
            } catch (NumberFormatException e) {
                return FALSESTRING;
            }
            return TRUESTRING;
        }
    },
    ISLIST("islist", 1) {
        @Override
        public String apply() {
            try {
                MuaList.parseMuaList(Environment.nextParameter());
            } catch (MuaListFormatException e) {
                return FALSESTRING;
            }
            return TRUESTRING;
        }
    },
    ISBOOL("isbool", 1) {
        @Override
        public String apply() {
            String val = Environment.nextParameter();
            val = val.toLowerCase();
            return val.matches("(true|false)") ? TRUESTRING : FALSESTRING;
        }
    },
    ISEMPTY("isempty", 1) {
        @Override
        public String apply() {
            String val = Environment.nextParameter();
            if (val.compareTo("") == 0 || val.compareTo("[]") == 0)
                return TRUESTRING;
            else
                return FALSESTRING;
        }
    },
    ISWORD("isword", 1) {
        @Override
        public String apply() {
            String val = Environment.nextParameter();
            try {
                Double.parseDouble(val);
                return FALSESTRING;
            } catch (NumberFormatException e) {
                try {
                    MuaList.parseMuaList(val);
                    return FALSESTRING;
                } catch (MuaListFormatException ee) {
                    return TRUESTRING;
                }
            }
        }
    },
    IF("if", 3) {
        @Override
        public String apply() {
            Boolean cond = Boolean.parseBoolean(Environment.nextParameter());
            MuaList trueList = MuaList.parseMuaList(Environment.nextParameter());
            MuaList falseList = MuaList.parseMuaList(Environment.nextParameter());

            MuaList list = cond.booleanValue() ? trueList : falseList;
            if (list.isEmpty()) {
                return "[]";
            }
            if (list.size() == 1 && Command.get(list.get(0)) == null) {
                return list.get(0);
            }

            String res = "";
            Environment.allocateNewInputPool();
            Environment.allocateNewCommandStack();
            Environment.allocateNewParameterQ();
            Environment.contextReturnVal.push("");
            for (String s : list.toArray(new String[1])) {
                Environment.push2CurInputPool(s);
            }
            while (Environment.hasNextInPool()) {
                Environment.run();
            }

            res = Environment.contextReturnVal.peek();
            Environment.removeCurParameterQ();
            Environment.removeCurCommandStack();
            Environment.removeCurInputPool();
            return res;
        }
    },
    FUNC("func", 0) {
        @Override
        public String apply() {
            MuaList funcList = MuaList.parseMuaList(Environment.nextParameter());
            if (funcList.size() != 2)
                throw new InvalidFunction();
            MuaList argList = MuaList.parseMuaList(funcList.get(0));
            MuaList runList = MuaList.parseMuaList(funcList.get(1));

            if (argList.isEmpty()) {
                if (runList.isEmpty()) {
                    return "[]";
                } else if (runList.size() == 1) {
                    return runList.get(0);
                }
            }

            HashMap<String, String> curNameMap = new HashMap<>();
            for (String s : argList) {
                String res = Environment.nextParameter();
                if (s.matches("^[_a-zA-Z]\\w*")) {
                    curNameMap.put(s, res);
                } else
                    throw new InvalidName();
            }

            Environment.contextNameMap.push(curNameMap);
            Environment.allocateNewInputPool();
            Environment.allocateNewCommandStack();
            Environment.allocateNewParameterQ();
            Environment.contextReturnVal.push("");

            // boolean hasReturnValue = false;
            for (String s : runList.toArray(new String[1])) {
                // if (!hasReturnValue && s.matches(".*return.*"))
                // hasReturnValue = true;
                Environment.push2CurInputPool(s);
            }
            while (Environment.hasNextInPool()) {
                Environment.run();
            }

            String res = Environment.contextReturnVal.peek();
            Environment.removeCurParameterQ();
            Environment.removeCurCommandStack();
            Environment.removeCurInputPool();
            Environment.removeCurNameMap();
            // return hasReturnValue ? res : "";
            return res;
        }
    },
    RETURN("return", 1) {
        @Override
        public String apply() {
            String res = Environment.nextParameter();
            Environment.clearCurInputPool();
            return res;
        }
    },
    EXPORT("export", 1) {
        @Override
        public String apply() {
            String key = Environment.nextParameter();
            if (Environment.contextNameMap.peek().get(key) == null)
                throw new InvalidName();

            String val = Environment.contextNameMap.peek().get(key);
            Environment.contextNameMap.getLast().put(key, val);
            return val;
        }
    },
    WORD("word", 2) {
        @Override
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();
            return lVal + rVal;
        }
    },
    SENTENCE("sentence", 2) {
        @Override
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();

            MuaList resList = new MuaList();

            try {
                MuaList lList = MuaList.parseMuaList(lVal);
                for (String s : lList) {
                    resList.add(s);
                }
            } catch (MuaListFormatException mfe) {
                resList.add(lVal);
            }

            try {
                MuaList rList = MuaList.parseMuaList(rVal);
                for (String s : rList) {
                    resList.add(s);
                }
            } catch (MuaListFormatException mfe) {
                resList.add(rVal);
            }

            return resList.toString();
        }
    },
    LIST("list", 2) {
        @Override
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();

            MuaList resList = new MuaList();
            resList.add(lVal);
            resList.add(rVal);

            return resList.toString();
        }
    },
    JOIN("join", 2) {
        @Override
        public String apply() {
            MuaList list = MuaList.parseMuaList(Environment.nextParameter());
            String val = Environment.nextParameter();
            list.add(val);
            return list.toString();
        }
    },
    FIRST("first", 1) {
        @Override
        public String apply() {
            String val = Environment.nextParameter();
            try {
                MuaList list = MuaList.parseMuaList(val);
                return list.get(0);
            } catch (MuaListFormatException e) {
                return val.substring(0, 1);
            }
        }
    },
    LAST("last", 1) {
        @Override
        public String apply() {
            String val = Environment.nextParameter();
            try {
                MuaList list = MuaList.parseMuaList(val);
                return list.get(list.size() - 1);
            } catch (MuaListFormatException e) {
                return val.substring(val.length() - 1, val.length());
            }
        }
    },
    BUTFIRST("butfirst", 1) {
        @Override
        public String apply() {
            String val = Environment.nextParameter();
            try {
                MuaList list = MuaList.parseMuaList(val);
                list.remove(0);
                return list.toString();
            } catch (MuaListFormatException e) {
                return val.substring(1, val.length());
            }
        }
    },
    BUTLAST("butlast", 1) {
        @Override
        public String apply() {
            String val = Environment.nextParameter();
            try {
                MuaList list = MuaList.parseMuaList(val);
                list.remove(list.size() - 1);
                return list.toString();
            } catch (MuaListFormatException e) {
                return val.substring(0, val.length() - 1);
            }
        }
    },
    RANDOM("random", 1) {
        @Override
        public String apply() {
            double val = Double.parseDouble(Environment.nextParameter());
            return "" + Math.random() * val;
        }
    },
    INT("int", 1) {
        @Override
        public String apply() {
            double val = Double.parseDouble(Environment.nextParameter());
            return "" + (int) Math.floor(val);
        }
    },
    SQRT("sqrt", 1) {
        @Override
        public String apply() {
            double val = Double.parseDouble(Environment.nextParameter());
            return "" + Math.sqrt(val);
        }
    },
    SAVE("save", 1) {
        @Override
        public String apply() {
            String file = Environment.nextParameter();
            try (PrintWriter pw = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))), true)) {
                for (Entry<String, String> pair : Environment.contextNameMap.peek().entrySet()) {
                    pw.write("make\n\"" + pair.getKey() + "\n");
                    String val = pair.getValue();
                    try {
                        MuaList.parseMuaList(val);
                        pw.write(val + "\n");
                        continue;
                    } catch (MuaListFormatException mfe) {
                        // Do nothing
                    }

                    try {
                        Double.parseDouble(val);
                        pw.write(val + "\n");
                        continue;
                    } catch (NumberFormatException nfe) {
                        // Do nothing
                    }

                    try {
                        Boolean.parseBoolean(val);
                        pw.write(val + "\n");
                    } catch (NumberFormatException nfe) {
                        pw.write("\"" + val + "\n");
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return file;
        }
    },
    LOAD("load", 1) {
        @Override
        public String apply() {
            String file = Environment.nextParameter();
            try (BufferedReader br = new BufferedReader((new InputStreamReader(new FileInputStream(file))))) {
                while (true) {
                    String line = br.readLine();
                    if (line == null)
                        break;
                    for (String s : line.split(" ")) {
                        Environment.push2CurInputPool(s);
                    }

                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return TRUESTRING;
        }
    },
    ERALL("erall", 0) {
        @Override
        public String apply() {
            Environment.contextNameMap.peek().clear();
            return TRUESTRING;
        }
    },
    POALL("poall", 0) {
        @Override
        public String apply() {
            MuaList nameList = new MuaList();
            for (String s : Environment.contextNameMap.peek().keySet()) {
                nameList.add(s);
            }
            return nameList.toString();
        }
    };

    private final String cmd;
    private final int opNeed;
    private static final String TRUESTRING = "true";
    private static final String FALSESTRING = "false";

    private Command(String cmd, int opNeed) {
        this.cmd = cmd;
        this.opNeed = opNeed;
    }

    public abstract String apply();

    public static Command get(String key) {
        for (Command c : values()) {
            if (c.cmd.compareTo(key) == 0) {
                return c;
            }
        }
        return null;
    }

    public int getOpNeed() {
        return opNeed;
    }
}