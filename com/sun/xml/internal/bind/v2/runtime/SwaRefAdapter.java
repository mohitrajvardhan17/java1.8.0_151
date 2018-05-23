package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class SwaRefAdapter
  extends XmlAdapter<String, DataHandler>
{
  public SwaRefAdapter() {}
  
  public DataHandler unmarshal(String paramString)
  {
    AttachmentUnmarshaller localAttachmentUnmarshaller = getInstanceparent.getAttachmentUnmarshaller();
    return localAttachmentUnmarshaller.getAttachmentAsDataHandler(paramString);
  }
  
  public String marshal(DataHandler paramDataHandler)
  {
    if (paramDataHandler == null) {
      return null;
    }
    AttachmentMarshaller localAttachmentMarshaller = getInstanceattachmentMarshaller;
    return localAttachmentMarshaller.addSwaRefAttachment(paramDataHandler);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\SwaRefAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */