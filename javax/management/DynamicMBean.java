package javax.management;

public abstract interface DynamicMBean
{
  public abstract Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException;
  
  public abstract void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;
  
  public abstract AttributeList getAttributes(String[] paramArrayOfString);
  
  public abstract AttributeList setAttributes(AttributeList paramAttributeList);
  
  public abstract Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException;
  
  public abstract MBeanInfo getMBeanInfo();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\DynamicMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */