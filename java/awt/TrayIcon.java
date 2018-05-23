package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.peer.TrayIconPeer;
import java.security.AccessControlContext;
import java.security.AccessController;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.TrayIconAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;

public class TrayIcon
{
  private Image image;
  private String tooltip;
  private PopupMenu popup;
  private boolean autosize;
  private int id;
  private String actionCommand;
  private transient TrayIconPeer peer;
  transient MouseListener mouseListener;
  transient MouseMotionListener mouseMotionListener;
  transient ActionListener actionListener;
  private final AccessControlContext acc = AccessController.getContext();
  
  final AccessControlContext getAccessControlContext()
  {
    if (acc == null) {
      throw new SecurityException("TrayIcon is missing AccessControlContext");
    }
    return acc;
  }
  
  private TrayIcon()
    throws UnsupportedOperationException, HeadlessException, SecurityException
  {
    SystemTray.checkSystemTrayAllowed();
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if (!SystemTray.isSupported()) {
      throw new UnsupportedOperationException();
    }
    SunToolkit.insertTargetMapping(this, AppContext.getAppContext());
  }
  
  public TrayIcon(Image paramImage)
  {
    this();
    if (paramImage == null) {
      throw new IllegalArgumentException("creating TrayIcon with null Image");
    }
    setImage(paramImage);
  }
  
  public TrayIcon(Image paramImage, String paramString)
  {
    this(paramImage);
    setToolTip(paramString);
  }
  
  public TrayIcon(Image paramImage, String paramString, PopupMenu paramPopupMenu)
  {
    this(paramImage, paramString);
    setPopupMenu(paramPopupMenu);
  }
  
  public void setImage(Image paramImage)
  {
    if (paramImage == null) {
      throw new NullPointerException("setting null Image");
    }
    image = paramImage;
    TrayIconPeer localTrayIconPeer = peer;
    if (localTrayIconPeer != null) {
      localTrayIconPeer.updateImage();
    }
  }
  
  public Image getImage()
  {
    return image;
  }
  
  public void setPopupMenu(PopupMenu paramPopupMenu)
  {
    if (paramPopupMenu == popup) {
      return;
    }
    synchronized (TrayIcon.class)
    {
      if (paramPopupMenu != null)
      {
        if (isTrayIconPopup) {
          throw new IllegalArgumentException("the PopupMenu is already set for another TrayIcon");
        }
        isTrayIconPopup = true;
      }
      if (popup != null) {
        popup.isTrayIconPopup = false;
      }
      popup = paramPopupMenu;
    }
  }
  
  public PopupMenu getPopupMenu()
  {
    return popup;
  }
  
  public void setToolTip(String paramString)
  {
    tooltip = paramString;
    TrayIconPeer localTrayIconPeer = peer;
    if (localTrayIconPeer != null) {
      localTrayIconPeer.setToolTip(paramString);
    }
  }
  
  public String getToolTip()
  {
    return tooltip;
  }
  
  public void setImageAutoSize(boolean paramBoolean)
  {
    autosize = paramBoolean;
    TrayIconPeer localTrayIconPeer = peer;
    if (localTrayIconPeer != null) {
      localTrayIconPeer.updateImage();
    }
  }
  
  public boolean isImageAutoSize()
  {
    return autosize;
  }
  
  public synchronized void addMouseListener(MouseListener paramMouseListener)
  {
    if (paramMouseListener == null) {
      return;
    }
    mouseListener = AWTEventMulticaster.add(mouseListener, paramMouseListener);
  }
  
  public synchronized void removeMouseListener(MouseListener paramMouseListener)
  {
    if (paramMouseListener == null) {
      return;
    }
    mouseListener = AWTEventMulticaster.remove(mouseListener, paramMouseListener);
  }
  
  public synchronized MouseListener[] getMouseListeners()
  {
    return (MouseListener[])AWTEventMulticaster.getListeners(mouseListener, MouseListener.class);
  }
  
  public synchronized void addMouseMotionListener(MouseMotionListener paramMouseMotionListener)
  {
    if (paramMouseMotionListener == null) {
      return;
    }
    mouseMotionListener = AWTEventMulticaster.add(mouseMotionListener, paramMouseMotionListener);
  }
  
  public synchronized void removeMouseMotionListener(MouseMotionListener paramMouseMotionListener)
  {
    if (paramMouseMotionListener == null) {
      return;
    }
    mouseMotionListener = AWTEventMulticaster.remove(mouseMotionListener, paramMouseMotionListener);
  }
  
  public synchronized MouseMotionListener[] getMouseMotionListeners()
  {
    return (MouseMotionListener[])AWTEventMulticaster.getListeners(mouseMotionListener, MouseMotionListener.class);
  }
  
  public String getActionCommand()
  {
    return actionCommand;
  }
  
  public void setActionCommand(String paramString)
  {
    actionCommand = paramString;
  }
  
  public synchronized void addActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    actionListener = AWTEventMulticaster.add(actionListener, paramActionListener);
  }
  
  public synchronized void removeActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    actionListener = AWTEventMulticaster.remove(actionListener, paramActionListener);
  }
  
  public synchronized ActionListener[] getActionListeners()
  {
    return (ActionListener[])AWTEventMulticaster.getListeners(actionListener, ActionListener.class);
  }
  
  public void displayMessage(String paramString1, String paramString2, MessageType paramMessageType)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      throw new NullPointerException("displaying the message with both caption and text being null");
    }
    TrayIconPeer localTrayIconPeer = peer;
    if (localTrayIconPeer != null) {
      localTrayIconPeer.displayMessage(paramString1, paramString2, paramMessageType.name());
    }
  }
  
  public Dimension getSize()
  {
    return SystemTray.getSystemTray().getTrayIconSize();
  }
  
  void addNotify()
    throws AWTException
  {
    synchronized (this)
    {
      if (peer == null)
      {
        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        if ((localToolkit instanceof SunToolkit)) {
          peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
        } else if ((localToolkit instanceof HeadlessToolkit)) {
          peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
        }
      }
    }
    peer.setToolTip(tooltip);
  }
  
  void removeNotify()
  {
    TrayIconPeer localTrayIconPeer = null;
    synchronized (this)
    {
      localTrayIconPeer = peer;
      peer = null;
    }
    if (localTrayIconPeer != null) {
      localTrayIconPeer.dispose();
    }
  }
  
  void setID(int paramInt)
  {
    id = paramInt;
  }
  
  int getID()
  {
    return id;
  }
  
  void dispatchEvent(AWTEvent paramAWTEvent)
  {
    EventQueue.setCurrentEventAndMostRecentTime(paramAWTEvent);
    Toolkit.getDefaultToolkit().notifyAWTEventListeners(paramAWTEvent);
    processEvent(paramAWTEvent);
  }
  
  void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof MouseEvent)) {
      switch (paramAWTEvent.getID())
      {
      case 500: 
      case 501: 
      case 502: 
        processMouseEvent((MouseEvent)paramAWTEvent);
        break;
      case 503: 
        processMouseMotionEvent((MouseEvent)paramAWTEvent);
        break;
      default: 
        return;
      }
    } else if ((paramAWTEvent instanceof ActionEvent)) {
      processActionEvent((ActionEvent)paramAWTEvent);
    }
  }
  
  void processMouseEvent(MouseEvent paramMouseEvent)
  {
    MouseListener localMouseListener = mouseListener;
    if (localMouseListener != null)
    {
      int i = paramMouseEvent.getID();
      switch (i)
      {
      case 501: 
        localMouseListener.mousePressed(paramMouseEvent);
        break;
      case 502: 
        localMouseListener.mouseReleased(paramMouseEvent);
        break;
      case 500: 
        localMouseListener.mouseClicked(paramMouseEvent);
        break;
      }
    }
  }
  
  void processMouseMotionEvent(MouseEvent paramMouseEvent)
  {
    MouseMotionListener localMouseMotionListener = mouseMotionListener;
    if ((localMouseMotionListener != null) && (paramMouseEvent.getID() == 503)) {
      localMouseMotionListener.mouseMoved(paramMouseEvent);
    }
  }
  
  void processActionEvent(ActionEvent paramActionEvent)
  {
    ActionListener localActionListener = actionListener;
    if (localActionListener != null) {
      localActionListener.actionPerformed(paramActionEvent);
    }
  }
  
  private static native void initIDs();
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setTrayIconAccessor(new AWTAccessor.TrayIconAccessor()
    {
      public void addNotify(TrayIcon paramAnonymousTrayIcon)
        throws AWTException
      {
        paramAnonymousTrayIcon.addNotify();
      }
      
      public void removeNotify(TrayIcon paramAnonymousTrayIcon)
      {
        paramAnonymousTrayIcon.removeNotify();
      }
    });
  }
  
  public static enum MessageType
  {
    ERROR,  WARNING,  INFO,  NONE;
    
    private MessageType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\TrayIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */