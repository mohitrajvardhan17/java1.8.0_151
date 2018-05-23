package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSliderUI.TrackListener;
import sun.swing.SwingUtilities2;

public class SynthSliderUI
  extends BasicSliderUI
  implements PropertyChangeListener, SynthUI
{
  private Rectangle valueRect = new Rectangle();
  private boolean paintValue;
  private Dimension lastSize;
  private int trackHeight;
  private int trackBorder;
  private int thumbWidth;
  private int thumbHeight;
  private SynthStyle style;
  private SynthStyle sliderTrackStyle;
  private SynthStyle sliderThumbStyle;
  private transient boolean thumbActive;
  private transient boolean thumbPressed;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthSliderUI((JSlider)paramJComponent);
  }
  
  protected SynthSliderUI(JSlider paramJSlider)
  {
    super(paramJSlider);
  }
  
  protected void installDefaults(JSlider paramJSlider)
  {
    updateStyle(paramJSlider);
  }
  
  protected void uninstallDefaults(JSlider paramJSlider)
  {
    SynthContext localSynthContext = getContext(paramJSlider, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    localSynthContext = getContext(paramJSlider, Region.SLIDER_TRACK, 1);
    sliderTrackStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    sliderTrackStyle = null;
    localSynthContext = getContext(paramJSlider, Region.SLIDER_THUMB, 1);
    sliderThumbStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    sliderThumbStyle = null;
  }
  
  protected void installListeners(JSlider paramJSlider)
  {
    super.installListeners(paramJSlider);
    paramJSlider.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners(JSlider paramJSlider)
  {
    paramJSlider.removePropertyChangeListener(this);
    super.uninstallListeners(paramJSlider);
  }
  
  private void updateStyle(JSlider paramJSlider)
  {
    SynthContext localSynthContext = getContext(paramJSlider, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      thumbWidth = style.getInt(localSynthContext, "Slider.thumbWidth", 30);
      thumbHeight = style.getInt(localSynthContext, "Slider.thumbHeight", 14);
      String str = (String)slider.getClientProperty("JComponent.sizeVariant");
      if (str != null) {
        if ("large".equals(str))
        {
          thumbWidth = ((int)(thumbWidth * 1.15D));
          thumbHeight = ((int)(thumbHeight * 1.15D));
        }
        else if ("small".equals(str))
        {
          thumbWidth = ((int)(thumbWidth * 0.857D));
          thumbHeight = ((int)(thumbHeight * 0.857D));
        }
        else if ("mini".equals(str))
        {
          thumbWidth = ((int)(thumbWidth * 0.784D));
          thumbHeight = ((int)(thumbHeight * 0.784D));
        }
      }
      trackBorder = style.getInt(localSynthContext, "Slider.trackBorder", 1);
      trackHeight = (thumbHeight + trackBorder * 2);
      paintValue = style.getBoolean(localSynthContext, "Slider.paintValue", true);
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions(paramJSlider);
        installKeyboardActions(paramJSlider);
      }
    }
    localSynthContext.dispose();
    localSynthContext = getContext(paramJSlider, Region.SLIDER_TRACK, 1);
    sliderTrackStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
    localSynthContext = getContext(paramJSlider, Region.SLIDER_THUMB, 1);
    sliderThumbStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
  }
  
  protected BasicSliderUI.TrackListener createTrackListener(JSlider paramJSlider)
  {
    return new SynthTrackListener(null);
  }
  
  private void updateThumbState(int paramInt1, int paramInt2)
  {
    setThumbActive(thumbRect.contains(paramInt1, paramInt2));
  }
  
  private void updateThumbState(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    updateThumbState(paramInt1, paramInt2);
    setThumbPressed(paramBoolean);
  }
  
  private void setThumbActive(boolean paramBoolean)
  {
    if (thumbActive != paramBoolean)
    {
      thumbActive = paramBoolean;
      slider.repaint(thumbRect);
    }
  }
  
  private void setThumbPressed(boolean paramBoolean)
  {
    if (thumbPressed != paramBoolean)
    {
      thumbPressed = paramBoolean;
      slider.repaint(thumbRect);
    }
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    if (paramJComponent == null) {
      throw new NullPointerException("Component must be non-null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Width and height must be >= 0");
    }
    if ((slider.getPaintLabels()) && (labelsHaveSameBaselines()))
    {
      Insets localInsets = new Insets(0, 0, 0, 0);
      SynthContext localSynthContext1 = getContext(slider, Region.SLIDER_TRACK);
      style.getInsets(localSynthContext1, localInsets);
      localSynthContext1.dispose();
      int j;
      int k;
      int i1;
      if (slider.getOrientation() == 0)
      {
        int i = 0;
        if (paintValue)
        {
          SynthContext localSynthContext2 = getContext(slider);
          i = localSynthContext2.getStyle().getGraphicsUtils(localSynthContext2).getMaximumCharHeight(localSynthContext2);
          localSynthContext2.dispose();
        }
        j = 0;
        if (slider.getPaintTicks()) {
          j = getTickLength();
        }
        k = getHeightOfTallestLabel();
        int m = i + trackHeight + top + bottom + j + k + 4;
        i1 = paramInt2 / 2 - m / 2;
        i1 += i + 2;
        i1 += trackHeight + top + bottom;
        i1 += j + 2;
        JComponent localJComponent1 = (JComponent)slider.getLabelTable().elements().nextElement();
        Dimension localDimension1 = localJComponent1.getPreferredSize();
        return i1 + localJComponent1.getBaseline(width, height);
      }
      Integer localInteger = slider.getInverted() ? getLowestValue() : getHighestValue();
      if (localInteger != null)
      {
        j = insetCache.top;
        k = 0;
        if (paintValue)
        {
          SynthContext localSynthContext3 = getContext(slider);
          k = localSynthContext3.getStyle().getGraphicsUtils(localSynthContext3).getMaximumCharHeight(localSynthContext3);
          localSynthContext3.dispose();
        }
        int n = paramInt2 - insetCache.top - insetCache.bottom;
        i1 = j + k;
        int i2 = n - k;
        int i3 = yPositionForValue(localInteger.intValue(), i1, i2);
        JComponent localJComponent2 = (JComponent)slider.getLabelTable().get(localInteger);
        Dimension localDimension2 = localJComponent2.getPreferredSize();
        return i3 - height / 2 + localJComponent2.getBaseline(width, height);
      }
    }
    return -1;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    recalculateIfInsetsChanged();
    Dimension localDimension = new Dimension(contentRect.width, contentRect.height);
    if (slider.getOrientation() == 1) {
      height = 200;
    } else {
      width = 200;
    }
    Insets localInsets = slider.getInsets();
    width += left + right;
    height += top + bottom;
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    recalculateIfInsetsChanged();
    Dimension localDimension = new Dimension(contentRect.width, contentRect.height);
    if (slider.getOrientation() == 1) {
      height = (thumbRect.height + insetCache.top + insetCache.bottom);
    } else {
      width = (thumbRect.width + insetCache.left + insetCache.right);
    }
    return localDimension;
  }
  
  protected void calculateGeometry()
  {
    calculateThumbSize();
    layout();
    calculateThumbLocation();
  }
  
  protected void layout()
  {
    SynthContext localSynthContext1 = getContext(slider);
    SynthGraphicsUtils localSynthGraphicsUtils = style.getGraphicsUtils(localSynthContext1);
    Insets localInsets = new Insets(0, 0, 0, 0);
    SynthContext localSynthContext2 = getContext(slider, Region.SLIDER_TRACK);
    style.getInsets(localSynthContext2, localInsets);
    localSynthContext2.dispose();
    int k;
    int m;
    int n;
    int j;
    if (slider.getOrientation() == 0)
    {
      valueRect.height = 0;
      if (paintValue) {
        valueRect.height = localSynthGraphicsUtils.getMaximumCharHeight(localSynthContext1);
      }
      trackRect.height = trackHeight;
      tickRect.height = 0;
      if (slider.getPaintTicks()) {
        tickRect.height = getTickLength();
      }
      labelRect.height = 0;
      if (slider.getPaintLabels()) {
        labelRect.height = getHeightOfTallestLabel();
      }
      contentRect.height = (valueRect.height + trackRect.height + top + bottom + tickRect.height + labelRect.height + 4);
      contentRect.width = (slider.getWidth() - insetCache.left - insetCache.right);
      int i = 0;
      if (slider.getPaintLabels())
      {
        trackRect.x = insetCache.left;
        trackRect.width = contentRect.width;
        Dictionary localDictionary = slider.getLabelTable();
        if (localDictionary != null)
        {
          k = slider.getMinimum();
          m = slider.getMaximum();
          n = Integer.MAX_VALUE;
          int i1 = Integer.MIN_VALUE;
          Enumeration localEnumeration = localDictionary.keys();
          while (localEnumeration.hasMoreElements())
          {
            int i2 = ((Integer)localEnumeration.nextElement()).intValue();
            if ((i2 >= k) && (i2 < n)) {
              n = i2;
            }
            if ((i2 <= m) && (i2 > i1)) {
              i1 = i2;
            }
          }
          i = getPadForLabel(n);
          i = Math.max(i, getPadForLabel(i1));
        }
      }
      valueRect.x = (trackRect.x = tickRect.x = labelRect.x = insetCache.left + i);
      valueRect.width = (trackRect.width = tickRect.width = labelRect.width = contentRect.width - i * 2);
      j = slider.getHeight() / 2 - contentRect.height / 2;
      valueRect.y = j;
      j += valueRect.height + 2;
      trackRect.y = (j + top);
      j += trackRect.height + top + bottom;
      tickRect.y = j;
      j += tickRect.height + 2;
      labelRect.y = j;
      j += labelRect.height;
    }
    else
    {
      trackRect.width = trackHeight;
      tickRect.width = 0;
      if (slider.getPaintTicks()) {
        tickRect.width = getTickLength();
      }
      labelRect.width = 0;
      if (slider.getPaintLabels()) {
        labelRect.width = getWidthOfWidestLabel();
      }
      valueRect.y = insetCache.top;
      valueRect.height = 0;
      if (paintValue) {
        valueRect.height = localSynthGraphicsUtils.getMaximumCharHeight(localSynthContext1);
      }
      FontMetrics localFontMetrics = slider.getFontMetrics(slider.getFont());
      valueRect.width = Math.max(localSynthGraphicsUtils.computeStringWidth(localSynthContext1, slider.getFont(), localFontMetrics, "" + slider.getMaximum()), localSynthGraphicsUtils.computeStringWidth(localSynthContext1, slider.getFont(), localFontMetrics, "" + slider.getMinimum()));
      j = valueRect.width / 2;
      k = left + trackRect.width / 2;
      m = trackRect.width / 2 + right + tickRect.width + labelRect.width;
      contentRect.width = (Math.max(k, j) + Math.max(m, j) + 2 + insetCache.left + insetCache.right);
      contentRect.height = (slider.getHeight() - insetCache.top - insetCache.bottom);
      trackRect.y = (tickRect.y = labelRect.y = valueRect.y + valueRect.height);
      trackRect.height = (tickRect.height = labelRect.height = contentRect.height - valueRect.height);
      n = slider.getWidth() / 2 - contentRect.width / 2;
      if (SynthLookAndFeel.isLeftToRight(slider))
      {
        if (j > k) {
          n += j - k;
        }
        trackRect.x = (n + left);
        n += left + trackRect.width + right;
        tickRect.x = n;
        labelRect.x = (n + tickRect.width + 2);
      }
      else
      {
        if (j > m) {
          n += j - m;
        }
        labelRect.x = n;
        n += labelRect.width + 2;
        tickRect.x = n;
        trackRect.x = (n + tickRect.width + left);
      }
    }
    localSynthContext1.dispose();
    lastSize = slider.getSize();
  }
  
  private int getPadForLabel(int paramInt)
  {
    int i = 0;
    JComponent localJComponent = (JComponent)slider.getLabelTable().get(Integer.valueOf(paramInt));
    if (localJComponent != null)
    {
      int j = xPositionForValue(paramInt);
      int k = getPreferredSizewidth / 2;
      if (j - k < insetCache.left) {
        i = Math.max(i, insetCache.left - (j - k));
      }
      if (j + k > slider.getWidth() - insetCache.right) {
        i = Math.max(i, j + k - (slider.getWidth() - insetCache.right));
      }
    }
    return i;
  }
  
  protected void calculateThumbLocation()
  {
    super.calculateThumbLocation();
    if (slider.getOrientation() == 0) {
      thumbRect.y += trackBorder;
    } else {
      thumbRect.x += trackBorder;
    }
    Point localPoint = slider.getMousePosition();
    if (localPoint != null) {
      updateThumbState(x, y);
    }
  }
  
  public void setThumbLocation(int paramInt1, int paramInt2)
  {
    super.setThumbLocation(paramInt1, paramInt2);
    slider.repaint(valueRect.x, valueRect.y, valueRect.width, valueRect.height);
    setThumbActive(false);
  }
  
  protected int xPositionForValue(int paramInt)
  {
    int i = slider.getMinimum();
    int j = slider.getMaximum();
    int k = trackRect.x + thumbRect.width / 2 + trackBorder;
    int m = trackRect.x + trackRect.width - thumbRect.width / 2 - trackBorder;
    int n = m - k;
    double d1 = j - i;
    double d2 = n / d1;
    if (!drawInverted())
    {
      i1 = k;
      i1 = (int)(i1 + Math.round(d2 * (paramInt - i)));
    }
    else
    {
      i1 = m;
      i1 = (int)(i1 - Math.round(d2 * (paramInt - i)));
    }
    int i1 = Math.max(k, i1);
    i1 = Math.min(m, i1);
    return i1;
  }
  
  protected int yPositionForValue(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = slider.getMinimum();
    int j = slider.getMaximum();
    int k = paramInt2 + thumbRect.height / 2 + trackBorder;
    int m = paramInt2 + paramInt3 - thumbRect.height / 2 - trackBorder;
    int n = m - k;
    double d1 = j - i;
    double d2 = n / d1;
    if (!drawInverted())
    {
      i1 = k;
      i1 = (int)(i1 + Math.round(d2 * (j - paramInt1)));
    }
    else
    {
      i1 = k;
      i1 = (int)(i1 + Math.round(d2 * (paramInt1 - i)));
    }
    int i1 = Math.max(k, i1);
    i1 = Math.min(m, i1);
    return i1;
  }
  
  public int valueForYPosition(int paramInt)
  {
    int j = slider.getMinimum();
    int k = slider.getMaximum();
    int m = trackRect.y + thumbRect.height / 2 + trackBorder;
    int n = trackRect.y + trackRect.height - thumbRect.height / 2 - trackBorder;
    int i1 = n - m;
    int i;
    if (paramInt <= m)
    {
      i = drawInverted() ? j : k;
    }
    else if (paramInt >= n)
    {
      i = drawInverted() ? k : j;
    }
    else
    {
      int i2 = paramInt - m;
      double d1 = k - j;
      double d2 = d1 / i1;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? j + i3 : k - i3;
    }
    return i;
  }
  
  public int valueForXPosition(int paramInt)
  {
    int j = slider.getMinimum();
    int k = slider.getMaximum();
    int m = trackRect.x + thumbRect.width / 2 + trackBorder;
    int n = trackRect.x + trackRect.width - thumbRect.width / 2 - trackBorder;
    int i1 = n - m;
    int i;
    if (paramInt <= m)
    {
      i = drawInverted() ? k : j;
    }
    else if (paramInt >= n)
    {
      i = drawInverted() ? j : k;
    }
    else
    {
      int i2 = paramInt - m;
      double d1 = k - j;
      double d2 = d1 / i1;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? k - i3 : j + i3;
    }
    return i;
  }
  
  protected Dimension getThumbSize()
  {
    Dimension localDimension = new Dimension();
    if (slider.getOrientation() == 1)
    {
      width = thumbHeight;
      height = thumbWidth;
    }
    else
    {
      width = thumbWidth;
      height = thumbHeight;
    }
    return localDimension;
  }
  
  protected void recalculateIfInsetsChanged()
  {
    SynthContext localSynthContext = getContext(slider);
    Insets localInsets1 = style.getInsets(localSynthContext, null);
    Insets localInsets2 = slider.getInsets();
    left += left;
    right += right;
    top += top;
    bottom += bottom;
    if (!localInsets1.equals(insetCache))
    {
      insetCache = localInsets1;
      calculateGeometry();
    }
    localSynthContext.dispose();
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
    SynthStyle localSynthStyle = null;
    if (paramRegion == Region.SLIDER_TRACK) {
      localSynthStyle = sliderTrackStyle;
    } else if (paramRegion == Region.SLIDER_THUMB) {
      localSynthStyle = sliderThumbStyle;
    }
    return SynthContext.getContext(paramJComponent, paramRegion, localSynthStyle, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion)
  {
    if ((paramRegion == Region.SLIDER_THUMB) && (thumbActive) && (paramJComponent.isEnabled()))
    {
      int i = thumbPressed ? 4 : 2;
      if (paramJComponent.isFocusOwner()) {
        i |= 0x100;
      }
      return i;
    }
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintSliderBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), slider.getOrientation());
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
    recalculateIfInsetsChanged();
    recalculateIfOrientationChanged();
    Rectangle localRectangle = paramGraphics.getClipBounds();
    if ((lastSize == null) || (!lastSize.equals(slider.getSize()))) {
      calculateGeometry();
    }
    Object localObject;
    if (paintValue)
    {
      localObject = SwingUtilities2.getFontMetrics(slider, paramGraphics);
      int i = paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, paramGraphics.getFont(), (FontMetrics)localObject, "" + slider.getValue());
      valueRect.x = (thumbRect.x + (thumbRect.width - i) / 2);
      if (slider.getOrientation() == 0)
      {
        if (valueRect.x + i > insetCache.left + contentRect.width) {
          valueRect.x = (insetCache.left + contentRect.width - i);
        }
        valueRect.x = Math.max(valueRect.x, 0);
      }
      paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, "" + slider.getValue(), valueRect.x, valueRect.y, -1);
    }
    if ((slider.getPaintTrack()) && (localRectangle.intersects(trackRect)))
    {
      localObject = getContext(slider, Region.SLIDER_TRACK);
      paintTrack((SynthContext)localObject, paramGraphics, trackRect);
      ((SynthContext)localObject).dispose();
    }
    if (localRectangle.intersects(thumbRect))
    {
      localObject = getContext(slider, Region.SLIDER_THUMB);
      paintThumb((SynthContext)localObject, paramGraphics, thumbRect);
      ((SynthContext)localObject).dispose();
    }
    if ((slider.getPaintTicks()) && (localRectangle.intersects(tickRect))) {
      paintTicks(paramGraphics);
    }
    if ((slider.getPaintLabels()) && (localRectangle.intersects(labelRect))) {
      paintLabels(paramGraphics);
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintSliderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, slider.getOrientation());
  }
  
  protected void paintThumb(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
  {
    int i = slider.getOrientation();
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintSliderThumbBackground(paramSynthContext, paramGraphics, x, y, width, height, i);
    paramSynthContext.getPainter().paintSliderThumbBorder(paramSynthContext, paramGraphics, x, y, width, height, i);
  }
  
  protected void paintTrack(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
  {
    int i = slider.getOrientation();
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintSliderTrackBackground(paramSynthContext, paramGraphics, x, y, width, height, i);
    paramSynthContext.getPainter().paintSliderTrackBorder(paramSynthContext, paramGraphics, x, y, width, height, i);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JSlider)paramPropertyChangeEvent.getSource());
    }
  }
  
  private class SynthTrackListener
    extends BasicSliderUI.TrackListener
  {
    private SynthTrackListener()
    {
      super();
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      SynthSliderUI.this.setThumbActive(false);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      super.mousePressed(paramMouseEvent);
      SynthSliderUI.this.setThumbPressed(thumbRect.contains(paramMouseEvent.getX(), paramMouseEvent.getY()));
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      super.mouseReleased(paramMouseEvent);
      SynthSliderUI.this.updateThumbState(paramMouseEvent.getX(), paramMouseEvent.getY(), false);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (!slider.isEnabled()) {
        return;
      }
      currentMouseX = paramMouseEvent.getX();
      currentMouseY = paramMouseEvent.getY();
      if (!isDragging()) {
        return;
      }
      slider.setValueIsAdjusting(true);
      int i;
      switch (slider.getOrientation())
      {
      case 1: 
        int j = thumbRect.height / 2;
        int k = paramMouseEvent.getY() - offset;
        int m = trackRect.y;
        int n = trackRect.y + trackRect.height - j - trackBorder;
        int i1 = yPositionForValue(slider.getMaximum() - slider.getExtent());
        if (drawInverted())
        {
          n = i1;
          m += j;
        }
        else
        {
          m = i1;
        }
        k = Math.max(k, m - j);
        k = Math.min(k, n - j);
        setThumbLocation(thumbRect.x, k);
        i = k + j;
        slider.setValue(valueForYPosition(i));
        break;
      case 0: 
        int i2 = thumbRect.width / 2;
        int i3 = paramMouseEvent.getX() - offset;
        int i4 = trackRect.x + i2 + trackBorder;
        int i5 = trackRect.x + trackRect.width - i2 - trackBorder;
        int i6 = xPositionForValue(slider.getMaximum() - slider.getExtent());
        if (drawInverted()) {
          i4 = i6;
        } else {
          i5 = i6;
        }
        i3 = Math.max(i3, i4 - i2);
        i3 = Math.min(i3, i5 - i2);
        setThumbLocation(i3, thumbRect.y);
        i = i3 + i2;
        slider.setValue(valueForXPosition(i));
        break;
      default: 
        return;
      }
      if (slider.getValueIsAdjusting()) {
        SynthSliderUI.this.setThumbActive(true);
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      SynthSliderUI.this.updateThumbState(paramMouseEvent.getX(), paramMouseEvent.getY());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthSliderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */