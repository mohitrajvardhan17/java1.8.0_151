package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMInputSource;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXInputSource;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.StAXInputSource;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.State;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.State;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

public final class XMLSchemaFactory
  extends SchemaFactory
{
  private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private final XMLSchemaLoader fXMLSchemaLoader = new XMLSchemaLoader();
  private ErrorHandler fErrorHandler;
  private LSResourceResolver fLSResourceResolver;
  private final DOMEntityResolverWrapper fDOMEntityResolverWrapper;
  private ErrorHandlerWrapper fErrorHandlerWrapper;
  private XMLSecurityManager fSecurityManager;
  private XMLSecurityPropertyManager fSecurityPropertyMgr;
  private XMLGrammarPoolWrapper fXMLGrammarPoolWrapper;
  private final boolean fUseServicesMechanism;
  
  public XMLSchemaFactory()
  {
    this(true);
  }
  
  public static XMLSchemaFactory newXMLSchemaFactoryNoServiceLoader()
  {
    return new XMLSchemaFactory(false);
  }
  
  private XMLSchemaFactory(boolean paramBoolean)
  {
    fUseServicesMechanism = paramBoolean;
    fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
    fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
    fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
    fXMLSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
    fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", fXMLGrammarPoolWrapper);
    fXMLSchemaLoader.setEntityResolver(fDOMEntityResolverWrapper);
    fXMLSchemaLoader.setErrorHandler(fErrorHandlerWrapper);
    fSecurityManager = new XMLSecurityManager(true);
    fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
    fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
  }
  
  public boolean isSchemaLanguageSupported(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "SchemaLanguageNull", null));
    }
    if (paramString.length() == 0) {
      throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "SchemaLanguageLengthZero", null));
    }
    return paramString.equals("http://www.w3.org/2001/XMLSchema");
  }
  
  public LSResourceResolver getResourceResolver()
  {
    return fLSResourceResolver;
  }
  
  public void setResourceResolver(LSResourceResolver paramLSResourceResolver)
  {
    fLSResourceResolver = paramLSResourceResolver;
    fDOMEntityResolverWrapper.setEntityResolver(paramLSResourceResolver);
    fXMLSchemaLoader.setEntityResolver(fDOMEntityResolverWrapper);
  }
  
  public ErrorHandler getErrorHandler()
  {
    return fErrorHandler;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    fErrorHandler = paramErrorHandler;
    fErrorHandlerWrapper.setErrorHandler(paramErrorHandler != null ? paramErrorHandler : DraconianErrorHandler.getInstance());
    fXMLSchemaLoader.setErrorHandler(fErrorHandlerWrapper);
  }
  
  public Schema newSchema(Source[] paramArrayOfSource)
    throws SAXException
  {
    XMLGrammarPoolImplExtension localXMLGrammarPoolImplExtension = new XMLGrammarPoolImplExtension();
    fXMLGrammarPoolWrapper.setGrammarPool(localXMLGrammarPoolImplExtension);
    XMLInputSource[] arrayOfXMLInputSource = new XMLInputSource[paramArrayOfSource.length];
    Object localObject2;
    for (int i = 0; i < paramArrayOfSource.length; i++)
    {
      localObject1 = paramArrayOfSource[i];
      Object localObject3;
      String str;
      if ((localObject1 instanceof StreamSource))
      {
        localObject2 = (StreamSource)localObject1;
        localObject3 = ((StreamSource)localObject2).getPublicId();
        str = ((StreamSource)localObject2).getSystemId();
        InputStream localInputStream = ((StreamSource)localObject2).getInputStream();
        Reader localReader = ((StreamSource)localObject2).getReader();
        arrayOfXMLInputSource[i] = new XMLInputSource((String)localObject3, str, null);
        arrayOfXMLInputSource[i].setByteStream(localInputStream);
        arrayOfXMLInputSource[i].setCharacterStream(localReader);
      }
      else if ((localObject1 instanceof SAXSource))
      {
        localObject2 = (SAXSource)localObject1;
        localObject3 = ((SAXSource)localObject2).getInputSource();
        if (localObject3 == null) {
          throw new SAXException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "SAXSourceNullInputSource", null));
        }
        arrayOfXMLInputSource[i] = new SAXInputSource(((SAXSource)localObject2).getXMLReader(), (InputSource)localObject3);
      }
      else if ((localObject1 instanceof DOMSource))
      {
        localObject2 = (DOMSource)localObject1;
        localObject3 = ((DOMSource)localObject2).getNode();
        str = ((DOMSource)localObject2).getSystemId();
        arrayOfXMLInputSource[i] = new DOMInputSource((Node)localObject3, str);
      }
      else if ((localObject1 instanceof StAXSource))
      {
        localObject2 = (StAXSource)localObject1;
        localObject3 = ((StAXSource)localObject2).getXMLEventReader();
        if (localObject3 != null) {
          arrayOfXMLInputSource[i] = new StAXInputSource((XMLEventReader)localObject3);
        } else {
          arrayOfXMLInputSource[i] = new StAXInputSource(((StAXSource)localObject2).getXMLStreamReader());
        }
      }
      else
      {
        if (localObject1 == null) {
          throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "SchemaSourceArrayMemberNull", null));
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "SchemaFactorySourceUnrecognized", new Object[] { localObject1.getClass().getName() }));
      }
    }
    try
    {
      fXMLSchemaLoader.loadGrammar(arrayOfXMLInputSource);
    }
    catch (XNIException localXNIException)
    {
      throw Util.toSAXException(localXNIException);
    }
    catch (IOException localIOException)
    {
      localObject1 = new SAXParseException(localIOException.getMessage(), null, localIOException);
      fErrorHandler.error((SAXParseException)localObject1);
      throw ((Throwable)localObject1);
    }
    fXMLGrammarPoolWrapper.setGrammarPool(null);
    int j = localXMLGrammarPoolImplExtension.getGrammarCount();
    Object localObject1 = null;
    if (j > 1)
    {
      localObject1 = new XMLSchema(new ReadOnlyGrammarPool(localXMLGrammarPoolImplExtension));
    }
    else if (j == 1)
    {
      localObject2 = localXMLGrammarPoolImplExtension.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
      localObject1 = new SimpleXMLSchema(localObject2[0]);
    }
    else
    {
      localObject1 = new EmptyXMLSchema();
    }
    propagateFeatures((AbstractXMLSchema)localObject1);
    propagateProperties((AbstractXMLSchema)localObject1);
    return (Schema)localObject1;
  }
  
  public Schema newSchema()
    throws SAXException
  {
    WeakReferenceXMLSchema localWeakReferenceXMLSchema = new WeakReferenceXMLSchema();
    propagateFeatures(localWeakReferenceXMLSchema);
    propagateProperties(localWeakReferenceXMLSchema);
    return localWeakReferenceXMLSchema;
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "FeatureNameNull", null));
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      return (fSecurityManager != null) && (fSecurityManager.isSecureProcessing());
    }
    try
    {
      return fXMLSchemaLoader.getFeature(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[] { str }));
    }
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "ProperyNameNull", null));
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      return fSecurityManager;
    }
    if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { paramString }));
    }
    try
    {
      return fXMLSchemaLoader.getProperty(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { str }));
    }
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "FeatureNameNull", null));
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
    {
      if ((System.getSecurityManager() != null) && (!paramBoolean)) {
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
      }
      fSecurityManager.setSecureProcessing(paramBoolean);
      if ((paramBoolean) && (Constants.IS_JDK8_OR_ABOVE))
      {
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
      }
      fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
      return;
    }
    if ((paramString.equals("http://www.oracle.com/feature/use-service-mechanism")) && (System.getSecurityManager() != null)) {
      return;
    }
    try
    {
      fXMLSchemaLoader.setFeature(paramString, paramBoolean);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[] { str }));
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "ProperyNameNull", null));
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
    {
      fSecurityManager = XMLSecurityManager.convert(paramObject, fSecurityManager);
      fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
      return;
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      if (paramObject == null) {
        fSecurityPropertyMgr = new XMLSecurityPropertyManager();
      } else {
        fSecurityPropertyMgr = ((XMLSecurityPropertyManager)paramObject);
      }
      fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
      return;
    }
    if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { paramString }));
    }
    try
    {
      if (((fSecurityManager == null) || (!fSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject))) && ((fSecurityPropertyMgr == null) || (!fSecurityPropertyMgr.setValue(paramString, XMLSecurityPropertyManager.State.APIPROPERTY, paramObject)))) {
        fXMLSchemaLoader.setProperty(paramString, paramObject);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { str }));
    }
  }
  
  private void propagateFeatures(AbstractXMLSchema paramAbstractXMLSchema)
  {
    paramAbstractXMLSchema.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", (fSecurityManager != null) && (fSecurityManager.isSecureProcessing()));
    paramAbstractXMLSchema.setFeature("http://www.oracle.com/feature/use-service-mechanism", fUseServicesMechanism);
    String[] arrayOfString = fXMLSchemaLoader.getRecognizedFeatures();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      boolean bool = fXMLSchemaLoader.getFeature(arrayOfString[i]);
      paramAbstractXMLSchema.setFeature(arrayOfString[i], bool);
    }
  }
  
  private void propagateProperties(AbstractXMLSchema paramAbstractXMLSchema)
  {
    String[] arrayOfString = fXMLSchemaLoader.getRecognizedProperties();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      Object localObject = fXMLSchemaLoader.getProperty(arrayOfString[i]);
      paramAbstractXMLSchema.setProperty(arrayOfString[i], localObject);
    }
  }
  
  static class XMLGrammarPoolImplExtension
    extends XMLGrammarPoolImpl
  {
    public XMLGrammarPoolImplExtension() {}
    
    public XMLGrammarPoolImplExtension(int paramInt)
    {
      super();
    }
    
    int getGrammarCount()
    {
      return fGrammarCount;
    }
  }
  
  static class XMLGrammarPoolWrapper
    implements XMLGrammarPool
  {
    private XMLGrammarPool fGrammarPool;
    
    XMLGrammarPoolWrapper() {}
    
    public Grammar[] retrieveInitialGrammarSet(String paramString)
    {
      return fGrammarPool.retrieveInitialGrammarSet(paramString);
    }
    
    public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar)
    {
      fGrammarPool.cacheGrammars(paramString, paramArrayOfGrammar);
    }
    
    public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      return fGrammarPool.retrieveGrammar(paramXMLGrammarDescription);
    }
    
    public void lockPool()
    {
      fGrammarPool.lockPool();
    }
    
    public void unlockPool()
    {
      fGrammarPool.unlockPool();
    }
    
    public void clear()
    {
      fGrammarPool.clear();
    }
    
    void setGrammarPool(XMLGrammarPool paramXMLGrammarPool)
    {
      fGrammarPool = paramXMLGrammarPool;
    }
    
    XMLGrammarPool getGrammarPool()
    {
      return fGrammarPool;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\XMLSchemaFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */