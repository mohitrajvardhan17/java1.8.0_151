package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.TemplatesHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TemplatesHandlerImpl
  implements ContentHandler, TemplatesHandler, SourceLoader
{
  private String _systemId;
  private int _indentNumber;
  private URIResolver _uriResolver = null;
  private TransformerFactoryImpl _tfactory = null;
  private Parser _parser = null;
  private TemplatesImpl _templates = null;
  
  protected TemplatesHandlerImpl(int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl)
  {
    _indentNumber = paramInt;
    _tfactory = paramTransformerFactoryImpl;
    XSLTC localXSLTC = new XSLTC(paramTransformerFactoryImpl.useServicesMechnism(), paramTransformerFactoryImpl.getFeatureManager());
    if (paramTransformerFactoryImpl.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
      localXSLTC.setSecureProcessing(true);
    }
    localXSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet", (String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet"));
    localXSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", (String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD"));
    localXSLTC.setProperty("http://apache.org/xml/properties/security-manager", paramTransformerFactoryImpl.getAttribute("http://apache.org/xml/properties/security-manager"));
    if ("true".equals(paramTransformerFactoryImpl.getAttribute("enable-inlining"))) {
      localXSLTC.setTemplateInlining(true);
    } else {
      localXSLTC.setTemplateInlining(false);
    }
    _parser = localXSLTC.getParser();
  }
  
  public String getSystemId()
  {
    return _systemId;
  }
  
  public void setSystemId(String paramString)
  {
    _systemId = paramString;
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    _uriResolver = paramURIResolver;
  }
  
  public Templates getTemplates()
  {
    return _templates;
  }
  
  public InputSource loadSource(String paramString1, String paramString2, XSLTC paramXSLTC)
  {
    try
    {
      Source localSource = _uriResolver.resolve(paramString1, paramString2);
      if (localSource != null) {
        return Util.getInputSource(paramXSLTC, localSource);
      }
    }
    catch (TransformerException localTransformerException) {}
    return null;
  }
  
  public void startDocument()
  {
    XSLTC localXSLTC = _parser.getXSLTC();
    localXSLTC.init();
    localXSLTC.setOutputType(2);
    _parser.startDocument();
  }
  
  public void endDocument()
    throws SAXException
  {
    _parser.endDocument();
    try
    {
      XSLTC localXSLTC = _parser.getXSLTC();
      if (_systemId != null) {
        str = Util.baseName(_systemId);
      } else {
        str = (String)_tfactory.getAttribute("translet-name");
      }
      localXSLTC.setClassName(str);
      String str = localXSLTC.getClassName();
      Stylesheet localStylesheet = null;
      SyntaxTreeNode localSyntaxTreeNode = _parser.getDocumentRoot();
      if ((!_parser.errorsFound()) && (localSyntaxTreeNode != null))
      {
        localStylesheet = _parser.makeStylesheet(localSyntaxTreeNode);
        localStylesheet.setSystemId(_systemId);
        localStylesheet.setParentStylesheet(null);
        if (localXSLTC.getTemplateInlining()) {
          localStylesheet.setTemplateInlining(true);
        } else {
          localStylesheet.setTemplateInlining(false);
        }
        if (_uriResolver != null) {
          localStylesheet.setSourceLoader(this);
        }
        _parser.setCurrentStylesheet(localStylesheet);
        localXSLTC.setStylesheet(localStylesheet);
        _parser.createAST(localStylesheet);
      }
      if ((!_parser.errorsFound()) && (localStylesheet != null))
      {
        localStylesheet.setMultiDocument(localXSLTC.isMultiDocument());
        localStylesheet.setHasIdCall(localXSLTC.hasIdCall());
        synchronized (localXSLTC.getClass())
        {
          localStylesheet.translate();
        }
      }
      if (!_parser.errorsFound())
      {
        ??? = localXSLTC.getBytecodes();
        if (??? != null)
        {
          _templates = new TemplatesImpl(localXSLTC.getBytecodes(), str, _parser.getOutputProperties(), _indentNumber, _tfactory);
          if (_uriResolver != null) {
            _templates.setURIResolver(_uriResolver);
          }
        }
      }
      else
      {
        ??? = new StringBuffer();
        Vector localVector = _parser.getErrors();
        int i = localVector.size();
        for (int j = 0; j < i; j++)
        {
          if (((StringBuffer)???).length() > 0) {
            ((StringBuffer)???).append('\n');
          }
          ((StringBuffer)???).append(localVector.elementAt(j).toString());
        }
        throw new SAXException("JAXP_COMPILE_ERR", new TransformerException(((StringBuffer)???).toString()));
      }
    }
    catch (CompilerException localCompilerException)
    {
      throw new SAXException("JAXP_COMPILE_ERR", localCompilerException);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    _parser.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
  {
    _parser.endPrefixMapping(paramString);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    _parser.startElement(paramString1, paramString2, paramString3, paramAttributes);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    _parser.endElement(paramString1, paramString2, paramString3);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    _parser.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
  {
    _parser.processingInstruction(paramString1, paramString2);
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    _parser.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void skippedEntity(String paramString)
  {
    _parser.skippedEntity(paramString);
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    setSystemId(paramLocator.getSystemId());
    _parser.setDocumentLocator(paramLocator);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TemplatesHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */