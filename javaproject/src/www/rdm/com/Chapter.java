package www.rdm.com;

import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

class Chapter {
    private static final Logger LOGGER = Logger.getLogger(Chapter.class.getName());

    BorderPane border = new BorderPane();
    Project200 a = new Project200();
    GridPane grid2 = new GridPane();
    boolean check = false;
    TextField tf1;
    String filename = null;
    String realchaptername = null;

    void splitbychapter() throws IOException {
        PdfChapterSplitService service = new PdfChapterSplitService();
        int[] pages = service.findChapter(filename, realchaptername);

        if (pages == null) {
            Stage prstage = new Stage();
            Yourchoice yourchoice = new Yourchoice(realchaptername);
            try {
                yourchoice.start(prstage);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Failed to show chapter-not-found dialog", ex);
            }
        } else {
            File file = a.savefile();
            if (file == null) return;
            try {
                service.extractPages(filename, pages[0], pages[1], file);
                Openfile openfile = new Openfile();
                openfile.openm(file);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to split PDF", ex);
                a.badpdfcall();
            }
        }

        filename = null;
        check = true;
        border.setLeft(gridbybutton());
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
                if (tf1.getText() != null && !tf1.getText().isEmpty()) {
                    realchaptername = tf1.getText();
                    try {
                        splitbychapter();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, "Failed to split PDF by chapter", ex);
                        a.badpdfcall();
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

    Scene chapterstring(Stage stage, Scene parentScene) throws NoClassDefFoundError {
        Project200 a = new Project200();
        grid2 = a.gridinfo();
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
                    LOGGER.log(Level.WARNING, "Failed to open PDF file", e);
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
                stage.setScene(parentScene);
            }
        });

        return new Scene(border, 420, 500);
    }
}
