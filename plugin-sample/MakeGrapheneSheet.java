package plugin;
import java.io.*;
import data.*;
import tools.*;
import viewer.viewConfigPanel.plugin.ModelingPluginInterface;

public class MakeGrapheneSheet implements ModelingPluginInterface {
  public String getName(){
    return "Graphene Sheet";
  }
  public void make(String dir, int fnum,
                   int Nx, int Ny, int Nz){
    Atoms atoms=new Atoms();

    //header
    atoms.totalFrame=1;
    atoms.startTime=0.f;
    atoms.timeInterval=0.f;

    //body
    float deq=1.48f/0.529177f*0.965f;//C


    float[][] sheet={{ 0.0f, 0.0f, 0.0f },
                     { 0.5f, 1f/6f, 0.0f },
                     { 0.5f, 0.5f, 0.0f },
                     { 0.0f, 1f/6f+0.5f, 0.0f }};

    atoms.n=4*Nx*Ny*Nz;
    atoms.nData=1;
    atoms.allocate(atoms.n);

    atoms.h[0][0]=deq*(float)Math.sqrt(3.f)*Nx;
    atoms.h[1][0]=0.f;
    atoms.h[2][0]=0.f;
    atoms.h[0][1]=0.f;
    atoms.h[1][1]=deq*3.f*Ny;
    atoms.h[2][1]=0.f;
    atoms.h[0][2]=0.f;
    atoms.h[1][2]=0.f;
    atoms.h[2][2]=deq*Nz;
    Matrix.inv(atoms.h,atoms.hinv);
    //init x
    int inc=0;
    for(int i=0;i<Nx;i++){
      for(int j=0;j<Ny;j++){
        for(int k=0;k<Nz;k++){
          for(int l=0;l<4;l++){
            atoms.r[inc][0]=(sheet[l][0]+i)*deq*(float)Math.sqrt(3.f);
            atoms.r[inc][1]=(sheet[l][1]+j)*deq*3.f;
            atoms.r[inc][2]=(sheet[l][2]+k)*deq;
            atoms.tag[inc]=1;
            inc++;
          }//l
        }//k
      }//j
    }//i

    //write
    MyFileIO atomFileIO= new MyFileIO(dir+File.separator+String.format("%04d-graphene.Akira",fnum));
    atomFileIO.wopen();
    atomFileIO.writeHeader(1,0.f,1.f,false);
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,new Bonds());
    atomFileIO.writeFooter();
    atomFileIO.wclose();

  }
}
