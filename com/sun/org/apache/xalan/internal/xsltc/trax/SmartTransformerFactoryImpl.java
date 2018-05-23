package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.io.PrintStream;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.XMLFilter;

public class SmartTransformerFactoryImpl
  extends SAXTransformerFactory
{
  private static final String CLASS_NAME = "SmartTransformerFactoryImpl";
  private SAXTransformerFactory _xsltcFactory = null;
  private SAXTransformerFactory _xalanFactory = null;
  private SAXTransformerFactory _currFactory = null;
  private ErrorListener _errorlistener = null;
  private URIResolver _uriresolver = null;
  private boolean featureSecureProcessing = false;
  
  public SmartTransformerFactoryImpl() {}
  
  private void createXSLTCTransformerFactory()
  {
    _xsltcFactory = new TransformerFactoryImpl();
    _currFactory = _xsltcFactory;
  }
  
  private void createXalanTransformerFactory()
  {
    String str = "com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.";
    try
    {
      Class localClass = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl", true);
      _xalanFactory = ((SAXTransformerFactory)localClass.newInstance());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
    }
    catch (InstantiationException localInstantiationException)
    {
      System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
    }
    _currFactory = _xalanFactory;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
    throws IllegalArgumentException
  {
    _errorlistener = paramErrorListener;
  }
  
  public ErrorListener getErrorListener()
  {
    return _errorlistener;
  }
  
  public Object getAttribute(String paramString)
    throws IllegalArgumentException
  {
    if ((paramString.equals("translet-name")) || (paramString.equals("debug")))
    {
      if (_xsltcFactory == null) {
        createXSLTCTransformerFactory();
      }
      return _xsltcFactory.getAttribute(paramString);
    }
    if (_xalanFactory == null) {
      createXalanTransformerFactory();
    }
    return _xalanFactory.getAttribute(paramString);
  }
  
  public void setAttribute(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    if ((paramString.equals("translet-name")) || (paramString.equals("debug")))
    {
      if (_xsltcFactory == null) {
        createXSLTCTransformerFactory();
      }
      _xsltcFactory.setAttribute(paramString, paramObject);
    }
    else
    {
      if (_xalanFactory == null) {
        createXalanTransformerFactory();
      }
      _xalanFactory.setAttribute(paramString, paramObject);
    }
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws TransformerConfigurationException
  {
    if (paramString == null)
    {
      localErrorMsg = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
      throw new NullPointerException(localErrorMsg.toString());
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
    {
      featureSecureProcessing = paramBoolean;
      return;
    }
    ErrorMsg localErrorMsg = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", paramString);
    throw new TransformerConfigurationException(localErrorMsg.toString());
  }
  
  public boolean getFeature(String paramString)
  {
    String[] arrayOfString = { "http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature" };
    if (paramString == null)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
      throw new NullPointerException(localErrorMsg.toString());
    }
    for (int i = 0; i < arrayOfString.length; i++) {
      if (paramString.equals(arrayOfString[i])) {
        return true;
      }
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      return featureSecureProcessing;
    }
    return false;
  }
  
  public URIResolver getURIResolver()
  {
    return _uriresolver;
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    _uriresolver = paramURIResolver;
  }
  
  public Source getAssociatedStylesheet(Source paramSource, String paramString1, String paramString2, String paramString3)
    throws TransformerConfigurationException
  {
    if (_currFactory == null) {
      createXSLTCTransformerFactory();
    }
    return _currFactory.getAssociatedStylesheet(paramSource, paramString1, paramString2, paramString3);
  }
  
  public Transformer newTransformer()
    throws TransformerConfigurationException
  {
    if (_xalanFactory == null) {
      createXalanTransformerFactory();
    }
    if (_errorlistener != null) {
      _xalanFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xalanFactory.setURIResolver(_uriresolver);
    }
    _currFactory = _xalanFactory;
    return _currFactory.newTransformer();
  }
  
  public Transformer newTransformer(Source paramSource)
    throws TransformerConfigurationException
  {
    if (_xalanFactory == null) {
      createXalanTransformerFactory();
    }
    if (_errorlistener != null) {
      _xalanFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xalanFactory.setURIResolver(_uriresolver);
    }
    _currFactory = _xalanFactory;
    return _currFactory.newTransformer(paramSource);
  }
  
  public Templates newTemplates(Source paramSource)
    throws TransformerConfigurationException
  {
    if (_xsltcFactory == null) {
      createXSLTCTransformerFactory();
    }
    if (_errorlistener != null) {
      _xsltcFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xsltcFactory.setURIResolver(_uriresolver);
    }
    _currFactory = _xsltcFactory;
    return _currFactory.newTemplates(paramSource);
  }
  
  public TemplatesHandler newTemplatesHandler()
    throws TransformerConfigurationException
  {
    if (_xsltcFactory == null) {
      createXSLTCTransformerFactory();
    }
    if (_errorlistener != null) {
      _xsltcFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xsltcFactory.setURIResolver(_uriresolver);
    }
    return _xsltcFactory.newTemplatesHandler();
  }
  
  public TransformerHandler newTransformerHandler()
    throws TransformerConfigurationException
  {
    if (_xalanFactory == null) {
      createXalanTransformerFactory();
    }
    if (_errorlistener != null) {
      _xalanFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xalanFactory.setURIResolver(_uriresolver);
    }
    return _xalanFactory.newTransformerHandler();
  }
  
  public TransformerHandler newTransformerHandler(Source paramSource)
    throws TransformerConfigurationException
  {
    if (_xalanFactory == null) {
      createXalanTransformerFactory();
    }
    if (_errorlistener != null) {
      _xalanFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xalanFactory.setURIResolver(_uriresolver);
    }
    return _xalanFactory.newTransformerHandler(paramSource);
  }
  
  public TransformerHandler newTransformerHandler(Templates paramTemplates)
    throws TransformerConfigurationException
  {
    if (_xsltcFactory == null) {
      createXSLTCTransformerFactory();
    }
    if (_errorlistener != null) {
      _xsltcFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xsltcFactory.setURIResolver(_uriresolver);
    }
    return _xsltcFactory.newTransformerHandler(paramTemplates);
  }
  
  public XMLFilter newXMLFilter(Source paramSource)
    throws TransformerConfigurationException
  {
    if (_xsltcFactory == null) {
      createXSLTCTransformerFactory();
    }
    if (_errorlistener != null) {
      _xsltcFactory.setErrorListener(_errorlistener);
    }
    if (_uriresolver != null) {
      _xsltcFactory.setURIResolver(_uriresolver);
    }
    Templates localTemplates = _xsltcFactory.newTemplates(paramSource);
    if (localTemplates == null) {
      return null;
    }
    return newXMLFilter(localTemplates);
  }
  
  public XMLFilter newXMLFilter(Templates paramTemplates)
    throws TransformerConfigurationException
  {
    try
    {
      return new TrAXFilter(paramTemplates);
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      if (_xsltcFactory == null) {
        createXSLTCTransformerFactory();
      }
      ErrorListener localErrorListener = _xsltcFactory.getErrorListener();
      if (localErrorListener != null) {
        try
        {
          localErrorListener.fatalError(localTransformerConfigurationException);
          return null;
        }
        catch (TransformerException localTransformerException)
        {
          new TransformerConfigurationException(localTransformerException);
        }
      }
      throw localTransformerConfigurationException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SmartTransformerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */