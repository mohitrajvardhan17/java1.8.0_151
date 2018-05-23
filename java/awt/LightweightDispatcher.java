package java.awt;

import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.InputEventAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetEvent;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

class LightweightDispatcher
  implements Serializable, AWTEventListener
{
  private static final long serialVersionUID = 5184291520170872969L;
  private static final int LWD_MOUSE_DRAGGED_OVER = 1500;
  private static final PlatformLogger eventLog;
  private static final int BUTTONS_DOWN_MASK;
  private Container nativeContainer;
  private Component focus;
  private transient WeakReference<Component> mouseEventTarget;
  private transient WeakReference<Component> targetLastEntered;
  private transient WeakReference<Component> targetLastEnteredDT;
  private transient boolean isMouseInNativeContainer = false;
  private transient boolean isMouseDTInNativeContainer = false;
  private Cursor nativeCursor;
  private long eventMask;
  private static final long PROXY_EVENT_MASK = 131132L;
  private static final long MOUSE_MASK = 131120L;
  
  LightweightDispatcher(Container paramContainer)
  {
    nativeContainer = paramContainer;
    mouseEventTarget = new WeakReference(null);
    targetLastEntered = new WeakReference(null);
    targetLastEnteredDT = new WeakReference(null);
    eventMask = 0L;
  }
  
  void dispose()
  {
    stopListeningForOtherDrags();
    mouseEventTarget.clear();
    targetLastEntered.clear();
    targetLastEnteredDT.clear();
  }
  
  void enableEvents(long paramLong)
  {
    eventMask |= paramLong;
  }
  
  boolean dispatchEvent(AWTEvent paramAWTEvent)
  {
    boolean bool = false;
    Object localObject;
    if ((paramAWTEvent instanceof SunDropTargetEvent))
    {
      localObject = (SunDropTargetEvent)paramAWTEvent;
      bool = processDropTargetEvent((SunDropTargetEvent)localObject);
    }
    else
    {
      if (((paramAWTEvent instanceof MouseEvent)) && ((eventMask & 0x20030) != 0L))
      {
        localObject = (MouseEvent)paramAWTEvent;
        bool = processMouseEvent((MouseEvent)localObject);
      }
      if (paramAWTEvent.getID() == 503) {
        nativeContainer.updateCursorImmediately();
      }
    }
    return bool;
  }
  
  private boolean isMouseGrab(MouseEvent paramMouseEvent)
  {
    int i = paramMouseEvent.getModifiersEx();
    if ((paramMouseEvent.getID() == 501) || (paramMouseEvent.getID() == 502)) {
      i ^= InputEvent.getMaskForButton(paramMouseEvent.getButton());
    }
    return (i & BUTTONS_DOWN_MASK) != 0;
  }
  
  private boolean processMouseEvent(MouseEvent paramMouseEvent)
  {
    int i = paramMouseEvent.getID();
    Component localComponent1 = nativeContainer.getMouseEventTarget(paramMouseEvent.getX(), paramMouseEvent.getY(), true);
    trackMouseEnterExit(localComponent1, paramMouseEvent);
    Component localComponent2 = (Component)mouseEventTarget.get();
    if ((!isMouseGrab(paramMouseEvent)) && (i != 500))
    {
      localComponent2 = localComponent1 != nativeContainer ? localComponent1 : null;
      mouseEventTarget = new WeakReference(localComponent2);
    }
    if (localComponent2 != null)
    {
      switch (i)
      {
      case 504: 
      case 505: 
        break;
      case 501: 
        retargetMouseEvent(localComponent2, i, paramMouseEvent);
        break;
      case 502: 
        retargetMouseEvent(localComponent2, i, paramMouseEvent);
        break;
      case 500: 
        if (localComponent1 == localComponent2) {
          retargetMouseEvent(localComponent1, i, paramMouseEvent);
        }
        break;
      case 503: 
        retargetMouseEvent(localComponent2, i, paramMouseEvent);
        break;
      case 506: 
        if (isMouseGrab(paramMouseEvent)) {
          retargetMouseEvent(localComponent2, i, paramMouseEvent);
        }
        break;
      case 507: 
        if ((eventLog.isLoggable(PlatformLogger.Level.FINEST)) && (localComponent1 != null)) {
          eventLog.finest("retargeting mouse wheel to " + localComponent1.getName() + ", " + localComponent1.getClass());
        }
        retargetMouseEvent(localComponent1, i, paramMouseEvent);
      }
      if (i != 507) {
        paramMouseEvent.consume();
      }
    }
    return paramMouseEvent.isConsumed();
  }
  
  private boolean processDropTargetEvent(SunDropTargetEvent paramSunDropTargetEvent)
  {
    int i = paramSunDropTargetEvent.getID();
    int j = paramSunDropTargetEvent.getX();
    int k = paramSunDropTargetEvent.getY();
    if (!nativeContainer.contains(j, k))
    {
      localObject = nativeContainer.getSize();
      if (width <= j) {
        j = width - 1;
      } else if (j < 0) {
        j = 0;
      }
      if (height <= k) {
        k = height - 1;
      } else if (k < 0) {
        k = 0;
      }
    }
    Object localObject = nativeContainer.getDropTargetEventTarget(j, k, true);
    trackMouseEnterExit((Component)localObject, paramSunDropTargetEvent);
    if ((localObject != nativeContainer) && (localObject != null)) {
      switch (i)
      {
      case 504: 
      case 505: 
        break;
      default: 
        retargetMouseEvent((Component)localObject, i, paramSunDropTargetEvent);
        paramSunDropTargetEvent.consume();
      }
    }
    return paramSunDropTargetEvent.isConsumed();
  }
  
  private void trackDropTargetEnterExit(Component paramComponent, MouseEvent paramMouseEvent)
  {
    int i = paramMouseEvent.getID();
    if ((i == 504) && (isMouseDTInNativeContainer)) {
      targetLastEnteredDT.clear();
    } else if (i == 504) {
      isMouseDTInNativeContainer = true;
    } else if (i == 505) {
      isMouseDTInNativeContainer = false;
    }
    Component localComponent = retargetMouseEnterExit(paramComponent, paramMouseEvent, (Component)targetLastEnteredDT.get(), isMouseDTInNativeContainer);
    targetLastEnteredDT = new WeakReference(localComponent);
  }
  
  private void trackMouseEnterExit(Component paramComponent, MouseEvent paramMouseEvent)
  {
    if ((paramMouseEvent instanceof SunDropTargetEvent))
    {
      trackDropTargetEnterExit(paramComponent, paramMouseEvent);
      return;
    }
    int i = paramMouseEvent.getID();
    if ((i != 505) && (i != 506) && (i != 1500) && (!isMouseInNativeContainer))
    {
      isMouseInNativeContainer = true;
      startListeningForOtherDrags();
    }
    else if (i == 505)
    {
      isMouseInNativeContainer = false;
      stopListeningForOtherDrags();
    }
    Component localComponent = retargetMouseEnterExit(paramComponent, paramMouseEvent, (Component)targetLastEntered.get(), isMouseInNativeContainer);
    targetLastEntered = new WeakReference(localComponent);
  }
  
  private Component retargetMouseEnterExit(Component paramComponent1, MouseEvent paramMouseEvent, Component paramComponent2, boolean paramBoolean)
  {
    int i = paramMouseEvent.getID();
    Component localComponent = paramBoolean ? paramComponent1 : null;
    if (paramComponent2 != localComponent)
    {
      if (paramComponent2 != null) {
        retargetMouseEvent(paramComponent2, 505, paramMouseEvent);
      }
      if (i == 505) {
        paramMouseEvent.consume();
      }
      if (localComponent != null) {
        retargetMouseEvent(localComponent, 504, paramMouseEvent);
      }
      if (i == 504) {
        paramMouseEvent.consume();
      }
    }
    return localComponent;
  }
  
  private void startListeningForOtherDrags()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        nativeContainer.getToolkit().addAWTEventListener(LightweightDispatcher.this, 48L);
        return null;
      }
    });
  }
  
  private void stopListeningForOtherDrags()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        nativeContainer.getToolkit().removeAWTEventListener(LightweightDispatcher.this);
        return null;
      }
    });
  }
  
  public void eventDispatched(AWTEvent paramAWTEvent)
  {
    int i = ((paramAWTEvent instanceof MouseEvent)) && (!(paramAWTEvent instanceof SunDropTargetEvent)) && (id == 506) && (paramAWTEvent.getSource() != nativeContainer) ? 1 : 0;
    if (i == 0) {
      return;
    }
    MouseEvent localMouseEvent1 = (MouseEvent)paramAWTEvent;
    MouseEvent localMouseEvent2;
    synchronized (nativeContainer.getTreeLock())
    {
      Component localComponent = localMouseEvent1.getComponent();
      if (!localComponent.isShowing()) {
        return;
      }
      for (Container localContainer = nativeContainer; (localContainer != null) && (!(localContainer instanceof Window)); localContainer = localContainer.getParent_NoClientCode()) {}
      if ((localContainer == null) || (((Window)localContainer).isModalBlocked())) {
        return;
      }
      localMouseEvent2 = new MouseEvent(nativeContainer, 1500, localMouseEvent1.getWhen(), localMouseEvent1.getModifiersEx() | localMouseEvent1.getModifiers(), localMouseEvent1.getX(), localMouseEvent1.getY(), localMouseEvent1.getXOnScreen(), localMouseEvent1.getYOnScreen(), localMouseEvent1.getClickCount(), localMouseEvent1.isPopupTrigger(), localMouseEvent1.getButton());
      localMouseEvent1.copyPrivateDataInto(localMouseEvent2);
      final Point localPoint = localComponent.getLocationOnScreen();
      if (AppContext.getAppContext() != nativeContainer.appContext)
      {
        localObject1 = localMouseEvent2;
        Runnable local3 = new Runnable()
        {
          public void run()
          {
            if (!nativeContainer.isShowing()) {
              return;
            }
            Point localPoint = nativeContainer.getLocationOnScreen();
            localObject1.translatePoint(localPointx - x, localPointy - y);
            Component localComponent = nativeContainer.getMouseEventTarget(localObject1.getX(), localObject1.getY(), true);
            LightweightDispatcher.this.trackMouseEnterExit(localComponent, localObject1);
          }
        };
        SunToolkit.executeOnEventHandlerThread(nativeContainer, local3);
        return;
      }
      if (!nativeContainer.isShowing()) {
        return;
      }
      final Object localObject1 = nativeContainer.getLocationOnScreen();
      localMouseEvent2.translatePoint(x - x, y - y);
    }
    ??? = nativeContainer.getMouseEventTarget(localMouseEvent2.getX(), localMouseEvent2.getY(), true);
    trackMouseEnterExit((Component)???, localMouseEvent2);
  }
  
  void retargetMouseEvent(Component paramComponent, int paramInt, MouseEvent paramMouseEvent)
  {
    if (paramComponent == null) {
      return;
    }
    int i = paramMouseEvent.getX();
    int j = paramMouseEvent.getY();
    for (Object localObject1 = paramComponent; (localObject1 != null) && (localObject1 != nativeContainer); localObject1 = ((Component)localObject1).getParent())
    {
      i -= x;
      j -= y;
    }
    if (localObject1 != null)
    {
      Object localObject2;
      if ((paramMouseEvent instanceof SunDropTargetEvent)) {
        localObject2 = new SunDropTargetEvent(paramComponent, paramInt, i, j, ((SunDropTargetEvent)paramMouseEvent).getDispatcher());
      } else if (paramInt == 507) {
        localObject2 = new MouseWheelEvent(paramComponent, paramInt, paramMouseEvent.getWhen(), paramMouseEvent.getModifiersEx() | paramMouseEvent.getModifiers(), i, j, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), ((MouseWheelEvent)paramMouseEvent).getScrollType(), ((MouseWheelEvent)paramMouseEvent).getScrollAmount(), ((MouseWheelEvent)paramMouseEvent).getWheelRotation(), ((MouseWheelEvent)paramMouseEvent).getPreciseWheelRotation());
      } else {
        localObject2 = new MouseEvent(paramComponent, paramInt, paramMouseEvent.getWhen(), paramMouseEvent.getModifiersEx() | paramMouseEvent.getModifiers(), i, j, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), paramMouseEvent.getButton());
      }
      paramMouseEvent.copyPrivateDataInto((AWTEvent)localObject2);
      if (paramComponent == nativeContainer)
      {
        ((Container)paramComponent).dispatchEventToSelf((AWTEvent)localObject2);
      }
      else
      {
        assert (AppContext.getAppContext() == appContext);
        if (nativeContainer.modalComp != null)
        {
          if (((Container)nativeContainer.modalComp).isAncestorOf(paramComponent)) {
            paramComponent.dispatchEvent((AWTEvent)localObject2);
          } else {
            paramMouseEvent.consume();
          }
        }
        else {
          paramComponent.dispatchEvent((AWTEvent)localObject2);
        }
      }
      if ((paramInt == 507) && (((MouseEvent)localObject2).isConsumed())) {
        paramMouseEvent.consume();
      }
    }
  }
  
  static
  {
    eventLog = PlatformLogger.getLogger("java.awt.event.LightweightDispatcher");
    int[] arrayOfInt1 = AWTAccessor.getInputEventAccessor().getButtonDownMasks();
    int i = 0;
    for (int m : arrayOfInt1) {
      i |= m;
    }
    BUTTONS_DOWN_MASK = i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\LightweightDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */