package javax.xml.transform;

public abstract class TransformerFactory
{
  protected TransformerFactory() {}
  
  public static TransformerFactory newInstance()
    throws TransformerFactoryConfigurationError
  {
    return (TransformerFactory)FactoryFinder.find(TransformerFactory.class, "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
  }
  
  public static TransformerFactory newInstance(String paramString, ClassLoader paramClassLoader)
    throws TransformerFactoryConfigurationError
  {
    return (TransformerFactory)FactoryFinder.newInstance(TransformerFactory.class, paramString, paramClassLoader, false, false);
  }
  
  public abstract Transformer newTransformer(Source paramSource)
    throws TransformerConfigurationException;
  
  public abstract Transformer newTransformer()
    throws TransformerConfigurationException;
  
  public abstract Templates newTemplates(Source paramSource)
    throws TransformerConfigurationException;
  
  public abstract Source getAssociatedStylesheet(Source paramSource, String paramString1, String paramString2, String paramString3)
    throws TransformerConfigurationException;
  
  public abstract void setURIResolver(URIResolver paramURIResolver);
  
  public abstract URIResolver getURIResolver();
  
  public abstract void setFeature(String paramString, boolean paramBoolean)
    throws TransformerConfigurationException;
  
  public abstract boolean getFeature(String paramString);
  
  public abstract void setAttribute(String paramString, Object paramObject);
  
  public abstract Object getAttribute(String paramString);
  
  public abstract void setErrorListener(ErrorListener paramErrorListener);
  
  public abstract ErrorListener getErrorListener();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\TransformerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */