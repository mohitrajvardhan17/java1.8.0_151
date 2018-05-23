package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class TransformerHandlerImpl
  implements TransformerHandler, DeclHandler
{
  private TransformerImpl _transformer;
  private AbstractTranslet _translet = null;
  private String _systemId;
  private SAXImpl _dom = null;
  private ContentHandler _handler = null;
  private LexicalHandler _lexHandler = null;
  private DTDHandler _dtdHandler = null;
  private DeclHandler _declHandler = null;
  private Result _result = null;
  private Locator _locator = null;
  private boolean _done = false;
  private boolean _isIdentity = false;
  
  public TransformerHandlerImpl(TransformerImpl paramTransformerImpl)
  {
    _transformer = paramTransformerImpl;
    if (paramTransformerImpl.isIdentity())
    {
      _handler = new DefaultHandler();
      _isIdentity = true;
    }
    else
    {
      _translet = _transformer.getTranslet();
    }
  }
  
  public String getSystemId()
  {
    return _systemId;
  }
  
  public void setSystemId(String paramString)
  {
    _systemId = paramString;
  }
  
  public Transformer getTransformer()
  {
    return _transformer;
  }
  
  public void setResult(Result paramResult)
    throws IllegalArgumentException
  {
    _result = paramResult;
    Object localObject;
    if (null == paramResult)
    {
      localObject = new ErrorMsg("ER_RESULT_NULL");
      throw new IllegalArgumentException(((ErrorMsg)localObject).toString());
    }
    if (_isIdentity) {
      try
      {
        localObject = _transformer.getOutputHandler(paramResult);
        _transformer.transferOutputProperties((SerializationHandler)localObject);
        _handler = ((ContentHandler)localObject);
        _lexHandler = ((LexicalHandler)localObject);
      }
      catch (TransformerException localTransformerException1)
      {
        _result = null;
      }
    } else if (_done) {
      try
      {
        _transformer.setDOM(_dom);
        _transformer.transform(null, _result);
      }
      catch (TransformerException localTransformerException2)
      {
        throw new IllegalArgumentException(localTransformerException2.getMessage());
      }
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    _handler.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void startDocument()
    throws SAXException
  {
    if (_result == null)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("JAXP_SET_RESULT_ERR");
      throw new SAXException(localErrorMsg.toString());
    }
    if (!_isIdentity)
    {
      boolean bool = _translet != null ? _translet.hasIdCall() : false;
      XSLTCDTMManager localXSLTCDTMManager = null;
      try
      {
        localXSLTCDTMManager = _transformer.getTransformerFactory().createNewDTMManagerInstance();
      }
      catch (Exception localException)
      {
        throw new SAXException(localException);
      }
      DOMWSFilter localDOMWSFilter;
      if ((_translet != null) && ((_translet instanceof StripFilter))) {
        localDOMWSFilter = new DOMWSFilter(_translet);
      } else {
        localDOMWSFilter = null;
      }
      _dom = ((SAXImpl)localXSLTCDTMManager.getDTM(null, false, localDOMWSFilter, true, false, bool));
      _handler = _dom.getBuilder();
      _lexHandler = ((LexicalHandler)_handler);
      _dtdHandler = ((DTDHandler)_handler);
      _declHandler = ((DeclHandler)_handler);
      _dom.setDocumentURI(_systemId);
      if (_locator != null) {
        _handler.setDocumentLocator(_locator);
      }
    }
    _handler.startDocument();
  }
  
  public void endDocument()
    throws SAXException
  {
    _handler.endDocument();
    if (!_isIdentity)
    {
      if (_result != null) {
        try
        {
          _transformer.setDOM(_dom);
          _transformer.transform(null, _result);
        }
        catch (TransformerException localTransformerException)
        {
          throw new SAXException(localTransformerException);
        }
      }
      _done = true;
      _transformer.setDOM(_dom);
    }
    if ((_isIdentity) && ((_result instanceof DOMResult))) {
      ((DOMResult)_result).setNode(_transformer.getTransletOutputHandlerFactory().getNode());
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    _handler.startElement(paramString1, paramString2, paramString3, paramAttributes);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    _handler.endElement(paramString1, paramString2, paramString3);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    _handler.processingInstruction(paramString1, paramString2);
  }
  
  public void startCDATA()
    throws SAXException
  {
    if (_lexHandler != null) {
      _lexHandler.startCDATA();
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    if (_lexHandler != null) {
      _lexHandler.endCDATA();
    }
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (_lexHandler != null) {
      _lexHandler.comment(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    _handler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    _locator = paramLocator;
    if (_handler != null) {
      _handler.setDocumentLocator(paramLocator);
    }
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    _handler.skippedEntity(paramString);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    _handler.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    _handler.endPrefixMapping(paramString);
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (_lexHandler != null) {
      _lexHandler.startDTD(paramString1, paramString2, paramString3);
    }
  }
  
  public void endDTD()
    throws SAXException
  {
    if (_lexHandler != null) {
      _lexHandler.endDTD();
    }
  }
  
  public void startEntity(String paramString)
    throws SAXException
  {
    if (_lexHandler != null) {
      _lexHandler.startEntity(paramString);
    }
  }
  
  public void endEntity(String paramString)
    throws SAXException
  {
    if (_lexHandler != null) {
      _lexHandler.endEntity(paramString);
    }
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    if (_dtdHandler != null) {
      _dtdHandler.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
    }
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (_dtdHandler != null) {
      _dtdHandler.notationDecl(paramString1, paramString2, paramString3);
    }
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {
    if (_declHandler != null) {
      _declHandler.attributeDecl(paramString1, paramString2, paramString3, paramString4, paramString5);
    }
  }
  
  public void elementDecl(String paramString1, String paramString2)
    throws SAXException
  {
    if (_declHandler != null) {
      _declHandler.elementDecl(paramString1, paramString2);
    }
  }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (_declHandler != null) {
      _declHandler.externalEntityDecl(paramString1, paramString2, paramString3);
    }
  }
  
  public void internalEntityDecl(String paramString1, String paramString2)
    throws SAXException
  {
    if (_declHandler != null) {
      _declHandler.internalEntityDecl(paramString1, paramString2);
    }
  }
  
  public void reset()
  {
    _systemId = null;
    _dom = null;
    _handler = null;
    _lexHandler = null;
    _dtdHandler = null;
    _declHandler = null;
    _result = null;
    _locator = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TransformerHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */