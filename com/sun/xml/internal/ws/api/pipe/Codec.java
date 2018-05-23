package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public abstract interface Codec
{
  public abstract String getMimeType();
  
  public abstract ContentType getStaticContentType(Packet paramPacket);
  
  public abstract ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
    throws IOException;
  
  public abstract ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel);
  
  public abstract Codec copy();
  
  public abstract void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException;
  
  public abstract void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */