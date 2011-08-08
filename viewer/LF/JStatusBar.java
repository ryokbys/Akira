package viewer.LF;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import viewer.LF.*;

public class JStatusBar extends JPanel {

  private JPanel contentPanel;
  private TrinagleSquareWindowsCornerIcon icon;
  private FormLayout layout;
  private int layoutCoordinateX = 2;
  private int layoutCoordinateY = 2;

  public JStatusBar() {
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(getWidth(), 26));

    JLabel resizeIconLabel = new JLabel(new TrinagleSquareWindowsCornerIcon());
    resizeIconLabel.setOpaque(false);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(resizeIconLabel, BorderLayout.SOUTH);
    rightPanel.setOpaque(false);
    add(rightPanel, BorderLayout.EAST);

    contentPanel = new JPanel( );
    contentPanel.setOpaque(false);
    layout = new FormLayout("2dlu, pref:grow", "2dlu, fill:12dlu, 2dlu");

    contentPanel.setLayout(layout);
    add(contentPanel, BorderLayout.CENTER);

    setBackground(new Color(236, 233, 216));
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    int y = 0;
    g.setColor(new Color(156, 154, 140));
    g.drawLine(0, y, getWidth(), y);
    y++;
    g.setColor(new Color(196, 194, 183));
    g.drawLine(0, y, getWidth(), y);
    y++;
    g.setColor(new Color(218, 215, 201));
    g.drawLine(0, y, getWidth(), y);
    y++;
    g.setColor(new Color(233, 231, 217));
    g.drawLine(0, y, getWidth(), y);

    y = getHeight() - 3;
    g.setColor(new Color(233, 232, 218));
    g.drawLine(0, y, getWidth(), y);
    y++;
    g.setColor(new Color(233, 231, 216));
    g.drawLine(0, y, getWidth(), y);
    y = getHeight() - 1;
    g.setColor(new Color(221, 221, 220));
    g.drawLine(0, y, getWidth(), y);
  }

  public void setMainLeftComponent(JComponent component){
    contentPanel.add(component, new CellConstraints(2, 2));
  }

  public void set3(JComponent c1,JComponent c2,JComponent c3){
    contentPanel.add(c1, new CellConstraints(2, 2));

    layout.appendColumn(new ColumnSpec("2dlu"));
    layout.appendColumn(new ColumnSpec("30dlu"));
    layout.appendColumn(new ColumnSpec("25dlu"));
    layout.appendColumn(new ColumnSpec("5dlu"));

    layoutCoordinateX++;
    contentPanel.add(new SeparatorPanel(Color.GRAY, Color.WHITE),
                     new CellConstraints(layoutCoordinateX, layoutCoordinateY) );

    layoutCoordinateX++;
    contentPanel.add(c2,new CellConstraints(layoutCoordinateX, layoutCoordinateY) );

    layoutCoordinateX++;
    contentPanel.add(c3,new CellConstraints(layoutCoordinateX, layoutCoordinateY) );
  }

  public void addRightComponent(JComponent component, int dialogUnits){
    layout.appendColumn(new ColumnSpec("2dlu"));
    layout.appendColumn(new ColumnSpec(dialogUnits + "dlu"));
    layoutCoordinateX++;

    contentPanel.add(new SeparatorPanel(Color.GRAY, Color.WHITE),
                     new CellConstraints(layoutCoordinateX, layoutCoordinateY) );
    layoutCoordinateX++;
    contentPanel.add(component,
                     new CellConstraints(layoutCoordinateX, layoutCoordinateY) );
  }
}

class SeparatorPanel extends JPanel {
  private Color leftColor;
  private Color rightColor;
  SeparatorPanel(Color left, Color right) {
    this.leftColor = left;
    this.rightColor = right;
    setOpaque(false);
  }
  protected void paintComponent(Graphics g) {
    g.setColor(leftColor);
    g.drawLine(0,0, 0,getHeight( ));
    g.setColor(rightColor);
    g.drawLine(1,0, 1,getHeight( ));
  }
}
