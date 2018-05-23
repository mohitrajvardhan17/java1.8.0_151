package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.lang.reflect.Field;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventObject;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.AWTEventAccessor;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class AWTEvent
  extends EventObject
{
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.AWTEvent");
  private byte[] bdata;
  protected int id;
  protected boolean consumed = false;
  private volatile transient AccessControlContext acc = AccessController.getContext();
  transient boolean focusManagerIsDispatching = false;
  transient boolean isPosted;
  private transient boolean isSystemGenerated;
  public static final long COMPONENT_EVENT_MASK = 1L;
  public static final long CONTAINER_EVENT_MASK = 2L;
  public static final long FOCUS_EVENT_MASK = 4L;
  public static final long KEY_EVENT_MASK = 8L;
  public static final long MOUSE_EVENT_MASK = 16L;
  public static final long MOUSE_MOTION_EVENT_MASK = 32L;
  public static final long WINDOW_EVENT_MASK = 64L;
  public static final long ACTION_EVENT_MASK = 128L;
  public static final long ADJUSTMENT_EVENT_MASK = 256L;
  public static final long ITEM_EVENT_MASK = 512L;
  public static final long TEXT_EVENT_MASK = 1024L;
  public static final long INPUT_METHOD_EVENT_MASK = 2048L;
  static final long INPUT_METHODS_ENABLED_MASK = 4096L;
  public static final long PAINT_EVENT_MASK = 8192L;
  public static final long INVOCATION_EVENT_MASK = 16384L;
  public static final long HIERARCHY_EVENT_MASK = 32768L;
  public static final long HIERARCHY_BOUNDS_EVENT_MASK = 65536L;
  public static final long MOUSE_WHEEL_EVENT_MASK = 131072L;
  public static final long WINDOW_STATE_EVENT_MASK = 262144L;
  public static final long WINDOW_FOCUS_EVENT_MASK = 524288L;
  public static final int RESERVED_ID_MAX = 1999;
  private static Field inputEvent_CanAccessSystemClipboard_Field = null;
  private static final long serialVersionUID = -1825314779160409405L;
  
  final AccessControlContext getAccessControlContext()
  {
    if (acc == null) {
      throw new SecurityException("AWTEvent is missing AccessControlContext");
    }
    return acc;
  }
  
  private static synchronized Field get_InputEvent_CanAccessSystemClipboard()
  {
    if (inputEvent_CanAccessSystemClipboard_Field == null) {
      inputEvent_CanAccessSystemClipboard_Field = (Field)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Field run()
        {
          Field localField = null;
          try
          {
            localField = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
            localField.setAccessible(true);
            return localField;
          }
          catch (SecurityException localSecurityException)
          {
            if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
              AWTEvent.log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got SecurityException ", localSecurityException);
            }
          }
          catch (NoSuchFieldException localNoSuchFieldException)
          {
            if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
              AWTEvent.log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got NoSuchFieldException ", localNoSuchFieldException);
            }
          }
          return null;
        }
      });
    }
    return inputEvent_CanAccessSystemClipboard_Field;
  }
  
  private static native void initIDs();
  
  public AWTEvent(Event paramEvent)
  {
    this(target, id);
  }
  
  public AWTEvent(Object paramObject, int paramInt)
  {
    super(paramObject);
    id = paramInt;
    switch (paramInt)
    {
    case 601: 
    case 701: 
    case 900: 
    case 1001: 
      consumed = true;
      break;
    }
  }
  
  public void setSource(Object paramObject)
  {
    if (source == paramObject) {
      return;
    }
    Object localObject1 = null;
    if ((paramObject instanceof Component)) {
      for (localObject1 = (Component)paramObject; (localObject1 != null) && (peer != null) && ((peer instanceof LightweightPeer)); localObject1 = parent) {}
    }
    synchronized (this)
    {
      source = paramObject;
      if (localObject1 != null)
      {
        ComponentPeer localComponentPeer = peer;
        if (localComponentPeer != null) {
          nativeSetSource(localComponentPeer);
        }
      }
    }
  }
  
  private native void nativeSetSource(ComponentPeer paramComponentPeer);
  
  public int getID()
  {
    return id;
  }
  
  public String toString()
  {
    String str = null;
    if ((source instanceof Component)) {
      str = ((Component)source).getName();
    } else if ((source instanceof MenuComponent)) {
      str = ((MenuComponent)source).getName();
    }
    return getClass().getName() + "[" + paramString() + "] on " + (str != null ? str : source);
  }
  
  public String paramString()
  {
    return "";
  }
  
  protected void consume()
  {
    switch (id)
    {
    case 401: 
    case 402: 
    case 501: 
    case 502: 
    case 503: 
    case 504: 
    case 505: 
    case 506: 
    case 507: 
    case 1100: 
    case 1101: 
      consumed = true;
      break;
    }
  }
  
  protected boolean isConsumed()
  {
    return consumed;
  }
  
  Event convertToOld()
  {
    Object localObject1 = getSource();
    int i = id;
    Object localObject2;
    switch (id)
    {
    case 401: 
    case 402: 
      KeyEvent localKeyEvent = (KeyEvent)this;
      if (localKeyEvent.isActionKey()) {
        i = id == 401 ? 403 : 404;
      }
      int j = localKeyEvent.getKeyCode();
      if ((j == 16) || (j == 17) || (j == 18)) {
        return null;
      }
      return new Event(localObject1, localKeyEvent.getWhen(), i, 0, 0, Event.getOldEventKey(localKeyEvent), localKeyEvent.getModifiers() & 0xFFFFFFEF);
    case 501: 
    case 502: 
    case 503: 
    case 504: 
    case 505: 
    case 506: 
      MouseEvent localMouseEvent = (MouseEvent)this;
      Event localEvent = new Event(localObject1, localMouseEvent.getWhen(), i, localMouseEvent.getX(), localMouseEvent.getY(), 0, localMouseEvent.getModifiers() & 0xFFFFFFEF);
      clickCount = localMouseEvent.getClickCount();
      return localEvent;
    case 1004: 
      return new Event(localObject1, 1004, null);
    case 1005: 
      return new Event(localObject1, 1005, null);
    case 201: 
    case 203: 
    case 204: 
      return new Event(localObject1, i, null);
    case 100: 
      if (((localObject1 instanceof Frame)) || ((localObject1 instanceof Dialog)))
      {
        localObject2 = ((Component)localObject1).getLocation();
        return new Event(localObject1, 0L, 205, x, y, 0, 0);
      }
      break;
    case 1001: 
      localObject2 = (ActionEvent)this;
      String str;
      if ((localObject1 instanceof Button)) {
        str = ((Button)localObject1).getLabel();
      } else if ((localObject1 instanceof MenuItem)) {
        str = ((MenuItem)localObject1).getLabel();
      } else {
        str = ((ActionEvent)localObject2).getActionCommand();
      }
      return new Event(localObject1, 0L, i, 0, 0, 0, ((ActionEvent)localObject2).getModifiers(), str);
    case 701: 
      ItemEvent localItemEvent = (ItemEvent)this;
      Object localObject3;
      if ((localObject1 instanceof List))
      {
        i = localItemEvent.getStateChange() == 1 ? 701 : 702;
        localObject3 = localItemEvent.getItem();
      }
      else
      {
        i = 1001;
        if ((localObject1 instanceof Choice)) {
          localObject3 = localItemEvent.getItem();
        } else {
          localObject3 = Boolean.valueOf(localItemEvent.getStateChange() == 1);
        }
      }
      return new Event(localObject1, i, localObject3);
    case 601: 
      AdjustmentEvent localAdjustmentEvent = (AdjustmentEvent)this;
      switch (localAdjustmentEvent.getAdjustmentType())
      {
      case 1: 
        i = 602;
        break;
      case 2: 
        i = 601;
        break;
      case 4: 
        i = 604;
        break;
      case 3: 
        i = 603;
        break;
      case 5: 
        if (localAdjustmentEvent.getValueIsAdjusting()) {
          i = 605;
        } else {
          i = 607;
        }
        break;
      default: 
        return null;
      }
      return new Event(localObject1, i, Integer.valueOf(localAdjustmentEvent.getValue()));
    }
    return null;
  }
  
  void copyPrivateDataInto(AWTEvent paramAWTEvent)
  {
    bdata = bdata;
    if (((this instanceof InputEvent)) && ((paramAWTEvent instanceof InputEvent)))
    {
      Field localField = get_InputEvent_CanAccessSystemClipboard();
      if (localField != null) {
        try
        {
          boolean bool = localField.getBoolean(this);
          localField.setBoolean(paramAWTEvent, bool);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("AWTEvent.copyPrivateDataInto() got IllegalAccessException ", localIllegalAccessException);
          }
        }
      }
    }
    isSystemGenerated = isSystemGenerated;
  }
  
  void dispatched()
  {
    if ((this instanceof InputEvent))
    {
      Field localField = get_InputEvent_CanAccessSystemClipboard();
      if (localField != null) {
        try
        {
          localField.setBoolean(this, false);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("AWTEvent.dispatched() got IllegalAccessException ", localIllegalAccessException);
          }
        }
      }
    }
  }
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setAWTEventAccessor(new AWTAccessor.AWTEventAccessor()
    {
      public void setPosted(AWTEvent paramAnonymousAWTEvent)
      {
        isPosted = true;
      }
      
      public void setSystemGenerated(AWTEvent paramAnonymousAWTEvent)
      {
        isSystemGenerated = true;
      }
      
      public boolean isSystemGenerated(AWTEvent paramAnonymousAWTEvent)
      {
        return isSystemGenerated;
      }
      
      public AccessControlContext getAccessControlContext(AWTEvent paramAnonymousAWTEvent)
      {
        return paramAnonymousAWTEvent.getAccessControlContext();
      }
      
      public byte[] getBData(AWTEvent paramAnonymousAWTEvent)
      {
        return bdata;
      }
      
      public void setBData(AWTEvent paramAnonymousAWTEvent, byte[] paramAnonymousArrayOfByte)
      {
        bdata = paramAnonymousArrayOfByte;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\AWTEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */