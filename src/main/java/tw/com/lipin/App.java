package tw.com.lipin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tw.com.lipin.controller.MainController;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public Stage mainStage;
    private Parent mainNode;

    @Override
    public void start(Stage stage) throws Exception {

        mainStage = stage;

        initMainLayout();

        Scene scene = new Scene(mainNode);

        mainStage.initStyle(StageStyle.UTILITY);
        mainStage.centerOnScreen();//打開時在使用這螢幕的正中間
        mainStage.setScene(scene);
        mainStage.setResizable(false);//不能調整大小
        mainStage.show();

    }
    private void initMainLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("mainView.fxml"));
        mainNode = loader.load();

//        MainController controller = loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}