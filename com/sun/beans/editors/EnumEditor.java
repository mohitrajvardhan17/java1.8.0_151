package com.sun.beans.editors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

public final class EnumEditor
  implements PropertyEditor
{
  private final List<PropertyChangeListener> listeners = new ArrayList();
  private final Class type;
  private final String[] tags;
  private Object value;
  
  public EnumEditor(Class paramClass)
  {
    Object[] arrayOfObject = paramClass.getEnumConstants();
    if (arrayOfObject == null) {
      throw new IllegalArgumentException("Unsupported " + paramClass);
    }
    type = paramClass;
    tags = new String[arrayOfObject.length];
    for (int i = 0; i < arrayOfObject.length; i++) {
      tags[i] = ((Enum)arrayOfObject[i]).name();
    }
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public void setValue(Object paramObject)
  {
    if ((paramObject != null) && (!type.isInstance(paramObject))) {
      throw new IllegalArgumentException("Unsupported value: " + paramObject);
    }
    Object localObject1;
    PropertyChangeListener[] arrayOfPropertyChangeListener;
    synchronized (listeners)
    {
      localObject1 = value;
      value = paramObject;
      if (paramObject == null ? localObject1 == null : paramObject.equals(localObject1)) {
        return;
      }
      int i = listeners.size();
      if (i == 0) {
        return;
      }
      arrayOfPropertyChangeListener = (PropertyChangeListener[])listeners.toArray(new PropertyChangeListener[i]);
    }
    ??? = new PropertyChangeEvent(this, null, localObject1, paramObject);
    for (Object localObject4 : arrayOfPropertyChangeListener) {
      ((PropertyChangeListener)localObject4).propertyChange((PropertyChangeEvent)???);
    }
  }
  
  public String getAsText()
  {
    return value != null ? ((Enum)value).name() : null;
  }
  
  public void setAsText(String paramString)
  {
    setValue(paramString != null ? Enum.valueOf(type, paramString) : null);
  }
  
  public String[] getTags()
  {
    return (String[])tags.clone();
  }
  
  public String getJavaInitializationString()
  {
    String str = getAsText();
    return str != null ? type.getName() + '.' + str : "null";
  }
  
  public boolean isPaintable()
  {
    return false;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {}
  
  public boolean supportsCustomEditor()
  {
    return false;
  }
  
  public Component getCustomEditor()
  {
    return null;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (listeners)
    {
      listeners.add(paramPropertyChangeListener);
    }
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (listeners)
    {
      listeners.remove(paramPropertyChangeListener);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\EnumEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */