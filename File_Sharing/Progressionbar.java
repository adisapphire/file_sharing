package file_sharing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import javax.swing.JPanel;

public class Progressionbar extends JPanel {
    
    float progress = 0;
    
    @Override protected void paintComponent (Graphics g) {
            AffineTransform at = new AffineTransform();
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform save_ctx = g2.getTransform();
            AffineTransform current_ctx = new AffineTransform ();
            current_ctx.concatenate(at);
            current_ctx.translate(getWidth() / 2, getHeight() / 2);
            current_ctx.rotate(Math.toRadians(270));
            g2.transform(current_ctx); 
            g2.setColor (Color.GRAY);
            g2.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
            g2.drawArc(-(getHeight() - 40 / 2) / 2, -(getHeight() - 40 / 2) / 2,getHeight() - 20,  getHeight() -20, 0, 360);
            g2.setColor (Color.red);
            g2.drawArc(-(getHeight() - 40 / 2) / 2, - (getHeight()- 40 / 2) / 2, getHeight() - 20, getHeight() - 20, 0, -(int) (progress * 3.6));
            AffineTransform current_ctx2 = new AffineTransform();
            current_ctx2.rotate(Math.toRadians (90));
            g2.transform(current_ctx2); 
            g2.setFont(new Font("Verdana", Font.PLAIN, 20));
            FontMetrics fn = g2.getFontMetrics(); 
            Rectangle2D r = fn.getStringBounds(progress + "%", g2);
            int x = (0 - (int) r.getWidth()) / 2;
            int y = (0 - (int) r.getHeight()) / 2 + fn.getAscent();
            g2.drawString(progress + "%", x, y);
            g2. transform(save_ctx);
            
    }       
    public void RenderProgress(float current_progress) {
        progress =current_progress;
        repaint();
    }
    
   
    
}
