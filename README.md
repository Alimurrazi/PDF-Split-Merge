# PDF Split & Merge

A Java/JavaFX desktop application for splitting, merging, and removing pages from PDF files. Built with iText 5 for PDF processing and JavaFX 21 for the UI.

<img width="517" height="653" alt="image" src="https://github.com/user-attachments/assets/50653bb5-1a4b-43fa-a1c3-25b85a353812" />


---

## Features

### Core Operations

| Feature | Description |
|---|---|
| **Split by Page Range** | Extract specific page ranges from a PDF into a new file |
| **Split by Chapter Name** | Automatically detect chapter headings by font size and extract a chapter as a PDF |
| **Merge PDFs** | Combine multiple PDF files into a single document in the order you choose |
| **Remove Pages** | Remove specific page ranges from a PDF and save the result |

### UX

- Drag and drop PDF files directly onto the application
- Progress indicator (spinner) during processing — UI stays responsive
- Buttons disabled during operations to prevent concurrent conflicts
- Open result file immediately after processing
- Back navigation between screens
- Validation feedback on page range inputs
- Status labels change color: gray (idle) → blue (file selected) → error messages in red

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| UI Framework | JavaFX 21 |
| PDF Library | iText 5.5.13.4 |
| Build | Maven 3.9.x |
| Package | `www.rdm.com` |

---

## Project Structure

```
PDF-Split-Merge/
├── javaproject/
│   ├── src/www/rdm/com/         # Java source files
│   │   ├── Project200.java      # Main app + home screen
│   │   ├── Mergepdfs.java       # Merge scene
│   │   ├── Removepage.java      # Remove pages scene
│   │   ├── StringNumber.java    # Split method chooser
│   │   ├── Chapter.java         # Split by chapter name scene
│   │   ├── Openfile.java        # "Open result?" dialog
│   │   ├── Yourchoice.java      # Chapter not found dialog
│   │   ├── Badpdf.java          # Corrupt/invalid PDF dialog
│   │   ├── PdfSplitService.java          # Split logic
│   │   ├── PdfMergeService.java          # Merge logic
│   │   ├── PdfPageRemoveService.java     # Remove pages logic
│   │   ├── PdfChapterSplitService.java   # Chapter detection + split logic
│   │   ├── SemTextExtractionStrategy.java # Custom iText text extractor
│   │   └── TextExtractionState.java      # State holder for text extraction
│   └── pom.xml                  # Maven configuration
└── CLAUDE.md                    # Developer notes
```

---

## Architecture & Design Patterns

### Scene Navigation
Navigation is handled by swapping JavaFX `Scene` objects on a single `Stage`. Each screen is built as a static method that returns a configured `Scene`.

### Service Layer
PDF operations are extracted into dedicated service classes, keeping UI code free of library-specific logic.

```
UI Scene  →  Service Class           →  iText 5
---------     ----------------------    --------
Mergepdfs  →  PdfMergeService        →  PdfReader / PdfCopy
Removepage →  PdfPageRemoveService   →  PdfReader / PdfCopy
Project200 →  PdfSplitService        →  PdfReader / PdfCopy
Chapter    →  PdfChapterSplitService →  PdfReader + SemTextExtractionStrategy
```

### Background Threading
All PDF operations run on a `javafx.concurrent.Task` thread. This keeps the UI thread free so the progress spinner remains active during long operations.

### Strategy Pattern (Chapter Detection)
`SemTextExtractionStrategy` implements iText's `TextExtractionStrategy` interface. It tracks font sizes alongside extracted text to identify chapter headings — the largest font on a page is treated as a heading. This feeds `PdfChapterSplitService` which finds the start and end pages for a given chapter name.

---

## How to Use

### Prerequisites

- JDK 21 (e.g., [Eclipse Temurin 21](https://adoptium.net/))
- Maven 3.9.x
- (Optional) VS Code with Extension Pack for Java

### Step 1 — Build

```bash
cd javaproject
mvn package
# Output: target/pdf-split-merge-1.0-SNAPSHOT.jar
```

This compiles the source and bundles all dependencies (iText, JavaFX) into a single JAR.

### Step 2 — Run

```bash
java -jar target/pdf-split-merge-1.0-SNAPSHOT.jar
```

### Run without building (development shortcut)

```bash
cd javaproject
mvn javafx:run
```

Compiles and launches in one step — useful during development, no JAR produced.

---

### Walkthrough

#### Split a PDF by Page Range

1. Click **Split & Merge** on the home screen.
2. Choose **By Page Number**.
3. Click **Select PDF** (or drag a PDF onto the window).
4. Enter a page range (e.g., `1` to `5`) and click **Add Pages**. Repeat for additional ranges.
5. Click **Finish**, choose a save location, and wait for processing to complete.
6. Optionally open the result when prompted.

#### Split a PDF by Chapter Name

1. Click **Split & Merge** on the home screen.
2. Choose **By Chapter Name**.
3. Drag or select your PDF.
4. Type the chapter heading exactly as it appears in the document (case-insensitive).
5. Click **Finish** — the app scans the PDF for the heading using font-size analysis and extracts the matching chapter.

#### Merge PDFs

1. Click **Merge PDFs** on the home screen.
2. Click **Add PDF** or drag multiple PDFs onto the window. Files are merged in the order listed.
3. Click **Finish**, choose a save location.

#### Remove Pages

1. Click **Remove Pages** on the home screen.
2. Select or drag a PDF.
3. Enter the page range to remove (e.g., `3` to `6`) and click **Add Pages**. Repeat as needed.
4. Click **Finish** and choose a save location.

---

## Error Handling

| Scenario | Behavior |
|---|---|
| Corrupted or password-protected PDF | `Badpdf` dialog shown with a warning message |
| Chapter heading not found | `Yourchoice` dialog shown; user can retry |
| Invalid page range (from > to, or 0) | Inline validation error shown below the input |
| Malformed individual pages | Skipped with a warning log; rest of file processed |

---

## Dependency Notes

- **iText 5.5.13.4** — managed via Maven (`com.itextpdf:itextpdf`). No local JAR needed.
- **Do not upgrade to iText 7+** — it has a completely different API that would require significant refactoring.
