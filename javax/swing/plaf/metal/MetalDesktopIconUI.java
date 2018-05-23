package javax.swing.plaf.metal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class MetalDesktopIconUI
  extends BasicDesktopIconUI
{
  JButton button;
  JLabel label;
  TitleListener titleListener;
  private int width;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalDesktopIconUI();
  }
  
  public MetalDesktopIconUI() {}
  
  protected void installDefaults()
  {
    super.installDefaults();
    LookAndFeel.installColorsAndFont(desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground", "DesktopIcon.font");
    width = UIManager.getInt("DesktopIcon.width");
  }
  
  protected void installComponents()
  {
    frame = desktopIcon.getInternalFrame();
    Icon localIcon = frame.getFrameIcon();
    String str = frame.getTitle();
    button = new JButton(str, localIcon);
    button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        deiconize();
      }
    });
    button.setFont(desktopIcon.getFont());
    button.setBackground(desktopIcon.getBackground());
    button.setForeground(desktopIcon.getForeground());
    int i = button.getPreferredSize().height;
    MetalBumps localMetalBumps = new MetalBumps(i / 3, i, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
    label = new JLabel(localMetalBumps);
    label.setBorder(new MatteBorder(0, 2, 0, 1, desktopIcon.getBackground()));
    desktopIcon.setLayout(new BorderLayout(2, 0));
    desktopIcon.add(button, "Center");
    desktopIcon.add(label, "West");
  }
  
  protected void uninstallComponents()
  {
    desktopIcon.setLayout(null);
    desktopIcon.remove(label);
    desktopIcon.remove(button);
    button = null;
    frame = null;
  }
  
  protected void installListeners()
  {
    super.installListeners();
    desktopIcon.getInternalFrame().addPropertyChangeListener(titleListener = new TitleListener());
  }
  
  protected void uninstallListeners()
  {
    desktopIcon.getInternalFrame().removePropertyChangeListener(titleListener);
    titleListener = null;
    super.uninstallListeners();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return getMinimumSize(paramJComponent);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return new Dimension(width, desktopIcon.getLayout().minimumLayoutSize(desktopIcon).height);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return getMinimumSize(paramJComponent);
  }
  
  class TitleListener
    implements PropertyChangeListener
  {
    TitleListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getPropertyName().equals("title")) {
        button.setText((String)paramPropertyChangeEvent.getNewValue());
      }
      if (paramPropertyChangeEvent.getPropertyName().equals("frameIcon")) {
        button.setIcon((Icon)paramPropertyChangeEvent.getNewValue());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */