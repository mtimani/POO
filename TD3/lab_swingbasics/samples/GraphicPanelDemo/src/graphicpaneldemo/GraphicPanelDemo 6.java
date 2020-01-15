/*
 * GraphicPanelDemo.java
 *
 * Created on September 22, 2006, 8:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package graphicpaneldemo;

import java.awt.*;
public class GraphicPanelDemo extends Panel {
    GraphicPanelDemo() {
        setBackground(Color.black);
    }
    public void paint(Graphics g) {
        g.setColor(new Color(0,255,0));	//green
        g.setFont(new Font("Helvetica",Font.PLAIN,16));
        g.drawString("Hello GUI World!", 30, 100);
        g.setColor(new Color(1.0f,0,0));	//red
        g.fillRect(30, 100, 150, 10);
    }
    /* need to place Panel in Frame or other Window */
    public static void main(String args[]) {
        Frame f = new Frame("Testing Graphics Panel");
        GraphicPanelDemo gp = new GraphicPanelDemo();
        f.add(gp);
        f.setSize(600, 300);
        f.setVisible(true);
    }
}