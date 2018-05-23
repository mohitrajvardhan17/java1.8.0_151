package javax.management;

class BooleanValueExp
  extends QueryEval
  implements ValueExp
{
  private static final long serialVersionUID = 7754922052666594581L;
  private boolean val = false;
  
  BooleanValueExp(boolean paramBoolean)
  {
    val = paramBoolean;
  }
  
  BooleanValueExp(Boolean paramBoolean)
  {
    val = paramBoolean.booleanValue();
  }
  
  public Boolean getValue()
  {
    return Boolean.valueOf(val);
  }
  
  public String toString()
  {
    return String.valueOf(val);
  }
  
  public ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    return this;
  }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer)
  {
    super.setMBeanServer(paramMBeanServer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\BooleanValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */