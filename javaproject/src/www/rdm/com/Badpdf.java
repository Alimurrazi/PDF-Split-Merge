package www.rdm.com;

import java.nio.file.Path;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class Badpdf extends Application {
    private final Path pdfpath;

    Badpdf(Path pdfpath) {
        this.pdfpath = pdfpath;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Text icon = new Text("⚠");
        icon.setFont(Font.font("Verdana", 36));
        icon.setFill(Color.web("#D32F2F"));

        Label title = new Label("Cannot Open PDF");
        title.setFont(Font.font("Verdana", 16));
        title.setTextFill(Color.web("#D32F2F"));

        Label fileLabel = new Label(pdfpath != null ? pdfpath.toString() : "Unknown file");
        fileLabel.setFont(Font.font("Arial", 13));
        fileLabel.setTextFill(Color.web("#555555"));
        fileLabel.setWrapText(true);

        Label messageLabel = new Label("This PDF is either password-protected or corrupted.\nPlease choose a different file.");
        messageLabel.setFont(Font.font("Arial", 13));
        messageLabel.setWrapText(true);

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("secondary-button");
        closeButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(12, icon, title, fileLabel, messageLabel, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        Scene scene = new Scene(layout, 420, 230);
        scene.getStylesheets().add(Badpdf.class.getResource("/www/rdm/com/styles.css").toExternalForm());
        primaryStage.setTitle("Cannot Open PDF");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
