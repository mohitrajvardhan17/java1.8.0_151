package java.beans;

public class PropertyVetoException
  extends Exception
{
  private static final long serialVersionUID = 129596057694162164L;
  private PropertyChangeEvent evt;
  
  public PropertyVetoException(String paramString, PropertyChangeEvent paramPropertyChangeEvent)
  {
    super(paramString);
    evt = paramPropertyChangeEvent;
  }
  
  public PropertyChangeEvent getPropertyChangeEvent()
  {
    return evt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PropertyVetoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */