package documentkeeper;

import documentkeeper.db.*;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;

    @Override
    public void start(Stage globalStage) throws Exception {
        stage = globalStage;

//        java.sql.ResultSet rs = Db.get().query("SELECT * FROM FIlEs");
//        rs.next();
//        System.out.println(rs.getString("name"));
        ArrayList<Files> s = Files.Find("SELECT * from files", Files.class);
//            Db.get().query("SELECT * from files");
        System.out.println("Files id name and size: " + s.get(1).id + ", " +  s.get(1).name + ", " + s.get(1).bytes);
//        System.out.println("n4: " + s.n4);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = (Parent)loader.load();
//        FXMLDocumentController controller = loader.getController();
//        controller.setStageAndSetupListeners(stage); // or what you want to do

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
