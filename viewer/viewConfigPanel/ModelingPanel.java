package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.lang.reflect.Method;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.awt.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.viewConfigPanel.plugin.*;
import viewer.*;
import data.*;

/**
 * Molecular Dynamics
 */
public class ModelingPanel extends JPanel implements ActionListener{

  Controller ctrl;
  private ViewConfig vconf;
  public ModelingPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
  }

  public void actionPerformed( ActionEvent e){
    int nx=(Integer)spNx.getValue();
    int ny=(Integer)spNy.getValue();
    int nz=(Integer)spNz.getValue();

    //plugin
    for(int i=0;i<plugins.size();i++){
      if(e.getActionCommand().equals(pluginName.get(i))){
        (plugins.get(i)).make(nx,ny,nz);
        break;
      }
    }

  }

  private JSpinner spNx,spNy,spNz;
  private void createPanel(){
    spNx = new JSpinner(new SpinnerNumberModel(4, 0, null, 1));
    spNx.setFocusable(false);
    spNx.setPreferredSize(new Dimension(50, 25));
    spNy = new JSpinner(new SpinnerNumberModel(4, 0, null, 1));
    spNy.setFocusable(false);
    spNy.setPreferredSize(new Dimension(50, 25));
    spNz = new JSpinner(new SpinnerNumberModel(4, 0, null, 1));
    spNz.setFocusable(false);
    spNz.setPreferredSize(new Dimension(50, 25));

    add(spNx);
    add(spNy);
    add(spNz);

    createPluginButton();
  }

  private ArrayList<ModelingPluginInterface> plugins = new ArrayList<ModelingPluginInterface>();
  private ArrayList<String> pluginName = new ArrayList<String>();
  static void addClassPathToClassLoader(File classPath){
    try{
      URLClassLoader classLoader=(URLClassLoader) ClassLoader.getSystemClassLoader();
      Class classClassLoader = URLClassLoader.class;
      Method methodAddUrl = classClassLoader.getDeclaredMethod("addURL", URL.class);
      methodAddUrl.setAccessible(true);
      methodAddUrl.invoke(classLoader, classPath.toURI().toURL());
      //System.out.println("added "+classPath);
    }catch(Exception e){
      //e.printStackTrace();
    }
  }
  private void createPluginButton(){
    String dir=vconf.pluginDir;
    //String dir=vconf.pluginDir+File.separator+"modeling"+File.separator;
    System.out.println("model plugin: "+dir);
    try {
      File f = new File(dir);
      String[] files = f.list();
      for (int i = 0; i < files.length; i++) {
        if (files[i].endsWith(".class")){
          //eliminate ".class"
          String classname = files[i].substring(0,files[i].length() - ".class".length());
          Class c = Class.forName("plugin."+classname);
          Class[] ifs = c.getInterfaces();//interface name
          for(int j = 0; j < ifs.length; j++){
            if(ifs[j].equals(ModelingPluginInterface.class)){
              addClassPathToClassLoader(new File(f.getAbsolutePath()+File.separator+files[i]));
              ModelingPluginInterface plg = (ModelingPluginInterface)c.newInstance();
              plugins.add(plg);
              pluginName.add(plg.getName());
              System.out.println("modeling plugin; "+classname+".class is added");
            }
          }//j
        }//if
      }//i
    } catch (ClassNotFoundException ex) {
      //System.out.println(" --noclass");
      //ex.printStackTrace();
    }catch(Exception ex){
      //System.out.println(" --exception");
      //ex.printStackTrace();
    }

    //add
    for(int i=0;i<plugins.size();i++){
      JButton btn=new JButton(pluginName.get(i));
      btn.setActionCommand(pluginName.get(i));
      btn.addActionListener( this );
      add(btn);
    }

  }


}//ModelingPanel
