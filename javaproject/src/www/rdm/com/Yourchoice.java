/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package www.rdm.com;

import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static www.rdm.com.Project200.scene1;

/**
 *
 * @author RANA_CSE
 */
class Yourchoice extends Application{

     Project200 a=new Project200();
     Chapter b=new Chapter();
    @Override
    public void start(Stage primaryStage)  {
        GridPane grid=new GridPane();
        grid=a.gridinfo();
        
        Text t = new Text();
        t.setFont(new Font(15));
        t.setText("Sorry!\nWe have find no section in this Pdf\nNamed "+b.realchaptername);
        t.setFont(Font.font ("Verdana", 18));
        t.setFill(Color.RED);
        grid.add(t, 0, 0);
        Scene scene=new Scene(grid,400,125);
     
        primaryStage.setTitle("PDF split & merge");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
