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
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class Mergepdfs {
    private static final Logger LOGGER = Logger.getLogger(Mergepdfs.class.getName());

    BorderPane border = new BorderPane();
    List<String> filelist = new ArrayList<>();
    int fileListRow;
    Project200 project = new Project200();
    boolean refreshPending = false;
    GridPane fileListGrid = new GridPane();
    Label emptyStateLabel;

    GridPane gridbybutton(Text t) {
        t.setFont(new Font(20));
        fileListGrid.add(t, 0, fileListRow);
        fileListRow++;
        if (refreshPending) {
            refreshPending = false;
            fileListGrid.getChildren().clear();
        }
        return fileListGrid;
    }

    Scene merge(Stage stage, Scene homeScene) {
        fileListGrid = project.gridinfo();

        emptyStateLabel = new Label("No files added yet.\nClick 'Select PDF' or drag PDF files here.");
        emptyStateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #BBBBBB; -fx-font-style: italic;");
        emptyStateLabel.setWrapText(true);
        fileListGrid.add(emptyStateLabel, 0, 0);

        // Toolbar
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: #1976D2;");

        Button btn1 = new Button("Select PDF");
        Button btn3 = new Button("Refresh");
        btn1.getStyleClass().add("toolbar-button");
        btn3.getStyleClass().add("toolbar-button");

        hbox.getChildren().addAll(btn1, btn3);
        border.setTop(hbox);
        border.setCenter(fileListGrid);

        fileListRow = 1;

        // Bottom bar: Back + Finish + progress + status
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(5, 10, 10, 10));
        bottomBox.setSpacing(15);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        Button back = new Button("Back");
        back.getStyleClass().add("secondary-button");

        Button btn2 = new Button("Finish");
        btn2.getStyleClass().add("primary-button");
        btn2.setDisable(true);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(22, 22);
        progress.setVisible(false);

        Label statusLabel = new Label("No files added");
        statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");

        bottomBox.getChildren().addAll(back, btn2, progress, statusLabel);
        border.setBottom(bottomBox);

        // Drag & drop: each dropped PDF is added to the merge list
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
                for (File droppedFile : db.getFiles()) {
                    if (droppedFile.getName().toLowerCase().endsWith(".pdf")) {
                        String path = droppedFile.getAbsolutePath();
                        try {
                            PdfReader reader = new PdfReader(path);
                            reader.close();
                            filelist.add(path);
                            Text t = new Text(filelist.size() + ".  " + droppedFile.getName());
                            border.setCenter(gridbybutton(t));
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Dropped file is not a valid PDF: " + path, e);
                        }
                        success = true;
                    }
                }
                fileListGrid.getChildren().remove(emptyStateLabel);
                int count = filelist.size();
                statusLabel.setText(count + " file" + (count == 1 ? "" : "s") + " added");
                statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                btn2.setDisable(false);
            }
            event.setDropCompleted(success);
            event.consume();
        });

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Project200 p = new Project200();
                try {
                    String filename = p.filepath();
                    if (filename == null) return;
                    Path pathp = Paths.get(filename);
                    project.pdfpath = pathp.getFileName();
                    PdfReader pdfreader = new PdfReader(filename);
                    try {
                        filelist.add(filename);
                        Text t = new Text();
                        Path path = Paths.get(filename);
                        t.setText(filelist.size() + ".  " + path.getFileName());
                        border.setCenter(gridbybutton(t));
                        fileListGrid.getChildren().remove(emptyStateLabel);
                        int count = filelist.size();
                        statusLabel.setText(count + " file" + (count == 1 ? "" : "s") + " added");
                        statusLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
                        btn2.setDisable(false);
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
                if (filelist.isEmpty()) {
                    statusLabel.setText("Please add at least one PDF first");
                    statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                    return;
                }
                File file = project.savefile();
                if (file == null) return;

                btn2.setDisable(true);
                progress.setVisible(true);
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        new PdfMergeService().merge(filelist, file);
                        return null;
                    }
                };
                task.setOnSucceeded(ev -> {
                    progress.setVisible(false);
                    btn2.setDisable(false);
                    new Openfile().openm(file);
                });
                task.setOnFailed(ev -> {
                    progress.setVisible(false);
                    btn2.setDisable(false);
                    LOGGER.log(Level.SEVERE, "Failed to merge PDFs", task.getException());
                    project.badpdfcall();
                });
                new Thread(task).start();
            }
        });

        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                filelist.clear();
                fileListRow = 1;
                Text t1 = new Text();
                t1.setText(null);
                refreshPending = true;
                border.setCenter(gridbybutton(t1));
                fileListGrid.add(emptyStateLabel, 0, 0);
                statusLabel.setText("No files added");
                statusLabel.setStyle("-fx-text-fill: #777777; -fx-font-style: italic;");
                btn2.setDisable(true);
            }
        });

        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                stage.setScene(homeScene);
            }
        });

        Scene scene = new Scene(border, 420, 500);
        scene.getStylesheets().add(Project200.class.getResource("/www/rdm/com/styles.css").toExternalForm());
        return scene;
    }
}
