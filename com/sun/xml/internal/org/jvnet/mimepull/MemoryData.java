package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

final class MemoryData
  implements Data
{
  private static final Logger LOGGER = Logger.getLogger(MemoryData.class.getName());
  private final byte[] data;
  private final int len;
  private final MIMEConfig config;
  
  MemoryData(ByteBuffer paramByteBuffer, MIMEConfig paramMIMEConfig)
  {
    data = paramByteBuffer.array();
    len = paramByteBuffer.limit();
    config = paramMIMEConfig;
  }
  
  public int size()
  {
    return len;
  }
  
  public byte[] read()
  {
    return data;
  }
  
  public long writeTo(DataFile paramDataFile)
  {
    return paramDataFile.writeTo(data, 0, len);
  }
  
  public Data createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer)
  {
    if ((!config.isOnlyMemory()) && (inMemory >= config.memoryThreshold))
    {
      try
      {
        String str1 = config.getTempFilePrefix();
        String str2 = config.getTempFileSuffix();
        File localFile = TempFiles.createTempFile(str1, str2, config.getTempDir());
        localFile.deleteOnExit();
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "Created temp file = {0}", localFile);
        }
        localFile.deleteOnExit();
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "Created temp file = {0}", localFile);
        }
        dataFile = new DataFile(localFile);
      }
      catch (IOException localIOException)
      {
        throw new MIMEParsingException(localIOException);
      }
      if (head != null) {
        for (Chunk localChunk = head; localChunk != null; localChunk = next)
        {
          long l = data.writeTo(dataFile);
          data = new FileData(dataFile, l, len);
        }
      }
      return new FileData(dataFile, paramByteBuffer);
    }
    return new MemoryData(paramByteBuffer, config);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MemoryData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */