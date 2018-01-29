package documentkeeper;

import documentkeeper.db.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Main extends Application {

    public static Stage stage;

    @Override
    public void start(Stage globalStage) throws Exception {
        stage = globalStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = (Parent)loader.load();

//        FXMLDocumentController controller = loader.getController();
//        controller.init(); // or whatever you want to do

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }



    // WIP 2-way encryption
//    public static String encrypt(String strClearText, String strKey) throws Exception{
//        String strData="";
//
//        try {
//            SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(),"Blowfish");
//            Cipher cipher = Cipher.getInstance("Blowfish");
//            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
//            byte[] encrypted=cipher.doFinal(strClearText.getBytes());
//            strData=new String(encrypted);
//        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
//        return strData;
//    }

//    public static String decrypt(String strEncrypted,String strKey) throws Exception{
//        String strData="";
//
//        try {
//            SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
//            Cipher cipher=Cipher.getInstance("Blowfish");
//            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
//            byte[] decrypted=cipher.doFinal(strEncrypted.getBytes());
//            strData=new String(decrypted);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new Exception(e);
//        }
//        return strData;
//    }


}
