package documentkeeper;

import documentkeeper.db.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
//import javafx.stage.Stage;


public class FXMLDocumentController implements Initializable {

    // Variables

    private final FileChooser myFileChooser = new FileChooser();

    @FXML private final Button btn = new Button();

    @FXML private final TreeView importedFiles = new TreeView();


    // Initialize

    @Override
    public void initialize(java.net.URL url, java.util.ResourceBundle rb) {
        configureFileChooser();
        TreeItem root = new TreeItem("hah");
        importedFiles.setRoot(root);
        loadFilesFromDb();

//        Path p = java.nio.file.Paths.get("C:/Users/Nodebite/Desktop/programmering/skolor/sysjm2/document-keeper/imported-files/615660723");
//        try {
//            Hash.decrypt(java.nio.file.Files.readAllBytes(p).toString());
//        } catch (IOException ex) {
//            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    // Event listeners

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
            db.clear();
        }
        event.setDropCompleted(success);
        event.consume();
    }

    // Methods

    private void loadFilesFromDb(){
        ObservableList children = importedFiles.getRoot().getChildren();
        children.clear();
        ArrayList<Files> allFiles = Files.findAll(Files.class);
        allFiles.forEach((f) -> {
            children.add(new TreeItem(f.name));
        });
    }

    private void importFiles(List<File> files) {
        files.forEach((f)->{

            Path srcPath = Paths.get(f.getPath());
            byte[] data = null;

            try {
                data = java.nio.file.Files.readAllBytes(srcPath);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (data == null) {
                return;
            }

            long checksum = Hash.checksum(data);

            Files file = new Files(f.getName(), checksum);
            boolean didSave = file.save();
            if (didSave) {
                byte[] encryptedFileContent = Hash.stringToBytes(Hash.encrypt(new String(data)));
                Path dstPath = Paths.get("./imported-files/" + checksum);
                saveEncryptedFile(encryptedFileContent, dstPath);
            }
        });
        loadFilesFromDb();
    }
    private void saveEncryptedFile(byte[] encrypted, Path dest) {
        try {
            java.nio.file.Files.write(dest, encrypted, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
    }

    private void saveEncryptedFile(byte[] encrypted, File dest) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(dest);
            os.write(encrypted, 0, encrypted.length);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (os != null) { os.close(); }
            } catch (IOException ex) {}
        }
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
