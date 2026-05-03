#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────────
# SLCAS Build & Run Script (Linux / macOS)
# Smart Library Circulation & Automation System — COS 202
# MIVA Open University
# ─────────────────────────────────────────────────────────────────────────────

echo "========================================================"
echo "  SLCAS — Smart Library Circulation & Automation System"
echo "  COS 202 | MIVA Open University"
echo "========================================================"

# Check Java
if ! command -v javac &> /dev/null; then
    echo "ERROR: javac not found. Please install JDK 11+ and add to PATH."
    exit 1
fi

echo ""
echo "Java version: $(java -version 2>&1 | head -1)"
echo ""

# Clean and create bin directory
rm -rf bin
mkdir -p bin

echo "Compiling source files..."

# Compile all Java files
javac -d bin \
    src/model/Borrowable.java \
    src/model/LibraryItem.java \
    src/model/Book.java \
    src/model/Magazine.java \
    src/model/Journal.java \
    src/model/BorrowRecord.java \
    src/model/UserAccount.java \
    src/utils/IDGenerator.java \
    src/utils/FileHandler.java \
    src/controller/SearchEngine.java \
    src/controller/SortEngine.java \
    src/controller/LibraryManager.java \
    src/gui/UITheme.java \
    src/gui/GradientPanel.java \
    src/gui/StyledButton.java \
    src/gui/StatCard.java \
    src/gui/LibraryTable.java \
    src/gui/ViewItemsPanel.java \
    src/gui/BorrowPanel.java \
    src/gui/SearchSortPanel.java \
    src/gui/AdminPanel.java \
    src/gui/ReportsPanel.java \
    src/gui/MainWindow.java \
    src/Main.java

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilation successful!"
    echo ""
    echo "Launching SLCAS..."
    echo ""
    java -cp bin Main
else
    echo ""
    echo "Compilation FAILED. Please check the errors above."
    exit 1
fi
