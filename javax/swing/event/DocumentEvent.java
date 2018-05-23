package javax.swing.event;

import javax.swing.text.Document;
import javax.swing.text.Element;

public abstract interface DocumentEvent
{
  public abstract int getOffset();
  
  public abstract int getLength();
  
  public abstract Document getDocument();
  
  public abstract EventType getType();
  
  public abstract ElementChange getChange(Element paramElement);
  
  public static abstract interface ElementChange
  {
    public abstract Element getElement();
    
    public abstract int getIndex();
    
    public abstract Element[] getChildrenRemoved();
    
    public abstract Element[] getChildrenAdded();
  }
  
  public static final class EventType
  {
    public static final EventType INSERT = new EventType("INSERT");
    public static final EventType REMOVE = new EventType("REMOVE");
    public static final EventType CHANGE = new EventType("CHANGE");
    private String typeString;
    
    private EventType(String paramString)
    {
      typeString = paramString;
    }
    
    public String toString()
    {
      return typeString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\DocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */