package file_sharing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

public class User_window extends javax.swing.JFrame {
    public Socket socket            = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    public ServerSocket    server   = null;
    String rcv = "";
    String user;
    RandomAccessFile rw;
    String fn;
    String file_recv="";
    long current_file_pointer_read = 0;
    long current_file_pointer_write = 0;
    long length;
    Logger logger;
    FileHandler fileHandler;
    public User_window() {
        initComponents();
    }
    public User_window(Socket socket ,String user,Logger logger,FileHandler fileHandler) {
        initComponents();
        this.socket=socket;
        this.user = user;
        this.logger = logger;
        this.fileHandler = fileHandler;
        this.username.setText(user); 
    }
    public void print(){
         this.chat_area.append(rcv+"\n");
    }
    public void logout(){
        Thread sendMessage = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                   
                    dos.write(CreateDataPacket("141".getBytes("UTF8"), "logout".getBytes("UTF8")));
                    dos.flush();
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


        public void datatransferread(){
                try {
                    dis = new DataInputStream(this.socket.getInputStream());
                    dos = new DataOutputStream(this.socket.getOutputStream());
                    Thread t = new Thread(new Runnable(){
                        public void run(){
                            try {                       
                                String name = user;
                                dos.writeUTF(name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t.start();
                    Thread readMessage = new Thread(new Runnable()
                    {
                        
                        @Override
                        
                        public void run() {
                            try{
                                
                                
                               
                                while (true) {
                                    if (dis.read() == 2) {
                                        byte[] cmd_buff = new byte[3];
                                        dis.read(cmd_buff, 0, cmd_buff.length);
                                        byte[] recv_buff = ReadStream(dis);
                                        switch (Integer.parseInt(new String(cmd_buff))){
                                            case 101:
                                                    recieve_popup r = new recieve_popup();
                                                    r.setVisible(true);
                                                    file_recv = new String(recv_buff);
                                                    StringTokenizer rc = new StringTokenizer(file_recv, "@");
                                                    String file_name = rc.nextToken();
                                                    file_recv = rc.nextToken();
                                                    length=Long.parseLong(rc.nextToken());
                                                    
                                                    r.msg.setText(file_name+"------->>>>"+file_recv);
                                                    logger.addHandler(fileHandler);
                                                    SimpleFormatter formatter = new SimpleFormatter();
                                                    fileHandler.setFormatter(formatter);
                                                    Calendar cal = Calendar.getInstance();
                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                                    String strDate = sdf.format(cal.getTime());
                                                    System.out.println("Current date in String Format: " + strDate);

                                                    if (logger.isLoggable(Level.INFO)) {
                                                        logger.info(" "+file_name+"  "+strDate+" am  "+"recieve"+"  "+file_recv);
                                                        System.out.println("sender **************   actiuon");
                                                    }
                                                    Thread q = new Thread(new Runnable(){
                                                    
                                                        public void run(){
                                                    while(true){
        //                                                System.out.println(r.k);
                                                    if(r.k==1){
                                                        
                                                            try {
                                                                dos.write(CreateDataPacket("161".getBytes("UTF8"), file_recv.getBytes("UTF8")));
                                                                dos.flush();
                                                            } catch (IOException ex) {
                                                                Logger.getLogger(User_window.class.getName()).log(Level.SEVERE, null, ex);
                                                            }
                                                           fn= r.folder.toString();
                                                           if(fn.charAt(fn.length()-1)!='\\')
                                                               fn=fn+"\\";
                                                           
                                                            break;
                                                        
                                                    }
                                                    if (r.k==2){
                                                        break;
                                                    }
                                                    }
                                                        }
                                                    });
                                                    q.start();
                                                break;
                                            case 111:
                                                
                                                rw = new RandomAccessFile(fn + new String(recv_buff), "rw");
                                                dos.write(CreateDataPacket("151".getBytes("UTF8"), String.valueOf(current_file_pointer_read).getBytes("UTF8")));
                                                
                                                dos.flush();
                                                break;
                                            case 121:
                                                rw.seek(current_file_pointer_read);
                                                rw.write(recv_buff);
                                                current_file_pointer_read = rw.getFilePointer();
                                                progressionbar1.RenderProgress(((float)current_file_pointer_read/length)*100);
                                                System.out.println("Download percentage: " + ((float)current_file_pointer_read/length)*100+"%");
                                                dos.write(CreateDataPacket("151".getBytes("UTF8"), String.valueOf(current_file_pointer_read).getBytes("UTF8")));
                                                dos.flush();
                                                break;
                                            case 131:
                                                if ("Close".equals(new String(recv_buff))) {
                                                    rw.close();
                                                    current_file_pointer_read=0;
                                                }
                                                break;
                                            case 141:
                                                rcv= new String(recv_buff);
                                                if(rcv.contains("Joined")){
                                                    additem();
                                                }
                                                print();
                                                break;
                                            case 151:
                                                current_file_pointer_write = Long.valueOf(new String(recv_buff));
                                                System.out.println(current_file_pointer_write);
                                                
                                                progressionbar1.RenderProgress(((float)current_file_pointer_write/length)*100);
                                                     
                                                       
                                                int buff_len = (int) (rw.length() - current_file_pointer_write < 20000 ? rw.length() - current_file_pointer_write : 20000);
                                                byte[] temp_buff = new byte[buff_len];
                                                if (current_file_pointer_write != rw.length()) {
                                                    rw.seek(current_file_pointer_write);
                                                    rw.read(temp_buff, 0, temp_buff.length);
                                                    dos.write(CreateDataPacket("121".getBytes("UTF8"), temp_buff));
                                                    dos.flush();
                                                    System.out.println("Upload percentage: " + ((float)current_file_pointer_write/length)*100+"%");
                                                } else {
                                                    dos.write(CreateDataPacket("131".getBytes("UTF8"), "Close".getBytes("UTF8")));
                                                    dos.flush();
                                                    current_file_pointer_write=0;
                                                    
                                                }
                                                break;
                                            case 161:
                                                try {
                                                        
                                                    rw = new RandomAccessFile(file, "r");
                                                    dos.write(CreateDataPacket("111".getBytes("UTF8"), file.getName().getBytes("UTF8")));
                                                    dos.flush();
                                                    } catch (IOException ex) {
                                                    Logger.getLogger(User_window.class.getName()).log(Level.SEVERE, null, ex);
                                                    }
                                                break;
                                                
                                        }
                                    }
                                    
                                }
                            }
                            catch(IOException e){}
                            
                        }
                        
                        
                    });
                    
                    readMessage.start();
                } catch (IOException ex) {
                    Logger.getLogger(User_window.class.getName()).log(Level.SEVERE, null, ex);
                }
                             
                             
    }

                       




    private byte[] CreateDataPacket(byte[] cmd, byte[] data) {
	        byte[] packet = null;
	        try {
	            byte[] initialize = new byte[1];
	            initialize[0] = 2;
                    
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
    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopupMenu2 = new javax.swing.JPopupMenu();
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
        progressionbar1 = new file_sharing.Progressionbar();

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

        file_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                file_name_textActionPerformed(evt);
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

        javax.swing.GroupLayout progressionbar1Layout = new javax.swing.GroupLayout(progressionbar1);
        progressionbar1.setLayout(progressionbar1Layout);
        progressionbar1Layout.setHorizontalGroup(
            progressionbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 236, Short.MAX_VALUE)
        );
        progressionbar1Layout.setVerticalGroup(
            progressionbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(progressionbar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(users, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(Send_file)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
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
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(file_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(browse)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(disconnect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(file_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browse))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(history)
                            .addComponent(watch_video)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(users, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Send_file))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(progressionbar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chat_text, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(send, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        pack();
    }
    private void sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendActionPerformed
        msg=this.chat_text.getText();
        chat_text.setText("");
                try {
                    if(!msg.equalsIgnoreCase("")){
                    
                   
                  dos.write(CreateDataPacket("141".getBytes("UTF8"), msg.getBytes("UTF8")));
                      dos.flush();
                   
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    
                }
               
            
        

    }
    private void chat_textActionPerformed(java.awt.event.ActionEvent evt) {

    }
    private void disconnectActionPerformed(java.awt.event.ActionEvent evt) {
        this.logout();
        System.exit(0);
    }
    private void watch_videoActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void historyActionPerformed(java.awt.event.ActionEvent evt) {
        hi h = new hi();
        h.setVisible(true);
    }
    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {        
        
    }

    private void chat_areaPropertyChange(java.beans.PropertyChangeEvent evt) {
      this.chat_area.setEditable(false);
    }

    private void chat_areaInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        
    }
    private void usernamePropertyChange(java.beans.PropertyChangeEvent evt) {
        this.username.setEditable(false);
        
    }

    private void usersActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void usersItemStateChanged(java.awt.event.ItemEvent evt) {

    }

    private void browseActionPerformed(java.awt.event.ActionEvent evt) {
         JFileChooser jf = new JFileChooser();
                    int aa  = jf.showOpenDialog(null);
                    System.out.println(aa);
                    if(aa==JFileChooser.APPROVE_OPTION){
                        char cbuf [] = null;
                        file = jf.getSelectedFile();
                        file_name_text.setText(file.getAbsolutePath());
                    }
    }

    private void Send_fileActionPerformed(java.awt.event.ActionEvent evt) {
        try {

            length=file.length();
            String s= file.getName()+"@"+ users.getSelectedItem().toString()+"@"+file.length();
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String strDate = sdf.format(cal.getTime());
            System.out.println("Current date in String Format: " + strDate);
            
            if (logger.isLoggable(Level.INFO)) {
                logger.info(" "+file.getName()+"  "+strDate+" pm  send  "+this.user);
            }

            dos.write(CreateDataPacket("101".getBytes("UTF8"), s.getBytes("UTF8")));
            
            dos.flush();
        } catch (IOException ex) {
            Logger.getLogger(User_window.class.getName()).log(Level.SEVERE, null, ex);
        }
                
                
        
    }//GEN-LAST:event_Send_fileActionPerformed

    private void file_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_file_name_textActionPerformed
        // TODO add your handling code here:
    }
    public static void main(String args[]) {

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
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private file_sharing.Progressionbar progressionbar1;
    private javax.swing.JButton send;
    private javax.swing.JTextField username;
    private javax.swing.JComboBox<String> users;
    private javax.swing.JButton watch_video;
    // End of variables declaration//GEN-END:variables
}
