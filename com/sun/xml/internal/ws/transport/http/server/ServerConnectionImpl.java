package com.sun.xml.internal.ws.transport.http.server;

import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;
import com.sun.xml.internal.ws.util.ReadAllStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

final class ServerConnectionImpl
  extends WSHTTPConnection
  implements WebServiceContextDelegate
{
  private final HttpExchange httpExchange;
  private int status;
  private final HttpAdapter adapter;
  private LWHSInputStream in;
  private OutputStream out;
  private static final BasePropertySet.PropertyMap model = parse(ServerConnectionImpl.class);
  
  public ServerConnectionImpl(@NotNull HttpAdapter paramHttpAdapter, @NotNull HttpExchange paramHttpExchange)
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
    return httpExchange.getRequestHeaders().getFirst(paramString);
  }
  
  public void setResponseHeaders(Map<String, List<String>> paramMap)
  {
    Headers localHeaders = httpExchange.getResponseHeaders();
    localHeaders.clear();
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      if ((!"Content-Length".equalsIgnoreCase(str)) && (!"Content-Type".equalsIgnoreCase(str))) {
        localHeaders.put(str, new ArrayList(localList));
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
    return httpExchange.getRequestHeaders().get(paramString);
  }
  
  @PropertySet.Property({"javax.xml.ws.http.response.headers", "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers"})
  public Map<String, List<String>> getResponseHeaders()
  {
    return httpExchange.getResponseHeaders();
  }
  
  public void setContentTypeResponseHeader(@NotNull String paramString)
  {
    httpExchange.getResponseHeaders().set("Content-Type", paramString);
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
  {
    if (in == null) {
      in = new LWHSInputStream(httpExchange.getRequestBody());
    }
    return in;
  }
  
  @NotNull
  public OutputStream getOutput()
    throws IOException
  {
    if (out == null)
    {
      String str = httpExchange.getResponseHeaders().getFirst("Content-Length");
      int i = str != null ? Integer.parseInt(str) : 0;
      httpExchange.sendResponseHeaders(getStatus(), i);
      out = new FilterOutputStream(httpExchange.getResponseBody())
      {
        boolean closed;
        
        public void close()
          throws IOException
        {
          if (!closed)
          {
            closed = true;
            in.readAll();
            try
            {
              super.close();
            }
            catch (IOException localIOException) {}
          }
        }
        
        public void write(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
          throws IOException
        {
          out.write(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
        }
      };
    }
    return out;
  }
  
  @NotNull
  public WebServiceContextDelegate getWebServiceContextDelegate()
  {
    return this;
  }
  
  public Principal getUserPrincipal(Packet paramPacket)
  {
    return httpExchange.getPrincipal();
  }
  
  public boolean isUserInRole(Packet paramPacket, String paramString)
  {
    return false;
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
    return httpExchange instanceof HttpsExchange;
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
    URI localURI = httpExchange.getRequestURI();
    String str = localURI.getQuery();
    if (str != null) {
      return str;
    }
    return null;
  }
  
  @PropertySet.Property({"javax.xml.ws.http.request.pathinfo"})
  public String getPathInfo()
  {
    URI localURI = httpExchange.getRequestURI();
    String str1 = localURI.getPath();
    String str2 = httpExchange.getHttpContext().getPath();
    if (str1.length() > str2.length()) {
      return str1.substring(str2.length());
    }
    return null;
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
    localStringBuilder.append((httpExchange instanceof HttpsExchange) ? "https" : "http");
    localStringBuilder.append("://");
    String str = httpExchange.getRequestHeaders().getFirst("Host");
    if (str != null)
    {
      localStringBuilder.append(str);
    }
    else
    {
      localStringBuilder.append(httpExchange.getLocalAddress().getHostName());
      localStringBuilder.append(":");
      localStringBuilder.append(httpExchange.getLocalAddress().getPort());
    }
    return localStringBuilder.toString();
  }
  
  public String getProtocol()
  {
    return httpExchange.getProtocol();
  }
  
  public void setContentLengthResponseHeader(int paramInt)
  {
    httpExchange.getResponseHeaders().set("Content-Length", "" + paramInt);
  }
  
  public String getRequestURI()
  {
    return httpExchange.getRequestURI().toString();
  }
  
  public String getRequestScheme()
  {
    return (httpExchange instanceof HttpsExchange) ? "https" : "http";
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
  
  private static class LWHSInputStream
    extends FilterInputStream
  {
    boolean closed;
    boolean readAll;
    
    LWHSInputStream(InputStream paramInputStream)
    {
      super();
    }
    
    void readAll()
      throws IOException
    {
      if ((!closed) && (!readAll))
      {
        ReadAllStream localReadAllStream = new ReadAllStream();
        localReadAllStream.readAll(in, 4000000L);
        in.close();
        in = localReadAllStream;
        readAll = true;
      }
    }
    
    public void close()
      throws IOException
    {
      if (!closed)
      {
        readAll();
        super.close();
        closed = true;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */