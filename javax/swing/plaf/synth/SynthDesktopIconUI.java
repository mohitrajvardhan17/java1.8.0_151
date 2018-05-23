package javax.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class SynthDesktopIconUI
  extends BasicDesktopIconUI
  implements SynthUI, PropertyChangeListener
{
  private SynthStyle style;
  private Handler handler = new Handler(null);
  
  public SynthDesktopIconUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthDesktopIconUI();
  }
  
  protected void installComponents()
  {
    if (UIManager.getBoolean("InternalFrame.useTaskBar"))
    {
      iconPane = new JToggleButton(frame.getTitle(), frame.getFrameIcon())
      {
        public String getToolTipText()
        {
          return getText();
        }
        
        public JPopupMenu getComponentPopupMenu()
        {
          return frame.getComponentPopupMenu();
        }
      };
      ToolTipManager.sharedInstance().registerComponent(iconPane);
      iconPane.setFont(desktopIcon.getFont());
      iconPane.setBackground(desktopIcon.getBackground());
      iconPane.setForeground(desktopIcon.getForeground());
    }
    else
    {
      iconPane = new SynthInternalFrameTitlePane(frame);
      iconPane.setName("InternalFrame.northPane");
    }
    desktopIcon.setLayout(new BorderLayout());
    desktopIcon.add(iconPane, "Center");
  }
  
  protected void installListeners()
  {
    super.installListeners();
    desktopIcon.addPropertyChangeListener(this);
    if ((iconPane instanceof JToggleButton))
    {
      frame.addPropertyChangeListener(this);
      ((JToggleButton)iconPane).addActionListener(handler);
    }
  }
  
  protected void uninstallListeners()
  {
    if ((iconPane instanceof JToggleButton))
    {
      ((JToggleButton)iconPane).removeActionListener(handler);
      frame.removePropertyChangeListener(this);
    }
    desktopIcon.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  protected void installDefaults()
  {
    updateStyle(desktopIcon);
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(desktopIcon, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintDesktopIconBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintDesktopIconBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if ((paramPropertyChangeEvent.getSource() instanceof JInternalFrame.JDesktopIcon))
    {
      if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
        updateStyle((JInternalFrame.JDesktopIcon)paramPropertyChangeEvent.getSource());
      }
    }
    else if ((paramPropertyChangeEvent.getSource() instanceof JInternalFrame))
    {
      JInternalFrame localJInternalFrame = (JInternalFrame)paramPropertyChangeEvent.getSource();
      if ((iconPane instanceof JToggleButton))
      {
        JToggleButton localJToggleButton = (JToggleButton)iconPane;
        String str = paramPropertyChangeEvent.getPropertyName();
        if (str == "title") {
          localJToggleButton.setText((String)paramPropertyChangeEvent.getNewValue());
        } else if (str == "frameIcon") {
          localJToggleButton.setIcon((Icon)paramPropertyChangeEvent.getNewValue());
        } else if ((str == "icon") || (str == "selected")) {
          localJToggleButton.setSelected((!localJInternalFrame.isIcon()) && (localJInternalFrame.isSelected()));
        }
      }
    }
  }
  
  private final class Handler
    implements ActionListener
  {
    private Handler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if ((paramActionEvent.getSource() instanceof JToggleButton))
      {
        JToggleButton localJToggleButton = (JToggleButton)paramActionEvent.getSource();
        try
        {
          boolean bool = localJToggleButton.isSelected();
          if ((!bool) && (!frame.isIconifiable()))
          {
            localJToggleButton.setSelected(true);
          }
          else
          {
            frame.setIcon(!bool);
            if (bool) {
              frame.setSelected(true);
            }
          }
        }
        catch (PropertyVetoException localPropertyVetoException) {}
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */