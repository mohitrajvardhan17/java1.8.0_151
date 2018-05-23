package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MultipartDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class MimeMultipart
{
  protected DataSource ds = null;
  protected boolean parsed = true;
  protected FinalArrayList parts = new FinalArrayList();
  protected ContentType contentType;
  protected MimeBodyPart parent;
  protected static final boolean ignoreMissingEndBoundary = SAAJUtil.getSystemBoolean("saaj.mime.multipart.ignoremissingendboundary");
  
  public MimeMultipart()
  {
    this("mixed");
  }
  
  public MimeMultipart(String paramString)
  {
    String str = UniqueValue.getUniqueBoundaryValue();
    contentType = new ContentType("multipart", paramString, null);
    contentType.setParameter("boundary", str);
  }
  
  public MimeMultipart(DataSource paramDataSource, ContentType paramContentType)
    throws MessagingException
  {
    parsed = false;
    ds = paramDataSource;
    if (paramContentType == null) {
      contentType = new ContentType(paramDataSource.getContentType());
    } else {
      contentType = paramContentType;
    }
  }
  
  public void setSubType(String paramString)
  {
    contentType.setSubType(paramString);
  }
  
  public int getCount()
    throws MessagingException
  {
    parse();
    if (parts == null) {
      return 0;
    }
    return parts.size();
  }
  
  public MimeBodyPart getBodyPart(int paramInt)
    throws MessagingException
  {
    parse();
    if (parts == null) {
      throw new IndexOutOfBoundsException("No such BodyPart");
    }
    return (MimeBodyPart)parts.get(paramInt);
  }
  
  public MimeBodyPart getBodyPart(String paramString)
    throws MessagingException
  {
    parse();
    int i = getCount();
    for (int j = 0; j < i; j++)
    {
      MimeBodyPart localMimeBodyPart = getBodyPart(j);
      String str = localMimeBodyPart.getContentID();
      Object localObject = str != null ? str.replaceFirst("^<", "").replaceFirst(">$", "") : null;
      if ((str != null) && ((str.equals(paramString)) || (paramString.equals(localObject)))) {
        return localMimeBodyPart;
      }
    }
    return null;
  }
  
  protected void updateHeaders()
    throws MessagingException
  {
    for (int i = 0; i < parts.size(); i++) {
      ((MimeBodyPart)parts.get(i)).updateHeaders();
    }
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException
  {
    parse();
    String str = "--" + contentType.getParameter("boundary");
    for (int i = 0; i < parts.size(); i++)
    {
      OutputUtil.writeln(str, paramOutputStream);
      getBodyPart(i).writeTo(paramOutputStream);
      OutputUtil.writeln(paramOutputStream);
    }
    OutputUtil.writeAsAscii(str, paramOutputStream);
    OutputUtil.writeAsAscii("--", paramOutputStream);
    paramOutputStream.flush();
  }
  
  protected void parse()
    throws MessagingException
  {
    if (parsed) {
      return;
    }
    SharedInputStream localSharedInputStream = null;
    long l1 = 0L;
    long l2 = 0L;
    int i = 0;
    Object localObject;
    try
    {
      localObject = ds.getInputStream();
      if ((!(localObject instanceof ByteArrayInputStream)) && (!(localObject instanceof BufferedInputStream)) && (!(localObject instanceof SharedInputStream))) {
        localObject = new BufferedInputStream((InputStream)localObject);
      }
    }
    catch (Exception localException)
    {
      throw new MessagingException("No inputstream from datasource");
    }
    if ((localObject instanceof SharedInputStream)) {
      localSharedInputStream = (SharedInputStream)localObject;
    }
    String str1 = "--" + contentType.getParameter("boundary");
    byte[] arrayOfByte = ASCIIUtility.getBytes(str1);
    int j = arrayOfByte.length;
    try
    {
      LineInputStream localLineInputStream = new LineInputStream((InputStream)localObject);
      String str2;
      while ((str2 = localLineInputStream.readLine()) != null)
      {
        for (k = str2.length() - 1; k >= 0; k--)
        {
          int m = str2.charAt(k);
          if ((m != 32) && (m != 9)) {
            break;
          }
        }
        str2 = str2.substring(0, k + 1);
        if (str2.equals(str1)) {
          break;
        }
      }
      if (str2 == null) {
        throw new MessagingException("Missing start boundary");
      }
      int k = 0;
      while (k == 0)
      {
        InternetHeaders localInternetHeaders = null;
        if (localSharedInputStream != null)
        {
          l1 = localSharedInputStream.getPosition();
          while (((str2 = localLineInputStream.readLine()) != null) && (str2.length() > 0)) {}
          if (str2 == null)
          {
            if (ignoreMissingEndBoundary) {
              break;
            }
            throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
          }
        }
        else
        {
          localInternetHeaders = createInternetHeaders((InputStream)localObject);
        }
        if (!((InputStream)localObject).markSupported()) {
          throw new MessagingException("Stream doesn't support mark");
        }
        ByteOutputStream localByteOutputStream = null;
        if (localSharedInputStream == null) {
          localByteOutputStream = new ByteOutputStream();
        }
        int i1 = 1;
        int i2 = -1;
        int i3 = -1;
        for (;;)
        {
          if (i1 != 0)
          {
            ((InputStream)localObject).mark(j + 4 + 1000);
            for (int i4 = 0; (i4 < j) && (((InputStream)localObject).read() == arrayOfByte[i4]); i4++) {}
            if (i4 == j)
            {
              int i5 = ((InputStream)localObject).read();
              if ((i5 == 45) && (((InputStream)localObject).read() == 45))
              {
                k = 1;
                i = 1;
                break;
              }
              while ((i5 == 32) || (i5 == 9)) {
                i5 = ((InputStream)localObject).read();
              }
              if (i5 == 10) {
                break;
              }
              if (i5 == 13)
              {
                ((InputStream)localObject).mark(1);
                if (((InputStream)localObject).read() == 10) {
                  break;
                }
                ((InputStream)localObject).reset();
                break;
              }
            }
            ((InputStream)localObject).reset();
            if ((localByteOutputStream != null) && (i2 != -1))
            {
              localByteOutputStream.write(i2);
              if (i3 != -1) {
                localByteOutputStream.write(i3);
              }
              i2 = i3 = -1;
            }
          }
          int n;
          if ((n = ((InputStream)localObject).read()) < 0)
          {
            k = 1;
            break;
          }
          if ((n == 13) || (n == 10))
          {
            i1 = 1;
            if (localSharedInputStream != null) {
              l2 = localSharedInputStream.getPosition() - 1L;
            }
            i2 = n;
            if (n == 13)
            {
              ((InputStream)localObject).mark(1);
              if ((n = ((InputStream)localObject).read()) == 10) {
                i3 = n;
              } else {
                ((InputStream)localObject).reset();
              }
            }
          }
          else
          {
            i1 = 0;
            if (localByteOutputStream != null) {
              localByteOutputStream.write(n);
            }
          }
        }
        MimeBodyPart localMimeBodyPart;
        if (localSharedInputStream != null) {
          localMimeBodyPart = createMimeBodyPart(localSharedInputStream.newStream(l1, l2));
        } else {
          localMimeBodyPart = createMimeBodyPart(localInternetHeaders, localByteOutputStream.getBytes(), localByteOutputStream.getCount());
        }
        addBodyPart(localMimeBodyPart);
      }
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("IO Error", localIOException);
    }
    if ((!ignoreMissingEndBoundary) && (i == 0) && (localSharedInputStream == null)) {
      throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
    }
    parsed = true;
  }
  
  protected InternetHeaders createInternetHeaders(InputStream paramInputStream)
    throws MessagingException
  {
    return new InternetHeaders(paramInputStream);
  }
  
  protected MimeBodyPart createMimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte, int paramInt)
  {
    return new MimeBodyPart(paramInternetHeaders, paramArrayOfByte, paramInt);
  }
  
  protected MimeBodyPart createMimeBodyPart(InputStream paramInputStream)
    throws MessagingException
  {
    return new MimeBodyPart(paramInputStream);
  }
  
  protected void setMultipartDataSource(MultipartDataSource paramMultipartDataSource)
    throws MessagingException
  {
    contentType = new ContentType(paramMultipartDataSource.getContentType());
    int i = paramMultipartDataSource.getCount();
    for (int j = 0; j < i; j++) {
      addBodyPart(paramMultipartDataSource.getBodyPart(j));
    }
  }
  
  public ContentType getContentType()
  {
    return contentType;
  }
  
  public boolean removeBodyPart(MimeBodyPart paramMimeBodyPart)
    throws MessagingException
  {
    if (parts == null) {
      throw new MessagingException("No such body part");
    }
    boolean bool = parts.remove(paramMimeBodyPart);
    paramMimeBodyPart.setParent(null);
    return bool;
  }
  
  public void removeBodyPart(int paramInt)
  {
    if (parts == null) {
      throw new IndexOutOfBoundsException("No such BodyPart");
    }
    MimeBodyPart localMimeBodyPart = (MimeBodyPart)parts.get(paramInt);
    parts.remove(paramInt);
    localMimeBodyPart.setParent(null);
  }
  
  public synchronized void addBodyPart(MimeBodyPart paramMimeBodyPart)
  {
    if (parts == null) {
      parts = new FinalArrayList();
    }
    parts.add(paramMimeBodyPart);
    paramMimeBodyPart.setParent(this);
  }
  
  public synchronized void addBodyPart(MimeBodyPart paramMimeBodyPart, int paramInt)
  {
    if (parts == null) {
      parts = new FinalArrayList();
    }
    parts.add(paramInt, paramMimeBodyPart);
    paramMimeBodyPart.setParent(this);
  }
  
  MimeBodyPart getParent()
  {
    return parent;
  }
  
  void setParent(MimeBodyPart paramMimeBodyPart)
  {
    parent = paramMimeBodyPart;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimeMultipart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */