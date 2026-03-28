package www.rdm.com;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class Openfile {
    private Desktop desktop = Desktop.getDesktop();

    void openm(File file) {
        Stage stage = new Stage();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        Text t = new Text();
        t.setFont(new Font(15));
        t.setText("Your PDF is complete .\nDo you want to open it now?");
        grid.add(t, 3, 1);
        Button btn1 = new Button("Yes");
        Button btn2 = new Button("No");
        btn1.getStyleClass().add("primary-button");
        btn2.getStyleClass().add("secondary-button");
        grid.add(btn1, 3, 7);
        grid.add(btn2, 7, 7);

        btn2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                stage.close();
            }
        });

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    desktop.open(file);
                    stage.close();
                } catch (IOException ex) {
                    Logger.getLogger(Openfile.class.getName()).log(Level.SEVERE, "Failed to open PDF file", ex);
                }
            }
        });

        Scene scene = new Scene(grid, 400, 175);
        scene.getStylesheets().add(Project200.class.getResource("/www/rdm/com/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
