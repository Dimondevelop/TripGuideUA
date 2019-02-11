package ua.tripguide.tripguideua.Utils;

import java.util.ArrayList;

public class UtilMethods {


    public static String listToFormatString(ArrayList<String> strings) { //Додає всі елементи ArrayList в рядок, розділяючи їх комою та пробілом. Вкінці ставить крапку. Перща літера велика, всі остальні маленькі.
        StringBuilder string = new StringBuilder();
        for (String s : strings) {
            string.append(s).append(", ");
        }
        string.setLength(string.length() - 2);
        string.append(".");
        String str = String.valueOf(string);
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static boolean containsArray(String s, ArrayList<String> c) { //Перевіряє чи є в рядку хоч один з рядкових елементів ArrayList, якщо так - повертає true, якщо ні - false.
        for (String ss : c) {
            if (s.contains(ss)) return true;
        }
        return false;
    }




}
