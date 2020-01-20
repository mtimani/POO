/*
 * FlowLayoutDemo.java
 *
 * Created on September 22, 2006, 8:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package flowlayoutdemo;

import java.awt.*;
class FlowLayoutDemo extends Frame {
    public static void main(String args[]) {
        FlowLayoutDemo fld = new FlowLayoutDemo();
        fld.setLayout(new FlowLayout(FlowLayout.RIGHT,
                10, 10));
        fld.add(new Button("ONE"));
        fld.add(new Button("TWO"));
        fld.add(new Button("THREE"));
        fld.setSize(100, 100);
        fld.setVisible(true);
    }
}