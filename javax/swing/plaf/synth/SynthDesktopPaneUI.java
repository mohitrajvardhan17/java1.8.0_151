package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class SynthDesktopPaneUI
  extends BasicDesktopPaneUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private TaskBar taskBar;
  private DesktopManager oldDesktopManager;
  
  public SynthDesktopPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthDesktopPaneUI();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    desktop.addPropertyChangeListener(this);
    if (taskBar != null)
    {
      desktop.addComponentListener(taskBar);
      desktop.addContainerListener(taskBar);
    }
  }
  
  protected void installDefaults()
  {
    updateStyle(desktop);
    if (UIManager.getBoolean("InternalFrame.useTaskBar"))
    {
      taskBar = new TaskBar();
      for (Component localComponent : desktop.getComponents())
      {
        JInternalFrame.JDesktopIcon localJDesktopIcon;
        if ((localComponent instanceof JInternalFrame.JDesktopIcon))
        {
          localJDesktopIcon = (JInternalFrame.JDesktopIcon)localComponent;
        }
        else
        {
          if (!(localComponent instanceof JInternalFrame)) {
            continue;
          }
          localJDesktopIcon = ((JInternalFrame)localComponent).getDesktopIcon();
        }
        if (localJDesktopIcon.getParent() == desktop) {
          desktop.remove(localJDesktopIcon);
        }
        if (localJDesktopIcon.getParent() != taskBar)
        {
          taskBar.add(localJDesktopIcon);
          localJDesktopIcon.getInternalFrame().addComponentListener(taskBar);
        }
      }
      taskBar.setBackground(desktop.getBackground());
      desktop.add(taskBar, Integer.valueOf(JLayeredPane.PALETTE_LAYER.intValue() + 1));
      if (desktop.isShowing()) {
        taskBar.adjustSize();
      }
    }
  }
  
  private void updateStyle(JDesktopPane paramJDesktopPane)
  {
    SynthStyle localSynthStyle = style;
    SynthContext localSynthContext = getContext(paramJDesktopPane, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (localSynthStyle != null)
    {
      uninstallKeyboardActions();
      installKeyboardActions();
    }
    localSynthContext.dispose();
  }
  
  protected void uninstallListeners()
  {
    if (taskBar != null)
    {
      desktop.removeComponentListener(taskBar);
      desktop.removeContainerListener(taskBar);
    }
    desktop.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(desktop, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    if (taskBar != null)
    {
      for (Component localComponent : taskBar.getComponents())
      {
        JInternalFrame.JDesktopIcon localJDesktopIcon = (JInternalFrame.JDesktopIcon)localComponent;
        taskBar.remove(localJDesktopIcon);
        localJDesktopIcon.setPreferredSize(null);
        JInternalFrame localJInternalFrame = localJDesktopIcon.getInternalFrame();
        if (localJInternalFrame.isIcon()) {
          desktop.add(localJDesktopIcon);
        }
        localJInternalFrame.removeComponentListener(taskBar);
      }
      desktop.remove(taskBar);
      taskBar = null;
    }
  }
  
  protected void installDesktopManager()
  {
    if (UIManager.getBoolean("InternalFrame.useTaskBar"))
    {
      desktopManager = (oldDesktopManager = desktop.getDesktopManager());
      if (!(desktopManager instanceof SynthDesktopManager))
      {
        desktopManager = new SynthDesktopManager();
        desktop.setDesktopManager(desktopManager);
      }
    }
    else
    {
      super.installDesktopManager();
    }
  }
  
  protected void uninstallDesktopManager()
  {
    if ((oldDesktopManager != null) && (!(oldDesktopManager instanceof UIResource)))
    {
      desktopManager = desktop.getDesktopManager();
      if ((desktopManager == null) || ((desktopManager instanceof UIResource))) {
        desktop.setDesktopManager(oldDesktopManager);
      }
    }
    oldDesktopManager = null;
    super.uninstallDesktopManager();
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
    localSynthContext.getPainter().paintDesktopPaneBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
    paramSynthContext.getPainter().paintDesktopPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JDesktopPane)paramPropertyChangeEvent.getSource());
    }
    if ((paramPropertyChangeEvent.getPropertyName() == "ancestor") && (taskBar != null)) {
      taskBar.adjustSize();
    }
  }
  
  class SynthDesktopManager
    extends DefaultDesktopManager
    implements UIResource
  {
    SynthDesktopManager() {}
    
    public void maximizeFrame(JInternalFrame paramJInternalFrame)
    {
      if (paramJInternalFrame.isIcon())
      {
        try
        {
          paramJInternalFrame.setIcon(false);
        }
        catch (PropertyVetoException localPropertyVetoException1) {}
      }
      else
      {
        paramJInternalFrame.setNormalBounds(paramJInternalFrame.getBounds());
        Container localContainer = paramJInternalFrame.getParent();
        setBoundsForFrame(paramJInternalFrame, 0, 0, localContainer.getWidth(), localContainer.getHeight() - taskBar.getHeight());
      }
      try
      {
        paramJInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException2) {}
    }
    
    public void iconifyFrame(JInternalFrame paramJInternalFrame)
    {
      Container localContainer = paramJInternalFrame.getParent();
      JDesktopPane localJDesktopPane = paramJInternalFrame.getDesktopPane();
      boolean bool = paramJInternalFrame.isSelected();
      if (localContainer == null) {
        return;
      }
      JInternalFrame.JDesktopIcon localJDesktopIcon = paramJInternalFrame.getDesktopIcon();
      if (!paramJInternalFrame.isMaximum()) {
        paramJInternalFrame.setNormalBounds(paramJInternalFrame.getBounds());
      }
      localContainer.remove(paramJInternalFrame);
      localContainer.repaint(paramJInternalFrame.getX(), paramJInternalFrame.getY(), paramJInternalFrame.getWidth(), paramJInternalFrame.getHeight());
      try
      {
        paramJInternalFrame.setSelected(false);
      }
      catch (PropertyVetoException localPropertyVetoException1) {}
      if (bool) {
        for (Component localComponent : localContainer.getComponents()) {
          if ((localComponent instanceof JInternalFrame))
          {
            try
            {
              ((JInternalFrame)localComponent).setSelected(true);
            }
            catch (PropertyVetoException localPropertyVetoException2) {}
            ((JInternalFrame)localComponent).moveToFront();
            return;
          }
        }
      }
    }
    
    public void deiconifyFrame(JInternalFrame paramJInternalFrame)
    {
      JInternalFrame.JDesktopIcon localJDesktopIcon = paramJInternalFrame.getDesktopIcon();
      Container localContainer = localJDesktopIcon.getParent();
      if (localContainer != null)
      {
        localContainer = localContainer.getParent();
        if (localContainer != null)
        {
          localContainer.add(paramJInternalFrame);
          if (paramJInternalFrame.isMaximum())
          {
            int i = localContainer.getWidth();
            int j = localContainer.getHeight() - taskBar.getHeight();
            if ((paramJInternalFrame.getWidth() != i) || (paramJInternalFrame.getHeight() != j)) {
              setBoundsForFrame(paramJInternalFrame, 0, 0, i, j);
            }
          }
          if (paramJInternalFrame.isSelected()) {
            paramJInternalFrame.moveToFront();
          } else {
            try
            {
              paramJInternalFrame.setSelected(true);
            }
            catch (PropertyVetoException localPropertyVetoException) {}
          }
        }
      }
    }
    
    protected void removeIconFor(JInternalFrame paramJInternalFrame)
    {
      super.removeIconFor(paramJInternalFrame);
      taskBar.validate();
    }
    
    public void setBoundsForFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.setBoundsForFrame(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
      if ((taskBar != null) && (paramInt2 >= taskBar.getY())) {
        paramJComponent.setLocation(paramJComponent.getX(), taskBar.getY() - getInsetstop);
      }
    }
  }
  
  static class TaskBar
    extends JPanel
    implements ComponentListener, ContainerListener
  {
    TaskBar()
    {
      setOpaque(true);
      setLayout(new FlowLayout(0, 0, 0)
      {
        public void layoutContainer(Container paramAnonymousContainer)
        {
          Component[] arrayOfComponent1 = paramAnonymousContainer.getComponents();
          int i = arrayOfComponent1.length;
          if (i > 0)
          {
            int j = 0;
            Component[] arrayOfComponent2;
            for (arrayOfComponent2 : arrayOfComponent1)
            {
              arrayOfComponent2.setPreferredSize(null);
              Dimension localDimension1 = arrayOfComponent2.getPreferredSize();
              if (width > j) {
                j = width;
              }
            }
            ??? = paramAnonymousContainer.getInsets();
            ??? = paramAnonymousContainer.getWidth() - left - right;
            ??? = Math.min(j, Math.max(10, ??? / i));
            for (Component localComponent : arrayOfComponent1)
            {
              Dimension localDimension2 = localComponent.getPreferredSize();
              localComponent.setPreferredSize(new Dimension(???, height));
            }
          }
          super.layoutContainer(paramAnonymousContainer);
        }
      });
      setBorder(new BevelBorder(0)
      {
        protected void paintRaisedBevel(Component paramAnonymousComponent, Graphics paramAnonymousGraphics, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          Color localColor = paramAnonymousGraphics.getColor();
          paramAnonymousGraphics.translate(paramAnonymousInt1, paramAnonymousInt2);
          paramAnonymousGraphics.setColor(getHighlightOuterColor(paramAnonymousComponent));
          paramAnonymousGraphics.drawLine(0, 0, 0, paramAnonymousInt4 - 2);
          paramAnonymousGraphics.drawLine(1, 0, paramAnonymousInt3 - 2, 0);
          paramAnonymousGraphics.setColor(getShadowOuterColor(paramAnonymousComponent));
          paramAnonymousGraphics.drawLine(0, paramAnonymousInt4 - 1, paramAnonymousInt3 - 1, paramAnonymousInt4 - 1);
          paramAnonymousGraphics.drawLine(paramAnonymousInt3 - 1, 0, paramAnonymousInt3 - 1, paramAnonymousInt4 - 2);
          paramAnonymousGraphics.translate(-paramAnonymousInt1, -paramAnonymousInt2);
          paramAnonymousGraphics.setColor(localColor);
        }
      });
    }
    
    void adjustSize()
    {
      JDesktopPane localJDesktopPane = (JDesktopPane)getParent();
      if (localJDesktopPane != null)
      {
        int i = getPreferredSizeheight;
        Insets localInsets = getInsets();
        if (i == top + bottom) {
          if (getHeight() <= i) {
            i += 21;
          } else {
            i = getHeight();
          }
        }
        setBounds(0, localJDesktopPane.getHeight() - i, localJDesktopPane.getWidth(), i);
        revalidate();
        repaint();
      }
    }
    
    public void componentResized(ComponentEvent paramComponentEvent)
    {
      if ((paramComponentEvent.getSource() instanceof JDesktopPane)) {
        adjustSize();
      }
    }
    
    public void componentMoved(ComponentEvent paramComponentEvent) {}
    
    public void componentShown(ComponentEvent paramComponentEvent)
    {
      if ((paramComponentEvent.getSource() instanceof JInternalFrame)) {
        adjustSize();
      }
    }
    
    public void componentHidden(ComponentEvent paramComponentEvent)
    {
      if ((paramComponentEvent.getSource() instanceof JInternalFrame))
      {
        ((JInternalFrame)paramComponentEvent.getSource()).getDesktopIcon().setVisible(false);
        revalidate();
      }
    }
    
    public void componentAdded(ContainerEvent paramContainerEvent)
    {
      if ((paramContainerEvent.getChild() instanceof JInternalFrame))
      {
        JDesktopPane localJDesktopPane = (JDesktopPane)paramContainerEvent.getSource();
        JInternalFrame localJInternalFrame = (JInternalFrame)paramContainerEvent.getChild();
        JInternalFrame.JDesktopIcon localJDesktopIcon = localJInternalFrame.getDesktopIcon();
        for (Component localComponent : getComponents()) {
          if (localComponent == localJDesktopIcon) {
            return;
          }
        }
        add(localJDesktopIcon);
        localJInternalFrame.addComponentListener(this);
        if (getComponentCount() == 1) {
          adjustSize();
        }
      }
    }
    
    public void componentRemoved(ContainerEvent paramContainerEvent)
    {
      if ((paramContainerEvent.getChild() instanceof JInternalFrame))
      {
        JInternalFrame localJInternalFrame = (JInternalFrame)paramContainerEvent.getChild();
        if (!localJInternalFrame.isIcon())
        {
          remove(localJInternalFrame.getDesktopIcon());
          localJInternalFrame.removeComponentListener(this);
          revalidate();
          repaint();
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */