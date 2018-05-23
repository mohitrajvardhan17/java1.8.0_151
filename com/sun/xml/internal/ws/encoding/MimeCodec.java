package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.AttachmentEx.MimeHeader;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.UUID;

abstract class MimeCodec
  implements Codec
{
  public static final String MULTIPART_RELATED_MIME_TYPE = "multipart/related";
  protected Codec mimeRootCodec;
  protected final SOAPVersion version;
  protected final WSFeatureList features;
  
  protected MimeCodec(SOAPVersion paramSOAPVersion, WSFeatureList paramWSFeatureList)
  {
    version = paramSOAPVersion;
    features = paramWSFeatureList;
  }
  
  public String getMimeType()
  {
    return "multipart/related";
  }
  
  protected Codec getMimeRootCodec(Packet paramPacket)
  {
    return mimeRootCodec;
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
    throws IOException
  {
    Message localMessage = paramPacket.getMessage();
    if (localMessage == null) {
      return null;
    }
    ContentTypeImpl localContentTypeImpl = (ContentTypeImpl)getStaticContentType(paramPacket);
    String str1 = localContentTypeImpl.getBoundary();
    int i = str1 != null ? 1 : 0;
    Codec localCodec = getMimeRootCodec(paramPacket);
    Object localObject;
    if (i != 0)
    {
      writeln("--" + str1, paramOutputStream);
      localContentType = localCodec.getStaticContentType(paramPacket);
      localObject = localContentType != null ? localContentType.getContentType() : localCodec.getMimeType();
      writeln("Content-Type: " + (String)localObject, paramOutputStream);
      writeln(paramOutputStream);
    }
    ContentType localContentType = localCodec.encode(paramPacket, paramOutputStream);
    if (i != 0)
    {
      writeln(paramOutputStream);
      localObject = localMessage.getAttachments().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Attachment localAttachment = (Attachment)((Iterator)localObject).next();
        writeln("--" + str1, paramOutputStream);
        String str2 = localAttachment.getContentId();
        if ((str2 != null) && (str2.length() > 0) && (str2.charAt(0) != '<')) {
          str2 = '<' + str2 + '>';
        }
        writeln("Content-Id:" + str2, paramOutputStream);
        writeln("Content-Type: " + localAttachment.getContentType(), paramOutputStream);
        writeCustomMimeHeaders(localAttachment, paramOutputStream);
        writeln("Content-Transfer-Encoding: binary", paramOutputStream);
        writeln(paramOutputStream);
        localAttachment.writeTo(paramOutputStream);
        writeln(paramOutputStream);
      }
      writeAsAscii("--" + str1, paramOutputStream);
      writeAsAscii("--", paramOutputStream);
    }
    return i != 0 ? localContentTypeImpl : localContentType;
  }
  
  private void writeCustomMimeHeaders(Attachment paramAttachment, OutputStream paramOutputStream)
    throws IOException
  {
    if ((paramAttachment instanceof AttachmentEx))
    {
      Iterator localIterator = ((AttachmentEx)paramAttachment).getMimeHeaders();
      while (localIterator.hasNext())
      {
        AttachmentEx.MimeHeader localMimeHeader = (AttachmentEx.MimeHeader)localIterator.next();
        String str = localMimeHeader.getName();
        if ((!"Content-Type".equalsIgnoreCase(str)) && (!"Content-Id".equalsIgnoreCase(str))) {
          writeln(str + ": " + localMimeHeader.getValue(), paramOutputStream);
        }
      }
    }
  }
  
  public ContentType getStaticContentType(Packet paramPacket)
  {
    ContentType localContentType = (ContentType)paramPacket.getInternalContentType();
    if (localContentType != null) {
      return localContentType;
    }
    Message localMessage = paramPacket.getMessage();
    int i = !localMessage.getAttachments().isEmpty() ? 1 : 0;
    Codec localCodec = getMimeRootCodec(paramPacket);
    if (i != 0)
    {
      String str1 = "uuid:" + UUID.randomUUID().toString();
      String str2 = "boundary=\"" + str1 + "\"";
      String str3 = "multipart/related; type=\"" + localCodec.getMimeType() + "\"; " + str2;
      ContentTypeImpl localContentTypeImpl = new ContentTypeImpl(str3, soapAction, null);
      localContentTypeImpl.setBoundary(str1);
      localContentTypeImpl.setBoundaryParameter(str2);
      paramPacket.setContentType(localContentTypeImpl);
      return localContentTypeImpl;
    }
    localContentType = localCodec.getStaticContentType(paramPacket);
    paramPacket.setContentType(localContentType);
    return localContentType;
  }
  
  protected MimeCodec(MimeCodec paramMimeCodec)
  {
    version = version;
    features = features;
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    MimeMultipartParser localMimeMultipartParser = new MimeMultipartParser(paramInputStream, paramString, (StreamingAttachmentFeature)features.get(StreamingAttachmentFeature.class));
    decode(localMimeMultipartParser, paramPacket);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket)
  {
    throw new UnsupportedOperationException();
  }
  
  protected abstract void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket)
    throws IOException;
  
  public abstract MimeCodec copy();
  
  public static void writeln(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    writeAsAscii(paramString, paramOutputStream);
    writeln(paramOutputStream);
  }
  
  public static void writeAsAscii(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      paramOutputStream.write((byte)paramString.charAt(j));
    }
  }
  
  public static void writeln(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(13);
    paramOutputStream.write(10);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\MimeCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */