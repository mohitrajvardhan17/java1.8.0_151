package javax.swing;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.TooManyListenersException;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.AWTEventAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.reflect.misc.MethodUtil;
import sun.swing.SwingAccessor;
import sun.swing.SwingAccessor.JTextComponentAccessor;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class TransferHandler
  implements Serializable
{
  public static final int NONE = 0;
  public static final int COPY = 1;
  public static final int MOVE = 2;
  public static final int COPY_OR_MOVE = 3;
  public static final int LINK = 1073741824;
  private Image dragImage;
  private Point dragImageOffset;
  private String propertyName;
  private static SwingDragGestureRecognizer recognizer = null;
  static final Action cutAction = new TransferAction("cut");
  static final Action copyAction = new TransferAction("copy");
  static final Action pasteAction = new TransferAction("paste");
  
  public static Action getCutAction()
  {
    return cutAction;
  }
  
  public static Action getCopyAction()
  {
    return copyAction;
  }
  
  public static Action getPasteAction()
  {
    return pasteAction;
  }
  
  public TransferHandler(String paramString)
  {
    propertyName = paramString;
  }
  
  protected TransferHandler()
  {
    this(null);
  }
  
  public void setDragImage(Image paramImage)
  {
    dragImage = paramImage;
  }
  
  public Image getDragImage()
  {
    return dragImage;
  }
  
  public void setDragImageOffset(Point paramPoint)
  {
    dragImageOffset = new Point(paramPoint);
  }
  
  public Point getDragImageOffset()
  {
    if (dragImageOffset == null) {
      return new Point(0, 0);
    }
    return new Point(dragImageOffset);
  }
  
  public void exportAsDrag(JComponent paramJComponent, InputEvent paramInputEvent, int paramInt)
  {
    int i = getSourceActions(paramJComponent);
    if ((!(paramInputEvent instanceof MouseEvent)) || ((paramInt != 1) && (paramInt != 2) && (paramInt != 1073741824)) || ((i & paramInt) == 0)) {
      paramInt = 0;
    }
    if ((paramInt != 0) && (!GraphicsEnvironment.isHeadless()))
    {
      if (recognizer == null) {
        recognizer = new SwingDragGestureRecognizer(new DragHandler(null));
      }
      recognizer.gestured(paramJComponent, (MouseEvent)paramInputEvent, i, paramInt);
    }
    else
    {
      exportDone(paramJComponent, null, 0);
    }
  }
  
  public void exportToClipboard(JComponent paramJComponent, Clipboard paramClipboard, int paramInt)
    throws IllegalStateException
  {
    if (((paramInt == 1) || (paramInt == 2)) && ((getSourceActions(paramJComponent) & paramInt) != 0))
    {
      Transferable localTransferable = createTransferable(paramJComponent);
      if (localTransferable != null) {
        try
        {
          paramClipboard.setContents(localTransferable, null);
          exportDone(paramJComponent, localTransferable, paramInt);
          return;
        }
        catch (IllegalStateException localIllegalStateException)
        {
          exportDone(paramJComponent, localTransferable, 0);
          throw localIllegalStateException;
        }
      }
    }
    exportDone(paramJComponent, null, 0);
  }
  
  public boolean importData(TransferSupport paramTransferSupport)
  {
    return (paramTransferSupport.getComponent() instanceof JComponent) ? importData((JComponent)paramTransferSupport.getComponent(), paramTransferSupport.getTransferable()) : false;
  }
  
  public boolean importData(JComponent paramJComponent, Transferable paramTransferable)
  {
    PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(paramJComponent);
    if (localPropertyDescriptor != null)
    {
      Method localMethod = localPropertyDescriptor.getWriteMethod();
      if (localMethod == null) {
        return false;
      }
      Class[] arrayOfClass = localMethod.getParameterTypes();
      if (arrayOfClass.length != 1) {
        return false;
      }
      DataFlavor localDataFlavor = getPropertyDataFlavor(arrayOfClass[0], paramTransferable.getTransferDataFlavors());
      if (localDataFlavor != null) {
        try
        {
          Object localObject = paramTransferable.getTransferData(localDataFlavor);
          Object[] arrayOfObject = { localObject };
          MethodUtil.invoke(localMethod, paramJComponent, arrayOfObject);
          return true;
        }
        catch (Exception localException)
        {
          System.err.println("Invocation failed");
        }
      }
    }
    return false;
  }
  
  public boolean canImport(TransferSupport paramTransferSupport)
  {
    return (paramTransferSupport.getComponent() instanceof JComponent) ? canImport((JComponent)paramTransferSupport.getComponent(), paramTransferSupport.getDataFlavors()) : false;
  }
  
  public boolean canImport(JComponent paramJComponent, DataFlavor[] paramArrayOfDataFlavor)
  {
    PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(paramJComponent);
    if (localPropertyDescriptor != null)
    {
      Method localMethod = localPropertyDescriptor.getWriteMethod();
      if (localMethod == null) {
        return false;
      }
      Class[] arrayOfClass = localMethod.getParameterTypes();
      if (arrayOfClass.length != 1) {
        return false;
      }
      DataFlavor localDataFlavor = getPropertyDataFlavor(arrayOfClass[0], paramArrayOfDataFlavor);
      if (localDataFlavor != null) {
        return true;
      }
    }
    return false;
  }
  
  public int getSourceActions(JComponent paramJComponent)
  {
    PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(paramJComponent);
    if (localPropertyDescriptor != null) {
      return 1;
    }
    return 0;
  }
  
  public Icon getVisualRepresentation(Transferable paramTransferable)
  {
    return null;
  }
  
  protected Transferable createTransferable(JComponent paramJComponent)
  {
    PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(paramJComponent);
    if (localPropertyDescriptor != null) {
      return new PropertyTransferable(localPropertyDescriptor, paramJComponent);
    }
    return null;
  }
  
  protected void exportDone(JComponent paramJComponent, Transferable paramTransferable, int paramInt) {}
  
  private PropertyDescriptor getPropertyDescriptor(JComponent paramJComponent)
  {
    if (propertyName == null) {
      return null;
    }
    Class localClass = paramJComponent.getClass();
    BeanInfo localBeanInfo;
    try
    {
      localBeanInfo = Introspector.getBeanInfo(localClass);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      return null;
    }
    PropertyDescriptor[] arrayOfPropertyDescriptor = localBeanInfo.getPropertyDescriptors();
    for (int i = 0; i < arrayOfPropertyDescriptor.length; i++) {
      if (propertyName.equals(arrayOfPropertyDescriptor[i].getName()))
      {
        Method localMethod = arrayOfPropertyDescriptor[i].getReadMethod();
        if (localMethod != null)
        {
          Class[] arrayOfClass = localMethod.getParameterTypes();
          if ((arrayOfClass == null) || (arrayOfClass.length == 0)) {
            return arrayOfPropertyDescriptor[i];
          }
        }
      }
    }
    return null;
  }
  
  private DataFlavor getPropertyDataFlavor(Class<?> paramClass, DataFlavor[] paramArrayOfDataFlavor)
  {
    for (int i = 0; i < paramArrayOfDataFlavor.length; i++)
    {
      DataFlavor localDataFlavor = paramArrayOfDataFlavor[i];
      if (("application".equals(localDataFlavor.getPrimaryType())) && ("x-java-jvm-local-objectref".equals(localDataFlavor.getSubType())) && (paramClass.isAssignableFrom(localDataFlavor.getRepresentationClass()))) {
        return localDataFlavor;
      }
    }
    return null;
  }
  
  private static DropTargetListener getDropTargetListener()
  {
    synchronized (DropHandler.class)
    {
      DropHandler localDropHandler = (DropHandler)AppContext.getAppContext().get(DropHandler.class);
      if (localDropHandler == null)
      {
        localDropHandler = new DropHandler(null);
        AppContext.getAppContext().put(DropHandler.class, localDropHandler);
      }
      return localDropHandler;
    }
  }
  
  private static class DragHandler
    implements DragGestureListener, DragSourceListener
  {
    private boolean scrolls;
    
    private DragHandler() {}
    
    public void dragGestureRecognized(DragGestureEvent paramDragGestureEvent)
    {
      JComponent localJComponent = (JComponent)paramDragGestureEvent.getComponent();
      TransferHandler localTransferHandler = localJComponent.getTransferHandler();
      Transferable localTransferable = localTransferHandler.createTransferable(localJComponent);
      if (localTransferable != null)
      {
        scrolls = localJComponent.getAutoscrolls();
        localJComponent.setAutoscrolls(false);
        try
        {
          Image localImage = localTransferHandler.getDragImage();
          if (localImage == null) {
            paramDragGestureEvent.startDrag(null, localTransferable, this);
          } else {
            paramDragGestureEvent.startDrag(null, localImage, localTransferHandler.getDragImageOffset(), localTransferable, this);
          }
          return;
        }
        catch (RuntimeException localRuntimeException)
        {
          localJComponent.setAutoscrolls(scrolls);
        }
      }
      localTransferHandler.exportDone(localJComponent, localTransferable, 0);
    }
    
    public void dragEnter(DragSourceDragEvent paramDragSourceDragEvent) {}
    
    public void dragOver(DragSourceDragEvent paramDragSourceDragEvent) {}
    
    public void dragExit(DragSourceEvent paramDragSourceEvent) {}
    
    public void dragDropEnd(DragSourceDropEvent paramDragSourceDropEvent)
    {
      DragSourceContext localDragSourceContext = paramDragSourceDropEvent.getDragSourceContext();
      JComponent localJComponent = (JComponent)localDragSourceContext.getComponent();
      if (paramDragSourceDropEvent.getDropSuccess()) {
        localJComponent.getTransferHandler().exportDone(localJComponent, localDragSourceContext.getTransferable(), paramDragSourceDropEvent.getDropAction());
      } else {
        localJComponent.getTransferHandler().exportDone(localJComponent, localDragSourceContext.getTransferable(), 0);
      }
      localJComponent.setAutoscrolls(scrolls);
    }
    
    public void dropActionChanged(DragSourceDragEvent paramDragSourceDragEvent) {}
  }
  
  private static class DropHandler
    implements DropTargetListener, Serializable, ActionListener
  {
    private Timer timer;
    private Point lastPosition;
    private Rectangle outer = new Rectangle();
    private Rectangle inner = new Rectangle();
    private int hysteresis = 10;
    private Component component;
    private Object state;
    private TransferHandler.TransferSupport support = new TransferHandler.TransferSupport(null, (DropTargetEvent)null, null);
    private static final int AUTOSCROLL_INSET = 10;
    
    private DropHandler() {}
    
    private void updateAutoscrollRegion(JComponent paramJComponent)
    {
      Rectangle localRectangle = paramJComponent.getVisibleRect();
      outer.setBounds(x, y, width, height);
      Insets localInsets = new Insets(0, 0, 0, 0);
      if ((paramJComponent instanceof Scrollable))
      {
        int i = 20;
        if (width >= i) {
          left = (right = 10);
        }
        if (height >= i) {
          top = (bottom = 10);
        }
      }
      inner.setBounds(x + left, y + top, width - (left + right), height - (top + bottom));
    }
    
    private void autoscroll(JComponent paramJComponent, Point paramPoint)
    {
      if ((paramJComponent instanceof Scrollable))
      {
        Scrollable localScrollable = (Scrollable)paramJComponent;
        int i;
        Rectangle localRectangle;
        if (y < inner.y)
        {
          i = localScrollable.getScrollableUnitIncrement(outer, 1, -1);
          localRectangle = new Rectangle(inner.x, outer.y - i, inner.width, i);
          paramJComponent.scrollRectToVisible(localRectangle);
        }
        else if (y > inner.y + inner.height)
        {
          i = localScrollable.getScrollableUnitIncrement(outer, 1, 1);
          localRectangle = new Rectangle(inner.x, outer.y + outer.height, inner.width, i);
          paramJComponent.scrollRectToVisible(localRectangle);
        }
        if (x < inner.x)
        {
          i = localScrollable.getScrollableUnitIncrement(outer, 0, -1);
          localRectangle = new Rectangle(outer.x - i, inner.y, i, inner.height);
          paramJComponent.scrollRectToVisible(localRectangle);
        }
        else if (x > inner.x + inner.width)
        {
          i = localScrollable.getScrollableUnitIncrement(outer, 0, 1);
          localRectangle = new Rectangle(outer.x + outer.width, inner.y, i, inner.height);
          paramJComponent.scrollRectToVisible(localRectangle);
        }
      }
    }
    
    private void initPropertiesIfNecessary()
    {
      if (timer == null)
      {
        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        Integer localInteger = (Integer)localToolkit.getDesktopProperty("DnD.Autoscroll.interval");
        timer = new Timer(localInteger == null ? 100 : localInteger.intValue(), this);
        localInteger = (Integer)localToolkit.getDesktopProperty("DnD.Autoscroll.initialDelay");
        timer.setInitialDelay(localInteger == null ? 100 : localInteger.intValue());
        localInteger = (Integer)localToolkit.getDesktopProperty("DnD.Autoscroll.cursorHysteresis");
        if (localInteger != null) {
          hysteresis = localInteger.intValue();
        }
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      updateAutoscrollRegion((JComponent)component);
      if ((outer.contains(lastPosition)) && (!inner.contains(lastPosition))) {
        autoscroll((JComponent)component, lastPosition);
      }
    }
    
    private void setComponentDropLocation(TransferHandler.TransferSupport paramTransferSupport, boolean paramBoolean)
    {
      TransferHandler.DropLocation localDropLocation = paramTransferSupport == null ? null : paramTransferSupport.getDropLocation();
      if (SunToolkit.isInstanceOf(component, "javax.swing.text.JTextComponent")) {
        state = SwingAccessor.getJTextComponentAccessor().setDropLocation((JTextComponent)component, localDropLocation, state, paramBoolean);
      } else if ((component instanceof JComponent)) {
        state = ((JComponent)component).setDropLocation(localDropLocation, state, paramBoolean);
      }
    }
    
    private void handleDrag(DropTargetDragEvent paramDropTargetDragEvent)
    {
      TransferHandler localTransferHandler = ((TransferHandler.HasGetTransferHandler)component).getTransferHandler();
      if (localTransferHandler == null)
      {
        paramDropTargetDragEvent.rejectDrag();
        setComponentDropLocation(null, false);
        return;
      }
      TransferHandler.TransferSupport.access$500(support, component, paramDropTargetDragEvent);
      boolean bool1 = localTransferHandler.canImport(support);
      if (bool1) {
        paramDropTargetDragEvent.acceptDrag(support.getDropAction());
      } else {
        paramDropTargetDragEvent.rejectDrag();
      }
      boolean bool2 = TransferHandler.TransferSupport.access$600(support) ? TransferHandler.TransferSupport.access$700(support) : bool1;
      setComponentDropLocation(bool2 ? support : null, false);
    }
    
    public void dragEnter(DropTargetDragEvent paramDropTargetDragEvent)
    {
      state = null;
      component = paramDropTargetDragEvent.getDropTargetContext().getComponent();
      handleDrag(paramDropTargetDragEvent);
      if ((component instanceof JComponent))
      {
        lastPosition = paramDropTargetDragEvent.getLocation();
        updateAutoscrollRegion((JComponent)component);
        initPropertiesIfNecessary();
      }
    }
    
    public void dragOver(DropTargetDragEvent paramDropTargetDragEvent)
    {
      handleDrag(paramDropTargetDragEvent);
      if (!(component instanceof JComponent)) {
        return;
      }
      Point localPoint = paramDropTargetDragEvent.getLocation();
      if ((Math.abs(x - lastPosition.x) > hysteresis) || (Math.abs(y - lastPosition.y) > hysteresis))
      {
        if (timer.isRunning()) {
          timer.stop();
        }
      }
      else if (!timer.isRunning()) {
        timer.start();
      }
      lastPosition = localPoint;
    }
    
    public void dragExit(DropTargetEvent paramDropTargetEvent)
    {
      cleanup(false);
    }
    
    public void drop(DropTargetDropEvent paramDropTargetDropEvent)
    {
      TransferHandler localTransferHandler = ((TransferHandler.HasGetTransferHandler)component).getTransferHandler();
      if (localTransferHandler == null)
      {
        paramDropTargetDropEvent.rejectDrop();
        cleanup(false);
        return;
      }
      TransferHandler.TransferSupport.access$500(support, component, paramDropTargetDropEvent);
      boolean bool1 = localTransferHandler.canImport(support);
      if (bool1)
      {
        paramDropTargetDropEvent.acceptDrop(support.getDropAction());
        boolean bool2 = TransferHandler.TransferSupport.access$600(support) ? TransferHandler.TransferSupport.access$700(support) : bool1;
        setComponentDropLocation(bool2 ? support : null, false);
        boolean bool3;
        try
        {
          bool3 = localTransferHandler.importData(support);
        }
        catch (RuntimeException localRuntimeException)
        {
          bool3 = false;
        }
        paramDropTargetDropEvent.dropComplete(bool3);
        cleanup(bool3);
      }
      else
      {
        paramDropTargetDropEvent.rejectDrop();
        cleanup(false);
      }
    }
    
    public void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent)
    {
      if (component == null) {
        return;
      }
      handleDrag(paramDropTargetDragEvent);
    }
    
    private void cleanup(boolean paramBoolean)
    {
      setComponentDropLocation(null, paramBoolean);
      if ((component instanceof JComponent)) {
        ((JComponent)component).dndDone();
      }
      if (timer != null) {
        timer.stop();
      }
      state = null;
      component = null;
      lastPosition = null;
    }
  }
  
  public static class DropLocation
  {
    private final Point dropPoint;
    
    protected DropLocation(Point paramPoint)
    {
      if (paramPoint == null) {
        throw new IllegalArgumentException("Point cannot be null");
      }
      dropPoint = new Point(paramPoint);
    }
    
    public final Point getDropPoint()
    {
      return new Point(dropPoint);
    }
    
    public String toString()
    {
      return getClass().getName() + "[dropPoint=" + dropPoint + "]";
    }
  }
  
  static abstract interface HasGetTransferHandler
  {
    public abstract TransferHandler getTransferHandler();
  }
  
  static class PropertyTransferable
    implements Transferable
  {
    JComponent component;
    PropertyDescriptor property;
    
    PropertyTransferable(PropertyDescriptor paramPropertyDescriptor, JComponent paramJComponent)
    {
      property = paramPropertyDescriptor;
      component = paramJComponent;
    }
    
    public DataFlavor[] getTransferDataFlavors()
    {
      DataFlavor[] arrayOfDataFlavor = new DataFlavor[1];
      Class localClass = property.getPropertyType();
      String str = "application/x-java-jvm-local-objectref;class=" + localClass.getName();
      try
      {
        arrayOfDataFlavor[0] = new DataFlavor(str);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        arrayOfDataFlavor = new DataFlavor[0];
      }
      return arrayOfDataFlavor;
    }
    
    public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
    {
      Class localClass = property.getPropertyType();
      return ("application".equals(paramDataFlavor.getPrimaryType())) && ("x-java-jvm-local-objectref".equals(paramDataFlavor.getSubType())) && (paramDataFlavor.getRepresentationClass().isAssignableFrom(localClass));
    }
    
    public Object getTransferData(DataFlavor paramDataFlavor)
      throws UnsupportedFlavorException, IOException
    {
      if (!isDataFlavorSupported(paramDataFlavor)) {
        throw new UnsupportedFlavorException(paramDataFlavor);
      }
      Method localMethod = property.getReadMethod();
      Object localObject = null;
      try
      {
        localObject = MethodUtil.invoke(localMethod, component, (Object[])null);
      }
      catch (Exception localException)
      {
        throw new IOException("Property read failed: " + property.getName());
      }
      return localObject;
    }
  }
  
  private static class SwingDragGestureRecognizer
    extends DragGestureRecognizer
  {
    SwingDragGestureRecognizer(DragGestureListener paramDragGestureListener)
    {
      super(null, 0, paramDragGestureListener);
    }
    
    void gestured(JComponent paramJComponent, MouseEvent paramMouseEvent, int paramInt1, int paramInt2)
    {
      setComponent(paramJComponent);
      setSourceActions(paramInt1);
      appendEvent(paramMouseEvent);
      fireDragGestureRecognized(paramInt2, paramMouseEvent.getPoint());
    }
    
    protected void registerListeners() {}
    
    protected void unregisterListeners() {}
  }
  
  static class SwingDropTarget
    extends DropTarget
    implements UIResource
  {
    private EventListenerList listenerList;
    
    SwingDropTarget(Component paramComponent)
    {
      super(1073741827, null);
      try
      {
        super.addDropTargetListener(TransferHandler.access$200());
      }
      catch (TooManyListenersException localTooManyListenersException) {}
    }
    
    public void addDropTargetListener(DropTargetListener paramDropTargetListener)
      throws TooManyListenersException
    {
      if (listenerList == null) {
        listenerList = new EventListenerList();
      }
      listenerList.add(DropTargetListener.class, paramDropTargetListener);
    }
    
    public void removeDropTargetListener(DropTargetListener paramDropTargetListener)
    {
      if (listenerList != null) {
        listenerList.remove(DropTargetListener.class, paramDropTargetListener);
      }
    }
    
    public void dragEnter(DropTargetDragEvent paramDropTargetDragEvent)
    {
      super.dragEnter(paramDropTargetDragEvent);
      if (listenerList != null)
      {
        Object[] arrayOfObject = listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class) {
            ((DropTargetListener)arrayOfObject[(i + 1)]).dragEnter(paramDropTargetDragEvent);
          }
        }
      }
    }
    
    public void dragOver(DropTargetDragEvent paramDropTargetDragEvent)
    {
      super.dragOver(paramDropTargetDragEvent);
      if (listenerList != null)
      {
        Object[] arrayOfObject = listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class) {
            ((DropTargetListener)arrayOfObject[(i + 1)]).dragOver(paramDropTargetDragEvent);
          }
        }
      }
    }
    
    public void dragExit(DropTargetEvent paramDropTargetEvent)
    {
      super.dragExit(paramDropTargetEvent);
      Object localObject;
      if (listenerList != null)
      {
        localObject = listenerList.getListenerList();
        for (int i = localObject.length - 2; i >= 0; i -= 2) {
          if (localObject[i] == DropTargetListener.class) {
            ((DropTargetListener)localObject[(i + 1)]).dragExit(paramDropTargetEvent);
          }
        }
      }
      if (!isActive())
      {
        localObject = TransferHandler.access$200();
        if ((localObject != null) && ((localObject instanceof TransferHandler.DropHandler))) {
          ((TransferHandler.DropHandler)localObject).cleanup(false);
        }
      }
    }
    
    public void drop(DropTargetDropEvent paramDropTargetDropEvent)
    {
      super.drop(paramDropTargetDropEvent);
      if (listenerList != null)
      {
        Object[] arrayOfObject = listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class) {
            ((DropTargetListener)arrayOfObject[(i + 1)]).drop(paramDropTargetDropEvent);
          }
        }
      }
    }
    
    public void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent)
    {
      super.dropActionChanged(paramDropTargetDragEvent);
      if (listenerList != null)
      {
        Object[] arrayOfObject = listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class) {
            ((DropTargetListener)arrayOfObject[(i + 1)]).dropActionChanged(paramDropTargetDragEvent);
          }
        }
      }
    }
  }
  
  static class TransferAction
    extends UIAction
    implements UIResource
  {
    private static final JavaSecurityAccess javaSecurityAccess = ;
    private static Object SandboxClipboardKey = new Object();
    
    TransferAction(String paramString)
    {
      super();
    }
    
    public boolean isEnabled(Object paramObject)
    {
      return (!(paramObject instanceof JComponent)) || (((JComponent)paramObject).getTransferHandler() != null);
    }
    
    public void actionPerformed(final ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      final PrivilegedAction local1 = new PrivilegedAction()
      {
        public Void run()
        {
          TransferHandler.TransferAction.this.actionPerformedImpl(paramActionEvent);
          return null;
        }
      };
      AccessControlContext localAccessControlContext1 = AccessController.getContext();
      AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext((Component)localObject);
      final AccessControlContext localAccessControlContext3 = AWTAccessor.getAWTEventAccessor().getAccessControlContext(paramActionEvent);
      if (localAccessControlContext2 == null) {
        javaSecurityAccess.doIntersectionPrivilege(local1, localAccessControlContext1, localAccessControlContext3);
      } else {
        javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
        {
          public Void run()
          {
            TransferHandler.TransferAction.javaSecurityAccess.doIntersectionPrivilege(local1, localAccessControlContext3);
            return null;
          }
        }, localAccessControlContext1, localAccessControlContext2);
      }
    }
    
    private void actionPerformedImpl(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      if ((localObject instanceof JComponent))
      {
        JComponent localJComponent = (JComponent)localObject;
        TransferHandler localTransferHandler = localJComponent.getTransferHandler();
        Clipboard localClipboard = getClipboard(localJComponent);
        String str = (String)getValue("Name");
        Transferable localTransferable = null;
        try
        {
          if ((localClipboard != null) && (localTransferHandler != null) && (str != null)) {
            if ("cut".equals(str)) {
              localTransferHandler.exportToClipboard(localJComponent, localClipboard, 2);
            } else if ("copy".equals(str)) {
              localTransferHandler.exportToClipboard(localJComponent, localClipboard, 1);
            } else if ("paste".equals(str)) {
              localTransferable = localClipboard.getContents(null);
            }
          }
        }
        catch (IllegalStateException localIllegalStateException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJComponent);
          return;
        }
        if (localTransferable != null) {
          localTransferHandler.importData(new TransferHandler.TransferSupport(localJComponent, localTransferable));
        }
      }
    }
    
    private Clipboard getClipboard(JComponent paramJComponent)
    {
      if (SwingUtilities2.canAccessSystemClipboard()) {
        return paramJComponent.getToolkit().getSystemClipboard();
      }
      Clipboard localClipboard = (Clipboard)AppContext.getAppContext().get(SandboxClipboardKey);
      if (localClipboard == null)
      {
        localClipboard = new Clipboard("Sandboxed Component Clipboard");
        AppContext.getAppContext().put(SandboxClipboardKey, localClipboard);
      }
      return localClipboard;
    }
  }
  
  public static final class TransferSupport
  {
    private boolean isDrop;
    private Component component;
    private boolean showDropLocationIsSet;
    private boolean showDropLocation;
    private int dropAction = -1;
    private Object source;
    private TransferHandler.DropLocation dropLocation;
    
    private TransferSupport(Component paramComponent, DropTargetEvent paramDropTargetEvent)
    {
      isDrop = true;
      setDNDVariables(paramComponent, paramDropTargetEvent);
    }
    
    public TransferSupport(Component paramComponent, Transferable paramTransferable)
    {
      if (paramComponent == null) {
        throw new NullPointerException("component is null");
      }
      if (paramTransferable == null) {
        throw new NullPointerException("transferable is null");
      }
      isDrop = false;
      component = paramComponent;
      source = paramTransferable;
    }
    
    private void setDNDVariables(Component paramComponent, DropTargetEvent paramDropTargetEvent)
    {
      assert (isDrop);
      component = paramComponent;
      source = paramDropTargetEvent;
      dropLocation = null;
      dropAction = -1;
      showDropLocationIsSet = false;
      if (source == null) {
        return;
      }
      assert (((source instanceof DropTargetDragEvent)) || ((source instanceof DropTargetDropEvent)));
      Point localPoint = (source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)source).getLocation() : ((DropTargetDropEvent)source).getLocation();
      if (SunToolkit.isInstanceOf(paramComponent, "javax.swing.text.JTextComponent")) {
        dropLocation = SwingAccessor.getJTextComponentAccessor().dropLocationForPoint((JTextComponent)paramComponent, localPoint);
      } else if ((paramComponent instanceof JComponent)) {
        dropLocation = ((JComponent)paramComponent).dropLocationForPoint(localPoint);
      }
    }
    
    public boolean isDrop()
    {
      return isDrop;
    }
    
    public Component getComponent()
    {
      return component;
    }
    
    private void assureIsDrop()
    {
      if (!isDrop) {
        throw new IllegalStateException("Not a drop");
      }
    }
    
    public TransferHandler.DropLocation getDropLocation()
    {
      assureIsDrop();
      if (dropLocation == null)
      {
        Point localPoint = (source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)source).getLocation() : ((DropTargetDropEvent)source).getLocation();
        dropLocation = new TransferHandler.DropLocation(localPoint);
      }
      return dropLocation;
    }
    
    public void setShowDropLocation(boolean paramBoolean)
    {
      assureIsDrop();
      showDropLocation = paramBoolean;
      showDropLocationIsSet = true;
    }
    
    public void setDropAction(int paramInt)
    {
      assureIsDrop();
      int i = paramInt & getSourceDropActions();
      if ((i != 1) && (i != 2) && (i != 1073741824)) {
        throw new IllegalArgumentException("unsupported drop action: " + paramInt);
      }
      dropAction = paramInt;
    }
    
    public int getDropAction()
    {
      return dropAction == -1 ? getUserDropAction() : dropAction;
    }
    
    public int getUserDropAction()
    {
      assureIsDrop();
      return (source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)source).getDropAction() : ((DropTargetDropEvent)source).getDropAction();
    }
    
    public int getSourceDropActions()
    {
      assureIsDrop();
      return (source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)source).getSourceActions() : ((DropTargetDropEvent)source).getSourceActions();
    }
    
    public DataFlavor[] getDataFlavors()
    {
      if (isDrop)
      {
        if ((source instanceof DropTargetDragEvent)) {
          return ((DropTargetDragEvent)source).getCurrentDataFlavors();
        }
        return ((DropTargetDropEvent)source).getCurrentDataFlavors();
      }
      return ((Transferable)source).getTransferDataFlavors();
    }
    
    public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
    {
      if (isDrop)
      {
        if ((source instanceof DropTargetDragEvent)) {
          return ((DropTargetDragEvent)source).isDataFlavorSupported(paramDataFlavor);
        }
        return ((DropTargetDropEvent)source).isDataFlavorSupported(paramDataFlavor);
      }
      return ((Transferable)source).isDataFlavorSupported(paramDataFlavor);
    }
    
    public Transferable getTransferable()
    {
      if (isDrop)
      {
        if ((source instanceof DropTargetDragEvent)) {
          return ((DropTargetDragEvent)source).getTransferable();
        }
        return ((DropTargetDropEvent)source).getTransferable();
      }
      return (Transferable)source;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\TransferHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */