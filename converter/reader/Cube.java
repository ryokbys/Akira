package converter.reader;
import java.io.*;
import java.nio.*;
import java.util.*;

import tools.*;
import data.*;
import converter.*;


/**
 * Reading CUBE
 */
public class Cube{
  public static void conv(MyFileIO atomFileIO,ConvConfig cconf,
                          int itarget,int ithFrame){

    String fileName=cconf.readFilePath.get(itarget)+"/"+cconf.readFileName.get(itarget);
    float[] tp =new float[3];
    Atoms atoms=new Atoms();
    Bonds bonds=new Bonds();

    try {
      FileReader fr = new FileReader( fileName );
      BufferedReader br = new BufferedReader( fr );
      String line;
      String[] elem;
      Tokens tokens = new Tokens();
      Exponent epnum = new Exponent();
      float[] dataRange=new float[2];


      ithFrame++;
      System.out.println(String.format(" Frame: %d/%d (%s:%s)",
                                       ithFrame,
                                       cconf.getTotalFrame(),
                                       cconf.readFilePath.get(itarget),
                                       cconf.readFileName.get(itarget)));
      //title
      line = br.readLine();
      //title
      line = br.readLine();

      //N, origin
      line = br.readLine();
      tokens.setString( line );
      tokens.setDelim( " " );
      elem = tokens.getTokens();
      epnum.setString( elem[0] );
      int natm=(int)epnum.getNumber();
      float[] org=new float[3];
      epnum.setString( elem[1] );
      org[0]=(float)epnum.getNumber();
      epnum.setString( elem[2] );
      org[1]=(float)epnum.getNumber();
      epnum.setString( elem[3] );
      org[2]=(float)epnum.getNumber();

      //read a, b, c
      int[] nv=new int[3];
      for( int i=0; i<3; i++ ){
        line = br.readLine();
        tokens.setString( line );
        elem = tokens.getTokens();
        epnum.setString( elem[0] );
        nv[i] = (int)(epnum.getNumber());
        for( int j=0; j<3; j++ ){
          epnum.setString( elem[j+1] );
          atoms.h[i][j] = (float)(epnum.getNumber())*nv[i];
        }
      }
      Matrix.inv(atoms.h,atoms.hinv);

      int nx=nv[0];
      int ny=nv[1];
      int nz=nv[2];

      int nvoxel=nx*ny*nz;
      //set natom
      atoms.n=nvoxel+natm;
      atoms.nData = 1;
      //allocate
      atoms.allocate(atoms.n);

      //atoms
      for(int i=0;i<natm;i++){
        line = br.readLine();
        tokens.setString( line );
        elem = tokens.getTokens();
        //atomic num.
        epnum.setString( elem[0] );
        int is=(int)(epnum.getNumber());
        atoms.tag[i]=(byte)is;

        //# of e in valence
        epnum.setString( elem[1] );
        //atoms.data[i][0]=(float)(epnum.getNumber());
        atoms.data[i][0]=0.f;

        for(int j=0;j<3;j++){
          epnum.setString( elem[j+2] );
          atoms.r[i][j]= (float)(epnum.getNumber())-org[j];
        }

      }

      System.out.println(String.format("  |- # of atoms : %d",natm));
      System.out.println(String.format("  |- # of voxels: %d = %d*%d*%d",nvoxel,nx,ny,nz));

      //for charge
      float chgsum=0.f;
      //read chg
      int ix,iy,iz;
      float vol=
        atoms.h[0][0]*(atoms.h[1][1]*atoms.h[2][2]-atoms.h[1][2]*atoms.h[2][1])
        +atoms.h[0][1]*(atoms.h[1][2]*atoms.h[2][0]-atoms.h[1][0]*atoms.h[2][2])
        +atoms.h[0][2]*(atoms.h[1][0]*atoms.h[2][1]-atoms.h[1][1]*atoms.h[2][0]);

      System.out.println(String.format("  |- volume     : %f",vol));

      float tmp;
      int inc=0;
      while(inc!=nvoxel){
        line = br.readLine();
        tokens.setString( line );
        elem = tokens.getTokens(); //total data of a line

        for(int i=0;i<elem.length;i++){
          epnum.setString( elem[i] );
          tmp = (float)epnum.getNumber();
          chgsum+=tmp;
          if(dataRange[0] > tmp) dataRange[0] = tmp;
          if(dataRange[1] < tmp) dataRange[1] = tmp;
          //chg
          iz=inc%nz;
          iy=(inc/nz)%ny;
          ix=inc/(nz*ny);
          int ir=iz+iy*nz+ix*nz*ny;
          atoms.data[ir+natm][0]=tmp;
          atoms.tag[ir+natm]=(byte)Const.VOLUME_DATA_TAG;
          tp[0]=(ix+0.5f)/(float)nx;
          tp[1]=(iy+0.5f)/(float)ny;
          tp[2]=(iz+0.5f)/(float)nz;
          atoms.r[ir+natm]=Tool.mulH( atoms.h, tp);
          inc++;
        }
        if(inc==nvoxel)continue;
      }

      System.out.println(String.format("  |- total CHARGE: %f",chgsum));
      System.out.println(String.format("  |- max CHARGE[e/Ang^3]: %8.3e",dataRange[1]));
      System.out.println(String.format("  |- min CHARGE[e/Ang^3]: %8.3e",dataRange[0]));


      br.close();
      fr.close();


    }//end of try
    catch ( IOException e ){
      System.out.println(" CANNOT READ " + fileName );
      (new File(cconf.systemName+cconf.fileExtension)).delete();
      System.exit(0);
    }
    atomFileIO.existBonds=false;
    atomFileIO.write(atoms,bonds);

  }//end of CUBE
}
