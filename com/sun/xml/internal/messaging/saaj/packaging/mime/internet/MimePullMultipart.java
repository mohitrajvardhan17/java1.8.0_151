package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.soap.AttachmentPartImpl;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataSource;

public class MimePullMultipart
  extends MimeMultipart
{
  private InputStream in = null;
  private String boundary = null;
  private MIMEMessage mm = null;
  private DataSource dataSource = null;
  private ContentType contType = null;
  private String startParam = null;
  private MIMEPart soapPart = null;
  
  public MimePullMultipart(DataSource paramDataSource, ContentType paramContentType)
    throws MessagingException
  {
    parsed = false;
    if (paramContentType == null) {
      contType = new ContentType(paramDataSource.getContentType());
    } else {
      contType = paramContentType;
    }
    dataSource = paramDataSource;
    boundary = contType.getParameter("boundary");
  }
  
  public MIMEPart readAndReturnSOAPPart()
    throws MessagingException
  {
    if (soapPart != null) {
      throw new MessagingException("Inputstream from datasource was already consumed");
    }
    readSOAPPart();
    return soapPart;
  }
  
  protected void readSOAPPart()
    throws MessagingException
  {
    try
    {
      if (soapPart != null) {
        return;
      }
      in = dataSource.getInputStream();
      MIMEConfig localMIMEConfig = new MIMEConfig();
      mm = new MIMEMessage(in, boundary, localMIMEConfig);
      String str = contType.getParameter("start");
      if (startParam == null)
      {
        soapPart = mm.getPart(0);
      }
      else
      {
        if ((str != null) && (str.length() > 2) && (str.charAt(0) == '<') && (str.charAt(str.length() - 1) == '>')) {
          str = str.substring(1, str.length() - 1);
        }
        startParam = str;
        soapPart = mm.getPart(startParam);
      }
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("No inputstream from datasource", localIOException);
    }
  }
  
  public void parseAll()
    throws MessagingException
  {
    if (parsed) {
      return;
    }
    if (soapPart == null) {
      readSOAPPart();
    }
    List localList = mm.getAttachments();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      MIMEPart localMIMEPart = (MIMEPart)localIterator.next();
      if (localMIMEPart != soapPart)
      {
        AttachmentPartImpl localAttachmentPartImpl = new AttachmentPartImpl(localMIMEPart);
        addBodyPart(new MimeBodyPart(localMIMEPart));
      }
    }
    parsed = true;
  }
  
  protected void parse()
    throws MessagingException
  {
    parseAll();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimePullMultipart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */