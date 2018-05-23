package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.AttachmentEx.MimeHeader;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class MimeMultipartParser
{
  private final String start;
  private final MIMEMessage message;
  private Attachment root;
  private ContentTypeImpl contentType;
  private final Map<String, Attachment> attachments = new HashMap();
  private boolean gotAll;
  
  public MimeMultipartParser(InputStream paramInputStream, String paramString, StreamingAttachmentFeature paramStreamingAttachmentFeature)
  {
    contentType = new ContentTypeImpl(paramString);
    String str1 = contentType.getBoundary();
    if ((str1 == null) || (str1.equals(""))) {
      throw new WebServiceException("MIME boundary parameter not found" + contentType);
    }
    message = (paramStreamingAttachmentFeature != null ? new MIMEMessage(paramInputStream, str1, paramStreamingAttachmentFeature.getConfig()) : new MIMEMessage(paramInputStream, str1));
    String str2 = contentType.getRootId();
    if ((str2 != null) && (str2.length() > 2) && (str2.charAt(0) == '<') && (str2.charAt(str2.length() - 1) == '>')) {
      str2 = str2.substring(1, str2.length() - 1);
    }
    start = str2;
  }
  
  @Nullable
  public Attachment getRootPart()
  {
    if (root == null) {
      root = new PartAttachment(start != null ? message.getPart(start) : message.getPart(0));
    }
    return root;
  }
  
  @NotNull
  public Map<String, Attachment> getAttachmentParts()
  {
    if (!gotAll)
    {
      MIMEPart localMIMEPart1 = start != null ? message.getPart(start) : message.getPart(0);
      List localList = message.getAttachments();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        MIMEPart localMIMEPart2 = (MIMEPart)localIterator.next();
        if (localMIMEPart2 != localMIMEPart1)
        {
          String str = localMIMEPart2.getContentId();
          if (!attachments.containsKey(str))
          {
            PartAttachment localPartAttachment = new PartAttachment(localMIMEPart2);
            attachments.put(localPartAttachment.getContentId(), localPartAttachment);
          }
        }
      }
      gotAll = true;
    }
    return attachments;
  }
  
  @Nullable
  public Attachment getAttachmentPart(String paramString)
    throws IOException
  {
    Object localObject = (Attachment)attachments.get(paramString);
    if (localObject == null)
    {
      MIMEPart localMIMEPart = message.getPart(paramString);
      localObject = new PartAttachment(localMIMEPart);
      attachments.put(paramString, localObject);
    }
    return (Attachment)localObject;
  }
  
  public ContentTypeImpl getContentType()
  {
    return contentType;
  }
  
  static class PartAttachment
    implements AttachmentEx
  {
    final MIMEPart part;
    byte[] buf;
    private StreamingDataHandler streamingDataHandler;
    
    PartAttachment(MIMEPart paramMIMEPart)
    {
      part = paramMIMEPart;
    }
    
    @NotNull
    public String getContentId()
    {
      return part.getContentId();
    }
    
    @NotNull
    public String getContentType()
    {
      return part.getContentType();
    }
    
    public byte[] asByteArray()
    {
      ByteArrayBuffer localByteArrayBuffer;
      if (buf == null)
      {
        localByteArrayBuffer = new ByteArrayBuffer();
        try
        {
          localByteArrayBuffer.write(part.readOnce());
          if (localByteArrayBuffer != null) {
            try
            {
              localByteArrayBuffer.close();
            }
            catch (IOException localIOException1)
            {
              Logger.getLogger(MimeMultipartParser.class.getName()).log(Level.FINE, null, localIOException1);
            }
          }
          buf = localByteArrayBuffer.toByteArray();
        }
        catch (IOException localIOException2)
        {
          throw new WebServiceException(localIOException2);
        }
        finally
        {
          if (localByteArrayBuffer != null) {
            try
            {
              localByteArrayBuffer.close();
            }
            catch (IOException localIOException3)
            {
              Logger.getLogger(MimeMultipartParser.class.getName()).log(Level.FINE, null, localIOException3);
            }
          }
        }
      }
      return buf;
    }
    
    public DataHandler asDataHandler()
    {
      if (streamingDataHandler == null) {
        streamingDataHandler = (buf != null ? new DataSourceStreamingDataHandler(new ByteArrayDataSource(buf, getContentType())) : new MIMEPartStreamingDataHandler(part));
      }
      return streamingDataHandler;
    }
    
    public Source asSource()
    {
      return buf != null ? new StreamSource(new ByteArrayInputStream(buf)) : new StreamSource(part.read());
    }
    
    public InputStream asInputStream()
    {
      return buf != null ? new ByteArrayInputStream(buf) : part.read();
    }
    
    public void writeTo(OutputStream paramOutputStream)
      throws IOException
    {
      if (buf != null)
      {
        paramOutputStream.write(buf);
      }
      else
      {
        InputStream localInputStream = part.read();
        byte[] arrayOfByte = new byte[' '];
        int i;
        while ((i = localInputStream.read(arrayOfByte)) != -1) {
          paramOutputStream.write(arrayOfByte, 0, i);
        }
        localInputStream.close();
      }
    }
    
    public void writeTo(SOAPMessage paramSOAPMessage)
      throws SOAPException
    {
      paramSOAPMessage.createAttachmentPart().setDataHandler(asDataHandler());
    }
    
    public Iterator<AttachmentEx.MimeHeader> getMimeHeaders()
    {
      final Iterator localIterator = part.getAllHeaders().iterator();
      new Iterator()
      {
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public AttachmentEx.MimeHeader next()
        {
          final Header localHeader = (Header)localIterator.next();
          new AttachmentEx.MimeHeader()
          {
            public String getValue()
            {
              return localHeader.getValue();
            }
            
            public String getName()
            {
              return localHeader.getName();
            }
          };
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\MimeMultipartParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */