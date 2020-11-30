package mua;

import java.util.ArrayList;

public class MuaList extends ArrayList<Object> {
    private static final long serialVersionUID = 6778385261416856263L;

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("[");
        for (Object o : super.toArray()) {
            res.append(o.toString() + " ");
        }
        res.replace(res.length() - 1, res.length(), "]");
        return res.toString();
    }

    public String readlist() {
        String line = "";
        while (line.compareTo("") == 0) {
            line = Environment.in.nextLine();
        }
        for (String listElement : line.split("[\\s]")) {
            add(listElement);
        }
        return line;
    }

    public static MuaList parseMuaList(String listContent) {
        MuaList list = new MuaList();
        if (listContent.charAt(0) != '[' && listContent.charAt(listContent.length() - 1) != ']')
            throw new MuaListFormatException();
        listContent = listContent.substring(1, listContent.length() - 1);

        StringBuffer listElement = new StringBuffer();
        int isInList = 0;
        for (String s : listContent.split("[\\s]")) {
            if (isInList != 0) {
                listElement.append(" " + s);
                isInList += Environment.countBracket(s);
                if (isInList == 0)
                    list.add(listElement.toString());
            } else if (s.charAt(0) != '[') {
                list.add(s);
            } else {
                isInList += Environment.countBracket(s);
                listElement = new StringBuffer();
                listElement.append(s);
                if (isInList == 0)
                    list.add(listElement.toString());
            }
        }

        return list;
    }
}

class MuaListFormatException extends RuntimeException {
    private static final long serialVersionUID = 2630149395941128696L;
}