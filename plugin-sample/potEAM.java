package plugin;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import data.*;
import viewer.viewConfigPanel.plugin.*;

////////////
public class potEAM implements MDPluginInterface{

  public String getName(){
    return "EAM pot.";
  }

  public double[][] getForce(double[][]h, int natm,double[][] r){
    return null;
  }
  double[] chgScale( double[][]h, double[] in ){
    double[] out = new double[3];
    for(int k=0;k<3;k++) out[k]=(float)(h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2]);
    return out;
  }



}//MDFrame
