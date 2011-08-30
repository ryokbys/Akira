package plugin;
import java.io.*;
import data.*;
import tools.*;
import viewer.viewConfigPanel.plugin.ModelingPluginInterface;

public class MakeBetaQuartzSiO2 implements ModelingPluginInterface {
  public String getName(){
    return "Beta-Quartz SiO2";
  }

  public void make(String dir, int fnum,
                   int Nx, int Ny, int Nz){
    Atoms atoms=new Atoms();

    //header
    atoms.totalFrame=1;
    atoms.startTime=0.f;
    atoms.timeInterval=0.f;

    //body
    float a=4.9965f/0.5291772f;
    float c=5.456f/0.5291772f;

    //normalized
    float[][] si={{0.500000f,0.000000f,0.000000f},
                  {0.000000f,0.500000f,0.666667f},
                  {0.500000f,0.500000f,0.333333f}};
    float[][] o={{0.415700f,0.207800f,0.166667f},
                 {0.792200f,0.207900f,0.833333f},
                 {0.792100f,0.584300f,0.500000f},
                 {0.207800f,0.415700f,0.500000f},
                 {0.207900f,0.792200f,0.833333f},
                 {0.584300f,0.792100f,0.166667f}};

    atoms.n=9*Nx*Ny*Nz;
    atoms.nData=1;
    atoms.allocate(atoms.n);

    atoms.h[0][0]=a*Nx;
    atoms.h[1][0]=0.f;
    atoms.h[2][0]=0.f;
    atoms.h[0][1]=a*(float)Math.cos(120*Math.PI/180)*Ny;
    atoms.h[1][1]=a*(float)Math.sin(120*Math.PI/180)*Ny;
    atoms.h[2][1]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][2]=0.f;
    atoms.h[2][2]=c*Nz;

    Matrix.inv(atoms.h,atoms.hinv);
    //init x
    int inc=0;
    for(int i=0;i<Nx;i++){
      for(int j=0;j<Ny;j++){
        for(int k=0;k<Nz;k++){
          for(int l=0;l<3;l++){
            atoms.tag[inc]=1;
            atoms.r[inc]=mulH(atoms.h,(si[l][0]+i)/Nx,(si[l][1]+j)/Ny,(si[l][2]+k)/Nz);
            inc++;
          }//l
          for(int l=0;l<6;l++){
            atoms.tag[inc]=2;
            atoms.r[inc]=mulH(atoms.h,(o[l][0]+i)/Nx,(o[l][1]+j)/Ny,(o[l][2]+k)/Nz);
            inc++;
          }//l
        }//k
      }//j
    }//i

    //write
    MyFileIO atomFileIO= new MyFileIO(dir+File.separator+String.format("%04d-b-quartz-sio2.Akira",fnum));
    atomFileIO.wopen();
    atomFileIO.writeHeader(1,0.f,1.f,false);
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,new Bonds());
    atomFileIO.writeFooter();
    atomFileIO.wclose();

  }
  private float[] mulH(float[][]h, float x,float y,float z){
    float[] out=new float[3];
      for(int j=0;j<3;j++)
        out[j]=h[j][0]*x+h[j][1]*y+h[j][2]*z;
      return out;
  }

}
