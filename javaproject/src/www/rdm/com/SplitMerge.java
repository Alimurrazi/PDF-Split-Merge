package www.rdm.com;

import java.io.File;
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

class SplitMerge {
    private static final Logger LOGGER = Logger.getLogger(SplitMerge.class.getName());
    private static final String TOOLBAR_STYLE = "-fx-background-color: #1976D2;";

    private GridPane pageRangeGrid;
    private int nextGridRow = 3;
    private String inputfile = null;
    private boolean refreshPending = false, headerAdded = false;
    private final List<int[]> pageRanges = new ArrayList<>();
    private boolean beforeend = false;
    private String filename = null;

    private GridPane gridbybutton() {
        if (!headerAdded) {
            Label text = new Label("Contributing Pages");
            text.setFont(new Font("Arial", 25));
            pageRangeGrid.add(text, 0, 1);
            headerAdded = true;
        }

        Button okbutton = new Button("OK");
        okbutton.getStyleClass().add("small-button");
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
        pageRangeGrid.add(hbox1, 0, nextGridRow);

        okbutton.setOnAction(new EventHandler<ActionEvent>() {
            int from, to;
            @Override
            public void handle(ActionEvent e) {
                errorLabel.setText("");
                if (fromField.getText() == null || fromField.getText().isEmpty()
                        || toField.getText() == null || toField.getText().isEmpty()) {
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
            pageRangeGrid.getChildren().clear();
            pageRanges.clear();
            refreshPending = false;
            headerAdded = false;
            nextGridRow = 2;
        }
        return pageRangeGrid;
    }

    Scene show(Stage stage, Scene parentScene) {
        BorderPane border = new BorderPane();
        pageRangeGrid = UiHelper.gridinfo();

        // Toolbar
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle(TOOLBAR_STYLE);

        Button btn1 = new Button("Select PDF");
        Button btn2 = new Button("Add Pages");
        Button btn4 = new Button("Refresh");
        btn1.getStyleClass().add("toolbar-button");
        btn2.getStyleClass().add("toolbar-button");
        btn4.getStyleClass().add("toolbar-button");

        hbox.getChildren().addAll(btn1, btn2, btn4);

        // Bottom bar: Back + Finish + progress + status
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(5, 10, 10, 10));
        bottomBox.setSpacing(15);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        Button btn5 = new Button("Back");
        btn5.getStyleClass().add("secondary-button");

        Button btn3 = new Button("Finish");
        btn3.getStyleClass().add("primary-button");
        btn3.setDisable(true);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(22, 22);
        progress.setVisible(false);

        Label statusLabel = new Label("No file selected");
        statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");

        bottomBox.getChildren().addAll(btn5, btn3, progress, statusLabel);

        border.setTop(hbox);
        border.setCenter(pageRangeGrid);
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
                    inputfile = dropped.getAbsolutePath();
                    filename = dropped.getName();
                    statusLabel.setText("Selected: " + filename);
                    statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                    btn3.setDisable(false);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        btn1.setOnAction(event -> {
            try {
                String selected = UiHelper.filepath();
                if (selected != null) {
                    inputfile = selected;
                    filename = Paths.get(selected).getFileName().toString();
                    statusLabel.setText("Selected: " + filename);
                    statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                    btn3.setDisable(false);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to open PDF file", e);
                UiHelper.badpdfcall(inputfile != null ? Paths.get(inputfile).getFileName() : null);
            }
        });

        btn2.setOnAction(event -> border.setCenter(gridbybutton()));

        btn3.setOnAction(event -> {
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
            File file = UiHelper.savefile();
            if (file == null) return;

            btn3.setDisable(true);
            progress.setVisible(true);
            String currentInput = inputfile;
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    new PdfSplitService().split(currentInput, pageRanges, file);
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
                LOGGER.log(Level.SEVERE, "Failed to split/merge PDF", task.getException());
                UiHelper.badpdfcall(inputfile != null ? Paths.get(inputfile).getFileName() : null);
            });
            new Thread(task).start();
        });

        btn4.setOnAction(event -> {
            refreshPending = true;
            inputfile = null;
            beforeend = false;
            statusLabel.setText("No file selected");
            statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");
            btn3.setDisable(true);
            border.setCenter(gridbybutton());
        });

        btn5.setOnAction(event -> stage.setScene(parentScene));

        Scene scene = new Scene(border, 420, 500);
        scene.getStylesheets().add(Project200.class.getResource("/www/rdm/com/styles.css").toExternalForm());
        return scene;
    }
}
