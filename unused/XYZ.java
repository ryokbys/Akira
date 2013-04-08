package converter.reader;
import java.io.*;
import java.nio.*;
import java.util.*;

import tools.*;
import data.*;
import converter.*;


/**
 * XYZ-format file reader
 */
public class XYZ{
  public static void conv(AkiraFileIO atomFileIO,ConvConfig cconf,
                          int itarget,int ithFrame){

    for(int ifrm=cconf.startFrame.get(itarget);
        ifrm<=cconf.endFrame.get(itarget);
        ifrm+=cconf.frameInc.get(itarget)){


      String fileName=String.format(cconf.readFilePath.get(itarget)+"/"
                                    +cconf.readFileName.get(itarget),ifrm);


      float[] tp =new float[3];
      Atoms atoms=new Atoms();
      Bonds bonds=new Bonds();
      float rmax,rmin;

      Hashtable pTable= new Hashtable();
      pTable.put("H", new Integer(1));
      pTable.put("He",new Integer(2));
      pTable.put("Li",new Integer(3));
      pTable.put("Be",new Integer(4));
      pTable.put("B", new Integer(5));
      pTable.put("C", new Integer(6));
      pTable.put("N", new Integer(7));
      pTable.put("O", new Integer(8));
      pTable.put("F", new Integer(9));
      pTable.put("Ne",new Integer(10));
      pTable.put("Na",new Integer(11));
      pTable.put("Mg",new Integer(12));
      pTable.put("Al",new Integer(13));
      pTable.put("Si",new Integer(14));
      pTable.put("P",new Integer(15));
      pTable.put("S",new Integer(16));
      pTable.put("Cl",new Integer(17));
      pTable.put("Ar",new Integer(18));
      pTable.put("K",new Integer(19));
      pTable.put("Ca",new Integer(20));


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
        //-----1st line: # of atoms
        line = br.readLine();
        tokens.setString( line );
        tokens.setDelim( " " );
        elem = tokens.getTokens();
        epnum.setString( elem[0] );
        int natm=(int)epnum.getNumber();

        //-----2nd line: maybe comment line?
        line = br.readLine();

        //-----3rd to the end: species, x, y, z
        atoms.n=natm;
        atoms.nData=1;
        //Allocate
        atoms.allocate(atoms.n);
        //Read atoms
        rmax= 0.0f;
        rmin= 0.0f;
        for(int i=0;i<natm;i++){
          line = br.readLine();
          tokens.setString( line );
          elem = tokens.getTokens();
          //atomic num.
          int is= ((Integer)pTable.get(elem[0])).intValue();
          //epnum.setString( elem[0] );
          // int is=(int)(epnum.getNumber());
          atoms.tag[i]=(byte)is;

          for(int j=0;j<3;j++){
            epnum.setString( elem[j+1] );
            float tmp= (float)(epnum.getNumber());
            if(rmax<tmp) rmax=tmp;
            if(rmin>tmp) rmin=tmp;
            atoms.r[i][j]= tmp;
          }
          /*
           * System.out.println(String.format(" %d, %10.2f %10.2f %10.2f"
           *                                  ,atoms.tag[i]
           *                                  ,atoms.r[i][0]
           *                                  ,atoms.r[i][1]
           *                                  ,atoms.r[i][2]));
           */
          atoms.data[i][0]= 1.f;
        }
        //Assumeing system box is cubic and large enough
        atoms.h[0][0]= (rmax-rmin)*1.5f;
        atoms.h[1][1]= (rmax-rmin)*1.5f;
        atoms.h[2][2]= (rmax-rmin)*1.5f;
        Matrix.inv(atoms.h,atoms.hinv);
        //Shift origin of atom positions
        for( int i=0;i<natm;i++ ){
          for( int j=0;j<3;j++ ){
            atoms.r[i][j]=atoms.r[i][j] -rmin;
          }
          /*
           * System.out.println(String.format(" %d, %10.2f %10.2f %10.2f"
           *                                  ,atoms.tag[i]
           *                                  ,atoms.r[i][0]
           *                                  ,atoms.r[i][1]
           *                                  ,atoms.r[i][2]));
           */
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
  }//end of conv
}
