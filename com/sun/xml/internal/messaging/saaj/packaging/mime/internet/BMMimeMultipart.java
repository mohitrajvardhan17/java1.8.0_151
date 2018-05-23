package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import javax.activation.DataSource;

public class BMMimeMultipart
  extends MimeMultipart
{
  private boolean begining = true;
  int[] bcs = new int['Ā'];
  int[] gss = null;
  private static final int BUFFER_SIZE = 4096;
  private byte[] buffer = new byte['က'];
  private byte[] prevBuffer = new byte['က'];
  private BitSet lastPartFound = new BitSet(1);
  private InputStream in = null;
  private String boundary = null;
  int b = 0;
  private boolean lazyAttachments = false;
  byte[] buf = new byte['Ѐ'];
  
  public BMMimeMultipart() {}
  
  public BMMimeMultipart(String paramString)
  {
    super(paramString);
  }
  
  public BMMimeMultipart(DataSource paramDataSource, ContentType paramContentType)
    throws MessagingException
  {
    super(paramDataSource, paramContentType);
    boundary = paramContentType.getParameter("boundary");
  }
  
  public InputStream initStream()
    throws MessagingException
  {
    if (in == null)
    {
      try
      {
        in = ds.getInputStream();
        if ((!(in instanceof ByteArrayInputStream)) && (!(in instanceof BufferedInputStream)) && (!(in instanceof SharedInputStream))) {
          in = new BufferedInputStream(in);
        }
      }
      catch (Exception localException)
      {
        throw new MessagingException("No inputstream from datasource");
      }
      if (!in.markSupported()) {
        throw new MessagingException("InputStream does not support Marking");
      }
    }
    return in;
  }
  
  protected void parse()
    throws MessagingException
  {
    if (parsed) {
      return;
    }
    initStream();
    SharedInputStream localSharedInputStream = null;
    if ((in instanceof SharedInputStream)) {
      localSharedInputStream = (SharedInputStream)in;
    }
    String str = "--" + boundary;
    byte[] arrayOfByte = ASCIIUtility.getBytes(str);
    try
    {
      parse(in, arrayOfByte, localSharedInputStream);
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("IO Error", localIOException);
    }
    catch (Exception localException)
    {
      throw new MessagingException("Error", localException);
    }
    parsed = true;
  }
  
  public boolean lastBodyPartFound()
  {
    return lastPartFound.get(0);
  }
  
  public MimeBodyPart getNextPart(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream)
    throws Exception
  {
    if (!paramInputStream.markSupported()) {
      throw new Exception("InputStream does not support Marking");
    }
    if (begining)
    {
      compile(paramArrayOfByte);
      if (!skipPreamble(paramInputStream, paramArrayOfByte, paramSharedInputStream)) {
        throw new Exception("Missing Start Boundary, or boundary does not start on a new line");
      }
      begining = false;
    }
    if (lastBodyPartFound()) {
      throw new Exception("No parts found in Multipart InputStream");
    }
    if (paramSharedInputStream != null)
    {
      long l1 = paramSharedInputStream.getPosition();
      b = readHeaders(paramInputStream);
      if (b == -1) {
        throw new Exception("End of Stream encountered while reading part headers");
      }
      localObject = new long[1];
      localObject[0] = -1L;
      b = readBody(paramInputStream, paramArrayOfByte, (long[])localObject, null, paramSharedInputStream);
      if ((!ignoreMissingEndBoundary) && (b == -1) && (!lastBodyPartFound())) {
        throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
      }
      long l2 = localObject[0];
      MimeBodyPart localMimeBodyPart = createMimeBodyPart(paramSharedInputStream.newStream(l1, l2));
      addBodyPart(localMimeBodyPart);
      return localMimeBodyPart;
    }
    InternetHeaders localInternetHeaders = createInternetHeaders(paramInputStream);
    ByteOutputStream localByteOutputStream = new ByteOutputStream();
    b = readBody(paramInputStream, paramArrayOfByte, null, localByteOutputStream, null);
    if ((!ignoreMissingEndBoundary) && (b == -1) && (!lastBodyPartFound())) {
      throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
    }
    Object localObject = createMimeBodyPart(localInternetHeaders, localByteOutputStream.getBytes(), localByteOutputStream.getCount());
    addBodyPart((MimeBodyPart)localObject);
    return (MimeBodyPart)localObject;
  }
  
  public boolean parse(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream)
    throws Exception
  {
    while ((!lastPartFound.get(0)) && (b != -1)) {
      getNextPart(paramInputStream, paramArrayOfByte, paramSharedInputStream);
    }
    return true;
  }
  
  private int readHeaders(InputStream paramInputStream)
    throws Exception
  {
    int i = paramInputStream.read();
    while (i != -1) {
      if (i == 13)
      {
        i = paramInputStream.read();
        if (i == 10)
        {
          i = paramInputStream.read();
          if (i == 13)
          {
            i = paramInputStream.read();
            if (i == 10) {
              return i;
            }
          }
        }
      }
      else
      {
        i = paramInputStream.read();
      }
    }
    if (i == -1) {
      throw new Exception("End of inputstream while reading Mime-Part Headers");
    }
    return i;
  }
  
  private int readBody(InputStream paramInputStream, byte[] paramArrayOfByte, long[] paramArrayOfLong, ByteOutputStream paramByteOutputStream, SharedInputStream paramSharedInputStream)
    throws Exception
  {
    if (!find(paramInputStream, paramArrayOfByte, paramArrayOfLong, paramByteOutputStream, paramSharedInputStream)) {
      throw new Exception("Missing boundary delimitier while reading Body Part");
    }
    return b;
  }
  
  private boolean skipPreamble(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream)
    throws Exception
  {
    if (!find(paramInputStream, paramArrayOfByte, paramSharedInputStream)) {
      return false;
    }
    if (lastPartFound.get(0)) {
      throw new Exception("Found closing boundary delimiter while trying to skip preamble");
    }
    return true;
  }
  
  public int readNext(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt, BitSet paramBitSet, long[] paramArrayOfLong, SharedInputStream paramSharedInputStream)
    throws Exception
  {
    int i = paramInputStream.read(buffer, 0, paramInt);
    if (i == -1)
    {
      paramBitSet.flip(0);
    }
    else if (i < paramInt)
    {
      int j = 0;
      long l = 0L;
      for (int k = i; k < paramInt; k++)
      {
        if (paramSharedInputStream != null) {
          l = paramSharedInputStream.getPosition();
        }
        j = paramInputStream.read();
        if (j == -1)
        {
          paramBitSet.flip(0);
          if (paramSharedInputStream == null) {
            break;
          }
          paramArrayOfLong[0] = l;
          break;
        }
        buffer[k] = ((byte)j);
      }
      i = k;
    }
    return i;
  }
  
  public boolean find(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream)
    throws Exception
  {
    int j = paramArrayOfByte.length;
    int k = j - 1;
    int m = 0;
    BitSet localBitSet = new BitSet(1);
    long[] arrayOfLong = new long[1];
    for (;;)
    {
      paramInputStream.mark(j);
      m = readNext(paramInputStream, buffer, j, localBitSet, arrayOfLong, paramSharedInputStream);
      if (localBitSet.get(0)) {
        return false;
      }
      for (int i = k; (i >= 0) && (buffer[i] == paramArrayOfByte[i]); i--) {}
      if (i < 0)
      {
        if (!skipLWSPAndCRLF(paramInputStream)) {
          throw new Exception("Boundary does not terminate with CRLF");
        }
        return true;
      }
      int n = Math.max(i + 1 - bcs[(buffer[i] & 0x7F)], gss[i]);
      paramInputStream.reset();
      paramInputStream.skip(n);
    }
  }
  
  public boolean find(InputStream paramInputStream, byte[] paramArrayOfByte, long[] paramArrayOfLong, ByteOutputStream paramByteOutputStream, SharedInputStream paramSharedInputStream)
    throws Exception
  {
    int j = paramArrayOfByte.length;
    int k = j - 1;
    int m = 0;
    int n = 0;
    long l = -1L;
    byte[] arrayOfByte = null;
    int i1 = 1;
    BitSet localBitSet = new BitSet(1);
    for (;;)
    {
      paramInputStream.mark(j);
      if (i1 == 0)
      {
        arrayOfByte = prevBuffer;
        prevBuffer = buffer;
        buffer = arrayOfByte;
      }
      if (paramSharedInputStream != null) {
        l = paramSharedInputStream.getPosition();
      }
      m = readNext(paramInputStream, buffer, j, localBitSet, paramArrayOfLong, paramSharedInputStream);
      if (m == -1)
      {
        b = -1;
        if ((n == j) && (paramSharedInputStream == null)) {
          paramByteOutputStream.write(prevBuffer, 0, n);
        }
        return true;
      }
      if (m < j)
      {
        if (paramSharedInputStream == null) {
          paramByteOutputStream.write(buffer, 0, m);
        }
        b = -1;
        return true;
      }
      for (int i = k; (i >= 0) && (buffer[i] == paramArrayOfByte[i]); i--) {}
      if (i < 0)
      {
        if (n > 0) {
          if (n <= 2)
          {
            if (n == 2)
            {
              if (prevBuffer[1] == 10)
              {
                if ((prevBuffer[0] != 13) && (prevBuffer[0] != 10)) {
                  paramByteOutputStream.write(prevBuffer, 0, 1);
                }
                if (paramSharedInputStream != null) {
                  paramArrayOfLong[0] = l;
                }
              }
              else
              {
                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
              }
            }
            else if (n == 1)
            {
              if (prevBuffer[0] != 10) {
                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
              }
              if (paramSharedInputStream != null) {
                paramArrayOfLong[0] = l;
              }
            }
          }
          else if (n > 2) {
            if ((prevBuffer[(n - 2)] == 13) && (prevBuffer[(n - 1)] == 10))
            {
              if (paramSharedInputStream != null) {
                paramArrayOfLong[0] = (l - 2L);
              } else {
                paramByteOutputStream.write(prevBuffer, 0, n - 2);
              }
            }
            else if (prevBuffer[(n - 1)] == 10)
            {
              if (paramSharedInputStream != null) {
                paramArrayOfLong[0] = (l - 1L);
              } else {
                paramByteOutputStream.write(prevBuffer, 0, n - 1);
              }
            }
            else {
              throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
            }
          }
        }
        if (!skipLWSPAndCRLF(paramInputStream)) {}
        return true;
      }
      if ((n > 0) && (paramSharedInputStream == null)) {
        if (prevBuffer[(n - 1)] == 13)
        {
          if (buffer[0] == 10)
          {
            int i2 = k - 1;
            for (i2 = k - 1; (i2 > 0) && (buffer[(i2 + 1)] == paramArrayOfByte[i2]); i2--) {}
            if (i2 == 0) {
              paramByteOutputStream.write(prevBuffer, 0, n - 1);
            } else {
              paramByteOutputStream.write(prevBuffer, 0, n);
            }
          }
          else
          {
            paramByteOutputStream.write(prevBuffer, 0, n);
          }
        }
        else {
          paramByteOutputStream.write(prevBuffer, 0, n);
        }
      }
      n = Math.max(i + 1 - bcs[(buffer[i] & 0x7F)], gss[i]);
      paramInputStream.reset();
      paramInputStream.skip(n);
      if (i1 != 0) {
        i1 = 0;
      }
    }
  }
  
  private boolean skipLWSPAndCRLF(InputStream paramInputStream)
    throws Exception
  {
    b = paramInputStream.read();
    if (b == 10) {
      return true;
    }
    if (b == 13)
    {
      b = paramInputStream.read();
      if (b == 13) {
        b = paramInputStream.read();
      }
      if (b == 10) {
        return true;
      }
      throw new Exception("transport padding after a Mime Boundary  should end in a CRLF, found CR only");
    }
    if (b == 45)
    {
      b = paramInputStream.read();
      if (b != 45) {
        throw new Exception("Unexpected singular '-' character after Mime Boundary");
      }
      lastPartFound.flip(0);
      b = paramInputStream.read();
    }
    while ((b != -1) && ((b == 32) || (b == 9)))
    {
      b = paramInputStream.read();
      if (b == 10) {
        return true;
      }
      if (b == 13)
      {
        b = paramInputStream.read();
        if (b == 13) {
          b = paramInputStream.read();
        }
        if (b == 10) {
          return true;
        }
      }
    }
    if (b == -1)
    {
      if (!lastPartFound.get(0)) {
        throw new Exception("End of Multipart Stream before encountering  closing boundary delimiter");
      }
      return true;
    }
    return false;
  }
  
  private void compile(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    for (int j = 0; j < i; j++) {
      bcs[paramArrayOfByte[j]] = (j + 1);
    }
    gss = new int[i];
    label99:
    for (j = i; j > 0; j--)
    {
      for (int k = i - 1; k >= j; k--)
      {
        if (paramArrayOfByte[k] != paramArrayOfByte[(k - j)]) {
          break label99;
        }
        gss[(k - 1)] = j;
      }
      while (k > 0) {
        gss[(--k)] = j;
      }
    }
    gss[(i - 1)] = 1;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException
  {
    if (in != null) {
      contentType.setParameter("boundary", boundary);
    }
    String str = "--" + contentType.getParameter("boundary");
    for (int i = 0; i < parts.size(); i++)
    {
      OutputUtil.writeln(str, paramOutputStream);
      ((MimeBodyPart)parts.get(i)).writeTo(paramOutputStream);
      OutputUtil.writeln(paramOutputStream);
    }
    if (in != null)
    {
      OutputUtil.writeln(str, paramOutputStream);
      if (((paramOutputStream instanceof ByteOutputStream)) && (lazyAttachments))
      {
        ((ByteOutputStream)paramOutputStream).write(in);
      }
      else
      {
        ByteOutputStream localByteOutputStream = new ByteOutputStream(in.available());
        localByteOutputStream.write(in);
        localByteOutputStream.writeTo(paramOutputStream);
        in = localByteOutputStream.newInputStream();
      }
    }
    else
    {
      OutputUtil.writeAsAscii(str, paramOutputStream);
      OutputUtil.writeAsAscii("--", paramOutputStream);
    }
  }
  
  public void setInputStream(InputStream paramInputStream)
  {
    in = paramInputStream;
  }
  
  public InputStream getInputStream()
  {
    return in;
  }
  
  public void setBoundary(String paramString)
  {
    boundary = paramString;
    if (contentType != null) {
      contentType.setParameter("boundary", paramString);
    }
  }
  
  public String getBoundary()
  {
    return boundary;
  }
  
  public boolean isEndOfStream()
  {
    return b == -1;
  }
  
  public void setLazyAttachments(boolean paramBoolean)
  {
    lazyAttachments = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\BMMimeMultipart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */