package javax.management.remote;

import java.io.IOException;
import java.util.Map;

public abstract interface JMXConnectorProvider
{
  public abstract JMXConnector newJMXConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXConnectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */