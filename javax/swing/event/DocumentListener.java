package javax.swing.event;

import java.util.EventListener;

public abstract interface DocumentListener
  extends EventListener
{
  public abstract void insertUpdate(DocumentEvent paramDocumentEvent);
  
  public abstract void removeUpdate(DocumentEvent paramDocumentEvent);
  
  public abstract void changedUpdate(DocumentEvent paramDocumentEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\DocumentListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */