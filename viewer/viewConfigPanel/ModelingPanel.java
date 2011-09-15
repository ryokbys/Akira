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
  int fnum=0;
  public void actionPerformed( ActionEvent e){
    int nx=(Integer)spNx.getValue();
    int ny=(Integer)spNy.getValue();
    int nz=(Integer)spNz.getValue();

    //plugin
    for(int i=0;i<plugins.size();i++){
      if(e.getActionCommand().equals(plugins.get(i).getPluginName())){
        fnum++;
        String dir;
        if(ctrl.getActiveRW()==null)
          dir= System.getProperty("user.home");
        else
          dir=ctrl.getActiveRW().getFileDirectory();

        JFileChooser chooser = new JFileChooser(dir);
        chooser.setDialogTitle("save to ...");
        chooser.setSelectedFile(new File(String.format("%04d.%s.Akira",fnum,plugins.get(i).getSaveFileName())));
        int s = chooser.showSaveDialog( null );
        if( s == JFileChooser.APPROVE_OPTION ){
          dir= chooser.getSelectedFile().getAbsolutePath();
        }else{
          return;
        }

        (plugins.get(i)).make(dir,nx,ny,nz);
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

    JPanel jp=new JPanel();
    jp.setLayout(new GridLayout(3,2));
    jp.add(new JLabel("Nx"));
    jp.add(spNx);
    jp.add(new JLabel("Ny"));
    jp.add(spNy);
    jp.add(new JLabel("Nz"));
    jp.add(spNz);

    JPanel pluginPanel=createPluginButton();
    SpringLayout layout = new SpringLayout();
    setLayout(layout);
    layout.putConstraint(SpringLayout.NORTH, jp, 5, SpringLayout.NORTH,this);
    layout.putConstraint(SpringLayout.SOUTH, jp, -5, SpringLayout.SOUTH,this);
    layout.putConstraint(SpringLayout.WEST, jp, 5, SpringLayout.WEST,this);

    layout.putConstraint(SpringLayout.NORTH, pluginPanel, 5, SpringLayout.NORTH,this);
    layout.putConstraint(SpringLayout.SOUTH, pluginPanel, -5, SpringLayout.SOUTH,this);
    layout.putConstraint(SpringLayout.EAST, pluginPanel, -5, SpringLayout.EAST,this);
    layout.putConstraint(SpringLayout.WEST, pluginPanel, 5, SpringLayout.EAST,jp);



    add(jp);
    add(pluginPanel);
  }

  private ArrayList<ModelingPluginInterface> plugins = new ArrayList<ModelingPluginInterface>();

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
  private JPanel createPluginButton(){
    String dir=vconf.pluginDir;
    //String dir=vconf.pluginDir+File.separator+"modeling"+File.separator;
    System.out.println("modeling plugin: "+dir);
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
    JPanel jp=new JPanel();
    jp.setLayout(new GridLayout(0,6));
    for(int i=0;i<plugins.size();i++){
      JButton btn=new JButton(plugins.get(i).getPluginName());
      btn.setActionCommand(plugins.get(i).getPluginName());
      btn.addActionListener( this );
      jp.add(btn);
    }
    return jp;
  }


}//ModelingPanel
