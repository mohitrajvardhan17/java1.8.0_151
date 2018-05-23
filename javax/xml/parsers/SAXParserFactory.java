package javax.xml.parsers;

import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class SAXParserFactory
{
  private boolean validating = false;
  private boolean namespaceAware = false;
  
  protected SAXParserFactory() {}
  
  public static SAXParserFactory newInstance()
  {
    return (SAXParserFactory)FactoryFinder.find(SAXParserFactory.class, "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
  }
  
  public static SAXParserFactory newInstance(String paramString, ClassLoader paramClassLoader)
  {
    return (SAXParserFactory)FactoryFinder.newInstance(SAXParserFactory.class, paramString, paramClassLoader, false);
  }
  
  public abstract SAXParser newSAXParser()
    throws ParserConfigurationException, SAXException;
  
  public void setNamespaceAware(boolean paramBoolean)
  {
    namespaceAware = paramBoolean;
  }
  
  public void setValidating(boolean paramBoolean)
  {
    validating = paramBoolean;
  }
  
  public boolean isNamespaceAware()
  {
    return namespaceAware;
  }
  
  public boolean isValidating()
  {
    return validating;
  }
  
  public abstract void setFeature(String paramString, boolean paramBoolean)
    throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;
  
  public abstract boolean getFeature(String paramString)
    throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;
  
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\parsers\SAXParserFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */