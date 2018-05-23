package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

final class StreamSOAP12Codec
  extends StreamSOAPCodec
{
  public static final String SOAP12_MIME_TYPE = "application/soap+xml";
  public static final String DEFAULT_SOAP12_CONTENT_TYPE = "application/soap+xml; charset=utf-8";
  private static final List<String> EXPECTED_CONTENT_TYPES = Collections.singletonList("application/soap+xml");
  
  StreamSOAP12Codec()
  {
    super(SOAPVersion.SOAP_12);
  }
  
  StreamSOAP12Codec(WSBinding paramWSBinding)
  {
    super(paramWSBinding);
  }
  
  StreamSOAP12Codec(WSFeatureList paramWSFeatureList)
  {
    super(paramWSFeatureList);
  }
  
  public String getMimeType()
  {
    return "application/soap+xml";
  }
  
  protected com.sun.xml.internal.ws.api.pipe.ContentType getContentType(Packet paramPacket)
  {
    ContentTypeImpl.Builder localBuilder = getContenTypeBuilder(paramPacket);
    if (soapAction == null) {
      return localBuilder.build();
    }
    contentType = (contentType + ";action=" + fixQuotesAroundSoapAction(soapAction));
    return localBuilder.build();
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket, AttachmentSet paramAttachmentSet)
    throws IOException
  {
    ContentType localContentType = new ContentType(paramString);
    soapAction = fixQuotesAroundSoapAction(localContentType.getParameter("action"));
    super.decode(paramInputStream, paramString, paramPacket, paramAttachmentSet);
  }
  
  private String fixQuotesAroundSoapAction(String paramString)
  {
    if ((paramString != null) && ((!paramString.startsWith("\"")) || (!paramString.endsWith("\""))))
    {
      String str = paramString;
      if (!paramString.startsWith("\"")) {
        str = "\"" + str;
      }
      if (!paramString.endsWith("\"")) {
        str = str + "\"";
      }
      return str;
    }
    return paramString;
  }
  
  protected List<String> getExpectedContentTypes()
  {
    return EXPECTED_CONTENT_TYPES;
  }
  
  protected String getDefaultContentType()
  {
    return "application/soap+xml; charset=utf-8";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\StreamSOAP12Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */