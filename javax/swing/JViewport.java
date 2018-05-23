package javax.swing;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.peer.ComponentPeer;
import java.beans.Transient;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ViewportUI;

public class JViewport
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "ViewportUI";
  static final Object EnableWindowBlit = "EnableWindowBlit";
  protected boolean isViewSizeSet = false;
  protected Point lastPaintPosition = null;
  @Deprecated
  protected boolean backingStore = false;
  protected transient Image backingStoreImage = null;
  protected boolean scrollUnderway = false;
  private ComponentListener viewListener = null;
  private transient ChangeEvent changeEvent = null;
  public static final int BLIT_SCROLL_MODE = 1;
  public static final int BACKINGSTORE_SCROLL_MODE = 2;
  public static final int SIMPLE_SCROLL_MODE = 0;
  private int scrollMode = 1;
  private transient boolean repaintAll;
  private transient boolean waitingForRepaint;
  private transient Timer repaintTimer;
  private transient boolean inBlitPaint;
  private boolean hasHadValidView;
  private boolean viewChanged;
  
  public JViewport()
  {
    setLayout(createLayoutManager());
    setOpaque(true);
    updateUI();
    setInheritsPopupMenu(true);
  }
  
  public ViewportUI getUI()
  {
    return (ViewportUI)ui;
  }
  
  public void setUI(ViewportUI paramViewportUI)
  {
    super.setUI(paramViewportUI);
  }
  
  public void updateUI()
  {
    setUI((ViewportUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ViewportUI";
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    setView(paramComponent);
  }
  
  public void remove(Component paramComponent)
  {
    paramComponent.removeComponentListener(viewListener);
    super.remove(paramComponent);
  }
  
  public void scrollRectToVisible(Rectangle paramRectangle)
  {
    Component localComponent = getView();
    if (localComponent == null) {
      return;
    }
    if (!localComponent.isValid()) {
      validateView();
    }
    int i = positionAdjustment(getWidth(), width, x);
    int j = positionAdjustment(getHeight(), height, y);
    if ((i != 0) || (j != 0))
    {
      Point localPoint = getViewPosition();
      Dimension localDimension1 = localComponent.getSize();
      int k = x;
      int m = y;
      Dimension localDimension2 = getExtentSize();
      x -= i;
      y -= j;
      if (localComponent.isValid())
      {
        if (getParent().getComponentOrientation().isLeftToRight())
        {
          if (x + width > width) {
            x = Math.max(0, width - width);
          } else if (x < 0) {
            x = 0;
          }
        }
        else if (width > width) {
          x = (width - width);
        } else {
          x = Math.max(0, Math.min(width - width, x));
        }
        if (y + height > height) {
          y = Math.max(0, height - height);
        } else if (y < 0) {
          y = 0;
        }
      }
      if ((x != k) || (y != m))
      {
        setViewPosition(localPoint);
        scrollUnderway = false;
      }
    }
  }
  
  private void validateView()
  {
    Container localContainer = SwingUtilities.getValidateRoot(this, false);
    if (localContainer == null) {
      return;
    }
    localContainer.validate();
    RepaintManager localRepaintManager = RepaintManager.currentManager(this);
    if (localRepaintManager != null) {
      localRepaintManager.removeInvalidComponent((JComponent)localContainer);
    }
  }
  
  private int positionAdjustment(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt3 >= 0) && (paramInt2 + paramInt3 <= paramInt1)) {
      return 0;
    }
    if ((paramInt3 <= 0) && (paramInt2 + paramInt3 >= paramInt1)) {
      return 0;
    }
    if ((paramInt3 > 0) && (paramInt2 <= paramInt1)) {
      return -paramInt3 + paramInt1 - paramInt2;
    }
    if ((paramInt3 >= 0) && (paramInt2 >= paramInt1)) {
      return -paramInt3;
    }
    if ((paramInt3 <= 0) && (paramInt2 <= paramInt1)) {
      return -paramInt3;
    }
    if ((paramInt3 < 0) && (paramInt2 >= paramInt1)) {
      return -paramInt3 + paramInt1 - paramInt2;
    }
    return 0;
  }
  
  public final void setBorder(Border paramBorder)
  {
    if (paramBorder != null) {
      throw new IllegalArgumentException("JViewport.setBorder() not supported");
    }
  }
  
  public final Insets getInsets()
  {
    return new Insets(0, 0, 0, 0);
  }
  
  public final Insets getInsets(Insets paramInsets)
  {
    left = (top = right = bottom = 0);
    return paramInsets;
  }
  
  private Graphics getBackingStoreGraphics(Graphics paramGraphics)
  {
    Graphics localGraphics = backingStoreImage.getGraphics();
    localGraphics.setColor(paramGraphics.getColor());
    localGraphics.setFont(paramGraphics.getFont());
    localGraphics.setClip(paramGraphics.getClipBounds());
    return localGraphics;
  }
  
  /* Error */
  private void paintViaBackingStore(Graphics paramGraphics)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 673	javax/swing/JViewport:getBackingStoreGraphics	(Ljava/awt/Graphics;)Ljava/awt/Graphics;
    //   5: astore_2
    //   6: aload_0
    //   7: aload_2
    //   8: invokespecial 622	javax/swing/JComponent:paint	(Ljava/awt/Graphics;)V
    //   11: aload_1
    //   12: aload_0
    //   13: getfield 538	javax/swing/JViewport:backingStoreImage	Ljava/awt/Image;
    //   16: iconst_0
    //   17: iconst_0
    //   18: aload_0
    //   19: invokevirtual 587	java/awt/Graphics:drawImage	(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
    //   22: pop
    //   23: aload_2
    //   24: invokevirtual 576	java/awt/Graphics:dispose	()V
    //   27: goto +10 -> 37
    //   30: astore_3
    //   31: aload_2
    //   32: invokevirtual 576	java/awt/Graphics:dispose	()V
    //   35: aload_3
    //   36: athrow
    //   37: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	this	JViewport
    //   0	38	1	paramGraphics	Graphics
    //   5	27	2	localGraphics	Graphics
    //   30	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   6	23	30	finally
  }
  
  private void paintViaBackingStore(Graphics paramGraphics, Rectangle paramRectangle)
  {
    Graphics localGraphics = getBackingStoreGraphics(paramGraphics);
    try
    {
      super.paint(localGraphics);
      paramGraphics.setClip(paramRectangle);
      paramGraphics.drawImage(backingStoreImage, 0, 0, this);
    }
    finally
    {
      localGraphics.dispose();
    }
  }
  
  public boolean isOptimizedDrawingEnabled()
  {
    return false;
  }
  
  protected boolean isPaintingOrigin()
  {
    return scrollMode == 2;
  }
  
  private Point getViewLocation()
  {
    Component localComponent = getView();
    if (localComponent != null) {
      return localComponent.getLocation();
    }
    return new Point(0, 0);
  }
  
  public void paint(Graphics paramGraphics)
  {
    int i = getWidth();
    int j = getHeight();
    if ((i <= 0) || (j <= 0)) {
      return;
    }
    if (inBlitPaint)
    {
      super.paint(paramGraphics);
      return;
    }
    if (repaintAll)
    {
      repaintAll = false;
      localRectangle1 = paramGraphics.getClipBounds();
      if ((width < getWidth()) || (height < getHeight()))
      {
        waitingForRepaint = true;
        if (repaintTimer == null) {
          repaintTimer = createRepaintTimer();
        }
        repaintTimer.stop();
        repaintTimer.start();
      }
      else
      {
        if (repaintTimer != null) {
          repaintTimer.stop();
        }
        waitingForRepaint = false;
      }
    }
    else if (waitingForRepaint)
    {
      localRectangle1 = paramGraphics.getClipBounds();
      if ((width >= getWidth()) && (height >= getHeight()))
      {
        waitingForRepaint = false;
        repaintTimer.stop();
      }
    }
    if ((!backingStore) || (isBlitting()) || (getView() == null))
    {
      super.paint(paramGraphics);
      lastPaintPosition = getViewLocation();
      return;
    }
    Rectangle localRectangle1 = getView().getBounds();
    if (!isOpaque()) {
      paramGraphics.clipRect(0, 0, width, height);
    }
    Object localObject1;
    if (backingStoreImage == null)
    {
      backingStoreImage = createImage(i, j);
      localObject1 = paramGraphics.getClipBounds();
      if ((width != i) || (height != j))
      {
        if (!isOpaque()) {
          paramGraphics.setClip(0, 0, Math.min(width, i), Math.min(height, j));
        } else {
          paramGraphics.setClip(0, 0, i, j);
        }
        paintViaBackingStore(paramGraphics, (Rectangle)localObject1);
      }
      else
      {
        paintViaBackingStore(paramGraphics);
      }
    }
    else if ((!scrollUnderway) || (lastPaintPosition.equals(getViewLocation())))
    {
      paintViaBackingStore(paramGraphics);
    }
    else
    {
      localObject1 = new Point();
      Point localPoint1 = new Point();
      Dimension localDimension = new Dimension();
      Rectangle localRectangle2 = new Rectangle();
      Point localPoint2 = getViewLocation();
      int k = x - lastPaintPosition.x;
      int m = y - lastPaintPosition.y;
      boolean bool = computeBlit(k, m, (Point)localObject1, localPoint1, localDimension, localRectangle2);
      if (!bool)
      {
        paintViaBackingStore(paramGraphics);
      }
      else
      {
        int n = x - x;
        int i1 = y - y;
        Rectangle localRectangle3 = paramGraphics.getClipBounds();
        paramGraphics.setClip(0, 0, i, j);
        Graphics localGraphics = getBackingStoreGraphics(paramGraphics);
        try
        {
          localGraphics.copyArea(x, y, width, height, n, i1);
          paramGraphics.setClip(x, y, width, height);
          Rectangle localRectangle4 = localRectangle1.intersection(localRectangle2);
          localGraphics.setClip(localRectangle4);
          super.paint(localGraphics);
          paramGraphics.drawImage(backingStoreImage, 0, 0, this);
        }
        finally
        {
          localGraphics.dispose();
        }
      }
    }
    lastPaintPosition = getViewLocation();
    scrollUnderway = false;
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = (getWidth() != paramInt3) || (getHeight() != paramInt4) ? 1 : 0;
    if (i != 0) {
      backingStoreImage = null;
    }
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((i != 0) || (viewChanged))
    {
      viewChanged = false;
      fireStateChanged();
    }
  }
  
  public void setScrollMode(int paramInt)
  {
    scrollMode = paramInt;
    backingStore = (paramInt == 2);
  }
  
  public int getScrollMode()
  {
    return scrollMode;
  }
  
  @Deprecated
  public boolean isBackingStoreEnabled()
  {
    return scrollMode == 2;
  }
  
  @Deprecated
  public void setBackingStoreEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {
      setScrollMode(2);
    } else {
      setScrollMode(1);
    }
  }
  
  private boolean isBlitting()
  {
    Component localComponent = getView();
    return (scrollMode == 1) && ((localComponent instanceof JComponent)) && (localComponent.isOpaque());
  }
  
  public Component getView()
  {
    return getComponentCount() > 0 ? getComponent(0) : null;
  }
  
  public void setView(Component paramComponent)
  {
    int i = getComponentCount();
    for (int j = i - 1; j >= 0; j--) {
      remove(getComponent(j));
    }
    isViewSizeSet = false;
    if (paramComponent != null)
    {
      super.addImpl(paramComponent, null, -1);
      viewListener = createViewListener();
      paramComponent.addComponentListener(viewListener);
    }
    if (hasHadValidView) {
      fireStateChanged();
    } else if (paramComponent != null) {
      hasHadValidView = true;
    }
    viewChanged = true;
    revalidate();
    repaint();
  }
  
  public Dimension getViewSize()
  {
    Component localComponent = getView();
    if (localComponent == null) {
      return new Dimension(0, 0);
    }
    if (isViewSizeSet) {
      return localComponent.getSize();
    }
    return localComponent.getPreferredSize();
  }
  
  public void setViewSize(Dimension paramDimension)
  {
    Component localComponent = getView();
    if (localComponent != null)
    {
      Dimension localDimension = localComponent.getSize();
      if (!paramDimension.equals(localDimension))
      {
        scrollUnderway = false;
        localComponent.setSize(paramDimension);
        isViewSizeSet = true;
        fireStateChanged();
      }
    }
  }
  
  public Point getViewPosition()
  {
    Component localComponent = getView();
    if (localComponent != null)
    {
      Point localPoint = localComponent.getLocation();
      x = (-x);
      y = (-y);
      return localPoint;
    }
    return new Point(0, 0);
  }
  
  public void setViewPosition(Point paramPoint)
  {
    Component localComponent = getView();
    if (localComponent == null) {
      return;
    }
    int k = x;
    int m = y;
    Object localObject1;
    int i;
    int j;
    if ((localComponent instanceof JComponent))
    {
      localObject1 = (JComponent)localComponent;
      i = ((JComponent)localObject1).getX();
      j = ((JComponent)localObject1).getY();
    }
    else
    {
      localObject1 = localComponent.getBounds();
      i = x;
      j = y;
    }
    int n = -k;
    int i1 = -m;
    if ((i != n) || (j != i1))
    {
      if ((!waitingForRepaint) && (isBlitting()) && (canUseWindowBlitter()))
      {
        RepaintManager localRepaintManager = RepaintManager.currentManager(this);
        JComponent localJComponent = (JComponent)localComponent;
        Rectangle localRectangle1 = localRepaintManager.getDirtyRegion(localJComponent);
        if ((localRectangle1 == null) || (!localRectangle1.contains(localJComponent.getVisibleRect())))
        {
          localRepaintManager.beginPaint();
          try
          {
            Graphics localGraphics = JComponent.safelyGetGraphics(this);
            flushViewDirtyRegion(localGraphics, localRectangle1);
            localComponent.setLocation(n, i1);
            Rectangle localRectangle2 = new Rectangle(0, 0, getWidth(), Math.min(getHeight(), localJComponent.getHeight()));
            localGraphics.setClip(localRectangle2);
            repaintAll = ((windowBlitPaint(localGraphics)) && (needsRepaintAfterBlit()));
            localGraphics.dispose();
            localRepaintManager.notifyRepaintPerformed(this, x, y, width, height);
            localRepaintManager.markCompletelyClean((JComponent)getParent());
            localRepaintManager.markCompletelyClean(this);
            localRepaintManager.markCompletelyClean(localJComponent);
          }
          finally
          {
            localRepaintManager.endPaint();
          }
        }
        else
        {
          localComponent.setLocation(n, i1);
          repaintAll = false;
        }
      }
      else
      {
        scrollUnderway = true;
        localComponent.setLocation(n, i1);
        repaintAll = false;
      }
      revalidate();
      fireStateChanged();
    }
  }
  
  public Rectangle getViewRect()
  {
    return new Rectangle(getViewPosition(), getExtentSize());
  }
  
  protected boolean computeBlit(int paramInt1, int paramInt2, Point paramPoint1, Point paramPoint2, Dimension paramDimension, Rectangle paramRectangle)
  {
    int i = Math.abs(paramInt1);
    int j = Math.abs(paramInt2);
    Dimension localDimension = getExtentSize();
    if ((paramInt1 == 0) && (paramInt2 != 0) && (j < height))
    {
      if (paramInt2 < 0)
      {
        y = (-paramInt2);
        y = 0;
        y = (height + paramInt2);
      }
      else
      {
        y = 0;
        y = paramInt2;
        y = 0;
      }
      x = (x = x = 0);
      width = width;
      height -= j;
      width = width;
      height = j;
      return true;
    }
    if ((paramInt2 == 0) && (paramInt1 != 0) && (i < width))
    {
      if (paramInt1 < 0)
      {
        x = (-paramInt1);
        x = 0;
        x = (width + paramInt1);
      }
      else
      {
        x = 0;
        x = paramInt1;
        x = 0;
      }
      y = (y = y = 0);
      width -= i;
      height = height;
      width = i;
      height = height;
      return true;
    }
    return false;
  }
  
  @Transient
  public Dimension getExtentSize()
  {
    return getSize();
  }
  
  public Dimension toViewCoordinates(Dimension paramDimension)
  {
    return new Dimension(paramDimension);
  }
  
  public Point toViewCoordinates(Point paramPoint)
  {
    return new Point(paramPoint);
  }
  
  public void setExtentSize(Dimension paramDimension)
  {
    Dimension localDimension = getExtentSize();
    if (!paramDimension.equals(localDimension))
    {
      setSize(paramDimension);
      fireStateChanged();
    }
  }
  
  protected ViewListener createViewListener()
  {
    return new ViewListener();
  }
  
  protected LayoutManager createLayoutManager()
  {
    return ViewportLayout.SHARED_INSTANCE;
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(changeEvent);
      }
    }
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Container localContainer = getParent();
    if (localContainer != null) {
      localContainer.repaint(paramLong, paramInt1 + getX(), paramInt2 + getY(), paramInt3, paramInt4);
    } else {
      super.repaint(paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected String paramString()
  {
    String str1 = isViewSizeSet ? "true" : "false";
    String str2 = lastPaintPosition != null ? lastPaintPosition.toString() : "";
    String str3 = scrollUnderway ? "true" : "false";
    return super.paramString() + ",isViewSizeSet=" + str1 + ",lastPaintPosition=" + str2 + ",scrollUnderway=" + str3;
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    super.firePropertyChange(paramString, paramObject1, paramObject2);
    if (paramString.equals(EnableWindowBlit)) {
      if (paramObject2 != null) {
        setScrollMode(1);
      } else {
        setScrollMode(0);
      }
    }
  }
  
  private boolean needsRepaintAfterBlit()
  {
    for (Container localContainer = getParent(); (localContainer != null) && (localContainer.isLightweight()); localContainer = localContainer.getParent()) {}
    if (localContainer != null)
    {
      ComponentPeer localComponentPeer = localContainer.getPeer();
      if ((localComponentPeer != null) && (localComponentPeer.canDetermineObscurity()) && (!localComponentPeer.isObscured())) {
        return false;
      }
    }
    return true;
  }
  
  private Timer createRepaintTimer()
  {
    Timer localTimer = new Timer(300, new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        if (waitingForRepaint) {
          repaint();
        }
      }
    });
    localTimer.setRepeats(false);
    return localTimer;
  }
  
  private void flushViewDirtyRegion(Graphics paramGraphics, Rectangle paramRectangle)
  {
    JComponent localJComponent = (JComponent)getView();
    if ((paramRectangle != null) && (width > 0) && (height > 0))
    {
      x += localJComponent.getX();
      y += localJComponent.getY();
      Rectangle localRectangle = paramGraphics.getClipBounds();
      if (localRectangle == null) {
        paramGraphics.setClip(0, 0, getWidth(), getHeight());
      }
      paramGraphics.clipRect(x, y, width, height);
      localRectangle = paramGraphics.getClipBounds();
      if ((width > 0) && (height > 0)) {
        paintView(paramGraphics);
      }
    }
  }
  
  private boolean windowBlitPaint(Graphics paramGraphics)
  {
    int i = getWidth();
    int j = getHeight();
    if ((i == 0) || (j == 0)) {
      return false;
    }
    RepaintManager localRepaintManager = RepaintManager.currentManager(this);
    JComponent localJComponent = (JComponent)getView();
    boolean bool1;
    if ((lastPaintPosition == null) || (lastPaintPosition.equals(getViewLocation())))
    {
      paintView(paramGraphics);
      bool1 = false;
    }
    else
    {
      Point localPoint1 = new Point();
      Point localPoint2 = new Point();
      Dimension localDimension = new Dimension();
      Rectangle localRectangle1 = new Rectangle();
      Point localPoint3 = getViewLocation();
      int k = x - lastPaintPosition.x;
      int m = y - lastPaintPosition.y;
      boolean bool2 = computeBlit(k, m, localPoint1, localPoint2, localDimension, localRectangle1);
      if (!bool2)
      {
        paintView(paramGraphics);
        bool1 = false;
      }
      else
      {
        Rectangle localRectangle2 = localJComponent.getBounds().intersection(localRectangle1);
        x -= localJComponent.getX();
        y -= localJComponent.getY();
        blitDoubleBuffered(localJComponent, paramGraphics, x, y, width, height, x, y, x, y, width, height);
        bool1 = true;
      }
    }
    lastPaintPosition = getViewLocation();
    return bool1;
  }
  
  private void blitDoubleBuffered(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    RepaintManager localRepaintManager = RepaintManager.currentManager(this);
    int i = paramInt7 - paramInt5;
    int j = paramInt8 - paramInt6;
    Composite localComposite = null;
    if ((paramGraphics instanceof Graphics2D))
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      localComposite = localGraphics2D.getComposite();
      localGraphics2D.setComposite(AlphaComposite.Src);
    }
    localRepaintManager.copyArea(this, paramGraphics, paramInt5, paramInt6, paramInt9, paramInt10, i, j, false);
    if (localComposite != null) {
      ((Graphics2D)paramGraphics).setComposite(localComposite);
    }
    int k = paramJComponent.getX();
    int m = paramJComponent.getY();
    paramGraphics.translate(k, m);
    paramGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
    paramJComponent.paintForceDoubleBuffered(paramGraphics);
    paramGraphics.translate(-k, -m);
  }
  
  private void paintView(Graphics paramGraphics)
  {
    Rectangle localRectangle = paramGraphics.getClipBounds();
    JComponent localJComponent = (JComponent)getView();
    if (localJComponent.getWidth() >= getWidth())
    {
      int i = localJComponent.getX();
      int j = localJComponent.getY();
      paramGraphics.translate(i, j);
      paramGraphics.setClip(x - i, y - j, width, height);
      localJComponent.paintForceDoubleBuffered(paramGraphics);
      paramGraphics.translate(-i, -j);
      paramGraphics.setClip(x, y, width, height);
    }
    else
    {
      try
      {
        inBlitPaint = true;
        paintForceDoubleBuffered(paramGraphics);
      }
      finally
      {
        inBlitPaint = false;
      }
    }
  }
  
  private boolean canUseWindowBlitter()
  {
    if ((!isShowing()) || ((!(getParent() instanceof JComponent)) && (!(getView() instanceof JComponent)))) {
      return false;
    }
    if (isPainting()) {
      return false;
    }
    Rectangle localRectangle1 = RepaintManager.currentManager(this).getDirtyRegion((JComponent)getParent());
    if ((localRectangle1 != null) && (width > 0) && (height > 0)) {
      return false;
    }
    Rectangle localRectangle2 = new Rectangle(0, 0, getWidth(), getHeight());
    Rectangle localRectangle3 = new Rectangle();
    Rectangle localRectangle4 = null;
    Object localObject2 = null;
    for (Object localObject1 = this; (localObject1 != null) && (isLightweightComponent((Component)localObject1)); localObject1 = ((Container)localObject1).getParent())
    {
      int i = ((Container)localObject1).getX();
      int j = ((Container)localObject1).getY();
      int k = ((Container)localObject1).getWidth();
      int m = ((Container)localObject1).getHeight();
      localRectangle3.setBounds(localRectangle2);
      SwingUtilities.computeIntersection(0, 0, k, m, localRectangle2);
      if (!localRectangle2.equals(localRectangle3)) {
        return false;
      }
      if ((localObject2 != null) && ((localObject1 instanceof JComponent)) && (!((JComponent)localObject1).isOptimizedDrawingEnabled()))
      {
        Component[] arrayOfComponent = ((Container)localObject1).getComponents();
        int n = 0;
        for (int i1 = arrayOfComponent.length - 1; i1 >= 0; i1--) {
          if (arrayOfComponent[i1] == localObject2)
          {
            n = i1 - 1;
            break;
          }
        }
        while (n >= 0)
        {
          localRectangle4 = arrayOfComponent[n].getBounds(localRectangle4);
          if (localRectangle4.intersects(localRectangle2)) {
            return false;
          }
          n--;
        }
      }
      x += i;
      y += j;
      localObject2 = localObject1;
    }
    return localObject1 != null;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJViewport();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJViewport
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJViewport()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.VIEWPORT;
    }
  }
  
  protected class ViewListener
    extends ComponentAdapter
    implements Serializable
  {
    protected ViewListener() {}
    
    public void componentResized(ComponentEvent paramComponentEvent)
    {
      fireStateChanged();
      revalidate();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JViewport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */