package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicInternalFrameUI
  extends InternalFrameUI
{
  protected JInternalFrame frame;
  private Handler handler;
  protected MouseInputAdapter borderListener;
  protected PropertyChangeListener propertyChangeListener;
  protected LayoutManager internalFrameLayout;
  protected ComponentListener componentListener;
  protected MouseInputListener glassPaneDispatcher;
  private InternalFrameListener internalFrameListener;
  protected JComponent northPane;
  protected JComponent southPane;
  protected JComponent westPane;
  protected JComponent eastPane;
  protected BasicInternalFrameTitlePane titlePane;
  private static DesktopManager sharedDesktopManager;
  private boolean componentListenerAdded = false;
  private Rectangle parentBounds;
  private boolean dragging = false;
  private boolean resizing = false;
  @Deprecated
  protected KeyStroke openMenuKey;
  private boolean keyBindingRegistered = false;
  private boolean keyBindingActive = false;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicInternalFrameUI((JInternalFrame)paramJComponent);
  }
  
  public BasicInternalFrameUI(JInternalFrame paramJInternalFrame)
  {
    LookAndFeel localLookAndFeel = UIManager.getLookAndFeel();
    if ((localLookAndFeel instanceof BasicLookAndFeel)) {
      ((BasicLookAndFeel)localLookAndFeel).installAWTEventListener();
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    frame = ((JInternalFrame)paramJComponent);
    installDefaults();
    installListeners();
    installComponents();
    installKeyboardActions();
    LookAndFeel.installProperty(frame, "opaque", Boolean.TRUE);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    if (paramJComponent != frame) {
      throw new IllegalComponentStateException(this + " was asked to deinstall() " + paramJComponent + " when it only knows about " + frame + ".");
    }
    uninstallKeyboardActions();
    uninstallComponents();
    uninstallListeners();
    uninstallDefaults();
    updateFrameCursor();
    handler = null;
    frame = null;
  }
  
  protected void installDefaults()
  {
    Icon localIcon = frame.getFrameIcon();
    if ((localIcon == null) || ((localIcon instanceof UIResource))) {
      frame.setFrameIcon(UIManager.getIcon("InternalFrame.icon"));
    }
    Container localContainer = frame.getContentPane();
    if (localContainer != null)
    {
      Color localColor = localContainer.getBackground();
      if ((localColor instanceof UIResource)) {
        localContainer.setBackground(null);
      }
    }
    frame.setLayout(internalFrameLayout = createLayoutManager());
    frame.setBackground(UIManager.getLookAndFeelDefaults().getColor("control"));
    LookAndFeel.installBorder(frame, "InternalFrame.border");
  }
  
  protected void installKeyboardActions()
  {
    createInternalFrameListener();
    if (internalFrameListener != null) {
      frame.addInternalFrameListener(internalFrameListener);
    }
    LazyActionMap.installLazyActionMap(frame, BasicInternalFrameUI.class, "InternalFrame.actionMap");
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new UIAction("showSystemMenu")
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        JInternalFrame localJInternalFrame = (JInternalFrame)paramAnonymousActionEvent.getSource();
        if ((localJInternalFrame.getUI() instanceof BasicInternalFrameUI))
        {
          JComponent localJComponent = ((BasicInternalFrameUI)localJInternalFrame.getUI()).getNorthPane();
          if ((localJComponent instanceof BasicInternalFrameTitlePane)) {
            ((BasicInternalFrameTitlePane)localJComponent).showSystemMenu();
          }
        }
      }
      
      public boolean isEnabled(Object paramAnonymousObject)
      {
        if ((paramAnonymousObject instanceof JInternalFrame))
        {
          JInternalFrame localJInternalFrame = (JInternalFrame)paramAnonymousObject;
          if ((localJInternalFrame.getUI() instanceof BasicInternalFrameUI)) {
            return ((BasicInternalFrameUI)localJInternalFrame.getUI()).isKeyBindingActive();
          }
        }
        return false;
      }
    });
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  protected void installComponents()
  {
    setNorthPane(createNorthPane(frame));
    setSouthPane(createSouthPane(frame));
    setEastPane(createEastPane(frame));
    setWestPane(createWestPane(frame));
  }
  
  protected void installListeners()
  {
    borderListener = createBorderListener(frame);
    propertyChangeListener = createPropertyChangeListener();
    frame.addPropertyChangeListener(propertyChangeListener);
    installMouseHandlers(frame);
    glassPaneDispatcher = createGlassPaneDispatcher();
    if (glassPaneDispatcher != null)
    {
      frame.getGlassPane().addMouseListener(glassPaneDispatcher);
      frame.getGlassPane().addMouseMotionListener(glassPaneDispatcher);
    }
    componentListener = createComponentListener();
    if (frame.getParent() != null) {
      parentBounds = frame.getParent().getBounds();
    }
    if ((frame.getParent() != null) && (!componentListenerAdded))
    {
      frame.getParent().addComponentListener(componentListener);
      componentListenerAdded = true;
    }
  }
  
  private WindowFocusListener getWindowFocusListener()
  {
    return getHandler();
  }
  
  private void cancelResize()
  {
    if ((resizing) && ((borderListener instanceof BorderListener))) {
      ((BorderListener)borderListener).finishMouseReleased();
    }
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 2) {
      return createInputMap(paramInt);
    }
    return null;
  }
  
  InputMap createInputMap(int paramInt)
  {
    if (paramInt == 2)
    {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(frame, this, "InternalFrame.windowBindings");
      if (arrayOfObject != null) {
        return LookAndFeel.makeComponentInputMap(frame, arrayOfObject);
      }
    }
    return null;
  }
  
  protected void uninstallDefaults()
  {
    Icon localIcon = frame.getFrameIcon();
    if ((localIcon instanceof UIResource)) {
      frame.setFrameIcon(null);
    }
    internalFrameLayout = null;
    frame.setLayout(null);
    LookAndFeel.uninstallBorder(frame);
  }
  
  protected void uninstallComponents()
  {
    setNorthPane(null);
    setSouthPane(null);
    setEastPane(null);
    setWestPane(null);
    if (titlePane != null) {
      titlePane.uninstallDefaults();
    }
    titlePane = null;
  }
  
  protected void uninstallListeners()
  {
    if ((frame.getParent() != null) && (componentListenerAdded))
    {
      frame.getParent().removeComponentListener(componentListener);
      componentListenerAdded = false;
    }
    componentListener = null;
    if (glassPaneDispatcher != null)
    {
      frame.getGlassPane().removeMouseListener(glassPaneDispatcher);
      frame.getGlassPane().removeMouseMotionListener(glassPaneDispatcher);
      glassPaneDispatcher = null;
    }
    deinstallMouseHandlers(frame);
    frame.removePropertyChangeListener(propertyChangeListener);
    propertyChangeListener = null;
    borderListener = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    if (internalFrameListener != null) {
      frame.removeInternalFrameListener(internalFrameListener);
    }
    internalFrameListener = null;
    SwingUtilities.replaceUIInputMap(frame, 2, null);
    SwingUtilities.replaceUIActionMap(frame, null);
  }
  
  void updateFrameCursor()
  {
    if (resizing) {
      return;
    }
    Cursor localCursor = frame.getLastCursor();
    if (localCursor == null) {
      localCursor = Cursor.getPredefinedCursor(0);
    }
    frame.setCursor(localCursor);
  }
  
  protected LayoutManager createLayoutManager()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    if (frame == paramJComponent) {
      return frame.getLayout().preferredLayoutSize(paramJComponent);
    }
    return new Dimension(100, 100);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    if (frame == paramJComponent) {
      return frame.getLayout().minimumLayoutSize(paramJComponent);
    }
    return new Dimension(0, 0);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  protected void replacePane(JComponent paramJComponent1, JComponent paramJComponent2)
  {
    if (paramJComponent1 != null)
    {
      deinstallMouseHandlers(paramJComponent1);
      frame.remove(paramJComponent1);
    }
    if (paramJComponent2 != null)
    {
      frame.add(paramJComponent2);
      installMouseHandlers(paramJComponent2);
    }
  }
  
  protected void deinstallMouseHandlers(JComponent paramJComponent)
  {
    paramJComponent.removeMouseListener(borderListener);
    paramJComponent.removeMouseMotionListener(borderListener);
  }
  
  protected void installMouseHandlers(JComponent paramJComponent)
  {
    paramJComponent.addMouseListener(borderListener);
    paramJComponent.addMouseMotionListener(borderListener);
  }
  
  protected JComponent createNorthPane(JInternalFrame paramJInternalFrame)
  {
    titlePane = new BasicInternalFrameTitlePane(paramJInternalFrame);
    return titlePane;
  }
  
  protected JComponent createSouthPane(JInternalFrame paramJInternalFrame)
  {
    return null;
  }
  
  protected JComponent createWestPane(JInternalFrame paramJInternalFrame)
  {
    return null;
  }
  
  protected JComponent createEastPane(JInternalFrame paramJInternalFrame)
  {
    return null;
  }
  
  protected MouseInputAdapter createBorderListener(JInternalFrame paramJInternalFrame)
  {
    return new BorderListener();
  }
  
  protected void createInternalFrameListener()
  {
    internalFrameListener = getHandler();
  }
  
  protected final boolean isKeyBindingRegistered()
  {
    return keyBindingRegistered;
  }
  
  protected final void setKeyBindingRegistered(boolean paramBoolean)
  {
    keyBindingRegistered = paramBoolean;
  }
  
  public final boolean isKeyBindingActive()
  {
    return keyBindingActive;
  }
  
  protected final void setKeyBindingActive(boolean paramBoolean)
  {
    keyBindingActive = paramBoolean;
  }
  
  protected void setupMenuOpenKey()
  {
    InputMap localInputMap = getInputMap(2);
    SwingUtilities.replaceUIInputMap(frame, 2, localInputMap);
  }
  
  protected void setupMenuCloseKey() {}
  
  public JComponent getNorthPane()
  {
    return northPane;
  }
  
  public void setNorthPane(JComponent paramJComponent)
  {
    if ((northPane != null) && ((northPane instanceof BasicInternalFrameTitlePane))) {
      ((BasicInternalFrameTitlePane)northPane).uninstallListeners();
    }
    replacePane(northPane, paramJComponent);
    northPane = paramJComponent;
    if ((paramJComponent instanceof BasicInternalFrameTitlePane)) {
      titlePane = ((BasicInternalFrameTitlePane)paramJComponent);
    }
  }
  
  public JComponent getSouthPane()
  {
    return southPane;
  }
  
  public void setSouthPane(JComponent paramJComponent)
  {
    southPane = paramJComponent;
  }
  
  public JComponent getWestPane()
  {
    return westPane;
  }
  
  public void setWestPane(JComponent paramJComponent)
  {
    westPane = paramJComponent;
  }
  
  public JComponent getEastPane()
  {
    return eastPane;
  }
  
  public void setEastPane(JComponent paramJComponent)
  {
    eastPane = paramJComponent;
  }
  
  protected DesktopManager getDesktopManager()
  {
    if ((frame.getDesktopPane() != null) && (frame.getDesktopPane().getDesktopManager() != null)) {
      return frame.getDesktopPane().getDesktopManager();
    }
    if (sharedDesktopManager == null) {
      sharedDesktopManager = createDesktopManager();
    }
    return sharedDesktopManager;
  }
  
  protected DesktopManager createDesktopManager()
  {
    return new DefaultDesktopManager();
  }
  
  protected void closeFrame(JInternalFrame paramJInternalFrame)
  {
    BasicLookAndFeel.playSound(frame, "InternalFrame.closeSound");
    getDesktopManager().closeFrame(paramJInternalFrame);
  }
  
  protected void maximizeFrame(JInternalFrame paramJInternalFrame)
  {
    BasicLookAndFeel.playSound(frame, "InternalFrame.maximizeSound");
    getDesktopManager().maximizeFrame(paramJInternalFrame);
  }
  
  protected void minimizeFrame(JInternalFrame paramJInternalFrame)
  {
    if (!paramJInternalFrame.isIcon()) {
      BasicLookAndFeel.playSound(frame, "InternalFrame.restoreDownSound");
    }
    getDesktopManager().minimizeFrame(paramJInternalFrame);
  }
  
  protected void iconifyFrame(JInternalFrame paramJInternalFrame)
  {
    BasicLookAndFeel.playSound(frame, "InternalFrame.minimizeSound");
    getDesktopManager().iconifyFrame(paramJInternalFrame);
  }
  
  protected void deiconifyFrame(JInternalFrame paramJInternalFrame)
  {
    if (!paramJInternalFrame.isMaximum()) {
      BasicLookAndFeel.playSound(frame, "InternalFrame.restoreUpSound");
    }
    getDesktopManager().deiconifyFrame(paramJInternalFrame);
  }
  
  protected void activateFrame(JInternalFrame paramJInternalFrame)
  {
    getDesktopManager().activateFrame(paramJInternalFrame);
  }
  
  protected void deactivateFrame(JInternalFrame paramJInternalFrame)
  {
    getDesktopManager().deactivateFrame(paramJInternalFrame);
  }
  
  protected ComponentListener createComponentListener()
  {
    return getHandler();
  }
  
  protected MouseInputListener createGlassPaneDispatcher()
  {
    return null;
  }
  
  protected class BasicInternalFrameListener
    implements InternalFrameListener
  {
    protected BasicInternalFrameListener() {}
    
    public void internalFrameClosing(InternalFrameEvent paramInternalFrameEvent)
    {
      BasicInternalFrameUI.this.getHandler().internalFrameClosing(paramInternalFrameEvent);
    }
    
    public void internalFrameClosed(InternalFrameEvent paramInternalFrameEvent)
    {
      BasicInternalFrameUI.this.getHandler().internalFrameClosed(paramInternalFrameEvent);
    }
    
    public void internalFrameOpened(InternalFrameEvent paramInternalFrameEvent)
    {
      BasicInternalFrameUI.this.getHandler().internalFrameOpened(paramInternalFrameEvent);
    }
    
    public void internalFrameIconified(InternalFrameEvent paramInternalFrameEvent)
    {
      BasicInternalFrameUI.this.getHandler().internalFrameIconified(paramInternalFrameEvent);
    }
    
    public void internalFrameDeiconified(InternalFrameEvent paramInternalFrameEvent)
    {
      BasicInternalFrameUI.this.getHandler().internalFrameDeiconified(paramInternalFrameEvent);
    }
    
    public void internalFrameActivated(InternalFrameEvent paramInternalFrameEvent)
    {
      BasicInternalFrameUI.this.getHandler().internalFrameActivated(paramInternalFrameEvent);
    }
    
    public void internalFrameDeactivated(InternalFrameEvent paramInternalFrameEvent)
    {
      BasicInternalFrameUI.this.getHandler().internalFrameDeactivated(paramInternalFrameEvent);
    }
  }
  
  protected class BorderListener
    extends MouseInputAdapter
    implements SwingConstants
  {
    int _x;
    int _y;
    int __x;
    int __y;
    Rectangle startingBounds;
    int resizeDir;
    protected final int RESIZE_NONE = 0;
    private boolean discardRelease = false;
    int resizeCornerSize = 16;
    
    protected BorderListener() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      if ((paramMouseEvent.getClickCount() > 1) && (paramMouseEvent.getSource() == getNorthPane())) {
        if ((frame.isIconifiable()) && (frame.isIcon())) {
          try
          {
            frame.setIcon(false);
          }
          catch (PropertyVetoException localPropertyVetoException1) {}
        } else if (frame.isMaximizable()) {
          if (!frame.isMaximum()) {
            try
            {
              frame.setMaximum(true);
            }
            catch (PropertyVetoException localPropertyVetoException2) {}
          } else {
            try
            {
              frame.setMaximum(false);
            }
            catch (PropertyVetoException localPropertyVetoException3) {}
          }
        }
      }
    }
    
    void finishMouseReleased()
    {
      if (discardRelease)
      {
        discardRelease = false;
        return;
      }
      if (resizeDir == 0)
      {
        getDesktopManager().endDraggingFrame(frame);
        dragging = false;
      }
      else
      {
        Window localWindow = SwingUtilities.getWindowAncestor(frame);
        if (localWindow != null) {
          localWindow.removeWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener());
        }
        Container localContainer = frame.getTopLevelAncestor();
        if ((localContainer instanceof RootPaneContainer))
        {
          Component localComponent = ((RootPaneContainer)localContainer).getGlassPane();
          localComponent.setCursor(Cursor.getPredefinedCursor(0));
          localComponent.setVisible(false);
        }
        getDesktopManager().endResizingFrame(frame);
        resizing = false;
        updateFrameCursor();
      }
      _x = 0;
      _y = 0;
      __x = 0;
      __y = 0;
      startingBounds = null;
      resizeDir = 0;
      discardRelease = true;
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      finishMouseReleased();
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      Point localPoint1 = SwingUtilities.convertPoint((Component)paramMouseEvent.getSource(), paramMouseEvent.getX(), paramMouseEvent.getY(), null);
      __x = paramMouseEvent.getX();
      __y = paramMouseEvent.getY();
      _x = x;
      _y = y;
      startingBounds = frame.getBounds();
      resizeDir = 0;
      discardRelease = false;
      try
      {
        frame.setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException) {}
      Insets localInsets = frame.getInsets();
      Point localPoint2 = new Point(__x, __y);
      Object localObject1;
      if (paramMouseEvent.getSource() == getNorthPane())
      {
        localObject1 = getNorthPane().getLocation();
        x += x;
        y += y;
      }
      if ((paramMouseEvent.getSource() == getNorthPane()) && (x > left) && (y > top) && (x < frame.getWidth() - right))
      {
        getDesktopManager().beginDraggingFrame(frame);
        dragging = true;
        return;
      }
      if (!frame.isResizable()) {
        return;
      }
      if ((paramMouseEvent.getSource() == frame) || (paramMouseEvent.getSource() == getNorthPane()))
      {
        if (x <= left)
        {
          if (y < resizeCornerSize + top) {
            resizeDir = 8;
          } else if (y > frame.getHeight() - resizeCornerSize - bottom) {
            resizeDir = 6;
          } else {
            resizeDir = 7;
          }
        }
        else if (x >= frame.getWidth() - right)
        {
          if (y < resizeCornerSize + top) {
            resizeDir = 2;
          } else if (y > frame.getHeight() - resizeCornerSize - bottom) {
            resizeDir = 4;
          } else {
            resizeDir = 3;
          }
        }
        else if (y <= top)
        {
          if (x < resizeCornerSize + left) {
            resizeDir = 8;
          } else if (x > frame.getWidth() - resizeCornerSize - right) {
            resizeDir = 2;
          } else {
            resizeDir = 1;
          }
        }
        else if (y >= frame.getHeight() - bottom)
        {
          if (x < resizeCornerSize + left) {
            resizeDir = 6;
          } else if (x > frame.getWidth() - resizeCornerSize - right) {
            resizeDir = 4;
          } else {
            resizeDir = 5;
          }
        }
        else
        {
          discardRelease = true;
          return;
        }
        localObject1 = Cursor.getPredefinedCursor(0);
        switch (resizeDir)
        {
        case 5: 
          localObject1 = Cursor.getPredefinedCursor(9);
          break;
        case 1: 
          localObject1 = Cursor.getPredefinedCursor(8);
          break;
        case 7: 
          localObject1 = Cursor.getPredefinedCursor(10);
          break;
        case 3: 
          localObject1 = Cursor.getPredefinedCursor(11);
          break;
        case 4: 
          localObject1 = Cursor.getPredefinedCursor(5);
          break;
        case 6: 
          localObject1 = Cursor.getPredefinedCursor(4);
          break;
        case 8: 
          localObject1 = Cursor.getPredefinedCursor(6);
          break;
        case 2: 
          localObject1 = Cursor.getPredefinedCursor(7);
        }
        Container localContainer = frame.getTopLevelAncestor();
        if ((localContainer instanceof RootPaneContainer))
        {
          localObject2 = ((RootPaneContainer)localContainer).getGlassPane();
          ((Component)localObject2).setVisible(true);
          ((Component)localObject2).setCursor((Cursor)localObject1);
        }
        getDesktopManager().beginResizingFrame(frame, resizeDir);
        resizing = true;
        Object localObject2 = SwingUtilities.getWindowAncestor(frame);
        if (localObject2 != null) {
          ((Window)localObject2).addWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener());
        }
        return;
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (startingBounds == null) {
        return;
      }
      Point localPoint = SwingUtilities.convertPoint((Component)paramMouseEvent.getSource(), paramMouseEvent.getX(), paramMouseEvent.getY(), null);
      int i = _x - x;
      int j = _y - y;
      Dimension localDimension1 = frame.getMinimumSize();
      Dimension localDimension2 = frame.getMaximumSize();
      Insets localInsets = frame.getInsets();
      if (dragging)
      {
        if ((frame.isMaximum()) || ((paramMouseEvent.getModifiers() & 0x10) != 16)) {
          return;
        }
        Dimension localDimension3 = frame.getParent().getSize();
        int i2 = width;
        int i3 = height;
        k = startingBounds.x - i;
        m = startingBounds.y - j;
        if (k + left <= -__x) {
          k = -__x - left + 1;
        }
        if (m + top <= -__y) {
          m = -__y - top + 1;
        }
        if (k + __x + right >= i2) {
          k = i2 - __x - right - 1;
        }
        if (m + __y + bottom >= i3) {
          m = i3 - __y - bottom - 1;
        }
        getDesktopManager().dragFrame(frame, k, m);
        return;
      }
      if (!frame.isResizable()) {
        return;
      }
      int k = frame.getX();
      int m = frame.getY();
      int n = frame.getWidth();
      int i1 = frame.getHeight();
      parentBounds = frame.getParent().getBounds();
      switch (resizeDir)
      {
      case 0: 
        return;
      case 1: 
        if (startingBounds.height + j < height) {
          j = -(startingBounds.height - height);
        } else if (startingBounds.height + j > height) {
          j = height - startingBounds.height;
        }
        if (startingBounds.y - j < 0) {
          j = startingBounds.y;
        }
        k = startingBounds.x;
        m = startingBounds.y - j;
        n = startingBounds.width;
        i1 = startingBounds.height + j;
        break;
      case 2: 
        if (startingBounds.height + j < height) {
          j = -(startingBounds.height - height);
        } else if (startingBounds.height + j > height) {
          j = height - startingBounds.height;
        }
        if (startingBounds.y - j < 0) {
          j = startingBounds.y;
        }
        if (startingBounds.width - i < width) {
          i = startingBounds.width - width;
        } else if (startingBounds.width - i > width) {
          i = -(width - startingBounds.width);
        }
        if (startingBounds.x + startingBounds.width - i > parentBounds.width) {
          i = startingBounds.x + startingBounds.width - parentBounds.width;
        }
        k = startingBounds.x;
        m = startingBounds.y - j;
        n = startingBounds.width - i;
        i1 = startingBounds.height + j;
        break;
      case 3: 
        if (startingBounds.width - i < width) {
          i = startingBounds.width - width;
        } else if (startingBounds.width - i > width) {
          i = -(width - startingBounds.width);
        }
        if (startingBounds.x + startingBounds.width - i > parentBounds.width) {
          i = startingBounds.x + startingBounds.width - parentBounds.width;
        }
        n = startingBounds.width - i;
        i1 = startingBounds.height;
        break;
      case 4: 
        if (startingBounds.width - i < width) {
          i = startingBounds.width - width;
        } else if (startingBounds.width - i > width) {
          i = -(width - startingBounds.width);
        }
        if (startingBounds.x + startingBounds.width - i > parentBounds.width) {
          i = startingBounds.x + startingBounds.width - parentBounds.width;
        }
        if (startingBounds.height - j < height) {
          j = startingBounds.height - height;
        } else if (startingBounds.height - j > height) {
          j = -(height - startingBounds.height);
        }
        if (startingBounds.y + startingBounds.height - j > parentBounds.height) {
          j = startingBounds.y + startingBounds.height - parentBounds.height;
        }
        n = startingBounds.width - i;
        i1 = startingBounds.height - j;
        break;
      case 5: 
        if (startingBounds.height - j < height) {
          j = startingBounds.height - height;
        } else if (startingBounds.height - j > height) {
          j = -(height - startingBounds.height);
        }
        if (startingBounds.y + startingBounds.height - j > parentBounds.height) {
          j = startingBounds.y + startingBounds.height - parentBounds.height;
        }
        n = startingBounds.width;
        i1 = startingBounds.height - j;
        break;
      case 6: 
        if (startingBounds.height - j < height) {
          j = startingBounds.height - height;
        } else if (startingBounds.height - j > height) {
          j = -(height - startingBounds.height);
        }
        if (startingBounds.y + startingBounds.height - j > parentBounds.height) {
          j = startingBounds.y + startingBounds.height - parentBounds.height;
        }
        if (startingBounds.width + i < width) {
          i = -(startingBounds.width - width);
        } else if (startingBounds.width + i > width) {
          i = width - startingBounds.width;
        }
        if (startingBounds.x - i < 0) {
          i = startingBounds.x;
        }
        k = startingBounds.x - i;
        m = startingBounds.y;
        n = startingBounds.width + i;
        i1 = startingBounds.height - j;
        break;
      case 7: 
        if (startingBounds.width + i < width) {
          i = -(startingBounds.width - width);
        } else if (startingBounds.width + i > width) {
          i = width - startingBounds.width;
        }
        if (startingBounds.x - i < 0) {
          i = startingBounds.x;
        }
        k = startingBounds.x - i;
        m = startingBounds.y;
        n = startingBounds.width + i;
        i1 = startingBounds.height;
        break;
      case 8: 
        if (startingBounds.width + i < width) {
          i = -(startingBounds.width - width);
        } else if (startingBounds.width + i > width) {
          i = width - startingBounds.width;
        }
        if (startingBounds.x - i < 0) {
          i = startingBounds.x;
        }
        if (startingBounds.height + j < height) {
          j = -(startingBounds.height - height);
        } else if (startingBounds.height + j > height) {
          j = height - startingBounds.height;
        }
        if (startingBounds.y - j < 0) {
          j = startingBounds.y;
        }
        k = startingBounds.x - i;
        m = startingBounds.y - j;
        n = startingBounds.width + i;
        i1 = startingBounds.height + j;
        break;
      default: 
        return;
      }
      getDesktopManager().resizeFrame(frame, k, m, n, i1);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      if (!frame.isResizable()) {
        return;
      }
      if ((paramMouseEvent.getSource() == frame) || (paramMouseEvent.getSource() == getNorthPane()))
      {
        Insets localInsets = frame.getInsets();
        Point localPoint1 = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
        if (paramMouseEvent.getSource() == getNorthPane())
        {
          Point localPoint2 = getNorthPane().getLocation();
          x += x;
          y += y;
        }
        if (x <= left)
        {
          if (y < resizeCornerSize + top) {
            frame.setCursor(Cursor.getPredefinedCursor(6));
          } else if (y > frame.getHeight() - resizeCornerSize - bottom) {
            frame.setCursor(Cursor.getPredefinedCursor(4));
          } else {
            frame.setCursor(Cursor.getPredefinedCursor(10));
          }
        }
        else if (x >= frame.getWidth() - right)
        {
          if (paramMouseEvent.getY() < resizeCornerSize + top) {
            frame.setCursor(Cursor.getPredefinedCursor(7));
          } else if (y > frame.getHeight() - resizeCornerSize - bottom) {
            frame.setCursor(Cursor.getPredefinedCursor(5));
          } else {
            frame.setCursor(Cursor.getPredefinedCursor(11));
          }
        }
        else if (y <= top)
        {
          if (x < resizeCornerSize + left) {
            frame.setCursor(Cursor.getPredefinedCursor(6));
          } else if (x > frame.getWidth() - resizeCornerSize - right) {
            frame.setCursor(Cursor.getPredefinedCursor(7));
          } else {
            frame.setCursor(Cursor.getPredefinedCursor(8));
          }
        }
        else if (y >= frame.getHeight() - bottom)
        {
          if (x < resizeCornerSize + left) {
            frame.setCursor(Cursor.getPredefinedCursor(4));
          } else if (x > frame.getWidth() - resizeCornerSize - right) {
            frame.setCursor(Cursor.getPredefinedCursor(5));
          } else {
            frame.setCursor(Cursor.getPredefinedCursor(9));
          }
        }
        else {
          updateFrameCursor();
        }
        return;
      }
      updateFrameCursor();
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      updateFrameCursor();
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      updateFrameCursor();
    }
  }
  
  protected class ComponentHandler
    implements ComponentListener
  {
    protected ComponentHandler() {}
    
    public void componentResized(ComponentEvent paramComponentEvent)
    {
      BasicInternalFrameUI.this.getHandler().componentResized(paramComponentEvent);
    }
    
    public void componentMoved(ComponentEvent paramComponentEvent)
    {
      BasicInternalFrameUI.this.getHandler().componentMoved(paramComponentEvent);
    }
    
    public void componentShown(ComponentEvent paramComponentEvent)
    {
      BasicInternalFrameUI.this.getHandler().componentShown(paramComponentEvent);
    }
    
    public void componentHidden(ComponentEvent paramComponentEvent)
    {
      BasicInternalFrameUI.this.getHandler().componentHidden(paramComponentEvent);
    }
  }
  
  protected class GlassPaneDispatcher
    implements MouseInputListener
  {
    protected GlassPaneDispatcher() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicInternalFrameUI.this.getHandler().mousePressed(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      BasicInternalFrameUI.this.getHandler().mouseEntered(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicInternalFrameUI.this.getHandler().mouseMoved(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      BasicInternalFrameUI.this.getHandler().mouseExited(paramMouseEvent);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      BasicInternalFrameUI.this.getHandler().mouseClicked(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicInternalFrameUI.this.getHandler().mouseReleased(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      BasicInternalFrameUI.this.getHandler().mouseDragged(paramMouseEvent);
    }
  }
  
  private class Handler
    implements ComponentListener, InternalFrameListener, LayoutManager, MouseInputListener, PropertyChangeListener, WindowFocusListener, SwingConstants
  {
    private Handler() {}
    
    public void windowGainedFocus(WindowEvent paramWindowEvent) {}
    
    public void windowLostFocus(WindowEvent paramWindowEvent)
    {
      BasicInternalFrameUI.this.cancelResize();
    }
    
    public void componentResized(ComponentEvent paramComponentEvent)
    {
      Rectangle localRectangle1 = ((Component)paramComponentEvent.getSource()).getBounds();
      JInternalFrame.JDesktopIcon localJDesktopIcon = null;
      if (frame != null)
      {
        localJDesktopIcon = frame.getDesktopIcon();
        if (frame.isMaximum()) {
          frame.setBounds(0, 0, width, height);
        }
      }
      if (localJDesktopIcon != null)
      {
        Rectangle localRectangle2 = localJDesktopIcon.getBounds();
        int i = y + (height - parentBounds.height);
        localJDesktopIcon.setBounds(x, i, width, height);
      }
      if (!parentBounds.equals(localRectangle1)) {
        parentBounds = localRectangle1;
      }
      if (frame != null) {
        frame.validate();
      }
    }
    
    public void componentMoved(ComponentEvent paramComponentEvent) {}
    
    public void componentShown(ComponentEvent paramComponentEvent) {}
    
    public void componentHidden(ComponentEvent paramComponentEvent) {}
    
    public void internalFrameClosed(InternalFrameEvent paramInternalFrameEvent)
    {
      frame.removeInternalFrameListener(BasicInternalFrameUI.this.getHandler());
    }
    
    public void internalFrameActivated(InternalFrameEvent paramInternalFrameEvent)
    {
      if (!isKeyBindingRegistered())
      {
        setKeyBindingRegistered(true);
        setupMenuOpenKey();
        setupMenuCloseKey();
      }
      if (isKeyBindingRegistered()) {
        setKeyBindingActive(true);
      }
    }
    
    public void internalFrameDeactivated(InternalFrameEvent paramInternalFrameEvent)
    {
      setKeyBindingActive(false);
    }
    
    public void internalFrameClosing(InternalFrameEvent paramInternalFrameEvent) {}
    
    public void internalFrameOpened(InternalFrameEvent paramInternalFrameEvent) {}
    
    public void internalFrameIconified(InternalFrameEvent paramInternalFrameEvent) {}
    
    public void internalFrameDeiconified(InternalFrameEvent paramInternalFrameEvent) {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      Insets localInsets = frame.getInsets();
      Dimension localDimension1 = new Dimension(frame.getRootPane().getPreferredSize());
      width += left + right;
      height += top + bottom;
      Dimension localDimension2;
      if (getNorthPane() != null)
      {
        localDimension2 = getNorthPane().getPreferredSize();
        width = Math.max(width, width);
        height += height;
      }
      if (getSouthPane() != null)
      {
        localDimension2 = getSouthPane().getPreferredSize();
        width = Math.max(width, width);
        height += height;
      }
      if (getEastPane() != null)
      {
        localDimension2 = getEastPane().getPreferredSize();
        width += width;
        height = Math.max(height, height);
      }
      if (getWestPane() != null)
      {
        localDimension2 = getWestPane().getPreferredSize();
        width += width;
        height = Math.max(height, height);
      }
      return localDimension1;
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      Dimension localDimension = new Dimension();
      if ((getNorthPane() != null) && ((getNorthPane() instanceof BasicInternalFrameTitlePane))) {
        localDimension = new Dimension(getNorthPane().getMinimumSize());
      }
      Insets localInsets = frame.getInsets();
      width += left + right;
      height += top + bottom;
      return localDimension;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      Insets localInsets = frame.getInsets();
      int i = left;
      int j = top;
      int k = frame.getWidth() - left - right;
      int m = frame.getHeight() - top - bottom;
      Dimension localDimension;
      if (getNorthPane() != null)
      {
        localDimension = getNorthPane().getPreferredSize();
        if (DefaultLookup.getBoolean(frame, BasicInternalFrameUI.this, "InternalFrame.layoutTitlePaneAtOrigin", false))
        {
          j = 0;
          m += top;
          getNorthPane().setBounds(0, 0, frame.getWidth(), height);
        }
        else
        {
          getNorthPane().setBounds(i, j, k, height);
        }
        j += height;
        m -= height;
      }
      if (getSouthPane() != null)
      {
        localDimension = getSouthPane().getPreferredSize();
        getSouthPane().setBounds(i, frame.getHeight() - bottom - height, k, height);
        m -= height;
      }
      if (getWestPane() != null)
      {
        localDimension = getWestPane().getPreferredSize();
        getWestPane().setBounds(i, j, width, m);
        k -= width;
        i += width;
      }
      if (getEastPane() != null)
      {
        localDimension = getEastPane().getPreferredSize();
        getEastPane().setBounds(k - width, j, width, m);
        k -= width;
      }
      if (frame.getRootPane() != null) {
        frame.getRootPane().setBounds(i, j, k, m);
      }
    }
    
    public void mousePressed(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent) {}
    
    public void mouseDragged(MouseEvent paramMouseEvent) {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      JInternalFrame localJInternalFrame = (JInternalFrame)paramPropertyChangeEvent.getSource();
      Object localObject1 = paramPropertyChangeEvent.getNewValue();
      Object localObject2 = paramPropertyChangeEvent.getOldValue();
      if ("closed" == str)
      {
        if (localObject1 == Boolean.TRUE)
        {
          BasicInternalFrameUI.this.cancelResize();
          if ((frame.getParent() != null) && (componentListenerAdded)) {
            frame.getParent().removeComponentListener(componentListener);
          }
          closeFrame(localJInternalFrame);
        }
      }
      else if ("maximum" == str)
      {
        if (localObject1 == Boolean.TRUE) {
          maximizeFrame(localJInternalFrame);
        } else {
          minimizeFrame(localJInternalFrame);
        }
      }
      else if ("icon" == str)
      {
        if (localObject1 == Boolean.TRUE) {
          iconifyFrame(localJInternalFrame);
        } else {
          deiconifyFrame(localJInternalFrame);
        }
      }
      else if ("selected" == str)
      {
        if ((localObject1 == Boolean.TRUE) && (localObject2 == Boolean.FALSE)) {
          activateFrame(localJInternalFrame);
        } else if ((localObject1 == Boolean.FALSE) && (localObject2 == Boolean.TRUE)) {
          deactivateFrame(localJInternalFrame);
        }
      }
      else if (str == "ancestor")
      {
        if (localObject1 == null) {
          BasicInternalFrameUI.this.cancelResize();
        }
        if (frame.getParent() != null) {
          parentBounds = localJInternalFrame.getParent().getBounds();
        } else {
          parentBounds = null;
        }
        if ((frame.getParent() != null) && (!componentListenerAdded))
        {
          localJInternalFrame.getParent().addComponentListener(componentListener);
          componentListenerAdded = true;
        }
      }
      else if (("title" == str) || (str == "closable") || (str == "iconable") || (str == "maximizable"))
      {
        Dimension localDimension1 = frame.getMinimumSize();
        Dimension localDimension2 = frame.getSize();
        if (width > width) {
          frame.setSize(width, height);
        }
      }
    }
  }
  
  public class InternalFrameLayout
    implements LayoutManager
  {
    public InternalFrameLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent)
    {
      BasicInternalFrameUI.this.getHandler().addLayoutComponent(paramString, paramComponent);
    }
    
    public void removeLayoutComponent(Component paramComponent)
    {
      BasicInternalFrameUI.this.getHandler().removeLayoutComponent(paramComponent);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return BasicInternalFrameUI.this.getHandler().preferredLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return BasicInternalFrameUI.this.getHandler().minimumLayoutSize(paramContainer);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      BasicInternalFrameUI.this.getHandler().layoutContainer(paramContainer);
    }
  }
  
  public class InternalFramePropertyChangeListener
    implements PropertyChangeListener
  {
    public InternalFramePropertyChangeListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicInternalFrameUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */