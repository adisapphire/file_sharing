/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_sharing;

import file_sharing.Server_Main;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author rockstar
 */



class ClientHandler implements Runnable 
		{
                    
		    Scanner scn = new Scanner(System.in);
		    private String name;
		     DataInputStream dis;
		     DataOutputStream dos;
		    Socket s=null;
                    String file_recv="";
		    boolean isloggedin;
		     
		    // constructor
		    public ClientHandler(Socket s, String name,
		                            DataInputStream dis, DataOutputStream dos) {
		        this.dis = dis;
		        this.dos = dos;
		        this.name = name;
		        this.s = s;
		        this.isloggedin=true;
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
                    
                    
		    @Override
		    public void run() {
                        
		        String received;
				try{
                                    
				for (ClientHandler mc : chat_window.ar) 
		                {
		                    // if the recipient is found, write on its
		                    // output stream
                   //dos.write(CreateDataPacket("151".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
		                    if (!mc.name.equals(this.name) && mc.isloggedin==true) 
		                    {
		                        mc.dos.write(CreateDataPacket("141".getBytes("UTF8"), ("\t"+"'"+this.name+"'" + " Joined").getBytes("UTF8")));
		                        
		                    }
		                }
                                for (ClientHandler mc : chat_window.ar) 
		                {
		                    // if the recipient is found, write on its
		                    // output stream
		                    if (mc.isloggedin==true)
		                    {
		                        this.dos.write(CreateDataPacket("141".getBytes("UTF8"), ("\t"+"'"+mc.name+"'" + " Joined").getBytes("UTF8")));
		                        
		                    }
		                }
		            } catch (IOException e) {
		                 
		                e.printStackTrace();
		            }
                                
                                
		        while (true) 
		        {
		            try
		            {
		                
                               
                               if (dis.read() == 2) {
	                        byte[] cmd_buff = new byte[3];
	                        dis.read(cmd_buff, 0, cmd_buff.length);
	                        byte[] recv_buff = ReadStream(dis);
                                
	                        switch (Integer.parseInt(new String(cmd_buff))){
                                    
                                    case 101:
                                           this.file_recv = new String(recv_buff);
                                           StringTokenizer rc = new StringTokenizer(this.file_recv, "@");
                                            String file_name = rc.nextToken();
                                            this.file_recv = rc.nextToken();
                                            String lengt = rc.nextToken();
                                           for (ClientHandler mc : chat_window.ar) 
                                        {
                                           if(mc.name.equals(this.file_recv)){
                                           mc.dos.write(CreateDataPacket("101".getBytes("UTF8"), (file_name + "@"+ this.name+"@"+lengt).getBytes("UTF8")));
                                           mc.dos.flush();
                                                   }
                                        }
                                        break;
                                    case 111:
                                        for (ClientHandler mc : chat_window.ar) 
                                        {
                                            if(mc.name.equals(this.file_recv)){
                                           mc.dos.write(CreateDataPacket("111".getBytes("UTF8"), recv_buff));
                                           mc.dos.flush();
                                                   }
                                        }
                                        break;
                                        case 161:
                                            
                                            this.file_recv = new String(recv_buff);
                                          
                                        for (ClientHandler mc : chat_window.ar) 
                                        {
                                            if(mc.name.equals(this.file_recv)){
                                           mc.dos.write(CreateDataPacket("161".getBytes("UTF8"), recv_buff));
                                           mc.dos.flush();
                                                   }
                                        }
                                        break;
                                    case 121:
                                        for (ClientHandler mc : chat_window.ar) 
                                        {
                                        if(mc.name.equals(this.file_recv)){
                                        mc.dos.write(CreateDataPacket("121".getBytes("UTF8"), recv_buff));
                                        mc.dos.flush();
                                        }
                                        }
                                        break;
                                    case 131:
                                        for (ClientHandler mc : chat_window.ar) 
                                        {
                                            if(mc.name.equals(this.file_recv)){
                                        mc.dos.write(CreateDataPacket("131".getBytes("UTF8"), recv_buff));
                                       mc.dos.flush();
                                        }
                                        }
                                        break;
                                    case 151:
                                        for (ClientHandler mc : chat_window.ar) 
                                        {
                                            if(mc.name.equals(this.file_recv)){
                                        mc.dos.write(CreateDataPacket("151".getBytes("UTF8"), recv_buff));
                                           mc.dos.flush();
                                        }
                                        }
                                        break;
                                case 141:
                                    received=new String(recv_buff);
                                    
                                    if(received.equals("logout")){
                                            this.isloggedin=false;
                                            chat_window.i--;
                                            chat_window.uname = this.name;
                                            for (ClientHandler mc : chat_window.ar){
                                                // if the recipient is found, write on its
                                                // output stream
                                                if (mc.isloggedin==true)
                                                {
                                                    mc.dos.write(CreateDataPacket("141".getBytes("UTF8"), ("\t"+"'"+this.name+"'" + " logeed out").getBytes("UTF8")));
                                                    mc.dos.flush();

                                                }
                                            }



                                            this.s.close();
                                            break;
                                    }
		               
                                    else if(received.contains("#")){
                                        StringTokenizer st = new StringTokenizer(received, " ");
                                        String MsgToSend ="";
                                        String recipient = "";
                                        while (st.hasMoreTokens()){
                                            String token = st.nextToken();
                                            System.out.println(token);
                                            if(token.indexOf('#')>=0){
                                                System.out.println("******  ha aa gya # ********");
                                                StringBuilder msg = new StringBuilder (token);
                                                msg.deleteCharAt(0);
                                                MsgToSend+=msg.toString();
                                            }
//                                            else{
                                                recipient+=token+" ";
//                                            }
                                        }

                                            for (ClientHandler mc : chat_window.ar) 
                                            {

                                            if (mc.name.equals(MsgToSend) && mc.isloggedin==true) 
                                                {
                                                  mc.dos.write(CreateDataPacket("141".getBytes("UTF8"), ("#"+MsgToSend+" : "+recipient).getBytes("UTF8")));
                                                   mc.dos.flush();
                                                        break;
                                                }
                                            
                                            }

                                    }
                                    else{ // ar is the vector storing client of active users
                                        for (ClientHandler mc : chat_window.ar){
                                            // if the recipient is found, write on its
                                            // output stream
                                            if (mc.isloggedin==true){
                                                mc.dos.write(CreateDataPacket("141".getBytes("UTF8"), (this.name+" : "+received).getBytes("UTF8")));
                                                mc.dos.flush();
                                             }
                                        }
                                    }
                                    break;
                               }
                               }
		            } catch (IOException e) {
		                 
		                e.printStackTrace();
				break;
		            }
		             if(this.isloggedin==false){
                                    break;
                             }
		        }
		        try
		        {
		            // closing resources
		            this.dis.close();
		            this.dos.close();
		             
		        }catch(IOException e){
		            e.printStackTrace();
				
		        }
		    }
		}
public class chat_window extends javax.swing.JFrame {
static Vector<ClientHandler> ar = new Vector<>();
    static int i=1;
    private Socket          socket   = null;
    private ServerSocket    server   = null;
        static String uname = "";
    public chat_window(ServerSocket    server ,Socket          socket) {
        initComponents();
       
        this.socket = socket;
        this.server = server;
        
        
        
    }
        public void deleteuser(){
                this.User_name.setText(this.User_name.getText().replace("## "+uname+ "\n", ""));
        
        }
    
       
        public void main_chat(){
                   
        
        }
    
        public void serverchat() {
            
			while (true) 
	        {
	            // Accept the incoming request
				try {
	         socket = server.accept();
                 
	            System.out.println("Client accepted");
	 
	            System.out.println("New client request received : " + socket);
	             
	            // obtain input and output streams
	            DataInputStream dis = new DataInputStream(socket.getInputStream());
	            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
	             
	            System.out.println("Creating a new handler for this client...");
	 			String name = dis.readUTF();
                                this.User_name.append("## "+name+ "\n");
                                this.no_of_users.setText(i+"");
                                if(uname!=""){
                                    deleteuser();
                                    
                                }
	            // Create a new handler object for handling this request.
	            ClientHandler mtch = new ClientHandler(socket,name, dis, dos);
	 
	            // Create a new Thread with this object.
	            Thread t = new Thread(mtch);
	             
	            System.out.println("Adding "+name+" to active client list");
                            
	            // add this client to active clients list
	            ar.add(mtch);
	 			
	            // start the thread.
	            t.start();
				}catch(IOException e) {}
	            // increment i for new client.
	            // i is used for naming only, and can be replaced
	            // by any naming scheme
	            
	 i++;
	        }
		}
    /**
     * Creates new form chat_window
     */
    public chat_window() {
        initComponents();
        
        
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
	            
	        }
	        return packet;
	    }
private byte[] ReadStream(DataInputStream dis) {
	        byte[] data_buff = null;
	        try {
	            int b = 0;
	            String buff_length = "";
	            while ((b = dis.read()) != 4) {
	                buff_length += (char) b;
	            }
	            int data_length = Integer.parseInt(buff_length);
	            data_buff = new byte[Integer.parseInt(buff_length)];
	            int byte_read = 0;
	            int byte_offset = 0;
	            while (byte_offset < data_length) {
	                byte_read = dis.read(data_buff, byte_offset, data_length - byte_offset);
	                byte_offset += byte_read;
	            }
	        } catch (IOException ex) {
	            
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        User_name = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        no_of_users = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("#Chat");
        jLabel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jLabel1AncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        User_name.setColumns(20);
        User_name.setRows(5);
        User_name.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                User_namePropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(User_name);

        jLabel2.setText("Users::");

        no_of_users.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                no_of_usersActionPerformed(evt);
            }
        });
        no_of_users.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                no_of_usersPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(no_of_users, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(no_of_users, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jLabel1AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1AncestorAdded

    private void User_namePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_User_namePropertyChange
        // TODO add your handling code here:
        this.User_name.setEditable(false);
    }//GEN-LAST:event_User_namePropertyChange

    private void no_of_usersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_no_of_usersActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_no_of_usersActionPerformed

    private void no_of_usersPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_no_of_usersPropertyChange
        // TODO add your handling code here:
        this.no_of_users.setEditable(false);
    }//GEN-LAST:event_no_of_usersPropertyChange

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
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new chat_window().setVisible(true);
                
                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea User_name;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField no_of_users;
    // End of variables declaration//GEN-END:variables
}
