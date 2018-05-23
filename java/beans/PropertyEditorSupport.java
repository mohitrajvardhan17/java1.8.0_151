package java.beans;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

public class PropertyEditorSupport
  implements PropertyEditor
{
  private Object value;
  private Object source;
  private Vector<PropertyChangeListener> listeners;
  
  public PropertyEditorSupport()
  {
    setSource(this);
  }
  
  public PropertyEditorSupport(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    setSource(paramObject);
  }
  
  public Object getSource()
  {
    return source;
  }
  
  public void setSource(Object paramObject)
  {
    source = paramObject;
  }
  
  public void setValue(Object paramObject)
  {
    value = paramObject;
    firePropertyChange();
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public boolean isPaintable()
  {
    return false;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {}
  
  public String getJavaInitializationString()
  {
    return "???";
  }
  
  public String getAsText()
  {
    return value != null ? value.toString() : null;
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    if ((value instanceof String))
    {
      setValue(paramString);
      return;
    }
    throw new IllegalArgumentException(paramString);
  }
  
  public String[] getTags()
  {
    return null;
  }
  
  public Component getCustomEditor()
  {
    return null;
  }
  
  public boolean supportsCustomEditor()
  {
    return false;
  }
  
  public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (listeners == null) {
      listeners = new Vector();
    }
    listeners.addElement(paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (listeners == null) {
      return;
    }
    listeners.removeElement(paramPropertyChangeListener);
  }
  
  public void firePropertyChange()
  {
    Vector localVector;
    synchronized (this)
    {
      if (listeners == null) {
        return;
      }
      localVector = unsafeClone(listeners);
    }
    ??? = new PropertyChangeEvent(source, null, null, null);
    for (int i = 0; i < localVector.size(); i++)
    {
      PropertyChangeListener localPropertyChangeListener = (PropertyChangeListener)localVector.elementAt(i);
      localPropertyChangeListener.propertyChange((PropertyChangeEvent)???);
    }
  }
  
  private <T> Vector<T> unsafeClone(Vector<T> paramVector)
  {
    return (Vector)paramVector.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PropertyEditorSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */