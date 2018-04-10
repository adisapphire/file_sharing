/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediaplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

/**
 *
 * @author Asus
 */
public class FXMLDocumentController implements Initializable {
    
    private String filePath;
    private MediaPlayer mdp;
    
    @FXML
    private Label label;
    
    @FXML
    private MediaView mediaView;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
      
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("video and audio extension file please", "*.mp4", "*.mp3", "*.mkv");
        
        fileChooser.getExtensionFilters().add(filter);
        
        File file = fileChooser.showOpenDialog(null);
        
        filePath = file.toURI().toString();
        
        if(filePath != null)
        {
            Media media = new Media(filePath);
            mdp  = new MediaPlayer(media);
            mediaView.setMediaPlayer(mdp);
            mdp.play();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
