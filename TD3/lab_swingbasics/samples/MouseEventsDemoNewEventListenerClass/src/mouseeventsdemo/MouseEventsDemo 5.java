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
public class MouseEventsDemo extends Frame {
    
    TextField tf;
    public MouseEventsDemo(String title){
        super(title);
        tf = new TextField(60);
        
        MyMouseEventListener mymouseeventlistener = new MyMouseEventListener();
        mymouseeventlistener.setTf(tf);
        
        addMouseListener(mymouseeventlistener);
    }
    
    public void launchFrame() {
        /* Add components to the frame */
        add(tf, BorderLayout.SOUTH);
        setSize(300,300);
        setVisible(true);
    }
    
    public static void main(String args[]) {
        MouseEventsDemo med =
                new MouseEventsDemo("Mouse Events Demo");
        med.launchFrame();
    }
}