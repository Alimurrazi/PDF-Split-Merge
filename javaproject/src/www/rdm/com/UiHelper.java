package www.rdm.com;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

class UiHelper {
    private static final Logger LOGGER = Logger.getLogger(UiHelper.class.getName());

    static String filepath() {
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files", "*.pdf");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        return file == null ? null : file.getAbsolutePath();
    }

    static File savefile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF", ".pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showSaveDialog(null);
    }

    static void badpdfcall(Path pdfpath) {
        if (pdfpath == null) return;
        Stage prstage = new Stage();
        Badpdf badpdf = new Badpdf(pdfpath);
        try {
            badpdf.start(prstage);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to show bad PDF dialog", e);
        }
    }

    static GridPane gridinfo() {
        GridPane gri = new GridPane();
        gri.setAlignment(Pos.TOP_LEFT);
        gri.setHgap(10);
        gri.setVgap(10);
        gri.setPadding(new Insets(25, 25, 25, 25));
        return gri;
    }
}
