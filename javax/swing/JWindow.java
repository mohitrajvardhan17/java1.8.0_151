package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.Window.AccessibleAWTWindow;
import java.awt.event.WindowListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import sun.awt.SunToolkit;

public class JWindow
  extends Window
  implements Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler
{
  protected JRootPane rootPane;
  protected boolean rootPaneCheckingEnabled = false;
  private TransferHandler transferHandler;
  protected AccessibleContext accessibleContext = null;
  
  public JWindow()
  {
    this((Frame)null);
  }
  
  public JWindow(GraphicsConfiguration paramGraphicsConfiguration)
  {
    this(null, paramGraphicsConfiguration);
    super.setFocusableWindowState(false);
  }
  
  public JWindow(Frame paramFrame)
  {
    super(paramFrame == null ? SwingUtilities.getSharedOwnerFrame() : paramFrame);
    if (paramFrame == null)
    {
      WindowListener localWindowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      addWindowListener(localWindowListener);
    }
    windowInit();
  }
  
  public JWindow(Window paramWindow)
  {
    super(paramWindow == null ? SwingUtilities.getSharedOwnerFrame() : paramWindow);
    if (paramWindow == null)
    {
      WindowListener localWindowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      addWindowListener(localWindowListener);
    }
    windowInit();
  }
  
  public JWindow(Window paramWindow, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramWindow == null ? SwingUtilities.getSharedOwnerFrame() : paramWindow, paramGraphicsConfiguration);
    if (paramWindow == null)
    {
      WindowListener localWindowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      addWindowListener(localWindowListener);
    }
    windowInit();
  }
  
  protected void windowInit()
  {
    setLocale(JComponent.getDefaultLocale());
    setRootPane(createRootPane());
    setRootPaneCheckingEnabled(true);
    SunToolkit.checkAndSetPolicy(this);
  }
  
  protected JRootPane createRootPane()
  {
    JRootPane localJRootPane = new JRootPane();
    localJRootPane.setOpaque(true);
    return localJRootPane;
  }
  
  protected boolean isRootPaneCheckingEnabled()
  {
    return rootPaneCheckingEnabled;
  }
  
  public void setTransferHandler(TransferHandler paramTransferHandler)
  {
    TransferHandler localTransferHandler = transferHandler;
    transferHandler = paramTransferHandler;
    SwingUtilities.installSwingDropTargetAsNecessary(this, transferHandler);
    firePropertyChange("transferHandler", localTransferHandler, paramTransferHandler);
  }
  
  public TransferHandler getTransferHandler()
  {
    return transferHandler;
  }
  
  public void update(Graphics paramGraphics)
  {
    paint(paramGraphics);
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
    if (paramComponent == rootPane) {
      super.remove(paramComponent);
    } else {
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
  
  public JRootPane getRootPane()
  {
    return rootPane;
  }
  
  /* Error */
  protected void setRootPane(JRootPane paramJRootPane)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 182	javax/swing/JWindow:rootPane	Ljavax/swing/JRootPane;
    //   4: ifnull +11 -> 15
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 182	javax/swing/JWindow:rootPane	Ljavax/swing/JRootPane;
    //   12: invokevirtual 214	javax/swing/JWindow:remove	(Ljava/awt/Component;)V
    //   15: aload_0
    //   16: aload_1
    //   17: putfield 182	javax/swing/JWindow:rootPane	Ljavax/swing/JRootPane;
    //   20: aload_0
    //   21: getfield 182	javax/swing/JWindow:rootPane	Ljavax/swing/JRootPane;
    //   24: ifnull +39 -> 63
    //   27: aload_0
    //   28: invokevirtual 212	javax/swing/JWindow:isRootPaneCheckingEnabled	()Z
    //   31: istore_2
    //   32: aload_0
    //   33: iconst_0
    //   34: invokevirtual 213	javax/swing/JWindow:setRootPaneCheckingEnabled	(Z)V
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 182	javax/swing/JWindow:rootPane	Ljavax/swing/JRootPane;
    //   42: ldc 2
    //   44: invokevirtual 224	javax/swing/JWindow:add	(Ljava/awt/Component;Ljava/lang/Object;)V
    //   47: aload_0
    //   48: iload_2
    //   49: invokevirtual 213	javax/swing/JWindow:setRootPaneCheckingEnabled	(Z)V
    //   52: goto +11 -> 63
    //   55: astore_3
    //   56: aload_0
    //   57: iload_2
    //   58: invokevirtual 213	javax/swing/JWindow:setRootPaneCheckingEnabled	(Z)V
    //   61: aload_3
    //   62: athrow
    //   63: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	this	JWindow
    //   0	64	1	paramJRootPane	JRootPane
    //   31	27	2	bool	boolean
    //   55	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	47	55	finally
  }
  
  public Container getContentPane()
  {
    return getRootPane().getContentPane();
  }
  
  public void setContentPane(Container paramContainer)
  {
    getRootPane().setContentPane(paramContainer);
  }
  
  public JLayeredPane getLayeredPane()
  {
    return getRootPane().getLayeredPane();
  }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane)
  {
    getRootPane().setLayeredPane(paramJLayeredPane);
  }
  
  public Component getGlassPane()
  {
    return getRootPane().getGlassPane();
  }
  
  public void setGlassPane(Component paramComponent)
  {
    getRootPane().setGlassPane(paramComponent);
  }
  
  public Graphics getGraphics()
  {
    JComponent.getGraphicsInvoked(this);
    return super.getGraphics();
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (RepaintManager.HANDLE_TOP_LEVEL_PAINT) {
      RepaintManager.currentManager(this).addDirtyRegion(this, paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      super.repaint(paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected String paramString()
  {
    String str = rootPaneCheckingEnabled ? "true" : "false";
    return super.paramString() + ",rootPaneCheckingEnabled=" + str;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJWindow();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJWindow
    extends Window.AccessibleAWTWindow
  {
    protected AccessibleJWindow()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */