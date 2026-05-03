package utils;

import model.*;
import java.io.*;
import java.util.*;

public class FileHandler {
    private static final String DATA_DIR = "data";
    private static final String ITEMS_FILE = DATA_DIR + "/items.csv";
    private static final String USERS_FILE = DATA_DIR + "/users.csv";
    private static final String RECORDS_FILE = DATA_DIR + "/records.csv";

    public static void ensureDataDir() {
        new File(DATA_DIR).mkdirs();
    }

    // ─── Save Items ────────────────────────────────────────────────────────────
    public static void saveItems(List<LibraryItem> items) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            for (LibraryItem item : items) {
                pw.println(item.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
        }
    }

    // ─── Load Items ────────────────────────────────────────────────────────────
    public static List<LibraryItem> loadItems() {
        List<LibraryItem> items = new ArrayList<>();
        File f = new File(ITEMS_FILE);
        if (!f.exists()) return items;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                LibraryItem item = null;
                if (line.startsWith("Book,")) item = Book.fromCSV(line);
                else if (line.startsWith("Magazine,")) item = Magazine.fromCSV(line);
                else if (line.startsWith("Journal,")) item = Journal.fromCSV(line);
                if (item != null) items.add(item);
            }
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
        return items;
    }

    // ─── Save Users ────────────────────────────────────────────────────────────
    public static void saveUsers(List<UserAccount> users) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (UserAccount user : users) {
                pw.println(user.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // ─── Load Users ────────────────────────────────────────────────────────────
    public static List<UserAccount> loadUsers() {
        List<UserAccount> users = new ArrayList<>();
        File f = new File(USERS_FILE);
        if (!f.exists()) return users;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                UserAccount u = UserAccount.fromCSV(line);
                if (u != null) users.add(u);
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    // ─── Save Records ──────────────────────────────────────────────────────────
    public static void saveRecords(List<BorrowRecord> records) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(RECORDS_FILE))) {
            for (BorrowRecord r : records) {
                pw.println(r.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error saving records: " + e.getMessage());
        }
    }

    // ─── Load Records ──────────────────────────────────────────────────────────
    public static List<BorrowRecord> loadRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        File f = new File(RECORDS_FILE);
        if (!f.exists()) return records;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                BorrowRecord r = BorrowRecord.fromCSV(line);
                if (r != null) records.add(r);
            }
        } catch (IOException e) {
            System.err.println("Error loading records: " + e.getMessage());
        }
        return records;
    }

    // ─── Export Report ─────────────────────────────────────────────────────────
    public static void exportReport(String filename, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.print(content);
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }
}
