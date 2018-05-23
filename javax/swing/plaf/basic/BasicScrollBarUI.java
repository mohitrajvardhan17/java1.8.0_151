package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicScrollBarUI
  extends ScrollBarUI
  implements LayoutManager, SwingConstants
{
  private static final int POSITIVE_SCROLL = 1;
  private static final int NEGATIVE_SCROLL = -1;
  private static final int MIN_SCROLL = 2;
  private static final int MAX_SCROLL = 3;
  protected Dimension minimumThumbSize;
  protected Dimension maximumThumbSize;
  protected Color thumbHighlightColor;
  protected Color thumbLightShadowColor;
  protected Color thumbDarkShadowColor;
  protected Color thumbColor;
  protected Color trackColor;
  protected Color trackHighlightColor;
  protected JScrollBar scrollbar;
  protected JButton incrButton;
  protected JButton decrButton;
  protected boolean isDragging;
  protected TrackListener trackListener;
  protected ArrowButtonListener buttonListener;
  protected ModelListener modelListener;
  protected Rectangle thumbRect;
  protected Rectangle trackRect;
  protected int trackHighlight;
  protected static final int NO_HIGHLIGHT = 0;
  protected static final int DECREASE_HIGHLIGHT = 1;
  protected static final int INCREASE_HIGHLIGHT = 2;
  protected ScrollListener scrollListener;
  protected PropertyChangeListener propertyChangeListener;
  protected Timer scrollTimer;
  private static final int scrollSpeedThrottle = 60;
  private boolean supportsAbsolutePositioning;
  protected int scrollBarWidth;
  private Handler handler;
  private boolean thumbActive;
  private boolean useCachedValue = false;
  private int scrollBarValue;
  protected int incrGap;
  protected int decrGap;
  
  public BasicScrollBarUI() {}
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("positiveUnitIncrement"));
    paramLazyActionMap.put(new Actions("positiveBlockIncrement"));
    paramLazyActionMap.put(new Actions("negativeUnitIncrement"));
    paramLazyActionMap.put(new Actions("negativeBlockIncrement"));
    paramLazyActionMap.put(new Actions("minScroll"));
    paramLazyActionMap.put(new Actions("maxScroll"));
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicScrollBarUI();
  }
  
  protected void configureScrollBarColors()
  {
    LookAndFeel.installColors(scrollbar, "ScrollBar.background", "ScrollBar.foreground");
    thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
    thumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow");
    thumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow");
    thumbColor = UIManager.getColor("ScrollBar.thumb");
    trackColor = UIManager.getColor("ScrollBar.track");
    trackHighlightColor = UIManager.getColor("ScrollBar.trackHighlight");
  }
  
  public void installUI(JComponent paramJComponent)
  {
    scrollbar = ((JScrollBar)paramJComponent);
    thumbRect = new Rectangle(0, 0, 0, 0);
    trackRect = new Rectangle(0, 0, 0, 0);
    installDefaults();
    installComponents();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    scrollbar = ((JScrollBar)paramJComponent);
    uninstallListeners();
    uninstallDefaults();
    uninstallComponents();
    uninstallKeyboardActions();
    thumbRect = null;
    scrollbar = null;
    incrButton = null;
    decrButton = null;
  }
  
  protected void installDefaults()
  {
    scrollBarWidth = UIManager.getInt("ScrollBar.width");
    if (scrollBarWidth <= 0) {
      scrollBarWidth = 16;
    }
    minimumThumbSize = ((Dimension)UIManager.get("ScrollBar.minimumThumbSize"));
    maximumThumbSize = ((Dimension)UIManager.get("ScrollBar.maximumThumbSize"));
    Boolean localBoolean = (Boolean)UIManager.get("ScrollBar.allowsAbsolutePositioning");
    supportsAbsolutePositioning = (localBoolean != null ? localBoolean.booleanValue() : false);
    trackHighlight = 0;
    if ((scrollbar.getLayout() == null) || ((scrollbar.getLayout() instanceof UIResource))) {
      scrollbar.setLayout(this);
    }
    configureScrollBarColors();
    LookAndFeel.installBorder(scrollbar, "ScrollBar.border");
    LookAndFeel.installProperty(scrollbar, "opaque", Boolean.TRUE);
    scrollBarValue = scrollbar.getValue();
    incrGap = UIManager.getInt("ScrollBar.incrementButtonGap");
    decrGap = UIManager.getInt("ScrollBar.decrementButtonGap");
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
        decrGap = ((int)(decrGap * 0.714D));
      }
      else if ("mini".equals(str))
      {
        scrollBarWidth = ((int)(scrollBarWidth * 0.714D));
        incrGap = ((int)(incrGap * 0.714D));
        decrGap = ((int)(decrGap * 0.714D));
      }
    }
  }
  
  protected void installComponents()
  {
    switch (scrollbar.getOrientation())
    {
    case 1: 
      incrButton = createIncreaseButton(5);
      decrButton = createDecreaseButton(1);
      break;
    case 0: 
      if (scrollbar.getComponentOrientation().isLeftToRight())
      {
        incrButton = createIncreaseButton(3);
        decrButton = createDecreaseButton(7);
      }
      else
      {
        incrButton = createIncreaseButton(7);
        decrButton = createDecreaseButton(3);
      }
      break;
    }
    scrollbar.add(incrButton);
    scrollbar.add(decrButton);
    scrollbar.setEnabled(scrollbar.isEnabled());
  }
  
  protected void uninstallComponents()
  {
    scrollbar.remove(incrButton);
    scrollbar.remove(decrButton);
  }
  
  protected void installListeners()
  {
    trackListener = createTrackListener();
    buttonListener = createArrowButtonListener();
    modelListener = createModelListener();
    propertyChangeListener = createPropertyChangeListener();
    scrollbar.addMouseListener(trackListener);
    scrollbar.addMouseMotionListener(trackListener);
    scrollbar.getModel().addChangeListener(modelListener);
    scrollbar.addPropertyChangeListener(propertyChangeListener);
    scrollbar.addFocusListener(getHandler());
    if (incrButton != null) {
      incrButton.addMouseListener(buttonListener);
    }
    if (decrButton != null) {
      decrButton.addMouseListener(buttonListener);
    }
    scrollListener = createScrollListener();
    scrollTimer = new Timer(60, scrollListener);
    scrollTimer.setInitialDelay(300);
  }
  
  protected void installKeyboardActions()
  {
    LazyActionMap.installLazyActionMap(scrollbar, BasicScrollBarUI.class, "ScrollBar.actionMap");
    InputMap localInputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(scrollbar, 0, localInputMap);
    localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(scrollbar, 1, localInputMap);
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(scrollbar, 0, null);
    SwingUtilities.replaceUIActionMap(scrollbar, null);
  }
  
  private InputMap getInputMap(int paramInt)
  {
    InputMap localInputMap1;
    InputMap localInputMap2;
    if (paramInt == 0)
    {
      localInputMap1 = (InputMap)DefaultLookup.get(scrollbar, this, "ScrollBar.focusInputMap");
      if ((scrollbar.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(scrollbar, this, "ScrollBar.focusInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    if (paramInt == 1)
    {
      localInputMap1 = (InputMap)DefaultLookup.get(scrollbar, this, "ScrollBar.ancestorInputMap");
      if ((scrollbar.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(scrollbar, this, "ScrollBar.ancestorInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    return null;
  }
  
  protected void uninstallListeners()
  {
    scrollTimer.stop();
    scrollTimer = null;
    if (decrButton != null) {
      decrButton.removeMouseListener(buttonListener);
    }
    if (incrButton != null) {
      incrButton.removeMouseListener(buttonListener);
    }
    scrollbar.getModel().removeChangeListener(modelListener);
    scrollbar.removeMouseListener(trackListener);
    scrollbar.removeMouseMotionListener(trackListener);
    scrollbar.removePropertyChangeListener(propertyChangeListener);
    scrollbar.removeFocusListener(getHandler());
    handler = null;
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(scrollbar);
    if (scrollbar.getLayout() == this) {
      scrollbar.setLayout(null);
    }
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected TrackListener createTrackListener()
  {
    return new TrackListener();
  }
  
  protected ArrowButtonListener createArrowButtonListener()
  {
    return new ArrowButtonListener();
  }
  
  protected ModelListener createModelListener()
  {
    return new ModelListener();
  }
  
  protected ScrollListener createScrollListener()
  {
    return new ScrollListener();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private void updateThumbState(int paramInt1, int paramInt2)
  {
    Rectangle localRectangle = getThumbBounds();
    setThumbRollover(localRectangle.contains(paramInt1, paramInt2));
  }
  
  protected void setThumbRollover(boolean paramBoolean)
  {
    if (thumbActive != paramBoolean)
    {
      thumbActive = paramBoolean;
      scrollbar.repaint(getThumbBounds());
    }
  }
  
  public boolean isThumbRollover()
  {
    return thumbActive;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    paintTrack(paramGraphics, paramJComponent, getTrackBounds());
    Rectangle localRectangle = getThumbBounds();
    if (localRectangle.intersects(paramGraphics.getClipBounds())) {
      paintThumb(paramGraphics, paramJComponent, localRectangle);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return scrollbar.getOrientation() == 1 ? new Dimension(scrollBarWidth, 48) : new Dimension(48, scrollBarWidth);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  protected JButton createDecreaseButton(int paramInt)
  {
    return new BasicArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
  }
  
  protected JButton createIncreaseButton(int paramInt)
  {
    return new BasicArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
  }
  
  protected void paintDecreaseHighlight(Graphics paramGraphics)
  {
    Insets localInsets = scrollbar.getInsets();
    Rectangle localRectangle = getThumbBounds();
    paramGraphics.setColor(trackHighlightColor);
    int i;
    int j;
    int k;
    int m;
    if (scrollbar.getOrientation() == 1)
    {
      i = left;
      j = trackRect.y;
      k = scrollbar.getWidth() - (left + right);
      m = y - j;
      paramGraphics.fillRect(i, j, k, m);
    }
    else
    {
      if (scrollbar.getComponentOrientation().isLeftToRight())
      {
        i = trackRect.x;
        j = x - i;
      }
      else
      {
        i = x + width;
        j = trackRect.x + trackRect.width - i;
      }
      k = top;
      m = scrollbar.getHeight() - (top + bottom);
      paramGraphics.fillRect(i, k, j, m);
    }
  }
  
  protected void paintIncreaseHighlight(Graphics paramGraphics)
  {
    Insets localInsets = scrollbar.getInsets();
    Rectangle localRectangle = getThumbBounds();
    paramGraphics.setColor(trackHighlightColor);
    int i;
    int j;
    int k;
    int m;
    if (scrollbar.getOrientation() == 1)
    {
      i = left;
      j = y + height;
      k = scrollbar.getWidth() - (left + right);
      m = trackRect.y + trackRect.height - j;
      paramGraphics.fillRect(i, j, k, m);
    }
    else
    {
      if (scrollbar.getComponentOrientation().isLeftToRight())
      {
        i = x + width;
        j = trackRect.x + trackRect.width - i;
      }
      else
      {
        i = trackRect.x;
        j = x - i;
      }
      k = top;
      m = scrollbar.getHeight() - (top + bottom);
      paramGraphics.fillRect(i, k, j, m);
    }
  }
  
  protected void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    paramGraphics.setColor(trackColor);
    paramGraphics.fillRect(x, y, width, height);
    if (trackHighlight == 1) {
      paintDecreaseHighlight(paramGraphics);
    } else if (trackHighlight == 2) {
      paintIncreaseHighlight(paramGraphics);
    }
  }
  
  protected void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    if ((paramRectangle.isEmpty()) || (!scrollbar.isEnabled())) {
      return;
    }
    int i = width;
    int j = height;
    paramGraphics.translate(x, y);
    paramGraphics.setColor(thumbDarkShadowColor);
    SwingUtilities2.drawRect(paramGraphics, 0, 0, i - 1, j - 1);
    paramGraphics.setColor(thumbColor);
    paramGraphics.fillRect(0, 0, i - 1, j - 1);
    paramGraphics.setColor(thumbHighlightColor);
    SwingUtilities2.drawVLine(paramGraphics, 1, 1, j - 2);
    SwingUtilities2.drawHLine(paramGraphics, 2, i - 3, 1);
    paramGraphics.setColor(thumbLightShadowColor);
    SwingUtilities2.drawHLine(paramGraphics, 2, i - 2, j - 2);
    SwingUtilities2.drawVLine(paramGraphics, i - 2, 1, j - 3);
    paramGraphics.translate(-x, -y);
  }
  
  protected Dimension getMinimumThumbSize()
  {
    return minimumThumbSize;
  }
  
  protected Dimension getMaximumThumbSize()
  {
    return maximumThumbSize;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    return getPreferredSize((JComponent)paramContainer);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    return getMinimumSize((JComponent)paramContainer);
  }
  
  private int getValue(JScrollBar paramJScrollBar)
  {
    return useCachedValue ? scrollBarValue : paramJScrollBar.getValue();
  }
  
  protected void layoutVScrollbar(JScrollBar paramJScrollBar)
  {
    Dimension localDimension = paramJScrollBar.getSize();
    Insets localInsets = paramJScrollBar.getInsets();
    int i = width - (left + right);
    int j = left;
    boolean bool = DefaultLookup.getBoolean(scrollbar, this, "ScrollBar.squareButtons", false);
    int k = bool ? i : decrButton.getPreferredSize().height;
    int m = top;
    int n = bool ? i : incrButton.getPreferredSize().height;
    int i1 = height - (bottom + n);
    int i2 = top + bottom;
    int i3 = k + n;
    int i4 = decrGap + incrGap;
    float f1 = height - (i2 + i3) - i4;
    float f2 = paramJScrollBar.getMinimum();
    float f3 = paramJScrollBar.getVisibleAmount();
    float f4 = paramJScrollBar.getMaximum() - f2;
    float f5 = getValue(paramJScrollBar);
    int i5 = f4 <= 0.0F ? getMaximumThumbSizeheight : (int)(f1 * (f3 / f4));
    i5 = Math.max(i5, getMinimumThumbSizeheight);
    i5 = Math.min(i5, getMaximumThumbSizeheight);
    int i6 = i1 - incrGap - i5;
    if (f5 < paramJScrollBar.getMaximum() - paramJScrollBar.getVisibleAmount())
    {
      float f6 = f1 - i5;
      i6 = (int)(0.5F + f6 * ((f5 - f2) / (f4 - f3)));
      i6 += m + k + decrGap;
    }
    int i7 = height - i2;
    if (i7 < i3)
    {
      n = k = i7 / 2;
      i1 = height - (bottom + n);
    }
    decrButton.setBounds(j, m, i, k);
    incrButton.setBounds(j, i1, i, n);
    int i8 = m + k + decrGap;
    int i9 = i1 - incrGap - i8;
    trackRect.setBounds(j, i8, i, i9);
    if (i5 >= (int)f1)
    {
      if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
        setThumbBounds(j, i8, i, i9);
      } else {
        setThumbBounds(0, 0, 0, 0);
      }
    }
    else
    {
      if (i6 + i5 > i1 - incrGap) {
        i6 = i1 - incrGap - i5;
      }
      if (i6 < m + k + decrGap) {
        i6 = m + k + decrGap + 1;
      }
      setThumbBounds(j, i6, i, i5);
    }
  }
  
  protected void layoutHScrollbar(JScrollBar paramJScrollBar)
  {
    Dimension localDimension = paramJScrollBar.getSize();
    Insets localInsets = paramJScrollBar.getInsets();
    int i = height - (top + bottom);
    int j = top;
    boolean bool1 = paramJScrollBar.getComponentOrientation().isLeftToRight();
    boolean bool2 = DefaultLookup.getBoolean(scrollbar, this, "ScrollBar.squareButtons", false);
    int k = bool2 ? i : decrButton.getPreferredSize().width;
    int m = bool2 ? i : incrButton.getPreferredSize().width;
    if (!bool1)
    {
      n = k;
      k = m;
      m = n;
    }
    int n = left;
    int i1 = width - (right + m);
    int i2 = bool1 ? decrGap : incrGap;
    int i3 = bool1 ? incrGap : decrGap;
    int i4 = left + right;
    int i5 = k + m;
    float f1 = width - (i4 + i5) - (i2 + i3);
    float f2 = paramJScrollBar.getMinimum();
    float f3 = paramJScrollBar.getMaximum();
    float f4 = paramJScrollBar.getVisibleAmount();
    float f5 = f3 - f2;
    float f6 = getValue(paramJScrollBar);
    int i6 = f5 <= 0.0F ? getMaximumThumbSizewidth : (int)(f1 * (f4 / f5));
    i6 = Math.max(i6, getMinimumThumbSizewidth);
    i6 = Math.min(i6, getMaximumThumbSizewidth);
    int i7 = bool1 ? i1 - i3 - i6 : n + k + i2;
    if (f6 < f3 - paramJScrollBar.getVisibleAmount())
    {
      float f7 = f1 - i6;
      if (bool1) {
        i7 = (int)(0.5F + f7 * ((f6 - f2) / (f5 - f4)));
      } else {
        i7 = (int)(0.5F + f7 * ((f3 - f4 - f6) / (f5 - f4)));
      }
      i7 += n + k + i2;
    }
    int i8 = width - i4;
    if (i8 < i5)
    {
      m = k = i8 / 2;
      i1 = width - (right + m + i3);
    }
    (bool1 ? decrButton : incrButton).setBounds(n, j, k, i);
    (bool1 ? incrButton : decrButton).setBounds(i1, j, m, i);
    int i9 = n + k + i2;
    int i10 = i1 - i3 - i9;
    trackRect.setBounds(i9, j, i10, i);
    if (i6 >= (int)f1)
    {
      if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
        setThumbBounds(i9, j, i10, i);
      } else {
        setThumbBounds(0, 0, 0, 0);
      }
    }
    else
    {
      if (i7 + i6 > i1 - i3) {
        i7 = i1 - i3 - i6;
      }
      if (i7 < n + k + i2) {
        i7 = n + k + i2 + 1;
      }
      setThumbBounds(i7, j, i6, i);
    }
  }
  
  public void layoutContainer(Container paramContainer)
  {
    if (isDragging) {
      return;
    }
    JScrollBar localJScrollBar = (JScrollBar)paramContainer;
    switch (localJScrollBar.getOrientation())
    {
    case 1: 
      layoutVScrollbar(localJScrollBar);
      break;
    case 0: 
      layoutHScrollbar(localJScrollBar);
    }
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
    scrollbar.repaint(i, j, k - i, m - j);
    setThumbRollover(false);
  }
  
  protected Rectangle getThumbBounds()
  {
    return thumbRect;
  }
  
  protected Rectangle getTrackBounds()
  {
    return trackRect;
  }
  
  static void scrollByBlock(JScrollBar paramJScrollBar, int paramInt)
  {
    int i = paramJScrollBar.getValue();
    int j = paramJScrollBar.getBlockIncrement(paramInt);
    int k = j * (paramInt > 0 ? 1 : -1);
    int m = i + k;
    if ((k > 0) && (m < i)) {
      m = paramJScrollBar.getMaximum();
    } else if ((k < 0) && (m > i)) {
      m = paramJScrollBar.getMinimum();
    }
    paramJScrollBar.setValue(m);
  }
  
  protected void scrollByBlock(int paramInt)
  {
    scrollByBlock(scrollbar, paramInt);
    trackHighlight = (paramInt > 0 ? 2 : 1);
    Rectangle localRectangle = getTrackBounds();
    scrollbar.repaint(x, y, width, height);
  }
  
  static void scrollByUnits(JScrollBar paramJScrollBar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = -1;
    if (paramBoolean) {
      if (paramInt1 < 0) {
        j = paramJScrollBar.getValue() - paramJScrollBar.getBlockIncrement(paramInt1);
      } else {
        j = paramJScrollBar.getValue() + paramJScrollBar.getBlockIncrement(paramInt1);
      }
    }
    for (int k = 0; k < paramInt2; k++)
    {
      int i;
      if (paramInt1 > 0) {
        i = paramJScrollBar.getUnitIncrement(paramInt1);
      } else {
        i = -paramJScrollBar.getUnitIncrement(paramInt1);
      }
      int m = paramJScrollBar.getValue();
      int n = m + i;
      if ((i > 0) && (n < m)) {
        n = paramJScrollBar.getMaximum();
      } else if ((i < 0) && (n > m)) {
        n = paramJScrollBar.getMinimum();
      }
      if (m == n) {
        break;
      }
      if ((paramBoolean) && (k > 0))
      {
        assert (j != -1);
        if (((paramInt1 < 0) && (n < j)) || ((paramInt1 > 0) && (n > j))) {
          break;
        }
      }
      paramJScrollBar.setValue(n);
    }
  }
  
  protected void scrollByUnit(int paramInt)
  {
    scrollByUnits(scrollbar, paramInt, 1, false);
  }
  
  public boolean getSupportsAbsolutePositioning()
  {
    return supportsAbsolutePositioning;
  }
  
  private boolean isMouseLeftOfThumb()
  {
    return trackListener.currentMouseX < getThumbBoundsx;
  }
  
  private boolean isMouseRightOfThumb()
  {
    Rectangle localRectangle = getThumbBounds();
    return trackListener.currentMouseX > x + width;
  }
  
  private boolean isMouseBeforeThumb()
  {
    return scrollbar.getComponentOrientation().isLeftToRight() ? isMouseLeftOfThumb() : isMouseRightOfThumb();
  }
  
  private boolean isMouseAfterThumb()
  {
    return scrollbar.getComponentOrientation().isLeftToRight() ? isMouseRightOfThumb() : isMouseLeftOfThumb();
  }
  
  private void updateButtonDirections()
  {
    int i = scrollbar.getOrientation();
    if (scrollbar.getComponentOrientation().isLeftToRight())
    {
      if ((incrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)incrButton).setDirection(i == 0 ? 3 : 5);
      }
      if ((decrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)decrButton).setDirection(i == 0 ? 7 : 1);
      }
    }
    else
    {
      if ((incrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)incrButton).setDirection(i == 0 ? 7 : 5);
      }
      if ((decrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)decrButton).setDirection(i == 0 ? 3 : 1);
      }
    }
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
    private static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
    private static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
    private static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
    private static final String MIN_SCROLL = "minScroll";
    private static final String MAX_SCROLL = "maxScroll";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JScrollBar localJScrollBar = (JScrollBar)paramActionEvent.getSource();
      String str = getName();
      if (str == "positiveUnitIncrement") {
        scroll(localJScrollBar, 1, false);
      } else if (str == "positiveBlockIncrement") {
        scroll(localJScrollBar, 1, true);
      } else if (str == "negativeUnitIncrement") {
        scroll(localJScrollBar, -1, false);
      } else if (str == "negativeBlockIncrement") {
        scroll(localJScrollBar, -1, true);
      } else if (str == "minScroll") {
        scroll(localJScrollBar, 2, true);
      } else if (str == "maxScroll") {
        scroll(localJScrollBar, 3, true);
      }
    }
    
    private void scroll(JScrollBar paramJScrollBar, int paramInt, boolean paramBoolean)
    {
      if ((paramInt == -1) || (paramInt == 1))
      {
        int i;
        if (paramBoolean)
        {
          if (paramInt == -1) {
            i = -1 * paramJScrollBar.getBlockIncrement(-1);
          } else {
            i = paramJScrollBar.getBlockIncrement(1);
          }
        }
        else if (paramInt == -1) {
          i = -1 * paramJScrollBar.getUnitIncrement(-1);
        } else {
          i = paramJScrollBar.getUnitIncrement(1);
        }
        paramJScrollBar.setValue(paramJScrollBar.getValue() + i);
      }
      else if (paramInt == 2)
      {
        paramJScrollBar.setValue(paramJScrollBar.getMinimum());
      }
      else if (paramInt == 3)
      {
        paramJScrollBar.setValue(paramJScrollBar.getMaximum());
      }
    }
  }
  
  protected class ArrowButtonListener
    extends MouseAdapter
  {
    boolean handledEvent;
    
    protected ArrowButtonListener() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!scrollbar.isEnabled()) {
        return;
      }
      if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
        return;
      }
      int i = paramMouseEvent.getSource() == incrButton ? 1 : -1;
      scrollByUnit(i);
      scrollTimer.stop();
      scrollListener.setDirection(i);
      scrollListener.setScrollByBlock(false);
      scrollTimer.start();
      handledEvent = true;
      if ((!scrollbar.hasFocus()) && (scrollbar.isRequestFocusEnabled())) {
        scrollbar.requestFocus();
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      scrollTimer.stop();
      handledEvent = false;
      scrollbar.setValueIsAdjusting(false);
    }
  }
  
  private class Handler
    implements FocusListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      scrollbar.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      scrollbar.repaint();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject;
      if ("model" == str)
      {
        localObject = (BoundedRangeModel)paramPropertyChangeEvent.getOldValue();
        BoundedRangeModel localBoundedRangeModel = (BoundedRangeModel)paramPropertyChangeEvent.getNewValue();
        ((BoundedRangeModel)localObject).removeChangeListener(modelListener);
        localBoundedRangeModel.addChangeListener(modelListener);
        scrollBarValue = scrollbar.getValue();
        scrollbar.repaint();
        scrollbar.revalidate();
      }
      else if ("orientation" == str)
      {
        BasicScrollBarUI.this.updateButtonDirections();
      }
      else if ("componentOrientation" == str)
      {
        BasicScrollBarUI.this.updateButtonDirections();
        localObject = BasicScrollBarUI.this.getInputMap(0);
        SwingUtilities.replaceUIInputMap(scrollbar, 0, (InputMap)localObject);
      }
    }
  }
  
  protected class ModelListener
    implements ChangeListener
  {
    protected ModelListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (!useCachedValue) {
        scrollBarValue = scrollbar.getValue();
      }
      layoutContainer(scrollbar);
      useCachedValue = false;
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicScrollBarUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  protected class ScrollListener
    implements ActionListener
  {
    int direction = 1;
    boolean useBlockIncrement;
    
    public ScrollListener()
    {
      direction = 1;
      useBlockIncrement = false;
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
      if (useBlockIncrement)
      {
        scrollByBlock(direction);
        if (scrollbar.getOrientation() == 1)
        {
          if (direction > 0)
          {
            if (getThumbBounds().y + getThumbBounds().height >= trackListener.currentMouseY) {
              ((Timer)paramActionEvent.getSource()).stop();
            }
          }
          else if (getThumbBounds().y <= trackListener.currentMouseY) {
            ((Timer)paramActionEvent.getSource()).stop();
          }
        }
        else if (((direction > 0) && (!BasicScrollBarUI.this.isMouseAfterThumb())) || ((direction < 0) && (!BasicScrollBarUI.this.isMouseBeforeThumb()))) {
          ((Timer)paramActionEvent.getSource()).stop();
        }
      }
      else
      {
        scrollByUnit(direction);
      }
      if ((direction > 0) && (scrollbar.getValue() + scrollbar.getVisibleAmount() >= scrollbar.getMaximum())) {
        ((Timer)paramActionEvent.getSource()).stop();
      } else if ((direction < 0) && (scrollbar.getValue() <= scrollbar.getMinimum())) {
        ((Timer)paramActionEvent.getSource()).stop();
      }
    }
  }
  
  protected class TrackListener
    extends MouseAdapter
    implements MouseMotionListener
  {
    protected transient int offset;
    protected transient int currentMouseX;
    protected transient int currentMouseY;
    private transient int direction = 1;
    
    protected TrackListener() {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (isDragging) {
        BasicScrollBarUI.this.updateThumbState(paramMouseEvent.getX(), paramMouseEvent.getY());
      }
      if ((SwingUtilities.isRightMouseButton(paramMouseEvent)) || ((!getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))) {
        return;
      }
      if (!scrollbar.isEnabled()) {
        return;
      }
      Rectangle localRectangle = getTrackBounds();
      scrollbar.repaint(x, y, width, height);
      trackHighlight = 0;
      isDragging = false;
      offset = 0;
      scrollTimer.stop();
      useCachedValue = true;
      scrollbar.setValueIsAdjusting(false);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if ((SwingUtilities.isRightMouseButton(paramMouseEvent)) || ((!getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))) {
        return;
      }
      if (!scrollbar.isEnabled()) {
        return;
      }
      if ((!scrollbar.hasFocus()) && (scrollbar.isRequestFocusEnabled())) {
        scrollbar.requestFocus();
      }
      useCachedValue = true;
      scrollbar.setValueIsAdjusting(true);
      currentMouseX = paramMouseEvent.getX();
      currentMouseY = paramMouseEvent.getY();
      if (getThumbBounds().contains(currentMouseX, currentMouseY))
      {
        switch (scrollbar.getOrientation())
        {
        case 1: 
          offset = (currentMouseY - getThumbBounds().y);
          break;
        case 0: 
          offset = (currentMouseX - getThumbBounds().x);
        }
        isDragging = true;
        return;
      }
      if ((getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))
      {
        switch (scrollbar.getOrientation())
        {
        case 1: 
          offset = (getThumbBounds().height / 2);
          break;
        case 0: 
          offset = (getThumbBounds().width / 2);
        }
        isDragging = true;
        setValueFrom(paramMouseEvent);
        return;
      }
      isDragging = false;
      Dimension localDimension = scrollbar.getSize();
      direction = 1;
      int i;
      switch (scrollbar.getOrientation())
      {
      case 1: 
        if (getThumbBounds().isEmpty())
        {
          i = height / 2;
          direction = (currentMouseY < i ? -1 : 1);
        }
        else
        {
          i = getThumbBounds().y;
          direction = (currentMouseY < i ? -1 : 1);
        }
        break;
      case 0: 
        if (getThumbBounds().isEmpty())
        {
          i = width / 2;
          direction = (currentMouseX < i ? -1 : 1);
        }
        else
        {
          i = getThumbBounds().x;
          direction = (currentMouseX < i ? -1 : 1);
        }
        if (!scrollbar.getComponentOrientation().isLeftToRight()) {
          direction = (-direction);
        }
        break;
      }
      scrollByBlock(direction);
      scrollTimer.stop();
      scrollListener.setDirection(direction);
      scrollListener.setScrollByBlock(true);
      startScrollTimerIfNecessary();
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if ((SwingUtilities.isRightMouseButton(paramMouseEvent)) || ((!getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))) {
        return;
      }
      if ((!scrollbar.isEnabled()) || (getThumbBounds().isEmpty())) {
        return;
      }
      if (isDragging)
      {
        setValueFrom(paramMouseEvent);
      }
      else
      {
        currentMouseX = paramMouseEvent.getX();
        currentMouseY = paramMouseEvent.getY();
        BasicScrollBarUI.this.updateThumbState(currentMouseX, currentMouseY);
        startScrollTimerIfNecessary();
      }
    }
    
    private void setValueFrom(MouseEvent paramMouseEvent)
    {
      boolean bool = isThumbRollover();
      BoundedRangeModel localBoundedRangeModel = scrollbar.getModel();
      Rectangle localRectangle = getThumbBounds();
      int i;
      int j;
      int k;
      float f1;
      if (scrollbar.getOrientation() == 1)
      {
        i = trackRect.y;
        j = trackRect.y + trackRect.height - height;
        k = Math.min(j, Math.max(i, paramMouseEvent.getY() - offset));
        setThumbBounds(x, k, width, height);
        f1 = getTrackBounds().height;
      }
      else
      {
        i = trackRect.x;
        j = trackRect.x + trackRect.width - width;
        k = Math.min(j, Math.max(i, paramMouseEvent.getX() - offset));
        setThumbBounds(k, y, width, height);
        f1 = getTrackBounds().width;
      }
      if (k == j)
      {
        if ((scrollbar.getOrientation() == 1) || (scrollbar.getComponentOrientation().isLeftToRight())) {
          scrollbar.setValue(localBoundedRangeModel.getMaximum() - localBoundedRangeModel.getExtent());
        } else {
          scrollbar.setValue(localBoundedRangeModel.getMinimum());
        }
      }
      else
      {
        float f2 = localBoundedRangeModel.getMaximum() - localBoundedRangeModel.getExtent();
        float f3 = f2 - localBoundedRangeModel.getMinimum();
        float f4 = k - i;
        float f5 = j - i;
        int m;
        if ((scrollbar.getOrientation() == 1) || (scrollbar.getComponentOrientation().isLeftToRight())) {
          m = (int)(0.5D + f4 / f5 * f3);
        } else {
          m = (int)(0.5D + (j - k) / f5 * f3);
        }
        useCachedValue = true;
        scrollBarValue = (m + localBoundedRangeModel.getMinimum());
        scrollbar.setValue(adjustValueIfNecessary(scrollBarValue));
      }
      setThumbRollover(bool);
    }
    
    private int adjustValueIfNecessary(int paramInt)
    {
      if ((scrollbar.getParent() instanceof JScrollPane))
      {
        JScrollPane localJScrollPane = (JScrollPane)scrollbar.getParent();
        JViewport localJViewport = localJScrollPane.getViewport();
        Component localComponent = localJViewport.getView();
        if ((localComponent instanceof JList))
        {
          JList localJList = (JList)localComponent;
          if (DefaultLookup.getBoolean(localJList, localJList.getUI(), "List.lockToPositionOnScroll", false))
          {
            int i = paramInt;
            int j = localJList.getLayoutOrientation();
            int k = scrollbar.getOrientation();
            int m;
            Rectangle localRectangle1;
            if ((k == 1) && (j == 0))
            {
              m = localJList.locationToIndex(new Point(0, paramInt));
              localRectangle1 = localJList.getCellBounds(m, m);
              if (localRectangle1 != null) {
                i = y;
              }
            }
            if ((k == 0) && ((j == 1) || (j == 2))) {
              if (localJScrollPane.getComponentOrientation().isLeftToRight())
              {
                m = localJList.locationToIndex(new Point(paramInt, 0));
                localRectangle1 = localJList.getCellBounds(m, m);
                if (localRectangle1 != null) {
                  i = x;
                }
              }
              else
              {
                Point localPoint = new Point(paramInt, 0);
                int n = getExtentSizewidth;
                x += n - 1;
                int i1 = localJList.locationToIndex(localPoint);
                Rectangle localRectangle2 = localJList.getCellBounds(i1, i1);
                if (localRectangle2 != null) {
                  i = x + width - n;
                }
              }
            }
            paramInt = i;
          }
        }
      }
      return paramInt;
    }
    
    private void startScrollTimerIfNecessary()
    {
      if (scrollTimer.isRunning()) {
        return;
      }
      Rectangle localRectangle = getThumbBounds();
      switch (scrollbar.getOrientation())
      {
      case 1: 
        if (direction > 0)
        {
          if (y + height < trackListener.currentMouseY) {
            scrollTimer.start();
          }
        }
        else if (y > trackListener.currentMouseY) {
          scrollTimer.start();
        }
        break;
      case 0: 
        if (((direction > 0) && (BasicScrollBarUI.this.isMouseAfterThumb())) || ((direction < 0) && (BasicScrollBarUI.this.isMouseBeforeThumb()))) {
          scrollTimer.start();
        }
        break;
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      if (!isDragging) {
        BasicScrollBarUI.this.updateThumbState(paramMouseEvent.getX(), paramMouseEvent.getY());
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (!isDragging) {
        setThumbRollover(false);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */