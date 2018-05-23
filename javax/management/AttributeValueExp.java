package javax.management;

public class AttributeValueExp
  implements ValueExp
{
  private static final long serialVersionUID = -7768025046539163385L;
  private String attr;
  
  @Deprecated
  public AttributeValueExp() {}
  
  public AttributeValueExp(String paramString)
  {
    attr = paramString;
  }
  
  public String getAttributeName()
  {
    return attr;
  }
  
  public ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    Object localObject = getAttribute(paramObjectName);
    if ((localObject instanceof Number)) {
      return new NumericValueExp((Number)localObject);
    }
    if ((localObject instanceof String)) {
      return new StringValueExp((String)localObject);
    }
    if ((localObject instanceof Boolean)) {
      return new BooleanValueExp((Boolean)localObject);
    }
    throw new BadAttributeValueExpException(localObject);
  }
  
  public String toString()
  {
    return attr;
  }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer) {}
  
  protected Object getAttribute(ObjectName paramObjectName)
  {
    try
    {
      MBeanServer localMBeanServer = QueryEval.getMBeanServer();
      return localMBeanServer.getAttribute(paramObjectName, attr);
    }
    catch (Exception localException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\AttributeValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */