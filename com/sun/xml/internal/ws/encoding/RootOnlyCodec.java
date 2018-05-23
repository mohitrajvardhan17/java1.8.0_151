package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public abstract interface RootOnlyCodec
  extends Codec
{
  public abstract void decode(@NotNull InputStream paramInputStream, @NotNull String paramString, @NotNull Packet paramPacket, @NotNull AttachmentSet paramAttachmentSet)
    throws IOException;
  
  public abstract void decode(@NotNull ReadableByteChannel paramReadableByteChannel, @NotNull String paramString, @NotNull Packet paramPacket, @NotNull AttachmentSet paramAttachmentSet);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\RootOnlyCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */