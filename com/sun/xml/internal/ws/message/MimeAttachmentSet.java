package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.resources.EncodingMessages;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.ws.WebServiceException;

public final class MimeAttachmentSet
  implements AttachmentSet
{
  private final MimeMultipartParser mpp;
  private Map<String, Attachment> atts = new HashMap();
  
  public MimeAttachmentSet(MimeMultipartParser paramMimeMultipartParser)
  {
    mpp = paramMimeMultipartParser;
  }
  
  @Nullable
  public Attachment get(String paramString)
  {
    Attachment localAttachment = (Attachment)atts.get(paramString);
    if (localAttachment != null) {
      return localAttachment;
    }
    try
    {
      localAttachment = mpp.getAttachmentPart(paramString);
      if (localAttachment != null) {
        atts.put(paramString, localAttachment);
      }
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(paramString), localIOException);
    }
    return localAttachment;
  }
  
  public boolean isEmpty()
  {
    return (atts.size() <= 0) && (mpp.getAttachmentParts().isEmpty());
  }
  
  public void add(Attachment paramAttachment)
  {
    atts.put(paramAttachment.getContentId(), paramAttachment);
  }
  
  public Iterator<Attachment> iterator()
  {
    Map localMap = mpp.getAttachmentParts();
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (atts.get(localEntry.getKey()) == null) {
        atts.put(localEntry.getKey(), localEntry.getValue());
      }
    }
    return atts.values().iterator();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\MimeAttachmentSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */