package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DOMStreamReader
  implements XMLStreamReader, NamespaceContext
{
  protected Node _current;
  private Node _start;
  private NamedNodeMap _namedNodeMap;
  protected String wholeText;
  private final FinalArrayList<Attr> _currentAttributes = new FinalArrayList();
  protected Scope[] scopes = new Scope[8];
  protected int depth = 0;
  protected int _state;
  
  public DOMStreamReader() {}
  
  public DOMStreamReader(Node paramNode)
  {
    setCurrentNode(paramNode);
  }
  
  public void setCurrentNode(Node paramNode)
  {
    scopes[0] = new Scope(null);
    depth = 0;
    _start = (_current = paramNode);
    _state = 7;
  }
  
  public void close()
    throws XMLStreamException
  {}
  
  protected void splitAttributes()
  {
    _currentAttributes.clear();
    Scope localScope = allocateScope();
    _namedNodeMap = _current.getAttributes();
    if (_namedNodeMap != null)
    {
      i = _namedNodeMap.getLength();
      for (int j = 0; j < i; j++)
      {
        Attr localAttr2 = (Attr)_namedNodeMap.item(j);
        String str = localAttr2.getNodeName();
        if ((str.startsWith("xmlns:")) || (str.equals("xmlns"))) {
          currentNamespaces.add(localAttr2);
        } else {
          _currentAttributes.add(localAttr2);
        }
      }
    }
    ensureNs(_current);
    for (int i = _currentAttributes.size() - 1; i >= 0; i--)
    {
      Attr localAttr1 = (Attr)_currentAttributes.get(i);
      if (fixNull(localAttr1.getNamespaceURI()).length() > 0) {
        ensureNs(localAttr1);
      }
    }
  }
  
  private void ensureNs(Node paramNode)
  {
    String str1 = fixNull(paramNode.getPrefix());
    String str2 = fixNull(paramNode.getNamespaceURI());
    Scope localScope = scopes[depth];
    String str3 = localScope.getNamespaceURI(str1);
    if (str1.length() == 0)
    {
      str3 = fixNull(str3);
      if (!str3.equals(str2)) {}
    }
    else if ((str3 != null) && (str3.equals(str2)))
    {
      return;
    }
    if ((str1.equals("xml")) || (str1.equals("xmlns"))) {
      return;
    }
    additionalNamespaces.add(str1);
    additionalNamespaces.add(str2);
  }
  
  private Scope allocateScope()
  {
    if (scopes.length == ++depth)
    {
      localObject = new Scope[scopes.length * 2];
      System.arraycopy(scopes, 0, localObject, 0, scopes.length);
      scopes = ((Scope[])localObject);
    }
    Object localObject = scopes[depth];
    if (localObject == null) {
      localObject = scopes[depth] = new Scope(scopes[(depth - 1)]);
    } else {
      ((Scope)localObject).reset();
    }
    return (Scope)localObject;
  }
  
  public int getAttributeCount()
  {
    if (_state == 1) {
      return _currentAttributes.size();
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeCount() called in illegal state");
  }
  
  public String getAttributeLocalName(int paramInt)
  {
    if (_state == 1)
    {
      String str = ((Attr)_currentAttributes.get(paramInt)).getLocalName();
      return str != null ? str : QName.valueOf(((Attr)_currentAttributes.get(paramInt)).getNodeName()).getLocalPart();
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeLocalName() called in illegal state");
  }
  
  public QName getAttributeName(int paramInt)
  {
    if (_state == 1)
    {
      Node localNode = (Node)_currentAttributes.get(paramInt);
      String str1 = localNode.getLocalName();
      if (str1 != null)
      {
        String str2 = localNode.getPrefix();
        String str3 = localNode.getNamespaceURI();
        return new QName(fixNull(str3), str1, fixNull(str2));
      }
      return QName.valueOf(localNode.getNodeName());
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeName() called in illegal state");
  }
  
  public String getAttributeNamespace(int paramInt)
  {
    if (_state == 1)
    {
      String str = ((Attr)_currentAttributes.get(paramInt)).getNamespaceURI();
      return fixNull(str);
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeNamespace() called in illegal state");
  }
  
  public String getAttributePrefix(int paramInt)
  {
    if (_state == 1)
    {
      String str = ((Attr)_currentAttributes.get(paramInt)).getPrefix();
      return fixNull(str);
    }
    throw new IllegalStateException("DOMStreamReader: getAttributePrefix() called in illegal state");
  }
  
  public String getAttributeType(int paramInt)
  {
    if (_state == 1) {
      return "CDATA";
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeType() called in illegal state");
  }
  
  public String getAttributeValue(int paramInt)
  {
    if (_state == 1) {
      return ((Attr)_currentAttributes.get(paramInt)).getNodeValue();
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
  }
  
  public String getAttributeValue(String paramString1, String paramString2)
  {
    if (_state == 1)
    {
      if (_namedNodeMap != null)
      {
        Node localNode = _namedNodeMap.getNamedItemNS(paramString1, paramString2);
        return localNode != null ? localNode.getNodeValue() : null;
      }
      return null;
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
  }
  
  public String getCharacterEncodingScheme()
  {
    return null;
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    throw new RuntimeException("DOMStreamReader: getElementText() not implemented");
  }
  
  public String getEncoding()
  {
    return null;
  }
  
  public int getEventType()
  {
    return _state;
  }
  
  public String getLocalName()
  {
    if ((_state == 1) || (_state == 2))
    {
      String str = _current.getLocalName();
      return str != null ? str : QName.valueOf(_current.getNodeName()).getLocalPart();
    }
    if (_state == 9) {
      return _current.getNodeName();
    }
    throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
  }
  
  public Location getLocation()
  {
    return DummyLocation.INSTANCE;
  }
  
  public QName getName()
  {
    if ((_state == 1) || (_state == 2))
    {
      String str1 = _current.getLocalName();
      if (str1 != null)
      {
        String str2 = _current.getPrefix();
        String str3 = _current.getNamespaceURI();
        return new QName(fixNull(str3), str1, fixNull(str2));
      }
      return QName.valueOf(_current.getNodeName());
    }
    throw new IllegalStateException("DOMStreamReader: getName() called in illegal state");
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return this;
  }
  
  private Scope getCheckedScope()
  {
    if ((_state == 1) || (_state == 2)) {
      return scopes[depth];
    }
    throw new IllegalStateException("DOMStreamReader: neither on START_ELEMENT nor END_ELEMENT");
  }
  
  public int getNamespaceCount()
  {
    return getCheckedScope().getNamespaceCount();
  }
  
  public String getNamespacePrefix(int paramInt)
  {
    return getCheckedScope().getNamespacePrefix(paramInt);
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return getCheckedScope().getNamespaceURI(paramInt);
  }
  
  public String getNamespaceURI()
  {
    if ((_state == 1) || (_state == 2))
    {
      String str = _current.getNamespaceURI();
      return fixNull(str);
    }
    return null;
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("DOMStreamReader: getNamespaceURI(String) call with a null prefix");
    }
    if (paramString.equals("xml")) {
      return "http://www.w3.org/XML/1998/namespace";
    }
    if (paramString.equals("xmlns")) {
      return "http://www.w3.org/2000/xmlns/";
    }
    String str1 = scopes[depth].getNamespaceURI(paramString);
    if (str1 != null) {
      return str1;
    }
    Node localNode = findRootElement();
    String str2 = "xmlns:" + paramString;
    while (localNode.getNodeType() != 9)
    {
      NamedNodeMap localNamedNodeMap = localNode.getAttributes();
      Attr localAttr = (Attr)localNamedNodeMap.getNamedItem(str2);
      if (localAttr != null) {
        return localAttr.getValue();
      }
      localNode = localNode.getParentNode();
    }
    return null;
  }
  
  public String getPrefix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("DOMStreamReader: getPrefix(String) call with a null namespace URI");
    }
    if (paramString.equals("http://www.w3.org/XML/1998/namespace")) {
      return "xml";
    }
    if (paramString.equals("http://www.w3.org/2000/xmlns/")) {
      return "xmlns";
    }
    String str = scopes[depth].getPrefix(paramString);
    if (str != null) {
      return str;
    }
    for (Node localNode = findRootElement(); localNode.getNodeType() != 9; localNode = localNode.getParentNode())
    {
      NamedNodeMap localNamedNodeMap = localNode.getAttributes();
      for (int i = localNamedNodeMap.getLength() - 1; i >= 0; i--)
      {
        Attr localAttr = (Attr)localNamedNodeMap.item(i);
        str = getPrefixForAttr(localAttr, paramString);
        if (str != null) {
          return str;
        }
      }
    }
    return null;
  }
  
  private Node findRootElement()
  {
    int i;
    for (Node localNode = _start; ((i = localNode.getNodeType()) != 9) && (i != 1); localNode = localNode.getParentNode()) {}
    return localNode;
  }
  
  private static String getPrefixForAttr(Attr paramAttr, String paramString)
  {
    String str1 = paramAttr.getNodeName();
    if ((!str1.startsWith("xmlns:")) && (!str1.equals("xmlns"))) {
      return null;
    }
    if (paramAttr.getValue().equals(paramString))
    {
      if (str1.equals("xmlns")) {
        return "";
      }
      String str2 = paramAttr.getLocalName();
      return str2 != null ? str2 : QName.valueOf(str1).getLocalPart();
    }
    return null;
  }
  
  public Iterator getPrefixes(String paramString)
  {
    String str = getPrefix(paramString);
    if (str == null) {
      return Collections.emptyList().iterator();
    }
    return Collections.singletonList(str).iterator();
  }
  
  public String getPIData()
  {
    if (_state == 3) {
      return ((ProcessingInstruction)_current).getData();
    }
    return null;
  }
  
  public String getPITarget()
  {
    if (_state == 3) {
      return ((ProcessingInstruction)_current).getTarget();
    }
    return null;
  }
  
  public String getPrefix()
  {
    if ((_state == 1) || (_state == 2))
    {
      String str = _current.getPrefix();
      return fixNull(str);
    }
    return null;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return null;
  }
  
  public String getText()
  {
    if (_state == 4) {
      return wholeText;
    }
    if ((_state == 12) || (_state == 5) || (_state == 9)) {
      return _current.getNodeValue();
    }
    throw new IllegalStateException("DOMStreamReader: getTextLength() called in illegal state");
  }
  
  public char[] getTextCharacters()
  {
    return getText().toCharArray();
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    String str = getText();
    int i = Math.min(paramInt3, str.length() - paramInt1);
    str.getChars(paramInt1, paramInt1 + i, paramArrayOfChar, paramInt2);
    return i;
  }
  
  public int getTextLength()
  {
    return getText().length();
  }
  
  public int getTextStart()
  {
    if ((_state == 4) || (_state == 12) || (_state == 5) || (_state == 9)) {
      return 0;
    }
    throw new IllegalStateException("DOMStreamReader: getTextStart() called in illegal state");
  }
  
  public String getVersion()
  {
    return null;
  }
  
  public boolean hasName()
  {
    return (_state == 1) || (_state == 2);
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    return _state != 8;
  }
  
  public boolean hasText()
  {
    if ((_state == 4) || (_state == 12) || (_state == 5) || (_state == 9)) {
      return getText().trim().length() > 0;
    }
    return false;
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    return false;
  }
  
  public boolean isCharacters()
  {
    return _state == 4;
  }
  
  public boolean isEndElement()
  {
    return _state == 2;
  }
  
  public boolean isStandalone()
  {
    return true;
  }
  
  public boolean isStartElement()
  {
    return _state == 1;
  }
  
  public boolean isWhiteSpace()
  {
    if ((_state == 4) || (_state == 12)) {
      return getText().trim().length() == 0;
    }
    return false;
  }
  
  private static int mapNodeTypeToState(int paramInt)
  {
    switch (paramInt)
    {
    case 4: 
      return 12;
    case 8: 
      return 5;
    case 1: 
      return 1;
    case 6: 
      return 15;
    case 5: 
      return 9;
    case 12: 
      return 14;
    case 7: 
      return 3;
    case 3: 
      return 4;
    }
    throw new RuntimeException("DOMStreamReader: Unexpected node type");
  }
  
  public int next()
    throws XMLStreamException
  {
    int i;
    do
    {
      Node localNode;
      do
      {
        i = _next();
        switch (i)
        {
        case 4: 
          localNode = _current.getPreviousSibling();
        }
      } while ((localNode != null) && (localNode.getNodeType() == 3));
      Text localText = (Text)_current;
      wholeText = localText.getWholeText();
    } while (wholeText.length() == 0);
    return 4;
    splitAttributes();
    return 1;
    return i;
  }
  
  protected int _next()
    throws XMLStreamException
  {
    Node localNode1;
    switch (_state)
    {
    case 8: 
      throw new IllegalStateException("DOMStreamReader: Calling next() at END_DOCUMENT");
    case 7: 
      if (_current.getNodeType() == 1) {
        return _state = 1;
      }
      localNode1 = _current.getFirstChild();
      if (localNode1 == null) {
        return _state = 8;
      }
      _current = localNode1;
      return _state = mapNodeTypeToState(_current.getNodeType());
    case 1: 
      localNode1 = _current.getFirstChild();
      if (localNode1 == null) {
        return _state = 2;
      }
      _current = localNode1;
      return _state = mapNodeTypeToState(_current.getNodeType());
    case 2: 
      depth -= 1;
    case 3: 
    case 4: 
    case 5: 
    case 9: 
    case 12: 
      if (_current == _start) {
        return _state = 8;
      }
      Node localNode2 = _current.getNextSibling();
      if (localNode2 == null)
      {
        _current = _current.getParentNode();
        _state = ((_current == null) || (_current.getNodeType() == 9) ? 8 : 2);
        return _state;
      }
      _current = localNode2;
      return _state = mapNodeTypeToState(_current.getNodeType());
    }
    throw new RuntimeException("DOMStreamReader: Unexpected internal state");
  }
  
  public int nextTag()
    throws XMLStreamException
  {
    for (int i = next(); ((i == 4) && (isWhiteSpace())) || ((i == 12) && (isWhiteSpace())) || (i == 6) || (i == 3) || (i == 5); i = next()) {}
    if ((i != 1) && (i != 2)) {
      throw new XMLStreamException2("DOMStreamReader: Expected start or end tag");
    }
    return i;
  }
  
  public void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramInt != _state) {
      throw new XMLStreamException2("DOMStreamReader: Required event type not found");
    }
    if ((paramString1 != null) && (!paramString1.equals(getNamespaceURI()))) {
      throw new XMLStreamException2("DOMStreamReader: Required namespaceURI not found");
    }
    if ((paramString2 != null) && (!paramString2.equals(getLocalName()))) {
      throw new XMLStreamException2("DOMStreamReader: Required localName not found");
    }
  }
  
  public boolean standaloneSet()
  {
    return true;
  }
  
  private static void displayDOM(Node paramNode, OutputStream paramOutputStream)
  {
    try
    {
      System.out.println("\n====\n");
      XmlUtil.newTransformer().transform(new DOMSource(paramNode), new StreamResult(paramOutputStream));
      System.out.println("\n====\n");
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private static void verifyDOMIntegrity(Node paramNode)
  {
    switch (paramNode.getNodeType())
    {
    case 1: 
    case 2: 
      if (paramNode.getLocalName() == null)
      {
        System.out.println("WARNING: DOM level 1 node found");
        System.out.println(" -> node.getNodeName() = " + paramNode.getNodeName());
        System.out.println(" -> node.getNamespaceURI() = " + paramNode.getNamespaceURI());
        System.out.println(" -> node.getLocalName() = " + paramNode.getLocalName());
        System.out.println(" -> node.getPrefix() = " + paramNode.getPrefix());
      }
      if (paramNode.getNodeType() == 2) {
        return;
      }
      NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
      for (int i = 0; i < localNamedNodeMap.getLength(); i++) {
        verifyDOMIntegrity(localNamedNodeMap.item(i));
      }
    case 9: 
      NodeList localNodeList = paramNode.getChildNodes();
      for (int j = 0; j < localNodeList.getLength(); j++) {
        verifyDOMIntegrity(localNodeList.item(j));
      }
    }
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  protected static final class Scope
  {
    final Scope parent;
    final FinalArrayList<Attr> currentNamespaces = new FinalArrayList();
    final FinalArrayList<String> additionalNamespaces = new FinalArrayList();
    
    Scope(Scope paramScope)
    {
      parent = paramScope;
    }
    
    void reset()
    {
      currentNamespaces.clear();
      additionalNamespaces.clear();
    }
    
    int getNamespaceCount()
    {
      return currentNamespaces.size() + additionalNamespaces.size() / 2;
    }
    
    String getNamespacePrefix(int paramInt)
    {
      int i = currentNamespaces.size();
      if (paramInt < i)
      {
        Attr localAttr = (Attr)currentNamespaces.get(paramInt);
        String str = localAttr.getLocalName();
        if (str == null) {
          str = QName.valueOf(localAttr.getNodeName()).getLocalPart();
        }
        return str.equals("xmlns") ? null : str;
      }
      return (String)additionalNamespaces.get((paramInt - i) * 2);
    }
    
    String getNamespaceURI(int paramInt)
    {
      int i = currentNamespaces.size();
      if (paramInt < i) {
        return ((Attr)currentNamespaces.get(paramInt)).getValue();
      }
      return (String)additionalNamespaces.get((paramInt - i) * 2 + 1);
    }
    
    String getPrefix(String paramString)
    {
      for (Scope localScope = this; localScope != null; localScope = parent)
      {
        for (int i = currentNamespaces.size() - 1; i >= 0; i--)
        {
          String str = DOMStreamReader.getPrefixForAttr((Attr)currentNamespaces.get(i), paramString);
          if (str != null) {
            return str;
          }
        }
        for (i = additionalNamespaces.size() - 2; i >= 0; i -= 2) {
          if (((String)additionalNamespaces.get(i + 1)).equals(paramString)) {
            return (String)additionalNamespaces.get(i);
          }
        }
      }
      return null;
    }
    
    String getNamespaceURI(@NotNull String paramString)
    {
      String str = "xmlns:" + paramString;
      for (Scope localScope = this; localScope != null; localScope = parent)
      {
        for (int i = currentNamespaces.size() - 1; i >= 0; i--)
        {
          Attr localAttr = (Attr)currentNamespaces.get(i);
          if (localAttr.getNodeName().equals(str)) {
            return localAttr.getValue();
          }
        }
        for (i = additionalNamespaces.size() - 2; i >= 0; i -= 2) {
          if (((String)additionalNamespaces.get(i)).equals(paramString)) {
            return (String)additionalNamespaces.get(i + 1);
          }
        }
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\streaming\DOMStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */