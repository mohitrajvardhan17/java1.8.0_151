package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMPGPData
  extends DOMStructure
  implements PGPData
{
  private final byte[] keyId;
  private final byte[] keyPacket;
  private final List<XMLStructure> externalElements;
  
  public DOMPGPData(byte[] paramArrayOfByte, List<? extends XMLStructure> paramList)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("keyPacket cannot be null");
    }
    if ((paramList == null) || (paramList.isEmpty()))
    {
      externalElements = Collections.emptyList();
    }
    else
    {
      externalElements = Collections.unmodifiableList(new ArrayList(paramList));
      int i = 0;
      int j = externalElements.size();
      while (i < j)
      {
        if (!(externalElements.get(i) instanceof XMLStructure)) {
          throw new ClassCastException("other[" + i + "] is not a valid PGPData type");
        }
        i++;
      }
    }
    keyPacket = ((byte[])paramArrayOfByte.clone());
    checkKeyPacket(paramArrayOfByte);
    keyId = null;
  }
  
  public DOMPGPData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, List<? extends XMLStructure> paramList)
  {
    if (paramArrayOfByte1 == null) {
      throw new NullPointerException("keyId cannot be null");
    }
    if (paramArrayOfByte1.length != 8) {
      throw new IllegalArgumentException("keyId must be 8 bytes long");
    }
    if ((paramList == null) || (paramList.isEmpty()))
    {
      externalElements = Collections.emptyList();
    }
    else
    {
      externalElements = Collections.unmodifiableList(new ArrayList(paramList));
      int i = 0;
      int j = externalElements.size();
      while (i < j)
      {
        if (!(externalElements.get(i) instanceof XMLStructure)) {
          throw new ClassCastException("other[" + i + "] is not a valid PGPData type");
        }
        i++;
      }
    }
    keyId = ((byte[])paramArrayOfByte1.clone());
    keyPacket = (paramArrayOfByte2 == null ? null : (byte[])paramArrayOfByte2.clone());
    if (paramArrayOfByte2 != null) {
      checkKeyPacket(paramArrayOfByte2);
    }
  }
  
  public DOMPGPData(Element paramElement)
    throws MarshalException
  {
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    NodeList localNodeList = paramElement.getChildNodes();
    int i = localNodeList.getLength();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      Node localNode = localNodeList.item(j);
      if (localNode.getNodeType() == 1)
      {
        Element localElement = (Element)localNode;
        String str = localElement.getLocalName();
        try
        {
          if (str.equals("PGPKeyID")) {
            arrayOfByte1 = Base64.decode(localElement);
          } else if (str.equals("PGPKeyPacket")) {
            arrayOfByte2 = Base64.decode(localElement);
          } else {
            localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localElement));
          }
        }
        catch (Base64DecodingException localBase64DecodingException)
        {
          throw new MarshalException(localBase64DecodingException);
        }
      }
    }
    keyId = arrayOfByte1;
    keyPacket = arrayOfByte2;
    externalElements = Collections.unmodifiableList(localArrayList);
  }
  
  public byte[] getKeyId()
  {
    return keyId == null ? null : (byte[])keyId.clone();
  }
  
  public byte[] getKeyPacket()
  {
    return keyPacket == null ? null : (byte[])keyPacket.clone();
  }
  
  public List getExternalElements()
  {
    return externalElements;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "PGPData", "http://www.w3.org/2000/09/xmldsig#", paramString);
    if (keyId != null)
    {
      localObject = DOMUtils.createElement(localDocument, "PGPKeyID", "http://www.w3.org/2000/09/xmldsig#", paramString);
      ((Element)localObject).appendChild(localDocument.createTextNode(Base64.encode(keyId)));
      localElement.appendChild((Node)localObject);
    }
    if (keyPacket != null)
    {
      localObject = DOMUtils.createElement(localDocument, "PGPKeyPacket", "http://www.w3.org/2000/09/xmldsig#", paramString);
      ((Element)localObject).appendChild(localDocument.createTextNode(Base64.encode(keyPacket)));
      localElement.appendChild((Node)localObject);
    }
    Object localObject = externalElements.iterator();
    while (((Iterator)localObject).hasNext())
    {
      XMLStructure localXMLStructure = (XMLStructure)((Iterator)localObject).next();
      DOMUtils.appendChild(localElement, ((javax.xml.crypto.dom.DOMStructure)localXMLStructure).getNode());
    }
    paramNode.appendChild(localElement);
  }
  
  private void checkKeyPacket(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 3) {
      throw new IllegalArgumentException("keypacket must be at least 3 bytes long");
    }
    int i = paramArrayOfByte[0];
    if ((i & 0x80) != 128) {
      throw new IllegalArgumentException("keypacket tag is invalid: bit 7 is not set");
    }
    if ((i & 0x40) != 64) {
      throw new IllegalArgumentException("old keypacket tag format is unsupported");
    }
    if (((i & 0x6) != 6) && ((i & 0xE) != 14) && ((i & 0x5) != 5) && ((i & 0x7) != 7)) {
      throw new IllegalArgumentException("keypacket tag is invalid: must be 6, 14, 5, or 7");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMPGPData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */