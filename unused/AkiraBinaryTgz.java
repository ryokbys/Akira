package converter.reader;
import java.io.*;
import java.nio.*;
import java.util.*;
import org.apache.tools.tar.*;
import java.util.zip.*;

import tools.*;
import data.*;
import converter.*;

/**
 * read binary kvs file on TGZ
 */
public class AkiraBinaryTgz{

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



        //read atom
        HashMap<Integer,Integer> readTagCount = new HashMap<Integer,Integer>();
        HashMap<Integer,Integer> tagCount = new HashMap<Integer,Integer>();

        Atoms atoms=new Atoms();
        Bonds bonds=new Bonds();

        //natm
        ByteBuffer bb = ByteBuffer.allocate(1024);
        bb.putInt(dis.readInt());
        bb.putInt(dis.readInt());//natm
        bb.putInt(dis.readInt());//ndata
        bb.putInt(dis.readInt());//nvblock
        bb.putInt(dis.readInt());//nvolume
        bb.putInt(dis.readInt());

        //nAtoms
        if(cconf.readFileEndian.get(itarget).startsWith("little"))
          bb.order(ByteOrder.LITTLE_ENDIAN);

        int natm=bb.getInt(4);
        atoms.nData=bb.getInt(8);
        if(atoms.nData>9)atoms.nData=9;
        int nvolBlock=bb.getInt(12);
        int nvolume=bb.getInt(16);
        atoms.allocate(natm+nvolume);

        //read h matrix
        bb=null;
        bb = ByteBuffer.allocate(1024);

        bb.putInt(dis.readInt());
        bb.putFloat(dis.readFloat());//h11
        bb.putFloat(dis.readFloat());//h12
        bb.putFloat(dis.readFloat());//h13
        bb.putInt(dis.readInt());
        bb.putInt(dis.readInt());
        bb.putFloat(dis.readFloat());//h21
        bb.putFloat(dis.readFloat());//h22
        bb.putFloat(dis.readFloat());//h23
        bb.putInt(dis.readInt());
        bb.putInt(dis.readInt());
        bb.putFloat(dis.readFloat());//h31
        bb.putFloat(dis.readFloat());//h32
        bb.putFloat(dis.readFloat());//h33
        bb.putInt(dis.readInt());

        if(cconf.readFileEndian.get(itarget).startsWith("little"))
          bb.order(ByteOrder.LITTLE_ENDIAN);
        //now h matrix is angstrom
        atoms.h[0][0] = (float)(bb.getFloat(4));
        atoms.h[1][0] = (float)(bb.getFloat(8));
        atoms.h[2][0] = (float)(bb.getFloat(12));

        atoms.h[0][1] = (float)(bb.getFloat(24));
        atoms.h[1][1] = (float)(bb.getFloat(28));
        atoms.h[2][1] = (float)(bb.getFloat(32));

        atoms.h[0][2] = (float)(bb.getFloat(44));
        atoms.h[1][2] = (float)(bb.getFloat(48));
        atoms.h[2][2] = (float)(bb.getFloat(52));
        Matrix.inv(atoms.h,atoms.hinv);


        //read
        for(int i=0; i<natm; i++){
          bb=null;
          bb = ByteBuffer.allocate(1024);
          bb.putInt(dis.readInt());
          bb.putInt(dis.readInt());//tag
          bb.putFloat(dis.readFloat());//ra(1,i)
          bb.putFloat(dis.readFloat());//ra(2,i)
          bb.putFloat(dis.readFloat());//ra(3,i)
          for(int j=0;j<atoms.nData;j++)bb.putFloat(dis.readFloat());//data
          bb.putInt(dis.readInt());

          if(cconf.readFileEndian.get(itarget).startsWith("little"))
            bb.order(ByteOrder.LITTLE_ENDIAN);

          int itag=bb.getInt(4);
          tp[0]=bb.getFloat(8);
          tp[1]=bb.getFloat(12);
          tp[2]=bb.getFloat(16);
          ra =  MDMath.mulH( atoms.h, tp );

          float[] data=new float[Const.DATA];
          for( int k=0; k<atoms.nData; k++ )data[k] = bb.getFloat(20+4*k);


          //check region
          if(cconf.isCutX)
            itag=Tool.cutRange(itag,ra, atoms.h, 'x', cconf.xMin, cconf.xMax);
          if(cconf.isCutY)
            itag=Tool.cutRange(itag,ra, atoms.h, 'y', cconf.yMin, cconf.yMax);
          if(cconf.isCutZ)
            itag=Tool.cutRange(itag,ra, atoms.h, 'z', cconf.zMin, cconf.zMax);
          if(cconf.isCutSphere)
            itag=Tool.cutShepre(itag,ra, atoms.h,cconf.cutCenter, cconf.cutRadius );


          //add
          if(itag>0){
            atoms.tag[atoms.n]=(byte)itag;
            for(int k=0;k<3;k++)atoms.r[atoms.n][k]=ra[k];
            for(int k=0;k<atoms.nData;k++)atoms.data[atoms.n][k]=data[k];

            atoms.n++;
            //tag count
            if(tagCount.containsKey(itag)){
              int inc=tagCount.get(itag);
              inc++;
              tagCount.put(itag,inc);
            }else{
              int inc=1;
              tagCount.put(itag,inc);
            }
          }

          //progress bar
          int digit=1;
          if(digit<natm/30)digit=natm/30;
          if(i%digit ==0 ){
            System.out.print("\r");
            System.out.print(String.format("reading %d atoms [",natm));
            for(int jj=0;jj<i/digit;jj++)System.out.print("=");
            System.out.print(">");
            for(int jj=0;jj<30-i/digit;jj++)System.out.print(" ");
            System.out.print("] ");
            System.out.print(String.format("%3.0f %%",i/(float)natm*100));
          }
        }//end of i::readNatoms

        //finish progress bar
        System.out.print("\r");
        for(int j=0;j<100;j++)System.out.print(" ");
        System.out.print("\r");


        //read atoms info
        if(cconf.isCutX || cconf.isCutY || cconf.isCutZ || cconf.isCutSphere){
          System.out.print(String.format("  |- read atoms   : %8d",natm));
          System.out.print(" (");
          Set set = readTagCount.keySet();
          Iterator iterator = set.iterator();
          Integer object;
          while(iterator.hasNext()){
            object = (Integer)iterator.next();
            System.out.print(String.format("tag%d: %d, ",
                                           object.intValue(),readTagCount.get(object)));
          }
          System.out.print("\b\b )\n");
        }
        //written atoms info
        System.out.print(String.format("  |- ATOMS        : %8d",atoms.n));
        System.out.print(" (");
        Set set = tagCount.keySet();
        Iterator iterator = set.iterator();
        Integer object;
        while(iterator.hasNext()){
          object = (Integer)iterator.next();
          System.out.print(String.format("tag%d: %d, ",
                                         object.intValue(),tagCount.get(object)));
        }
        System.out.print("\b\b )\n");


        //read volume data
        for(int iv=0;iv<nvolBlock;iv++){
          bb=null;
          bb = ByteBuffer.allocate(1024);
          bb.putInt(dis.readInt());
          bb.putInt(dis.readInt());//nvx
          bb.putInt(dis.readInt());//nvy
          bb.putInt(dis.readInt());//nvz
          bb.putInt(dis.readInt());
          if(cconf.readFileEndian.get(itarget).startsWith("little"))
            bb.order(ByteOrder.LITTLE_ENDIAN);
          int nvx=bb.getInt(4);
          int nvy=bb.getInt(8);
          int nvz=bb.getInt(12);
          int nvol=nvz*nvy*nvx;

          //origin of volume
          float[] vorg=new float[3];
          bb=null;
          bb = ByteBuffer.allocate(1024);
          bb.putInt(dis.readInt());
          bb.putFloat(dis.readFloat());//org x
          bb.putFloat(dis.readFloat());//org y
          bb.putFloat(dis.readFloat());//org z
          bb.putInt(dis.readInt());
          if(cconf.readFileEndian.get(itarget).startsWith("little"))
            bb.order(ByteOrder.LITTLE_ENDIAN);
          vorg[0] = (float)(bb.getFloat(4));
          vorg[1] = (float)(bb.getFloat(8));
          vorg[2] = (float)(bb.getFloat(12));

          //read h matrix
          float[][] hv=new float[3][3];
          bb=null;
          bb = ByteBuffer.allocate(1024);
          bb.putInt(dis.readInt());
          bb.putFloat(dis.readFloat());//h11
          bb.putFloat(dis.readFloat());//h12
          bb.putFloat(dis.readFloat());//h13
          bb.putInt(dis.readInt());
          bb.putInt(dis.readInt());
          bb.putFloat(dis.readFloat());//h21
          bb.putFloat(dis.readFloat());//h22
          bb.putFloat(dis.readFloat());//h23
          bb.putInt(dis.readInt());
          bb.putInt(dis.readInt());
          bb.putFloat(dis.readFloat());//h31
          bb.putFloat(dis.readFloat());//h32
          bb.putFloat(dis.readFloat());//h33
          bb.putInt(dis.readInt());

          if(cconf.readFileEndian.get(itarget).startsWith("little"))
            bb.order(ByteOrder.LITTLE_ENDIAN);
          //now h matrix is angstrom
          hv[0][0] = (float)(bb.getFloat(4));
          hv[1][0] = (float)(bb.getFloat(8));
          hv[2][0] = (float)(bb.getFloat(12));
          hv[0][1] = (float)(bb.getFloat(24));
          hv[1][1] = (float)(bb.getFloat(28));
          hv[2][1] = (float)(bb.getFloat(32));
          hv[0][2] = (float)(bb.getFloat(44));
          hv[1][2] = (float)(bb.getFloat(48));
          hv[2][2] = (float)(bb.getFloat(52));

          //volume data
          for(int ivz=0;ivz<nvz;ivz++){
            for(int ivy=0;ivy<nvy;ivy++){
              for(int ivx=0;ivx<nvx;ivx++){
                bb=null;
                bb = ByteBuffer.allocate(1024);
                bb.putInt(dis.readInt());
                bb.putFloat(dis.readFloat());//volume
                bb.putInt(dis.readInt());

                tp[0]=(ivx+0.5f)/(float)nvx;
                tp[1]=(ivy+0.5f)/(float)nvy;
                tp[2]=(ivz+0.5f)/(float)nvz;
                //add
                atoms.tag[atoms.n]=(byte)Const.VOLUME_DATA_TAG;
                atoms.r[atoms.n]=MDMath.mulH( hv, tp);
                atoms.r[atoms.n][0]+=vorg[0];
                atoms.r[atoms.n][1]+=vorg[1];
                atoms.r[atoms.n][2]+=vorg[2];
                atoms.data[atoms.n][0]=(float)(bb.getFloat(4));
                atoms.n++;

                //progress bar
                int digit=1;
                if(digit<nvol/30)digit=nvol/30;
                int ii=ivx+ivy*nvx+ivz*nvx*nvy;
                if(ii%digit ==0 ){
                  System.out.print("\r");
                  System.out.print(String.format("reading %d volume [",nvol));
                  for(int jj=0;jj<ii/digit;jj++)System.out.print("=");
                  System.out.print(">");
                  for(int jj=0;jj<30-ii/digit;jj++)System.out.print(" ");
                  System.out.print("] ");
                  System.out.print(String.format("%3.0f %%",ii/(float)nvol*100));
                }
              }
            }
          }
          //finish progress bar
          System.out.print("\r");
          for(int j=0;j<100;j++)System.out.print(" ");
          System.out.print("\r");
          //write info
          System.out.print(String.format("  |- VOLUME%d      : %8d\n",iv,nvol));
        }



        //close
        bos.close();
        dis.close();


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

    }//end of try
    catch ( IOException e ){
      System.out.println(" CANNOT READ " + filePath );
      (new File(cconf.systemName+cconf.fileExtension)).delete();
      System.exit(0);
    }//end of catch

  }//fromTGZBinary
}
