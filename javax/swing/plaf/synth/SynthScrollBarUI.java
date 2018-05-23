package javax.swing.plaf.synth;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SynthScrollBarUI
  extends BasicScrollBarUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private SynthStyle thumbStyle;
  private SynthStyle trackStyle;
  private boolean validMinimumThumbSize;
  
  public SynthScrollBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthScrollBarUI();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    trackHighlight = 0;
    if ((scrollbar.getLayout() == null) || ((scrollbar.getLayout() instanceof UIResource))) {
      scrollbar.setLayout(this);
    }
    configureScrollBarColors();
    updateStyle(scrollbar);
  }
  
  protected void configureScrollBarColors() {}
  
  private void updateStyle(JScrollBar paramJScrollBar)
  {
    SynthStyle localSynthStyle = style;
    SynthContext localSynthContext = getContext(paramJScrollBar, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      scrollBarWidth = style.getInt(localSynthContext, "ScrollBar.thumbHeight", 14);
      minimumThumbSize = ((Dimension)style.get(localSynthContext, "ScrollBar.minimumThumbSize"));
      if (minimumThumbSize == null)
      {
        minimumThumbSize = new Dimension();
        validMinimumThumbSize = false;
      }
      else
      {
        validMinimumThumbSize = true;
      }
      maximumThumbSize = ((Dimension)style.get(localSynthContext, "ScrollBar.maximumThumbSize"));
      if (maximumThumbSize == null) {
        maximumThumbSize = new Dimension(4096, 4097);
      }
      incrGap = style.getInt(localSynthContext, "ScrollBar.incrementButtonGap", 0);
      decrGap = style.getInt(localSynthContext, "ScrollBar.decrementButtonGap", 0);
      String str = (String)scrollbar.getClientProperty("JComponent.sizeVariant");
      if (str != null) {
        if ("large".equals(str))
        {
          scrollBarWidth = ((int)(scrollBarWidth * 1.15D));
          incrGap = ((int)(incrGap * 1.15D));
          decrGap = ((int)(decrGap * 1.15D));
        }
        else if ("small".equals(str))
        {
          scrollBarWidth = ((int)(scrollBarWidth * 0.857D));
          incrGap = ((int)(incrGap * 0.857D));
          decrGap = ((int)(decrGap * 0.857D));
        }
        else if ("mini".equals(str))
        {
          scrollBarWidth = ((int)(scrollBarWidth * 0.714D));
          incrGap = ((int)(incrGap * 0.714D));
          decrGap = ((int)(decrGap * 0.714D));
        }
      }
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
    localSynthContext = getContext(paramJScrollBar, Region.SCROLL_BAR_TRACK, 1);
    trackStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
    localSynthContext = getContext(paramJScrollBar, Region.SCROLL_BAR_THUMB, 1);
    thumbStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    scrollbar.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    scrollbar.removePropertyChangeListener(this);
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(scrollbar, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    localSynthContext = getContext(scrollbar, Region.SCROLL_BAR_TRACK, 1);
    trackStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    trackStyle = null;
    localSynthContext = getContext(scrollbar, Region.SCROLL_BAR_THUMB, 1);
    thumbStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    thumbStyle = null;
    super.uninstallDefaults();
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion)
  {
    return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion));
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt)
  {
    SynthStyle localSynthStyle = trackStyle;
    if (paramRegion == Region.SCROLL_BAR_THUMB) {
      localSynthStyle = thumbStyle;
    }
    return SynthContext.getContext(paramJComponent, paramRegion, localSynthStyle, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion)
  {
    if ((paramRegion == Region.SCROLL_BAR_THUMB) && (isThumbRollover()) && (paramJComponent.isEnabled())) {
      return 2;
    }
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public boolean getSupportsAbsolutePositioning()
  {
    SynthContext localSynthContext = getContext(scrollbar);
    boolean bool = style.getBoolean(localSynthContext, "ScrollBar.allowsAbsolutePositioning", false);
    localSynthContext.dispose();
    return bool;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintScrollBarBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), scrollbar.getOrientation());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    SynthContext localSynthContext = getContext(scrollbar, Region.SCROLL_BAR_TRACK);
    paintTrack(localSynthContext, paramGraphics, getTrackBounds());
    localSynthContext.dispose();
    localSynthContext = getContext(scrollbar, Region.SCROLL_BAR_THUMB);
    paintThumb(localSynthContext, paramGraphics, getThumbBounds());
    localSynthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintScrollBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, scrollbar.getOrientation());
  }
  
  protected void paintTrack(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
  {
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintScrollBarTrackBackground(paramSynthContext, paramGraphics, x, y, width, height, scrollbar.getOrientation());
    paramSynthContext.getPainter().paintScrollBarTrackBorder(paramSynthContext, paramGraphics, x, y, width, height, scrollbar.getOrientation());
  }
  
  protected void paintThumb(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
  {
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    int i = scrollbar.getOrientation();
    paramSynthContext.getPainter().paintScrollBarThumbBackground(paramSynthContext, paramGraphics, x, y, width, height, i);
    paramSynthContext.getPainter().paintScrollBarThumbBorder(paramSynthContext, paramGraphics, x, y, width, height, i);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Insets localInsets = paramJComponent.getInsets();
    return scrollbar.getOrientation() == 1 ? new Dimension(scrollBarWidth + left + right, 48) : new Dimension(48, scrollBarWidth + top + bottom);
  }
  
  protected Dimension getMinimumThumbSize()
  {
    if (!validMinimumThumbSize) {
      if (scrollbar.getOrientation() == 1)
      {
        minimumThumbSize.width = scrollBarWidth;
        minimumThumbSize.height = 7;
      }
      else
      {
        minimumThumbSize.width = 7;
        minimumThumbSize.height = scrollBarWidth;
      }
    }
    return minimumThumbSize;
  }
  
  protected JButton createDecreaseButton(int paramInt)
  {
    SynthArrowButton local1 = new SynthArrowButton(paramInt)
    {
      public boolean contains(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (decrGap < 0)
        {
          int i = getWidth();
          int j = getHeight();
          if (scrollbar.getOrientation() == 1) {
            j += decrGap;
          } else {
            i += decrGap;
          }
          return (paramAnonymousInt1 >= 0) && (paramAnonymousInt1 < i) && (paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < j);
        }
        return super.contains(paramAnonymousInt1, paramAnonymousInt2);
      }
    };
    local1.setName("ScrollBar.button");
    return local1;
  }
  
  protected JButton createIncreaseButton(int paramInt)
  {
    SynthArrowButton local2 = new SynthArrowButton(paramInt)
    {
      public boolean contains(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (incrGap < 0)
        {
          int i = getWidth();
          int j = getHeight();
          if (scrollbar.getOrientation() == 1)
          {
            j += incrGap;
            paramAnonymousInt2 += incrGap;
          }
          else
          {
            i += incrGap;
            paramAnonymousInt1 += incrGap;
          }
          return (paramAnonymousInt1 >= 0) && (paramAnonymousInt1 < i) && (paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < j);
        }
        return super.contains(paramAnonymousInt1, paramAnonymousInt2);
      }
    };
    local2.setName("ScrollBar.button");
    return local2;
  }
  
  protected void setThumbRollover(boolean paramBoolean)
  {
    if (isThumbRollover() != paramBoolean)
    {
      scrollbar.repaint(getThumbBounds());
      super.setThumbRollover(paramBoolean);
    }
  }
  
  private void updateButtonDirections()
  {
    int i = scrollbar.getOrientation();
    if (scrollbar.getComponentOrientation().isLeftToRight())
    {
      ((SynthArrowButton)incrButton).setDirection(i == 0 ? 3 : 5);
      ((SynthArrowButton)decrButton).setDirection(i == 0 ? 7 : 1);
    }
    else
    {
      ((SynthArrowButton)incrButton).setDirection(i == 0 ? 7 : 5);
      ((SynthArrowButton)decrButton).setDirection(i == 0 ? 3 : 1);
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JScrollBar)paramPropertyChangeEvent.getSource());
    }
    if ("orientation" == str) {
      updateButtonDirections();
    } else if ("componentOrientation" == str) {
      updateButtonDirections();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */