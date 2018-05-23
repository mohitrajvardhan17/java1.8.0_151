package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.DefaultKeyboardFocusManagerAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.SunToolkit;
import sun.awt.TimedWindowEvent;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class DefaultKeyboardFocusManager
  extends KeyboardFocusManager
{
  private static final PlatformLogger focusLog = PlatformLogger.getLogger("java.awt.focus.DefaultKeyboardFocusManager");
  private static final WeakReference<Window> NULL_WINDOW_WR = new WeakReference(null);
  private static final WeakReference<Component> NULL_COMPONENT_WR = new WeakReference(null);
  private WeakReference<Window> realOppositeWindowWR = NULL_WINDOW_WR;
  private WeakReference<Component> realOppositeComponentWR = NULL_COMPONENT_WR;
  private int inSendMessage;
  private LinkedList<KeyEvent> enqueuedKeyEvents = new LinkedList();
  private LinkedList<TypeAheadMarker> typeAheadMarkers = new LinkedList();
  private boolean consumeNextKeyTyped;
  
  public DefaultKeyboardFocusManager() {}
  
  private Window getOwningFrameDialog(Window paramWindow)
  {
    while ((paramWindow != null) && (!(paramWindow instanceof Frame)) && (!(paramWindow instanceof Dialog))) {
      paramWindow = (Window)paramWindow.getParent();
    }
    return paramWindow;
  }
  
  private void restoreFocus(FocusEvent paramFocusEvent, Window paramWindow)
  {
    Component localComponent1 = (Component)realOppositeComponentWR.get();
    Component localComponent2 = paramFocusEvent.getComponent();
    if (((paramWindow == null) || (!restoreFocus(paramWindow, localComponent2, false))) && ((localComponent1 == null) || (!doRestoreFocus(localComponent1, localComponent2, false))) && ((paramFocusEvent.getOppositeComponent() == null) || (!doRestoreFocus(paramFocusEvent.getOppositeComponent(), localComponent2, false)))) {
      clearGlobalFocusOwnerPriv();
    }
  }
  
  private void restoreFocus(WindowEvent paramWindowEvent)
  {
    Window localWindow = (Window)realOppositeWindowWR.get();
    if (((localWindow == null) || (!restoreFocus(localWindow, null, false))) && ((paramWindowEvent.getOppositeWindow() == null) || (!restoreFocus(paramWindowEvent.getOppositeWindow(), null, false)))) {
      clearGlobalFocusOwnerPriv();
    }
  }
  
  private boolean restoreFocus(Window paramWindow, Component paramComponent, boolean paramBoolean)
  {
    Component localComponent = KeyboardFocusManager.getMostRecentFocusOwner(paramWindow);
    if ((localComponent != null) && (localComponent != paramComponent) && (doRestoreFocus(localComponent, paramComponent, false))) {
      return true;
    }
    if (paramBoolean)
    {
      clearGlobalFocusOwnerPriv();
      return true;
    }
    return false;
  }
  
  private boolean restoreFocus(Component paramComponent, boolean paramBoolean)
  {
    return doRestoreFocus(paramComponent, null, paramBoolean);
  }
  
  private boolean doRestoreFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean)
  {
    if ((paramComponent1 != paramComponent2) && (paramComponent1.isShowing()) && (paramComponent1.canBeFocusOwner()) && (paramComponent1.requestFocus(false, CausedFocusEvent.Cause.ROLLBACK))) {
      return true;
    }
    Component localComponent = paramComponent1.getNextFocusCandidate();
    if ((localComponent != null) && (localComponent != paramComponent2) && (localComponent.requestFocusInWindow(CausedFocusEvent.Cause.ROLLBACK))) {
      return true;
    }
    if (paramBoolean)
    {
      clearGlobalFocusOwnerPriv();
      return true;
    }
    return false;
  }
  
  static boolean sendMessage(Component paramComponent, AWTEvent paramAWTEvent)
  {
    isPosted = true;
    AppContext localAppContext1 = AppContext.getAppContext();
    final AppContext localAppContext2 = appContext;
    DefaultKeyboardFocusManagerSentEvent localDefaultKeyboardFocusManagerSentEvent = new DefaultKeyboardFocusManagerSentEvent(paramAWTEvent, localAppContext1);
    if (localAppContext1 == localAppContext2)
    {
      localDefaultKeyboardFocusManagerSentEvent.dispatch();
    }
    else
    {
      if (localAppContext2.isDisposed()) {
        return false;
      }
      SunToolkit.postEvent(localAppContext2, localDefaultKeyboardFocusManagerSentEvent);
      if (EventQueue.isDispatchThread())
      {
        EventDispatchThread localEventDispatchThread = (EventDispatchThread)Thread.currentThread();
        localEventDispatchThread.pumpEvents(1007, new Conditional()
        {
          public boolean evaluate()
          {
            return (!val$se.dispatched) && (!localAppContext2.isDisposed());
          }
        });
      }
      else
      {
        synchronized (localDefaultKeyboardFocusManagerSentEvent)
        {
          for (;;)
          {
            if ((!dispatched) && (!localAppContext2.isDisposed())) {
              try
              {
                localDefaultKeyboardFocusManagerSentEvent.wait(1000L);
              }
              catch (InterruptedException localInterruptedException) {}
            }
          }
        }
      }
    }
    return dispatched;
  }
  
  private boolean repostIfFollowsKeyEvents(WindowEvent paramWindowEvent)
  {
    if (!(paramWindowEvent instanceof TimedWindowEvent)) {
      return false;
    }
    TimedWindowEvent localTimedWindowEvent = (TimedWindowEvent)paramWindowEvent;
    long l = localTimedWindowEvent.getWhen();
    synchronized (this)
    {
      KeyEvent localKeyEvent = enqueuedKeyEvents.isEmpty() ? null : (KeyEvent)enqueuedKeyEvents.getFirst();
      if ((localKeyEvent != null) && (l >= localKeyEvent.getWhen()))
      {
        TypeAheadMarker localTypeAheadMarker = typeAheadMarkers.isEmpty() ? null : (TypeAheadMarker)typeAheadMarkers.getFirst();
        if (localTypeAheadMarker != null)
        {
          Window localWindow = untilFocused.getContainingWindow();
          if ((localWindow != null) && (localWindow.isFocused()))
          {
            SunToolkit.postEvent(AppContext.getAppContext(), new SequencedEvent(paramWindowEvent));
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public boolean dispatchEvent(AWTEvent paramAWTEvent)
  {
    if ((focusLog.isLoggable(PlatformLogger.Level.FINE)) && (((paramAWTEvent instanceof WindowEvent)) || ((paramAWTEvent instanceof FocusEvent)))) {
      focusLog.fine("" + paramAWTEvent);
    }
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject5;
    Object localObject6;
    Object localObject4;
    Window localWindow3;
    switch (paramAWTEvent.getID())
    {
    case 207: 
      if (!repostIfFollowsKeyEvents((WindowEvent)paramAWTEvent))
      {
        localObject1 = (WindowEvent)paramAWTEvent;
        localObject2 = getGlobalFocusedWindow();
        localObject3 = ((WindowEvent)localObject1).getWindow();
        if (localObject3 != localObject2) {
          if ((!((Window)localObject3).isFocusableWindow()) || (!((Window)localObject3).isVisible()) || (!((Window)localObject3).isDisplayable()))
          {
            restoreFocus((WindowEvent)localObject1);
          }
          else
          {
            if (localObject2 != null)
            {
              boolean bool1 = sendMessage((Component)localObject2, new WindowEvent((Window)localObject2, 208, (Window)localObject3));
              if (!bool1)
              {
                setGlobalFocusOwner(null);
                setGlobalFocusedWindow(null);
              }
            }
            Window localWindow1 = getOwningFrameDialog((Window)localObject3);
            Window localWindow2 = getGlobalActiveWindow();
            if (localWindow1 != localWindow2)
            {
              sendMessage(localWindow1, new WindowEvent(localWindow1, 205, localWindow2));
              if (localWindow1 != getGlobalActiveWindow())
              {
                restoreFocus((WindowEvent)localObject1);
                break;
              }
            }
            setGlobalFocusedWindow((Window)localObject3);
            if (localObject3 != getGlobalFocusedWindow())
            {
              restoreFocus((WindowEvent)localObject1);
            }
            else
            {
              if (inSendMessage == 0)
              {
                localObject5 = KeyboardFocusManager.getMostRecentFocusOwner((Window)localObject3);
                if ((localObject5 == null) && (((Window)localObject3).isFocusableWindow())) {
                  localObject5 = ((Window)localObject3).getFocusTraversalPolicy().getInitialComponent((Window)localObject3);
                }
                localObject6 = null;
                synchronized (KeyboardFocusManager.class)
                {
                  localObject6 = ((Window)localObject3).setTemporaryLostComponent(null);
                }
                if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                  focusLog.finer("tempLost {0}, toFocus {1}", new Object[] { localObject6, localObject5 });
                }
                if (localObject6 != null) {
                  ((Component)localObject6).requestFocusInWindow(CausedFocusEvent.Cause.ACTIVATION);
                }
                if ((localObject5 != null) && (localObject5 != localObject6)) {
                  ((Component)localObject5).requestFocusInWindow(CausedFocusEvent.Cause.ACTIVATION);
                }
              }
              localObject5 = (Window)realOppositeWindowWR.get();
              if (localObject5 != ((WindowEvent)localObject1).getOppositeWindow()) {
                localObject1 = new WindowEvent((Window)localObject3, 207, (Window)localObject5);
              }
              return typeAheadAssertions((Component)localObject3, (AWTEvent)localObject1);
            }
          }
        }
      }
      break;
    case 205: 
      localObject1 = (WindowEvent)paramAWTEvent;
      localObject2 = getGlobalActiveWindow();
      localObject3 = ((WindowEvent)localObject1).getWindow();
      if (localObject2 != localObject3) {
        if (localObject2 != null)
        {
          boolean bool2 = sendMessage((Component)localObject2, new WindowEvent((Window)localObject2, 206, (Window)localObject3));
          if (!bool2) {
            setGlobalActiveWindow(null);
          }
          if (getGlobalActiveWindow() != null) {
            break;
          }
        }
        else
        {
          setGlobalActiveWindow((Window)localObject3);
          if (localObject3 == getGlobalActiveWindow()) {
            return typeAheadAssertions((Component)localObject3, (AWTEvent)localObject1);
          }
        }
      }
      break;
    case 1004: 
      localObject1 = (FocusEvent)paramAWTEvent;
      localObject2 = (localObject1 instanceof CausedFocusEvent) ? ((CausedFocusEvent)localObject1).getCause() : CausedFocusEvent.Cause.UNKNOWN;
      localObject3 = getGlobalFocusOwner();
      localObject4 = ((FocusEvent)localObject1).getComponent();
      if (localObject3 == localObject4)
      {
        if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
          focusLog.fine("Skipping {0} because focus owner is the same", new Object[] { paramAWTEvent });
        }
        dequeueKeyEvents(-1L, (Component)localObject4);
      }
      else
      {
        if (localObject3 != null)
        {
          boolean bool3 = sendMessage((Component)localObject3, new CausedFocusEvent((Component)localObject3, 1005, ((FocusEvent)localObject1).isTemporary(), (Component)localObject4, (CausedFocusEvent.Cause)localObject2));
          if (!bool3)
          {
            setGlobalFocusOwner(null);
            if (!((FocusEvent)localObject1).isTemporary()) {
              setGlobalPermanentFocusOwner(null);
            }
          }
        }
        localWindow3 = SunToolkit.getContainingWindow((Component)localObject4);
        localObject5 = getGlobalFocusedWindow();
        if ((localWindow3 != null) && (localWindow3 != localObject5))
        {
          sendMessage(localWindow3, new WindowEvent(localWindow3, 207, (Window)localObject5));
          if (localWindow3 != getGlobalFocusedWindow())
          {
            dequeueKeyEvents(-1L, (Component)localObject4);
            break;
          }
        }
        if ((!((Component)localObject4).isFocusable()) || (!((Component)localObject4).isShowing()) || ((!((Component)localObject4).isEnabled()) && (!((CausedFocusEvent.Cause)localObject2).equals(CausedFocusEvent.Cause.UNKNOWN))))
        {
          dequeueKeyEvents(-1L, (Component)localObject4);
          if (KeyboardFocusManager.isAutoFocusTransferEnabled())
          {
            if (localWindow3 == null) {
              restoreFocus((FocusEvent)localObject1, (Window)localObject5);
            } else {
              restoreFocus((FocusEvent)localObject1, localWindow3);
            }
            setMostRecentFocusOwner(localWindow3, null);
          }
        }
        else
        {
          setGlobalFocusOwner((Component)localObject4);
          if (localObject4 != getGlobalFocusOwner())
          {
            dequeueKeyEvents(-1L, (Component)localObject4);
            if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
              restoreFocus((FocusEvent)localObject1, localWindow3);
            }
          }
          else
          {
            if (!((FocusEvent)localObject1).isTemporary())
            {
              setGlobalPermanentFocusOwner((Component)localObject4);
              if (localObject4 != getGlobalPermanentFocusOwner())
              {
                dequeueKeyEvents(-1L, (Component)localObject4);
                if (!KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                  break;
                }
                restoreFocus((FocusEvent)localObject1, localWindow3);
                break;
              }
            }
            setNativeFocusOwner(getHeavyweight((Component)localObject4));
            localObject6 = (Component)realOppositeComponentWR.get();
            if ((localObject6 != null) && (localObject6 != ((FocusEvent)localObject1).getOppositeComponent()))
            {
              localObject1 = new CausedFocusEvent((Component)localObject4, 1004, ((FocusEvent)localObject1).isTemporary(), (Component)localObject6, (CausedFocusEvent.Cause)localObject2);
              isPosted = true;
            }
            return typeAheadAssertions((Component)localObject4, (AWTEvent)localObject1);
          }
        }
      }
      break;
    case 1005: 
      localObject1 = (FocusEvent)paramAWTEvent;
      localObject2 = getGlobalFocusOwner();
      if (localObject2 == null)
      {
        if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
          focusLog.fine("Skipping {0} because focus owner is null", new Object[] { paramAWTEvent });
        }
      }
      else if (localObject2 == ((FocusEvent)localObject1).getOppositeComponent())
      {
        if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
          focusLog.fine("Skipping {0} because current focus owner is equal to opposite", new Object[] { paramAWTEvent });
        }
      }
      else
      {
        setGlobalFocusOwner(null);
        if (getGlobalFocusOwner() != null)
        {
          restoreFocus((Component)localObject2, true);
        }
        else
        {
          if (!((FocusEvent)localObject1).isTemporary())
          {
            setGlobalPermanentFocusOwner(null);
            if (getGlobalPermanentFocusOwner() != null)
            {
              restoreFocus((Component)localObject2, true);
              break;
            }
          }
          else
          {
            localObject3 = ((Component)localObject2).getContainingWindow();
            if (localObject3 != null) {
              ((Window)localObject3).setTemporaryLostComponent((Component)localObject2);
            }
          }
          setNativeFocusOwner(null);
          ((FocusEvent)localObject1).setSource(localObject2);
          realOppositeComponentWR = (((FocusEvent)localObject1).getOppositeComponent() != null ? new WeakReference(localObject2) : NULL_COMPONENT_WR);
          return typeAheadAssertions((Component)localObject2, (AWTEvent)localObject1);
        }
      }
      break;
    case 206: 
      localObject1 = (WindowEvent)paramAWTEvent;
      localObject2 = getGlobalActiveWindow();
      if ((localObject2 != null) && (localObject2 == paramAWTEvent.getSource()))
      {
        setGlobalActiveWindow(null);
        if (getGlobalActiveWindow() == null)
        {
          ((WindowEvent)localObject1).setSource(localObject2);
          return typeAheadAssertions((Component)localObject2, (AWTEvent)localObject1);
        }
      }
      break;
    case 208: 
      if (!repostIfFollowsKeyEvents((WindowEvent)paramAWTEvent))
      {
        localObject1 = (WindowEvent)paramAWTEvent;
        localObject2 = getGlobalFocusedWindow();
        localObject3 = ((WindowEvent)localObject1).getWindow();
        localObject4 = getGlobalActiveWindow();
        localWindow3 = ((WindowEvent)localObject1).getOppositeWindow();
        if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
          focusLog.fine("Active {0}, Current focused {1}, losing focus {2} opposite {3}", new Object[] { localObject4, localObject2, localObject3, localWindow3 });
        }
        if ((localObject2 != null) && ((inSendMessage != 0) || (localObject3 != localObject4) || (localWindow3 != localObject2)))
        {
          localObject5 = getGlobalFocusOwner();
          if (localObject5 != null)
          {
            localObject6 = null;
            if (localWindow3 != null)
            {
              localObject6 = localWindow3.getTemporaryLostComponent();
              if (localObject6 == null) {
                localObject6 = localWindow3.getMostRecentFocusOwner();
              }
            }
            if (localObject6 == null) {
              localObject6 = localWindow3;
            }
            sendMessage((Component)localObject5, new CausedFocusEvent((Component)localObject5, 1005, true, (Component)localObject6, CausedFocusEvent.Cause.ACTIVATION));
          }
          setGlobalFocusedWindow(null);
          if (getGlobalFocusedWindow() != null)
          {
            restoreFocus((Window)localObject2, null, true);
          }
          else
          {
            ((WindowEvent)localObject1).setSource(localObject2);
            realOppositeWindowWR = (localWindow3 != null ? new WeakReference(localObject2) : NULL_WINDOW_WR);
            typeAheadAssertions((Component)localObject2, (AWTEvent)localObject1);
            if (localWindow3 == null)
            {
              sendMessage((Component)localObject4, new WindowEvent((Window)localObject4, 206, null));
              if (getGlobalActiveWindow() != null) {
                restoreFocus((Window)localObject2, null, true);
              }
            }
          }
        }
      }
      break;
    case 400: 
    case 401: 
    case 402: 
      return typeAheadAssertions(null, paramAWTEvent);
    default: 
      return false;
    }
    return true;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    Component localComponent1 = isPosted ? getFocusOwner() : paramKeyEvent.getComponent();
    if ((localComponent1 != null) && (localComponent1.isShowing()) && (localComponent1.canBeFocusOwner()) && (!paramKeyEvent.isConsumed()))
    {
      Component localComponent2 = paramKeyEvent.getComponent();
      if ((localComponent2 != null) && (localComponent2.isEnabled())) {
        redispatchEvent(localComponent2, paramKeyEvent);
      }
    }
    boolean bool = false;
    List localList = getKeyEventPostProcessors();
    if (localList != null)
    {
      localObject = localList.iterator();
      while ((!bool) && (((Iterator)localObject).hasNext())) {
        bool = ((KeyEventPostProcessor)((Iterator)localObject).next()).postProcessKeyEvent(paramKeyEvent);
      }
    }
    if (!bool) {
      postProcessKeyEvent(paramKeyEvent);
    }
    Object localObject = paramKeyEvent.getComponent();
    ComponentPeer localComponentPeer = ((Component)localObject).getPeer();
    if ((localComponentPeer == null) || ((localComponentPeer instanceof LightweightPeer)))
    {
      Container localContainer = ((Component)localObject).getNativeContainer();
      if (localContainer != null) {
        localComponentPeer = localContainer.getPeer();
      }
    }
    if (localComponentPeer != null) {
      localComponentPeer.handleEvent(paramKeyEvent);
    }
    return true;
  }
  
  public boolean postProcessKeyEvent(KeyEvent paramKeyEvent)
  {
    if (!paramKeyEvent.isConsumed())
    {
      Component localComponent = paramKeyEvent.getComponent();
      Container localContainer = (Container)((localComponent instanceof Container) ? localComponent : localComponent.getParent());
      if (localContainer != null) {
        localContainer.postProcessKeyEvent(paramKeyEvent);
      }
    }
    return true;
  }
  
  private void pumpApprovedKeyEvents()
  {
    KeyEvent localKeyEvent;
    do
    {
      localKeyEvent = null;
      synchronized (this)
      {
        if (enqueuedKeyEvents.size() != 0)
        {
          localKeyEvent = (KeyEvent)enqueuedKeyEvents.getFirst();
          if (typeAheadMarkers.size() != 0)
          {
            TypeAheadMarker localTypeAheadMarker = (TypeAheadMarker)typeAheadMarkers.getFirst();
            if (localKeyEvent.getWhen() > after) {
              localKeyEvent = null;
            }
          }
          if (localKeyEvent != null)
          {
            if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
              focusLog.finer("Pumping approved event {0}", new Object[] { localKeyEvent });
            }
            enqueuedKeyEvents.removeFirst();
          }
        }
      }
      if (localKeyEvent != null) {
        preDispatchKeyEvent(localKeyEvent);
      }
    } while (localKeyEvent != null);
  }
  
  void dumpMarkers()
  {
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
    {
      focusLog.finest(">>> Markers dump, time: {0}", new Object[] { Long.valueOf(System.currentTimeMillis()) });
      synchronized (this)
      {
        if (typeAheadMarkers.size() != 0)
        {
          Iterator localIterator = typeAheadMarkers.iterator();
          while (localIterator.hasNext())
          {
            TypeAheadMarker localTypeAheadMarker = (TypeAheadMarker)localIterator.next();
            focusLog.finest("    {0}", new Object[] { localTypeAheadMarker });
          }
        }
      }
    }
  }
  
  private boolean typeAheadAssertions(Component paramComponent, AWTEvent paramAWTEvent)
  {
    pumpApprovedKeyEvents();
    Object localObject1;
    switch (paramAWTEvent.getID())
    {
    case 400: 
    case 401: 
    case 402: 
      KeyEvent localKeyEvent = (KeyEvent)paramAWTEvent;
      synchronized (this)
      {
        if ((isPosted) && (typeAheadMarkers.size() != 0))
        {
          localObject1 = (TypeAheadMarker)typeAheadMarkers.getFirst();
          if (localKeyEvent.getWhen() > after)
          {
            if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
              focusLog.finer("Storing event {0} because of marker {1}", new Object[] { localKeyEvent, localObject1 });
            }
            enqueuedKeyEvents.addLast(localKeyEvent);
            return true;
          }
        }
      }
      return preDispatchKeyEvent(localKeyEvent);
    case 1004: 
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("Markers before FOCUS_GAINED on {0}", new Object[] { paramComponent });
      }
      dumpMarkers();
      synchronized (this)
      {
        int i = 0;
        if (hasMarker(paramComponent))
        {
          localObject1 = typeAheadMarkers.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            if (nextuntilFocused == paramComponent) {
              i = 1;
            } else {
              if (i != 0) {
                break;
              }
            }
            ((Iterator)localObject1).remove();
          }
        }
        else if (focusLog.isLoggable(PlatformLogger.Level.FINER))
        {
          focusLog.finer("Event without marker {0}", new Object[] { paramAWTEvent });
        }
      }
      focusLog.finest("Markers after FOCUS_GAINED");
      dumpMarkers();
      redispatchEvent(paramComponent, paramAWTEvent);
      pumpApprovedKeyEvents();
      return true;
    }
    redispatchEvent(paramComponent, paramAWTEvent);
    return true;
  }
  
  private boolean hasMarker(Component paramComponent)
  {
    Iterator localIterator = typeAheadMarkers.iterator();
    while (localIterator.hasNext()) {
      if (nextuntilFocused == paramComponent) {
        return true;
      }
    }
    return false;
  }
  
  void clearMarkers()
  {
    synchronized (this)
    {
      typeAheadMarkers.clear();
    }
  }
  
  private boolean preDispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (isPosted)
    {
      localObject1 = getFocusOwner();
      paramKeyEvent.setSource(localObject1 != null ? localObject1 : getFocusedWindow());
    }
    if (paramKeyEvent.getSource() == null) {
      return true;
    }
    EventQueue.setCurrentEventAndMostRecentTime(paramKeyEvent);
    Object localObject2;
    if (KeyboardFocusManager.isProxyActive(paramKeyEvent))
    {
      localObject1 = (Component)paramKeyEvent.getSource();
      localObject2 = ((Component)localObject1).getNativeContainer();
      if (localObject2 != null)
      {
        ComponentPeer localComponentPeer = ((Container)localObject2).getPeer();
        if (localComponentPeer != null)
        {
          localComponentPeer.handleEvent(paramKeyEvent);
          paramKeyEvent.consume();
        }
      }
      return true;
    }
    Object localObject1 = getKeyEventDispatchers();
    if (localObject1 != null)
    {
      localObject2 = ((List)localObject1).iterator();
      while (((Iterator)localObject2).hasNext()) {
        if (((KeyEventDispatcher)((Iterator)localObject2).next()).dispatchKeyEvent(paramKeyEvent)) {
          return true;
        }
      }
    }
    return dispatchKeyEvent(paramKeyEvent);
  }
  
  private void consumeNextKeyTyped(KeyEvent paramKeyEvent)
  {
    consumeNextKeyTyped = true;
  }
  
  private void consumeTraversalKey(KeyEvent paramKeyEvent)
  {
    paramKeyEvent.consume();
    consumeNextKeyTyped = ((paramKeyEvent.getID() == 401) && (!paramKeyEvent.isActionKey()));
  }
  
  private boolean consumeProcessedKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getID() == 400) && (consumeNextKeyTyped))
    {
      paramKeyEvent.consume();
      consumeNextKeyTyped = false;
      return true;
    }
    return false;
  }
  
  public void processKeyEvent(Component paramComponent, KeyEvent paramKeyEvent)
  {
    if (consumeProcessedKeyEvent(paramKeyEvent)) {
      return;
    }
    if (paramKeyEvent.getID() == 400) {
      return;
    }
    if ((paramComponent.getFocusTraversalKeysEnabled()) && (!paramKeyEvent.isConsumed()))
    {
      AWTKeyStroke localAWTKeyStroke1 = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
      AWTKeyStroke localAWTKeyStroke2 = AWTKeyStroke.getAWTKeyStroke(localAWTKeyStroke1.getKeyCode(), localAWTKeyStroke1.getModifiers(), !localAWTKeyStroke1.isOnKeyRelease());
      Set localSet = paramComponent.getFocusTraversalKeys(0);
      boolean bool1 = localSet.contains(localAWTKeyStroke1);
      boolean bool2 = localSet.contains(localAWTKeyStroke2);
      if ((bool1) || (bool2))
      {
        consumeTraversalKey(paramKeyEvent);
        if (bool1) {
          focusNextComponent(paramComponent);
        }
        return;
      }
      if (paramKeyEvent.getID() == 401) {
        consumeNextKeyTyped = false;
      }
      localSet = paramComponent.getFocusTraversalKeys(1);
      bool1 = localSet.contains(localAWTKeyStroke1);
      bool2 = localSet.contains(localAWTKeyStroke2);
      if ((bool1) || (bool2))
      {
        consumeTraversalKey(paramKeyEvent);
        if (bool1) {
          focusPreviousComponent(paramComponent);
        }
        return;
      }
      localSet = paramComponent.getFocusTraversalKeys(2);
      bool1 = localSet.contains(localAWTKeyStroke1);
      bool2 = localSet.contains(localAWTKeyStroke2);
      if ((bool1) || (bool2))
      {
        consumeTraversalKey(paramKeyEvent);
        if (bool1) {
          upFocusCycle(paramComponent);
        }
        return;
      }
      if ((!(paramComponent instanceof Container)) || (!((Container)paramComponent).isFocusCycleRoot())) {
        return;
      }
      localSet = paramComponent.getFocusTraversalKeys(3);
      bool1 = localSet.contains(localAWTKeyStroke1);
      bool2 = localSet.contains(localAWTKeyStroke2);
      if ((bool1) || (bool2))
      {
        consumeTraversalKey(paramKeyEvent);
        if (bool1) {
          downFocusCycle((Container)paramComponent);
        }
      }
    }
  }
  
  protected synchronized void enqueueKeyEvents(long paramLong, Component paramComponent)
  {
    if (paramComponent == null) {
      return;
    }
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("Enqueue at {0} for {1}", new Object[] { Long.valueOf(paramLong), paramComponent });
    }
    int i = 0;
    int j = typeAheadMarkers.size();
    ListIterator localListIterator = typeAheadMarkers.listIterator(j);
    while (j > 0)
    {
      TypeAheadMarker localTypeAheadMarker = (TypeAheadMarker)localListIterator.previous();
      if (after <= paramLong)
      {
        i = j;
        break;
      }
      j--;
    }
    typeAheadMarkers.add(i, new TypeAheadMarker(paramLong, paramComponent));
  }
  
  protected synchronized void dequeueKeyEvents(long paramLong, Component paramComponent)
  {
    if (paramComponent == null) {
      return;
    }
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("Dequeue at {0} for {1}", new Object[] { Long.valueOf(paramLong), paramComponent });
    }
    ListIterator localListIterator = typeAheadMarkers.listIterator(paramLong >= 0L ? typeAheadMarkers.size() : 0);
    TypeAheadMarker localTypeAheadMarker;
    if (paramLong < 0L)
    {
      do
      {
        if (!localListIterator.hasNext()) {
          break;
        }
        localTypeAheadMarker = (TypeAheadMarker)localListIterator.next();
      } while (untilFocused != paramComponent);
      localListIterator.remove();
      return;
    }
    while (localListIterator.hasPrevious())
    {
      localTypeAheadMarker = (TypeAheadMarker)localListIterator.previous();
      if ((untilFocused == paramComponent) && (after == paramLong))
      {
        localListIterator.remove();
        return;
      }
    }
  }
  
  protected synchronized void discardKeyEvents(Component paramComponent)
  {
    if (paramComponent == null) {
      return;
    }
    long l = -1L;
    Iterator localIterator = typeAheadMarkers.iterator();
    while (localIterator.hasNext())
    {
      TypeAheadMarker localTypeAheadMarker = (TypeAheadMarker)localIterator.next();
      Object localObject = untilFocused;
      for (int i = localObject == paramComponent ? 1 : 0; (i == 0) && (localObject != null) && (!(localObject instanceof Window)); i = localObject == paramComponent ? 1 : 0) {
        localObject = ((Component)localObject).getParent();
      }
      if (i != 0)
      {
        if (l < 0L) {
          l = after;
        }
        localIterator.remove();
      }
      else if (l >= 0L)
      {
        purgeStampedEvents(l, after);
        l = -1L;
      }
    }
    purgeStampedEvents(l, -1L);
  }
  
  private void purgeStampedEvents(long paramLong1, long paramLong2)
  {
    if (paramLong1 < 0L) {
      return;
    }
    Iterator localIterator = enqueuedKeyEvents.iterator();
    while (localIterator.hasNext())
    {
      KeyEvent localKeyEvent = (KeyEvent)localIterator.next();
      long l = localKeyEvent.getWhen();
      if ((paramLong1 < l) && ((paramLong2 < 0L) || (l <= paramLong2))) {
        localIterator.remove();
      }
      if ((paramLong2 >= 0L) && (l > paramLong2)) {
        break;
      }
    }
  }
  
  public void focusPreviousComponent(Component paramComponent)
  {
    if (paramComponent != null) {
      paramComponent.transferFocusBackward();
    }
  }
  
  public void focusNextComponent(Component paramComponent)
  {
    if (paramComponent != null) {
      paramComponent.transferFocus();
    }
  }
  
  public void upFocusCycle(Component paramComponent)
  {
    if (paramComponent != null) {
      paramComponent.transferFocusUpCycle();
    }
  }
  
  public void downFocusCycle(Container paramContainer)
  {
    if ((paramContainer != null) && (paramContainer.isFocusCycleRoot())) {
      paramContainer.transferFocusDownCycle();
    }
  }
  
  static
  {
    AWTAccessor.setDefaultKeyboardFocusManagerAccessor(new AWTAccessor.DefaultKeyboardFocusManagerAccessor()
    {
      public void consumeNextKeyTyped(DefaultKeyboardFocusManager paramAnonymousDefaultKeyboardFocusManager, KeyEvent paramAnonymousKeyEvent)
      {
        paramAnonymousDefaultKeyboardFocusManager.consumeNextKeyTyped(paramAnonymousKeyEvent);
      }
    });
  }
  
  private static class DefaultKeyboardFocusManagerSentEvent
    extends SentEvent
  {
    private static final long serialVersionUID = -2924743257508701758L;
    
    public DefaultKeyboardFocusManagerSentEvent(AWTEvent paramAWTEvent, AppContext paramAppContext)
    {
      super(paramAppContext);
    }
    
    public final void dispatch()
    {
      KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      DefaultKeyboardFocusManager localDefaultKeyboardFocusManager = (localKeyboardFocusManager instanceof DefaultKeyboardFocusManager) ? (DefaultKeyboardFocusManager)localKeyboardFocusManager : null;
      if (localDefaultKeyboardFocusManager != null) {
        synchronized (localDefaultKeyboardFocusManager)
        {
          DefaultKeyboardFocusManager.access$108(localDefaultKeyboardFocusManager);
        }
      }
      super.dispatch();
      if (localDefaultKeyboardFocusManager != null) {
        synchronized (localDefaultKeyboardFocusManager)
        {
          DefaultKeyboardFocusManager.access$110(localDefaultKeyboardFocusManager);
        }
      }
    }
  }
  
  private static class TypeAheadMarker
  {
    long after;
    Component untilFocused;
    
    TypeAheadMarker(long paramLong, Component paramComponent)
    {
      after = paramLong;
      untilFocused = paramComponent;
    }
    
    public String toString()
    {
      return ">>> Marker after " + after + " on " + untilFocused;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\DefaultKeyboardFocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */