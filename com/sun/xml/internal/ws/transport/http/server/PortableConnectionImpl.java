package com.sun.xml.internal.ws.transport.http.server;

import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.spi.http.HttpExchange;

final class PortableConnectionImpl
  extends WSHTTPConnection
  implements WebServiceContextDelegate
{
  private final HttpExchange httpExchange;
  private int status;
  private final HttpAdapter adapter;
  private boolean outputWritten;
  private static final BasePropertySet.PropertyMap model = parse(PortableConnectionImpl.class);
  
  public PortableConnectionImpl(@NotNull HttpAdapter paramHttpAdapter, @NotNull HttpExchange paramHttpExchange)
  {
    adapter = paramHttpAdapter;
    httpExchange = paramHttpExchange;
  }
  
  @PropertySet.Property({"javax.xml.ws.http.request.headers", "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers"})
  @NotNull
  public Map<String, List<String>> getRequestHeaders()
  {
    return httpExchange.getRequestHeaders();
  }
  
  public String getRequestHeader(String paramString)
  {
    return httpExchange.getRequestHeader(paramString);
  }
  
  public void setResponseHeaders(Map<String, List<String>> paramMap)
  {
    Map localMap = httpExchange.getResponseHeaders();
    localMap.clear();
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      if ((!str.equalsIgnoreCase("Content-Length")) && (!str.equalsIgnoreCase("Content-Type"))) {
        localMap.put(str, new ArrayList(localList));
      }
    }
  }
  
  public void setResponseHeader(String paramString, List<String> paramList)
  {
    httpExchange.getResponseHeaders().put(paramString, paramList);
  }
  
  public Set<String> getRequestHeaderNames()
  {
    return httpExchange.getRequestHeaders().keySet();
  }
  
  public List<String> getRequestHeaderValues(String paramString)
  {
    return (List)httpExchange.getRequestHeaders().get(paramString);
  }
  
  @PropertySet.Property({"javax.xml.ws.http.response.headers", "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers"})
  public Map<String, List<String>> getResponseHeaders()
  {
    return httpExchange.getResponseHeaders();
  }
  
  public void setContentTypeResponseHeader(@NotNull String paramString)
  {
    httpExchange.addResponseHeader("Content-Type", paramString);
  }
  
  public void setStatus(int paramInt)
  {
    status = paramInt;
  }
  
  @PropertySet.Property({"javax.xml.ws.http.response.code"})
  public int getStatus()
  {
    return status;
  }
  
  @NotNull
  public InputStream getInput()
    throws IOException
  {
    return httpExchange.getRequestBody();
  }
  
  @NotNull
  public OutputStream getOutput()
    throws IOException
  {
    assert (!outputWritten);
    outputWritten = true;
    httpExchange.setStatus(getStatus());
    return httpExchange.getResponseBody();
  }
  
  @NotNull
  public WebServiceContextDelegate getWebServiceContextDelegate()
  {
    return this;
  }
  
  public Principal getUserPrincipal(Packet paramPacket)
  {
    return httpExchange.getUserPrincipal();
  }
  
  public boolean isUserInRole(Packet paramPacket, String paramString)
  {
    return httpExchange.isUserInRole(paramString);
  }
  
  @NotNull
  public String getEPRAddress(Packet paramPacket, WSEndpoint paramWSEndpoint)
  {
    PortAddressResolver localPortAddressResolver = adapter.owner.createPortAddressResolver(getBaseAddress(), paramWSEndpoint.getImplementationClass());
    String str = localPortAddressResolver.getAddressFor(paramWSEndpoint.getServiceName(), paramWSEndpoint.getPortName().getLocalPart());
    if (str == null) {
      throw new WebServiceException(WsservletMessages.SERVLET_NO_ADDRESS_AVAILABLE(paramWSEndpoint.getPortName()));
    }
    return str;
  }
  
  @PropertySet.Property({"javax.xml.ws.servlet.context"})
  public Object getServletContext()
  {
    return httpExchange.getAttribute("javax.xml.ws.servlet.context");
  }
  
  @PropertySet.Property({"javax.xml.ws.servlet.response"})
  public Object getServletResponse()
  {
    return httpExchange.getAttribute("javax.xml.ws.servlet.response");
  }
  
  @PropertySet.Property({"javax.xml.ws.servlet.request"})
  public Object getServletRequest()
  {
    return httpExchange.getAttribute("javax.xml.ws.servlet.request");
  }
  
  public String getWSDLAddress(@NotNull Packet paramPacket, @NotNull WSEndpoint paramWSEndpoint)
  {
    String str = getEPRAddress(paramPacket, paramWSEndpoint);
    if (adapter.getEndpoint().getPort() != null) {
      return str + "?wsdl";
    }
    return null;
  }
  
  public boolean isSecure()
  {
    return httpExchange.getScheme().equals("https");
  }
  
  @PropertySet.Property({"javax.xml.ws.http.request.method"})
  @NotNull
  public String getRequestMethod()
  {
    return httpExchange.getRequestMethod();
  }
  
  @PropertySet.Property({"javax.xml.ws.http.request.querystring"})
  public String getQueryString()
  {
    return httpExchange.getQueryString();
  }
  
  @PropertySet.Property({"javax.xml.ws.http.request.pathinfo"})
  public String getPathInfo()
  {
    return httpExchange.getPathInfo();
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.http.exchange"})
  public HttpExchange getExchange()
  {
    return httpExchange;
  }
  
  @NotNull
  public String getBaseAddress()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(httpExchange.getScheme());
    localStringBuilder.append("://");
    localStringBuilder.append(httpExchange.getLocalAddress().getHostName());
    localStringBuilder.append(":");
    localStringBuilder.append(httpExchange.getLocalAddress().getPort());
    localStringBuilder.append(httpExchange.getContextPath());
    return localStringBuilder.toString();
  }
  
  public String getProtocol()
  {
    return httpExchange.getProtocol();
  }
  
  public void setContentLengthResponseHeader(int paramInt)
  {
    httpExchange.addResponseHeader("Content-Length", "" + paramInt);
  }
  
  public String getRequestURI()
  {
    return httpExchange.getRequestURI().toString();
  }
  
  public String getRequestScheme()
  {
    return httpExchange.getScheme();
  }
  
  public String getServerName()
  {
    return httpExchange.getLocalAddress().getHostName();
  }
  
  public int getServerPort()
  {
    return httpExchange.getLocalAddress().getPort();
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\PortableConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */