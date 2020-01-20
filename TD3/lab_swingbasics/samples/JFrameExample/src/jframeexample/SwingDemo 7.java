/*
 * Main.java
 *
 * Created on September 22, 2006, 8:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jframeexample;


import javax.swing.*;
import java.awt.*;
class SwingDemo {
    JFrame frame;
    JPanel panel;
    JTextField textField;
    JButton button;
    Container contentPane;
    public static void main(String args[]) {
        SwingDemo sd = new SwingDemo();
        sd.launchFrame();
    }
    void launchFrame() {
        /* initialization */
        frame = new JFrame("My First Swing Application");
        panel = new JPanel();
        textField = new JTextField("Default text");
        button = new JButton("Click me!");
        contentPane = frame.getContentPane();
        //add components to panel–FlowLayout by default
        panel.add(textField);
        panel.add(button);
        /* add components to contentPane– BorderLayout */
        contentPane.add(panel, BorderLayout.CENTER);
        frame.pack();  //Size of frame based on components
        frame.setVisible(true);
    }
}
