package com.example.practortorestaurantedesktopp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Make tabs take full width
        setupTabWidths();
        System.out.println("MainController initialized");
    }

    private void setupTabWidths() {
        // This will make tabs expand to fill available space
        mainTabPane.tabMinWidthProperty().bind(
                mainTabPane.widthProperty()
                        .divide(mainTabPane.getTabs().size())
                        .subtract(20) // Small margin
        );

        // Optional: Add listener for window resize
        mainTabPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustTabWidths();
        });
    }

    private void adjustTabWidths() {
        double tabPaneWidth = mainTabPane.getWidth();
        int tabCount = mainTabPane.getTabs().size();

        if (tabCount > 0 && tabPaneWidth > 0) {
            double tabWidth = (tabPaneWidth / tabCount) - 2; // Small gap between tabs
            mainTabPane.setTabMinWidth(tabWidth);
            mainTabPane.setTabMaxWidth(tabWidth);
        }
    }
}