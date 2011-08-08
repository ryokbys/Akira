package tools.DelaunayTriangle.misc;

import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.ProgressMonitor;

import tools.DelaunayTriangle.event.DelaunayEvent;
import tools.DelaunayTriangle.event.DelaunayListener;

/**
 * DelaunayProgressMonitor
 * <pre>
 * Delaunay分割処理のプログレスモニター
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class DelaunayProgressMonitor extends ProgressMonitor implements
    DelaunayListener {

  private DelaunayEvent.Status status=DelaunayEvent.Status.COMPLETE;
  private float prog=0;
  private Map<DelaunayEvent.Status,Boolean> map;
  private long time;

  /**
   * コンストラクタ
   *
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public DelaunayProgressMonitor(Component arg0, Object arg1, String arg2,
      int arg3, int arg4) {
    super(arg0, arg1, arg2, arg3, arg4);
    map=new HashMap<DelaunayEvent.Status,Boolean>();
    map.put(DelaunayEvent.Status.GENE_TRIANGLE, true);
    map.put(DelaunayEvent.Status.DIVIDE_BOUNDARY, false);
    map.put(DelaunayEvent.Status.DIVIDE_TRIANGLE, false);
    map.put(DelaunayEvent.Status.PROC_LAPLAS, false);
  }

  public void setSwitchAllOff(){
    Set<DelaunayEvent.Status> set=new HashSet<DelaunayEvent.Status>();
    set.addAll(map.keySet());
    Iterator<DelaunayEvent.Status> it=set.iterator();
    while(it.hasNext()){
      map.put(it.next(), false);
    }
  }

  public void setSwitchAllOn(){
    Set<DelaunayEvent.Status> set=new HashSet<DelaunayEvent.Status>();
    set.addAll(map.keySet());
    Iterator<DelaunayEvent.Status> it=set.iterator();
    while(it.hasNext()){
      map.put(it.next(), true);
    }
  }

  public void setSwitch(DelaunayEvent.Status st,boolean b){
    map.put(st,b);
  }

  public void delaunayEvenet(final DelaunayEvent evt) {
    DelaunayEvent.Status st=evt.getStatus();
    if(status==DelaunayEvent.Status.COMPLETE&&st==status)return;
    if(status!=DelaunayEvent.Status.COMPLETE&&st==DelaunayEvent.Status.COMPLETE){
      status=st;
      Runnable r=new Runnable(){
        public void run() {
          setProgress(getMaximum());
          close();
          time=System.currentTimeMillis()-time;
          System.out.println("processing time="+(((double)time)/1000.0)+"[s]");
        }
      };
      javax.swing.SwingUtilities.invokeLater(r);
    }else if(status==DelaunayEvent.Status.COMPLETE&&st!=DelaunayEvent.Status.COMPLETE){
      time=System.currentTimeMillis();
      status=st;
      if(!map.get(st))return;
      prog=(float)(getMaximum()-getMinimum());
      Runnable r=new Runnable(){
        public void run() {
          setNote(evt.getActionCommand());
          setProgress((int)(prog*evt.getProgress()));
        }
      };
      javax.swing.SwingUtilities.invokeLater(r);
    }else{
      if(!map.get(st))return;
      setProgress((int)(prog*evt.getProgress()));

      Runnable r=new Runnable(){
        public void run() {
          if(status==DelaunayEvent.Status.COMPLETE)return;
          setProgress((int)(prog*evt.getProgress()));
        }
      };
      javax.swing.SwingUtilities.invokeLater(r);
    }
  }

}
