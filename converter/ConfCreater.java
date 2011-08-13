package converter;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import converter.*;

//This is called, if there is no akiraconv.conf
public class ConfCreater extends JFrame implements ActionListener{

  AkiraConverter akiraconv;
  //constructor
  public ConfCreater(AkiraConverter akiraconv){
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Create config");
    setBounds(new Rectangle(40,40,700,700));

    add(createPanel());

    this.akiraconv=akiraconv;
    setVisible(true);
  }

  public void actionPerformed( ActionEvent e ){
    if( e.getSource() == btnSave ){
      setVisible(false);
      saveConfFile();
      akiraconv.startConv();
      System.exit(0);
    }
  }

  //save file
  void saveConfFile(){
    try{
      FileWriter fw = new FileWriter("./AkiraConverter.conf");
      fw.write(textArea.getText());
      fw.close();
    } catch (IOException ex){
      ex.printStackTrace();
    }

  }

  JTextArea textArea;
  JButton btnSave;

  //
  JPanel createPanel(){
    //text area
    textArea = new JTextArea();
    textArea.setFont(new Font("courier",Font.PLAIN,14));

    //set text area
    try{
      BufferedReader br =
        new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/converter/AkiraConverter.conf")));
      String line;
      while ((line = br.readLine()) != null){
        textArea.append(line + "\n");
      }
      br.close();
    } catch (IOException ex){
      ex.printStackTrace();
    }

    //scroller
    JScrollPane sp = new JScrollPane( textArea );
    sp.setFocusable(false);

    //button
    btnSave=new JButton("save & conv");
    btnSave.addActionListener(this);


    //jpanel
    JPanel jpanel=new JPanel();
    jpanel.setLayout(new BorderLayout(0,0));
    jpanel.add(btnSave,BorderLayout.NORTH);
    jpanel.add(sp,BorderLayout.CENTER);

    return jpanel;
  }


}
