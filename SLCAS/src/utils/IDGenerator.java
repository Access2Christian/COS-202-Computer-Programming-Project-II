package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger bookCounter = new AtomicInteger(1000);
    private static final AtomicInteger magCounter = new AtomicInteger(2000);
    private static final AtomicInteger journalCounter = new AtomicInteger(3000);
    private static final AtomicInteger userCounter = new AtomicInteger(100);
    private static final AtomicInteger recordCounter = new AtomicInteger(1);

    public static String generateBookId() {
        return "BK" + bookCounter.getAndIncrement();
    }

    public static String generateMagazineId() {
        return "MG" + magCounter.getAndIncrement();
    }

    public static String generateJournalId() {
        return "JR" + journalCounter.getAndIncrement();
    }

    public static String generateUserId() {
        return "USR" + userCounter.getAndIncrement();
    }

    public static String generateRecordId() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "REC" + ts + recordCounter.getAndIncrement();
    }

    public static void setBookCounter(int v) { bookCounter.set(v); }
    public static void setMagCounter(int v) { magCounter.set(v); }
    public static void setJournalCounter(int v) { journalCounter.set(v); }
    public static void setUserCounter(int v) { userCounter.set(v); }
    public static void setRecordCounter(int v) { recordCounter.set(v); }

    public static int getNextBookNum() { return bookCounter.get(); }
    public static int getNextMagNum() { return magCounter.get(); }
    public static int getNextJournalNum() { return journalCounter.get(); }
    public static int getNextUserNum() { return userCounter.get(); }
}
