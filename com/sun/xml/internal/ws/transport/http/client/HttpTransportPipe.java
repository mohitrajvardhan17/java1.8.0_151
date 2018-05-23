package com.sun.xml.internal.ws.transport.http.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.developer.HttpConfigFeature;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.Headers;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.StreamUtils;
import com.sun.xml.internal.ws.util.Version;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPBinding;

public class HttpTransportPipe
  extends AbstractTubeImpl
{
  private static final List<String> USER_AGENT = Collections.singletonList(RuntimeVersion.VERSION.toString());
  private static final Logger LOGGER = Logger.getLogger(HttpTransportPipe.class.getName());
  public static boolean dump;
  private final Codec codec;
  private final WSBinding binding;
  private final CookieHandler cookieJar;
  private final boolean sticky;
  
  public HttpTransportPipe(Codec paramCodec, WSBinding paramWSBinding)
  {
    codec = paramCodec;
    binding = paramWSBinding;
    sticky = isSticky(paramWSBinding);
    HttpConfigFeature localHttpConfigFeature = (HttpConfigFeature)paramWSBinding.getFeature(HttpConfigFeature.class);
    if (localHttpConfigFeature == null) {
      localHttpConfigFeature = new HttpConfigFeature();
    }
    cookieJar = localHttpConfigFeature.getCookieHandler();
  }
  
  private static boolean isSticky(WSBinding paramWSBinding)
  {
    boolean bool = false;
    WebServiceFeature[] arrayOfWebServiceFeature1 = paramWSBinding.getFeatures().toArray();
    for (WebServiceFeature localWebServiceFeature : arrayOfWebServiceFeature1) {
      if ((localWebServiceFeature instanceof StickyFeature))
      {
        bool = true;
        break;
      }
    }
    return bool;
  }
  
  private HttpTransportPipe(HttpTransportPipe paramHttpTransportPipe, TubeCloner paramTubeCloner)
  {
    this(codec.copy(), binding);
    paramTubeCloner.add(paramHttpTransportPipe, this);
  }
  
  public NextAction processException(@NotNull Throwable paramThrowable)
  {
    return doThrow(paramThrowable);
  }
  
  public NextAction processRequest(@NotNull Packet paramPacket)
  {
    return doReturnWith(process(paramPacket));
  }
  
  public NextAction processResponse(@NotNull Packet paramPacket)
  {
    return doReturnWith(paramPacket);
  }
  
  protected HttpClientTransport getTransport(Packet paramPacket, Map<String, List<String>> paramMap)
  {
    return new HttpClientTransport(paramPacket, paramMap);
  }
  
  public Packet process(Packet paramPacket)
  {
    try
    {
      Headers localHeaders = new Headers();
      Map localMap = (Map)invocationProperties.get("javax.xml.ws.http.request.headers");
      int i = 1;
      if (localMap != null)
      {
        localHeaders.putAll(localMap);
        if (localMap.get("User-Agent") != null) {
          i = 0;
        }
      }
      if (i != 0) {
        localHeaders.put("User-Agent", USER_AGENT);
      }
      addBasicAuth(paramPacket, localHeaders);
      addCookies(paramPacket, localHeaders);
      HttpClientTransport localHttpClientTransport = getTransport(paramPacket, localHeaders);
      paramPacket.addSatellite(new HttpResponseProperties(localHttpClientTransport));
      ContentType localContentType = codec.getStaticContentType(paramPacket);
      Object localObject;
      if (localContentType == null)
      {
        localObject = new ByteArrayBuffer();
        localContentType = codec.encode(paramPacket, (OutputStream)localObject);
        localHeaders.put("Content-Length", Collections.singletonList(Integer.toString(((ByteArrayBuffer)localObject).size())));
        localHeaders.put("Content-Type", Collections.singletonList(localContentType.getContentType()));
        if (localContentType.getAcceptHeader() != null) {
          localHeaders.put("Accept", Collections.singletonList(localContentType.getAcceptHeader()));
        }
        if ((binding instanceof SOAPBinding)) {
          writeSOAPAction(localHeaders, localContentType.getSOAPActionHeader());
        }
        if ((dump) || (LOGGER.isLoggable(Level.FINER))) {
          dump((ByteArrayBuffer)localObject, "HTTP request", localHeaders);
        }
        ((ByteArrayBuffer)localObject).writeTo(localHttpClientTransport.getOutput());
      }
      else
      {
        localHeaders.put("Content-Type", Collections.singletonList(localContentType.getContentType()));
        if (localContentType.getAcceptHeader() != null) {
          localHeaders.put("Accept", Collections.singletonList(localContentType.getAcceptHeader()));
        }
        if ((binding instanceof SOAPBinding)) {
          writeSOAPAction(localHeaders, localContentType.getSOAPActionHeader());
        }
        if ((dump) || (LOGGER.isLoggable(Level.FINER)))
        {
          localObject = new ByteArrayBuffer();
          codec.encode(paramPacket, (OutputStream)localObject);
          dump((ByteArrayBuffer)localObject, "HTTP request - " + endpointAddress, localHeaders);
          OutputStream localOutputStream = localHttpClientTransport.getOutput();
          if (localOutputStream != null) {
            ((ByteArrayBuffer)localObject).writeTo(localOutputStream);
          }
        }
        else
        {
          localObject = localHttpClientTransport.getOutput();
          if (localObject != null) {
            codec.encode(paramPacket, (OutputStream)localObject);
          }
        }
      }
      localHttpClientTransport.closeOutput();
      return createResponsePacket(paramPacket, localHttpClientTransport);
    }
    catch (WebServiceException localWebServiceException)
    {
      throw localWebServiceException;
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  private Packet createResponsePacket(Packet paramPacket, HttpClientTransport paramHttpClientTransport)
    throws IOException
  {
    paramHttpClientTransport.readResponseCodeAndMessage();
    recordCookies(paramPacket, paramHttpClientTransport);
    Object localObject = paramHttpClientTransport.getInput();
    if ((dump) || (LOGGER.isLoggable(Level.FINER)))
    {
      ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
      if (localObject != null)
      {
        localByteArrayBuffer.write((InputStream)localObject);
        ((InputStream)localObject).close();
      }
      dump(localByteArrayBuffer, "HTTP response - " + endpointAddress + " - " + statusCode, paramHttpClientTransport.getHeaders());
      localObject = localByteArrayBuffer.newInputStream();
    }
    int i = contentLength;
    InputStream localInputStream = null;
    if (i == -1)
    {
      localInputStream = StreamUtils.hasSomeData((InputStream)localObject);
      if (localInputStream != null) {
        localObject = localInputStream;
      }
    }
    if (((i == 0) || ((i == -1) && (localInputStream == null))) && (localObject != null))
    {
      ((InputStream)localObject).close();
      localObject = null;
    }
    checkStatusCode((InputStream)localObject, paramHttpClientTransport);
    Packet localPacket = paramPacket.createClientResponse(null);
    wasTransportSecure = paramHttpClientTransport.isSecure();
    if (localObject != null)
    {
      String str = paramHttpClientTransport.getContentType();
      if ((str != null) && (str.contains("text/html")) && ((binding instanceof SOAPBinding))) {
        throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(statusCode), statusMessage));
      }
      codec.decode((InputStream)localObject, str, localPacket);
    }
    return localPacket;
  }
  
  private void checkStatusCode(InputStream paramInputStream, HttpClientTransport paramHttpClientTransport)
    throws IOException
  {
    int i = statusCode;
    String str = statusMessage;
    if ((binding instanceof SOAPBinding))
    {
      if (binding.getSOAPVersion() == SOAPVersion.SOAP_12)
      {
        if ((i == 200) || (i == 202) || (isErrorCode(i))) {
          if ((isErrorCode(i)) && (paramInputStream == null)) {
            throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(i), str));
          }
        }
      }
      else if ((i == 200) || (i == 202) || (i == 500))
      {
        if ((i == 500) && (paramInputStream == null)) {
          throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(i), str));
        }
        return;
      }
      if (paramInputStream != null) {
        paramInputStream.close();
      }
      throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(i), str));
    }
  }
  
  private boolean isErrorCode(int paramInt)
  {
    return (paramInt == 500) || (paramInt == 400);
  }
  
  private void addCookies(Packet paramPacket, Map<String, List<String>> paramMap)
    throws IOException
  {
    Boolean localBoolean = (Boolean)invocationProperties.get("javax.xml.ws.session.maintain");
    if ((localBoolean != null) && (!localBoolean.booleanValue())) {
      return;
    }
    if ((sticky) || ((localBoolean != null) && (localBoolean.booleanValue())))
    {
      Map localMap = cookieJar.get(endpointAddress.getURI(), paramMap);
      processCookieHeaders(paramMap, localMap, "Cookie");
      processCookieHeaders(paramMap, localMap, "Cookie2");
    }
  }
  
  private void processCookieHeaders(Map<String, List<String>> paramMap1, Map<String, List<String>> paramMap2, String paramString)
  {
    List localList1 = (List)paramMap2.get(paramString);
    if ((localList1 != null) && (!localList1.isEmpty()))
    {
      List localList2 = mergeUserCookies(localList1, (List)paramMap1.get(paramString));
      paramMap1.put(paramString, localList2);
    }
  }
  
  private List<String> mergeUserCookies(List<String> paramList1, List<String> paramList2)
  {
    if ((paramList2 == null) || (paramList2.isEmpty())) {
      return paramList1;
    }
    HashMap localHashMap = new HashMap();
    cookieListToMap(paramList1, localHashMap);
    cookieListToMap(paramList2, localHashMap);
    return new ArrayList(localHashMap.values());
  }
  
  private void cookieListToMap(List<String> paramList, Map<String, String> paramMap)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      int i = str1.indexOf("=");
      String str2 = str1.substring(0, i);
      paramMap.put(str2, str1);
    }
  }
  
  private void recordCookies(Packet paramPacket, HttpClientTransport paramHttpClientTransport)
    throws IOException
  {
    Boolean localBoolean = (Boolean)invocationProperties.get("javax.xml.ws.session.maintain");
    if ((localBoolean != null) && (!localBoolean.booleanValue())) {
      return;
    }
    if ((sticky) || ((localBoolean != null) && (localBoolean.booleanValue()))) {
      cookieJar.put(endpointAddress.getURI(), paramHttpClientTransport.getHeaders());
    }
  }
  
  private void addBasicAuth(Packet paramPacket, Map<String, List<String>> paramMap)
  {
    String str1 = (String)invocationProperties.get("javax.xml.ws.security.auth.username");
    if (str1 != null)
    {
      String str2 = (String)invocationProperties.get("javax.xml.ws.security.auth.password");
      if (str2 != null)
      {
        StringBuilder localStringBuilder = new StringBuilder(str1);
        localStringBuilder.append(":");
        localStringBuilder.append(str2);
        String str3 = DatatypeConverter.printBase64Binary(localStringBuilder.toString().getBytes());
        paramMap.put("Authorization", Collections.singletonList("Basic " + str3));
      }
    }
  }
  
  private void writeSOAPAction(Map<String, List<String>> paramMap, String paramString)
  {
    if (SOAPVersion.SOAP_12.equals(binding.getSOAPVersion())) {
      return;
    }
    if (paramString != null) {
      paramMap.put("SOAPAction", Collections.singletonList(paramString));
    } else {
      paramMap.put("SOAPAction", Collections.singletonList("\"\""));
    }
  }
  
  public void preDestroy() {}
  
  public HttpTransportPipe copy(TubeCloner paramTubeCloner)
  {
    return new HttpTransportPipe(this, paramTubeCloner);
  }
  
  private void dump(ByteArrayBuffer paramByteArrayBuffer, String paramString, Map<String, List<String>> paramMap)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter localPrintWriter = new PrintWriter(localByteArrayOutputStream, true);
    localPrintWriter.println("---[" + paramString + "]---");
    Object localObject = paramMap.entrySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      if (((List)localEntry.getValue()).isEmpty())
      {
        localPrintWriter.println(localEntry.getValue());
      }
      else
      {
        Iterator localIterator = ((List)localEntry.getValue()).iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          localPrintWriter.println((String)localEntry.getKey() + ": " + str);
        }
      }
    }
    if (paramByteArrayBuffer.size() > HttpAdapter.dump_threshold)
    {
      localObject = paramByteArrayBuffer.getRawData();
      localByteArrayOutputStream.write((byte[])localObject, 0, HttpAdapter.dump_threshold);
      localPrintWriter.println();
      localPrintWriter.println(WsservletMessages.MESSAGE_TOO_LONG(HttpAdapter.class.getName() + ".dumpTreshold"));
    }
    else
    {
      paramByteArrayBuffer.writeTo(localByteArrayOutputStream);
    }
    localPrintWriter.println("--------------------");
    localObject = localByteArrayOutputStream.toString();
    if (dump) {
      System.out.println((String)localObject);
    }
    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.log(Level.FINER, (String)localObject);
    }
  }
  
  static
  {
    boolean bool;
    try
    {
      bool = Boolean.getBoolean(HttpTransportPipe.class.getName() + ".dump");
    }
    catch (Throwable localThrowable)
    {
      bool = false;
    }
    dump = bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\client\HttpTransportPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */