package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.AccessibleAWTDialog;
import java.awt.Dialog.ModalityType;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.SunToolkit;

public class JDialog
  extends Dialog
  implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler
{
  private static final Object defaultLookAndFeelDecoratedKey = new StringBuffer("JDialog.defaultLookAndFeelDecorated");
  private int defaultCloseOperation = 1;
  protected JRootPane rootPane;
  protected boolean rootPaneCheckingEnabled = false;
  private TransferHandler transferHandler;
  protected AccessibleContext accessibleContext = null;
  
  public JDialog()
  {
    this((Frame)null, false);
  }
  
  public JDialog(Frame paramFrame)
  {
    this(paramFrame, false);
  }
  
  public JDialog(Frame paramFrame, boolean paramBoolean)
  {
    this(paramFrame, "", paramBoolean);
  }
  
  public JDialog(Frame paramFrame, String paramString)
  {
    this(paramFrame, paramString, false);
  }
  
  public JDialog(Frame paramFrame, String paramString, boolean paramBoolean)
  {
    super(paramFrame == null ? SwingUtilities.getSharedOwnerFrame() : paramFrame, paramString, paramBoolean);
    if (paramFrame == null)
    {
      WindowListener localWindowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      addWindowListener(localWindowListener);
    }
    dialogInit();
  }
  
  public JDialog(Frame paramFrame, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramFrame == null ? SwingUtilities.getSharedOwnerFrame() : paramFrame, paramString, paramBoolean, paramGraphicsConfiguration);
    if (paramFrame == null)
    {
      WindowListener localWindowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      addWindowListener(localWindowListener);
    }
    dialogInit();
  }
  
  public JDialog(Dialog paramDialog)
  {
    this(paramDialog, false);
  }
  
  public JDialog(Dialog paramDialog, boolean paramBoolean)
  {
    this(paramDialog, "", paramBoolean);
  }
  
  public JDialog(Dialog paramDialog, String paramString)
  {
    this(paramDialog, paramString, false);
  }
  
  public JDialog(Dialog paramDialog, String paramString, boolean paramBoolean)
  {
    super(paramDialog, paramString, paramBoolean);
    dialogInit();
  }
  
  public JDialog(Dialog paramDialog, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramDialog, paramString, paramBoolean, paramGraphicsConfiguration);
    dialogInit();
  }
  
  public JDialog(Window paramWindow)
  {
    this(paramWindow, Dialog.ModalityType.MODELESS);
  }
  
  public JDialog(Window paramWindow, Dialog.ModalityType paramModalityType)
  {
    this(paramWindow, "", paramModalityType);
  }
  
  public JDialog(Window paramWindow, String paramString)
  {
    this(paramWindow, paramString, Dialog.ModalityType.MODELESS);
  }
  
  public JDialog(Window paramWindow, String paramString, Dialog.ModalityType paramModalityType)
  {
    super(paramWindow, paramString, paramModalityType);
    dialogInit();
  }
  
  public JDialog(Window paramWindow, String paramString, Dialog.ModalityType paramModalityType, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramWindow, paramString, paramModalityType, paramGraphicsConfiguration);
    dialogInit();
  }
  
  protected void dialogInit()
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
        getRootPane().setWindowDecorationStyle(2);
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
      }
    }
  }
  
  public void setDefaultCloseOperation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, or DISPOSE_ON_CLOSE");
    }
    int i = defaultCloseOperation;
    defaultCloseOperation = paramInt;
    firePropertyChange("defaultCloseOperation", i, paramInt);
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
    //   1: getfield 308	javax/swing/JDialog:rootPane	Ljavax/swing/JRootPane;
    //   4: ifnull +11 -> 15
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 308	javax/swing/JDialog:rootPane	Ljavax/swing/JRootPane;
    //   12: invokevirtual 345	javax/swing/JDialog:remove	(Ljava/awt/Component;)V
    //   15: aload_0
    //   16: aload_1
    //   17: putfield 308	javax/swing/JDialog:rootPane	Ljavax/swing/JRootPane;
    //   20: aload_0
    //   21: getfield 308	javax/swing/JDialog:rootPane	Ljavax/swing/JRootPane;
    //   24: ifnull +39 -> 63
    //   27: aload_0
    //   28: invokevirtual 339	javax/swing/JDialog:isRootPaneCheckingEnabled	()Z
    //   31: istore_2
    //   32: aload_0
    //   33: iconst_0
    //   34: invokevirtual 341	javax/swing/JDialog:setRootPaneCheckingEnabled	(Z)V
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 308	javax/swing/JDialog:rootPane	Ljavax/swing/JRootPane;
    //   42: ldc 5
    //   44: invokevirtual 357	javax/swing/JDialog:add	(Ljava/awt/Component;Ljava/lang/Object;)V
    //   47: aload_0
    //   48: iload_2
    //   49: invokevirtual 341	javax/swing/JDialog:setRootPaneCheckingEnabled	(Z)V
    //   52: goto +11 -> 63
    //   55: astore_3
    //   56: aload_0
    //   57: iload_2
    //   58: invokevirtual 341	javax/swing/JDialog:setRootPaneCheckingEnabled	(Z)V
    //   61: aload_3
    //   62: athrow
    //   63: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	this	JDialog
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
      accessibleContext = new AccessibleJDialog();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJDialog
    extends Dialog.AccessibleAWTDialog
  {
    protected AccessibleJDialog()
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
      if (isModal()) {
        localAccessibleStateSet.add(AccessibleState.MODAL);
      }
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */