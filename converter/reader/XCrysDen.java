package converter.reader;
import java.io.*;
import java.nio.*;
import java.util.*;

import tools.*;
import data.*;
import converter.*;


/**
 * Reading XCrysDen
 */
public class XCrysDen{
  public static void conv(MyFileIO atomFileIO,ConvConfig cconf,
                          int itarget,int ithFrame){

    for(int ifrm=cconf.startFrame.get(itarget);
        ifrm<=cconf.endFrame.get(itarget);
        ifrm+=cconf.frameInc.get(itarget)){
      //filename
      String readFile=String.format(cconf.readFilePath.get(itarget)+"/"
                                    +cconf.readFileName.get(itarget),ifrm);

      float[] tp =new float[3];
      Atoms atoms=new Atoms();
      Bonds bonds=new Bonds();

      try {
        FileReader fr = new FileReader( readFile );
        BufferedReader br = new BufferedReader( fr );
        String line;
        String[] elem;
        Tokens tokens = new Tokens();
        Exponent epnum = new Exponent();
        float[] dataRange=new float[2];


        ithFrame++;
        System.out.println(String.format(" Frame: %d/%d (%s)",ithFrame,
                                         cconf.getTotalFrame(),readFile));

        //CRYSTAL
        line = br.readLine();
        //
        while((line = br.readLine()) !=null){
          if(line.equalsIgnoreCase("PRIMVEC")){
            readBox(br,atoms);
          }else if(line.equalsIgnoreCase("PRIMCOORD")){
            readAtom(br,atoms);
          }
        }

        br.close();
        fr.close();
      }//end of try
      catch ( IOException e ){
        System.out.println(" CANNOT READ " + readFile );
        (new File(cconf.systemName+cconf.fileExtension)).delete();
        System.exit(0);
      }

      System.out.println(String.format("  |-atoms: %d",atoms.n));

      //create bonds
      if(cconf.isCreatingBonds){
        BondCreator bondCreator=new BondCreator(cconf);
        bondCreator.create(atoms,bonds);
        atomFileIO.existBonds=true;
      }else{
        System.out.println("  |- NO BONDS");
      }
      atomFileIO.write(atoms,bonds);
    }//ifrm
  }//end of xcrysden


  private static void readBox(BufferedReader br, Atoms atoms)throws IOException{
    String line;
    String[] elem;
    Tokens tokens = new Tokens();
    Exponent epnum = new Exponent();

    tokens.setDelim( " " );

    //ax,ay,az
    line = br.readLine();
    tokens.setString( line );
    elem = tokens.getTokens();
    for( int j=0; j<3; j++ ){
      epnum.setString( elem[j] );
      atoms.h[j][0] = (float)(epnum.getNumber());
    }

    //bx,by,bz
    line = br.readLine();
    tokens.setString( line );
    elem = tokens.getTokens();
    for( int j=0; j<3; j++ ){
      epnum.setString( elem[j] );
      atoms.h[j][1] = (float)(epnum.getNumber());
    }
    //cx,cy,cz
    line = br.readLine();
    tokens.setString( line );
    elem = tokens.getTokens();
    for( int j=0; j<3; j++ ){
      epnum.setString( elem[j] );
      atoms.h[j][2] = (float)(epnum.getNumber());
    }

    Matrix.inv(atoms.h,atoms.hinv);
  }


  private static void readAtom(BufferedReader br, Atoms atoms) throws IOException{
    String line;
    String[] elem;
    Tokens tokens = new Tokens();
    Exponent epnum = new Exponent();

    line = br.readLine();
    tokens.setString( line );
    tokens.setDelim( " " );
    elem = tokens.getTokens();
    epnum.setString( elem[0] );
    int natm=(int)epnum.getNumber();

    //set natom
    atoms.n=natm;
    atoms.nData = 0;
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

      for(int j=0;j<3;j++){
        epnum.setString( elem[j+1] );
        atoms.r[i][j]= (float)(epnum.getNumber());
      }

      //force
      if(elem.length>4){
        atoms.nData = 3;
        for(int j=0;j<3;j++){
          epnum.setString( elem[j+4] );
          atoms.data[i][j]= (float)(epnum.getNumber());
        }
      }

    }

  }
}
