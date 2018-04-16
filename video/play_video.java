import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.media.*;
 
class MediaPlayerDemo extends JFrame {
   private Player player;
   private File file;
 
   public MediaPlayerDemo()
   {
      super( "Demonstrating the Java Media Player" );
 
      JButton openFile = new JButton( "Open file to play" );
      openFile.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent e )
            {
               openFile();
               createPlayer();
            }
         }
      );
      getContentPane().add( openFile, BorderLayout.NORTH );
   
      setSize( 300, 300 );
      show();
   }
 
   private void openFile()
   {      
      JFileChooser fileChooser = new JFileChooser();
 
      fileChooser.setFileSelectionMode(
         JFileChooser.FILES_ONLY );
      int result = fileChooser.showOpenDialog( this );
 
      // user clicked Cancel button on dialog
      if ( result == JFileChooser.CANCEL_OPTION )
         file = null;
      else
         file = fileChooser.getSelectedFile();
   }
 
   private void createPlayer()
   {
      if ( file == null )
         return;
 
      removePreviousPlayer();
 
      try {
         // create a new player and add listener
         player = Manager.createPlayer( file.toURL() );
         player.addControllerListener( new EventHandler() );
         player.start();  // start player
      }
      catch ( Exception e ){
         JOptionPane.showMessageDialog( this,
            "Invalid file or location", "Error loading file",
            JOptionPane.ERROR_MESSAGE );
      }
   }
 
   private void removePreviousPlayer()
   {
      if ( player == null )
         return;
 
      player.close();
 
      Component visual = player.getVisualComponent();
      Component control = player.getControlPanelComponent();
 
      Container c = getContentPane();
      
      if ( visual != null ) 
         c.remove( visual );
 
      if ( control != null ) 
         c.remove( control );
   }
 
   public static void main(String args[])
   {
      MediaPlayerDemo app = new MediaPlayerDemo();
 
      app.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               System.exit(0);
            }
         }
      );
   }
 
   // inner class to handler events from media player
   private class EventHandler implements ControllerListener {
      public void controllerUpdate( ControllerEvent e ) {
         if ( e instanceof RealizeCompleteEvent ) {
            Container c = getContentPane();
          
            // load Visual and Control components if they exist
            Component visualComponent =
               player.getVisualComponent();
 
            if ( visualComponent != null )
               c.add( visualComponent, BorderLayout.CENTER );
 
            Component controlsComponent =
               player.getControlPanelComponent();
 
            if ( controlsComponent != null )
               c.add( controlsComponent, BorderLayout.SOUTH );
 
            c.doLayout();
         }
      }
   }
}