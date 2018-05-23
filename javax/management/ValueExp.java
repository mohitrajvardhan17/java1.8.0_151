package javax.management;

import java.io.Serializable;

public abstract interface ValueExp
  extends Serializable
{
  public abstract ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;
  
  @Deprecated
  public abstract void setMBeanServer(MBeanServer paramMBeanServer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\ValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */