/*
 * FrameWControlsDemo.java
 *
 * Created on September 22, 2006, 8:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package framewcontrolsdemo;

import java.awt.*;
class FrameWControlsDemo extends Frame {
    public static void main(String args[]) {
        FrameWControlsDemo fwc = new  FrameWControlsDemo();
        fwc.setLayout(new FlowLayout());
        fwc.setSize(600, 600);
        fwc.add(new Button("Test Me!"));
        fwc.add(new Label("Labe"));
        fwc.add(new TextField());
        CheckboxGroup cbg = new CheckboxGroup();
        fwc.add(new Checkbox("chk1", cbg, true));
        fwc.add(new Checkbox("chk2", cbg, false));
        fwc.add(new Checkbox("chk3", cbg, false));
        List list = new List(3, false);
        list.add("MTV");
        list.add("V");
        fwc.add(list);
        Choice chooser = new Choice();
        chooser.add("Avril");
        chooser.add("Monica");
        chooser.add("Britney");
        fwc.add(chooser);
        fwc.add(new Scrollbar());
        fwc.setVisible(true);
    }
}