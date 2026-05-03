***

# SLCAS | Smart Library Circulation & Automation System

**SLCAS** is a high-performance Java desktop application built for the **COS 202 (Computer Programming II)** course at **MIVA Open University**. It serves as a comprehensive demonstration of Advanced Object-Oriented Programming, Core Data Structures, and efficient Search/Sort Algorithms.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white) 
![Swing](https://img.shields.io/badge/UI-Swing-blue?style=for-the-badge)

---

## 🚀 Project Overview
The system simulates a university library environment, managing diverse items (Books, Magazines, Journals) and user accounts. It features a modern dark-themed GUI, real-time analytics, and automated workflows for borrowing and returning items.

### Key Highlights:
- **Pure Java SE:** Built entirely without external libraries or frameworks.
- **Advanced GUI:** Custom-painted components, animated buttons, and live dashboard charts using Graphics2D.
- **Persistence:** CSV-based data storage for items, users, and transaction records.

---

## 🛠️ Technical Implementation

### 1. Data Structures
| Structure | Java Implementation | Purpose |
| :--- | :--- | :--- |
| **ArrayList** | `ArrayList<T>` | Primary storage for items, users, and records (O(1) access). |
| **Queue** | `LinkedList<T>` | FIFO waitlist for item reservations. |
| **Stack** | `Stack<T>` | LIFO undo mechanism for administrative actions. |
| **Fixed Array** | `LibraryItem[5]` | High-speed cache for the most frequently accessed items. |

### 2. Algorithms
The system allows users to toggle between different algorithms via the UI to observe performance:
*   **Sorting:** Merge Sort (Recursive), Quick Sort, Selection Sort, and Insertion Sort.
*   **Searching:** Linear Search, Binary Search (on sorted data), and Recursive Search.
*   **Recursion:** Used for category counting, title searching, and an escalating overdue fine computation engine.

---

## 🏗️ System Architecture
The project follows a modular **MVC-inspired** architecture:

- **`model/`**: Defines the data hierarchy (Abstract `LibraryItem`, `Borrowable` interface).
- **`controller/`**: Contains the `LibraryManager` (Singleton) and the Search/Sort engines.
- **`gui/`**: Custom Swing components and the main dashboard panels.
- **`utils/`**: Helper classes for ID generation and CSV File I/O.

---

## 📋 Features
- [x] **Catalogue Management:** Add/Remove Books, Magazines, and Journals.
- [x] **Borrowing System:** Automated due-date tracking and availability checks.
- [x] **Reservation Queue:** Fair FIFO handling for high-demand items.
- [x] **Admin Undo:** Stack-based recovery for accidental deletions or edits.
- [x] **Reporting:** Visual Donut/Bar charts for category distribution and "Top 5 Most Borrowed" lists.
- [x] **Export:** Generate and save text-based reports to your local machine.

---

## 🚦 Getting Started

### Prerequisites
*   JDK 11 or higher.
*   Git (for cloning the repository).

### Installation & Execution
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Access2Christian/COS-202-Computer-Programming-Project-II.git
   cd SLCAS
   ```
2. **Compile the project:**
   ```bash
   javac -d bin src/**/*.java
   ```
3. **Run the application:**
   ```bash
   java -cp bin src/Main.java
   ```
   *(Note: Alternatively, use the provided `build_and_run.bat` or `.sh` scripts.)*

---

## 👥 Group Members (Team SLCAS)
*   **Christian Nnaji** (2024/B/SENG/0246) - Software Engineering
*   **Samuel Nkanor** (2024/B/SENG/0200) - Software Engineering
*   **Ogundipe Temitayo Ayomide** (2024/B/CYB/0345) - Cybersecurity
*   **Abolanle Onabanjo** (2024/B/SENG/0833) - Software Engineering
*   **Joshua Ogiriosa** (2024/B/SENG/0351) - Software Engineering

---

## 📄 License
This project is developed as part of an academic curriculum at MIVA Open University.

---
**Submission Date:** July 3rd, 2026.

***

### How to add this to your GitHub:
1. Create a new file in your local `SLCAS` folder named `README.md`.
2. Paste the content above into that file and save it.
3. Run the following commands in your terminal:
   ```powershell
   git add README.md
   git commit -m "Add project documentation"
   git push origin main
   ```
