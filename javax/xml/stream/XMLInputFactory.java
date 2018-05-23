package javax.xml.stream;

import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

public abstract class XMLInputFactory
{
  public static final String IS_NAMESPACE_AWARE = "javax.xml.stream.isNamespaceAware";
  public static final String IS_VALIDATING = "javax.xml.stream.isValidating";
  public static final String IS_COALESCING = "javax.xml.stream.isCoalescing";
  public static final String IS_REPLACING_ENTITY_REFERENCES = "javax.xml.stream.isReplacingEntityReferences";
  public static final String IS_SUPPORTING_EXTERNAL_ENTITIES = "javax.xml.stream.isSupportingExternalEntities";
  public static final String SUPPORT_DTD = "javax.xml.stream.supportDTD";
  public static final String REPORTER = "javax.xml.stream.reporter";
  public static final String RESOLVER = "javax.xml.stream.resolver";
  public static final String ALLOCATOR = "javax.xml.stream.allocator";
  static final String DEFAULIMPL = "com.sun.xml.internal.stream.XMLInputFactoryImpl";
  
  protected XMLInputFactory() {}
  
  public static XMLInputFactory newInstance()
    throws FactoryConfigurationError
  {
    return (XMLInputFactory)FactoryFinder.find(XMLInputFactory.class, "com.sun.xml.internal.stream.XMLInputFactoryImpl");
  }
  
  public static XMLInputFactory newFactory()
    throws FactoryConfigurationError
  {
    return (XMLInputFactory)FactoryFinder.find(XMLInputFactory.class, "com.sun.xml.internal.stream.XMLInputFactoryImpl");
  }
  
  /**
   * @deprecated
   */
  public static XMLInputFactory newInstance(String paramString, ClassLoader paramClassLoader)
    throws FactoryConfigurationError
  {
    return (XMLInputFactory)FactoryFinder.find(XMLInputFactory.class, paramString, paramClassLoader, null);
  }
  
  public static XMLInputFactory newFactory(String paramString, ClassLoader paramClassLoader)
    throws FactoryConfigurationError
  {
    return (XMLInputFactory)FactoryFinder.find(XMLInputFactory.class, paramString, paramClassLoader, null);
  }
  
  public abstract XMLStreamReader createXMLStreamReader(Reader paramReader)
    throws XMLStreamException;
  
  public abstract XMLStreamReader createXMLStreamReader(Source paramSource)
    throws XMLStreamException;
  
  public abstract XMLStreamReader createXMLStreamReader(InputStream paramInputStream)
    throws XMLStreamException;
  
  public abstract XMLStreamReader createXMLStreamReader(InputStream paramInputStream, String paramString)
    throws XMLStreamException;
  
  public abstract XMLStreamReader createXMLStreamReader(String paramString, InputStream paramInputStream)
    throws XMLStreamException;
  
  public abstract XMLStreamReader createXMLStreamReader(String paramString, Reader paramReader)
    throws XMLStreamException;
  
  public abstract XMLEventReader createXMLEventReader(Reader paramReader)
    throws XMLStreamException;
  
  public abstract XMLEventReader createXMLEventReader(String paramString, Reader paramReader)
    throws XMLStreamException;
  
  public abstract XMLEventReader createXMLEventReader(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException;
  
  public abstract XMLEventReader createXMLEventReader(Source paramSource)
    throws XMLStreamException;
  
  public abstract XMLEventReader createXMLEventReader(InputStream paramInputStream)
    throws XMLStreamException;
  
  public abstract XMLEventReader createXMLEventReader(InputStream paramInputStream, String paramString)
    throws XMLStreamException;
  
  public abstract XMLEventReader createXMLEventReader(String paramString, InputStream paramInputStream)
    throws XMLStreamException;
  
  public abstract XMLStreamReader createFilteredReader(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter)
    throws XMLStreamException;
  
  public abstract XMLEventReader createFilteredReader(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter)
    throws XMLStreamException;
  
  public abstract XMLResolver getXMLResolver();
  
  public abstract void setXMLResolver(XMLResolver paramXMLResolver);
  
  public abstract XMLReporter getXMLReporter();
  
  public abstract void setXMLReporter(XMLReporter paramXMLReporter);
  
  public abstract void setProperty(String paramString, Object paramObject)
    throws IllegalArgumentException;
  
  public abstract Object getProperty(String paramString)
    throws IllegalArgumentException;
  
  public abstract boolean isPropertySupported(String paramString);
  
  public abstract void setEventAllocator(XMLEventAllocator paramXMLEventAllocator);
  
  public abstract XMLEventAllocator getEventAllocator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\XMLInputFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */