package tools;

import java.util.*;
import java.io.*;

import data.*;
import tools.*;

/**
 * create pair list
 */

public class PairList{

  private static float[] mulH( float[][] h,float[] in ){
    float[] out = new float[3];
    for(int k=0; k<3; k++)
      out[k] = (h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2]);
    return out;
  }

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
                                                 boolean deleteVoxel,boolean isConsiderPBC){
    ArrayList<ArrayList<Integer>> lspr= PairList.makePairList(atoms,rcut,deleteVoxel,isConsiderPBC);

    ArrayList<ArrayList<Integer>> kthNeighbors= new ArrayList<ArrayList<Integer>>();

    for(int i=0;i<atoms.n;i++){
      LinkedList<MyLength> iLengthList= new LinkedList<MyLength>();
      ArrayList<Integer> iList = lspr.get(i);
      for(int jj=0;jj<iList.size();jj++){
        int j=iList.get(jj);
        //length
        double rij2=0.0;
        for(int l=0;l<3;l++){
          rij2+=(atoms.r[j][l]-atoms.r[i][l])*(atoms.r[j][l]-atoms.r[i][l]);
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
        for(int k=0;k<kth;k++)iNeighbor.add(iLengthList.get(k).n);
      }else{
        for(int k=0;k<iLengthList.size();k++)iNeighbor.add(iLengthList.get(k).n);
      }


      kthNeighbors.add(iNeighbor);
    }//i

    return kthNeighbors;

  }

  /**
   * ペアリスト生成
   */
  public static ArrayList makePairList(Atoms atoms, float rcut, boolean deleteVoxel,boolean isConsiderPBC){
    //--- decide cell size for linked-list
    //int nx= (int)(atoms.h[0][0]/rcut)+1; // num of cells along x-direction
    //int ny= (int)(atoms.h[1][1]/rcut)+1;
    //int nz= (int)(atoms.h[2][2]/rcut)+1;

    double lx=Math.sqrt(atoms.h[0][0]*atoms.h[0][0]+
                        atoms.h[1][0]*atoms.h[1][0]+
                        atoms.h[2][0]*atoms.h[2][0]);
    double ly=Math.sqrt(atoms.h[0][1]*atoms.h[0][1]+
                        atoms.h[1][1]*atoms.h[1][1]+
                        atoms.h[2][1]*atoms.h[2][1]);
    double lz=Math.sqrt(atoms.h[0][2]*atoms.h[0][2]+
                        atoms.h[1][2]*atoms.h[1][2]+
                        atoms.h[2][2]*atoms.h[2][2]);

    int nx= (int)(lx/rcut)+1; // num of cells along x-direction
    int ny= (int)(ly/rcut)+1;
    int nz= (int)(lz/rcut)+1;

    if(nx < 3 || ny < 3 || nz < 3 ){
      // linked-list may not be necessary, because the system is small
      return makePairListDirectly(atoms,rcut,deleteVoxel,isConsiderPBC);
    }else{ // enough large system
      int nlist= atoms.n +nx*ny*nz; // size of the list
      int[] llist= new int[nlist];
      //--- make linked-list
      makeLinkedList(nx,ny,nz,nlist,llist,atoms,deleteVoxel);

      //--- make pair-list using linked-list
      return makePairListUsingLinkedList(rcut,nx,ny,nz,nlist,llist,atoms,deleteVoxel,isConsiderPBC);
    }

  }

  // make linked-list
  public static void makeLinkedList(int nx, int ny, int nz, int nlist, int[] llist, Atoms atoms, boolean deleteVoxel){
    // [0:natm-1]=body, [natm:]=header
    for(int i=0; i<nlist; i++)llist[i]= -1;

    float dx=1.f/nx;
    float dy=1.f/ny;
    float dz=1.f/nz;

    for(int i=0; i<atoms.n; i++){
      int itag = atoms.tag[i];
      if( itag < 0 )continue;
      if(deleteVoxel &&  itag==Const.VOLUME_DATA_TAG)break;//原子，volumedataの順で格納されているのを仮定
      float[] tp=mulH(atoms.hinv,atoms.r[i]);
      int ix= (int)(tp[0]/dx);
      int iy= (int)(tp[1]/dy);
      int iz= (int)(tp[2]/dz);
      int ii= ix*ny*nz +iy*nz +iz;
      int j= llist[atoms.n +ii];
      llist[i]= j;
      llist[atoms.n +ii]= i;
    }
  }

  // make pair list (Verlet list) using linked-list
  static ArrayList makePairListUsingLinkedList(float rcut,int nx, int ny, int nz,
                                               int nlist,int[] llist,Atoms atoms, boolean deleteVoxel,boolean isConsiderPBC){

    float rc2= rcut*rcut;
    ArrayList<ArrayList<Integer>> lspr= new ArrayList<ArrayList<Integer>>();
    float ds[]  = new float[3];

    for(int i=0;i<atoms.n;i++){
      int itag = atoms.tag[i];
      if( itag < 0 )continue;
      //if( itag==Const.VOLUME_DATA_TAG)break;//原子，volumedataの順で格納されているのを仮定
      ArrayList<Integer> iList = new ArrayList<Integer>();
      float[] xi=mulH(atoms.hinv,atoms.r[i]);
      int ix= (int)(xi[0]*nx);
      int iy= (int)(xi[1]*ny);
      int iz= (int)(xi[2]*nz);
      for(int idx=-1;idx<=1;idx++){
        int jx=ix+idx;
        if(isConsiderPBC){
          if(jx<0)jx=nx-1;
          if(jx>=nx)jx=0;;
        }else{
          if(jx<0 || jx>=nx)continue;
        }
        for(int idy=-1;idy<=1;idy++){
          int jy=iy+idy;
          if(isConsiderPBC){
            if(jy<0)jy=ny-1;
            if(jy>=ny)jy=0;;
          }else{
            if(jy<0 || jy>=ny)continue;
          }
          for(int idz=-1;idz<=1;idz++){
            int jz=iz+idz;
            if(isConsiderPBC){
              if(jz<0)jz=nz-1;
              if(jz>=nz)jz=0;;
            }else{
              if(jz<0 || jz>=nz)continue;
            }

            int jj=jx*ny*nz +jy*nz +jz;

            int j=llist[atoms.n+jj];
            while(j>=0){
              int jtag = atoms.tag[j];
              if( j!=i && jtag > 0 && jtag!=Const.VOLUME_DATA_TAG){
                float[] xj=mulH(atoms.hinv,atoms.r[j]);
                for(int k=0;k<3;k++)ds[k]= (xj[k]-xi[k]);
                //consider PBC
                if(isConsiderPBC){
                  for(int k=0;k<3;k++){
                    if(ds[k]>0.5)ds[k]=ds[k]-1.0f;
                    if(ds[k]<-0.5)ds[k]=ds[k]+1.0f;
                  }
                }

                float[] d=mulH(atoms.h,ds);
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
  private static ArrayList makePairListDirectly(Atoms atoms,float rcut, boolean deleteVoxel,boolean isConsiderPBC){
    float rc2 = rcut*rcut;
    float ds[]  = new float[3];

    ArrayList<ArrayList<Integer>> lspr= new ArrayList<ArrayList<Integer>>();


    for(int i=0; i<atoms.n; i++){
      int itag = atoms.tag[i];
      if( itag < 0 )continue;
      if(deleteVoxel && itag==Const.VOLUME_DATA_TAG)break;//原子，volumedataの順で格納されているのを仮定
      ArrayList<Integer> iList = new ArrayList<Integer>();
      float[] xi=mulH(atoms.hinv,atoms.r[i]);
      for(int j=0; j<atoms.n; j++){
        if(i==j)continue;
        float[] xj=mulH(atoms.hinv,atoms.r[j]);
        for(int k=0;k<3;k++)ds[k]= (xj[k]-xi[k]);
        //consider PBC
        if(isConsiderPBC){
          for(int k=0;k<3;k++){
            if(ds[k]>0.5)ds[k]=ds[k]-1.0f;
            if(ds[k]<-0.5)ds[k]=ds[k]+1.0f;
          }
        }

        float[] d=mulH(atoms.h,ds);
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

      //read
      for(int i=0; i<atoms.n; i++){
        if(atoms.tag[i]==Const.VOLUME_DATA_TAG)break;

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
