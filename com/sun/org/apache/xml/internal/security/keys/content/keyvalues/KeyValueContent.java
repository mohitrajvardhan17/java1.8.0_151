package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.security.PublicKey;

public abstract interface KeyValueContent
{
  public abstract PublicKey getPublicKey()
    throws XMLSecurityException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\keyvalues\KeyValueContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */