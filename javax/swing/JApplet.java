package javax.swing;

import java.applet.Applet;
import java.applet.Applet.AccessibleApplet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import sun.awt.SunToolkit;

public class JApplet
  extends Applet
  implements Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler
{
  protected JRootPane rootPane;
  protected boolean rootPaneCheckingEnabled = false;
  private TransferHandler transferHandler;
  protected AccessibleContext accessibleContext = null;
  
  public JApplet()
    throws HeadlessException
  {
    TimerQueue localTimerQueue = TimerQueue.sharedInstance();
    if (localTimerQueue != null) {
      localTimerQueue.startIfNeeded();
    }
    setForeground(Color.black);
    setBackground(Color.white);
    setLocale(JComponent.getDefaultLocale());
    setLayout(new BorderLayout());
    setRootPane(createRootPane());
    setRootPaneCheckingEnabled(true);
    setFocusTraversalPolicyProvider(true);
    SunToolkit.checkAndSetPolicy(this);
    enableEvents(8L);
  }
  
  protected JRootPane createRootPane()
  {
    JRootPane localJRootPane = new JRootPane();
    localJRootPane.setOpaque(true);
    return localJRootPane;
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
    //   1: getfield 203	javax/swing/JApplet:rootPane	Ljavax/swing/JRootPane;
    //   4: ifnull +11 -> 15
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 203	javax/swing/JApplet:rootPane	Ljavax/swing/JRootPane;
    //   12: invokevirtual 226	javax/swing/JApplet:remove	(Ljava/awt/Component;)V
    //   15: aload_0
    //   16: aload_1
    //   17: putfield 203	javax/swing/JApplet:rootPane	Ljavax/swing/JRootPane;
    //   20: aload_0
    //   21: getfield 203	javax/swing/JApplet:rootPane	Ljavax/swing/JRootPane;
    //   24: ifnull +39 -> 63
    //   27: aload_0
    //   28: invokevirtual 220	javax/swing/JApplet:isRootPaneCheckingEnabled	()Z
    //   31: istore_2
    //   32: aload_0
    //   33: iconst_0
    //   34: invokevirtual 223	javax/swing/JApplet:setRootPaneCheckingEnabled	(Z)V
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 203	javax/swing/JApplet:rootPane	Ljavax/swing/JRootPane;
    //   42: ldc 4
    //   44: invokevirtual 234	javax/swing/JApplet:add	(Ljava/awt/Component;Ljava/lang/Object;)V
    //   47: aload_0
    //   48: iload_2
    //   49: invokevirtual 223	javax/swing/JApplet:setRootPaneCheckingEnabled	(Z)V
    //   52: goto +11 -> 63
    //   55: astore_3
    //   56: aload_0
    //   57: iload_2
    //   58: invokevirtual 223	javax/swing/JApplet:setRootPaneCheckingEnabled	(Z)V
    //   61: aload_3
    //   62: athrow
    //   63: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	this	JApplet
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
    String str1 = rootPane != null ? rootPane.toString() : "";
    String str2 = rootPaneCheckingEnabled ? "true" : "false";
    return super.paramString() + ",rootPane=" + str1 + ",rootPaneCheckingEnabled=" + str2;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJApplet();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJApplet
    extends Applet.AccessibleApplet
  {
    protected AccessibleJApplet()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JApplet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */