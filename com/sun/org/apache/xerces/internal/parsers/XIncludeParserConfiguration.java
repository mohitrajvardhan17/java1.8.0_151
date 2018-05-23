package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import java.util.Map;

public class XIncludeParserConfiguration
  extends XML11Configuration
{
  private XIncludeHandler fXIncludeHandler = new XIncludeHandler();
  protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
  protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
  protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
  protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  public XIncludeParserConfiguration()
  {
    this(null, null, null);
  }
  
  public XIncludeParserConfiguration(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null, null);
  }
  
  public XIncludeParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    this(paramSymbolTable, paramXMLGrammarPool, null);
  }
  
  public XIncludeParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager)
  {
    super(paramSymbolTable, paramXMLGrammarPool, paramXMLComponentManager);
    addCommonComponent(fXIncludeHandler);
    String[] arrayOfString1 = { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" };
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = { "http://apache.org/xml/properties/internal/xinclude-handler", "http://apache.org/xml/properties/internal/namespace-context" };
    addRecognizedProperties(arrayOfString2);
    setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
    setProperty("http://apache.org/xml/properties/internal/xinclude-handler", fXIncludeHandler);
    setProperty("http://apache.org/xml/properties/internal/namespace-context", new XIncludeNamespaceSupport());
  }
  
  protected void configurePipeline()
  {
    super.configurePipeline();
    fDTDScanner.setDTDHandler(fDTDProcessor);
    fDTDProcessor.setDTDSource(fDTDScanner);
    fDTDProcessor.setDTDHandler(fXIncludeHandler);
    fXIncludeHandler.setDTDSource(fDTDProcessor);
    fXIncludeHandler.setDTDHandler(fDTDHandler);
    if (fDTDHandler != null) {
      fDTDHandler.setDTDSource(fXIncludeHandler);
    }
    XMLDocumentSource localXMLDocumentSource = null;
    if (fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE)
    {
      localXMLDocumentSource = fSchemaValidator.getDocumentSource();
    }
    else
    {
      localXMLDocumentSource = fLastComponent;
      fLastComponent = fXIncludeHandler;
    }
    if (localXMLDocumentSource != null)
    {
      XMLDocumentHandler localXMLDocumentHandler = localXMLDocumentSource.getDocumentHandler();
      localXMLDocumentSource.setDocumentHandler(fXIncludeHandler);
      fXIncludeHandler.setDocumentSource(localXMLDocumentSource);
      if (localXMLDocumentHandler != null)
      {
        fXIncludeHandler.setDocumentHandler(localXMLDocumentHandler);
        localXMLDocumentHandler.setDocumentSource(fXIncludeHandler);
      }
    }
    else
    {
      setDocumentHandler(fXIncludeHandler);
    }
  }
  
  protected void configureXML11Pipeline()
  {
    super.configureXML11Pipeline();
    fXML11DTDScanner.setDTDHandler(fXML11DTDProcessor);
    fXML11DTDProcessor.setDTDSource(fXML11DTDScanner);
    fXML11DTDProcessor.setDTDHandler(fXIncludeHandler);
    fXIncludeHandler.setDTDSource(fXML11DTDProcessor);
    fXIncludeHandler.setDTDHandler(fDTDHandler);
    if (fDTDHandler != null) {
      fDTDHandler.setDTDSource(fXIncludeHandler);
    }
    XMLDocumentSource localXMLDocumentSource = null;
    if (fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE)
    {
      localXMLDocumentSource = fSchemaValidator.getDocumentSource();
    }
    else
    {
      localXMLDocumentSource = fLastComponent;
      fLastComponent = fXIncludeHandler;
    }
    XMLDocumentHandler localXMLDocumentHandler = localXMLDocumentSource.getDocumentHandler();
    localXMLDocumentSource.setDocumentHandler(fXIncludeHandler);
    fXIncludeHandler.setDocumentSource(localXMLDocumentSource);
    if (localXMLDocumentHandler != null)
    {
      fXIncludeHandler.setDocumentHandler(localXMLDocumentHandler);
      localXMLDocumentHandler.setDocumentSource(fXIncludeHandler);
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    if (paramString.equals("http://apache.org/xml/properties/internal/xinclude-handler")) {}
    super.setProperty(paramString, paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XIncludeParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */