package gui.utils;

import javax.swing.*;

public class Dialogs {
    public static String input(String msg, String title) {
        return JOptionPane.showInputDialog(null, msg, title, JOptionPane.QUESTION_MESSAGE);
    }
    public static int inputInt(String msg, String title) {
        while (true) {
            String s = input(msg, title);
            if (s == null) return -1;
            try { return Integer.parseInt(s.trim()); }
            catch (NumberFormatException e) { JOptionPane.showMessageDialog(null, "Enter a valid integer."); }
        }
    }
    public static double inputDouble(String msg, String title) {
        while (true) {
            String s = input(msg, title);
            if (s == null) return -1;
            try { return Double.parseDouble(s.trim()); }
            catch (NumberFormatException e) { JOptionPane.showMessageDialog(null, "Enter a valid number."); }
        }
    }
    public static void info(String msg) { JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
    public static void error(String msg) { JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}