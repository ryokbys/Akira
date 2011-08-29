package plugin;
import java.io.*;
import data.*;
import tools.MyFileIO;
import viewer.viewConfigPanel.plugin.ModelingPluginInterface;

public class MakeFCCAl implements ModelingPluginInterface {
  public String getName(){
    return "Make FCC Al";
  }

  public void make(int Nx, int Ny, int Nz){
    Atoms atoms=new Atoms();

    //header
    atoms.totalFrame=1;
    atoms.startTime=0.f;
    atoms.timeInterval=0.f;

    //body
    float cunit=4.0496f/0.529f;
    float[][] fcc={{0.0f, 0.0f, 0.0f },
                    {0.5f, 0.5f, 0.0f },
                    {0.5f, 0.0f, 0.5f },
                    {0.0f, 0.5f, 0.5f }};

    atoms.n=4*Nx*Ny*Nz;
    atoms.nData=1;
    atoms.allocate(atoms.n);

    atoms.h[0][0]=cunit*Nx;
    atoms.h[1][0]=0.f;
    atoms.h[2][0]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][1]=cunit*Ny;
    atoms.h[2][1]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][2]=0.f;
    atoms.h[2][2]=cunit*Nz;
    atoms.hinv[0][0]=1.f/atoms.h[0][0];
    atoms.hinv[1][0]=0.f;
    atoms.hinv[2][0]=0.f;
    atoms.hinv[0][2]=0.f;
    atoms.hinv[1][1]=1.f/atoms.h[1][1];
    atoms.hinv[2][1]=0.f;
    atoms.hinv[0][2]=0.f;
    atoms.hinv[1][2]=0.f;
    atoms.hinv[2][2]=1.f/atoms.h[2][2];
    //init x
    int inc=0;
    for(int i=0;i<Nx;i++){
      for(int j=0;j<Ny;j++){
        for(int k=0;k<Nz;k++){
          for(int l=0;l<4;l++){
            atoms.r[inc][0]=(fcc[l][0]+i)*cunit;
            atoms.r[inc][1]=(fcc[l][1]+j)*cunit;
            atoms.r[inc][2]=(fcc[l][2]+k)*cunit;
            atoms.tag[inc]=1;
            inc++;
          }//l
        }//k
      }//j
    }//i

    //write
    MyFileIO atomFileIO= new MyFileIO("model.Akira");
    atomFileIO.wopen();
    atomFileIO.writeHeader(1,0.f,1.f,false);
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,new Bonds());
    atomFileIO.writeFooter();
    atomFileIO.wclose();

  }
}
