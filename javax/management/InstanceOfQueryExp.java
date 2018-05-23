package javax.management;

class InstanceOfQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = -1081892073854801359L;
  private StringValueExp classNameValue;
  
  public InstanceOfQueryExp(StringValueExp paramStringValueExp)
  {
    if (paramStringValueExp == null) {
      throw new IllegalArgumentException("Null class name.");
    }
    classNameValue = paramStringValueExp;
  }
  
  public StringValueExp getClassNameValue()
  {
    return classNameValue;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    StringValueExp localStringValueExp;
    try
    {
      localStringValueExp = (StringValueExp)classNameValue.apply(paramObjectName);
    }
    catch (ClassCastException localClassCastException)
    {
      BadStringOperationException localBadStringOperationException = new BadStringOperationException(localClassCastException.toString());
      localBadStringOperationException.initCause(localClassCastException);
      throw localBadStringOperationException;
    }
    try
    {
      return getMBeanServer().isInstanceOf(paramObjectName, localStringValueExp.getValue());
    }
    catch (InstanceNotFoundException localInstanceNotFoundException) {}
    return false;
  }
  
  public String toString()
  {
    return "InstanceOf " + classNameValue.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\InstanceOfQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */