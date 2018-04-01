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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import javax.swing.JFileChooser;

import javax.swing.JFileChooser;

/**
 *
 * @author rockstar
 */


 class Client {
	

}
public class Client_Main extends javax.swing.JFrame {

    public Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
	public void setupconnection(String ip,int port) {
         
            
	try
        {
            this.socket = new Socket(ip, port);
            System.out.println("Connection Established!");
            
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
	}
	
	
	
	public void serverfiletransfer() {
		 try {
	            
	            
	            input = new DataInputStream(socket.getInputStream());
	            out = new DataOutputStream(socket.getOutputStream());
	            JFileChooser jfc = new JFileChooser();
	            int dialog_value = jfc.showOpenDialog(null);
	            if (dialog_value == JFileChooser.APPROVE_OPTION) {
	                File target_file = jfc.getSelectedFile();
	                out.write(this.CreateDataPacket("124".getBytes("UTF8"), target_file.getName().getBytes("UTF8")));
	                out.flush();
	                RandomAccessFile rw = new RandomAccessFile(target_file, "r");
	                long current_file_pointer = 0;
	                boolean loop_break = false;
	                while (true) {
	                    if (input.read() == 2) {
	                        byte[] cmd_buff = new byte[3];
	                        input.read(cmd_buff, 0, cmd_buff.length);
	                        byte[] recv_buff = this.ReadStream(input);
	                        switch (Integer.parseInt(new String(cmd_buff))) {
	                            case 125:
	                                current_file_pointer = Long.valueOf(new String(recv_buff));
	                                int buff_len = (int) (rw.length() - current_file_pointer < 20000 ? rw.length() - current_file_pointer : 20000);
	                                byte[] temp_buff = new byte[buff_len];
	                                if (current_file_pointer != rw.length()) {
	                                    rw.seek(current_file_pointer);
	                                    rw.read(temp_buff, 0, temp_buff.length);
	                                    out.write(this.CreateDataPacket("126".getBytes("UTF8"), temp_buff));
	                                    out.flush();
	                                    System.out.println("Upload percentage: " + ((float)current_file_pointer/rw.length())*100+"%");
	                                } else {
	                                    loop_break = true;
	                                }
	                                break;
	                        }
	                    }
	                    if (loop_break == true) {
	                        System.out.println("Stop Server informed");
	                        out.write(this.CreateDataPacket("127".getBytes("UTF8"), "Close".getBytes("UTF8")));
	                        out.flush();
	                        socket.close();
	                        System.out.println("Client Socket Closed");
	                        break;
	                    }
	                }
	            }
	        } catch (UnknownHostException ex) {
	            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (IOException ex) {
	            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
	
    /**
     * Creates new form Client_Main
     */
    public Client_Main() {
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

        ipaddress = new javax.swing.JTextField();
        port_no = new javax.swing.JTextField();
        connect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ipaddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipaddressActionPerformed(evt);
            }
        });

        port_no.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                port_noActionPerformed(evt);
            }
        });

        connect.setText("Connect");
        connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectActionPerformed(evt);
            }
        });

        jLabel1.setText("Ip Address");

        jLabel2.setText("Port Number");

        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });

        jLabel3.setText("Username::");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(connect)
                    .addComponent(ipaddress, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addComponent(port_no))
                .addContainerGap(126, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(73, 73, 73)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipaddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(port_no, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(connect)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectActionPerformed
        // TODO add your handling code here:
        
            ip = this.ipaddress.getText();
            pn =  Integer.parseInt(this.port_no.getText());
            user = this.username.getText();
            this.setupconnection(ip, pn);
            
            if(this.socket!=null && user!=null){
                
                    this.hide();
                    User_window uw = new User_window(this.socket,user);
                    uw.setVisible(true);
            }
            
            
    }//GEN-LAST:event_connectActionPerformed

    private void port_noActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_port_noActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_port_noActionPerformed

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameActionPerformed

    private void ipaddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipaddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipaddressActionPerformed

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
            java.util.logging.Logger.getLogger(Client_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client_Main().setVisible(true);
            }
        });
    }
private String ip;
    private int pn;
    private String user;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connect;
    private javax.swing.JTextField ipaddress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField port_no;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
