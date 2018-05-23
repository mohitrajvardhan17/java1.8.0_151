package javax.management;

public abstract interface PersistentMBean
{
  public abstract void load()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;
  
  public abstract void store()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\PersistentMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */