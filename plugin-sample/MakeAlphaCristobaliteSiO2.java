package plugin;
import java.io.*;
import data.*;
import tools.*;
import viewer.viewConfigPanel.plugin.ModelingPluginInterface;

public class MakeAlphaCristobaliteSiO2 implements ModelingPluginInterface {
  public String getName(){
    return "Alpha-Cristobalite SiO2";
  }

  public void make(String dir, int fnum,
                   int Nx, int Ny, int Nz){
    Atoms atoms=new Atoms();

    //header
    atoms.totalFrame=1;
    atoms.startTime=0.f;
    atoms.timeInterval=0.f;

    //body
    atoms.n=12*Nx*Ny*Nz;
    atoms.nData=1;
    atoms.allocate(atoms.n);

    float a=4.97170f/0.529f;
    atoms.h[0][0]=a*Nx;
    atoms.h[1][0]=0.f;
    atoms.h[2][0]=0.f;
    atoms.h[0][1]=0.f;
    atoms.h[1][1]=a*Ny;
    atoms.h[2][1]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][2]=0.f;
    atoms.h[2][2]=a*Nz;

    Matrix.inv(atoms.h,atoms.hinv);

    float[][] si={{0.300400f,0.300400f,0.000000f},
                  {0.199600f,0.800400f,0.250000f},
                  {0.699600f,0.699600f,0.500000f},
                  {0.800400f,0.199600f,0.750000f}};
    float[][] o={{0.239800f,0.104400f,0.178900f},
                 {0.395600f,0.739800f,0.428900f},
                 {0.760200f,0.895600f,0.678900f},
                 {0.604400f,0.260200f,0.928900f},
                 {0.104400f,0.239800f,0.821100f},
                 {0.739800f,0.395600f,0.571100f},
                 {0.895600f,0.760200f,0.321100f},
                 {0.260200f,0.604400f,0.071100f}};

    //init x
    int inc=0;
    for(int i=0;i<Nx;i++){
      for(int j=0;j<Ny;j++){
        for(int k=0;k<Nz;k++){
          //Si
          for(int l=0;l<4;l++){
            atoms.tag[inc]=1;
            atoms.r[inc][0]=(si[l][0]+i)*a;
            atoms.r[inc][1]=(si[l][1]+j)*a;
            atoms.r[inc][2]=(si[l][2]+k)*a;
            inc++;
          }
          //O
          for(int l=0;l<8;l++){
            atoms.tag[inc]=2;
            atoms.r[inc][0]=(o[l][0]+i)*a;
            atoms.r[inc][1]=(o[l][1]+j)*a;
            atoms.r[inc][2]=(o[l][2]+k)*a;
            inc++;
          }
        }//k
      }//j
    }//i

    //write
    MyFileIO atomFileIO= new MyFileIO(dir+File.separator+String.format("%04d-a-quartz-sio2.Akira",fnum));
    atomFileIO.wopen();
    atomFileIO.writeHeader(1,0.f,1.f,false);
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,new Bonds());
    atomFileIO.writeFooter();
    atomFileIO.wclose();

  }
}
