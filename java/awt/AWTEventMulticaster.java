package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;

public class AWTEventMulticaster
  implements ComponentListener, ContainerListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, WindowListener, WindowFocusListener, WindowStateListener, ActionListener, ItemListener, AdjustmentListener, TextListener, InputMethodListener, HierarchyListener, HierarchyBoundsListener, MouseWheelListener
{
  protected final EventListener a;
  protected final EventListener b;
  
  protected AWTEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    a = paramEventListener1;
    b = paramEventListener2;
  }
  
  protected EventListener remove(EventListener paramEventListener)
  {
    if (paramEventListener == a) {
      return b;
    }
    if (paramEventListener == b) {
      return a;
    }
    EventListener localEventListener1 = removeInternal(a, paramEventListener);
    EventListener localEventListener2 = removeInternal(b, paramEventListener);
    if ((localEventListener1 == a) && (localEventListener2 == b)) {
      return this;
    }
    return addInternal(localEventListener1, localEventListener2);
  }
  
  public void componentResized(ComponentEvent paramComponentEvent)
  {
    ((ComponentListener)a).componentResized(paramComponentEvent);
    ((ComponentListener)b).componentResized(paramComponentEvent);
  }
  
  public void componentMoved(ComponentEvent paramComponentEvent)
  {
    ((ComponentListener)a).componentMoved(paramComponentEvent);
    ((ComponentListener)b).componentMoved(paramComponentEvent);
  }
  
  public void componentShown(ComponentEvent paramComponentEvent)
  {
    ((ComponentListener)a).componentShown(paramComponentEvent);
    ((ComponentListener)b).componentShown(paramComponentEvent);
  }
  
  public void componentHidden(ComponentEvent paramComponentEvent)
  {
    ((ComponentListener)a).componentHidden(paramComponentEvent);
    ((ComponentListener)b).componentHidden(paramComponentEvent);
  }
  
  public void componentAdded(ContainerEvent paramContainerEvent)
  {
    ((ContainerListener)a).componentAdded(paramContainerEvent);
    ((ContainerListener)b).componentAdded(paramContainerEvent);
  }
  
  public void componentRemoved(ContainerEvent paramContainerEvent)
  {
    ((ContainerListener)a).componentRemoved(paramContainerEvent);
    ((ContainerListener)b).componentRemoved(paramContainerEvent);
  }
  
  public void focusGained(FocusEvent paramFocusEvent)
  {
    ((FocusListener)a).focusGained(paramFocusEvent);
    ((FocusListener)b).focusGained(paramFocusEvent);
  }
  
  public void focusLost(FocusEvent paramFocusEvent)
  {
    ((FocusListener)a).focusLost(paramFocusEvent);
    ((FocusListener)b).focusLost(paramFocusEvent);
  }
  
  public void keyTyped(KeyEvent paramKeyEvent)
  {
    ((KeyListener)a).keyTyped(paramKeyEvent);
    ((KeyListener)b).keyTyped(paramKeyEvent);
  }
  
  public void keyPressed(KeyEvent paramKeyEvent)
  {
    ((KeyListener)a).keyPressed(paramKeyEvent);
    ((KeyListener)b).keyPressed(paramKeyEvent);
  }
  
  public void keyReleased(KeyEvent paramKeyEvent)
  {
    ((KeyListener)a).keyReleased(paramKeyEvent);
    ((KeyListener)b).keyReleased(paramKeyEvent);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    ((MouseListener)a).mouseClicked(paramMouseEvent);
    ((MouseListener)b).mouseClicked(paramMouseEvent);
  }
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    ((MouseListener)a).mousePressed(paramMouseEvent);
    ((MouseListener)b).mousePressed(paramMouseEvent);
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    ((MouseListener)a).mouseReleased(paramMouseEvent);
    ((MouseListener)b).mouseReleased(paramMouseEvent);
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent)
  {
    ((MouseListener)a).mouseEntered(paramMouseEvent);
    ((MouseListener)b).mouseEntered(paramMouseEvent);
  }
  
  public void mouseExited(MouseEvent paramMouseEvent)
  {
    ((MouseListener)a).mouseExited(paramMouseEvent);
    ((MouseListener)b).mouseExited(paramMouseEvent);
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    ((MouseMotionListener)a).mouseDragged(paramMouseEvent);
    ((MouseMotionListener)b).mouseDragged(paramMouseEvent);
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent)
  {
    ((MouseMotionListener)a).mouseMoved(paramMouseEvent);
    ((MouseMotionListener)b).mouseMoved(paramMouseEvent);
  }
  
  public void windowOpened(WindowEvent paramWindowEvent)
  {
    ((WindowListener)a).windowOpened(paramWindowEvent);
    ((WindowListener)b).windowOpened(paramWindowEvent);
  }
  
  public void windowClosing(WindowEvent paramWindowEvent)
  {
    ((WindowListener)a).windowClosing(paramWindowEvent);
    ((WindowListener)b).windowClosing(paramWindowEvent);
  }
  
  public void windowClosed(WindowEvent paramWindowEvent)
  {
    ((WindowListener)a).windowClosed(paramWindowEvent);
    ((WindowListener)b).windowClosed(paramWindowEvent);
  }
  
  public void windowIconified(WindowEvent paramWindowEvent)
  {
    ((WindowListener)a).windowIconified(paramWindowEvent);
    ((WindowListener)b).windowIconified(paramWindowEvent);
  }
  
  public void windowDeiconified(WindowEvent paramWindowEvent)
  {
    ((WindowListener)a).windowDeiconified(paramWindowEvent);
    ((WindowListener)b).windowDeiconified(paramWindowEvent);
  }
  
  public void windowActivated(WindowEvent paramWindowEvent)
  {
    ((WindowListener)a).windowActivated(paramWindowEvent);
    ((WindowListener)b).windowActivated(paramWindowEvent);
  }
  
  public void windowDeactivated(WindowEvent paramWindowEvent)
  {
    ((WindowListener)a).windowDeactivated(paramWindowEvent);
    ((WindowListener)b).windowDeactivated(paramWindowEvent);
  }
  
  public void windowStateChanged(WindowEvent paramWindowEvent)
  {
    ((WindowStateListener)a).windowStateChanged(paramWindowEvent);
    ((WindowStateListener)b).windowStateChanged(paramWindowEvent);
  }
  
  public void windowGainedFocus(WindowEvent paramWindowEvent)
  {
    ((WindowFocusListener)a).windowGainedFocus(paramWindowEvent);
    ((WindowFocusListener)b).windowGainedFocus(paramWindowEvent);
  }
  
  public void windowLostFocus(WindowEvent paramWindowEvent)
  {
    ((WindowFocusListener)a).windowLostFocus(paramWindowEvent);
    ((WindowFocusListener)b).windowLostFocus(paramWindowEvent);
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    ((ActionListener)a).actionPerformed(paramActionEvent);
    ((ActionListener)b).actionPerformed(paramActionEvent);
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent)
  {
    ((ItemListener)a).itemStateChanged(paramItemEvent);
    ((ItemListener)b).itemStateChanged(paramItemEvent);
  }
  
  public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent)
  {
    ((AdjustmentListener)a).adjustmentValueChanged(paramAdjustmentEvent);
    ((AdjustmentListener)b).adjustmentValueChanged(paramAdjustmentEvent);
  }
  
  public void textValueChanged(TextEvent paramTextEvent)
  {
    ((TextListener)a).textValueChanged(paramTextEvent);
    ((TextListener)b).textValueChanged(paramTextEvent);
  }
  
  public void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent)
  {
    ((InputMethodListener)a).inputMethodTextChanged(paramInputMethodEvent);
    ((InputMethodListener)b).inputMethodTextChanged(paramInputMethodEvent);
  }
  
  public void caretPositionChanged(InputMethodEvent paramInputMethodEvent)
  {
    ((InputMethodListener)a).caretPositionChanged(paramInputMethodEvent);
    ((InputMethodListener)b).caretPositionChanged(paramInputMethodEvent);
  }
  
  public void hierarchyChanged(HierarchyEvent paramHierarchyEvent)
  {
    ((HierarchyListener)a).hierarchyChanged(paramHierarchyEvent);
    ((HierarchyListener)b).hierarchyChanged(paramHierarchyEvent);
  }
  
  public void ancestorMoved(HierarchyEvent paramHierarchyEvent)
  {
    ((HierarchyBoundsListener)a).ancestorMoved(paramHierarchyEvent);
    ((HierarchyBoundsListener)b).ancestorMoved(paramHierarchyEvent);
  }
  
  public void ancestorResized(HierarchyEvent paramHierarchyEvent)
  {
    ((HierarchyBoundsListener)a).ancestorResized(paramHierarchyEvent);
    ((HierarchyBoundsListener)b).ancestorResized(paramHierarchyEvent);
  }
  
  public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent)
  {
    ((MouseWheelListener)a).mouseWheelMoved(paramMouseWheelEvent);
    ((MouseWheelListener)b).mouseWheelMoved(paramMouseWheelEvent);
  }
  
  public static ComponentListener add(ComponentListener paramComponentListener1, ComponentListener paramComponentListener2)
  {
    return (ComponentListener)addInternal(paramComponentListener1, paramComponentListener2);
  }
  
  public static ContainerListener add(ContainerListener paramContainerListener1, ContainerListener paramContainerListener2)
  {
    return (ContainerListener)addInternal(paramContainerListener1, paramContainerListener2);
  }
  
  public static FocusListener add(FocusListener paramFocusListener1, FocusListener paramFocusListener2)
  {
    return (FocusListener)addInternal(paramFocusListener1, paramFocusListener2);
  }
  
  public static KeyListener add(KeyListener paramKeyListener1, KeyListener paramKeyListener2)
  {
    return (KeyListener)addInternal(paramKeyListener1, paramKeyListener2);
  }
  
  public static MouseListener add(MouseListener paramMouseListener1, MouseListener paramMouseListener2)
  {
    return (MouseListener)addInternal(paramMouseListener1, paramMouseListener2);
  }
  
  public static MouseMotionListener add(MouseMotionListener paramMouseMotionListener1, MouseMotionListener paramMouseMotionListener2)
  {
    return (MouseMotionListener)addInternal(paramMouseMotionListener1, paramMouseMotionListener2);
  }
  
  public static WindowListener add(WindowListener paramWindowListener1, WindowListener paramWindowListener2)
  {
    return (WindowListener)addInternal(paramWindowListener1, paramWindowListener2);
  }
  
  public static WindowStateListener add(WindowStateListener paramWindowStateListener1, WindowStateListener paramWindowStateListener2)
  {
    return (WindowStateListener)addInternal(paramWindowStateListener1, paramWindowStateListener2);
  }
  
  public static WindowFocusListener add(WindowFocusListener paramWindowFocusListener1, WindowFocusListener paramWindowFocusListener2)
  {
    return (WindowFocusListener)addInternal(paramWindowFocusListener1, paramWindowFocusListener2);
  }
  
  public static ActionListener add(ActionListener paramActionListener1, ActionListener paramActionListener2)
  {
    return (ActionListener)addInternal(paramActionListener1, paramActionListener2);
  }
  
  public static ItemListener add(ItemListener paramItemListener1, ItemListener paramItemListener2)
  {
    return (ItemListener)addInternal(paramItemListener1, paramItemListener2);
  }
  
  public static AdjustmentListener add(AdjustmentListener paramAdjustmentListener1, AdjustmentListener paramAdjustmentListener2)
  {
    return (AdjustmentListener)addInternal(paramAdjustmentListener1, paramAdjustmentListener2);
  }
  
  public static TextListener add(TextListener paramTextListener1, TextListener paramTextListener2)
  {
    return (TextListener)addInternal(paramTextListener1, paramTextListener2);
  }
  
  public static InputMethodListener add(InputMethodListener paramInputMethodListener1, InputMethodListener paramInputMethodListener2)
  {
    return (InputMethodListener)addInternal(paramInputMethodListener1, paramInputMethodListener2);
  }
  
  public static HierarchyListener add(HierarchyListener paramHierarchyListener1, HierarchyListener paramHierarchyListener2)
  {
    return (HierarchyListener)addInternal(paramHierarchyListener1, paramHierarchyListener2);
  }
  
  public static HierarchyBoundsListener add(HierarchyBoundsListener paramHierarchyBoundsListener1, HierarchyBoundsListener paramHierarchyBoundsListener2)
  {
    return (HierarchyBoundsListener)addInternal(paramHierarchyBoundsListener1, paramHierarchyBoundsListener2);
  }
  
  public static MouseWheelListener add(MouseWheelListener paramMouseWheelListener1, MouseWheelListener paramMouseWheelListener2)
  {
    return (MouseWheelListener)addInternal(paramMouseWheelListener1, paramMouseWheelListener2);
  }
  
  public static ComponentListener remove(ComponentListener paramComponentListener1, ComponentListener paramComponentListener2)
  {
    return (ComponentListener)removeInternal(paramComponentListener1, paramComponentListener2);
  }
  
  public static ContainerListener remove(ContainerListener paramContainerListener1, ContainerListener paramContainerListener2)
  {
    return (ContainerListener)removeInternal(paramContainerListener1, paramContainerListener2);
  }
  
  public static FocusListener remove(FocusListener paramFocusListener1, FocusListener paramFocusListener2)
  {
    return (FocusListener)removeInternal(paramFocusListener1, paramFocusListener2);
  }
  
  public static KeyListener remove(KeyListener paramKeyListener1, KeyListener paramKeyListener2)
  {
    return (KeyListener)removeInternal(paramKeyListener1, paramKeyListener2);
  }
  
  public static MouseListener remove(MouseListener paramMouseListener1, MouseListener paramMouseListener2)
  {
    return (MouseListener)removeInternal(paramMouseListener1, paramMouseListener2);
  }
  
  public static MouseMotionListener remove(MouseMotionListener paramMouseMotionListener1, MouseMotionListener paramMouseMotionListener2)
  {
    return (MouseMotionListener)removeInternal(paramMouseMotionListener1, paramMouseMotionListener2);
  }
  
  public static WindowListener remove(WindowListener paramWindowListener1, WindowListener paramWindowListener2)
  {
    return (WindowListener)removeInternal(paramWindowListener1, paramWindowListener2);
  }
  
  public static WindowStateListener remove(WindowStateListener paramWindowStateListener1, WindowStateListener paramWindowStateListener2)
  {
    return (WindowStateListener)removeInternal(paramWindowStateListener1, paramWindowStateListener2);
  }
  
  public static WindowFocusListener remove(WindowFocusListener paramWindowFocusListener1, WindowFocusListener paramWindowFocusListener2)
  {
    return (WindowFocusListener)removeInternal(paramWindowFocusListener1, paramWindowFocusListener2);
  }
  
  public static ActionListener remove(ActionListener paramActionListener1, ActionListener paramActionListener2)
  {
    return (ActionListener)removeInternal(paramActionListener1, paramActionListener2);
  }
  
  public static ItemListener remove(ItemListener paramItemListener1, ItemListener paramItemListener2)
  {
    return (ItemListener)removeInternal(paramItemListener1, paramItemListener2);
  }
  
  public static AdjustmentListener remove(AdjustmentListener paramAdjustmentListener1, AdjustmentListener paramAdjustmentListener2)
  {
    return (AdjustmentListener)removeInternal(paramAdjustmentListener1, paramAdjustmentListener2);
  }
  
  public static TextListener remove(TextListener paramTextListener1, TextListener paramTextListener2)
  {
    return (TextListener)removeInternal(paramTextListener1, paramTextListener2);
  }
  
  public static InputMethodListener remove(InputMethodListener paramInputMethodListener1, InputMethodListener paramInputMethodListener2)
  {
    return (InputMethodListener)removeInternal(paramInputMethodListener1, paramInputMethodListener2);
  }
  
  public static HierarchyListener remove(HierarchyListener paramHierarchyListener1, HierarchyListener paramHierarchyListener2)
  {
    return (HierarchyListener)removeInternal(paramHierarchyListener1, paramHierarchyListener2);
  }
  
  public static HierarchyBoundsListener remove(HierarchyBoundsListener paramHierarchyBoundsListener1, HierarchyBoundsListener paramHierarchyBoundsListener2)
  {
    return (HierarchyBoundsListener)removeInternal(paramHierarchyBoundsListener1, paramHierarchyBoundsListener2);
  }
  
  public static MouseWheelListener remove(MouseWheelListener paramMouseWheelListener1, MouseWheelListener paramMouseWheelListener2)
  {
    return (MouseWheelListener)removeInternal(paramMouseWheelListener1, paramMouseWheelListener2);
  }
  
  protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    if (paramEventListener1 == null) {
      return paramEventListener2;
    }
    if (paramEventListener2 == null) {
      return paramEventListener1;
    }
    return new AWTEventMulticaster(paramEventListener1, paramEventListener2);
  }
  
  protected static EventListener removeInternal(EventListener paramEventListener1, EventListener paramEventListener2)
  {
    if ((paramEventListener1 == paramEventListener2) || (paramEventListener1 == null)) {
      return null;
    }
    if ((paramEventListener1 instanceof AWTEventMulticaster)) {
      return ((AWTEventMulticaster)paramEventListener1).remove(paramEventListener2);
    }
    return paramEventListener1;
  }
  
  protected void saveInternal(ObjectOutputStream paramObjectOutputStream, String paramString)
    throws IOException
  {
    if ((a instanceof AWTEventMulticaster))
    {
      ((AWTEventMulticaster)a).saveInternal(paramObjectOutputStream, paramString);
    }
    else if ((a instanceof Serializable))
    {
      paramObjectOutputStream.writeObject(paramString);
      paramObjectOutputStream.writeObject(a);
    }
    if ((b instanceof AWTEventMulticaster))
    {
      ((AWTEventMulticaster)b).saveInternal(paramObjectOutputStream, paramString);
    }
    else if ((b instanceof Serializable))
    {
      paramObjectOutputStream.writeObject(paramString);
      paramObjectOutputStream.writeObject(b);
    }
  }
  
  protected static void save(ObjectOutputStream paramObjectOutputStream, String paramString, EventListener paramEventListener)
    throws IOException
  {
    if (paramEventListener == null) {
      return;
    }
    if ((paramEventListener instanceof AWTEventMulticaster))
    {
      ((AWTEventMulticaster)paramEventListener).saveInternal(paramObjectOutputStream, paramString);
    }
    else if ((paramEventListener instanceof Serializable))
    {
      paramObjectOutputStream.writeObject(paramString);
      paramObjectOutputStream.writeObject(paramEventListener);
    }
  }
  
  private static int getListenerCount(EventListener paramEventListener, Class<?> paramClass)
  {
    if ((paramEventListener instanceof AWTEventMulticaster))
    {
      AWTEventMulticaster localAWTEventMulticaster = (AWTEventMulticaster)paramEventListener;
      return getListenerCount(a, paramClass) + getListenerCount(b, paramClass);
    }
    return paramClass.isInstance(paramEventListener) ? 1 : 0;
  }
  
  private static int populateListenerArray(EventListener[] paramArrayOfEventListener, EventListener paramEventListener, int paramInt)
  {
    if ((paramEventListener instanceof AWTEventMulticaster))
    {
      AWTEventMulticaster localAWTEventMulticaster = (AWTEventMulticaster)paramEventListener;
      int i = populateListenerArray(paramArrayOfEventListener, a, paramInt);
      return populateListenerArray(paramArrayOfEventListener, b, i);
    }
    if (paramArrayOfEventListener.getClass().getComponentType().isInstance(paramEventListener))
    {
      paramArrayOfEventListener[paramInt] = paramEventListener;
      return paramInt + 1;
    }
    return paramInt;
  }
  
  public static <T extends EventListener> T[] getListeners(EventListener paramEventListener, Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("Listener type should not be null");
    }
    int i = getListenerCount(paramEventListener, paramClass);
    EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(paramClass, i);
    populateListenerArray(arrayOfEventListener, paramEventListener, 0);
    return arrayOfEventListener;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\AWTEventMulticaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */