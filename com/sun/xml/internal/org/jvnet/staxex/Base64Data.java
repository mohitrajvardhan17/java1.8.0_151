package com.sun.xml.internal.org.jvnet.staxex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Base64Data
  implements CharSequence, Cloneable
{
  private DataHandler dataHandler;
  private byte[] data;
  private int dataLen;
  private boolean dataCloneByRef;
  private String mimeType;
  private static final Logger logger;
  private static final int CHUNK_SIZE;
  
  public Base64Data() {}
  
  public Base64Data(Base64Data paramBase64Data)
  {
    paramBase64Data.get();
    if (dataCloneByRef)
    {
      data = data;
    }
    else
    {
      data = new byte[dataLen];
      System.arraycopy(data, 0, data, 0, dataLen);
    }
    dataCloneByRef = true;
    dataLen = dataLen;
    dataHandler = null;
    mimeType = mimeType;
  }
  
  public void set(byte[] paramArrayOfByte, int paramInt, String paramString, boolean paramBoolean)
  {
    data = paramArrayOfByte;
    dataLen = paramInt;
    dataCloneByRef = paramBoolean;
    dataHandler = null;
    mimeType = paramString;
  }
  
  public void set(byte[] paramArrayOfByte, int paramInt, String paramString)
  {
    set(paramArrayOfByte, paramInt, paramString, false);
  }
  
  public void set(byte[] paramArrayOfByte, String paramString)
  {
    set(paramArrayOfByte, paramArrayOfByte.length, paramString, false);
  }
  
  public void set(DataHandler paramDataHandler)
  {
    assert (paramDataHandler != null);
    dataHandler = paramDataHandler;
    data = null;
  }
  
  public DataHandler getDataHandler()
  {
    if (dataHandler == null) {
      dataHandler = new Base64StreamingDataHandler(new Base64DataSource(null));
    } else if (!(dataHandler instanceof StreamingDataHandler)) {
      dataHandler = new FilterDataHandler(dataHandler);
    }
    return dataHandler;
  }
  
  public byte[] getExact()
  {
    get();
    if (dataLen != data.length)
    {
      byte[] arrayOfByte = new byte[dataLen];
      System.arraycopy(data, 0, arrayOfByte, 0, dataLen);
      data = arrayOfByte;
    }
    return data;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (dataHandler != null) {
      return dataHandler.getInputStream();
    }
    return new ByteArrayInputStream(data, 0, dataLen);
  }
  
  public boolean hasData()
  {
    return data != null;
  }
  
  public byte[] get()
  {
    if (data == null) {
      try
      {
        ByteArrayOutputStreamEx localByteArrayOutputStreamEx = new ByteArrayOutputStreamEx(1024);
        InputStream localInputStream = dataHandler.getDataSource().getInputStream();
        localByteArrayOutputStreamEx.readFrom(localInputStream);
        localInputStream.close();
        data = localByteArrayOutputStreamEx.getBuffer();
        dataLen = localByteArrayOutputStreamEx.size();
        dataCloneByRef = true;
      }
      catch (IOException localIOException)
      {
        dataLen = 0;
      }
    }
    return data;
  }
  
  public int getDataLen()
  {
    get();
    return dataLen;
  }
  
  public String getMimeType()
  {
    if (mimeType == null) {
      return "application/octet-stream";
    }
    return mimeType;
  }
  
  public int length()
  {
    get();
    return (dataLen + 2) / 3 * 4;
  }
  
  public char charAt(int paramInt)
  {
    int i = paramInt % 4;
    int j = paramInt / 4 * 3;
    int k;
    switch (i)
    {
    case 0: 
      return Base64Encoder.encode(data[j] >> 2);
    case 1: 
      if (j + 1 < dataLen) {
        k = data[(j + 1)];
      } else {
        k = 0;
      }
      return Base64Encoder.encode((data[j] & 0x3) << 4 | k >> 4 & 0xF);
    case 2: 
      if (j + 1 < dataLen)
      {
        k = data[(j + 1)];
        int m;
        if (j + 2 < dataLen) {
          m = data[(j + 2)];
        } else {
          m = 0;
        }
        return Base64Encoder.encode((k & 0xF) << 2 | m >> 6 & 0x3);
      }
      return '=';
    case 3: 
      if (j + 2 < dataLen) {
        return Base64Encoder.encode(data[(j + 2)] & 0x3F);
      }
      return '=';
    }
    throw new IllegalStateException();
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    get();
    for (int i = paramInt1; i < paramInt2; i++) {
      localStringBuilder.append(charAt(i));
    }
    return localStringBuilder;
  }
  
  public String toString()
  {
    get();
    return Base64Encoder.print(data, 0, dataLen);
  }
  
  public void writeTo(char[] paramArrayOfChar, int paramInt)
  {
    get();
    Base64Encoder.print(data, 0, dataLen, paramArrayOfChar, paramInt);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws IOException, XMLStreamException
  {
    if (data == null)
    {
      try
      {
        InputStream localInputStream = dataHandler.getDataSource().getInputStream();
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        Base64EncoderStream localBase64EncoderStream = new Base64EncoderStream(paramXMLStreamWriter, localByteArrayOutputStream);
        byte[] arrayOfByte = new byte[CHUNK_SIZE];
        int i;
        while ((i = localInputStream.read(arrayOfByte)) != -1) {
          localBase64EncoderStream.write(arrayOfByte, 0, i);
        }
        localByteArrayOutputStream.close();
        localBase64EncoderStream.close();
      }
      catch (IOException localIOException)
      {
        dataLen = 0;
        throw localIOException;
      }
    }
    else
    {
      String str = Base64Encoder.print(data, 0, dataLen);
      paramXMLStreamWriter.writeCharacters(str);
    }
  }
  
  public Base64Data clone()
  {
    try
    {
      Base64Data localBase64Data = (Base64Data)super.clone();
      localBase64Data.get();
      if (dataCloneByRef)
      {
        data = data;
      }
      else
      {
        data = new byte[dataLen];
        System.arraycopy(data, 0, data, 0, dataLen);
      }
      dataCloneByRef = true;
      dataLen = dataLen;
      dataHandler = null;
      mimeType = mimeType;
      return localBase64Data;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      Logger.getLogger(Base64Data.class.getName()).log(Level.SEVERE, null, localCloneNotSupportedException);
    }
    return null;
  }
  
  static String getProperty(String paramString)
  {
    if (System.getSecurityManager() == null) {
      return System.getProperty(paramString);
    }
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperty(val$propName);
      }
    });
  }
  
  static
  {
    logger = Logger.getLogger(Base64Data.class.getName());
    int i = 1024;
    try
    {
      String str = getProperty("com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize");
      if (str != null) {
        i = Integer.parseInt(str);
      }
    }
    catch (Exception localException)
    {
      logger.log(Level.INFO, "Error reading com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize property", localException);
    }
    CHUNK_SIZE = i;
  }
  
  private final class Base64DataSource
    implements DataSource
  {
    private Base64DataSource() {}
    
    public String getContentType()
    {
      return getMimeType();
    }
    
    public InputStream getInputStream()
    {
      return new ByteArrayInputStream(data, 0, dataLen);
    }
    
    public String getName()
    {
      return null;
    }
    
    public OutputStream getOutputStream()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private final class Base64StreamingDataHandler
    extends StreamingDataHandler
  {
    Base64StreamingDataHandler(DataSource paramDataSource)
    {
      super();
    }
    
    public InputStream readOnce()
      throws IOException
    {
      return getDataSource().getInputStream();
    }
    
    /* Error */
    public void moveTo(File paramFile)
      throws IOException
    {
      // Byte code:
      //   0: new 29	java/io/FileOutputStream
      //   3: dup
      //   4: aload_1
      //   5: invokespecial 57	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   8: astore_2
      //   9: aload_2
      //   10: aload_0
      //   11: getfield 50	com/sun/xml/internal/org/jvnet/staxex/Base64Data$Base64StreamingDataHandler:this$0	Lcom/sun/xml/internal/org/jvnet/staxex/Base64Data;
      //   14: invokestatic 52	com/sun/xml/internal/org/jvnet/staxex/Base64Data:access$100	(Lcom/sun/xml/internal/org/jvnet/staxex/Base64Data;)[B
      //   17: iconst_0
      //   18: aload_0
      //   19: getfield 50	com/sun/xml/internal/org/jvnet/staxex/Base64Data$Base64StreamingDataHandler:this$0	Lcom/sun/xml/internal/org/jvnet/staxex/Base64Data;
      //   22: invokestatic 51	com/sun/xml/internal/org/jvnet/staxex/Base64Data:access$200	(Lcom/sun/xml/internal/org/jvnet/staxex/Base64Data;)I
      //   25: invokevirtual 56	java/io/FileOutputStream:write	([BII)V
      //   28: aload_2
      //   29: invokevirtual 55	java/io/FileOutputStream:close	()V
      //   32: goto +10 -> 42
      //   35: astore_3
      //   36: aload_2
      //   37: invokevirtual 55	java/io/FileOutputStream:close	()V
      //   40: aload_3
      //   41: athrow
      //   42: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	43	0	this	Base64StreamingDataHandler
      //   0	43	1	paramFile	File
      //   8	29	2	localFileOutputStream	FileOutputStream
      //   35	6	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   9	28	35	finally
    }
    
    public void close()
      throws IOException
    {}
  }
  
  private static final class FilterDataHandler
    extends StreamingDataHandler
  {
    FilterDataHandler(DataHandler paramDataHandler)
    {
      super();
    }
    
    public InputStream readOnce()
      throws IOException
    {
      return getDataSource().getInputStream();
    }
    
    public void moveTo(File paramFile)
      throws IOException
    {
      byte[] arrayOfByte = new byte[' '];
      InputStream localInputStream = null;
      FileOutputStream localFileOutputStream = null;
      try
      {
        localInputStream = getDataSource().getInputStream();
        localFileOutputStream = new FileOutputStream(paramFile);
        for (;;)
        {
          int i = localInputStream.read(arrayOfByte);
          if (i == -1) {
            break;
          }
          localFileOutputStream.write(arrayOfByte, 0, i);
        }
        return;
      }
      finally
      {
        if (localInputStream != null) {
          try
          {
            localInputStream.close();
          }
          catch (IOException localIOException3) {}
        }
        if (localFileOutputStream != null) {
          try
          {
            localFileOutputStream.close();
          }
          catch (IOException localIOException4) {}
        }
      }
    }
    
    public void close()
      throws IOException
    {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\Base64Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */