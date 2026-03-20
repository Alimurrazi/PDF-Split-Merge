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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

class Removepage extends Project200 {
    private static final Logger LOGGER = Logger.getLogger(Removepage.class.getName());

    Project200 a = new Project200();
    GridPane grid = new GridPane();

    GridPane gridbybutton() {
        if (coun == 1) {
            Label text = new Label("Contributing Pages");
            text.setFont(new Font("Arial", 25));
            grid.add(text, 0, 1);
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
        grid.add(hbox1, 0, b);

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
            grid.getChildren().clear();
            pageRanges.clear();
            d = 0;
            coun = 1;
            b = 2;
        }
        return grid;
    }

    void select(String inputfile) {
        Document document = new Document();
        File selectedfile = null;
        PdfReader pdfreader = null;
        try {
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

                Set<Integer> pagesToRemove = new HashSet<>();
                for (int[] range : pageRanges) {
                    for (int j = range[0]; j <= range[1]; j++) {
                        pagesToRemove.add(j);
                    }
                }

                for (int i = 1; i <= pdfreader.getNumberOfPages(); i++) {
                    if (!pagesToRemove.contains(i)) {
                        try {
                            copy.addPage(copy.getImportedPage(pdfreader, i));
                        } catch (BadPdfFormatException ex) {
                            LOGGER.log(Level.WARNING, "Skipping malformed page " + i, ex);
                        }
                    }
                }

                document.close();
            }
            Openfile openfile = new Openfile();
            openfile.openm(selectedfile);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to remove pages from PDF", ex);
            a.badpdfcall();
        } finally {
            if (pdfreader != null) pdfreader.close();
        }
    }

    Scene remove(Stage stage, Scene homeScene) {
        BorderPane border = new BorderPane();
        grid = gridinfo();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Button btn1 = new Button("Select PDF");
        Button btn2 = new Button("Add Pages");
        Button btn3 = new Button("Finish");
        Button btn4 = new Button("Refresh");
        Button btn5 = new Button("Back");
        hbox.getChildren().addAll(btn1, btn2, btn3, btn4);

        btn5.setStyle("-fx-font: 18 arial; -fx-base: #b6e7c9;");
        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        hbox1.getChildren().add(btn5);

        border.setTop(hbox);
        border.setBottom(hbox1);

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    inputfile = a.filepath();
                    if (inputfile == null) return;
                    Path path = Paths.get(inputfile);
                    a.pdfpath = path.getFileName();
                    PdfReader pdfreader = new PdfReader(inputfile);
                    try {
                    } finally {
                        pdfreader.close();
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to open PDF file", e);
                    a.badpdfcall();
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
                    select(inputfile);
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
                stage.setScene(homeScene);
            }
        });

        return new Scene(border, 420, 500);
    }
}
