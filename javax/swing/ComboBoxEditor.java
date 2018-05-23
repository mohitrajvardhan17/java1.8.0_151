package javax.swing;

import java.awt.Component;
import java.awt.event.ActionListener;

public abstract interface ComboBoxEditor
{
  public abstract Component getEditorComponent();
  
  public abstract void setItem(Object paramObject);
  
  public abstract Object getItem();
  
  public abstract void selectAll();
  
  public abstract void addActionListener(ActionListener paramActionListener);
  
  public abstract void removeActionListener(ActionListener paramActionListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ComboBoxEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */