package mua;

public enum Command {
    MAKE ("make") {
        public String apply() throws InvalidName, InvalidCommand {
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
        public String apply() throws InvalidCommand, InvalidName {
            return Environment.nameMap.get(Environment.nextParameter()).toString();
        }
    },
    READ ("read") {
        public String apply() {
            return Environment.in.next();
        }
    },
    PRINT ("print") {
        public String apply() throws InvalidCommand, InvalidName {
            String value = Environment.nextParameter();
            System.out.println(value);
            return value;
        }
    },
    ADD ("add") {
        public String apply() throws InvalidCommand, InvalidName {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal + rVal);
        }
    },
    SUB ("sub") {
        public String apply() throws InvalidCommand, InvalidName {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal - rVal);
        }
    },
    MUL ("mul") {
        public String apply() throws InvalidCommand, InvalidName {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal * rVal);
        }
    },
    DIV ("div") {
        public String apply() throws InvalidCommand, InvalidName {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal / rVal);
        }
    },
    MOD ("mod") {
        public String apply() throws InvalidCommand, InvalidName {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Double.toString(lVal % rVal);
        }
    },
    ERASE ("erase") {
        public String apply() throws InvalidCommand, InvalidName {
            return Environment.nameMap.remove(Environment.nextParameter()).toString();
        }
    },
    ISNAME ("isname") {
        public String apply() throws InvalidCommand, InvalidName {
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
    REPEAT ("repeat") {
        public String apply() throws InvalidCommand, InvalidName {
            int noRepeat = Integer.parseInt(Environment.nextParameter());
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
                res = Environment.cmdQueue.pop();
            }
            return res.substring(1);
        }
    },
    EQ ("eq") {
        public String apply() throws InvalidCommand, InvalidName {
            Boolean res = Environment.nextParameter().compareTo(Environment.nextParameter()) == 0;
            return res.toString();
        }
    },
    GT ("gt") {
        public String apply() throws InvalidCommand, InvalidName {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Boolean.toString(lVal > rVal);
        }
    },
    LT ("lt") {
        public String apply() throws InvalidCommand, InvalidName {
            double lVal = Double.parseDouble(Environment.nextParameter());
            double rVal = Double.parseDouble(Environment.nextParameter());
            return Boolean.toString(lVal < rVal);
        }
    },
    AND ("and") {
        public String apply() throws InvalidCommand, InvalidName {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            boolean rVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(lVal && rVal);
        }
    },
    OR ("or") {
        public String apply() throws InvalidCommand, InvalidName  {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            boolean rVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(lVal || rVal);
        }
    },
    NOT ("not") {
        public String apply() throws InvalidCommand, InvalidName {
            boolean lVal = Boolean.parseBoolean(Environment.nextParameter());
            return Boolean.toString(!lVal);
        }
    };

    private final String cmd;

    private Command(String cmd) {
        this.cmd = cmd;
    }

    public abstract String apply() throws InvalidName, InvalidCommand;

    public static Command get(String key) {
        for (Command c : values()) {
            if (c.cmd.compareTo(key) == 0) {
                return c;
            }
        }
        return null;
    }
}