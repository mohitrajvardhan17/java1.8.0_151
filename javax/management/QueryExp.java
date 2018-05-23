package javax.management;

import java.io.Serializable;

public abstract interface QueryExp
  extends Serializable
{
  public abstract boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;
  
  public abstract void setMBeanServer(MBeanServer paramMBeanServer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\QueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */