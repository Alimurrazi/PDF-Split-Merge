package www.rdm.com;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileOutputStream;
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

    public static Path pdfpath = null;
    BorderPane border = new BorderPane();
    GridPane grid, grid1;
    static Scene scene, scene1, scene2, scene3, scene4, scenesn, scene5, scene6;
    int b = 3;
    String inputfile = null;
    int d = 0, coun = 1;
    List<int[]> pageRanges = new ArrayList<>();
    boolean beforeend = false;
    static Stage pristage;
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
            Badpdf badpdf = new Badpdf();
            try {
                badpdf.start(prstage);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to show bad PDF dialog", e);
            }
        }
    }

    void spliteandmerge(String inputfile) {
        File selectedfile = null;
        Document document = new Document();
        PdfReader pdfreader = null;
        try {
            Path path = Paths.get(filename);
            pdfpath = path.getFileName();
            File file = savefile();
            if (file == null) return;
            selectedfile = file;
            pdfreader = new PdfReader(inputfile);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                PdfCopy copy;
                try {
                    copy = new PdfCopy(document, fos);
                } catch (DocumentException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to create PDF writer", ex);
                    return;
                }
                document.open();
                for (int[] range : pageRanges) {
                    for (int j = range[0]; j <= range[1]; j++) {
                        try {
                            copy.addPage(copy.getImportedPage(pdfreader, j));
                        } catch (BadPdfFormatException ex) {
                            LOGGER.log(Level.WARNING, "Skipping malformed page " + j, ex);
                            badpdfcall();
                        }
                    }
                }
                document.close();
            }
            Openfile openfile = new Openfile();
            openfile.openm(selectedfile);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to split/merge PDF", ex);
            badpdfcall();
        } finally {
            if (pdfreader != null) pdfreader.close();
        }
    }

    GridPane gridbybutton() {
        if (coun == 1) {
            Label text = new Label("Contributing Pages");
            text.setFont(new Font("Arial", 25));
            grid1.add(text, 0, 1);
            coun = 2;
        }

        Button okbutton = new Button("OK");
        okbutton.setStyle("-fx-font: 12 arial; -fx-base: #b6e7c9;");
        TextField tf1 = new TextField();
        TextField tf2 = new TextField();
        tf1.setPrefWidth(100);
        tf2.setPrefWidth(100);
        tf1.setPromptText("From");
        tf2.setPromptText("To");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        HBox hbox1 = new HBox();
        hbox1.setSpacing(10);
        hbox1.getChildren().addAll(tf1, tf2, okbutton, errorLabel);
        grid1.add(hbox1, 0, b);

        okbutton.setOnAction(new EventHandler<ActionEvent>() {
            int from, to;
            @Override
            public void handle(ActionEvent e) {
                errorLabel.setText("");
                if (tf1.getText() == null || tf1.getText().isEmpty() || tf2.getText() == null || tf2.getText().isEmpty()) {
                    errorLabel.setText("Both fields are required");
                    return;
                }
                try {
                    from = Integer.parseInt(tf1.getText().trim());
                    to = Integer.parseInt(tf2.getText().trim());
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
        b = b + 1;

        if (d == 1) {
            grid1.getChildren().clear();
            pageRanges.clear();
            d = 0;
            coun = 1;
            b = 2;
        }
        return grid1;
    }

    void fbtn1() {
        BorderPane border = new BorderPane();
        grid1 = gridinfo();
        scene1 = new Scene(border, 420, 500);
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
        border.setLeft(grid1);
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
                d = 1;
                border.setLeft(gridbybutton());
            }
        });

        btn5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pristage.setScene(scenesn);
            }
        });
    }

    public void start(Stage primaryStage) {
        pristage = primaryStage;
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

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                StringNumber stringnumber = new StringNumber();
                stringnumber.selectbox();
                pristage.setScene(scenesn);
            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Mergepdfs mergepdfs = new Mergepdfs();
                mergepdfs.merge();
                pristage.setScene(scene2);
            }
        });

        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Removepage removepage = new Removepage();
                removepage.remove();
                pristage.setScene(scene3);
            }
        });

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        Button exit = new Button();
        exit.setText("Exit");
        exit.setStyle("-fx-font: 18 arial; -fx-base: #b6e7c9;");
        hbox1.getChildren().addAll(exit);

        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Platform.exit();
            }
        });

        border.setTop(hbox);
        border.setBottom(hbox1);
        scene = new Scene(border, 420, 500);
        pristage.setTitle("PDF split & merge");
        pristage.setScene(scene);
        pristage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
