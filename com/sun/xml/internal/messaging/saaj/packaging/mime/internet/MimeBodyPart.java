package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public final class MimeBodyPart
{
  public static final String ATTACHMENT = "attachment";
  public static final String INLINE = "inline";
  private static boolean setDefaultTextCharset = true;
  private DataHandler dh;
  private byte[] content;
  private int contentLength;
  private int start = 0;
  private InputStream contentStream;
  private final InternetHeaders headers;
  private MimeMultipart parent;
  private MIMEPart mimePart;
  
  public MimeBodyPart()
  {
    headers = new InternetHeaders();
  }
  
  public MimeBodyPart(InputStream paramInputStream)
    throws MessagingException
  {
    if ((!(paramInputStream instanceof ByteArrayInputStream)) && (!(paramInputStream instanceof BufferedInputStream)) && (!(paramInputStream instanceof SharedInputStream))) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    headers = new InternetHeaders(paramInputStream);
    Object localObject;
    if ((paramInputStream instanceof SharedInputStream))
    {
      localObject = (SharedInputStream)paramInputStream;
      contentStream = ((SharedInputStream)localObject).newStream(((SharedInputStream)localObject).getPosition(), -1L);
    }
    else
    {
      try
      {
        localObject = new ByteOutputStream();
        ((ByteOutputStream)localObject).write(paramInputStream);
        content = ((ByteOutputStream)localObject).getBytes();
        contentLength = ((ByteOutputStream)localObject).getCount();
      }
      catch (IOException localIOException)
      {
        throw new MessagingException("Error reading input stream", localIOException);
      }
    }
  }
  
  public MimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte, int paramInt)
  {
    headers = paramInternetHeaders;
    content = paramArrayOfByte;
    contentLength = paramInt;
  }
  
  public MimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    headers = paramInternetHeaders;
    content = paramArrayOfByte;
    start = paramInt1;
    contentLength = paramInt2;
  }
  
  public MimeBodyPart(MIMEPart paramMIMEPart)
  {
    mimePart = paramMIMEPart;
    headers = new InternetHeaders();
    List localList = mimePart.getAllHeaders();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Header localHeader = (Header)localIterator.next();
      headers.addHeader(localHeader.getName(), localHeader.getValue());
    }
  }
  
  public MimeMultipart getParent()
  {
    return parent;
  }
  
  public void setParent(MimeMultipart paramMimeMultipart)
  {
    parent = paramMimeMultipart;
  }
  
  public int getSize()
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
    if (content != null) {
      return contentLength;
    }
    if (contentStream != null) {
      try
      {
        int i = contentStream.available();
        if (i > 0) {
          return i;
        }
      }
      catch (IOException localIOException2) {}
    }
    return -1;
  }
  
  public int getLineCount()
  {
    return -1;
  }
  
  public String getContentType()
  {
    if (mimePart != null) {
      return mimePart.getContentType();
    }
    String str = getHeader("Content-Type", null);
    if (str == null) {
      str = "text/plain";
    }
    return str;
  }
  
  public boolean isMimeType(String paramString)
  {
    boolean bool;
    try
    {
      ContentType localContentType = new ContentType(getContentType());
      bool = localContentType.match(paramString);
    }
    catch (ParseException localParseException)
    {
      bool = getContentType().equalsIgnoreCase(paramString);
    }
    return bool;
  }
  
  public String getDisposition()
    throws MessagingException
  {
    String str = getHeader("Content-Disposition", null);
    if (str == null) {
      return null;
    }
    ContentDisposition localContentDisposition = new ContentDisposition(str);
    return localContentDisposition.getDisposition();
  }
  
  public void setDisposition(String paramString)
    throws MessagingException
  {
    if (paramString == null)
    {
      removeHeader("Content-Disposition");
    }
    else
    {
      String str = getHeader("Content-Disposition", null);
      if (str != null)
      {
        ContentDisposition localContentDisposition = new ContentDisposition(str);
        localContentDisposition.setDisposition(paramString);
        paramString = localContentDisposition.toString();
      }
      setHeader("Content-Disposition", paramString);
    }
  }
  
  public String getEncoding()
    throws MessagingException
  {
    String str = getHeader("Content-Transfer-Encoding", null);
    if (str == null) {
      return null;
    }
    str = str.trim();
    if ((str.equalsIgnoreCase("7bit")) || (str.equalsIgnoreCase("8bit")) || (str.equalsIgnoreCase("quoted-printable")) || (str.equalsIgnoreCase("base64"))) {
      return str;
    }
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(str, "()<>@,;:\\\"\t []/?=");
    HeaderTokenizer.Token localToken;
    int i;
    do
    {
      localToken = localHeaderTokenizer.next();
      i = localToken.getType();
      if (i == -4) {
        break;
      }
    } while (i != -1);
    return localToken.getValue();
    return str;
  }
  
  public String getContentID()
  {
    return getHeader("Content-ID", null);
  }
  
  public void setContentID(String paramString)
  {
    if (paramString == null) {
      removeHeader("Content-ID");
    } else {
      setHeader("Content-ID", paramString);
    }
  }
  
  public String getContentMD5()
  {
    return getHeader("Content-MD5", null);
  }
  
  public void setContentMD5(String paramString)
  {
    setHeader("Content-MD5", paramString);
  }
  
  public String[] getContentLanguage()
    throws MessagingException
  {
    String str = getHeader("Content-Language", null);
    if (str == null) {
      return null;
    }
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(str, "()<>@,;:\\\"\t []/?=");
    FinalArrayList localFinalArrayList = new FinalArrayList();
    for (;;)
    {
      HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
      int i = localToken.getType();
      if (i == -4) {
        break;
      }
      if (i == -1) {
        localFinalArrayList.add(localToken.getValue());
      }
    }
    if (localFinalArrayList.size() == 0) {
      return null;
    }
    return (String[])localFinalArrayList.toArray(new String[localFinalArrayList.size()]);
  }
  
  public void setContentLanguage(String[] paramArrayOfString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfString[0]);
    for (int i = 1; i < paramArrayOfString.length; i++) {
      localStringBuffer.append(',').append(paramArrayOfString[i]);
    }
    setHeader("Content-Language", localStringBuffer.toString());
  }
  
  public String getDescription()
  {
    String str = getHeader("Content-Description", null);
    if (str == null) {
      return null;
    }
    try
    {
      return MimeUtility.decodeText(MimeUtility.unfold(str));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return str;
  }
  
  public void setDescription(String paramString)
    throws MessagingException
  {
    setDescription(paramString, null);
  }
  
  public void setDescription(String paramString1, String paramString2)
    throws MessagingException
  {
    if (paramString1 == null)
    {
      removeHeader("Content-Description");
      return;
    }
    try
    {
      setHeader("Content-Description", MimeUtility.fold(21, MimeUtility.encodeText(paramString1, paramString2, null)));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new MessagingException("Encoding error", localUnsupportedEncodingException);
    }
  }
  
  public String getFileName()
    throws MessagingException
  {
    String str1 = null;
    String str2 = getHeader("Content-Disposition", null);
    Object localObject;
    if (str2 != null)
    {
      localObject = new ContentDisposition(str2);
      str1 = ((ContentDisposition)localObject).getParameter("filename");
    }
    if (str1 == null)
    {
      str2 = getHeader("Content-Type", null);
      if (str2 != null) {
        try
        {
          localObject = new ContentType(str2);
          str1 = ((ContentType)localObject).getParameter("name");
        }
        catch (ParseException localParseException) {}
      }
    }
    return str1;
  }
  
  public void setFileName(String paramString)
    throws MessagingException
  {
    String str = getHeader("Content-Disposition", null);
    ContentDisposition localContentDisposition = new ContentDisposition(str == null ? "attachment" : str);
    localContentDisposition.setParameter("filename", paramString);
    setHeader("Content-Disposition", localContentDisposition.toString());
    str = getHeader("Content-Type", null);
    if (str != null) {
      try
      {
        ContentType localContentType = new ContentType(str);
        localContentType.setParameter("name", paramString);
        setHeader("Content-Type", localContentType.toString());
      }
      catch (ParseException localParseException) {}
    }
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return getDataHandler().getInputStream();
  }
  
  InputStream getContentStream()
    throws MessagingException
  {
    if (mimePart != null) {
      return mimePart.read();
    }
    if (contentStream != null) {
      return ((SharedInputStream)contentStream).newStream(0L, -1L);
    }
    if (content != null) {
      return new ByteArrayInputStream(content, start, contentLength);
    }
    throw new MessagingException("No content");
  }
  
  public InputStream getRawInputStream()
    throws MessagingException
  {
    return getContentStream();
  }
  
  public DataHandler getDataHandler()
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
          return "MIMEPart Wrapped DataSource";
        }
      });
    }
    if (dh == null) {
      dh = new DataHandler(new MimePartDataSource(this));
    }
    return dh;
  }
  
  public Object getContent()
    throws IOException
  {
    return getDataHandler().getContent();
  }
  
  public void setDataHandler(DataHandler paramDataHandler)
  {
    if (mimePart != null) {
      mimePart = null;
    }
    dh = paramDataHandler;
    content = null;
    contentStream = null;
    removeHeader("Content-Type");
    removeHeader("Content-Transfer-Encoding");
  }
  
  public void setContent(Object paramObject, String paramString)
  {
    if (mimePart != null) {
      mimePart = null;
    }
    if ((paramObject instanceof MimeMultipart)) {
      setContent((MimeMultipart)paramObject);
    } else {
      setDataHandler(new DataHandler(paramObject, paramString));
    }
  }
  
  public void setText(String paramString)
  {
    setText(paramString, null);
  }
  
  public void setText(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      if (MimeUtility.checkAscii(paramString1) != 1) {
        paramString2 = MimeUtility.getDefaultMIMECharset();
      } else {
        paramString2 = "us-ascii";
      }
    }
    setContent(paramString1, "text/plain; charset=" + MimeUtility.quote(paramString2, "()<>@,;:\\\"\t []/?="));
  }
  
  public void setContent(MimeMultipart paramMimeMultipart)
  {
    if (mimePart != null) {
      mimePart = null;
    }
    setDataHandler(new DataHandler(paramMimeMultipart, paramMimeMultipart.getContentType().toString()));
    paramMimeMultipart.setParent(this);
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException
  {
    List localList = headers.getAllHeaderLines();
    int i = localList.size();
    for (int j = 0; j < i; j++) {
      OutputUtil.writeln((String)localList.get(j), paramOutputStream);
    }
    OutputUtil.writeln(paramOutputStream);
    if (contentStream != null)
    {
      ((SharedInputStream)contentStream).writeTo(0L, -1L, paramOutputStream);
    }
    else if (content != null)
    {
      paramOutputStream.write(content, start, contentLength);
    }
    else
    {
      OutputStream localOutputStream;
      if (dh != null)
      {
        localOutputStream = MimeUtility.encode(paramOutputStream, getEncoding());
        getDataHandler().writeTo(localOutputStream);
        if (paramOutputStream != localOutputStream) {
          localOutputStream.flush();
        }
      }
      else if (mimePart != null)
      {
        localOutputStream = MimeUtility.encode(paramOutputStream, getEncoding());
        getDataHandler().writeTo(localOutputStream);
        if (paramOutputStream != localOutputStream) {
          localOutputStream.flush();
        }
      }
      else
      {
        throw new MessagingException("no content");
      }
    }
  }
  
  public String[] getHeader(String paramString)
  {
    return headers.getHeader(paramString);
  }
  
  public String getHeader(String paramString1, String paramString2)
  {
    return headers.getHeader(paramString1, paramString2);
  }
  
  public void setHeader(String paramString1, String paramString2)
  {
    headers.setHeader(paramString1, paramString2);
  }
  
  public void addHeader(String paramString1, String paramString2)
  {
    headers.addHeader(paramString1, paramString2);
  }
  
  public void removeHeader(String paramString)
  {
    headers.removeHeader(paramString);
  }
  
  public FinalArrayList getAllHeaders()
  {
    return headers.getAllHeaders();
  }
  
  public void addHeaderLine(String paramString)
  {
    headers.addHeaderLine(paramString);
  }
  
  protected void updateHeaders()
    throws MessagingException
  {
    DataHandler localDataHandler = getDataHandler();
    if (localDataHandler == null) {
      return;
    }
    try
    {
      String str1 = localDataHandler.getContentType();
      int i = 0;
      int j = getHeader("Content-Type") == null ? 1 : 0;
      ContentType localContentType = new ContentType(str1);
      Object localObject1;
      if (localContentType.match("multipart/*"))
      {
        i = 1;
        localObject1 = localDataHandler.getContent();
        ((MimeMultipart)localObject1).updateHeaders();
      }
      else if (localContentType.match("message/rfc822"))
      {
        i = 1;
      }
      Object localObject2;
      if (i == 0)
      {
        if (getHeader("Content-Transfer-Encoding") == null) {
          setEncoding(MimeUtility.getEncoding(localDataHandler));
        }
        if ((j != 0) && (setDefaultTextCharset) && (localContentType.match("text/*")) && (localContentType.getParameter("charset") == null))
        {
          localObject2 = getEncoding();
          if ((localObject2 != null) && (((String)localObject2).equalsIgnoreCase("7bit"))) {
            localObject1 = "us-ascii";
          } else {
            localObject1 = MimeUtility.getDefaultMIMECharset();
          }
          localContentType.setParameter("charset", (String)localObject1);
          str1 = localContentType.toString();
        }
      }
      if (j != 0)
      {
        localObject1 = getHeader("Content-Disposition", null);
        if (localObject1 != null)
        {
          localObject2 = new ContentDisposition((String)localObject1);
          String str2 = ((ContentDisposition)localObject2).getParameter("filename");
          if (str2 != null)
          {
            localContentType.setParameter("name", str2);
            str1 = localContentType.toString();
          }
        }
        setHeader("Content-Type", str1);
      }
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("IOException updating headers", localIOException);
    }
  }
  
  private void setEncoding(String paramString)
  {
    setHeader("Content-Transfer-Encoding", paramString);
  }
  
  static
  {
    try
    {
      String str = System.getProperty("mail.mime.setdefaulttextcharset");
      setDefaultTextCharset = (str == null) || (!str.equalsIgnoreCase("false"));
    }
    catch (SecurityException localSecurityException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimeBodyPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */