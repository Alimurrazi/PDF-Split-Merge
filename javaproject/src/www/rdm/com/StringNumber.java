package www.rdm.com;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

class StringNumber {

    Project200 a = new Project200();
    BorderPane border = new BorderPane();

    Scene selectbox(Stage stage, Scene homeScene) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        Button btn1 = new Button("By Page Number");
        Button btn2 = new Button("By Chapter's Name");
        hbox.getChildren().addAll(btn1, btn2);
        hbox.setStyle("-fx-background-color: #1976D2;");
        border.setTop(hbox);

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        Button back = new Button("Back");
        back.setStyle("-fx-font: 14 arial;");
        hbox1.getChildren().add(back);
        border.setBottom(hbox1);

        // Array allows lambdas to reference scenesn before it is assigned below
        Scene[] sceneRef = new Scene[1];

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Project200 p = new Project200();
                Scene scene1 = p.fbtn1(stage, sceneRef[0]);
                stage.setScene(scene1);
            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Chapter chapter = new Chapter();
                Scene scene5 = chapter.chapterstring(stage, sceneRef[0]);
                stage.setScene(scene5);
            }
        });

        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                stage.setScene(homeScene);
            }
        });

        Scene scenesn = new Scene(border, 420, 500);
        sceneRef[0] = scenesn;
        return scenesn;
    }
}
