package www.rdm.com;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class Yourchoice extends Application {
    private final String realchaptername;

    Yourchoice(String realchaptername) {
        this.realchaptername = realchaptername;
    }

    @Override
    public void start(Stage primaryStage) {
        Project200 a = new Project200();
        GridPane grid = a.gridinfo();

        Text t = new Text();
        t.setText("Sorry!\nWe have find no section in this Pdf\nNamed " + realchaptername);
        t.setFont(Font.font("Verdana", 18));
        t.setFill(Color.RED);
        grid.add(t, 0, 0);

        Scene scene = new Scene(grid, 400, 125);
        primaryStage.setTitle("PDF split & merge");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
