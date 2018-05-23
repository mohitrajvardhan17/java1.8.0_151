package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

final class ValidatorImpl
  extends Validator
  implements PSVIProvider
{
  private XMLSchemaValidatorComponentManager fComponentManager;
  private ValidatorHandlerImpl fSAXValidatorHelper;
  private DOMValidatorHelper fDOMValidatorHelper;
  private StreamValidatorHelper fStreamValidatorHelper;
  private StAXValidatorHelper fStaxValidatorHelper;
  private boolean fConfigurationChanged = false;
  private boolean fErrorHandlerChanged = false;
  private boolean fResourceResolverChanged = false;
  private static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
  
  public ValidatorImpl(XSGrammarPoolContainer paramXSGrammarPoolContainer)
  {
    fComponentManager = new XMLSchemaValidatorComponentManager(paramXSGrammarPoolContainer);
    setErrorHandler(null);
    setResourceResolver(null);
  }
  
  public void validate(Source paramSource, Result paramResult)
    throws SAXException, IOException
  {
    if ((paramSource instanceof SAXSource))
    {
      if (fSAXValidatorHelper == null) {
        fSAXValidatorHelper = new ValidatorHandlerImpl(fComponentManager);
      }
      fSAXValidatorHelper.validate(paramSource, paramResult);
    }
    else if ((paramSource instanceof DOMSource))
    {
      if (fDOMValidatorHelper == null) {
        fDOMValidatorHelper = new DOMValidatorHelper(fComponentManager);
      }
      fDOMValidatorHelper.validate(paramSource, paramResult);
    }
    else if ((paramSource instanceof StreamSource))
    {
      if (fStreamValidatorHelper == null) {
        fStreamValidatorHelper = new StreamValidatorHelper(fComponentManager);
      }
      fStreamValidatorHelper.validate(paramSource, paramResult);
    }
    else if ((paramSource instanceof StAXSource))
    {
      if (fStaxValidatorHelper == null) {
        fStaxValidatorHelper = new StAXValidatorHelper(fComponentManager);
      }
      fStaxValidatorHelper.validate(paramSource, paramResult);
    }
    else
    {
      if (paramSource == null) {
        throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(), "SourceParameterNull", null));
      }
      throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(), "SourceNotAccepted", new Object[] { paramSource.getClass().getName() }));
    }
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    fErrorHandlerChanged = (paramErrorHandler != null);
    fComponentManager.setErrorHandler(paramErrorHandler);
  }
  
  public ErrorHandler getErrorHandler()
  {
    return fComponentManager.getErrorHandler();
  }
  
  public void setResourceResolver(LSResourceResolver paramLSResourceResolver)
  {
    fResourceResolverChanged = (paramLSResourceResolver != null);
    fComponentManager.setResourceResolver(paramLSResourceResolver);
  }
  
  public LSResourceResolver getResourceResolver()
  {
    return fComponentManager.getResourceResolver();
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      return fComponentManager.getFeature(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      String str2 = localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED ? "feature-not-recognized" : "feature-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      fComponentManager.setFeature(paramString, paramBoolean);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_ALLOWED) {
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fComponentManager.getLocale(), "jaxp-secureprocessing-feature", null));
      }
      String str2;
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        str2 = "feature-not-recognized";
      } else {
        str2 = "feature-not-supported";
      }
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
    fConfigurationChanged = true;
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if ("http://apache.org/xml/properties/dom/current-element-node".equals(paramString)) {
      return fDOMValidatorHelper != null ? fDOMValidatorHelper.getCurrentElement() : null;
    }
    try
    {
      return fComponentManager.getProperty(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      String str2 = localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      fComponentManager.setProperty(paramString, paramObject);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      String str2 = localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
    fConfigurationChanged = true;
  }
  
  public void reset()
  {
    if (fConfigurationChanged)
    {
      fComponentManager.restoreInitialState();
      setErrorHandler(null);
      setResourceResolver(null);
      fConfigurationChanged = false;
      fErrorHandlerChanged = false;
      fResourceResolverChanged = false;
    }
    else
    {
      if (fErrorHandlerChanged)
      {
        setErrorHandler(null);
        fErrorHandlerChanged = false;
      }
      if (fResourceResolverChanged)
      {
        setResourceResolver(null);
        fResourceResolverChanged = false;
      }
    }
  }
  
  public ElementPSVI getElementPSVI()
  {
    return fSAXValidatorHelper != null ? fSAXValidatorHelper.getElementPSVI() : null;
  }
  
  public AttributePSVI getAttributePSVI(int paramInt)
  {
    return fSAXValidatorHelper != null ? fSAXValidatorHelper.getAttributePSVI(paramInt) : null;
  }
  
  public AttributePSVI getAttributePSVIByName(String paramString1, String paramString2)
  {
    return fSAXValidatorHelper != null ? fSAXValidatorHelper.getAttributePSVIByName(paramString1, paramString2) : null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ValidatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */