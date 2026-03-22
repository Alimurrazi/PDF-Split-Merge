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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Project200 extends Application {
    private static final Logger LOGGER = Logger.getLogger(Project200.class.getName());

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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PdF files", "*.pdf");
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

    void spliteandmerge(String inputfile) {
        Path path = Paths.get(filename);
        pdfpath = path.getFileName();
        File file = savefile();
        if (file == null) return;
        try {
            new PdfSplitService().split(inputfile, pageRanges, file);
            Openfile openfile = new Openfile();
            openfile.openm(file);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to split/merge PDF", ex);
            badpdfcall();
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
        okbutton.setStyle("-fx-font: 12 arial; -fx-base: #b6e7c9;");
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

    Scene fbtn1(Stage stage, Scene parentScene) {
        BorderPane border = new BorderPane();
        pageRangeGrid = gridinfo();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #008000;");

        Button btn1 = new Button("Select PDF");
        Button btn2 = new Button("Add Pages");
        Button btn3 = new Button("Finish");
        Button btn4 = new Button("Refresh");
        Button btn5 = new Button("Back");
        hbox.getChildren().addAll(btn1, btn2, btn3, btn4);

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        btn5.setStyle("-fx-font: 18 arial; -fx-base: #b6e7c9;");
        hbox1.getChildren().add(btn5);

        border.setTop(hbox);
        border.setLeft(pageRangeGrid);
        border.setBottom(hbox1);

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    inputfile = filepath();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to open PDF file", e);
                    badpdfcall();
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
                if (inputfile != null && beforeend == true) {
                    spliteandmerge(inputfile);
                }
            }
        });

        btn4.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                refreshPending = true;
                border.setLeft(gridbybutton());
            }
        });

        btn5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(parentScene);
            }
        });

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
        border.setTop(hbox);
        hbox.setStyle("-fx-background-color: #b6e7c9;");

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        Button exit = new Button();
        exit.setText("Exit");
        exit.setStyle("-fx-font: 18 arial; -fx-base: #b6e7c9;");
        hbox1.getChildren().addAll(exit);

        border.setTop(hbox);
        border.setBottom(hbox1);

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

        primaryStage.setTitle("PDF split & merge");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
