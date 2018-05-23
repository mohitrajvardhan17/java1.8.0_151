package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.SliderUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicSliderUI
  extends SliderUI
{
  private static final Actions SHARED_ACTION = new Actions();
  public static final int POSITIVE_SCROLL = 1;
  public static final int NEGATIVE_SCROLL = -1;
  public static final int MIN_SCROLL = -2;
  public static final int MAX_SCROLL = 2;
  protected Timer scrollTimer;
  protected JSlider slider;
  protected Insets focusInsets = null;
  protected Insets insetCache = null;
  protected boolean leftToRightCache = true;
  protected Rectangle focusRect = null;
  protected Rectangle contentRect = null;
  protected Rectangle labelRect = null;
  protected Rectangle tickRect = null;
  protected Rectangle trackRect = null;
  protected Rectangle thumbRect = null;
  protected int trackBuffer = 0;
  private transient boolean isDragging;
  protected TrackListener trackListener;
  protected ChangeListener changeListener;
  protected ComponentListener componentListener;
  protected FocusListener focusListener;
  protected ScrollListener scrollListener;
  protected PropertyChangeListener propertyChangeListener;
  private Handler handler;
  private int lastValue;
  private Color shadowColor;
  private Color highlightColor;
  private Color focusColor;
  private boolean checkedLabelBaselines;
  private boolean sameLabelBaselines;
  private static Rectangle unionRect = new Rectangle();
  
  protected Color getShadowColor()
  {
    return shadowColor;
  }
  
  protected Color getHighlightColor()
  {
    return highlightColor;
  }
  
  protected Color getFocusColor()
  {
    return focusColor;
  }
  
  protected boolean isDragging()
  {
    return isDragging;
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicSliderUI((JSlider)paramJComponent);
  }
  
  public BasicSliderUI(JSlider paramJSlider) {}
  
  public void installUI(JComponent paramJComponent)
  {
    slider = ((JSlider)paramJComponent);
    checkedLabelBaselines = false;
    slider.setEnabled(slider.isEnabled());
    LookAndFeel.installProperty(slider, "opaque", Boolean.TRUE);
    isDragging = false;
    trackListener = createTrackListener(slider);
    changeListener = createChangeListener(slider);
    componentListener = createComponentListener(slider);
    focusListener = createFocusListener(slider);
    scrollListener = createScrollListener(slider);
    propertyChangeListener = createPropertyChangeListener(slider);
    installDefaults(slider);
    installListeners(slider);
    installKeyboardActions(slider);
    scrollTimer = new Timer(100, scrollListener);
    scrollTimer.setInitialDelay(300);
    insetCache = slider.getInsets();
    leftToRightCache = BasicGraphicsUtils.isLeftToRight(slider);
    focusRect = new Rectangle();
    contentRect = new Rectangle();
    labelRect = new Rectangle();
    tickRect = new Rectangle();
    trackRect = new Rectangle();
    thumbRect = new Rectangle();
    lastValue = slider.getValue();
    calculateGeometry();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    if (paramJComponent != slider) {
      throw new IllegalComponentStateException(this + " was asked to deinstall() " + paramJComponent + " when it only knows about " + slider + ".");
    }
    scrollTimer.stop();
    scrollTimer = null;
    uninstallDefaults(slider);
    uninstallListeners(slider);
    uninstallKeyboardActions(slider);
    insetCache = null;
    leftToRightCache = true;
    focusRect = null;
    contentRect = null;
    labelRect = null;
    tickRect = null;
    trackRect = null;
    thumbRect = null;
    trackListener = null;
    changeListener = null;
    componentListener = null;
    focusListener = null;
    scrollListener = null;
    propertyChangeListener = null;
    slider = null;
  }
  
  protected void installDefaults(JSlider paramJSlider)
  {
    LookAndFeel.installBorder(paramJSlider, "Slider.border");
    LookAndFeel.installColorsAndFont(paramJSlider, "Slider.background", "Slider.foreground", "Slider.font");
    highlightColor = UIManager.getColor("Slider.highlight");
    shadowColor = UIManager.getColor("Slider.shadow");
    focusColor = UIManager.getColor("Slider.focus");
    focusInsets = ((Insets)UIManager.get("Slider.focusInsets"));
    if (focusInsets == null) {
      focusInsets = new InsetsUIResource(2, 2, 2, 2);
    }
  }
  
  protected void uninstallDefaults(JSlider paramJSlider)
  {
    LookAndFeel.uninstallBorder(paramJSlider);
    focusInsets = null;
  }
  
  protected TrackListener createTrackListener(JSlider paramJSlider)
  {
    return new TrackListener();
  }
  
  protected ChangeListener createChangeListener(JSlider paramJSlider)
  {
    return getHandler();
  }
  
  protected ComponentListener createComponentListener(JSlider paramJSlider)
  {
    return getHandler();
  }
  
  protected FocusListener createFocusListener(JSlider paramJSlider)
  {
    return getHandler();
  }
  
  protected ScrollListener createScrollListener(JSlider paramJSlider)
  {
    return new ScrollListener();
  }
  
  protected PropertyChangeListener createPropertyChangeListener(JSlider paramJSlider)
  {
    return getHandler();
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected void installListeners(JSlider paramJSlider)
  {
    paramJSlider.addMouseListener(trackListener);
    paramJSlider.addMouseMotionListener(trackListener);
    paramJSlider.addFocusListener(focusListener);
    paramJSlider.addComponentListener(componentListener);
    paramJSlider.addPropertyChangeListener(propertyChangeListener);
    paramJSlider.getModel().addChangeListener(changeListener);
  }
  
  protected void uninstallListeners(JSlider paramJSlider)
  {
    paramJSlider.removeMouseListener(trackListener);
    paramJSlider.removeMouseMotionListener(trackListener);
    paramJSlider.removeFocusListener(focusListener);
    paramJSlider.removeComponentListener(componentListener);
    paramJSlider.removePropertyChangeListener(propertyChangeListener);
    paramJSlider.getModel().removeChangeListener(changeListener);
    handler = null;
  }
  
  protected void installKeyboardActions(JSlider paramJSlider)
  {
    InputMap localInputMap = getInputMap(0, paramJSlider);
    SwingUtilities.replaceUIInputMap(paramJSlider, 0, localInputMap);
    LazyActionMap.installLazyActionMap(paramJSlider, BasicSliderUI.class, "Slider.actionMap");
  }
  
  InputMap getInputMap(int paramInt, JSlider paramJSlider)
  {
    if (paramInt == 0)
    {
      InputMap localInputMap1 = (InputMap)DefaultLookup.get(paramJSlider, this, "Slider.focusInputMap");
      InputMap localInputMap2;
      if ((paramJSlider.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(paramJSlider, this, "Slider.focusInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("positiveUnitIncrement"));
    paramLazyActionMap.put(new Actions("positiveBlockIncrement"));
    paramLazyActionMap.put(new Actions("negativeUnitIncrement"));
    paramLazyActionMap.put(new Actions("negativeBlockIncrement"));
    paramLazyActionMap.put(new Actions("minScroll"));
    paramLazyActionMap.put(new Actions("maxScroll"));
  }
  
  protected void uninstallKeyboardActions(JSlider paramJSlider)
  {
    SwingUtilities.replaceUIActionMap(paramJSlider, null);
    SwingUtilities.replaceUIInputMap(paramJSlider, 0, null);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if ((slider.getPaintLabels()) && (labelsHaveSameBaselines()))
    {
      FontMetrics localFontMetrics = slider.getFontMetrics(slider.getFont());
      Insets localInsets = slider.getInsets();
      Dimension localDimension = getThumbSize();
      int k;
      int m;
      int n;
      int i1;
      int i2;
      int i3;
      if (slider.getOrientation() == 0)
      {
        int i = getTickLength();
        int j = paramInt2 - top - bottom - focusInsets.top - focusInsets.bottom;
        k = height;
        m = k;
        if (slider.getPaintTicks()) {
          m += i;
        }
        m += getHeightOfTallestLabel();
        n = top + focusInsets.top + (j - m - 1) / 2;
        i1 = k;
        i2 = n + i1;
        i3 = i;
        if (!slider.getPaintTicks()) {
          i3 = 0;
        }
        int i4 = i2 + i3;
        return i4 + localFontMetrics.getAscent();
      }
      boolean bool = slider.getInverted();
      Integer localInteger = bool ? getLowestValue() : getHighestValue();
      if (localInteger != null)
      {
        k = height;
        m = Math.max(localFontMetrics.getHeight() / 2, k / 2);
        n = focusInsets.top + top;
        i1 = n + m;
        i2 = paramInt2 - focusInsets.top - focusInsets.bottom - top - bottom - m - m;
        i3 = yPositionForValue(localInteger.intValue(), i1, i2);
        return i3 - localFontMetrics.getHeight() / 2 + localFontMetrics.getAscent();
      }
    }
    return 0;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  protected boolean labelsHaveSameBaselines()
  {
    if (!checkedLabelBaselines)
    {
      checkedLabelBaselines = true;
      Dictionary localDictionary = slider.getLabelTable();
      if (localDictionary != null)
      {
        sameLabelBaselines = true;
        Enumeration localEnumeration = localDictionary.elements();
        int i = -1;
        while (localEnumeration.hasMoreElements())
        {
          JComponent localJComponent = (JComponent)localEnumeration.nextElement();
          Dimension localDimension = localJComponent.getPreferredSize();
          int j = localJComponent.getBaseline(width, height);
          if (j >= 0)
          {
            if (i == -1)
            {
              i = j;
            }
            else if (i != j)
            {
              sameLabelBaselines = false;
              break;
            }
          }
          else
          {
            sameLabelBaselines = false;
            break;
          }
        }
      }
      else
      {
        sameLabelBaselines = false;
      }
    }
    return sameLabelBaselines;
  }
  
  public Dimension getPreferredHorizontalSize()
  {
    Dimension localDimension = (Dimension)DefaultLookup.get(slider, this, "Slider.horizontalSize");
    if (localDimension == null) {
      localDimension = new Dimension(200, 21);
    }
    return localDimension;
  }
  
  public Dimension getPreferredVerticalSize()
  {
    Dimension localDimension = (Dimension)DefaultLookup.get(slider, this, "Slider.verticalSize");
    if (localDimension == null) {
      localDimension = new Dimension(21, 200);
    }
    return localDimension;
  }
  
  public Dimension getMinimumHorizontalSize()
  {
    Dimension localDimension = (Dimension)DefaultLookup.get(slider, this, "Slider.minimumHorizontalSize");
    if (localDimension == null) {
      localDimension = new Dimension(36, 21);
    }
    return localDimension;
  }
  
  public Dimension getMinimumVerticalSize()
  {
    Dimension localDimension = (Dimension)DefaultLookup.get(slider, this, "Slider.minimumVerticalSize");
    if (localDimension == null) {
      localDimension = new Dimension(21, 36);
    }
    return localDimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    recalculateIfInsetsChanged();
    Dimension localDimension;
    if (slider.getOrientation() == 1)
    {
      localDimension = new Dimension(getPreferredVerticalSize());
      width = (insetCache.left + insetCache.right);
      width += focusInsets.left + focusInsets.right;
      width += trackRect.width + tickRect.width + labelRect.width;
    }
    else
    {
      localDimension = new Dimension(getPreferredHorizontalSize());
      height = (insetCache.top + insetCache.bottom);
      height += focusInsets.top + focusInsets.bottom;
      height += trackRect.height + tickRect.height + labelRect.height;
    }
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    recalculateIfInsetsChanged();
    Dimension localDimension;
    if (slider.getOrientation() == 1)
    {
      localDimension = new Dimension(getMinimumVerticalSize());
      width = (insetCache.left + insetCache.right);
      width += focusInsets.left + focusInsets.right;
      width += trackRect.width + tickRect.width + labelRect.width;
    }
    else
    {
      localDimension = new Dimension(getMinimumHorizontalSize());
      height = (insetCache.top + insetCache.bottom);
      height += focusInsets.top + focusInsets.bottom;
      height += trackRect.height + tickRect.height + labelRect.height;
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(paramJComponent);
    if (slider.getOrientation() == 1) {
      height = 32767;
    } else {
      width = 32767;
    }
    return localDimension;
  }
  
  protected void calculateGeometry()
  {
    calculateFocusRect();
    calculateContentRect();
    calculateThumbSize();
    calculateTrackBuffer();
    calculateTrackRect();
    calculateTickRect();
    calculateLabelRect();
    calculateThumbLocation();
  }
  
  protected void calculateFocusRect()
  {
    focusRect.x = insetCache.left;
    focusRect.y = insetCache.top;
    focusRect.width = (slider.getWidth() - (insetCache.left + insetCache.right));
    focusRect.height = (slider.getHeight() - (insetCache.top + insetCache.bottom));
  }
  
  protected void calculateThumbSize()
  {
    Dimension localDimension = getThumbSize();
    thumbRect.setSize(width, height);
  }
  
  protected void calculateContentRect()
  {
    contentRect.x = (focusRect.x + focusInsets.left);
    contentRect.y = (focusRect.y + focusInsets.top);
    contentRect.width = (focusRect.width - (focusInsets.left + focusInsets.right));
    contentRect.height = (focusRect.height - (focusInsets.top + focusInsets.bottom));
  }
  
  private int getTickSpacing()
  {
    int i = slider.getMajorTickSpacing();
    int j = slider.getMinorTickSpacing();
    int k;
    if (j > 0) {
      k = j;
    } else if (i > 0) {
      k = i;
    } else {
      k = 0;
    }
    return k;
  }
  
  protected void calculateThumbLocation()
  {
    int i;
    if (slider.getSnapToTicks())
    {
      i = slider.getValue();
      int j = i;
      int k = getTickSpacing();
      if (k != 0)
      {
        if ((i - slider.getMinimum()) % k != 0)
        {
          float f = (i - slider.getMinimum()) / k;
          int m = Math.round(f);
          if ((f - (int)f == 0.5D) && (i < lastValue)) {
            m--;
          }
          j = slider.getMinimum() + m * k;
        }
        if (j != i) {
          slider.setValue(j);
        }
      }
    }
    if (slider.getOrientation() == 0)
    {
      i = xPositionForValue(slider.getValue());
      thumbRect.x = (i - thumbRect.width / 2);
      thumbRect.y = trackRect.y;
    }
    else
    {
      i = yPositionForValue(slider.getValue());
      thumbRect.x = trackRect.x;
      thumbRect.y = (i - thumbRect.height / 2);
    }
  }
  
  protected void calculateTrackBuffer()
  {
    if ((slider.getPaintLabels()) && (slider.getLabelTable() != null))
    {
      Component localComponent1 = getHighestValueLabel();
      Component localComponent2 = getLowestValueLabel();
      if (slider.getOrientation() == 0)
      {
        trackBuffer = (Math.max(getBoundswidth, getBoundswidth) / 2);
        trackBuffer = Math.max(trackBuffer, thumbRect.width / 2);
      }
      else
      {
        trackBuffer = (Math.max(getBoundsheight, getBoundsheight) / 2);
        trackBuffer = Math.max(trackBuffer, thumbRect.height / 2);
      }
    }
    else if (slider.getOrientation() == 0)
    {
      trackBuffer = (thumbRect.width / 2);
    }
    else
    {
      trackBuffer = (thumbRect.height / 2);
    }
  }
  
  protected void calculateTrackRect()
  {
    int i;
    if (slider.getOrientation() == 0)
    {
      i = thumbRect.height;
      if (slider.getPaintTicks()) {
        i += getTickLength();
      }
      if (slider.getPaintLabels()) {
        i += getHeightOfTallestLabel();
      }
      trackRect.x = (contentRect.x + trackBuffer);
      trackRect.y = (contentRect.y + (contentRect.height - i - 1) / 2);
      trackRect.width = (contentRect.width - trackBuffer * 2);
      trackRect.height = thumbRect.height;
    }
    else
    {
      i = thumbRect.width;
      if (BasicGraphicsUtils.isLeftToRight(slider))
      {
        if (slider.getPaintTicks()) {
          i += getTickLength();
        }
        if (slider.getPaintLabels()) {
          i += getWidthOfWidestLabel();
        }
      }
      else
      {
        if (slider.getPaintTicks()) {
          i -= getTickLength();
        }
        if (slider.getPaintLabels()) {
          i -= getWidthOfWidestLabel();
        }
      }
      trackRect.x = (contentRect.x + (contentRect.width - i - 1) / 2);
      trackRect.y = (contentRect.y + trackBuffer);
      trackRect.width = thumbRect.width;
      trackRect.height = (contentRect.height - trackBuffer * 2);
    }
  }
  
  protected int getTickLength()
  {
    return 8;
  }
  
  protected void calculateTickRect()
  {
    if (slider.getOrientation() == 0)
    {
      tickRect.x = trackRect.x;
      tickRect.y = (trackRect.y + trackRect.height);
      tickRect.width = trackRect.width;
      tickRect.height = (slider.getPaintTicks() ? getTickLength() : 0);
    }
    else
    {
      tickRect.width = (slider.getPaintTicks() ? getTickLength() : 0);
      if (BasicGraphicsUtils.isLeftToRight(slider)) {
        tickRect.x = (trackRect.x + trackRect.width);
      } else {
        tickRect.x = (trackRect.x - tickRect.width);
      }
      tickRect.y = trackRect.y;
      tickRect.height = trackRect.height;
    }
  }
  
  protected void calculateLabelRect()
  {
    if (slider.getPaintLabels())
    {
      if (slider.getOrientation() == 0)
      {
        labelRect.x = (tickRect.x - trackBuffer);
        labelRect.y = (tickRect.y + tickRect.height);
        labelRect.width = (tickRect.width + trackBuffer * 2);
        labelRect.height = getHeightOfTallestLabel();
      }
      else
      {
        if (BasicGraphicsUtils.isLeftToRight(slider))
        {
          labelRect.x = (tickRect.x + tickRect.width);
          labelRect.width = getWidthOfWidestLabel();
        }
        else
        {
          labelRect.width = getWidthOfWidestLabel();
          labelRect.x = (tickRect.x - labelRect.width);
        }
        labelRect.y = (tickRect.y - trackBuffer);
        labelRect.height = (tickRect.height + trackBuffer * 2);
      }
    }
    else if (slider.getOrientation() == 0)
    {
      labelRect.x = tickRect.x;
      labelRect.y = (tickRect.y + tickRect.height);
      labelRect.width = tickRect.width;
      labelRect.height = 0;
    }
    else
    {
      if (BasicGraphicsUtils.isLeftToRight(slider)) {
        labelRect.x = (tickRect.x + tickRect.width);
      } else {
        labelRect.x = tickRect.x;
      }
      labelRect.y = tickRect.y;
      labelRect.width = 0;
      labelRect.height = tickRect.height;
    }
  }
  
  protected Dimension getThumbSize()
  {
    Dimension localDimension = new Dimension();
    if (slider.getOrientation() == 1)
    {
      width = 20;
      height = 11;
    }
    else
    {
      width = 11;
      height = 20;
    }
    return localDimension;
  }
  
  protected int getWidthOfWidestLabel()
  {
    Dictionary localDictionary = slider.getLabelTable();
    int i = 0;
    if (localDictionary != null)
    {
      Enumeration localEnumeration = localDictionary.keys();
      while (localEnumeration.hasMoreElements())
      {
        JComponent localJComponent = (JComponent)localDictionary.get(localEnumeration.nextElement());
        i = Math.max(getPreferredSizewidth, i);
      }
    }
    return i;
  }
  
  protected int getHeightOfTallestLabel()
  {
    Dictionary localDictionary = slider.getLabelTable();
    int i = 0;
    if (localDictionary != null)
    {
      Enumeration localEnumeration = localDictionary.keys();
      while (localEnumeration.hasMoreElements())
      {
        JComponent localJComponent = (JComponent)localDictionary.get(localEnumeration.nextElement());
        i = Math.max(getPreferredSizeheight, i);
      }
    }
    return i;
  }
  
  protected int getWidthOfHighValueLabel()
  {
    Component localComponent = getHighestValueLabel();
    int i = 0;
    if (localComponent != null) {
      i = getPreferredSizewidth;
    }
    return i;
  }
  
  protected int getWidthOfLowValueLabel()
  {
    Component localComponent = getLowestValueLabel();
    int i = 0;
    if (localComponent != null) {
      i = getPreferredSizewidth;
    }
    return i;
  }
  
  protected int getHeightOfHighValueLabel()
  {
    Component localComponent = getHighestValueLabel();
    int i = 0;
    if (localComponent != null) {
      i = getPreferredSizeheight;
    }
    return i;
  }
  
  protected int getHeightOfLowValueLabel()
  {
    Component localComponent = getLowestValueLabel();
    int i = 0;
    if (localComponent != null) {
      i = getPreferredSizeheight;
    }
    return i;
  }
  
  protected boolean drawInverted()
  {
    if (slider.getOrientation() == 0)
    {
      if (BasicGraphicsUtils.isLeftToRight(slider)) {
        return slider.getInverted();
      }
      return !slider.getInverted();
    }
    return slider.getInverted();
  }
  
  protected Integer getHighestValue()
  {
    Dictionary localDictionary = slider.getLabelTable();
    if (localDictionary == null) {
      return null;
    }
    Enumeration localEnumeration = localDictionary.keys();
    Integer localInteger;
    for (Object localObject = null; localEnumeration.hasMoreElements(); localObject = localInteger)
    {
      localInteger = (Integer)localEnumeration.nextElement();
      if ((localObject != null) && (localInteger.intValue() <= ((Integer)localObject).intValue())) {}
    }
    return (Integer)localObject;
  }
  
  protected Integer getLowestValue()
  {
    Dictionary localDictionary = slider.getLabelTable();
    if (localDictionary == null) {
      return null;
    }
    Enumeration localEnumeration = localDictionary.keys();
    Integer localInteger;
    for (Object localObject = null; localEnumeration.hasMoreElements(); localObject = localInteger)
    {
      localInteger = (Integer)localEnumeration.nextElement();
      if ((localObject != null) && (localInteger.intValue() >= ((Integer)localObject).intValue())) {}
    }
    return (Integer)localObject;
  }
  
  protected Component getLowestValueLabel()
  {
    Integer localInteger = getLowestValue();
    if (localInteger != null) {
      return (Component)slider.getLabelTable().get(localInteger);
    }
    return null;
  }
  
  protected Component getHighestValueLabel()
  {
    Integer localInteger = getHighestValue();
    if (localInteger != null) {
      return (Component)slider.getLabelTable().get(localInteger);
    }
    return null;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    recalculateIfInsetsChanged();
    recalculateIfOrientationChanged();
    Rectangle localRectangle = paramGraphics.getClipBounds();
    if ((!localRectangle.intersects(trackRect)) && (slider.getPaintTrack())) {
      calculateGeometry();
    }
    if ((slider.getPaintTrack()) && (localRectangle.intersects(trackRect))) {
      paintTrack(paramGraphics);
    }
    if ((slider.getPaintTicks()) && (localRectangle.intersects(tickRect))) {
      paintTicks(paramGraphics);
    }
    if ((slider.getPaintLabels()) && (localRectangle.intersects(labelRect))) {
      paintLabels(paramGraphics);
    }
    if ((slider.hasFocus()) && (localRectangle.intersects(focusRect))) {
      paintFocus(paramGraphics);
    }
    if (localRectangle.intersects(thumbRect)) {
      paintThumb(paramGraphics);
    }
  }
  
  protected void recalculateIfInsetsChanged()
  {
    Insets localInsets = slider.getInsets();
    if (!localInsets.equals(insetCache))
    {
      insetCache = localInsets;
      calculateGeometry();
    }
  }
  
  protected void recalculateIfOrientationChanged()
  {
    boolean bool = BasicGraphicsUtils.isLeftToRight(slider);
    if (bool != leftToRightCache)
    {
      leftToRightCache = bool;
      calculateGeometry();
    }
  }
  
  public void paintFocus(Graphics paramGraphics)
  {
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, focusRect.x, focusRect.y, focusRect.width, focusRect.height);
  }
  
  public void paintTrack(Graphics paramGraphics)
  {
    Rectangle localRectangle = trackRect;
    int i;
    int j;
    if (slider.getOrientation() == 0)
    {
      i = height / 2 - 2;
      j = width;
      paramGraphics.translate(x, y + i);
      paramGraphics.setColor(getShadowColor());
      paramGraphics.drawLine(0, 0, j - 1, 0);
      paramGraphics.drawLine(0, 1, 0, 2);
      paramGraphics.setColor(getHighlightColor());
      paramGraphics.drawLine(0, 3, j, 3);
      paramGraphics.drawLine(j, 0, j, 3);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(1, 1, j - 2, 1);
      paramGraphics.translate(-x, -(y + i));
    }
    else
    {
      i = width / 2 - 2;
      j = height;
      paramGraphics.translate(x + i, y);
      paramGraphics.setColor(getShadowColor());
      paramGraphics.drawLine(0, 0, 0, j - 1);
      paramGraphics.drawLine(1, 0, 2, 0);
      paramGraphics.setColor(getHighlightColor());
      paramGraphics.drawLine(3, 0, 3, j);
      paramGraphics.drawLine(0, j, 3, j);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(1, 1, 1, j - 2);
      paramGraphics.translate(-(x + i), -y);
    }
  }
  
  public void paintTicks(Graphics paramGraphics)
  {
    Rectangle localRectangle = tickRect;
    paramGraphics.setColor(DefaultLookup.getColor(slider, this, "Slider.tickColor", Color.black));
    int i;
    int j;
    if (slider.getOrientation() == 0)
    {
      paramGraphics.translate(0, y);
      if (slider.getMinorTickSpacing() > 0)
      {
        i = slider.getMinimum();
        while (i <= slider.getMaximum())
        {
          j = xPositionForValue(i);
          paintMinorTickForHorizSlider(paramGraphics, localRectangle, j);
          if (Integer.MAX_VALUE - slider.getMinorTickSpacing() < i) {
            break;
          }
          i += slider.getMinorTickSpacing();
        }
      }
      if (slider.getMajorTickSpacing() > 0)
      {
        i = slider.getMinimum();
        while (i <= slider.getMaximum())
        {
          j = xPositionForValue(i);
          paintMajorTickForHorizSlider(paramGraphics, localRectangle, j);
          if (Integer.MAX_VALUE - slider.getMajorTickSpacing() < i) {
            break;
          }
          i += slider.getMajorTickSpacing();
        }
      }
      paramGraphics.translate(0, -y);
    }
    else
    {
      paramGraphics.translate(x, 0);
      if (slider.getMinorTickSpacing() > 0)
      {
        i = 0;
        if (!BasicGraphicsUtils.isLeftToRight(slider))
        {
          i = width - width / 2;
          paramGraphics.translate(i, 0);
        }
        j = slider.getMinimum();
        while (j <= slider.getMaximum())
        {
          int k = yPositionForValue(j);
          paintMinorTickForVertSlider(paramGraphics, localRectangle, k);
          if (Integer.MAX_VALUE - slider.getMinorTickSpacing() < j) {
            break;
          }
          j += slider.getMinorTickSpacing();
        }
        if (!BasicGraphicsUtils.isLeftToRight(slider)) {
          paramGraphics.translate(-i, 0);
        }
      }
      if (slider.getMajorTickSpacing() > 0)
      {
        if (!BasicGraphicsUtils.isLeftToRight(slider)) {
          paramGraphics.translate(2, 0);
        }
        i = slider.getMinimum();
        while (i <= slider.getMaximum())
        {
          j = yPositionForValue(i);
          paintMajorTickForVertSlider(paramGraphics, localRectangle, j);
          if (Integer.MAX_VALUE - slider.getMajorTickSpacing() < i) {
            break;
          }
          i += slider.getMajorTickSpacing();
        }
        if (!BasicGraphicsUtils.isLeftToRight(slider)) {
          paramGraphics.translate(-2, 0);
        }
      }
      paramGraphics.translate(-x, 0);
    }
  }
  
  protected void paintMinorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.drawLine(paramInt, 0, paramInt, height / 2 - 1);
  }
  
  protected void paintMajorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.drawLine(paramInt, 0, paramInt, height - 2);
  }
  
  protected void paintMinorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.drawLine(0, paramInt, width / 2 - 1, paramInt);
  }
  
  protected void paintMajorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    paramGraphics.drawLine(0, paramInt, width - 2, paramInt);
  }
  
  public void paintLabels(Graphics paramGraphics)
  {
    Rectangle localRectangle = labelRect;
    Dictionary localDictionary = slider.getLabelTable();
    if (localDictionary != null)
    {
      Enumeration localEnumeration = localDictionary.keys();
      int i = slider.getMinimum();
      int j = slider.getMaximum();
      boolean bool = slider.isEnabled();
      while (localEnumeration.hasMoreElements())
      {
        Integer localInteger = (Integer)localEnumeration.nextElement();
        int k = localInteger.intValue();
        if ((k >= i) && (k <= j))
        {
          JComponent localJComponent = (JComponent)localDictionary.get(localInteger);
          localJComponent.setEnabled(bool);
          if ((localJComponent instanceof JLabel))
          {
            Icon localIcon = localJComponent.isEnabled() ? ((JLabel)localJComponent).getIcon() : ((JLabel)localJComponent).getDisabledIcon();
            if ((localIcon instanceof ImageIcon)) {
              Toolkit.getDefaultToolkit().checkImage(((ImageIcon)localIcon).getImage(), -1, -1, slider);
            }
          }
          if (slider.getOrientation() == 0)
          {
            paramGraphics.translate(0, y);
            paintHorizontalLabel(paramGraphics, k, localJComponent);
            paramGraphics.translate(0, -y);
          }
          else
          {
            int m = 0;
            if (!BasicGraphicsUtils.isLeftToRight(slider)) {
              m = width - getPreferredSizewidth;
            }
            paramGraphics.translate(x + m, 0);
            paintVerticalLabel(paramGraphics, k, localJComponent);
            paramGraphics.translate(-x - m, 0);
          }
        }
      }
    }
  }
  
  protected void paintHorizontalLabel(Graphics paramGraphics, int paramInt, Component paramComponent)
  {
    int i = xPositionForValue(paramInt);
    int j = i - getPreferredSizewidth / 2;
    paramGraphics.translate(j, 0);
    paramComponent.paint(paramGraphics);
    paramGraphics.translate(-j, 0);
  }
  
  protected void paintVerticalLabel(Graphics paramGraphics, int paramInt, Component paramComponent)
  {
    int i = yPositionForValue(paramInt);
    int j = i - getPreferredSizeheight / 2;
    paramGraphics.translate(0, j);
    paramComponent.paint(paramGraphics);
    paramGraphics.translate(0, -j);
  }
  
  public void paintThumb(Graphics paramGraphics)
  {
    Rectangle localRectangle = thumbRect;
    int i = width;
    int j = height;
    paramGraphics.translate(x, y);
    if (slider.isEnabled()) {
      paramGraphics.setColor(slider.getBackground());
    } else {
      paramGraphics.setColor(slider.getBackground().darker());
    }
    Boolean localBoolean = (Boolean)slider.getClientProperty("Slider.paintThumbArrowShape");
    if (((!slider.getPaintTicks()) && (localBoolean == null)) || (localBoolean == Boolean.FALSE))
    {
      paramGraphics.fillRect(0, 0, i, j);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(0, j - 1, i - 1, j - 1);
      paramGraphics.drawLine(i - 1, 0, i - 1, j - 1);
      paramGraphics.setColor(highlightColor);
      paramGraphics.drawLine(0, 0, 0, j - 2);
      paramGraphics.drawLine(1, 0, i - 2, 0);
      paramGraphics.setColor(shadowColor);
      paramGraphics.drawLine(1, j - 2, i - 2, j - 2);
      paramGraphics.drawLine(i - 2, 1, i - 2, j - 3);
    }
    else
    {
      int k;
      Polygon localPolygon;
      if (slider.getOrientation() == 0)
      {
        k = i / 2;
        paramGraphics.fillRect(1, 1, i - 3, j - 1 - k);
        localPolygon = new Polygon();
        localPolygon.addPoint(1, j - k);
        localPolygon.addPoint(k - 1, j - 1);
        localPolygon.addPoint(i - 2, j - 1 - k);
        paramGraphics.fillPolygon(localPolygon);
        paramGraphics.setColor(highlightColor);
        paramGraphics.drawLine(0, 0, i - 2, 0);
        paramGraphics.drawLine(0, 1, 0, j - 1 - k);
        paramGraphics.drawLine(0, j - k, k - 1, j - 1);
        paramGraphics.setColor(Color.black);
        paramGraphics.drawLine(i - 1, 0, i - 1, j - 2 - k);
        paramGraphics.drawLine(i - 1, j - 1 - k, i - 1 - k, j - 1);
        paramGraphics.setColor(shadowColor);
        paramGraphics.drawLine(i - 2, 1, i - 2, j - 2 - k);
        paramGraphics.drawLine(i - 2, j - 1 - k, i - 1 - k, j - 2);
      }
      else
      {
        k = j / 2;
        if (BasicGraphicsUtils.isLeftToRight(slider))
        {
          paramGraphics.fillRect(1, 1, i - 1 - k, j - 3);
          localPolygon = new Polygon();
          localPolygon.addPoint(i - k - 1, 0);
          localPolygon.addPoint(i - 1, k);
          localPolygon.addPoint(i - 1 - k, j - 2);
          paramGraphics.fillPolygon(localPolygon);
          paramGraphics.setColor(highlightColor);
          paramGraphics.drawLine(0, 0, 0, j - 2);
          paramGraphics.drawLine(1, 0, i - 1 - k, 0);
          paramGraphics.drawLine(i - k - 1, 0, i - 1, k);
          paramGraphics.setColor(Color.black);
          paramGraphics.drawLine(0, j - 1, i - 2 - k, j - 1);
          paramGraphics.drawLine(i - 1 - k, j - 1, i - 1, j - 1 - k);
          paramGraphics.setColor(shadowColor);
          paramGraphics.drawLine(1, j - 2, i - 2 - k, j - 2);
          paramGraphics.drawLine(i - 1 - k, j - 2, i - 2, j - k - 1);
        }
        else
        {
          paramGraphics.fillRect(5, 1, i - 1 - k, j - 3);
          localPolygon = new Polygon();
          localPolygon.addPoint(k, 0);
          localPolygon.addPoint(0, k);
          localPolygon.addPoint(k, j - 2);
          paramGraphics.fillPolygon(localPolygon);
          paramGraphics.setColor(highlightColor);
          paramGraphics.drawLine(k - 1, 0, i - 2, 0);
          paramGraphics.drawLine(0, k, k, 0);
          paramGraphics.setColor(Color.black);
          paramGraphics.drawLine(0, j - 1 - k, k, j - 1);
          paramGraphics.drawLine(k, j - 1, i - 1, j - 1);
          paramGraphics.setColor(shadowColor);
          paramGraphics.drawLine(k, j - 2, i - 2, j - 2);
          paramGraphics.drawLine(i - 1, 1, i - 1, j - 2);
        }
      }
    }
    paramGraphics.translate(-x, -y);
  }
  
  public void setThumbLocation(int paramInt1, int paramInt2)
  {
    unionRect.setBounds(thumbRect);
    thumbRect.setLocation(paramInt1, paramInt2);
    SwingUtilities.computeUnion(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, unionRect);
    slider.repaint(unionRectx, unionRecty, unionRectwidth, unionRectheight);
  }
  
  public void scrollByBlock(int paramInt)
  {
    synchronized (slider)
    {
      int i = (slider.getMaximum() - slider.getMinimum()) / 10;
      if (i == 0) {
        i = 1;
      }
      if (slider.getSnapToTicks())
      {
        j = getTickSpacing();
        if (i < j) {
          i = j;
        }
      }
      int j = i * (paramInt > 0 ? 1 : -1);
      slider.setValue(slider.getValue() + j);
    }
  }
  
  public void scrollByUnit(int paramInt)
  {
    synchronized (slider)
    {
      int i = paramInt > 0 ? 1 : -1;
      if (slider.getSnapToTicks()) {
        i *= getTickSpacing();
      }
      slider.setValue(slider.getValue() + i);
    }
  }
  
  protected void scrollDueToClickInTrack(int paramInt)
  {
    scrollByBlock(paramInt);
  }
  
  protected int xPositionForValue(int paramInt)
  {
    int i = slider.getMinimum();
    int j = slider.getMaximum();
    int k = trackRect.width;
    double d1 = j - i;
    double d2 = k / d1;
    int m = trackRect.x;
    int n = trackRect.x + (trackRect.width - 1);
    if (!drawInverted())
    {
      i1 = m;
      i1 = (int)(i1 + Math.round(d2 * (paramInt - i)));
    }
    else
    {
      i1 = n;
      i1 = (int)(i1 - Math.round(d2 * (paramInt - i)));
    }
    int i1 = Math.max(m, i1);
    i1 = Math.min(n, i1);
    return i1;
  }
  
  protected int yPositionForValue(int paramInt)
  {
    return yPositionForValue(paramInt, trackRect.y, trackRect.height);
  }
  
  protected int yPositionForValue(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = slider.getMinimum();
    int j = slider.getMaximum();
    double d1 = j - i;
    double d2 = paramInt3 / d1;
    int k = paramInt2 + (paramInt3 - 1);
    if (!drawInverted())
    {
      m = paramInt2;
      m = (int)(m + Math.round(d2 * (j - paramInt1)));
    }
    else
    {
      m = paramInt2;
      m = (int)(m + Math.round(d2 * (paramInt1 - i)));
    }
    int m = Math.max(paramInt2, m);
    m = Math.min(k, m);
    return m;
  }
  
  public int valueForYPosition(int paramInt)
  {
    int j = slider.getMinimum();
    int k = slider.getMaximum();
    int m = trackRect.height;
    int n = trackRect.y;
    int i1 = trackRect.y + (trackRect.height - 1);
    int i;
    if (paramInt <= n)
    {
      i = drawInverted() ? j : k;
    }
    else if (paramInt >= i1)
    {
      i = drawInverted() ? k : j;
    }
    else
    {
      int i2 = paramInt - n;
      double d1 = k - j;
      double d2 = d1 / m;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? j + i3 : k - i3;
    }
    return i;
  }
  
  public int valueForXPosition(int paramInt)
  {
    int j = slider.getMinimum();
    int k = slider.getMaximum();
    int m = trackRect.width;
    int n = trackRect.x;
    int i1 = trackRect.x + (trackRect.width - 1);
    int i;
    if (paramInt <= n)
    {
      i = drawInverted() ? k : j;
    }
    else if (paramInt >= i1)
    {
      i = drawInverted() ? j : k;
    }
    else
    {
      int i2 = paramInt - n;
      double d1 = k - j;
      double d2 = d1 / m;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? k - i3 : j + i3;
    }
    return i;
  }
  
  public class ActionScroller
    extends AbstractAction
  {
    int dir;
    boolean block;
    JSlider slider;
    
    public ActionScroller(JSlider paramJSlider, int paramInt, boolean paramBoolean)
    {
      dir = paramInt;
      block = paramBoolean;
      slider = paramJSlider;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      BasicSliderUI.Actions.access$600(BasicSliderUI.SHARED_ACTION, slider, BasicSliderUI.this, dir, block);
    }
    
    public boolean isEnabled()
    {
      boolean bool = true;
      if (slider != null) {
        bool = slider.isEnabled();
      }
      return bool;
    }
  }
  
  private static class Actions
    extends UIAction
  {
    public static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
    public static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
    public static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
    public static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
    public static final String MIN_SCROLL_INCREMENT = "minScroll";
    public static final String MAX_SCROLL_INCREMENT = "maxScroll";
    
    Actions()
    {
      super();
    }
    
    public Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JSlider localJSlider = (JSlider)paramActionEvent.getSource();
      BasicSliderUI localBasicSliderUI = (BasicSliderUI)BasicLookAndFeel.getUIOfType(localJSlider.getUI(), BasicSliderUI.class);
      String str = getName();
      if (localBasicSliderUI == null) {
        return;
      }
      if ("positiveUnitIncrement" == str) {
        scroll(localJSlider, localBasicSliderUI, 1, false);
      } else if ("negativeUnitIncrement" == str) {
        scroll(localJSlider, localBasicSliderUI, -1, false);
      } else if ("positiveBlockIncrement" == str) {
        scroll(localJSlider, localBasicSliderUI, 1, true);
      } else if ("negativeBlockIncrement" == str) {
        scroll(localJSlider, localBasicSliderUI, -1, true);
      } else if ("minScroll" == str) {
        scroll(localJSlider, localBasicSliderUI, -2, false);
      } else if ("maxScroll" == str) {
        scroll(localJSlider, localBasicSliderUI, 2, false);
      }
    }
    
    private void scroll(JSlider paramJSlider, BasicSliderUI paramBasicSliderUI, int paramInt, boolean paramBoolean)
    {
      boolean bool = paramJSlider.getInverted();
      if ((paramInt == -1) || (paramInt == 1))
      {
        if (bool) {
          paramInt = paramInt == 1 ? -1 : 1;
        }
        if (paramBoolean) {
          paramBasicSliderUI.scrollByBlock(paramInt);
        } else {
          paramBasicSliderUI.scrollByUnit(paramInt);
        }
      }
      else
      {
        if (bool) {
          paramInt = paramInt == -2 ? 2 : -2;
        }
        paramJSlider.setValue(paramInt == -2 ? paramJSlider.getMinimum() : paramJSlider.getMaximum());
      }
    }
  }
  
  public class ChangeHandler
    implements ChangeListener
  {
    public ChangeHandler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicSliderUI.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
  
  public class ComponentHandler
    extends ComponentAdapter
  {
    public ComponentHandler() {}
    
    public void componentResized(ComponentEvent paramComponentEvent)
    {
      BasicSliderUI.this.getHandler().componentResized(paramComponentEvent);
    }
  }
  
  public class FocusHandler
    implements FocusListener
  {
    public FocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicSliderUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicSliderUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements ChangeListener, ComponentListener, FocusListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (!isDragging)
      {
        calculateThumbLocation();
        slider.repaint();
      }
      lastValue = slider.getValue();
    }
    
    public void componentHidden(ComponentEvent paramComponentEvent) {}
    
    public void componentMoved(ComponentEvent paramComponentEvent) {}
    
    public void componentResized(ComponentEvent paramComponentEvent)
    {
      calculateGeometry();
      slider.repaint();
    }
    
    public void componentShown(ComponentEvent paramComponentEvent) {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      slider.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      slider.repaint();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if ((str == "orientation") || (str == "inverted") || (str == "labelTable") || (str == "majorTickSpacing") || (str == "minorTickSpacing") || (str == "paintTicks") || (str == "paintTrack") || (str == "font") || (str == "paintLabels") || (str == "Slider.paintThumbArrowShape"))
      {
        checkedLabelBaselines = false;
        calculateGeometry();
        slider.repaint();
      }
      else if (str == "componentOrientation")
      {
        calculateGeometry();
        slider.repaint();
        InputMap localInputMap = getInputMap(0, slider);
        SwingUtilities.replaceUIInputMap(slider, 0, localInputMap);
      }
      else if (str == "model")
      {
        ((BoundedRangeModel)paramPropertyChangeEvent.getOldValue()).removeChangeListener(changeListener);
        ((BoundedRangeModel)paramPropertyChangeEvent.getNewValue()).addChangeListener(changeListener);
        calculateThumbLocation();
        slider.repaint();
      }
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicSliderUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  public class ScrollListener
    implements ActionListener
  {
    int direction = 1;
    boolean useBlockIncrement;
    
    public ScrollListener()
    {
      direction = 1;
      useBlockIncrement = true;
    }
    
    public ScrollListener(int paramInt, boolean paramBoolean)
    {
      direction = paramInt;
      useBlockIncrement = paramBoolean;
    }
    
    public void setDirection(int paramInt)
    {
      direction = paramInt;
    }
    
    public void setScrollByBlock(boolean paramBoolean)
    {
      useBlockIncrement = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (useBlockIncrement) {
        scrollByBlock(direction);
      } else {
        scrollByUnit(direction);
      }
      if (!trackListener.shouldScroll(direction)) {
        ((Timer)paramActionEvent.getSource()).stop();
      }
    }
  }
  
  static class SharedActionScroller
    extends AbstractAction
  {
    int dir;
    boolean block;
    
    public SharedActionScroller(int paramInt, boolean paramBoolean)
    {
      dir = paramInt;
      block = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JSlider localJSlider = (JSlider)paramActionEvent.getSource();
      BasicSliderUI localBasicSliderUI = (BasicSliderUI)BasicLookAndFeel.getUIOfType(localJSlider.getUI(), BasicSliderUI.class);
      if (localBasicSliderUI == null) {
        return;
      }
      BasicSliderUI.SHARED_ACTION.scroll(localJSlider, localBasicSliderUI, dir, block);
    }
  }
  
  public class TrackListener
    extends MouseInputAdapter
  {
    protected transient int offset;
    protected transient int currentMouseX;
    protected transient int currentMouseY;
    
    public TrackListener() {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (!slider.isEnabled()) {
        return;
      }
      offset = 0;
      scrollTimer.stop();
      isDragging = false;
      slider.setValueIsAdjusting(false);
      slider.repaint();
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!slider.isEnabled()) {
        return;
      }
      calculateGeometry();
      currentMouseX = paramMouseEvent.getX();
      currentMouseY = paramMouseEvent.getY();
      if (slider.isRequestFocusEnabled()) {
        slider.requestFocus();
      }
      if (thumbRect.contains(currentMouseX, currentMouseY))
      {
        if ((UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag")) && (!SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
          return;
        }
        switch (slider.getOrientation())
        {
        case 1: 
          offset = (currentMouseY - thumbRect.y);
          break;
        case 0: 
          offset = (currentMouseX - thumbRect.x);
        }
        isDragging = true;
        return;
      }
      if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
        return;
      }
      isDragging = false;
      slider.setValueIsAdjusting(true);
      Dimension localDimension = slider.getSize();
      int i = 1;
      int j;
      switch (slider.getOrientation())
      {
      case 1: 
        if (thumbRect.isEmpty())
        {
          j = height / 2;
          if (!drawInverted()) {
            i = currentMouseY < j ? 1 : -1;
          } else {
            i = currentMouseY < j ? -1 : 1;
          }
        }
        else
        {
          j = thumbRect.y;
          if (!drawInverted()) {
            i = currentMouseY < j ? 1 : -1;
          } else {
            i = currentMouseY < j ? -1 : 1;
          }
        }
        break;
      case 0: 
        if (thumbRect.isEmpty())
        {
          j = width / 2;
          if (!drawInverted()) {
            i = currentMouseX < j ? -1 : 1;
          } else {
            i = currentMouseX < j ? 1 : -1;
          }
        }
        else
        {
          j = thumbRect.x;
          if (!drawInverted()) {
            i = currentMouseX < j ? -1 : 1;
          } else {
            i = currentMouseX < j ? 1 : -1;
          }
        }
        break;
      }
      if (shouldScroll(i)) {
        scrollDueToClickInTrack(i);
      }
      if (shouldScroll(i))
      {
        scrollTimer.stop();
        scrollListener.setDirection(i);
        scrollTimer.start();
      }
    }
    
    public boolean shouldScroll(int paramInt)
    {
      Rectangle localRectangle = thumbRect;
      if (slider.getOrientation() == 1)
      {
        if (drawInverted() ? paramInt < 0 : paramInt > 0)
        {
          if (y <= currentMouseY) {
            return false;
          }
        }
        else if (y + height >= currentMouseY) {
          return false;
        }
      }
      else if (drawInverted() ? paramInt < 0 : paramInt > 0)
      {
        if (x + width >= currentMouseX) {
          return false;
        }
      }
      else if (x <= currentMouseX) {
        return false;
      }
      if ((paramInt > 0) && (slider.getValue() + slider.getExtent() >= slider.getMaximum())) {
        return false;
      }
      return (paramInt >= 0) || (slider.getValue() > slider.getMinimum());
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (!slider.isEnabled()) {
        return;
      }
      currentMouseX = paramMouseEvent.getX();
      currentMouseY = paramMouseEvent.getY();
      if (!isDragging) {
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
        int n = trackRect.y + (trackRect.height - 1);
        int i1 = yPositionForValue(slider.getMaximum() - slider.getExtent());
        if (drawInverted()) {
          n = i1;
        } else {
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
        int i4 = trackRect.x;
        int i5 = trackRect.x + (trackRect.width - 1);
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
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicSliderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */