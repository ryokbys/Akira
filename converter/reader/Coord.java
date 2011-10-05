package converter.reader;
import java.io.*;
import java.nio.*;
import java.util.*;

import tools.*;
import data.*;
import converter.*;
/**
 *
 */
public class Coord{
  public static void conv(MyFileIO atomFileIO,ConvConfig cconf,
                          int itarget,int ithFrame){


    for(int ifrm=cconf.startFrame.get(itarget);
        ifrm<=cconf.endFrame.get(itarget);
        ifrm+=cconf.frameInc.get(itarget)){

      ithFrame++;
      //filename
      String fileName=String.format(cconf.readFilePath.get(itarget)+"/"
                                    +cconf.readFileName.get(itarget),ifrm);

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

        System.out.println(String.format(" Frame: %d/%d (%s:%s)",
                                         ithFrame,
                                         cconf.getTotalFrame(),
                                         cconf.readFilePath.get(itarget),
                                         cconf.readFileName.get(itarget)));

        ArrayList<Float> pos= new ArrayList<Float>();

        tokens.setDelim( " " );
        while((line = br.readLine())!=null){
          tokens.setString( line );
          elem = tokens.getTokens();
          for( int j=0; j<3; j++ ){
            epnum.setString( elem[j] );
            float tmp=(float)(epnum.getNumber());
            if(atoms.h[j][j]<tmp)atoms.h[j][j]=tmp;
            pos.add(tmp);
          }
        }
        Matrix.inv(atoms.h,atoms.hinv);

        //allocate
        int natm=pos.size()/3;

        //set natom
        atoms.n=natm;
        atoms.nData = 1;

        atoms.allocate(natm);
        for(int i=0;i<natm;i++){
          atoms.tag[i]=(byte)1;
          atoms.r[i][0]=pos.get(3*i);
          atoms.r[i][1]=pos.get(3*i+1);
          atoms.r[i][2]=pos.get(3*i+2);
          atoms.data[i][0]=1.f;
        }

        System.out.println(String.format("  |- # of atoms : %d",natm));


        br.close();
        fr.close();


      }//end of try
      catch ( IOException e ){
        System.out.println(" CANNOT READ " + fileName );
        (new File(cconf.systemName+cconf.fileExtension)).delete();
        System.exit(0);
      }


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
    }
  }
}
