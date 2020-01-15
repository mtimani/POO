/*
 * CloseFrameDemo.java
 *
 * Created on September 22, 2006, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package closeframedemo;

import java.awt.*;
import java.awt.event.*;

class CloseFrameDemo extends Frame
        implements WindowListener {
    Label label;
    CloseFrameDemo(String title) {
        super(title);
        label = new Label("Close the frame.");
        this.addWindowListener(this);
    }
    
    void launchFrame() {
        setSize(300,300);
        setVisible(true);
    }
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
    public static void main(String args[]) {
        CloseFrameDemo cf =
                new CloseFrameDemo("Close Window Example");
        cf.launchFrame();
    }
}