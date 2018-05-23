package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTabbedPaneUI
  extends TabbedPaneUI
  implements SwingConstants
{
  protected JTabbedPane tabPane;
  protected Color highlight;
  protected Color lightHighlight;
  protected Color shadow;
  protected Color darkShadow;
  protected Color focus;
  private Color selectedColor;
  protected int textIconGap;
  protected int tabRunOverlay;
  protected Insets tabInsets;
  protected Insets selectedTabPadInsets;
  protected Insets tabAreaInsets;
  protected Insets contentBorderInsets;
  private boolean tabsOverlapBorder;
  private boolean tabsOpaque = true;
  private boolean contentOpaque = true;
  @Deprecated
  protected KeyStroke upKey;
  @Deprecated
  protected KeyStroke downKey;
  @Deprecated
  protected KeyStroke leftKey;
  @Deprecated
  protected KeyStroke rightKey;
  protected int[] tabRuns = new int[10];
  protected int runCount = 0;
  protected int selectedRun = -1;
  protected Rectangle[] rects = new Rectangle[0];
  protected int maxTabHeight;
  protected int maxTabWidth;
  protected ChangeListener tabChangeListener;
  protected PropertyChangeListener propertyChangeListener;
  protected MouseListener mouseListener;
  protected FocusListener focusListener;
  private Insets currentPadInsets = new Insets(0, 0, 0, 0);
  private Insets currentTabAreaInsets = new Insets(0, 0, 0, 0);
  private Component visibleComponent;
  private Vector<View> htmlViews;
  private Hashtable<Integer, Integer> mnemonicToIndexMap;
  private InputMap mnemonicInputMap;
  private ScrollableTabSupport tabScroller;
  private TabContainer tabContainer;
  protected transient Rectangle calcRect = new Rectangle(0, 0, 0, 0);
  private int focusIndex;
  private Handler handler;
  private int rolloverTabIndex;
  private boolean isRunsDirty;
  private boolean calculatedBaseline;
  private int baseline;
  private static int[] xCropLen = { 1, 1, 0, 0, 1, 1, 2, 2 };
  private static int[] yCropLen = { 0, 3, 3, 6, 6, 9, 9, 12 };
  private static final int CROP_SEGMENT = 12;
  
  public BasicTabbedPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTabbedPaneUI();
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("navigateNext"));
    paramLazyActionMap.put(new Actions("navigatePrevious"));
    paramLazyActionMap.put(new Actions("navigateRight"));
    paramLazyActionMap.put(new Actions("navigateLeft"));
    paramLazyActionMap.put(new Actions("navigateUp"));
    paramLazyActionMap.put(new Actions("navigateDown"));
    paramLazyActionMap.put(new Actions("navigatePageUp"));
    paramLazyActionMap.put(new Actions("navigatePageDown"));
    paramLazyActionMap.put(new Actions("requestFocus"));
    paramLazyActionMap.put(new Actions("requestFocusForVisibleComponent"));
    paramLazyActionMap.put(new Actions("setSelectedIndex"));
    paramLazyActionMap.put(new Actions("selectTabWithFocus"));
    paramLazyActionMap.put(new Actions("scrollTabsForwardAction"));
    paramLazyActionMap.put(new Actions("scrollTabsBackwardAction"));
  }
  
  public void installUI(JComponent paramJComponent)
  {
    tabPane = ((JTabbedPane)paramJComponent);
    calculatedBaseline = false;
    rolloverTabIndex = -1;
    focusIndex = -1;
    paramJComponent.setLayout(createLayoutManager());
    installComponents();
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDefaults();
    uninstallComponents();
    paramJComponent.setLayout(null);
    tabPane = null;
  }
  
  protected LayoutManager createLayoutManager()
  {
    if (tabPane.getTabLayoutPolicy() == 1) {
      return new TabbedPaneScrollLayout(null);
    }
    return new TabbedPaneLayout();
  }
  
  private boolean scrollableTabLayoutEnabled()
  {
    return tabPane.getLayout() instanceof TabbedPaneScrollLayout;
  }
  
  protected void installComponents()
  {
    if ((scrollableTabLayoutEnabled()) && (tabScroller == null))
    {
      tabScroller = new ScrollableTabSupport(tabPane.getTabPlacement());
      tabPane.add(tabScroller.viewport);
    }
    installTabContainer();
  }
  
  private void installTabContainer()
  {
    for (int i = 0; i < tabPane.getTabCount(); i++)
    {
      Component localComponent = tabPane.getTabComponentAt(i);
      if (localComponent != null)
      {
        if (tabContainer == null) {
          tabContainer = new TabContainer();
        }
        tabContainer.add(localComponent);
      }
    }
    if (tabContainer == null) {
      return;
    }
    if (scrollableTabLayoutEnabled()) {
      tabScroller.tabPanel.add(tabContainer);
    } else {
      tabPane.add(tabContainer);
    }
  }
  
  protected JButton createScrollButton(int paramInt)
  {
    if ((paramInt != 5) && (paramInt != 1) && (paramInt != 3) && (paramInt != 7)) {
      throw new IllegalArgumentException("Direction must be one of: SOUTH, NORTH, EAST or WEST");
    }
    return new ScrollableTabButton(paramInt);
  }
  
  protected void uninstallComponents()
  {
    uninstallTabContainer();
    if (scrollableTabLayoutEnabled())
    {
      tabPane.remove(tabScroller.viewport);
      tabPane.remove(tabScroller.scrollForwardButton);
      tabPane.remove(tabScroller.scrollBackwardButton);
      tabScroller = null;
    }
  }
  
  private void uninstallTabContainer()
  {
    if (tabContainer == null) {
      return;
    }
    tabContainer.notifyTabbedPane = false;
    tabContainer.removeAll();
    if (scrollableTabLayoutEnabled())
    {
      tabContainer.remove(tabScroller.croppedEdge);
      tabScroller.tabPanel.remove(tabContainer);
    }
    else
    {
      tabPane.remove(tabContainer);
    }
    tabContainer = null;
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background", "TabbedPane.foreground", "TabbedPane.font");
    highlight = UIManager.getColor("TabbedPane.light");
    lightHighlight = UIManager.getColor("TabbedPane.highlight");
    shadow = UIManager.getColor("TabbedPane.shadow");
    darkShadow = UIManager.getColor("TabbedPane.darkShadow");
    focus = UIManager.getColor("TabbedPane.focus");
    selectedColor = UIManager.getColor("TabbedPane.selected");
    textIconGap = UIManager.getInt("TabbedPane.textIconGap");
    tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
    selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
    tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
    tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");
    contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
    tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
    tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
    contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
    Object localObject = UIManager.get("TabbedPane.opaque");
    if (localObject == null) {
      localObject = Boolean.FALSE;
    }
    LookAndFeel.installProperty(tabPane, "opaque", localObject);
    if (tabInsets == null) {
      tabInsets = new Insets(0, 4, 1, 4);
    }
    if (selectedTabPadInsets == null) {
      selectedTabPadInsets = new Insets(2, 2, 2, 1);
    }
    if (tabAreaInsets == null) {
      tabAreaInsets = new Insets(3, 2, 0, 2);
    }
    if (contentBorderInsets == null) {
      contentBorderInsets = new Insets(2, 2, 3, 3);
    }
  }
  
  protected void uninstallDefaults()
  {
    highlight = null;
    lightHighlight = null;
    shadow = null;
    darkShadow = null;
    focus = null;
    tabInsets = null;
    selectedTabPadInsets = null;
    tabAreaInsets = null;
    contentBorderInsets = null;
  }
  
  protected void installListeners()
  {
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      tabPane.addPropertyChangeListener(propertyChangeListener);
    }
    if ((tabChangeListener = createChangeListener()) != null) {
      tabPane.addChangeListener(tabChangeListener);
    }
    if ((mouseListener = createMouseListener()) != null) {
      tabPane.addMouseListener(mouseListener);
    }
    tabPane.addMouseMotionListener(getHandler());
    if ((focusListener = createFocusListener()) != null) {
      tabPane.addFocusListener(focusListener);
    }
    tabPane.addContainerListener(getHandler());
    if (tabPane.getTabCount() > 0) {
      htmlViews = createHTMLVector();
    }
  }
  
  protected void uninstallListeners()
  {
    if (mouseListener != null)
    {
      tabPane.removeMouseListener(mouseListener);
      mouseListener = null;
    }
    tabPane.removeMouseMotionListener(getHandler());
    if (focusListener != null)
    {
      tabPane.removeFocusListener(focusListener);
      focusListener = null;
    }
    tabPane.removeContainerListener(getHandler());
    if (htmlViews != null)
    {
      htmlViews.removeAllElements();
      htmlViews = null;
    }
    if (tabChangeListener != null)
    {
      tabPane.removeChangeListener(tabChangeListener);
      tabChangeListener = null;
    }
    if (propertyChangeListener != null)
    {
      tabPane.removePropertyChangeListener(propertyChangeListener);
      propertyChangeListener = null;
    }
    handler = null;
  }
  
  protected MouseListener createMouseListener()
  {
    return getHandler();
  }
  
  protected FocusListener createFocusListener()
  {
    return getHandler();
  }
  
  protected ChangeListener createChangeListener()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
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
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(tabPane, 1, localInputMap);
    localInputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(tabPane, 0, localInputMap);
    LazyActionMap.installLazyActionMap(tabPane, BasicTabbedPaneUI.class, "TabbedPane.actionMap");
    updateMnemonics();
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(tabPane, this, "TabbedPane.ancestorInputMap");
    }
    if (paramInt == 0) {
      return (InputMap)DefaultLookup.get(tabPane, this, "TabbedPane.focusInputMap");
    }
    return null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(tabPane, null);
    SwingUtilities.replaceUIInputMap(tabPane, 1, null);
    SwingUtilities.replaceUIInputMap(tabPane, 0, null);
    SwingUtilities.replaceUIInputMap(tabPane, 2, null);
    mnemonicToIndexMap = null;
    mnemonicInputMap = null;
  }
  
  private void updateMnemonics()
  {
    resetMnemonics();
    for (int i = tabPane.getTabCount() - 1; i >= 0; i--)
    {
      int j = tabPane.getMnemonicAt(i);
      if (j > 0) {
        addMnemonic(i, j);
      }
    }
  }
  
  private void resetMnemonics()
  {
    if (mnemonicToIndexMap != null)
    {
      mnemonicToIndexMap.clear();
      mnemonicInputMap.clear();
    }
  }
  
  private void addMnemonic(int paramInt1, int paramInt2)
  {
    if (mnemonicToIndexMap == null) {
      initMnemonics();
    }
    mnemonicInputMap.put(KeyStroke.getKeyStroke(paramInt2, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "setSelectedIndex");
    mnemonicToIndexMap.put(Integer.valueOf(paramInt2), Integer.valueOf(paramInt1));
  }
  
  private void initMnemonics()
  {
    mnemonicToIndexMap = new Hashtable();
    mnemonicInputMap = new ComponentInputMapUIResource(tabPane);
    mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(tabPane, 2));
    SwingUtilities.replaceUIInputMap(tabPane, 2, mnemonicInputMap);
  }
  
  private void setRolloverTab(int paramInt1, int paramInt2)
  {
    setRolloverTab(tabForCoordinate(tabPane, paramInt1, paramInt2, false));
  }
  
  protected void setRolloverTab(int paramInt)
  {
    rolloverTabIndex = paramInt;
  }
  
  protected int getRolloverTab()
  {
    return rolloverTabIndex;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return null;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return null;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = calculateBaselineIfNecessary();
    if (i != -1)
    {
      int j = tabPane.getTabPlacement();
      Insets localInsets1 = tabPane.getInsets();
      Insets localInsets2 = getTabAreaInsets(j);
      switch (j)
      {
      case 1: 
        i += top + top;
        return i;
      case 3: 
        i = paramInt2 - bottom - bottom - maxTabHeight + i;
        return i;
      case 2: 
      case 4: 
        i += top + top;
        return i;
      }
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    switch (tabPane.getTabPlacement())
    {
    case 1: 
    case 2: 
    case 4: 
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    case 3: 
      return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  protected int getBaseline(int paramInt)
  {
    if (tabPane.getTabComponentAt(paramInt) != null)
    {
      int i = getBaselineOffset();
      if (i != 0) {
        return -1;
      }
      Component localComponent = tabPane.getTabComponentAt(paramInt);
      Dimension localDimension = localComponent.getPreferredSize();
      Insets localInsets = getTabInsets(tabPane.getTabPlacement(), paramInt);
      int m = maxTabHeight - top - bottom;
      return localComponent.getBaseline(width, height) + (m - height) / 2 + top;
    }
    Object localObject = getTextViewForTab(paramInt);
    if (localObject != null)
    {
      j = (int)((View)localObject).getPreferredSpan(1);
      k = BasicHTML.getHTMLBaseline((View)localObject, (int)((View)localObject).getPreferredSpan(0), j);
      if (k >= 0) {
        return maxTabHeight / 2 - j / 2 + k + getBaselineOffset();
      }
      return -1;
    }
    localObject = getFontMetrics();
    int j = ((FontMetrics)localObject).getHeight();
    int k = ((FontMetrics)localObject).getAscent();
    return maxTabHeight / 2 - j / 2 + k + getBaselineOffset();
  }
  
  protected int getBaselineOffset()
  {
    switch (tabPane.getTabPlacement())
    {
    case 1: 
      if (tabPane.getTabCount() > 1) {
        return 1;
      }
      return -1;
    case 3: 
      if (tabPane.getTabCount() > 1) {
        return -1;
      }
      return 1;
    }
    return maxTabHeight % 2;
  }
  
  private int calculateBaselineIfNecessary()
  {
    if (!calculatedBaseline)
    {
      calculatedBaseline = true;
      baseline = -1;
      if (tabPane.getTabCount() > 0) {
        calculateBaseline();
      }
    }
    return baseline;
  }
  
  private void calculateBaseline()
  {
    int i = tabPane.getTabCount();
    int j = tabPane.getTabPlacement();
    maxTabHeight = calculateMaxTabHeight(j);
    baseline = getBaseline(0);
    if (isHorizontalTabPlacement())
    {
      for (int k = 1; k < i; k++) {
        if (getBaseline(k) != baseline)
        {
          baseline = -1;
          break;
        }
      }
    }
    else
    {
      FontMetrics localFontMetrics = getFontMetrics();
      int m = localFontMetrics.getHeight();
      int n = calculateTabHeight(j, 0, m);
      for (int i1 = 1; i1 < i; i1++)
      {
        int i2 = calculateTabHeight(j, i1, m);
        if (n != i2)
        {
          baseline = -1;
          break;
        }
      }
    }
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    int i = tabPane.getSelectedIndex();
    int j = tabPane.getTabPlacement();
    ensureCurrentLayout();
    if (tabsOverlapBorder) {
      paintContentBorder(paramGraphics, j, i);
    }
    if (!scrollableTabLayoutEnabled()) {
      paintTabArea(paramGraphics, j, i);
    }
    if (!tabsOverlapBorder) {
      paintContentBorder(paramGraphics, j, i);
    }
  }
  
  protected void paintTabArea(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    int i = tabPane.getTabCount();
    Rectangle localRectangle1 = new Rectangle();
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = paramGraphics.getClipBounds();
    for (int j = runCount - 1; j >= 0; j--)
    {
      int k = tabRuns[j];
      int m = tabRuns[(j + 1)];
      int n = m != 0 ? m - 1 : i - 1;
      for (int i1 = k; i1 <= n; i1++) {
        if ((i1 != paramInt2) && (rects[i1].intersects(localRectangle3))) {
          paintTab(paramGraphics, paramInt1, rects, i1, localRectangle1, localRectangle2);
        }
      }
    }
    if ((paramInt2 >= 0) && (rects[paramInt2].intersects(localRectangle3))) {
      paintTab(paramGraphics, paramInt1, rects, paramInt2, localRectangle1, localRectangle2);
    }
  }
  
  protected void paintTab(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
    int i = tabPane.getSelectedIndex();
    boolean bool = i == paramInt2;
    if ((tabsOpaque) || (tabPane.isOpaque())) {
      paintTabBackground(paramGraphics, paramInt1, paramInt2, x, y, width, height, bool);
    }
    paintTabBorder(paramGraphics, paramInt1, paramInt2, x, y, width, height, bool);
    String str1 = tabPane.getTitleAt(paramInt2);
    Font localFont = tabPane.getFont();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(tabPane, paramGraphics, localFont);
    Icon localIcon = getIconForTab(paramInt2);
    layoutLabel(paramInt1, localFontMetrics, paramInt2, str1, localIcon, localRectangle, paramRectangle1, paramRectangle2, bool);
    if (tabPane.getTabComponentAt(paramInt2) == null)
    {
      String str2 = str1;
      if ((scrollableTabLayoutEnabled()) && (tabScroller.croppedEdge.isParamsSet()) && (tabScroller.croppedEdge.getTabIndex() == paramInt2) && (isHorizontalTabPlacement()))
      {
        int j = tabScroller.croppedEdge.getCropline() - (x - x) - tabScroller.croppedEdge.getCroppedSideWidth();
        str2 = SwingUtilities2.clipStringIfNecessary(null, localFontMetrics, str1, j);
      }
      else if ((!scrollableTabLayoutEnabled()) && (isHorizontalTabPlacement()))
      {
        str2 = SwingUtilities2.clipStringIfNecessary(null, localFontMetrics, str1, width);
      }
      paintText(paramGraphics, paramInt1, localFont, localFontMetrics, paramInt2, str2, paramRectangle2, bool);
      paintIcon(paramGraphics, paramInt1, paramInt2, localIcon, paramRectangle1, bool);
    }
    paintFocusIndicator(paramGraphics, paramInt1, paramArrayOfRectangle, paramInt2, paramRectangle1, paramRectangle2, bool);
  }
  
  private boolean isHorizontalTabPlacement()
  {
    return (tabPane.getTabPlacement() == 1) || (tabPane.getTabPlacement() == 3);
  }
  
  private static Polygon createCroppedTabShape(int paramInt1, Rectangle paramRectangle, int paramInt2)
  {
    int i;
    int j;
    int k;
    int m;
    switch (paramInt1)
    {
    case 2: 
    case 4: 
      i = width;
      j = x;
      k = x + width;
      m = y + height;
      break;
    case 1: 
    case 3: 
    default: 
      i = height;
      j = y;
      k = y + height;
      m = x + width;
    }
    int n = i / 12;
    if (i % 12 > 0) {
      n++;
    }
    int i1 = 2 + n * 8;
    int[] arrayOfInt1 = new int[i1];
    int[] arrayOfInt2 = new int[i1];
    int i2 = 0;
    arrayOfInt1[i2] = m;
    arrayOfInt2[(i2++)] = k;
    arrayOfInt1[i2] = m;
    arrayOfInt2[(i2++)] = j;
    for (int i3 = 0; i3 < n; i3++) {
      for (int i4 = 0; i4 < xCropLen.length; i4++)
      {
        arrayOfInt1[i2] = (paramInt2 - xCropLen[i4]);
        arrayOfInt2[i2] = (j + i3 * 12 + yCropLen[i4]);
        if (arrayOfInt2[i2] >= k)
        {
          arrayOfInt2[i2] = k;
          i2++;
          break;
        }
        i2++;
      }
    }
    if ((paramInt1 == 1) || (paramInt1 == 3)) {
      return new Polygon(arrayOfInt1, arrayOfInt2, i2);
    }
    return new Polygon(arrayOfInt2, arrayOfInt1, i2);
  }
  
  private void paintCroppedTabEdge(Graphics paramGraphics)
  {
    int i = tabScroller.croppedEdge.getTabIndex();
    int j = tabScroller.croppedEdge.getCropline();
    int k;
    int m;
    int n;
    switch (tabPane.getTabPlacement())
    {
    case 2: 
    case 4: 
      k = rects[i].x;
      m = j;
      n = k;
      paramGraphics.setColor(shadow);
    }
    while (n <= k + rects[i].width)
    {
      for (int i1 = 0; i1 < xCropLen.length; i1 += 2) {
        paramGraphics.drawLine(n + yCropLen[i1], m - xCropLen[i1], n + yCropLen[(i1 + 1)] - 1, m - xCropLen[(i1 + 1)]);
      }
      n += 12;
      continue;
      k = j;
      m = rects[i].y;
      i1 = m;
      paramGraphics.setColor(shadow);
      while (i1 <= m + rects[i].height)
      {
        for (int i2 = 0; i2 < xCropLen.length; i2 += 2) {
          paramGraphics.drawLine(k - xCropLen[i2], i1 + yCropLen[i2], k - xCropLen[(i2 + 1)], i1 + yCropLen[(i2 + 1)] - 1);
        }
        i1 += 12;
      }
    }
  }
  
  protected void layoutLabel(int paramInt1, FontMetrics paramFontMetrics, int paramInt2, String paramString, Icon paramIcon, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, boolean paramBoolean)
  {
    x = (y = x = y = 0);
    View localView = getTextViewForTab(paramInt2);
    if (localView != null) {
      tabPane.putClientProperty("html", localView);
    }
    SwingUtilities.layoutCompoundLabel(tabPane, paramFontMetrics, paramString, paramIcon, 0, 0, 0, 11, paramRectangle1, paramRectangle2, paramRectangle3, textIconGap);
    tabPane.putClientProperty("html", null);
    int i = getTabLabelShiftX(paramInt1, paramInt2, paramBoolean);
    int j = getTabLabelShiftY(paramInt1, paramInt2, paramBoolean);
    x += i;
    y += j;
    x += i;
    y += j;
  }
  
  protected void paintIcon(Graphics paramGraphics, int paramInt1, int paramInt2, Icon paramIcon, Rectangle paramRectangle, boolean paramBoolean)
  {
    if (paramIcon != null) {
      paramIcon.paintIcon(tabPane, paramGraphics, x, y);
    }
  }
  
  protected void paintText(Graphics paramGraphics, int paramInt1, Font paramFont, FontMetrics paramFontMetrics, int paramInt2, String paramString, Rectangle paramRectangle, boolean paramBoolean)
  {
    paramGraphics.setFont(paramFont);
    View localView = getTextViewForTab(paramInt2);
    if (localView != null)
    {
      localView.paint(paramGraphics, paramRectangle);
    }
    else
    {
      int i = tabPane.getDisplayedMnemonicIndexAt(paramInt2);
      if ((tabPane.isEnabled()) && (tabPane.isEnabledAt(paramInt2)))
      {
        Object localObject = tabPane.getForegroundAt(paramInt2);
        if ((paramBoolean) && ((localObject instanceof UIResource)))
        {
          Color localColor = UIManager.getColor("TabbedPane.selectedForeground");
          if (localColor != null) {
            localObject = localColor;
          }
        }
        paramGraphics.setColor((Color)localObject);
        SwingUtilities2.drawStringUnderlineCharAt(tabPane, paramGraphics, paramString, i, x, y + paramFontMetrics.getAscent());
      }
      else
      {
        paramGraphics.setColor(tabPane.getBackgroundAt(paramInt2).brighter());
        SwingUtilities2.drawStringUnderlineCharAt(tabPane, paramGraphics, paramString, i, x, y + paramFontMetrics.getAscent());
        paramGraphics.setColor(tabPane.getBackgroundAt(paramInt2).darker());
        SwingUtilities2.drawStringUnderlineCharAt(tabPane, paramGraphics, paramString, i, x - 1, y + paramFontMetrics.getAscent() - 1);
      }
    }
  }
  
  protected int getTabLabelShiftX(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Rectangle localRectangle = rects[paramInt2];
    String str = paramBoolean ? "selectedLabelShift" : "labelShift";
    int i = DefaultLookup.getInt(tabPane, this, "TabbedPane." + str, 1);
    switch (paramInt1)
    {
    case 2: 
      return i;
    case 4: 
      return -i;
    }
    return width % 2;
  }
  
  protected int getTabLabelShiftY(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Rectangle localRectangle = rects[paramInt2];
    int i = paramBoolean ? DefaultLookup.getInt(tabPane, this, "TabbedPane.selectedLabelShift", -1) : DefaultLookup.getInt(tabPane, this, "TabbedPane.labelShift", 1);
    switch (paramInt1)
    {
    case 3: 
      return -i;
    case 2: 
    case 4: 
      return height % 2;
    }
    return i;
  }
  
  protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean)
  {
    Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
    if ((tabPane.hasFocus()) && (paramBoolean))
    {
      paramGraphics.setColor(focus);
      int i;
      int j;
      int k;
      int m;
      switch (paramInt1)
      {
      case 2: 
        i = x + 3;
        j = y + 3;
        k = width - 5;
        m = height - 6;
        break;
      case 4: 
        i = x + 2;
        j = y + 3;
        k = width - 5;
        m = height - 6;
        break;
      case 3: 
        i = x + 3;
        j = y + 2;
        k = width - 6;
        m = height - 5;
        break;
      case 1: 
      default: 
        i = x + 3;
        j = y + 3;
        k = width - 6;
        m = height - 5;
      }
      BasicGraphicsUtils.drawDashedRect(paramGraphics, i, j, k, m);
    }
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    paramGraphics.setColor(lightHighlight);
    switch (paramInt1)
    {
    case 2: 
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 1, paramInt4);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      break;
    case 4: 
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
      break;
    case 3: 
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
      break;
    case 1: 
    default: 
      paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 1);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
    }
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    paramGraphics.setColor((!paramBoolean) || (selectedColor == null) ? tabPane.getBackgroundAt(paramInt2) : selectedColor);
    switch (paramInt1)
    {
    case 2: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 1, paramInt6 - 3);
      break;
    case 4: 
      paramGraphics.fillRect(paramInt3, paramInt4 + 1, paramInt5 - 2, paramInt6 - 3);
      break;
    case 3: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4, paramInt5 - 3, paramInt6 - 1);
      break;
    case 1: 
    default: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 3, paramInt6 - 1);
    }
  }
  
  protected void paintContentBorder(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    int i = tabPane.getWidth();
    int j = tabPane.getHeight();
    Insets localInsets1 = tabPane.getInsets();
    Insets localInsets2 = getTabAreaInsets(paramInt1);
    int k = left;
    int m = top;
    int n = i - right - left;
    int i1 = j - top - bottom;
    switch (paramInt1)
    {
    case 2: 
      k += calculateTabAreaWidth(paramInt1, runCount, maxTabWidth);
      if (tabsOverlapBorder) {
        k -= right;
      }
      n -= k - left;
      break;
    case 4: 
      n -= calculateTabAreaWidth(paramInt1, runCount, maxTabWidth);
      if (tabsOverlapBorder) {
        n += left;
      }
      break;
    case 3: 
      i1 -= calculateTabAreaHeight(paramInt1, runCount, maxTabHeight);
      if (tabsOverlapBorder) {
        i1 += top;
      }
      break;
    case 1: 
    default: 
      m += calculateTabAreaHeight(paramInt1, runCount, maxTabHeight);
      if (tabsOverlapBorder) {
        m -= bottom;
      }
      i1 -= m - top;
    }
    if ((tabPane.getTabCount() > 0) && ((contentOpaque) || (tabPane.isOpaque())))
    {
      Color localColor = UIManager.getColor("TabbedPane.contentAreaColor");
      if (localColor != null) {
        paramGraphics.setColor(localColor);
      } else if ((selectedColor == null) || (paramInt2 == -1)) {
        paramGraphics.setColor(tabPane.getBackground());
      } else {
        paramGraphics.setColor(selectedColor);
      }
      paramGraphics.fillRect(k, m, n, i1);
    }
    paintContentBorderTopEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderLeftEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderBottomEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderRightEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
  }
  
  protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(lightHighlight);
    if ((paramInt1 != 1) || (paramInt2 < 0) || (y + height + 1 < paramInt4) || (x < paramInt3) || (x > paramInt3 + paramInt5))
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
    }
    else
    {
      paramGraphics.drawLine(paramInt3, paramInt4, x - 1, paramInt4);
      if (x + width < paramInt3 + paramInt5 - 2)
      {
        paramGraphics.drawLine(x + width, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      }
      else
      {
        paramGraphics.setColor(shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      }
    }
  }
  
  protected void paintContentBorderLeftEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(lightHighlight);
    if ((paramInt1 != 2) || (paramInt2 < 0) || (x + width + 1 < paramInt3) || (y < paramInt4) || (y > paramInt4 + paramInt6))
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 2);
    }
    else
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, y - 1);
      if (y + height < paramInt4 + paramInt6 - 2) {
        paramGraphics.drawLine(paramInt3, y + height, paramInt3, paramInt4 + paramInt6 - 2);
      }
    }
  }
  
  protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(shadow);
    if ((paramInt1 != 3) || (paramInt2 < 0) || (y - 1 > paramInt6) || (x < paramInt3) || (x > paramInt3 + paramInt5))
    {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, x - 1, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, x - 1, paramInt4 + paramInt6 - 1);
      if (x + width < paramInt3 + paramInt5 - 2)
      {
        paramGraphics.setColor(shadow);
        paramGraphics.drawLine(x + width, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(darkShadow);
        paramGraphics.drawLine(x + width, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      }
    }
  }
  
  protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(shadow);
    if ((paramInt1 != 4) || (paramInt2 < 0) || (x - 1 > paramInt5) || (y < paramInt4) || (y > paramInt4 + paramInt6))
    {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, y - 1);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, y - 1);
      if (y + height < paramInt4 + paramInt6 - 2)
      {
        paramGraphics.setColor(shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, y + height, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(darkShadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, y + height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
      }
    }
  }
  
  private void ensureCurrentLayout()
  {
    if (!tabPane.isValid()) {
      tabPane.validate();
    }
    if (!tabPane.isValid())
    {
      TabbedPaneLayout localTabbedPaneLayout = (TabbedPaneLayout)tabPane.getLayout();
      localTabbedPaneLayout.calculateLayoutInfo();
    }
  }
  
  public Rectangle getTabBounds(JTabbedPane paramJTabbedPane, int paramInt)
  {
    ensureCurrentLayout();
    Rectangle localRectangle = new Rectangle();
    return getTabBounds(paramInt, localRectangle);
  }
  
  public int getTabRunCount(JTabbedPane paramJTabbedPane)
  {
    ensureCurrentLayout();
    return runCount;
  }
  
  public int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2)
  {
    return tabForCoordinate(paramJTabbedPane, paramInt1, paramInt2, true);
  }
  
  private int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean) {
      ensureCurrentLayout();
    }
    if (isRunsDirty) {
      return -1;
    }
    Point localPoint = new Point(paramInt1, paramInt2);
    if (scrollableTabLayoutEnabled())
    {
      translatePointToTabPanel(paramInt1, paramInt2, localPoint);
      Rectangle localRectangle = tabScroller.viewport.getViewRect();
      if (!localRectangle.contains(localPoint)) {
        return -1;
      }
    }
    int i = tabPane.getTabCount();
    for (int j = 0; j < i; j++) {
      if (rects[j].contains(x, y)) {
        return j;
      }
    }
    return -1;
  }
  
  protected Rectangle getTabBounds(int paramInt, Rectangle paramRectangle)
  {
    width = rects[paramInt].width;
    height = rects[paramInt].height;
    if (scrollableTabLayoutEnabled())
    {
      Point localPoint1 = tabScroller.viewport.getLocation();
      Point localPoint2 = tabScroller.viewport.getViewPosition();
      x = (rects[paramInt].x + x - x);
      y = (rects[paramInt].y + y - y);
    }
    else
    {
      x = rects[paramInt].x;
      y = rects[paramInt].y;
    }
    return paramRectangle;
  }
  
  private int getClosestTab(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = Math.min(rects.length, tabPane.getTabCount());
    int k = j;
    int m = tabPane.getTabPlacement();
    int n = (m == 1) || (m == 3) ? 1 : 0;
    int i1 = n != 0 ? paramInt1 : paramInt2;
    while (i != k)
    {
      int i2 = (k + i) / 2;
      int i3;
      int i4;
      if (n != 0)
      {
        i3 = rects[i2].x;
        i4 = i3 + rects[i2].width;
      }
      else
      {
        i3 = rects[i2].y;
        i4 = i3 + rects[i2].height;
      }
      if (i1 < i3)
      {
        k = i2;
        if (i == k) {
          return Math.max(0, i2 - 1);
        }
      }
      else if (i1 >= i4)
      {
        i = i2;
        if (k - i <= 1) {
          return Math.max(i2 + 1, j - 1);
        }
      }
      else
      {
        return i2;
      }
    }
    return i;
  }
  
  private Point translatePointToTabPanel(int paramInt1, int paramInt2, Point paramPoint)
  {
    Point localPoint1 = tabScroller.viewport.getLocation();
    Point localPoint2 = tabScroller.viewport.getViewPosition();
    x = (paramInt1 - x + x);
    y = (paramInt2 - y + y);
    return paramPoint;
  }
  
  protected Component getVisibleComponent()
  {
    return visibleComponent;
  }
  
  protected void setVisibleComponent(Component paramComponent)
  {
    if ((visibleComponent != null) && (visibleComponent != paramComponent) && (visibleComponent.getParent() == tabPane) && (visibleComponent.isVisible())) {
      visibleComponent.setVisible(false);
    }
    if ((paramComponent != null) && (!paramComponent.isVisible())) {
      paramComponent.setVisible(true);
    }
    visibleComponent = paramComponent;
  }
  
  protected void assureRectsCreated(int paramInt)
  {
    int i = rects.length;
    if (paramInt != i)
    {
      Rectangle[] arrayOfRectangle = new Rectangle[paramInt];
      System.arraycopy(rects, 0, arrayOfRectangle, 0, Math.min(i, paramInt));
      rects = arrayOfRectangle;
      for (int j = i; j < paramInt; j++) {
        rects[j] = new Rectangle();
      }
    }
  }
  
  protected void expandTabRunsArray()
  {
    int i = tabRuns.length;
    int[] arrayOfInt = new int[i + 10];
    System.arraycopy(tabRuns, 0, arrayOfInt, 0, runCount);
    tabRuns = arrayOfInt;
  }
  
  protected int getRunForTab(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < runCount; i++)
    {
      int j = tabRuns[i];
      int k = lastTabInRun(paramInt1, i);
      if ((paramInt2 >= j) && (paramInt2 <= k)) {
        return i;
      }
    }
    return 0;
  }
  
  protected int lastTabInRun(int paramInt1, int paramInt2)
  {
    if (runCount == 1) {
      return paramInt1 - 1;
    }
    int i = paramInt2 == runCount - 1 ? 0 : paramInt2 + 1;
    if (tabRuns[i] == 0) {
      return paramInt1 - 1;
    }
    return tabRuns[i] - 1;
  }
  
  protected int getTabRunOverlay(int paramInt)
  {
    return tabRunOverlay;
  }
  
  protected int getTabRunIndent(int paramInt1, int paramInt2)
  {
    return 0;
  }
  
  protected boolean shouldPadTabRun(int paramInt1, int paramInt2)
  {
    return runCount > 1;
  }
  
  protected boolean shouldRotateTabRuns(int paramInt)
  {
    return true;
  }
  
  protected Icon getIconForTab(int paramInt)
  {
    return (!tabPane.isEnabled()) || (!tabPane.isEnabledAt(paramInt)) ? tabPane.getDisabledIconAt(paramInt) : tabPane.getIconAt(paramInt);
  }
  
  protected View getTextViewForTab(int paramInt)
  {
    if (htmlViews != null) {
      return (View)htmlViews.elementAt(paramInt);
    }
    return null;
  }
  
  protected int calculateTabHeight(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    Component localComponent = tabPane.getTabComponentAt(paramInt2);
    if (localComponent != null)
    {
      i = getPreferredSizeheight;
    }
    else
    {
      localObject = getTextViewForTab(paramInt2);
      if (localObject != null) {
        i += (int)((View)localObject).getPreferredSpan(1);
      } else {
        i += paramInt3;
      }
      Icon localIcon = getIconForTab(paramInt2);
      if (localIcon != null) {
        i = Math.max(i, localIcon.getIconHeight());
      }
    }
    Object localObject = getTabInsets(paramInt1, paramInt2);
    i += top + bottom + 2;
    return i;
  }
  
  protected int calculateMaxTabHeight(int paramInt)
  {
    FontMetrics localFontMetrics = getFontMetrics();
    int i = tabPane.getTabCount();
    int j = 0;
    int k = localFontMetrics.getHeight();
    for (int m = 0; m < i; m++) {
      j = Math.max(calculateTabHeight(paramInt, m, k), j);
    }
    return j;
  }
  
  protected int calculateTabWidth(int paramInt1, int paramInt2, FontMetrics paramFontMetrics)
  {
    Insets localInsets = getTabInsets(paramInt1, paramInt2);
    int i = left + right + 3;
    Component localComponent = tabPane.getTabComponentAt(paramInt2);
    if (localComponent != null)
    {
      i += getPreferredSizewidth;
    }
    else
    {
      Icon localIcon = getIconForTab(paramInt2);
      if (localIcon != null) {
        i += localIcon.getIconWidth() + textIconGap;
      }
      View localView = getTextViewForTab(paramInt2);
      if (localView != null)
      {
        i += (int)localView.getPreferredSpan(0);
      }
      else
      {
        String str = tabPane.getTitleAt(paramInt2);
        i += SwingUtilities2.stringWidth(tabPane, paramFontMetrics, str);
      }
    }
    return i;
  }
  
  protected int calculateMaxTabWidth(int paramInt)
  {
    FontMetrics localFontMetrics = getFontMetrics();
    int i = tabPane.getTabCount();
    int j = 0;
    for (int k = 0; k < i; k++) {
      j = Math.max(calculateTabWidth(paramInt, k, localFontMetrics), j);
    }
    return j;
  }
  
  protected int calculateTabAreaHeight(int paramInt1, int paramInt2, int paramInt3)
  {
    Insets localInsets = getTabAreaInsets(paramInt1);
    int i = getTabRunOverlay(paramInt1);
    return paramInt2 > 0 ? paramInt2 * (paramInt3 - i) + i + top + bottom : 0;
  }
  
  protected int calculateTabAreaWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    Insets localInsets = getTabAreaInsets(paramInt1);
    int i = getTabRunOverlay(paramInt1);
    return paramInt2 > 0 ? paramInt2 * (paramInt3 - i) + i + left + right : 0;
  }
  
  protected Insets getTabInsets(int paramInt1, int paramInt2)
  {
    return tabInsets;
  }
  
  protected Insets getSelectedTabPadInsets(int paramInt)
  {
    rotateInsets(selectedTabPadInsets, currentPadInsets, paramInt);
    return currentPadInsets;
  }
  
  protected Insets getTabAreaInsets(int paramInt)
  {
    rotateInsets(tabAreaInsets, currentTabAreaInsets, paramInt);
    return currentTabAreaInsets;
  }
  
  protected Insets getContentBorderInsets(int paramInt)
  {
    return contentBorderInsets;
  }
  
  protected FontMetrics getFontMetrics()
  {
    Font localFont = tabPane.getFont();
    return tabPane.getFontMetrics(localFont);
  }
  
  protected void navigateSelectedTab(int paramInt)
  {
    int i = tabPane.getTabPlacement();
    int j = DefaultLookup.getBoolean(tabPane, this, "TabbedPane.selectionFollowsFocus", true) ? tabPane.getSelectedIndex() : getFocusIndex();
    int k = tabPane.getTabCount();
    boolean bool = BasicGraphicsUtils.isLeftToRight(tabPane);
    if (k <= 0) {
      return;
    }
    int m;
    switch (i)
    {
    case 2: 
    case 4: 
      switch (paramInt)
      {
      case 12: 
        selectNextTab(j);
        break;
      case 13: 
        selectPreviousTab(j);
        break;
      case 1: 
        selectPreviousTabInRun(j);
        break;
      case 5: 
        selectNextTabInRun(j);
        break;
      case 7: 
        m = getTabRunOffset(i, k, j, false);
        selectAdjacentRunTab(i, j, m);
        break;
      case 3: 
        m = getTabRunOffset(i, k, j, true);
        selectAdjacentRunTab(i, j, m);
      }
      break;
    case 1: 
    case 3: 
    default: 
      switch (paramInt)
      {
      case 12: 
        selectNextTab(j);
        break;
      case 13: 
        selectPreviousTab(j);
        break;
      case 1: 
        m = getTabRunOffset(i, k, j, false);
        selectAdjacentRunTab(i, j, m);
        break;
      case 5: 
        m = getTabRunOffset(i, k, j, true);
        selectAdjacentRunTab(i, j, m);
        break;
      case 3: 
        if (bool) {
          selectNextTabInRun(j);
        } else {
          selectPreviousTabInRun(j);
        }
        break;
      case 7: 
        if (bool) {
          selectPreviousTabInRun(j);
        } else {
          selectNextTabInRun(j);
        }
        break;
      }
      break;
    }
  }
  
  protected void selectNextTabInRun(int paramInt)
  {
    int i = tabPane.getTabCount();
    for (int j = getNextTabIndexInRun(i, paramInt); (j != paramInt) && (!tabPane.isEnabledAt(j)); j = getNextTabIndexInRun(i, j)) {}
    navigateTo(j);
  }
  
  protected void selectPreviousTabInRun(int paramInt)
  {
    int i = tabPane.getTabCount();
    for (int j = getPreviousTabIndexInRun(i, paramInt); (j != paramInt) && (!tabPane.isEnabledAt(j)); j = getPreviousTabIndexInRun(i, j)) {}
    navigateTo(j);
  }
  
  protected void selectNextTab(int paramInt)
  {
    for (int i = getNextTabIndex(paramInt); (i != paramInt) && (!tabPane.isEnabledAt(i)); i = getNextTabIndex(i)) {}
    navigateTo(i);
  }
  
  protected void selectPreviousTab(int paramInt)
  {
    for (int i = getPreviousTabIndex(paramInt); (i != paramInt) && (!tabPane.isEnabledAt(i)); i = getPreviousTabIndex(i)) {}
    navigateTo(i);
  }
  
  protected void selectAdjacentRunTab(int paramInt1, int paramInt2, int paramInt3)
  {
    if (runCount < 2) {
      return;
    }
    Rectangle localRectangle = rects[paramInt2];
    int i;
    switch (paramInt1)
    {
    case 2: 
    case 4: 
      i = tabForCoordinate(tabPane, x + width / 2 + paramInt3, y + height / 2);
      break;
    case 1: 
    case 3: 
    default: 
      i = tabForCoordinate(tabPane, x + width / 2, y + height / 2 + paramInt3);
    }
    if (i != -1)
    {
      while ((!tabPane.isEnabledAt(i)) && (i != paramInt2)) {
        i = getNextTabIndex(i);
      }
      navigateTo(i);
    }
  }
  
  private void navigateTo(int paramInt)
  {
    if (DefaultLookup.getBoolean(tabPane, this, "TabbedPane.selectionFollowsFocus", true)) {
      tabPane.setSelectedIndex(paramInt);
    } else {
      setFocusIndex(paramInt, true);
    }
  }
  
  void setFocusIndex(int paramInt, boolean paramBoolean)
  {
    if ((paramBoolean) && (!isRunsDirty))
    {
      repaintTab(focusIndex);
      focusIndex = paramInt;
      repaintTab(focusIndex);
    }
    else
    {
      focusIndex = paramInt;
    }
  }
  
  private void repaintTab(int paramInt)
  {
    if ((!isRunsDirty) && (paramInt >= 0) && (paramInt < tabPane.getTabCount())) {
      tabPane.repaint(getTabBounds(tabPane, paramInt));
    }
  }
  
  private void validateFocusIndex()
  {
    if (focusIndex >= tabPane.getTabCount()) {
      setFocusIndex(tabPane.getSelectedIndex(), false);
    }
  }
  
  protected int getFocusIndex()
  {
    return focusIndex;
  }
  
  protected int getTabRunOffset(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = getRunForTab(paramInt2, paramInt3);
    int j;
    switch (paramInt1)
    {
    case 2: 
      if (i == 0) {
        j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, runCount, maxTabWidth) - maxTabWidth) : -maxTabWidth;
      } else if (i == runCount - 1) {
        j = paramBoolean ? maxTabWidth : calculateTabAreaWidth(paramInt1, runCount, maxTabWidth) - maxTabWidth;
      } else {
        j = paramBoolean ? maxTabWidth : -maxTabWidth;
      }
      break;
    case 4: 
      if (i == 0) {
        j = paramBoolean ? maxTabWidth : calculateTabAreaWidth(paramInt1, runCount, maxTabWidth) - maxTabWidth;
      } else if (i == runCount - 1) {
        j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, runCount, maxTabWidth) - maxTabWidth) : -maxTabWidth;
      } else {
        j = paramBoolean ? maxTabWidth : -maxTabWidth;
      }
      break;
    case 3: 
      if (i == 0) {
        j = paramBoolean ? maxTabHeight : calculateTabAreaHeight(paramInt1, runCount, maxTabHeight) - maxTabHeight;
      } else if (i == runCount - 1) {
        j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, runCount, maxTabHeight) - maxTabHeight) : -maxTabHeight;
      } else {
        j = paramBoolean ? maxTabHeight : -maxTabHeight;
      }
      break;
    case 1: 
    default: 
      if (i == 0) {
        j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, runCount, maxTabHeight) - maxTabHeight) : -maxTabHeight;
      } else if (i == runCount - 1) {
        j = paramBoolean ? maxTabHeight : calculateTabAreaHeight(paramInt1, runCount, maxTabHeight) - maxTabHeight;
      } else {
        j = paramBoolean ? maxTabHeight : -maxTabHeight;
      }
      break;
    }
    return j;
  }
  
  protected int getPreviousTabIndex(int paramInt)
  {
    int i = paramInt - 1 >= 0 ? paramInt - 1 : tabPane.getTabCount() - 1;
    return i >= 0 ? i : 0;
  }
  
  protected int getNextTabIndex(int paramInt)
  {
    return (paramInt + 1) % tabPane.getTabCount();
  }
  
  protected int getNextTabIndexInRun(int paramInt1, int paramInt2)
  {
    if (runCount < 2) {
      return getNextTabIndex(paramInt2);
    }
    int i = getRunForTab(paramInt1, paramInt2);
    int j = getNextTabIndex(paramInt2);
    if (j == tabRuns[getNextTabRun(i)]) {
      return tabRuns[i];
    }
    return j;
  }
  
  protected int getPreviousTabIndexInRun(int paramInt1, int paramInt2)
  {
    if (runCount < 2) {
      return getPreviousTabIndex(paramInt2);
    }
    int i = getRunForTab(paramInt1, paramInt2);
    if (paramInt2 == tabRuns[i])
    {
      int j = tabRuns[getNextTabRun(i)] - 1;
      return j != -1 ? j : paramInt1 - 1;
    }
    return getPreviousTabIndex(paramInt2);
  }
  
  protected int getPreviousTabRun(int paramInt)
  {
    int i = paramInt - 1 >= 0 ? paramInt - 1 : runCount - 1;
    return i >= 0 ? i : 0;
  }
  
  protected int getNextTabRun(int paramInt)
  {
    return (paramInt + 1) % runCount;
  }
  
  protected static void rotateInsets(Insets paramInsets1, Insets paramInsets2, int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
      top = left;
      left = top;
      bottom = right;
      right = bottom;
      break;
    case 3: 
      top = bottom;
      left = left;
      bottom = top;
      right = right;
      break;
    case 4: 
      top = left;
      left = bottom;
      bottom = right;
      right = top;
      break;
    case 1: 
    default: 
      top = top;
      left = left;
      bottom = bottom;
      right = right;
    }
  }
  
  boolean requestFocusForVisibleComponent()
  {
    return SwingUtilities2.tabbedPaneChangeFocusTo(getVisibleComponent());
  }
  
  private Vector<View> createHTMLVector()
  {
    Vector localVector = new Vector();
    int i = tabPane.getTabCount();
    if (i > 0) {
      for (int j = 0; j < i; j++)
      {
        String str = tabPane.getTitleAt(j);
        if (BasicHTML.isHTMLString(str)) {
          localVector.addElement(BasicHTML.createHTMLView(tabPane, str));
        } else {
          localVector.addElement(null);
        }
      }
    }
    return localVector;
  }
  
  private static class Actions
    extends UIAction
  {
    static final String NEXT = "navigateNext";
    static final String PREVIOUS = "navigatePrevious";
    static final String RIGHT = "navigateRight";
    static final String LEFT = "navigateLeft";
    static final String UP = "navigateUp";
    static final String DOWN = "navigateDown";
    static final String PAGE_UP = "navigatePageUp";
    static final String PAGE_DOWN = "navigatePageDown";
    static final String REQUEST_FOCUS = "requestFocus";
    static final String REQUEST_FOCUS_FOR_VISIBLE = "requestFocusForVisibleComponent";
    static final String SET_SELECTED = "setSelectedIndex";
    static final String SELECT_FOCUSED = "selectTabWithFocus";
    static final String SCROLL_FORWARD = "scrollTabsForwardAction";
    static final String SCROLL_BACKWARD = "scrollTabsBackwardAction";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str1 = getName();
      JTabbedPane localJTabbedPane = (JTabbedPane)paramActionEvent.getSource();
      BasicTabbedPaneUI localBasicTabbedPaneUI = (BasicTabbedPaneUI)BasicLookAndFeel.getUIOfType(localJTabbedPane.getUI(), BasicTabbedPaneUI.class);
      if (localBasicTabbedPaneUI == null) {
        return;
      }
      if (str1 == "navigateNext")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(12);
      }
      else if (str1 == "navigatePrevious")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(13);
      }
      else if (str1 == "navigateRight")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(3);
      }
      else if (str1 == "navigateLeft")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(7);
      }
      else if (str1 == "navigateUp")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(1);
      }
      else if (str1 == "navigateDown")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(5);
      }
      else
      {
        int i;
        if (str1 == "navigatePageUp")
        {
          i = localJTabbedPane.getTabPlacement();
          if ((i == 1) || (i == 3)) {
            localBasicTabbedPaneUI.navigateSelectedTab(7);
          } else {
            localBasicTabbedPaneUI.navigateSelectedTab(1);
          }
        }
        else if (str1 == "navigatePageDown")
        {
          i = localJTabbedPane.getTabPlacement();
          if ((i == 1) || (i == 3)) {
            localBasicTabbedPaneUI.navigateSelectedTab(3);
          } else {
            localBasicTabbedPaneUI.navigateSelectedTab(5);
          }
        }
        else if (str1 == "requestFocus")
        {
          localJTabbedPane.requestFocus();
        }
        else if (str1 == "requestFocusForVisibleComponent")
        {
          localBasicTabbedPaneUI.requestFocusForVisibleComponent();
        }
        else if (str1 == "setSelectedIndex")
        {
          String str2 = paramActionEvent.getActionCommand();
          if ((str2 != null) && (str2.length() > 0))
          {
            int k = paramActionEvent.getActionCommand().charAt(0);
            if ((k >= 97) && (k <= 122)) {
              k -= 32;
            }
            Integer localInteger = (Integer)mnemonicToIndexMap.get(Integer.valueOf(k));
            if ((localInteger != null) && (localJTabbedPane.isEnabledAt(localInteger.intValue()))) {
              localJTabbedPane.setSelectedIndex(localInteger.intValue());
            }
          }
        }
        else if (str1 == "selectTabWithFocus")
        {
          int j = localBasicTabbedPaneUI.getFocusIndex();
          if (j != -1) {
            localJTabbedPane.setSelectedIndex(j);
          }
        }
        else if (str1 == "scrollTabsForwardAction")
        {
          if (localBasicTabbedPaneUI.scrollableTabLayoutEnabled()) {
            tabScroller.scrollForward(localJTabbedPane.getTabPlacement());
          }
        }
        else if ((str1 == "scrollTabsBackwardAction") && (localBasicTabbedPaneUI.scrollableTabLayoutEnabled()))
        {
          tabScroller.scrollBackward(localJTabbedPane.getTabPlacement());
        }
      }
    }
  }
  
  private class CroppedEdge
    extends JPanel
    implements UIResource
  {
    private Shape shape;
    private int tabIndex;
    private int cropline;
    private int cropx;
    private int cropy;
    
    public CroppedEdge()
    {
      setOpaque(false);
    }
    
    public void setParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      tabIndex = paramInt1;
      cropline = paramInt2;
      cropx = paramInt3;
      cropy = paramInt4;
      Rectangle localRectangle = rects[paramInt1];
      setBounds(localRectangle);
      shape = BasicTabbedPaneUI.createCroppedTabShape(tabPane.getTabPlacement(), localRectangle, paramInt2);
      if ((getParent() == null) && (tabContainer != null)) {
        tabContainer.add(this, 0);
      }
    }
    
    public void resetParams()
    {
      shape = null;
      if ((getParent() == tabContainer) && (tabContainer != null)) {
        tabContainer.remove(this);
      }
    }
    
    public boolean isParamsSet()
    {
      return shape != null;
    }
    
    public int getTabIndex()
    {
      return tabIndex;
    }
    
    public int getCropline()
    {
      return cropline;
    }
    
    public int getCroppedSideWidth()
    {
      return 3;
    }
    
    private Color getBgColor()
    {
      Container localContainer = tabPane.getParent();
      if (localContainer != null)
      {
        Color localColor = localContainer.getBackground();
        if (localColor != null) {
          return localColor;
        }
      }
      return UIManager.getColor("control");
    }
    
    protected void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      if ((isParamsSet()) && ((paramGraphics instanceof Graphics2D)))
      {
        Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
        localGraphics2D.clipRect(0, 0, getWidth(), getHeight());
        localGraphics2D.setColor(getBgColor());
        localGraphics2D.translate(cropx, cropy);
        localGraphics2D.fill(shape);
        BasicTabbedPaneUI.this.paintCroppedTabEdge(paramGraphics);
        localGraphics2D.translate(-cropx, -cropy);
      }
    }
  }
  
  public class FocusHandler
    extends FocusAdapter
  {
    public FocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicTabbedPaneUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicTabbedPaneUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements ChangeListener, ContainerListener, FocusListener, MouseListener, MouseMotionListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramPropertyChangeEvent.getSource();
      String str = paramPropertyChangeEvent.getPropertyName();
      boolean bool1 = BasicTabbedPaneUI.this.scrollableTabLayoutEnabled();
      if (str == "mnemonicAt")
      {
        BasicTabbedPaneUI.this.updateMnemonics();
        localJTabbedPane.repaint();
      }
      else if (str == "displayedMnemonicIndexAt")
      {
        localJTabbedPane.repaint();
      }
      else if (str == "indexForTitle")
      {
        calculatedBaseline = false;
        Integer localInteger = (Integer)paramPropertyChangeEvent.getNewValue();
        if (htmlViews != null) {
          htmlViews.removeElementAt(localInteger.intValue());
        }
        updateHtmlViews(localInteger.intValue());
      }
      else if (str == "tabLayoutPolicy")
      {
        uninstallUI(localJTabbedPane);
        installUI(localJTabbedPane);
        calculatedBaseline = false;
      }
      else if (str == "tabPlacement")
      {
        if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
          tabScroller.createButtons();
        }
        calculatedBaseline = false;
      }
      else if ((str == "opaque") && (bool1))
      {
        boolean bool2 = ((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue();
        tabScroller.tabPanel.setOpaque(bool2);
        tabScroller.viewport.setOpaque(bool2);
      }
      else
      {
        Object localObject;
        if ((str == "background") && (bool1))
        {
          localObject = (Color)paramPropertyChangeEvent.getNewValue();
          tabScroller.tabPanel.setBackground((Color)localObject);
          tabScroller.viewport.setBackground((Color)localObject);
          Color localColor = selectedColor == null ? localObject : selectedColor;
          tabScroller.scrollForwardButton.setBackground(localColor);
          tabScroller.scrollBackwardButton.setBackground(localColor);
        }
        else if (str == "indexForTabComponent")
        {
          if (tabContainer != null) {
            BasicTabbedPaneUI.TabContainer.access$1700(tabContainer);
          }
          localObject = tabPane.getTabComponentAt(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
          if (localObject != null) {
            if (tabContainer == null) {
              BasicTabbedPaneUI.this.installTabContainer();
            } else {
              tabContainer.add((Component)localObject);
            }
          }
          tabPane.revalidate();
          tabPane.repaint();
          calculatedBaseline = false;
        }
        else if (str == "indexForNullComponent")
        {
          isRunsDirty = true;
          updateHtmlViews(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
        }
        else if (str == "font")
        {
          calculatedBaseline = false;
        }
      }
    }
    
    private void updateHtmlViews(int paramInt)
    {
      String str = tabPane.getTitleAt(paramInt);
      boolean bool = BasicHTML.isHTMLString(str);
      if (bool)
      {
        if (htmlViews == null)
        {
          htmlViews = BasicTabbedPaneUI.this.createHTMLVector();
        }
        else
        {
          View localView = BasicHTML.createHTMLView(tabPane, str);
          htmlViews.insertElementAt(localView, paramInt);
        }
      }
      else if (htmlViews != null) {
        htmlViews.insertElementAt(null, paramInt);
      }
      BasicTabbedPaneUI.this.updateMnemonics();
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramChangeEvent.getSource();
      localJTabbedPane.revalidate();
      localJTabbedPane.repaint();
      setFocusIndex(localJTabbedPane.getSelectedIndex(), false);
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
      {
        BasicTabbedPaneUI.this.ensureCurrentLayout();
        int i = localJTabbedPane.getSelectedIndex();
        if ((i < rects.length) && (i != -1)) {
          tabScroller.tabPanel.scrollRectToVisible((Rectangle)rects[i].clone());
        }
      }
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      BasicTabbedPaneUI.this.setRolloverTab(paramMouseEvent.getX(), paramMouseEvent.getY());
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      setRolloverTab(-1);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!tabPane.isEnabled()) {
        return;
      }
      int i = tabForCoordinate(tabPane, paramMouseEvent.getX(), paramMouseEvent.getY());
      if ((i >= 0) && (tabPane.isEnabledAt(i))) {
        if (i != tabPane.getSelectedIndex()) {
          tabPane.setSelectedIndex(i);
        } else if (tabPane.isRequestFocusEnabled()) {
          tabPane.requestFocus();
        }
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent) {}
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicTabbedPaneUI.this.setRolloverTab(paramMouseEvent.getX(), paramMouseEvent.getY());
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      setFocusIndex(tabPane.getSelectedIndex(), true);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicTabbedPaneUI.this.repaintTab(focusIndex);
    }
    
    public void componentAdded(ContainerEvent paramContainerEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramContainerEvent.getContainer();
      Component localComponent = paramContainerEvent.getChild();
      if ((localComponent instanceof UIResource)) {
        return;
      }
      isRunsDirty = true;
      updateHtmlViews(localJTabbedPane.indexOfComponent(localComponent));
    }
    
    public void componentRemoved(ContainerEvent paramContainerEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramContainerEvent.getContainer();
      Component localComponent = paramContainerEvent.getChild();
      if ((localComponent instanceof UIResource)) {
        return;
      }
      Integer localInteger = (Integer)localJTabbedPane.getClientProperty("__index_to_remove__");
      if (localInteger != null)
      {
        int i = localInteger.intValue();
        if ((htmlViews != null) && (htmlViews.size() > i)) {
          htmlViews.removeElementAt(i);
        }
        localJTabbedPane.putClientProperty("__index_to_remove__", null);
      }
      isRunsDirty = true;
      BasicTabbedPaneUI.this.updateMnemonics();
      BasicTabbedPaneUI.this.validateFocusIndex();
    }
  }
  
  public class MouseHandler
    extends MouseAdapter
  {
    public MouseHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicTabbedPaneUI.this.getHandler().mousePressed(paramMouseEvent);
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicTabbedPaneUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  private class ScrollableTabButton
    extends BasicArrowButton
    implements UIResource, SwingConstants
  {
    public ScrollableTabButton(int paramInt)
    {
      super(UIManager.getColor("TabbedPane.selected"), UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"), UIManager.getColor("TabbedPane.highlight"));
    }
  }
  
  private class ScrollableTabPanel
    extends JPanel
    implements UIResource
  {
    public ScrollableTabPanel()
    {
      super();
      setOpaque(tabPane.isOpaque());
      Color localColor = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (localColor == null) {
        localColor = tabPane.getBackground();
      }
      setBackground(localColor);
    }
    
    public void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      paintTabArea(paramGraphics, tabPane.getTabPlacement(), tabPane.getSelectedIndex());
      if ((tabScroller.croppedEdge.isParamsSet()) && (tabContainer == null))
      {
        Rectangle localRectangle = rects[tabScroller.croppedEdge.getTabIndex()];
        paramGraphics.translate(x, y);
        tabScroller.croppedEdge.paintComponent(paramGraphics);
        paramGraphics.translate(-x, -y);
      }
    }
    
    public void doLayout()
    {
      if (getComponentCount() > 0)
      {
        Component localComponent = getComponent(0);
        localComponent.setBounds(0, 0, getWidth(), getHeight());
      }
    }
  }
  
  private class ScrollableTabSupport
    implements ActionListener, ChangeListener
  {
    public BasicTabbedPaneUI.ScrollableTabViewport viewport = new BasicTabbedPaneUI.ScrollableTabViewport(BasicTabbedPaneUI.this);
    public BasicTabbedPaneUI.ScrollableTabPanel tabPanel = new BasicTabbedPaneUI.ScrollableTabPanel(BasicTabbedPaneUI.this);
    public JButton scrollForwardButton;
    public JButton scrollBackwardButton;
    public BasicTabbedPaneUI.CroppedEdge croppedEdge;
    public int leadingTabIndex;
    private Point tabViewPosition = new Point(0, 0);
    
    ScrollableTabSupport(int paramInt)
    {
      viewport.setView(tabPanel);
      viewport.addChangeListener(this);
      croppedEdge = new BasicTabbedPaneUI.CroppedEdge(BasicTabbedPaneUI.this);
      createButtons();
    }
    
    void createButtons()
    {
      if (scrollForwardButton != null)
      {
        tabPane.remove(scrollForwardButton);
        scrollForwardButton.removeActionListener(this);
        tabPane.remove(scrollBackwardButton);
        scrollBackwardButton.removeActionListener(this);
      }
      int i = tabPane.getTabPlacement();
      if ((i == 1) || (i == 3))
      {
        scrollForwardButton = createScrollButton(3);
        scrollBackwardButton = createScrollButton(7);
      }
      else
      {
        scrollForwardButton = createScrollButton(5);
        scrollBackwardButton = createScrollButton(1);
      }
      scrollForwardButton.addActionListener(this);
      scrollBackwardButton.addActionListener(this);
      tabPane.add(scrollForwardButton);
      tabPane.add(scrollBackwardButton);
    }
    
    public void scrollForward(int paramInt)
    {
      Dimension localDimension = viewport.getViewSize();
      Rectangle localRectangle = viewport.getViewRect();
      if ((paramInt == 1) || (paramInt == 3))
      {
        if (width < width - x) {}
      }
      else if (height >= height - y) {
        return;
      }
      setLeadingTabIndex(paramInt, leadingTabIndex + 1);
    }
    
    public void scrollBackward(int paramInt)
    {
      if (leadingTabIndex == 0) {
        return;
      }
      setLeadingTabIndex(paramInt, leadingTabIndex - 1);
    }
    
    public void setLeadingTabIndex(int paramInt1, int paramInt2)
    {
      leadingTabIndex = paramInt2;
      Dimension localDimension1 = viewport.getViewSize();
      Rectangle localRectangle = viewport.getViewRect();
      Dimension localDimension2;
      switch (paramInt1)
      {
      case 1: 
      case 3: 
        tabViewPosition.x = (leadingTabIndex == 0 ? 0 : rects[leadingTabIndex].x);
        if (width - tabViewPosition.x < width)
        {
          localDimension2 = new Dimension(width - tabViewPosition.x, height);
          viewport.setExtentSize(localDimension2);
        }
        break;
      case 2: 
      case 4: 
        tabViewPosition.y = (leadingTabIndex == 0 ? 0 : rects[leadingTabIndex].y);
        if (height - tabViewPosition.y < height)
        {
          localDimension2 = new Dimension(width, height - tabViewPosition.y);
          viewport.setExtentSize(localDimension2);
        }
        break;
      }
      viewport.setViewPosition(tabViewPosition);
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      updateView();
    }
    
    private void updateView()
    {
      int i = tabPane.getTabPlacement();
      int j = tabPane.getTabCount();
      assureRectsCreated(j);
      Rectangle localRectangle1 = viewport.getBounds();
      Dimension localDimension = viewport.getViewSize();
      Rectangle localRectangle2 = viewport.getViewRect();
      leadingTabIndex = BasicTabbedPaneUI.this.getClosestTab(x, y);
      if (leadingTabIndex + 1 < j) {
        switch (i)
        {
        case 1: 
        case 3: 
          if (rects[leadingTabIndex].x < x) {
            leadingTabIndex += 1;
          }
          break;
        case 2: 
        case 4: 
          if (rects[leadingTabIndex].y < y) {
            leadingTabIndex += 1;
          }
          break;
        }
      }
      Insets localInsets = getContentBorderInsets(i);
      switch (i)
      {
      case 2: 
        tabPane.repaint(x + width, y, left, height);
        scrollBackwardButton.setEnabled((y > 0) && (leadingTabIndex > 0));
        scrollForwardButton.setEnabled((leadingTabIndex < j - 1) && (height - y > height));
        break;
      case 4: 
        tabPane.repaint(x - right, y, right, height);
        scrollBackwardButton.setEnabled((y > 0) && (leadingTabIndex > 0));
        scrollForwardButton.setEnabled((leadingTabIndex < j - 1) && (height - y > height));
        break;
      case 3: 
        tabPane.repaint(x, y - bottom, width, bottom);
        scrollBackwardButton.setEnabled((x > 0) && (leadingTabIndex > 0));
        scrollForwardButton.setEnabled((leadingTabIndex < j - 1) && (width - x > width));
        break;
      case 1: 
      default: 
        tabPane.repaint(x, y + height, width, top);
        scrollBackwardButton.setEnabled((x > 0) && (leadingTabIndex > 0));
        scrollForwardButton.setEnabled((leadingTabIndex < j - 1) && (width - x > width));
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      ActionMap localActionMap = tabPane.getActionMap();
      if (localActionMap != null)
      {
        String str;
        if (paramActionEvent.getSource() == scrollForwardButton) {
          str = "scrollTabsForwardAction";
        } else {
          str = "scrollTabsBackwardAction";
        }
        Action localAction = localActionMap.get(str);
        if ((localAction != null) && (localAction.isEnabled())) {
          localAction.actionPerformed(new ActionEvent(tabPane, 1001, null, paramActionEvent.getWhen(), paramActionEvent.getModifiers()));
        }
      }
    }
    
    public String toString()
    {
      return "viewport.viewSize=" + viewport.getViewSize() + "\nviewport.viewRectangle=" + viewport.getViewRect() + "\nleadingTabIndex=" + leadingTabIndex + "\ntabViewPosition=" + tabViewPosition;
    }
  }
  
  private class ScrollableTabViewport
    extends JViewport
    implements UIResource
  {
    public ScrollableTabViewport()
    {
      setName("TabbedPane.scrollableViewport");
      setScrollMode(0);
      setOpaque(tabPane.isOpaque());
      Color localColor = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (localColor == null) {
        localColor = tabPane.getBackground();
      }
      setBackground(localColor);
    }
  }
  
  private class TabContainer
    extends JPanel
    implements UIResource
  {
    private boolean notifyTabbedPane = true;
    
    public TabContainer()
    {
      super();
      setOpaque(false);
    }
    
    public void remove(Component paramComponent)
    {
      int i = tabPane.indexOfTabComponent(paramComponent);
      super.remove(paramComponent);
      if ((notifyTabbedPane) && (i != -1)) {
        tabPane.setTabComponentAt(i, null);
      }
    }
    
    private void removeUnusedTabComponents()
    {
      for (Component localComponent : getComponents()) {
        if (!(localComponent instanceof UIResource))
        {
          int k = tabPane.indexOfTabComponent(localComponent);
          if (k == -1) {
            super.remove(localComponent);
          }
        }
      }
    }
    
    public boolean isOptimizedDrawingEnabled()
    {
      return (tabScroller != null) && (!tabScroller.croppedEdge.isParamsSet());
    }
    
    public void doLayout()
    {
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
      {
        tabScroller.tabPanel.repaint();
        tabScroller.updateView();
      }
      else
      {
        tabPane.repaint(getBounds());
      }
    }
  }
  
  public class TabSelectionHandler
    implements ChangeListener
  {
    public TabSelectionHandler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicTabbedPaneUI.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
  
  public class TabbedPaneLayout
    implements LayoutManager
  {
    public TabbedPaneLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return calculateSize(false);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return calculateSize(true);
    }
    
    protected Dimension calculateSize(boolean paramBoolean)
    {
      int i = tabPane.getTabPlacement();
      Insets localInsets1 = tabPane.getInsets();
      Insets localInsets2 = getContentBorderInsets(i);
      Insets localInsets3 = getTabAreaInsets(i);
      Dimension localDimension1 = new Dimension(0, 0);
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      for (int i1 = 0; i1 < tabPane.getTabCount(); i1++)
      {
        Component localComponent = tabPane.getComponentAt(i1);
        if (localComponent != null)
        {
          Dimension localDimension2 = paramBoolean ? localComponent.getMinimumSize() : localComponent.getPreferredSize();
          if (localDimension2 != null)
          {
            n = Math.max(height, n);
            m = Math.max(width, m);
          }
        }
      }
      k += m;
      j += n;
      switch (i)
      {
      case 2: 
      case 4: 
        j = Math.max(j, calculateMaxTabHeight(i));
        i1 = preferredTabAreaWidth(i, j - top - bottom);
        k += i1;
        break;
      case 1: 
      case 3: 
      default: 
        k = Math.max(k, calculateMaxTabWidth(i));
        i1 = preferredTabAreaHeight(i, k - left - right);
        j += i1;
      }
      return new Dimension(k + left + right + left + right, j + bottom + top + top + bottom);
    }
    
    protected int preferredTabAreaHeight(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = getFontMetrics();
      int i = tabPane.getTabCount();
      int j = 0;
      if (i > 0)
      {
        int k = 1;
        int m = 0;
        int n = calculateMaxTabHeight(paramInt1);
        for (int i1 = 0; i1 < i; i1++)
        {
          int i2 = calculateTabWidth(paramInt1, i1, localFontMetrics);
          if ((m != 0) && (m + i2 > paramInt2))
          {
            k++;
            m = 0;
          }
          m += i2;
        }
        j = calculateTabAreaHeight(paramInt1, k, n);
      }
      return j;
    }
    
    protected int preferredTabAreaWidth(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = getFontMetrics();
      int i = tabPane.getTabCount();
      int j = 0;
      if (i > 0)
      {
        int k = 1;
        int m = 0;
        int n = localFontMetrics.getHeight();
        maxTabWidth = calculateMaxTabWidth(paramInt1);
        for (int i1 = 0; i1 < i; i1++)
        {
          int i2 = calculateTabHeight(paramInt1, i1, n);
          if ((m != 0) && (m + i2 > paramInt2))
          {
            k++;
            m = 0;
          }
          m += i2;
        }
        j = calculateTabAreaWidth(paramInt1, k, maxTabWidth);
      }
      return j;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      setRolloverTab(-1);
      int i = tabPane.getTabPlacement();
      Insets localInsets1 = tabPane.getInsets();
      int j = tabPane.getSelectedIndex();
      Component localComponent1 = getVisibleComponent();
      calculateLayoutInfo();
      Component localComponent2 = null;
      if (j < 0)
      {
        if (localComponent1 != null) {
          setVisibleComponent(null);
        }
      }
      else {
        localComponent2 = tabPane.getComponentAt(j);
      }
      int i2 = 0;
      int i3 = 0;
      Insets localInsets2 = getContentBorderInsets(i);
      int i4 = 0;
      if (localComponent2 != null)
      {
        if ((localComponent2 != localComponent1) && (localComponent1 != null) && (SwingUtilities.findFocusOwner(localComponent1) != null)) {
          i4 = 1;
        }
        setVisibleComponent(localComponent2);
      }
      Rectangle localRectangle = tabPane.getBounds();
      int i5 = tabPane.getComponentCount();
      if (i5 > 0)
      {
        int k;
        int m;
        switch (i)
        {
        case 2: 
          i2 = calculateTabAreaWidth(i, runCount, maxTabWidth);
          k = left + i2 + left;
          m = top + top;
          break;
        case 4: 
          i2 = calculateTabAreaWidth(i, runCount, maxTabWidth);
          k = left + left;
          m = top + top;
          break;
        case 3: 
          i3 = calculateTabAreaHeight(i, runCount, maxTabHeight);
          k = left + left;
          m = top + top;
          break;
        case 1: 
        default: 
          i3 = calculateTabAreaHeight(i, runCount, maxTabHeight);
          k = left + left;
          m = top + i3 + top;
        }
        int n = width - i2 - left - right - left - right;
        int i1 = height - i3 - top - bottom - top - bottom;
        for (int i6 = 0; i6 < i5; i6++)
        {
          Component localComponent3 = tabPane.getComponent(i6);
          if (localComponent3 == tabContainer)
          {
            int i7 = i2 == 0 ? width : i2 + left + right + left + right;
            int i8 = i3 == 0 ? height : i3 + top + bottom + top + bottom;
            int i9 = 0;
            int i10 = 0;
            if (i == 3) {
              i10 = height - i8;
            } else if (i == 4) {
              i9 = width - i7;
            }
            localComponent3.setBounds(i9, i10, i7, i8);
          }
          else
          {
            localComponent3.setBounds(k, m, n, i1);
          }
        }
      }
      layoutTabComponents();
      if ((i4 != 0) && (!requestFocusForVisibleComponent())) {
        tabPane.requestFocus();
      }
    }
    
    public void calculateLayoutInfo()
    {
      int i = tabPane.getTabCount();
      assureRectsCreated(i);
      calculateTabRects(tabPane.getTabPlacement(), i);
      isRunsDirty = false;
    }
    
    private void layoutTabComponents()
    {
      if (tabContainer == null) {
        return;
      }
      Rectangle localRectangle = new Rectangle();
      Point localPoint = new Point(-tabContainer.getX(), -tabContainer.getY());
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
        BasicTabbedPaneUI.this.translatePointToTabPanel(0, 0, localPoint);
      }
      for (int i = 0; i < tabPane.getTabCount(); i++)
      {
        Component localComponent = tabPane.getTabComponentAt(i);
        if (localComponent != null)
        {
          getTabBounds(i, localRectangle);
          Dimension localDimension = localComponent.getPreferredSize();
          Insets localInsets = getTabInsets(tabPane.getTabPlacement(), i);
          int j = x + left + x;
          int k = y + top + y;
          int m = width - left - right;
          int n = height - top - bottom;
          int i1 = j + (m - width) / 2;
          int i2 = k + (n - height) / 2;
          int i3 = tabPane.getTabPlacement();
          boolean bool = i == tabPane.getSelectedIndex();
          localComponent.setBounds(i1 + getTabLabelShiftX(i3, i, bool), i2 + getTabLabelShiftY(i3, i, bool), width, height);
        }
      }
    }
    
    protected void calculateTabRects(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = getFontMetrics();
      Dimension localDimension = tabPane.getSize();
      Insets localInsets1 = tabPane.getInsets();
      Insets localInsets2 = getTabAreaInsets(paramInt1);
      int i = localFontMetrics.getHeight();
      int j = tabPane.getSelectedIndex();
      int i4 = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
      boolean bool = BasicGraphicsUtils.isLeftToRight(tabPane);
      int i1;
      int i2;
      int i3;
      switch (paramInt1)
      {
      case 2: 
        maxTabWidth = calculateMaxTabWidth(paramInt1);
        i1 = left + left;
        i2 = top + top;
        i3 = height - (bottom + bottom);
        break;
      case 4: 
        maxTabWidth = calculateMaxTabWidth(paramInt1);
        i1 = width - right - right - maxTabWidth;
        i2 = top + top;
        i3 = height - (bottom + bottom);
        break;
      case 3: 
        maxTabHeight = calculateMaxTabHeight(paramInt1);
        i1 = left + left;
        i2 = height - bottom - bottom - maxTabHeight;
        i3 = width - (right + right);
        break;
      case 1: 
      default: 
        maxTabHeight = calculateMaxTabHeight(paramInt1);
        i1 = left + left;
        i2 = top + top;
        i3 = width - (right + right);
      }
      int k = getTabRunOverlay(paramInt1);
      runCount = 0;
      selectedRun = -1;
      if (paramInt2 == 0) {
        return;
      }
      Rectangle localRectangle;
      for (int m = 0; m < paramInt2; m++)
      {
        localRectangle = rects[m];
        if (i4 == 0)
        {
          if (m > 0)
          {
            x = (rects[(m - 1)].x + rects[(m - 1)].width);
          }
          else
          {
            tabRuns[0] = 0;
            runCount = 1;
            maxTabWidth = 0;
            x = i1;
          }
          width = calculateTabWidth(paramInt1, m, localFontMetrics);
          maxTabWidth = Math.max(maxTabWidth, width);
          if ((x != i1) && (x + width > i3))
          {
            if (runCount > tabRuns.length - 1) {
              expandTabRunsArray();
            }
            tabRuns[runCount] = m;
            runCount += 1;
            x = i1;
          }
          y = i2;
          height = maxTabHeight;
        }
        else
        {
          if (m > 0)
          {
            y = (rects[(m - 1)].y + rects[(m - 1)].height);
          }
          else
          {
            tabRuns[0] = 0;
            runCount = 1;
            maxTabHeight = 0;
            y = i2;
          }
          height = calculateTabHeight(paramInt1, m, i);
          maxTabHeight = Math.max(maxTabHeight, height);
          if ((y != i2) && (y + height > i3))
          {
            if (runCount > tabRuns.length - 1) {
              expandTabRunsArray();
            }
            tabRuns[runCount] = m;
            runCount += 1;
            y = i2;
          }
          x = i1;
          width = maxTabWidth;
        }
        if (m == j) {
          selectedRun = (runCount - 1);
        }
      }
      if (runCount > 1)
      {
        normalizeTabRuns(paramInt1, paramInt2, i4 != 0 ? i2 : i1, i3);
        selectedRun = getRunForTab(paramInt2, j);
        if (shouldRotateTabRuns(paramInt1)) {
          rotateTabRuns(paramInt1, selectedRun);
        }
      }
      int i5;
      for (m = runCount - 1; m >= 0; m--)
      {
        i5 = tabRuns[m];
        int i6 = tabRuns[(m + 1)];
        int i7 = i6 != 0 ? i6 - 1 : paramInt2 - 1;
        int n;
        if (i4 == 0)
        {
          for (n = i5; n <= i7; n++)
          {
            localRectangle = rects[n];
            y = i2;
            x += getTabRunIndent(paramInt1, m);
          }
          if (shouldPadTabRun(paramInt1, m)) {
            padTabRun(paramInt1, i5, i7, i3);
          }
          if (paramInt1 == 3) {
            i2 -= maxTabHeight - k;
          } else {
            i2 += maxTabHeight - k;
          }
        }
        else
        {
          for (n = i5; n <= i7; n++)
          {
            localRectangle = rects[n];
            x = i1;
            y += getTabRunIndent(paramInt1, m);
          }
          if (shouldPadTabRun(paramInt1, m)) {
            padTabRun(paramInt1, i5, i7, i3);
          }
          if (paramInt1 == 4) {
            i1 -= maxTabWidth - k;
          } else {
            i1 += maxTabWidth - k;
          }
        }
      }
      padSelectedTab(paramInt1, j);
      if ((!bool) && (i4 == 0))
      {
        i5 = width - (right + right);
        for (m = 0; m < paramInt2; m++) {
          rects[m].x = (i5 - rects[m].x - rects[m].width);
        }
      }
    }
    
    protected void rotateTabRuns(int paramInt1, int paramInt2)
    {
      for (int i = 0; i < paramInt2; i++)
      {
        int j = tabRuns[0];
        for (int k = 1; k < runCount; k++) {
          tabRuns[(k - 1)] = tabRuns[k];
        }
        tabRuns[(runCount - 1)] = j;
      }
    }
    
    protected void normalizeTabRuns(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
      int j = runCount - 1;
      int k = 1;
      double d = 1.25D;
      while (k != 0)
      {
        int m = lastTabInRun(paramInt2, j);
        int n = lastTabInRun(paramInt2, j - 1);
        int i1;
        int i2;
        if (i == 0)
        {
          i1 = rects[m].x + rects[m].width;
          i2 = (int)(maxTabWidth * d);
        }
        else
        {
          i1 = rects[m].y + rects[m].height;
          i2 = (int)(maxTabHeight * d * 2.0D);
        }
        if (paramInt4 - i1 > i2)
        {
          tabRuns[j] = n;
          if (i == 0) {
            rects[n].x = paramInt3;
          } else {
            rects[n].y = paramInt3;
          }
          for (int i3 = n + 1; i3 <= m; i3++) {
            if (i == 0) {
              rects[i3].x = (rects[(i3 - 1)].x + rects[(i3 - 1)].width);
            } else {
              rects[i3].y = (rects[(i3 - 1)].y + rects[(i3 - 1)].height);
            }
          }
        }
        else if (j == runCount - 1)
        {
          k = 0;
        }
        if (j - 1 > 0)
        {
          j--;
        }
        else
        {
          j = runCount - 1;
          d += 0.25D;
        }
      }
    }
    
    protected void padTabRun(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rectangle localRectangle1 = rects[paramInt3];
      int i;
      int j;
      float f;
      int k;
      Rectangle localRectangle2;
      if ((paramInt1 == 1) || (paramInt1 == 3))
      {
        i = x + width - rects[paramInt2].x;
        j = paramInt4 - (x + width);
        f = j / i;
        for (k = paramInt2; k <= paramInt3; k++)
        {
          localRectangle2 = rects[k];
          if (k > paramInt2) {
            x = (rects[(k - 1)].x + rects[(k - 1)].width);
          }
          width += Math.round(width * f);
        }
        width = (paramInt4 - x);
      }
      else
      {
        i = y + height - rects[paramInt2].y;
        j = paramInt4 - (y + height);
        f = j / i;
        for (k = paramInt2; k <= paramInt3; k++)
        {
          localRectangle2 = rects[k];
          if (k > paramInt2) {
            y = (rects[(k - 1)].y + rects[(k - 1)].height);
          }
          height += Math.round(height * f);
        }
        height = (paramInt4 - y);
      }
    }
    
    protected void padSelectedTab(int paramInt1, int paramInt2)
    {
      if (paramInt2 >= 0)
      {
        Rectangle localRectangle = rects[paramInt2];
        Insets localInsets1 = getSelectedTabPadInsets(paramInt1);
        x -= left;
        width += left + right;
        y -= top;
        height += top + bottom;
        if (!BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
        {
          Dimension localDimension = tabPane.getSize();
          Insets localInsets2 = tabPane.getInsets();
          int i;
          int j;
          if ((paramInt1 == 2) || (paramInt1 == 4))
          {
            i = top - y;
            if (i > 0)
            {
              y += i;
              height -= i;
            }
            j = y + height + bottom - height;
            if (j > 0) {
              height -= j;
            }
          }
          else
          {
            i = left - x;
            if (i > 0)
            {
              x += i;
              width -= i;
            }
            j = x + width + right - width;
            if (j > 0) {
              width -= j;
            }
          }
        }
      }
    }
  }
  
  private class TabbedPaneScrollLayout
    extends BasicTabbedPaneUI.TabbedPaneLayout
  {
    private TabbedPaneScrollLayout()
    {
      super();
    }
    
    protected int preferredTabAreaHeight(int paramInt1, int paramInt2)
    {
      return calculateMaxTabHeight(paramInt1);
    }
    
    protected int preferredTabAreaWidth(int paramInt1, int paramInt2)
    {
      return calculateMaxTabWidth(paramInt1);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      setRolloverTab(-1);
      int i = tabPane.getTabPlacement();
      int j = tabPane.getTabCount();
      Insets localInsets1 = tabPane.getInsets();
      int k = tabPane.getSelectedIndex();
      Component localComponent1 = getVisibleComponent();
      calculateLayoutInfo();
      Component localComponent2 = null;
      if (k < 0)
      {
        if (localComponent1 != null) {
          setVisibleComponent(null);
        }
      }
      else {
        localComponent2 = tabPane.getComponentAt(k);
      }
      if (tabPane.getTabCount() == 0)
      {
        tabScroller.croppedEdge.resetParams();
        tabScroller.scrollForwardButton.setVisible(false);
        tabScroller.scrollBackwardButton.setVisible(false);
        return;
      }
      int m = 0;
      if (localComponent2 != null)
      {
        if ((localComponent2 != localComponent1) && (localComponent1 != null) && (SwingUtilities.findFocusOwner(localComponent1) != null)) {
          m = 1;
        }
        setVisibleComponent(localComponent2);
      }
      Insets localInsets2 = getContentBorderInsets(i);
      Rectangle localRectangle = tabPane.getBounds();
      int i8 = tabPane.getComponentCount();
      if (i8 > 0)
      {
        int i2;
        int i3;
        int n;
        int i1;
        int i4;
        int i5;
        int i6;
        int i7;
        switch (i)
        {
        case 2: 
          i2 = calculateTabAreaWidth(i, runCount, maxTabWidth);
          i3 = height - top - bottom;
          n = left;
          i1 = top;
          i4 = n + i2 + left;
          i5 = i1 + top;
          i6 = width - left - right - i2 - left - right;
          i7 = height - top - bottom - top - bottom;
          break;
        case 4: 
          i2 = calculateTabAreaWidth(i, runCount, maxTabWidth);
          i3 = height - top - bottom;
          n = width - right - i2;
          i1 = top;
          i4 = left + left;
          i5 = top + top;
          i6 = width - left - right - i2 - left - right;
          i7 = height - top - bottom - top - bottom;
          break;
        case 3: 
          i2 = width - left - right;
          i3 = calculateTabAreaHeight(i, runCount, maxTabHeight);
          n = left;
          i1 = height - bottom - i3;
          i4 = left + left;
          i5 = top + top;
          i6 = width - left - right - left - right;
          i7 = height - top - bottom - i3 - top - bottom;
          break;
        case 1: 
        default: 
          i2 = width - left - right;
          i3 = calculateTabAreaHeight(i, runCount, maxTabHeight);
          n = left;
          i1 = top;
          i4 = n + left;
          i5 = i1 + i3 + top;
          i6 = width - left - right - left - right;
          i7 = height - top - bottom - i3 - top - bottom;
        }
        for (int i9 = 0; i9 < i8; i9++)
        {
          Component localComponent3 = tabPane.getComponent(i9);
          Object localObject1;
          Object localObject2;
          int i10;
          int i11;
          int i13;
          int i14;
          if ((tabScroller != null) && (localComponent3 == tabScroller.viewport))
          {
            localObject1 = (JViewport)localComponent3;
            localObject2 = ((JViewport)localObject1).getViewRect();
            i10 = i2;
            i11 = i3;
            Dimension localDimension = tabScroller.scrollForwardButton.getPreferredSize();
            switch (i)
            {
            case 2: 
            case 4: 
              i13 = rects[(j - 1)].y + rects[(j - 1)].height;
              if (i13 > i3)
              {
                i11 = i3 > 2 * height ? i3 - 2 * height : 0;
                if (i13 - y <= i11) {
                  i11 = i13 - y;
                }
              }
              break;
            case 1: 
            case 3: 
            default: 
              i14 = rects[(j - 1)].x + rects[(j - 1)].width;
              if (i14 > i2)
              {
                i10 = i2 > 2 * width ? i2 - 2 * width : 0;
                if (i14 - x <= i10) {
                  i10 = i14 - x;
                }
              }
              break;
            }
            localComponent3.setBounds(n, i1, i10, i11);
          }
          else if ((tabScroller != null) && ((localComponent3 == tabScroller.scrollForwardButton) || (localComponent3 == tabScroller.scrollBackwardButton)))
          {
            localObject1 = localComponent3;
            localObject2 = ((Component)localObject1).getPreferredSize();
            i10 = 0;
            i11 = 0;
            int i12 = width;
            i13 = height;
            i14 = 0;
            switch (i)
            {
            case 2: 
            case 4: 
              int i15 = rects[(j - 1)].y + rects[(j - 1)].height;
              if (i15 > i3)
              {
                i14 = 1;
                i10 = i == 2 ? n + i2 - width : n;
                i11 = localComponent3 == tabScroller.scrollForwardButton ? height - bottom - height : height - bottom - 2 * height;
              }
              break;
            case 1: 
            case 3: 
            default: 
              int i16 = rects[(j - 1)].x + rects[(j - 1)].width;
              if (i16 > i2)
              {
                i14 = 1;
                i10 = localComponent3 == tabScroller.scrollForwardButton ? width - left - width : width - left - 2 * width;
                i11 = i == 1 ? i1 + i3 - height : i1;
              }
              break;
            }
            localComponent3.setVisible(i14);
            if (i14 != 0) {
              localComponent3.setBounds(i10, i11, i12, i13);
            }
          }
          else
          {
            localComponent3.setBounds(i4, i5, i6, i7);
          }
        }
        super.layoutTabComponents();
        layoutCroppedEdge();
        if ((m != 0) && (!requestFocusForVisibleComponent())) {
          tabPane.requestFocus();
        }
      }
    }
    
    private void layoutCroppedEdge()
    {
      tabScroller.croppedEdge.resetParams();
      Rectangle localRectangle1 = tabScroller.viewport.getViewRect();
      for (int j = 0; j < rects.length; j++)
      {
        Rectangle localRectangle2 = rects[j];
        int i;
        switch (tabPane.getTabPlacement())
        {
        case 2: 
        case 4: 
          i = y + height;
          if ((y < i) && (y + height > i)) {
            tabScroller.croppedEdge.setParams(j, i - y - 1, -currentTabAreaInsets.left, 0);
          }
          break;
        case 1: 
        case 3: 
        default: 
          i = x + width;
          if ((x < i - 1) && (x + width > i)) {
            tabScroller.croppedEdge.setParams(j, i - x - 1, 0, -currentTabAreaInsets.top);
          }
          break;
        }
      }
    }
    
    protected void calculateTabRects(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = getFontMetrics();
      Dimension localDimension = tabPane.getSize();
      Insets localInsets1 = tabPane.getInsets();
      Insets localInsets2 = getTabAreaInsets(paramInt1);
      int i = localFontMetrics.getHeight();
      int j = tabPane.getSelectedIndex();
      int m = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
      boolean bool = BasicGraphicsUtils.isLeftToRight(tabPane);
      int n = left;
      int i1 = top;
      int i2 = 0;
      int i3 = 0;
      switch (paramInt1)
      {
      case 2: 
      case 4: 
        maxTabWidth = calculateMaxTabWidth(paramInt1);
        break;
      case 1: 
      case 3: 
      default: 
        maxTabHeight = calculateMaxTabHeight(paramInt1);
      }
      runCount = 0;
      selectedRun = -1;
      if (paramInt2 == 0) {
        return;
      }
      selectedRun = 0;
      runCount = 1;
      for (int k = 0; k < paramInt2; k++)
      {
        Rectangle localRectangle = rects[k];
        if (m == 0)
        {
          if (k > 0)
          {
            x = (rects[(k - 1)].x + rects[(k - 1)].width);
          }
          else
          {
            tabRuns[0] = 0;
            maxTabWidth = 0;
            i3 += maxTabHeight;
            x = n;
          }
          width = calculateTabWidth(paramInt1, k, localFontMetrics);
          i2 = x + width;
          maxTabWidth = Math.max(maxTabWidth, width);
          y = i1;
          height = maxTabHeight;
        }
        else
        {
          if (k > 0)
          {
            y = (rects[(k - 1)].y + rects[(k - 1)].height);
          }
          else
          {
            tabRuns[0] = 0;
            maxTabHeight = 0;
            i2 = maxTabWidth;
            y = i1;
          }
          height = calculateTabHeight(paramInt1, k, i);
          i3 = y + height;
          maxTabHeight = Math.max(maxTabHeight, height);
          x = n;
          width = maxTabWidth;
        }
      }
      if (tabsOverlapBorder) {
        padSelectedTab(paramInt1, j);
      }
      if ((!bool) && (m == 0))
      {
        int i4 = width - (right + right);
        for (k = 0; k < paramInt2; k++) {
          rects[k].x = (i4 - rects[k].x - rects[k].width);
        }
      }
      tabScroller.tabPanel.setPreferredSize(new Dimension(i2, i3));
      tabScroller.tabPanel.invalidate();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */