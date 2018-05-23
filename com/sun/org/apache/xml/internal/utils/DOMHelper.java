package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @deprecated
 */
public class DOMHelper
{
  Map<Node, NSInfo> m_NSInfos = new HashMap();
  protected static final NSInfo m_NSInfoUnProcWithXMLNS = new NSInfo(false, true);
  protected static final NSInfo m_NSInfoUnProcWithoutXMLNS = new NSInfo(false, false);
  protected static final NSInfo m_NSInfoUnProcNoAncestorXMLNS = new NSInfo(false, false, 2);
  protected static final NSInfo m_NSInfoNullWithXMLNS = new NSInfo(true, true);
  protected static final NSInfo m_NSInfoNullWithoutXMLNS = new NSInfo(true, false);
  protected static final NSInfo m_NSInfoNullNoAncestorXMLNS = new NSInfo(true, false, 2);
  protected Vector m_candidateNoAncestorXMLNS = new Vector();
  protected Document m_DOMFactory = null;
  
  public DOMHelper() {}
  
  public static Document createDocument(boolean paramBoolean)
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setValidating(true);
      if (paramBoolean) {
        try
        {
          localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (ParserConfigurationException localParserConfigurationException2) {}
      }
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      Document localDocument = localDocumentBuilder.newDocument();
      return localDocument;
    }
    catch (ParserConfigurationException localParserConfigurationException1)
    {
      throw new RuntimeException(XMLMessages.createXMLMessage("ER_CREATEDOCUMENT_NOT_SUPPORTED", null));
    }
  }
  
  public static Document createDocument()
  {
    return createDocument(false);
  }
  
  public boolean shouldStripSourceNode(Node paramNode)
    throws TransformerException
  {
    return false;
  }
  
  public String getUniqueID(Node paramNode)
  {
    return "N" + Integer.toHexString(paramNode.hashCode()).toUpperCase();
  }
  
  public static boolean isNodeAfter(Node paramNode1, Node paramNode2)
  {
    if ((paramNode1 == paramNode2) || (isNodeTheSame(paramNode1, paramNode2))) {
      return true;
    }
    boolean bool = true;
    Node localNode1 = getParentOfNode(paramNode1);
    Node localNode2 = getParentOfNode(paramNode2);
    if ((localNode1 == localNode2) || (isNodeTheSame(localNode1, localNode2)))
    {
      if (null != localNode1) {
        bool = isNodeAfterSibling(localNode1, paramNode1, paramNode2);
      }
    }
    else
    {
      int i = 2;
      int j = 2;
      while (localNode1 != null)
      {
        i++;
        localNode1 = getParentOfNode(localNode1);
      }
      while (localNode2 != null)
      {
        j++;
        localNode2 = getParentOfNode(localNode2);
      }
      Node localNode3 = paramNode1;
      Node localNode4 = paramNode2;
      int k;
      int m;
      if (i < j)
      {
        k = j - i;
        for (m = 0; m < k; m++) {
          localNode4 = getParentOfNode(localNode4);
        }
      }
      else if (i > j)
      {
        k = i - j;
        for (m = 0; m < k; m++) {
          localNode3 = getParentOfNode(localNode3);
        }
      }
      Node localNode5 = null;
      Node localNode6 = null;
      while (null != localNode3)
      {
        if ((localNode3 == localNode4) || (isNodeTheSame(localNode3, localNode4)))
        {
          if (null == localNode5)
          {
            bool = i < j;
            break;
          }
          bool = isNodeAfterSibling(localNode3, localNode5, localNode6);
          break;
        }
        localNode5 = localNode3;
        localNode3 = getParentOfNode(localNode3);
        localNode6 = localNode4;
        localNode4 = getParentOfNode(localNode4);
      }
    }
    return bool;
  }
  
  public static boolean isNodeTheSame(Node paramNode1, Node paramNode2)
  {
    if (((paramNode1 instanceof DTMNodeProxy)) && ((paramNode2 instanceof DTMNodeProxy))) {
      return ((DTMNodeProxy)paramNode1).equals((DTMNodeProxy)paramNode2);
    }
    return paramNode1 == paramNode2;
  }
  
  private static boolean isNodeAfterSibling(Node paramNode1, Node paramNode2, Node paramNode3)
  {
    boolean bool = false;
    int i = paramNode2.getNodeType();
    int j = paramNode3.getNodeType();
    if ((2 != i) && (2 == j))
    {
      bool = false;
    }
    else if ((2 == i) && (2 != j))
    {
      bool = true;
    }
    else
    {
      Object localObject;
      int k;
      int m;
      if (2 == i)
      {
        localObject = paramNode1.getAttributes();
        k = ((NamedNodeMap)localObject).getLength();
        m = 0;
        int n = 0;
        for (int i1 = 0; i1 < k; i1++)
        {
          Node localNode = ((NamedNodeMap)localObject).item(i1);
          if ((paramNode2 == localNode) || (isNodeTheSame(paramNode2, localNode)))
          {
            if (n != 0)
            {
              bool = false;
              break;
            }
            m = 1;
          }
          else if ((paramNode3 == localNode) || (isNodeTheSame(paramNode3, localNode)))
          {
            if (m != 0)
            {
              bool = true;
              break;
            }
            n = 1;
          }
        }
      }
      else
      {
        localObject = paramNode1.getFirstChild();
        k = 0;
        m = 0;
        while (null != localObject)
        {
          if ((paramNode2 == localObject) || (isNodeTheSame(paramNode2, (Node)localObject)))
          {
            if (m != 0)
            {
              bool = false;
              break;
            }
            k = 1;
          }
          else if ((paramNode3 == localObject) || (isNodeTheSame(paramNode3, (Node)localObject)))
          {
            if (k != 0)
            {
              bool = true;
              break;
            }
            m = 1;
          }
          localObject = ((Node)localObject).getNextSibling();
        }
      }
    }
    return bool;
  }
  
  public short getLevel(Node paramNode)
  {
    for (short s = 1; null != (paramNode = getParentOfNode(paramNode)); s = (short)(s + 1)) {}
    return s;
  }
  
  public String getNamespaceForPrefix(String paramString, Element paramElement)
  {
    Object localObject = paramElement;
    String str1 = null;
    if (paramString.equals("xml"))
    {
      str1 = "http://www.w3.org/XML/1998/namespace";
    }
    else if (paramString.equals("xmlns"))
    {
      str1 = "http://www.w3.org/2000/xmlns/";
    }
    else
    {
      String str2 = "xmlns:" + paramString;
      int i;
      while ((null != localObject) && (null == str1) && (((i = ((Node)localObject).getNodeType()) == 1) || (i == 5)))
      {
        if (i == 1)
        {
          Attr localAttr = ((Element)localObject).getAttributeNode(str2);
          if (localAttr != null)
          {
            str1 = localAttr.getNodeValue();
            break;
          }
        }
        localObject = getParentOfNode((Node)localObject);
      }
    }
    return str1;
  }
  
  public String getNamespaceOfNode(Node paramNode)
  {
    int i = paramNode.getNodeType();
    NSInfo localNSInfo;
    boolean bool1;
    if (2 != i)
    {
      localNSInfo = (NSInfo)m_NSInfos.get(paramNode);
      bool1 = localNSInfo == null ? false : m_hasProcessedNS;
    }
    else
    {
      bool1 = false;
      localNSInfo = null;
    }
    String str1;
    if (bool1)
    {
      str1 = m_namespace;
    }
    else
    {
      str1 = null;
      String str2 = paramNode.getNodeName();
      int j = str2.indexOf(':');
      String str3;
      if (2 == i)
      {
        if (j > 0) {
          str3 = str2.substring(0, j);
        } else {
          return str1;
        }
      }
      else {
        str3 = j >= 0 ? str2.substring(0, j) : "";
      }
      int k = 0;
      boolean bool2 = false;
      if (str3.equals("xml"))
      {
        str1 = "http://www.w3.org/XML/1998/namespace";
      }
      else
      {
        Node localNode1 = paramNode;
        while ((null != localNode1) && (null == str1) && ((null == localNSInfo) || (m_ancestorHasXMLNSAttrs != 2)))
        {
          int m = localNode1.getNodeType();
          if ((null == localNSInfo) || (m_hasXMLNSAttrs))
          {
            n = 0;
            if (m == 1)
            {
              NamedNodeMap localNamedNodeMap = localNode1.getAttributes();
              for (int i2 = 0; i2 < localNamedNodeMap.getLength(); i2++)
              {
                Node localNode2 = localNamedNodeMap.item(i2);
                String str4 = localNode2.getNodeName();
                if (str4.charAt(0) == 'x')
                {
                  boolean bool3 = str4.startsWith("xmlns:");
                  if ((str4.equals("xmlns")) || (bool3))
                  {
                    if (paramNode == localNode1) {
                      bool2 = true;
                    }
                    n = 1;
                    k = 1;
                    String str5 = bool3 ? str4.substring(6) : "";
                    if (str5.equals(str3))
                    {
                      str1 = localNode2.getNodeValue();
                      break;
                    }
                  }
                }
              }
            }
            if ((2 != m) && (null == localNSInfo) && (paramNode != localNode1))
            {
              localNSInfo = n != 0 ? m_NSInfoUnProcWithXMLNS : m_NSInfoUnProcWithoutXMLNS;
              m_NSInfos.put(localNode1, localNSInfo);
            }
          }
          if (2 == m)
          {
            localNode1 = getParentOfNode(localNode1);
          }
          else
          {
            m_candidateNoAncestorXMLNS.addElement(localNode1);
            m_candidateNoAncestorXMLNS.addElement(localNSInfo);
            localNode1 = localNode1.getParentNode();
          }
          if (null != localNode1) {
            localNSInfo = (NSInfo)m_NSInfos.get(localNode1);
          }
        }
        int n = m_candidateNoAncestorXMLNS.size();
        if (n > 0)
        {
          if ((0 == k) && (null == localNode1)) {
            for (int i1 = 0; i1 < n; i1 += 2)
            {
              Object localObject = m_candidateNoAncestorXMLNS.elementAt(i1 + 1);
              if (localObject == m_NSInfoUnProcWithoutXMLNS) {
                m_NSInfos.put((Node)m_candidateNoAncestorXMLNS.elementAt(i1), m_NSInfoUnProcNoAncestorXMLNS);
              } else if (localObject == m_NSInfoNullWithoutXMLNS) {
                m_NSInfos.put((Node)m_candidateNoAncestorXMLNS.elementAt(i1), m_NSInfoNullNoAncestorXMLNS);
              }
            }
          }
          m_candidateNoAncestorXMLNS.removeAllElements();
        }
      }
      if (2 != i) {
        if (null == str1)
        {
          if (k != 0)
          {
            if (bool2) {
              m_NSInfos.put(paramNode, m_NSInfoNullWithXMLNS);
            } else {
              m_NSInfos.put(paramNode, m_NSInfoNullWithoutXMLNS);
            }
          }
          else {
            m_NSInfos.put(paramNode, m_NSInfoNullNoAncestorXMLNS);
          }
        }
        else {
          m_NSInfos.put(paramNode, new NSInfo(str1, bool2));
        }
      }
    }
    return str1;
  }
  
  public String getLocalNameOfNode(Node paramNode)
  {
    String str = paramNode.getNodeName();
    int i = str.indexOf(':');
    return i < 0 ? str : str.substring(i + 1);
  }
  
  public String getExpandedElementName(Element paramElement)
  {
    String str = getNamespaceOfNode(paramElement);
    return null != str ? str + ":" + getLocalNameOfNode(paramElement) : getLocalNameOfNode(paramElement);
  }
  
  public String getExpandedAttributeName(Attr paramAttr)
  {
    String str = getNamespaceOfNode(paramAttr);
    return null != str ? str + ":" + getLocalNameOfNode(paramAttr) : getLocalNameOfNode(paramAttr);
  }
  
  /**
   * @deprecated
   */
  public boolean isIgnorableWhitespace(Text paramText)
  {
    boolean bool = false;
    return bool;
  }
  
  /**
   * @deprecated
   */
  public Node getRoot(Node paramNode)
  {
    Node localNode = null;
    while (paramNode != null)
    {
      localNode = paramNode;
      paramNode = getParentOfNode(paramNode);
    }
    return localNode;
  }
  
  public Node getRootNode(Node paramNode)
  {
    int i = paramNode.getNodeType();
    return (9 == i) || (11 == i) ? paramNode : paramNode.getOwnerDocument();
  }
  
  public boolean isNamespaceNode(Node paramNode)
  {
    if (2 == paramNode.getNodeType())
    {
      String str = paramNode.getNodeName();
      return (str.startsWith("xmlns:")) || (str.equals("xmlns"));
    }
    return false;
  }
  
  public static Node getParentOfNode(Node paramNode)
    throws RuntimeException
  {
    int i = paramNode.getNodeType();
    Object localObject;
    if (2 == i)
    {
      Document localDocument = paramNode.getOwnerDocument();
      DOMImplementation localDOMImplementation = localDocument.getImplementation();
      if ((localDOMImplementation != null) && (localDOMImplementation.hasFeature("Core", "2.0")))
      {
        localObject = ((Attr)paramNode).getOwnerElement();
        return (Node)localObject;
      }
      Element localElement = localDocument.getDocumentElement();
      if (null == localElement) {
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT", null));
      }
      localObject = locateAttrParent(localElement, paramNode);
    }
    else
    {
      localObject = paramNode.getParentNode();
    }
    return (Node)localObject;
  }
  
  public Element getElementByID(String paramString, Document paramDocument)
  {
    return null;
  }
  
  public String getUnparsedEntityURI(String paramString, Document paramDocument)
  {
    String str1 = "";
    DocumentType localDocumentType = paramDocument.getDoctype();
    if (null != localDocumentType)
    {
      NamedNodeMap localNamedNodeMap = localDocumentType.getEntities();
      if (null == localNamedNodeMap) {
        return str1;
      }
      Entity localEntity = (Entity)localNamedNodeMap.getNamedItem(paramString);
      if (null == localEntity) {
        return str1;
      }
      String str2 = localEntity.getNotationName();
      if (null != str2)
      {
        str1 = localEntity.getSystemId();
        if (null == str1) {
          str1 = localEntity.getPublicId();
        }
      }
    }
    return str1;
  }
  
  private static Node locateAttrParent(Element paramElement, Node paramNode)
  {
    Object localObject = null;
    Attr localAttr = paramElement.getAttributeNode(paramNode.getNodeName());
    if (localAttr == paramNode) {
      localObject = paramElement;
    }
    if (null == localObject) {
      for (Node localNode = paramElement.getFirstChild(); null != localNode; localNode = localNode.getNextSibling()) {
        if (1 == localNode.getNodeType())
        {
          localObject = locateAttrParent((Element)localNode, paramNode);
          if (null != localObject) {
            break;
          }
        }
      }
    }
    return (Node)localObject;
  }
  
  public void setDOMFactory(Document paramDocument)
  {
    m_DOMFactory = paramDocument;
  }
  
  public Document getDOMFactory()
  {
    if (null == m_DOMFactory) {
      m_DOMFactory = createDocument();
    }
    return m_DOMFactory;
  }
  
  /* Error */
  public static String getNodeData(Node paramNode)
  {
    // Byte code:
    //   0: invokestatic 336	com/sun/org/apache/xml/internal/utils/StringBufferPool:get	()Lcom/sun/org/apache/xml/internal/utils/FastStringBuffer;
    //   3: astore_1
    //   4: aload_0
    //   5: aload_1
    //   6: invokestatic 323	com/sun/org/apache/xml/internal/utils/DOMHelper:getNodeData	(Lorg/w3c/dom/Node;Lcom/sun/org/apache/xml/internal/utils/FastStringBuffer;)V
    //   9: aload_1
    //   10: invokevirtual 330	com/sun/org/apache/xml/internal/utils/FastStringBuffer:length	()I
    //   13: ifle +10 -> 23
    //   16: aload_1
    //   17: invokevirtual 331	com/sun/org/apache/xml/internal/utils/FastStringBuffer:toString	()Ljava/lang/String;
    //   20: goto +5 -> 25
    //   23: ldc 1
    //   25: astore_2
    //   26: aload_1
    //   27: invokestatic 337	com/sun/org/apache/xml/internal/utils/StringBufferPool:free	(Lcom/sun/org/apache/xml/internal/utils/FastStringBuffer;)V
    //   30: goto +10 -> 40
    //   33: astore_3
    //   34: aload_1
    //   35: invokestatic 337	com/sun/org/apache/xml/internal/utils/StringBufferPool:free	(Lcom/sun/org/apache/xml/internal/utils/FastStringBuffer;)V
    //   38: aload_3
    //   39: athrow
    //   40: aload_2
    //   41: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	paramNode	Node
    //   3	32	1	localFastStringBuffer	FastStringBuffer
    //   25	16	2	str	String
    //   33	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	26	33	finally
  }
  
  public static void getNodeData(Node paramNode, FastStringBuffer paramFastStringBuffer)
  {
    switch (paramNode.getNodeType())
    {
    case 1: 
    case 9: 
    case 11: 
      for (Node localNode = paramNode.getFirstChild(); null != localNode; localNode = localNode.getNextSibling()) {
        getNodeData(localNode, paramFastStringBuffer);
      }
      break;
    case 3: 
    case 4: 
      paramFastStringBuffer.append(paramNode.getNodeValue());
      break;
    case 2: 
      paramFastStringBuffer.append(paramNode.getNodeValue());
      break;
    case 7: 
      break;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\DOMHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */