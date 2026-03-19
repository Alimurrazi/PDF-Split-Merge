package www.rdm.com;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class Badpdf extends Application {
    Project200 a = new Project200();

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = a.gridinfo();

        Text t = new Text();
        t.setText("Your selected PDF\n" + a.pdfpath + "\nis either Protected or Damaged\nPlease select another one");
        t.setFont(Font.font("Verdana", 18));
        t.setFill(Color.RED);
        grid.add(t, 0, 1);

        Scene scene = new Scene(grid, 400, 175);
        primaryStage.setTitle("PDF split & merge");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
