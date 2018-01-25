package documentkeeper;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
//import javafx.stage.Stage;


public class FXMLDocumentController implements Initializable {

    private final FileChooser myFileChooser = new FileChooser();

    @FXML
    private Button btn;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");

        myFileChooser.showOpenMultipleDialog(DocumentKeeper.stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureFileChooser();
        System.out.println("initialized");
    }


    private void configureFileChooser() {
        myFileChooser.setTitle("Import documents");

        myFileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );

        myFileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Common files", "*.pdf", "*.txt"),
            new FileChooser.ExtensionFilter("Word", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("All files", "*.*")
        );
    }

}
