/*
 * SampleFrameDemo.java
 *
 * Created on September 22, 2006, 8:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sampleframedemo;

import java.awt.*;
/* Try the availble buttons in the frame */
public class SampleFrameDemo extends Frame {
   public static void main(String args[]) {
      SampleFrameDemo sf = new SampleFrameDemo();
      sf.setSize(100, 100); //Try removing this
      sf.setVisible(true);	 //Try removing this
   }
}