package javax.xml.xpath;

public abstract class XPathFactory
{
  public static final String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory";
  public static final String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom";
  private static SecuritySupport ss = new SecuritySupport();
  
  protected XPathFactory() {}
  
  public static XPathFactory newInstance()
  {
    try
    {
      return newInstance("http://java.sun.com/jaxp/xpath/dom");
    }
    catch (XPathFactoryConfigurationException localXPathFactoryConfigurationException)
    {
      throw new RuntimeException("XPathFactory#newInstance() failed to create an XPathFactory for the default object model: http://java.sun.com/jaxp/xpath/dom with the XPathFactoryConfigurationException: " + localXPathFactoryConfigurationException.toString());
    }
  }
  
  public static XPathFactory newInstance(String paramString)
    throws XPathFactoryConfigurationException
  {
    if (paramString == null) {
      throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
    }
    if (paramString.length() == 0) {
      throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
    }
    ClassLoader localClassLoader = ss.getContextClassLoader();
    if (localClassLoader == null) {
      localClassLoader = XPathFactory.class.getClassLoader();
    }
    XPathFactory localXPathFactory = new XPathFactoryFinder(localClassLoader).newFactory(paramString);
    if (localXPathFactory == null) {
      throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + paramString);
    }
    return localXPathFactory;
  }
  
  public static XPathFactory newInstance(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws XPathFactoryConfigurationException
  {
    ClassLoader localClassLoader = paramClassLoader;
    if (paramString1 == null) {
      throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
    }
    if (paramString1.length() == 0) {
      throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
    }
    if (localClassLoader == null) {
      localClassLoader = ss.getContextClassLoader();
    }
    XPathFactory localXPathFactory = new XPathFactoryFinder(localClassLoader).createInstance(paramString2);
    if (localXPathFactory == null) {
      throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + paramString1);
    }
    if (localXPathFactory.isObjectModelSupported(paramString1)) {
      return localXPathFactory;
    }
    throw new XPathFactoryConfigurationException("Factory " + paramString2 + " doesn't support given " + paramString1 + " object model");
  }
  
  public abstract boolean isObjectModelSupported(String paramString);
  
  public abstract void setFeature(String paramString, boolean paramBoolean)
    throws XPathFactoryConfigurationException;
  
  public abstract boolean getFeature(String paramString)
    throws XPathFactoryConfigurationException;
  
  public abstract void setXPathVariableResolver(XPathVariableResolver paramXPathVariableResolver);
  
  public abstract void setXPathFunctionResolver(XPathFunctionResolver paramXPathFunctionResolver);
  
  public abstract XPath newXPath();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\xpath\XPathFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */