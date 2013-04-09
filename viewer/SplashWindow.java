package viewer;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;


public class SplashWindow extends Window {

  private static SplashWindow instance;
  private Image image;
  private boolean paintCalled = false;

  private SplashWindow(Frame parent, Image image){
    super(parent);
    this.image = image;

    parent.setAlwaysOnTop(true);

    // make background opaque
    setBackground( new Color(0x00000000, true) );

    // Load the image
    MediaTracker mt = new MediaTracker(this);
    mt.addImage(image,0);
    try {
      mt.waitForID(0);
    } catch(InterruptedException ie){}

    // Abort on failure
    if(mt.isErrorID(0)){
      setSize(0,0);
      System.err.println("Warning: SplashWindow couldn't load splash image.");
      synchronized(this){
        paintCalled = true;
        notifyAll();
      }
      return;
    }

    // Center the window on the screen
    int imgWidth = image.getWidth(this);
    int imgHeight = image.getHeight(this);
    setSize(imgWidth, imgHeight);
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(
                (screenDim.width - imgWidth) / 2,
                (screenDim.height - imgHeight) / 2
                );

    // Users shall be able to close the splash window by
    // clicking on its display area. This mouse listener
    // listens for mouse clicks and disposes the splash window.
    MouseAdapter disposeOnClick = new MouseAdapter(){
        public void mouseClicked(MouseEvent evt){
          // Note: To avoid that method splash hangs, we
          // must set paintCalled to true and call notifyAll.
          // This is necessary because the mouse click may
          // occur before the contents of the window
          // has been painted.
          synchronized(SplashWindow.this){
            SplashWindow.this.paintCalled = true;
            SplashWindow.this.notifyAll();
          }
          dispose();
        }
      };
    addMouseListener(disposeOnClick);
  }

  /**
   * Updates the display area of the window.
   */
  public void update(Graphics g){
    // Note: Since the paint method is going to draw an
    // image that covers the complete area of the component we
    // do not fill the component with its background color
    // here. This avoids flickering.
    paint(g);
  }
  /**
   * Paints the image on the window.
   */
  public void paint(Graphics g){
    g.drawImage(image, 0, 0, this);

    // Notify method splash that the window
    // has been painted.
    // Note: To improve performance we do not enter
    // the synchronized block unless we have to.
    if(! paintCalled){
      paintCalled = true;
      synchronized (this){ notifyAll(); }
    }
  }

  public static void splash(Image image){
    if(instance == null && image != null){
      Frame f = new Frame();
      // Create the splash image
      instance = new SplashWindow(f, image);
      // Show the window.
      //instance.show();
      instance.setVisible(true);

      // Note: To make sure the user gets a chance to see the
      // splash window we wait until its paint method has been
      // called at least once by the AWT event dispatcher thread.
      // If more than one processor is available, we don't wait,
      // and maximize CPU throughput instead.
      if(! EventQueue.isDispatchThread()
          && Runtime.getRuntime().availableProcessors() == 1){
        synchronized (instance){
          while (! instance.paintCalled){
            try { instance.wait(); } catch (InterruptedException e){}
          }
        }
      }
    }else{
      System.out.println("image not found");
    }
  }
  public static void splash(URL imageURL){
    if(imageURL != null){
      splash(Toolkit.getDefaultToolkit().createImage(imageURL));
    }
  }

  public static void disposeSplash(){
    if(instance != null){
      instance.getOwner().dispose();
      instance = null;
    }
  }

  public static void invokeMain(String className, String[] args){
    try {
      Class.forName(className)
        .getMethod("main", new Class[] {String[].class})
        .invoke(null, new Object[] {args});
    } catch (Exception e){
      InternalError error = new InternalError("Failed to invoke main method");
      error.initCause(e);
      throw error;
    }
  }
}
