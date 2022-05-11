package com.company.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PlayListPageController implements Initializable {
    private String fileLocation;
    static ArrayList<String> filesName = new ArrayList<>();
    static ArrayList<String> filesLoc = new ArrayList<>();

    @FXML
    private ListView<String> listView;

    @FXML
    private Button addFile;

    @FXML
    private Button deleteFile;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showFiles();
        setOnActions();
    }

    private void showFiles(){
        for (String s : filesName) {
            listView.getItems().add(s);
        }
    }

    private void setOnActions(){
        addFile.setOnAction(e -> {
            addFile();
        });
        deleteFile.setOnAction(e -> {
            removeFile();
        });
    }

    private void addFile(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a File (*.mp4/*.mp3)"
                , "*.mp3", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileLocation = file.toURI().toString();
            filesName.add(file.getName());
            filesLoc.add(fileLocation);
            listView.getItems().add(file.getName());
        }
    }

    private void removeFile(){
        String fileChosen = listView.getSelectionModel().getSelectedItem();
        if (fileChosen != null){
            listView.getItems().remove(fileChosen);

            for (int i = 0; i < filesName.size();i++){
                if (filesName.get(i).equals(fileChosen)){
                    filesName.remove(i);
                    filesLoc.remove(i);
                }
            }
        }
    }
}
