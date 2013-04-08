package converter.reader;
import java.io.*;
import java.nio.*;
import java.util.*;
import org.apache.tools.tar.*;
import java.util.zip.*;

import tools.*;
import data.*;
import converter.*;


public class XCrysDenTgz{

  public static void conv(AkiraFileIO atomFileIO,ConvConfig cconf,
                          int itarget,int ithFrame){


    String filePath;
    String fileFormat;

    String line;
    String[] elem;
    Tokens tokens = new Tokens();
    Exponent epnum = new Exponent();
    float[] tp = new float[3];
    float[] ra = new float[3];
    String name;

    String readFile;

    filePath=cconf.readFilePath.get(itarget);
    try {
      FileInputStream fis = new FileInputStream(filePath);
      TarInputStream tin = new TarInputStream(new GZIPInputStream(fis));
      TarEntry tarEnt = tin.getNextEntry();

      int ifrm=cconf.startFrame.get(itarget);
      readFile=String.format(cconf.readFileName.get(itarget),ifrm);
      name = tarEnt.getName();

      //search "fileName"
      while(tarEnt != null && ifrm<=cconf.endFrame.get(itarget)){
        ithFrame++;
        System.out.println(String.format
                           (" Frame: %d/%d (%s:%s)",ithFrame,cconf.getTotalFrame(),
                            filePath,readFile));


        while(!name.matches(".*"+readFile+".*")){
          tarEnt = tin.getNextEntry();
          name = tarEnt.getName();
        }


        int size = (int)tarEnt.getSize();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
        tin.copyEntryContents(bos);

        byte[] tba = bos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(tba));

        BufferedReader br = new BufferedReader(new InputStreamReader(dis));

        Atoms atoms=new Atoms();
        Bonds bonds=new Bonds();

        //CRYSTAL
        line=br.readLine();
        while((line = br.readLine()) !=null){
          if(line.matches(".*PRIMVEC.*")){
            XCrysDen.readBox(br,atoms);
          }else if(line.matches(".*PRIMCOORD.*")){
            XCrysDen.readAtom(br,atoms);
          }
        }

        //close
        bos.close();
        dis.close();
        br.close();


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

        //write to file
        atomFileIO.write(atoms,bonds);
        System.out.print("\n");

        //next
        ifrm+=cconf.frameInc.get(itarget);
        readFile=String.format(cconf.readFileName.get(itarget),ifrm);
      }//end of while

      //close
      tin.close();

    }catch ( IOException e ){
      System.out.println(" CANNOT READ " + filePath );
      (new File(cconf.systemName+cconf.fileExtension)).delete();
      System.exit(0);
    }//end of catch

  }//end of conv

}
