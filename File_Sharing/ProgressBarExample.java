/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_sharing;

/**
 *
 * @author avsingh
 */
import javax.swing.*;    
public class ProgressBarExample extends JFrame{    
    JProgressBar jb;    
    int num=0;     
    ProgressBarExample(){    
        jb=new JProgressBar(0,2000);    
        jb.setBounds(40,40,160,30);         
        jb.setValue(0);    
        jb.setStringPainted(true);    
        add(jb);    
        setSize(250,150);    
        setLayout(null);    
    }    
    public void iterate(long i){    
                
          try{jb.setValue((int)i);}catch(Exception e){}    
     }    

    void iterate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
   
}   