package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.FramePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.FrameAccessor;
import sun.awt.SunToolkit;

public class Frame
  extends Window
  implements MenuContainer
{
  @Deprecated
  public static final int DEFAULT_CURSOR = 0;
  @Deprecated
  public static final int CROSSHAIR_CURSOR = 1;
  @Deprecated
  public static final int TEXT_CURSOR = 2;
  @Deprecated
  public static final int WAIT_CURSOR = 3;
  @Deprecated
  public static final int SW_RESIZE_CURSOR = 4;
  @Deprecated
  public static final int SE_RESIZE_CURSOR = 5;
  @Deprecated
  public static final int NW_RESIZE_CURSOR = 6;
  @Deprecated
  public static final int NE_RESIZE_CURSOR = 7;
  @Deprecated
  public static final int N_RESIZE_CURSOR = 8;
  @Deprecated
  public static final int S_RESIZE_CURSOR = 9;
  @Deprecated
  public static final int W_RESIZE_CURSOR = 10;
  @Deprecated
  public static final int E_RESIZE_CURSOR = 11;
  @Deprecated
  public static final int HAND_CURSOR = 12;
  @Deprecated
  public static final int MOVE_CURSOR = 13;
  public static final int NORMAL = 0;
  public static final int ICONIFIED = 1;
  public static final int MAXIMIZED_HORIZ = 2;
  public static final int MAXIMIZED_VERT = 4;
  public static final int MAXIMIZED_BOTH = 6;
  Rectangle maximizedBounds;
  String title = "Untitled";
  MenuBar menuBar;
  boolean resizable = true;
  boolean undecorated = false;
  boolean mbManagement = false;
  private int state = 0;
  Vector<Window> ownedWindows;
  private static final String base = "frame";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 2673458971256075116L;
  private int frameSerializedDataVersion = 1;
  
  public Frame()
    throws HeadlessException
  {
    this("");
  }
  
  public Frame(GraphicsConfiguration paramGraphicsConfiguration)
  {
    this("", paramGraphicsConfiguration);
  }
  
  public Frame(String paramString)
    throws HeadlessException
  {
    init(paramString, null);
  }
  
  public Frame(String paramString, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramGraphicsConfiguration);
    init(paramString, paramGraphicsConfiguration);
  }
  
  private void init(String paramString, GraphicsConfiguration paramGraphicsConfiguration)
  {
    title = paramString;
    SunToolkit.checkAndSetPolicy(this);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 19
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 245	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 444	java/lang/StringBuilder:<init>	()V
    //   12: ldc 15
    //   14: invokevirtual 447	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 374	java/awt/Frame:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 374	java/awt/Frame:nameCounter	I
    //   26: invokevirtual 446	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 445	java/lang/StringBuilder:toString	()Ljava/lang/String;
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
    //   0	40	0	this	Frame
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
      if (peer == null) {
        peer = getToolkit().createFrame(this);
      }
      FramePeer localFramePeer = (FramePeer)peer;
      MenuBar localMenuBar = menuBar;
      if (localMenuBar != null)
      {
        mbManagement = true;
        localMenuBar.addNotify();
        localFramePeer.setMenuBar(localMenuBar);
      }
      localFramePeer.setMaximizedBounds(maximizedBounds);
      super.addNotify();
    }
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public void setTitle(String paramString)
  {
    String str = title;
    if (paramString == null) {
      paramString = "";
    }
    synchronized (this)
    {
      title = paramString;
      FramePeer localFramePeer = (FramePeer)peer;
      if (localFramePeer != null) {
        localFramePeer.setTitle(paramString);
      }
    }
    firePropertyChange("title", str, paramString);
  }
  
  public Image getIconImage()
  {
    List localList = icons;
    if ((localList != null) && (localList.size() > 0)) {
      return (Image)localList.get(0);
    }
    return null;
  }
  
  public void setIconImage(Image paramImage)
  {
    super.setIconImage(paramImage);
  }
  
  public MenuBar getMenuBar()
  {
    return menuBar;
  }
  
  public void setMenuBar(MenuBar paramMenuBar)
  {
    synchronized (getTreeLock())
    {
      if (menuBar == paramMenuBar) {
        return;
      }
      if ((paramMenuBar != null) && (parent != null)) {
        parent.remove(paramMenuBar);
      }
      if (menuBar != null) {
        remove(menuBar);
      }
      menuBar = paramMenuBar;
      if (menuBar != null)
      {
        menuBar.parent = this;
        FramePeer localFramePeer = (FramePeer)peer;
        if (localFramePeer != null)
        {
          mbManagement = true;
          menuBar.addNotify();
          invalidateIfValid();
          localFramePeer.setMenuBar(menuBar);
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
    boolean bool = resizable;
    int i = 0;
    synchronized (this)
    {
      resizable = paramBoolean;
      FramePeer localFramePeer = (FramePeer)peer;
      if (localFramePeer != null)
      {
        localFramePeer.setResizable(paramBoolean);
        i = 1;
      }
    }
    if (i != 0) {
      invalidateIfValid();
    }
    firePropertyChange("resizable", bool, paramBoolean);
  }
  
  public synchronized void setState(int paramInt)
  {
    int i = getExtendedState();
    if ((paramInt == 1) && ((i & 0x1) == 0)) {
      setExtendedState(i | 0x1);
    } else if ((paramInt == 0) && ((i & 0x1) != 0)) {
      setExtendedState(i & 0xFFFFFFFE);
    }
  }
  
  public void setExtendedState(int paramInt)
  {
    if (!isFrameStateSupported(paramInt)) {
      return;
    }
    synchronized (getObjectLock())
    {
      state = paramInt;
    }
    ??? = (FramePeer)peer;
    if (??? != null) {
      ((FramePeer)???).setState(paramInt);
    }
  }
  
  private boolean isFrameStateSupported(int paramInt)
  {
    if (!getToolkit().isFrameStateSupported(paramInt))
    {
      if (((paramInt & 0x1) != 0) && (!getToolkit().isFrameStateSupported(1))) {
        return false;
      }
      paramInt &= 0xFFFFFFFE;
      return getToolkit().isFrameStateSupported(paramInt);
    }
    return true;
  }
  
  public synchronized int getState()
  {
    return (getExtendedState() & 0x1) != 0 ? 1 : 0;
  }
  
  /* Error */
  public int getExtendedState()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 408	java/awt/Frame:getObjectLock	()Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 375	java/awt/Frame:state	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Frame
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void setMaximizedBounds(Rectangle paramRectangle)
  {
    synchronized (getObjectLock())
    {
      maximizedBounds = paramRectangle;
    }
    ??? = (FramePeer)peer;
    if (??? != null) {
      ((FramePeer)???).setMaximizedBounds(paramRectangle);
    }
  }
  
  /* Error */
  public Rectangle getMaximizedBounds()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 408	java/awt/Frame:getObjectLock	()Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 380	java/awt/Frame:maximizedBounds	Ljava/awt/Rectangle;
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
    //   0	19	0	this	Frame
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void setUndecorated(boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if (isDisplayable()) {
        throw new IllegalComponentStateException("The frame is displayable.");
      }
      if (!paramBoolean)
      {
        if (getOpacity() < 1.0F) {
          throw new IllegalComponentStateException("The frame is not opaque");
        }
        if (getShape() != null) {
          throw new IllegalComponentStateException("The frame does not have a default shape");
        }
        Color localColor = getBackground();
        if ((localColor != null) && (localColor.getAlpha() < 255)) {
          throw new IllegalComponentStateException("The frame background color is not opaque");
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
        throw new IllegalComponentStateException("The frame is decorated");
      }
      super.setOpacity(paramFloat);
    }
  }
  
  public void setShape(Shape paramShape)
  {
    synchronized (getTreeLock())
    {
      if ((paramShape != null) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The frame is decorated");
      }
      super.setShape(paramShape);
    }
  }
  
  public void setBackground(Color paramColor)
  {
    synchronized (getTreeLock())
    {
      if ((paramColor != null) && (paramColor.getAlpha() < 255) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The frame is decorated");
      }
      super.setBackground(paramColor);
    }
  }
  
  public void remove(MenuComponent paramMenuComponent)
  {
    if (paramMenuComponent == null) {
      return;
    }
    synchronized (getTreeLock())
    {
      if (paramMenuComponent == menuBar)
      {
        menuBar = null;
        FramePeer localFramePeer = (FramePeer)peer;
        if (localFramePeer != null)
        {
          mbManagement = true;
          invalidateIfValid();
          localFramePeer.setMenuBar(null);
          paramMenuComponent.removeNotify();
        }
        parent = null;
      }
      else
      {
        super.remove(paramMenuComponent);
      }
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      FramePeer localFramePeer = (FramePeer)peer;
      if (localFramePeer != null)
      {
        getState();
        if (menuBar != null)
        {
          mbManagement = true;
          localFramePeer.setMenuBar(null);
          menuBar.removeNotify();
        }
      }
      super.removeNotify();
    }
  }
  
  void postProcessKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((menuBar != null) && (menuBar.handleShortcut(paramKeyEvent)))
    {
      paramKeyEvent.consume();
      return;
    }
    super.postProcessKeyEvent(paramKeyEvent);
  }
  
  protected String paramString()
  {
    String str = super.paramString();
    if (title != null) {
      str = str + ",title=" + title;
    }
    if (resizable) {
      str = str + ",resizable";
    }
    int i = getExtendedState();
    if (i == 0)
    {
      str = str + ",normal";
    }
    else
    {
      if ((i & 0x1) != 0) {
        str = str + ",iconified";
      }
      if ((i & 0x6) == 6) {
        str = str + ",maximized";
      } else if ((i & 0x2) != 0) {
        str = str + ",maximized_horiz";
      } else if ((i & 0x4) != 0) {
        str = str + ",maximized_vert";
      }
    }
    return str;
  }
  
  @Deprecated
  public void setCursor(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 13)) {
      throw new IllegalArgumentException("illegal cursor type");
    }
    setCursor(Cursor.getPredefinedCursor(paramInt));
  }
  
  @Deprecated
  public int getCursorType()
  {
    return getCursor().getType();
  }
  
  public static Frame[] getFrames()
  {
    Window[] arrayOfWindow1 = Window.getWindows();
    int i = 0;
    for (Object localObject2 : arrayOfWindow1) {
      if ((localObject2 instanceof Frame)) {
        i++;
      }
    }
    ??? = new Frame[i];
    ??? = 0;
    for (Window localWindow : arrayOfWindow1) {
      if ((localWindow instanceof Frame)) {
        ???[(???++)] = ((Frame)localWindow);
      }
    }
    return (Frame[])???;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if ((icons != null) && (icons.size() > 0))
    {
      Image localImage = (Image)icons.get(0);
      if ((localImage instanceof Serializable))
      {
        paramObjectOutputStream.writeObject(localImage);
        return;
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      Image localImage = (Image)paramObjectInputStream.readObject();
      if (icons == null)
      {
        icons = new ArrayList();
        icons.add(localImage);
      }
    }
    catch (OptionalDataException localOptionalDataException)
    {
      if (!eof) {
        throw localOptionalDataException;
      }
    }
    if (menuBar != null) {
      menuBar.parent = this;
    }
    if (ownedWindows != null)
    {
      for (int i = 0; i < ownedWindows.size(); i++) {
        connectOwnedWindow((Window)ownedWindows.elementAt(i));
      }
      ownedWindows = null;
    }
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTFrame();
    }
    return accessibleContext;
  }
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setFrameAccessor(new AWTAccessor.FrameAccessor()
    {
      public void setExtendedState(Frame paramAnonymousFrame, int paramAnonymousInt)
      {
        synchronized (paramAnonymousFrame.getObjectLock())
        {
          state = paramAnonymousInt;
        }
      }
      
      /* Error */
      public int getExtendedState(Frame paramAnonymousFrame)
      {
        // Byte code:
        //   0: aload_1
        //   1: invokevirtual 40	java/awt/Frame:getObjectLock	()Ljava/lang/Object;
        //   4: dup
        //   5: astore_2
        //   6: monitorenter
        //   7: aload_1
        //   8: invokestatic 38	java/awt/Frame:access$000	(Ljava/awt/Frame;)I
        //   11: aload_2
        //   12: monitorexit
        //   13: ireturn
        //   14: astore_3
        //   15: aload_2
        //   16: monitorexit
        //   17: aload_3
        //   18: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	19	0	this	1
        //   0	19	1	paramAnonymousFrame	Frame
        //   5	11	2	Ljava/lang/Object;	Object
        //   14	4	3	localObject1	Object
        // Exception table:
        //   from	to	target	type
        //   7	13	14	finally
        //   14	17	14	finally
      }
      
      /* Error */
      public Rectangle getMaximizedBounds(Frame paramAnonymousFrame)
      {
        // Byte code:
        //   0: aload_1
        //   1: invokevirtual 40	java/awt/Frame:getObjectLock	()Ljava/lang/Object;
        //   4: dup
        //   5: astore_2
        //   6: monitorenter
        //   7: aload_1
        //   8: getfield 37	java/awt/Frame:maximizedBounds	Ljava/awt/Rectangle;
        //   11: aload_2
        //   12: monitorexit
        //   13: areturn
        //   14: astore_3
        //   15: aload_2
        //   16: monitorexit
        //   17: aload_3
        //   18: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	19	0	this	1
        //   0	19	1	paramAnonymousFrame	Frame
        //   5	11	2	Ljava/lang/Object;	Object
        //   14	4	3	localObject1	Object
        // Exception table:
        //   from	to	target	type
        //   7	13	14	finally
        //   14	17	14	finally
      }
    });
  }
  
  protected class AccessibleAWTFrame
    extends Window.AccessibleAWTWindow
  {
    private static final long serialVersionUID = -6172960752956030250L;
    
    protected AccessibleAWTFrame()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.FRAME;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getFocusOwner() != null) {
        localAccessibleStateSet.add(AccessibleState.ACTIVE);
      }
      if (isResizable()) {
        localAccessibleStateSet.add(AccessibleState.RESIZABLE);
      }
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Frame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */