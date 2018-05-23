package javax.swing.plaf.basic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicSplitPaneUI
  extends SplitPaneUI
{
  protected static final String NON_CONTINUOUS_DIVIDER = "nonContinuousDivider";
  protected static int KEYBOARD_DIVIDER_MOVE_OFFSET = 3;
  protected JSplitPane splitPane;
  protected BasicHorizontalLayoutManager layoutManager;
  protected BasicSplitPaneDivider divider;
  protected PropertyChangeListener propertyChangeListener;
  protected FocusListener focusListener;
  private Handler handler;
  private Set<KeyStroke> managingFocusForwardTraversalKeys;
  private Set<KeyStroke> managingFocusBackwardTraversalKeys;
  protected int dividerSize;
  protected Component nonContinuousLayoutDivider;
  protected boolean draggingHW;
  protected int beginDragDividerLocation;
  @Deprecated
  protected KeyStroke upKey;
  @Deprecated
  protected KeyStroke downKey;
  @Deprecated
  protected KeyStroke leftKey;
  @Deprecated
  protected KeyStroke rightKey;
  @Deprecated
  protected KeyStroke homeKey;
  @Deprecated
  protected KeyStroke endKey;
  @Deprecated
  protected KeyStroke dividerResizeToggleKey;
  @Deprecated
  protected ActionListener keyboardUpLeftListener;
  @Deprecated
  protected ActionListener keyboardDownRightListener;
  @Deprecated
  protected ActionListener keyboardHomeListener;
  @Deprecated
  protected ActionListener keyboardEndListener;
  @Deprecated
  protected ActionListener keyboardResizeToggleListener;
  private int orientation;
  private int lastDragLocation;
  private boolean continuousLayout;
  private boolean dividerKeyboardResize;
  private boolean dividerLocationIsSet;
  private Color dividerDraggingColor;
  private boolean rememberPaneSizes;
  private boolean keepHidden = false;
  boolean painted;
  boolean ignoreDividerLocationChange;
  
  public BasicSplitPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicSplitPaneUI();
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("negativeIncrement"));
    paramLazyActionMap.put(new Actions("positiveIncrement"));
    paramLazyActionMap.put(new Actions("selectMin"));
    paramLazyActionMap.put(new Actions("selectMax"));
    paramLazyActionMap.put(new Actions("startResize"));
    paramLazyActionMap.put(new Actions("toggleFocus"));
    paramLazyActionMap.put(new Actions("focusOutForward"));
    paramLazyActionMap.put(new Actions("focusOutBackward"));
  }
  
  public void installUI(JComponent paramJComponent)
  {
    splitPane = ((JSplitPane)paramJComponent);
    dividerLocationIsSet = false;
    dividerKeyboardResize = false;
    keepHidden = false;
    installDefaults();
    installListeners();
    installKeyboardActions();
    setLastDragLocation(-1);
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installBorder(splitPane, "SplitPane.border");
    LookAndFeel.installColors(splitPane, "SplitPane.background", "SplitPane.foreground");
    LookAndFeel.installProperty(splitPane, "opaque", Boolean.TRUE);
    if (divider == null) {
      divider = createDefaultDivider();
    }
    divider.setBasicSplitPaneUI(this);
    Border localBorder = divider.getBorder();
    if ((localBorder == null) || (!(localBorder instanceof UIResource))) {
      divider.setBorder(UIManager.getBorder("SplitPaneDivider.border"));
    }
    dividerDraggingColor = UIManager.getColor("SplitPaneDivider.draggingColor");
    setOrientation(splitPane.getOrientation());
    Integer localInteger = (Integer)UIManager.get("SplitPane.dividerSize");
    LookAndFeel.installProperty(splitPane, "dividerSize", Integer.valueOf(localInteger == null ? 10 : localInteger.intValue()));
    divider.setDividerSize(splitPane.getDividerSize());
    dividerSize = divider.getDividerSize();
    splitPane.add(divider, "divider");
    setContinuousLayout(splitPane.isContinuousLayout());
    resetLayoutManager();
    if (nonContinuousLayoutDivider == null) {
      setNonContinuousLayoutDivider(createDefaultNonContinuousLayoutDivider(), true);
    } else {
      setNonContinuousLayoutDivider(nonContinuousLayoutDivider, true);
    }
    if (managingFocusForwardTraversalKeys == null)
    {
      managingFocusForwardTraversalKeys = new HashSet();
      managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
    }
    splitPane.setFocusTraversalKeys(0, managingFocusForwardTraversalKeys);
    if (managingFocusBackwardTraversalKeys == null)
    {
      managingFocusBackwardTraversalKeys = new HashSet();
      managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
    }
    splitPane.setFocusTraversalKeys(1, managingFocusBackwardTraversalKeys);
  }
  
  protected void installListeners()
  {
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      splitPane.addPropertyChangeListener(propertyChangeListener);
    }
    if ((focusListener = createFocusListener()) != null) {
      splitPane.addFocusListener(focusListener);
    }
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(splitPane, 1, localInputMap);
    LazyActionMap.installLazyActionMap(splitPane, BasicSplitPaneUI.class, "SplitPane.actionMap");
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(splitPane, this, "SplitPane.ancestorInputMap");
    }
    return null;
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDefaults();
    dividerLocationIsSet = false;
    dividerKeyboardResize = false;
    splitPane = null;
  }
  
  protected void uninstallDefaults()
  {
    if (splitPane.getLayout() == layoutManager) {
      splitPane.setLayout(null);
    }
    if (nonContinuousLayoutDivider != null) {
      splitPane.remove(nonContinuousLayoutDivider);
    }
    LookAndFeel.uninstallBorder(splitPane);
    Border localBorder = divider.getBorder();
    if ((localBorder instanceof UIResource)) {
      divider.setBorder(null);
    }
    splitPane.remove(divider);
    divider.setBasicSplitPaneUI(null);
    layoutManager = null;
    divider = null;
    nonContinuousLayoutDivider = null;
    setNonContinuousLayoutDivider(null);
    splitPane.setFocusTraversalKeys(0, null);
    splitPane.setFocusTraversalKeys(1, null);
  }
  
  protected void uninstallListeners()
  {
    if (propertyChangeListener != null)
    {
      splitPane.removePropertyChangeListener(propertyChangeListener);
      propertyChangeListener = null;
    }
    if (focusListener != null)
    {
      splitPane.removeFocusListener(focusListener);
      focusListener = null;
    }
    keyboardUpLeftListener = null;
    keyboardDownRightListener = null;
    keyboardHomeListener = null;
    keyboardEndListener = null;
    keyboardResizeToggleListener = null;
    handler = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(splitPane, null);
    SwingUtilities.replaceUIInputMap(splitPane, 1, null);
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
  
  protected FocusListener createFocusListener()
  {
    return getHandler();
  }
  
  @Deprecated
  protected ActionListener createKeyboardUpLeftListener()
  {
    return new KeyboardUpLeftHandler();
  }
  
  @Deprecated
  protected ActionListener createKeyboardDownRightListener()
  {
    return new KeyboardDownRightHandler();
  }
  
  @Deprecated
  protected ActionListener createKeyboardHomeListener()
  {
    return new KeyboardHomeHandler();
  }
  
  @Deprecated
  protected ActionListener createKeyboardEndListener()
  {
    return new KeyboardEndHandler();
  }
  
  @Deprecated
  protected ActionListener createKeyboardResizeToggleListener()
  {
    return new KeyboardResizeToggleHandler();
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setOrientation(int paramInt)
  {
    orientation = paramInt;
  }
  
  public boolean isContinuousLayout()
  {
    return continuousLayout;
  }
  
  public void setContinuousLayout(boolean paramBoolean)
  {
    continuousLayout = paramBoolean;
  }
  
  public int getLastDragLocation()
  {
    return lastDragLocation;
  }
  
  public void setLastDragLocation(int paramInt)
  {
    lastDragLocation = paramInt;
  }
  
  int getKeyboardMoveIncrement()
  {
    return 3;
  }
  
  public BasicSplitPaneDivider getDivider()
  {
    return divider;
  }
  
  protected Component createDefaultNonContinuousLayoutDivider()
  {
    new Canvas()
    {
      public void paint(Graphics paramAnonymousGraphics)
      {
        if ((!isContinuousLayout()) && (getLastDragLocation() != -1))
        {
          Dimension localDimension = splitPane.getSize();
          paramAnonymousGraphics.setColor(dividerDraggingColor);
          if (orientation == 1) {
            paramAnonymousGraphics.fillRect(0, 0, dividerSize - 1, height - 1);
          } else {
            paramAnonymousGraphics.fillRect(0, 0, width - 1, dividerSize - 1);
          }
        }
      }
    };
  }
  
  protected void setNonContinuousLayoutDivider(Component paramComponent)
  {
    setNonContinuousLayoutDivider(paramComponent, true);
  }
  
  protected void setNonContinuousLayoutDivider(Component paramComponent, boolean paramBoolean)
  {
    rememberPaneSizes = paramBoolean;
    if ((nonContinuousLayoutDivider != null) && (splitPane != null)) {
      splitPane.remove(nonContinuousLayoutDivider);
    }
    nonContinuousLayoutDivider = paramComponent;
  }
  
  private void addHeavyweightDivider()
  {
    if ((nonContinuousLayoutDivider != null) && (splitPane != null))
    {
      Component localComponent1 = splitPane.getLeftComponent();
      Component localComponent2 = splitPane.getRightComponent();
      int i = splitPane.getDividerLocation();
      if (localComponent1 != null) {
        splitPane.setLeftComponent(null);
      }
      if (localComponent2 != null) {
        splitPane.setRightComponent(null);
      }
      splitPane.remove(divider);
      splitPane.add(nonContinuousLayoutDivider, "nonContinuousDivider", splitPane.getComponentCount());
      splitPane.setLeftComponent(localComponent1);
      splitPane.setRightComponent(localComponent2);
      splitPane.add(divider, "divider");
      if (rememberPaneSizes) {
        splitPane.setDividerLocation(i);
      }
    }
  }
  
  public Component getNonContinuousLayoutDivider()
  {
    return nonContinuousLayoutDivider;
  }
  
  public JSplitPane getSplitPane()
  {
    return splitPane;
  }
  
  public BasicSplitPaneDivider createDefaultDivider()
  {
    return new BasicSplitPaneDivider(this);
  }
  
  public void resetToPreferredSizes(JSplitPane paramJSplitPane)
  {
    if (splitPane != null)
    {
      layoutManager.resetToPreferredSizes();
      splitPane.revalidate();
      splitPane.repaint();
    }
  }
  
  public void setDividerLocation(JSplitPane paramJSplitPane, int paramInt)
  {
    if (!ignoreDividerLocationChange)
    {
      dividerLocationIsSet = true;
      splitPane.revalidate();
      splitPane.repaint();
      if (keepHidden)
      {
        Insets localInsets = splitPane.getInsets();
        int i = splitPane.getOrientation();
        if (((i == 0) && (paramInt != top) && (paramInt != splitPane.getHeight() - divider.getHeight() - top)) || ((i == 1) && (paramInt != left) && (paramInt != splitPane.getWidth() - divider.getWidth() - left))) {
          setKeepHidden(false);
        }
      }
    }
    else
    {
      ignoreDividerLocationChange = false;
    }
  }
  
  public int getDividerLocation(JSplitPane paramJSplitPane)
  {
    if (orientation == 1) {
      return divider.getLocation().x;
    }
    return divider.getLocation().y;
  }
  
  public int getMinimumDividerLocation(JSplitPane paramJSplitPane)
  {
    int i = 0;
    Component localComponent = splitPane.getLeftComponent();
    if ((localComponent != null) && (localComponent.isVisible()))
    {
      Insets localInsets = splitPane.getInsets();
      Dimension localDimension = localComponent.getMinimumSize();
      if (orientation == 1) {
        i = width;
      } else {
        i = height;
      }
      if (localInsets != null) {
        if (orientation == 1) {
          i += left;
        } else {
          i += top;
        }
      }
    }
    return i;
  }
  
  public int getMaximumDividerLocation(JSplitPane paramJSplitPane)
  {
    Dimension localDimension1 = splitPane.getSize();
    int i = 0;
    Component localComponent = splitPane.getRightComponent();
    if (localComponent != null)
    {
      Insets localInsets = splitPane.getInsets();
      Dimension localDimension2 = new Dimension(0, 0);
      if (localComponent.isVisible()) {
        localDimension2 = localComponent.getMinimumSize();
      }
      if (orientation == 1) {
        i = width - width;
      } else {
        i = height - height;
      }
      i -= dividerSize;
      if (localInsets != null) {
        if (orientation == 1) {
          i -= right;
        } else {
          i -= top;
        }
      }
    }
    return Math.max(getMinimumDividerLocation(splitPane), i);
  }
  
  public void finishedPaintingChildren(JSplitPane paramJSplitPane, Graphics paramGraphics)
  {
    if ((paramJSplitPane == splitPane) && (getLastDragLocation() != -1) && (!isContinuousLayout()) && (!draggingHW))
    {
      Dimension localDimension = splitPane.getSize();
      paramGraphics.setColor(dividerDraggingColor);
      if (orientation == 1) {
        paramGraphics.fillRect(getLastDragLocation(), 0, dividerSize - 1, height - 1);
      } else {
        paramGraphics.fillRect(0, lastDragLocation, width - 1, dividerSize - 1);
      }
    }
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if ((!painted) && (splitPane.getDividerLocation() < 0))
    {
      ignoreDividerLocationChange = true;
      splitPane.setDividerLocation(getDividerLocation(splitPane));
    }
    painted = true;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    if (splitPane != null) {
      return layoutManager.preferredLayoutSize(splitPane);
    }
    return new Dimension(0, 0);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    if (splitPane != null) {
      return layoutManager.minimumLayoutSize(splitPane);
    }
    return new Dimension(0, 0);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    if (splitPane != null) {
      return layoutManager.maximumLayoutSize(splitPane);
    }
    return new Dimension(0, 0);
  }
  
  public Insets getInsets(JComponent paramJComponent)
  {
    return null;
  }
  
  protected void resetLayoutManager()
  {
    if (orientation == 1) {
      layoutManager = new BasicHorizontalLayoutManager(0);
    } else {
      layoutManager = new BasicHorizontalLayoutManager(1);
    }
    splitPane.setLayout(layoutManager);
    layoutManager.updateComponents();
    splitPane.revalidate();
    splitPane.repaint();
  }
  
  void setKeepHidden(boolean paramBoolean)
  {
    keepHidden = paramBoolean;
  }
  
  private boolean getKeepHidden()
  {
    return keepHidden;
  }
  
  protected void startDragging()
  {
    Component localComponent1 = splitPane.getLeftComponent();
    Component localComponent2 = splitPane.getRightComponent();
    beginDragDividerLocation = getDividerLocation(splitPane);
    draggingHW = false;
    ComponentPeer localComponentPeer;
    if ((localComponent1 != null) && ((localComponentPeer = localComponent1.getPeer()) != null) && (!(localComponentPeer instanceof LightweightPeer))) {
      draggingHW = true;
    } else if ((localComponent2 != null) && ((localComponentPeer = localComponent2.getPeer()) != null) && (!(localComponentPeer instanceof LightweightPeer))) {
      draggingHW = true;
    }
    if (orientation == 1)
    {
      setLastDragLocation(divider.getBounds().x);
      dividerSize = divider.getSize().width;
      if ((!isContinuousLayout()) && (draggingHW))
      {
        nonContinuousLayoutDivider.setBounds(getLastDragLocation(), 0, dividerSize, splitPane.getHeight());
        addHeavyweightDivider();
      }
    }
    else
    {
      setLastDragLocation(divider.getBounds().y);
      dividerSize = divider.getSize().height;
      if ((!isContinuousLayout()) && (draggingHW))
      {
        nonContinuousLayoutDivider.setBounds(0, getLastDragLocation(), splitPane.getWidth(), dividerSize);
        addHeavyweightDivider();
      }
    }
  }
  
  protected void dragDividerTo(int paramInt)
  {
    if (getLastDragLocation() != paramInt) {
      if (isContinuousLayout())
      {
        splitPane.setDividerLocation(paramInt);
        setLastDragLocation(paramInt);
      }
      else
      {
        int i = getLastDragLocation();
        setLastDragLocation(paramInt);
        int j;
        if (orientation == 1)
        {
          if (draggingHW)
          {
            nonContinuousLayoutDivider.setLocation(getLastDragLocation(), 0);
          }
          else
          {
            j = splitPane.getHeight();
            splitPane.repaint(i, 0, dividerSize, j);
            splitPane.repaint(paramInt, 0, dividerSize, j);
          }
        }
        else if (draggingHW)
        {
          nonContinuousLayoutDivider.setLocation(0, getLastDragLocation());
        }
        else
        {
          j = splitPane.getWidth();
          splitPane.repaint(0, i, j, dividerSize);
          splitPane.repaint(0, paramInt, j, dividerSize);
        }
      }
    }
  }
  
  protected void finishDraggingTo(int paramInt)
  {
    dragDividerTo(paramInt);
    setLastDragLocation(-1);
    if (!isContinuousLayout())
    {
      Component localComponent = splitPane.getLeftComponent();
      Rectangle localRectangle = localComponent.getBounds();
      if (draggingHW)
      {
        if (orientation == 1) {
          nonContinuousLayoutDivider.setLocation(-dividerSize, 0);
        } else {
          nonContinuousLayoutDivider.setLocation(0, -dividerSize);
        }
        splitPane.remove(nonContinuousLayoutDivider);
      }
      splitPane.setDividerLocation(paramInt);
    }
  }
  
  @Deprecated
  protected int getDividerBorderSize()
  {
    return 1;
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String NEGATIVE_INCREMENT = "negativeIncrement";
    private static final String POSITIVE_INCREMENT = "positiveIncrement";
    private static final String SELECT_MIN = "selectMin";
    private static final String SELECT_MAX = "selectMax";
    private static final String START_RESIZE = "startResize";
    private static final String TOGGLE_FOCUS = "toggleFocus";
    private static final String FOCUS_OUT_FORWARD = "focusOutForward";
    private static final String FOCUS_OUT_BACKWARD = "focusOutBackward";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JSplitPane localJSplitPane = (JSplitPane)paramActionEvent.getSource();
      BasicSplitPaneUI localBasicSplitPaneUI = (BasicSplitPaneUI)BasicLookAndFeel.getUIOfType(localJSplitPane.getUI(), BasicSplitPaneUI.class);
      if (localBasicSplitPaneUI == null) {
        return;
      }
      String str = getName();
      if (str == "negativeIncrement")
      {
        if (dividerKeyboardResize) {
          localJSplitPane.setDividerLocation(Math.max(0, localBasicSplitPaneUI.getDividerLocation(localJSplitPane) - localBasicSplitPaneUI.getKeyboardMoveIncrement()));
        }
      }
      else if (str == "positiveIncrement")
      {
        if (dividerKeyboardResize) {
          localJSplitPane.setDividerLocation(localBasicSplitPaneUI.getDividerLocation(localJSplitPane) + localBasicSplitPaneUI.getKeyboardMoveIncrement());
        }
      }
      else if (str == "selectMin")
      {
        if (dividerKeyboardResize) {
          localJSplitPane.setDividerLocation(0);
        }
      }
      else
      {
        Object localObject;
        if (str == "selectMax")
        {
          if (dividerKeyboardResize)
          {
            localObject = localJSplitPane.getInsets();
            int i = localObject != null ? bottom : 0;
            int j = localObject != null ? right : 0;
            if (orientation == 0) {
              localJSplitPane.setDividerLocation(localJSplitPane.getHeight() - i);
            } else {
              localJSplitPane.setDividerLocation(localJSplitPane.getWidth() - j);
            }
          }
        }
        else if (str == "startResize")
        {
          if (!dividerKeyboardResize)
          {
            localJSplitPane.requestFocus();
          }
          else
          {
            localObject = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, localJSplitPane);
            if (localObject != null) {
              ((JSplitPane)localObject).requestFocus();
            }
          }
        }
        else if (str == "toggleFocus") {
          toggleFocus(localJSplitPane);
        } else if (str == "focusOutForward") {
          moveFocus(localJSplitPane, 1);
        } else if (str == "focusOutBackward") {
          moveFocus(localJSplitPane, -1);
        }
      }
    }
    
    private void moveFocus(JSplitPane paramJSplitPane, int paramInt)
    {
      Container localContainer = paramJSplitPane.getFocusCycleRootAncestor();
      FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
      Component localComponent = paramInt > 0 ? localFocusTraversalPolicy.getComponentAfter(localContainer, paramJSplitPane) : localFocusTraversalPolicy.getComponentBefore(localContainer, paramJSplitPane);
      HashSet localHashSet = new HashSet();
      if (paramJSplitPane.isAncestorOf(localComponent)) {
        do
        {
          localHashSet.add(localComponent);
          localContainer = localComponent.getFocusCycleRootAncestor();
          localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
          localComponent = paramInt > 0 ? localFocusTraversalPolicy.getComponentAfter(localContainer, localComponent) : localFocusTraversalPolicy.getComponentBefore(localContainer, localComponent);
        } while ((paramJSplitPane.isAncestorOf(localComponent)) && (!localHashSet.contains(localComponent)));
      }
      if ((localComponent != null) && (!paramJSplitPane.isAncestorOf(localComponent))) {
        localComponent.requestFocus();
      }
    }
    
    private void toggleFocus(JSplitPane paramJSplitPane)
    {
      Component localComponent1 = paramJSplitPane.getLeftComponent();
      Component localComponent2 = paramJSplitPane.getRightComponent();
      KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      Component localComponent3 = localKeyboardFocusManager.getFocusOwner();
      Component localComponent4 = getNextSide(paramJSplitPane, localComponent3);
      if (localComponent4 != null)
      {
        if ((localComponent3 != null) && (((SwingUtilities.isDescendingFrom(localComponent3, localComponent1)) && (SwingUtilities.isDescendingFrom(localComponent4, localComponent1))) || ((SwingUtilities.isDescendingFrom(localComponent3, localComponent2)) && (SwingUtilities.isDescendingFrom(localComponent4, localComponent2))))) {
          return;
        }
        SwingUtilities2.compositeRequestFocus(localComponent4);
      }
    }
    
    private Component getNextSide(JSplitPane paramJSplitPane, Component paramComponent)
    {
      Component localComponent1 = paramJSplitPane.getLeftComponent();
      Component localComponent2 = paramJSplitPane.getRightComponent();
      Component localComponent3;
      if ((paramComponent != null) && (SwingUtilities.isDescendingFrom(paramComponent, localComponent1)) && (localComponent2 != null))
      {
        localComponent3 = getFirstAvailableComponent(localComponent2);
        if (localComponent3 != null) {
          return localComponent3;
        }
      }
      JSplitPane localJSplitPane = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, paramJSplitPane);
      if (localJSplitPane != null)
      {
        localComponent3 = getNextSide(localJSplitPane, paramComponent);
      }
      else
      {
        localComponent3 = getFirstAvailableComponent(localComponent1);
        if (localComponent3 == null) {
          localComponent3 = getFirstAvailableComponent(localComponent2);
        }
      }
      return localComponent3;
    }
    
    private Component getFirstAvailableComponent(Component paramComponent)
    {
      if ((paramComponent != null) && ((paramComponent instanceof JSplitPane)))
      {
        JSplitPane localJSplitPane = (JSplitPane)paramComponent;
        Component localComponent = getFirstAvailableComponent(localJSplitPane.getLeftComponent());
        if (localComponent != null) {
          paramComponent = localComponent;
        } else {
          paramComponent = getFirstAvailableComponent(localJSplitPane.getRightComponent());
        }
      }
      return paramComponent;
    }
  }
  
  public class BasicHorizontalLayoutManager
    implements LayoutManager2
  {
    protected int[] sizes;
    protected Component[] components;
    private int lastSplitPaneSize;
    private boolean doReset;
    private int axis;
    
    BasicHorizontalLayoutManager()
    {
      this(0);
    }
    
    BasicHorizontalLayoutManager(int paramInt)
    {
      axis = paramInt;
      components = new Component[3];
      components[0] = (components[1] = components[2] = null);
      sizes = new int[3];
    }
    
    public void layoutContainer(Container paramContainer)
    {
      Dimension localDimension1 = paramContainer.getSize();
      if ((height <= 0) || (width <= 0))
      {
        lastSplitPaneSize = 0;
        return;
      }
      int i = splitPane.getDividerLocation();
      Insets localInsets = splitPane.getInsets();
      int j = getAvailableSize(localDimension1, localInsets);
      int k = getSizeForPrimaryAxis(localDimension1);
      int m = getDividerLocation(splitPane);
      int n = getSizeForPrimaryAxis(localInsets, true);
      Dimension localDimension2 = components[2] == null ? null : components[2].getPreferredSize();
      if (((doReset) && (!dividerLocationIsSet)) || (i < 0))
      {
        resetToPreferredSizes(j);
      }
      else if ((lastSplitPaneSize <= 0) || (j == lastSplitPaneSize) || (!painted) || ((localDimension2 != null) && (getSizeForPrimaryAxis(localDimension2) != sizes[2])))
      {
        if (localDimension2 != null) {
          sizes[2] = getSizeForPrimaryAxis(localDimension2);
        } else {
          sizes[2] = 0;
        }
        setDividerLocation(i - n, j);
        dividerLocationIsSet = false;
      }
      else if (j != lastSplitPaneSize)
      {
        distributeSpace(j - lastSplitPaneSize, BasicSplitPaneUI.this.getKeepHidden());
      }
      doReset = false;
      dividerLocationIsSet = false;
      lastSplitPaneSize = j;
      int i1 = getInitialLocation(localInsets);
      int i2 = 0;
      while (i2 < 3)
      {
        if ((components[i2] != null) && (components[i2].isVisible()))
        {
          setComponentToSize(components[i2], sizes[i2], i1, localInsets, localDimension1);
          i1 += sizes[i2];
        }
        switch (i2)
        {
        case 0: 
          i2 = 2;
          break;
        case 2: 
          i2 = 1;
          break;
        case 1: 
          i2 = 3;
        }
      }
      if (painted)
      {
        int i3 = getDividerLocation(splitPane);
        if (i3 != i - n)
        {
          int i4 = splitPane.getLastDividerLocation();
          ignoreDividerLocationChange = true;
          try
          {
            splitPane.setDividerLocation(i3);
            splitPane.setLastDividerLocation(i4);
          }
          finally
          {
            ignoreDividerLocationChange = false;
          }
        }
      }
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent)
    {
      int i = 1;
      if (paramString != null)
      {
        if (paramString.equals("divider"))
        {
          components[2] = paramComponent;
          sizes[2] = getSizeForPrimaryAxis(paramComponent.getPreferredSize());
        }
        else if ((paramString.equals("left")) || (paramString.equals("top")))
        {
          components[0] = paramComponent;
          sizes[0] = 0;
        }
        else if ((paramString.equals("right")) || (paramString.equals("bottom")))
        {
          components[1] = paramComponent;
          sizes[1] = 0;
        }
        else if (!paramString.equals("nonContinuousDivider"))
        {
          i = 0;
        }
      }
      else {
        i = 0;
      }
      if (i == 0) {
        throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + paramString);
      }
      doReset = true;
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      int i = 0;
      int j = 0;
      Insets localInsets = splitPane.getInsets();
      for (int k = 0; k < 3; k++) {
        if (components[k] != null)
        {
          Dimension localDimension = components[k].getMinimumSize();
          int m = getSizeForSecondaryAxis(localDimension);
          i += getSizeForPrimaryAxis(localDimension);
          if (m > j) {
            j = m;
          }
        }
      }
      if (localInsets != null)
      {
        i += getSizeForPrimaryAxis(localInsets, true) + getSizeForPrimaryAxis(localInsets, false);
        j += getSizeForSecondaryAxis(localInsets, true) + getSizeForSecondaryAxis(localInsets, false);
      }
      if (axis == 0) {
        return new Dimension(i, j);
      }
      return new Dimension(j, i);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      int i = 0;
      int j = 0;
      Insets localInsets = splitPane.getInsets();
      for (int k = 0; k < 3; k++) {
        if (components[k] != null)
        {
          Dimension localDimension = components[k].getPreferredSize();
          int m = getSizeForSecondaryAxis(localDimension);
          i += getSizeForPrimaryAxis(localDimension);
          if (m > j) {
            j = m;
          }
        }
      }
      if (localInsets != null)
      {
        i += getSizeForPrimaryAxis(localInsets, true) + getSizeForPrimaryAxis(localInsets, false);
        j += getSizeForSecondaryAxis(localInsets, true) + getSizeForSecondaryAxis(localInsets, false);
      }
      if (axis == 0) {
        return new Dimension(i, j);
      }
      return new Dimension(j, i);
    }
    
    public void removeLayoutComponent(Component paramComponent)
    {
      for (int i = 0; i < 3; i++) {
        if (components[i] == paramComponent)
        {
          components[i] = null;
          sizes[i] = 0;
          doReset = true;
        }
      }
    }
    
    public void addLayoutComponent(Component paramComponent, Object paramObject)
    {
      if ((paramObject == null) || ((paramObject instanceof String))) {
        addLayoutComponent((String)paramObject, paramComponent);
      } else {
        throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
      }
    }
    
    public float getLayoutAlignmentX(Container paramContainer)
    {
      return 0.0F;
    }
    
    public float getLayoutAlignmentY(Container paramContainer)
    {
      return 0.0F;
    }
    
    public void invalidateLayout(Container paramContainer) {}
    
    public Dimension maximumLayoutSize(Container paramContainer)
    {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public void resetToPreferredSizes()
    {
      doReset = true;
    }
    
    protected void resetSizeAt(int paramInt)
    {
      sizes[paramInt] = 0;
      doReset = true;
    }
    
    protected void setSizes(int[] paramArrayOfInt)
    {
      System.arraycopy(paramArrayOfInt, 0, sizes, 0, 3);
    }
    
    protected int[] getSizes()
    {
      int[] arrayOfInt = new int[3];
      System.arraycopy(sizes, 0, arrayOfInt, 0, 3);
      return arrayOfInt;
    }
    
    protected int getPreferredSizeOfComponent(Component paramComponent)
    {
      return getSizeForPrimaryAxis(paramComponent.getPreferredSize());
    }
    
    int getMinimumSizeOfComponent(Component paramComponent)
    {
      return getSizeForPrimaryAxis(paramComponent.getMinimumSize());
    }
    
    protected int getSizeOfComponent(Component paramComponent)
    {
      return getSizeForPrimaryAxis(paramComponent.getSize());
    }
    
    protected int getAvailableSize(Dimension paramDimension, Insets paramInsets)
    {
      if (paramInsets == null) {
        return getSizeForPrimaryAxis(paramDimension);
      }
      return getSizeForPrimaryAxis(paramDimension) - (getSizeForPrimaryAxis(paramInsets, true) + getSizeForPrimaryAxis(paramInsets, false));
    }
    
    protected int getInitialLocation(Insets paramInsets)
    {
      if (paramInsets != null) {
        return getSizeForPrimaryAxis(paramInsets, true);
      }
      return 0;
    }
    
    protected void setComponentToSize(Component paramComponent, int paramInt1, int paramInt2, Insets paramInsets, Dimension paramDimension)
    {
      if (paramInsets != null)
      {
        if (axis == 0) {
          paramComponent.setBounds(paramInt2, top, paramInt1, height - (top + bottom));
        } else {
          paramComponent.setBounds(left, paramInt2, width - (left + right), paramInt1);
        }
      }
      else if (axis == 0) {
        paramComponent.setBounds(paramInt2, 0, paramInt1, height);
      } else {
        paramComponent.setBounds(0, paramInt2, width, paramInt1);
      }
    }
    
    int getSizeForPrimaryAxis(Dimension paramDimension)
    {
      if (axis == 0) {
        return width;
      }
      return height;
    }
    
    int getSizeForSecondaryAxis(Dimension paramDimension)
    {
      if (axis == 0) {
        return height;
      }
      return width;
    }
    
    int getSizeForPrimaryAxis(Insets paramInsets, boolean paramBoolean)
    {
      if (axis == 0)
      {
        if (paramBoolean) {
          return left;
        }
        return right;
      }
      if (paramBoolean) {
        return top;
      }
      return bottom;
    }
    
    int getSizeForSecondaryAxis(Insets paramInsets, boolean paramBoolean)
    {
      if (axis == 0)
      {
        if (paramBoolean) {
          return top;
        }
        return bottom;
      }
      if (paramBoolean) {
        return left;
      }
      return right;
    }
    
    protected void updateComponents()
    {
      Component localComponent1 = splitPane.getLeftComponent();
      if (components[0] != localComponent1)
      {
        components[0] = localComponent1;
        if (localComponent1 == null) {
          sizes[0] = 0;
        } else {
          sizes[0] = -1;
        }
      }
      localComponent1 = splitPane.getRightComponent();
      if (components[1] != localComponent1)
      {
        components[1] = localComponent1;
        if (localComponent1 == null) {
          sizes[1] = 0;
        } else {
          sizes[1] = -1;
        }
      }
      Component[] arrayOfComponent = splitPane.getComponents();
      Component localComponent2 = components[2];
      components[2] = null;
      for (int i = arrayOfComponent.length - 1; i >= 0; i--) {
        if ((arrayOfComponent[i] != components[0]) && (arrayOfComponent[i] != components[1]) && (arrayOfComponent[i] != nonContinuousLayoutDivider))
        {
          if (localComponent2 != arrayOfComponent[i])
          {
            components[2] = arrayOfComponent[i];
            break;
          }
          components[2] = localComponent2;
          break;
        }
      }
      if (components[2] == null) {
        sizes[2] = 0;
      } else {
        sizes[2] = getSizeForPrimaryAxis(components[2].getPreferredSize());
      }
    }
    
    void setDividerLocation(int paramInt1, int paramInt2)
    {
      int i = (components[0] != null) && (components[0].isVisible()) ? 1 : 0;
      int j = (components[1] != null) && (components[1].isVisible()) ? 1 : 0;
      int k = (components[2] != null) && (components[2].isVisible()) ? 1 : 0;
      int m = paramInt2;
      if (k != 0) {
        m -= sizes[2];
      }
      paramInt1 = Math.max(0, Math.min(paramInt1, m));
      if (i != 0)
      {
        if (j != 0)
        {
          sizes[0] = paramInt1;
          sizes[1] = (m - paramInt1);
        }
        else
        {
          sizes[0] = m;
          sizes[1] = 0;
        }
      }
      else if (j != 0)
      {
        sizes[1] = m;
        sizes[0] = 0;
      }
    }
    
    int[] getPreferredSizes()
    {
      int[] arrayOfInt = new int[3];
      for (int i = 0; i < 3; i++) {
        if ((components[i] != null) && (components[i].isVisible())) {
          arrayOfInt[i] = getPreferredSizeOfComponent(components[i]);
        } else {
          arrayOfInt[i] = -1;
        }
      }
      return arrayOfInt;
    }
    
    int[] getMinimumSizes()
    {
      int[] arrayOfInt = new int[3];
      for (int i = 0; i < 2; i++) {
        if ((components[i] != null) && (components[i].isVisible())) {
          arrayOfInt[i] = getMinimumSizeOfComponent(components[i]);
        } else {
          arrayOfInt[i] = -1;
        }
      }
      arrayOfInt[2] = (components[2] != null ? getMinimumSizeOfComponent(components[2]) : -1);
      return arrayOfInt;
    }
    
    void resetToPreferredSizes(int paramInt)
    {
      int[] arrayOfInt = getPreferredSizes();
      int i = 0;
      for (int j = 0; j < 3; j++) {
        if (arrayOfInt[j] != -1) {
          i += arrayOfInt[j];
        }
      }
      if (i > paramInt)
      {
        arrayOfInt = getMinimumSizes();
        i = 0;
        for (j = 0; j < 3; j++) {
          if (arrayOfInt[j] != -1) {
            i += arrayOfInt[j];
          }
        }
      }
      setSizes(arrayOfInt);
      distributeSpace(paramInt - i, false);
    }
    
    void distributeSpace(int paramInt, boolean paramBoolean)
    {
      int i = (components[0] != null) && (components[0].isVisible()) ? 1 : 0;
      int j = (components[1] != null) && (components[1].isVisible()) ? 1 : 0;
      if (paramBoolean) {
        if ((i != 0) && (getSizeForPrimaryAxis(components[0].getSize()) == 0))
        {
          i = 0;
          if ((j != 0) && (getSizeForPrimaryAxis(components[1].getSize()) == 0)) {
            i = 1;
          }
        }
        else if ((j != 0) && (getSizeForPrimaryAxis(components[1].getSize()) == 0))
        {
          j = 0;
        }
      }
      if ((i != 0) && (j != 0))
      {
        double d = splitPane.getResizeWeight();
        int k = (int)(d * paramInt);
        int m = paramInt - k;
        sizes[0] += k;
        sizes[1] += m;
        int n = getMinimumSizeOfComponent(components[0]);
        int i1 = getMinimumSizeOfComponent(components[1]);
        int i2 = sizes[0] >= n ? 1 : 0;
        int i3 = sizes[1] >= i1 ? 1 : 0;
        if ((i2 == 0) && (i3 == 0))
        {
          if (sizes[0] < 0)
          {
            sizes[1] += sizes[0];
            sizes[0] = 0;
          }
          else if (sizes[1] < 0)
          {
            sizes[0] += sizes[1];
            sizes[1] = 0;
          }
        }
        else if (i2 == 0)
        {
          if (sizes[1] - (n - sizes[0]) < i1)
          {
            if (sizes[0] < 0)
            {
              sizes[1] += sizes[0];
              sizes[0] = 0;
            }
          }
          else
          {
            sizes[1] -= n - sizes[0];
            sizes[0] = n;
          }
        }
        else if (i3 == 0) {
          if (sizes[0] - (i1 - sizes[1]) < n)
          {
            if (sizes[1] < 0)
            {
              sizes[0] += sizes[1];
              sizes[1] = 0;
            }
          }
          else
          {
            sizes[0] -= i1 - sizes[1];
            sizes[1] = i1;
          }
        }
        if (sizes[0] < 0) {
          sizes[0] = 0;
        }
        if (sizes[1] < 0) {
          sizes[1] = 0;
        }
      }
      else if (i != 0)
      {
        sizes[0] = Math.max(0, sizes[0] + paramInt);
      }
      else if (j != 0)
      {
        sizes[1] = Math.max(0, sizes[1] + paramInt);
      }
    }
  }
  
  public class BasicVerticalLayoutManager
    extends BasicSplitPaneUI.BasicHorizontalLayoutManager
  {
    public BasicVerticalLayoutManager()
    {
      super(1);
    }
  }
  
  public class FocusHandler
    extends FocusAdapter
  {
    public FocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicSplitPaneUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicSplitPaneUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements FocusListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getSource() == splitPane)
      {
        String str = paramPropertyChangeEvent.getPropertyName();
        if (str == "orientation")
        {
          orientation = splitPane.getOrientation();
          resetLayoutManager();
        }
        else if (str == "continuousLayout")
        {
          setContinuousLayout(splitPane.isContinuousLayout());
          if (!isContinuousLayout()) {
            if (nonContinuousLayoutDivider == null) {
              setNonContinuousLayoutDivider(createDefaultNonContinuousLayoutDivider(), true);
            } else if (nonContinuousLayoutDivider.getParent() == null) {
              setNonContinuousLayoutDivider(nonContinuousLayoutDivider, true);
            }
          }
        }
        else if (str == "dividerSize")
        {
          divider.setDividerSize(splitPane.getDividerSize());
          dividerSize = divider.getDividerSize();
          splitPane.revalidate();
          splitPane.repaint();
        }
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      dividerKeyboardResize = true;
      splitPane.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      dividerKeyboardResize = false;
      splitPane.repaint();
    }
  }
  
  public class KeyboardDownRightHandler
    implements ActionListener
  {
    public KeyboardDownRightHandler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (dividerKeyboardResize) {
        splitPane.setDividerLocation(getDividerLocation(splitPane) + getKeyboardMoveIncrement());
      }
    }
  }
  
  public class KeyboardEndHandler
    implements ActionListener
  {
    public KeyboardEndHandler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (dividerKeyboardResize)
      {
        Insets localInsets = splitPane.getInsets();
        int i = localInsets != null ? bottom : 0;
        int j = localInsets != null ? right : 0;
        if (orientation == 0) {
          splitPane.setDividerLocation(splitPane.getHeight() - i);
        } else {
          splitPane.setDividerLocation(splitPane.getWidth() - j);
        }
      }
    }
  }
  
  public class KeyboardHomeHandler
    implements ActionListener
  {
    public KeyboardHomeHandler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (dividerKeyboardResize) {
        splitPane.setDividerLocation(0);
      }
    }
  }
  
  public class KeyboardResizeToggleHandler
    implements ActionListener
  {
    public KeyboardResizeToggleHandler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (!dividerKeyboardResize) {
        splitPane.requestFocus();
      }
    }
  }
  
  public class KeyboardUpLeftHandler
    implements ActionListener
  {
    public KeyboardUpLeftHandler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (dividerKeyboardResize) {
        splitPane.setDividerLocation(Math.max(0, getDividerLocation(splitPane) - getKeyboardMoveIncrement()));
      }
    }
  }
  
  public class PropertyHandler
    implements PropertyChangeListener
  {
    public PropertyHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicSplitPaneUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */