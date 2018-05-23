package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;

public class ClientProvider
  implements JMXConnectorProvider
{
  public ClientProvider() {}
  
  public JMXConnector newJMXConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
    throws IOException
  {
    if (!paramJMXServiceURL.getProtocol().equals("iiop")) {
      throw new MalformedURLException("Protocol not iiop: " + paramJMXServiceURL.getProtocol());
    }
    return new RMIConnector(paramJMXServiceURL, paramMap);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\protocol\iiop\ClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */