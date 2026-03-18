# PDF Split & Merge

A Java/JavaFX desktop application for splitting, merging, and removing pages from PDF files.

## Project Structure

```
javaproject/
├── src/www/rdm/com/       # Java source files
├── build/classes/         # Compiled .class files
├── dist/
│   ├── Project200.jar     # Pre-built JAR
│   └── lib/
│       └── itextpdf-5.5.9.jar  # PDF library dependency
├── nbproject/             # NetBeans project config
└── build.xml              # Ant build script
```

## Key Classes

| Class | Purpose |
|---|---|
| `Project200` | Main JavaFX application, entry point |
| `Mergepdfs` | Merge multiple PDFs into one |
| `Removepage` | Remove specific pages from a PDF |
| `Chapter` / `StringNumber` | Split PDF by page ranges |
| `Openfile` | Open resulting PDF after processing |
| `Badpdf` | Error dialog for corrupted/invalid PDFs |

## Tech Stack

- **Language:** Java 8
- **UI:** JavaFX (bundled with JDK 8)
- **PDF Library:** iText 5.5.9 (`com.itextpdf:itextpdf`)
- **Build:** Apache Ant (NetBeans project)
- **Package:** `www.rdm.com`

## Running Locally

### Prerequisites
- Java 8 (JDK 8 with bundled JavaFX) — e.g., [Liberica JDK 8](https://bell-sw.com/pages/downloads/)

### Option 1: Run pre-built JAR
```bash
java -jar javaproject/dist/Project200.jar
```

### Option 2: Open in NetBeans
1. Open NetBeans → File → Open Project → select `javaproject/`
2. Fix library path: update `file.reference.itextpdf-5.5.9.jar` in `nbproject/project.properties` to point to `dist/lib/itextpdf-5.5.9.jar`
3. Press F6 to run

### Option 3: Ant build
```bash
cd javaproject
ant run
```

## Dependency Notes

- The iText JAR path is hardcoded to `G:\itextpdf-5.5.9.jar` in `nbproject/project.properties`
- A copy of the JAR exists at `javaproject/dist/lib/itextpdf-5.5.9.jar`
- To update iText: replace the JAR with `itextpdf-5.5.13.4.jar` (latest iText 5, drop-in compatible)
- iText 7+ requires significant API changes
