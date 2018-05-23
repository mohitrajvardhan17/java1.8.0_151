package javax.xml.parsers;

import javax.xml.validation.Schema;

public abstract class DocumentBuilderFactory
{
  private boolean validating = false;
  private boolean namespaceAware = false;
  private boolean whitespace = false;
  private boolean expandEntityRef = true;
  private boolean ignoreComments = false;
  private boolean coalescing = false;
  
  protected DocumentBuilderFactory() {}
  
  public static DocumentBuilderFactory newInstance()
  {
    return (DocumentBuilderFactory)FactoryFinder.find(DocumentBuilderFactory.class, "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
  }
  
  public static DocumentBuilderFactory newInstance(String paramString, ClassLoader paramClassLoader)
  {
    return (DocumentBuilderFactory)FactoryFinder.newInstance(DocumentBuilderFactory.class, paramString, paramClassLoader, false);
  }
  
  public abstract DocumentBuilder newDocumentBuilder()
    throws ParserConfigurationException;
  
  public void setNamespaceAware(boolean paramBoolean)
  {
    namespaceAware = paramBoolean;
  }
  
  public void setValidating(boolean paramBoolean)
  {
    validating = paramBoolean;
  }
  
  public void setIgnoringElementContentWhitespace(boolean paramBoolean)
  {
    whitespace = paramBoolean;
  }
  
  public void setExpandEntityReferences(boolean paramBoolean)
  {
    expandEntityRef = paramBoolean;
  }
  
  public void setIgnoringComments(boolean paramBoolean)
  {
    ignoreComments = paramBoolean;
  }
  
  public void setCoalescing(boolean paramBoolean)
  {
    coalescing = paramBoolean;
  }
  
  public boolean isNamespaceAware()
  {
    return namespaceAware;
  }
  
  public boolean isValidating()
  {
    return validating;
  }
  
  public boolean isIgnoringElementContentWhitespace()
  {
    return whitespace;
  }
  
  public boolean isExpandEntityReferences()
  {
    return expandEntityRef;
  }
  
  public boolean isIgnoringComments()
  {
    return ignoreComments;
  }
  
  public boolean isCoalescing()
  {
    return coalescing;
  }
  
  public abstract void setAttribute(String paramString, Object paramObject)
    throws IllegalArgumentException;
  
  public abstract Object getAttribute(String paramString)
    throws IllegalArgumentException;
  
  public abstract void setFeature(String paramString, boolean paramBoolean)
    throws ParserConfigurationException;
  
  public abstract boolean getFeature(String paramString)
    throws ParserConfigurationException;
  
  public Schema getSchema()
  {
    throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\"");
  }
  
  public void setSchema(Schema paramSchema)
  {
    throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\"");
  }
  
  public void setXIncludeAware(boolean paramBoolean)
  {
    if (paramBoolean) {
      throw new UnsupportedOperationException(" setXIncludeAware is not supported on this JAXP implementation or earlier: " + getClass());
    }
  }
  
  public boolean isXIncludeAware()
  {
    throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\"");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\parsers\DocumentBuilderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */