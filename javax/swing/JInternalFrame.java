package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.event.EventListenerList;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopIconUI;
import javax.swing.plaf.InternalFrameUI;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

public class JInternalFrame
  extends JComponent
  implements Accessible, WindowConstants, RootPaneContainer
{
  private static final String uiClassID = "InternalFrameUI";
  protected JRootPane rootPane;
  protected boolean rootPaneCheckingEnabled = false;
  protected boolean closable;
  protected boolean isClosed;
  protected boolean maximizable;
  protected boolean isMaximum;
  protected boolean iconable;
  protected boolean isIcon;
  protected boolean resizable;
  protected boolean isSelected;
  protected Icon frameIcon;
  protected String title;
  protected JDesktopIcon desktopIcon;
  private Cursor lastCursor;
  private boolean opened;
  private Rectangle normalBounds = null;
  private int defaultCloseOperation = 2;
  private Component lastFocusOwner;
  public static final String CONTENT_PANE_PROPERTY = "contentPane";
  public static final String MENU_BAR_PROPERTY = "JMenuBar";
  public static final String TITLE_PROPERTY = "title";
  public static final String LAYERED_PANE_PROPERTY = "layeredPane";
  public static final String ROOT_PANE_PROPERTY = "rootPane";
  public static final String GLASS_PANE_PROPERTY = "glassPane";
  public static final String FRAME_ICON_PROPERTY = "frameIcon";
  public static final String IS_SELECTED_PROPERTY = "selected";
  public static final String IS_CLOSED_PROPERTY = "closed";
  public static final String IS_MAXIMUM_PROPERTY = "maximum";
  public static final String IS_ICON_PROPERTY = "icon";
  private static final Object PROPERTY_CHANGE_LISTENER_KEY = new StringBuilder("InternalFramePropertyChangeListener");
  boolean isDragging = false;
  boolean danger = false;
  
  private static void addPropertyChangeListenerIfNecessary()
  {
    if (AppContext.getAppContext().get(PROPERTY_CHANGE_LISTENER_KEY) == null)
    {
      FocusPropertyChangeListener localFocusPropertyChangeListener = new FocusPropertyChangeListener(null);
      AppContext.getAppContext().put(PROPERTY_CHANGE_LISTENER_KEY, localFocusPropertyChangeListener);
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(localFocusPropertyChangeListener);
    }
  }
  
  private static void updateLastFocusOwner(Component paramComponent)
  {
    if (paramComponent != null) {
      for (Object localObject = paramComponent; (localObject != null) && (!(localObject instanceof Window)); localObject = ((Component)localObject).getParent()) {
        if ((localObject instanceof JInternalFrame)) {
          ((JInternalFrame)localObject).setLastFocusOwner(paramComponent);
        }
      }
    }
  }
  
  public JInternalFrame()
  {
    this("", false, false, false, false);
  }
  
  public JInternalFrame(String paramString)
  {
    this(paramString, false, false, false, false);
  }
  
  public JInternalFrame(String paramString, boolean paramBoolean)
  {
    this(paramString, paramBoolean, false, false, false);
  }
  
  public JInternalFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramString, paramBoolean1, paramBoolean2, false, false);
  }
  
  public JInternalFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramString, paramBoolean1, paramBoolean2, paramBoolean3, false);
  }
  
  public JInternalFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    setRootPane(createRootPane());
    setLayout(new BorderLayout());
    title = paramString;
    resizable = paramBoolean1;
    closable = paramBoolean2;
    maximizable = paramBoolean3;
    isMaximum = false;
    iconable = paramBoolean4;
    isIcon = false;
    setVisible(false);
    setRootPaneCheckingEnabled(true);
    desktopIcon = new JDesktopIcon(this);
    updateUI();
    SunToolkit.checkAndSetPolicy(this);
    addPropertyChangeListenerIfNecessary();
  }
  
  protected JRootPane createRootPane()
  {
    return new JRootPane();
  }
  
  public InternalFrameUI getUI()
  {
    return (InternalFrameUI)ui;
  }
  
  /* Error */
  public void setUI(InternalFrameUI paramInternalFrameUI)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 658	javax/swing/JInternalFrame:isRootPaneCheckingEnabled	()Z
    //   4: istore_2
    //   5: aload_0
    //   6: iconst_0
    //   7: invokevirtual 666	javax/swing/JInternalFrame:setRootPaneCheckingEnabled	(Z)V
    //   10: aload_0
    //   11: aload_1
    //   12: invokespecial 639	javax/swing/JComponent:setUI	(Ljavax/swing/plaf/ComponentUI;)V
    //   15: aload_0
    //   16: iload_2
    //   17: invokevirtual 666	javax/swing/JInternalFrame:setRootPaneCheckingEnabled	(Z)V
    //   20: goto +11 -> 31
    //   23: astore_3
    //   24: aload_0
    //   25: iload_2
    //   26: invokevirtual 666	javax/swing/JInternalFrame:setRootPaneCheckingEnabled	(Z)V
    //   29: aload_3
    //   30: athrow
    //   31: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	JInternalFrame
    //   0	32	1	paramInternalFrameUI	InternalFrameUI
    //   4	22	2	bool	boolean
    //   23	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	15	23	finally
  }
  
  public void updateUI()
  {
    setUI((InternalFrameUI)UIManager.getUI(this));
    invalidate();
    if (desktopIcon != null) {
      desktopIcon.updateUIWhenHidden();
    }
  }
  
  void updateUIWhenHidden()
  {
    setUI((InternalFrameUI)UIManager.getUI(this));
    invalidate();
    Component[] arrayOfComponent1 = getComponents();
    if (arrayOfComponent1 != null) {
      for (Component localComponent : arrayOfComponent1) {
        SwingUtilities.updateComponentTreeUI(localComponent);
      }
    }
  }
  
  public String getUIClassID()
  {
    return "InternalFrameUI";
  }
  
  protected boolean isRootPaneCheckingEnabled()
  {
    return rootPaneCheckingEnabled;
  }
  
  protected void setRootPaneCheckingEnabled(boolean paramBoolean)
  {
    rootPaneCheckingEnabled = paramBoolean;
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    if (isRootPaneCheckingEnabled()) {
      getContentPane().add(paramComponent, paramObject, paramInt);
    } else {
      super.addImpl(paramComponent, paramObject, paramInt);
    }
  }
  
  public void remove(Component paramComponent)
  {
    int i = getComponentCount();
    super.remove(paramComponent);
    if (i == getComponentCount()) {
      getContentPane().remove(paramComponent);
    }
  }
  
  public void setLayout(LayoutManager paramLayoutManager)
  {
    if (isRootPaneCheckingEnabled()) {
      getContentPane().setLayout(paramLayoutManager);
    } else {
      super.setLayout(paramLayoutManager);
    }
  }
  
  @Deprecated
  public JMenuBar getMenuBar()
  {
    return getRootPane().getMenuBar();
  }
  
  public JMenuBar getJMenuBar()
  {
    return getRootPane().getJMenuBar();
  }
  
  @Deprecated
  public void setMenuBar(JMenuBar paramJMenuBar)
  {
    JMenuBar localJMenuBar = getMenuBar();
    getRootPane().setJMenuBar(paramJMenuBar);
    firePropertyChange("JMenuBar", localJMenuBar, paramJMenuBar);
  }
  
  public void setJMenuBar(JMenuBar paramJMenuBar)
  {
    JMenuBar localJMenuBar = getMenuBar();
    getRootPane().setJMenuBar(paramJMenuBar);
    firePropertyChange("JMenuBar", localJMenuBar, paramJMenuBar);
  }
  
  public Container getContentPane()
  {
    return getRootPane().getContentPane();
  }
  
  public void setContentPane(Container paramContainer)
  {
    Container localContainer = getContentPane();
    getRootPane().setContentPane(paramContainer);
    firePropertyChange("contentPane", localContainer, paramContainer);
  }
  
  public JLayeredPane getLayeredPane()
  {
    return getRootPane().getLayeredPane();
  }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane)
  {
    JLayeredPane localJLayeredPane = getLayeredPane();
    getRootPane().setLayeredPane(paramJLayeredPane);
    firePropertyChange("layeredPane", localJLayeredPane, paramJLayeredPane);
  }
  
  public Component getGlassPane()
  {
    return getRootPane().getGlassPane();
  }
  
  public void setGlassPane(Component paramComponent)
  {
    Component localComponent = getGlassPane();
    getRootPane().setGlassPane(paramComponent);
    firePropertyChange("glassPane", localComponent, paramComponent);
  }
  
  public JRootPane getRootPane()
  {
    return rootPane;
  }
  
  protected void setRootPane(JRootPane paramJRootPane)
  {
    if (rootPane != null) {
      remove(rootPane);
    }
    JRootPane localJRootPane = getRootPane();
    rootPane = paramJRootPane;
    if (rootPane != null)
    {
      boolean bool = isRootPaneCheckingEnabled();
      try
      {
        setRootPaneCheckingEnabled(false);
        add(rootPane, "Center");
      }
      finally
      {
        setRootPaneCheckingEnabled(bool);
      }
    }
    firePropertyChange("rootPane", localJRootPane, paramJRootPane);
  }
  
  public void setClosable(boolean paramBoolean)
  {
    Boolean localBoolean1 = closable ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    closable = paramBoolean;
    firePropertyChange("closable", localBoolean1, localBoolean2);
  }
  
  public boolean isClosable()
  {
    return closable;
  }
  
  public boolean isClosed()
  {
    return isClosed;
  }
  
  public void setClosed(boolean paramBoolean)
    throws PropertyVetoException
  {
    if (isClosed == paramBoolean) {
      return;
    }
    Boolean localBoolean1 = isClosed ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    if (paramBoolean) {
      fireInternalFrameEvent(25550);
    }
    fireVetoableChange("closed", localBoolean1, localBoolean2);
    isClosed = paramBoolean;
    if (isClosed) {
      setVisible(false);
    }
    firePropertyChange("closed", localBoolean1, localBoolean2);
    if (isClosed) {
      dispose();
    } else if (opened) {}
  }
  
  public void setResizable(boolean paramBoolean)
  {
    Boolean localBoolean1 = resizable ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    resizable = paramBoolean;
    firePropertyChange("resizable", localBoolean1, localBoolean2);
  }
  
  public boolean isResizable()
  {
    return isMaximum ? false : resizable;
  }
  
  public void setIconifiable(boolean paramBoolean)
  {
    Boolean localBoolean1 = iconable ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    iconable = paramBoolean;
    firePropertyChange("iconable", localBoolean1, localBoolean2);
  }
  
  public boolean isIconifiable()
  {
    return iconable;
  }
  
  public boolean isIcon()
  {
    return isIcon;
  }
  
  public void setIcon(boolean paramBoolean)
    throws PropertyVetoException
  {
    if (isIcon == paramBoolean) {
      return;
    }
    firePropertyChange("ancestor", null, getParent());
    Boolean localBoolean1 = isIcon ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    fireVetoableChange("icon", localBoolean1, localBoolean2);
    isIcon = paramBoolean;
    firePropertyChange("icon", localBoolean1, localBoolean2);
    if (paramBoolean) {
      fireInternalFrameEvent(25552);
    } else {
      fireInternalFrameEvent(25553);
    }
  }
  
  public void setMaximizable(boolean paramBoolean)
  {
    Boolean localBoolean1 = maximizable ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    maximizable = paramBoolean;
    firePropertyChange("maximizable", localBoolean1, localBoolean2);
  }
  
  public boolean isMaximizable()
  {
    return maximizable;
  }
  
  public boolean isMaximum()
  {
    return isMaximum;
  }
  
  public void setMaximum(boolean paramBoolean)
    throws PropertyVetoException
  {
    if (isMaximum == paramBoolean) {
      return;
    }
    Boolean localBoolean1 = isMaximum ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    fireVetoableChange("maximum", localBoolean1, localBoolean2);
    isMaximum = paramBoolean;
    firePropertyChange("maximum", localBoolean1, localBoolean2);
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public void setTitle(String paramString)
  {
    String str = title;
    title = paramString;
    firePropertyChange("title", str, paramString);
  }
  
  public void setSelected(boolean paramBoolean)
    throws PropertyVetoException
  {
    if ((paramBoolean) && (isSelected))
    {
      restoreSubcomponentFocus();
      return;
    }
    if ((isSelected == paramBoolean) || ((paramBoolean) && (isIcon ? !desktopIcon.isShowing() : !isShowing()))) {
      return;
    }
    Boolean localBoolean1 = isSelected ? Boolean.TRUE : Boolean.FALSE;
    Boolean localBoolean2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    fireVetoableChange("selected", localBoolean1, localBoolean2);
    if (paramBoolean) {
      restoreSubcomponentFocus();
    }
    isSelected = paramBoolean;
    firePropertyChange("selected", localBoolean1, localBoolean2);
    if (isSelected) {
      fireInternalFrameEvent(25554);
    } else {
      fireInternalFrameEvent(25555);
    }
    repaint();
  }
  
  public boolean isSelected()
  {
    return isSelected;
  }
  
  public void setFrameIcon(Icon paramIcon)
  {
    Icon localIcon = frameIcon;
    frameIcon = paramIcon;
    firePropertyChange("frameIcon", localIcon, paramIcon);
  }
  
  public Icon getFrameIcon()
  {
    return frameIcon;
  }
  
  public void moveToFront()
  {
    if (isIcon())
    {
      if ((getDesktopIcon().getParent() instanceof JLayeredPane)) {
        ((JLayeredPane)getDesktopIcon().getParent()).moveToFront(getDesktopIcon());
      }
    }
    else if ((getParent() instanceof JLayeredPane)) {
      ((JLayeredPane)getParent()).moveToFront(this);
    }
  }
  
  public void moveToBack()
  {
    if (isIcon())
    {
      if ((getDesktopIcon().getParent() instanceof JLayeredPane)) {
        ((JLayeredPane)getDesktopIcon().getParent()).moveToBack(getDesktopIcon());
      }
    }
    else if ((getParent() instanceof JLayeredPane)) {
      ((JLayeredPane)getParent()).moveToBack(this);
    }
  }
  
  public Cursor getLastCursor()
  {
    return lastCursor;
  }
  
  public void setCursor(Cursor paramCursor)
  {
    if (paramCursor == null)
    {
      lastCursor = null;
      super.setCursor(paramCursor);
      return;
    }
    int i = paramCursor.getType();
    if ((i != 4) && (i != 5) && (i != 6) && (i != 7) && (i != 8) && (i != 9) && (i != 10) && (i != 11)) {
      lastCursor = paramCursor;
    }
    super.setCursor(paramCursor);
  }
  
  public void setLayer(Integer paramInteger)
  {
    if ((getParent() != null) && ((getParent() instanceof JLayeredPane)))
    {
      JLayeredPane localJLayeredPane = (JLayeredPane)getParent();
      localJLayeredPane.setLayer(this, paramInteger.intValue(), localJLayeredPane.getPosition(this));
    }
    else
    {
      JLayeredPane.putLayer(this, paramInteger.intValue());
      if (getParent() != null) {
        getParent().repaint(getX(), getY(), getWidth(), getHeight());
      }
    }
  }
  
  public void setLayer(int paramInt)
  {
    setLayer(Integer.valueOf(paramInt));
  }
  
  public int getLayer()
  {
    return JLayeredPane.getLayer(this);
  }
  
  public JDesktopPane getDesktopPane()
  {
    for (Container localContainer = getParent(); (localContainer != null) && (!(localContainer instanceof JDesktopPane)); localContainer = localContainer.getParent()) {}
    if (localContainer == null) {
      for (localContainer = getDesktopIcon().getParent(); (localContainer != null) && (!(localContainer instanceof JDesktopPane)); localContainer = localContainer.getParent()) {}
    }
    return (JDesktopPane)localContainer;
  }
  
  public void setDesktopIcon(JDesktopIcon paramJDesktopIcon)
  {
    JDesktopIcon localJDesktopIcon = getDesktopIcon();
    desktopIcon = paramJDesktopIcon;
    firePropertyChange("desktopIcon", localJDesktopIcon, paramJDesktopIcon);
  }
  
  public JDesktopIcon getDesktopIcon()
  {
    return desktopIcon;
  }
  
  public Rectangle getNormalBounds()
  {
    if (normalBounds != null) {
      return normalBounds;
    }
    return getBounds();
  }
  
  public void setNormalBounds(Rectangle paramRectangle)
  {
    normalBounds = paramRectangle;
  }
  
  public Component getFocusOwner()
  {
    if (isSelected()) {
      return lastFocusOwner;
    }
    return null;
  }
  
  public Component getMostRecentFocusOwner()
  {
    if (isSelected()) {
      return getFocusOwner();
    }
    if (lastFocusOwner != null) {
      return lastFocusOwner;
    }
    FocusTraversalPolicy localFocusTraversalPolicy = getFocusTraversalPolicy();
    if ((localFocusTraversalPolicy instanceof InternalFrameFocusTraversalPolicy)) {
      return ((InternalFrameFocusTraversalPolicy)localFocusTraversalPolicy).getInitialComponent(this);
    }
    Component localComponent = localFocusTraversalPolicy.getDefaultComponent(this);
    if (localComponent != null) {
      return localComponent;
    }
    return getContentPane();
  }
  
  public void restoreSubcomponentFocus()
  {
    if (isIcon())
    {
      SwingUtilities2.compositeRequestFocus(getDesktopIcon());
    }
    else
    {
      Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
      if ((localComponent == null) || (!SwingUtilities.isDescendingFrom(localComponent, this)))
      {
        setLastFocusOwner(getMostRecentFocusOwner());
        if (lastFocusOwner == null) {
          setLastFocusOwner(getContentPane());
        }
        lastFocusOwner.requestFocus();
      }
    }
  }
  
  private void setLastFocusOwner(Component paramComponent)
  {
    lastFocusOwner = paramComponent;
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    validate();
    repaint();
  }
  
  public void addInternalFrameListener(InternalFrameListener paramInternalFrameListener)
  {
    listenerList.add(InternalFrameListener.class, paramInternalFrameListener);
    enableEvents(0L);
  }
  
  public void removeInternalFrameListener(InternalFrameListener paramInternalFrameListener)
  {
    listenerList.remove(InternalFrameListener.class, paramInternalFrameListener);
  }
  
  public InternalFrameListener[] getInternalFrameListeners()
  {
    return (InternalFrameListener[])listenerList.getListeners(InternalFrameListener.class);
  }
  
  protected void fireInternalFrameEvent(int paramInt)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    InternalFrameEvent localInternalFrameEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == InternalFrameListener.class)
      {
        if (localInternalFrameEvent == null) {
          localInternalFrameEvent = new InternalFrameEvent(this, paramInt);
        }
        switch (localInternalFrameEvent.getID())
        {
        case 25549: 
          ((InternalFrameListener)arrayOfObject[(i + 1)]).internalFrameOpened(localInternalFrameEvent);
          break;
        case 25550: 
          ((InternalFrameListener)arrayOfObject[(i + 1)]).internalFrameClosing(localInternalFrameEvent);
          break;
        case 25551: 
          ((InternalFrameListener)arrayOfObject[(i + 1)]).internalFrameClosed(localInternalFrameEvent);
          break;
        case 25552: 
          ((InternalFrameListener)arrayOfObject[(i + 1)]).internalFrameIconified(localInternalFrameEvent);
          break;
        case 25553: 
          ((InternalFrameListener)arrayOfObject[(i + 1)]).internalFrameDeiconified(localInternalFrameEvent);
          break;
        case 25554: 
          ((InternalFrameListener)arrayOfObject[(i + 1)]).internalFrameActivated(localInternalFrameEvent);
          break;
        case 25555: 
          ((InternalFrameListener)arrayOfObject[(i + 1)]).internalFrameDeactivated(localInternalFrameEvent);
        }
      }
    }
  }
  
  public void doDefaultCloseAction()
  {
    fireInternalFrameEvent(25550);
    switch (defaultCloseOperation)
    {
    case 0: 
      break;
    case 1: 
      setVisible(false);
      if (isSelected()) {
        try
        {
          setSelected(false);
        }
        catch (PropertyVetoException localPropertyVetoException1) {}
      }
      break;
    case 2: 
      try
      {
        fireVetoableChange("closed", Boolean.FALSE, Boolean.TRUE);
        isClosed = true;
        setVisible(false);
        firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
        dispose();
      }
      catch (PropertyVetoException localPropertyVetoException2) {}
    }
  }
  
  public void setDefaultCloseOperation(int paramInt)
  {
    defaultCloseOperation = paramInt;
  }
  
  public int getDefaultCloseOperation()
  {
    return defaultCloseOperation;
  }
  
  public void pack()
  {
    try
    {
      if (isIcon()) {
        setIcon(false);
      } else if (isMaximum()) {
        setMaximum(false);
      }
    }
    catch (PropertyVetoException localPropertyVetoException)
    {
      return;
    }
    setSize(getPreferredSize());
    validate();
  }
  
  public void show()
  {
    if (isVisible()) {
      return;
    }
    if (!opened)
    {
      fireInternalFrameEvent(25549);
      opened = true;
    }
    getDesktopIcon().setVisible(true);
    toFront();
    super.show();
    if (isIcon) {
      return;
    }
    if (!isSelected()) {
      try
      {
        setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException) {}
    }
  }
  
  public void hide()
  {
    if (isIcon()) {
      getDesktopIcon().setVisible(false);
    }
    super.hide();
  }
  
  public void dispose()
  {
    if (isVisible()) {
      setVisible(false);
    }
    if (isSelected()) {
      try
      {
        setSelected(false);
      }
      catch (PropertyVetoException localPropertyVetoException) {}
    }
    if (!isClosed)
    {
      firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
      isClosed = true;
    }
    fireInternalFrameEvent(25551);
  }
  
  public void toFront()
  {
    moveToFront();
  }
  
  public void toBack()
  {
    moveToBack();
  }
  
  public final void setFocusCycleRoot(boolean paramBoolean) {}
  
  public final boolean isFocusCycleRoot()
  {
    return true;
  }
  
  public final Container getFocusCycleRootAncestor()
  {
    return null;
  }
  
  public final String getWarningString()
  {
    return null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("InternalFrameUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null))
      {
        boolean bool = isRootPaneCheckingEnabled();
        try
        {
          setRootPaneCheckingEnabled(false);
          ui.installUI(this);
        }
        finally
        {
          setRootPaneCheckingEnabled(bool);
        }
      }
    }
  }
  
  /* Error */
  void compWriteObjectNotify()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 658	javax/swing/JInternalFrame:isRootPaneCheckingEnabled	()Z
    //   4: istore_1
    //   5: aload_0
    //   6: iconst_0
    //   7: invokevirtual 666	javax/swing/JInternalFrame:setRootPaneCheckingEnabled	(Z)V
    //   10: aload_0
    //   11: invokespecial 628	javax/swing/JComponent:compWriteObjectNotify	()V
    //   14: aload_0
    //   15: iload_1
    //   16: invokevirtual 666	javax/swing/JInternalFrame:setRootPaneCheckingEnabled	(Z)V
    //   19: goto +11 -> 30
    //   22: astore_2
    //   23: aload_0
    //   24: iload_1
    //   25: invokevirtual 666	javax/swing/JInternalFrame:setRootPaneCheckingEnabled	(Z)V
    //   28: aload_2
    //   29: athrow
    //   30: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	JInternalFrame
    //   4	21	1	bool	boolean
    //   22	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	14	22	finally
  }
  
  protected String paramString()
  {
    String str1 = rootPane != null ? rootPane.toString() : "";
    String str2 = rootPaneCheckingEnabled ? "true" : "false";
    String str3 = closable ? "true" : "false";
    String str4 = isClosed ? "true" : "false";
    String str5 = maximizable ? "true" : "false";
    String str6 = isMaximum ? "true" : "false";
    String str7 = iconable ? "true" : "false";
    String str8 = isIcon ? "true" : "false";
    String str9 = resizable ? "true" : "false";
    String str10 = isSelected ? "true" : "false";
    String str11 = frameIcon != null ? frameIcon.toString() : "";
    String str12 = title != null ? title : "";
    String str13 = desktopIcon != null ? desktopIcon.toString() : "";
    String str14 = opened ? "true" : "false";
    String str15;
    if (defaultCloseOperation == 1) {
      str15 = "HIDE_ON_CLOSE";
    } else if (defaultCloseOperation == 2) {
      str15 = "DISPOSE_ON_CLOSE";
    } else if (defaultCloseOperation == 0) {
      str15 = "DO_NOTHING_ON_CLOSE";
    } else {
      str15 = "";
    }
    return super.paramString() + ",closable=" + str3 + ",defaultCloseOperation=" + str15 + ",desktopIcon=" + str13 + ",frameIcon=" + str11 + ",iconable=" + str7 + ",isClosed=" + str4 + ",isIcon=" + str8 + ",isMaximum=" + str6 + ",isSelected=" + str10 + ",maximizable=" + str5 + ",opened=" + str14 + ",resizable=" + str9 + ",rootPane=" + str1 + ",rootPaneCheckingEnabled=" + str2 + ",title=" + str12;
  }
  
  protected void paintComponent(Graphics paramGraphics)
  {
    if (isDragging) {
      danger = true;
    }
    super.paintComponent(paramGraphics);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJInternalFrame();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJInternalFrame
    extends JComponent.AccessibleJComponent
    implements AccessibleValue
  {
    protected AccessibleJInternalFrame()
    {
      super();
    }
    
    public String getAccessibleName()
    {
      String str = accessibleName;
      if (str == null) {
        str = (String)getClientProperty("AccessibleName");
      }
      if (str == null) {
        str = getTitle();
      }
      return str;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.INTERNAL_FRAME;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return Integer.valueOf(getLayer());
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      if (paramNumber == null) {
        return false;
      }
      setLayer(new Integer(paramNumber.intValue()));
      return true;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return Integer.valueOf(Integer.MIN_VALUE);
    }
    
    public Number getMaximumAccessibleValue()
    {
      return Integer.valueOf(Integer.MAX_VALUE);
    }
  }
  
  private static class FocusPropertyChangeListener
    implements PropertyChangeListener
  {
    private FocusPropertyChangeListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getPropertyName() == "permanentFocusOwner") {
        JInternalFrame.updateLastFocusOwner((Component)paramPropertyChangeEvent.getNewValue());
      }
    }
  }
  
  public static class JDesktopIcon
    extends JComponent
    implements Accessible
  {
    JInternalFrame internalFrame;
    
    public JDesktopIcon(JInternalFrame paramJInternalFrame)
    {
      setVisible(false);
      setInternalFrame(paramJInternalFrame);
      updateUI();
    }
    
    public DesktopIconUI getUI()
    {
      return (DesktopIconUI)ui;
    }
    
    public void setUI(DesktopIconUI paramDesktopIconUI)
    {
      super.setUI(paramDesktopIconUI);
    }
    
    public JInternalFrame getInternalFrame()
    {
      return internalFrame;
    }
    
    public void setInternalFrame(JInternalFrame paramJInternalFrame)
    {
      internalFrame = paramJInternalFrame;
    }
    
    public JDesktopPane getDesktopPane()
    {
      if (getInternalFrame() != null) {
        return getInternalFrame().getDesktopPane();
      }
      return null;
    }
    
    public void updateUI()
    {
      int i = ui != null ? 1 : 0;
      setUI((DesktopIconUI)UIManager.getUI(this));
      invalidate();
      Dimension localDimension = getPreferredSize();
      setSize(width, height);
      if ((internalFrame != null) && (internalFrame.getUI() != null)) {
        SwingUtilities.updateComponentTreeUI(internalFrame);
      }
    }
    
    void updateUIWhenHidden()
    {
      setUI((DesktopIconUI)UIManager.getUI(this));
      Dimension localDimension = getPreferredSize();
      setSize(width, height);
      invalidate();
      Component[] arrayOfComponent1 = getComponents();
      if (arrayOfComponent1 != null) {
        for (Component localComponent : arrayOfComponent1) {
          SwingUtilities.updateComponentTreeUI(localComponent);
        }
      }
    }
    
    public String getUIClassID()
    {
      return "DesktopIconUI";
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      if (getUIClassID().equals("DesktopIconUI"))
      {
        byte b = JComponent.getWriteObjCounter(this);
        b = (byte)(b - 1);
        JComponent.setWriteObjCounter(this, b);
        if ((b == 0) && (ui != null)) {
          ui.installUI(this);
        }
      }
    }
    
    public AccessibleContext getAccessibleContext()
    {
      if (accessibleContext == null) {
        accessibleContext = new AccessibleJDesktopIcon();
      }
      return accessibleContext;
    }
    
    protected class AccessibleJDesktopIcon
      extends JComponent.AccessibleJComponent
      implements AccessibleValue
    {
      protected AccessibleJDesktopIcon()
      {
        super();
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return AccessibleRole.DESKTOP_ICON;
      }
      
      public AccessibleValue getAccessibleValue()
      {
        return this;
      }
      
      public Number getCurrentAccessibleValue()
      {
        AccessibleContext localAccessibleContext = getInternalFrame().getAccessibleContext();
        AccessibleValue localAccessibleValue = localAccessibleContext.getAccessibleValue();
        if (localAccessibleValue != null) {
          return localAccessibleValue.getCurrentAccessibleValue();
        }
        return null;
      }
      
      public boolean setCurrentAccessibleValue(Number paramNumber)
      {
        if (paramNumber == null) {
          return false;
        }
        AccessibleContext localAccessibleContext = getInternalFrame().getAccessibleContext();
        AccessibleValue localAccessibleValue = localAccessibleContext.getAccessibleValue();
        if (localAccessibleValue != null) {
          return localAccessibleValue.setCurrentAccessibleValue(paramNumber);
        }
        return false;
      }
      
      public Number getMinimumAccessibleValue()
      {
        AccessibleContext localAccessibleContext = getInternalFrame().getAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleValue)) {
          return ((AccessibleValue)localAccessibleContext).getMinimumAccessibleValue();
        }
        return null;
      }
      
      public Number getMaximumAccessibleValue()
      {
        AccessibleContext localAccessibleContext = getInternalFrame().getAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleValue)) {
          return ((AccessibleValue)localAccessibleContext).getMaximumAccessibleValue();
        }
        return null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JInternalFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */