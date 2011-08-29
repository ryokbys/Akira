package plugin;
import java.io.*;
import data.*;
import tools.*;
import viewer.viewConfigPanel.plugin.ModelingPluginInterface;

public class MakeTetrahedronSiO2 implements ModelingPluginInterface {
  public String getName(){
    return "Make SiO2 Tetrahedron";
  }

  public void make(String dir, int fnum,
                   int Nx, int Ny, int Nz){
    Atoms atoms=new Atoms();

    //header
    atoms.totalFrame=1;
    atoms.startTime=0.f;
    atoms.timeInterval=0.f;

    //body
    atoms.n=5;
    atoms.nData=1;
    atoms.allocate(atoms.n);

    atoms.h[0][0]=4.8355f*10;
    atoms.h[1][0]=0.f;
    atoms.h[2][0]=0.f;
    atoms.h[0][1]=0.f;
    atoms.h[1][1]=4.8355f*10;
    atoms.h[2][1]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][2]=0.f;
    atoms.h[2][2]=4.8355f*10;
    Matrix.inv(atoms.h,atoms.hinv);

    //init x
    int inc=0;
    //O
    atoms.tag[inc]=2;
    atoms.r[inc][0]=1.16405f/0.5291772f;
    atoms.r[inc][1]=1.31891f/0.5291772f;
    atoms.r[inc][2]=0.98363f/0.5291772f;

    inc++;
    atoms.tag[inc]=2;
    atoms.r[inc][0]=2.16659f/0.5291772f;
    atoms.r[inc][1]=2.24354f/0.5291772f;
    atoms.r[inc][2]=3.21607f/0.5291772f;

    inc++;
    atoms.tag[inc]=2;
    atoms.r[inc][0]=0.88442f/0.5291772f;
    atoms.r[inc][1]=-0.02021f/0.5291772f;
    atoms.r[inc][2]=3.14502f/0.5291772f;

    inc++;
    atoms.tag[inc]=2;
    atoms.r[inc][0]=-0.36664f/0.5291772f;
    atoms.r[inc][1]=2.26034f/0.5291772f;
    atoms.r[inc][2]=2.86625f/0.5291772f;

    //Si
    inc++;
    atoms.tag[inc]=1;
    atoms.r[inc][0]=0.96210f/0.5291772f;
    atoms.r[inc][1]=1.45075f/0.5291772f;
    atoms.r[inc][2]=2.55281f/0.5291772f;

    float shift=atoms.h[0][0]*0.5f;
    for(int i=0;i<atoms.n;i++){
      atoms.r[i][0]+=shift-atoms.r[4][0];
      atoms.r[i][1]+=shift-atoms.r[4][1];
      atoms.r[i][2]+=shift-atoms.r[4][2];
    }

    //write
    MyFileIO atomFileIO= new MyFileIO(String.format("%04d-tetrahedron-sio2.Akira",fnum));
    atomFileIO.wopen();
    atomFileIO.writeHeader(1,0.f,1.f,false);
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,new Bonds());
    atomFileIO.writeFooter();
    atomFileIO.wclose();

  }
}
