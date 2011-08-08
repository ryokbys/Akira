package tools.DelaunayTriangle.misc;

import java.util.ArrayList;
import java.util.List;

import tools.DelaunayTriangle.Delaunay;
import tools.DelaunayTriangle.MeshUtility2D;
import tools.DelaunayTriangle.event.DelaunayEvent;
import tools.DelaunayTriangle.event.DelaunayListener;

/**
 * DelaunayController
 * <pre>
 * Delaunay分割処理のコントロールクラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class DelaunayController {
  private Delaunay del;
  private double[][] data;
  private List<double[][]> boundary;
  private List<DelaunayListener> listeners;
  private List<int[]> error;

  /**
   * コンストラクタ
   *
   * @param d　Delaunay
   * @param n　節点配列
   */
  public DelaunayController(Delaunay d,double[][] n){
    listeners=new ArrayList<DelaunayListener>();
    data=n;
    del=d;
    boundary=new ArrayList<double[][]>();
  }

  /**
   * コンストラクタ
   *
   * @param d　Delaunay
   * @param n　節点配列
   * @param b　境界配列
   */
  public DelaunayController(Delaunay d,double[][] n,List<double[][]> b){
    listeners=new ArrayList<DelaunayListener>();
    data=n;
    del=d;
    boundary=b;
  }

  /**
   * 分割処理実行
   * (二次元での節点の重複検査を行わない）
   *
   */
  public void proc3D(){
    DelaunayEvent evt=new DelaunayEvent(this,DelaunayEvent.Status.GENE_TRIANGLE);
    fireDelaunayEvent(evt);
    int iz=data.length/20;
    float num=(float)data.length;
    for(int i=0;i<data.length;i++){
      if(!del.insertNode(data[i])){
        throw new DelaunayException("The No."+Integer.toString(i)+" node wasn't able to be inserted.");
      };
      if(i%iz==0){
        evt.setProgress((float)i/num);
        fireDelaunayEvent(evt);
      }
    }
    evt.setProgress(1.0f);
    evt.setStatus(DelaunayEvent.Status.COMPLETE);
    fireDelaunayEvent(evt);
  }

  /**
   * 分割処理実行
   * (二次元での節点の重複検査を行う）
   *
   */
  public void proc2D(){
    DelaunayEvent evt=new DelaunayEvent(this,DelaunayEvent.Status.GENE_TRIANGLE);
    fireDelaunayEvent(evt);
    List<double[][]> ll=new ArrayList<double[][]>();
    ll.addAll(boundary);
    ll.add(data);
    error=MeshUtility2D.isNodeOverlaps(ll);
    if(error.size()>0)throw new DelaunayException("The node overlaps. ");
    for(double[][] dd : boundary){
      del.addBoundary(dd, true);
    }
    int iz=data.length/20;
    float num=(float)data.length;
    for(int i=0;i<data.length;i++){
      if(!del.insertNode(data[i])){
        throw new DelaunayException("The No."+Integer.toString(i)+" node wasn't able to be inserted.");
      };
      if(i%iz==0){
        evt.setProgress((float)i/num);
        fireDelaunayEvent(evt);
      }
    }
    evt.setProgress(1.0f);
    evt.setStatus(DelaunayEvent.Status.COMPLETE);
    fireDelaunayEvent(evt);
  }

  /**
   * 節点の重複リストを取得
   *
   * @return　重複リスト
   */
  public List<int[]> getOverlapNodeList(){
    return error;
  }

  /*
   * リスナーへの通知
   */
  private void fireDelaunayEvent(DelaunayEvent evt){
    int n=listeners.size();
    if(n==0)return;
    for(int i=0;i<n;i++){
      listeners.get(i).delaunayEvenet(evt);
    }
  }

  /**
   * DelaunayListsnerを追加
   *
   * @param ll DelaunayListsner
   * @return 追加処理の適否
   */
  public boolean addDelaunayListener(DelaunayListener ll){
    return listeners.add(ll);
  }

  /**
   * DelaunayListsnerを除去
   *
   * @param ll DelaunayListsner
   * @return 除去処理の適否
   */
  public boolean removeDelaunayListener(DelaunayListener ll){
    return listeners.remove(ll);
  }

}
