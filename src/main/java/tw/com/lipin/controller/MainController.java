package tw.com.lipin.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollBar;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import tw.com.lipin.App;
import tw.com.lipin.model.MainModel;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class MainController implements Initializable {

    public MainModel mainModel;
    public JFXButton openFileButton;
    public JFXProgressBar fileProgressBar;
    public JFXButton starButton;
    public VBox fileDataVBox;
    public ScrollBar dataScrollBar;
    public AnchorPane taskListPane;
    private List<File> indexFileUriList = new ArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainModel = new MainModel();
        mainModel.setDirectory("./");//初始用戶點擊選擇器時的路徑

        starButton.setDisable(true);//初始禁用執行按鈕

        dataScrollBar.setVisibleAmount(30);

        dataScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                fileDataVBox.setLayoutY(-(t1.doubleValue() * (fileDateBoxHeight / 100)));
            }
        });
    }

    Double fileDateBoxHeight;
    private void fileDataVBoxEvent() {
        fileDateBoxHeight = 0.0;
        System.out.println("高度：" + taskListPane.getHeight());
        fileDataVBox.setAlignment(Pos.TOP_CENTER);
        fileDataVBox.getChildren().addAll();
        fileDataVBox.setPadding(new Insets(taskListPane.getHeight() + 20, 80, 50, 100));
        fileDataVBox.setSpacing(20);
        for (File fileAddress : indexFileUriList) {

            String directory = "null";

            String[] filetoArray = fileAddress.toString().split("/");

            //抓出有game命名的資料夾
            for (String fileName : filetoArray) {
                fileName = fileName.toLowerCase();
                if (fileName.contains("game")){
                    directory = fileName;
                }
            }

            fileDataVBox.getChildren().add(addStackPane(fileAddress.toString(),directory));
        }

        //抓取當前總共會有的高度,讓scrollBar能確定要拉取的高度
        fileDataVBox.getChildren().forEach(new Consumer<Node>() {
            @Override
            public void accept(Node node) {
                StackPane stackPane = (StackPane) node;
                fileDateBoxHeight += stackPane.getMinHeight()+10;
            }
        });
        fileDateBoxHeight = fileDateBoxHeight + 40;
        System.out.println(fileDateBoxHeight);
    }

    //建立一個StackPane,目的勢將有抓出index.php的檔案資料顯示給用戶看
    private StackPane addStackPane(String fileAddress,String directory) {
        StackPane stackPane = new StackPane();//底部stackPane
        stackPane.setMinHeight(110);

        //添加刪除的按鈕icon
        URL imgUrl = App.class.getResource("images/trashIcon.png");
        Image img = new Image(imgUrl.toExternalForm());
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(60);
        imgView.setFitWidth(60);

        //能刪除該資料的按鈕
        JFXButton deleteBtn = new JFXButton("", imgView);
        deleteBtn.setFont(new Font(16));
        deleteBtn.setPrefWidth(80);
        deleteBtn.setPrefHeight(110);
        deleteBtn.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), null)));
        deleteBtn.setFocusTraversable(false);

        HBox hBox = new HBox();//底層
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setBackground(new Background(new BackgroundFill(Color.WHEAT, new CornerRadii(10), null)));
        hBox.getChildren().addAll(deleteBtn);

        VBox vBox2 = new VBox();//上層
        vBox2.setAlignment(Pos.CENTER);

        Label directoryLabel = new Label(directory);//顯示該檔案game資料夾名
        directoryLabel.setFont(Font.font(null, FontWeight.BOLD, 18));
        directoryLabel.setTextFill(Color.valueOf("#FFE153"));

        Label label = new Label(fileAddress);//顯示該檔案位置
        label.setFont(Font.font(null, FontWeight.BOLD, 16));
        label.setTextFill(Color.valueOf("#8CEA00"));
        label.setWrapText(true);//自動換行

        vBox2.setBackground(new Background(new BackgroundFill(Color.valueOf("#272727"), new CornerRadii(10), null)));
        vBox2.setPadding(new Insets(0, 15, 0, 15));
        vBox2.getChildren().addAll(directoryLabel,label);
        vBox2.setSpacing(5);


        stackPane.getChildren().addAll(hBox, vBox2);
        stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf("000"), new CornerRadii(20), null)));

        //設置陰影效果
        DropShadow ds = new DropShadow();
        ds.setColor(Color.valueOf("#ADADAD"));
        ds.setOffsetX(3);
        ds.setOffsetY(5);
        ds.setRadius(10);
        ds.setSpread(0.3);
        stackPane.setEffect(ds);

        hBoxEvent(vBox2);
        deleteBtnButtonEvent(deleteBtn, stackPane);

        return stackPane;
    }

    /**
     * 用戶點擊刪除按鈕事件時
     *
     * @param button
     * @param stackPane
     */
    private void deleteBtnButtonEvent(JFXButton button, StackPane stackPane) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                TranslateTransition tt = new TranslateTransition();
                tt.setToX(-fileDataVBox.getWidth());
                tt.setDuration(Duration.seconds(0.5));
                tt.setNode(stackPane);
                tt.play();

                tt.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {

                        stackPane.getChildren().forEach(new Consumer<Node>() {
                            @Override
                            public void accept(Node node) {
                                if (node instanceof VBox) {
                                    VBox vBox = (VBox) node;
                                    vBox.getChildren().forEach(new Consumer<Node>() {
                                        @Override
                                        public void accept(Node node) {
                                            if (node instanceof Label) {
                                                Label label = (Label) node;
                                                String text = label.getText();
                                                for (int i = 0; i < indexFileUriList.size(); i++) {
                                                    if (indexFileUriList.get(i).toString().equals(text)) {
                                                        indexFileUriList.remove(i);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        fileDataVBox.getChildren().remove(stackPane);
                    }
                });
            }
        });
    }

    /**
     * 監聽頂層hbox是否有被點擊,如果有,將讓他往右位移40px
     *
     * @param vBox
     */
    private void hBoxEvent(VBox vBox) {
        final Boolean[] hBoxBoolean = {false};

        vBox.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Glow glow = new Glow();
                vBox.setEffect(glow);
            }
        });

        vBox.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                vBox.setEffect(null);
            }
        });

        vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hBoxBoolean[0] = !hBoxBoolean[0];

                DisplacementMap ds = new DisplacementMap();
                vBox.setEffect(ds);

                TranslateTransition tt = hboxAnimation(hBoxBoolean[0], vBox);
                tt.play();
            }
        });
    }

    /**
     * 跑動畫,讓頂部hbx往右位移
     *
     * @return
     */
    private TranslateTransition hboxAnimation(Boolean hBoxSwitch, VBox vBox) {
        TranslateTransition tt = new TranslateTransition();
        if (hBoxSwitch) {
            tt.setToX(vBox.getLayoutX() - 70);
        } else {
            tt.setToX(0);
        }
        tt.setDuration(Duration.seconds(0.5));
        tt.setNode(vBox);

        return tt;
    }

    /**
     * 資料夾選擇按鈕
     *
     * @param actionEvent
     */
    boolean buttonEvent = true;

    public void openFileEvent(ActionEvent actionEvent) {

        indexFileUriList.clear();//清空存放的陣列

        //如果有監聽到按鈕事件,將暫時將openFile案紐關閉
        if (buttonEvent) openFileButton.setDisable(true);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(mainModel.getDirectory()));
        Stage stage = new Stage();

        Timeline timeline = new Timeline(new KeyFrame(

                Duration.millis(16),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {

                        File file = directoryChooser.showDialog(stage);

                        //當每次打開文件選擇器的時候

                        fileProgressBar.setProgress(0);//將進度條的par歸0
                        starButton.setDisable(true);//將右邊star按鈕禁用

                        //文件選擇器打開後,在打開openFile的按鈕監聽
                        openFileButton.setDisable(false);
                        fileDataVBox.getChildren().clear();
                        if (file != null) {
                            mainModel.setDirectory(file.toString());//更新用戶點擊的路徑
                            fileSaveInList(file);
                            squenceIndexFileUriList();//將list重新排序

                            if (indexFileUriList.size() >= 1) {
                                fileProgressBar.setProgress(1);//進度條讓他完成
                                starButton.setDisable(false);
                                fileDataVBoxEvent();
                            } else {
                                fileProgressBar.setProgress(0);//進度條讓他歸0
                            }
                        }
                    }
                }
        ));
        timeline.play();

    }

    /**
     * 抓取該資料夾下所有index開頭,副檔名為.php的檔案
     * 加入 indexFileUriList 中
     */
    private void fileSaveInList(File mainFile) {
        fileProgressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        File rootFile = mainFile;//拿取使用者點擊的路徑

        File[] files = rootFile.listFiles();//查詢該目錄下的所有檔案
        if (files != null) {
            for (File file : files) {
                String[] fileNames = file.toString().split("/");
                //抓出該url路徑下的檔案名稱
                String fileName = fileNames[fileNames.length - 1];

                //找尋index.php 該文件副檔名,如果檔案名長度大於9才能尋訪
                if (fileName.length() > 9) {
                    String checkFileName = fileName.substring(fileName.length() - 4);

                    //檢查檔案名如果開頭是index,結尾是.php,就加入ArrayList中
                    if (fileName.substring(0, 5).equals("index") && checkFileName.equals(".php")) {
                        indexFileUriList.add(file);
                    }
                }
                //如果該file路徑是資料夾的話,就繼續重複尋訪該資料夾下的資料
                if (file.isDirectory()) {
                    fileSaveInList(file);
                }

            }
        }
    }
    /**
     * 排序indexFileUriList讓他有小至大排序
     */
    LinkedList<File> list = new LinkedList<>();
    private void squenceIndexFileUriList(){
        list.clear();
        for (int i =0 ; i<indexFileUriList.size() ; i++){
            String[] gameNames = indexFileUriList.get(i).toString().split("/");
            int gameNumber = 0;
            for (String name :gameNames){
                name = name.toLowerCase();
                if (name.contains("game")){
                    gameNumber = Integer.parseInt(name.replace("game",""));
                    break;
                }
            }
            if (i>0){
                for (int j = list.size()-1; j>=0 ; j-- ){
                    String[] gameNames2 = list.get(j).toString().split("/");
                    int gameNumber2 = 0;
                    for (String name :gameNames2){
                        name = name.toLowerCase();
                        if (name.contains("game")){
                            gameNumber2 = Integer.parseInt(name.replace("game",""));
                            break;
                        }
                    }
                    if (gameNumber>=gameNumber2){
                        list.remove(indexFileUriList.get(i));
                        list.add(j+1,indexFileUriList.get(i));
                        break;
                    }else if (j==0&& gameNumber<=gameNumber2){
                        list.add(0,indexFileUriList.get(i));
                    }else {
                        System.out.println("有錯誤？"+gameNumber);
                    }
                }
                System.out.println(list.toString());
            }else {
                list.add(indexFileUriList.get(i));
            }
        }
        indexFileUriList.clear();
        indexFileUriList.addAll(list);
        System.out.println(indexFileUriList.size());
    }

    /**
     * 開始執行按鈕
     */
    ArrayList<String> fileTextToList = new ArrayList<>();
    public void StartEvent(ActionEvent actionEvent) throws IOException {
        fileProgressBar.setProgress(0);
        double progressValue =  ((double)indexFileUriList.size()-1)/100;

        double value = 0;
        //讀取有index(MD5).php的檔案
        for (File fileName : indexFileUriList) {
            value += progressValue;
            inputData(fileName);
            outputData(fileName);
            fileProgressBar.setProgress(value);
        }
        fileDataVBox.getChildren().clear();

    }

    /**
     * 讀取資料
     *
     * @param fileName
     */
    private void inputData(File fileName) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String fileData = null;
            while ((fileData = reader.readLine()) != null) {
                if (fileData.contains("<?php include('./index")) {
                    fileTextToList.add("<?php include('./index.html') ?>");
                } else {
                    fileTextToList.add(fileData);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader.close();
    }

    /**
     * 將新資料輸出
     */
    private void outputData(File fileName) throws IOException {
        String[] file = fileName.toString().split("/");
        String newFile = "";
        for (int i =0 ; i<file.length-1 ;i++){
            if (!file[i].trim().isEmpty())
                newFile += "/"+file[i];
        }
        newFile +="/index.php";
        System.out.println("輸出位置"+newFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));

        for (String text:fileTextToList){
            writer.write(text);
            writer.newLine();
        }
        writer.flush();
        writer.close();
        fileName.delete();//刪除該位置的檔案
        fileTextToList.clear();
    }
}
