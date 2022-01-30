package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class WindowController {

    double xOffset, yOffset;
    double initialWidth, initialHeight;

    @FXML
    StackPane stackPane;
    @FXML
    AnchorPane loadingScreen, windowRoot, windowPane, windowContentRoot;
    @FXML
    Button minimizeButton, maximizeButton, closeButton;
    @FXML
    FontIcon maximizeButtonIcon;
    @FXML
    Region topbar;
    @FXML
    Label windowTitle, resizeButton;

    @FXML
    void initialize() {

    }

    public void initWindow() {
        Stage primaryStage = (Stage) topbar.getScene().getWindow();
        topbar.setOnMousePressed(pressEvent -> {
            xOffset = primaryStage.getX() - pressEvent.getScreenX();
            yOffset = primaryStage.getY() - pressEvent.getScreenY();
        });
        topbar.setOnMouseDragged(dragEvent -> {
            primaryStage.setX(dragEvent.getScreenX() + xOffset);
            primaryStage.setY(dragEvent.getScreenY() + yOffset);
        });
        resizeButton.setOnMousePressed(pressEvent -> {
            initialWidth = primaryStage.getWidth();
            initialHeight = primaryStage.getHeight();
            xOffset = pressEvent.getScreenX();
            yOffset = pressEvent.getScreenY();
        });
        resizeButton.setOnMouseDragged(dragEvent -> {
            primaryStage.setWidth(initialWidth + dragEvent.getScreenX() - xOffset);
            primaryStage.setHeight(initialHeight + dragEvent.getScreenY() - yOffset);
        });
    }

    void setLoadingScreenVisible(boolean state) {
        loadingScreen.setVisible(state);
    }

    @FXML
    public void onMinimizeButtonPressed(ActionEvent actionEvent) {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void onMaximizeButtonPressed(ActionEvent actionEvent) {
        Stage stage = (Stage) topbar.getScene().getWindow();
        boolean state = stage.isMaximized();
        if (!state) {
            stage.setMaximized(true);
            // the following lines assure that an UNDECORATED stage doesn't cover the taskbar
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double height = primaryScreenBounds.getHeight();
            stage.setHeight(height);
            maximizeButtonIcon.setIconLiteral("far-window-restore");
        } else {
            stage.setMaximized(false);
            maximizeButtonIcon.setIconLiteral("far-window-maximize");
        }
    }

    @FXML
    public void onCloseButtonPressed(ActionEvent actionEvent) {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.close();
    }

    public void setWidth(double width) {
        windowPane.setPrefWidth(width);
    }

    public void setHeight(double height) {
        windowPane.setPrefHeight(height);
    }

    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    public void setContents(Node contents) {
        windowContentRoot.getChildren().setAll(contents);
        AnchorPane.setTopAnchor(contents, 5d);
        AnchorPane.setRightAnchor(contents, 5d);
        AnchorPane.setBottomAnchor(contents, 5d);
        AnchorPane.setLeftAnchor(contents, 5d);
    }

    public void setContents(Node contents, Double topAnchor, Double rightAnchor, Double bottomAnchor, Double leftAnchor) {
        windowContentRoot.getChildren().setAll(contents);
        if (topAnchor!=null)
            AnchorPane.setTopAnchor(contents, topAnchor);
        if (rightAnchor!=null)
            AnchorPane.setRightAnchor(contents, rightAnchor);
        if (bottomAnchor!=null)
            AnchorPane.setBottomAnchor(contents, bottomAnchor);
        if (leftAnchor!=null)
            AnchorPane.setLeftAnchor(contents, leftAnchor);
    }

    public void setTitle(String title) {
        windowTitle.setText(title);
    }

}
