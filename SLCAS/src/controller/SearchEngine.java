package controller;

import model.LibraryItem;
import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

    // ─── Linear Search ─────────────────────────────────────────────────────────
    public static List<LibraryItem> linearSearchByTitle(List<LibraryItem> items, String query) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (LibraryItem item : items) {
            if (item.getTitle().toLowerCase().contains(q)) {
                results.add(item);
            }
        }
        return results;
    }

    public static List<LibraryItem> linearSearchByAuthor(List<LibraryItem> items, String query) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (LibraryItem item : items) {
            if (item.getAuthor().toLowerCase().contains(q)) {
                results.add(item);
            }
        }
        return results;
    }

    public static List<LibraryItem> linearSearchByType(List<LibraryItem> items, String type) {
        List<LibraryItem> results = new ArrayList<>();
        for (LibraryItem item : items) {
            if (item.getItemType().equalsIgnoreCase(type)) {
                results.add(item);
            }
        }
        return results;
    }

    public static List<LibraryItem> linearSearchByCategory(List<LibraryItem> items, String category) {
        List<LibraryItem> results = new ArrayList<>();
        String q = category.toLowerCase().trim();
        for (LibraryItem item : items) {
            if (item.getCategory().toLowerCase().contains(q)) {
                results.add(item);
            }
        }
        return results;
    }

    public static LibraryItem linearSearchById(List<LibraryItem> items, String id) {
        for (LibraryItem item : items) {
            if (item.getId().equalsIgnoreCase(id)) return item;
        }
        return null;
    }

    // ─── Binary Search (list must be sorted by title) ──────────────────────────
    public static LibraryItem binarySearchByTitle(List<LibraryItem> sortedItems, String title) {
        int low = 0, high = sortedItems.size() - 1;
        String target = title.toLowerCase().trim();
        while (low <= high) {
            int mid = (low + high) / 2;
            String midTitle = sortedItems.get(mid).getTitle().toLowerCase();
            int cmp = midTitle.compareTo(target);
            if (cmp == 0) return sortedItems.get(mid);
            else if (midTitle.contains(target)) return sortedItems.get(mid);
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    public static List<LibraryItem> binarySearchByTitleAll(List<LibraryItem> sortedItems, String title) {
        List<LibraryItem> results = new ArrayList<>();
        String target = title.toLowerCase().trim();
        // Find any match first
        int idx = binarySearchIndex(sortedItems, target);
        if (idx < 0) return results;
        // Expand around found index
        int left = idx;
        while (left > 0 && sortedItems.get(left - 1).getTitle().toLowerCase().contains(target)) left--;
        int right = idx;
        while (right < sortedItems.size() - 1 && sortedItems.get(right + 1).getTitle().toLowerCase().contains(target)) right++;
        for (int i = left; i <= right; i++) results.add(sortedItems.get(i));
        return results;
    }

    private static int binarySearchIndex(List<LibraryItem> items, String target) {
        int low = 0, high = items.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            String midTitle = items.get(mid).getTitle().toLowerCase();
            if (midTitle.contains(target)) return mid;
            else if (midTitle.compareTo(target) < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    // ─── Recursive Search ──────────────────────────────────────────────────────
    public static List<LibraryItem> recursiveSearchByTitle(List<LibraryItem> items, String query, int index, List<LibraryItem> results) {
        if (index >= items.size()) return results;
        if (items.get(index).getTitle().toLowerCase().contains(query.toLowerCase())) {
            results.add(items.get(index));
        }
        return recursiveSearchByTitle(items, query, index + 1, results);
    }

    public static List<LibraryItem> recursiveSearchByAuthor(List<LibraryItem> items, String query, int index, List<LibraryItem> results) {
        if (index >= items.size()) return results;
        if (items.get(index).getAuthor().toLowerCase().contains(query.toLowerCase())) {
            results.add(items.get(index));
        }
        return recursiveSearchByAuthor(items, query, index + 1, results);
    }

    // Recursive count by category
    public static int recursiveCountByCategory(List<LibraryItem> items, String category, int index) {
        if (index >= items.size()) return 0;
        int match = items.get(index).getCategory().equalsIgnoreCase(category) ? 1 : 0;
        return match + recursiveCountByCategory(items, category, index + 1);
    }

    // Recursive count by type
    public static int recursiveCountByType(List<LibraryItem> items, String type, int index) {
        if (index >= items.size()) return 0;
        int match = items.get(index).getItemType().equalsIgnoreCase(type) ? 1 : 0;
        return match + recursiveCountByType(items, type, index + 1);
    }

    // General multi-field search
    public static List<LibraryItem> search(List<LibraryItem> items, String query, String field, String algorithm) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>(items);
        switch (algorithm) {
            case "Binary":
                if ("Title".equals(field)) return binarySearchByTitleAll(items, query);
                // Binary only works well for title; fall through to linear for others
            case "Recursive":
                List<LibraryItem> recResults = new ArrayList<>();
                if ("Title".equals(field)) return recursiveSearchByTitle(items, query, 0, recResults);
                if ("Author".equals(field)) return recursiveSearchByAuthor(items, query, 0, recResults);
                return linearSearchByTitle(items, query);
            default: // Linear
                if ("Title".equals(field)) return linearSearchByTitle(items, query);
                if ("Author".equals(field)) return linearSearchByAuthor(items, query);
                if ("Type".equals(field)) return linearSearchByType(items, query);
                if ("Category".equals(field)) return linearSearchByCategory(items, query);
                return linearSearchByTitle(items, query);
        }
    }
}
