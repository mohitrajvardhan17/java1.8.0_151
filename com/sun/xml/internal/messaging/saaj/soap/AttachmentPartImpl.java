package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePartDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;

public class AttachmentPartImpl
  extends AttachmentPart
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  private final MimeHeaders headers = new MimeHeaders();
  private MimeBodyPart rawContent = null;
  private DataHandler dataHandler = null;
  private MIMEPart mimePart = null;
  
  public AttachmentPartImpl()
  {
    initializeJavaActivationHandlers();
  }
  
  public AttachmentPartImpl(MIMEPart paramMIMEPart)
  {
    mimePart = paramMIMEPart;
    List localList = paramMIMEPart.getAllHeaders();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      com.sun.xml.internal.org.jvnet.mimepull.Header localHeader = (com.sun.xml.internal.org.jvnet.mimepull.Header)localIterator.next();
      headers.addHeader(localHeader.getName(), localHeader.getValue());
    }
  }
  
  public int getSize()
    throws SOAPException
  {
    if (mimePart != null) {
      try
      {
        return mimePart.read().available();
      }
      catch (IOException localIOException1)
      {
        return -1;
      }
    }
    if ((rawContent == null) && (dataHandler == null)) {
      return 0;
    }
    if (rawContent != null) {
      try
      {
        return rawContent.getSize();
      }
      catch (Exception localException)
      {
        log.log(Level.SEVERE, "SAAJ0573.soap.attachment.getrawbytes.ioexception", new String[] { localException.getLocalizedMessage() });
        throw new SOAPExceptionImpl("Raw InputStream Error: " + localException);
      }
    }
    ByteOutputStream localByteOutputStream = new ByteOutputStream();
    try
    {
      dataHandler.writeTo(localByteOutputStream);
    }
    catch (IOException localIOException2)
    {
      log.log(Level.SEVERE, "SAAJ0501.soap.data.handler.err", new String[] { localIOException2.getLocalizedMessage() });
      throw new SOAPExceptionImpl("Data handler error: " + localIOException2);
    }
    return localByteOutputStream.size();
  }
  
  public void clearContent()
  {
    if (mimePart != null)
    {
      mimePart.close();
      mimePart = null;
    }
    dataHandler = null;
    rawContent = null;
  }
  
  public Object getContent()
    throws SOAPException
  {
    try
    {
      if (mimePart != null) {
        return mimePart.read();
      }
      if (dataHandler != null) {
        return getDataHandler().getContent();
      }
      if (rawContent != null) {
        return rawContent.getContent();
      }
      log.severe("SAAJ0572.soap.no.content.for.attachment");
      throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }
    catch (Exception localException)
    {
      log.log(Level.SEVERE, "SAAJ0575.soap.attachment.getcontent.exception", localException);
      throw new SOAPExceptionImpl(localException.getLocalizedMessage());
    }
  }
  
  public void setContent(Object paramObject, String paramString)
    throws IllegalArgumentException
  {
    if (mimePart != null)
    {
      mimePart.close();
      mimePart = null;
    }
    DataHandler localDataHandler = new DataHandler(paramObject, paramString);
    setDataHandler(localDataHandler);
  }
  
  public DataHandler getDataHandler()
    throws SOAPException
  {
    if (mimePart != null) {
      new DataHandler(new DataSource()
      {
        public InputStream getInputStream()
          throws IOException
        {
          return mimePart.read();
        }
        
        public OutputStream getOutputStream()
          throws IOException
        {
          throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option");
        }
        
        public String getContentType()
        {
          return mimePart.getContentType();
        }
        
        public String getName()
        {
          return "MIMEPart Wrapper DataSource";
        }
      });
    }
    if (dataHandler == null)
    {
      if (rawContent != null) {
        return new DataHandler(new MimePartDataSource(rawContent));
      }
      log.severe("SAAJ0502.soap.no.handler.for.attachment");
      throw new SOAPExceptionImpl("No data handler associated with this attachment");
    }
    return dataHandler;
  }
  
  public void setDataHandler(DataHandler paramDataHandler)
    throws IllegalArgumentException
  {
    if (mimePart != null)
    {
      mimePart.close();
      mimePart = null;
    }
    if (paramDataHandler == null)
    {
      log.severe("SAAJ0503.soap.no.null.to.dataHandler");
      throw new IllegalArgumentException("Null dataHandler argument to setDataHandler");
    }
    dataHandler = paramDataHandler;
    rawContent = null;
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "SAAJ0580.soap.set.Content-Type", new String[] { paramDataHandler.getContentType() });
    }
    setMimeHeader("Content-Type", paramDataHandler.getContentType());
  }
  
  public void removeAllMimeHeaders()
  {
    headers.removeAllHeaders();
  }
  
  public void removeMimeHeader(String paramString)
  {
    headers.removeHeader(paramString);
  }
  
  public String[] getMimeHeader(String paramString)
  {
    return headers.getHeader(paramString);
  }
  
  public void setMimeHeader(String paramString1, String paramString2)
  {
    headers.setHeader(paramString1, paramString2);
  }
  
  public void addMimeHeader(String paramString1, String paramString2)
  {
    headers.addHeader(paramString1, paramString2);
  }
  
  public Iterator getAllMimeHeaders()
  {
    return headers.getAllHeaders();
  }
  
  public Iterator getMatchingMimeHeaders(String[] paramArrayOfString)
  {
    return headers.getMatchingHeaders(paramArrayOfString);
  }
  
  public Iterator getNonMatchingMimeHeaders(String[] paramArrayOfString)
  {
    return headers.getNonMatchingHeaders(paramArrayOfString);
  }
  
  boolean hasAllHeaders(MimeHeaders paramMimeHeaders)
  {
    if (paramMimeHeaders != null)
    {
      Iterator localIterator = paramMimeHeaders.getAllHeaders();
      while (localIterator.hasNext())
      {
        MimeHeader localMimeHeader = (MimeHeader)localIterator.next();
        String[] arrayOfString = headers.getHeader(localMimeHeader.getName());
        int i = 0;
        if (arrayOfString != null) {
          for (int j = 0; j < arrayOfString.length; j++) {
            if (localMimeHeader.getValue().equalsIgnoreCase(arrayOfString[j]))
            {
              i = 1;
              break;
            }
          }
        }
        if (i == 0) {
          return false;
        }
      }
    }
    return true;
  }
  
  MimeBodyPart getMimePart()
    throws SOAPException
  {
    try
    {
      if (mimePart != null) {
        return new MimeBodyPart(mimePart);
      }
      if (rawContent != null)
      {
        copyMimeHeaders(headers, rawContent);
        return rawContent;
      }
      MimeBodyPart localMimeBodyPart = new MimeBodyPart();
      localMimeBodyPart.setDataHandler(dataHandler);
      copyMimeHeaders(headers, localMimeBodyPart);
      return localMimeBodyPart;
    }
    catch (Exception localException)
    {
      log.severe("SAAJ0504.soap.cannot.externalize.attachment");
      throw new SOAPExceptionImpl("Unable to externalize attachment", localException);
    }
  }
  
  public static void copyMimeHeaders(MimeHeaders paramMimeHeaders, MimeBodyPart paramMimeBodyPart)
    throws SOAPException
  {
    Iterator localIterator = paramMimeHeaders.getAllHeaders();
    while (localIterator.hasNext()) {
      try
      {
        MimeHeader localMimeHeader = (MimeHeader)localIterator.next();
        paramMimeBodyPart.setHeader(localMimeHeader.getName(), localMimeHeader.getValue());
      }
      catch (Exception localException)
      {
        log.severe("SAAJ0505.soap.cannot.copy.mime.hdr");
        throw new SOAPExceptionImpl("Unable to copy MIME header", localException);
      }
    }
  }
  
  public static void copyMimeHeaders(MimeBodyPart paramMimeBodyPart, AttachmentPartImpl paramAttachmentPartImpl)
    throws SOAPException
  {
    try
    {
      FinalArrayList localFinalArrayList = paramMimeBodyPart.getAllHeaders();
      int i = localFinalArrayList.size();
      for (int j = 0; j < i; j++)
      {
        com.sun.xml.internal.messaging.saaj.packaging.mime.Header localHeader = (com.sun.xml.internal.messaging.saaj.packaging.mime.Header)localFinalArrayList.get(j);
        if (!localHeader.getName().equalsIgnoreCase("Content-Type")) {
          paramAttachmentPartImpl.addMimeHeader(localHeader.getName(), localHeader.getValue());
        }
      }
    }
    catch (Exception localException)
    {
      log.severe("SAAJ0506.soap.cannot.copy.mime.hdrs.into.attachment");
      throw new SOAPExceptionImpl("Unable to copy MIME headers into attachment", localException);
    }
  }
  
  public void setBase64Content(InputStream paramInputStream, String paramString)
    throws SOAPException
  {
    if (mimePart != null)
    {
      mimePart.close();
      mimePart = null;
    }
    dataHandler = null;
    InputStream localInputStream = null;
    try
    {
      localInputStream = MimeUtility.decode(paramInputStream, "base64");
      InternetHeaders localInternetHeaders = new InternetHeaders();
      localInternetHeaders.setHeader("Content-Type", paramString);
      ByteOutputStream localByteOutputStream = new ByteOutputStream();
      localByteOutputStream.write(localInputStream);
      rawContent = new MimeBodyPart(localInternetHeaders, localByteOutputStream.getBytes(), localByteOutputStream.getCount());
      setMimeHeader("Content-Type", paramString);
      return;
    }
    catch (Exception localException)
    {
      log.log(Level.SEVERE, "SAAJ0578.soap.attachment.setbase64content.exception", localException);
      throw new SOAPExceptionImpl(localException.getLocalizedMessage());
    }
    finally
    {
      try
      {
        localInputStream.close();
      }
      catch (IOException localIOException2)
      {
        throw new SOAPException(localIOException2);
      }
    }
  }
  
  public InputStream getBase64Content()
    throws SOAPException
  {
    InputStream localInputStream;
    if (mimePart != null)
    {
      localInputStream = mimePart.read();
    }
    else if (rawContent != null)
    {
      try
      {
        localInputStream = rawContent.getInputStream();
      }
      catch (Exception localException1)
      {
        log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", localException1);
        throw new SOAPExceptionImpl(localException1.getLocalizedMessage());
      }
    }
    else if (dataHandler != null)
    {
      try
      {
        localInputStream = dataHandler.getInputStream();
      }
      catch (IOException localIOException1)
      {
        log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
        throw new SOAPExceptionImpl("DataHandler error" + localIOException1);
      }
    }
    else
    {
      log.severe("SAAJ0572.soap.no.content.for.attachment");
      throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }
    int j = 1024;
    if (localInputStream != null) {
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(j);
        OutputStream localOutputStream = MimeUtility.encode(localByteArrayOutputStream, "base64");
        byte[] arrayOfByte = new byte[j];
        int i;
        while ((i = localInputStream.read(arrayOfByte, 0, j)) != -1) {
          localOutputStream.write(arrayOfByte, 0, i);
        }
        localOutputStream.flush();
        arrayOfByte = localByteArrayOutputStream.toByteArray();
        ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
        return localByteArrayInputStream;
      }
      catch (Exception localException2)
      {
        log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", localException2);
        throw new SOAPExceptionImpl(localException2.getLocalizedMessage());
      }
      finally
      {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException3) {}
      }
    }
    log.log(Level.SEVERE, "SAAJ0572.soap.no.content.for.attachment");
    throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
  }
  
  public void setRawContent(InputStream paramInputStream, String paramString)
    throws SOAPException
  {
    if (mimePart != null)
    {
      mimePart.close();
      mimePart = null;
    }
    dataHandler = null;
    try
    {
      InternetHeaders localInternetHeaders = new InternetHeaders();
      localInternetHeaders.setHeader("Content-Type", paramString);
      ByteOutputStream localByteOutputStream = new ByteOutputStream();
      localByteOutputStream.write(paramInputStream);
      rawContent = new MimeBodyPart(localInternetHeaders, localByteOutputStream.getBytes(), localByteOutputStream.getCount());
      setMimeHeader("Content-Type", paramString);
      return;
    }
    catch (Exception localException)
    {
      log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", localException);
      throw new SOAPExceptionImpl(localException.getLocalizedMessage());
    }
    finally
    {
      try
      {
        paramInputStream.close();
      }
      catch (IOException localIOException2)
      {
        throw new SOAPException(localIOException2);
      }
    }
  }
  
  public void setRawContentBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString)
    throws SOAPException
  {
    if (mimePart != null)
    {
      mimePart.close();
      mimePart = null;
    }
    if (paramArrayOfByte == null) {
      throw new SOAPExceptionImpl("Null content passed to setRawContentBytes");
    }
    dataHandler = null;
    try
    {
      InternetHeaders localInternetHeaders = new InternetHeaders();
      localInternetHeaders.setHeader("Content-Type", paramString);
      rawContent = new MimeBodyPart(localInternetHeaders, paramArrayOfByte, paramInt1, paramInt2);
      setMimeHeader("Content-Type", paramString);
    }
    catch (Exception localException)
    {
      log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", localException);
      throw new SOAPExceptionImpl(localException.getLocalizedMessage());
    }
  }
  
  public InputStream getRawContent()
    throws SOAPException
  {
    if (mimePart != null) {
      return mimePart.read();
    }
    if (rawContent != null) {
      try
      {
        return rawContent.getInputStream();
      }
      catch (Exception localException)
      {
        log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", localException);
        throw new SOAPExceptionImpl(localException.getLocalizedMessage());
      }
    }
    if (dataHandler != null) {
      try
      {
        return dataHandler.getInputStream();
      }
      catch (IOException localIOException)
      {
        log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
        throw new SOAPExceptionImpl("DataHandler error" + localIOException);
      }
    }
    log.severe("SAAJ0572.soap.no.content.for.attachment");
    throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
  }
  
  public byte[] getRawContentBytes()
    throws SOAPException
  {
    InputStream localInputStream;
    if (mimePart != null) {
      try
      {
        localInputStream = mimePart.read();
        return ASCIIUtility.getBytes(localInputStream);
      }
      catch (IOException localIOException1)
      {
        log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", localIOException1);
        throw new SOAPExceptionImpl(localIOException1);
      }
    }
    if (rawContent != null) {
      try
      {
        localInputStream = rawContent.getInputStream();
        return ASCIIUtility.getBytes(localInputStream);
      }
      catch (Exception localException)
      {
        log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", localException);
        throw new SOAPExceptionImpl(localException);
      }
    }
    if (dataHandler != null) {
      try
      {
        localInputStream = dataHandler.getInputStream();
        return ASCIIUtility.getBytes(localInputStream);
      }
      catch (IOException localIOException2)
      {
        log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
        throw new SOAPExceptionImpl("DataHandler error" + localIOException2);
      }
    }
    log.severe("SAAJ0572.soap.no.content.for.attachment");
    throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
  }
  
  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public MimeHeaders getMimeHeaders()
  {
    return headers;
  }
  
  public static void initializeJavaActivationHandlers()
  {
    try
    {
      CommandMap localCommandMap = CommandMap.getDefaultCommandMap();
      if ((localCommandMap instanceof MailcapCommandMap))
      {
        MailcapCommandMap localMailcapCommandMap = (MailcapCommandMap)localCommandMap;
        if (!cmdMapInitialized(localMailcapCommandMap))
        {
          localMailcapCommandMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
          localMailcapCommandMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
          localMailcapCommandMap.addMailcap("application/fastinfoset;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.FastInfosetDataContentHandler");
          localMailcapCommandMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.ImageDataContentHandler");
          localMailcapCommandMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.StringDataContentHandler");
        }
      }
    }
    catch (Throwable localThrowable) {}
  }
  
  private static boolean cmdMapInitialized(MailcapCommandMap paramMailcapCommandMap)
  {
    CommandInfo[] arrayOfCommandInfo1 = paramMailcapCommandMap.getAllCommands("application/fastinfoset");
    if ((arrayOfCommandInfo1 == null) || (arrayOfCommandInfo1.length == 0)) {
      return false;
    }
    String str1 = "com.sun.xml.internal.ws.binding.FastInfosetDataContentHandler";
    for (CommandInfo localCommandInfo : arrayOfCommandInfo1)
    {
      String str2 = localCommandInfo.getCommandClass();
      if (str1.equals(str2)) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\AttachmentPartImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */