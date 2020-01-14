/*
 * BorderLayoutDemo.java
 *
 * Created on September 22, 2006, 8:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package borderlayoutdemo;

import java.awt.*;
class BorderLayoutDemo extends Frame {
    public static void main(String args[]) {
        BorderLayoutDemo bld = new BorderLayoutDemo();
        bld.setLayout(new BorderLayout(10, 10));
        bld.add(new Button("NORTH"), BorderLayout.NORTH);
        bld.add(new Button("SOUTH"), BorderLayout.SOUTH);
        bld.add(new Button("EAST"), BorderLayout.EAST);
        bld.add(new Button("WEST"), BorderLayout.WEST);
        bld.add(new Button("CENTER"), BorderLayout.CENTER);
        bld.setSize(200, 200);
        bld.setVisible(true);
    }
}
