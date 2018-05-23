package javax.management;

public class AttributeChangeNotification
  extends Notification
{
  private static final long serialVersionUID = 535176054565814134L;
  public static final String ATTRIBUTE_CHANGE = "jmx.attribute.change";
  private String attributeName = null;
  private String attributeType = null;
  private Object oldValue = null;
  private Object newValue = null;
  
  public AttributeChangeNotification(Object paramObject1, long paramLong1, long paramLong2, String paramString1, String paramString2, String paramString3, Object paramObject2, Object paramObject3)
  {
    super("jmx.attribute.change", paramObject1, paramLong1, paramLong2, paramString1);
    attributeName = paramString2;
    attributeType = paramString3;
    oldValue = paramObject2;
    newValue = paramObject3;
  }
  
  public String getAttributeName()
  {
    return attributeName;
  }
  
  public String getAttributeType()
  {
    return attributeType;
  }
  
  public Object getOldValue()
  {
    return oldValue;
  }
  
  public Object getNewValue()
  {
    return newValue;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\AttributeChangeNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */