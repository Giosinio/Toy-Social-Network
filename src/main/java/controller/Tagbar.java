package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class Tagbar<T> {

    @FXML
    FlowPane tagbar;
    @FXML
    FlowPane tags;
    @FXML
    ComboBox<T> tagField;
    @FXML
    ObservableList<T> tagValues = FXCollections.observableArrayList();

    private class Tag extends HBox {

        private Label label;
        private Button button;
        private T data;

        public Tag(T data, String display) {
            getStyleClass().add("tag");
            label = new Label(display);
            label.getStyleClass().add("tag-text");
            maxWidthProperty().bind(tags.prefWidthProperty());
            button = new Button("X");
            button.getStyleClass().add("tag-button");
            getChildren().add(label);
            getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    tagValues.remove(data);
                    tags.getChildren().remove(Tag.this);
                }
            });
        }

    }

    @FXML
    void initialize() {
        tagbar.getStyleClass().add("tag-bar");
        tagbar.getStyleClass().add("text-input");
        tags = (FlowPane) tagbar.getChildren().get(0);
        tags.getChildren().clear();
        tagField = (ComboBox) tagbar.getChildren().get(1);
        tagField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String text = tagField.getEditor().getText();
                if (text.isEmpty())
                    return;
                T data = tagField.getValue();
                if (tagValues.contains(data))
                    return;
                addTag(tagField.getValue(), text);
                tagValues.add(data);
                tagField.getSelectionModel().clearSelection();
                tagField.setValue(null);
            }
        });
    }

    void addTag(T data, String text) {
        Tag tag = new Tag(data, text);
        tags.getChildren().add(tag);
    }

    public void setPromptText(String text) {
        tagField.setPromptText(text);
    }

    public void trackWidthOf(Region region) {
        tags.prefWrapLengthProperty().bind(region.widthProperty());
        tags.prefWidthProperty().bind(region.widthProperty());
        tags.maxWidthProperty().bind(region.widthProperty());
        tagbar.prefWrapLengthProperty().bind(region.widthProperty());
        tagbar.prefWidthProperty().bind(region.widthProperty());
    }

    public void clear() {
        tagValues.clear();
        tags.getChildren().clear();
        tagField.setValue(null);
    }

    public ComboBox<T> getTagField() {
        return tagField;
    }

    public ObservableList<T> getTagValues() {
        return tagValues;
    }

}
