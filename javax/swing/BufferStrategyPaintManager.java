package javax.swing;

import com.sun.java.swing.SwingUtilities3;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.awt.SubRegionShowable;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

class BufferStrategyPaintManager
  extends RepaintManager.PaintManager
{
  private static Method COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
  private static Method COMPONENT_GET_BUFFER_STRATEGY_METHOD;
  private static final PlatformLogger LOGGER = PlatformLogger.getLogger("javax.swing.BufferStrategyPaintManager");
  private ArrayList<BufferInfo> bufferInfos = new ArrayList(1);
  private boolean painting;
  private boolean showing;
  private int accumulatedX;
  private int accumulatedY;
  private int accumulatedMaxX;
  private int accumulatedMaxY;
  private JComponent rootJ;
  private int xOffset;
  private int yOffset;
  private Graphics bsg;
  private BufferStrategy bufferStrategy;
  private BufferInfo bufferInfo;
  private boolean disposeBufferOnEnd;
  
  private static Method getGetBufferStrategyMethod()
  {
    if (COMPONENT_GET_BUFFER_STRATEGY_METHOD == null) {
      getMethods();
    }
    return COMPONENT_GET_BUFFER_STRATEGY_METHOD;
  }
  
  private static Method getCreateBufferStrategyMethod()
  {
    if (COMPONENT_CREATE_BUFFER_STRATEGY_METHOD == null) {
      getMethods();
    }
    return COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
  }
  
  private static void getMethods()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          BufferStrategyPaintManager.access$002(Component.class.getDeclaredMethod("createBufferStrategy", new Class[] { Integer.TYPE, BufferCapabilities.class }));
          BufferStrategyPaintManager.COMPONENT_CREATE_BUFFER_STRATEGY_METHOD.setAccessible(true);
          BufferStrategyPaintManager.access$102(Component.class.getDeclaredMethod("getBufferStrategy", new Class[0]));
          BufferStrategyPaintManager.COMPONENT_GET_BUFFER_STRATEGY_METHOD.setAccessible(true);
        }
        catch (SecurityException localSecurityException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
        return null;
      }
    });
  }
  
  BufferStrategyPaintManager() {}
  
  protected void dispose()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList;
        synchronized (BufferStrategyPaintManager.this)
        {
          while (showing) {
            try
            {
              wait();
            }
            catch (InterruptedException localInterruptedException) {}
          }
          localArrayList = bufferInfos;
          bufferInfos = null;
        }
        BufferStrategyPaintManager.this.dispose(localArrayList);
      }
    });
  }
  
  private void dispose(List<BufferInfo> paramList)
  {
    if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
      LOGGER.finer("BufferStrategyPaintManager disposed", new RuntimeException());
    }
    if (paramList != null)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        BufferInfo localBufferInfo = (BufferInfo)localIterator.next();
        localBufferInfo.dispose();
      }
    }
  }
  
  public boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (this)
    {
      if (painting) {
        return false;
      }
      showing = true;
    }
    try
    {
      ??? = getBufferInfo(paramContainer);
      BufferStrategy localBufferStrategy;
      if ((??? != null) && (((BufferInfo)???).isInSync()) && ((localBufferStrategy = ((BufferInfo)???).getBufferStrategy(false)) != null))
      {
        SubRegionShowable localSubRegionShowable = (SubRegionShowable)localBufferStrategy;
        boolean bool1 = ((BufferInfo)???).getPaintAllOnExpose();
        ((BufferInfo)???).setPaintAllOnExpose(false);
        if (localSubRegionShowable.showIfNotLost(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4))
        {
          boolean bool2 = !bool1;
          return bool2;
        }
        bufferInfo.setContentsLostDuringExpose(true);
      }
    }
    finally
    {
      synchronized (this)
      {
        showing = false;
        notifyAll();
      }
    }
    return false;
  }
  
  public boolean paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Container localContainer = fetchRoot(paramJComponent1);
    if (prepare(paramJComponent1, localContainer, true, paramInt1, paramInt2, paramInt3, paramInt4))
    {
      if (((paramGraphics instanceof SunGraphics2D)) && (((SunGraphics2D)paramGraphics).getDestination() == localContainer))
      {
        int i = bsg).constrainX;
        int j = bsg).constrainY;
        if ((i != 0) || (j != 0)) {
          bsg.translate(-i, -j);
        }
        ((SunGraphics2D)bsg).constrain(xOffset + i, yOffset + j, paramInt1 + paramInt3, paramInt2 + paramInt4);
        bsg.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
        paramJComponent1.paintToOffscreen(bsg, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
        accumulate(xOffset + paramInt1, yOffset + paramInt2, paramInt3, paramInt4);
        return true;
      }
      bufferInfo.setInSync(false);
    }
    if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
      LOGGER.finer("prepare failed");
    }
    return super.paint(paramJComponent1, paramJComponent2, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    Container localContainer = fetchRoot(paramJComponent);
    if ((prepare(paramJComponent, localContainer, false, 0, 0, 0, 0)) && (bufferInfo.isInSync()))
    {
      if (paramBoolean)
      {
        Rectangle localRectangle = paramJComponent.getVisibleRect();
        int i = xOffset + paramInt1;
        int j = yOffset + paramInt2;
        bsg.clipRect(xOffset + x, yOffset + y, width, height);
        bsg.copyArea(i, j, paramInt3, paramInt4, paramInt5, paramInt6);
      }
      else
      {
        bsg.copyArea(xOffset + paramInt1, yOffset + paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
      accumulate(paramInt1 + xOffset + paramInt5, paramInt2 + yOffset + paramInt6, paramInt3, paramInt4);
    }
    else
    {
      if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
        LOGGER.finer("copyArea: prepare failed or not in sync");
      }
      if (!flushAccumulatedRegion()) {
        rootJ.repaint();
      } else {
        super.copyArea(paramJComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
      }
    }
  }
  
  public void beginPaint()
  {
    synchronized (this)
    {
      painting = true;
      while (showing) {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    if (LOGGER.isLoggable(PlatformLogger.Level.FINEST)) {
      LOGGER.finest("beginPaint");
    }
    resetAccumulated();
  }
  
  public void endPaint()
  {
    if (LOGGER.isLoggable(PlatformLogger.Level.FINEST)) {
      LOGGER.finest("endPaint: region " + accumulatedX + " " + accumulatedY + " " + accumulatedMaxX + " " + accumulatedMaxY);
    }
    if ((painting) && (!flushAccumulatedRegion())) {
      if (!isRepaintingRoot())
      {
        repaintRoot(rootJ);
      }
      else
      {
        resetDoubleBufferPerWindow();
        rootJ.repaint();
      }
    }
    BufferInfo localBufferInfo = null;
    synchronized (this)
    {
      painting = false;
      if (disposeBufferOnEnd)
      {
        disposeBufferOnEnd = false;
        localBufferInfo = bufferInfo;
        bufferInfos.remove(localBufferInfo);
      }
    }
    if (localBufferInfo != null) {
      localBufferInfo.dispose();
    }
  }
  
  private boolean flushAccumulatedRegion()
  {
    boolean bool1 = true;
    if (accumulatedX != Integer.MAX_VALUE)
    {
      SubRegionShowable localSubRegionShowable = (SubRegionShowable)bufferStrategy;
      boolean bool2 = bufferStrategy.contentsLost();
      if (!bool2)
      {
        localSubRegionShowable.show(accumulatedX, accumulatedY, accumulatedMaxX, accumulatedMaxY);
        bool2 = bufferStrategy.contentsLost();
      }
      if (bool2)
      {
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
          LOGGER.finer("endPaint: contents lost");
        }
        bufferInfo.setInSync(false);
        bool1 = false;
      }
    }
    resetAccumulated();
    return bool1;
  }
  
  private void resetAccumulated()
  {
    accumulatedX = Integer.MAX_VALUE;
    accumulatedY = Integer.MAX_VALUE;
    accumulatedMaxX = 0;
    accumulatedMaxY = 0;
  }
  
  public void doubleBufferingChanged(final JRootPane paramJRootPane)
  {
    if (((!paramJRootPane.isDoubleBuffered()) || (!paramJRootPane.getUseTrueDoubleBuffering())) && (paramJRootPane.getParent() != null)) {
      if (!SwingUtilities.isEventDispatchThread())
      {
        Runnable local3 = new Runnable()
        {
          public void run()
          {
            BufferStrategyPaintManager.this.doubleBufferingChanged0(paramJRootPane);
          }
        };
        SwingUtilities.invokeLater(local3);
      }
      else
      {
        doubleBufferingChanged0(paramJRootPane);
      }
    }
  }
  
  private void doubleBufferingChanged0(JRootPane paramJRootPane)
  {
    BufferInfo localBufferInfo;
    synchronized (this)
    {
      while (showing) {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      localBufferInfo = getBufferInfo(paramJRootPane.getParent());
      if ((painting) && (bufferInfo == localBufferInfo))
      {
        disposeBufferOnEnd = true;
        localBufferInfo = null;
      }
      else if (localBufferInfo != null)
      {
        bufferInfos.remove(localBufferInfo);
      }
    }
    if (localBufferInfo != null) {
      localBufferInfo.dispose();
    }
  }
  
  private boolean prepare(JComponent paramJComponent, Container paramContainer, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (bsg != null)
    {
      bsg.dispose();
      bsg = null;
    }
    bufferStrategy = null;
    if (paramContainer != null)
    {
      int i = 0;
      BufferInfo localBufferInfo = getBufferInfo(paramContainer);
      if (localBufferInfo == null)
      {
        i = 1;
        localBufferInfo = new BufferInfo(paramContainer);
        bufferInfos.add(localBufferInfo);
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
          LOGGER.finer("prepare: new BufferInfo: " + paramContainer);
        }
      }
      bufferInfo = localBufferInfo;
      if (!localBufferInfo.hasBufferStrategyChanged())
      {
        bufferStrategy = localBufferInfo.getBufferStrategy(true);
        if (bufferStrategy != null)
        {
          bsg = bufferStrategy.getDrawGraphics();
          if (bufferStrategy.contentsRestored())
          {
            i = 1;
            if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
              LOGGER.finer("prepare: contents restored in prepare");
            }
          }
        }
        else
        {
          return false;
        }
        if (localBufferInfo.getContentsLostDuringExpose())
        {
          i = 1;
          localBufferInfo.setContentsLostDuringExpose(false);
          if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
            LOGGER.finer("prepare: contents lost on expose");
          }
        }
        if ((paramBoolean) && (paramJComponent == rootJ) && (paramInt1 == 0) && (paramInt2 == 0) && (paramJComponent.getWidth() == paramInt3) && (paramJComponent.getHeight() == paramInt4))
        {
          localBufferInfo.setInSync(true);
        }
        else if (i != 0)
        {
          localBufferInfo.setInSync(false);
          if (!isRepaintingRoot()) {
            repaintRoot(rootJ);
          } else {
            resetDoubleBufferPerWindow();
          }
        }
        return bufferInfos != null;
      }
    }
    return false;
  }
  
  private Container fetchRoot(JComponent paramJComponent)
  {
    int i = 0;
    rootJ = paramJComponent;
    Object localObject = paramJComponent;
    xOffset = (yOffset = 0);
    while ((localObject != null) && (!(localObject instanceof Window)) && (!SunToolkit.isInstanceOf(localObject, "java.applet.Applet")))
    {
      xOffset += ((Container)localObject).getX();
      yOffset += ((Container)localObject).getY();
      localObject = ((Container)localObject).getParent();
      if (localObject != null) {
        if ((localObject instanceof JComponent)) {
          rootJ = ((JComponent)localObject);
        } else if (!((Container)localObject).isLightweight()) {
          if (i == 0) {
            i = 1;
          } else {
            return null;
          }
        }
      }
    }
    if (((localObject instanceof RootPaneContainer)) && ((rootJ instanceof JRootPane)) && (rootJ.isDoubleBuffered()) && (((JRootPane)rootJ).getUseTrueDoubleBuffering())) {
      return (Container)localObject;
    }
    return null;
  }
  
  private void resetDoubleBufferPerWindow()
  {
    if (bufferInfos != null)
    {
      dispose(bufferInfos);
      bufferInfos = null;
      repaintManager.setPaintManager(null);
    }
  }
  
  private BufferInfo getBufferInfo(Container paramContainer)
  {
    for (int i = bufferInfos.size() - 1; i >= 0; i--)
    {
      BufferInfo localBufferInfo = (BufferInfo)bufferInfos.get(i);
      Container localContainer = localBufferInfo.getRoot();
      if (localContainer == null)
      {
        bufferInfos.remove(i);
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
          LOGGER.finer("BufferInfo pruned, root null");
        }
      }
      else if (localContainer == paramContainer)
      {
        return localBufferInfo;
      }
    }
    return null;
  }
  
  private void accumulate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    accumulatedX = Math.min(paramInt1, accumulatedX);
    accumulatedY = Math.min(paramInt2, accumulatedY);
    accumulatedMaxX = Math.max(accumulatedMaxX, paramInt1 + paramInt3);
    accumulatedMaxY = Math.max(accumulatedMaxY, paramInt2 + paramInt4);
  }
  
  private class BufferInfo
    extends ComponentAdapter
    implements WindowListener
  {
    private WeakReference<BufferStrategy> weakBS;
    private WeakReference<Container> root;
    private boolean inSync;
    private boolean contentsLostDuringExpose;
    private boolean paintAllOnExpose;
    
    public BufferInfo(Container paramContainer)
    {
      root = new WeakReference(paramContainer);
      paramContainer.addComponentListener(this);
      if ((paramContainer instanceof Window)) {
        ((Window)paramContainer).addWindowListener(this);
      }
    }
    
    public void setPaintAllOnExpose(boolean paramBoolean)
    {
      paintAllOnExpose = paramBoolean;
    }
    
    public boolean getPaintAllOnExpose()
    {
      return paintAllOnExpose;
    }
    
    public void setContentsLostDuringExpose(boolean paramBoolean)
    {
      contentsLostDuringExpose = paramBoolean;
    }
    
    public boolean getContentsLostDuringExpose()
    {
      return contentsLostDuringExpose;
    }
    
    public void setInSync(boolean paramBoolean)
    {
      inSync = paramBoolean;
    }
    
    public boolean isInSync()
    {
      return inSync;
    }
    
    public Container getRoot()
    {
      return root == null ? null : (Container)root.get();
    }
    
    public BufferStrategy getBufferStrategy(boolean paramBoolean)
    {
      BufferStrategy localBufferStrategy = weakBS == null ? null : (BufferStrategy)weakBS.get();
      if ((localBufferStrategy == null) && (paramBoolean))
      {
        localBufferStrategy = createBufferStrategy();
        if (localBufferStrategy != null) {
          weakBS = new WeakReference(localBufferStrategy);
        }
        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
          BufferStrategyPaintManager.LOGGER.finer("getBufferStrategy: created bs: " + localBufferStrategy);
        }
      }
      return localBufferStrategy;
    }
    
    public boolean hasBufferStrategyChanged()
    {
      Container localContainer = getRoot();
      if (localContainer != null)
      {
        BufferStrategy localBufferStrategy1 = null;
        BufferStrategy localBufferStrategy2 = null;
        localBufferStrategy1 = getBufferStrategy(false);
        if ((localContainer instanceof Window)) {
          localBufferStrategy2 = ((Window)localContainer).getBufferStrategy();
        } else {
          try
          {
            localBufferStrategy2 = (BufferStrategy)BufferStrategyPaintManager.access$700().invoke(localContainer, new Object[0]);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            if (!$assertionsDisabled) {
              throw new AssertionError();
            }
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            if (!$assertionsDisabled) {
              throw new AssertionError();
            }
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            if (!$assertionsDisabled) {
              throw new AssertionError();
            }
          }
        }
        if (localBufferStrategy2 != localBufferStrategy1)
        {
          if (localBufferStrategy1 != null) {
            localBufferStrategy1.dispose();
          }
          weakBS = null;
          return true;
        }
      }
      return false;
    }
    
    private BufferStrategy createBufferStrategy()
    {
      Container localContainer = getRoot();
      if (localContainer == null) {
        return null;
      }
      BufferStrategy localBufferStrategy = null;
      if (SwingUtilities3.isVsyncRequested(localContainer))
      {
        localBufferStrategy = createBufferStrategy(localContainer, true);
        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
          BufferStrategyPaintManager.LOGGER.finer("createBufferStrategy: using vsynced strategy");
        }
      }
      if (localBufferStrategy == null) {
        localBufferStrategy = createBufferStrategy(localContainer, false);
      }
      if (!(localBufferStrategy instanceof SubRegionShowable)) {
        localBufferStrategy = null;
      }
      return localBufferStrategy;
    }
    
    private BufferStrategy createBufferStrategy(Container paramContainer, boolean paramBoolean)
    {
      Object localObject;
      if (paramBoolean) {
        localObject = new ExtendedBufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.COPIED, ExtendedBufferCapabilities.VSyncType.VSYNC_ON);
      } else {
        localObject = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), null);
      }
      BufferStrategy localBufferStrategy = null;
      if (SunToolkit.isInstanceOf(paramContainer, "java.applet.Applet")) {
        try
        {
          BufferStrategyPaintManager.access$800().invoke(paramContainer, new Object[] { Integer.valueOf(2), localObject });
          localBufferStrategy = (BufferStrategy)BufferStrategyPaintManager.access$700().invoke(paramContainer, new Object[0]);
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
            BufferStrategyPaintManager.LOGGER.finer("createBufferStratety failed", localInvocationTargetException);
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      } else {
        try
        {
          ((Window)paramContainer).createBufferStrategy(2, (BufferCapabilities)localObject);
          localBufferStrategy = ((Window)paramContainer).getBufferStrategy();
        }
        catch (AWTException localAWTException)
        {
          if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
            BufferStrategyPaintManager.LOGGER.finer("createBufferStratety failed", localAWTException);
          }
        }
      }
      return localBufferStrategy;
    }
    
    public void dispose()
    {
      Container localContainer = getRoot();
      if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
        BufferStrategyPaintManager.LOGGER.finer("disposed BufferInfo for: " + localContainer);
      }
      if (localContainer != null)
      {
        localContainer.removeComponentListener(this);
        if ((localContainer instanceof Window)) {
          ((Window)localContainer).removeWindowListener(this);
        }
        BufferStrategy localBufferStrategy = getBufferStrategy(false);
        if (localBufferStrategy != null) {
          localBufferStrategy.dispose();
        }
      }
      root = null;
      weakBS = null;
    }
    
    public void componentHidden(ComponentEvent paramComponentEvent)
    {
      Container localContainer = getRoot();
      if ((localContainer != null) && (localContainer.isVisible())) {
        localContainer.repaint();
      } else {
        setPaintAllOnExpose(true);
      }
    }
    
    public void windowIconified(WindowEvent paramWindowEvent)
    {
      setPaintAllOnExpose(true);
    }
    
    public void windowClosed(WindowEvent paramWindowEvent)
    {
      synchronized (BufferStrategyPaintManager.this)
      {
        while (showing) {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        bufferInfos.remove(this);
      }
      dispose();
    }
    
    public void windowOpened(WindowEvent paramWindowEvent) {}
    
    public void windowClosing(WindowEvent paramWindowEvent) {}
    
    public void windowDeiconified(WindowEvent paramWindowEvent) {}
    
    public void windowActivated(WindowEvent paramWindowEvent) {}
    
    public void windowDeactivated(WindowEvent paramWindowEvent) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\BufferStrategyPaintManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */