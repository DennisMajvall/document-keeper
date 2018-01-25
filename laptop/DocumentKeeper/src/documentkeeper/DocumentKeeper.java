package documentkeeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DocumentKeeper extends Application {

    public static Stage stage;

    @Override
    public void start(Stage globalStage) throws Exception {
        stage = globalStage;
        new Db();

//        Db.query("SELECT * FROM stuff");
        Stuff s = Stuff.FindById(3, Stuff.class);
        System.out.println("stuff1 id and name: " + s.id + " " +  s.name);
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
