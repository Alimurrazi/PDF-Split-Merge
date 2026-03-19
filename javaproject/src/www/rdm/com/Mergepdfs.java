package www.rdm.com;

import com.itextpdf.text.Document;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileOutputStream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Mergepdfs {
    BorderPane border = new BorderPane();
    String[] filelist = new String[200];
    int serial;
    int filenamer;
    Project200 a = new Project200();
    int check = 0;
    GridPane grid1 = new GridPane();

    void mergefiles() {
        File selectedfile = null;
        try {
            int i, j;
            Document document = new Document();
            File file = a.savefile();
            if (file == null) return;
            selectedfile = file;
            try (FileOutputStream fos = new FileOutputStream(file)) {
                PdfCopy pdfcopy = new PdfCopy(document, fos);
                document.open();
                for (i = 0; i < serial; i++) {
                    PdfReader pdfreader = new PdfReader(filelist[i]);
                    try {
                        for (j = 1; j <= pdfreader.getNumberOfPages(); j++) {
                            try {
                                pdfcopy.addPage(pdfcopy.getImportedPage(pdfreader, j));
                            } catch (Exception e) {
                            }
                        }
                    } finally {
                        pdfreader.close();
                    }
                }
                document.close();
            }
            Openfile openfile = new Openfile();
            openfile.openm(selectedfile);
        } catch (Exception e) {
        }
    }

    GridPane gridbybutton(Text t) {
        t.setFont(new Font(20));
        grid1.add(t, 0, filenamer);
        filenamer++;
        if (check == 1) {
            check = 0;
            grid1.getChildren().clear();
        }
        return grid1;
    }

    void merge() {
        grid1 = a.gridinfo();
        a.scene2 = new Scene(border, 420, 500);
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        Button btn1 = new Button("Select Pdf");
        Button btn2 = new Button("Finish");
        Button btn3 = new Button("Refresh");
        hbox.getChildren().addAll(btn1, btn2, btn3);
        hbox.setStyle("-fx-background-color: #336699;");
        border.setTop(hbox);
        border.setLeft(grid1);

        serial = 0;
        filenamer = 1;

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Project200 project200 = new Project200();
                String filename = null;
                try {
                    filename = project200.filepath();
                    if (filename == null) return;
                    Path pathp = Paths.get(filename);
                    a.pdfpath = pathp.getFileName();
                    PdfReader pdfreader = new PdfReader(filename);
                    try {
                        filelist[serial] = filename;
                        serial++;
                        Text t = new Text();
                        Path path = Paths.get(filename);
                        t.setText(Integer.toString(serial) + "." + "  " + path.getFileName());
                        border.setLeft(gridbybutton(t));
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
                mergefiles();
            }
        });

        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Text t1 = new Text();
                t1.setText(null);
                check = 1;
                border.setLeft(gridbybutton(t1));
            }
        });

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        Button back = new Button();
        back.setText("back");
        back.setStyle("-fx-font: 18 arial; -fx-base: #b6e7c9;");
        hbox1.getChildren().add(back);
        border.setBottom(hbox1);

        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                a.pristage.setScene(a.scene);
            }
        });
    }
}
