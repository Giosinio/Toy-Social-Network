package controller;

import business.FriendshipService;
import business.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.dto.UserDTO;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utils.config.ApplicationContext;

import java.awt.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class AboutController {

    @FXML
    Button backButton;

    @FXML
    public void initialize() {
    }

    @FXML
    public void onBitbucketButtonPressed() {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("https://bitbucket.org/george2001/toysocialnetwork/src/master/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonPressed(ActionEvent clickedEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
