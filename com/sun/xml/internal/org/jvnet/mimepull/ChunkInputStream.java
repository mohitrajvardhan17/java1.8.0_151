package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;

final class ChunkInputStream
  extends InputStream
{
  Chunk current;
  int offset;
  int len;
  final MIMEMessage msg;
  final MIMEPart part;
  byte[] buf;
  
  public ChunkInputStream(MIMEMessage paramMIMEMessage, MIMEPart paramMIMEPart, Chunk paramChunk)
  {
    current = paramChunk;
    len = current.data.size();
    buf = current.data.read();
    msg = paramMIMEMessage;
    part = paramMIMEPart;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (!fetch()) {
      return -1;
    }
    paramInt2 = Math.min(paramInt2, len - offset);
    System.arraycopy(buf, offset, paramArrayOfByte, paramInt1, paramInt2);
    return paramInt2;
  }
  
  public int read()
    throws IOException
  {
    if (!fetch()) {
      return -1;
    }
    return buf[(offset++)] & 0xFF;
  }
  
  private boolean fetch()
  {
    if (current == null) {
      throw new IllegalStateException("Stream already closed");
    }
    while (offset == len)
    {
      while ((!part.parsed) && (current.next == null)) {
        msg.makeProgress();
      }
      current = current.next;
      if (current == null) {
        return false;
      }
      offset = 0;
      buf = current.data.read();
      len = current.data.size();
    }
    return true;
  }
  
  public void close()
    throws IOException
  {
    super.close();
    current = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\ChunkInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */