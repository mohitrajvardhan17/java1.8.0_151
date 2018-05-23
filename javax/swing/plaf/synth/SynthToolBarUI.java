package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box.Filler;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.plaf.basic.BasicToolBarUI.DragWindow;
import sun.swing.plaf.synth.SynthIcon;

public class SynthToolBarUI
  extends BasicToolBarUI
  implements PropertyChangeListener, SynthUI
{
  private Icon handleIcon = null;
  private Rectangle contentRect = new Rectangle();
  private SynthStyle style;
  private SynthStyle contentStyle;
  private SynthStyle dragWindowStyle;
  
  public SynthToolBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthToolBarUI();
  }
  
  protected void installDefaults()
  {
    toolBar.setLayout(createLayout());
    updateStyle(toolBar);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    toolBar.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    toolBar.removePropertyChangeListener(this);
  }
  
  private void updateStyle(JToolBar paramJToolBar)
  {
    SynthContext localSynthContext = getContext(paramJToolBar, Region.TOOL_BAR_CONTENT, null, 1);
    contentStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
    localSynthContext = getContext(paramJToolBar, Region.TOOL_BAR_DRAG_WINDOW, null, 1);
    dragWindowStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
    localSynthContext = getContext(paramJToolBar, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (localSynthStyle != style)
    {
      handleIcon = style.getIcon(localSynthContext, "ToolBar.handleIcon");
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(toolBar, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    handleIcon = null;
    localSynthContext = getContext(toolBar, Region.TOOL_BAR_CONTENT, contentStyle, 1);
    contentStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    contentStyle = null;
    localSynthContext = getContext(toolBar, Region.TOOL_BAR_DRAG_WINDOW, dragWindowStyle, 1);
    dragWindowStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    dragWindowStyle = null;
    toolBar.setLayout(null);
  }
  
  protected void installComponents() {}
  
  protected void uninstallComponents() {}
  
  protected LayoutManager createLayout()
  {
    return new SynthToolBarLayoutManager();
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle)
  {
    return SynthContext.getContext(paramJComponent, paramRegion, paramSynthStyle, getComponentState(paramJComponent, paramRegion));
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, paramRegion, paramSynthStyle, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion)
  {
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintToolBarBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), toolBar.getOrientation());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintToolBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, toolBar.getOrientation());
  }
  
  protected void setBorderToNonRollover(Component paramComponent) {}
  
  protected void setBorderToRollover(Component paramComponent) {}
  
  protected void setBorderToNormal(Component paramComponent) {}
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    if ((handleIcon != null) && (toolBar.isFloatable()))
    {
      int i = toolBar.getComponentOrientation().isLeftToRight() ? 0 : toolBar.getWidth() - SynthIcon.getIconWidth(handleIcon, paramSynthContext);
      SynthIcon.paintIcon(handleIcon, paramSynthContext, paramGraphics, i, 0, SynthIcon.getIconWidth(handleIcon, paramSynthContext), SynthIcon.getIconHeight(handleIcon, paramSynthContext));
    }
    SynthContext localSynthContext = getContext(toolBar, Region.TOOL_BAR_CONTENT, contentStyle);
    paintContent(localSynthContext, paramGraphics, contentRect);
    localSynthContext.dispose();
  }
  
  protected void paintContent(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
  {
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintToolBarContentBackground(paramSynthContext, paramGraphics, x, y, width, height, toolBar.getOrientation());
    paramSynthContext.getPainter().paintToolBarContentBorder(paramSynthContext, paramGraphics, x, y, width, height, toolBar.getOrientation());
  }
  
  protected void paintDragWindow(Graphics paramGraphics)
  {
    int i = dragWindow.getWidth();
    int j = dragWindow.getHeight();
    SynthContext localSynthContext = getContext(toolBar, Region.TOOL_BAR_DRAG_WINDOW, dragWindowStyle);
    SynthLookAndFeel.updateSubregion(localSynthContext, paramGraphics, new Rectangle(0, 0, i, j));
    localSynthContext.getPainter().paintToolBarDragWindowBackground(localSynthContext, paramGraphics, 0, 0, i, j, dragWindow.getOrientation());
    localSynthContext.getPainter().paintToolBarDragWindowBorder(localSynthContext, paramGraphics, 0, 0, i, j, dragWindow.getOrientation());
    localSynthContext.dispose();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JToolBar)paramPropertyChangeEvent.getSource());
    }
  }
  
  class SynthToolBarLayoutManager
    implements LayoutManager
  {
    SynthToolBarLayoutManager() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      JToolBar localJToolBar = (JToolBar)paramContainer;
      Insets localInsets = localJToolBar.getInsets();
      Dimension localDimension1 = new Dimension();
      SynthContext localSynthContext = getContext(localJToolBar);
      int i;
      Component localComponent;
      Dimension localDimension2;
      if (localJToolBar.getOrientation() == 0)
      {
        width = (localJToolBar.isFloatable() ? SynthIcon.getIconWidth(handleIcon, localSynthContext) : 0);
        for (i = 0; i < localJToolBar.getComponentCount(); i++)
        {
          localComponent = localJToolBar.getComponent(i);
          if (localComponent.isVisible())
          {
            localDimension2 = localComponent.getMinimumSize();
            width += width;
            height = Math.max(height, height);
          }
        }
      }
      else
      {
        height = (localJToolBar.isFloatable() ? SynthIcon.getIconHeight(handleIcon, localSynthContext) : 0);
        for (i = 0; i < localJToolBar.getComponentCount(); i++)
        {
          localComponent = localJToolBar.getComponent(i);
          if (localComponent.isVisible())
          {
            localDimension2 = localComponent.getMinimumSize();
            width = Math.max(width, width);
            height += height;
          }
        }
      }
      width += left + right;
      height += top + bottom;
      localSynthContext.dispose();
      return localDimension1;
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      JToolBar localJToolBar = (JToolBar)paramContainer;
      Insets localInsets = localJToolBar.getInsets();
      Dimension localDimension1 = new Dimension();
      SynthContext localSynthContext = getContext(localJToolBar);
      int i;
      Component localComponent;
      Dimension localDimension2;
      if (localJToolBar.getOrientation() == 0)
      {
        width = (localJToolBar.isFloatable() ? SynthIcon.getIconWidth(handleIcon, localSynthContext) : 0);
        for (i = 0; i < localJToolBar.getComponentCount(); i++)
        {
          localComponent = localJToolBar.getComponent(i);
          if (localComponent.isVisible())
          {
            localDimension2 = localComponent.getPreferredSize();
            width += width;
            height = Math.max(height, height);
          }
        }
      }
      else
      {
        height = (localJToolBar.isFloatable() ? SynthIcon.getIconHeight(handleIcon, localSynthContext) : 0);
        for (i = 0; i < localJToolBar.getComponentCount(); i++)
        {
          localComponent = localJToolBar.getComponent(i);
          if (localComponent.isVisible())
          {
            localDimension2 = localComponent.getPreferredSize();
            width = Math.max(width, width);
            height += height;
          }
        }
      }
      width += left + right;
      height += top + bottom;
      localSynthContext.dispose();
      return localDimension1;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      JToolBar localJToolBar = (JToolBar)paramContainer;
      Insets localInsets = localJToolBar.getInsets();
      boolean bool = localJToolBar.getComponentOrientation().isLeftToRight();
      SynthContext localSynthContext = getContext(localJToolBar);
      int i = 0;
      for (int j = 0; j < localJToolBar.getComponentCount(); j++) {
        if (isGlue(localJToolBar.getComponent(j))) {
          i++;
        }
      }
      int k;
      int m;
      int n;
      int i1;
      int i2;
      Component localComponent;
      Dimension localDimension;
      int i3;
      int i4;
      if (localJToolBar.getOrientation() == 0)
      {
        j = localJToolBar.isFloatable() ? SynthIcon.getIconWidth(handleIcon, localSynthContext) : 0;
        contentRect.x = (bool ? j : 0);
        contentRect.y = 0;
        contentRect.width = (localJToolBar.getWidth() - j);
        contentRect.height = localJToolBar.getHeight();
        k = bool ? j + left : localJToolBar.getWidth() - j - right;
        m = top;
        n = localJToolBar.getHeight() - top - bottom;
        i1 = 0;
        if (i > 0)
        {
          i2 = minimumLayoutSizewidth;
          i1 = (localJToolBar.getWidth() - i2) / i;
          if (i1 < 0) {
            i1 = 0;
          }
        }
        for (i2 = 0; i2 < localJToolBar.getComponentCount(); i2++)
        {
          localComponent = localJToolBar.getComponent(i2);
          if (localComponent.isVisible())
          {
            localDimension = localComponent.getPreferredSize();
            if ((height >= n) || ((localComponent instanceof JSeparator)))
            {
              i3 = m;
              i4 = n;
            }
            else
            {
              i3 = m + n / 2 - height / 2;
              i4 = height;
            }
            if (isGlue(localComponent)) {
              width += i1;
            }
            localComponent.setBounds(bool ? k : k - width, i3, width, i4);
            k = bool ? k + width : k - width;
          }
        }
      }
      else
      {
        j = localJToolBar.isFloatable() ? SynthIcon.getIconHeight(handleIcon, localSynthContext) : 0;
        contentRect.x = 0;
        contentRect.y = j;
        contentRect.width = localJToolBar.getWidth();
        contentRect.height = (localJToolBar.getHeight() - j);
        k = left;
        m = localJToolBar.getWidth() - left - right;
        n = j + top;
        i1 = 0;
        if (i > 0)
        {
          i2 = minimumLayoutSizeheight;
          i1 = (localJToolBar.getHeight() - i2) / i;
          if (i1 < 0) {
            i1 = 0;
          }
        }
        for (i2 = 0; i2 < localJToolBar.getComponentCount(); i2++)
        {
          localComponent = localJToolBar.getComponent(i2);
          if (localComponent.isVisible())
          {
            localDimension = localComponent.getPreferredSize();
            if ((width >= m) || ((localComponent instanceof JSeparator)))
            {
              i3 = k;
              i4 = m;
            }
            else
            {
              i3 = k + m / 2 - width / 2;
              i4 = width;
            }
            if (isGlue(localComponent)) {
              height += i1;
            }
            localComponent.setBounds(i3, n, i4, height);
            n += height;
          }
        }
      }
      localSynthContext.dispose();
    }
    
    private boolean isGlue(Component paramComponent)
    {
      if ((paramComponent.isVisible()) && ((paramComponent instanceof Box.Filler)))
      {
        Box.Filler localFiller = (Box.Filler)paramComponent;
        Dimension localDimension1 = localFiller.getMinimumSize();
        Dimension localDimension2 = localFiller.getPreferredSize();
        return (width == 0) && (height == 0) && (width == 0) && (height == 0);
      }
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */