package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract interface Serializer
{
  public abstract void setCanonicalizer(Canonicalizer paramCanonicalizer);
  
  public abstract byte[] serializeToByteArray(Element paramElement)
    throws Exception;
  
  public abstract byte[] serializeToByteArray(NodeList paramNodeList)
    throws Exception;
  
  public abstract byte[] canonSerializeToByteArray(Node paramNode)
    throws Exception;
  
  public abstract Node deserialize(byte[] paramArrayOfByte, Node paramNode)
    throws XMLEncryptionException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\Serializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */