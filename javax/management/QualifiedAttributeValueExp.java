package javax.management;

class QualifiedAttributeValueExp
  extends AttributeValueExp
{
  private static final long serialVersionUID = 8832517277410933254L;
  private String className;
  
  @Deprecated
  public QualifiedAttributeValueExp() {}
  
  public QualifiedAttributeValueExp(String paramString1, String paramString2)
  {
    super(paramString2);
    className = paramString1;
  }
  
  public String getAttrClassName()
  {
    return className;
  }
  
  public ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    try
    {
      MBeanServer localMBeanServer = QueryEval.getMBeanServer();
      String str = localMBeanServer.getObjectInstance(paramObjectName).getClassName();
      if (str.equals(className)) {
        return super.apply(paramObjectName);
      }
      throw new InvalidApplicationException("Class name is " + str + ", should be " + className);
    }
    catch (Exception localException)
    {
      throw new InvalidApplicationException("Qualified attribute: " + localException);
    }
  }
  
  public String toString()
  {
    if (className != null) {
      return className + "." + super.toString();
    }
    return super.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\QualifiedAttributeValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */