/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package www.rdm.com;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author RANA_CSE
 */
class StringNumber {

    Project200 a=new Project200();
    BorderPane border=new BorderPane();
    
    void selectbox() {
          a.scenesn=new Scene(border,420,500);
          HBox hbox=new HBox();
       //   hbox.setPadding(new Insets(2));
          hbox.setPadding(new Insets(15, 12, 15, 12));
          hbox.setSpacing(10);
       //   hbox.setSpacing(10);
          Button btn1=new Button("By Page Number");
          Button btn2=new Button("By Chapter's Name");
          hbox.getChildren().addAll(btn1,btn2);
          hbox.setStyle("-fx-background-color: #336699;");
          border.setTop(hbox);
          
          btn1.setOnAction(new EventHandler<ActionEvent>()
         {
          @Override
          public void handle(ActionEvent event)
          {
          a.fbtn1();
          a.pristage.setScene(a.scene1);
          }
         }
         );
          
         btn2.setOnAction(new EventHandler<ActionEvent>()
         {
          public void handle(ActionEvent event)
          {
           Chapter chapter=new Chapter();
           chapter.chapterstring();
           a.pristage.setScene(a.scene5);
          }
         }
         );
         
        HBox hbox1=new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        Button back=new Button();
        back.setText("back");
        back.setStyle("-fx-font: 18 arial; -fx-base: #b6e7c9;");
        hbox1.getChildren().add(back);
        border.setBottom(hbox1);
        
        back.setOnAction(new EventHandler<ActionEvent>()
        {
          public void handle(ActionEvent event)
          {
              a.pristage.setScene(a.scene);
          }
        }
        );
    }
}