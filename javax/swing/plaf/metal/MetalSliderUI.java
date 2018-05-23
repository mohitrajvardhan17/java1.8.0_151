package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSliderUI.PropertyChangeHandler;
import javax.swing.plaf.basic.BasicSliderUI.ScrollListener;

public class MetalSliderUI
  extends BasicSliderUI
{
  protected final int TICK_BUFFER = 4;
  protected boolean filledSlider = false;
  protected static Color thumbColor;
  protected static Color highlightColor;
  protected static Color darkShadowColor;
  protected static int trackWidth;
  protected static int tickLength;
  private int safeLength;
  protected static Icon horizThumbIcon;
  protected static Icon vertThumbIcon;
  private static Icon SAFE_HORIZ_THUMB_ICON;
  private static Icon SAFE_VERT_THUMB_ICON;
  protected final String SLIDER_FILL = "JSlider.isFilled";
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalSliderUI();
  }
  
  public MetalSliderUI()
  {
    super(null);
  }
  
  private static Icon getHorizThumbIcon()
  {
    if (System.getSecurityManager() != null) {
      return SAFE_HORIZ_THUMB_ICON;
    }
    return horizThumbIcon;
  }
  
  private static Icon getVertThumbIcon()
  {
    if (System.getSecurityManager() != null) {
      return SAFE_VERT_THUMB_ICON;
    }
    return vertThumbIcon;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    trackWidth = ((Integer)UIManager.get("Slider.trackWidth")).intValue();
    tickLength = safeLength = ((Integer)UIManager.get("Slider.majorTickLength")).intValue();
    horizThumbIcon = SAFE_HORIZ_THUMB_ICON = UIManager.getIcon("Slider.horizontalThumbIcon");
    vertThumbIcon = SAFE_VERT_THUMB_ICON = UIManager.getIcon("Slider.verticalThumbIcon");
    super.installUI(paramJComponent);
    thumbColor = UIManager.getColor("Slider.thumb");
    highlightColor = UIManager.getColor("Slider.highlight");
    darkShadowColor = UIManager.getColor("Slider.darkShadow");
    scrollListener.setScrollByBlock(false);
    prepareFilledSliderField();
  }
  
  protected PropertyChangeListener createPropertyChangeListener(JSlider paramJSlider)
  {
    return new MetalPropertyListener();
  }
  
  private void prepareFilledSliderField()
  {
    filledSlider = MetalLookAndFeel.usingOcean();
    Object localObject = slider.getClientProperty("JSlider.isFilled");
    if (localObject != null) {
      filledSlider = ((Boolean)localObject).booleanValue();
    }
  }
  
  public void paintThumb(Graphics paramGraphics)
  {
    Rectangle localRectangle = thumbRect;
    paramGraphics.translate(x, y);
    if (slider.getOrientation() == 0) {
      getHorizThumbIcon().paintIcon(slider, paramGraphics, 0, 0);
    } else {
      getVertThumbIcon().paintIcon(slider, paramGraphics, 0, 0);
    }
    paramGraphics.translate(-x, -y);
  }
  
  private Rectangle getPaintTrackRect()
  {
    int i = 0;
    int k = 0;
    int m;
    int j;
    if (slider.getOrientation() == 0)
    {
      m = trackRect.height - 1 - getThumbOverhang();
      k = m - (getTrackWidth() - 1);
      j = trackRect.width - 1;
    }
    else
    {
      if (MetalUtils.isLeftToRight(slider))
      {
        i = trackRect.width - getThumbOverhang() - getTrackWidth();
        j = trackRect.width - getThumbOverhang() - 1;
      }
      else
      {
        i = getThumbOverhang();
        j = getThumbOverhang() + getTrackWidth() - 1;
      }
      m = trackRect.height - 1;
    }
    return new Rectangle(trackRect.x + i, trackRect.y + k, j - i, m - k);
  }
  
  public void paintTrack(Graphics paramGraphics)
  {
    if (MetalLookAndFeel.usingOcean())
    {
      oceanPaintTrack(paramGraphics);
      return;
    }
    Color localColor = !slider.isEnabled() ? MetalLookAndFeel.getControlShadow() : slider.getForeground();
    boolean bool = MetalUtils.isLeftToRight(slider);
    paramGraphics.translate(trackRect.x, trackRect.y);
    int i = 0;
    int j = 0;
    int m;
    int k;
    if (slider.getOrientation() == 0)
    {
      m = trackRect.height - 1 - getThumbOverhang();
      j = m - (getTrackWidth() - 1);
      k = trackRect.width - 1;
    }
    else
    {
      if (bool)
      {
        i = trackRect.width - getThumbOverhang() - getTrackWidth();
        k = trackRect.width - getThumbOverhang() - 1;
      }
      else
      {
        i = getThumbOverhang();
        k = getThumbOverhang() + getTrackWidth() - 1;
      }
      m = trackRect.height - 1;
    }
    if (slider.isEnabled())
    {
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.drawRect(i, j, k - i - 1, m - j - 1);
      paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
      paramGraphics.drawLine(i + 1, m, k, m);
      paramGraphics.drawLine(k, j + 1, k, m);
      paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
      paramGraphics.drawLine(i + 1, j + 1, k - 2, j + 1);
      paramGraphics.drawLine(i + 1, j + 1, i + 1, m - 2);
    }
    else
    {
      paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
      paramGraphics.drawRect(i, j, k - i - 1, m - j - 1);
    }
    if (filledSlider)
    {
      int n;
      int i1;
      int i3;
      int i2;
      int i4;
      if (slider.getOrientation() == 0)
      {
        n = thumbRect.x + thumbRect.width / 2;
        n -= trackRect.x;
        i1 = !slider.isEnabled() ? j : j + 1;
        i3 = !slider.isEnabled() ? m - 1 : m - 2;
        if (!drawInverted())
        {
          i2 = !slider.isEnabled() ? i : i + 1;
          i4 = n;
        }
        else
        {
          i2 = n;
          i4 = !slider.isEnabled() ? k - 1 : k - 2;
        }
      }
      else
      {
        n = thumbRect.y + thumbRect.height / 2;
        n -= trackRect.y;
        i2 = !slider.isEnabled() ? i : i + 1;
        i4 = !slider.isEnabled() ? k - 1 : k - 2;
        if (!drawInverted())
        {
          i1 = n;
          i3 = !slider.isEnabled() ? m - 1 : m - 2;
        }
        else
        {
          i1 = !slider.isEnabled() ? j : j + 1;
          i3 = n;
        }
      }
      if (slider.isEnabled())
      {
        paramGraphics.setColor(slider.getBackground());
        paramGraphics.drawLine(i2, i1, i4, i1);
        paramGraphics.drawLine(i2, i1, i2, i3);
        paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
        paramGraphics.fillRect(i2 + 1, i1 + 1, i4 - i2, i3 - i1);
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
        paramGraphics.fillRect(i2, i1, i4 - i2, i3 - i1);
      }
    }
    paramGraphics.translate(-trackRect.x, -trackRect.y);
  }
  
  private void oceanPaintTrack(Graphics paramGraphics)
  {
    boolean bool1 = MetalUtils.isLeftToRight(slider);
    boolean bool2 = drawInverted();
    Color localColor = (Color)UIManager.get("Slider.altTrackColor");
    Rectangle localRectangle = getPaintTrackRect();
    paramGraphics.translate(x, y);
    int i = width;
    int j = height;
    int k;
    int m;
    int n;
    if (slider.getOrientation() == 0)
    {
      k = thumbRect.x + thumbRect.width / 2 - x;
      if (slider.isEnabled())
      {
        if (k > 0)
        {
          paramGraphics.setColor(bool2 ? MetalLookAndFeel.getControlDarkShadow() : MetalLookAndFeel.getPrimaryControlDarkShadow());
          paramGraphics.drawRect(0, 0, k - 1, j - 1);
        }
        if (k < i)
        {
          paramGraphics.setColor(bool2 ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(k, 0, i - k - 1, j - 1);
        }
        if (filledSlider)
        {
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
          if (bool2)
          {
            m = k;
            n = i - 2;
            paramGraphics.drawLine(1, 1, k, 1);
          }
          else
          {
            m = 1;
            n = k;
            paramGraphics.drawLine(k, 1, i - 1, 1);
          }
          if (j == 6)
          {
            paramGraphics.setColor(MetalLookAndFeel.getWhite());
            paramGraphics.drawLine(m, 1, n, 1);
            paramGraphics.setColor(localColor);
            paramGraphics.drawLine(m, 2, n, 2);
            paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
            paramGraphics.drawLine(m, 3, n, 3);
            paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
            paramGraphics.drawLine(m, 4, n, 4);
          }
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
        if (k > 0) {
          if ((!bool2) && (filledSlider)) {
            paramGraphics.fillRect(0, 0, k - 1, j - 1);
          } else {
            paramGraphics.drawRect(0, 0, k - 1, j - 1);
          }
        }
        if (k < i) {
          if ((bool2) && (filledSlider)) {
            paramGraphics.fillRect(k, 0, i - k - 1, j - 1);
          } else {
            paramGraphics.drawRect(k, 0, i - k - 1, j - 1);
          }
        }
      }
    }
    else
    {
      k = thumbRect.y + thumbRect.height / 2 - y;
      if (slider.isEnabled())
      {
        if (k > 0)
        {
          paramGraphics.setColor(bool2 ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(0, 0, i - 1, k - 1);
        }
        if (k < j)
        {
          paramGraphics.setColor(bool2 ? MetalLookAndFeel.getControlDarkShadow() : MetalLookAndFeel.getPrimaryControlDarkShadow());
          paramGraphics.drawRect(0, k, i - 1, j - k - 1);
        }
        if (filledSlider)
        {
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
          if (drawInverted())
          {
            m = 1;
            n = k;
            if (bool1) {
              paramGraphics.drawLine(1, k, 1, j - 1);
            } else {
              paramGraphics.drawLine(i - 2, k, i - 2, j - 1);
            }
          }
          else
          {
            m = k;
            n = j - 2;
            if (bool1) {
              paramGraphics.drawLine(1, 1, 1, k);
            } else {
              paramGraphics.drawLine(i - 2, 1, i - 2, k);
            }
          }
          if (i == 6)
          {
            paramGraphics.setColor(bool1 ? MetalLookAndFeel.getWhite() : MetalLookAndFeel.getPrimaryControlShadow());
            paramGraphics.drawLine(1, m, 1, n);
            paramGraphics.setColor(bool1 ? localColor : MetalLookAndFeel.getControlShadow());
            paramGraphics.drawLine(2, m, 2, n);
            paramGraphics.setColor(bool1 ? MetalLookAndFeel.getControlShadow() : localColor);
            paramGraphics.drawLine(3, m, 3, n);
            paramGraphics.setColor(bool1 ? MetalLookAndFeel.getPrimaryControlShadow() : MetalLookAndFeel.getWhite());
            paramGraphics.drawLine(4, m, 4, n);
          }
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
        if (k > 0) {
          if ((bool2) && (filledSlider)) {
            paramGraphics.fillRect(0, 0, i - 1, k - 1);
          } else {
            paramGraphics.drawRect(0, 0, i - 1, k - 1);
          }
        }
        if (k < j) {
          if ((!bool2) && (filledSlider)) {
            paramGraphics.fillRect(0, k, i - 1, j - k - 1);
          } else {
            paramGraphics.drawRect(0, k, i - 1, j - k - 1);
          }
        }
      }
    }
    paramGraphics.translate(-x, -y);
  }
  
  public void paintFocus(Graphics paramGraphics) {}
  
  protected Dimension getThumbSize()
  {
    Dimension localDimension = new Dimension();
    if (slider.getOrientation() == 1)
    {
      width = getVertThumbIcon().getIconWidth();
      height = getVertThumbIcon().getIconHeight();
    }
    else
    {
      width = getHorizThumbIcon().getIconWidth();
      height = getHorizThumbIcon().getIconHeight();
    }
    return localDimension;
  }
  
  public int getTickLength()
  {
    return slider.getOrientation() == 0 ? safeLength + 4 + 1 : safeLength + 4 + 3;
  }
  
  protected int getTrackWidth()
  {
    if (slider.getOrientation() == 0) {
      return (int)(0.4375D * thumbRect.height);
    }
    return (int)(0.4375D * thumbRect.width);
  }
  
  protected int getTrackLength()
  {
    if (slider.getOrientation() == 0) {
      return trackRect.width;
    }
    return trackRect.height;
  }
  
  protected int getThumbOverhang()
  {
    return (int)(getThumbSize().getHeight() - getTrackWidth()) / 2;
  }
  
  protected void scrollDueToClickInTrack(int paramInt)
  {
    scrollByUnit(paramInt);
  }
  
  protected void paintMinorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.setColor(slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow());
    paramGraphics.drawLine(paramInt, 4, paramInt, 4 + safeLength / 2);
  }
  
  protected void paintMajorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.setColor(slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow());
    paramGraphics.drawLine(paramInt, 4, paramInt, 4 + (safeLength - 1));
  }
  
  protected void paintMinorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.setColor(slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow());
    if (MetalUtils.isLeftToRight(slider)) {
      paramGraphics.drawLine(4, paramInt, 4 + safeLength / 2, paramInt);
    } else {
      paramGraphics.drawLine(0, paramInt, safeLength / 2, paramInt);
    }
  }
  
  protected void paintMajorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.setColor(slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow());
    if (MetalUtils.isLeftToRight(slider)) {
      paramGraphics.drawLine(4, paramInt, 4 + safeLength, paramInt);
    } else {
      paramGraphics.drawLine(0, paramInt, safeLength, paramInt);
    }
  }
  
  protected class MetalPropertyListener
    extends BasicSliderUI.PropertyChangeHandler
  {
    protected MetalPropertyListener()
    {
      super();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      super.propertyChange(paramPropertyChangeEvent);
      if (paramPropertyChangeEvent.getPropertyName().equals("JSlider.isFilled")) {
        MetalSliderUI.this.prepareFilledSliderField();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalSliderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */