package com.oracle.webservices.internal.impl.internalspi.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import java.io.IOException;
import java.io.InputStream;

public abstract interface StreamDecoder
{
  public abstract Message decode(InputStream paramInputStream, String paramString, AttachmentSet paramAttachmentSet, SOAPVersion paramSOAPVersion)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\impl\internalspi\encoding\StreamDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */