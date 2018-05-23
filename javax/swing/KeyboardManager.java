package javax.swing;

import java.applet.Applet;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.awt.EmbeddedFrame;

class KeyboardManager
{
  static KeyboardManager currentManager = new KeyboardManager();
  Hashtable<Container, Hashtable> containerMap = new Hashtable();
  Hashtable<ComponentKeyStrokePair, Container> componentKeyStrokeMap = new Hashtable();
  
  KeyboardManager() {}
  
  public static KeyboardManager getCurrentManager()
  {
    return currentManager;
  }
  
  public static void setCurrentManager(KeyboardManager paramKeyboardManager)
  {
    currentManager = paramKeyboardManager;
  }
  
  public void registerKeyStroke(KeyStroke paramKeyStroke, JComponent paramJComponent)
  {
    Container localContainer = getTopAncestor(paramJComponent);
    if (localContainer == null) {
      return;
    }
    Hashtable localHashtable = (Hashtable)containerMap.get(localContainer);
    if (localHashtable == null) {
      localHashtable = registerNewTopContainer(localContainer);
    }
    Object localObject = localHashtable.get(paramKeyStroke);
    if (localObject == null)
    {
      localHashtable.put(paramKeyStroke, paramJComponent);
    }
    else
    {
      Vector localVector;
      if ((localObject instanceof Vector))
      {
        localVector = (Vector)localObject;
        if (!localVector.contains(paramJComponent)) {
          localVector.addElement(paramJComponent);
        }
      }
      else if ((localObject instanceof JComponent))
      {
        if (localObject != paramJComponent)
        {
          localVector = new Vector();
          localVector.addElement((JComponent)localObject);
          localVector.addElement(paramJComponent);
          localHashtable.put(paramKeyStroke, localVector);
        }
      }
      else
      {
        System.out.println("Unexpected condition in registerKeyStroke");
        Thread.dumpStack();
      }
    }
    componentKeyStrokeMap.put(new ComponentKeyStrokePair(paramJComponent, paramKeyStroke), localContainer);
    if ((localContainer instanceof EmbeddedFrame)) {
      ((EmbeddedFrame)localContainer).registerAccelerator(paramKeyStroke);
    }
  }
  
  private static Container getTopAncestor(JComponent paramJComponent)
  {
    for (Container localContainer = paramJComponent.getParent(); localContainer != null; localContainer = localContainer.getParent()) {
      if ((((localContainer instanceof Window)) && (((Window)localContainer).isFocusableWindow())) || ((localContainer instanceof Applet)) || ((localContainer instanceof JInternalFrame))) {
        return localContainer;
      }
    }
    return null;
  }
  
  public void unregisterKeyStroke(KeyStroke paramKeyStroke, JComponent paramJComponent)
  {
    ComponentKeyStrokePair localComponentKeyStrokePair = new ComponentKeyStrokePair(paramJComponent, paramKeyStroke);
    Container localContainer = (Container)componentKeyStrokeMap.get(localComponentKeyStrokePair);
    if (localContainer == null) {
      return;
    }
    Hashtable localHashtable = (Hashtable)containerMap.get(localContainer);
    if (localHashtable == null)
    {
      Thread.dumpStack();
      return;
    }
    Object localObject = localHashtable.get(paramKeyStroke);
    if (localObject == null)
    {
      Thread.dumpStack();
      return;
    }
    if (((localObject instanceof JComponent)) && (localObject == paramJComponent))
    {
      localHashtable.remove(paramKeyStroke);
    }
    else if ((localObject instanceof Vector))
    {
      Vector localVector = (Vector)localObject;
      localVector.removeElement(paramJComponent);
      if (localVector.isEmpty()) {
        localHashtable.remove(paramKeyStroke);
      }
    }
    if (localHashtable.isEmpty()) {
      containerMap.remove(localContainer);
    }
    componentKeyStrokeMap.remove(localComponentKeyStrokePair);
    if ((localContainer instanceof EmbeddedFrame)) {
      ((EmbeddedFrame)localContainer).unregisterAccelerator(paramKeyStroke);
    }
  }
  
  public boolean fireKeyboardAction(KeyEvent paramKeyEvent, boolean paramBoolean, Container paramContainer)
  {
    if (paramKeyEvent.isConsumed())
    {
      System.out.println("Acquired pre-used event!");
      Thread.dumpStack();
    }
    KeyStroke localKeyStroke2 = null;
    KeyStroke localKeyStroke1;
    if (paramKeyEvent.getID() == 400)
    {
      localKeyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyChar());
    }
    else
    {
      if (paramKeyEvent.getKeyCode() != paramKeyEvent.getExtendedKeyCode()) {
        localKeyStroke2 = KeyStroke.getKeyStroke(paramKeyEvent.getExtendedKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
      }
      localKeyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
    }
    Hashtable localHashtable = (Hashtable)containerMap.get(paramContainer);
    Object localObject1;
    Object localObject2;
    if (localHashtable != null)
    {
      localObject1 = null;
      if (localKeyStroke2 != null)
      {
        localObject1 = localHashtable.get(localKeyStroke2);
        if (localObject1 != null) {
          localKeyStroke1 = localKeyStroke2;
        }
      }
      if (localObject1 == null) {
        localObject1 = localHashtable.get(localKeyStroke1);
      }
      if (localObject1 != null) {
        if ((localObject1 instanceof JComponent))
        {
          localObject2 = (JComponent)localObject1;
          if ((((JComponent)localObject2).isShowing()) && (((JComponent)localObject2).isEnabled())) {
            fireBinding((JComponent)localObject2, localKeyStroke1, paramKeyEvent, paramBoolean);
          }
        }
        else if ((localObject1 instanceof Vector))
        {
          localObject2 = (Vector)localObject1;
          for (int i = ((Vector)localObject2).size() - 1; i >= 0; i--)
          {
            JComponent localJComponent = (JComponent)((Vector)localObject2).elementAt(i);
            if ((localJComponent.isShowing()) && (localJComponent.isEnabled()))
            {
              fireBinding(localJComponent, localKeyStroke1, paramKeyEvent, paramBoolean);
              if (paramKeyEvent.isConsumed()) {
                return true;
              }
            }
          }
        }
        else
        {
          System.out.println("Unexpected condition in fireKeyboardAction " + localObject1);
          Thread.dumpStack();
        }
      }
    }
    if (paramKeyEvent.isConsumed()) {
      return true;
    }
    if (localHashtable != null)
    {
      localObject1 = (Vector)localHashtable.get(JMenuBar.class);
      if (localObject1 != null)
      {
        localObject2 = ((Vector)localObject1).elements();
        while (((Enumeration)localObject2).hasMoreElements())
        {
          JMenuBar localJMenuBar = (JMenuBar)((Enumeration)localObject2).nextElement();
          if ((localJMenuBar.isShowing()) && (localJMenuBar.isEnabled()))
          {
            int j = (localKeyStroke2 != null) && (!localKeyStroke2.equals(localKeyStroke1)) ? 1 : 0;
            if (j != 0) {
              fireBinding(localJMenuBar, localKeyStroke2, paramKeyEvent, paramBoolean);
            }
            if ((j == 0) || (!paramKeyEvent.isConsumed())) {
              fireBinding(localJMenuBar, localKeyStroke1, paramKeyEvent, paramBoolean);
            }
            if (paramKeyEvent.isConsumed()) {
              return true;
            }
          }
        }
      }
    }
    return paramKeyEvent.isConsumed();
  }
  
  void fireBinding(JComponent paramJComponent, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, boolean paramBoolean)
  {
    if (paramJComponent.processKeyBinding(paramKeyStroke, paramKeyEvent, 2, paramBoolean)) {
      paramKeyEvent.consume();
    }
  }
  
  public void registerMenuBar(JMenuBar paramJMenuBar)
  {
    Container localContainer = getTopAncestor(paramJMenuBar);
    if (localContainer == null) {
      return;
    }
    Hashtable localHashtable = (Hashtable)containerMap.get(localContainer);
    if (localHashtable == null) {
      localHashtable = registerNewTopContainer(localContainer);
    }
    Vector localVector = (Vector)localHashtable.get(JMenuBar.class);
    if (localVector == null)
    {
      localVector = new Vector();
      localHashtable.put(JMenuBar.class, localVector);
    }
    if (!localVector.contains(paramJMenuBar)) {
      localVector.addElement(paramJMenuBar);
    }
  }
  
  public void unregisterMenuBar(JMenuBar paramJMenuBar)
  {
    Container localContainer = getTopAncestor(paramJMenuBar);
    if (localContainer == null) {
      return;
    }
    Hashtable localHashtable = (Hashtable)containerMap.get(localContainer);
    if (localHashtable != null)
    {
      Vector localVector = (Vector)localHashtable.get(JMenuBar.class);
      if (localVector != null)
      {
        localVector.removeElement(paramJMenuBar);
        if (localVector.isEmpty())
        {
          localHashtable.remove(JMenuBar.class);
          if (localHashtable.isEmpty()) {
            containerMap.remove(localContainer);
          }
        }
      }
    }
  }
  
  protected Hashtable registerNewTopContainer(Container paramContainer)
  {
    Hashtable localHashtable = new Hashtable();
    containerMap.put(paramContainer, localHashtable);
    return localHashtable;
  }
  
  class ComponentKeyStrokePair
  {
    Object component;
    Object keyStroke;
    
    public ComponentKeyStrokePair(Object paramObject1, Object paramObject2)
    {
      component = paramObject1;
      keyStroke = paramObject2;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof ComponentKeyStrokePair)) {
        return false;
      }
      ComponentKeyStrokePair localComponentKeyStrokePair = (ComponentKeyStrokePair)paramObject;
      return (component.equals(component)) && (keyStroke.equals(keyStroke));
    }
    
    public int hashCode()
    {
      return component.hashCode() * keyStroke.hashCode();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\KeyboardManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */