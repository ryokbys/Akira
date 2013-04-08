package converter.reader;
import java.io.*;
import java.nio.*;
import java.util.*;

import tools.*;
import data.*;
import converter.*;

/**
 * Reading CHGCAR of VASP
 */

public class CHGCAR{

  public static void conv(AkiraFileIO atomFileIO,
                          ConvConfig cconf,
                          int itarget,
                          int ithFrame){

    for(int ifrm=cconf.startFrame.get(itarget);
        ifrm<=cconf.endFrame.get(itarget);
        ifrm+=cconf.frameInc.get(itarget)){

      ithFrame++;
      //filename
      String readFile=String.format(cconf.readFilePath.get(itarget)+"/"
                                    +cconf.readFileName.get(itarget),ifrm);

      float tmp;
      float[] tp =new float[3];
      Atoms atoms=new Atoms();
      //Bonds bonds=new Bonds();

      try {
        FileReader fr = new FileReader( readFile );
        BufferedReader br = new BufferedReader( fr );
        String line;
        String[] elem;
        Tokens tokens = new Tokens();
        Exponent epnum = new Exponent();


        //read atom
        HashMap<Integer,Integer> readTagCount = new HashMap<Integer,Integer>();
        HashMap<Integer,Integer> tagCount = new HashMap<Integer,Integer>();

        System.out.println(String.format(" Frame: %d/%d (%s:%s)",
                                         ithFrame,
                                         cconf.getTotalFrame(),
                                         cconf.readFilePath.get(itarget),
                                         cconf.readFileName.get(itarget)));
        //title
        line = br.readLine();

        //scale fac.
        line = br.readLine();
        tokens.setString( line );
        tokens.setDelim( " " );
        elem = tokens.getTokens();
        epnum.setString( elem[0] );
        float fac=(float)epnum.getNumber();

        //read h matrix
        for( int i=0; i<3; i++ ){
          line = br.readLine();
          tokens.setString( line );
          elem = tokens.getTokens();
          for( int j=0; j<3; j++ ){
            epnum.setString( elem[j] );
            //now h matrix is angstrom
            atoms.hmat[i][j] = (float)(epnum.getNumber())*fac;
            //System.out.println(String.format("h %f",atoms.h[i][j]));
          }
        }
        Matrix.inv(atoms.hmat,atoms.hmati);


        //# of atoms
        int natm=0;
        int[] na=new int[100];
        line = br.readLine();
        tokens.setString( line );
        elem = tokens.getTokens();
        for(int i=0;i<elem.length;i++){
          epnum.setString( elem[i] );
          na[i]= (int)epnum.getNumber();
          natm+= na[i];
        }
        //coord. style
        line = br.readLine();

        //atoms
        int iatm=0;
        float[][] tmpAtoms=new float[natm][3];
        for(int i=0;i<natm;i++){
          line = br.readLine();
          tokens.setString( line );
          elem = tokens.getTokens();
          for( int j=0; j<3; j++ ){
            epnum.setString( elem[j] );
            tmpAtoms[iatm][j]= (float)(epnum.getNumber());
          }
          iatm++;
        }

        //blanck line
        line = br.readLine();

        //# of voxel
        line = br.readLine();
        tokens.setString( line );
        elem = tokens.getTokens();
        epnum.setString( elem[0] );
        int nx = (int)epnum.getNumber();
        epnum.setString( elem[1] );
        int ny = (int)epnum.getNumber();
        epnum.setString( elem[2] );
        int nz = (int)epnum.getNumber();
        int nvoxel=nx*ny*nz;

        //set natom
        atoms.n=nvoxel+natm;
        atoms.nData = 1;
        //allocate
        //atoms.allocate(atoms.n);

        //atoms
        iatm=0;
        int naTMP=na[0];
        int is=1;
        for(int i=0;i<natm;i++){

          if(iatm<na[0]){
            is=1;
          }else{
            for(int j=1;j<na.length;j++){
              if(na[j-1]<=iatm && iatm<na[j]){
                is=j+1;
                break;
              }
            }
          }

          //tag count
          if(tagCount.containsKey(is)){
            int inc=tagCount.get(is);
            inc++;
            tagCount.put(is,inc);
          }else{
            int inc=1;
            tagCount.put(is,inc);
          }

          atoms.tag[iatm]=(byte)is;

          atoms.data[iatm][0]=0.f;
          atoms.r[iatm]=Tool.mulH( atoms.h, tmpAtoms[i] );
          iatm++;
        }

        System.out.println(String.format("  |- # of atoms : %d",natm));
        System.out.println(String.format("  |- # of voxels: %d = %d*%d*%d",nvoxel,nx,ny,nz));

        //for charge
        float chgsum=0.f;
        //read chg
        int inc=0;
        int ix,iy,iz;
        float vol=
          atoms.h[0][0]*(atoms.h[1][1]*atoms.h[2][2]-atoms.h[1][2]*atoms.h[2][1])
          +atoms.h[0][1]*(atoms.h[1][2]*atoms.h[2][0]-atoms.h[1][0]*atoms.h[2][2])
          +atoms.h[0][2]*(atoms.h[1][0]*atoms.h[2][1]-atoms.h[1][1]*atoms.h[2][0]);

        System.out.println(String.format("  |- volume     : %f",vol));

        for(int i=0; i<nvoxel/5; i++){
          line = br.readLine();
          tokens.setString( line );
          elem = tokens.getTokens(); //total data of a line
          for(int j=0;j<5;j++){
            epnum.setString( elem[j] );
            tmp = (float)epnum.getNumber();
            tmp =tmp/(float)nvoxel;
            chgsum+=tmp;
            tmp =tmp/vol;
            //chg
            iz=inc/(nx*ny);
            iy=(inc/nx)%ny;
            ix=inc%nx;
            int ir=iz+iy*nz+ix*nz*ny;
            tp[0]=(ix+0.5f)/(float)nx;
            tp[1]=(iy+0.5f)/(float)ny;
            tp[2]=(iz+0.5f)/(float)nz;
            atoms.tag[iatm]=(byte)Const.VOLUME_DATA_TAG;
            atoms.r[iatm]=Tool.mulH( atoms.h, tp);
            atoms.data[iatm][0]=tmp;
            inc++;
            iatm++;
          }
        }
        //reminder
        int m=nvoxel%5;
        if(m!=0){
          line = br.readLine();
          tokens.setString( line );
          elem = tokens.getTokens(); //total data of a line
          for(int j=0;j<m;j++){
            epnum.setString( elem[j] );
            tmp = (float)epnum.getNumber();
            tmp =tmp/(float)nvoxel;
            chgsum+=tmp;
            tmp =tmp/vol;

            //chg
            iz=inc/(nx*ny);
            iy=(inc/nx)%ny;
            ix=inc%nx;
            int ir=iz+iy*nz+ix*nz*ny;
            tp[0]=(ix+0.5f)/(float)nx;
            tp[1]=(iy+0.5f)/(float)ny;
            tp[2]=(iz+0.5f)/(float)nz;
            atoms.tag[iatm]=(byte)Const.VOLUME_DATA_TAG;
            atoms.r[iatm]=Tool.mulH( atoms.h, tp);
            atoms.data[iatm][0]=tmp;
            inc++;
            iatm++;
          }
        }
        System.out.println(String.format("  |- total CHARGE: %f",chgsum));
        //System.out.println(String.format("  |- max CHARGE[e/Ang^3]: %8.3e",dataRange[0][1]));
        //System.out.println(String.format("  |- min CHARGE[e/Ang^3]: %8.3e",dataRange[0][0]));

        br.close();
        fr.close();

      }//end of try
      catch ( IOException e ){
        System.out.println(" CANNOT READ " + readFile );
        (new File(cconf.systemName+cconf.fileExtension)).delete();
        System.exit(0);
      }

      //create bonds
      //create bonds
      if(cconf.createBondsWithLength){
        BondCreator bondCreator=new BondCreator(cconf);
        bondCreator.createWithBondLength(atoms,bonds);
        atomFileIO.existBonds=true;
      }else if(cconf.createBondsWithFile){
        BondCreator bondCreator=new BondCreator(cconf);
        bondCreator.createWithBondList(atoms,bonds,cconf,itarget,ifrm);
        atomFileIO.existBonds=true;
      }else{
        System.out.println("  |- NO BONDS");
        atomFileIO.existBonds=false;
      }

      atomFileIO.write(atoms,bonds);

    }//end of CHGCAR
  }
}
