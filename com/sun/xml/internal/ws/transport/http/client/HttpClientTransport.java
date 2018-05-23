package com.sun.xml.internal.ws.transport.http.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.transport.Headers;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;

public class HttpClientTransport
{
  private static final byte[] THROW_AWAY_BUFFER = new byte[' '];
  int statusCode;
  String statusMessage;
  int contentLength;
  private final Map<String, List<String>> reqHeaders;
  private Map<String, List<String>> respHeaders = null;
  private OutputStream outputStream;
  private boolean https;
  private HttpURLConnection httpConnection = null;
  private final EndpointAddress endpoint;
  private final Packet context;
  private final Integer chunkSize;
  
  public HttpClientTransport(@NotNull Packet paramPacket, @NotNull Map<String, List<String>> paramMap)
  {
    endpoint = endpointAddress;
    context = paramPacket;
    reqHeaders = paramMap;
    chunkSize = ((Integer)context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size"));
  }
  
  OutputStream getOutput()
  {
    try
    {
      createHttpConnection();
      if (requiresOutputStream())
      {
        outputStream = httpConnection.getOutputStream();
        if (chunkSize != null) {
          outputStream = new WSChunkedOuputStream(outputStream, chunkSize.intValue());
        }
        List localList = (List)reqHeaders.get("Content-Encoding");
        if ((localList != null) && (((String)localList.get(0)).contains("gzip"))) {
          outputStream = new GZIPOutputStream(outputStream);
        }
      }
      httpConnection.connect();
    }
    catch (Exception localException)
    {
      throw new ClientTransportException(ClientMessages.localizableHTTP_CLIENT_FAILED(localException), localException);
    }
    return outputStream;
  }
  
  void closeOutput()
    throws IOException
  {
    if (outputStream != null)
    {
      outputStream.close();
      outputStream = null;
    }
  }
  
  @Nullable
  InputStream getInput()
  {
    Object localObject;
    try
    {
      localObject = readResponse();
      if (localObject != null)
      {
        String str = httpConnection.getContentEncoding();
        if ((str != null) && (str.contains("gzip"))) {
          localObject = new GZIPInputStream((InputStream)localObject);
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(statusCode), statusMessage), localIOException);
    }
    return (InputStream)localObject;
  }
  
  public Map<String, List<String>> getHeaders()
  {
    if (respHeaders != null) {
      return respHeaders;
    }
    respHeaders = new Headers();
    respHeaders.putAll(httpConnection.getHeaderFields());
    return respHeaders;
  }
  
  @Nullable
  protected InputStream readResponse()
  {
    InputStream localInputStream1;
    try
    {
      localInputStream1 = httpConnection.getInputStream();
    }
    catch (IOException localIOException)
    {
      localInputStream1 = httpConnection.getErrorStream();
    }
    if (localInputStream1 == null) {
      return localInputStream1;
    }
    final InputStream localInputStream2 = localInputStream1;
    new FilterInputStream(localInputStream2)
    {
      boolean closed;
      
      public void close()
        throws IOException
      {
        if (!closed)
        {
          closed = true;
          while (localInputStream2.read(HttpClientTransport.THROW_AWAY_BUFFER) != -1) {}
          super.close();
        }
      }
    };
  }
  
  protected void readResponseCodeAndMessage()
  {
    try
    {
      statusCode = httpConnection.getResponseCode();
      statusMessage = httpConnection.getResponseMessage();
      contentLength = httpConnection.getContentLength();
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  protected HttpURLConnection openConnection(Packet paramPacket)
  {
    return null;
  }
  
  protected boolean checkHTTPS(HttpURLConnection paramHttpURLConnection)
  {
    if ((paramHttpURLConnection instanceof HttpsURLConnection))
    {
      String str = (String)context.invocationProperties.get("com.sun.xml.internal.ws.client.http.HostnameVerificationProperty");
      if ((str != null) && (str.equalsIgnoreCase("true"))) {
        ((HttpsURLConnection)paramHttpURLConnection).setHostnameVerifier(new HttpClientVerifier(null));
      }
      HostnameVerifier localHostnameVerifier = (HostnameVerifier)context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.hostname.verifier");
      if (localHostnameVerifier != null) {
        ((HttpsURLConnection)paramHttpURLConnection).setHostnameVerifier(localHostnameVerifier);
      }
      SSLSocketFactory localSSLSocketFactory = (SSLSocketFactory)context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory");
      if (localSSLSocketFactory != null) {
        ((HttpsURLConnection)paramHttpURLConnection).setSSLSocketFactory(localSSLSocketFactory);
      }
      return true;
    }
    return false;
  }
  
  private void createHttpConnection()
    throws IOException
  {
    httpConnection = openConnection(context);
    if (httpConnection == null) {
      httpConnection = ((HttpURLConnection)endpoint.openConnection());
    }
    String str1 = endpoint.getURI().getScheme();
    if (str1.equals("https")) {
      https = true;
    }
    if (checkHTTPS(httpConnection)) {
      https = true;
    }
    httpConnection.setAllowUserInteraction(true);
    httpConnection.setDoOutput(true);
    httpConnection.setDoInput(true);
    String str2 = (String)context.invocationProperties.get("javax.xml.ws.http.request.method");
    String str3 = str2 != null ? str2 : "POST";
    httpConnection.setRequestMethod(str3);
    Integer localInteger1 = (Integer)context.invocationProperties.get("com.sun.xml.internal.ws.request.timeout");
    if (localInteger1 != null) {
      httpConnection.setReadTimeout(localInteger1.intValue());
    }
    Integer localInteger2 = (Integer)context.invocationProperties.get("com.sun.xml.internal.ws.connect.timeout");
    if (localInteger2 != null) {
      httpConnection.setConnectTimeout(localInteger2.intValue());
    }
    Integer localInteger3 = (Integer)context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
    if (localInteger3 != null) {
      httpConnection.setChunkedStreamingMode(localInteger3.intValue());
    }
    Iterator localIterator1 = reqHeaders.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      if (!"Content-Length".equals(localEntry.getKey()))
      {
        Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
        while (localIterator2.hasNext())
        {
          String str4 = (String)localIterator2.next();
          httpConnection.addRequestProperty((String)localEntry.getKey(), str4);
        }
      }
    }
  }
  
  boolean isSecure()
  {
    return https;
  }
  
  protected void setStatusCode(int paramInt)
  {
    statusCode = paramInt;
  }
  
  private boolean requiresOutputStream()
  {
    return (!httpConnection.getRequestMethod().equalsIgnoreCase("GET")) && (!httpConnection.getRequestMethod().equalsIgnoreCase("HEAD")) && (!httpConnection.getRequestMethod().equalsIgnoreCase("DELETE"));
  }
  
  @Nullable
  String getContentType()
  {
    return httpConnection.getContentType();
  }
  
  public int getContentLength()
  {
    return httpConnection.getContentLength();
  }
  
  static
  {
    try
    {
      JAXBContext.newInstance(new Class[0]).createUnmarshaller();
    }
    catch (JAXBException localJAXBException) {}
  }
  
  private static class HttpClientVerifier
    implements HostnameVerifier
  {
    private HttpClientVerifier() {}
    
    public boolean verify(String paramString, SSLSession paramSSLSession)
    {
      return true;
    }
  }
  
  private static class LocalhostHttpClientVerifier
    implements HostnameVerifier
  {
    private LocalhostHttpClientVerifier() {}
    
    public boolean verify(String paramString, SSLSession paramSSLSession)
    {
      return ("localhost".equalsIgnoreCase(paramString)) || ("127.0.0.1".equals(paramString));
    }
  }
  
  private static final class WSChunkedOuputStream
    extends FilterOutputStream
  {
    final int chunkSize;
    
    WSChunkedOuputStream(OutputStream paramOutputStream, int paramInt)
    {
      super();
      chunkSize = paramInt;
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      while (paramInt2 > 0)
      {
        int i = paramInt2 > chunkSize ? chunkSize : paramInt2;
        out.write(paramArrayOfByte, paramInt1, i);
        paramInt2 -= i;
        paramInt1 += i;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\client\HttpClientTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */