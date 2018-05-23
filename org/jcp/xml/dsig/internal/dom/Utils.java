package org.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class Utils
{
  private Utils() {}
  
  public static byte[] readBytesFromStream(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte['Ѐ'];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
      if (i < 1024) {
        break;
      }
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  static Set<Node> toNodeSet(Iterator<Node> paramIterator)
  {
    HashSet localHashSet = new HashSet();
    while (paramIterator.hasNext())
    {
      Node localNode = (Node)paramIterator.next();
      localHashSet.add(localNode);
      if (localNode.getNodeType() == 1)
      {
        NamedNodeMap localNamedNodeMap = localNode.getAttributes();
        int i = 0;
        int j = localNamedNodeMap.getLength();
        while (i < j)
        {
          localHashSet.add(localNamedNodeMap.item(i));
          i++;
        }
      }
    }
    return localHashSet;
  }
  
  public static String parseIdFromSameDocumentURI(String paramString)
  {
    if (paramString.length() == 0) {
      return null;
    }
    String str = paramString.substring(1);
    if ((str != null) && (str.startsWith("xpointer(id(")))
    {
      int i = str.indexOf('\'');
      int j = str.indexOf('\'', i + 1);
      str = str.substring(i + 1, j);
    }
    return str;
  }
  
  public static boolean sameDocumentURI(String paramString)
  {
    return (paramString != null) && ((paramString.length() == 0) || (paramString.charAt(0) == '#'));
  }
  
  static boolean secureValidation(XMLCryptoContext paramXMLCryptoContext)
  {
    if (paramXMLCryptoContext == null) {
      return false;
    }
    return getBoolean(paramXMLCryptoContext, "org.jcp.xml.dsig.secureValidation");
  }
  
  private static boolean getBoolean(XMLCryptoContext paramXMLCryptoContext, String paramString)
  {
    Boolean localBoolean = (Boolean)paramXMLCryptoContext.getProperty(paramString);
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */