package tools.DelaunayTriangle;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import tools.DelaunayTriangle.misc.DelaunayException;

/**
 * Delaunay2D
 * <pre>
 * 二次元Delaunay分割処理クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class Delaunay2D implements Delaunay{

  private static final long serialVersionUID = 1L;
  private List<double[]> data;
  private List<Point2D> node;
  private List<int[]> elem;
  private List<int[]> neigh;
  private Map<Integer,Integer> boundary;
  private Map<Integer,Integer> boundaryID;
  private double maxX,minX,maxY,minY,min,max;
  private static double EPS=1e-12;
  private int ids=0;
  private int bid=1;

  /**
   * コンストラクタ
   *
   * @param _minX 処理対象領域のx座標最小値
   * @param _maxX 処理対象領域のx座標最大値
   * @param _minY 処理対象領域のy座標最小値
   * @param _maxY 処理対象領域のy座標最大値
   */
  public Delaunay2D(double _minX,double _maxX,double _minY,double _maxY){
    data=new ArrayList<double[]>();
    node=new ArrayList<Point2D>();
    elem=new ArrayList<int[]>();
    neigh=new ArrayList<int[]>();
    boundary=new HashMap<Integer,Integer>();
    minX=_minX;minY=_minY;
    maxX=_maxX;maxY=_maxY;
    min=Math.min(minX, minY);
    max=Math.max(maxX, maxY);
    Point2D[] sp=new Point2D[3];
    sp[0]=new Point2D.Double(-1.23,-0.5);
    sp[1]=new Point2D.Double(2.23,-0.5);
    sp[2]=new Point2D.Double(0.5,2.5);
    for(int i=0;i<sp.length;i++)node.add(sp[i]);
    elem.add(new int[]{0,1,2});
    neigh.add(new int[]{-1,-1,-1});
    boundaryID=new HashMap<Integer,Integer>();
  }

  /**
   * コンストラクタ
   *
   * @param r 処理対象領域（Rectangle2D）
   */
  public Delaunay2D(Rectangle2D r){
    this(r.getX(),r.getX()+r.getWidth(),r.getY(),r.getHeight());
  }

  /**
   * コンストラクタ
   *
   * @param area 処理対象領域（x最小座標、x最大座標、y最小座標、y最大座標）
   */
  public Delaunay2D(double[] area){
    this(area[0],area[1],area[2],area[3]);
  }

  /**
   * コンストラクタ
   *
   * @param md メッシュデータ（MeshData2D）
   */
  public Delaunay2D(MeshData2D md){
    this(md.getMinMax());
    double[][] d=md.getNode();
    Point2D[] pos=new Point2D[d.length];
    for(int i=0;i<pos.length;i++){
      pos[i]=normalizePos(d[i]);
    }
    node.clear();
    for(int i=0;i<d.length;i++){
      data.add(d[i]);
      node.add(pos[i]);
    }
    int[][] e=md.getElem();
    elem.clear();
    for(int i=0;i<e.length;i++){
      elem.add(e[i]);
    }
    int[][] m=md.getMap();
    neigh.clear();
    for(int i=0;i<m.length;i++){
      neigh.add(m[i]);
    }
    int[][] b=md.getBoundary();
    for(int i=0;i<b.length;i++){
      boundary.put(b[i][0], b[i][1]);
      boundaryID.put(b[i][0], b[i][2]);
    }
  }

  /**
   * Delaunay2Dの全データを取得（スーパーノード含む）
   *
   * @return メッシュデータ（MeshData2D）
   */
  public MeshData2D getDelaunayData(){
    List<double[]> tmp=new ArrayList<double[]>();
    tmp.addAll(data);
    double[][] sn=this.getTransformedSuperNode();
    for(int i=0;i<sn.length;i++){
      tmp.add(i,sn[i]);
    }
    MeshData2D m=new MeshData2D(
        tmp.toArray(new double[tmp.size()][]),
        elem.toArray(new int[elem.size()][]),
        neigh.toArray(new int[neigh.size()][]),getBoundary());
    m.setMinMax(new double[]{minX,maxX,minY,maxY});
    return m;
  }

  /**
   *
   * メッシュデータを取得（スーパーノード等削除）
   *
   * @return メッシュデータ（MeshData2D）
   */
  public MeshData2D getMeshData(){
    int[] et=new int[elem.size()];
    int id=0;
    for(int i=0;i<et.length;i++){
      int[] e=elem.get(i);
      if(e[0]<3||e[1]<3||e[2]<3){
        et[i]=-1;
      }else if(!isInBoundary(i)){
        et[i]=-1;
      }else{
        et[i]=id++;
      }
    }
    List<int[]> ee=new ArrayList<int[]>();
    List<int[]> me=new ArrayList<int[]>();
    int[] tp=new int[3];
    for(int i=0;i<et.length;i++){
      int[] e=elem.get(i);
      int[] m=neigh.get(i);
      if(et[i]!=-1){
        ee.add(new int[]{e[0]-3,e[1]-3,e[2]-3});
        for(int j=0;j<m.length;j++){
          if(m[j]==-1){
            tp[j]=-1;
          }else{
            tp[j]=et[m[j]];
          }
        }
        me.add(new int[]{tp[0],tp[1],tp[2]});
      }
    }
    Point2D[] pos=new Point2D[data.size()];
    for(int i=0;i<pos.length;i++){
      pos[i]=node.get(i+3);
    }
    MeshData2D m=new MeshData2D(
      getNode(),ee.toArray(new int[ee.size()][]),
        me.toArray(new int[me.size()][]),this.getBoundary(me));
    m.setMinMax(new double[]{minX,maxX,minY,maxY});
    return m;
  }

  /*
   * 節点配列を取得する。
   *
   * @return double[]{x座標,y座標,･･･}
   */
  private double[][] getNode(){
    return data.toArray(new double[data.size()][]);
  }

  /*
   * 境界データの取得
   *
   * @return int[] 境界節点のID
   */
  private int[][] getBoundary(){
    int n=elem.size();
    int[][] bb=new int[n][3];
    for(int i=0;i<n;i++){
      int[] e=elem.get(i);
      for(int j=0;j<3;j++){
        if(isBoundary(e[j],e[(j+1)%3])){
          bb[i][j]=boundaryID.get(e[j]);
        }else{
          bb[i][j]=0;
        }
      }
    }
    return bb;
  }

  /*
   * 境界データの取得
   *
   * @return int[] 境界節点のID
   */
  private int[][] getBoundary(List<int[]> ex){
    int n=ex.size();
    int[][] bb=new int[n][3];
    for(int i=0;i<n;i++){
      int[] e=ex.get(i);
      for(int j=0;j<3;j++){
        if(isBoundary(e[j]+3,e[(j+1)%3]+3)){
          bb[i][j]=boundaryID.get(e[j]-3);
        }else{
          bb[i][j]=0;
        }
      }
    }
    return bb;
  }

  public boolean insertNode(double[] val){
    Point2D p=normalizePos(val);
    ids=getLocation(ids,p);
    if(ids==-1){
      ids=0;
      return false;
    }
    if(isInBoundary(ids)){
      data.add(val);
      node.add(p);
      execDelaunay(ids,node.size()-1);
      return true;
    }else{
      return false;
    }
  }

  public void addBoundary(double[][] arg,boolean isClose){
    int sz=node.size();
    for(int i=0;i<arg.length;i++){
      Point2D p=normalizePos(arg[i]);
      if(node.contains(p))continue;
      ids=getLocation(ids,p);
      if(ids==-1){
        ids=0;
        continue;
      }
      if(isInBoundary(ids)){
        data.add(arg[i]);
        node.add(p);
        execDelaunay(ids,node.size()-1);
        if(i>0){
          boundary.put(sz+i-1,sz+i);
          boundaryID.put(sz+i-1, bid);
        }
      }
    }
    if(isClose){
      boundary.put(node.size()-1,sz);
      boundaryID.put(node.size()-1, bid);
    }
    bid++;
  }

  /*
   * 領域のチェック
   */
  private boolean checkBounds(int id){
    int[] e=this.elem.get(id);
    if(e[0]<3||e[1]<3||e[2]<3){
      return false;
    }else{
      return true;
    }
  }

  /*
   * 境界の判定
   *
   */
  private boolean isInBoundary(int id){
    if(this.bid>1){
      if(!this.checkBounds(id))return false;
    }
    int[] e=elem.get(id);
    for(int i=0;i<e.length;i++){
      Integer b0=boundaryID.get(e[i]);
      if(b0==null)continue;
      Integer b1=boundaryID.get(e[(i+1)%3]);
      if(b1==null)continue;
      if(b0.intValue()-b1.intValue()==0){
        Integer v0=boundary.get(e[(i+1)%3]);
        if(v0==null)continue;
        if(e[i]==v0.intValue()){
          return false;
        }
      }
    }
    return true;
  }

  /*
   * 要素分割処理
   */
  private void execDelaunay(int elemId,int nodeId){
    Stack<Integer> stack=new Stack<Integer>();
    int nn=elem.size();
    int[] em=elem.get(elemId);
    int[] te=new int[]{em[0],em[1],em[2]};
    int[] e0=new int[]{nodeId,em[0],em[1]};
    int[] e1=new int[]{nodeId,em[1],em[2]};
    int[] e2=new int[]{nodeId,em[2],em[0]};
    em[0]=e0[0];em[1]=e0[1];em[2]=e0[2];
    elem.add(e1);elem.add(e2);
    int[] jm=neigh.get(elemId);
    int[] tmp=new int[]{jm[0],jm[1],jm[2]};
    int[] j0=new int[]{nn+1,jm[0],nn};
    int[] j1=new int[]{elemId,jm[1],nn+1};
    int[] j2=new int[]{nn,jm[2],elemId};
    jm[0]=j0[0];jm[1]=j0[1];jm[2]=j0[2];
    neigh.add(j1);neigh.add(j2);
    if(tmp[0]!=-1){
      if(!isBoundary(te[0],te[1]))stack.push(elemId);
      }
    if(tmp[1]!=-1){
      int ix=edge(tmp[1],elemId,neigh);
      neigh.get(tmp[1])[ix]=nn;
      if(!isBoundary(te[1],te[2]))stack.push(nn);
    }
    if(tmp[2]!=-1){
      int ix=edge(tmp[2],elemId,neigh);
      neigh.get(tmp[2])[ix]=nn+1;
      if(!isBoundary(te[2],te[0]))stack.push(nn+1);
    }
    while(!stack.isEmpty()){
      int il=stack.pop();
      int ir=neigh.get(il)[1];
      int ierl=edge(ir,il,neigh)%3;
      int iera=(ierl+1)%3;
      int ierb=(iera+1)%3;
      int iv1=elem.get(ir)[ierl];
      int iv2=elem.get(ir)[iera];
      int iv3=elem.get(ir)[ierb];
      if(isSwap(iv1,iv2,iv3,nodeId)){
        int ja=neigh.get(ir)[iera];
        int jb=neigh.get(ir)[ierb];
        int jc=neigh.get(il)[2];
        elem.get(il)[2]=iv3;
        neigh.get(il)[1]=ja;
        neigh.get(il)[2]=ir;
        int[] picElem=elem.get(ir);
        picElem[0]=nodeId;picElem[1]=iv3;picElem[2]=iv1;
        picElem=neigh.get(ir);
        picElem[0]=il;picElem[1]=jb;picElem[2]=jc;
        if(ja!=-1){
          int ix=edge(ja,ir,neigh);
          neigh.get(ja)[ix]=il;
          if(!isBoundary(iv2,iv3))stack.push(il);
        }
        if(jb!=-1){
          if(!isBoundary(iv3,iv1))stack.push(ir);
        }
        if(jc!=-1){
          int ix=edge(jc,il,neigh);
          neigh.get(jc)[ix]=ir;
        }
      }
    }
  }

  /*
   * 対象とする辺(nodeID0-nodeID1)が境界か否かを判別
   */
  private boolean isBoundary(int nodeId0,int nodeId1){
    Integer p=boundary.get(nodeId0);
    if(p!=null&&p.intValue()==nodeId1)return true;
    p=boundary.get(nodeId1);
    if(p!=null&&p.intValue()==nodeId0){
      return true;
    }else{
      return false;
    }
  }

  /*
   * 2つの要素の共用辺番号を取得
   */
  private int edge(int elemId,int targeId,List<int[]> map){
    int[] j=map.get(elemId);
    for(int i=0;i<j.length;i++){
      if(j[i]==targeId)return i;
    }
    throw new DelaunayException("#edge-> Elements not ajustment.");
  }

  /*
   * スワッピング処理の要否を判別
   */
  private boolean isSwap(int aId,int bId,int cId,int pId){
    double x13=node.get(aId).getX()-node.get(cId).getX();
    double y13=node.get(aId).getY()-node.get(cId).getY();
    double x23=node.get(bId).getX()-node.get(cId).getX();
    double y23=node.get(bId).getY()-node.get(cId).getY();
    double x1p=node.get(aId).getX()-node.get(pId).getX();
    double y1p=node.get(aId).getY()-node.get(pId).getY();
    double x2p=node.get(bId).getX()-node.get(pId).getX();
    double y2p=node.get(bId).getY()-node.get(pId).getY();
    double cosa=x13*x23+y13*y23;
    double cosb=x2p*x1p+y1p*y2p;
    if(cosa>=0&&cosb>=0){
      return false;
    }else if(cosa<0&&cosb<0){
      return true;
    }else{
      double sina=x13*y23-x23*y13;
      double sinb=x2p*y1p-x1p*y2p;
      if((sina*cosb+sinb*cosa)<0){
        return true;
      }else{
        return false;
      }
    }
  }

  /*
   * 節点が位置する要素のIDを取得。
   * -1は、領域外に位置することを示す。
   *
   */
  private int getLocation(int id,Point2D p){
    int[] em=elem.get(id);
    for(int i=0;i<em.length;i++){
      Point2D a=node.get(em[i]);
      Point2D b=node.get(em[(i+1)%3]);
      if(isLeft(a,b,p)<0){
        int n=neigh.get(id)[i];
        if(n==-1)return -1;
        return getLocation(n,p);
      }
    }
    return id;
  }

  /**
   * 点xyが含まれる要素のID番号を取得する。
   *
   * @param id　初期ID
   * @param xy　点
   * @return　要素ID
   */
  public int getLocation(int id,double[] xy){
    Point2D p=this.normalizePos(xy);
    int[] em=elem.get(id);
    for(int i=0;i<em.length;i++){
      Point2D a=node.get(em[i]);
      Point2D b=node.get(em[(i+1)%3]);
      if(isLeft(a,b,p)<0){
        int n=neigh.get(id)[i];
        if(n==-1)return -1;
        return getLocation(n,p);
      }
    }
    return id;
  }

  /*
   * double[]より、正規化されたPoint2Dを取得
   *
   */
  private Point2D normalizePos(double[] val){
    double xx=(val[0]-min)/(max-min);
    double yy=(val[1]-min)/(max-min);
    return new Point2D.Double(xx,yy);
  }

  private double[][] getTransformedSuperNode(){
    double[][] ret=new double[3][2];
    for(int i=0;i<ret.length;i++){
      Point2D p=node.get(i);
      ret[i][0]=p.getX()*(maxX-minX)+minX;
      ret[i][1]=p.getY()*(maxY-minY)+minY;
    }
    return ret;
  }

  /*
   * 辺と節点の位置関係を取得
   *
   * 返値が0の場合、節点は辺上に位置する。
   * 返値が0より大きい場合、節点は辺の左側に位置する。
   * 返値が0未満の場合、節点は辺の右側に位置する。
   *
   */
  private static double isLeft(Point2D a,Point2D b,Point2D p){
    double v0=(a.getY()-p.getY())*(b.getX()-p.getX());
    double v1=(a.getX()-p.getX())*(b.getY()-p.getY());
    if(Math.abs(v1-v0)<EPS){
      return 0;
    }else{
      return (v1-v0);
    }
  }

  /**
   * 許容誤差の設定
   *
   * @param eps 誤差
   */
  public static void setEPS(double eps){
    EPS=eps;
  }
}
