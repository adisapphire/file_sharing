/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_sharing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

/**
 *
 * @author rockstar
 */







public class User_window extends javax.swing.JFrame {

    /**
     * Creates new form User_window
     */
    public Socket socket            = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    public ServerSocket    server   = null;
    String rcv = "";
    String user;
    public User_window() {
        initComponents();
    }
public User_window(Socket socket ,String user) {
        initComponents();
        
        this.socket=socket;
         this.user = user;
        this.clientchat();
        this.username.setText(user);
       
    }
public void print(){
     this.chat_area.append(rcv+"\n");
}
public void logout(){
                Thread sendMessage = new Thread(new Runnable()
    {
        @Override
        public void run() {
            try {
               
                
                dos.writeUTF("logout");
            } catch (IOException e) {
                e.printStackTrace();
        }}});
        sendMessage.start();
     

}
public void additem(){
            Pattern pattern = Pattern.compile("'(.*?)'");
            Matcher matcher = pattern.matcher(rcv);
            
            if (matcher.find() && !matcher.group(1).equalsIgnoreCase(this.username.getText()))
                {
                    this.users.addItem(matcher.group(1));
    
                }
        
}

public void file_download(){
 new Thread(new ClientWorker(socket,dis,dos)).start();
					

}
	    
public void file_upload(){
    try{
        System.out.println("ok12");
                    dis = new DataInputStream(socket.getInputStream());
	            dos = new DataOutputStream(socket.getOutputStream());
          dos.write(this.CreateDataPacket("124".getBytes("UTF8"), file.getName().getBytes("UTF8")));
                dos.flush();
                System.out.println(this.CreateDataPacket("124".getBytes("UTF8"), file.getName().getBytes("UTF8")));
                RandomAccessFile rw = new RandomAccessFile(file, "r");
                long current_file_pointer = 0;
                boolean loop_break = false;
                System.out.println("ok4");
                while (true) {
                    if (dis.read() == 2) {
                        byte[] cmd_buff = new byte[3];
                        dis.read(cmd_buff, 0, cmd_buff.length);
                        byte[] recv_buff = this.ReadStream(dis);
                        switch (Integer.parseInt(new String(cmd_buff))) {
                            case 125:
                                current_file_pointer = Long.valueOf(new String(recv_buff));
                                int buff_len = (int) (rw.length() - current_file_pointer < 100000 ? rw.length() - current_file_pointer : 100000);
                                byte[] temp_buff = new byte[buff_len];
                                if (current_file_pointer != rw.length()) {
                                    rw.seek(current_file_pointer);
                                    rw.read(temp_buff, 0, temp_buff.length);
                                    dos.write(this.CreateDataPacket("126".getBytes("UTF8"), temp_buff));
                                    dos.flush();
                                    System.out.println("Upload percentage: " + ((float)current_file_pointer/rw.length())*100+"%");
                                } else {
                                    loop_break = true;
                                }
                                break;
                        }
                    }
                    if (loop_break == true) {
                        System.out.println("Stop Server informed");
                        dos.write(this.CreateDataPacket("127".getBytes("UTF8"), "Close".getBytes("UTF8")));
                        dos.flush();
                        socket.close();
                        System.out.println("Client Socket Closed");
                        break;
                    }
                }
    }catch(IOException e){}

}

public void clientchat() {
    try{
    this.dis = new DataInputStream(this.socket.getInputStream());
    this.dos = new DataOutputStream(this.socket.getOutputStream());
    
    // sendMessage thread
    
    // readMessage thread
    Thread sendMessage = new Thread(new Runnable()
    {
        @Override
        public void run() {
            try {
               
                String name = user;
                dos.writeUTF(name);
            } catch (IOException e) {
                e.printStackTrace();
        }}});
        sendMessage.start();
        
    Thread readMessage;
        readMessage = new Thread(new Runnable()
        {
            
            @Override
            
            public void run() {
                
                while (true) {
                    try {
                        // read the message sent to this client
                        rcv = dis.readUTF();
                        if(rcv.contains("Joined")){
                                additem();
                        }
                        else if(rcv.contains("rockstar_you_are_great_downloading")){
                                    file_download();
                        }
                        else if(rcv.contains("rockstar_you_are_great_uploading")){
                                        file_upload();
                                }
                        
                        print();
                        
                    } catch (IOException e) {
                        
                        e.printStackTrace();
                        break;
                    }
                }
            }

        
        });
    
    readMessage.start();
    
    }
    catch(IOException e){}
	}


    private byte[] CreateDataPacket(byte[] cmd, byte[] data) {
	        byte[] packet = null;
	        try {
	            byte[] initialize = new byte[1];
	            initialize[0] = 2;
                    System.out.println(initialize);
	            byte[] separator = new byte[1];
	            separator[0] = 4;
	            byte[] data_length = String.valueOf(data.length).getBytes("UTF8");
	            packet = new byte[initialize.length + cmd.length + separator.length + data_length.length + data.length];

	            System.arraycopy(initialize, 0, packet, 0, initialize.length);
	            System.arraycopy(cmd, 0, packet, initialize.length, cmd.length);
	            System.arraycopy(data_length, 0, packet, initialize.length + cmd.length, data_length.length);
	            System.arraycopy(separator, 0, packet, initialize.length + cmd.length + data_length.length, separator.length);
	            System.arraycopy(data, 0, packet, initialize.length + cmd.length + data_length.length + separator.length, data.length);

	        } catch (UnsupportedEncodingException ex) {
	            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        return packet;
	    }
    private byte[] ReadStream(DataInputStream din) {
        byte[] data_buff = null;
        try {
            int b = 0;
            String buff_length = "";
            while ((b = din.read()) != 4) {
                buff_length += (char) b;
            }
            int data_length = Integer.parseInt(buff_length);
            data_buff = new byte[Integer.parseInt(buff_length)];
            int byte_read = 0;
            int byte_offset = 0;
            while (byte_offset < data_length) {
                byte_read = din.read(data_buff, byte_offset, data_length - byte_offset);
                byte_offset += byte_read;
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data_buff;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        chat_area = new javax.swing.JTextArea();
        chat_text = new javax.swing.JTextField();
        send = new javax.swing.JButton();
        disconnect = new javax.swing.JButton();
        history = new javax.swing.JButton();
        watch_video = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        browse = new javax.swing.JButton();
        file_name_text = new javax.swing.JTextField();
        Send_file = new javax.swing.JButton();
        users = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        chat_area.setColumns(20);
        chat_area.setRows(5);
        chat_area.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                chat_areaInputMethodTextChanged(evt);
            }
        });
        chat_area.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chat_areaPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(chat_area);

        chat_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chat_textActionPerformed(evt);
            }
        });

        send.setText("Send");
        send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendActionPerformed(evt);
            }
        });

        disconnect.setText("Disconnect");
        disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectActionPerformed(evt);
            }
        });

        history.setText("History");
        history.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyActionPerformed(evt);
            }
        });

        watch_video.setText("Watch Video");
        watch_video.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                watch_videoActionPerformed(evt);
            }
        });

        jLabel1.setText("Username::");

        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });
        username.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                usernamePropertyChange(evt);
            }
        });

        browse.setText("Browse");
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseActionPerformed(evt);
            }
        });

        Send_file.setText("Send");
        Send_file.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Send_fileActionPerformed(evt);
            }
        });

        users.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));
        users.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                usersItemStateChanged(evt);
            }
        });
        users.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(261, Short.MAX_VALUE)
                        .addComponent(users, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Send_file)
                        .addGap(65, 65, 65)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(disconnect, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chat_text, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(send))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(history)
                        .addGap(31, 31, 31)
                        .addComponent(watch_video)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addComponent(file_name_text)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browse)
                .addGap(215, 215, 215))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(history)
                            .addComponent(watch_video))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chat_text, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(send, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(disconnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(browse)
                            .addComponent(file_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Send_file)
                            .addComponent(users, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(289, 289, 289))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendActionPerformed
        // TODO add your handling code here:
        msg=this.chat_text.getText();
        chat_text.setText("");
       
    
        Thread sendMessage = new Thread(new Runnable()
    {
        @Override
        public void run() {
            /*
            }*/
            
            
                
                // read the message to deliver.
                
                
                try {
                    // write on the output stream
                    if(msg!=null){
                    dos.writeUTF(msg);
                   
                    
                    
                   
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    
                }
               
            
        }
    });
         sendMessage.start();
        
    }//GEN-LAST:event_sendActionPerformed

    private void chat_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chat_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chat_textActionPerformed

    private void disconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectActionPerformed
        
            // TODO add your handling code here:
            this.logout();
            
       
        
        
        System.exit(0);
    }//GEN-LAST:event_disconnectActionPerformed

    private void watch_videoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_watch_videoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_watch_videoActionPerformed

    private void historyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyActionPerformed
        // TODO add your handling code here:
        hi h = new hi();
        h.setVisible(true);
    }//GEN-LAST:event_historyActionPerformed

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        // TODO add your handling code here:
        
        
    }//GEN-LAST:event_usernameActionPerformed

    private void chat_areaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chat_areaPropertyChange
        // TODO add your handling code here:
      this.chat_area.setEditable(false);
    }//GEN-LAST:event_chat_areaPropertyChange

    private void chat_areaInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_chat_areaInputMethodTextChanged
        // TODO add your handling code here:
        
    }//GEN-LAST:event_chat_areaInputMethodTextChanged

    private void usernamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_usernamePropertyChange
        // TODO add your handling code here:
        this.username.setEditable(false);
        
    }//GEN-LAST:event_usernamePropertyChange

    private void usersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usersActionPerformed

    private void usersItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_usersItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_usersItemStateChanged

    private void browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseActionPerformed
        // TODO add your handling code here:
         JFileChooser jf = new JFileChooser();
                    int aa  = jf.showOpenDialog(null);
                    System.out.println(aa);
                    if(aa==JFileChooser.APPROVE_OPTION){
                        char cbuf [] = null;
                        file = jf.getSelectedFile();
                        file_name_text.setText(file.getAbsolutePath());
                        
                    }
    }//GEN-LAST:event_browseActionPerformed

    private void Send_fileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Send_fileActionPerformed
        // TODO add your handling code here:
         Thread sendMessage = new Thread(new Runnable()
    {
        @Override
        public void run() {
            try {
               
                
                dos.writeUTF("rockstar_you_are_great_uploading");
                System.out.println("ok1");
            } catch (IOException e) {
                e.printStackTrace();
        }}});
        sendMessage.start();
     
        Thread t1 = new Thread( new Runnable(){
                        
                        public void run(){
                                  file_upload();
                        }
                        
                        }
                        
                        
                        );
    }//GEN-LAST:event_Send_fileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(User_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(User_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(User_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(User_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new User_window().setVisible(true);
            }
        });
    }
    private String msg;
    private File file;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Send_file;
    private javax.swing.JButton browse;
    private javax.swing.JTextArea chat_area;
    private javax.swing.JTextField chat_text;
    private javax.swing.JButton disconnect;
    private javax.swing.JTextField file_name_text;
    private javax.swing.JButton history;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton send;
    private javax.swing.JTextField username;
    private javax.swing.JComboBox<String> users;
    private javax.swing.JButton watch_video;
    // End of variables declaration//GEN-END:variables
}
