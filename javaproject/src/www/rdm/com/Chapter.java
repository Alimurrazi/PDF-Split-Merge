package www.rdm.com;

import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

class Chapter {
    private static final Logger LOGGER = Logger.getLogger(Chapter.class.getName());

    BorderPane border = new BorderPane();
    Project200 project = new Project200();
    GridPane contentGrid = new GridPane();
    boolean refreshPending = false;
    TextField chapterNameField;
    String filename = null;
    String chapterName = null;
    ProgressIndicator progress;
    Button startButton;
    Label statusLabel;

    void splitbychapter() {
        if (progress != null) progress.setVisible(true);
        if (startButton != null) startButton.setDisable(true);

        // Ask for save location on FX thread before going to background
        File saveFile = project.savefile();
        if (saveFile == null) {
            if (progress != null) progress.setVisible(false);
            if (startButton != null) startButton.setDisable(false);
            return;
        }

        String currentFilename = filename;
        String currentChapterName = chapterName;

        Task<int[]> task = new Task<>() {
            @Override
            protected int[] call() throws Exception {
                return new PdfChapterSplitService().findChapter(currentFilename, currentChapterName);
            }
        };

        task.setOnSucceeded(ev -> {
            if (progress != null) progress.setVisible(false);
            if (startButton != null) startButton.setDisable(false);

            int[] pages = task.getValue();
            if (pages == null) {
                Stage prstage = new Stage();
                Yourchoice yourchoice = new Yourchoice(currentChapterName);
                try {
                    yourchoice.start(prstage);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Failed to show chapter-not-found dialog", ex);
                }
            } else {
                try {
                    new PdfChapterSplitService().extractPages(currentFilename, pages[0], pages[1], saveFile);
                    new Openfile().openm(saveFile);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Failed to extract chapter pages", ex);
                    project.badpdfcall();
                }
            }

            filename = null;
            refreshPending = true;
            border.setLeft(gridbybutton());
        });

        task.setOnFailed(ev -> {
            if (progress != null) progress.setVisible(false);
            if (startButton != null) startButton.setDisable(false);
            LOGGER.log(Level.SEVERE, "Failed to split PDF by chapter", task.getException());
            project.badpdfcall();
        });

        new Thread(task).start();
    }

    GridPane gridbybutton() {
        Button btn = new Button("Start");
        startButton = btn;
        chapterNameField = new TextField();
        chapterNameField.setPrefWidth(100);
        chapterNameField.setPromptText("Chapter's Name");
        contentGrid.add(chapterNameField, 0, 0);
        contentGrid.add(btn, 0, 1);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (chapterNameField.getText() != null && !chapterNameField.getText().isEmpty()) {
                    chapterName = chapterNameField.getText();
                    splitbychapter();
                }
            }
        });

        if (refreshPending) {
            refreshPending = false;
            contentGrid.getChildren().clear();
        }
        return contentGrid;
    }

    Scene chapterstring(Stage stage, Scene parentScene) throws NoClassDefFoundError {
        contentGrid = project.gridinfo();

        // Toolbar
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: #1976D2;");

        Button btn1 = new Button("Select PDF");
        Button btn2 = new Button("Refresh");

        progress = new ProgressIndicator();
        progress.setPrefSize(22, 22);
        progress.setVisible(false);

        hbox.getChildren().addAll(btn1, btn2, progress);
        border.setTop(hbox);

        // Bottom bar: Back + status
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(5, 10, 10, 10));
        bottomBox.setSpacing(15);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        Button back = new Button("Back");
        back.setStyle("-fx-font: 14 arial;");

        statusLabel = new Label("No file selected");
        statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");

        bottomBox.getChildren().addAll(back, statusLabel);
        border.setBottom(bottomBox);

        // Drag & drop: drop a PDF to select it as input
        border.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        border.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                File dropped = db.getFiles().stream()
                        .filter(f -> f.getName().toLowerCase().endsWith(".pdf"))
                        .findFirst().orElse(null);
                if (dropped != null) {
                    try {
                        PdfReader reader = new PdfReader(dropped.getAbsolutePath());
                        reader.close();
                        filename = dropped.getAbsolutePath();
                        project.pdfpath = Paths.get(filename).getFileName();
                        statusLabel.setText("Selected: " + dropped.getName());
                        statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                        border.setLeft(gridbybutton());
                        success = true;
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Dropped file is not a valid PDF", e);
                        project.badpdfcall();
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                filename = null;
                try {
                    filename = project.filepath();
                    if (filename == null) return;
                    Path pathp = Paths.get(filename);
                    project.pdfpath = pathp.getFileName();
                    PdfReader pdfreader = new PdfReader(filename);
                    try {
                        statusLabel.setText("Selected: " + pathp.getFileName());
                        statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                        border.setLeft(gridbybutton());
                    } finally {
                        pdfreader.close();
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to open PDF file", e);
                    project.badpdfcall();
                }
            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                filename = null;
                refreshPending = true;
                statusLabel.setText("No file selected");
                statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");
                border.setLeft(gridbybutton());
            }
        });

        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                stage.setScene(parentScene);
            }
        });

        return new Scene(border, 420, 500);
    }
}
