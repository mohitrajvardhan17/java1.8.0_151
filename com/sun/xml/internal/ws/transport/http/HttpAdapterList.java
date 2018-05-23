package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class HttpAdapterList<T extends HttpAdapter>
  extends AbstractList<T>
  implements DeploymentDescriptorParser.AdapterFactory<T>
{
  private final List<T> adapters = new ArrayList();
  private final Map<PortInfo, String> addressMap = new HashMap();
  
  public HttpAdapterList() {}
  
  public T createAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint)
  {
    HttpAdapter localHttpAdapter = createHttpAdapter(paramString1, paramString2, paramWSEndpoint);
    adapters.add(localHttpAdapter);
    WSDLPort localWSDLPort = paramWSEndpoint.getPort();
    if (localWSDLPort != null)
    {
      PortInfo localPortInfo = new PortInfo(localWSDLPort.getOwner().getName(), localWSDLPort.getName().getLocalPart(), paramWSEndpoint.getImplementationClass());
      addressMap.put(localPortInfo, getValidPath(paramString2));
    }
    return localHttpAdapter;
  }
  
  protected abstract T createHttpAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint);
  
  private String getValidPath(@NotNull String paramString)
  {
    if (paramString.endsWith("/*")) {
      return paramString.substring(0, paramString.length() - 2);
    }
    return paramString;
  }
  
  public PortAddressResolver createPortAddressResolver(final String paramString, final Class<?> paramClass)
  {
    new PortAddressResolver()
    {
      public String getAddressFor(@NotNull QName paramAnonymousQName, @NotNull String paramAnonymousString)
      {
        String str = (String)addressMap.get(new HttpAdapterList.PortInfo(paramAnonymousQName, paramAnonymousString, paramClass));
        if (str == null)
        {
          Iterator localIterator = addressMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            if ((paramAnonymousQName.equals(HttpAdapterList.PortInfo.access$100((HttpAdapterList.PortInfo)localEntry.getKey()))) && (paramAnonymousString.equals(HttpAdapterList.PortInfo.access$200((HttpAdapterList.PortInfo)localEntry.getKey()))))
            {
              str = (String)localEntry.getValue();
              break;
            }
          }
        }
        return paramString + str;
      }
    };
  }
  
  public T get(int paramInt)
  {
    return (HttpAdapter)adapters.get(paramInt);
  }
  
  public int size()
  {
    return adapters.size();
  }
  
  private static class PortInfo
  {
    private final QName serviceName;
    private final String portName;
    private final Class<?> implClass;
    
    PortInfo(@NotNull QName paramQName, @NotNull String paramString, Class<?> paramClass)
    {
      serviceName = paramQName;
      portName = paramString;
      implClass = paramClass;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof PortInfo))
      {
        PortInfo localPortInfo = (PortInfo)paramObject;
        if (implClass == null) {
          return (serviceName.equals(serviceName)) && (portName.equals(portName)) && (implClass == null);
        }
        return (serviceName.equals(serviceName)) && (portName.equals(portName)) && (implClass.equals(implClass));
      }
      return false;
    }
    
    public int hashCode()
    {
      int i = serviceName.hashCode() + portName.hashCode();
      return implClass != null ? i + implClass.hashCode() : i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\HttpAdapterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */