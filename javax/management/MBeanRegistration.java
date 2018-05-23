package javax.management;

public abstract interface MBeanRegistration
{
  public abstract ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception;
  
  public abstract void postRegister(Boolean paramBoolean);
  
  public abstract void preDeregister()
    throws Exception;
  
  public abstract void postDeregister();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanRegistration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */