package www.rdm.com;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        btn1.getStyleClass().add("toolbar-button");
        btn2.getStyleClass().add("toolbar-button");
        hbox.getChildren().addAll(btn1, btn2);
        hbox.setStyle("-fx-background-color: #1976D2;");
        border.setTop(hbox);

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 10, 10));
        hbox1.setSpacing(10);
        Button back = new Button("Back");
        back.getStyleClass().add("secondary-button");
        hbox1.getChildren().add(back);
        border.setBottom(hbox1);

        // Center: explain the two split options
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20, 20, 20, 20));
        centerBox.setAlignment(Pos.TOP_CENTER);

        Label heading = new Label("How would you like to split?");
        heading.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        VBox card1 = optionCard("By Page Number",
                "Extract specific pages or ranges from a PDF.\n"
                + "Example: pages 1\u20135 and 10\u201315 saved as a new file.");

        VBox card2 = optionCard("By Chapter Name",
                "Automatically detect chapter headings using font-size analysis\n"
                + "and extract a full chapter into a new file.");

        centerBox.getChildren().addAll(heading, card1, card2);
        border.setCenter(centerBox);

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
        scenesn.getStylesheets().add(Project200.class.getResource("/www/rdm/com/styles.css").toExternalForm());
        sceneRef[0] = scenesn;
        return scenesn;
    }

    private VBox optionCard(String title, String description) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12, 15, 12, 15));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #BBDEFB;"
                + " -fx-border-radius: 4; -fx-background-radius: 4;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        descLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, descLabel);
        return card;
    }
}
