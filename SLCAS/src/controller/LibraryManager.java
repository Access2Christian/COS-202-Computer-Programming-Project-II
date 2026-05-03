package controller;

import model.*;
import utils.*;
import java.util.*;
import java.time.LocalDate;

public class LibraryManager {
    // ─── Core Data Structures ──────────────────────────────────────────────────
    private ArrayList<LibraryItem> items;          // All library items
    private ArrayList<UserAccount> users;          // All registered users
    private ArrayList<BorrowRecord> allRecords;    // All borrow records

    private Queue<String[]> reservationQueue;      // Waitlist: [itemId, userId, date]
    private Stack<String[]> undoStack;             // Undo stack: [action, serialized data]
    private LibraryItem[] frequentCache;           // Fixed-size cache for top accessed items
    private static final int CACHE_SIZE = 5;

    private static LibraryManager instance;

    private LibraryManager() {
        items = new ArrayList<>();
        users = new ArrayList<>();
        allRecords = new ArrayList<>();
        reservationQueue = new LinkedList<>();
        undoStack = new Stack<>();
        frequentCache = new LibraryItem[CACHE_SIZE];
        loadData();
        if (items.isEmpty()) loadSampleData();
    }

    public static LibraryManager getInstance() {
        if (instance == null) instance = new LibraryManager();
        return instance;
    }

    // ─── Item Management ───────────────────────────────────────────────────────
    public void addItem(LibraryItem item) {
        items.add(item);
        undoStack.push(new String[]{"ADD_ITEM", item.getId()});
        updateCache();
        saveData();
    }

    public boolean removeItem(String itemId) {
        LibraryItem found = SearchEngine.linearSearchById(items, itemId);
        if (found == null) return false;
        items.remove(found);
        undoStack.push(new String[]{"REMOVE_ITEM", found.toCSV()});
        updateCache();
        saveData();
        return true;
    }

    public boolean updateItem(String itemId, LibraryItem updated) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(itemId)) {
                undoStack.push(new String[]{"UPDATE_ITEM", items.get(i).toCSV()});
                items.set(i, updated);
                updateCache();
                saveData();
                return true;
            }
        }
        return false;
    }

    // ─── Undo Last Admin Action ────────────────────────────────────────────────
    public String undoLastAction() {
        if (undoStack.isEmpty()) return "Nothing to undo.";
        String[] action = undoStack.pop();
        switch (action[0]) {
            case "ADD_ITEM":
                items.removeIf(i -> i.getId().equals(action[1]));
                updateCache(); saveData();
                return "Undid ADD: removed item " + action[1];
            case "REMOVE_ITEM":
                LibraryItem restored = parseItemFromCSV(action[1]);
                if (restored != null) { items.add(0, restored); updateCache(); saveData(); }
                return "Undid REMOVE: restored item";
            case "UPDATE_ITEM":
                LibraryItem prev = parseItemFromCSV(action[1]);
                if (prev != null) {
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getId().equals(prev.getId())) {
                            items.set(i, prev); break;
                        }
                    }
                    updateCache(); saveData();
                }
                return "Undid UPDATE: reverted item";
            case "ADD_USER":
                users.removeIf(u -> u.getId().equals(action[1]));
                saveData();
                return "Undid ADD USER: removed user " + action[1];
            default:
                return "Unknown action undone.";
        }
    }

    private LibraryItem parseItemFromCSV(String csv) {
        if (csv.startsWith("Book,")) return Book.fromCSV(csv);
        if (csv.startsWith("Magazine,")) return Magazine.fromCSV(csv);
        if (csv.startsWith("Journal,")) return Journal.fromCSV(csv);
        return null;
    }

    // ─── Reservation Queue ─────────────────────────────────────────────────────
    public void addReservation(String itemId, String userId) {
        reservationQueue.offer(new String[]{itemId, userId, LocalDate.now().toString()});
    }

    public String[] processNextReservation() {
        return reservationQueue.poll();
    }

    public List<String[]> getAllReservations() {
        return new ArrayList<>(reservationQueue);
    }

    public boolean hasReservations(String itemId) {
        for (String[] r : reservationQueue) {
            if (r[0].equals(itemId)) return true;
        }
        return false;
    }

    public int getQueueSize() { return reservationQueue.size(); }

    // ─── Frequent Cache Update ─────────────────────────────────────────────────
    private void updateCache() {
        List<LibraryItem> sorted = SortEngine.sort(items, "Quick Sort", "Access");
        for (int i = 0; i < CACHE_SIZE && i < sorted.size(); i++) {
            frequentCache[i] = sorted.get(i);
        }
    }

    public LibraryItem[] getFrequentCache() { return frequentCache; }

    // ─── User Management ───────────────────────────────────────────────────────
    public void addUser(UserAccount user) {
        users.add(user);
        undoStack.push(new String[]{"ADD_USER", user.getId()});
        saveData();
    }

    public boolean removeUser(String userId) {
        UserAccount found = getUserById(userId);
        if (found == null) return false;
        users.remove(found);
        saveData();
        return true;
    }

    public UserAccount getUserById(String userId) {
        for (UserAccount u : users) {
            if (u.getId().equals(userId)) return u;
        }
        return null;
    }

    public UserAccount getUserByName(String name) {
        for (UserAccount u : users) {
            if (u.getName().toLowerCase().contains(name.toLowerCase())) return u;
        }
        return null;
    }

    // ─── Borrow / Return ───────────────────────────────────────────────────────
    public BorrowRecord borrowItem(String itemId, String userId, int loanDays) {
        LibraryItem item = SearchEngine.linearSearchById(items, itemId);
        UserAccount user = getUserById(userId);
        if (item == null || user == null) return null;
        if (!(item instanceof Borrowable)) return null;
        Borrowable bItem = (Borrowable) item;
        LocalDate dueDate = LocalDate.now().plusDays(loanDays);
        if (bItem.borrow(userId, dueDate)) {
            BorrowRecord record = new BorrowRecord(
                IDGenerator.generateRecordId(), itemId, item.getTitle(),
                item.getItemType(), userId, user.getName(),
                LocalDate.now(), dueDate);
            allRecords.add(record);
            user.addBorrowRecord(record);
            updateCache();
            saveData();
            return record;
        }
        return null;
    }

    public boolean returnItem(String itemId, String userId) {
        LibraryItem item = SearchEngine.linearSearchById(items, itemId);
        if (item == null || !(item instanceof Borrowable)) return false;
        Borrowable bItem = (Borrowable) item;
        if (bItem.returnItem()) {
            // Update the record
            for (BorrowRecord r : allRecords) {
                if (r.getItemId().equals(itemId) && r.getUserId().equals(userId) && !r.isReturned()) {
                    r.setReturned(true);
                    r.setReturnDate(LocalDate.now());
                    break;
                }
            }
            // Process queue for this item
            if (hasReservations(itemId)) {
                String[] next = processNextReservation();
                if (next != null) {
                    // notify next user (in real app, send email)
                }
            }
            saveData();
            return true;
        }
        return false;
    }

    // ─── Statistics ────────────────────────────────────────────────────────────
    public int getTotalItems() { return items.size(); }
    public int getTotalUsers() { return users.size(); }
    public int getAvailableItems() {
        int count = 0;
        for (LibraryItem item : items) {
            if (item instanceof Borrowable && ((Borrowable)item).isAvailable()) count++;
        }
        return count;
    }
    public int getOverdueCount() {
        return (int) allRecords.stream().filter(r -> !r.isReturned() && r.isOverdue()).count();
    }
    public int getActiveLoans() {
        return (int) allRecords.stream().filter(r -> !r.isReturned()).count();
    }

    // Recursive total count by category
    public Map<String, Integer> getCategoryDistribution() {
        Map<String, Integer> dist = new LinkedHashMap<>();
        List<String> categories = new ArrayList<>();
        for (LibraryItem item : items) {
            if (!categories.contains(item.getCategory())) categories.add(item.getCategory());
        }
        for (String cat : categories) {
            dist.put(cat, SearchEngine.recursiveCountByCategory(items, cat, 0));
        }
        return dist;
    }

    public Map<String, Integer> getTypeDistribution() {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("Book", SearchEngine.recursiveCountByType(items, "Book", 0));
        dist.put("Magazine", SearchEngine.recursiveCountByType(items, "Magazine", 0));
        dist.put("Journal", SearchEngine.recursiveCountByType(items, "Journal", 0));
        return dist;
    }

    public List<LibraryItem> getMostBorrowed(int top) {
        List<LibraryItem> sorted = SortEngine.sort(items, "Quick Sort", "Access");
        return sorted.subList(0, Math.min(top, sorted.size()));
    }

    public List<UserAccount> getUsersWithOverdue() {
        List<UserAccount> result = new ArrayList<>();
        for (UserAccount u : users) {
            if (u.getOverdueLoans() > 0) result.add(u);
        }
        return result;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────
    public ArrayList<LibraryItem> getItems() { return items; }
    public ArrayList<UserAccount> getUsers() { return users; }
    public ArrayList<BorrowRecord> getAllRecords() { return allRecords; }
    public Stack<String[]> getUndoStack() { return undoStack; }

    // ─── Persistence ───────────────────────────────────────────────────────────
    public void saveData() {
        FileHandler.saveItems(items);
        FileHandler.saveUsers(users);
        FileHandler.saveRecords(allRecords);
    }

    private void loadData() {
        List<LibraryItem> loadedItems = FileHandler.loadItems();
        List<UserAccount> loadedUsers = FileHandler.loadUsers();
        List<BorrowRecord> loadedRecords = FileHandler.loadRecords();
        items.addAll(loadedItems);
        users.addAll(loadedUsers);
        allRecords.addAll(loadedRecords);
        // Reconnect records to users
        for (BorrowRecord r : allRecords) {
            UserAccount u = getUserById(r.getUserId());
            if (u != null) u.addBorrowRecord(r);
        }
        // Update ID generators
        int maxBook = 1000, maxMag = 2000, maxJrn = 3000, maxUsr = 100;
        for (LibraryItem i : items) {
            try {
                if (i.getId().startsWith("BK")) maxBook = Math.max(maxBook, Integer.parseInt(i.getId().substring(2)) + 1);
                else if (i.getId().startsWith("MG")) maxMag = Math.max(maxMag, Integer.parseInt(i.getId().substring(2)) + 1);
                else if (i.getId().startsWith("JR")) maxJrn = Math.max(maxJrn, Integer.parseInt(i.getId().substring(2)) + 1);
            } catch (Exception ignored) {}
        }
        for (UserAccount u : users) {
            try { if (u.getId().startsWith("USR")) maxUsr = Math.max(maxUsr, Integer.parseInt(u.getId().substring(3)) + 1); }
            catch (Exception ignored) {}
        }
        IDGenerator.setBookCounter(maxBook);
        IDGenerator.setMagCounter(maxMag);
        IDGenerator.setJournalCounter(maxJrn);
        IDGenerator.setUserCounter(maxUsr);
        updateCache();
    }

    // ─── Sample Data ───────────────────────────────────────────────────────────
    private void loadSampleData() {
        // Books
        addItem(new Book("BK1001","Clean Code","Robert C. Martin",2008,"Software Engineering",
            "A handbook of agile software craftsmanship","978-0132350884","Prentice Hall",3,1));
        addItem(new Book("BK1002","Design Patterns","Gang of Four",1994,"Software Engineering",
            "Elements of reusable object-oriented software","978-0201633610","Addison-Wesley",2,1));
        addItem(new Book("BK1003","The Pragmatic Programmer","Andrew Hunt",1999,"Software Engineering",
            "From journeyman to master","978-0135957059","Addison-Wesley",2,2));
        addItem(new Book("BK1004","Introduction to Algorithms","Thomas Cormen",2009,"Computer Science",
            "Comprehensive introduction to modern algorithms","978-0262033848","MIT Press",4,3));
        addItem(new Book("BK1005","Artificial Intelligence: A Modern Approach","Stuart Russell",2020,"Artificial Intelligence",
            "Leading textbook in AI field","978-0134610993","Pearson",2,4));
        addItem(new Book("BK1006","Database System Concepts","Silberschatz",2019,"Database",
            "Classic database textbook","978-0078022159","McGraw-Hill",3,7));
        addItem(new Book("BK1007","Computer Networks","Andrew Tanenbaum",2010,"Networking",
            "Comprehensive networking textbook","978-0132126953","Pearson",2,5));
        addItem(new Book("BK1008","Operating System Concepts","Silberschatz",2018,"Operating Systems",
            "The classic OS textbook","978-1119320913","Wiley",3,10));
        addItem(new Book("BK1009","Structure and Interpretation","Abelson & Sussman",1996,"Computer Science",
            "Landmark programming textbook","978-0262510875","MIT Press",1,2));
        addItem(new Book("BK1010","The Art of Computer Programming","Donald Knuth",2011,"Computer Science",
            "Definitive description of classical algorithms","978-0321751041","Addison-Wesley",1,3));

        // Magazines
        addItem(new Magazine("MG2001","IEEE Spectrum","IEEE Editorial",2024,"Technology",
            "Flagship publication of the IEEE",3,62,"IEEE","Monthly"));
        addItem(new Magazine("MG2002","Nature","Magdalena Skipper",2024,"Science",
            "International journal of science",7998,629,"Springer Nature","Weekly"));
        addItem(new Magazine("MG2003","MIT Technology Review","Mat Honan",2024,"Technology",
            "Reporting on technology and its impact",1,127,"MIT","Bimonthly"));
        addItem(new Magazine("MG2004","Scientific American","Laura Helmuth",2024,"Science",
            "Popular science magazine",4,330,"Springer Nature","Monthly"));

        // Journals
        addItem(new Journal("JR3001","ACM Computing Surveys","Various Authors",2024,"Computer Science",
            "Survey and tutorial articles on computer science",56,1,"10.1145/CS2024","ACM","Computer Science"));
        addItem(new Journal("JR3002","Journal of Machine Learning Research","Lawrence Saul",2024,"AI/ML",
            "Premier journal for ML research",25,1,"10.JMLR2024","MIT","Machine Learning"));
        addItem(new Journal("JR3003","Communications of the ACM","Andrew Chien",2024,"Computer Science",
            "Leading print and online publication for CS professionals",67,1,"10.1145/CACM2024","ACM","Computer Science"));
        addItem(new Journal("JR3004","IEEE Transactions on Software Engineering","Lionel Briand",2024,"Software Engineering",
            "Premier outlet for SE research",50,2,"10.1109/TSE2024","IEEE","Software Engineering"));

        // Users
        addUser(new UserAccount("USR100","Amara Okonkwo","amara@miva.edu","08012345678","Student","Computer Science"));
        addUser(new UserAccount("USR101","Dr. Emeka Nwosu","emeka.nwosu@miva.edu","08023456789","Faculty","Information Technology"));
        addUser(new UserAccount("USR102","Fatima Al-Hassan","fatima@miva.edu","08034567890","Student","Electrical Engineering"));
        addUser(new UserAccount("USR103","Chidi Obi","chidi@miva.edu","08045678901","Staff","Library"));
        addUser(new UserAccount("USR104","Ngozi Adeyemi","ngozi@miva.edu","08056789012","Student","Mathematics"));
        addUser(new UserAccount("USR105","Prof. Bello Musa","bello.musa@miva.edu","08067890123","Faculty","Physics"));

        IDGenerator.setBookCounter(1011);
        IDGenerator.setMagCounter(2005);
        IDGenerator.setJournalCounter(3005);
        IDGenerator.setUserCounter(106);
        saveData();
    }
}
