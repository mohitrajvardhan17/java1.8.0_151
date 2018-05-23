package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSerializer
  implements Serializer
{
  protected Canonicalizer canon;
  
  public AbstractSerializer() {}
  
  public void setCanonicalizer(Canonicalizer paramCanonicalizer)
  {
    canon = paramCanonicalizer;
  }
  
  public String serialize(Element paramElement)
    throws Exception
  {
    return canonSerialize(paramElement);
  }
  
  public byte[] serializeToByteArray(Element paramElement)
    throws Exception
  {
    return canonSerializeToByteArray(paramElement);
  }
  
  public String serialize(NodeList paramNodeList)
    throws Exception
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    canon.setWriter(localByteArrayOutputStream);
    canon.notReset();
    for (int i = 0; i < paramNodeList.getLength(); i++) {
      canon.canonicalizeSubtree(paramNodeList.item(i));
    }
    String str = localByteArrayOutputStream.toString("UTF-8");
    localByteArrayOutputStream.reset();
    return str;
  }
  
  public byte[] serializeToByteArray(NodeList paramNodeList)
    throws Exception
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    canon.setWriter(localByteArrayOutputStream);
    canon.notReset();
    for (int i = 0; i < paramNodeList.getLength(); i++) {
      canon.canonicalizeSubtree(paramNodeList.item(i));
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public String canonSerialize(Node paramNode)
    throws Exception
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    canon.setWriter(localByteArrayOutputStream);
    canon.notReset();
    canon.canonicalizeSubtree(paramNode);
    String str = localByteArrayOutputStream.toString("UTF-8");
    localByteArrayOutputStream.reset();
    return str;
  }
  
  public byte[] canonSerializeToByteArray(Node paramNode)
    throws Exception
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    canon.setWriter(localByteArrayOutputStream);
    canon.notReset();
    canon.canonicalizeSubtree(paramNode);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public abstract Node deserialize(String paramString, Node paramNode)
    throws XMLEncryptionException;
  
  public abstract Node deserialize(byte[] paramArrayOfByte, Node paramNode)
    throws XMLEncryptionException;
  
  protected static byte[] createContext(byte[] paramArrayOfByte, Node paramNode)
    throws XMLEncryptionException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localByteArrayOutputStream, "UTF-8");
      localOutputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
      HashMap localHashMap = new HashMap();
      for (Node localNode1 = paramNode; localNode1 != null; localNode1 = localNode1.getParentNode())
      {
        NamedNodeMap localNamedNodeMap = localNode1.getAttributes();
        if (localNamedNodeMap != null) {
          for (int i = 0; i < localNamedNodeMap.getLength(); i++)
          {
            Node localNode2 = localNamedNodeMap.item(i);
            String str = localNode2.getNodeName();
            if (((str.equals("xmlns")) || (str.startsWith("xmlns:"))) && (!localHashMap.containsKey(localNode2.getNodeName())))
            {
              localOutputStreamWriter.write(" ");
              localOutputStreamWriter.write(str);
              localOutputStreamWriter.write("=\"");
              localOutputStreamWriter.write(localNode2.getNodeValue());
              localOutputStreamWriter.write("\"");
              localHashMap.put(str, localNode2.getNodeValue());
            }
          }
        }
      }
      localOutputStreamWriter.write(">");
      localOutputStreamWriter.flush();
      localByteArrayOutputStream.write(paramArrayOfByte);
      localOutputStreamWriter.write("</dummy>");
      localOutputStreamWriter.close();
      return localByteArrayOutputStream.toByteArray();
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new XMLEncryptionException("empty", localUnsupportedEncodingException);
    }
    catch (IOException localIOException)
    {
      throw new XMLEncryptionException("empty", localIOException);
    }
  }
  
  protected static String createContext(String paramString, Node paramNode)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
    HashMap localHashMap = new HashMap();
    for (Node localNode1 = paramNode; localNode1 != null; localNode1 = localNode1.getParentNode())
    {
      NamedNodeMap localNamedNodeMap = localNode1.getAttributes();
      if (localNamedNodeMap != null) {
        for (int i = 0; i < localNamedNodeMap.getLength(); i++)
        {
          Node localNode2 = localNamedNodeMap.item(i);
          String str = localNode2.getNodeName();
          if (((str.equals("xmlns")) || (str.startsWith("xmlns:"))) && (!localHashMap.containsKey(localNode2.getNodeName())))
          {
            localStringBuilder.append(" " + str + "=\"" + localNode2.getNodeValue() + "\"");
            localHashMap.put(str, localNode2.getNodeValue());
          }
        }
      }
    }
    localStringBuilder.append(">" + paramString + "</dummy>");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\AbstractSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */