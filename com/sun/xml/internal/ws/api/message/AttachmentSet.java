package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.Nullable;

public abstract interface AttachmentSet
  extends Iterable<Attachment>
{
  @Nullable
  public abstract Attachment get(String paramString);
  
  public abstract boolean isEmpty();
  
  public abstract void add(Attachment paramAttachment);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\AttachmentSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */