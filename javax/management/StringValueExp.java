package javax.management;

public class StringValueExp
  implements ValueExp
{
  private static final long serialVersionUID = -3256390509806284044L;
  private String val;
  
  public StringValueExp() {}
  
  public StringValueExp(String paramString)
  {
    val = paramString;
  }
  
  public String getValue()
  {
    return val;
  }
  
  public String toString()
  {
    return "'" + val.replace("'", "''") + "'";
  }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer) {}
  
  public ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\StringValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */