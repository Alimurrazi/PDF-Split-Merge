package www.rdm.com;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Project200 extends Application {
    private static final String TOOLBAR_STYLE = "-fx-background-color: #1976D2;";

    BorderPane border = new BorderPane();
    private Button splitBtn, mergeBtn, removeBtn;

    private HBox buildToolbar() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        splitBtn = new Button("Split and Merge");
        mergeBtn = new Button("Merge PDFs");
        removeBtn = new Button("Remove Pages");
        splitBtn.getStyleClass().add("toolbar-button");
        mergeBtn.getStyleClass().add("toolbar-button");
        removeBtn.getStyleClass().add("toolbar-button");
        hbox.getChildren().addAll(splitBtn, mergeBtn, removeBtn);
        hbox.setStyle(TOOLBAR_STYLE);
        return hbox;
    }

    private VBox buildCenterContent() {
        VBox centerBox = new VBox(12);
        centerBox.setPadding(new Insets(20, 20, 10, 20));
        centerBox.setAlignment(Pos.TOP_CENTER);

        Label appTitle = new Label("PDF Split & Merge");
        appTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label subtitle = new Label("Choose an operation from the toolbar above");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

        VBox card1 = featureCard("Split & Merge",
                "Extract specific page ranges from a PDF into a new file.\nSupports multiple ranges in one pass.");
        VBox card2 = featureCard("Merge PDFs",
                "Combine multiple PDF files into a single document in the order you choose.");
        VBox card3 = featureCard("Remove Pages",
                "Remove specific page ranges from a PDF and save the result.");

        Label hint = new Label("Tip: Drag & drop PDF files directly onto any screen");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #999999; -fx-font-style: italic;");
        hint.setWrapText(true);

        centerBox.getChildren().addAll(appTitle, subtitle, card1, card2, card3, hint);
        return centerBox;
    }

    private VBox featureCard(String title, String description) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #DDDDDD;"
                + " -fx-border-radius: 4; -fx-background-radius: 4;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        descLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, descLabel);
        return card;
    }

    public void start(Stage primaryStage) {
        HBox toolbar = buildToolbar();

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(0, 10, 10, 10));
        bottomBox.setSpacing(10);
        Button exit = new Button("Exit");
        exit.getStyleClass().add("secondary-button");
        bottomBox.getChildren().add(exit);

        border.setTop(toolbar);
        border.setCenter(buildCenterContent());
        border.setBottom(bottomBox);

        Scene homeScene = new Scene(border, 420, 500);
        homeScene.getStylesheets().add(Project200.class.getResource("/www/rdm/com/styles.css").toExternalForm());

        splitBtn.setOnAction(e -> {
            Scene scenesn = new SplitOptions().selectbox(primaryStage, homeScene);
            primaryStage.setScene(scenesn);
        });
        mergeBtn.setOnAction(e -> {
            Scene scene2 = new Mergepdfs().merge(primaryStage, homeScene);
            primaryStage.setScene(scene2);
        });
        removeBtn.setOnAction(e -> {
            Scene scene3 = new Removepage().remove(primaryStage, homeScene);
            primaryStage.setScene(scene3);
        });
        exit.setOnAction(e -> Platform.exit());

        primaryStage.setTitle("PDF Split & Merge");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
