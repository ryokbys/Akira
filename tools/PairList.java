package tools;

import java.util.*;
import java.io.*;

import data.*;
import tools.*;

/**
 * create pair list
 */

public class PairList{

  /**
   * makeKthNearestPairListの為の内部クラス
   */
  static class MyLength{
    int n;
    double length;
    MyLength(int i, double length){
      this.n=i;
      this.length=length;
    }
  }

  /**
   * 距離が小さい方からkth番目までのペアリストを作る．
   */
  public static ArrayList makeKthNearestPairList(Atoms atoms, float rcut,int kth,
                                                 boolean deleteVoxel,boolean pbcOn){
    ArrayList<ArrayList<Integer>> lspr= PairList.makePairList(atoms,rcut,deleteVoxel,pbcOn);

    ArrayList<ArrayList<Integer>> kthNeighbors= new ArrayList<ArrayList<Integer>>();
    int natm= atoms.getNumAtoms();

    for( int i=0; i<natm; i++ ){
      Atom ai= atoms.getAtom(i);
      LinkedList<MyLength> iLengthList= new LinkedList<MyLength>();
      ArrayList<Integer> iList = lspr.get(i);
      for(int jj=0;jj<iList.size();jj++){
        int j=iList.get(jj);
        Atom aj= atoms.getAtom(j);
        //length
        double rij2=0.0;
        for(int l=0;l<3;l++){
          rij2+=( aj.pos[l]-ai.pos[l] )*( aj.pos[l]-ai.pos[l] );
        }

        //add j to iLengthList in sorted order
        boolean isAdded=false;
        for(int k=0;k<iLengthList.size()-1;k++){
          if(iLengthList.get(k).length<rij2 && rij2<iLengthList.get(k+1).length){
            iLengthList.add(k,new MyLength(j,rij2));
            isAdded=true;
            break;
          }
        }
        if(!isAdded)iLengthList.add(new MyLength(j,rij2));

      }//jj

      //add only kth components
      ArrayList<Integer> iNeighbor=new ArrayList<Integer>();
      if(iLengthList.size()>kth){
        for(int k=0;k<kth;k++)
          iNeighbor.add(iLengthList.get(k).n);
      }else{
        for(int k=0;k<iLengthList.size();k++)
          iNeighbor.add(iLengthList.get(k).n);
      }


      kthNeighbors.add(iNeighbor);
    }//i

    return kthNeighbors;

  }

  /**
   * ペアリスト生成
   */
  public static ArrayList makePairList(Atoms atoms, float rcut, boolean deleteVoxel,boolean pbcOn){
    //--- decide cell size for linked-list
    //int nx= (int)(atoms.h[0][0]/rcut)+1; // num of cells along x-direction
    //int ny= (int)(atoms.h[1][1]/rcut)+1;
    //int nz= (int)(atoms.h[2][2]/rcut)+1;

    float[][] h= atoms.hmat;

    double lx=Math.sqrt(h[0][0]*h[0][0]+
                        h[1][0]*h[1][0]+
                        h[2][0]*h[2][0]);
    double ly=Math.sqrt(h[0][1]*h[0][1]+
                        h[1][1]*h[1][1]+
                        h[2][1]*h[2][1]);
    double lz=Math.sqrt(h[0][2]*h[0][2]+
                        h[1][2]*h[1][2]+
                        h[2][2]*h[2][2]);

    int nx= (int)(lx/rcut)+1; // num of cells along x-direction
    int ny= (int)(ly/rcut)+1;
    int nz= (int)(lz/rcut)+1;

    if(nx < 3 || ny < 3 || nz < 3 ){
      // linked-list may not be necessary, because the system is small
      return makePairListDirectly( atoms,rcut,deleteVoxel,pbcOn );
    }else{ // enough large system
      int nlist= atoms.getNumAtoms() +nx*ny*nz; // size of the list
      int[] llist= new int[nlist];
      //--- make linked-list
      makeLinkedList( nx,ny,nz,nlist,llist,atoms,deleteVoxel );

      //--- make pair-list using linked-list
      return makePairListUsingLinkedList( rcut,nx,ny,nz,nlist,llist,atoms,deleteVoxel,pbcOn );
    }

  }

  // make linked-list
  public static void makeLinkedList(int nx, int ny, int nz, int nlist, int[] llist, Atoms atoms, boolean deleteVoxel){
    // [0:natm-1]=body, [natm:]=header
    for(int i=0; i<nlist; i++)
      llist[i]= -1;

    float dx=1.f/nx;
    float dy=1.f/ny;
    float dz=1.f/nz;
    int natm= atoms.getNumAtoms();
    
    for(int i=0; i<natm; i++){
      Atom ai= atoms.getAtom(i);
      int itag = ai.tag;
      if( itag < 0 ) continue;
      if( deleteVoxel &&  itag==Const.VOLUME_DATA_TAG ) break;//原子，volumedataの順で格納されているのを仮定
      float[] tp=MDMath.mulH( atoms.hmati, ai.pos );
      int ix= (int)(tp[0]/dx);
      if(ix<0)ix=0;
      if(ix>=nx)ix=nx-1;
      int iy= (int)(tp[1]/dy);
      if(iy<0)iy=0;
      if(iy>=ny)iy=ny-1;
      int iz= (int)(tp[2]/dz);
      if(iz<0)iz=0;
      if(iz>=nz)iz=nz-1;

      int ii= ix*ny*nz +iy*nz +iz;
      int j= llist[natm +ii];
      llist[i]= j;
      llist[natm +ii]= i;
    }
  }

  // make pair list (Verlet list) using linked-list
  static ArrayList makePairListUsingLinkedList(float rcut,int nx, int ny, int nz,
                                               int nlist,int[] llist,Atoms atoms, boolean deleteVoxel,boolean pbcOn){

    float rc2= rcut*rcut;
    ArrayList<ArrayList<Integer>> lspr= new ArrayList<ArrayList<Integer>>();
    float ds[]  = new float[3];
    int natm= atoms.getNumAtoms();

    for(int i=0;i<natm;i++){
      Atom ai= atoms.getAtom(i);
      int itag = ai.tag;
      if( itag < 0 )continue;
      //if( itag==Const.VOLUME_DATA_TAG)break;//原子，volumedataの順で格納されているのを仮定
      ArrayList<Integer> iList = new ArrayList<Integer>();
      float[] xi=MDMath.mulH(atoms.hmati, ai.pos);
      int ix= (int)(xi[0]*nx);
      int iy= (int)(xi[1]*ny);
      int iz= (int)(xi[2]*nz);
      for(int idx=-1;idx<=1;idx++){
        int jx=ix+idx;
        if(pbcOn){
          if(jx<0)jx=nx-1;
          if(jx>=nx)jx=0;;
        }else{
          if(jx<0 || jx>=nx)continue;
        }
        for(int idy=-1;idy<=1;idy++){
          int jy=iy+idy;
          if(pbcOn){
            if(jy<0)jy=ny-1;
            if(jy>=ny)jy=0;;
          }else{
            if(jy<0 || jy>=ny)continue;
          }
          for(int idz=-1;idz<=1;idz++){
            int jz=iz+idz;
            if(pbcOn){
              if(jz<0)jz=nz-1;
              if(jz>=nz)jz=0;;
            }else{
              if(jz<0 || jz>=nz)continue;
            }

            int jj=jx*ny*nz +jy*nz +jz;

            int j=llist[natm+jj];
            while(j>=0){
              Atom aj= atoms.getAtom(j);
              int jtag = aj.tag;
              if( j!=i && jtag > 0 && jtag!=Const.VOLUME_DATA_TAG){
                float[] xj=MDMath.mulH( atoms.hmati, aj.pos );
                for(int k=0;k<3;k++)
                  ds[k]= (xj[k]-xi[k]);
                //consider PBC
                if(pbcOn){
                  for(int k=0;k<3;k++){
                    if(ds[k] > 0.5f) ds[k]=ds[k]-1.0f;
                    if(ds[k] <-0.5f) ds[k]=ds[k]+1.0f;
                  }
                }

                float[] d=MDMath.mulH( atoms.hmat,ds );
                float r2= d[0]*d[0] +d[1]*d[1] +d[2]*d[2];
                if(r2 < rc2)iList.add(j);
              }
              j= llist[j];
            }
          }//idz
        }//idy
      }//idx

      lspr.add(iList);
    }

    return lspr;
  }


  // make pair-list directly, brute-force O(N^2) method
  private static ArrayList makePairListDirectly(Atoms atoms,float rcut, boolean deleteVoxel,boolean pbcOn){
    float rc2 = rcut*rcut;
    float ds[]  = new float[3];

    ArrayList<ArrayList<Integer>> lspr= new ArrayList<ArrayList<Integer>>();
    int natm= atoms.getNumAtoms();

    for(int i=0; i<natm; i++){
      Atom ai= atoms.getAtom(i);
      int itag = ai.tag;
      if( itag < 0 )continue;
      if( deleteVoxel && itag==Const.VOLUME_DATA_TAG ) break;//原子，volumedataの順で格納されているのを仮定
      ArrayList<Integer> iList = new ArrayList<Integer>();
      float[] xi=MDMath.mulH(atoms.hmati, ai.pos );
      for(int j=0; j<natm; j++){
        Atom aj= atoms.getAtom(j);
        if(i==j)continue;
        float[] xj=MDMath.mulH( atoms.hmati, aj.pos );
        for(int k=0;k<3;k++)
          ds[k]= (xj[k]-xi[k]);
        // PBC
        if(pbcOn){
          for(int k=0;k<3;k++){
            if(ds[k]> 0.5f) ds[k]=ds[k]-1.0f;
            if(ds[k]<-0.5f) ds[k]=ds[k]+1.0f;
          }
        }

        float[] d=MDMath.mulH(atoms.hmat,ds);
        float r2= d[0]*d[0] +d[1]*d[1] +d[2]*d[2];
        if(r2 < rc2)iList.add(j);
      }
      lspr.add(iList);
    }

    return lspr;
  }


  public static ArrayList readPairList(String bondFile,Atoms atoms){
    ArrayList<ArrayList<Integer>> lspr= new ArrayList<ArrayList<Integer>>();

    try {
      FileReader fr = new FileReader(bondFile);
      BufferedReader br = new BufferedReader( fr );
      String line;
      String[] elem;
      Tokens tokens = new Tokens();
      tokens.setDelim( " " );
      Exponent epnum = new Exponent();
      int natm= atoms.getNumAtoms();

      //read
      for(int i=0; i<natm; i++){
        Atom ai= atoms.getAtom(i);
        if( ai.tag==Const.VOLUME_DATA_TAG ) break;

        ArrayList<Integer> iList = new ArrayList<Integer>();

        //read
        line = br.readLine();
        //parse
        tokens.setString( line );
        elem = tokens.getTokens();
        for( int j=0; j<elem.length; j++ ){
          epnum.setString( elem[j] );
          iList.add((int)(epnum.getNumber())-1);//NOTE: index begin with 0
        }
        lspr.add(iList);
      }


      br.close();
      fr.close();
    }
    catch ( IOException e ){
      System.out.println("no read: "+bondFile);
    }

    return lspr;
  }

}
