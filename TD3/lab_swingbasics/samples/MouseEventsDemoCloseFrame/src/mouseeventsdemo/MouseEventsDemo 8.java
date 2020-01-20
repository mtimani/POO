/*
 * MouseEventsDemo.java
 *
 * Created on September 22, 2006, 12:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mouseeventsdemo;

import java.awt.*;
import java.awt.event.*;
public class MouseEventsDemo extends Frame implements
        MouseListener, MouseMotionListener, WindowListener {
    
    TextField tf;
    
    // Method implementations of MouseListener and 
    // MouseMotionListener interfaces
    
    public MouseEventsDemo(String title){
        super(title);
        tf = new TextField(60);
        addMouseListener(this);
        addWindowListener(this);
    }
    
    public void launchFrame() {
        /* Add components to the frame */
        add(tf, BorderLayout.SOUTH);
        setSize(300,300);
        setVisible(true);
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
    
    // Methods implementations of WindowListener Interface
    
    public void windowActivated(WindowEvent e) {
    }
    
    public void windowClosed(WindowEvent e) {
    }
    
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        System.exit(0);
    }
    
    public void windowDeactivated(WindowEvent e) {
    }
    
    public void windowDeiconified(WindowEvent e) {
    }
    
    public void windowIconified(WindowEvent e) {
    }
    
    public void windowOpened(WindowEvent e) {
    }
   
    // Main method
    
    public static void main(String args[]) {
        MouseEventsDemo med =
                new MouseEventsDemo("Mouse Events Demo");
        med.launchFrame();
    }
}