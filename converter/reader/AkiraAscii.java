package converter.reader;

import java.io.*;
import java.util.*;
import java.lang.Math;

import tools.*;
import data.*;
import converter.*;

/**
 * Reading function from ASCII
 */
public class AkiraAscii{
  public static void conv(AkiraFileIO atomFileIO,
                          ConvConfig cconf,
                          int itarget,
                          int ithFrame){

    for(int ifrm=cconf.startFrame.get(itarget);
        ifrm<=cconf.endFrame.get(itarget);
        ifrm+=cconf.frameInc.get(itarget)){
      //filename
      String readFile=String.format(cconf.readFilePath.get(itarget)+"/"
                                    +cconf.readFileName.get(itarget),ifrm);

      ithFrame++;
      System.out.println(String.format(" Frame: %d/%d (%s)",ithFrame,
                                       cconf.getTotalFrame(),readFile));

      Atoms atoms=new Atoms();

      //read
      AkiraAscii.read(readFile,atoms,cconf);

      //create bonds
      if(cconf.createBondsWithLength){
        BondCreator bondCreator=new BondCreator(cconf);
        bondCreator.createWithBondLength(atoms);
        atomFileIO.existBonds=true;
      }else if(cconf.createBondsWithFile){
        BondCreator bondCreator=new BondCreator(cconf);
        bondCreator.createWithBondList(atoms,cconf,itarget,ifrm);
        atomFileIO.existBonds=true;
      }else{
        System.out.println("  |- NO BONDS");
        atomFileIO.existBonds=false;
      }

      //write to file
      atomFileIO.write(atoms);
      System.out.print("\n");
    }//ifrm
  }

  /**
   * read ASCII file
   */
  private static void read(String fileName,
                           Atoms atoms,
                           ConvConfig cconf){
    try {
      FileReader fr = new FileReader( fileName );
      BufferedReader br = new BufferedReader( fr );
      String line;
      String[] elem;
      Tokens tokens = new Tokens();
      Exponent epnum = new Exponent();
      int ndata;

      float[] tp = new float[3];
      float[] ra = new float[3];

      HashMap<Integer,Integer> readTagCount = new HashMap<Integer,Integer>();
      HashMap<Integer,Integer> tagCount = new HashMap<Integer,Integer>();


      // line 1: n
      line = br.readLine();
      tokens.setString( line );
      tokens.setDelim( " " );
      elem = tokens.getTokens();
      int natm=Integer.parseInt( elem[0] );
      // atoms.nData = Integer.parseInt( elem[1] );
      // if(atoms.nData>9)atoms.nData=9;
      ndata= (byte)Math.min( Byte.parseByte( elem[1] ), Atom.MAX_NUM_DATA );
      int nvolBlock=Integer.parseInt( elem[2] );
      int nvolume=Integer.parseInt( elem[3] );

      //read h matrix
      for( int i=0; i<3; i++ ){
        line = br.readLine();
        tokens.setString( line );
        elem = tokens.getTokens();
        for( int j=0; j<3; j++ ){
          epnum.setString( elem[j] );
          //now h matrix is angstrom
          atoms.hmat[i][j] = (float)(epnum.getNumber());
        }
      }
      Matrix.inv(atoms.hmat,atoms.hmati);

      //read
      int dataStartPosition = 4;
      for(int i=0; i<natm; i++){
        line = br.readLine();
        tokens.setString( line );
        elem = tokens.getTokens(); //total data of a line

        //1st colum is species
        epnum.setString( elem[0] );
        int itag= (int)epnum.getNumber();
        //tag counter
        if(readTagCount.containsKey(itag)){
          int inc=readTagCount.get(itag);
          inc++;
          readTagCount.put(itag,inc);
        }else{
          int inc=1;
          readTagCount.put(itag,inc);
        }

        // 2nd~4th colum is position data
        // position data is not scaled for cutRegion
        for( int k=0; k<3; k++ ){
          epnum.setString( elem[k+1] );
          tp[k] = (float)epnum.getNumber();
        }
        ra =  Tool.mulH( atoms.hmat, tp );

        float[] data=new float[ndata];
        for( int k=0; k<ndata; k++ ){
          epnum.setString( elem[k+dataStartPosition] );
          data[k]=(float)epnum.getNumber();
        }

        //check region
        if(cconf.isCutX)
          itag=Tool.cutRange(itag,ra, atoms.hmat, 'x', cconf.xMin, cconf.xMax);
        if(cconf.isCutY)
          itag=Tool.cutRange(itag,ra, atoms.hmat, 'y', cconf.yMin, cconf.yMax);
        if(cconf.isCutZ)
          itag=Tool.cutRange(itag,ra, atoms.hmat, 'z', cconf.zMin, cconf.zMax);
        if(cconf.isCutSphere)
          itag=Tool.cutShepre(itag,ra, atoms.hmat,cconf.cutCenter, cconf.cutRadius );

        //add
        if(itag>0){
          Atom atom= new Atom((byte)itag);
          //atoms.tag[atoms.n]=(byte)itag;
          //for(int k=0;k<3;k++)atoms.r[atoms.n][k]=ra[k];
          atom.pos[0]= ra[0];
          atom.pos[1]= ra[1];
          atom.pos[2]= ra[2];
          //for(int k=0;k<Atom.MAX_NUM_DATA;k++)atoms.data[atoms.n][k]=data[k];
          for(int k=0;k<ndata;k++)
            atom.auxData[k]=data[k];

          atoms.addAtom(atom);

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
      }//end of i::readNAtoms
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
      System.out.print(String.format("  |- ATOMS        : %8d",atoms.getNumAtoms()));
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
        //division of volume
        line = br.readLine();
        tokens.setString( line );
        tokens.setDelim( " " );
        elem = tokens.getTokens();
        epnum.setString( elem[0] );
        int nvx= (int)(epnum.getNumber());
        epnum.setString( elem[1] );
        int nvy= (int)(epnum.getNumber());
        epnum.setString( elem[2] );
        int nvz= (int)(epnum.getNumber());
        int nvol=nvz*nvy*nvx;

        //origin of volume
        float[] vorg=new float[3];
        line = br.readLine();
        tokens.setString( line );
        tokens.setDelim( " " );
        elem = tokens.getTokens();
        for( int j=0; j<3; j++ ){
          epnum.setString( elem[j] );
          vorg[j] = (float)(epnum.getNumber());
        }
        //h-matirx of volume
        float[][] hv=new float[3][3];
        for( int i=0; i<3; i++ ){
          line = br.readLine();
          tokens.setString( line );
          elem = tokens.getTokens();
          for( int j=0; j<3; j++ ){
            epnum.setString( elem[j] );
            hv[i][j] = (float)(epnum.getNumber());
          }
        }

        /**
         * At this moment (2013-04-05), negleting volumetric data
         *
        //volume data
        float rr=nvx*nvx/4;
        for(int ivz=0;ivz<nvz;ivz++){
          float rz=(ivz-nvz/2);
          for(int ivy=0;ivy<nvy;ivy++){
            float ry=(ivy-nvy/2);
            for(int ivx=0;ivx<nvx;ivx++){
              float rx=(ivx-nvx/2);
              line = br.readLine();
              tokens.setString( line );
              elem = tokens.getTokens(); //total data of a line
              epnum.setString( elem[0] );
              float voxel = (float)(epnum.getNumber());

              if(rx*rx+ry*ry+rz*rz>rr)continue;

              tp[0]=(ivx+0.5f)/(float)nvx;
              tp[1]=(ivy+0.5f)/(float)nvy;
              tp[2]=(ivz+0.5f)/(float)nvz;
              //add
              atoms.tag[atoms.n]=(byte)Const.VOLUME_DATA_TAG;
              atoms.r[atoms.n]=Tool.mulH( hv, tp);
              atoms.r[atoms.n][0]+=vorg[0];
              atoms.r[atoms.n][1]+=vorg[1];
              atoms.r[atoms.n][2]+=vorg[2];
              atoms.data[atoms.n][0]=voxel;
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
            }//ix
          }//iy
        }//iz
        //finish progress bar
        System.out.print("\r");
        for(int j=0;j<100;j++)System.out.print(" ");
        System.out.print("\r");
        //write info
        System.out.print(String.format("  |- VOLUME%d      : %8d\n",iv,nvol));
        */
      }//end of iv

      br.close();
      fr.close();


    }catch ( IOException e ){
      System.out.println(" CANNOT READ " + fileName );
      (new File(cconf.systemName+cconf.fileExtension)).delete();
      System.exit(0);
    }
  }


}
