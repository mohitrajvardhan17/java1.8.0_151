package com.sun.xml.internal.ws.message.saaj;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.message.DOMHeader;
import javax.xml.soap.SOAPHeaderElement;

public final class SAAJHeader
  extends DOMHeader<SOAPHeaderElement>
{
  public SAAJHeader(SOAPHeaderElement paramSOAPHeaderElement)
  {
    super(paramSOAPHeaderElement);
  }
  
  @NotNull
  public String getRole(@NotNull SOAPVersion paramSOAPVersion)
  {
    String str = getAttribute(nsUri, roleAttributeName);
    if ((str == null) || (str.equals(""))) {
      str = implicitRole;
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\saaj\SAAJHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */