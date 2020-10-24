package mua;

public enum Command {
    MAKE ("make") {
        public String apply() {
            String key = Environment.nextParameter();
            String value = Environment.nextParameter();
            if (key.matches("^[a-zA-Z]\\w*")) {
                Environment.nameMap.put(key, value);
                return value;
            } else {
                throw new InvalidName();
            }
        }
    },
    THING ("thing") {
        public String apply() {
            return Environment.nameMap.get(Environment.nextParameter()).toString();
        }
    },
    READ ("read") {
        public String apply() {
            return Environment.in.next();
        }
    },
    PRINT ("print") {
        public String apply() {
            String value = Environment.nextParameter();
            System.out.println(value);
            return value;
        }
    },
    ADD ("add") {
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal + rVal);
        }
    },
    SUB ("sub") {
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal - rVal);
        }
    },
    MUL ("mul") {
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal * rVal);
        }
    },
    DIV ("div") {
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal / rVal);
        }
    },
    MOD ("mod") {
        public String apply() {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal % rVal);
        }
    },
    ERASE ("erase") {
        public String apply() {
            return Environment.nameMap.remove(Environment.nextParameter()).toString();
        }
    },
    ISNAME ("isname") {
        public String apply() {
            Boolean res = Environment.nameMap.containsKey(Environment.nextParameter());
            return res.toString();
        }
    },
    READLIST ("readlist") {
        public String apply() {
            MuaList list = new MuaList();
            list.readlist();
            return list.toString();
        }
    },
    RUN ("run") {
        public String apply() {
            int noRepeat = 1;
            MuaList list = MuaList.parseMuaList(Environment.nextParameter());

            int i;
            String res = "";
            for(i = 1; i <= noRepeat; i++) {
                for(Object o: list.toArray()) {
                    Environment.append(o.toString());
                }
                while(Environment.hasNext()) {
                    Environment.run();
                }
                res = Environment.paramQueue.pop();
            }
            return res.substring(1);
        }
    },
    EQ ("eq") {
        public String apply() {
            Boolean res = Environment.nextParameter().compareTo(Environment.nextParameter()) == 0;
            return res.toString();
        }
    },
    GT ("gt") {
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();

            try {
                double lNumber = Double.parseDouble(lVal);
                double rNumber = Double.parseDouble(rVal);
                return Boolean.toString(lNumber > rNumber);
            }
            catch(NumberFormatException e) {
                return lVal.compareTo(rVal) > 0 ? TRUESTRING:FALSESTRING;
            }
            
        }
    },
    LT ("lt") {
        public String apply() {
            String lVal = Environment.nextParameter();
            String rVal = Environment.nextParameter();

            try {
                double lNumber = Double.parseDouble(lVal);
                double rNumber = Double.parseDouble(rVal);
                return Boolean.toString(lNumber < rNumber);
            }
            catch(NumberFormatException e) {
                return lVal.compareTo(rVal) < 0 ? TRUESTRING:FALSESTRING;
            }
        }
    },
    AND ("and") {
        public String apply() {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            boolean rVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(lVal && rVal);
        }
    },
    OR ("or") {
        public String apply() {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            boolean rVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(lVal || rVal);
        }
    },
    NOT ("not") {
        public String apply() {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(!lVal);
        }
    },
    ISNUMBER ("isnumber") {
        public String apply() {
            try {
                Double.parseDouble(Environment.nextParameter());
            }
            catch(NumberFormatException e) {
                return FALSESTRING;
            }
            return TRUESTRING;
        }
    },
    ISLIST ("islist") {
        public String apply() {
            try {
                MuaList.parseMuaList(Environment.nextParameter());
            }
            catch(MuaListFormatException e) {
                return FALSESTRING;
            }
            return TRUESTRING;
        }
    },
    ISBOOL ("isbool") {
        public String apply() {
            String val = Environment.nextParameter();
            val = val.toLowerCase();
            return val.matches("(true|false)")?TRUESTRING:FALSESTRING;
        }
    },
    ISEMPTY ("isempty") {
        public String apply() {
            String val = Environment.nextParameter();
            if(val.compareTo("") == 0 || val.compareTo("[]") == 0) return TRUESTRING;
            else
                return FALSESTRING;
        }
    },
    ISWORD("isword") {
        public String apply() {
            String val = Environment.nextParameter();
            try {
                Double.parseDouble(val);
                return FALSESTRING;
            }
            catch(NumberFormatException e) {
                try {
                    MuaList.parseMuaList(val);
                    return FALSESTRING;
                }
                catch(MuaListFormatException ee) {
                    return TRUESTRING;
                }
            }
        }
    },
    IF("if") {
        public String apply() {
            Boolean cond = Boolean.parseBoolean(Environment.nextParameter());
            MuaList trueList = MuaList.parseMuaList(Environment.nextParameter());
            MuaList falseList = MuaList.parseMuaList(Environment.nextParameter());

            MuaList list = cond.booleanValue() ? trueList : falseList;
            if (list.isEmpty()) {
                return "[]";
            }
            if (list.size() == 1 && Command.get(list.get(0).toString()) == null) {
                return list.get(0).toString();
            }
            String res = "";
            for (Object o : list.toArray()) {
                Environment.append(o.toString());
            }
            while (Environment.hasNext()) {
                Environment.run();
            }
            res = Environment.paramQueue.pop();
            return res.substring(1);
        }
    };

    private final String cmd;
    private static final String TRUESTRING = "true";
    private static final String FALSESTRING = "false";
    private Command(String cmd) {
        this.cmd = cmd;
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
}