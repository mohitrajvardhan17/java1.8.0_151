package javax.swing.colorchooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

class DefaultSwatchChooserPanel
  extends AbstractColorChooserPanel
{
  SwatchPanel swatchPanel;
  RecentSwatchPanel recentSwatchPanel;
  MouseListener mainSwatchListener;
  MouseListener recentSwatchListener;
  private KeyListener mainSwatchKeyListener;
  private KeyListener recentSwatchKeyListener;
  
  public DefaultSwatchChooserPanel()
  {
    setInheritsPopupMenu(true);
  }
  
  public String getDisplayName()
  {
    return UIManager.getString("ColorChooser.swatchesNameText", getLocale());
  }
  
  public int getMnemonic()
  {
    return getInt("ColorChooser.swatchesMnemonic", -1);
  }
  
  public int getDisplayedMnemonicIndex()
  {
    return getInt("ColorChooser.swatchesDisplayedMnemonicIndex", -1);
  }
  
  public Icon getSmallDisplayIcon()
  {
    return null;
  }
  
  public Icon getLargeDisplayIcon()
  {
    return null;
  }
  
  public void installChooserPanel(JColorChooser paramJColorChooser)
  {
    super.installChooserPanel(paramJColorChooser);
  }
  
  protected void buildChooser()
  {
    String str = UIManager.getString("ColorChooser.swatchesRecentText", getLocale());
    GridBagLayout localGridBagLayout = new GridBagLayout();
    GridBagConstraints localGridBagConstraints = new GridBagConstraints();
    JPanel localJPanel1 = new JPanel(localGridBagLayout);
    swatchPanel = new MainSwatchPanel();
    swatchPanel.putClientProperty("AccessibleName", getDisplayName());
    swatchPanel.setInheritsPopupMenu(true);
    recentSwatchPanel = new RecentSwatchPanel();
    recentSwatchPanel.putClientProperty("AccessibleName", str);
    mainSwatchKeyListener = new MainSwatchKeyListener(null);
    mainSwatchListener = new MainSwatchListener();
    swatchPanel.addMouseListener(mainSwatchListener);
    swatchPanel.addKeyListener(mainSwatchKeyListener);
    recentSwatchListener = new RecentSwatchListener();
    recentSwatchKeyListener = new RecentSwatchKeyListener(null);
    recentSwatchPanel.addMouseListener(recentSwatchListener);
    recentSwatchPanel.addKeyListener(recentSwatchKeyListener);
    JPanel localJPanel2 = new JPanel(new BorderLayout());
    CompoundBorder localCompoundBorder = new CompoundBorder(new LineBorder(Color.black), new LineBorder(Color.white));
    localJPanel2.setBorder(localCompoundBorder);
    localJPanel2.add(swatchPanel, "Center");
    anchor = 25;
    gridwidth = 1;
    gridheight = 2;
    Insets localInsets = insets;
    insets = new Insets(0, 0, 0, 10);
    localJPanel1.add(localJPanel2, localGridBagConstraints);
    insets = localInsets;
    recentSwatchPanel.setInheritsPopupMenu(true);
    JPanel localJPanel3 = new JPanel(new BorderLayout());
    localJPanel3.setBorder(localCompoundBorder);
    localJPanel3.setInheritsPopupMenu(true);
    localJPanel3.add(recentSwatchPanel, "Center");
    JLabel localJLabel = new JLabel(str);
    localJLabel.setLabelFor(recentSwatchPanel);
    gridwidth = 0;
    gridheight = 1;
    weighty = 1.0D;
    localJPanel1.add(localJLabel, localGridBagConstraints);
    weighty = 0.0D;
    gridheight = 0;
    insets = new Insets(0, 0, 0, 2);
    localJPanel1.add(localJPanel3, localGridBagConstraints);
    localJPanel1.setInheritsPopupMenu(true);
    add(localJPanel1);
  }
  
  public void uninstallChooserPanel(JColorChooser paramJColorChooser)
  {
    super.uninstallChooserPanel(paramJColorChooser);
    swatchPanel.removeMouseListener(mainSwatchListener);
    swatchPanel.removeKeyListener(mainSwatchKeyListener);
    recentSwatchPanel.removeMouseListener(recentSwatchListener);
    recentSwatchPanel.removeKeyListener(recentSwatchKeyListener);
    swatchPanel = null;
    recentSwatchPanel = null;
    mainSwatchListener = null;
    mainSwatchKeyListener = null;
    recentSwatchListener = null;
    recentSwatchKeyListener = null;
    removeAll();
  }
  
  public void updateChooser() {}
  
  private class MainSwatchKeyListener
    extends KeyAdapter
  {
    private MainSwatchKeyListener() {}
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      if (32 == paramKeyEvent.getKeyCode())
      {
        Color localColor = swatchPanel.getSelectedColor();
        setSelectedColor(localColor);
        recentSwatchPanel.setMostRecentColor(localColor);
      }
    }
  }
  
  class MainSwatchListener
    extends MouseAdapter
    implements Serializable
  {
    MainSwatchListener() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (isEnabled())
      {
        Color localColor = swatchPanel.getColorForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
        setSelectedColor(localColor);
        swatchPanel.setSelectedColorFromLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
        recentSwatchPanel.setMostRecentColor(localColor);
        swatchPanel.requestFocusInWindow();
      }
    }
  }
  
  private class RecentSwatchKeyListener
    extends KeyAdapter
  {
    private RecentSwatchKeyListener() {}
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      if (32 == paramKeyEvent.getKeyCode())
      {
        Color localColor = recentSwatchPanel.getSelectedColor();
        setSelectedColor(localColor);
      }
    }
  }
  
  class RecentSwatchListener
    extends MouseAdapter
    implements Serializable
  {
    RecentSwatchListener() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (isEnabled())
      {
        Color localColor = recentSwatchPanel.getColorForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
        recentSwatchPanel.setSelectedColorFromLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
        setSelectedColor(localColor);
        recentSwatchPanel.requestFocusInWindow();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\DefaultSwatchChooserPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */