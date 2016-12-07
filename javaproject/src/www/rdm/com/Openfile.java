/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package www.rdm.com;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author RANA_CSE
 */
class Openfile {
        private Desktop desktop = Desktop.getDesktop();
    void openm(File file) {
        Stage stage=new Stage();
        GridPane grid=new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        Text t = new Text();
        t.setFont(new Font(15));
        t.setText("Your PDF is complete .\nDo you want to open it now?");
        grid.add(t, 3, 1);
        Button btn1=new Button("Yes");
        Button btn2=new Button("No");
        grid.add(btn1,3, 7);
        grid.add(btn2,7,7);
        
        btn2.setOnAction(new EventHandler<ActionEvent>()
        {
         public void handle(ActionEvent e)
         {   
      //   Platform.exit();
             stage.close();
     //    return;
         }
        }
        );
        
         btn1.setOnAction(new EventHandler<ActionEvent>()
        {
         public void handle(ActionEvent e)
         {   
                        try {
                desktop.open(file);
          //      return;
          //      Platform.exit();
                stage.close();
            } catch (IOException ex) {
              //  Logger.getLogger(Openfile.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
        }
        );
        
        Scene scene=new Scene(grid,400,175);
   //     Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
