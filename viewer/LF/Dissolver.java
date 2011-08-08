package viewer.LF;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

public class Dissolver extends JComponent implements Runnable {
  static int dissolveType=5;

  int countMax=20;
  int sleepCount=50;
  public Dissolver() {
    type=(int)(Math.random()*dissolveType);

    //type=5;

    if(type==3 || type==4 || type==5){
      countMax*=10;
      sleepCount/=10;
    }
  }
  public void dissolveExit(JFrame frame) {
    try {
      this.frame = frame;
      Robot robot = new Robot();

      // cap screen w/ frame to frame buffer
      Rectangle frame_rect = frame.getBounds();
      frame_buffer = robot.createScreenCapture(frame_rect);

      // hide frame
      frame.setVisible(false);

      // cap screen w/o frame
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      Rectangle screen_rect = new Rectangle(0,0,screensize.width, screensize.height);
      screen_buffer = robot.createScreenCapture(screen_rect);

      // create big window w/o decorations
      fullscreen = new Window(new JFrame());
      fullscreen.setSize(screensize);
      fullscreen.add(this);
      this.setSize(screensize);

      fullscreen.setVisible(true);

      // start animation
      new Thread(this).start();
    } catch (Exception ex) {
      System.out.println(ex);
      ex.printStackTrace();
    }
  }

  public void run(){
    try {
      count = 0;
      for(int i=0; i<countMax; i++) {
        count = i;
        fullscreen.repaint();
        Thread.currentThread().sleep(sleepCount);
      }
    } catch (InterruptedException ex) {
    }
    fullscreen.dispose();
  }

  Frame frame;
  Window fullscreen;
  BufferedImage frame_buffer;
  BufferedImage screen_buffer;
  int count;
  int type;

  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;

    AffineTransform old_trans;
    float scale;
    Composite fade,old_comp;
    switch (type) {
    case 1: //spin out
      // draw the screen, offset in case the window isn't at 0,0
      g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
      // save the current transform
      old_trans = g2.getTransform();
      // move to the upper left hand corner of the frame
      g2.translate(frame.getX(), frame.getY());
      // move the frame off towards the left
      g2.translate(-((count+1) * (frame.getX()+frame.getWidth())/20),0);
      // shrink the frame
      scale = 1f / ((float)count+1);
      g2.scale(scale,scale);
      // rotate around the center
      g2.rotate(((float)count)/3.14/1.3, frame.getWidth()/2, frame.getHeight()/2);
      // finally draw the frame
      g2.drawImage(frame_buffer,0,0,null);
      // restore the current transform
      g2.setTransform(old_trans);
      break;

    case 2: //scale effect
      // draw the screen, offset in case the window isn't at 0,0
      g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
      // save the current transform
      old_trans = g2.getTransform();
      // move to the upper left hand corner of the frame
      g2.translate(frame.getX(), frame.getY());
      // move the frame off towards the left
      g2.translate(-((count+1) * (frame.getX()+frame.getWidth())/20),0);
      // shrink the frame
      scale = 1f / ((float)count+1);
      g2.scale(scale,scale);
      // finally draw the frame
      g2.drawImage(frame_buffer,0,0,null);
      // restore the current transform
      g2.setTransform(old_trans);
      break;

    case 3: //swing x
      // draw the screen, offset in case the window isn't at 0,0
      g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
      // save the current transform
      old_trans = g2.getTransform();
      // move to the upper left hand corner of the frame
      g2.translate(frame.getX(), frame.getY());
      // swing x
      g2.translate
        (-Math.sin((double)(count+1)*100),0);
      //decrease alpha
      fade =AlphaComposite.getInstance
        (AlphaComposite.SRC_OVER,1.0f-((float)count)/(float)countMax);
      g2.setComposite(fade);
      // finally draw the frame
      g2.drawImage(frame_buffer,0,0,null);
      // restore the current transform
      g2.setTransform(old_trans);
      break;
    case 4: //swing y
      // draw the screen, offset in case the window isn't at 0,0
      g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
      // save the current transform
      old_trans = g2.getTransform();
      // move to the upper left hand corner of the frame
      g2.translate(frame.getX(), frame.getY());
      // swing y
      g2.translate
        (0,-Math.sin((double)(count+1)*50));
      //decrease alpha
      fade =AlphaComposite.getInstance
        (AlphaComposite.SRC_OVER,1.0f-((float)count)/(float)countMax);
      g2.setComposite(fade);
      // finally draw the frame
      g2.drawImage(frame_buffer,0,0,null);
      // restore the current transform
      g2.setTransform(old_trans);
      break;
    case 5: //chu chu train
      // draw the screen, offset in case the window isn't at 0,0
      g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
      // save the current transform
      old_trans = g2.getTransform();
      // move to the upper left hand corner of the frame
      g2.translate(frame.getX(), frame.getY());
      // swing x,y
      g2.translate
        (-Math.cos((double)(count+1)*50),-Math.sin((double)(count+1)*50));
      //decrease alpha
      fade =AlphaComposite.getInstance
        (AlphaComposite.SRC_OVER,1.0f-((float)count)/(float)countMax);
      g2.setComposite(fade);
      // finally draw the frame
      g2.drawImage(frame_buffer,0,0,null);
      // restore the current transform
      g2.setTransform(old_trans);
      break;

    default: //fade out
      // draw the screen, offset in case the window isn't at 0,0
      g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
      // draw the frame
      old_comp = g2.getComposite();
      //decrease alpha
      fade =AlphaComposite.getInstance
        (AlphaComposite.SRC_OVER,1.0f-((float)count)/(float)countMax);
      g2.setComposite(fade);
      g2.drawImage(frame_buffer,frame.getX(),frame.getY(),null);
      g2.setComposite(old_comp);
      break;

    }


  }


}
