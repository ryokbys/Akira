package plugin;
import java.io.*;
import data.*;
import tools.*;
import viewer.viewConfigPanel.plugin.ModelingPluginInterface;

public class MakeSheetLJ implements ModelingPluginInterface {
  public String getName(){
    return "Make Sheet LJ";
  }

  public void make(String dir, int fnum,
                   int Nx, int Ny, int Nz){
    Atoms atoms=new Atoms();

    //header
    atoms.totalFrame=1;
    atoms.startTime=0.f;
    atoms.timeInterval=0.f;

    //body
    float deq=3.41f*1.4142f/0.529f;


    float[][] sheet={{ 0.0f, 0.0f, 0.0f },
                     { 0.5f, 0.5f, 0.0f }};

    atoms.n=2*Nx*Ny*Nz;
    atoms.nData=1;
    atoms.allocate(atoms.n);

    float cx=deq;
    float cy=deq*(float)Math.sqrt(3.);
    float cz=deq;

    atoms.h[0][0]=cx*Nx;
    atoms.h[1][0]=0.f;
    atoms.h[2][0]=0.f;
    atoms.h[0][1]=0.f;
    atoms.h[1][1]=cx*Ny;
    atoms.h[2][1]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][2]=0.f;
    atoms.h[2][2]=cz*Nz;

    Matrix.inv(atoms.h,atoms.hinv);
    //init x
    int inc=0;
    for(int i=0;i<Nx;i++){
      for(int j=0;j<Ny;j++){
        for(int k=0;k<Nz;k++){
          for(int l=0;l<2;l++){
            atoms.r[inc][0]=(sheet[l][0]+i)*cx;
            atoms.r[inc][1]=(sheet[l][1]+j)*cy;
            atoms.r[inc][2]=(sheet[l][2]+k)*cz;
            atoms.tag[inc]=1;
            inc++;
          }//l
        }//k
      }//j
    }//i

    //write
    MyFileIO atomFileIO= new MyFileIO(dir+File.separator+String.format("%04d-LJ-sheet.Akira",fnum));
    atomFileIO.wopen();
    atomFileIO.writeHeader(1,0.f,1.f,false);
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,new Bonds());
    atomFileIO.writeFooter();
    atomFileIO.wclose();

  }
}
