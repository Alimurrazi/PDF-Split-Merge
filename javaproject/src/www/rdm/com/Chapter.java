package www.rdm.com;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.lang.String;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;

class Chapter {
    BorderPane border = new BorderPane();
    Project200 a = new Project200();
    GridPane grid2 = new GridPane();
    boolean check = false;
    static TextField tf1;
    public static String filename, chaptername, realchaptername, clear;
    File file;

    public static int compp = 0, compl = 0;
    public static float max = 0;
    public static int startforinput, endforinput;
    public static float ara[][] = new float[5000][1000];
    public static int ara1[] = new int[5000];
    public static String sara[][] = new String[5000][1000];

    void splitbychapter() throws IOException {
        PdfReader reader = new PdfReader(filename);
        try {
            SemTextExtractionStrategy semtextextractionstrategy = new SemTextExtractionStrategy();
            try (PrintWriter out = new PrintWriter(new FileOutputStream(filename + ".txt"))) {
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    compp = i;
                    compl = 0;
                    out.println(PdfTextExtractor.getTextFromPage(reader, i, (TextExtractionStrategy) semtextextractionstrategy));
                }
                out.flush();
            }
        } finally {
            reader.close();
        }

        File filet = new File(filename + ".txt");
        filet.delete();

        check = false;

        for (int i = startforinput + 1; i <= compp; i++) {
            if (check == true)
                break;
            for (int j = 1; j <= ara1[i]; j++) {
                if (ara[i][j] == max) {
                    endforinput = i - 1;
                    check = true;
                    break;
                }
            }
        }

        if (startforinput == 0) {
            Stage prstage = new Stage();
            Yourchoice yourchoice = new Yourchoice();
            try {
                yourchoice.start(prstage);
            } catch (Exception ex) {
            }
        } else {
            file = a.savefile();
            if (file == null) return;
            split();
        }

        filename = null;
        check = true;
        border.setLeft(gridbybutton());
        startforinput = 0;
        endforinput = 0;
        for (int i = 1; i <= compp; i++) {
            for (int j = 1; j <= ara1[i]; j++) {
                clear = " ";
                clear = clear.replaceAll("\\s+", "");
                sara[i][j] = clear;
            }
        }
    }

    GridPane gridbybutton() {
        Button btn = new Button("Start");
        tf1 = new TextField();
        tf1.setPrefWidth(100);
        tf1.setPromptText("Chapter's Name");
        grid2.add(tf1, 0, 0);
        grid2.add(btn, 0, 1);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if ((tf1.getText() != null && !tf1.getText().isEmpty())) {
                    chaptername = tf1.getText();
                    realchaptername = chaptername;
                    chaptername = chaptername.replaceAll("\\s+", "");
                    chaptername = chaptername.toLowerCase();
                    try {
                        splitbychapter();
                    } catch (IOException ex) {
                    }
                }
            }
        });

        if (check == true) {
            check = false;
            grid2.getChildren().clear();
        }
        return grid2;
    }

    void chapterstring() throws NoClassDefFoundError {
        Project200 a = new Project200();
        grid2 = a.gridinfo();
        a.scene5 = new Scene(border, 420, 500);
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        Button btn1 = new Button("Select Pdf");
        Button btn2 = new Button("Refresh");
        hbox.getChildren().addAll(btn1, btn2);
        hbox.setStyle("-fx-background-color: #336699;");
        border.setTop(hbox);

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        Button back = new Button();
        back.setText("back");
        back.setStyle("-fx-font: 18 arial; -fx-base: #b6e7c9;");
        hbox1.getChildren().add(back);
        border.setBottom(hbox1);

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                filename = null;
                try {
                    filename = a.filepath();
                    if (filename == null) return;
                    Path pathp = Paths.get(filename);
                    a.pdfpath = pathp.getFileName();
                    PdfReader pdfreader = new PdfReader(filename);
                    try {
                        border.setLeft(gridbybutton());
                    } finally {
                        pdfreader.close();
                    }
                } catch (Exception e) {
                    a.badpdfcall();
                }
            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                filename = null;
                check = true;
                border.setLeft(gridbybutton());
            }
        });

        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                a.pristage.setScene(a.scenesn);
            }
        });
    }

    private void split() {
        File selectedfile = null;
        Document document = new Document();
        PdfReader pdfreader = null;
        try {
            selectedfile = file;
            pdfreader = new PdfReader(filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                PdfCopy copy;
                try {
                    copy = new PdfCopy(document, fos);
                } catch (DocumentException ex) {
                    return;
                }
                document.open();
                for (int i = startforinput; i <= endforinput; i++) {
                    try {
                        copy.addPage(copy.getImportedPage(pdfreader, i));
                    } catch (BadPdfFormatException ex) {
                    }
                }
                document.close();
            }
            Openfile openfile = new Openfile();
            openfile.openm(selectedfile);
        } catch (Exception ex) {
        } finally {
            if (pdfreader != null) pdfreader.close();
        }
    }
}
