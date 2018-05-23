package javax.swing;

import com.sun.java.swing.SwingUtilities3;
import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.awt.image.VolatileImage;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AWTAccessor.WindowAccessor;
import sun.awt.AppContext;
import sun.awt.DisplayChangedListener;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingAccessor;
import sun.swing.SwingAccessor.RepaintManagerAccessor;
import sun.swing.SwingUtilities2.RepaintListener;

public class RepaintManager
{
  static final boolean HANDLE_TOP_LEVEL_PAINT;
  private static final short BUFFER_STRATEGY_NOT_SPECIFIED = 0;
  private static final short BUFFER_STRATEGY_SPECIFIED_ON = 1;
  private static final short BUFFER_STRATEGY_SPECIFIED_OFF = 2;
  private static final short BUFFER_STRATEGY_TYPE;
  private Map<GraphicsConfiguration, VolatileImage> volatileMap = new HashMap(1);
  private Map<Container, Rectangle> hwDirtyComponents;
  private Map<Component, Rectangle> dirtyComponents;
  private Map<Component, Rectangle> tmpDirtyComponents;
  private List<Component> invalidComponents;
  private List<Runnable> runnableList;
  boolean doubleBufferingEnabled = true;
  private Dimension doubleBufferMaxSize;
  DoubleBufferInfo standardDoubleBuffer;
  private PaintManager paintManager;
  private static final Object repaintManagerKey = RepaintManager.class;
  static boolean volatileImageBufferEnabled = true;
  private static final int volatileBufferType;
  private static boolean nativeDoubleBuffering;
  private static final int VOLATILE_LOOP_MAX = 2;
  private int paintDepth = 0;
  private short bufferStrategyType;
  private boolean painting;
  private JComponent repaintRoot;
  private Thread paintThread;
  private final ProcessingRunnable processingRunnable;
  private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
  private static final DisplayChangedListener displayChangedHandler = new DisplayChangedHandler();
  Rectangle tmp = new Rectangle();
  private List<SwingUtilities2.RepaintListener> repaintListeners = new ArrayList(1);
  
  public static RepaintManager currentManager(Component paramComponent)
  {
    return currentManager(AppContext.getAppContext());
  }
  
  static RepaintManager currentManager(AppContext paramAppContext)
  {
    RepaintManager localRepaintManager = (RepaintManager)paramAppContext.get(repaintManagerKey);
    if (localRepaintManager == null)
    {
      localRepaintManager = new RepaintManager(BUFFER_STRATEGY_TYPE);
      paramAppContext.put(repaintManagerKey, localRepaintManager);
    }
    return localRepaintManager;
  }
  
  public static RepaintManager currentManager(JComponent paramJComponent)
  {
    return currentManager(paramJComponent);
  }
  
  public static void setCurrentManager(RepaintManager paramRepaintManager)
  {
    if (paramRepaintManager != null) {
      SwingUtilities.appContextPut(repaintManagerKey, paramRepaintManager);
    } else {
      SwingUtilities.appContextRemove(repaintManagerKey);
    }
  }
  
  public RepaintManager()
  {
    this((short)2);
  }
  
  private RepaintManager(short paramShort)
  {
    synchronized (this)
    {
      dirtyComponents = new IdentityHashMap();
      tmpDirtyComponents = new IdentityHashMap();
      bufferStrategyType = paramShort;
      hwDirtyComponents = new IdentityHashMap();
    }
    processingRunnable = new ProcessingRunnable(null);
  }
  
  private void displayChanged()
  {
    clearImages();
  }
  
  public synchronized void addInvalidComponent(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.addInvalidComponent(paramJComponent);
      return;
    }
    Container localContainer = SwingUtilities.getValidateRoot(paramJComponent, true);
    if (localContainer == null) {
      return;
    }
    if (invalidComponents == null)
    {
      invalidComponents = new ArrayList();
    }
    else
    {
      int i = invalidComponents.size();
      for (int j = 0; j < i; j++) {
        if (localContainer == invalidComponents.get(j)) {
          return;
        }
      }
    }
    invalidComponents.add(localContainer);
    scheduleProcessingRunnable(SunToolkit.targetToAppContext(paramJComponent));
  }
  
  public synchronized void removeInvalidComponent(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.removeInvalidComponent(paramJComponent);
      return;
    }
    if (invalidComponents != null)
    {
      int i = invalidComponents.indexOf(paramJComponent);
      if (i != -1) {
        invalidComponents.remove(i);
      }
    }
  }
  
  private void addDirtyRegion0(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 <= 0) || (paramInt4 <= 0) || (paramContainer == null)) {
      return;
    }
    if ((paramContainer.getWidth() <= 0) || (paramContainer.getHeight() <= 0)) {
      return;
    }
    if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4)) {
      return;
    }
    Object localObject1 = null;
    for (Container localContainer = paramContainer; localContainer != null; localContainer = localContainer.getParent())
    {
      if ((!localContainer.isVisible()) || (localContainer.getPeer() == null)) {
        return;
      }
      if (((localContainer instanceof Window)) || ((localContainer instanceof Applet)))
      {
        if (((localContainer instanceof Frame)) && ((((Frame)localContainer).getExtendedState() & 0x1) == 1)) {
          return;
        }
        localObject1 = localContainer;
        break;
      }
    }
    if (localObject1 == null) {
      return;
    }
    synchronized (this)
    {
      if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4)) {
        return;
      }
      dirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    scheduleProcessingRunnable(SunToolkit.targetToAppContext(paramContainer));
  }
  
  public void addDirtyRegion(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.addDirtyRegion(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    addDirtyRegion0(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void addDirtyRegion(Window paramWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addDirtyRegion0(paramWindow, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void addDirtyRegion(Applet paramApplet, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addDirtyRegion0(paramApplet, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void scheduleHeavyWeightPaints()
  {
    Map localMap;
    synchronized (this)
    {
      if (hwDirtyComponents.size() == 0) {
        return;
      }
      localMap = hwDirtyComponents;
      hwDirtyComponents = new IdentityHashMap();
    }
    ??? = localMap.keySet().iterator();
    while (((Iterator)???).hasNext())
    {
      Container localContainer = (Container)((Iterator)???).next();
      Rectangle localRectangle = (Rectangle)localMap.get(localContainer);
      if ((localContainer instanceof Window)) {
        addDirtyRegion((Window)localContainer, x, y, width, height);
      } else if ((localContainer instanceof Applet)) {
        addDirtyRegion((Applet)localContainer, x, y, width, height);
      } else {
        addDirtyRegion0(localContainer, x, y, width, height);
      }
    }
  }
  
  void nativeAddDirtyRegion(AppContext paramAppContext, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 > 0) && (paramInt4 > 0))
    {
      synchronized (this)
      {
        Rectangle localRectangle = (Rectangle)hwDirtyComponents.get(paramContainer);
        if (localRectangle == null) {
          hwDirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
        } else {
          hwDirtyComponents.put(paramContainer, SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle));
        }
      }
      scheduleProcessingRunnable(paramAppContext);
    }
  }
  
  void nativeQueueSurfaceDataRunnable(AppContext paramAppContext, final Component paramComponent, final Runnable paramRunnable)
  {
    synchronized (this)
    {
      if (runnableList == null) {
        runnableList = new LinkedList();
      }
      runnableList.add(new Runnable()
      {
        public void run()
        {
          AccessControlContext localAccessControlContext1 = AccessController.getContext();
          AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(paramComponent);
          RepaintManager.javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
          {
            public Void run()
            {
              val$r.run();
              return null;
            }
          }, localAccessControlContext1, localAccessControlContext2);
        }
      });
    }
    scheduleProcessingRunnable(paramAppContext);
  }
  
  private synchronized boolean extendDirtyRegion(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle localRectangle = (Rectangle)dirtyComponents.get(paramComponent);
    if (localRectangle != null)
    {
      SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle);
      return true;
    }
    return false;
  }
  
  public Rectangle getDirtyRegion(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.getDirtyRegion(paramJComponent);
    }
    Rectangle localRectangle;
    synchronized (this)
    {
      localRectangle = (Rectangle)dirtyComponents.get(paramJComponent);
    }
    if (localRectangle == null) {
      return new Rectangle(0, 0, 0, 0);
    }
    return new Rectangle(localRectangle);
  }
  
  public void markCompletelyDirty(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.markCompletelyDirty(paramJComponent);
      return;
    }
    addDirtyRegion(paramJComponent, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public void markCompletelyClean(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.markCompletelyClean(paramJComponent);
      return;
    }
    synchronized (this)
    {
      dirtyComponents.remove(paramJComponent);
    }
  }
  
  public boolean isCompletelyDirty(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.isCompletelyDirty(paramJComponent);
    }
    Rectangle localRectangle = getDirtyRegion(paramJComponent);
    return (width == Integer.MAX_VALUE) && (height == Integer.MAX_VALUE);
  }
  
  public void validateInvalidComponents()
  {
    List localList;
    synchronized (this)
    {
      if (invalidComponents == null) {
        return;
      }
      localList = invalidComponents;
      invalidComponents = null;
    }
    ??? = localList.size();
    for (Object localObject2 = 0; localObject2 < ???; localObject2++)
    {
      final Component localComponent = (Component)localList.get(localObject2);
      AccessControlContext localAccessControlContext1 = AccessController.getContext();
      AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(localComponent);
      javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
      {
        public Void run()
        {
          localComponent.validate();
          return null;
        }
      }, localAccessControlContext1, localAccessControlContext2);
    }
  }
  
  private void prePaintDirtyRegions()
  {
    Map localMap;
    List localList;
    synchronized (this)
    {
      localMap = dirtyComponents;
      localList = runnableList;
      runnableList = null;
    }
    if (localList != null)
    {
      ??? = localList.iterator();
      while (((Iterator)???).hasNext())
      {
        Runnable localRunnable = (Runnable)((Iterator)???).next();
        localRunnable.run();
      }
    }
    paintDirtyRegions();
    if (localMap.size() > 0) {
      paintDirtyRegions(localMap);
    }
  }
  
  private void updateWindows(Map<Component, Rectangle> paramMap)
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((!(localToolkit instanceof SunToolkit)) || (!((SunToolkit)localToolkit).needUpdateWindow())) {
      return;
    }
    HashSet localHashSet = new HashSet();
    Set localSet = paramMap.keySet();
    Iterator localIterator = localSet.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Component)localIterator.next();
      Window localWindow = (localObject instanceof Window) ? (Window)localObject : SwingUtilities.getWindowAncestor((Component)localObject);
      if ((localWindow != null) && (!localWindow.isOpaque())) {
        localHashSet.add(localWindow);
      }
    }
    localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      localObject = (Window)localIterator.next();
      AWTAccessor.getWindowAccessor().updateWindow((Window)localObject);
    }
  }
  
  boolean isPainting()
  {
    return painting;
  }
  
  public void paintDirtyRegions()
  {
    synchronized (this)
    {
      Map localMap = tmpDirtyComponents;
      tmpDirtyComponents = dirtyComponents;
      dirtyComponents = localMap;
      dirtyComponents.clear();
    }
    paintDirtyRegions(tmpDirtyComponents);
  }
  
  private void paintDirtyRegions(final Map<Component, Rectangle> paramMap)
  {
    if (paramMap.isEmpty()) {
      return;
    }
    final ArrayList localArrayList = new ArrayList(paramMap.size());
    final Object localObject1 = paramMap.keySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Component localComponent1 = (Component)((Iterator)localObject1).next();
      collectDirtyComponents(paramMap, localComponent1, localArrayList);
    }
    localObject1 = new AtomicInteger(localArrayList.size());
    painting = true;
    try
    {
      for (int i = 0; i < ((AtomicInteger)localObject1).get(); i++)
      {
        final int j = i;
        final Component localComponent2 = (Component)localArrayList.get(i);
        AccessControlContext localAccessControlContext1 = AccessController.getContext();
        AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(localComponent2);
        javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
        {
          public Void run()
          {
            Rectangle localRectangle = (Rectangle)paramMap.get(localComponent2);
            if (localRectangle == null) {
              return null;
            }
            int i = localComponent2.getHeight();
            int j = localComponent2.getWidth();
            SwingUtilities.computeIntersection(0, 0, j, i, localRectangle);
            if ((localComponent2 instanceof JComponent))
            {
              ((JComponent)localComponent2).paintImmediately(x, y, width, height);
            }
            else if (localComponent2.isShowing())
            {
              Graphics localGraphics = JComponent.safelyGetGraphics(localComponent2, localComponent2);
              if (localGraphics != null)
              {
                localGraphics.setClip(x, y, width, height);
                try
                {
                  localComponent2.paint(localGraphics);
                }
                finally
                {
                  localGraphics.dispose();
                }
              }
            }
            if (repaintRoot != null)
            {
              RepaintManager.this.adjustRoots(repaintRoot, localArrayList, j + 1);
              localObject1.set(localArrayList.size());
              paintManager.isRepaintingRoot = true;
              repaintRoot.paintImmediately(0, 0, repaintRoot.getWidth(), repaintRoot.getHeight());
              paintManager.isRepaintingRoot = false;
              repaintRoot = null;
            }
            return null;
          }
        }, localAccessControlContext1, localAccessControlContext2);
      }
    }
    finally
    {
      painting = false;
    }
    updateWindows(paramMap);
    paramMap.clear();
  }
  
  private void adjustRoots(JComponent paramJComponent, List<Component> paramList, int paramInt)
  {
    for (int i = paramList.size() - 1; i >= paramInt; i--)
    {
      for (Object localObject = (Component)paramList.get(i); (localObject != paramJComponent) && (localObject != null) && ((localObject instanceof JComponent)); localObject = ((Component)localObject).getParent()) {}
      if (localObject == paramJComponent) {
        paramList.remove(i);
      }
    }
  }
  
  void collectDirtyComponents(Map<Component, Rectangle> paramMap, Component paramComponent, List<Component> paramList)
  {
    Object localObject2;
    Object localObject1 = localObject2 = paramComponent;
    int n = paramComponent.getX();
    int i1 = paramComponent.getY();
    int i2 = paramComponent.getWidth();
    int i3 = paramComponent.getHeight();
    int k;
    int i = k = 0;
    int m;
    int j = m = 0;
    tmp.setBounds((Rectangle)paramMap.get(paramComponent));
    SwingUtilities.computeIntersection(0, 0, i2, i3, tmp);
    if (tmp.isEmpty()) {
      return;
    }
    while ((localObject1 instanceof JComponent))
    {
      Container localContainer = ((Component)localObject1).getParent();
      if (localContainer == null) {
        break;
      }
      localObject1 = localContainer;
      i += n;
      j += i1;
      tmp.setLocation(tmp.x + n, tmp.y + i1);
      n = ((Component)localObject1).getX();
      i1 = ((Component)localObject1).getY();
      i2 = ((Component)localObject1).getWidth();
      i3 = ((Component)localObject1).getHeight();
      tmp = SwingUtilities.computeIntersection(0, 0, i2, i3, tmp);
      if (tmp.isEmpty()) {
        return;
      }
      if (paramMap.get(localObject1) != null)
      {
        localObject2 = localObject1;
        k = i;
        m = j;
      }
    }
    if (paramComponent != localObject2)
    {
      tmp.setLocation(tmp.x + k - i, tmp.y + m - j);
      Rectangle localRectangle = (Rectangle)paramMap.get(localObject2);
      SwingUtilities.computeUnion(tmp.x, tmp.y, tmp.width, tmp.height, localRectangle);
    }
    if (!paramList.contains(localObject2)) {
      paramList.add(localObject2);
    }
  }
  
  public synchronized String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (dirtyComponents != null) {
      localStringBuffer.append("" + dirtyComponents);
    }
    return localStringBuffer.toString();
  }
  
  public Image getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
  {
    RepaintManager localRepaintManager = getDelegate(paramComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.getOffscreenBuffer(paramComponent, paramInt1, paramInt2);
    }
    return _getOffscreenBuffer(paramComponent, paramInt1, paramInt2);
  }
  
  public Image getVolatileOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
  {
    RepaintManager localRepaintManager = getDelegate(paramComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.getVolatileOffscreenBuffer(paramComponent, paramInt1, paramInt2);
    }
    Window localWindow = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
    if (!localWindow.isOpaque())
    {
      localObject = Toolkit.getDefaultToolkit();
      if (((localObject instanceof SunToolkit)) && (((SunToolkit)localObject).needUpdateWindow())) {
        return null;
      }
    }
    Object localObject = paramComponent.getGraphicsConfiguration();
    if (localObject == null) {
      localObject = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    Dimension localDimension = getDoubleBufferMaximumSize();
    int i = paramInt1 > width ? width : paramInt1 < 1 ? 1 : paramInt1;
    int j = paramInt2 > height ? height : paramInt2 < 1 ? 1 : paramInt2;
    VolatileImage localVolatileImage = (VolatileImage)volatileMap.get(localObject);
    if ((localVolatileImage == null) || (localVolatileImage.getWidth() < i) || (localVolatileImage.getHeight() < j))
    {
      if (localVolatileImage != null) {
        localVolatileImage.flush();
      }
      localVolatileImage = ((GraphicsConfiguration)localObject).createCompatibleVolatileImage(i, j, volatileBufferType);
      volatileMap.put(localObject, localVolatileImage);
    }
    return localVolatileImage;
  }
  
  private Image _getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
  {
    Dimension localDimension = getDoubleBufferMaximumSize();
    Window localWindow = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
    if (!localWindow.isOpaque())
    {
      localObject = Toolkit.getDefaultToolkit();
      if (((localObject instanceof SunToolkit)) && (((SunToolkit)localObject).needUpdateWindow())) {
        return null;
      }
    }
    if (standardDoubleBuffer == null) {
      standardDoubleBuffer = new DoubleBufferInfo(null);
    }
    DoubleBufferInfo localDoubleBufferInfo = standardDoubleBuffer;
    int i = paramInt1 > width ? width : paramInt1 < 1 ? 1 : paramInt1;
    int j = paramInt2 > height ? height : paramInt2 < 1 ? 1 : paramInt2;
    if ((needsReset) || ((image != null) && ((size.width < i) || (size.height < j))))
    {
      needsReset = false;
      if (image != null)
      {
        image.flush();
        image = null;
      }
      i = Math.max(size.width, i);
      j = Math.max(size.height, j);
    }
    Object localObject = image;
    if (image == null)
    {
      localObject = paramComponent.createImage(i, j);
      size = new Dimension(i, j);
      if ((paramComponent instanceof JComponent))
      {
        ((JComponent)paramComponent).setCreatedDoubleBuffer(true);
        image = ((Image)localObject);
      }
    }
    return (Image)localObject;
  }
  
  public void setDoubleBufferMaximumSize(Dimension paramDimension)
  {
    doubleBufferMaxSize = paramDimension;
    if (doubleBufferMaxSize == null) {
      clearImages();
    } else {
      clearImages(width, height);
    }
  }
  
  private void clearImages()
  {
    clearImages(0, 0);
  }
  
  private void clearImages(int paramInt1, int paramInt2)
  {
    if ((standardDoubleBuffer != null) && (standardDoubleBuffer.image != null) && ((standardDoubleBuffer.image.getWidth(null) > paramInt1) || (standardDoubleBuffer.image.getHeight(null) > paramInt2)))
    {
      standardDoubleBuffer.image.flush();
      standardDoubleBuffer.image = null;
    }
    Iterator localIterator = volatileMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      GraphicsConfiguration localGraphicsConfiguration = (GraphicsConfiguration)localIterator.next();
      VolatileImage localVolatileImage = (VolatileImage)volatileMap.get(localGraphicsConfiguration);
      if ((localVolatileImage.getWidth() > paramInt1) || (localVolatileImage.getHeight() > paramInt2))
      {
        localVolatileImage.flush();
        localIterator.remove();
      }
    }
  }
  
  public Dimension getDoubleBufferMaximumSize()
  {
    if (doubleBufferMaxSize == null) {
      try
      {
        Rectangle localRectangle = new Rectangle();
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice localGraphicsDevice : localGraphicsEnvironment.getScreenDevices())
        {
          GraphicsConfiguration localGraphicsConfiguration = localGraphicsDevice.getDefaultConfiguration();
          localRectangle = localRectangle.union(localGraphicsConfiguration.getBounds());
        }
        doubleBufferMaxSize = new Dimension(width, height);
      }
      catch (HeadlessException localHeadlessException)
      {
        doubleBufferMaxSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
      }
    }
    return doubleBufferMaxSize;
  }
  
  public void setDoubleBufferingEnabled(boolean paramBoolean)
  {
    doubleBufferingEnabled = paramBoolean;
    PaintManager localPaintManager = getPaintManager();
    if ((!paramBoolean) && (localPaintManager.getClass() != PaintManager.class)) {
      setPaintManager(new PaintManager());
    }
  }
  
  public boolean isDoubleBufferingEnabled()
  {
    return doubleBufferingEnabled;
  }
  
  void resetDoubleBuffer()
  {
    if (standardDoubleBuffer != null) {
      standardDoubleBuffer.needsReset = true;
    }
  }
  
  void resetVolatileDoubleBuffer(GraphicsConfiguration paramGraphicsConfiguration)
  {
    Image localImage = (Image)volatileMap.remove(paramGraphicsConfiguration);
    if (localImage != null) {
      localImage.flush();
    }
  }
  
  boolean useVolatileDoubleBuffer()
  {
    return volatileImageBufferEnabled;
  }
  
  private synchronized boolean isPaintingThread()
  {
    return Thread.currentThread() == paintThread;
  }
  
  void paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    PaintManager localPaintManager = getPaintManager();
    if ((!isPaintingThread()) && (localPaintManager.getClass() != PaintManager.class))
    {
      localPaintManager = new PaintManager();
      repaintManager = this;
    }
    if (!localPaintManager.paint(paramJComponent1, paramJComponent2, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4))
    {
      paramGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
      paramJComponent1.paintToOffscreen(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
    }
  }
  
  void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    getPaintManager().copyArea(paramJComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
  }
  
  private void addRepaintListener(SwingUtilities2.RepaintListener paramRepaintListener)
  {
    repaintListeners.add(paramRepaintListener);
  }
  
  private void removeRepaintListener(SwingUtilities2.RepaintListener paramRepaintListener)
  {
    repaintListeners.remove(paramRepaintListener);
  }
  
  void notifyRepaintPerformed(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Iterator localIterator = repaintListeners.iterator();
    while (localIterator.hasNext())
    {
      SwingUtilities2.RepaintListener localRepaintListener = (SwingUtilities2.RepaintListener)localIterator.next();
      localRepaintListener.repaintPerformed(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  void beginPaint()
  {
    int i = 0;
    Thread localThread = Thread.currentThread();
    int j;
    synchronized (this)
    {
      j = paintDepth;
      if ((paintThread == null) || (localThread == paintThread))
      {
        paintThread = localThread;
        paintDepth += 1;
      }
      else
      {
        i = 1;
      }
    }
    if ((i == 0) && (j == 0)) {
      getPaintManager().beginPaint();
    }
  }
  
  void endPaint()
  {
    if (isPaintingThread())
    {
      PaintManager localPaintManager = null;
      synchronized (this)
      {
        if (--paintDepth == 0) {
          localPaintManager = getPaintManager();
        }
      }
      if (localPaintManager != null)
      {
        localPaintManager.endPaint();
        synchronized (this)
        {
          paintThread = null;
        }
      }
    }
  }
  
  boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return getPaintManager().show(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void doubleBufferingChanged(JRootPane paramJRootPane)
  {
    getPaintManager().doubleBufferingChanged(paramJRootPane);
  }
  
  void setPaintManager(PaintManager paramPaintManager)
  {
    if (paramPaintManager == null) {
      paramPaintManager = new PaintManager();
    }
    PaintManager localPaintManager;
    synchronized (this)
    {
      localPaintManager = paintManager;
      paintManager = paramPaintManager;
      repaintManager = this;
    }
    if (localPaintManager != null) {
      localPaintManager.dispose();
    }
  }
  
  private synchronized PaintManager getPaintManager()
  {
    if (paintManager == null)
    {
      BufferStrategyPaintManager localBufferStrategyPaintManager = null;
      if ((doubleBufferingEnabled) && (!nativeDoubleBuffering)) {
        switch (bufferStrategyType)
        {
        case 0: 
          Toolkit localToolkit = Toolkit.getDefaultToolkit();
          if ((localToolkit instanceof SunToolkit))
          {
            SunToolkit localSunToolkit = (SunToolkit)localToolkit;
            if (localSunToolkit.useBufferPerWindow()) {
              localBufferStrategyPaintManager = new BufferStrategyPaintManager();
            }
          }
          break;
        case 1: 
          localBufferStrategyPaintManager = new BufferStrategyPaintManager();
          break;
        }
      }
      setPaintManager(localBufferStrategyPaintManager);
    }
    return paintManager;
  }
  
  private void scheduleProcessingRunnable(AppContext paramAppContext)
  {
    if (processingRunnable.markPending())
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      if ((localToolkit instanceof SunToolkit)) {
        SunToolkit.getSystemEventQueueImplPP(paramAppContext).postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), processingRunnable));
      } else {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), processingRunnable));
      }
    }
  }
  
  private RepaintManager getDelegate(Component paramComponent)
  {
    RepaintManager localRepaintManager = SwingUtilities3.getDelegateRepaintManager(paramComponent);
    if (this == localRepaintManager) {
      localRepaintManager = null;
    }
    return localRepaintManager;
  }
  
  static
  {
    SwingAccessor.setRepaintManagerAccessor(new SwingAccessor.RepaintManagerAccessor()
    {
      public void addRepaintListener(RepaintManager paramAnonymousRepaintManager, SwingUtilities2.RepaintListener paramAnonymousRepaintListener)
      {
        paramAnonymousRepaintManager.addRepaintListener(paramAnonymousRepaintListener);
      }
      
      public void removeRepaintListener(RepaintManager paramAnonymousRepaintManager, SwingUtilities2.RepaintListener paramAnonymousRepaintListener)
      {
        paramAnonymousRepaintManager.removeRepaintListener(paramAnonymousRepaintListener);
      }
    });
    volatileImageBufferEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.volatileImageBufferEnabled", "true")));
    boolean bool = GraphicsEnvironment.isHeadless();
    if ((volatileImageBufferEnabled) && (bool)) {
      volatileImageBufferEnabled = false;
    }
    nativeDoubleBuffering = "true".equals(AccessController.doPrivileged(new GetPropertyAction("awt.nativeDoubleBuffering")));
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.bufferPerWindow"));
    if (bool) {
      BUFFER_STRATEGY_TYPE = 2;
    } else if (str == null) {
      BUFFER_STRATEGY_TYPE = 0;
    } else if ("true".equals(str)) {
      BUFFER_STRATEGY_TYPE = 1;
    } else {
      BUFFER_STRATEGY_TYPE = 2;
    }
    HANDLE_TOP_LEVEL_PAINT = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.handleTopLevelPaint", "true")));
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
      ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(displayChangedHandler);
    }
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (((localToolkit instanceof SunToolkit)) && (((SunToolkit)localToolkit).isSwingBackbufferTranslucencySupported())) {
      volatileBufferType = 3;
    } else {
      volatileBufferType = 1;
    }
  }
  
  private static final class DisplayChangedHandler
    implements DisplayChangedListener
  {
    DisplayChangedHandler() {}
    
    public void displayChanged() {}
    
    public void paletteChanged() {}
    
    private static void scheduleDisplayChanges()
    {
      Iterator localIterator = AppContext.getAppContexts().iterator();
      while (localIterator.hasNext())
      {
        AppContext localAppContext = (AppContext)localIterator.next();
        synchronized (localAppContext)
        {
          if (!localAppContext.isDisposed())
          {
            EventQueue localEventQueue = (EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY);
            if (localEventQueue != null) {
              localEventQueue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new RepaintManager.DisplayChangedRunnable(null)));
            }
          }
        }
      }
    }
  }
  
  private static final class DisplayChangedRunnable
    implements Runnable
  {
    private DisplayChangedRunnable() {}
    
    public void run()
    {
      RepaintManager.currentManager((JComponent)null).displayChanged();
    }
  }
  
  private class DoubleBufferInfo
  {
    public Image image;
    public Dimension size;
    public boolean needsReset = false;
    
    private DoubleBufferInfo() {}
  }
  
  static class PaintManager
  {
    protected RepaintManager repaintManager;
    boolean isRepaintingRoot;
    
    PaintManager() {}
    
    public boolean paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      boolean bool = false;
      Image localImage;
      if ((repaintManager.useVolatileDoubleBuffer()) && ((localImage = getValidImage(repaintManager.getVolatileOffscreenBuffer(paramJComponent2, paramInt3, paramInt4))) != null))
      {
        VolatileImage localVolatileImage = (VolatileImage)localImage;
        GraphicsConfiguration localGraphicsConfiguration = paramJComponent2.getGraphicsConfiguration();
        for (int i = 0; (!bool) && (i < 2); i++)
        {
          if (localVolatileImage.validate(localGraphicsConfiguration) == 2)
          {
            repaintManager.resetVolatileDoubleBuffer(localGraphicsConfiguration);
            localImage = repaintManager.getVolatileOffscreenBuffer(paramJComponent2, paramInt3, paramInt4);
            localVolatileImage = (VolatileImage)localImage;
          }
          paintDoubleBuffered(paramJComponent1, localVolatileImage, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
          bool = !localVolatileImage.contentsLost();
        }
      }
      if ((!bool) && ((localImage = getValidImage(repaintManager.getOffscreenBuffer(paramJComponent2, paramInt3, paramInt4))) != null))
      {
        paintDoubleBuffered(paramJComponent1, localImage, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        bool = true;
      }
      return bool;
    }
    
    public void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
    {
      paramGraphics.copyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    
    public void beginPaint() {}
    
    public void endPaint() {}
    
    public boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return false;
    }
    
    public void doubleBufferingChanged(JRootPane paramJRootPane) {}
    
    protected void paintDoubleBuffered(JComponent paramJComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Graphics localGraphics = paramImage.getGraphics();
      int i = Math.min(paramInt3, paramImage.getWidth(null));
      int j = Math.min(paramInt4, paramImage.getHeight(null));
      try
      {
        int k = paramInt1;
        int n = paramInt1 + paramInt3;
        while (k < n)
        {
          int m = paramInt2;
          int i1 = paramInt2 + paramInt4;
          while (m < i1)
          {
            localGraphics.translate(-k, -m);
            localGraphics.setClip(k, m, i, j);
            Graphics2D localGraphics2D;
            Object localObject1;
            if ((RepaintManager.volatileBufferType != 1) && ((localGraphics instanceof Graphics2D)))
            {
              localGraphics2D = (Graphics2D)localGraphics;
              localObject1 = localGraphics2D.getBackground();
              localGraphics2D.setBackground(paramJComponent.getBackground());
              localGraphics2D.clearRect(k, m, i, j);
              localGraphics2D.setBackground((Color)localObject1);
            }
            paramJComponent.paintToOffscreen(localGraphics, k, m, i, j, n, i1);
            paramGraphics.setClip(k, m, i, j);
            if ((RepaintManager.volatileBufferType != 1) && ((paramGraphics instanceof Graphics2D)))
            {
              localGraphics2D = (Graphics2D)paramGraphics;
              localObject1 = localGraphics2D.getComposite();
              localGraphics2D.setComposite(AlphaComposite.Src);
              localGraphics2D.drawImage(paramImage, k, m, paramJComponent);
              localGraphics2D.setComposite((Composite)localObject1);
            }
            else
            {
              paramGraphics.drawImage(paramImage, k, m, paramJComponent);
            }
            localGraphics.translate(k, m);
            m += j;
          }
          k += i;
        }
      }
      finally
      {
        localGraphics.dispose();
      }
    }
    
    private Image getValidImage(Image paramImage)
    {
      if ((paramImage != null) && (paramImage.getWidth(null) > 0) && (paramImage.getHeight(null) > 0)) {
        return paramImage;
      }
      return null;
    }
    
    protected void repaintRoot(JComponent paramJComponent)
    {
      assert (repaintManager.repaintRoot == null);
      if (repaintManager.painting) {
        repaintManager.repaintRoot = paramJComponent;
      } else {
        paramJComponent.repaint();
      }
    }
    
    protected boolean isRepaintingRoot()
    {
      return isRepaintingRoot;
    }
    
    protected void dispose() {}
  }
  
  private final class ProcessingRunnable
    implements Runnable
  {
    private boolean pending;
    
    private ProcessingRunnable() {}
    
    public synchronized boolean markPending()
    {
      if (!pending)
      {
        pending = true;
        return true;
      }
      return false;
    }
    
    public void run()
    {
      synchronized (this)
      {
        pending = false;
      }
      scheduleHeavyWeightPaints();
      validateInvalidComponents();
      RepaintManager.this.prePaintDirtyRegions();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\RepaintManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */