package controller;

import model.LibraryItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortEngine {

    // ─── Comparators ───────────────────────────────────────────────────────────
    public static Comparator<LibraryItem> byTitle() {
        return Comparator.comparing(i -> i.getTitle().toLowerCase());
    }

    public static Comparator<LibraryItem> byAuthor() {
        return Comparator.comparing(i -> i.getAuthor().toLowerCase());
    }

    public static Comparator<LibraryItem> byYear() {
        return Comparator.comparingInt(LibraryItem::getYear);
    }

    public static Comparator<LibraryItem> byAccessCount() {
        return (a, b) -> b.getAccessCount() - a.getAccessCount();
    }

    // ─── Selection Sort ────────────────────────────────────────────────────────
    public static List<LibraryItem> selectionSort(List<LibraryItem> items, Comparator<LibraryItem> comp) {
        List<LibraryItem> list = new ArrayList<>(items);
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (comp.compare(list.get(j), list.get(minIdx)) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                LibraryItem temp = list.get(i);
                list.set(i, list.get(minIdx));
                list.set(minIdx, temp);
            }
        }
        return list;
    }

    // ─── Insertion Sort ────────────────────────────────────────────────────────
    public static List<LibraryItem> insertionSort(List<LibraryItem> items, Comparator<LibraryItem> comp) {
        List<LibraryItem> list = new ArrayList<>(items);
        int n = list.size();
        for (int i = 1; i < n; i++) {
            LibraryItem key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comp.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        return list;
    }

    // ─── Merge Sort ────────────────────────────────────────────────────────────
    public static List<LibraryItem> mergeSort(List<LibraryItem> items, Comparator<LibraryItem> comp) {
        if (items.size() <= 1) return new ArrayList<>(items);
        int mid = items.size() / 2;
        List<LibraryItem> left = mergeSort(items.subList(0, mid), comp);
        List<LibraryItem> right = mergeSort(items.subList(mid, items.size()), comp);
        return merge(left, right, comp);
    }

    private static List<LibraryItem> merge(List<LibraryItem> left, List<LibraryItem> right, Comparator<LibraryItem> comp) {
        List<LibraryItem> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (comp.compare(left.get(i), right.get(j)) <= 0) result.add(left.get(i++));
            else result.add(right.get(j++));
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    // ─── Quick Sort ────────────────────────────────────────────────────────────
    public static List<LibraryItem> quickSort(List<LibraryItem> items, Comparator<LibraryItem> comp) {
        List<LibraryItem> list = new ArrayList<>(items);
        quickSortHelper(list, 0, list.size() - 1, comp);
        return list;
    }

    private static void quickSortHelper(List<LibraryItem> list, int low, int high, Comparator<LibraryItem> comp) {
        if (low < high) {
            int pi = partition(list, low, high, comp);
            quickSortHelper(list, low, pi - 1, comp);
            quickSortHelper(list, pi + 1, high, comp);
        }
    }

    private static int partition(List<LibraryItem> list, int low, int high, Comparator<LibraryItem> comp) {
        LibraryItem pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comp.compare(list.get(j), pivot) <= 0) {
                i++;
                LibraryItem temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        LibraryItem temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
    }

    // ─── Unified Sort Entry ────────────────────────────────────────────────────
    public static List<LibraryItem> sort(List<LibraryItem> items, String algorithm, String field) {
        Comparator<LibraryItem> comp;
        switch (field) {
            case "Author": comp = byAuthor(); break;
            case "Year":   comp = byYear(); break;
            case "Access": comp = byAccessCount(); break;
            default:       comp = byTitle();
        }
        switch (algorithm) {
            case "Selection Sort": return selectionSort(items, comp);
            case "Insertion Sort": return insertionSort(items, comp);
            case "Quick Sort":     return quickSort(items, comp);
            default:               return mergeSort(items, comp);
        }
    }
}
