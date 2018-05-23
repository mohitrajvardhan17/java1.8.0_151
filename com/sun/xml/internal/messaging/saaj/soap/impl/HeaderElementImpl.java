package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

public abstract class HeaderElementImpl
  extends ElementImpl
  implements SOAPHeaderElement
{
  protected static Name RELAY_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("relay");
  protected static Name MUST_UNDERSTAND_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("mustUnderstand");
  Name actorAttNameWithoutNS = NameImpl.createFromTagName("actor");
  Name roleAttNameWithoutNS = NameImpl.createFromTagName("role");
  
  public HeaderElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  public HeaderElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  protected abstract NameImpl getActorAttributeName();
  
  protected abstract NameImpl getRoleAttributeName();
  
  protected abstract NameImpl getMustunderstandAttributeName();
  
  protected abstract boolean getMustunderstandAttributeValue(String paramString);
  
  protected abstract String getMustunderstandLiteralValue(boolean paramBoolean);
  
  protected abstract NameImpl getRelayAttributeName();
  
  protected abstract boolean getRelayAttributeValue(String paramString);
  
  protected abstract String getRelayLiteralValue(boolean paramBoolean);
  
  protected abstract String getActorOrRole();
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    if (!(paramSOAPElement instanceof SOAPHeader))
    {
      log.severe("SAAJ0130.impl.header.elem.parent.mustbe.header");
      throw new SOAPException("Parent of a SOAPHeaderElement has to be a SOAPHeader");
    }
    super.setParentElement(paramSOAPElement);
  }
  
  public void setActor(String paramString)
  {
    try
    {
      removeAttribute(getActorAttributeName());
      addAttribute(getActorAttributeName(), paramString);
    }
    catch (SOAPException localSOAPException) {}
  }
  
  public void setRole(String paramString)
    throws SOAPException
  {
    removeAttribute(getRoleAttributeName());
    addAttribute(getRoleAttributeName(), paramString);
  }
  
  public String getActor()
  {
    String str = getAttributeValue(getActorAttributeName());
    return str;
  }
  
  public String getRole()
  {
    String str = getAttributeValue(getRoleAttributeName());
    return str;
  }
  
  public void setMustUnderstand(boolean paramBoolean)
  {
    try
    {
      removeAttribute(getMustunderstandAttributeName());
      addAttribute(getMustunderstandAttributeName(), getMustunderstandLiteralValue(paramBoolean));
    }
    catch (SOAPException localSOAPException) {}
  }
  
  public boolean getMustUnderstand()
  {
    String str = getAttributeValue(getMustunderstandAttributeName());
    if (str != null) {
      return getMustunderstandAttributeValue(str);
    }
    return false;
  }
  
  public void setRelay(boolean paramBoolean)
    throws SOAPException
  {
    removeAttribute(getRelayAttributeName());
    addAttribute(getRelayAttributeName(), getRelayLiteralValue(paramBoolean));
  }
  
  public boolean getRelay()
  {
    String str = getAttributeValue(getRelayAttributeName());
    if (str != null) {
      return getRelayAttributeValue(str);
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\HeaderElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */