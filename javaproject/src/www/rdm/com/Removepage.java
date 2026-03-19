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
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import static www.rdm.com.Project200.scene1;

class Removepage extends Project200 {

    BorderPane borderpane = new BorderPane();
    Project200 a = new Project200();
    GridPane grid = new GridPane();
    int count = 1;
    boolean checkpage[] = new boolean[50000];

    GridPane gridbybutton() {
        {
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

            HBox hbox1 = new HBox();
            hbox1.setSpacing(10);
            hbox1.getChildren().addAll(tf1, tf2, okbutton);
            grid.add(hbox1, 0, b);

            okbutton.setOnAction(new EventHandler<ActionEvent>() {
                int from, to;
                @Override
                public void handle(ActionEvent e) {
                    beforeend = true;
                    if ((tf1.getText() != null && !tf1.getText().isEmpty() && tf2.getText() != null && !tf2.getText().isEmpty())) {
                        row = GridPane.getRowIndex(hbox1);
                        s1 = tf1.getText();
                        s2 = tf2.getText();
                        from = Integer.parseInt(s1);
                        to = Integer.parseInt(s2);
                        firstpart[row] = from;
                        secondpart[row] = to;
                        if (from < lowestpage)
                            lowestpage = from;
                        if (to > highestpage)
                            highestpage = to;
                    }
                }
            });
            b = b + 1;
        }
        if (d == 1) {
            grid.getChildren().clear();
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
            int i, j;
            File file = savefile();
            if (file == null) return;
            selectedfile = file;
            pdfreader = new PdfReader(inputfile);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                PdfCopy copy;
                try {
                    copy = new PdfCopy(document, fos);
                } catch (DocumentException ex) {
                    return;
                }
                document.open();

                Arrays.fill(checkpage, true);

                for (i = 2; i <= b; i++) {
                    for (j = firstpart[i]; j <= secondpart[i]; j++) {
                        if (j != 0)
                            checkpage[j] = false;
                    }
                }

                for (i = 1; i <= pdfreader.getNumberOfPages(); i++) {
                    if (checkpage[i] == true) {
                        try {
                            copy.addPage(copy.getImportedPage(pdfreader, i));
                        } catch (BadPdfFormatException ex) {
                        }
                    }
                }

                document.close();
            }
            Openfile openfile = new Openfile();
            openfile.openm(selectedfile);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            if (pdfreader != null) pdfreader.close();
        }
    }

    void remove() {
        BorderPane border = new BorderPane();
        grid = gridinfo();
        scene3 = new Scene(border, 420, 500);
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
                inputfile = null;
                try {
                    inputfile = a.filepath();
                    if (inputfile == null) return;
                    Arrays.fill(selectedpage, false);
                    Path path = Paths.get(inputfile);
                    a.pdfpath = path.getFileName();
                    PdfReader pdfreader = new PdfReader(inputfile);
                    try {
                    } finally {
                        pdfreader.close();
                    }
                } catch (Exception e) {
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
                pristage.setScene(scene);
            }
        });
    }
}
