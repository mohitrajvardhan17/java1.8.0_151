package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

public class ToolTipManager
  extends MouseAdapter
  implements MouseMotionListener
{
  Timer enterTimer = new Timer(750, new insideTimerAction());
  Timer exitTimer;
  Timer insideTimer;
  String toolTipText;
  Point preferredLocation;
  JComponent insideComponent;
  MouseEvent mouseEvent;
  boolean showImmediately;
  private static final Object TOOL_TIP_MANAGER_KEY = new Object();
  transient Popup tipWindow;
  private Window window;
  JToolTip tip;
  private Rectangle popupRect = null;
  private Rectangle popupFrameRect = null;
  boolean enabled = true;
  private boolean tipShowing = false;
  private FocusListener focusChangeListener = null;
  private MouseMotionListener moveBeforeEnterListener = null;
  private KeyListener accessibilityKeyListener = null;
  private KeyStroke postTip;
  private KeyStroke hideTip;
  protected boolean lightWeightPopupEnabled = true;
  protected boolean heavyWeightPopupEnabled = false;
  
  ToolTipManager()
  {
    enterTimer.setRepeats(false);
    exitTimer = new Timer(500, new outsideTimerAction());
    exitTimer.setRepeats(false);
    insideTimer = new Timer(4000, new stillInsideTimerAction());
    insideTimer.setRepeats(false);
    moveBeforeEnterListener = new MoveBeforeEnterListener(null);
    accessibilityKeyListener = new AccessibilityKeyListener(null);
    postTip = KeyStroke.getKeyStroke(112, 2);
    hideTip = KeyStroke.getKeyStroke(27, 0);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    enabled = paramBoolean;
    if (!paramBoolean) {
      hideTipWindow();
    }
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  public void setLightWeightPopupEnabled(boolean paramBoolean)
  {
    lightWeightPopupEnabled = paramBoolean;
  }
  
  public boolean isLightWeightPopupEnabled()
  {
    return lightWeightPopupEnabled;
  }
  
  public void setInitialDelay(int paramInt)
  {
    enterTimer.setInitialDelay(paramInt);
  }
  
  public int getInitialDelay()
  {
    return enterTimer.getInitialDelay();
  }
  
  public void setDismissDelay(int paramInt)
  {
    insideTimer.setInitialDelay(paramInt);
  }
  
  public int getDismissDelay()
  {
    return insideTimer.getInitialDelay();
  }
  
  public void setReshowDelay(int paramInt)
  {
    exitTimer.setInitialDelay(paramInt);
  }
  
  public int getReshowDelay()
  {
    return exitTimer.getInitialDelay();
  }
  
  private GraphicsConfiguration getDrawingGC(Point paramPoint)
  {
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice1 = localGraphicsEnvironment.getScreenDevices();
    for (GraphicsDevice localGraphicsDevice : arrayOfGraphicsDevice1)
    {
      GraphicsConfiguration[] arrayOfGraphicsConfiguration1 = localGraphicsDevice.getConfigurations();
      for (GraphicsConfiguration localGraphicsConfiguration : arrayOfGraphicsConfiguration1)
      {
        Rectangle localRectangle = localGraphicsConfiguration.getBounds();
        if (localRectangle.contains(paramPoint)) {
          return localGraphicsConfiguration;
        }
      }
    }
    return null;
  }
  
  void showTipWindow()
  {
    if ((insideComponent == null) || (!insideComponent.isShowing())) {
      return;
    }
    String str = UIManager.getString("ToolTipManager.enableToolTipMode");
    Object localObject;
    if ("activeApplication".equals(str))
    {
      localObject = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      if (((KeyboardFocusManager)localObject).getFocusedWindow() == null) {
        return;
      }
    }
    if (enabled)
    {
      Point localPoint1 = insideComponent.getLocationOnScreen();
      Point localPoint3;
      if (preferredLocation != null) {
        localPoint3 = new Point(x + preferredLocation.x, y + preferredLocation.y);
      } else {
        localPoint3 = mouseEvent.getLocationOnScreen();
      }
      GraphicsConfiguration localGraphicsConfiguration = getDrawingGC(localPoint3);
      if (localGraphicsConfiguration == null)
      {
        localPoint3 = mouseEvent.getLocationOnScreen();
        localGraphicsConfiguration = getDrawingGC(localPoint3);
        if (localGraphicsConfiguration == null) {
          localGraphicsConfiguration = insideComponent.getGraphicsConfiguration();
        }
      }
      Rectangle localRectangle = localGraphicsConfiguration.getBounds();
      Insets localInsets = Toolkit.getDefaultToolkit().getScreenInsets(localGraphicsConfiguration);
      x += left;
      y += top;
      width -= left + right;
      height -= top + bottom;
      boolean bool = SwingUtilities.isLeftToRight(insideComponent);
      hideTipWindow();
      tip = insideComponent.createToolTip();
      tip.setTipText(toolTipText);
      localObject = tip.getPreferredSize();
      Point localPoint2;
      if (preferredLocation != null)
      {
        localPoint2 = localPoint3;
        if (!bool) {
          x -= width;
        }
      }
      else
      {
        localPoint2 = new Point(x + mouseEvent.getX(), y + mouseEvent.getY() + 20);
        if ((!bool) && (x - width >= 0)) {
          x -= width;
        }
      }
      if (popupRect == null) {
        popupRect = new Rectangle();
      }
      popupRect.setBounds(x, y, width, height);
      if (x < x) {
        x = x;
      } else if (x - x + width > width) {
        x = (x + Math.max(0, width - width));
      }
      if (y < y) {
        y = y;
      } else if (y - y + height > height) {
        y = (y + Math.max(0, height - height));
      }
      PopupFactory localPopupFactory = PopupFactory.getSharedInstance();
      if (lightWeightPopupEnabled)
      {
        int i = getPopupFitHeight(popupRect, insideComponent);
        int j = getPopupFitWidth(popupRect, insideComponent);
        if ((j > 0) || (i > 0)) {
          localPopupFactory.setPopupType(1);
        } else {
          localPopupFactory.setPopupType(0);
        }
      }
      else
      {
        localPopupFactory.setPopupType(1);
      }
      tipWindow = localPopupFactory.getPopup(insideComponent, tip, x, y);
      localPopupFactory.setPopupType(0);
      tipWindow.show();
      Window localWindow = SwingUtilities.windowForComponent(insideComponent);
      window = SwingUtilities.windowForComponent(tip);
      if ((window != null) && (window != localWindow)) {
        window.addMouseListener(this);
      } else {
        window = null;
      }
      insideTimer.start();
      tipShowing = true;
    }
  }
  
  void hideTipWindow()
  {
    if (tipWindow != null)
    {
      if (window != null)
      {
        window.removeMouseListener(this);
        window = null;
      }
      tipWindow.hide();
      tipWindow = null;
      tipShowing = false;
      tip = null;
      insideTimer.stop();
    }
  }
  
  public static ToolTipManager sharedInstance()
  {
    Object localObject = SwingUtilities.appContextGet(TOOL_TIP_MANAGER_KEY);
    if ((localObject instanceof ToolTipManager)) {
      return (ToolTipManager)localObject;
    }
    ToolTipManager localToolTipManager = new ToolTipManager();
    SwingUtilities.appContextPut(TOOL_TIP_MANAGER_KEY, localToolTipManager);
    return localToolTipManager;
  }
  
  public void registerComponent(JComponent paramJComponent)
  {
    paramJComponent.removeMouseListener(this);
    paramJComponent.addMouseListener(this);
    paramJComponent.removeMouseMotionListener(moveBeforeEnterListener);
    paramJComponent.addMouseMotionListener(moveBeforeEnterListener);
    paramJComponent.removeKeyListener(accessibilityKeyListener);
    paramJComponent.addKeyListener(accessibilityKeyListener);
  }
  
  public void unregisterComponent(JComponent paramJComponent)
  {
    paramJComponent.removeMouseListener(this);
    paramJComponent.removeMouseMotionListener(moveBeforeEnterListener);
    paramJComponent.removeKeyListener(accessibilityKeyListener);
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent)
  {
    initiateToolTip(paramMouseEvent);
  }
  
  private void initiateToolTip(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent.getSource() == window) {
      return;
    }
    JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
    localJComponent.removeMouseMotionListener(moveBeforeEnterListener);
    exitTimer.stop();
    Point localPoint1 = paramMouseEvent.getPoint();
    if ((x < 0) || (x >= localJComponent.getWidth()) || (y < 0) || (y >= localJComponent.getHeight())) {
      return;
    }
    if (insideComponent != null) {
      enterTimer.stop();
    }
    localJComponent.removeMouseMotionListener(this);
    localJComponent.addMouseMotionListener(this);
    int i = insideComponent == localJComponent ? 1 : 0;
    insideComponent = localJComponent;
    if (tipWindow != null)
    {
      mouseEvent = paramMouseEvent;
      if (showImmediately)
      {
        String str = localJComponent.getToolTipText(paramMouseEvent);
        Point localPoint2 = localJComponent.getToolTipLocation(paramMouseEvent);
        int j = localPoint2 == null ? 1 : preferredLocation != null ? preferredLocation.equals(localPoint2) : 0;
        if ((i == 0) || (!toolTipText.equals(str)) || (j == 0))
        {
          toolTipText = str;
          preferredLocation = localPoint2;
          showTipWindow();
        }
      }
      else
      {
        enterTimer.start();
      }
    }
  }
  
  public void mouseExited(MouseEvent paramMouseEvent)
  {
    int i = 1;
    Object localObject;
    Point localPoint1;
    if ((insideComponent != null) || ((window != null) && (paramMouseEvent.getSource() == window) && (insideComponent != null)))
    {
      localObject = insideComponent.getTopLevelAncestor();
      if (localObject != null)
      {
        localPoint1 = paramMouseEvent.getPoint();
        SwingUtilities.convertPointToScreen(localPoint1, window);
        x -= ((Container)localObject).getX();
        y -= ((Container)localObject).getY();
        localPoint1 = SwingUtilities.convertPoint(null, localPoint1, insideComponent);
        if ((x >= 0) && (x < insideComponent.getWidth()) && (y >= 0) && (y < insideComponent.getHeight())) {
          i = 0;
        } else {
          i = 1;
        }
      }
    }
    else if ((paramMouseEvent.getSource() == insideComponent) && (tipWindow != null))
    {
      localObject = SwingUtilities.getWindowAncestor(insideComponent);
      if (localObject != null)
      {
        localPoint1 = SwingUtilities.convertPoint(insideComponent, paramMouseEvent.getPoint(), (Component)localObject);
        Rectangle localRectangle = insideComponent.getTopLevelAncestor().getBounds();
        x += x;
        y += y;
        Point localPoint2 = new Point(0, 0);
        SwingUtilities.convertPointToScreen(localPoint2, tip);
        x = x;
        y = y;
        width = tip.getWidth();
        height = tip.getHeight();
        if ((x >= x) && (x < x + width) && (y >= y) && (y < y + height)) {
          i = 0;
        } else {
          i = 1;
        }
      }
    }
    if (i != 0)
    {
      enterTimer.stop();
      if (insideComponent != null) {
        insideComponent.removeMouseMotionListener(this);
      }
      insideComponent = null;
      toolTipText = null;
      mouseEvent = null;
      hideTipWindow();
      exitTimer.restart();
    }
  }
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    hideTipWindow();
    enterTimer.stop();
    showImmediately = false;
    insideComponent = null;
    mouseEvent = null;
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {}
  
  public void mouseMoved(MouseEvent paramMouseEvent)
  {
    if (tipShowing)
    {
      checkForTipChange(paramMouseEvent);
    }
    else if (showImmediately)
    {
      JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
      toolTipText = localJComponent.getToolTipText(paramMouseEvent);
      if (toolTipText != null)
      {
        preferredLocation = localJComponent.getToolTipLocation(paramMouseEvent);
        mouseEvent = paramMouseEvent;
        insideComponent = localJComponent;
        exitTimer.stop();
        showTipWindow();
      }
    }
    else
    {
      insideComponent = ((JComponent)paramMouseEvent.getSource());
      mouseEvent = paramMouseEvent;
      toolTipText = null;
      enterTimer.restart();
    }
  }
  
  private void checkForTipChange(MouseEvent paramMouseEvent)
  {
    JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
    String str = localJComponent.getToolTipText(paramMouseEvent);
    Point localPoint = localJComponent.getToolTipLocation(paramMouseEvent);
    if ((str != null) || (localPoint != null))
    {
      mouseEvent = paramMouseEvent;
      if (((str != null) && (str.equals(toolTipText))) || ((str == null) && (((localPoint != null) && (localPoint.equals(preferredLocation))) || (localPoint == null))))
      {
        if (tipWindow != null) {
          insideTimer.restart();
        } else {
          enterTimer.restart();
        }
      }
      else
      {
        toolTipText = str;
        preferredLocation = localPoint;
        if (showImmediately)
        {
          hideTipWindow();
          showTipWindow();
          exitTimer.stop();
        }
        else
        {
          enterTimer.restart();
        }
      }
    }
    else
    {
      toolTipText = null;
      preferredLocation = null;
      mouseEvent = null;
      insideComponent = null;
      hideTipWindow();
      enterTimer.stop();
      exitTimer.restart();
    }
  }
  
  static Frame frameForComponent(Component paramComponent)
  {
    while (!(paramComponent instanceof Frame)) {
      paramComponent = paramComponent.getParent();
    }
    return (Frame)paramComponent;
  }
  
  private FocusListener createFocusChangeListener()
  {
    new FocusAdapter()
    {
      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
        hideTipWindow();
        insideComponent = null;
        JComponent localJComponent = (JComponent)paramAnonymousFocusEvent.getSource();
        localJComponent.removeFocusListener(focusChangeListener);
      }
    };
  }
  
  private int getPopupFitWidth(Rectangle paramRectangle, Component paramComponent)
  {
    if (paramComponent != null) {
      for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent())
      {
        if (((localContainer instanceof JFrame)) || ((localContainer instanceof JDialog)) || ((localContainer instanceof JWindow))) {
          return getWidthAdjust(localContainer.getBounds(), paramRectangle);
        }
        if (((localContainer instanceof JApplet)) || ((localContainer instanceof JInternalFrame)))
        {
          if (popupFrameRect == null) {
            popupFrameRect = new Rectangle();
          }
          Point localPoint = localContainer.getLocationOnScreen();
          popupFrameRect.setBounds(x, y, getBoundswidth, getBoundsheight);
          return getWidthAdjust(popupFrameRect, paramRectangle);
        }
      }
    }
    return 0;
  }
  
  private int getPopupFitHeight(Rectangle paramRectangle, Component paramComponent)
  {
    if (paramComponent != null) {
      for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent())
      {
        if (((localContainer instanceof JFrame)) || ((localContainer instanceof JDialog)) || ((localContainer instanceof JWindow))) {
          return getHeightAdjust(localContainer.getBounds(), paramRectangle);
        }
        if (((localContainer instanceof JApplet)) || ((localContainer instanceof JInternalFrame)))
        {
          if (popupFrameRect == null) {
            popupFrameRect = new Rectangle();
          }
          Point localPoint = localContainer.getLocationOnScreen();
          popupFrameRect.setBounds(x, y, getBoundswidth, getBoundsheight);
          return getHeightAdjust(popupFrameRect, paramRectangle);
        }
      }
    }
    return 0;
  }
  
  private int getHeightAdjust(Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    if ((y >= y) && (y + height <= y + height)) {
      return 0;
    }
    return y + height - (y + height) + 5;
  }
  
  private int getWidthAdjust(Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    if ((x >= x) && (x + width <= x + width)) {
      return 0;
    }
    return x + width - (x + width) + 5;
  }
  
  private void show(JComponent paramJComponent)
  {
    if (tipWindow != null)
    {
      hideTipWindow();
      insideComponent = null;
    }
    else
    {
      hideTipWindow();
      enterTimer.stop();
      exitTimer.stop();
      insideTimer.stop();
      insideComponent = paramJComponent;
      if (insideComponent != null)
      {
        toolTipText = insideComponent.getToolTipText();
        preferredLocation = new Point(10, insideComponent.getHeight() + 10);
        showTipWindow();
        if (focusChangeListener == null) {
          focusChangeListener = createFocusChangeListener();
        }
        insideComponent.addFocusListener(focusChangeListener);
      }
    }
  }
  
  private void hide(JComponent paramJComponent)
  {
    hideTipWindow();
    paramJComponent.removeFocusListener(focusChangeListener);
    preferredLocation = null;
    insideComponent = null;
  }
  
  private class AccessibilityKeyListener
    extends KeyAdapter
  {
    private AccessibilityKeyListener() {}
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      if (!paramKeyEvent.isConsumed())
      {
        JComponent localJComponent = (JComponent)paramKeyEvent.getComponent();
        KeyStroke localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramKeyEvent);
        if (hideTip.equals(localKeyStroke))
        {
          if (tipWindow != null)
          {
            ToolTipManager.this.hide(localJComponent);
            paramKeyEvent.consume();
          }
        }
        else if (postTip.equals(localKeyStroke))
        {
          ToolTipManager.this.show(localJComponent);
          paramKeyEvent.consume();
        }
      }
    }
  }
  
  private class MoveBeforeEnterListener
    extends MouseMotionAdapter
  {
    private MoveBeforeEnterListener() {}
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      ToolTipManager.this.initiateToolTip(paramMouseEvent);
    }
  }
  
  protected class insideTimerAction
    implements ActionListener
  {
    protected insideTimerAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if ((insideComponent != null) && (insideComponent.isShowing()))
      {
        if ((toolTipText == null) && (mouseEvent != null))
        {
          toolTipText = insideComponent.getToolTipText(mouseEvent);
          preferredLocation = insideComponent.getToolTipLocation(mouseEvent);
        }
        if (toolTipText != null)
        {
          showImmediately = true;
          showTipWindow();
        }
        else
        {
          insideComponent = null;
          toolTipText = null;
          preferredLocation = null;
          mouseEvent = null;
          hideTipWindow();
        }
      }
    }
  }
  
  protected class outsideTimerAction
    implements ActionListener
  {
    protected outsideTimerAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      showImmediately = false;
    }
  }
  
  protected class stillInsideTimerAction
    implements ActionListener
  {
    protected stillInsideTimerAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      hideTipWindow();
      enterTimer.stop();
      showImmediately = false;
      insideComponent = null;
      mouseEvent = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ToolTipManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */