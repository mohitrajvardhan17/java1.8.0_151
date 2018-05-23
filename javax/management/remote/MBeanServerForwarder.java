package javax.management.remote;

import javax.management.MBeanServer;

public abstract interface MBeanServerForwarder
  extends MBeanServer
{
  public abstract MBeanServer getMBeanServer();
  
  public abstract void setMBeanServer(MBeanServer paramMBeanServer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\MBeanServerForwarder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */