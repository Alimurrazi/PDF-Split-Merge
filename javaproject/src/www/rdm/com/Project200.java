package www.rdm.com;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Project200 extends Application {
    private static final Logger LOGGER = Logger.getLogger(Project200.class.getName());
    private static final String TOOLBAR_STYLE = "-fx-background-color: #1976D2;";

    public Path pdfpath = null;
    BorderPane border = new BorderPane();
    GridPane grid, pageRangeGrid;
    int nextGridRow = 3;
    String inputfile = null;
    boolean refreshPending = false, headerAdded = false;
    List<int[]> pageRanges = new ArrayList<>();
    boolean beforeend = false;
    String filename = null;

    GridPane gridinfo() {
        GridPane gri = new GridPane();
        gri.setAlignment(Pos.TOP_LEFT);
        gri.setHgap(10);
        gri.setVgap(10);
        gri.setPadding(new Insets(25, 25, 25, 25));
        return gri;
    }

    String filepath() {
        Stage mystage = null;
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files", "*.pdf");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(mystage);
        inputfile = null;
        if (file == null) return null;
        inputfile = file.getAbsolutePath();
        filename = file.getName();
        return inputfile;
    }

    File savefile() {
        Stage mystage = null;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF", ".pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(mystage);
        return file;
    }

    void badpdfcall() {
        if (pdfpath != null) {
            Stage prstage = new Stage();
            Badpdf badpdf = new Badpdf(pdfpath);
            try {
                badpdf.start(prstage);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to show bad PDF dialog", e);
            }
        }
    }

    GridPane gridbybutton() {
        if (!headerAdded) {
            Label text = new Label("Contributing Pages");
            text.setFont(new Font("Arial", 25));
            pageRangeGrid.add(text, 0, 1);
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
        pageRangeGrid.add(hbox1, 0, nextGridRow);

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
            pageRangeGrid.getChildren().clear();
            pageRanges.clear();
            refreshPending = false;
            headerAdded = false;
            nextGridRow = 2;
        }
        return pageRangeGrid;
    }

    private VBox featureCard(String title, String description) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #DDDDDD;"
                + " -fx-border-radius: 4; -fx-background-radius: 4;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        descLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, descLabel);
        return card;
    }

    Scene fbtn1(Stage stage, Scene parentScene) {
        BorderPane border = new BorderPane();
        pageRangeGrid = gridinfo();

        // Toolbar
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle(TOOLBAR_STYLE);

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
                    pdfpath = Paths.get(inputfile).getFileName();
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
                String selected = filepath();
                if (selected != null) {
                    pdfpath = Paths.get(selected).getFileName();
                    statusLabel.setText("Selected: " + filename);
                    statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                    btn3.setDisable(false);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to open PDF file", e);
                badpdfcall();
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
            Path path = Paths.get(filename);
            pdfpath = path.getFileName();
            File file = savefile();
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
                badpdfcall();
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

        return new Scene(border, 420, 500);
    }

    public void start(Stage primaryStage) {
        grid = gridinfo();

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        Button btn1 = new Button("Split and Merge");
        Button btn2 = new Button("Merge PDFs");
        Button btn3 = new Button("Remove Pages");

        hbox.getChildren().addAll(btn1, btn2, btn3);
        hbox.setStyle(TOOLBAR_STYLE);

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        Button exit = new Button("Exit");
        exit.setStyle("-fx-font: 14 arial;");
        hbox1.getChildren().add(exit);

        border.setTop(hbox);
        border.setBottom(hbox1);

        // Center: app description + feature cards
        VBox centerBox = new VBox(12);
        centerBox.setPadding(new Insets(20, 20, 10, 20));
        centerBox.setAlignment(Pos.TOP_CENTER);

        Label appTitle = new Label("PDF Split & Merge");
        appTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label subtitle = new Label("Choose an operation from the toolbar above");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

        VBox card1 = featureCard("Split & Merge",
                "Extract specific page ranges from a PDF into a new file.\nSupports multiple ranges in one pass.");
        VBox card2 = featureCard("Merge PDFs",
                "Combine multiple PDF files into a single document in the order you choose.");
        VBox card3 = featureCard("Remove Pages",
                "Remove specific page ranges from a PDF and save the result.");

        Label hint = new Label("Tip: Drag & drop PDF files directly onto any screen");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #999999; -fx-font-style: italic;");
        hint.setWrapText(true);

        centerBox.getChildren().addAll(appTitle, subtitle, card1, card2, card3, hint);
        border.setCenter(centerBox);

        Scene homeScene = new Scene(border, 420, 500);

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                StringNumber stringnumber = new StringNumber();
                Scene scenesn = stringnumber.selectbox(primaryStage, homeScene);
                primaryStage.setScene(scenesn);
            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Mergepdfs mergepdfs = new Mergepdfs();
                Scene scene2 = mergepdfs.merge(primaryStage, homeScene);
                primaryStage.setScene(scene2);
            }
        });

        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Removepage removepage = new Removepage();
                Scene scene3 = removepage.remove(primaryStage, homeScene);
                primaryStage.setScene(scene3);
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Platform.exit();
            }
        });

        primaryStage.setTitle("PDF Split & Merge");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
