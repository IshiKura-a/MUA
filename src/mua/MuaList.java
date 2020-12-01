package mua;

import java.util.ArrayList;

public class MuaList extends ArrayList<String> {
    private static final long serialVersionUID = 6778385261416856263L;

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("[");
        for (String s : super.toArray(new String[1])) {
            res.append(s + " ");
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

        StringBuilder listElement = new StringBuilder();
        int isInList = 0;
        for (String s : listContent.split("[\\s]")) {
            if (s.matches("[\\s]*"))
                continue;
            if (isInList != 0) {
                listElement.append(" ");
                isInList = countBracket(list, s, listElement, isInList);
            } else if (s.charAt(0) != '[') {
                list.add(s);
            } else {
                listElement.delete(0, listElement.length());
                isInList = countBracket(list, s, listElement, isInList);
            }
        }

        return list;
    }

    private static int countBracket(MuaList list, String s, StringBuilder listElement, int isInList) {
        for (char c : s.toCharArray()) {
            if (c == '[') {
                isInList++;
            } else if (c == ']') {
                isInList--;
            }
            listElement.append(Character.toString(c));
            if (isInList == 0) {
                list.add(listElement.toString());
                listElement.delete(0, listElement.length());
            }
        }
        return isInList;
    }
}

class MuaListFormatException extends RuntimeException {
    private static final long serialVersionUID = 2630149395941128696L;
}