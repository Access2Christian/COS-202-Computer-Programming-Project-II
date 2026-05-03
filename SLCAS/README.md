# SLCAS — Smart Library Circulation & Automation System
### COS 202 | MIVA Open University

---

## Overview

A fully featured Java Swing desktop application that manages library books, magazines, and journals. Features a beautiful dark-themed GUI with dashboards, search, sort, borrowing workflow, reservation queues, overdue tracking, and visual reports.

---

## How to Run

### Prerequisites
- **JDK 11 or higher** installed and on your PATH
- (JRE alone won't work — you need JDK for compilation)

### Linux / macOS
```bash
chmod +x build_and_run.sh
./build_and_run.sh
```

### Windows
```cmd
build_and_run.bat
```

> Data is saved automatically to the `data/` folder (created on first run).

---

## Project Structure

```
SLCAS/
├── src/
│   ├── Main.java                     ← Entry point
│   ├── model/
│   │   ├── Borrowable.java           ← Interface
│   │   ├── LibraryItem.java          ← Abstract base class
│   │   ├── Book.java                 ← Extends LibraryItem, implements Borrowable
│   │   ├── Magazine.java             ← Extends LibraryItem, implements Borrowable
│   │   ├── Journal.java              ← Extends LibraryItem, implements Borrowable
│   │   ├── UserAccount.java          ← User model with borrow history
│   │   └── BorrowRecord.java         ← Individual borrow transaction
│   ├── controller/
│   │   ├── LibraryManager.java       ← Central data manager (Singleton)
│   │   ├── SearchEngine.java         ← Linear, Binary, Recursive search
│   │   └── SortEngine.java           ← Selection, Insertion, Merge, Quick sort
│   ├── gui/
│   │   ├── UITheme.java              ← Dark theme constants & helpers
│   │   ├── GradientPanel.java        ← Custom gradient-painted panel
│   │   ├── StyledButton.java         ← Animated rounded button
│   │   ├── StatCard.java             ← Dashboard metric card
│   │   ├── LibraryTable.java         ← Custom table with type-colored rows
│   │   ├── MainWindow.java           ← Main JFrame with all panels
│   │   ├── ViewItemsPanel.java       ← Catalogue tab
│   │   ├── BorrowPanel.java          ← Borrow/Return tab
│   │   ├── SearchSortPanel.java      ← Search & Sort tab
│   │   ├── AdminPanel.java           ← Admin tab
│   │   └── ReportsPanel.java         ← Reports tab with charts
│   └── utils/
│       ├── IDGenerator.java          ← Auto-incrementing IDs
│       └── FileHandler.java          ← CSV persistence
├── build_and_run.sh                  ← Linux/macOS build script
├── build_and_run.bat                 ← Windows build script
└── data/                             ← Auto-generated on first run
    ├── items.csv
    ├── users.csv
    └── records.csv
```

---

## Feature Checklist

### OOP (Requirement 1 & 2)
- [x] Abstract class `LibraryItem` with common fields
- [x] Subclasses: `Book`, `Magazine`, `Journal`
- [x] Interface `Borrowable` (borrow, returnItem, isAvailable, isOverdue)
- [x] Polymorphism: `LibraryManager` processes any `LibraryItem`
- [x] Encapsulation: all fields private, exposed via getters/setters
- [x] Package organization: `model`, `controller`, `gui`, `utils`

### Data Structures (Requirement 3)
| Structure | Usage |
|-----------|-------|
| `ArrayList<LibraryItem>` | All library items |
| `ArrayList<UserAccount>` | All registered users |
| `ArrayList<BorrowRecord>` | All borrow records |
| `Queue<String[]>` (LinkedList) | Reservation / waitlist queue |
| `Stack<String[]>` | Undo last admin action |
| `LibraryItem[]` (Array, size 5) | Fixed-size frequent-access cache |

### Search Algorithms (Requirement 4)
| Algorithm | Where Used |
|-----------|------------|
| Linear Search | Default for all fields; `linearSearchByTitle/Author/Type/Category/Id` |
| Binary Search | Title search when list is sorted; `binarySearchByTitleAll` |
| Recursive Search | Recursive title & author search; recursive count by category/type |

### Sorting Algorithms (Requirement 5)
| Algorithm | Complexity |
|-----------|------------|
| Selection Sort | O(n²) |
| Insertion Sort | O(n²) |
| Merge Sort | O(n log n) — stable |
| Quick Sort | O(n log n) avg |

All four available via GUI dropdown in **Search & Sort** tab.

### Recursion (Requirement 6)
- `recursiveSearchByTitle` / `recursiveSearchByAuthor` — catalogue search
- `recursiveCountByCategory` / `recursiveCountByType` — category stats
- `computeFineRecursive` — overdue fine with escalating rates
- `mergeSort` — recursive divide-and-conquer

### Event-Driven GUI (Requirement 7)
- Button click handlers for all actions
- ActionListener on search text fields (Enter to search)
- ListSelectionListener on tables (auto-fill return form)
- Timer for live clock (1-second updates)
- Timer for overdue reminder (30-second intervals)

### GUI Components (Requirement 8)
- [x] Main JFrame with 5 tabbed panels
- [x] Tables (`JTable`) with custom cell renderers
- [x] Buttons: Add, Borrow, Return, Sort, Search, Delete, Undo
- [x] Text fields with placeholder tooltips
- [x] Combo boxes for algorithm, filter, loan period selection
- [x] Labels with styled typography
- [x] Dialog boxes (JOptionPane, JFileChooser)
- [x] Status bar at bottom
- [x] Layouts: BorderLayout, FlowLayout, GridLayout, GridBagLayout

### Advanced GUI (Requirement 9)
- [x] Custom cell renderers (LibraryTable colored by type & status)
- [x] File chooser dialog (export report)
- [x] Timer-driven overdue notifications (every 30s)
- [x] Input validation with dialog popups
- [x] Keyboard shortcuts / mnemonics (Alt+1–5, Alt+S, Alt+O)
- [x] Tooltips on all buttons and tabs
- [x] Custom painted components (StyledButton with hover animation, StatCard with gradient glow, gradient header)

### Mandatory Features (Requirement 10)
- [x] Add new items (Books, Magazines, Journals)
- [x] Borrow and return items
- [x] Reservation queue
- [x] Search by title, author, type, category
- [x] Sort with selectable algorithms and fields
- [x] Undo last admin action (Stack)
- [x] GUI dashboard with stat cards
- [x] Data saved & loaded (CSV files)
- [x] Reports: most borrowed, overdue users, category distribution

---

## Pre-loaded Sample Data

### Books (10)
- Clean Code, Design Patterns, The Pragmatic Programmer, Intro to Algorithms, AI: A Modern Approach, Database System Concepts, Computer Networks, Operating System Concepts, SICP, The Art of Computer Programming

### Magazines (4)
- IEEE Spectrum, Nature, MIT Technology Review, Scientific American

### Journals (4)
- ACM Computing Surveys, JMLR, CACM, IEEE TSE

### Users (6)
- 3 Students, 2 Faculty, 1 Staff

---

## Sample Workflow

1. **View items**: Click "Catalogue" tab — all items shown
2. **Borrow a book**: Go to "Borrow/Return" → Enter `BK1001`, `USR100`, 14 days → Click Borrow
3. **Search**: Go to "Search & Sort" → Type "algorithm" → Select "Recursive Search" → Click Search
4. **Sort**: Select "Merge Sort" → "Year" → Click Sort
5. **Add item**: Go to "Admin" → "Add Book" tab → Fill form → Click Add Book
6. **Undo**: Admin tab header → "Undo Last Action"
7. **Return**: "Borrow/Return" → Click active loan row → Return Item
8. **Reports**: "Reports" tab → View charts → Export Report

---

## Technical Notes

- **No external libraries** — pure Java SE (Swing, java.time, java.io)
- **JDK 11+** required (uses `var`, `LocalDate`, lambdas)
- Data persists between sessions via CSV in `data/` directory
- Singleton pattern used for `LibraryManager`
- Fine calculation: ₦50/day (days 1–3), ₦75/day (days 4–7), ₦100/day (day 8+)
