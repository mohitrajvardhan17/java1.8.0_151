package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class XMLUtils
{
  private static boolean ignoreLineBreaks = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.ignoreLineBreaks"));
    }
  })).booleanValue();
  private static volatile String dsPrefix = "ds";
  private static volatile String ds11Prefix = "dsig11";
  private static volatile String xencPrefix = "xenc";
  private static volatile String xenc11Prefix = "xenc11";
  private static final Logger log = Logger.getLogger(XMLUtils.class.getName());
  
  private XMLUtils() {}
  
  public static void setDsPrefix(String paramString)
  {
    JavaUtils.checkRegisterPermission();
    dsPrefix = paramString;
  }
  
  public static void setDs11Prefix(String paramString)
  {
    JavaUtils.checkRegisterPermission();
    ds11Prefix = paramString;
  }
  
  public static void setXencPrefix(String paramString)
  {
    JavaUtils.checkRegisterPermission();
    xencPrefix = paramString;
  }
  
  public static void setXenc11Prefix(String paramString)
  {
    JavaUtils.checkRegisterPermission();
    xenc11Prefix = paramString;
  }
  
  public static Element getNextElement(Node paramNode)
  {
    for (Node localNode = paramNode; (localNode != null) && (localNode.getNodeType() != 1); localNode = localNode.getNextSibling()) {}
    return (Element)localNode;
  }
  
  public static void getSet(Node paramNode1, Set<Node> paramSet, Node paramNode2, boolean paramBoolean)
  {
    if ((paramNode2 != null) && (isDescendantOrSelf(paramNode2, paramNode1))) {
      return;
    }
    getSetRec(paramNode1, paramSet, paramNode2, paramBoolean);
  }
  
  private static void getSetRec(Node paramNode1, Set<Node> paramSet, Node paramNode2, boolean paramBoolean)
  {
    if (paramNode1 == paramNode2) {
      return;
    }
    Object localObject;
    switch (paramNode1.getNodeType())
    {
    case 1: 
      paramSet.add(paramNode1);
      Element localElement = (Element)paramNode1;
      if (localElement.hasAttributes())
      {
        localObject = localElement.getAttributes();
        for (int i = 0; i < ((NamedNodeMap)localObject).getLength(); i++) {
          paramSet.add(((NamedNodeMap)localObject).item(i));
        }
      }
    case 9: 
      for (localObject = paramNode1.getFirstChild(); localObject != null; localObject = ((Node)localObject).getNextSibling())
      {
        if (((Node)localObject).getNodeType() == 3)
        {
          paramSet.add(localObject);
          while ((localObject != null) && (((Node)localObject).getNodeType() == 3)) {
            localObject = ((Node)localObject).getNextSibling();
          }
          if (localObject == null) {
            return;
          }
        }
        getSetRec((Node)localObject, paramSet, paramNode2, paramBoolean);
      }
      return;
    case 8: 
      if (paramBoolean) {
        paramSet.add(paramNode1);
      }
      return;
    case 10: 
      return;
    }
    paramSet.add(paramNode1);
  }
  
  public static void outputDOM(Node paramNode, OutputStream paramOutputStream)
  {
    outputDOM(paramNode, paramOutputStream, false);
  }
  
  public static void outputDOM(Node paramNode, OutputStream paramOutputStream, boolean paramBoolean)
  {
    try
    {
      if (paramBoolean) {
        paramOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
      }
      paramOutputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(paramNode));
    }
    catch (IOException localIOException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException.getMessage(), localIOException);
      }
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localInvalidCanonicalizerException.getMessage(), localInvalidCanonicalizerException);
      }
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localCanonicalizationException.getMessage(), localCanonicalizationException);
      }
    }
  }
  
  public static void outputDOMc14nWithComments(Node paramNode, OutputStream paramOutputStream)
  {
    try
    {
      paramOutputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(paramNode));
    }
    catch (IOException localIOException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException.getMessage(), localIOException);
      }
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localInvalidCanonicalizerException.getMessage(), localInvalidCanonicalizerException);
      }
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localCanonicalizationException.getMessage(), localCanonicalizationException);
      }
    }
  }
  
  public static String getFullTextChildrenFromElement(Element paramElement)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (Node localNode = paramElement.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if (localNode.getNodeType() == 3) {
        localStringBuilder.append(((Text)localNode).getData());
      }
    }
    return localStringBuilder.toString();
  }
  
  public static Element createElementInSignatureSpace(Document paramDocument, String paramString)
  {
    if (paramDocument == null) {
      throw new RuntimeException("Document is null");
    }
    if ((dsPrefix == null) || (dsPrefix.length() == 0)) {
      return paramDocument.createElementNS("http://www.w3.org/2000/09/xmldsig#", paramString);
    }
    return paramDocument.createElementNS("http://www.w3.org/2000/09/xmldsig#", dsPrefix + ":" + paramString);
  }
  
  public static Element createElementInSignature11Space(Document paramDocument, String paramString)
  {
    if (paramDocument == null) {
      throw new RuntimeException("Document is null");
    }
    if ((ds11Prefix == null) || (ds11Prefix.length() == 0)) {
      return paramDocument.createElementNS("http://www.w3.org/2009/xmldsig11#", paramString);
    }
    return paramDocument.createElementNS("http://www.w3.org/2009/xmldsig11#", ds11Prefix + ":" + paramString);
  }
  
  public static Element createElementInEncryptionSpace(Document paramDocument, String paramString)
  {
    if (paramDocument == null) {
      throw new RuntimeException("Document is null");
    }
    if ((xencPrefix == null) || (xencPrefix.length() == 0)) {
      return paramDocument.createElementNS("http://www.w3.org/2001/04/xmlenc#", paramString);
    }
    return paramDocument.createElementNS("http://www.w3.org/2001/04/xmlenc#", xencPrefix + ":" + paramString);
  }
  
  public static Element createElementInEncryption11Space(Document paramDocument, String paramString)
  {
    if (paramDocument == null) {
      throw new RuntimeException("Document is null");
    }
    if ((xenc11Prefix == null) || (xenc11Prefix.length() == 0)) {
      return paramDocument.createElementNS("http://www.w3.org/2009/xmlenc11#", paramString);
    }
    return paramDocument.createElementNS("http://www.w3.org/2009/xmlenc11#", xenc11Prefix + ":" + paramString);
  }
  
  public static boolean elementIsInSignatureSpace(Element paramElement, String paramString)
  {
    if (paramElement == null) {
      return false;
    }
    return ("http://www.w3.org/2000/09/xmldsig#".equals(paramElement.getNamespaceURI())) && (paramElement.getLocalName().equals(paramString));
  }
  
  public static boolean elementIsInSignature11Space(Element paramElement, String paramString)
  {
    if (paramElement == null) {
      return false;
    }
    return ("http://www.w3.org/2009/xmldsig11#".equals(paramElement.getNamespaceURI())) && (paramElement.getLocalName().equals(paramString));
  }
  
  public static boolean elementIsInEncryptionSpace(Element paramElement, String paramString)
  {
    if (paramElement == null) {
      return false;
    }
    return ("http://www.w3.org/2001/04/xmlenc#".equals(paramElement.getNamespaceURI())) && (paramElement.getLocalName().equals(paramString));
  }
  
  public static boolean elementIsInEncryption11Space(Element paramElement, String paramString)
  {
    if (paramElement == null) {
      return false;
    }
    return ("http://www.w3.org/2009/xmlenc11#".equals(paramElement.getNamespaceURI())) && (paramElement.getLocalName().equals(paramString));
  }
  
  public static Document getOwnerDocument(Node paramNode)
  {
    if (paramNode.getNodeType() == 9) {
      return (Document)paramNode;
    }
    try
    {
      return paramNode.getOwnerDocument();
    }
    catch (NullPointerException localNullPointerException)
    {
      throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + localNullPointerException.getMessage() + "\"");
    }
  }
  
  public static Document getOwnerDocument(Set<Node> paramSet)
  {
    Object localObject = null;
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      Node localNode = (Node)localIterator.next();
      int i = localNode.getNodeType();
      if (i == 9) {
        return (Document)localNode;
      }
      try
      {
        if (i == 2) {
          return ((Attr)localNode).getOwnerElement().getOwnerDocument();
        }
        return localNode.getOwnerDocument();
      }
      catch (NullPointerException localNullPointerException)
      {
        localObject = localNullPointerException;
      }
    }
    throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + (localObject == null ? "" : ((NullPointerException)localObject).getMessage()) + "\"");
  }
  
  public static Element createDSctx(Document paramDocument, String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.trim().length() == 0)) {
      throw new IllegalArgumentException("You must supply a prefix");
    }
    Element localElement = paramDocument.createElementNS(null, "namespaceContext");
    localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + paramString1.trim(), paramString2);
    return localElement;
  }
  
  public static void addReturnToElement(Element paramElement)
  {
    if (!ignoreLineBreaks)
    {
      Document localDocument = paramElement.getOwnerDocument();
      paramElement.appendChild(localDocument.createTextNode("\n"));
    }
  }
  
  public static void addReturnToElement(Document paramDocument, HelperNodeList paramHelperNodeList)
  {
    if (!ignoreLineBreaks) {
      paramHelperNodeList.appendChild(paramDocument.createTextNode("\n"));
    }
  }
  
  public static void addReturnBeforeChild(Element paramElement, Node paramNode)
  {
    if (!ignoreLineBreaks)
    {
      Document localDocument = paramElement.getOwnerDocument();
      paramElement.insertBefore(localDocument.createTextNode("\n"), paramNode);
    }
  }
  
  public static Set<Node> convertNodelistToSet(NodeList paramNodeList)
  {
    if (paramNodeList == null) {
      return new HashSet();
    }
    int i = paramNodeList.getLength();
    HashSet localHashSet = new HashSet(i);
    for (int j = 0; j < i; j++) {
      localHashSet.add(paramNodeList.item(j));
    }
    return localHashSet;
  }
  
  public static void circumventBug2650(Document paramDocument)
  {
    Element localElement = paramDocument.getDocumentElement();
    Attr localAttr = localElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
    if (localAttr == null) {
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
    }
    circumventBug2650internal(paramDocument);
  }
  
  private static void circumventBug2650internal(Node paramNode)
  {
    Node localNode1 = null;
    for (Node localNode2 = null;; localNode2 = paramNode.getNextSibling())
    {
      switch (paramNode.getNodeType())
      {
      case 1: 
        Element localElement1 = (Element)paramNode;
        if (localElement1.hasChildNodes()) {
          if (localElement1.hasAttributes())
          {
            NamedNodeMap localNamedNodeMap = localElement1.getAttributes();
            int i = localNamedNodeMap.getLength();
            for (Node localNode3 = localElement1.getFirstChild(); localNode3 != null; localNode3 = localNode3.getNextSibling()) {
              if (localNode3.getNodeType() == 1)
              {
                Element localElement2 = (Element)localNode3;
                for (int j = 0; j < i; j++)
                {
                  Attr localAttr = (Attr)localNamedNodeMap.item(j);
                  if (("http://www.w3.org/2000/xmlns/".equals(localAttr.getNamespaceURI())) && (!localElement2.hasAttributeNS("http://www.w3.org/2000/xmlns/", localAttr.getLocalName()))) {
                    localElement2.setAttributeNS("http://www.w3.org/2000/xmlns/", localAttr.getName(), localAttr.getNodeValue());
                  }
                }
              }
            }
          }
        }
        break;
      case 5: 
      case 9: 
        localNode1 = paramNode;
        localNode2 = paramNode.getFirstChild();
      }
      while ((localNode2 == null) && (localNode1 != null))
      {
        localNode2 = localNode1.getNextSibling();
        localNode1 = localNode1.getParentNode();
      }
      if (localNode2 == null) {
        return;
      }
      paramNode = localNode2;
    }
  }
  
  public static Element selectDsNode(Node paramNode, String paramString, int paramInt)
  {
    while (paramNode != null)
    {
      if (("http://www.w3.org/2000/09/xmldsig#".equals(paramNode.getNamespaceURI())) && (paramNode.getLocalName().equals(paramString)))
      {
        if (paramInt == 0) {
          return (Element)paramNode;
        }
        paramInt--;
      }
      paramNode = paramNode.getNextSibling();
    }
    return null;
  }
  
  public static Element selectDs11Node(Node paramNode, String paramString, int paramInt)
  {
    while (paramNode != null)
    {
      if (("http://www.w3.org/2009/xmldsig11#".equals(paramNode.getNamespaceURI())) && (paramNode.getLocalName().equals(paramString)))
      {
        if (paramInt == 0) {
          return (Element)paramNode;
        }
        paramInt--;
      }
      paramNode = paramNode.getNextSibling();
    }
    return null;
  }
  
  public static Element selectXencNode(Node paramNode, String paramString, int paramInt)
  {
    while (paramNode != null)
    {
      if (("http://www.w3.org/2001/04/xmlenc#".equals(paramNode.getNamespaceURI())) && (paramNode.getLocalName().equals(paramString)))
      {
        if (paramInt == 0) {
          return (Element)paramNode;
        }
        paramInt--;
      }
      paramNode = paramNode.getNextSibling();
    }
    return null;
  }
  
  public static Text selectDsNodeText(Node paramNode, String paramString, int paramInt)
  {
    Object localObject = selectDsNode(paramNode, paramString, paramInt);
    if (localObject == null) {
      return null;
    }
    for (localObject = ((Node)localObject).getFirstChild(); (localObject != null) && (((Node)localObject).getNodeType() != 3); localObject = ((Node)localObject).getNextSibling()) {}
    return (Text)localObject;
  }
  
  public static Text selectDs11NodeText(Node paramNode, String paramString, int paramInt)
  {
    Object localObject = selectDs11Node(paramNode, paramString, paramInt);
    if (localObject == null) {
      return null;
    }
    for (localObject = ((Node)localObject).getFirstChild(); (localObject != null) && (((Node)localObject).getNodeType() != 3); localObject = ((Node)localObject).getNextSibling()) {}
    return (Text)localObject;
  }
  
  public static Text selectNodeText(Node paramNode, String paramString1, String paramString2, int paramInt)
  {
    Object localObject = selectNode(paramNode, paramString1, paramString2, paramInt);
    if (localObject == null) {
      return null;
    }
    for (localObject = ((Node)localObject).getFirstChild(); (localObject != null) && (((Node)localObject).getNodeType() != 3); localObject = ((Node)localObject).getNextSibling()) {}
    return (Text)localObject;
  }
  
  public static Element selectNode(Node paramNode, String paramString1, String paramString2, int paramInt)
  {
    while (paramNode != null)
    {
      if ((paramNode.getNamespaceURI() != null) && (paramNode.getNamespaceURI().equals(paramString1)) && (paramNode.getLocalName().equals(paramString2)))
      {
        if (paramInt == 0) {
          return (Element)paramNode;
        }
        paramInt--;
      }
      paramNode = paramNode.getNextSibling();
    }
    return null;
  }
  
  public static Element[] selectDsNodes(Node paramNode, String paramString)
  {
    return selectNodes(paramNode, "http://www.w3.org/2000/09/xmldsig#", paramString);
  }
  
  public static Element[] selectDs11Nodes(Node paramNode, String paramString)
  {
    return selectNodes(paramNode, "http://www.w3.org/2009/xmldsig11#", paramString);
  }
  
  public static Element[] selectNodes(Node paramNode, String paramString1, String paramString2)
  {
    ArrayList localArrayList = new ArrayList();
    while (paramNode != null)
    {
      if ((paramNode.getNamespaceURI() != null) && (paramNode.getNamespaceURI().equals(paramString1)) && (paramNode.getLocalName().equals(paramString2))) {
        localArrayList.add((Element)paramNode);
      }
      paramNode = paramNode.getNextSibling();
    }
    return (Element[])localArrayList.toArray(new Element[localArrayList.size()]);
  }
  
  public static Set<Node> excludeNodeFromSet(Node paramNode, Set<Node> paramSet)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      Node localNode = (Node)localIterator.next();
      if (!isDescendantOrSelf(paramNode, localNode)) {
        localHashSet.add(localNode);
      }
    }
    return localHashSet;
  }
  
  public static String getStrFromNode(Node paramNode)
  {
    if (paramNode.getNodeType() == 3)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      for (Node localNode = paramNode.getParentNode().getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
        if (localNode.getNodeType() == 3) {
          localStringBuilder.append(((Text)localNode).getData());
        }
      }
      return localStringBuilder.toString();
    }
    if (paramNode.getNodeType() == 2) {
      return ((Attr)paramNode).getNodeValue();
    }
    if (paramNode.getNodeType() == 7) {
      return ((ProcessingInstruction)paramNode).getNodeValue();
    }
    return null;
  }
  
  public static boolean isDescendantOrSelf(Node paramNode1, Node paramNode2)
  {
    if (paramNode1 == paramNode2) {
      return true;
    }
    Object localObject = paramNode2;
    for (;;)
    {
      if (localObject == null) {
        return false;
      }
      if (localObject == paramNode1) {
        return true;
      }
      if (((Node)localObject).getNodeType() == 2) {
        localObject = ((Attr)localObject).getOwnerElement();
      } else {
        localObject = ((Node)localObject).getParentNode();
      }
    }
  }
  
  public static boolean ignoreLineBreaks()
  {
    return ignoreLineBreaks;
  }
  
  public static String getAttributeValue(Element paramElement, String paramString)
  {
    Attr localAttr = paramElement.getAttributeNodeNS(null, paramString);
    return localAttr == null ? null : localAttr.getValue();
  }
  
  public static boolean protectAgainstWrappingAttack(Node paramNode, String paramString)
  {
    Node localNode1 = paramNode.getParentNode();
    Node localNode2 = null;
    Element localElement1 = null;
    String str = paramString.trim();
    if ((!str.isEmpty()) && (str.charAt(0) == '#')) {
      str = str.substring(1);
    }
    while (paramNode != null)
    {
      if (paramNode.getNodeType() == 1)
      {
        Element localElement2 = (Element)paramNode;
        NamedNodeMap localNamedNodeMap = localElement2.getAttributes();
        if (localNamedNodeMap != null) {
          for (int i = 0; i < localNamedNodeMap.getLength(); i++)
          {
            Attr localAttr = (Attr)localNamedNodeMap.item(i);
            if ((localAttr.isId()) && (str.equals(localAttr.getValue()))) {
              if (localElement1 == null)
              {
                localElement1 = localAttr.getOwnerElement();
              }
              else
              {
                log.log(Level.FINE, "Multiple elements with the same 'Id' attribute value!");
                return false;
              }
            }
          }
        }
      }
      localNode2 = paramNode;
      paramNode = paramNode.getFirstChild();
      if (paramNode == null) {}
      for (paramNode = localNode2.getNextSibling(); paramNode == null; paramNode = localNode2.getNextSibling())
      {
        localNode2 = localNode2.getParentNode();
        if (localNode2 == localNode1) {
          return true;
        }
      }
    }
    return true;
  }
  
  public static boolean protectAgainstWrappingAttack(Node paramNode, Element paramElement, String paramString)
  {
    Node localNode1 = paramNode.getParentNode();
    Node localNode2 = null;
    String str = paramString.trim();
    if ((!str.isEmpty()) && (str.charAt(0) == '#')) {
      str = str.substring(1);
    }
    while (paramNode != null)
    {
      if (paramNode.getNodeType() == 1)
      {
        Element localElement = (Element)paramNode;
        NamedNodeMap localNamedNodeMap = localElement.getAttributes();
        if (localNamedNodeMap != null) {
          for (int i = 0; i < localNamedNodeMap.getLength(); i++)
          {
            Attr localAttr = (Attr)localNamedNodeMap.item(i);
            if ((localAttr.isId()) && (str.equals(localAttr.getValue())) && (localElement != paramElement))
            {
              log.log(Level.FINE, "Multiple elements with the same 'Id' attribute value!");
              return false;
            }
          }
        }
      }
      localNode2 = paramNode;
      paramNode = paramNode.getFirstChild();
      if (paramNode == null) {}
      for (paramNode = localNode2.getNextSibling(); paramNode == null; paramNode = localNode2.getNextSibling())
      {
        localNode2 = localNode2.getParentNode();
        if (localNode2 == localNode1) {
          return true;
        }
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\XMLUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */