package documentkeeper;

import documentkeeper.db.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

            Path path = Paths.get(f.getPath());
            byte[] data = null;

            try {
                data = java.nio.file.Files.readAllBytes(path);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }

            long checksum = Hash.checksum(data);

            Files file = new Files(f.getName(), checksum);
            file.save();
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
