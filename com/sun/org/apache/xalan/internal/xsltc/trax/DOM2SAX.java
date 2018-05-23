package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class DOM2SAX
  implements XMLReader, Locator
{
  private static final String EMPTYSTRING = "";
  private static final String XMLNS_PREFIX = "xmlns";
  private Node _dom = null;
  private ContentHandler _sax = null;
  private LexicalHandler _lex = null;
  private SAXImpl _saxImpl = null;
  private Map<String, Stack> _nsPrefixes = new HashMap();
  
  public DOM2SAX(Node paramNode)
  {
    _dom = paramNode;
  }
  
  public ContentHandler getContentHandler()
  {
    return _sax;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
    throws NullPointerException
  {
    _sax = paramContentHandler;
    if ((paramContentHandler instanceof LexicalHandler)) {
      _lex = ((LexicalHandler)paramContentHandler);
    }
    if ((paramContentHandler instanceof SAXImpl)) {
      _saxImpl = ((SAXImpl)paramContentHandler);
    }
  }
  
  private boolean startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    boolean bool = true;
    Stack localStack = (Stack)_nsPrefixes.get(paramString1);
    if (localStack != null)
    {
      if (localStack.isEmpty())
      {
        _sax.startPrefixMapping(paramString1, paramString2);
        localStack.push(paramString2);
      }
      else
      {
        String str = (String)localStack.peek();
        if (!str.equals(paramString2))
        {
          _sax.startPrefixMapping(paramString1, paramString2);
          localStack.push(paramString2);
        }
        else
        {
          bool = false;
        }
      }
    }
    else
    {
      _sax.startPrefixMapping(paramString1, paramString2);
      _nsPrefixes.put(paramString1, localStack = new Stack());
      localStack.push(paramString2);
    }
    return bool;
  }
  
  private void endPrefixMapping(String paramString)
    throws SAXException
  {
    Stack localStack = (Stack)_nsPrefixes.get(paramString);
    if (localStack != null)
    {
      _sax.endPrefixMapping(paramString);
      localStack.pop();
    }
  }
  
  private static String getLocalName(Node paramNode)
  {
    String str1 = paramNode.getLocalName();
    if (str1 == null)
    {
      String str2 = paramNode.getNodeName();
      int i = str2.lastIndexOf(':');
      return i > 0 ? str2.substring(i + 1) : str2;
    }
    return str1;
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    parse(_dom);
  }
  
  public void parse()
    throws IOException, SAXException
  {
    if (_dom != null)
    {
      int i = _dom.getNodeType() != 9 ? 1 : 0;
      if (i != 0)
      {
        _sax.startDocument();
        parse(_dom);
        _sax.endDocument();
      }
      else
      {
        parse(_dom);
      }
    }
  }
  
  private void parse(Node paramNode)
    throws IOException, SAXException
  {
    Object localObject1 = null;
    if (paramNode == null) {
      return;
    }
    Object localObject2;
    switch (paramNode.getNodeType())
    {
    case 2: 
    case 5: 
    case 6: 
    case 10: 
    case 11: 
    case 12: 
      break;
    case 4: 
      String str1 = paramNode.getNodeValue();
      if (_lex != null)
      {
        _lex.startCDATA();
        _sax.characters(str1.toCharArray(), 0, str1.length());
        _lex.endCDATA();
      }
      else
      {
        _sax.characters(str1.toCharArray(), 0, str1.length());
      }
      break;
    case 8: 
      if (_lex != null)
      {
        localObject2 = paramNode.getNodeValue();
        _lex.comment(((String)localObject2).toCharArray(), 0, ((String)localObject2).length());
      }
      break;
    case 9: 
      _sax.setDocumentLocator(this);
      _sax.startDocument();
      for (localObject2 = paramNode.getFirstChild(); localObject2 != null; localObject2 = ((Node)localObject2).getNextSibling()) {
        parse((Node)localObject2);
      }
      _sax.endDocument();
      break;
    case 1: 
      Vector localVector = new Vector();
      AttributesImpl localAttributesImpl = new AttributesImpl();
      NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
      int i = localNamedNodeMap.getLength();
      String str5;
      String str2;
      for (int j = 0; j < i; j++)
      {
        localObject3 = localNamedNodeMap.item(j);
        str4 = ((Node)localObject3).getNodeName();
        if (str4.startsWith("xmlns"))
        {
          str5 = ((Node)localObject3).getNodeValue();
          int m = str4.lastIndexOf(':');
          str2 = m > 0 ? str4.substring(m + 1) : "";
          if (startPrefixMapping(str2, str5)) {
            localVector.addElement(str2);
          }
        }
      }
      for (j = 0; j < i; j++)
      {
        localObject3 = localNamedNodeMap.item(j);
        str4 = ((Node)localObject3).getNodeName();
        if (!str4.startsWith("xmlns"))
        {
          str5 = ((Node)localObject3).getNamespaceURI();
          String str6 = getLocalName((Node)localObject3);
          if (str5 != null)
          {
            int i1 = str4.lastIndexOf(':');
            if (i1 > 0)
            {
              str2 = str4.substring(0, i1);
            }
            else
            {
              str2 = BasisLibrary.generatePrefix();
              str4 = str2 + ':' + str4;
            }
            if (startPrefixMapping(str2, str5)) {
              localVector.addElement(str2);
            }
          }
          localAttributesImpl.addAttribute(((Node)localObject3).getNamespaceURI(), getLocalName((Node)localObject3), str4, "CDATA", ((Node)localObject3).getNodeValue());
        }
      }
      String str3 = paramNode.getNodeName();
      Object localObject3 = paramNode.getNamespaceURI();
      String str4 = getLocalName(paramNode);
      if (localObject3 != null)
      {
        k = str3.lastIndexOf(':');
        str2 = k > 0 ? str3.substring(0, k) : "";
        if (startPrefixMapping(str2, (String)localObject3)) {
          localVector.addElement(str2);
        }
      }
      if (_saxImpl != null) {
        _saxImpl.startElement((String)localObject3, str4, str3, localAttributesImpl, paramNode);
      } else {
        _sax.startElement((String)localObject3, str4, str3, localAttributesImpl);
      }
      for (localObject2 = paramNode.getFirstChild(); localObject2 != null; localObject2 = ((Node)localObject2).getNextSibling()) {
        parse((Node)localObject2);
      }
      _sax.endElement((String)localObject3, str4, str3);
      int k = localVector.size();
      for (int n = 0; n < k; n++) {
        endPrefixMapping((String)localVector.elementAt(n));
      }
      break;
    case 7: 
      _sax.processingInstruction(paramNode.getNodeName(), paramNode.getNodeValue());
      break;
    case 3: 
      String str7 = paramNode.getNodeValue();
      _sax.characters(str7.toCharArray(), 0, str7.length());
    }
  }
  
  public DTDHandler getDTDHandler()
  {
    return null;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return null;
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return false;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {}
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    throw new IOException("This method is not yet implemented.");
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
    throws NullPointerException
  {}
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
    throws NullPointerException
  {}
  
  public EntityResolver getEntityResolver()
  {
    return null;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
    throws NullPointerException
  {}
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {}
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return null;
  }
  
  public int getColumnNumber()
  {
    return 0;
  }
  
  public int getLineNumber()
  {
    return 0;
  }
  
  public String getPublicId()
  {
    return null;
  }
  
  public String getSystemId()
  {
    return null;
  }
  
  private String getNodeTypeFromCode(short paramShort)
  {
    String str = null;
    switch (paramShort)
    {
    case 2: 
      str = "ATTRIBUTE_NODE";
      break;
    case 4: 
      str = "CDATA_SECTION_NODE";
      break;
    case 8: 
      str = "COMMENT_NODE";
      break;
    case 11: 
      str = "DOCUMENT_FRAGMENT_NODE";
      break;
    case 9: 
      str = "DOCUMENT_NODE";
      break;
    case 10: 
      str = "DOCUMENT_TYPE_NODE";
      break;
    case 1: 
      str = "ELEMENT_NODE";
      break;
    case 6: 
      str = "ENTITY_NODE";
      break;
    case 5: 
      str = "ENTITY_REFERENCE_NODE";
      break;
    case 12: 
      str = "NOTATION_NODE";
      break;
    case 7: 
      str = "PROCESSING_INSTRUCTION_NODE";
      break;
    case 3: 
      str = "TEXT_NODE";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\DOM2SAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */