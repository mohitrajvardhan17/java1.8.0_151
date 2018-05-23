package javax.management.remote;

import java.io.IOException;
import java.util.Map;
import javax.management.MBeanServer;

public abstract interface JMXConnectorServerProvider
{
  public abstract JMXConnectorServer newJMXConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXConnectorServerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */