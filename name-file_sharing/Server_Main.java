/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_sharing;

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


class ClientWorker implements Runnable {

    private Socket target_socket;
    private DataInputStream din;
    private DataOutputStream dout;

    public ClientWorker(Socket recv_socket) {
        try {
            target_socket = recv_socket;
            din = new DataInputStream(target_socket.getInputStream());
            dout = new DataOutputStream(target_socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        RandomAccessFile rw = null;
        long current_file_pointer = 0;
        boolean loop_break = false;
        while (true) {
            byte[] initilize = new byte[1];
            try {
                din.read(initilize, 0, initilize.length);
                if (initilize[0] == 2) {
                    byte[] cmd_buff = new byte[3];
                    din.read(cmd_buff, 0, cmd_buff.length);
                    byte[] recv_data = ReadStream();
                    switch (Integer.parseInt(new String(cmd_buff))) {
                        case 124:
                            rw = new RandomAccessFile("C:\\Users\\avsingh\\Documents\\" + new String(recv_data), "rw");
                            dout.write(CreateDataPacket("125".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
                            dout.flush();
                            break;
                        case 126:
                            rw.seek(current_file_pointer);
                            rw.write(recv_data);                            
                            current_file_pointer = rw.getFilePointer();
                            System.out.println("Download percentage: " + ((float)current_file_pointer/rw.length())*100+"%");
                            dout.write(CreateDataPacket("125".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
                            dout.flush();
                            break;
                        case 127:
                            if ("Close".equals(new String(recv_data))) {
                                loop_break = true;
                            }
                            break;
                    }
                }
                if (loop_break == true) {
                    target_socket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private byte[] ReadStream() {
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
            Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data_buff;
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
            Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return packet;
    }
}
class Server {
			
	
		
		
		
		
		
	
}

public class Server_Main extends javax.swing.JFrame {

    /**
     * Creates new form Server_Main
     */
    public Socket          socket   = null;
    public ServerSocket    server   = null;
    public DataInputStream in       =  null;
    
		public void setupconnection(int port) {
                    System.out.println(port);
			try
	        {
	            this.server = new ServerSocket(port);
	            System.out.println("Server started");
	 
	            System.out.println("Waiting for a client ...");
                       
	            
	        }
	        catch(IOException i)
	        {
	            System.out.println(i);
	        }
		}
                
                public void serverfiletransfer() {
			 while (true) {
	                try {
						new Thread(new ClientWorker(this.server.accept())).start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
		}
    
    public Server_Main() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        portno = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        start_server = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        portno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portnoActionPerformed(evt);
            }
        });

        jLabel1.setText("Port Number");

        start_server.setText("Start");
        start_server.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_serverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(portno, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(start_server)))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(29, 29, 29)
                .addComponent(start_server)
                .addContainerGap(118, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void portnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portnoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portnoActionPerformed

    private void start_serverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start_serverActionPerformed
        // TODO add your handling code here:
        pn = Integer.parseInt(this.portno.getText());
        
              this.setupconnection(pn);
             
              if(this.server!=null){
                  
                  this.hide();
                  
              chat_window obj_chat = new chat_window(this.server,this.socket);
              obj_chat.setVisible(true);
              
              
              }
              
              
    }//GEN-LAST:event_start_serverActionPerformed

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
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server_Main().setVisible(true);
            }
        });
    }
  private int pn;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField portno;
    private javax.swing.JButton start_server;
    // End of variables declaration//GEN-END:variables
}
