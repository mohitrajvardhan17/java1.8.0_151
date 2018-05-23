package javax.swing;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuItemUI;

public class JMenuItem
  extends AbstractButton
  implements Accessible, MenuElement
{
  private static final String uiClassID = "MenuItemUI";
  private static final boolean TRACE = false;
  private static final boolean VERBOSE = false;
  private static final boolean DEBUG = false;
  private boolean isMouseDragged = false;
  private KeyStroke accelerator;
  
  public JMenuItem()
  {
    this(null, (Icon)null);
  }
  
  public JMenuItem(Icon paramIcon)
  {
    this(null, paramIcon);
  }
  
  public JMenuItem(String paramString)
  {
    this(paramString, (Icon)null);
  }
  
  public JMenuItem(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JMenuItem(String paramString, Icon paramIcon)
  {
    setModel(new DefaultButtonModel());
    init(paramString, paramIcon);
    initFocusability();
  }
  
  public JMenuItem(String paramString, int paramInt)
  {
    setModel(new DefaultButtonModel());
    init(paramString, null);
    setMnemonic(paramInt);
    initFocusability();
  }
  
  public void setModel(ButtonModel paramButtonModel)
  {
    super.setModel(paramButtonModel);
    if ((paramButtonModel instanceof DefaultButtonModel)) {
      ((DefaultButtonModel)paramButtonModel).setMenuItem(true);
    }
  }
  
  void initFocusability()
  {
    setFocusable(false);
  }
  
  protected void init(String paramString, Icon paramIcon)
  {
    if (paramString != null) {
      setText(paramString);
    }
    if (paramIcon != null) {
      setIcon(paramIcon);
    }
    addFocusListener(new MenuItemFocusListener(null));
    setUIProperty("borderPainted", Boolean.FALSE);
    setFocusPainted(false);
    setHorizontalTextPosition(11);
    setHorizontalAlignment(10);
    updateUI();
  }
  
  public void setUI(MenuItemUI paramMenuItemUI)
  {
    super.setUI(paramMenuItemUI);
  }
  
  public void updateUI()
  {
    setUI((MenuItemUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "MenuItemUI";
  }
  
  public void setArmed(boolean paramBoolean)
  {
    ButtonModel localButtonModel = getModel();
    boolean bool = localButtonModel.isArmed();
    if (localButtonModel.isArmed() != paramBoolean) {
      localButtonModel.setArmed(paramBoolean);
    }
  }
  
  public boolean isArmed()
  {
    ButtonModel localButtonModel = getModel();
    return localButtonModel.isArmed();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if ((!paramBoolean) && (!UIManager.getBoolean("MenuItem.disabledAreNavigable"))) {
      setArmed(false);
    }
    super.setEnabled(paramBoolean);
  }
  
  boolean alwaysOnTop()
  {
    return SwingUtilities.getAncestorOfClass(JInternalFrame.class, this) == null;
  }
  
  public void setAccelerator(KeyStroke paramKeyStroke)
  {
    KeyStroke localKeyStroke = accelerator;
    accelerator = paramKeyStroke;
    repaint();
    revalidate();
    firePropertyChange("accelerator", localKeyStroke, accelerator);
  }
  
  public KeyStroke getAccelerator()
  {
    return accelerator;
  }
  
  protected void configurePropertiesFromAction(Action paramAction)
  {
    super.configurePropertiesFromAction(paramAction);
    configureAcceleratorFromAction(paramAction);
  }
  
  void setIconFromAction(Action paramAction)
  {
    Icon localIcon = null;
    if (paramAction != null) {
      localIcon = (Icon)paramAction.getValue("SmallIcon");
    }
    setIcon(localIcon);
  }
  
  void largeIconChanged(Action paramAction) {}
  
  void smallIconChanged(Action paramAction)
  {
    setIconFromAction(paramAction);
  }
  
  void configureAcceleratorFromAction(Action paramAction)
  {
    KeyStroke localKeyStroke = paramAction == null ? null : (KeyStroke)paramAction.getValue("AcceleratorKey");
    setAccelerator(localKeyStroke);
  }
  
  protected void actionPropertyChanged(Action paramAction, String paramString)
  {
    if (paramString == "AcceleratorKey") {
      configureAcceleratorFromAction(paramAction);
    } else {
      super.actionPropertyChanged(paramAction, paramString);
    }
  }
  
  public void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    processMenuDragMouseEvent(new MenuDragMouseEvent(paramMouseEvent.getComponent(), paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), paramMouseEvent.getX(), paramMouseEvent.getY(), paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), paramArrayOfMenuElement, paramMenuSelectionManager));
  }
  
  public void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    MenuKeyEvent localMenuKeyEvent = new MenuKeyEvent(paramKeyEvent.getComponent(), paramKeyEvent.getID(), paramKeyEvent.getWhen(), paramKeyEvent.getModifiers(), paramKeyEvent.getKeyCode(), paramKeyEvent.getKeyChar(), paramArrayOfMenuElement, paramMenuSelectionManager);
    processMenuKeyEvent(localMenuKeyEvent);
    if (localMenuKeyEvent.isConsumed()) {
      paramKeyEvent.consume();
    }
  }
  
  public void processMenuDragMouseEvent(MenuDragMouseEvent paramMenuDragMouseEvent)
  {
    switch (paramMenuDragMouseEvent.getID())
    {
    case 504: 
      isMouseDragged = false;
      fireMenuDragMouseEntered(paramMenuDragMouseEvent);
      break;
    case 505: 
      isMouseDragged = false;
      fireMenuDragMouseExited(paramMenuDragMouseEvent);
      break;
    case 506: 
      isMouseDragged = true;
      fireMenuDragMouseDragged(paramMenuDragMouseEvent);
      break;
    case 502: 
      if (isMouseDragged) {
        fireMenuDragMouseReleased(paramMenuDragMouseEvent);
      }
      break;
    }
  }
  
  public void processMenuKeyEvent(MenuKeyEvent paramMenuKeyEvent)
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
  
  protected void fireMenuDragMouseEntered(MenuDragMouseEvent paramMenuDragMouseEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuDragMouseListener.class) {
        ((MenuDragMouseListener)arrayOfObject[(i + 1)]).menuDragMouseEntered(paramMenuDragMouseEvent);
      }
    }
  }
  
  protected void fireMenuDragMouseExited(MenuDragMouseEvent paramMenuDragMouseEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuDragMouseListener.class) {
        ((MenuDragMouseListener)arrayOfObject[(i + 1)]).menuDragMouseExited(paramMenuDragMouseEvent);
      }
    }
  }
  
  protected void fireMenuDragMouseDragged(MenuDragMouseEvent paramMenuDragMouseEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuDragMouseListener.class) {
        ((MenuDragMouseListener)arrayOfObject[(i + 1)]).menuDragMouseDragged(paramMenuDragMouseEvent);
      }
    }
  }
  
  protected void fireMenuDragMouseReleased(MenuDragMouseEvent paramMenuDragMouseEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuDragMouseListener.class) {
        ((MenuDragMouseListener)arrayOfObject[(i + 1)]).menuDragMouseReleased(paramMenuDragMouseEvent);
      }
    }
  }
  
  protected void fireMenuKeyPressed(MenuKeyEvent paramMenuKeyEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class) {
        ((MenuKeyListener)arrayOfObject[(i + 1)]).menuKeyPressed(paramMenuKeyEvent);
      }
    }
  }
  
  protected void fireMenuKeyReleased(MenuKeyEvent paramMenuKeyEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class) {
        ((MenuKeyListener)arrayOfObject[(i + 1)]).menuKeyReleased(paramMenuKeyEvent);
      }
    }
  }
  
  protected void fireMenuKeyTyped(MenuKeyEvent paramMenuKeyEvent)
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
    setArmed(paramBoolean);
  }
  
  public MenuElement[] getSubElements()
  {
    return new MenuElement[0];
  }
  
  public Component getComponent()
  {
    return this;
  }
  
  public void addMenuDragMouseListener(MenuDragMouseListener paramMenuDragMouseListener)
  {
    listenerList.add(MenuDragMouseListener.class, paramMenuDragMouseListener);
  }
  
  public void removeMenuDragMouseListener(MenuDragMouseListener paramMenuDragMouseListener)
  {
    listenerList.remove(MenuDragMouseListener.class, paramMenuDragMouseListener);
  }
  
  public MenuDragMouseListener[] getMenuDragMouseListeners()
  {
    return (MenuDragMouseListener[])listenerList.getListeners(MenuDragMouseListener.class);
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
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (getUIClassID().equals("MenuItemUI")) {
      updateUI();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("MenuItemUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJMenuItem();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJMenuItem
    extends AbstractButton.AccessibleAbstractButton
    implements ChangeListener
  {
    private boolean isArmed = false;
    private boolean hasFocus = false;
    private boolean isPressed = false;
    private boolean isSelected = false;
    
    AccessibleJMenuItem()
    {
      super();
      addChangeListener(this);
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU_ITEM;
    }
    
    private void fireAccessibilityFocusedEvent(JMenuItem paramJMenuItem)
    {
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement.length > 0)
      {
        MenuElement localMenuElement = arrayOfMenuElement[(arrayOfMenuElement.length - 1)];
        if (paramJMenuItem == localMenuElement) {
          firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
        }
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
      if (getModel().isArmed())
      {
        if (!isArmed)
        {
          isArmed = true;
          firePropertyChange("AccessibleState", null, AccessibleState.ARMED);
          fireAccessibilityFocusedEvent(JMenuItem.this);
        }
      }
      else if (isArmed)
      {
        isArmed = false;
        firePropertyChange("AccessibleState", AccessibleState.ARMED, null);
      }
      if (isFocusOwner())
      {
        if (!hasFocus)
        {
          hasFocus = true;
          firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
        }
      }
      else if (hasFocus)
      {
        hasFocus = false;
        firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null);
      }
      if (getModel().isPressed())
      {
        if (!isPressed)
        {
          isPressed = true;
          firePropertyChange("AccessibleState", null, AccessibleState.PRESSED);
        }
      }
      else if (isPressed)
      {
        isPressed = false;
        firePropertyChange("AccessibleState", AccessibleState.PRESSED, null);
      }
      if (getModel().isSelected())
      {
        if (!isSelected)
        {
          isSelected = true;
          firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
          fireAccessibilityFocusedEvent(JMenuItem.this);
        }
      }
      else if (isSelected)
      {
        isSelected = false;
        firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
      }
    }
  }
  
  private static class MenuItemFocusListener
    implements FocusListener, Serializable
  {
    private MenuItemFocusListener() {}
    
    public void focusGained(FocusEvent paramFocusEvent) {}
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      JMenuItem localJMenuItem = (JMenuItem)paramFocusEvent.getSource();
      if (localJMenuItem.isFocusPainted()) {
        localJMenuItem.repaint();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JMenuItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */