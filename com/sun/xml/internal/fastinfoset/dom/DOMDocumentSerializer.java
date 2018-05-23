package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap.Entry;
import com.sun.xml.internal.fastinfoset.util.NamespaceContextImplementation;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMDocumentSerializer
  extends Encoder
{
  protected NamespaceContextImplementation _namespaceScopeContext = new NamespaceContextImplementation();
  protected Node[] _attributes = new Node[32];
  
  public DOMDocumentSerializer() {}
  
  public final void serialize(Node paramNode)
    throws IOException
  {
    switch (paramNode.getNodeType())
    {
    case 9: 
      serialize((Document)paramNode);
      break;
    case 1: 
      serializeElementAsDocument(paramNode);
      break;
    case 8: 
      serializeComment(paramNode);
      break;
    case 7: 
      serializeProcessingInstruction(paramNode);
    }
  }
  
  public final void serialize(Document paramDocument)
    throws IOException
  {
    reset();
    encodeHeader(false);
    encodeInitialVocabulary();
    NodeList localNodeList = paramDocument.getChildNodes();
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode = localNodeList.item(i);
      switch (localNode.getNodeType())
      {
      case 1: 
        serializeElement(localNode);
        break;
      case 8: 
        serializeComment(localNode);
        break;
      case 7: 
        serializeProcessingInstruction(localNode);
      }
    }
    encodeDocumentTermination();
  }
  
  protected final void serializeElementAsDocument(Node paramNode)
    throws IOException
  {
    reset();
    encodeHeader(false);
    encodeInitialVocabulary();
    serializeElement(paramNode);
    encodeDocumentTermination();
  }
  
  protected final void serializeElement(Node paramNode)
    throws IOException
  {
    encodeTermination();
    int i = 0;
    _namespaceScopeContext.pushContext();
    String str2;
    Object localObject3;
    Object localObject4;
    if (paramNode.hasAttributes())
    {
      localObject1 = paramNode.getAttributes();
      for (int j = 0; j < ((NamedNodeMap)localObject1).getLength(); j++)
      {
        Node localNode = ((NamedNodeMap)localObject1).item(j);
        str2 = localNode.getNamespaceURI();
        if ((str2 != null) && (str2.equals("http://www.w3.org/2000/xmlns/")))
        {
          localObject3 = localNode.getLocalName();
          localObject4 = localNode.getNodeValue();
          if ((localObject3 == "xmlns") || (((String)localObject3).equals("xmlns"))) {
            localObject3 = "";
          }
          _namespaceScopeContext.declarePrefix((String)localObject3, (String)localObject4);
        }
        else
        {
          if (i == _attributes.length)
          {
            localObject3 = new Node[i * 3 / 2 + 1];
            System.arraycopy(_attributes, 0, localObject3, 0, i);
            _attributes = ((Node[])localObject3);
          }
          _attributes[(i++)] = localNode;
          localObject3 = localNode.getNamespaceURI();
          localObject4 = localNode.getPrefix();
          if ((localObject4 != null) && (!_namespaceScopeContext.getNamespaceURI((String)localObject4).equals(localObject3))) {
            _namespaceScopeContext.declarePrefix((String)localObject4, (String)localObject3);
          }
        }
      }
    }
    Object localObject1 = paramNode.getNamespaceURI();
    String str1 = paramNode.getPrefix();
    if (str1 == null) {
      str1 = "";
    }
    if ((localObject1 != null) && (!_namespaceScopeContext.getNamespaceURI(str1).equals(localObject1))) {
      _namespaceScopeContext.declarePrefix(str1, (String)localObject1);
    }
    if (!_namespaceScopeContext.isCurrentContextEmpty())
    {
      if (i > 0) {
        write(120);
      } else {
        write(56);
      }
      for (int k = _namespaceScopeContext.getCurrentContextStartIndex(); k < _namespaceScopeContext.getCurrentContextEndIndex(); k++)
      {
        str2 = _namespaceScopeContext.getPrefix(k);
        localObject3 = _namespaceScopeContext.getNamespaceURI(k);
        encodeNamespaceAttribute(str2, (String)localObject3);
      }
      write(240);
      _b = 0;
    }
    else
    {
      _b = (i > 0 ? 64 : 0);
    }
    Object localObject2 = localObject1;
    localObject2 = localObject2 == null ? "" : localObject2;
    encodeElement((String)localObject2, paramNode.getNodeName(), paramNode.getLocalName());
    if (i > 0)
    {
      for (int m = 0; m < i; m++)
      {
        localObject3 = _attributes[m];
        _attributes[m] = null;
        localObject2 = ((Node)localObject3).getNamespaceURI();
        localObject2 = localObject2 == null ? "" : localObject2;
        encodeAttribute((String)localObject2, ((Node)localObject3).getNodeName(), ((Node)localObject3).getLocalName());
        localObject4 = ((Node)localObject3).getNodeValue();
        boolean bool = isAttributeValueLengthMatchesLimit(((String)localObject4).length());
        encodeNonIdentifyingStringOnFirstBit((String)localObject4, _v.attributeValue, bool, false);
      }
      _b = 240;
      _terminate = true;
    }
    if (paramNode.hasChildNodes())
    {
      NodeList localNodeList = paramNode.getChildNodes();
      for (int n = 0; n < localNodeList.getLength(); n++)
      {
        localObject4 = localNodeList.item(n);
        switch (((Node)localObject4).getNodeType())
        {
        case 1: 
          serializeElement((Node)localObject4);
          break;
        case 3: 
          serializeText((Node)localObject4);
          break;
        case 4: 
          serializeCDATA((Node)localObject4);
          break;
        case 8: 
          serializeComment((Node)localObject4);
          break;
        case 7: 
          serializeProcessingInstruction((Node)localObject4);
        }
      }
    }
    encodeElementTermination();
    _namespaceScopeContext.popContext();
  }
  
  protected final void serializeText(Node paramNode)
    throws IOException
  {
    String str = paramNode.getNodeValue();
    int i = str != null ? str.length() : 0;
    if (i == 0) {
      return;
    }
    if (i < _charBuffer.length)
    {
      str.getChars(0, i, _charBuffer, 0);
      if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(_charBuffer, 0, i))) {
        return;
      }
      encodeTermination();
      encodeCharacters(_charBuffer, 0, i);
    }
    else
    {
      char[] arrayOfChar = str.toCharArray();
      if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(arrayOfChar, 0, i))) {
        return;
      }
      encodeTermination();
      encodeCharactersNoClone(arrayOfChar, 0, i);
    }
  }
  
  protected final void serializeCDATA(Node paramNode)
    throws IOException
  {
    String str = paramNode.getNodeValue();
    int i = str != null ? str.length() : 0;
    if (i == 0) {
      return;
    }
    char[] arrayOfChar = str.toCharArray();
    if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(arrayOfChar, 0, i))) {
      return;
    }
    encodeTermination();
    try
    {
      encodeCIIBuiltInAlgorithmDataAsCDATA(arrayOfChar, 0, i);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new IOException("");
    }
  }
  
  protected final void serializeComment(Node paramNode)
    throws IOException
  {
    if (getIgnoreComments()) {
      return;
    }
    encodeTermination();
    String str = paramNode.getNodeValue();
    int i = str != null ? str.length() : 0;
    if (i == 0)
    {
      encodeComment(_charBuffer, 0, 0);
    }
    else if (i < _charBuffer.length)
    {
      str.getChars(0, i, _charBuffer, 0);
      encodeComment(_charBuffer, 0, i);
    }
    else
    {
      char[] arrayOfChar = str.toCharArray();
      encodeCommentNoClone(arrayOfChar, 0, i);
    }
  }
  
  protected final void serializeProcessingInstruction(Node paramNode)
    throws IOException
  {
    if (getIgnoreProcesingInstructions()) {
      return;
    }
    encodeTermination();
    String str1 = paramNode.getNodeName();
    String str2 = paramNode.getNodeValue();
    encodeProcessingInstruction(str1, str2);
  }
  
  protected final void encodeElement(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.elementName.obtainEntry(paramString2);
    if (_valueIndex > 0)
    {
      QualifiedName[] arrayOfQualifiedName = _value;
      for (int i = 0; i < _valueIndex; i++) {
        if ((paramString1 == namespaceName) || (paramString1.equals(namespaceName)))
        {
          encodeNonZeroIntegerOnThirdBit(index);
          return;
        }
      }
    }
    if (paramString3 != null) {
      encodeLiteralElementQualifiedNameOnThirdBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, localEntry);
    } else {
      encodeLiteralElementQualifiedNameOnThirdBit(paramString1, "", paramString2, localEntry);
    }
  }
  
  protected final void encodeAttribute(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.attributeName.obtainEntry(paramString2);
    if (_valueIndex > 0)
    {
      QualifiedName[] arrayOfQualifiedName = _value;
      for (int i = 0; i < _valueIndex; i++) {
        if ((paramString1 == namespaceName) || (paramString1.equals(namespaceName)))
        {
          encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
          return;
        }
      }
    }
    if (paramString3 != null) {
      encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, localEntry);
    } else {
      encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, "", paramString2, localEntry);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\dom\DOMDocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */