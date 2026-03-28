package www.rdm.com;

import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;

class Removepage {
    private static final Logger LOGGER = Logger.getLogger(Removepage.class.getName());

    private final Project200 project = new Project200();
    private GridPane grid = new GridPane();
    private final List<int[]> pageRanges = new ArrayList<>();
    private boolean beforeend = false;
    private String inputfile = null;
    private int nextGridRow = 3;
    private boolean refreshPending = false, headerAdded = false;

    private GridPane gridbybutton() {
        if (!headerAdded) {
            Label text = new Label("Pages to Remove");
            text.setFont(new Font("Arial", 25));
            grid.add(text, 0, 1);
            headerAdded = true;
        }

        Button okbutton = new Button("OK");
        okbutton.setStyle("-fx-font: 12 arial;");
        TextField fromField = new TextField();
        TextField toField = new TextField();
        fromField.setPrefWidth(100);
        toField.setPrefWidth(100);
        fromField.setPromptText("From");
        toField.setPromptText("To");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        HBox hbox1 = new HBox();
        hbox1.setSpacing(10);
        hbox1.getChildren().addAll(fromField, toField, okbutton, errorLabel);
        grid.add(hbox1, 0, nextGridRow);

        okbutton.setOnAction(new EventHandler<ActionEvent>() {
            int from, to;
            @Override
            public void handle(ActionEvent e) {
                errorLabel.setText("");
                if (fromField.getText() == null || fromField.getText().isEmpty() || toField.getText() == null || toField.getText().isEmpty()) {
                    errorLabel.setText("Both fields are required");
                    return;
                }
                try {
                    from = Integer.parseInt(fromField.getText().trim());
                    to = Integer.parseInt(toField.getText().trim());
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Pages must be numbers");
                    return;
                }
                if (from < 1 || to < 1) {
                    errorLabel.setText("Page numbers must be positive");
                    return;
                }
                if (from > to) {
                    errorLabel.setText("'From' must be <= 'To'");
                    return;
                }
                pageRanges.add(new int[]{from, to});
                beforeend = true;
            }
        });
        nextGridRow = nextGridRow + 1;

        if (refreshPending) {
            grid.getChildren().clear();
            pageRanges.clear();
            refreshPending = false;
            headerAdded = false;
            nextGridRow = 2;
        }
        return grid;
    }

    Scene remove(Stage stage, Scene homeScene) {
        BorderPane border = new BorderPane();
        grid = project.gridinfo();

        // Toolbar
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: #1976D2;");

        Button btn1 = new Button("Select PDF");
        Button btn2 = new Button("Add Pages");
        Button btn4 = new Button("Refresh");

        hbox.getChildren().addAll(btn1, btn2, btn4);

        // Bottom bar: Back + Finish + progress + status
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(5, 10, 10, 10));
        bottomBox.setSpacing(15);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        Button btn5 = new Button("Back");
        btn5.setStyle("-fx-font: 14 arial;");

        Button btn3 = new Button("Finish");
        btn3.setStyle("-fx-font: 14 arial;");
        btn3.setDisable(true);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(22, 22);
        progress.setVisible(false);

        Label statusLabel = new Label("No file selected");
        statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");

        bottomBox.getChildren().addAll(btn5, btn3, progress, statusLabel);

        border.setTop(hbox);
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
                        inputfile = dropped.getAbsolutePath();
                        project.pdfpath = Paths.get(inputfile).getFileName();
                        statusLabel.setText("Selected: " + dropped.getName());
                        statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                        btn3.setDisable(false);
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
            @Override
            public void handle(ActionEvent event) {
                try {
                    inputfile = project.filepath();
                    if (inputfile == null) return;
                    Path path = Paths.get(inputfile);
                    project.pdfpath = path.getFileName();
                    PdfReader pdfreader = new PdfReader(inputfile);
                    try {
                        statusLabel.setText("Selected: " + path.getFileName());
                        statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                        btn3.setDisable(false);
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
            @Override
            public void handle(ActionEvent event) {
                border.setLeft(gridbybutton());
            }
        });

        btn3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (inputfile == null) {
                    statusLabel.setText("Please select a PDF first");
                    statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                    return;
                }
                if (!beforeend) {
                    statusLabel.setText("Please add at least one page range");
                    statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                    return;
                }
                File file = project.savefile();
                if (file == null) return;

                btn3.setDisable(true);
                progress.setVisible(true);
                String currentInput = inputfile;
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        new PdfPageRemoveService().removePages(currentInput, pageRanges, file);
                        return null;
                    }
                };
                task.setOnSucceeded(ev -> {
                    progress.setVisible(false);
                    btn3.setDisable(false);
                    new Openfile().openm(file);
                });
                task.setOnFailed(ev -> {
                    progress.setVisible(false);
                    btn3.setDisable(false);
                    LOGGER.log(Level.SEVERE, "Failed to remove pages from PDF", task.getException());
                    project.badpdfcall();
                });
                new Thread(task).start();
            }
        });

        btn4.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                inputfile = null;
                beforeend = false;
                refreshPending = true;
                statusLabel.setText("No file selected");
                statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");
                btn3.setDisable(true);
                border.setLeft(gridbybutton());
            }
        });

        btn5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(homeScene);
            }
        });

        return new Scene(border, 420, 500);
    }
}
