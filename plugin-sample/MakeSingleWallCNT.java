package plugin;
import java.io.*;
import javax.swing.*;

import data.*;
import tools.*;

import viewer.viewConfigPanel.plugin.ModelingPluginInterface;

public class MakeSingleWallCNT implements ModelingPluginInterface {
  public String getSaveFileName(){
    return "C-SWCNT";
  }
  public String getPluginName(){
    return "Single-Wall CNT";
  }

  float eps=0.0000001f;
  public void make(String dir, int Nx, int Ny, int Nz){
    Atoms atoms=new Atoms();

    //extend z-direction
    int nextz=4;

    String value = JOptionPane.showInputDialog("num of extend z",String.format("%d",nextz));
    if(value != null)nextz=Integer.parseInt(value);
    //set chirarity
    int mc1=4;
    value = JOptionPane.showInputDialog("chirarity 1",String.format("%d",mc1));
    if(value != null)mc1=Integer.parseInt(value);
    int mc2=7;
    value = JOptionPane.showInputDialog("chirarity 2",String.format("%d",mc2));
    if(value != null)mc2=Integer.parseInt(value);


    //header
    atoms.totalFrame=1;
    atoms.startTime=0.f;
    atoms.timeInterval=0.f;


    float bohr  =0.5291772f;
    float dcc= 1.44f/bohr;

    //a vectors, x_c = a * x_a
    float alc= dcc *(float)Math.sqrt(3.);
    float[][] a=new float[2][2];
    a[0][0]=(float)Math.sqrt(3f)*0.5f*alc;
    a[1][0]=0.5f*alc;
    a[0][1]=(float)Math.sqrt(3f)*0.5f*alc;
    a[1][1]=-0.5f*alc;

    //basic atoms in a-rep.
    float[][] ba=new float[2][2];
    ba[0][0]=0.f;
    ba[1][0]=0.f;
    ba[0][1]=2f/3f;
    ba[1][1]=2f/3f;


    //chiral vector: vc in cartessain
    float[] vc=new float[2];
    vc[0]=a[0][0]*mc1+a[0][1]*mc2;
    vc[1]=a[1][0]*mc1+a[1][1]*mc2;


    //primitive translation vector: vt in cartessian
    //greatest common divisor: d
    int nd=ngcd(2*mc1+mc2,mc1+2*mc2);
    int mt1=(2*mc1+mc2)/nd;
    int mt2=-(mc1+2*mc2)/nd;
    float[] vt=new float[2];
    vt[0]= a[0][0]*mt1 +a[0][1]*mt2;
    vt[1]= a[1][0]*mt1 +a[1][1]*mt2;

    //4 apexes of square sheet
    float[] ap1={0.f,0.f};
    float[] ap2={vc[0],vc[1]};
    float[] ap3={vc[0]+vt[0],vc[1]+vt[1]};
    float[] ap4={vt[0],vt[1]};

    //search atoms in the square
    int nmax=100000;
    float[][] rin=new float[nmax][2];

    int natm=0;
    for(int i2=mt2;i2<=mc2;i2++){
      for(int i1=0;i1<=mc1+mt1;i1++){
        for(int i=0;i<2;i++){
          float[] p=new float[2];
          p[0]=a[0][0]*(i1+ba[0][i]) +a[0][1]*(i2+ba[1][i])+eps;
          p[1]=a[1][0]*(i1+ba[0][i]) +a[1][1]*(i2+ba[1][i])+eps;
          //check p in square
          if(pinabcd(p,ap1,ap2,ap3,ap4)>=0){
            natm=natm+1;
            rin[natm][0]=p[0];
            rin[natm][1]=p[1];
          }
        }
      }
    }

    //chiral angle
    double angc= Math.atan2(vc[1],vc[0]);

    //rotation matrix
    float[][] rmat=new float[2][2];
    rmat[0][0]= (float)Math.cos(-angc);
    rmat[0][1]=-(float)Math.sin(-angc);
    rmat[1][0]= (float)Math.sin(-angc);
    rmat[1][1]= (float)Math.cos(-angc);

    //rotation
    float[][] ra=new float[natm][2];
    for(int i=0;i<natm;i++){
      ra[i][0]=rmat[0][0]*rin[i][0]+rmat[0][1]*rin[i][1];
      ra[i][1]=rmat[1][0]*rin[i][0]+rmat[1][1]*rin[i][1];
    }

    //reset unit vectors: cartessian coordinate
    a[0][0]=(float)Math.sqrt(vc[0]*vc[0]+vc[1]*vc[1]);
    a[0][1]=0.f;
    a[1][0]=0.f;
    a[1][1]=(float)Math.sqrt(vt[0]*vt[0]+vt[1]*vt[1]);

    //radius of SWNT
    float radius= a[0][0]/2.f/(float)Math.PI;

    //3D unit vector: a3d
    float[] a3d=new float[3];
    a3d[0]=radius *4;
    a3d[1]=radius *4;
    a3d[2]=a[1][1];

    //nanotube
    float[][] ra3d=new float[natm][3];
    for(int i=0;i<natm;i++){
      ra3d[i][0]=radius*(float)Math.cos(ra[i][0]/a[0][0]*2*Math.PI)+radius*2;
      ra3d[i][1]=radius*(float)Math.sin(ra[i][0]/a[0][0]*2*Math.PI)+radius*2;
      ra3d[i][2]=ra[i][1];
    }

    atoms.n=natm*nextz;
    atoms.nData=1;
    atoms.h[0][0]=a3d[0];
    atoms.h[1][0]=0.f;
    atoms.h[2][0]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][1]=a3d[1];
    atoms.h[2][1]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][2]=0.f;
    atoms.h[2][2]=a3d[2]*nextz;
    Matrix.inv(atoms.h,atoms.hinv);

    atoms.allocate(atoms.n);
    int n=0;
    for(int i=0;i<nextz;i++){
      for(int j=0;j<natm;j++){
        atoms.tag[n]=1;
        atoms.r[n][0]=ra3d[j][0];
        atoms.r[n][1]=ra3d[j][1];
        atoms.r[n][2]=ra3d[j][2]+a3d[2]*i;
        n++;
      }
    }





    //write
    MyFileIO atomFileIO= new MyFileIO(dir);
    atomFileIO.wopen();
    atomFileIO.writeHeader(1,0.f,1.f,false);
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,new Bonds());
    atomFileIO.writeFooter();
    atomFileIO.wclose();

  }

  private int side(float[] p, float[] a,float[] b){
    //Which side of the line AB the point p exists?

    //get vector product.
    float op=p[0]*(b[1]-a[1]) +a[0]*(p[1]-b[1]) +b[0]*(a[1]-p[1]);
    //return 1 if the p is on the left side of ab.
    if( op>0.f+eps ){
      return 1;
      //return -1 if the p is on the right side of ab.
    }else if( op<0.f-eps ){
      return -1;
      //return 0 if the p is on the line ab.
    }else{
      return 0;
    }
  }//side

  private int pinabcd(float[] p, float[] a, float[] b, float[] c, float[] d){
    //Point P is in the tetragon ABCD?
    int pinabcd=-1;
    int pab=side(p,a,b);
    int pbc=side(p,b,c);
    int pcd=side(p,c,d);
    int pda=side(p,d,a);

    //return 1 if the p is in the tetragon.
    if( pab>0 && pbc>0 && pcd>0 && pda>0 ){
      pinabcd=1;
      return pinabcd;
    }else if( pab<0 && pbc<0 && pcd<0 && pda< 0 ){
      pinabcd=1;
      return pinabcd;
    }

    //return 0 if the p is on the line.
    if( pab>=0 && pbc>=0 && pcd>=0 && pda>=0 ){
      pinabcd=0;
      return pinabcd;
    }else if( pab<=0 && pbc<=0 && pcd<=0 && pda<=0 ){
      pinabcd=0;
      return pinabcd;
    }

    return -1;
  }

  private int ngcd(int m, int n){
    //return the greatest common number
    if(m==0)return n;
    if(n==0)return m;

    int lm;
    if(m<n) lm=n;
    else    lm=m;

    int ln;
    if(m<n) ln=m;
    else    ln=n;

    while(true){
      int k=lm%ln;
      if(k==0)return ln;
      lm=ln;
      ln=k;
    }
  }
}
