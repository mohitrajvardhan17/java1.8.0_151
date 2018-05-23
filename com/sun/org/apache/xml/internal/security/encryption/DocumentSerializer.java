package com.sun.org.apache.xml.internal.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentSerializer
  extends AbstractSerializer
{
  protected DocumentBuilderFactory dbf;
  
  public DocumentSerializer() {}
  
  public Node deserialize(byte[] paramArrayOfByte, Node paramNode)
    throws XMLEncryptionException
  {
    byte[] arrayOfByte = createContext(paramArrayOfByte, paramNode);
    return deserialize(paramNode, new InputSource(new ByteArrayInputStream(arrayOfByte)));
  }
  
  public Node deserialize(String paramString, Node paramNode)
    throws XMLEncryptionException
  {
    String str = createContext(paramString, paramNode);
    return deserialize(paramNode, new InputSource(new StringReader(str)));
  }
  
  private Node deserialize(Node paramNode, InputSource paramInputSource)
    throws XMLEncryptionException
  {
    try
    {
      if (dbf == null)
      {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        dbf.setValidating(false);
      }
      DocumentBuilder localDocumentBuilder = dbf.newDocumentBuilder();
      Document localDocument1 = localDocumentBuilder.parse(paramInputSource);
      Document localDocument2 = null;
      if (9 == paramNode.getNodeType()) {
        localDocument2 = (Document)paramNode;
      } else {
        localDocument2 = paramNode.getOwnerDocument();
      }
      Element localElement = (Element)localDocument2.importNode(localDocument1.getDocumentElement(), true);
      DocumentFragment localDocumentFragment = localDocument2.createDocumentFragment();
      for (Node localNode = localElement.getFirstChild(); localNode != null; localNode = localElement.getFirstChild())
      {
        localElement.removeChild(localNode);
        localDocumentFragment.appendChild(localNode);
      }
      return localDocumentFragment;
    }
    catch (SAXException localSAXException)
    {
      throw new XMLEncryptionException("empty", localSAXException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new XMLEncryptionException("empty", localParserConfigurationException);
    }
    catch (IOException localIOException)
    {
      throw new XMLEncryptionException("empty", localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\DocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */