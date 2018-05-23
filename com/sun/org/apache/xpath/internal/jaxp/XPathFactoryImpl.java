package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xalan.internal.utils.FeatureManager.Feature;
import com.sun.org.apache.xalan.internal.utils.FeaturePropertyBase.State;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

public class XPathFactoryImpl
  extends XPathFactory
{
  private static final String CLASS_NAME = "XPathFactoryImpl";
  private XPathFunctionResolver xPathFunctionResolver = null;
  private XPathVariableResolver xPathVariableResolver = null;
  private boolean _isNotSecureProcessing = true;
  private boolean _isSecureMode = false;
  private boolean _useServicesMechanism = true;
  private final FeatureManager _featureManager = new FeatureManager();
  
  public XPathFactoryImpl()
  {
    this(true);
  }
  
  public static XPathFactory newXPathFactoryNoServiceLoader()
  {
    return new XPathFactoryImpl(false);
  }
  
  public XPathFactoryImpl(boolean paramBoolean)
  {
    if (System.getSecurityManager() != null)
    {
      _isSecureMode = true;
      _isNotSecureProcessing = false;
      _featureManager.setValue(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION, FeaturePropertyBase.State.FSP, "false");
    }
    _useServicesMechanism = paramBoolean;
  }
  
  public boolean isObjectModelSupported(String paramString)
  {
    String str;
    if (paramString == null)
    {
      str = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_NULL", new Object[] { getClass().getName() });
      throw new NullPointerException(str);
    }
    if (paramString.length() == 0)
    {
      str = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_EMPTY", new Object[] { getClass().getName() });
      throw new IllegalArgumentException(str);
    }
    return paramString.equals("http://java.sun.com/jaxp/xpath/dom");
  }
  
  public XPath newXPath()
  {
    return new XPathImpl(xPathVariableResolver, xPathFunctionResolver, !_isNotSecureProcessing, _useServicesMechanism, _featureManager);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XPathFactoryConfigurationException
  {
    if (paramString == null)
    {
      str = XSLMessages.createXPATHMessage("ER_FEATURE_NAME_NULL", new Object[] { "XPathFactoryImpl", new Boolean(paramBoolean) });
      throw new NullPointerException(str);
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
    {
      if ((_isSecureMode) && (!paramBoolean))
      {
        str = XSLMessages.createXPATHMessage("ER_SECUREPROCESSING_FEATURE", new Object[] { paramString, "XPathFactoryImpl", new Boolean(paramBoolean) });
        throw new XPathFactoryConfigurationException(str);
      }
      _isNotSecureProcessing = (!paramBoolean);
      if ((paramBoolean) && (_featureManager != null)) {
        _featureManager.setValue(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION, FeaturePropertyBase.State.FSP, "false");
      }
      return;
    }
    if (paramString.equals("http://www.oracle.com/feature/use-service-mechanism"))
    {
      if (!_isSecureMode) {
        _useServicesMechanism = paramBoolean;
      }
      return;
    }
    if ((_featureManager != null) && (_featureManager.setValue(paramString, FeaturePropertyBase.State.APIPROPERTY, paramBoolean))) {
      return;
    }
    String str = XSLMessages.createXPATHMessage("ER_FEATURE_UNKNOWN", new Object[] { paramString, "XPathFactoryImpl", new Boolean(paramBoolean) });
    throw new XPathFactoryConfigurationException(str);
  }
  
  public boolean getFeature(String paramString)
    throws XPathFactoryConfigurationException
  {
    if (paramString == null)
    {
      str1 = XSLMessages.createXPATHMessage("ER_GETTING_NULL_FEATURE", new Object[] { "XPathFactoryImpl" });
      throw new NullPointerException(str1);
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      return !_isNotSecureProcessing;
    }
    if (paramString.equals("http://www.oracle.com/feature/use-service-mechanism")) {
      return _useServicesMechanism;
    }
    String str1 = _featureManager != null ? _featureManager.getValueAsString(paramString) : null;
    if (str1 != null) {
      return _featureManager.isFeatureEnabled(paramString);
    }
    String str2 = XSLMessages.createXPATHMessage("ER_GETTING_UNKNOWN_FEATURE", new Object[] { paramString, "XPathFactoryImpl" });
    throw new XPathFactoryConfigurationException(str2);
  }
  
  public void setXPathFunctionResolver(XPathFunctionResolver paramXPathFunctionResolver)
  {
    if (paramXPathFunctionResolver == null)
    {
      String str = XSLMessages.createXPATHMessage("ER_NULL_XPATH_FUNCTION_RESOLVER", new Object[] { "XPathFactoryImpl" });
      throw new NullPointerException(str);
    }
    xPathFunctionResolver = paramXPathFunctionResolver;
  }
  
  public void setXPathVariableResolver(XPathVariableResolver paramXPathVariableResolver)
  {
    if (paramXPathVariableResolver == null)
    {
      String str = XSLMessages.createXPATHMessage("ER_NULL_XPATH_VARIABLE_RESOLVER", new Object[] { "XPathFactoryImpl" });
      throw new NullPointerException(str);
    }
    xPathVariableResolver = paramXPathVariableResolver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\XPathFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */