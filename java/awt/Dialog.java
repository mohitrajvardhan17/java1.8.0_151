package java.awt;

import java.awt.event.ComponentEvent;
import java.awt.event.InvocationEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.lang.ref.WeakReference;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.awt.util.IdentityLinkedList;
import sun.security.util.SecurityConstants.AWT;

public class Dialog
  extends Window
{
  boolean resizable = true;
  boolean undecorated = false;
  private transient boolean initialized = false;
  public static final ModalityType DEFAULT_MODALITY_TYPE = ModalityType.APPLICATION_MODAL;
  boolean modal;
  ModalityType modalityType;
  static transient IdentityArrayList<Dialog> modalDialogs = new IdentityArrayList();
  transient IdentityArrayList<Window> blockedWindows = new IdentityArrayList();
  String title;
  private transient ModalEventFilter modalFilter;
  private volatile transient SecondaryLoop secondaryLoop;
  volatile transient boolean isInHide = false;
  volatile transient boolean isInDispose = false;
  private static final String base = "dialog";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 5920926903803293709L;
  
  public Dialog(Frame paramFrame)
  {
    this(paramFrame, "", false);
  }
  
  public Dialog(Frame paramFrame, boolean paramBoolean)
  {
    this(paramFrame, "", paramBoolean);
  }
  
  public Dialog(Frame paramFrame, String paramString)
  {
    this(paramFrame, paramString, false);
  }
  
  public Dialog(Frame paramFrame, String paramString, boolean paramBoolean)
  {
    this(paramFrame, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }
  
  public Dialog(Frame paramFrame, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration)
  {
    this(paramFrame, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, paramGraphicsConfiguration);
  }
  
  public Dialog(Dialog paramDialog)
  {
    this(paramDialog, "", false);
  }
  
  public Dialog(Dialog paramDialog, String paramString)
  {
    this(paramDialog, paramString, false);
  }
  
  public Dialog(Dialog paramDialog, String paramString, boolean paramBoolean)
  {
    this(paramDialog, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }
  
  public Dialog(Dialog paramDialog, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration)
  {
    this(paramDialog, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, paramGraphicsConfiguration);
  }
  
  public Dialog(Window paramWindow)
  {
    this(paramWindow, "", ModalityType.MODELESS);
  }
  
  public Dialog(Window paramWindow, String paramString)
  {
    this(paramWindow, paramString, ModalityType.MODELESS);
  }
  
  public Dialog(Window paramWindow, ModalityType paramModalityType)
  {
    this(paramWindow, "", paramModalityType);
  }
  
  public Dialog(Window paramWindow, String paramString, ModalityType paramModalityType)
  {
    super(paramWindow);
    if ((paramWindow != null) && (!(paramWindow instanceof Frame)) && (!(paramWindow instanceof Dialog))) {
      throw new IllegalArgumentException("Wrong parent window");
    }
    title = paramString;
    setModalityType(paramModalityType);
    SunToolkit.checkAndSetPolicy(this);
    initialized = true;
  }
  
  public Dialog(Window paramWindow, String paramString, ModalityType paramModalityType, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramWindow, paramGraphicsConfiguration);
    if ((paramWindow != null) && (!(paramWindow instanceof Frame)) && (!(paramWindow instanceof Dialog))) {
      throw new IllegalArgumentException("wrong owner window");
    }
    title = paramString;
    setModalityType(paramModalityType);
    SunToolkit.checkAndSetPolicy(this);
    initialized = true;
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 18
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 323	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 748	java/lang/StringBuilder:<init>	()V
    //   12: ldc 11
    //   14: invokevirtual 752	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 598	java/awt/Dialog:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 598	java/awt/Dialog:nameCounter	I
    //   26: invokevirtual 750	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 749	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: aload_1
    //   33: monitorexit
    //   34: areturn
    //   35: astore_2
    //   36: aload_1
    //   37: monitorexit
    //   38: aload_2
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	Dialog
    //   3	34	1	Ljava/lang/Object;	Object
    //   35	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   5	34	35	finally
    //   35	38	35	finally
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if ((parent != null) && (parent.getPeer() == null)) {
        parent.addNotify();
      }
      if (peer == null) {
        peer = getToolkit().createDialog(this);
      }
      super.addNotify();
    }
  }
  
  public boolean isModal()
  {
    return isModal_NoClientCode();
  }
  
  final boolean isModal_NoClientCode()
  {
    return modalityType != ModalityType.MODELESS;
  }
  
  public void setModal(boolean paramBoolean)
  {
    modal = paramBoolean;
    setModalityType(paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }
  
  public ModalityType getModalityType()
  {
    return modalityType;
  }
  
  public void setModalityType(ModalityType paramModalityType)
  {
    if (paramModalityType == null) {
      paramModalityType = ModalityType.MODELESS;
    }
    if (!Toolkit.getDefaultToolkit().isModalityTypeSupported(paramModalityType)) {
      paramModalityType = ModalityType.MODELESS;
    }
    if (modalityType == paramModalityType) {
      return;
    }
    checkModalityPermission(paramModalityType);
    modalityType = paramModalityType;
    modal = (modalityType != ModalityType.MODELESS);
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public void setTitle(String paramString)
  {
    String str = title;
    synchronized (this)
    {
      title = paramString;
      DialogPeer localDialogPeer = (DialogPeer)peer;
      if (localDialogPeer != null) {
        localDialogPeer.setTitle(paramString);
      }
    }
    firePropertyChange("title", str, paramString);
  }
  
  private boolean conditionalShow(Component paramComponent, AtomicLong paramAtomicLong)
  {
    closeSplashScreen();
    boolean bool;
    synchronized (getTreeLock())
    {
      if (peer == null) {
        addNotify();
      }
      validateUnconditionally();
      if (visible)
      {
        toFront();
        bool = false;
      }
      else
      {
        visible = (bool = 1);
        if (!isModal())
        {
          checkShouldBeBlocked(this);
        }
        else
        {
          modalDialogs.add(this);
          modalShow();
        }
        if ((paramComponent != null) && (paramAtomicLong != null) && (isFocusable()) && (isEnabled()) && (!isModalBlocked()))
        {
          paramAtomicLong.set(Toolkit.getEventQueue().getMostRecentKeyEventTime());
          KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(paramAtomicLong.get(), paramComponent);
        }
        mixOnShowing();
        peer.setVisible(true);
        if (isModalBlocked()) {
          modalBlocker.toFront();
        }
        setLocationByPlatform(false);
        for (int i = 0; i < ownedWindowList.size(); i++)
        {
          Window localWindow = (Window)((WeakReference)ownedWindowList.elementAt(i)).get();
          if ((localWindow != null) && (showWithParent))
          {
            localWindow.show();
            showWithParent = false;
          }
        }
        Window.updateChildFocusableWindowState(this);
        createHierarchyEvents(1400, this, parent, 4L, Toolkit.enabledOnToolkit(32768L));
        if ((componentListener != null) || ((eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
        {
          ComponentEvent localComponentEvent = new ComponentEvent(this, 102);
          Toolkit.getEventQueue().postEvent(localComponentEvent);
        }
      }
    }
    if ((bool) && ((state & 0x1) == 0))
    {
      postWindowEvent(200);
      state |= 0x1;
    }
    return bool;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    super.setVisible(paramBoolean);
  }
  
  @Deprecated
  public void show()
  {
    if (!initialized) {
      throw new IllegalStateException("The dialog component has not been initialized properly");
    }
    beforeFirstShow = false;
    if (!isModal())
    {
      conditionalShow(null, null);
    }
    else
    {
      AppContext localAppContext1 = AppContext.getAppContext();
      AtomicLong localAtomicLong = new AtomicLong();
      Component localComponent = null;
      try
      {
        localComponent = getMostRecentFocusOwner();
        if (conditionalShow(localComponent, localAtomicLong))
        {
          modalFilter = ModalEventFilter.createFilterForDialog(this);
          Conditional local1 = new Conditional()
          {
            public boolean evaluate()
            {
              return windowClosingException == null;
            }
          };
          Object localObject1;
          AppContext localAppContext2;
          EventQueue localEventQueue;
          Object localObject2;
          if (modalityType == ModalityType.TOOLKIT_MODAL)
          {
            localObject1 = AppContext.getAppContexts().iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localAppContext2 = (AppContext)((Iterator)localObject1).next();
              if (localAppContext2 != localAppContext1)
              {
                localEventQueue = (EventQueue)localAppContext2.get(AppContext.EVENT_QUEUE_KEY);
                localObject2 = new Runnable()
                {
                  public void run() {}
                };
                localEventQueue.postEvent(new InvocationEvent(this, (Runnable)localObject2));
                EventDispatchThread localEventDispatchThread = localEventQueue.getDispatchThread();
                localEventDispatchThread.addEventFilter(modalFilter);
              }
            }
          }
          modalityPushed();
          try
          {
            localObject1 = (EventQueue)AccessController.doPrivileged(new PrivilegedAction()
            {
              public EventQueue run()
              {
                return Toolkit.getDefaultToolkit().getSystemEventQueue();
              }
            });
            secondaryLoop = ((EventQueue)localObject1).createSecondaryLoop(local1, modalFilter, 0L);
            if (!secondaryLoop.enter()) {
              secondaryLoop = null;
            }
          }
          finally
          {
            modalityPopped();
          }
          if (modalityType == ModalityType.TOOLKIT_MODAL)
          {
            localObject1 = AppContext.getAppContexts().iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localAppContext2 = (AppContext)((Iterator)localObject1).next();
              if (localAppContext2 != localAppContext1)
              {
                localEventQueue = (EventQueue)localAppContext2.get(AppContext.EVENT_QUEUE_KEY);
                localObject2 = localEventQueue.getDispatchThread();
                ((EventDispatchThread)localObject2).removeEventFilter(modalFilter);
              }
            }
          }
          if (windowClosingException != null)
          {
            windowClosingException.fillInStackTrace();
            throw windowClosingException;
          }
        }
      }
      finally
      {
        if (localComponent != null) {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(localAtomicLong.get(), localComponent);
        }
      }
    }
  }
  
  final void modalityPushed()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit))
    {
      SunToolkit localSunToolkit = (SunToolkit)localToolkit;
      localSunToolkit.notifyModalityPushed(this);
    }
  }
  
  final void modalityPopped()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit))
    {
      SunToolkit localSunToolkit = (SunToolkit)localToolkit;
      localSunToolkit.notifyModalityPopped(this);
    }
  }
  
  void interruptBlocking()
  {
    if (isModal())
    {
      disposeImpl();
    }
    else if (windowClosingException != null)
    {
      windowClosingException.fillInStackTrace();
      windowClosingException.printStackTrace();
      windowClosingException = null;
    }
  }
  
  private void hideAndDisposePreHandler()
  {
    isInHide = true;
    synchronized (getTreeLock())
    {
      if (secondaryLoop != null)
      {
        modalHide();
        if (modalFilter != null) {
          modalFilter.disable();
        }
        modalDialogs.remove(this);
      }
    }
  }
  
  private void hideAndDisposeHandler()
  {
    if (secondaryLoop != null)
    {
      secondaryLoop.exit();
      secondaryLoop = null;
    }
    isInHide = false;
  }
  
  @Deprecated
  public void hide()
  {
    hideAndDisposePreHandler();
    super.hide();
    if (!isInDispose) {
      hideAndDisposeHandler();
    }
  }
  
  void doDispose()
  {
    isInDispose = true;
    super.doDispose();
    hideAndDisposeHandler();
    isInDispose = false;
  }
  
  public void toBack()
  {
    super.toBack();
    if (visible) {
      synchronized (getTreeLock())
      {
        Iterator localIterator = blockedWindows.iterator();
        while (localIterator.hasNext())
        {
          Window localWindow = (Window)localIterator.next();
          localWindow.toBack_NoClientCode();
        }
      }
    }
  }
  
  public boolean isResizable()
  {
    return resizable;
  }
  
  public void setResizable(boolean paramBoolean)
  {
    int i = 0;
    synchronized (this)
    {
      resizable = paramBoolean;
      DialogPeer localDialogPeer = (DialogPeer)peer;
      if (localDialogPeer != null)
      {
        localDialogPeer.setResizable(paramBoolean);
        i = 1;
      }
    }
    if (i != 0) {
      invalidateIfValid();
    }
  }
  
  public void setUndecorated(boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if (isDisplayable()) {
        throw new IllegalComponentStateException("The dialog is displayable.");
      }
      if (!paramBoolean)
      {
        if (getOpacity() < 1.0F) {
          throw new IllegalComponentStateException("The dialog is not opaque");
        }
        if (getShape() != null) {
          throw new IllegalComponentStateException("The dialog does not have a default shape");
        }
        Color localColor = getBackground();
        if ((localColor != null) && (localColor.getAlpha() < 255)) {
          throw new IllegalComponentStateException("The dialog background color is not opaque");
        }
      }
      undecorated = paramBoolean;
    }
  }
  
  public boolean isUndecorated()
  {
    return undecorated;
  }
  
  public void setOpacity(float paramFloat)
  {
    synchronized (getTreeLock())
    {
      if ((paramFloat < 1.0F) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The dialog is decorated");
      }
      super.setOpacity(paramFloat);
    }
  }
  
  public void setShape(Shape paramShape)
  {
    synchronized (getTreeLock())
    {
      if ((paramShape != null) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The dialog is decorated");
      }
      super.setShape(paramShape);
    }
  }
  
  public void setBackground(Color paramColor)
  {
    synchronized (getTreeLock())
    {
      if ((paramColor != null) && (paramColor.getAlpha() < 255) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The dialog is decorated");
      }
      super.setBackground(paramColor);
    }
  }
  
  protected String paramString()
  {
    String str = super.paramString() + "," + modalityType;
    if (title != null) {
      str = str + ",title=" + title;
    }
    return str;
  }
  
  private static native void initIDs();
  
  void modalShow()
  {
    IdentityArrayList localIdentityArrayList1 = new IdentityArrayList();
    Iterator localIterator = modalDialogs.iterator();
    Dialog localDialog1;
    while (localIterator.hasNext())
    {
      localDialog1 = (Dialog)localIterator.next();
      if (localDialog1.shouldBlock(this))
      {
        for (localObject1 = localDialog1; (localObject1 != null) && (localObject1 != this); localObject1 = ((Window)localObject1).getOwner_NoClientCode()) {}
        if ((localObject1 == this) || (!shouldBlock(localDialog1)) || (modalityType.compareTo(localDialog1.getModalityType()) < 0)) {
          localIdentityArrayList1.add(localDialog1);
        }
      }
    }
    for (int i = 0; i < localIdentityArrayList1.size(); i++)
    {
      localDialog1 = (Dialog)localIdentityArrayList1.get(i);
      if (localDialog1.isModalBlocked())
      {
        localObject1 = localDialog1.getModalBlocker();
        if (!localIdentityArrayList1.contains(localObject1)) {
          localIdentityArrayList1.add(i + 1, localObject1);
        }
      }
    }
    if (localIdentityArrayList1.size() > 0) {
      ((Dialog)localIdentityArrayList1.get(0)).blockWindow(this);
    }
    IdentityArrayList localIdentityArrayList2 = new IdentityArrayList(localIdentityArrayList1);
    for (int j = 0; j < localIdentityArrayList2.size(); j++)
    {
      localObject1 = (Window)localIdentityArrayList2.get(j);
      localObject2 = ((Window)localObject1).getOwnedWindows_NoClientCode();
      for (Object localObject4 : localObject2) {
        localIdentityArrayList2.add(localObject4);
      }
    }
    Object localObject1 = new IdentityLinkedList();
    Object localObject2 = Window.getAllUnblockedWindows();
    ??? = ((IdentityArrayList)localObject2).iterator();
    while (((Iterator)???).hasNext())
    {
      Window localWindow = (Window)((Iterator)???).next();
      if ((shouldBlock(localWindow)) && (!localIdentityArrayList2.contains(localWindow))) {
        if (((localWindow instanceof Dialog)) && (((Dialog)localWindow).isModal_NoClientCode()))
        {
          Dialog localDialog2 = (Dialog)localWindow;
          if ((localDialog2.shouldBlock(this)) && (modalDialogs.indexOf(localDialog2) > modalDialogs.indexOf(this))) {}
        }
        else
        {
          ((List)localObject1).add(localWindow);
        }
      }
    }
    blockWindows((List)localObject1);
    if (!isModalBlocked()) {
      updateChildrenBlocking();
    }
  }
  
  void modalHide()
  {
    IdentityArrayList localIdentityArrayList = new IdentityArrayList();
    int i = blockedWindows.size();
    Window localWindow;
    for (int j = 0; j < i; j++)
    {
      localWindow = (Window)blockedWindows.get(0);
      localIdentityArrayList.add(localWindow);
      unblockWindow(localWindow);
    }
    for (j = 0; j < i; j++)
    {
      localWindow = (Window)localIdentityArrayList.get(j);
      if (((localWindow instanceof Dialog)) && (((Dialog)localWindow).isModal_NoClientCode()))
      {
        Dialog localDialog = (Dialog)localWindow;
        localDialog.modalShow();
      }
      else
      {
        checkShouldBeBlocked(localWindow);
      }
    }
  }
  
  boolean shouldBlock(Window paramWindow)
  {
    if ((!isVisible_NoClientCode()) || ((!paramWindow.isVisible_NoClientCode()) && (!isInShow)) || (isInHide) || (paramWindow == this) || (!isModal_NoClientCode())) {
      return false;
    }
    if (((paramWindow instanceof Dialog)) && (isInHide)) {
      return false;
    }
    Object localObject;
    for (Dialog localDialog = this; localDialog != null; localDialog = localDialog.getModalBlocker())
    {
      for (localObject = paramWindow; (localObject != null) && (localObject != localDialog); localObject = ((Component)localObject).getParent_NoClientCode()) {}
      if (localObject == localDialog) {
        return false;
      }
    }
    switch (modalityType)
    {
    case MODELESS: 
      return false;
    case DOCUMENT_MODAL: 
      if (paramWindow.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE))
      {
        for (localObject = this; (localObject != null) && (localObject != paramWindow); localObject = ((Component)localObject).getParent_NoClientCode()) {}
        return localObject == paramWindow;
      }
      return getDocumentRoot() == paramWindow.getDocumentRoot();
    case APPLICATION_MODAL: 
      return (!paramWindow.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE)) && (appContext == appContext);
    case TOOLKIT_MODAL: 
      return !paramWindow.isModalExcluded(ModalExclusionType.TOOLKIT_EXCLUDE);
    }
    return false;
  }
  
  void blockWindow(Window paramWindow)
  {
    if (!paramWindow.isModalBlocked())
    {
      paramWindow.setModalBlocked(this, true, true);
      blockedWindows.add(paramWindow);
    }
  }
  
  void blockWindows(List<Window> paramList)
  {
    DialogPeer localDialogPeer = (DialogPeer)peer;
    if (localDialogPeer == null) {
      return;
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Window localWindow = (Window)localIterator.next();
      if (!localWindow.isModalBlocked()) {
        localWindow.setModalBlocked(this, true, false);
      } else {
        localIterator.remove();
      }
    }
    localDialogPeer.blockWindows(paramList);
    blockedWindows.addAll(paramList);
  }
  
  void unblockWindow(Window paramWindow)
  {
    if ((paramWindow.isModalBlocked()) && (blockedWindows.contains(paramWindow)))
    {
      blockedWindows.remove(paramWindow);
      paramWindow.setModalBlocked(this, false, true);
    }
  }
  
  static void checkShouldBeBlocked(Window paramWindow)
  {
    synchronized (paramWindow.getTreeLock())
    {
      for (int i = 0; i < modalDialogs.size(); i++)
      {
        Dialog localDialog = (Dialog)modalDialogs.get(i);
        if (localDialog.shouldBlock(paramWindow))
        {
          localDialog.blockWindow(paramWindow);
          break;
        }
      }
    }
  }
  
  private void checkModalityPermission(ModalityType paramModalityType)
  {
    if (paramModalityType == ModalityType.TOOLKIT_MODAL)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    ModalityType localModalityType = (ModalityType)localGetField.get("modalityType", null);
    try
    {
      checkModalityPermission(localModalityType);
    }
    catch (AccessControlException localAccessControlException)
    {
      localModalityType = DEFAULT_MODALITY_TYPE;
    }
    if (localModalityType == null)
    {
      modal = localGetField.get("modal", false);
      setModal(modal);
    }
    else
    {
      modalityType = localModalityType;
    }
    resizable = localGetField.get("resizable", true);
    undecorated = localGetField.get("undecorated", false);
    title = ((String)localGetField.get("title", ""));
    blockedWindows = new IdentityArrayList();
    SunToolkit.checkAndSetPolicy(this);
    initialized = true;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTDialog();
    }
    return accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  protected class AccessibleAWTDialog
    extends Window.AccessibleAWTWindow
  {
    private static final long serialVersionUID = 4837230331833941201L;
    
    protected AccessibleAWTDialog()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.DIALOG;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getFocusOwner() != null) {
        localAccessibleStateSet.add(AccessibleState.ACTIVE);
      }
      if (isModal()) {
        localAccessibleStateSet.add(AccessibleState.MODAL);
      }
      if (isResizable()) {
        localAccessibleStateSet.add(AccessibleState.RESIZABLE);
      }
      return localAccessibleStateSet;
    }
  }
  
  public static enum ModalExclusionType
  {
    NO_EXCLUDE,  APPLICATION_EXCLUDE,  TOOLKIT_EXCLUDE;
    
    private ModalExclusionType() {}
  }
  
  public static enum ModalityType
  {
    MODELESS,  DOCUMENT_MODAL,  APPLICATION_MODAL,  TOOLKIT_MODAL;
    
    private ModalityType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Dialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */