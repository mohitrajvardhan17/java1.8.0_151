package javax.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.util.Iterator;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.swing.event.EventListenerList;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.basic.BasicComboPopup;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;

public class JPopupMenu
  extends JComponent
  implements Accessible, MenuElement
{
  private static final String uiClassID = "PopupMenuUI";
  private static final Object defaultLWPopupEnabledKey = new StringBuffer("JPopupMenu.defaultLWPopupEnabledKey");
  static boolean popupPostionFixDisabled = ((String)AccessController.doPrivileged(new GetPropertyAction("javax.swing.adjustPopupLocationToFit", ""))).equals("false");
  transient Component invoker;
  transient Popup popup;
  transient Frame frame;
  private int desiredLocationX;
  private int desiredLocationY;
  private String label = null;
  private boolean paintBorder = true;
  private Insets margin = null;
  private boolean lightWeightPopup = true;
  private SingleSelectionModel selectionModel;
  private static final Object classLock = new Object();
  private static final boolean TRACE = false;
  private static final boolean VERBOSE = false;
  private static final boolean DEBUG = false;
  
  public static void setDefaultLightWeightPopupEnabled(boolean paramBoolean)
  {
    SwingUtilities.appContextPut(defaultLWPopupEnabledKey, Boolean.valueOf(paramBoolean));
  }
  
  public static boolean getDefaultLightWeightPopupEnabled()
  {
    Boolean localBoolean = (Boolean)SwingUtilities.appContextGet(defaultLWPopupEnabledKey);
    if (localBoolean == null)
    {
      SwingUtilities.appContextPut(defaultLWPopupEnabledKey, Boolean.TRUE);
      return true;
    }
    return localBoolean.booleanValue();
  }
  
  public JPopupMenu()
  {
    this(null);
  }
  
  public JPopupMenu(String paramString)
  {
    label = paramString;
    lightWeightPopup = getDefaultLightWeightPopupEnabled();
    setSelectionModel(new DefaultSingleSelectionModel());
    enableEvents(16L);
    setFocusTraversalKeysEnabled(false);
    updateUI();
  }
  
  public PopupMenuUI getUI()
  {
    return (PopupMenuUI)ui;
  }
  
  public void setUI(PopupMenuUI paramPopupMenuUI)
  {
    super.setUI(paramPopupMenuUI);
  }
  
  public void updateUI()
  {
    setUI((PopupMenuUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "PopupMenuUI";
  }
  
  protected void processFocusEvent(FocusEvent paramFocusEvent)
  {
    super.processFocusEvent(paramFocusEvent);
  }
  
  protected void processKeyEvent(KeyEvent paramKeyEvent)
  {
    MenuSelectionManager.defaultManager().processKeyEvent(paramKeyEvent);
    if (paramKeyEvent.isConsumed()) {
      return;
    }
    super.processKeyEvent(paramKeyEvent);
  }
  
  public SingleSelectionModel getSelectionModel()
  {
    return selectionModel;
  }
  
  public void setSelectionModel(SingleSelectionModel paramSingleSelectionModel)
  {
    selectionModel = paramSingleSelectionModel;
  }
  
  public JMenuItem add(JMenuItem paramJMenuItem)
  {
    super.add(paramJMenuItem);
    return paramJMenuItem;
  }
  
  public JMenuItem add(String paramString)
  {
    return add(new JMenuItem(paramString));
  }
  
  public JMenuItem add(Action paramAction)
  {
    JMenuItem localJMenuItem = createActionComponent(paramAction);
    localJMenuItem.setAction(paramAction);
    add(localJMenuItem);
    return localJMenuItem;
  }
  
  Point adjustPopupLocationToFitScreen(int paramInt1, int paramInt2)
  {
    Point localPoint = new Point(paramInt1, paramInt2);
    if ((popupPostionFixDisabled == true) || (GraphicsEnvironment.isHeadless())) {
      return localPoint;
    }
    GraphicsConfiguration localGraphicsConfiguration = getCurrentGraphicsConfiguration(localPoint);
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    Rectangle localRectangle;
    if (localGraphicsConfiguration != null) {
      localRectangle = localGraphicsConfiguration.getBounds();
    } else {
      localRectangle = new Rectangle(localToolkit.getScreenSize());
    }
    Dimension localDimension = getPreferredSize();
    long l1 = x + width;
    long l2 = y + height;
    int i = width;
    int j = height;
    if (!canPopupOverlapTaskBar())
    {
      Insets localInsets = localToolkit.getScreenInsets(localGraphicsConfiguration);
      x += left;
      y += top;
      i -= left + right;
      j -= top + bottom;
    }
    int k = x + i;
    int m = y + j;
    if (l1 > k) {
      x = (k - width);
    }
    if (l2 > m) {
      y = (m - height);
    }
    if (x < x) {
      x = x;
    }
    if (y < y) {
      y = y;
    }
    return localPoint;
  }
  
  private GraphicsConfiguration getCurrentGraphicsConfiguration(Point paramPoint)
  {
    Object localObject = null;
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice = localGraphicsEnvironment.getScreenDevices();
    for (int i = 0; i < arrayOfGraphicsDevice.length; i++) {
      if (arrayOfGraphicsDevice[i].getType() == 0)
      {
        GraphicsConfiguration localGraphicsConfiguration = arrayOfGraphicsDevice[i].getDefaultConfiguration();
        if (localGraphicsConfiguration.getBounds().contains(paramPoint))
        {
          localObject = localGraphicsConfiguration;
          break;
        }
      }
    }
    if ((localObject == null) && (getInvoker() != null)) {
      localObject = getInvoker().getGraphicsConfiguration();
    }
    return (GraphicsConfiguration)localObject;
  }
  
  static boolean canPopupOverlapTaskBar()
  {
    boolean bool = true;
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit)) {
      bool = ((SunToolkit)localToolkit).canPopupOverlapTaskBar();
    }
    return bool;
  }
  
  protected JMenuItem createActionComponent(Action paramAction)
  {
    JMenuItem local1 = new JMenuItem()
    {
      protected PropertyChangeListener createActionPropertyChangeListener(Action paramAnonymousAction)
      {
        PropertyChangeListener localPropertyChangeListener = createActionChangeListener(this);
        if (localPropertyChangeListener == null) {
          localPropertyChangeListener = super.createActionPropertyChangeListener(paramAnonymousAction);
        }
        return localPropertyChangeListener;
      }
    };
    local1.setHorizontalTextPosition(11);
    local1.setVerticalTextPosition(0);
    return local1;
  }
  
  protected PropertyChangeListener createActionChangeListener(JMenuItem paramJMenuItem)
  {
    return paramJMenuItem.createActionPropertyChangeListener0(paramJMenuItem.getAction());
  }
  
  public void remove(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    if (paramInt > getComponentCount() - 1) {
      throw new IllegalArgumentException("index greater than the number of items.");
    }
    super.remove(paramInt);
  }
  
  public void setLightWeightPopupEnabled(boolean paramBoolean)
  {
    lightWeightPopup = paramBoolean;
  }
  
  public boolean isLightWeightPopupEnabled()
  {
    return lightWeightPopup;
  }
  
  public String getLabel()
  {
    return label;
  }
  
  public void setLabel(String paramString)
  {
    String str = label;
    label = paramString;
    firePropertyChange("label", str, paramString);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString);
    }
    invalidate();
    repaint();
  }
  
  public void addSeparator()
  {
    add(new Separator());
  }
  
  public void insert(Action paramAction, int paramInt)
  {
    JMenuItem localJMenuItem = createActionComponent(paramAction);
    localJMenuItem.setAction(paramAction);
    insert(localJMenuItem, paramInt);
  }
  
  public void insert(Component paramComponent, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index less than zero.");
    }
    int i = getComponentCount();
    Vector localVector = new Vector();
    for (int j = paramInt; j < i; j++)
    {
      localVector.addElement(getComponent(paramInt));
      remove(paramInt);
    }
    add(paramComponent);
    Iterator localIterator = localVector.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      add(localComponent);
    }
  }
  
  public void addPopupMenuListener(PopupMenuListener paramPopupMenuListener)
  {
    listenerList.add(PopupMenuListener.class, paramPopupMenuListener);
  }
  
  public void removePopupMenuListener(PopupMenuListener paramPopupMenuListener)
  {
    listenerList.remove(PopupMenuListener.class, paramPopupMenuListener);
  }
  
  public PopupMenuListener[] getPopupMenuListeners()
  {
    return (PopupMenuListener[])listenerList.getListeners(PopupMenuListener.class);
  }
  
  public void addMenuKeyListener(MenuKeyListener paramMenuKeyListener)
  {
    listenerList.add(MenuKeyListener.class, paramMenuKeyListener);
  }
  
  public void removeMenuKeyListener(MenuKeyListener paramMenuKeyListener)
  {
    listenerList.remove(MenuKeyListener.class, paramMenuKeyListener);
  }
  
  public MenuKeyListener[] getMenuKeyListeners()
  {
    return (MenuKeyListener[])listenerList.getListeners(MenuKeyListener.class);
  }
  
  protected void firePopupMenuWillBecomeVisible()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    PopupMenuEvent localPopupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class)
      {
        if (localPopupMenuEvent == null) {
          localPopupMenuEvent = new PopupMenuEvent(this);
        }
        ((PopupMenuListener)arrayOfObject[(i + 1)]).popupMenuWillBecomeVisible(localPopupMenuEvent);
      }
    }
  }
  
  protected void firePopupMenuWillBecomeInvisible()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    PopupMenuEvent localPopupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class)
      {
        if (localPopupMenuEvent == null) {
          localPopupMenuEvent = new PopupMenuEvent(this);
        }
        ((PopupMenuListener)arrayOfObject[(i + 1)]).popupMenuWillBecomeInvisible(localPopupMenuEvent);
      }
    }
  }
  
  protected void firePopupMenuCanceled()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    PopupMenuEvent localPopupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class)
      {
        if (localPopupMenuEvent == null) {
          localPopupMenuEvent = new PopupMenuEvent(this);
        }
        ((PopupMenuListener)arrayOfObject[(i + 1)]).popupMenuCanceled(localPopupMenuEvent);
      }
    }
  }
  
  boolean alwaysOnTop()
  {
    return true;
  }
  
  public void pack()
  {
    if (popup != null)
    {
      Dimension localDimension = getPreferredSize();
      if ((localDimension == null) || (width != getWidth()) || (height != getHeight())) {
        showPopup();
      } else {
        validate();
      }
    }
  }
  
  public void setVisible(boolean paramBoolean)
  {
    if (paramBoolean == isVisible()) {
      return;
    }
    Object localObject;
    if (!paramBoolean)
    {
      localObject = (Boolean)getClientProperty("JPopupMenu.firePopupMenuCanceled");
      if ((localObject != null) && (localObject == Boolean.TRUE))
      {
        putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.FALSE);
        firePopupMenuCanceled();
      }
      getSelectionModel().clearSelection();
    }
    else if (isPopupMenu())
    {
      localObject = new MenuElement[1];
      localObject[0] = this;
      MenuSelectionManager.defaultManager().setSelectedPath((MenuElement[])localObject);
    }
    if (paramBoolean)
    {
      firePopupMenuWillBecomeVisible();
      showPopup();
      firePropertyChange("visible", Boolean.FALSE, Boolean.TRUE);
    }
    else if (popup != null)
    {
      firePopupMenuWillBecomeInvisible();
      popup.hide();
      popup = null;
      firePropertyChange("visible", Boolean.TRUE, Boolean.FALSE);
      if (isPopupMenu()) {
        MenuSelectionManager.defaultManager().clearSelectedPath();
      }
    }
  }
  
  private void showPopup()
  {
    Popup localPopup1 = popup;
    if (localPopup1 != null) {
      localPopup1.hide();
    }
    PopupFactory localPopupFactory = PopupFactory.getSharedInstance();
    if (isLightWeightPopupEnabled()) {
      localPopupFactory.setPopupType(0);
    } else {
      localPopupFactory.setPopupType(2);
    }
    Point localPoint = adjustPopupLocationToFitScreen(desiredLocationX, desiredLocationY);
    desiredLocationX = x;
    desiredLocationY = y;
    Popup localPopup2 = getUI().getPopup(this, desiredLocationX, desiredLocationY);
    localPopupFactory.setPopupType(0);
    popup = localPopup2;
    localPopup2.show();
  }
  
  public boolean isVisible()
  {
    return popup != null;
  }
  
  public void setLocation(int paramInt1, int paramInt2)
  {
    int i = desiredLocationX;
    int j = desiredLocationY;
    desiredLocationX = paramInt1;
    desiredLocationY = paramInt2;
    if ((popup != null) && ((paramInt1 != i) || (paramInt2 != j))) {
      showPopup();
    }
  }
  
  private boolean isPopupMenu()
  {
    return (invoker != null) && (!(invoker instanceof JMenu));
  }
  
  public Component getInvoker()
  {
    return invoker;
  }
  
  public void setInvoker(Component paramComponent)
  {
    Component localComponent = invoker;
    invoker = paramComponent;
    if ((localComponent != invoker) && (ui != null))
    {
      ui.uninstallUI(this);
      ui.installUI(this);
    }
    invalidate();
  }
  
  public void show(Component paramComponent, int paramInt1, int paramInt2)
  {
    setInvoker(paramComponent);
    Frame localFrame = getFrame(paramComponent);
    if ((localFrame != frame) && (localFrame != null))
    {
      frame = localFrame;
      if (popup != null) {
        setVisible(false);
      }
    }
    if (paramComponent != null)
    {
      Point localPoint = paramComponent.getLocationOnScreen();
      long l1 = x + paramInt1;
      long l2 = y + paramInt2;
      if (l1 > 2147483647L) {
        l1 = 2147483647L;
      }
      if (l1 < -2147483648L) {
        l1 = -2147483648L;
      }
      if (l2 > 2147483647L) {
        l2 = 2147483647L;
      }
      if (l2 < -2147483648L) {
        l2 = -2147483648L;
      }
      setLocation((int)l1, (int)l2);
    }
    else
    {
      setLocation(paramInt1, paramInt2);
    }
    setVisible(true);
  }
  
  JPopupMenu getRootPopupMenu()
  {
    for (JPopupMenu localJPopupMenu = this; (localJPopupMenu != null) && (localJPopupMenu.isPopupMenu() != true) && (localJPopupMenu.getInvoker() != null) && (localJPopupMenu.getInvoker().getParent() != null) && ((localJPopupMenu.getInvoker().getParent() instanceof JPopupMenu)); localJPopupMenu = (JPopupMenu)localJPopupMenu.getInvoker().getParent()) {}
    return localJPopupMenu;
  }
  
  @Deprecated
  public Component getComponentAtIndex(int paramInt)
  {
    return getComponent(paramInt);
  }
  
  public int getComponentIndex(Component paramComponent)
  {
    int i = getComponentCount();
    Component[] arrayOfComponent = getComponents();
    for (int j = 0; j < i; j++)
    {
      Component localComponent = arrayOfComponent[j];
      if (localComponent == paramComponent) {
        return j;
      }
    }
    return -1;
  }
  
  public void setPopupSize(Dimension paramDimension)
  {
    Dimension localDimension1 = getPreferredSize();
    setPreferredSize(paramDimension);
    if (popup != null)
    {
      Dimension localDimension2 = getPreferredSize();
      if (!localDimension1.equals(localDimension2)) {
        showPopup();
      }
    }
  }
  
  public void setPopupSize(int paramInt1, int paramInt2)
  {
    setPopupSize(new Dimension(paramInt1, paramInt2));
  }
  
  public void setSelected(Component paramComponent)
  {
    SingleSelectionModel localSingleSelectionModel = getSelectionModel();
    int i = getComponentIndex(paramComponent);
    localSingleSelectionModel.setSelectedIndex(i);
  }
  
  public boolean isBorderPainted()
  {
    return paintBorder;
  }
  
  public void setBorderPainted(boolean paramBoolean)
  {
    paintBorder = paramBoolean;
    repaint();
  }
  
  protected void paintBorder(Graphics paramGraphics)
  {
    if (isBorderPainted()) {
      super.paintBorder(paramGraphics);
    }
  }
  
  public Insets getMargin()
  {
    if (margin == null) {
      return new Insets(0, 0, 0, 0);
    }
    return margin;
  }
  
  boolean isSubPopupMenu(JPopupMenu paramJPopupMenu)
  {
    int i = getComponentCount();
    Component[] arrayOfComponent = getComponents();
    for (int j = 0; j < i; j++)
    {
      Component localComponent = arrayOfComponent[j];
      if ((localComponent instanceof JMenu))
      {
        JMenu localJMenu = (JMenu)localComponent;
        JPopupMenu localJPopupMenu = localJMenu.getPopupMenu();
        if (localJPopupMenu == paramJPopupMenu) {
          return true;
        }
        if (localJPopupMenu.isSubPopupMenu(paramJPopupMenu)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static Frame getFrame(Component paramComponent)
  {
    for (Object localObject = paramComponent; (!(localObject instanceof Frame)) && (localObject != null); localObject = ((Component)localObject).getParent()) {}
    return (Frame)localObject;
  }
  
  protected String paramString()
  {
    String str1 = label != null ? label : "";
    String str2 = paintBorder ? "true" : "false";
    String str3 = margin != null ? margin.toString() : "";
    String str4 = isLightWeightPopupEnabled() ? "true" : "false";
    return super.paramString() + ",desiredLocationX=" + desiredLocationX + ",desiredLocationY=" + desiredLocationY + ",label=" + str1 + ",lightWeightPopupEnabled=" + str4 + ",margin=" + str3 + ",paintBorder=" + str2;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJPopupMenu();
    }
    return accessibleContext;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if ((invoker != null) && ((invoker instanceof Serializable)))
    {
      localVector.addElement("invoker");
      localVector.addElement(invoker);
    }
    if ((popup != null) && ((popup instanceof Serializable)))
    {
      localVector.addElement("popup");
      localVector.addElement(popup);
    }
    paramObjectOutputStream.writeObject(localVector);
    if (getUIClassID().equals("PopupMenuUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Vector localVector = (Vector)paramObjectInputStream.readObject();
    int i = 0;
    int j = localVector.size();
    if ((i < j) && (localVector.elementAt(i).equals("invoker")))
    {
      invoker = ((Component)localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("popup")))
    {
      popup = ((Popup)localVector.elementAt(++i));
      i++;
    }
  }
  
  public void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {}
  
  public void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    MenuKeyEvent localMenuKeyEvent = new MenuKeyEvent(paramKeyEvent.getComponent(), paramKeyEvent.getID(), paramKeyEvent.getWhen(), paramKeyEvent.getModifiers(), paramKeyEvent.getKeyCode(), paramKeyEvent.getKeyChar(), paramArrayOfMenuElement, paramMenuSelectionManager);
    processMenuKeyEvent(localMenuKeyEvent);
    if (localMenuKeyEvent.isConsumed()) {
      paramKeyEvent.consume();
    }
  }
  
  private void processMenuKeyEvent(MenuKeyEvent paramMenuKeyEvent)
  {
    switch (paramMenuKeyEvent.getID())
    {
    case 401: 
      fireMenuKeyPressed(paramMenuKeyEvent);
      break;
    case 402: 
      fireMenuKeyReleased(paramMenuKeyEvent);
      break;
    case 400: 
      fireMenuKeyTyped(paramMenuKeyEvent);
      break;
    }
  }
  
  private void fireMenuKeyPressed(MenuKeyEvent paramMenuKeyEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class) {
        ((MenuKeyListener)arrayOfObject[(i + 1)]).menuKeyPressed(paramMenuKeyEvent);
      }
    }
  }
  
  private void fireMenuKeyReleased(MenuKeyEvent paramMenuKeyEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class) {
        ((MenuKeyListener)arrayOfObject[(i + 1)]).menuKeyReleased(paramMenuKeyEvent);
      }
    }
  }
  
  private void fireMenuKeyTyped(MenuKeyEvent paramMenuKeyEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class) {
        ((MenuKeyListener)arrayOfObject[(i + 1)]).menuKeyTyped(paramMenuKeyEvent);
      }
    }
  }
  
  public void menuSelectionChanged(boolean paramBoolean)
  {
    if ((invoker instanceof JMenu))
    {
      JMenu localJMenu = (JMenu)invoker;
      if (paramBoolean) {
        localJMenu.setPopupMenuVisible(true);
      } else {
        localJMenu.setPopupMenuVisible(false);
      }
    }
    if ((isPopupMenu()) && (!paramBoolean)) {
      setVisible(false);
    }
  }
  
  public MenuElement[] getSubElements()
  {
    Vector localVector = new Vector();
    int i = getComponentCount();
    for (int j = 0; j < i; j++)
    {
      Component localComponent = getComponent(j);
      if ((localComponent instanceof MenuElement)) {
        localVector.addElement((MenuElement)localComponent);
      }
    }
    MenuElement[] arrayOfMenuElement = new MenuElement[localVector.size()];
    j = 0;
    i = localVector.size();
    while (j < i)
    {
      arrayOfMenuElement[j] = ((MenuElement)localVector.elementAt(j));
      j++;
    }
    return arrayOfMenuElement;
  }
  
  public Component getComponent()
  {
    return this;
  }
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent)
  {
    return getUI().isPopupTrigger(paramMouseEvent);
  }
  
  protected class AccessibleJPopupMenu
    extends JComponent.AccessibleJComponent
    implements PropertyChangeListener
  {
    protected AccessibleJPopupMenu()
    {
      super();
      addPropertyChangeListener(this);
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.POPUP_MENU;
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str == "visible") {
        if ((paramPropertyChangeEvent.getOldValue() == Boolean.FALSE) && (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE)) {
          handlePopupIsVisibleEvent(true);
        } else if ((paramPropertyChangeEvent.getOldValue() == Boolean.TRUE) && (paramPropertyChangeEvent.getNewValue() == Boolean.FALSE)) {
          handlePopupIsVisibleEvent(false);
        }
      }
    }
    
    private void handlePopupIsVisibleEvent(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
        fireActiveDescendant();
      }
      else
      {
        firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
      }
    }
    
    private void fireActiveDescendant()
    {
      if ((JPopupMenu.this instanceof BasicComboPopup))
      {
        JList localJList = ((BasicComboPopup)JPopupMenu.this).getList();
        if (localJList == null) {
          return;
        }
        AccessibleContext localAccessibleContext1 = localJList.getAccessibleContext();
        AccessibleSelection localAccessibleSelection = localAccessibleContext1.getAccessibleSelection();
        if (localAccessibleSelection == null) {
          return;
        }
        Accessible localAccessible = localAccessibleSelection.getAccessibleSelection(0);
        if (localAccessible == null) {
          return;
        }
        AccessibleContext localAccessibleContext2 = localAccessible.getAccessibleContext();
        if ((localAccessibleContext2 != null) && (invoker != null))
        {
          AccessibleContext localAccessibleContext3 = invoker.getAccessibleContext();
          if (localAccessibleContext3 != null) {
            localAccessibleContext3.firePropertyChange("AccessibleActiveDescendant", null, localAccessibleContext2);
          }
        }
      }
    }
  }
  
  public static class Separator
    extends JSeparator
  {
    public Separator()
    {
      super();
    }
    
    public String getUIClassID()
    {
      return "PopupMenuSeparatorUI";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JPopupMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */