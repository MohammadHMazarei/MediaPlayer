package com.company.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class MediaPageController implements Initializable {
    private String fileLocation;
    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean isPlay = false;
    private boolean isLoop = false;
    private boolean isNext = false;
    private double volSlider;

    @FXML
    private Button chooseFile;

    @FXML
    private Button back30BTN;

    @FXML
    private Button playBTN;

    @FXML
    private Button pauseBTN;

    @FXML
    private Button skip30BTN;

    @FXML
    private Button stopBTN;

    @FXML
    private Button loopBTN;

    @FXML
    private Button playListPage;

    @FXML
    private Button previousBTN;

    @FXML
    private Button nextBTN;

    @FXML
    private Slider videoSlider;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Label volumePercent;

    @FXML
    private Label currentTime;

    @FXML
    private Label totalTime;

    @FXML
    private MediaView mediaView;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumePercent.setText("0%");
        setOnActions();
    }


    private void setOnActions(){
        chooseFile.setOnAction(e -> {
            chooseFile();
        });
        playBTN.setOnAction(e ->{
            play();
        });
        pauseBTN.setOnAction(e ->{
            pause();
        });
        stopBTN.setOnAction(e ->{
            stop();
        });
        skip30BTN.setOnAction(e ->{
            skip30S();
        });
        back30BTN.setOnAction(e ->{
            back30S();
        });
        loopBTN.setOnAction(e ->{
            loop();
        });
        playListPage.setOnAction(e ->{
            try {
                openPlayListPage();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        nextBTN.setOnAction(e -> {
            next();
        });
        previousBTN.setOnAction(e -> {
            previous();
        });
    }


    private void chooseFile(){
        if (PlayListPageController.filesLoc.size() == 0) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a File (*.mp4/*.mp3)"
                    , "*.mp3", "*.mp4");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                fileLocation = file.toURI().toString();
                PlayListPageController.filesName.add(file.getName());
                PlayListPageController.filesLoc.add(fileLocation);
            }

            if (fileLocation != null) {
                media = new Media(fileLocation);
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);

            DoubleProperty width = mediaView.fitWidthProperty();
            DoubleProperty height = mediaView.fitHeightProperty();

            width.bind(Bindings.selectDouble(mediaView.sceneProperty() , "width"));
            height.bind(Bindings.selectDouble(mediaView.sceneProperty() , "height"));

                VBox vBox = new VBox();

                play();
            }
        }
    }

    private void play(){
        if (PlayListPageController.filesLoc.size() > 0) {
            if (!isPlay) {
                videoSlider();
                mousePressOnVideoSlider();
                mouseDraggedOnVideoSlider();
                maxDuration(media);
                volumeSlider();
                timeLabel();
                mediaPlayer.play();
                endOfMedia();
                isPlay = true;
            }
        }
        else{
            chooseFile();
            if (PlayListPageController.filesLoc.size() > 0)
                volumePercent.setText("100%");
        }
    }

    private void pause(){
        if (PlayListPageController.filesLoc.size() > 0) {
            if (isPlay) {
                mediaPlayer.pause();
                isPlay = false;
            }
        }
    }

    private void stop(){
        if (PlayListPageController.filesLoc.size() > 0) {
            mediaPlayer.stop();
            isPlay = false;
            videoSlider.setValue(0);
            currentTime.setVisible(false);
            totalTime.setVisible(false);
            mediaView.setMediaPlayer(null);
        }
    }

    private void skip30S(){
        if (PlayListPageController.filesLoc.size() > 0)
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(30)));
    }

    private void back30S(){
        if (PlayListPageController.filesLoc.size() > 0)
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-30)));
    }

    private void videoSlider(){
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            videoSlider.setValue(newValue.toSeconds());
        });
    }

    private void mousePressOnVideoSlider(){
        videoSlider.setOnMousePressed(e ->{
            mediaPlayer.seek(Duration.seconds(videoSlider.getValue()));
        });
    }

    private void mouseDraggedOnVideoSlider(){
        videoSlider.setOnMouseDragged(e ->{
            mediaPlayer.seek(Duration.seconds(videoSlider.getValue()));
        });
    }

    private void maxDuration(Media media){
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration totalTime = media.getDuration();
                videoSlider.setMax(totalTime.toSeconds());
            }
        });
    }

    private void volumeSlider(){
        if (PlayListPageController.filesLoc.size() == 1) {

            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volSlider = volumeSlider.getValue();

            volumeSlider.setOnMousePressed(e ->{
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                volSlider = volumeSlider.getValue();
                volumePercent.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String volPer = String.valueOf((int) volumeSlider.getValue());
                        return volPer + "%";
                    }
                }));
            });

            volumeSlider.setOnMouseDragged(e ->{
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                volSlider = volumeSlider.getValue();
                volumePercent.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String volPer = String.valueOf((int) volumeSlider.getValue());
                        return volPer + "%";
                    }
                }));
            });
        }
        else{
            mediaPlayer.setVolume(volSlider / 100);
            volumeSlider.setOnMousePressed(e ->{
                mediaPlayer.setVolume(volSlider / 100);
                volSlider = volumeSlider.getValue();
                volumePercent.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String volPer = String.valueOf((int) volumeSlider.getValue());
                        return volPer + "%";
                    }
                }));
            });

            volumeSlider.setOnMouseDragged(e ->{
                mediaPlayer.setVolume(volSlider / 100);
                volSlider = volumeSlider.getValue();
                volumePercent.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String volPer = String.valueOf((int) volumeSlider.getValue());
                        return volPer + "%";
                    }
                }));
            });
        }
    }

    private void loop(){
        if (PlayListPageController.filesLoc.size() > 0) {
            if (!isLoop) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                isLoop = true;
            } else {
                isLoop = false;
                endOfMedia();
            }
        }
    }

    private void endOfMedia(){
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if (!isLoop && PlayListPageController.filesLoc.size() == 1)
                    stop();
                else if (isLoop && PlayListPageController.filesLoc.size() > 1)
                    isLoop = true;
                else if (PlayListPageController.filesLoc.size() > 1)
                    next();
            }
        });
    }

    private void timeLabel(){
        currentTime.setVisible(true);
        totalTime.setVisible(true);
        currentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mediaPlayer.getCurrentTime()) + " / " + getTime(mediaPlayer.getMedia().getDuration());
            }
        } , mediaPlayer.currentTimeProperty()));
    }

    private String getTime(Duration time) {
        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59)
            seconds = seconds % 60;
        if (minutes > 59)
            minutes = minutes % 60;
        if (hours > 59)
            hours = hours % 60;

        if (hours > 0){
            return String.format("%d:%02d:%02d" , hours , minutes , seconds);
        }
        else
            return String.format("%02d:%02d" , minutes , seconds);
    }

    private void openPlayListPage() throws IOException {
        if (PlayListPageController.filesLoc.size() > 0) {
            AnchorPane root = FXMLLoader.load(this.getClass().getResource("../view/PlayListPage.fxml"));

            Stage stage = new Stage();
            stage.setTitle("ORIPlayer - PlayList");
            stage.resizableProperty().setValue(Boolean.FALSE);
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    private void next() {
        if (PlayListPageController.filesLoc.size() > 0) {
            if (isPlay) {
                if (PlayListPageController.filesLoc.size() > 1) {
                    stop();
                    for (int i = 0; i < PlayListPageController.filesLoc.size(); i++) {
                        if (PlayListPageController.filesLoc.get(i).equals(fileLocation) && i != PlayListPageController.filesLoc.size() - 1) {
                            fileLocation = PlayListPageController.filesLoc.get(i + 1);
                            media = new Media(fileLocation);
                            mediaPlayer = new MediaPlayer(media);
                            mediaView.setMediaPlayer(mediaPlayer);
                            play();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void previous(){
        if (PlayListPageController.filesLoc.size() > 0) {
            if (isPlay) {
                if (PlayListPageController.filesLoc.size() > 1) {
                    stop();
                    for (int i = PlayListPageController.filesLoc.size() - 1; i >= 0; i--) {
                        if (PlayListPageController.filesLoc.get(i).equals(fileLocation) && i != 0) {
                            fileLocation = PlayListPageController.filesLoc.get(i - 1);
                            media = new Media(fileLocation);
                            mediaPlayer = new MediaPlayer(media);
                            mediaView.setMediaPlayer(mediaPlayer);
                            play();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void setOnKeyboardPressed(){
        playSetOnKeyPress();
        pauseSetOnKeyPress();
        stopSetOnKeyPress();
        skip30SSetOnKeyPress();
        back30SSetOnKeyPress();
        upVolumeSetOnKeyPress();
        downVolumeSetOnKeyPress();
    }

    private void pauseSetOnKeyPress(){
        pauseBTN.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.PAUSE), new Runnable() {
            @Override
            public void run() {
                pauseBTN.fire();
            }
        });
    }

    private void playSetOnKeyPress(){
        playBTN.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.P), new Runnable() {
            @Override
            public void run() {
                playBTN.fire();
            }
        });
    }

    private void stopSetOnKeyPress(){
        stopBTN.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S), new Runnable() {
            @Override
            public void run() {
                stopBTN.fire();
            }
        });
    }

    private void skip30SSetOnKeyPress(){
        skip30BTN.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT), new Runnable() {
            @Override
            public void run() {
                skip30BTN.fire();
            }
        });
    }

    private void back30SSetOnKeyPress(){
        back30BTN.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT), new Runnable() {
            @Override
            public void run() {
                back30BTN.fire();
            }
        });
    }

    private void upVolumeSetOnKeyPress(){
        volumeSlider.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.UP), new Runnable() {
            @Override
            public void run() {
                volumeSlider.setValue((mediaPlayer.getVolume() * 100) + 5);
                volumeSlider.valueProperty().addListener(observable -> {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                    volumePercent.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            String volPer = String.valueOf((int)volumeSlider.getValue());
                            return volPer + "%";
                        }
                    }));
                });
            }
        });
    }

    private void downVolumeSetOnKeyPress(){
        volumeSlider.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN), new Runnable() {
            @Override
            public void run() {
                volumeSlider.setValue((mediaPlayer.getVolume() * 100) - 5);
                volumeSlider.valueProperty().addListener(observable -> {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                    volumePercent.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            String volPer = String.valueOf((int)volumeSlider.getValue());
                            return volPer + "%";
                        }
                    }));
                });
            }
        });
    }

}
