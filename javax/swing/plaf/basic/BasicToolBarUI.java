package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicToolBarUI
  extends ToolBarUI
  implements SwingConstants
{
  protected JToolBar toolBar;
  private boolean floating;
  private int floatingX;
  private int floatingY;
  private JFrame floatingFrame;
  private RootPaneContainer floatingToolBar;
  protected DragWindow dragWindow;
  private Container dockingSource;
  private int dockingSensitivity = 0;
  protected int focusedCompIndex = -1;
  protected Color dockingColor = null;
  protected Color floatingColor = null;
  protected Color dockingBorderColor = null;
  protected Color floatingBorderColor = null;
  protected MouseInputListener dockingListener;
  protected PropertyChangeListener propertyListener;
  protected ContainerListener toolBarContListener;
  protected FocusListener toolBarFocusListener;
  private Handler handler;
  protected String constraintBeforeFloating = "North";
  private static String IS_ROLLOVER = "JToolBar.isRollover";
  private static Border rolloverBorder;
  private static Border nonRolloverBorder;
  private static Border nonRolloverToggleBorder;
  private boolean rolloverBorders = false;
  private HashMap<AbstractButton, Border> borderTable = new HashMap();
  private Hashtable<AbstractButton, Boolean> rolloverTable = new Hashtable();
  @Deprecated
  protected KeyStroke upKey;
  @Deprecated
  protected KeyStroke downKey;
  @Deprecated
  protected KeyStroke leftKey;
  @Deprecated
  protected KeyStroke rightKey;
  private static String FOCUSED_COMP_INDEX = "JToolBar.focusedCompIndex";
  
  public BasicToolBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicToolBarUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    toolBar = ((JToolBar)paramJComponent);
    installDefaults();
    installComponents();
    installListeners();
    installKeyboardActions();
    dockingSensitivity = 0;
    floating = false;
    floatingX = (floatingY = 0);
    floatingToolBar = null;
    setOrientation(toolBar.getOrientation());
    LookAndFeel.installProperty(paramJComponent, "opaque", Boolean.TRUE);
    if (paramJComponent.getClientProperty(FOCUSED_COMP_INDEX) != null) {
      focusedCompIndex = ((Integer)paramJComponent.getClientProperty(FOCUSED_COMP_INDEX)).intValue();
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallComponents();
    uninstallListeners();
    uninstallKeyboardActions();
    if (isFloating()) {
      setFloating(false, null);
    }
    floatingToolBar = null;
    dragWindow = null;
    dockingSource = null;
    paramJComponent.putClientProperty(FOCUSED_COMP_INDEX, Integer.valueOf(focusedCompIndex));
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installBorder(toolBar, "ToolBar.border");
    LookAndFeel.installColorsAndFont(toolBar, "ToolBar.background", "ToolBar.foreground", "ToolBar.font");
    if ((dockingColor == null) || ((dockingColor instanceof UIResource))) {
      dockingColor = UIManager.getColor("ToolBar.dockingBackground");
    }
    if ((floatingColor == null) || ((floatingColor instanceof UIResource))) {
      floatingColor = UIManager.getColor("ToolBar.floatingBackground");
    }
    if ((dockingBorderColor == null) || ((dockingBorderColor instanceof UIResource))) {
      dockingBorderColor = UIManager.getColor("ToolBar.dockingForeground");
    }
    if ((floatingBorderColor == null) || ((floatingBorderColor instanceof UIResource))) {
      floatingBorderColor = UIManager.getColor("ToolBar.floatingForeground");
    }
    Object localObject = toolBar.getClientProperty(IS_ROLLOVER);
    if (localObject == null) {
      localObject = UIManager.get("ToolBar.isRollover");
    }
    if (localObject != null) {
      rolloverBorders = ((Boolean)localObject).booleanValue();
    }
    if (rolloverBorder == null) {
      rolloverBorder = createRolloverBorder();
    }
    if (nonRolloverBorder == null) {
      nonRolloverBorder = createNonRolloverBorder();
    }
    if (nonRolloverToggleBorder == null) {
      nonRolloverToggleBorder = createNonRolloverToggleBorder();
    }
    setRolloverBorders(isRolloverBorders());
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(toolBar);
    dockingColor = null;
    floatingColor = null;
    dockingBorderColor = null;
    floatingBorderColor = null;
    installNormalBorders(toolBar);
    rolloverBorder = null;
    nonRolloverBorder = null;
    nonRolloverToggleBorder = null;
  }
  
  protected void installComponents() {}
  
  protected void uninstallComponents() {}
  
  protected void installListeners()
  {
    dockingListener = createDockingListener();
    if (dockingListener != null)
    {
      toolBar.addMouseMotionListener(dockingListener);
      toolBar.addMouseListener(dockingListener);
    }
    propertyListener = createPropertyListener();
    if (propertyListener != null) {
      toolBar.addPropertyChangeListener(propertyListener);
    }
    toolBarContListener = createToolBarContListener();
    if (toolBarContListener != null) {
      toolBar.addContainerListener(toolBarContListener);
    }
    toolBarFocusListener = createToolBarFocusListener();
    if (toolBarFocusListener != null)
    {
      Component[] arrayOfComponent1 = toolBar.getComponents();
      for (Component localComponent : arrayOfComponent1) {
        localComponent.addFocusListener(toolBarFocusListener);
      }
    }
  }
  
  protected void uninstallListeners()
  {
    if (dockingListener != null)
    {
      toolBar.removeMouseMotionListener(dockingListener);
      toolBar.removeMouseListener(dockingListener);
      dockingListener = null;
    }
    if (propertyListener != null)
    {
      toolBar.removePropertyChangeListener(propertyListener);
      propertyListener = null;
    }
    if (toolBarContListener != null)
    {
      toolBar.removeContainerListener(toolBarContListener);
      toolBarContListener = null;
    }
    if (toolBarFocusListener != null)
    {
      Component[] arrayOfComponent1 = toolBar.getComponents();
      for (Component localComponent : arrayOfComponent1) {
        localComponent.removeFocusListener(toolBarFocusListener);
      }
      toolBarFocusListener = null;
    }
    handler = null;
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(toolBar, 1, localInputMap);
    LazyActionMap.installLazyActionMap(toolBar, BasicToolBarUI.class, "ToolBar.actionMap");
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(toolBar, this, "ToolBar.ancestorInputMap");
    }
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("navigateRight"));
    paramLazyActionMap.put(new Actions("navigateLeft"));
    paramLazyActionMap.put(new Actions("navigateUp"));
    paramLazyActionMap.put(new Actions("navigateDown"));
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(toolBar, null);
    SwingUtilities.replaceUIInputMap(toolBar, 1, null);
  }
  
  protected void navigateFocusedComp(int paramInt)
  {
    int i = toolBar.getComponentCount();
    int j;
    switch (paramInt)
    {
    case 3: 
    case 5: 
      if ((focusedCompIndex >= 0) && (focusedCompIndex < i)) {
        j = focusedCompIndex + 1;
      }
      break;
    case 1: 
    case 7: 
      while (j != focusedCompIndex)
      {
        if (j >= i) {
          j = 0;
        }
        Component localComponent = toolBar.getComponentAtIndex(j++);
        if ((localComponent != null) && (localComponent.isFocusTraversable()) && (localComponent.isEnabled()))
        {
          localComponent.requestFocus();
        }
        else
        {
          continue;
          if ((focusedCompIndex >= 0) && (focusedCompIndex < i))
          {
            j = focusedCompIndex - 1;
            while (j != focusedCompIndex)
            {
              if (j < 0) {
                j = i - 1;
              }
              localComponent = toolBar.getComponentAtIndex(j--);
              if ((localComponent != null) && (localComponent.isFocusTraversable()) && (localComponent.isEnabled()))
              {
                localComponent.requestFocus();
                break;
              }
            }
          }
        }
      }
    }
  }
  
  protected Border createRolloverBorder()
  {
    Object localObject = UIManager.get("ToolBar.rolloverBorder");
    if (localObject != null) {
      return (Border)localObject;
    }
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    return new CompoundBorder(new BasicBorders.RolloverButtonBorder(localUIDefaults.getColor("controlShadow"), localUIDefaults.getColor("controlDkShadow"), localUIDefaults.getColor("controlHighlight"), localUIDefaults.getColor("controlLtHighlight")), new BasicBorders.RolloverMarginBorder());
  }
  
  protected Border createNonRolloverBorder()
  {
    Object localObject = UIManager.get("ToolBar.nonrolloverBorder");
    if (localObject != null) {
      return (Border)localObject;
    }
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    return new CompoundBorder(new BasicBorders.ButtonBorder(localUIDefaults.getColor("Button.shadow"), localUIDefaults.getColor("Button.darkShadow"), localUIDefaults.getColor("Button.light"), localUIDefaults.getColor("Button.highlight")), new BasicBorders.RolloverMarginBorder());
  }
  
  private Border createNonRolloverToggleBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    return new CompoundBorder(new BasicBorders.RadioButtonBorder(localUIDefaults.getColor("ToggleButton.shadow"), localUIDefaults.getColor("ToggleButton.darkShadow"), localUIDefaults.getColor("ToggleButton.light"), localUIDefaults.getColor("ToggleButton.highlight")), new BasicBorders.RolloverMarginBorder());
  }
  
  protected JFrame createFloatingFrame(JToolBar paramJToolBar)
  {
    Window localWindow = SwingUtilities.getWindowAncestor(paramJToolBar);
    JFrame local1 = new JFrame(paramJToolBar.getName(), localWindow != null ? localWindow.getGraphicsConfiguration() : null)
    {
      protected JRootPane createRootPane()
      {
        JRootPane local1 = new JRootPane()
        {
          private boolean packing = false;
          
          public void validate()
          {
            super.validate();
            if (!packing)
            {
              packing = true;
              pack();
              packing = false;
            }
          }
        };
        local1.setOpaque(true);
        return local1;
      }
    };
    local1.getRootPane().setName("ToolBar.FloatingFrame");
    local1.setResizable(false);
    WindowListener localWindowListener = createFrameListener();
    local1.addWindowListener(localWindowListener);
    return local1;
  }
  
  protected RootPaneContainer createFloatingWindow(JToolBar paramJToolBar)
  {
    Window localWindow = SwingUtilities.getWindowAncestor(paramJToolBar);
    JDialog local1ToolBarDialog;
    if ((localWindow instanceof Frame)) {
      local1ToolBarDialog = new JDialog((Frame)localWindow, paramJToolBar.getName(), false)
      {
        protected JRootPane createRootPane()
        {
          JRootPane local1 = new JRootPane()
          {
            private boolean packing = false;
            
            public void validate()
            {
              super.validate();
              if (!packing)
              {
                packing = true;
                pack();
                packing = false;
              }
            }
          };
          local1.setOpaque(true);
          return local1;
        }
      };
    } else if ((localWindow instanceof Dialog)) {
      local1ToolBarDialog = new JDialog((Dialog)localWindow, paramJToolBar.getName(), false)
      {
        protected JRootPane createRootPane()
        {
          JRootPane local1 = new JRootPane()
          {
            private boolean packing = false;
            
            public void validate()
            {
              super.validate();
              if (!packing)
              {
                packing = true;
                pack();
                packing = false;
              }
            }
          };
          local1.setOpaque(true);
          return local1;
        }
      };
    } else {
      local1ToolBarDialog = new JDialog((Frame)null, paramJToolBar.getName(), false)
      {
        protected JRootPane createRootPane()
        {
          JRootPane local1 = new JRootPane()
          {
            private boolean packing = false;
            
            public void validate()
            {
              super.validate();
              if (!packing)
              {
                packing = true;
                pack();
                packing = false;
              }
            }
          };
          local1.setOpaque(true);
          return local1;
        }
      };
    }
    local1ToolBarDialog.getRootPane().setName("ToolBar.FloatingWindow");
    local1ToolBarDialog.setTitle(paramJToolBar.getName());
    local1ToolBarDialog.setResizable(false);
    WindowListener localWindowListener = createFrameListener();
    local1ToolBarDialog.addWindowListener(localWindowListener);
    return local1ToolBarDialog;
  }
  
  protected DragWindow createDragWindow(JToolBar paramJToolBar)
  {
    Window localWindow = null;
    if (toolBar != null)
    {
      for (localObject = toolBar.getParent(); (localObject != null) && (!(localObject instanceof Window)); localObject = ((Container)localObject).getParent()) {}
      if ((localObject != null) && ((localObject instanceof Window))) {
        localWindow = (Window)localObject;
      }
    }
    if (floatingToolBar == null) {
      floatingToolBar = createFloatingWindow(toolBar);
    }
    if ((floatingToolBar instanceof Window)) {
      localWindow = (Window)floatingToolBar;
    }
    Object localObject = new DragWindow(localWindow);
    return (DragWindow)localObject;
  }
  
  public boolean isRolloverBorders()
  {
    return rolloverBorders;
  }
  
  public void setRolloverBorders(boolean paramBoolean)
  {
    rolloverBorders = paramBoolean;
    if (rolloverBorders) {
      installRolloverBorders(toolBar);
    } else {
      installNonRolloverBorders(toolBar);
    }
  }
  
  protected void installRolloverBorders(JComponent paramJComponent)
  {
    Component[] arrayOfComponent1 = paramJComponent.getComponents();
    for (Component localComponent : arrayOfComponent1) {
      if ((localComponent instanceof JComponent))
      {
        ((JComponent)localComponent).updateUI();
        setBorderToRollover(localComponent);
      }
    }
  }
  
  protected void installNonRolloverBorders(JComponent paramJComponent)
  {
    Component[] arrayOfComponent1 = paramJComponent.getComponents();
    for (Component localComponent : arrayOfComponent1) {
      if ((localComponent instanceof JComponent))
      {
        ((JComponent)localComponent).updateUI();
        setBorderToNonRollover(localComponent);
      }
    }
  }
  
  protected void installNormalBorders(JComponent paramJComponent)
  {
    Component[] arrayOfComponent1 = paramJComponent.getComponents();
    for (Component localComponent : arrayOfComponent1) {
      setBorderToNormal(localComponent);
    }
  }
  
  protected void setBorderToRollover(Component paramComponent)
  {
    if ((paramComponent instanceof AbstractButton))
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      Border localBorder = (Border)borderTable.get(localAbstractButton);
      if ((localBorder == null) || ((localBorder instanceof UIResource))) {
        borderTable.put(localAbstractButton, localAbstractButton.getBorder());
      }
      if ((localAbstractButton.getBorder() instanceof UIResource)) {
        localAbstractButton.setBorder(getRolloverBorder(localAbstractButton));
      }
      rolloverTable.put(localAbstractButton, localAbstractButton.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
      localAbstractButton.setRolloverEnabled(true);
    }
  }
  
  protected Border getRolloverBorder(AbstractButton paramAbstractButton)
  {
    return rolloverBorder;
  }
  
  protected void setBorderToNonRollover(Component paramComponent)
  {
    if ((paramComponent instanceof AbstractButton))
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      Border localBorder = (Border)borderTable.get(localAbstractButton);
      if ((localBorder == null) || ((localBorder instanceof UIResource))) {
        borderTable.put(localAbstractButton, localAbstractButton.getBorder());
      }
      if ((localAbstractButton.getBorder() instanceof UIResource)) {
        localAbstractButton.setBorder(getNonRolloverBorder(localAbstractButton));
      }
      rolloverTable.put(localAbstractButton, localAbstractButton.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
      localAbstractButton.setRolloverEnabled(false);
    }
  }
  
  protected Border getNonRolloverBorder(AbstractButton paramAbstractButton)
  {
    if ((paramAbstractButton instanceof JToggleButton)) {
      return nonRolloverToggleBorder;
    }
    return nonRolloverBorder;
  }
  
  protected void setBorderToNormal(Component paramComponent)
  {
    if ((paramComponent instanceof AbstractButton))
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      Border localBorder = (Border)borderTable.remove(localAbstractButton);
      localAbstractButton.setBorder(localBorder);
      Boolean localBoolean = (Boolean)rolloverTable.remove(localAbstractButton);
      if (localBoolean != null) {
        localAbstractButton.setRolloverEnabled(localBoolean.booleanValue());
      }
    }
  }
  
  public void setFloatingLocation(int paramInt1, int paramInt2)
  {
    floatingX = paramInt1;
    floatingY = paramInt2;
  }
  
  public boolean isFloating()
  {
    return floating;
  }
  
  public void setFloating(boolean paramBoolean, Point paramPoint)
  {
    if (toolBar.isFloatable())
    {
      boolean bool = false;
      Window localWindow = SwingUtilities.getWindowAncestor(toolBar);
      if (localWindow != null) {
        bool = localWindow.isVisible();
      }
      if (dragWindow != null) {
        dragWindow.setVisible(false);
      }
      floating = paramBoolean;
      if (floatingToolBar == null) {
        floatingToolBar = createFloatingWindow(toolBar);
      }
      if (paramBoolean == true)
      {
        if (dockingSource == null)
        {
          dockingSource = toolBar.getParent();
          dockingSource.remove(toolBar);
        }
        constraintBeforeFloating = calculateConstraint();
        if (propertyListener != null) {
          UIManager.addPropertyChangeListener(propertyListener);
        }
        floatingToolBar.getContentPane().add(toolBar, "Center");
        if ((floatingToolBar instanceof Window))
        {
          ((Window)floatingToolBar).pack();
          ((Window)floatingToolBar).setLocation(floatingX, floatingY);
          if (bool) {
            ((Window)floatingToolBar).show();
          } else {
            localWindow.addWindowListener(new WindowAdapter()
            {
              public void windowOpened(WindowEvent paramAnonymousWindowEvent)
              {
                ((Window)floatingToolBar).show();
              }
            });
          }
        }
      }
      else
      {
        if (floatingToolBar == null) {
          floatingToolBar = createFloatingWindow(toolBar);
        }
        if ((floatingToolBar instanceof Window)) {
          ((Window)floatingToolBar).setVisible(false);
        }
        floatingToolBar.getContentPane().remove(toolBar);
        localObject = getDockingConstraint(dockingSource, paramPoint);
        if (localObject == null) {
          localObject = "North";
        }
        int i = mapConstraintToOrientation((String)localObject);
        setOrientation(i);
        if (dockingSource == null) {
          dockingSource = toolBar.getParent();
        }
        if (propertyListener != null) {
          UIManager.removePropertyChangeListener(propertyListener);
        }
        dockingSource.add((String)localObject, toolBar);
      }
      dockingSource.invalidate();
      Object localObject = dockingSource.getParent();
      if (localObject != null) {
        ((Container)localObject).validate();
      }
      dockingSource.repaint();
    }
  }
  
  private int mapConstraintToOrientation(String paramString)
  {
    int i = toolBar.getOrientation();
    if (paramString != null) {
      if ((paramString.equals("East")) || (paramString.equals("West"))) {
        i = 1;
      } else if ((paramString.equals("North")) || (paramString.equals("South"))) {
        i = 0;
      }
    }
    return i;
  }
  
  public void setOrientation(int paramInt)
  {
    toolBar.setOrientation(paramInt);
    if (dragWindow != null) {
      dragWindow.setOrientation(paramInt);
    }
  }
  
  public Color getDockingColor()
  {
    return dockingColor;
  }
  
  public void setDockingColor(Color paramColor)
  {
    dockingColor = paramColor;
  }
  
  public Color getFloatingColor()
  {
    return floatingColor;
  }
  
  public void setFloatingColor(Color paramColor)
  {
    floatingColor = paramColor;
  }
  
  private boolean isBlocked(Component paramComponent, Object paramObject)
  {
    if ((paramComponent instanceof Container))
    {
      Container localContainer = (Container)paramComponent;
      LayoutManager localLayoutManager = localContainer.getLayout();
      if ((localLayoutManager instanceof BorderLayout))
      {
        BorderLayout localBorderLayout = (BorderLayout)localLayoutManager;
        Component localComponent = localBorderLayout.getLayoutComponent(localContainer, paramObject);
        return (localComponent != null) && (localComponent != toolBar);
      }
    }
    return false;
  }
  
  public boolean canDock(Component paramComponent, Point paramPoint)
  {
    return (paramPoint != null) && (getDockingConstraint(paramComponent, paramPoint) != null);
  }
  
  private String calculateConstraint()
  {
    String str = null;
    LayoutManager localLayoutManager = dockingSource.getLayout();
    if ((localLayoutManager instanceof BorderLayout)) {
      str = (String)((BorderLayout)localLayoutManager).getConstraints(toolBar);
    }
    return str != null ? str : constraintBeforeFloating;
  }
  
  private String getDockingConstraint(Component paramComponent, Point paramPoint)
  {
    if (paramPoint == null) {
      return constraintBeforeFloating;
    }
    if (paramComponent.contains(paramPoint))
    {
      dockingSensitivity = (toolBar.getOrientation() == 0 ? toolBar.getSize().height : toolBar.getSize().width);
      if ((y < dockingSensitivity) && (!isBlocked(paramComponent, "North"))) {
        return "North";
      }
      if ((x >= paramComponent.getWidth() - dockingSensitivity) && (!isBlocked(paramComponent, "East"))) {
        return "East";
      }
      if ((x < dockingSensitivity) && (!isBlocked(paramComponent, "West"))) {
        return "West";
      }
      if ((y >= paramComponent.getHeight() - dockingSensitivity) && (!isBlocked(paramComponent, "South"))) {
        return "South";
      }
    }
    return null;
  }
  
  protected void dragTo(Point paramPoint1, Point paramPoint2)
  {
    if (toolBar.isFloatable()) {
      try
      {
        if (dragWindow == null) {
          dragWindow = createDragWindow(toolBar);
        }
        Point localPoint1 = dragWindow.getOffset();
        if (localPoint1 == null)
        {
          localObject1 = toolBar.getPreferredSize();
          localPoint1 = new Point(width / 2, height / 2);
          dragWindow.setOffset(localPoint1);
        }
        Object localObject1 = new Point(x + x, y + y);
        Point localPoint2 = new Point(x - x, y - y);
        if (dockingSource == null) {
          dockingSource = toolBar.getParent();
        }
        constraintBeforeFloating = calculateConstraint();
        Point localPoint3 = dockingSource.getLocationOnScreen();
        Point localPoint4 = new Point(x - x, y - y);
        Object localObject2;
        if (canDock(dockingSource, localPoint4))
        {
          dragWindow.setBackground(getDockingColor());
          localObject2 = getDockingConstraint(dockingSource, localPoint4);
          int i = mapConstraintToOrientation((String)localObject2);
          dragWindow.setOrientation(i);
          dragWindow.setBorderColor(dockingBorderColor);
        }
        else
        {
          dragWindow.setBackground(getFloatingColor());
          dragWindow.setBorderColor(floatingBorderColor);
          dragWindow.setOrientation(toolBar.getOrientation());
        }
        dragWindow.setLocation(x, y);
        if (!dragWindow.isVisible())
        {
          localObject2 = toolBar.getPreferredSize();
          dragWindow.setSize(width, height);
          dragWindow.show();
        }
      }
      catch (IllegalComponentStateException localIllegalComponentStateException) {}
    }
  }
  
  protected void floatAt(Point paramPoint1, Point paramPoint2)
  {
    if (toolBar.isFloatable()) {
      try
      {
        Point localPoint1 = dragWindow.getOffset();
        if (localPoint1 == null)
        {
          localPoint1 = paramPoint1;
          dragWindow.setOffset(localPoint1);
        }
        Point localPoint2 = new Point(x + x, y + y);
        setFloatingLocation(x - x, y - y);
        if (dockingSource != null)
        {
          Point localPoint3 = dockingSource.getLocationOnScreen();
          Point localPoint4 = new Point(x - x, y - y);
          if (canDock(dockingSource, localPoint4)) {
            setFloating(false, localPoint4);
          } else {
            setFloating(true, null);
          }
        }
        else
        {
          setFloating(true, null);
        }
        dragWindow.setOffset(null);
      }
      catch (IllegalComponentStateException localIllegalComponentStateException) {}
    }
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected ContainerListener createToolBarContListener()
  {
    return getHandler();
  }
  
  protected FocusListener createToolBarFocusListener()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createPropertyListener()
  {
    return getHandler();
  }
  
  protected MouseInputListener createDockingListener()
  {
    getHandlertb = toolBar;
    return getHandler();
  }
  
  protected WindowListener createFrameListener()
  {
    return new FrameListener();
  }
  
  protected void paintDragWindow(Graphics paramGraphics)
  {
    paramGraphics.setColor(dragWindow.getBackground());
    int i = dragWindow.getWidth();
    int j = dragWindow.getHeight();
    paramGraphics.fillRect(0, 0, i, j);
    paramGraphics.setColor(dragWindow.getBorderColor());
    paramGraphics.drawRect(0, 0, i - 1, j - 1);
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String NAVIGATE_RIGHT = "navigateRight";
    private static final String NAVIGATE_LEFT = "navigateLeft";
    private static final String NAVIGATE_UP = "navigateUp";
    private static final String NAVIGATE_DOWN = "navigateDown";
    
    public Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str = getName();
      JToolBar localJToolBar = (JToolBar)paramActionEvent.getSource();
      BasicToolBarUI localBasicToolBarUI = (BasicToolBarUI)BasicLookAndFeel.getUIOfType(localJToolBar.getUI(), BasicToolBarUI.class);
      if ("navigateRight" == str) {
        localBasicToolBarUI.navigateFocusedComp(3);
      } else if ("navigateLeft" == str) {
        localBasicToolBarUI.navigateFocusedComp(7);
      } else if ("navigateUp" == str) {
        localBasicToolBarUI.navigateFocusedComp(1);
      } else if ("navigateDown" == str) {
        localBasicToolBarUI.navigateFocusedComp(5);
      }
    }
  }
  
  public class DockingListener
    implements MouseInputListener
  {
    protected JToolBar toolBar;
    protected boolean isDragging = false;
    protected Point origin = null;
    
    public DockingListener(JToolBar paramJToolBar)
    {
      toolBar = paramJToolBar;
      getHandlertb = paramJToolBar;
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      BasicToolBarUI.this.getHandler().mouseClicked(paramMouseEvent);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      getHandlertb = toolBar;
      BasicToolBarUI.this.getHandler().mousePressed(paramMouseEvent);
      isDragging = getHandlerisDragging;
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      getHandlertb = toolBar;
      getHandlerisDragging = isDragging;
      getHandlerorigin = origin;
      BasicToolBarUI.this.getHandler().mouseReleased(paramMouseEvent);
      isDragging = getHandlerisDragging;
      origin = getHandlerorigin;
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      BasicToolBarUI.this.getHandler().mouseEntered(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      BasicToolBarUI.this.getHandler().mouseExited(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      getHandlertb = toolBar;
      getHandlerorigin = origin;
      BasicToolBarUI.this.getHandler().mouseDragged(paramMouseEvent);
      isDragging = getHandlerisDragging;
      origin = getHandlerorigin;
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicToolBarUI.this.getHandler().mouseMoved(paramMouseEvent);
    }
  }
  
  protected class DragWindow
    extends Window
  {
    Color borderColor = Color.gray;
    int orientation = toolBar.getOrientation();
    Point offset;
    
    DragWindow(Window paramWindow)
    {
      super();
    }
    
    public int getOrientation()
    {
      return orientation;
    }
    
    public void setOrientation(int paramInt)
    {
      if (isShowing())
      {
        if (paramInt == orientation) {
          return;
        }
        orientation = paramInt;
        Dimension localDimension = getSize();
        setSize(new Dimension(height, width));
        if (offset != null) {
          if (BasicGraphicsUtils.isLeftToRight(toolBar)) {
            setOffset(new Point(offset.y, offset.x));
          } else if (paramInt == 0) {
            setOffset(new Point(height - offset.y, offset.x));
          } else {
            setOffset(new Point(offset.y, width - offset.x));
          }
        }
        repaint();
      }
    }
    
    public Point getOffset()
    {
      return offset;
    }
    
    public void setOffset(Point paramPoint)
    {
      offset = paramPoint;
    }
    
    public void setBorderColor(Color paramColor)
    {
      if (borderColor == paramColor) {
        return;
      }
      borderColor = paramColor;
      repaint();
    }
    
    public Color getBorderColor()
    {
      return borderColor;
    }
    
    public void paint(Graphics paramGraphics)
    {
      paintDragWindow(paramGraphics);
      super.paint(paramGraphics);
    }
    
    public Insets getInsets()
    {
      return new Insets(1, 1, 1, 1);
    }
  }
  
  protected class FrameListener
    extends WindowAdapter
  {
    protected FrameListener() {}
    
    public void windowClosing(WindowEvent paramWindowEvent)
    {
      if (toolBar.isFloatable())
      {
        if (dragWindow != null) {
          dragWindow.setVisible(false);
        }
        floating = false;
        if (floatingToolBar == null) {
          floatingToolBar = createFloatingWindow(toolBar);
        }
        if ((floatingToolBar instanceof Window)) {
          ((Window)floatingToolBar).setVisible(false);
        }
        floatingToolBar.getContentPane().remove(toolBar);
        String str = constraintBeforeFloating;
        if (toolBar.getOrientation() == 0)
        {
          if ((str == "West") || (str == "East")) {
            str = "North";
          }
        }
        else if ((str == "North") || (str == "South")) {
          str = "West";
        }
        if (dockingSource == null) {
          dockingSource = toolBar.getParent();
        }
        if (propertyListener != null) {
          UIManager.removePropertyChangeListener(propertyListener);
        }
        dockingSource.add(toolBar, str);
        dockingSource.invalidate();
        Container localContainer = dockingSource.getParent();
        if (localContainer != null) {
          localContainer.validate();
        }
        dockingSource.repaint();
      }
    }
  }
  
  private class Handler
    implements ContainerListener, FocusListener, MouseInputListener, PropertyChangeListener
  {
    JToolBar tb;
    boolean isDragging = false;
    Point origin = null;
    
    private Handler() {}
    
    public void componentAdded(ContainerEvent paramContainerEvent)
    {
      Component localComponent = paramContainerEvent.getChild();
      if (toolBarFocusListener != null) {
        localComponent.addFocusListener(toolBarFocusListener);
      }
      if (isRolloverBorders()) {
        setBorderToRollover(localComponent);
      } else {
        setBorderToNonRollover(localComponent);
      }
    }
    
    public void componentRemoved(ContainerEvent paramContainerEvent)
    {
      Component localComponent = paramContainerEvent.getChild();
      if (toolBarFocusListener != null) {
        localComponent.removeFocusListener(toolBarFocusListener);
      }
      setBorderToNormal(localComponent);
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      Component localComponent = paramFocusEvent.getComponent();
      focusedCompIndex = toolBar.getComponentIndex(localComponent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!tb.isEnabled()) {
        return;
      }
      isDragging = false;
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (!tb.isEnabled()) {
        return;
      }
      if (isDragging)
      {
        Point localPoint = paramMouseEvent.getPoint();
        if (origin == null) {
          origin = paramMouseEvent.getComponent().getLocationOnScreen();
        }
        floatAt(localPoint, origin);
      }
      origin = null;
      isDragging = false;
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (!tb.isEnabled()) {
        return;
      }
      isDragging = true;
      Point localPoint = paramMouseEvent.getPoint();
      if (origin == null) {
        origin = paramMouseEvent.getComponent().getLocationOnScreen();
      }
      dragTo(localPoint, origin);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str == "lookAndFeel")
      {
        toolBar.updateUI();
      }
      else if (str == "orientation")
      {
        Component[] arrayOfComponent = toolBar.getComponents();
        int i = ((Integer)paramPropertyChangeEvent.getNewValue()).intValue();
        for (int j = 0; j < arrayOfComponent.length; j++) {
          if ((arrayOfComponent[j] instanceof JToolBar.Separator))
          {
            JToolBar.Separator localSeparator = (JToolBar.Separator)arrayOfComponent[j];
            if (i == 0) {
              localSeparator.setOrientation(1);
            } else {
              localSeparator.setOrientation(0);
            }
            Dimension localDimension1 = localSeparator.getSeparatorSize();
            if ((localDimension1 != null) && (width != height))
            {
              Dimension localDimension2 = new Dimension(height, width);
              localSeparator.setSeparatorSize(localDimension2);
            }
          }
        }
      }
      else if (str == BasicToolBarUI.IS_ROLLOVER)
      {
        installNormalBorders(toolBar);
        setRolloverBorders(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
      }
    }
  }
  
  protected class PropertyListener
    implements PropertyChangeListener
  {
    protected PropertyListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicToolBarUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  protected class ToolBarContListener
    implements ContainerListener
  {
    protected ToolBarContListener() {}
    
    public void componentAdded(ContainerEvent paramContainerEvent)
    {
      BasicToolBarUI.this.getHandler().componentAdded(paramContainerEvent);
    }
    
    public void componentRemoved(ContainerEvent paramContainerEvent)
    {
      BasicToolBarUI.this.getHandler().componentRemoved(paramContainerEvent);
    }
  }
  
  protected class ToolBarFocusListener
    implements FocusListener
  {
    protected ToolBarFocusListener() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicToolBarUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicToolBarUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */