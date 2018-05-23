package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xml.internal.serializer.NamespaceMappings;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Locator2;

public class DOM2TO
  implements XMLReader, Locator2
{
  private static final String EMPTYSTRING = "";
  private static final String XMLNS_PREFIX = "xmlns";
  private Node _dom;
  private SerializationHandler _handler;
  private String xmlVersion = null;
  private String xmlEncoding = null;
  
  public DOM2TO(Node paramNode, SerializationHandler paramSerializationHandler)
  {
    _dom = paramNode;
    _handler = paramSerializationHandler;
  }
  
  public ContentHandler getContentHandler()
  {
    return null;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler) {}
  
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
        _handler.startDocument();
        parse(_dom);
        _handler.endDocument();
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
    if (paramNode == null) {
      return;
    }
    Node localNode1;
    switch (paramNode.getNodeType())
    {
    case 2: 
    case 5: 
    case 6: 
    case 10: 
    case 12: 
      break;
    case 4: 
      _handler.startCDATA();
      _handler.characters(paramNode.getNodeValue());
      _handler.endCDATA();
      break;
    case 8: 
      _handler.comment(paramNode.getNodeValue());
      break;
    case 9: 
      setDocumentInfo((Document)paramNode);
      _handler.setDocumentLocator(this);
      _handler.startDocument();
      for (localNode1 = paramNode.getFirstChild(); localNode1 != null; localNode1 = localNode1.getNextSibling()) {
        parse(localNode1);
      }
      _handler.endDocument();
      break;
    case 11: 
      localNode1 = paramNode.getFirstChild();
    case 1: 
    case 7: 
    case 3: 
      while (localNode1 != null)
      {
        parse(localNode1);
        localNode1 = localNode1.getNextSibling();
        continue;
        String str1 = paramNode.getNodeName();
        _handler.startElement(null, null, str1);
        NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
        int j = localNamedNodeMap.getLength();
        String str3;
        int i;
        Object localObject1;
        for (int k = 0; k < j; k++)
        {
          Node localNode2 = localNamedNodeMap.item(k);
          localObject2 = localNode2.getNodeName();
          if (((String)localObject2).startsWith("xmlns"))
          {
            str3 = localNode2.getNodeValue();
            i = ((String)localObject2).lastIndexOf(':');
            localObject1 = i > 0 ? ((String)localObject2).substring(i + 1) : "";
            _handler.namespaceAfterStartElement((String)localObject1, str3);
          }
        }
        NamespaceMappings localNamespaceMappings = new NamespaceMappings();
        for (int m = 0; m < j; m++)
        {
          localObject2 = localNamedNodeMap.item(m);
          str3 = ((Node)localObject2).getNodeName();
          if (!str3.startsWith("xmlns"))
          {
            String str4 = ((Node)localObject2).getNamespaceURI();
            if ((str4 != null) && (!str4.equals("")))
            {
              i = str3.lastIndexOf(':');
              String str5 = localNamespaceMappings.lookupPrefix(str4);
              if (str5 == null) {
                str5 = localNamespaceMappings.generateNextPrefix();
              }
              localObject1 = i > 0 ? str3.substring(0, i) : str5;
              _handler.namespaceAfterStartElement((String)localObject1, str4);
              _handler.addAttribute((String)localObject1 + ":" + str3, ((Node)localObject2).getNodeValue());
            }
            else
            {
              _handler.addAttribute(str3, ((Node)localObject2).getNodeValue());
            }
          }
        }
        String str2 = paramNode.getNamespaceURI();
        Object localObject2 = paramNode.getLocalName();
        if (str2 != null)
        {
          i = str1.lastIndexOf(':');
          localObject1 = i > 0 ? str1.substring(0, i) : "";
          _handler.namespaceAfterStartElement((String)localObject1, str2);
        }
        else if ((str2 == null) && (localObject2 != null))
        {
          localObject1 = "";
          _handler.namespaceAfterStartElement((String)localObject1, "");
        }
        for (localNode1 = paramNode.getFirstChild(); localNode1 != null; localNode1 = localNode1.getNextSibling()) {
          parse(localNode1);
        }
        _handler.endElement(str1);
        break;
        _handler.processingInstruction(paramNode.getNodeName(), paramNode.getNodeValue());
        break;
        _handler.characters(paramNode.getNodeValue());
      }
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
  
  private void setDocumentInfo(Document paramDocument)
  {
    if (!paramDocument.getXmlStandalone()) {
      _handler.setStandalone(Boolean.toString(paramDocument.getXmlStandalone()));
    }
    setXMLVersion(paramDocument.getXmlVersion());
    setEncoding(paramDocument.getXmlEncoding());
  }
  
  public String getXMLVersion()
  {
    return xmlVersion;
  }
  
  private void setXMLVersion(String paramString)
  {
    if (paramString != null)
    {
      xmlVersion = paramString;
      _handler.setVersion(xmlVersion);
    }
  }
  
  public String getEncoding()
  {
    return xmlEncoding;
  }
  
  private void setEncoding(String paramString)
  {
    if (paramString != null)
    {
      xmlEncoding = paramString;
      _handler.setEncoding(paramString);
    }
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\DOM2TO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */