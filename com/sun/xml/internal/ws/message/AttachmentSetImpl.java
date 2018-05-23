package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.util.ArrayList;
import java.util.Iterator;

public final class AttachmentSetImpl
  implements AttachmentSet
{
  private final ArrayList<Attachment> attList = new ArrayList();
  
  public AttachmentSetImpl() {}
  
  public AttachmentSetImpl(Iterable<Attachment> paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Attachment localAttachment = (Attachment)localIterator.next();
      add(localAttachment);
    }
  }
  
  public Attachment get(String paramString)
  {
    for (int i = attList.size() - 1; i >= 0; i--)
    {
      Attachment localAttachment = (Attachment)attList.get(i);
      if (localAttachment.getContentId().equals(paramString)) {
        return localAttachment;
      }
    }
    return null;
  }
  
  public boolean isEmpty()
  {
    return attList.isEmpty();
  }
  
  public void add(Attachment paramAttachment)
  {
    attList.add(paramAttachment);
  }
  
  public Iterator<Attachment> iterator()
  {
    return attList.iterator();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\AttachmentSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */