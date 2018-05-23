package javax.management.remote;

import java.io.IOException;
import java.util.Map;

public abstract interface JMXConnectorServerMBean
{
  public abstract void start()
    throws IOException;
  
  public abstract void stop()
    throws IOException;
  
  public abstract boolean isActive();
  
  public abstract void setMBeanServerForwarder(MBeanServerForwarder paramMBeanServerForwarder);
  
  public abstract String[] getConnectionIds();
  
  public abstract JMXServiceURL getAddress();
  
  public abstract Map<String, ?> getAttributes();
  
  public abstract JMXConnector toJMXConnector(Map<String, ?> paramMap)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXConnectorServerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */