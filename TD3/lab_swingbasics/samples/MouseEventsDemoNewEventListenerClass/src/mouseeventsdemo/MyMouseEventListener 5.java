/*
 * MyMouseEventListener.java
 *
 * Created on September 22, 2006, 1:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mouseeventsdemo;

import java.awt.BorderLayout;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author sang
 */
public class MyMouseEventListener implements MouseListener, MouseMotionListener {
    
    TextField tf;
    
    /** Creates a new instance of MyMouseEventListener */
    public MyMouseEventListener() {
    }
   
    public void setTf(TextField tx){
        tf = tx;
    }
    
    public void mouseClicked(MouseEvent me) {
        String msg = "Mouse clicked.";
        tf.setText(msg);
    }
    public void mouseEntered(MouseEvent me) {
        String msg = "Mouse entered component.";
        tf.setText(msg);
    }
    public void mouseExited(MouseEvent me) {
        String msg = "Mouse exited component.";
        tf.setText(msg);
    }
    public void mousePressed(MouseEvent me) {
        String msg = "Mouse pressed.";
        tf.setText(msg);
    }
    public void mouseReleased(MouseEvent me) {
        String msg = "Mouse released.";
        tf.setText(msg);
    }
    public void mouseDragged(MouseEvent me) {
        String msg = "Mouse dragged at " + me.getX()
        + "," + me.getY();
        tf.setText(msg);
    }
    public void mouseMoved(MouseEvent me) {
        String msg = "Mouse moved at " + me.getX()
        + "," + me.getY();
        tf.setText(msg);
    }
    
}
