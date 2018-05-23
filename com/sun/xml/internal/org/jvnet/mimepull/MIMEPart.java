package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEPart
{
  private static final Logger LOGGER = Logger.getLogger(MIMEPart.class.getName());
  private volatile InternetHeaders headers;
  private volatile String contentId;
  private String contentType;
  private String contentTransferEncoding;
  volatile boolean parsed;
  final MIMEMessage msg;
  private final DataHead dataHead;
  
  MIMEPart(MIMEMessage paramMIMEMessage)
  {
    msg = paramMIMEMessage;
    dataHead = new DataHead(this);
  }
  
  MIMEPart(MIMEMessage paramMIMEMessage, String paramString)
  {
    this(paramMIMEMessage);
    contentId = paramString;
  }
  
  public InputStream read()
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = MimeUtility.decode(dataHead.read(), contentTransferEncoding);
    }
    catch (DecodingException localDecodingException)
    {
      if (LOGGER.isLoggable(Level.WARNING)) {
        LOGGER.log(Level.WARNING, null, localDecodingException);
      }
    }
    return localInputStream;
  }
  
  public void close()
  {
    dataHead.close();
  }
  
  public InputStream readOnce()
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = MimeUtility.decode(dataHead.readOnce(), contentTransferEncoding);
    }
    catch (DecodingException localDecodingException)
    {
      if (LOGGER.isLoggable(Level.WARNING)) {
        LOGGER.log(Level.WARNING, null, localDecodingException);
      }
    }
    return localInputStream;
  }
  
  public void moveTo(File paramFile)
  {
    dataHead.moveTo(paramFile);
  }
  
  public String getContentId()
  {
    if (contentId == null) {
      getHeaders();
    }
    return contentId;
  }
  
  public String getContentTransferEncoding()
  {
    if (contentTransferEncoding == null) {
      getHeaders();
    }
    return contentTransferEncoding;
  }
  
  public String getContentType()
  {
    if (contentType == null) {
      getHeaders();
    }
    return contentType;
  }
  
  private void getHeaders()
  {
    while (headers == null) {
      if ((!msg.makeProgress()) && (headers == null)) {
        throw new IllegalStateException("Internal Error. Didn't get Headers even after complete parsing.");
      }
    }
  }
  
  public List<String> getHeader(String paramString)
  {
    getHeaders();
    assert (headers != null);
    return headers.getHeader(paramString);
  }
  
  public List<? extends Header> getAllHeaders()
  {
    getHeaders();
    assert (headers != null);
    return headers.getAllHeaders();
  }
  
  void setHeaders(InternetHeaders paramInternetHeaders)
  {
    headers = paramInternetHeaders;
    List localList1 = getHeader("Content-Type");
    contentType = (localList1 == null ? "application/octet-stream" : (String)localList1.get(0));
    List localList2 = getHeader("Content-Transfer-Encoding");
    contentTransferEncoding = (localList2 == null ? "binary" : (String)localList2.get(0));
  }
  
  void addBody(ByteBuffer paramByteBuffer)
  {
    dataHead.addBody(paramByteBuffer);
  }
  
  void doneParsing()
  {
    parsed = true;
    dataHead.doneParsing();
  }
  
  void setContentId(String paramString)
  {
    contentId = paramString;
  }
  
  void setContentTransferEncoding(String paramString)
  {
    contentTransferEncoding = paramString;
  }
  
  public String toString()
  {
    return "Part=" + contentId + ":" + contentTransferEncoding;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */