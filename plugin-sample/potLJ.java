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
public class potLJ implements MDPluginInterface{

  public String getName(){
    return "LJ pot.";
  }

  public double[][] getForce(double[][]h, int natm,double[][] r){
    double fkb= 1.3806503e-23*(2.41889e-17*2.41889e-17)/9.1093897e-31/(0.5291772e-10*0.5291772e-10);
    double eps=120*fkb;
    double sgm=3.41/0.529177;//convert to a.u.
    double sgm6=sgm*sgm*sgm*sgm*sgm*sgm;

    double[][] f=new double[natm][3];

    //init
    double epot=0;
    for(int i=0;i<natm;i++){
      f[i][0]=0.;
      f[i][1]=0.;
      f[i][2]=0.;
    }

    double dv;
    double[] dsr=new double[3];
    double[] dr=new double[3];
    //cal force
    for(int i=0;i<natm-1;i++){
      for(int j=i+1;j<natm;j++){
        for(int k=0;k<3;k++){
          dsr[k]=r[j][k]-r[i][k];
          if(dsr[k]>0.5)dsr[k]-=1.0;
          if(dsr[k]<-0.5)dsr[k]+=1.0;
        }

        dr=chgScale( h,dsr );
        double r2=dr[0]*dr[0]+dr[1]*dr[1]+dr[2]*dr[2];
        double rij=Math.sqrt( r2 );
        double ri=1/rij;
        double ri6=ri*ri*ri*ri*ri*ri;
        double rr=sgm6*ri6;

        dv=-24*eps*(2*rr-1)*rr*ri;
        dv/=rij;
        f[i][0]+=dv*dr[0];
        f[i][1]+=dv*dr[1];
        f[i][2]+=dv*dr[2];
        f[j][0]-=dv*dr[0];
        f[j][1]-=dv*dr[1];
        f[j][2]-=dv*dr[2];
      }
    }
    return f;
  }
  double[] chgScale( double[][]h, double[] in ){
    double[] out = new double[3];
    for(int k=0;k<3;k++) out[k]=(float)(h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2]);
    return out;
  }



}//MDFrame
