package java.beans;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract interface PropertyEditor
{
  public abstract void setValue(Object paramObject);
  
  public abstract Object getValue();
  
  public abstract boolean isPaintable();
  
  public abstract void paintValue(Graphics paramGraphics, Rectangle paramRectangle);
  
  public abstract String getJavaInitializationString();
  
  public abstract String getAsText();
  
  public abstract void setAsText(String paramString)
    throws IllegalArgumentException;
  
  public abstract String[] getTags();
  
  public abstract Component getCustomEditor();
  
  public abstract boolean supportsCustomEditor();
  
  public abstract void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  public abstract void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PropertyEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */