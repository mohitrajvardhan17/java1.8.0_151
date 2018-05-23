package java.beans;

import java.util.EventObject;

public class PropertyChangeEvent
  extends EventObject
{
  private static final long serialVersionUID = 7042693688939648123L;
  private String propertyName;
  private Object newValue;
  private Object oldValue;
  private Object propagationId;
  
  public PropertyChangeEvent(Object paramObject1, String paramString, Object paramObject2, Object paramObject3)
  {
    super(paramObject1);
    propertyName = paramString;
    newValue = paramObject3;
    oldValue = paramObject2;
  }
  
  public String getPropertyName()
  {
    return propertyName;
  }
  
  public Object getNewValue()
  {
    return newValue;
  }
  
  public Object getOldValue()
  {
    return oldValue;
  }
  
  public void setPropagationId(Object paramObject)
  {
    propagationId = paramObject;
  }
  
  public Object getPropagationId()
  {
    return propagationId;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(getClass().getName());
    localStringBuilder.append("[propertyName=").append(getPropertyName());
    appendTo(localStringBuilder);
    localStringBuilder.append("; oldValue=").append(getOldValue());
    localStringBuilder.append("; newValue=").append(getNewValue());
    localStringBuilder.append("; propagationId=").append(getPropagationId());
    localStringBuilder.append("; source=").append(getSource());
    return "]";
  }
  
  void appendTo(StringBuilder paramStringBuilder) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PropertyChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */