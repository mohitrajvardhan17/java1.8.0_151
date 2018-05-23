package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI.PropertyChangeHandler;
import sun.swing.SwingUtilities2;

public class MetalScrollBarUI
  extends BasicScrollBarUI
{
  private static Color shadowColor;
  private static Color highlightColor;
  private static Color darkShadowColor;
  private static Color thumbColor;
  private static Color thumbShadow;
  private static Color thumbHighlightColor;
  protected MetalBumps bumps;
  protected MetalScrollButton increaseButton;
  protected MetalScrollButton decreaseButton;
  protected int scrollBarWidth;
  public static final String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
  protected boolean isFreeStanding = true;
  
  public MetalScrollBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalScrollBarUI();
  }
  
  protected void installDefaults()
  {
    scrollBarWidth = ((Integer)UIManager.get("ScrollBar.width")).intValue();
    super.installDefaults();
    bumps = new MetalBumps(10, 10, thumbHighlightColor, thumbShadow, thumbColor);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    ((ScrollBarListener)propertyChangeListener).handlePropertyChange(scrollbar.getClientProperty("JScrollBar.isFreeStanding"));
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return new ScrollBarListener();
  }
  
  protected void configureScrollBarColors()
  {
    super.configureScrollBarColors();
    shadowColor = UIManager.getColor("ScrollBar.shadow");
    highlightColor = UIManager.getColor("ScrollBar.highlight");
    darkShadowColor = UIManager.getColor("ScrollBar.darkShadow");
    thumbColor = UIManager.getColor("ScrollBar.thumb");
    thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");
    thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    if (scrollbar.getOrientation() == 1) {
      return new Dimension(scrollBarWidth, scrollBarWidth * 3 + 10);
    }
    return new Dimension(scrollBarWidth * 3 + 10, scrollBarWidth);
  }
  
  protected JButton createDecreaseButton(int paramInt)
  {
    decreaseButton = new MetalScrollButton(paramInt, scrollBarWidth, isFreeStanding);
    return decreaseButton;
  }
  
  protected JButton createIncreaseButton(int paramInt)
  {
    increaseButton = new MetalScrollButton(paramInt, scrollBarWidth, isFreeStanding);
    return increaseButton;
  }
  
  protected void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    paramGraphics.translate(x, y);
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    int i;
    if (scrollbar.getOrientation() == 1)
    {
      if (!isFreeStanding)
      {
        width += 2;
        if (!bool) {
          paramGraphics.translate(-1, 0);
        }
      }
      if (paramJComponent.isEnabled())
      {
        paramGraphics.setColor(darkShadowColor);
        SwingUtilities2.drawVLine(paramGraphics, 0, 0, height - 1);
        SwingUtilities2.drawVLine(paramGraphics, width - 2, 0, height - 1);
        SwingUtilities2.drawHLine(paramGraphics, 2, width - 1, height - 1);
        SwingUtilities2.drawHLine(paramGraphics, 2, width - 2, 0);
        paramGraphics.setColor(shadowColor);
        SwingUtilities2.drawVLine(paramGraphics, 1, 1, height - 2);
        SwingUtilities2.drawHLine(paramGraphics, 1, width - 3, 1);
        if (scrollbar.getValue() != scrollbar.getMaximum())
        {
          i = thumbRect.y + thumbRect.height - y;
          SwingUtilities2.drawHLine(paramGraphics, 1, width - 1, i);
        }
        paramGraphics.setColor(highlightColor);
        SwingUtilities2.drawVLine(paramGraphics, width - 1, 0, height - 1);
      }
      else
      {
        MetalUtils.drawDisabledBorder(paramGraphics, 0, 0, width, height);
      }
      if (!isFreeStanding)
      {
        width -= 2;
        if (!bool) {
          paramGraphics.translate(1, 0);
        }
      }
    }
    else
    {
      if (!isFreeStanding) {
        height += 2;
      }
      if (paramJComponent.isEnabled())
      {
        paramGraphics.setColor(darkShadowColor);
        SwingUtilities2.drawHLine(paramGraphics, 0, width - 1, 0);
        SwingUtilities2.drawVLine(paramGraphics, 0, 2, height - 2);
        SwingUtilities2.drawHLine(paramGraphics, 0, width - 1, height - 2);
        SwingUtilities2.drawVLine(paramGraphics, width - 1, 2, height - 1);
        paramGraphics.setColor(shadowColor);
        SwingUtilities2.drawHLine(paramGraphics, 1, width - 2, 1);
        SwingUtilities2.drawVLine(paramGraphics, 1, 1, height - 3);
        SwingUtilities2.drawHLine(paramGraphics, 0, width - 1, height - 1);
        if (scrollbar.getValue() != scrollbar.getMaximum())
        {
          i = thumbRect.x + thumbRect.width - x;
          SwingUtilities2.drawVLine(paramGraphics, i, 1, height - 1);
        }
      }
      else
      {
        MetalUtils.drawDisabledBorder(paramGraphics, 0, 0, width, height);
      }
      if (!isFreeStanding) {
        height -= 2;
      }
    }
    paramGraphics.translate(-x, -y);
  }
  
  protected void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    if (!paramJComponent.isEnabled()) {
      return;
    }
    if (MetalLookAndFeel.usingOcean())
    {
      oceanPaintThumb(paramGraphics, paramJComponent, paramRectangle);
      return;
    }
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    paramGraphics.translate(x, y);
    if (scrollbar.getOrientation() == 1)
    {
      if (!isFreeStanding)
      {
        width += 2;
        if (!bool) {
          paramGraphics.translate(-1, 0);
        }
      }
      paramGraphics.setColor(thumbColor);
      paramGraphics.fillRect(0, 0, width - 2, height - 1);
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, width - 2, height - 1);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, width - 3, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, height - 2);
      bumps.setBumpArea(width - 6, height - 7);
      bumps.paintIcon(paramJComponent, paramGraphics, 3, 4);
      if (!isFreeStanding)
      {
        width -= 2;
        if (!bool) {
          paramGraphics.translate(1, 0);
        }
      }
    }
    else
    {
      if (!isFreeStanding) {
        height += 2;
      }
      paramGraphics.setColor(thumbColor);
      paramGraphics.fillRect(0, 0, width - 1, height - 2);
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, width - 1, height - 2);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, width - 3, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, height - 3);
      bumps.setBumpArea(width - 7, height - 6);
      bumps.paintIcon(paramJComponent, paramGraphics, 4, 3);
      if (!isFreeStanding) {
        height -= 2;
      }
    }
    paramGraphics.translate(-x, -y);
  }
  
  private void oceanPaintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    paramGraphics.translate(x, y);
    int i;
    int j;
    int k;
    if (scrollbar.getOrientation() == 1)
    {
      if (!isFreeStanding)
      {
        width += 2;
        if (!bool) {
          paramGraphics.translate(-1, 0);
        }
      }
      if (thumbColor != null)
      {
        paramGraphics.setColor(thumbColor);
        paramGraphics.fillRect(0, 0, width - 2, height - 1);
      }
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, width - 2, height - 1);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, width - 3, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, height - 2);
      MetalUtils.drawGradient(paramJComponent, paramGraphics, "ScrollBar.gradient", 2, 2, width - 4, height - 3, false);
      i = width - 8;
      if ((i > 2) && (height >= 10))
      {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        j = height / 2 - 2;
        for (k = 0; k < 6; k += 2) {
          paramGraphics.fillRect(4, k + j, i, 1);
        }
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        j++;
        for (k = 0; k < 6; k += 2) {
          paramGraphics.fillRect(5, k + j, i, 1);
        }
      }
      if (!isFreeStanding)
      {
        width -= 2;
        if (!bool) {
          paramGraphics.translate(1, 0);
        }
      }
    }
    else
    {
      if (!isFreeStanding) {
        height += 2;
      }
      if (thumbColor != null)
      {
        paramGraphics.setColor(thumbColor);
        paramGraphics.fillRect(0, 0, width - 1, height - 2);
      }
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, width - 1, height - 2);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, width - 2, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, height - 3);
      MetalUtils.drawGradient(paramJComponent, paramGraphics, "ScrollBar.gradient", 2, 2, width - 3, height - 4, true);
      i = height - 8;
      if ((i > 2) && (width >= 10))
      {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        j = width / 2 - 2;
        for (k = 0; k < 6; k += 2) {
          paramGraphics.fillRect(j + k, 4, 1, i);
        }
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        j++;
        for (k = 0; k < 6; k += 2) {
          paramGraphics.fillRect(j + k, 5, 1, i);
        }
      }
      if (!isFreeStanding) {
        height -= 2;
      }
    }
    paramGraphics.translate(-x, -y);
  }
  
  protected Dimension getMinimumThumbSize()
  {
    return new Dimension(scrollBarWidth, scrollBarWidth);
  }
  
  protected void setThumbBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((thumbRect.x == paramInt1) && (thumbRect.y == paramInt2) && (thumbRect.width == paramInt3) && (thumbRect.height == paramInt4)) {
      return;
    }
    int i = Math.min(paramInt1, thumbRect.x);
    int j = Math.min(paramInt2, thumbRect.y);
    int k = Math.max(paramInt1 + paramInt3, thumbRect.x + thumbRect.width);
    int m = Math.max(paramInt2 + paramInt4, thumbRect.y + thumbRect.height);
    thumbRect.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    scrollbar.repaint(i, j, k - i + 1, m - j + 1);
  }
  
  class ScrollBarListener
    extends BasicScrollBarUI.PropertyChangeHandler
  {
    ScrollBarListener()
    {
      super();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str.equals("JScrollBar.isFreeStanding")) {
        handlePropertyChange(paramPropertyChangeEvent.getNewValue());
      } else {
        super.propertyChange(paramPropertyChangeEvent);
      }
    }
    
    public void handlePropertyChange(Object paramObject)
    {
      if (paramObject != null)
      {
        boolean bool = ((Boolean)paramObject).booleanValue();
        int i = (!bool) && (isFreeStanding == true) ? 1 : 0;
        int j = (bool == true) && (!isFreeStanding) ? 1 : 0;
        isFreeStanding = bool;
        if (i != 0) {
          toFlush();
        } else if (j != 0) {
          toFreeStanding();
        }
      }
      else if (!isFreeStanding)
      {
        isFreeStanding = true;
        toFreeStanding();
      }
      if (increaseButton != null) {
        increaseButton.setFreeStanding(isFreeStanding);
      }
      if (decreaseButton != null) {
        decreaseButton.setFreeStanding(isFreeStanding);
      }
    }
    
    protected void toFlush()
    {
      scrollBarWidth -= 2;
    }
    
    protected void toFreeStanding()
    {
      scrollBarWidth += 2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */