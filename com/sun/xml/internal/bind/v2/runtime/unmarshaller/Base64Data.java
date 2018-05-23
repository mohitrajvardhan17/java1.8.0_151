package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class Base64Data
  extends Pcdata
{
  private DataHandler dataHandler;
  private byte[] data;
  private int dataLen;
  @Nullable
  private String mimeType;
  
  public Base64Data() {}
  
  public void set(byte[] paramArrayOfByte, int paramInt, @Nullable String paramString)
  {
    data = paramArrayOfByte;
    dataLen = paramInt;
    dataHandler = null;
    mimeType = paramString;
  }
  
  public void set(byte[] paramArrayOfByte, @Nullable String paramString)
  {
    set(paramArrayOfByte, paramArrayOfByte.length, paramString);
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
      dataHandler = new DataHandler(new DataSource()
      {
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
      });
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
      return DatatypeConverterImpl.encode(data[j] >> 2);
    case 1: 
      if (j + 1 < dataLen) {
        k = data[(j + 1)];
      } else {
        k = 0;
      }
      return DatatypeConverterImpl.encode((data[j] & 0x3) << 4 | k >> 4 & 0xF);
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
        return DatatypeConverterImpl.encode((k & 0xF) << 2 | m >> 6 & 0x3);
      }
      return '=';
    case 3: 
      if (j + 2 < dataLen) {
        return DatatypeConverterImpl.encode(data[(j + 2)] & 0x3F);
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
    return DatatypeConverterImpl._printBase64Binary(data, 0, dataLen);
  }
  
  public void writeTo(char[] paramArrayOfChar, int paramInt)
  {
    get();
    DatatypeConverterImpl._printBase64Binary(data, 0, dataLen, paramArrayOfChar, paramInt);
  }
  
  public void writeTo(UTF8XmlOutput paramUTF8XmlOutput)
    throws IOException
  {
    get();
    paramUTF8XmlOutput.text(data, dataLen);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws IOException, XMLStreamException
  {
    get();
    DatatypeConverterImpl._printBase64Binary(data, 0, dataLen, paramXMLStreamWriter);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\Base64Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */