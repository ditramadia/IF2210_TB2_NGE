package com.example.if2210_tb2_nge;

import com.example.if2210_tb2_nge.adapter.DataStore;
import com.example.if2210_tb2_nge.adapter.DataStoreFactory;
import com.example.if2210_tb2_nge.repository.ItemsRepository;
import com.example.if2210_tb2_nge.pages.CustomerPage;
import com.example.if2210_tb2_nge.pages.HomePage;
import com.example.if2210_tb2_nge.pages.InventoryPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class NgeApp extends Application implements EventHandler<ActionEvent> {
    HomePage homePage;
    TabPane tabPane;
    String itemsFileName;
    String customersFileName;
    String transactionsFileName;

    @Override
    public void start(Stage stage) throws IOException {
        // land on homepage
        homePage = new HomePage();
        homePage.getHomeNavBtn().setOnAction(this);
        homePage.getInventoryNavBtn().setOnAction(this);
        homePage.getCustomerNavBtn().setOnAction(this);

        // app
        stage.setTitle("Gangguan Jawa");
        BorderPane root = new BorderPane();
//        root.setStyle("-fx-background-color: #8C7466");

        // load default repository
        try {
            DataStore defaultDataStore = DataStoreFactory.getDataStore("src/main/java/com/example/if2210_tb2_nge/repository/Items.json", "json");
            this.itemsFileName = "Items";
            Object defaultData = defaultDataStore.load();
            ItemsRepository.setItemsRepository(defaultData);
            ItemsRepository.printItems();
        } catch (Exception e) {}

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #8C7466");

        Menu directory =  new Menu("File");
//        file.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        MenuItem importDb = new MenuItem("Import Database");
        importDb.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Folder");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            // Set the file chooser to select only directories

            // Show the dialog and get the selected folder
            File selectedFolder = directoryChooser.showDialog(stage);

            if (selectedFolder != null) {
                // Do something with the selected folder
                System.out.println("Selected folder: " + selectedFolder.getAbsolutePath());

                //print all files in the folder
                File[] files = selectedFolder.listFiles();
                for (File file : files) {
                    if (file.isFile()) {
                        // choose file that has .json extension
                        if (file.getName().endsWith(".json")) {
                            System.out.println("JSON file: " + file.getAbsolutePath());
                            DataStore dataStore = DataStoreFactory.getDataStore(file.getAbsolutePath(), "json");
                            this.itemsFileName = file.getName().substring(0, file.getName().length() - 5);
                            Object data = dataStore.load();

                            // save data to repository
                            try {
                                ItemsRepository.setItemsRepository(data);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        else if (file.getName().endsWith(".xml")) {
                            System.out.println("XML file: " + file.getAbsolutePath());
                            DataStore dataStore = DataStoreFactory.getDataStore(file.getAbsolutePath(), "xml");
                            Object data = dataStore.load();

                            // save data to repository
                            try {
                                ItemsRepository.setItemsRepository(data);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        else {
                            System.out.println("OBJ file: " + file.getAbsolutePath());
                            DataStore dataStore = DataStoreFactory.getDataStore(file.getAbsolutePath(), "obj");
                            Object data = dataStore.load();

                            // save data to repository
                            try {
                                ItemsRepository.setItemsRepository(data);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }

        });


        MenuItem exportDb = new MenuItem("Export Database");
        exportDb.setOnAction(e -> {
            DataStore dataStore = DataStoreFactory.getDataStore("saves/" + this.itemsFileName + ".xml", "xml");
            Object data = ItemsRepository.saveItems();
            dataStore.save(data);
        });


        MenuItem exit = new MenuItem("Exit");
        directory.getItems().addAll(importDb,exportDb,exit);

        Menu plugin = new Menu ("Plugin");
        MenuItem importPlugin = new MenuItem("Import Plugin");
        MenuItem removePlugin = new MenuItem("Remove Plugin");
        plugin.getItems().addAll(importPlugin, removePlugin);

        Menu setting = new Menu("Setting");
        MenuItem datastore = new MenuItem("Data Store");
        setting.getItems().addAll(datastore);

        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        MenuItem author = new MenuItem("Author");
        MenuItem howToUse = new MenuItem("How To Use");
        help.getItems().addAll(howToUse, about, author);

        menuBar.setUseSystemMenuBar(true);
        menuBar.getMenus().addAll(directory,plugin, setting, help);
        // Tab Panel
        tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-width: 100; -fx-tab-max-width: 100; -fx-tab-min-height: 30; -fx-tab-max-height: 30; -fx-tab-background-radius: 20px; -fx-background-color: #FFFFFF; -fx-tab-background-color: #CCCCCC; -fx-tab-text-color: white;");

        File cssFile = new File("src/main/java/com/example/if2210_tb2_nge/style.css");
        String cssUrl = cssFile.toURI().toURL().toExternalForm();

//        tabPane.setStyle("-fx-background-color: #D7CDC7");
        Tab tab1 = homePage.getTab();
        tab1.selectedProperty().addListener(new ChangeListener<Boolean>(){

            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (t1.booleanValue()) {
                    // it would be better to use FadeTransitionBuilder here...
                    FadeTransition ft = new FadeTransition();
                    ft.setNode(tab1.getContent());
                    ft.setDuration(Duration.millis(2000));
                    ft.setFromValue(0.1);
                    ft.setToValue(1.0);
                    ft.setCycleCount(1);
                    ft.play();
                }
            }
        });
//        tab1.setStyle("-fx-background-color: #83695A; -fx-text-fill: white;");
        tab1.setClosable(false);
        tabPane.getTabs().add(tab1);

        root.setTop(menuBar);
        root.setCenter(tabPane);
        Scene scene = new Scene(root, 1920, 1024);
        root.getStylesheets().add(cssUrl);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            Button button = (Button) actionEvent.getSource();
            if (button.getText() == "Home") {
                HomePage newHomePage = new HomePage();
                newHomePage.getHomeNavBtn().setOnAction(this);
                newHomePage.getInventoryNavBtn().setOnAction(this);
                tabPane.getTabs().add(newHomePage.getTab());
                tabPane.getSelectionModel().select(newHomePage.getTab());
            } else if (button.getText() == "Inventory") {
                InventoryPage newMenuPage = null;
                try {
                    newMenuPage =  new InventoryPage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Tab newTab = newMenuPage.getTab();
                InventoryPage finalNewMenuPage = newMenuPage;
                newTab.setOnSelectionChanged(event -> {
                    if (newTab.isSelected()) {
                        // Call a function from MenuPage
                        try {
                            finalNewMenuPage.updateCard();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                tabPane.getTabs().add(newMenuPage.getTab());
                tabPane.getSelectionModel().select(newMenuPage.getTab());
            } else if (button.getText().trim().equals("Customer")) {
                CustomerPage newCustomerPage = null;
                try {
                    newCustomerPage = new CustomerPage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                tabPane.getTabs().add(newCustomerPage.getTab());
                tabPane.getSelectionModel().select(newCustomerPage.getTab());
            }
        }
    }

}

