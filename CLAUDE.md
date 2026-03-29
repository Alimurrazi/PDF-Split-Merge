# PDF Split & Merge

A Java/JavaFX desktop application for splitting, merging, and removing pages from PDF files.

## Project Structure

```
javaproject/
├── src/www/rdm/com/       # Java source files
├── target/                # Maven build output (ignored by git)
├── nbproject/             # NetBeans project config (legacy)
├── build.xml              # Ant build script (legacy)
└── pom.xml                # Maven build config
```

## Key Classes

| Class | Purpose |
|---|---|
| `Project200` | Main JavaFX application, entry point |
| `SplitOptions` | Split method chooser (by page range or by chapter) |
| `SplitMerge` | Split by page range scene |
| `Chapter` | Split by chapter name scene |
| `Mergepdfs` | Merge multiple PDFs into one |
| `Removepage` | Remove specific pages from a PDF |
| `UiHelper` | Shared UI utilities (file picker, save dialog, grid) |
| `Openfile` | Open resulting PDF after processing |
| `Badpdf` | Error dialog for corrupted/invalid PDFs |
| `ChapterNotFoundDialog` | Shown when chapter heading is not found in PDF |
| `ChapterHeadingStrategy` | Custom iText text extractor for font-size-aware chapter detection |
| `TextExtractionState` | State holder for chapter text extraction |

## Tech Stack

- **Language:** Java 21
- **UI:** JavaFX 21 (via `org.openjfx` Maven dependency)
- **PDF Library:** iText 5.5.13.4 (`com.itextpdf:itextpdf`)
- **Build:** Maven (`pom.xml`)
- **Package:** `www.rdm.com`

## Running Locally

### Prerequisites
- JDK 21 (e.g., Eclipse Temurin 21)
- Maven 3.9.x
- VS Code with Extension Pack for Java

### Run the app
```bash
cd javaproject
mvn javafx:run
```

### Compile only
```bash
mvn compile
```

### Build fat JAR
```bash
mvn package
# Output: target/pdf-split-merge-1.0-SNAPSHOT.jar
```

## Dependency Notes

- iText 5.5.13.4 is managed via Maven (no local JAR needed)
- iText 7+ requires significant API changes — stay on iText 5.x
- Legacy Ant/NetBeans files (`build.xml`, `nbproject/`) are kept but not used
