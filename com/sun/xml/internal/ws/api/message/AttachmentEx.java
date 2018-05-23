package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import java.util.Iterator;

public abstract interface AttachmentEx
  extends Attachment
{
  @NotNull
  public abstract Iterator<MimeHeader> getMimeHeaders();
  
  public static abstract interface MimeHeader
  {
    public abstract String getName();
    
    public abstract String getValue();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\AttachmentEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */