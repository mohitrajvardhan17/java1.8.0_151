package java.awt;

import java.awt.dnd.DropTarget;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.swing.JInternalFrame;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ContainerAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.java2d.pipe.Region;
import sun.security.action.GetBooleanAction;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class Container
  extends Component
{
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Container");
  private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.Container");
  private static final Component[] EMPTY_ARRAY = new Component[0];
  private List<Component> component = new ArrayList();
  LayoutManager layoutMgr;
  private LightweightDispatcher dispatcher;
  private transient FocusTraversalPolicy focusTraversalPolicy;
  private boolean focusCycleRoot = false;
  private boolean focusTraversalPolicyProvider;
  private transient Set<Thread> printingThreads;
  private transient boolean printing = false;
  transient ContainerListener containerListener;
  transient int listeningChildren;
  transient int listeningBoundsChildren;
  transient int descendantsCount;
  transient Color preserveBackgroundColor = null;
  private static final long serialVersionUID = 4613797578919906343L;
  static final boolean INCLUDE_SELF = true;
  static final boolean SEARCH_HEAVYWEIGHTS = true;
  private transient int numOfHWComponents = 0;
  private transient int numOfLWComponents = 0;
  private static final PlatformLogger mixingLog = PlatformLogger.getLogger("java.awt.mixing.Container");
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("ncomponents", Integer.TYPE), new ObjectStreamField("component", Component[].class), new ObjectStreamField("layoutMgr", LayoutManager.class), new ObjectStreamField("dispatcher", LightweightDispatcher.class), new ObjectStreamField("maxSize", Dimension.class), new ObjectStreamField("focusCycleRoot", Boolean.TYPE), new ObjectStreamField("containerSerializedDataVersion", Integer.TYPE), new ObjectStreamField("focusTraversalPolicyProvider", Boolean.TYPE) };
  private static final boolean isJavaAwtSmartInvalidate = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.awt.smartInvalidate"))).booleanValue();
  private static boolean descendUnconditionallyWhenValidating = false;
  transient Component modalComp;
  transient AppContext modalAppContext;
  private int containerSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public Container() {}
  
  void initializeFocusTraversalKeys()
  {
    focusTraversalKeys = new Set[4];
  }
  
  public int getComponentCount()
  {
    return countComponents();
  }
  
  @Deprecated
  public int countComponents()
  {
    return component.size();
  }
  
  public Component getComponent(int paramInt)
  {
    try
    {
      return (Component)component.get(paramInt);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new ArrayIndexOutOfBoundsException("No such child: " + paramInt);
    }
  }
  
  public Component[] getComponents()
  {
    return getComponents_NoClientCode();
  }
  
  final Component[] getComponents_NoClientCode()
  {
    return (Component[])component.toArray(EMPTY_ARRAY);
  }
  
  /* Error */
  Component[] getComponentsSync()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 1365	java/awt/Container:getTreeLock	()Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: invokevirtual 1322	java/awt/Container:getComponents	()[Ljava/awt/Component;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Container
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public Insets getInsets()
  {
    return insets();
  }
  
  @Deprecated
  public Insets insets()
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer instanceof ContainerPeer))
    {
      ContainerPeer localContainerPeer = (ContainerPeer)localComponentPeer;
      return (Insets)localContainerPeer.getInsets().clone();
    }
    return new Insets(0, 0, 0, 0);
  }
  
  public Component add(Component paramComponent)
  {
    addImpl(paramComponent, null, -1);
    return paramComponent;
  }
  
  public Component add(String paramString, Component paramComponent)
  {
    addImpl(paramComponent, paramString, -1);
    return paramComponent;
  }
  
  public Component add(Component paramComponent, int paramInt)
  {
    addImpl(paramComponent, null, paramInt);
    return paramComponent;
  }
  
  private void checkAddToSelf(Component paramComponent)
  {
    if ((paramComponent instanceof Container)) {
      for (Container localContainer = this; localContainer != null; localContainer = parent) {
        if (localContainer == paramComponent) {
          throw new IllegalArgumentException("adding container's parent to itself");
        }
      }
    }
  }
  
  private void checkNotAWindow(Component paramComponent)
  {
    if ((paramComponent instanceof Window)) {
      throw new IllegalArgumentException("adding a window to a container");
    }
  }
  
  private void checkAdding(Component paramComponent, int paramInt)
  {
    checkTreeLock();
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
    if ((paramInt > component.size()) || (paramInt < 0)) {
      throw new IllegalArgumentException("illegal component position");
    }
    if ((parent == this) && (paramInt == component.size())) {
      throw new IllegalArgumentException("illegal component position " + paramInt + " should be less then " + component.size());
    }
    checkAddToSelf(paramComponent);
    checkNotAWindow(paramComponent);
    Window localWindow1 = getContainingWindow();
    Window localWindow2 = paramComponent.getContainingWindow();
    if (localWindow1 != localWindow2) {
      throw new IllegalArgumentException("component and container should be in the same top-level window");
    }
    if (localGraphicsConfiguration != null) {
      paramComponent.checkGD(localGraphicsConfiguration.getDevice().getIDstring());
    }
  }
  
  private boolean removeDelicately(Component paramComponent, Container paramContainer, int paramInt)
  {
    checkTreeLock();
    int i = getComponentZOrder(paramComponent);
    boolean bool = isRemoveNotifyNeeded(paramComponent, this, paramContainer);
    if (bool) {
      paramComponent.removeNotify();
    }
    if (paramContainer != this)
    {
      if (layoutMgr != null) {
        layoutMgr.removeLayoutComponent(paramComponent);
      }
      adjustListeningChildren(32768L, -paramComponent.numListening(32768L));
      adjustListeningChildren(65536L, -paramComponent.numListening(65536L));
      adjustDescendants(-paramComponent.countHierarchyMembers());
      parent = null;
      if (bool) {
        paramComponent.setGraphicsConfiguration(null);
      }
      component.remove(i);
      invalidateIfValid();
    }
    else
    {
      component.remove(i);
      component.add(paramInt, paramComponent);
    }
    if (parent == null)
    {
      if ((containerListener != null) || ((eventMask & 0x2) != 0L) || (Toolkit.enabledOnToolkit(2L)))
      {
        ContainerEvent localContainerEvent = new ContainerEvent(this, 301, paramComponent);
        dispatchEvent(localContainerEvent);
      }
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if ((peer != null) && (layoutMgr == null) && (isVisible())) {
        updateCursorImmediately();
      }
    }
    return bool;
  }
  
  boolean canContainFocusOwner(Component paramComponent)
  {
    if ((!isEnabled()) || (!isDisplayable()) || (!isVisible()) || (!isFocusable())) {
      return false;
    }
    if (isFocusCycleRoot())
    {
      FocusTraversalPolicy localFocusTraversalPolicy = getFocusTraversalPolicy();
      if (((localFocusTraversalPolicy instanceof DefaultFocusTraversalPolicy)) && (!((DefaultFocusTraversalPolicy)localFocusTraversalPolicy).accept(paramComponent))) {
        return false;
      }
    }
    synchronized (getTreeLock())
    {
      if (parent != null) {
        return parent.canContainFocusOwner(paramComponent);
      }
    }
    return true;
  }
  
  final boolean hasHeavyweightDescendants()
  {
    checkTreeLock();
    return numOfHWComponents > 0;
  }
  
  final boolean hasLightweightDescendants()
  {
    checkTreeLock();
    return numOfLWComponents > 0;
  }
  
  Container getHeavyweightContainer()
  {
    checkTreeLock();
    if ((peer != null) && (!(peer instanceof LightweightPeer))) {
      return this;
    }
    return getNativeContainer();
  }
  
  private static boolean isRemoveNotifyNeeded(Component paramComponent, Container paramContainer1, Container paramContainer2)
  {
    if (paramContainer1 == null) {
      return false;
    }
    if (peer == null) {
      return false;
    }
    if (peer == null) {
      return true;
    }
    if (paramComponent.isLightweight())
    {
      boolean bool = paramComponent instanceof Container;
      if ((!bool) || ((bool) && (!((Container)paramComponent).hasHeavyweightDescendants()))) {
        return false;
      }
    }
    Container localContainer1 = paramContainer1.getHeavyweightContainer();
    Container localContainer2 = paramContainer2.getHeavyweightContainer();
    if (localContainer1 != localContainer2) {
      return !peer.isReparentSupported();
    }
    return false;
  }
  
  public void setComponentZOrder(Component paramComponent, int paramInt)
  {
    synchronized (getTreeLock())
    {
      Container localContainer = parent;
      int i = getComponentZOrder(paramComponent);
      if ((localContainer == this) && (paramInt == i)) {
        return;
      }
      checkAdding(paramComponent, paramInt);
      int j = localContainer != null ? localContainer.removeDelicately(paramComponent, this, paramInt) : 0;
      addDelicately(paramComponent, localContainer, paramInt);
      if ((j == 0) && (i != -1)) {
        paramComponent.mixOnZOrderChanging(i, paramInt);
      }
    }
  }
  
  private void reparentTraverse(ContainerPeer paramContainerPeer, Container paramContainer)
  {
    checkTreeLock();
    for (int i = 0; i < paramContainer.getComponentCount(); i++)
    {
      Component localComponent = paramContainer.getComponent(i);
      if (localComponent.isLightweight())
      {
        if ((localComponent instanceof Container)) {
          reparentTraverse(paramContainerPeer, (Container)localComponent);
        }
      }
      else {
        localComponent.getPeer().reparent(paramContainerPeer);
      }
    }
  }
  
  private void reparentChild(Component paramComponent)
  {
    checkTreeLock();
    if (paramComponent == null) {
      return;
    }
    if (paramComponent.isLightweight())
    {
      if ((paramComponent instanceof Container)) {
        reparentTraverse((ContainerPeer)getPeer(), (Container)paramComponent);
      }
    }
    else {
      paramComponent.getPeer().reparent((ContainerPeer)getPeer());
    }
  }
  
  private void addDelicately(Component paramComponent, Container paramContainer, int paramInt)
  {
    checkTreeLock();
    if (paramContainer != this)
    {
      if (paramInt == -1) {
        component.add(paramComponent);
      } else {
        component.add(paramInt, paramComponent);
      }
      parent = this;
      paramComponent.setGraphicsConfiguration(getGraphicsConfiguration());
      adjustListeningChildren(32768L, paramComponent.numListening(32768L));
      adjustListeningChildren(65536L, paramComponent.numListening(65536L));
      adjustDescendants(paramComponent.countHierarchyMembers());
    }
    else if (paramInt < component.size())
    {
      component.set(paramInt, paramComponent);
    }
    invalidateIfValid();
    Object localObject;
    if (peer != null) {
      if (peer == null)
      {
        paramComponent.addNotify();
      }
      else
      {
        localObject = getHeavyweightContainer();
        Container localContainer = paramContainer.getHeavyweightContainer();
        if (localContainer != localObject) {
          ((Container)localObject).reparentChild(paramComponent);
        }
        paramComponent.updateZOrder();
        if ((!paramComponent.isLightweight()) && (isLightweight())) {
          paramComponent.relocateComponent();
        }
      }
    }
    if (paramContainer != this)
    {
      if (layoutMgr != null) {
        if ((layoutMgr instanceof LayoutManager2)) {
          ((LayoutManager2)layoutMgr).addLayoutComponent(paramComponent, null);
        } else {
          layoutMgr.addLayoutComponent(null, paramComponent);
        }
      }
      if ((containerListener != null) || ((eventMask & 0x2) != 0L) || (Toolkit.enabledOnToolkit(2L)))
      {
        localObject = new ContainerEvent(this, 300, paramComponent);
        dispatchEvent((AWTEvent)localObject);
      }
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if ((paramComponent.isFocusOwner()) && (!paramComponent.canBeFocusOwnerRecursively()))
      {
        paramComponent.transferFocus();
      }
      else if ((paramComponent instanceof Container))
      {
        localObject = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if ((localObject != null) && (isParentOf((Component)localObject)) && (!((Component)localObject).canBeFocusOwnerRecursively())) {
          ((Component)localObject).transferFocus();
        }
      }
    }
    else
    {
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1400L, Toolkit.enabledOnToolkit(32768L));
    }
    if ((peer != null) && (layoutMgr == null) && (isVisible())) {
      updateCursorImmediately();
    }
  }
  
  public int getComponentZOrder(Component paramComponent)
  {
    if (paramComponent == null) {
      return -1;
    }
    synchronized (getTreeLock())
    {
      if (parent != this) {
        return -1;
      }
      return component.indexOf(paramComponent);
    }
  }
  
  public void add(Component paramComponent, Object paramObject)
  {
    addImpl(paramComponent, paramObject, -1);
  }
  
  public void add(Component paramComponent, Object paramObject, int paramInt)
  {
    addImpl(paramComponent, paramObject, paramInt);
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    synchronized (getTreeLock())
    {
      GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
      if ((paramInt > component.size()) || ((paramInt < 0) && (paramInt != -1))) {
        throw new IllegalArgumentException("illegal component position");
      }
      checkAddToSelf(paramComponent);
      checkNotAWindow(paramComponent);
      if (localGraphicsConfiguration != null) {
        paramComponent.checkGD(localGraphicsConfiguration.getDevice().getIDstring());
      }
      if (parent != null)
      {
        parent.remove(paramComponent);
        if (paramInt > component.size()) {
          throw new IllegalArgumentException("illegal component position");
        }
      }
      if (paramInt == -1) {
        component.add(paramComponent);
      } else {
        component.add(paramInt, paramComponent);
      }
      parent = this;
      paramComponent.setGraphicsConfiguration(localGraphicsConfiguration);
      adjustListeningChildren(32768L, paramComponent.numListening(32768L));
      adjustListeningChildren(65536L, paramComponent.numListening(65536L));
      adjustDescendants(paramComponent.countHierarchyMembers());
      invalidateIfValid();
      if (peer != null) {
        paramComponent.addNotify();
      }
      if (layoutMgr != null) {
        if ((layoutMgr instanceof LayoutManager2)) {
          ((LayoutManager2)layoutMgr).addLayoutComponent(paramComponent, paramObject);
        } else if ((paramObject instanceof String)) {
          layoutMgr.addLayoutComponent((String)paramObject, paramComponent);
        }
      }
      if ((containerListener != null) || ((eventMask & 0x2) != 0L) || (Toolkit.enabledOnToolkit(2L)))
      {
        ContainerEvent localContainerEvent = new ContainerEvent(this, 300, paramComponent);
        dispatchEvent(localContainerEvent);
      }
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if ((peer != null) && (layoutMgr == null) && (isVisible())) {
        updateCursorImmediately();
      }
    }
  }
  
  boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration)
  {
    checkTreeLock();
    boolean bool = super.updateGraphicsData(paramGraphicsConfiguration);
    Iterator localIterator = component.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      if (localComponent != null) {
        bool |= localComponent.updateGraphicsData(paramGraphicsConfiguration);
      }
    }
    return bool;
  }
  
  void checkGD(String paramString)
  {
    Iterator localIterator = component.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      if (localComponent != null) {
        localComponent.checkGD(paramString);
      }
    }
  }
  
  public void remove(int paramInt)
  {
    synchronized (getTreeLock())
    {
      if ((paramInt < 0) || (paramInt >= component.size())) {
        throw new ArrayIndexOutOfBoundsException(paramInt);
      }
      Component localComponent = (Component)component.get(paramInt);
      if (peer != null) {
        localComponent.removeNotify();
      }
      if (layoutMgr != null) {
        layoutMgr.removeLayoutComponent(localComponent);
      }
      adjustListeningChildren(32768L, -localComponent.numListening(32768L));
      adjustListeningChildren(65536L, -localComponent.numListening(65536L));
      adjustDescendants(-localComponent.countHierarchyMembers());
      parent = null;
      component.remove(paramInt);
      localComponent.setGraphicsConfiguration(null);
      invalidateIfValid();
      if ((containerListener != null) || ((eventMask & 0x2) != 0L) || (Toolkit.enabledOnToolkit(2L)))
      {
        ContainerEvent localContainerEvent = new ContainerEvent(this, 301, localComponent);
        dispatchEvent(localContainerEvent);
      }
      localComponent.createHierarchyEvents(1400, localComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if ((peer != null) && (layoutMgr == null) && (isVisible())) {
        updateCursorImmediately();
      }
    }
  }
  
  public void remove(Component paramComponent)
  {
    synchronized (getTreeLock())
    {
      if (parent == this)
      {
        int i = component.indexOf(paramComponent);
        if (i >= 0) {
          remove(i);
        }
      }
    }
  }
  
  public void removeAll()
  {
    synchronized (getTreeLock())
    {
      adjustListeningChildren(32768L, -listeningChildren);
      adjustListeningChildren(65536L, -listeningBoundsChildren);
      adjustDescendants(-descendantsCount);
      while (!component.isEmpty())
      {
        Component localComponent = (Component)component.remove(component.size() - 1);
        if (peer != null) {
          localComponent.removeNotify();
        }
        if (layoutMgr != null) {
          layoutMgr.removeLayoutComponent(localComponent);
        }
        parent = null;
        localComponent.setGraphicsConfiguration(null);
        if ((containerListener != null) || ((eventMask & 0x2) != 0L) || (Toolkit.enabledOnToolkit(2L)))
        {
          ContainerEvent localContainerEvent = new ContainerEvent(this, 301, localComponent);
          dispatchEvent(localContainerEvent);
        }
        localComponent.createHierarchyEvents(1400, localComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      }
      if ((peer != null) && (layoutMgr == null) && (isVisible())) {
        updateCursorImmediately();
      }
      invalidateIfValid();
    }
  }
  
  int numListening(long paramLong)
  {
    int i = super.numListening(paramLong);
    int j;
    Iterator localIterator;
    Component localComponent;
    if (paramLong == 32768L)
    {
      if (eventLog.isLoggable(PlatformLogger.Level.FINE))
      {
        j = 0;
        localIterator = component.iterator();
        while (localIterator.hasNext())
        {
          localComponent = (Component)localIterator.next();
          j += localComponent.numListening(paramLong);
        }
        if (listeningChildren != j) {
          eventLog.fine("Assertion (listeningChildren == sum) failed");
        }
      }
      return listeningChildren + i;
    }
    if (paramLong == 65536L)
    {
      if (eventLog.isLoggable(PlatformLogger.Level.FINE))
      {
        j = 0;
        localIterator = component.iterator();
        while (localIterator.hasNext())
        {
          localComponent = (Component)localIterator.next();
          j += localComponent.numListening(paramLong);
        }
        if (listeningBoundsChildren != j) {
          eventLog.fine("Assertion (listeningBoundsChildren == sum) failed");
        }
      }
      return listeningBoundsChildren + i;
    }
    if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
      eventLog.fine("This code must never be reached");
    }
    return i;
  }
  
  void adjustListeningChildren(long paramLong, int paramInt)
  {
    if (eventLog.isLoggable(PlatformLogger.Level.FINE))
    {
      int i = (paramLong == 32768L) || (paramLong == 65536L) || (paramLong == 98304L) ? 1 : 0;
      if (i == 0) {
        eventLog.fine("Assertion failed");
      }
    }
    if (paramInt == 0) {
      return;
    }
    if ((paramLong & 0x8000) != 0L) {
      listeningChildren += paramInt;
    }
    if ((paramLong & 0x10000) != 0L) {
      listeningBoundsChildren += paramInt;
    }
    adjustListeningChildrenOnParent(paramLong, paramInt);
  }
  
  void adjustDescendants(int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    descendantsCount += paramInt;
    adjustDecendantsOnParent(paramInt);
  }
  
  void adjustDecendantsOnParent(int paramInt)
  {
    if (parent != null) {
      parent.adjustDescendants(paramInt);
    }
  }
  
  int countHierarchyMembers()
  {
    if (log.isLoggable(PlatformLogger.Level.FINE))
    {
      int i = 0;
      Iterator localIterator = component.iterator();
      while (localIterator.hasNext())
      {
        Component localComponent = (Component)localIterator.next();
        i += localComponent.countHierarchyMembers();
      }
      if (descendantsCount != i) {
        log.fine("Assertion (descendantsCount == sum) failed");
      }
    }
    return descendantsCount + 1;
  }
  
  private int getListenersCount(int paramInt, boolean paramBoolean)
  {
    checkTreeLock();
    if (paramBoolean) {
      return descendantsCount;
    }
    switch (paramInt)
    {
    case 1400: 
      return listeningChildren;
    case 1401: 
    case 1402: 
      return listeningBoundsChildren;
    }
    return 0;
  }
  
  final int createHierarchyEvents(int paramInt, Component paramComponent, Container paramContainer, long paramLong, boolean paramBoolean)
  {
    checkTreeLock();
    int i = getListenersCount(paramInt, paramBoolean);
    int j = i;
    for (int k = 0; j > 0; k++) {
      j -= ((Component)component.get(k)).createHierarchyEvents(paramInt, paramComponent, paramContainer, paramLong, paramBoolean);
    }
    return i + super.createHierarchyEvents(paramInt, paramComponent, paramContainer, paramLong, paramBoolean);
  }
  
  final void createChildHierarchyEvents(int paramInt, long paramLong, boolean paramBoolean)
  {
    checkTreeLock();
    if (component.isEmpty()) {
      return;
    }
    int i = getListenersCount(paramInt, paramBoolean);
    int j = i;
    for (int k = 0; j > 0; k++) {
      j -= ((Component)component.get(k)).createHierarchyEvents(paramInt, this, parent, paramLong, paramBoolean);
    }
  }
  
  public LayoutManager getLayout()
  {
    return layoutMgr;
  }
  
  public void setLayout(LayoutManager paramLayoutManager)
  {
    layoutMgr = paramLayoutManager;
    invalidateIfValid();
  }
  
  public void doLayout()
  {
    layout();
  }
  
  @Deprecated
  public void layout()
  {
    LayoutManager localLayoutManager = layoutMgr;
    if (localLayoutManager != null) {
      localLayoutManager.layoutContainer(this);
    }
  }
  
  public boolean isValidateRoot()
  {
    return false;
  }
  
  void invalidateParent()
  {
    if ((!isJavaAwtSmartInvalidate) || (!isValidateRoot())) {
      super.invalidateParent();
    }
  }
  
  public void invalidate()
  {
    LayoutManager localLayoutManager = layoutMgr;
    if ((localLayoutManager instanceof LayoutManager2))
    {
      LayoutManager2 localLayoutManager2 = (LayoutManager2)localLayoutManager;
      localLayoutManager2.invalidateLayout(this);
    }
    super.invalidate();
  }
  
  public void validate()
  {
    boolean bool = false;
    synchronized (getTreeLock())
    {
      if (((!isValid()) || (descendUnconditionallyWhenValidating)) && (peer != null))
      {
        ContainerPeer localContainerPeer = null;
        if ((peer instanceof ContainerPeer)) {
          localContainerPeer = (ContainerPeer)peer;
        }
        if (localContainerPeer != null) {
          localContainerPeer.beginValidate();
        }
        validateTree();
        if (localContainerPeer != null)
        {
          localContainerPeer.endValidate();
          if (!descendUnconditionallyWhenValidating) {
            bool = isVisible();
          }
        }
      }
    }
    if (bool) {
      updateCursorImmediately();
    }
  }
  
  final void validateUnconditionally()
  {
    boolean bool = false;
    synchronized (getTreeLock())
    {
      descendUnconditionallyWhenValidating = true;
      validate();
      if ((peer instanceof ContainerPeer)) {
        bool = isVisible();
      }
      descendUnconditionallyWhenValidating = false;
    }
    if (bool) {
      updateCursorImmediately();
    }
  }
  
  protected void validateTree()
  {
    checkTreeLock();
    if ((!isValid()) || (descendUnconditionallyWhenValidating))
    {
      if ((peer instanceof ContainerPeer)) {
        ((ContainerPeer)peer).beginLayout();
      }
      if (!isValid()) {
        doLayout();
      }
      for (int i = 0; i < component.size(); i++)
      {
        Component localComponent = (Component)component.get(i);
        if (((localComponent instanceof Container)) && (!(localComponent instanceof Window)) && ((!localComponent.isValid()) || (descendUnconditionallyWhenValidating))) {
          ((Container)localComponent).validateTree();
        } else {
          localComponent.validate();
        }
      }
      if ((peer instanceof ContainerPeer)) {
        ((ContainerPeer)peer).endLayout();
      }
    }
    super.validate();
  }
  
  void invalidateTree()
  {
    synchronized (getTreeLock())
    {
      for (int i = 0; i < component.size(); i++)
      {
        Component localComponent = (Component)component.get(i);
        if ((localComponent instanceof Container)) {
          ((Container)localComponent).invalidateTree();
        } else {
          localComponent.invalidateIfValid();
        }
      }
      invalidateIfValid();
    }
  }
  
  public void setFont(Font paramFont)
  {
    int i = 0;
    Font localFont1 = getFont();
    super.setFont(paramFont);
    Font localFont2 = getFont();
    if ((localFont2 != localFont1) && ((localFont1 == null) || (!localFont1.equals(localFont2)))) {
      invalidateTree();
    }
  }
  
  public Dimension getPreferredSize()
  {
    return preferredSize();
  }
  
  @Deprecated
  public Dimension preferredSize()
  {
    Dimension localDimension = prefSize;
    if ((localDimension == null) || ((!isPreferredSizeSet()) && (!isValid()))) {
      synchronized (getTreeLock())
      {
        prefSize = (layoutMgr != null ? layoutMgr.preferredLayoutSize(this) : super.preferredSize());
        localDimension = prefSize;
      }
    }
    if (localDimension != null) {
      return new Dimension(localDimension);
    }
    return localDimension;
  }
  
  public Dimension getMinimumSize()
  {
    return minimumSize();
  }
  
  @Deprecated
  public Dimension minimumSize()
  {
    Dimension localDimension = minSize;
    if ((localDimension == null) || ((!isMinimumSizeSet()) && (!isValid()))) {
      synchronized (getTreeLock())
      {
        minSize = (layoutMgr != null ? layoutMgr.minimumLayoutSize(this) : super.minimumSize());
        localDimension = minSize;
      }
    }
    if (localDimension != null) {
      return new Dimension(localDimension);
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize()
  {
    Dimension localDimension = maxSize;
    if ((localDimension == null) || ((!isMaximumSizeSet()) && (!isValid()))) {
      synchronized (getTreeLock())
      {
        if ((layoutMgr instanceof LayoutManager2))
        {
          LayoutManager2 localLayoutManager2 = (LayoutManager2)layoutMgr;
          maxSize = localLayoutManager2.maximumLayoutSize(this);
        }
        else
        {
          maxSize = super.getMaximumSize();
        }
        localDimension = maxSize;
      }
    }
    if (localDimension != null) {
      return new Dimension(localDimension);
    }
    return localDimension;
  }
  
  public float getAlignmentX()
  {
    float f;
    if ((layoutMgr instanceof LayoutManager2)) {
      synchronized (getTreeLock())
      {
        LayoutManager2 localLayoutManager2 = (LayoutManager2)layoutMgr;
        f = localLayoutManager2.getLayoutAlignmentX(this);
      }
    } else {
      f = super.getAlignmentX();
    }
    return f;
  }
  
  public float getAlignmentY()
  {
    float f;
    if ((layoutMgr instanceof LayoutManager2)) {
      synchronized (getTreeLock())
      {
        LayoutManager2 localLayoutManager2 = (LayoutManager2)layoutMgr;
        f = localLayoutManager2.getLayoutAlignmentY(this);
      }
    } else {
      f = super.getAlignmentY();
    }
    return f;
  }
  
  public void paint(Graphics paramGraphics)
  {
    if (isShowing())
    {
      synchronized (getObjectLock())
      {
        if ((printing) && (printingThreads.contains(Thread.currentThread()))) {
          return;
        }
      }
      GraphicsCallback.PaintCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 2);
    }
  }
  
  public void update(Graphics paramGraphics)
  {
    if (isShowing())
    {
      if (!(peer instanceof LightweightPeer)) {
        paramGraphics.clearRect(0, 0, width, height);
      }
      paint(paramGraphics);
    }
  }
  
  public void print(Graphics paramGraphics)
  {
    if (isShowing())
    {
      Thread localThread = Thread.currentThread();
      try
      {
        synchronized (getObjectLock())
        {
          if (printingThreads == null) {
            printingThreads = new HashSet();
          }
          printingThreads.add(localThread);
          printing = true;
        }
        super.print(paramGraphics);
      }
      finally
      {
        synchronized (getObjectLock())
        {
          printingThreads.remove(localThread);
          printing = (!printingThreads.isEmpty());
        }
      }
      GraphicsCallback.PrintCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 2);
    }
  }
  
  public void paintComponents(Graphics paramGraphics)
  {
    if (isShowing()) {
      GraphicsCallback.PaintAllCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 4);
    }
  }
  
  void lightweightPaint(Graphics paramGraphics)
  {
    super.lightweightPaint(paramGraphics);
    paintHeavyweightComponents(paramGraphics);
  }
  
  void paintHeavyweightComponents(Graphics paramGraphics)
  {
    if (isShowing()) {
      GraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 3);
    }
  }
  
  public void printComponents(Graphics paramGraphics)
  {
    if (isShowing()) {
      GraphicsCallback.PrintAllCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 4);
    }
  }
  
  void lightweightPrint(Graphics paramGraphics)
  {
    super.lightweightPrint(paramGraphics);
    printHeavyweightComponents(paramGraphics);
  }
  
  void printHeavyweightComponents(Graphics paramGraphics)
  {
    if (isShowing()) {
      GraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 3);
    }
  }
  
  public synchronized void addContainerListener(ContainerListener paramContainerListener)
  {
    if (paramContainerListener == null) {
      return;
    }
    containerListener = AWTEventMulticaster.add(containerListener, paramContainerListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeContainerListener(ContainerListener paramContainerListener)
  {
    if (paramContainerListener == null) {
      return;
    }
    containerListener = AWTEventMulticaster.remove(containerListener, paramContainerListener);
  }
  
  public synchronized ContainerListener[] getContainerListeners()
  {
    return (ContainerListener[])getListeners(ContainerListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    ContainerListener localContainerListener = null;
    if (paramClass == ContainerListener.class) {
      localContainerListener = containerListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners(localContainerListener, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    int i = paramAWTEvent.getID();
    if ((i == 300) || (i == 301)) {
      return ((eventMask & 0x2) != 0L) || (containerListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof ContainerEvent))
    {
      processContainerEvent((ContainerEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processContainerEvent(ContainerEvent paramContainerEvent)
  {
    ContainerListener localContainerListener = containerListener;
    if (localContainerListener != null) {
      switch (paramContainerEvent.getID())
      {
      case 300: 
        localContainerListener.componentAdded(paramContainerEvent);
        break;
      case 301: 
        localContainerListener.componentRemoved(paramContainerEvent);
      }
    }
  }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent)
  {
    if ((dispatcher != null) && (dispatcher.dispatchEvent(paramAWTEvent)))
    {
      paramAWTEvent.consume();
      if (peer != null) {
        peer.handleEvent(paramAWTEvent);
      }
      return;
    }
    super.dispatchEventImpl(paramAWTEvent);
    synchronized (getTreeLock())
    {
      switch (paramAWTEvent.getID())
      {
      case 101: 
        createChildHierarchyEvents(1402, 0L, Toolkit.enabledOnToolkit(65536L));
        break;
      case 100: 
        createChildHierarchyEvents(1401, 0L, Toolkit.enabledOnToolkit(65536L));
      }
    }
  }
  
  void dispatchEventToSelf(AWTEvent paramAWTEvent)
  {
    super.dispatchEventImpl(paramAWTEvent);
  }
  
  Component getMouseEventTarget(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return getMouseEventTarget(paramInt1, paramInt2, paramBoolean, MouseEventTargetFilter.FILTER, false);
  }
  
  Component getDropTargetEventTarget(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return getMouseEventTarget(paramInt1, paramInt2, paramBoolean, DropTargetEventTargetFilter.FILTER, true);
  }
  
  private Component getMouseEventTarget(int paramInt1, int paramInt2, boolean paramBoolean1, EventTargetFilter paramEventTargetFilter, boolean paramBoolean2)
  {
    Component localComponent = null;
    if (paramBoolean2) {
      localComponent = getMouseEventTargetImpl(paramInt1, paramInt2, paramBoolean1, paramEventTargetFilter, true, paramBoolean2);
    }
    if ((localComponent == null) || (localComponent == this)) {
      localComponent = getMouseEventTargetImpl(paramInt1, paramInt2, paramBoolean1, paramEventTargetFilter, false, paramBoolean2);
    }
    return localComponent;
  }
  
  private Component getMouseEventTargetImpl(int paramInt1, int paramInt2, boolean paramBoolean1, EventTargetFilter paramEventTargetFilter, boolean paramBoolean2, boolean paramBoolean3)
  {
    synchronized (getTreeLock())
    {
      for (int i = 0; i < component.size(); i++)
      {
        Component localComponent1 = (Component)component.get(i);
        if ((localComponent1 != null) && (visible) && (((!paramBoolean2) && ((peer instanceof LightweightPeer))) || ((paramBoolean2) && (!(peer instanceof LightweightPeer)) && (localComponent1.contains(paramInt1 - x, paramInt2 - y))))) {
          if ((localComponent1 instanceof Container))
          {
            Container localContainer = (Container)localComponent1;
            Component localComponent2 = localContainer.getMouseEventTarget(paramInt1 - x, paramInt2 - y, paramBoolean1, paramEventTargetFilter, paramBoolean3);
            if (localComponent2 != null) {
              return localComponent2;
            }
          }
          else if (paramEventTargetFilter.accept(localComponent1))
          {
            return localComponent1;
          }
        }
      }
      i = ((peer instanceof LightweightPeer)) || (paramBoolean1) ? 1 : 0;
      boolean bool = contains(paramInt1, paramInt2);
      if ((bool) && (i != 0) && (paramEventTargetFilter.accept(this))) {
        return this;
      }
      return null;
    }
  }
  
  void proxyEnableEvents(long paramLong)
  {
    if ((peer instanceof LightweightPeer))
    {
      if (parent != null) {
        parent.proxyEnableEvents(paramLong);
      }
    }
    else if (dispatcher != null) {
      dispatcher.enableEvents(paramLong);
    }
  }
  
  @Deprecated
  public void deliverEvent(Event paramEvent)
  {
    Component localComponent = getComponentAt(x, y);
    if ((localComponent != null) && (localComponent != this))
    {
      paramEvent.translate(-x, -y);
      localComponent.deliverEvent(paramEvent);
    }
    else
    {
      postEvent(paramEvent);
    }
  }
  
  public Component getComponentAt(int paramInt1, int paramInt2)
  {
    return locate(paramInt1, paramInt2);
  }
  
  @Deprecated
  public Component locate(int paramInt1, int paramInt2)
  {
    if (!contains(paramInt1, paramInt2)) {
      return null;
    }
    Object localObject1 = null;
    synchronized (getTreeLock())
    {
      Iterator localIterator = component.iterator();
      while (localIterator.hasNext())
      {
        Component localComponent = (Component)localIterator.next();
        if (localComponent.contains(paramInt1 - x, paramInt2 - y))
        {
          if (!localComponent.isLightweight()) {
            return localComponent;
          }
          if (localObject1 == null) {
            localObject1 = localComponent;
          }
        }
      }
    }
    return (Component)(localObject1 != null ? localObject1 : this);
  }
  
  public Component getComponentAt(Point paramPoint)
  {
    return getComponentAt(x, y);
  }
  
  public Point getMousePosition(boolean paramBoolean)
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    PointerInfo localPointerInfo = (PointerInfo)AccessController.doPrivileged(new PrivilegedAction()
    {
      public PointerInfo run()
      {
        return MouseInfo.getPointerInfo();
      }
    });
    synchronized (getTreeLock())
    {
      Component localComponent = findUnderMouseInWindow(localPointerInfo);
      if (isSameOrAncestorOf(localComponent, paramBoolean)) {
        return pointRelativeToComponent(localPointerInfo.getLocation());
      }
      return null;
    }
  }
  
  boolean isSameOrAncestorOf(Component paramComponent, boolean paramBoolean)
  {
    return (this == paramComponent) || ((paramBoolean) && (isParentOf(paramComponent)));
  }
  
  public Component findComponentAt(int paramInt1, int paramInt2)
  {
    return findComponentAt(paramInt1, paramInt2, true);
  }
  
  final Component findComponentAt(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if (isRecursivelyVisible()) {
        return findComponentAtImpl(paramInt1, paramInt2, paramBoolean);
      }
    }
    return null;
  }
  
  final Component findComponentAtImpl(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((!contains(paramInt1, paramInt2)) || (!visible) || ((!paramBoolean) && (!enabled))) {
      return null;
    }
    Component localComponent1 = null;
    Iterator localIterator = component.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent2 = (Component)localIterator.next();
      int i = paramInt1 - x;
      int j = paramInt2 - y;
      if (localComponent2.contains(i, j)) {
        if (!localComponent2.isLightweight())
        {
          Component localComponent3 = getChildAt(localComponent2, i, j, paramBoolean);
          if (localComponent3 != null) {
            return localComponent3;
          }
        }
        else if (localComponent1 == null)
        {
          localComponent1 = getChildAt(localComponent2, i, j, paramBoolean);
        }
      }
    }
    return localComponent1 != null ? localComponent1 : this;
  }
  
  private static Component getChildAt(Component paramComponent, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((paramComponent instanceof Container)) {
      paramComponent = ((Container)paramComponent).findComponentAtImpl(paramInt1, paramInt2, paramBoolean);
    } else {
      paramComponent = paramComponent.getComponentAt(paramInt1, paramInt2);
    }
    if ((paramComponent != null) && (visible) && ((paramBoolean) || (enabled))) {
      return paramComponent;
    }
    return null;
  }
  
  public Component findComponentAt(Point paramPoint)
  {
    return findComponentAt(x, y);
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      super.addNotify();
      if (!(peer instanceof LightweightPeer)) {
        dispatcher = new LightweightDispatcher(this);
      }
      for (int i = 0; i < component.size(); i++) {
        ((Component)component.get(i)).addNotify();
      }
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      for (int i = component.size() - 1; i >= 0; i--)
      {
        Component localComponent = (Component)component.get(i);
        if (localComponent != null)
        {
          localComponent.setAutoFocusTransferOnDisposal(false);
          localComponent.removeNotify();
          localComponent.setAutoFocusTransferOnDisposal(true);
        }
      }
      if ((containsFocus()) && (KeyboardFocusManager.isAutoFocusTransferEnabledFor(this)) && (!transferFocus(false))) {
        transferFocusBackward(true);
      }
      if (dispatcher != null)
      {
        dispatcher.dispose();
        dispatcher = null;
      }
      super.removeNotify();
    }
  }
  
  public boolean isAncestorOf(Component paramComponent)
  {
    Container localContainer;
    if ((paramComponent == null) || ((localContainer = paramComponent.getParent()) == null)) {
      return false;
    }
    while (localContainer != null)
    {
      if (localContainer == this) {
        return true;
      }
      localContainer = localContainer.getParent();
    }
    return false;
  }
  
  private void startLWModal()
  {
    modalAppContext = AppContext.getAppContext();
    long l = Toolkit.getEventQueue().getMostRecentKeyEventTime();
    Component localComponent = Component.isInstanceOf(this, "javax.swing.JInternalFrame") ? ((JInternalFrame)this).getMostRecentFocusOwner() : null;
    if (localComponent != null) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(l, localComponent);
    }
    final Container localContainer;
    synchronized (getTreeLock())
    {
      localContainer = getHeavyweightContainer();
      if (modalComp != null)
      {
        modalComp = modalComp;
        modalComp = this;
        return;
      }
      modalComp = this;
    }
    ??? = new Runnable()
    {
      public void run()
      {
        EventDispatchThread localEventDispatchThread = (EventDispatchThread)Thread.currentThread();
        localEventDispatchThread.pumpEventsForHierarchy(new Conditional()
        {
          public boolean evaluate()
          {
            return (windowClosingException == null) && (val$nativeContainer.modalComp != null);
          }
        }, Container.this);
      }
    };
    if (EventQueue.isDispatchThread())
    {
      SequencedEvent localSequencedEvent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
      if (localSequencedEvent != null) {
        localSequencedEvent.dispose();
      }
      ((Runnable)???).run();
    }
    else
    {
      synchronized (getTreeLock())
      {
        Toolkit.getEventQueue().postEvent(new PeerEvent(this, (Runnable)???, 1L));
        for (;;)
        {
          if ((windowClosingException == null) && (modalComp != null)) {
            try
            {
              getTreeLock().wait();
            }
            catch (InterruptedException localInterruptedException) {}
          }
        }
      }
    }
    if (windowClosingException != null)
    {
      windowClosingException.fillInStackTrace();
      throw windowClosingException;
    }
    if (localComponent != null) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(l, localComponent);
    }
  }
  
  private void stopLWModal()
  {
    synchronized (getTreeLock())
    {
      if (modalAppContext != null)
      {
        Container localContainer = getHeavyweightContainer();
        if (localContainer != null)
        {
          if (modalComp != null)
          {
            modalComp = modalComp;
            modalComp = null;
            return;
          }
          modalComp = null;
        }
        SunToolkit.postEvent(modalAppContext, new PeerEvent(this, new WakingRunnable(), 1L));
      }
      EventQueue.invokeLater(new WakingRunnable());
      getTreeLock().notifyAll();
    }
  }
  
  protected String paramString()
  {
    String str = super.paramString();
    LayoutManager localLayoutManager = layoutMgr;
    if (localLayoutManager != null) {
      str = str + ",layout=" + localLayoutManager.getClass().getName();
    }
    return str;
  }
  
  public void list(PrintStream paramPrintStream, int paramInt)
  {
    super.list(paramPrintStream, paramInt);
    synchronized (getTreeLock())
    {
      for (int i = 0; i < component.size(); i++)
      {
        Component localComponent = (Component)component.get(i);
        if (localComponent != null) {
          localComponent.list(paramPrintStream, paramInt + 1);
        }
      }
    }
  }
  
  public void list(PrintWriter paramPrintWriter, int paramInt)
  {
    super.list(paramPrintWriter, paramInt);
    synchronized (getTreeLock())
    {
      for (int i = 0; i < component.size(); i++)
      {
        Component localComponent = (Component)component.get(i);
        if (localComponent != null) {
          localComponent.list(paramPrintWriter, paramInt + 1);
        }
      }
    }
  }
  
  public void setFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    setFocusTraversalKeys_NoIDCheck(paramInt, paramSet);
  }
  
  public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    return getFocusTraversalKeys_NoIDCheck(paramInt);
  }
  
  public boolean areFocusTraversalKeysSet(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    return (focusTraversalKeys != null) && (focusTraversalKeys[paramInt] != null);
  }
  
  public boolean isFocusCycleRoot(Container paramContainer)
  {
    if ((isFocusCycleRoot()) && (paramContainer == this)) {
      return true;
    }
    return super.isFocusCycleRoot(paramContainer);
  }
  
  private Container findTraversalRoot()
  {
    Container localContainer1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
    Container localContainer2;
    if (localContainer1 == this)
    {
      localContainer2 = this;
    }
    else
    {
      localContainer2 = getFocusCycleRootAncestor();
      if (localContainer2 == null) {
        localContainer2 = this;
      }
    }
    if (localContainer2 != localContainer1) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(localContainer2);
    }
    return localContainer2;
  }
  
  final boolean containsFocus()
  {
    Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    return isParentOf(localComponent);
  }
  
  private boolean isParentOf(Component paramComponent)
  {
    synchronized (getTreeLock())
    {
      while ((paramComponent != null) && (paramComponent != this) && (!(paramComponent instanceof Window))) {
        paramComponent = paramComponent.getParent();
      }
      return paramComponent == this;
    }
  }
  
  void clearMostRecentFocusOwnerOnHide()
  {
    int i = 0;
    Window localWindow = null;
    synchronized (getTreeLock())
    {
      localWindow = getContainingWindow();
      if (localWindow != null)
      {
        Component localComponent1 = KeyboardFocusManager.getMostRecentFocusOwner(localWindow);
        i = (localComponent1 == this) || (isParentOf(localComponent1)) ? 1 : 0;
        synchronized (KeyboardFocusManager.class)
        {
          Component localComponent2 = localWindow.getTemporaryLostComponent();
          if ((isParentOf(localComponent2)) || (localComponent2 == this)) {
            localWindow.setTemporaryLostComponent(null);
          }
        }
      }
    }
    if (i != 0) {
      KeyboardFocusManager.setMostRecentFocusOwner(localWindow, null);
    }
  }
  
  void clearCurrentFocusCycleRootOnHide()
  {
    KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    Container localContainer = localKeyboardFocusManager.getCurrentFocusCycleRoot();
    if ((localContainer == this) || (isParentOf(localContainer))) {
      localKeyboardFocusManager.setGlobalCurrentFocusCycleRootPriv(null);
    }
  }
  
  final Container getTraversalRoot()
  {
    if (isFocusCycleRoot()) {
      return findTraversalRoot();
    }
    return super.getTraversalRoot();
  }
  
  public void setFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy)
  {
    FocusTraversalPolicy localFocusTraversalPolicy;
    synchronized (this)
    {
      localFocusTraversalPolicy = focusTraversalPolicy;
      focusTraversalPolicy = paramFocusTraversalPolicy;
    }
    firePropertyChange("focusTraversalPolicy", localFocusTraversalPolicy, paramFocusTraversalPolicy);
  }
  
  public FocusTraversalPolicy getFocusTraversalPolicy()
  {
    if ((!isFocusTraversalPolicyProvider()) && (!isFocusCycleRoot())) {
      return null;
    }
    FocusTraversalPolicy localFocusTraversalPolicy = focusTraversalPolicy;
    if (localFocusTraversalPolicy != null) {
      return localFocusTraversalPolicy;
    }
    Container localContainer = getFocusCycleRootAncestor();
    if (localContainer != null) {
      return localContainer.getFocusTraversalPolicy();
    }
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
  }
  
  public boolean isFocusTraversalPolicySet()
  {
    return focusTraversalPolicy != null;
  }
  
  public void setFocusCycleRoot(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = focusCycleRoot;
      focusCycleRoot = paramBoolean;
    }
    firePropertyChange("focusCycleRoot", bool, paramBoolean);
  }
  
  public boolean isFocusCycleRoot()
  {
    return focusCycleRoot;
  }
  
  public final void setFocusTraversalPolicyProvider(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = focusTraversalPolicyProvider;
      focusTraversalPolicyProvider = paramBoolean;
    }
    firePropertyChange("focusTraversalPolicyProvider", bool, paramBoolean);
  }
  
  public final boolean isFocusTraversalPolicyProvider()
  {
    return focusTraversalPolicyProvider;
  }
  
  public void transferFocusDownCycle()
  {
    if (isFocusCycleRoot())
    {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(this);
      Component localComponent = getFocusTraversalPolicy().getDefaultComponent(this);
      if (localComponent != null) {
        localComponent.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_DOWN);
      }
    }
  }
  
  void preProcessKeyEvent(KeyEvent paramKeyEvent)
  {
    Container localContainer = parent;
    if (localContainer != null) {
      localContainer.preProcessKeyEvent(paramKeyEvent);
    }
  }
  
  void postProcessKeyEvent(KeyEvent paramKeyEvent)
  {
    Container localContainer = parent;
    if (localContainer != null) {
      localContainer.postProcessKeyEvent(paramKeyEvent);
    }
  }
  
  boolean postsOldMouseEvents()
  {
    return true;
  }
  
  public void applyComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    super.applyComponentOrientation(paramComponentOrientation);
    synchronized (getTreeLock())
    {
      for (int i = 0; i < component.size(); i++)
      {
        Component localComponent = (Component)component.get(i);
        localComponent.applyComponentOrientation(paramComponentOrientation);
      }
    }
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    super.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    super.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("ncomponents", component.size());
    localPutField.put("component", component.toArray(EMPTY_ARRAY));
    localPutField.put("layoutMgr", layoutMgr);
    localPutField.put("dispatcher", dispatcher);
    localPutField.put("maxSize", maxSize);
    localPutField.put("focusCycleRoot", focusCycleRoot);
    localPutField.put("containerSerializedDataVersion", containerSerializedDataVersion);
    localPutField.put("focusTraversalPolicyProvider", focusTraversalPolicyProvider);
    paramObjectOutputStream.writeFields();
    AWTEventMulticaster.save(paramObjectOutputStream, "containerL", containerListener);
    paramObjectOutputStream.writeObject(null);
    if ((focusTraversalPolicy instanceof Serializable)) {
      paramObjectOutputStream.writeObject(focusTraversalPolicy);
    } else {
      paramObjectOutputStream.writeObject(null);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Component[] arrayOfComponent = (Component[])localGetField.get("component", EMPTY_ARRAY);
    int i = Integer.valueOf(localGetField.get("ncomponents", 0)).intValue();
    component = new ArrayList(i);
    for (int j = 0; j < i; j++) {
      component.add(arrayOfComponent[j]);
    }
    layoutMgr = ((LayoutManager)localGetField.get("layoutMgr", null));
    dispatcher = ((LightweightDispatcher)localGetField.get("dispatcher", null));
    if (maxSize == null) {
      maxSize = ((Dimension)localGetField.get("maxSize", null));
    }
    focusCycleRoot = localGetField.get("focusCycleRoot", false);
    containerSerializedDataVersion = localGetField.get("containerSerializedDataVersion", 1);
    focusTraversalPolicyProvider = localGetField.get("focusTraversalPolicyProvider", false);
    List localList = component;
    Object localObject1 = localList.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Component)((Iterator)localObject1).next();
      parent = this;
      adjustListeningChildren(32768L, ((Component)localObject2).numListening(32768L));
      adjustListeningChildren(65536L, ((Component)localObject2).numListening(65536L));
      adjustDescendants(((Component)localObject2).countHierarchyMembers());
    }
    while (null != (localObject1 = paramObjectInputStream.readObject()))
    {
      localObject2 = ((String)localObject1).intern();
      if ("containerL" == localObject2) {
        addContainerListener((ContainerListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
    try
    {
      localObject2 = paramObjectInputStream.readObject();
      if ((localObject2 instanceof FocusTraversalPolicy)) {
        focusTraversalPolicy = ((FocusTraversalPolicy)localObject2);
      }
    }
    catch (OptionalDataException localOptionalDataException)
    {
      if (!eof) {
        throw localOptionalDataException;
      }
    }
  }
  
  Accessible getAccessibleAt(Point paramPoint)
  {
    synchronized (getTreeLock())
    {
      Object localObject2;
      if ((this instanceof Accessible))
      {
        localObject1 = (Accessible)this;
        AccessibleContext localAccessibleContext = ((Accessible)localObject1).getAccessibleContext();
        if (localAccessibleContext != null)
        {
          int k = localAccessibleContext.getAccessibleChildrenCount();
          for (int m = 0; m < k; m++)
          {
            localObject1 = localAccessibleContext.getAccessibleChild(m);
            if (localObject1 != null)
            {
              localAccessibleContext = ((Accessible)localObject1).getAccessibleContext();
              if (localAccessibleContext != null)
              {
                AccessibleComponent localAccessibleComponent = localAccessibleContext.getAccessibleComponent();
                if ((localAccessibleComponent != null) && (localAccessibleComponent.isShowing()))
                {
                  localObject2 = localAccessibleComponent.getLocation();
                  Point localPoint2 = new Point(x - x, y - y);
                  if (localAccessibleComponent.contains(localPoint2)) {
                    return (Accessible)localObject1;
                  }
                }
              }
            }
          }
        }
        return (Accessible)this;
      }
      Object localObject1 = this;
      if (!contains(x, y))
      {
        localObject1 = null;
      }
      else
      {
        int i = getComponentCount();
        for (int j = 0; j < i; j++)
        {
          localObject2 = getComponent(j);
          if ((localObject2 != null) && (((Component)localObject2).isShowing()))
          {
            Point localPoint1 = ((Component)localObject2).getLocation();
            if (((Component)localObject2).contains(x - x, y - y)) {
              localObject1 = localObject2;
            }
          }
        }
      }
      if ((localObject1 instanceof Accessible)) {
        return (Accessible)localObject1;
      }
      return null;
    }
  }
  
  int getAccessibleChildrenCount()
  {
    synchronized (getTreeLock())
    {
      int i = 0;
      Component[] arrayOfComponent = getComponents();
      for (int j = 0; j < arrayOfComponent.length; j++) {
        if ((arrayOfComponent[j] instanceof Accessible)) {
          i++;
        }
      }
      return i;
    }
  }
  
  Accessible getAccessibleChild(int paramInt)
  {
    synchronized (getTreeLock())
    {
      Component[] arrayOfComponent = getComponents();
      int i = 0;
      for (int j = 0; j < arrayOfComponent.length; j++) {
        if ((arrayOfComponent[j] instanceof Accessible))
        {
          if (i == paramInt) {
            return (Accessible)arrayOfComponent[j];
          }
          i++;
        }
      }
      return null;
    }
  }
  
  final void increaseComponentCount(Component paramComponent)
  {
    synchronized (getTreeLock())
    {
      if (!paramComponent.isDisplayable()) {
        throw new IllegalStateException("Peer does not exist while invoking the increaseComponentCount() method");
      }
      int i = 0;
      int j = 0;
      if ((paramComponent instanceof Container))
      {
        j = numOfLWComponents;
        i = numOfHWComponents;
      }
      if (paramComponent.isLightweight()) {
        j++;
      } else {
        i++;
      }
      for (Container localContainer = this; localContainer != null; localContainer = localContainer.getContainer())
      {
        numOfLWComponents += j;
        numOfHWComponents += i;
      }
    }
  }
  
  final void decreaseComponentCount(Component paramComponent)
  {
    synchronized (getTreeLock())
    {
      if (!paramComponent.isDisplayable()) {
        throw new IllegalStateException("Peer does not exist while invoking the decreaseComponentCount() method");
      }
      int i = 0;
      int j = 0;
      if ((paramComponent instanceof Container))
      {
        j = numOfLWComponents;
        i = numOfHWComponents;
      }
      if (paramComponent.isLightweight()) {
        j++;
      } else {
        i++;
      }
      for (Container localContainer = this; localContainer != null; localContainer = localContainer.getContainer())
      {
        numOfLWComponents -= j;
        numOfHWComponents -= i;
      }
    }
  }
  
  private int getTopmostComponentIndex()
  {
    checkTreeLock();
    if (getComponentCount() > 0) {
      return 0;
    }
    return -1;
  }
  
  private int getBottommostComponentIndex()
  {
    checkTreeLock();
    if (getComponentCount() > 0) {
      return getComponentCount() - 1;
    }
    return -1;
  }
  
  final Region getOpaqueShape()
  {
    checkTreeLock();
    if ((isLightweight()) && (isNonOpaqueForMixing()) && (hasLightweightDescendants()))
    {
      Region localRegion = Region.EMPTY_REGION;
      for (int i = 0; i < getComponentCount(); i++)
      {
        Component localComponent = getComponent(i);
        if ((localComponent.isLightweight()) && (localComponent.isShowing())) {
          localRegion = localRegion.getUnion(localComponent.getOpaqueShape());
        }
      }
      return localRegion.getIntersection(getNormalShape());
    }
    return super.getOpaqueShape();
  }
  
  final void recursiveSubtractAndApplyShape(Region paramRegion)
  {
    recursiveSubtractAndApplyShape(paramRegion, getTopmostComponentIndex(), getBottommostComponentIndex());
  }
  
  final void recursiveSubtractAndApplyShape(Region paramRegion, int paramInt)
  {
    recursiveSubtractAndApplyShape(paramRegion, paramInt, getBottommostComponentIndex());
  }
  
  final void recursiveSubtractAndApplyShape(Region paramRegion, int paramInt1, int paramInt2)
  {
    checkTreeLock();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
      mixingLog.fine("this = " + this + "; shape=" + paramRegion + "; fromZ=" + paramInt1 + "; toZ=" + paramInt2);
    }
    if (paramInt1 == -1) {
      return;
    }
    if (paramRegion.isEmpty()) {
      return;
    }
    if ((getLayout() != null) && (!isValid())) {
      return;
    }
    for (int i = paramInt1; i <= paramInt2; i++)
    {
      Component localComponent = getComponent(i);
      if (!localComponent.isLightweight()) {
        localComponent.subtractAndApplyShape(paramRegion);
      } else if (((localComponent instanceof Container)) && (((Container)localComponent).hasHeavyweightDescendants()) && (localComponent.isShowing())) {
        ((Container)localComponent).recursiveSubtractAndApplyShape(paramRegion);
      }
    }
  }
  
  final void recursiveApplyCurrentShape()
  {
    recursiveApplyCurrentShape(getTopmostComponentIndex(), getBottommostComponentIndex());
  }
  
  final void recursiveApplyCurrentShape(int paramInt)
  {
    recursiveApplyCurrentShape(paramInt, getBottommostComponentIndex());
  }
  
  final void recursiveApplyCurrentShape(int paramInt1, int paramInt2)
  {
    checkTreeLock();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
      mixingLog.fine("this = " + this + "; fromZ=" + paramInt1 + "; toZ=" + paramInt2);
    }
    if (paramInt1 == -1) {
      return;
    }
    if ((getLayout() != null) && (!isValid())) {
      return;
    }
    for (int i = paramInt1; i <= paramInt2; i++)
    {
      Component localComponent = getComponent(i);
      if (!localComponent.isLightweight()) {
        localComponent.applyCurrentShape();
      }
      if (((localComponent instanceof Container)) && (((Container)localComponent).hasHeavyweightDescendants())) {
        ((Container)localComponent).recursiveApplyCurrentShape();
      }
    }
  }
  
  private void recursiveShowHeavyweightChildren()
  {
    if ((!hasHeavyweightDescendants()) || (!isVisible())) {
      return;
    }
    for (int i = 0; i < getComponentCount(); i++)
    {
      Component localComponent = getComponent(i);
      if (localComponent.isLightweight())
      {
        if ((localComponent instanceof Container)) {
          ((Container)localComponent).recursiveShowHeavyweightChildren();
        }
      }
      else if (localComponent.isVisible())
      {
        ComponentPeer localComponentPeer = localComponent.getPeer();
        if (localComponentPeer != null) {
          localComponentPeer.setVisible(true);
        }
      }
    }
  }
  
  private void recursiveHideHeavyweightChildren()
  {
    if (!hasHeavyweightDescendants()) {
      return;
    }
    for (int i = 0; i < getComponentCount(); i++)
    {
      Component localComponent = getComponent(i);
      if (localComponent.isLightweight())
      {
        if ((localComponent instanceof Container)) {
          ((Container)localComponent).recursiveHideHeavyweightChildren();
        }
      }
      else if (localComponent.isVisible())
      {
        ComponentPeer localComponentPeer = localComponent.getPeer();
        if (localComponentPeer != null) {
          localComponentPeer.setVisible(false);
        }
      }
    }
  }
  
  private void recursiveRelocateHeavyweightChildren(Point paramPoint)
  {
    for (int i = 0; i < getComponentCount(); i++)
    {
      Component localComponent = getComponent(i);
      Object localObject;
      if (localComponent.isLightweight())
      {
        if (((localComponent instanceof Container)) && (((Container)localComponent).hasHeavyweightDescendants()))
        {
          localObject = new Point(paramPoint);
          ((Point)localObject).translate(localComponent.getX(), localComponent.getY());
          ((Container)localComponent).recursiveRelocateHeavyweightChildren((Point)localObject);
        }
      }
      else
      {
        localObject = localComponent.getPeer();
        if (localObject != null) {
          ((ComponentPeer)localObject).setBounds(x + localComponent.getX(), y + localComponent.getY(), localComponent.getWidth(), localComponent.getHeight(), 1);
        }
      }
    }
  }
  
  final boolean isRecursivelyVisibleUpToHeavyweightContainer()
  {
    if (!isLightweight()) {
      return true;
    }
    for (Container localContainer = this; (localContainer != null) && (localContainer.isLightweight()); localContainer = localContainer.getContainer()) {
      if (!localContainer.isVisible()) {
        return false;
      }
    }
    return true;
  }
  
  void mixOnShowing()
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this);
      }
      boolean bool = isLightweight();
      if ((bool) && (isRecursivelyVisibleUpToHeavyweightContainer())) {
        recursiveShowHeavyweightChildren();
      }
      if (!isMixingNeeded()) {
        return;
      }
      if ((!bool) || ((bool) && (hasHeavyweightDescendants()))) {
        recursiveApplyCurrentShape();
      }
      super.mixOnShowing();
    }
  }
  
  void mixOnHiding(boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; isLightweight=" + paramBoolean);
      }
      if (paramBoolean) {
        recursiveHideHeavyweightChildren();
      }
      super.mixOnHiding(paramBoolean);
    }
  }
  
  void mixOnReshaping()
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this);
      }
      boolean bool = isMixingNeeded();
      if ((isLightweight()) && (hasHeavyweightDescendants()))
      {
        Point localPoint = new Point(getX(), getY());
        for (Container localContainer = getContainer(); (localContainer != null) && (localContainer.isLightweight()); localContainer = localContainer.getContainer()) {
          localPoint.translate(localContainer.getX(), localContainer.getY());
        }
        recursiveRelocateHeavyweightChildren(localPoint);
        if (!bool) {
          return;
        }
        recursiveApplyCurrentShape();
      }
      if (!bool) {
        return;
      }
      super.mixOnReshaping();
    }
  }
  
  void mixOnZOrderChanging(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; oldZ=" + paramInt1 + "; newZ=" + paramInt2);
      }
      if (!isMixingNeeded()) {
        return;
      }
      int i = paramInt2 < paramInt1 ? 1 : 0;
      if ((i != 0) && (isLightweight()) && (hasHeavyweightDescendants())) {
        recursiveApplyCurrentShape();
      }
      super.mixOnZOrderChanging(paramInt1, paramInt2);
    }
  }
  
  void mixOnValidating()
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this);
      }
      if (!isMixingNeeded()) {
        return;
      }
      if (hasHeavyweightDescendants()) {
        recursiveApplyCurrentShape();
      }
      if ((isLightweight()) && (isNonOpaqueForMixing())) {
        subtractAndApplyShapeBelowMe();
      }
      super.mixOnValidating();
    }
  }
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setContainerAccessor(new AWTAccessor.ContainerAccessor()
    {
      public void validateUnconditionally(Container paramAnonymousContainer)
      {
        paramAnonymousContainer.validateUnconditionally();
      }
      
      public Component findComponentAt(Container paramAnonymousContainer, int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean)
      {
        return paramAnonymousContainer.findComponentAt(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousBoolean);
      }
    });
  }
  
  protected class AccessibleAWTContainer
    extends Component.AccessibleAWTComponent
  {
    private static final long serialVersionUID = 5081320404842566097L;
    private volatile transient int propertyListenersCount = 0;
    protected ContainerListener accessibleContainerHandler = null;
    
    protected AccessibleAWTContainer()
    {
      super();
    }
    
    public int getAccessibleChildrenCount()
    {
      return Container.this.getAccessibleChildrenCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      return Container.this.getAccessibleChild(paramInt);
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      return Container.this.getAccessibleAt(paramPoint);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    {
      if (accessibleContainerHandler == null) {
        accessibleContainerHandler = new AccessibleContainerHandler();
      }
      if (propertyListenersCount++ == 0) {
        addContainerListener(accessibleContainerHandler);
      }
      super.addPropertyChangeListener(paramPropertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    {
      if (--propertyListenersCount == 0) {
        removeContainerListener(accessibleContainerHandler);
      }
      super.removePropertyChangeListener(paramPropertyChangeListener);
    }
    
    protected class AccessibleContainerHandler
      implements ContainerListener
    {
      protected AccessibleContainerHandler() {}
      
      public void componentAdded(ContainerEvent paramContainerEvent)
      {
        Component localComponent = paramContainerEvent.getChild();
        if ((localComponent != null) && ((localComponent instanceof Accessible))) {
          firePropertyChange("AccessibleChild", null, ((Accessible)localComponent).getAccessibleContext());
        }
      }
      
      public void componentRemoved(ContainerEvent paramContainerEvent)
      {
        Component localComponent = paramContainerEvent.getChild();
        if ((localComponent != null) && ((localComponent instanceof Accessible))) {
          firePropertyChange("AccessibleChild", ((Accessible)localComponent).getAccessibleContext(), null);
        }
      }
    }
  }
  
  static class DropTargetEventTargetFilter
    implements Container.EventTargetFilter
  {
    static final Container.EventTargetFilter FILTER = new DropTargetEventTargetFilter();
    
    private DropTargetEventTargetFilter() {}
    
    public boolean accept(Component paramComponent)
    {
      DropTarget localDropTarget = paramComponent.getDropTarget();
      return (localDropTarget != null) && (localDropTarget.isActive());
    }
  }
  
  static abstract interface EventTargetFilter
  {
    public abstract boolean accept(Component paramComponent);
  }
  
  static class MouseEventTargetFilter
    implements Container.EventTargetFilter
  {
    static final Container.EventTargetFilter FILTER = new MouseEventTargetFilter();
    
    private MouseEventTargetFilter() {}
    
    public boolean accept(Component paramComponent)
    {
      return ((eventMask & 0x20) != 0L) || ((eventMask & 0x10) != 0L) || ((eventMask & 0x20000) != 0L) || (mouseListener != null) || (mouseMotionListener != null) || (mouseWheelListener != null);
    }
  }
  
  static final class WakingRunnable
    implements Runnable
  {
    WakingRunnable() {}
    
    public void run() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Container.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */