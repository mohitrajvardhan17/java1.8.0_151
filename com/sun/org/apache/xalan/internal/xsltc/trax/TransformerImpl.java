package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.MessageHandler;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public final class TransformerImpl
  extends Transformer
  implements DOMCache, ErrorListener
{
  private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
  private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
  private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
  private AbstractTranslet _translet = null;
  private String _method = null;
  private String _encoding = null;
  private String _sourceSystemId = null;
  private ErrorListener _errorListener = this;
  private URIResolver _uriResolver = null;
  private Properties _properties;
  private Properties _propertiesClone;
  private TransletOutputHandlerFactory _tohFactory = null;
  private DOM _dom = null;
  private int _indentNumber;
  private TransformerFactoryImpl _tfactory = null;
  private OutputStream _ostream = null;
  private XSLTCDTMManager _dtmManager = null;
  private XMLReaderManager _readerManager;
  private boolean _isIdentity = false;
  private boolean _isSecureProcessing = false;
  private boolean _useServicesMechanism;
  private String _accessExternalStylesheet = "all";
  private String _accessExternalDTD = "all";
  private XMLSecurityManager _securityManager;
  private Map<String, Object> _parameters = null;
  
  protected TransformerImpl(Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl)
  {
    this(null, paramProperties, paramInt, paramTransformerFactoryImpl);
    _isIdentity = true;
  }
  
  protected TransformerImpl(Translet paramTranslet, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl)
  {
    _translet = ((AbstractTranslet)paramTranslet);
    _properties = createOutputProperties(paramProperties);
    _propertiesClone = ((Properties)_properties.clone());
    _indentNumber = paramInt;
    _tfactory = paramTransformerFactoryImpl;
    _useServicesMechanism = _tfactory.useServicesMechnism();
    _accessExternalStylesheet = ((String)_tfactory.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet"));
    _accessExternalDTD = ((String)_tfactory.getAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD"));
    _securityManager = ((XMLSecurityManager)_tfactory.getAttribute("http://apache.org/xml/properties/security-manager"));
    _readerManager = XMLReaderManager.getInstance(_useServicesMechanism);
    _readerManager.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", _accessExternalDTD);
    _readerManager.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", _isSecureProcessing);
    _readerManager.setProperty("http://apache.org/xml/properties/security-manager", _securityManager);
  }
  
  public boolean isSecureProcessing()
  {
    return _isSecureProcessing;
  }
  
  public void setSecureProcessing(boolean paramBoolean)
  {
    _isSecureProcessing = paramBoolean;
    _readerManager.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", _isSecureProcessing);
  }
  
  public boolean useServicesMechnism()
  {
    return _useServicesMechanism;
  }
  
  public void setServicesMechnism(boolean paramBoolean)
  {
    _useServicesMechanism = paramBoolean;
  }
  
  protected AbstractTranslet getTranslet()
  {
    return _translet;
  }
  
  public boolean isIdentity()
  {
    return _isIdentity;
  }
  
  public void transform(Source paramSource, Result paramResult)
    throws TransformerException
  {
    if (!_isIdentity)
    {
      if (_translet == null)
      {
        localObject = new ErrorMsg("JAXP_NO_TRANSLET_ERR");
        throw new TransformerException(((ErrorMsg)localObject).toString());
      }
      transferOutputProperties(_translet);
    }
    Object localObject = getOutputHandler(paramResult);
    if (localObject == null)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("JAXP_NO_HANDLER_ERR");
      throw new TransformerException(localErrorMsg.toString());
    }
    if ((_uriResolver != null) && (!_isIdentity)) {
      _translet.setDOMCache(this);
    }
    if (_isIdentity) {
      transferOutputProperties((SerializationHandler)localObject);
    }
    transform(paramSource, (SerializationHandler)localObject, _encoding);
    try
    {
      if ((paramResult instanceof DOMResult)) {
        ((DOMResult)paramResult).setNode(_tohFactory.getNode());
      } else if ((paramResult instanceof StAXResult)) {
        if (((StAXResult)paramResult).getXMLEventWriter() != null) {
          _tohFactory.getXMLEventWriter().flush();
        } else if (((StAXResult)paramResult).getXMLStreamWriter() != null) {
          _tohFactory.getXMLStreamWriter().flush();
        }
      }
    }
    catch (Exception localException)
    {
      System.out.println("Result writing error");
    }
  }
  
  public SerializationHandler getOutputHandler(Result paramResult)
    throws TransformerException
  {
    _method = ((String)_properties.get("method"));
    _encoding = _properties.getProperty("encoding");
    _tohFactory = TransletOutputHandlerFactory.newInstance(_useServicesMechanism);
    _tohFactory.setEncoding(_encoding);
    if (_method != null) {
      _tohFactory.setOutputMethod(_method);
    }
    if (_indentNumber >= 0) {
      _tohFactory.setIndentNumber(_indentNumber);
    }
    try
    {
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if ((paramResult instanceof SAXResult))
      {
        localObject1 = (SAXResult)paramResult;
        localObject2 = ((SAXResult)localObject1).getHandler();
        _tohFactory.setHandler((ContentHandler)localObject2);
        localObject3 = ((SAXResult)localObject1).getLexicalHandler();
        if (localObject3 != null) {
          _tohFactory.setLexicalHandler((LexicalHandler)localObject3);
        }
        _tohFactory.setOutputType(1);
        return _tohFactory.getSerializationHandler();
      }
      if ((paramResult instanceof StAXResult))
      {
        if (((StAXResult)paramResult).getXMLEventWriter() != null) {
          _tohFactory.setXMLEventWriter(((StAXResult)paramResult).getXMLEventWriter());
        } else if (((StAXResult)paramResult).getXMLStreamWriter() != null) {
          _tohFactory.setXMLStreamWriter(((StAXResult)paramResult).getXMLStreamWriter());
        }
        _tohFactory.setOutputType(3);
        return _tohFactory.getSerializationHandler();
      }
      if ((paramResult instanceof DOMResult))
      {
        _tohFactory.setNode(((DOMResult)paramResult).getNode());
        _tohFactory.setNextSibling(((DOMResult)paramResult).getNextSibling());
        _tohFactory.setOutputType(2);
        return _tohFactory.getSerializationHandler();
      }
      if ((paramResult instanceof StreamResult))
      {
        localObject1 = (StreamResult)paramResult;
        _tohFactory.setOutputType(0);
        localObject2 = ((StreamResult)localObject1).getWriter();
        if (localObject2 != null)
        {
          _tohFactory.setWriter((Writer)localObject2);
          return _tohFactory.getSerializationHandler();
        }
        localObject3 = ((StreamResult)localObject1).getOutputStream();
        if (localObject3 != null)
        {
          _tohFactory.setOutputStream((OutputStream)localObject3);
          return _tohFactory.getSerializationHandler();
        }
        String str1 = paramResult.getSystemId();
        Object localObject4;
        if (str1 == null)
        {
          localObject4 = new ErrorMsg("JAXP_NO_RESULT_ERR");
          throw new TransformerException(((ErrorMsg)localObject4).toString());
        }
        if (str1.startsWith("file:"))
        {
          try
          {
            URI localURI = new URI(str1);
            str1 = "file:";
            String str2 = localURI.getHost();
            String str3 = localURI.getPath();
            if (str3 == null) {
              str3 = "";
            }
            if (str2 != null) {
              str1 = str1 + "//" + str2 + str3;
            } else {
              str1 = str1 + "//" + str3;
            }
          }
          catch (Exception localException) {}
          localObject4 = new URL(str1);
          _ostream = new FileOutputStream(((URL)localObject4).getFile());
          _tohFactory.setOutputStream(_ostream);
          return _tohFactory.getSerializationHandler();
        }
        if (str1.startsWith("http:"))
        {
          localObject4 = new URL(str1);
          URLConnection localURLConnection = ((URL)localObject4).openConnection();
          _tohFactory.setOutputStream(_ostream = localURLConnection.getOutputStream());
          return _tohFactory.getSerializationHandler();
        }
        _tohFactory.setOutputStream(_ostream = new FileOutputStream(new File(str1)));
        return _tohFactory.getSerializationHandler();
      }
    }
    catch (UnknownServiceException localUnknownServiceException)
    {
      throw new TransformerException(localUnknownServiceException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new TransformerException(localParserConfigurationException);
    }
    catch (IOException localIOException)
    {
      throw new TransformerException(localIOException);
    }
    return null;
  }
  
  protected void setDOM(DOM paramDOM)
  {
    _dom = paramDOM;
  }
  
  private DOM getDOM(Source paramSource)
    throws TransformerException
  {
    try
    {
      DOM localDOM;
      if (paramSource != null)
      {
        DOMWSFilter localDOMWSFilter;
        if ((_translet != null) && ((_translet instanceof StripFilter))) {
          localDOMWSFilter = new DOMWSFilter(_translet);
        } else {
          localDOMWSFilter = null;
        }
        boolean bool = _translet != null ? _translet.hasIdCall() : false;
        if (_dtmManager == null)
        {
          _dtmManager = _tfactory.createNewDTMManagerInstance();
          _dtmManager.setServicesMechnism(_useServicesMechanism);
        }
        localDOM = (DOM)_dtmManager.getDTM(paramSource, false, localDOMWSFilter, true, false, false, 0, bool);
      }
      else if (_dom != null)
      {
        localDOM = _dom;
        _dom = null;
      }
      else
      {
        return null;
      }
      if (!_isIdentity) {
        _translet.prepassDocument(localDOM);
      }
      return localDOM;
    }
    catch (Exception localException)
    {
      if (_errorListener != null) {
        postErrorToListener(localException.getMessage());
      }
      throw new TransformerException(localException);
    }
  }
  
  protected TransformerFactoryImpl getTransformerFactory()
  {
    return _tfactory;
  }
  
  protected TransletOutputHandlerFactory getTransletOutputHandlerFactory()
  {
    return _tohFactory;
  }
  
  private void transformIdentity(Source paramSource, SerializationHandler paramSerializationHandler)
    throws Exception
  {
    if (paramSource != null) {
      _sourceSystemId = paramSource.getSystemId();
    }
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if ((paramSource instanceof StreamSource))
    {
      localObject1 = (StreamSource)paramSource;
      localObject2 = ((StreamSource)localObject1).getInputStream();
      localObject3 = ((StreamSource)localObject1).getReader();
      XMLReader localXMLReader = _readerManager.getXMLReader();
      try
      {
        try
        {
          localXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", paramSerializationHandler);
          localXMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        }
        catch (SAXException localSAXException1) {}
        localXMLReader.setContentHandler(paramSerializationHandler);
        InputSource localInputSource;
        if (localObject2 != null)
        {
          localInputSource = new InputSource((InputStream)localObject2);
          localInputSource.setSystemId(_sourceSystemId);
        }
        else if (localObject3 != null)
        {
          localInputSource = new InputSource((Reader)localObject3);
          localInputSource.setSystemId(_sourceSystemId);
        }
        else if (_sourceSystemId != null)
        {
          localInputSource = new InputSource(_sourceSystemId);
        }
        else
        {
          ErrorMsg localErrorMsg = new ErrorMsg("JAXP_NO_SOURCE_ERR");
          throw new TransformerException(localErrorMsg.toString());
        }
        localXMLReader.parse(localInputSource);
      }
      finally
      {
        _readerManager.releaseXMLReader(localXMLReader);
      }
    }
    else if ((paramSource instanceof SAXSource))
    {
      localObject1 = (SAXSource)paramSource;
      localObject2 = ((SAXSource)localObject1).getXMLReader();
      localObject3 = ((SAXSource)localObject1).getInputSource();
      int i = 1;
      try
      {
        if (localObject2 == null)
        {
          localObject2 = _readerManager.getXMLReader();
          i = 0;
        }
        try
        {
          ((XMLReader)localObject2).setProperty("http://xml.org/sax/properties/lexical-handler", paramSerializationHandler);
          ((XMLReader)localObject2).setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        }
        catch (SAXException localSAXException2) {}
        ((XMLReader)localObject2).setContentHandler(paramSerializationHandler);
        ((XMLReader)localObject2).parse((InputSource)localObject3);
      }
      finally
      {
        if (i == 0) {
          _readerManager.releaseXMLReader((XMLReader)localObject2);
        }
      }
    }
    else if ((paramSource instanceof StAXSource))
    {
      localObject1 = (StAXSource)paramSource;
      Object localObject4;
      if (((StAXSource)localObject1).getXMLEventReader() != null)
      {
        localObject4 = ((StAXSource)localObject1).getXMLEventReader();
        localObject2 = new StAXEvent2SAX((XMLEventReader)localObject4);
        ((StAXEvent2SAX)localObject2).setContentHandler(paramSerializationHandler);
        ((StAXEvent2SAX)localObject2).parse();
        paramSerializationHandler.flushPending();
      }
      else if (((StAXSource)localObject1).getXMLStreamReader() != null)
      {
        localObject4 = ((StAXSource)localObject1).getXMLStreamReader();
        localObject3 = new StAXStream2SAX((XMLStreamReader)localObject4);
        ((StAXStream2SAX)localObject3).setContentHandler(paramSerializationHandler);
        ((StAXStream2SAX)localObject3).parse();
        paramSerializationHandler.flushPending();
      }
    }
    else if ((paramSource instanceof DOMSource))
    {
      localObject1 = (DOMSource)paramSource;
      new DOM2TO(((DOMSource)localObject1).getNode(), paramSerializationHandler).parse();
    }
    else if ((paramSource instanceof XSLTCSource))
    {
      localObject1 = ((XSLTCSource)paramSource).getDOM(null, _translet);
      ((SAXImpl)localObject1).copy(paramSerializationHandler);
    }
    else
    {
      localObject1 = new ErrorMsg("JAXP_NO_SOURCE_ERR");
      throw new TransformerException(((ErrorMsg)localObject1).toString());
    }
  }
  
  private void transform(Source paramSource, SerializationHandler paramSerializationHandler, String paramString)
    throws TransformerException
  {
    try
    {
      if ((((paramSource instanceof StreamSource)) && (paramSource.getSystemId() == null) && (((StreamSource)paramSource).getInputStream() == null) && (((StreamSource)paramSource).getReader() == null)) || (((paramSource instanceof SAXSource)) && (((SAXSource)paramSource).getInputSource() == null) && (((SAXSource)paramSource).getXMLReader() == null)) || (((paramSource instanceof DOMSource)) && (((DOMSource)paramSource).getNode() == null)))
      {
        DocumentBuilderFactory localDocumentBuilderFactory = FactoryImpl.getDOMFactory(_useServicesMechanism);
        DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
        String str = paramSource.getSystemId();
        paramSource = new DOMSource(localDocumentBuilder.newDocument());
        if (str != null) {
          paramSource.setSystemId(str);
        }
      }
      if (_isIdentity) {
        transformIdentity(paramSource, paramSerializationHandler);
      } else {
        _translet.transform(getDOM(paramSource), paramSerializationHandler);
      }
    }
    catch (TransletException localTransletException)
    {
      if (_errorListener != null) {
        postErrorToListener(localTransletException.getMessage());
      }
      throw new TransformerException(localTransletException);
    }
    catch (RuntimeException localRuntimeException)
    {
      if (_errorListener != null) {
        postErrorToListener(localRuntimeException.getMessage());
      }
      throw new TransformerException(localRuntimeException);
    }
    catch (Exception localException)
    {
      if (_errorListener != null) {
        postErrorToListener(localException.getMessage());
      }
      throw new TransformerException(localException);
    }
    finally
    {
      _dtmManager = null;
    }
    if (_ostream != null)
    {
      try
      {
        _ostream.close();
      }
      catch (IOException localIOException) {}
      _ostream = null;
    }
  }
  
  public ErrorListener getErrorListener()
  {
    return _errorListener;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
    throws IllegalArgumentException
  {
    if (paramErrorListener == null)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "Transformer");
      throw new IllegalArgumentException(localErrorMsg.toString());
    }
    _errorListener = paramErrorListener;
    if (_translet != null) {
      _translet.setMessageHandler(new MessageHandler(_errorListener));
    }
  }
  
  private void postErrorToListener(String paramString)
  {
    try
    {
      _errorListener.error(new TransformerException(paramString));
    }
    catch (TransformerException localTransformerException) {}
  }
  
  private void postWarningToListener(String paramString)
  {
    try
    {
      _errorListener.warning(new TransformerException(paramString));
    }
    catch (TransformerException localTransformerException) {}
  }
  
  public Properties getOutputProperties()
  {
    return (Properties)_properties.clone();
  }
  
  public String getOutputProperty(String paramString)
    throws IllegalArgumentException
  {
    if (!validOutputProperty(paramString))
    {
      ErrorMsg localErrorMsg = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", paramString);
      throw new IllegalArgumentException(localErrorMsg.toString());
    }
    return _properties.getProperty(paramString);
  }
  
  public void setOutputProperties(Properties paramProperties)
    throws IllegalArgumentException
  {
    if (paramProperties != null)
    {
      Enumeration localEnumeration = paramProperties.propertyNames();
      while (localEnumeration.hasMoreElements())
      {
        String str = (String)localEnumeration.nextElement();
        if (!isDefaultProperty(str, paramProperties)) {
          if (validOutputProperty(str))
          {
            _properties.setProperty(str, paramProperties.getProperty(str));
          }
          else
          {
            ErrorMsg localErrorMsg = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", str);
            throw new IllegalArgumentException(localErrorMsg.toString());
          }
        }
      }
    }
    else
    {
      _properties = _propertiesClone;
    }
  }
  
  public void setOutputProperty(String paramString1, String paramString2)
    throws IllegalArgumentException
  {
    if (!validOutputProperty(paramString1))
    {
      ErrorMsg localErrorMsg = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", paramString1);
      throw new IllegalArgumentException(localErrorMsg.toString());
    }
    _properties.setProperty(paramString1, paramString2);
  }
  
  private void transferOutputProperties(AbstractTranslet paramAbstractTranslet)
  {
    if (_properties == null) {
      return;
    }
    Enumeration localEnumeration = _properties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = (String)_properties.get(str1);
      if (str2 != null) {
        if (str1.equals("encoding")) {
          _encoding = str2;
        } else if (str1.equals("method")) {
          _method = str2;
        } else if (str1.equals("doctype-public")) {
          _doctypePublic = str2;
        } else if (str1.equals("doctype-system")) {
          _doctypeSystem = str2;
        } else if (str1.equals("media-type")) {
          _mediaType = str2;
        } else if (str1.equals("standalone")) {
          _standalone = str2;
        } else if (str1.equals("version")) {
          _version = str2;
        } else if (str1.equals("omit-xml-declaration")) {
          _omitHeader = ((str2 != null) && (str2.toLowerCase().equals("yes")));
        } else if (str1.equals("indent")) {
          _indent = ((str2 != null) && (str2.toLowerCase().equals("yes")));
        } else if (str1.equals("{http://xml.apache.org/xslt}indent-amount"))
        {
          if (str2 != null) {
            _indentamount = Integer.parseInt(str2);
          }
        }
        else if (str1.equals("{http://xml.apache.org/xalan}indent-amount"))
        {
          if (str2 != null) {
            _indentamount = Integer.parseInt(str2);
          }
        }
        else if (str1.equals("cdata-section-elements"))
        {
          if (str2 != null)
          {
            _cdata = null;
            StringTokenizer localStringTokenizer = new StringTokenizer(str2);
            while (localStringTokenizer.hasMoreTokens()) {
              paramAbstractTranslet.addCdataElement(localStringTokenizer.nextToken());
            }
          }
        }
        else if ((str1.equals("http://www.oracle.com/xml/is-standalone")) && (str2 != null) && (str2.equals("yes"))) {
          _isStandalone = true;
        }
      }
    }
  }
  
  public void transferOutputProperties(SerializationHandler paramSerializationHandler)
  {
    if (_properties == null) {
      return;
    }
    Object localObject1 = null;
    Object localObject2 = null;
    Enumeration localEnumeration = _properties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = (String)_properties.get(str1);
      if (str2 != null) {
        if (str1.equals("doctype-public"))
        {
          localObject1 = str2;
        }
        else if (str1.equals("doctype-system"))
        {
          localObject2 = str2;
        }
        else if (str1.equals("media-type"))
        {
          paramSerializationHandler.setMediaType(str2);
        }
        else if (str1.equals("standalone"))
        {
          paramSerializationHandler.setStandalone(str2);
        }
        else if (str1.equals("version"))
        {
          paramSerializationHandler.setVersion(str2);
        }
        else if (str1.equals("omit-xml-declaration"))
        {
          paramSerializationHandler.setOmitXMLDeclaration((str2 != null) && (str2.toLowerCase().equals("yes")));
        }
        else if (str1.equals("indent"))
        {
          paramSerializationHandler.setIndent((str2 != null) && (str2.toLowerCase().equals("yes")));
        }
        else if (str1.equals("{http://xml.apache.org/xslt}indent-amount"))
        {
          if (str2 != null) {
            paramSerializationHandler.setIndentAmount(Integer.parseInt(str2));
          }
        }
        else if (str1.equals("{http://xml.apache.org/xalan}indent-amount"))
        {
          if (str2 != null) {
            paramSerializationHandler.setIndentAmount(Integer.parseInt(str2));
          }
        }
        else if (str1.equals("http://www.oracle.com/xml/is-standalone"))
        {
          if ((str2 != null) && (str2.equals("yes"))) {
            paramSerializationHandler.setIsStandalone(true);
          }
        }
        else if ((str1.equals("cdata-section-elements")) && (str2 != null))
        {
          StringTokenizer localStringTokenizer = new StringTokenizer(str2);
          Vector localVector = null;
          while (localStringTokenizer.hasMoreTokens())
          {
            String str3 = localStringTokenizer.nextToken();
            int i = str3.lastIndexOf(':');
            String str4;
            String str5;
            if (i > 0)
            {
              str4 = str3.substring(0, i);
              str5 = str3.substring(i + 1);
            }
            else
            {
              str4 = null;
              str5 = str3;
            }
            if (localVector == null) {
              localVector = new Vector();
            }
            localVector.addElement(str4);
            localVector.addElement(str5);
          }
          paramSerializationHandler.setCdataSectionElements(localVector);
        }
      }
    }
    if ((localObject1 != null) || (localObject2 != null)) {
      paramSerializationHandler.setDoctype((String)localObject2, (String)localObject1);
    }
  }
  
  private Properties createOutputProperties(Properties paramProperties)
  {
    Properties localProperties1 = new Properties();
    setDefaults(localProperties1, "xml");
    Properties localProperties2 = new Properties(localProperties1);
    if (paramProperties != null)
    {
      localObject = paramProperties.propertyNames();
      while (((Enumeration)localObject).hasMoreElements())
      {
        String str = (String)((Enumeration)localObject).nextElement();
        localProperties2.setProperty(str, paramProperties.getProperty(str));
      }
    }
    else
    {
      localProperties2.setProperty("encoding", _translet._encoding);
      if (_translet._method != null) {
        localProperties2.setProperty("method", _translet._method);
      }
    }
    Object localObject = localProperties2.getProperty("method");
    if (localObject != null) {
      if (((String)localObject).equals("html")) {
        setDefaults(localProperties1, "html");
      } else if (((String)localObject).equals("text")) {
        setDefaults(localProperties1, "text");
      }
    }
    return localProperties2;
  }
  
  private void setDefaults(Properties paramProperties, String paramString)
  {
    Properties localProperties = OutputPropertiesFactory.getDefaultMethodProperties(paramString);
    Enumeration localEnumeration = localProperties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      paramProperties.setProperty(str, localProperties.getProperty(str));
    }
  }
  
  private boolean validOutputProperty(String paramString)
  {
    return (paramString.equals("encoding")) || (paramString.equals("method")) || (paramString.equals("indent")) || (paramString.equals("doctype-public")) || (paramString.equals("doctype-system")) || (paramString.equals("cdata-section-elements")) || (paramString.equals("media-type")) || (paramString.equals("omit-xml-declaration")) || (paramString.equals("standalone")) || (paramString.equals("version")) || (paramString.equals("http://www.oracle.com/xml/is-standalone")) || (paramString.charAt(0) == '{');
  }
  
  private boolean isDefaultProperty(String paramString, Properties paramProperties)
  {
    return paramProperties.get(paramString) == null;
  }
  
  public void setParameter(String paramString, Object paramObject)
  {
    if (paramObject == null)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("JAXP_INVALID_SET_PARAM_VALUE", paramString);
      throw new IllegalArgumentException(localErrorMsg.toString());
    }
    if (_isIdentity)
    {
      if (_parameters == null) {
        _parameters = new HashMap();
      }
      _parameters.put(paramString, paramObject);
    }
    else
    {
      _translet.addParameter(paramString, paramObject);
    }
  }
  
  public void clearParameters()
  {
    if ((_isIdentity) && (_parameters != null)) {
      _parameters.clear();
    } else {
      _translet.clearParameters();
    }
  }
  
  public final Object getParameter(String paramString)
  {
    if (_isIdentity) {
      return _parameters != null ? _parameters.get(paramString) : null;
    }
    return _translet.getParameter(paramString);
  }
  
  public URIResolver getURIResolver()
  {
    return _uriResolver;
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    _uriResolver = paramURIResolver;
  }
  
  public DOM retrieveDocument(String paramString1, String paramString2, Translet paramTranslet)
  {
    try
    {
      if (paramString2.length() == 0) {
        paramString2 = paramString1;
      }
      Source localSource = _uriResolver.resolve(paramString2, paramString1);
      if (localSource == null)
      {
        StreamSource localStreamSource = new StreamSource(SystemIDResolver.getAbsoluteURI(paramString2, paramString1));
        return getDOM(localStreamSource);
      }
      return getDOM(localSource);
    }
    catch (TransformerException localTransformerException)
    {
      if (_errorListener != null) {
        postErrorToListener("File not found: " + localTransformerException.getMessage());
      }
    }
    return null;
  }
  
  public void error(TransformerException paramTransformerException)
    throws TransformerException
  {
    Throwable localThrowable = paramTransformerException.getException();
    if (localThrowable != null) {
      System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), localThrowable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("ERROR_MSG", paramTransformerException.getMessageAndLocation()));
    }
    throw paramTransformerException;
  }
  
  public void fatalError(TransformerException paramTransformerException)
    throws TransformerException
  {
    Throwable localThrowable = paramTransformerException.getException();
    if (localThrowable != null) {
      System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), localThrowable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("FATAL_ERR_MSG", paramTransformerException.getMessageAndLocation()));
    }
    throw paramTransformerException;
  }
  
  public void warning(TransformerException paramTransformerException)
    throws TransformerException
  {
    Throwable localThrowable = paramTransformerException.getException();
    if (localThrowable != null) {
      System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), localThrowable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("WARNING_MSG", paramTransformerException.getMessageAndLocation()));
    }
  }
  
  public void reset()
  {
    _method = null;
    _encoding = null;
    _sourceSystemId = null;
    _errorListener = this;
    _uriResolver = null;
    _dom = null;
    _parameters = null;
    _indentNumber = 0;
    setOutputProperties(null);
    _tohFactory = null;
    _ostream = null;
  }
  
  static class MessageHandler
    extends MessageHandler
  {
    private ErrorListener _errorListener;
    
    public MessageHandler(ErrorListener paramErrorListener)
    {
      _errorListener = paramErrorListener;
    }
    
    public void displayMessage(String paramString)
    {
      if (_errorListener == null) {
        System.err.println(paramString);
      } else {
        try
        {
          _errorListener.warning(new TransformerException(paramString));
        }
        catch (TransformerException localTransformerException) {}
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TransformerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */