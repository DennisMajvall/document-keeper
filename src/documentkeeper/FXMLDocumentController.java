package documentkeeper;

import documentkeeper.db.*;
import java.io.File;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
//import javafx.stage.Stage;


public class FXMLDocumentController implements Initializable {

    private final FileChooser myFileChooser = new FileChooser();

    @FXML private Button btn;

    @FXML private TreeView importedFiles;

    @Override
    public void initialize(java.net.URL url, java.util.ResourceBundle rb) {
        configureFileChooser();
    }

    @FXML private void openImportBrowser(ActionEvent event) {
        importFiles(myFileChooser.showOpenMultipleDialog(Main.stage));
    }

    @FXML private void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = db.hasFiles();
        if (success) {
            importFiles(db.getFiles());
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void importFiles(List<File> files) {
        files.forEach((f)->{
            String s = f.getName();
            String encryptedName = "nope";

            int fileSize = (int)f.length(); // Checksum of file-content is a better choice.


            Files file = new Files(encryptedName, fileSize);
            boolean saveResult = file.save();
            
            if (!saveResult) {
                System.out.println("Already imported:" + s);
            } else {
                System.out.println("importing :" + s);
            }
        });
    }


    private void configureFileChooser() {
        myFileChooser.setTitle("Import documents");
// ./imported-files
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
