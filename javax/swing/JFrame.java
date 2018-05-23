package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Frame.AccessibleAWTFrame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.SunToolkit;

public class JFrame
  extends Frame
  implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler
{
  public static final int EXIT_ON_CLOSE = 3;
  private static final Object defaultLookAndFeelDecoratedKey = new StringBuffer("JFrame.defaultLookAndFeelDecorated");
  private int defaultCloseOperation = 1;
  private TransferHandler transferHandler;
  protected JRootPane rootPane;
  protected boolean rootPaneCheckingEnabled = false;
  protected AccessibleContext accessibleContext = null;
  
  public JFrame()
    throws HeadlessException
  {
    frameInit();
  }
  
  public JFrame(GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramGraphicsConfiguration);
    frameInit();
  }
  
  public JFrame(String paramString)
    throws HeadlessException
  {
    super(paramString);
    frameInit();
  }
  
  public JFrame(String paramString, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramString, paramGraphicsConfiguration);
    frameInit();
  }
  
  protected void frameInit()
  {
    enableEvents(72L);
    setLocale(JComponent.getDefaultLocale());
    setRootPane(createRootPane());
    setBackground(UIManager.getColor("control"));
    setRootPaneCheckingEnabled(true);
    if (isDefaultLookAndFeelDecorated())
    {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool)
      {
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(1);
      }
    }
    SunToolkit.checkAndSetPolicy(this);
  }
  
  protected JRootPane createRootPane()
  {
    JRootPane localJRootPane = new JRootPane();
    localJRootPane.setOpaque(true);
    return localJRootPane;
  }
  
  protected void processWindowEvent(WindowEvent paramWindowEvent)
  {
    super.processWindowEvent(paramWindowEvent);
    if (paramWindowEvent.getID() == 201) {
      switch (defaultCloseOperation)
      {
      case 1: 
        setVisible(false);
        break;
      case 2: 
        dispose();
        break;
      case 3: 
        System.exit(0);
        break;
      }
    }
  }
  
  public void setDefaultCloseOperation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2) && (paramInt != 3)) {
      throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, or EXIT_ON_CLOSE");
    }
    if (paramInt == 3)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkExit(0);
      }
    }
    if (defaultCloseOperation != paramInt)
    {
      int i = defaultCloseOperation;
      defaultCloseOperation = paramInt;
      firePropertyChange("defaultCloseOperation", i, paramInt);
    }
  }
  
  public int getDefaultCloseOperation()
  {
    return defaultCloseOperation;
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
  
  public void setJMenuBar(JMenuBar paramJMenuBar)
  {
    getRootPane().setMenuBar(paramJMenuBar);
  }
  
  public JMenuBar getJMenuBar()
  {
    return getRootPane().getMenuBar();
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
    //   1: getfield 290	javax/swing/JFrame:rootPane	Ljavax/swing/JRootPane;
    //   4: ifnull +11 -> 15
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 290	javax/swing/JFrame:rootPane	Ljavax/swing/JRootPane;
    //   12: invokevirtual 329	javax/swing/JFrame:remove	(Ljava/awt/Component;)V
    //   15: aload_0
    //   16: aload_1
    //   17: putfield 290	javax/swing/JFrame:rootPane	Ljavax/swing/JRootPane;
    //   20: aload_0
    //   21: getfield 290	javax/swing/JFrame:rootPane	Ljavax/swing/JRootPane;
    //   24: ifnull +39 -> 63
    //   27: aload_0
    //   28: invokevirtual 323	javax/swing/JFrame:isRootPaneCheckingEnabled	()Z
    //   31: istore_2
    //   32: aload_0
    //   33: iconst_0
    //   34: invokevirtual 325	javax/swing/JFrame:setRootPaneCheckingEnabled	(Z)V
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 290	javax/swing/JFrame:rootPane	Ljavax/swing/JRootPane;
    //   42: ldc 5
    //   44: invokevirtual 337	javax/swing/JFrame:add	(Ljava/awt/Component;Ljava/lang/Object;)V
    //   47: aload_0
    //   48: iload_2
    //   49: invokevirtual 325	javax/swing/JFrame:setRootPaneCheckingEnabled	(Z)V
    //   52: goto +11 -> 63
    //   55: astore_3
    //   56: aload_0
    //   57: iload_2
    //   58: invokevirtual 325	javax/swing/JFrame:setRootPaneCheckingEnabled	(Z)V
    //   61: aload_3
    //   62: athrow
    //   63: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	this	JFrame
    //   0	64	1	paramJRootPane	JRootPane
    //   31	27	2	bool	boolean
    //   55	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	47	55	finally
  }
  
  public void setIconImage(Image paramImage)
  {
    super.setIconImage(paramImage);
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
  
  public static void setDefaultLookAndFeelDecorated(boolean paramBoolean)
  {
    if (paramBoolean) {
      SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.TRUE);
    } else {
      SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.FALSE);
    }
  }
  
  public static boolean isDefaultLookAndFeelDecorated()
  {
    Boolean localBoolean = (Boolean)SwingUtilities.appContextGet(defaultLookAndFeelDecoratedKey);
    if (localBoolean == null) {
      localBoolean = Boolean.FALSE;
    }
    return localBoolean.booleanValue();
  }
  
  protected String paramString()
  {
    String str1;
    if (defaultCloseOperation == 1) {
      str1 = "HIDE_ON_CLOSE";
    } else if (defaultCloseOperation == 2) {
      str1 = "DISPOSE_ON_CLOSE";
    } else if (defaultCloseOperation == 0) {
      str1 = "DO_NOTHING_ON_CLOSE";
    } else if (defaultCloseOperation == 3) {
      str1 = "EXIT_ON_CLOSE";
    } else {
      str1 = "";
    }
    String str2 = rootPane != null ? rootPane.toString() : "";
    String str3 = rootPaneCheckingEnabled ? "true" : "false";
    return super.paramString() + ",defaultCloseOperation=" + str1 + ",rootPane=" + str2 + ",rootPaneCheckingEnabled=" + str3;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJFrame();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJFrame
    extends Frame.AccessibleAWTFrame
  {
    protected AccessibleJFrame()
    {
      super();
    }
    
    public String getAccessibleName()
    {
      if (accessibleName != null) {
        return accessibleName;
      }
      if (getTitle() == null) {
        return super.getAccessibleName();
      }
      return getTitle();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (isResizable()) {
        localAccessibleStateSet.add(AccessibleState.RESIZABLE);
      }
      if (getFocusOwner() != null) {
        localAccessibleStateSet.add(AccessibleState.ACTIVE);
      }
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */