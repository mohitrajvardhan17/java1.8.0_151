package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLStreamFilterImpl;
import com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class XMLInputFactoryImpl
  extends XMLInputFactory
{
  private PropertyManager fPropertyManager = new PropertyManager(1);
  private static final boolean DEBUG = false;
  private XMLStreamReaderImpl fTempReader = null;
  boolean fPropertyChanged = false;
  boolean fReuseInstance = false;
  
  public XMLInputFactoryImpl() {}
  
  void initEventReader()
  {
    fPropertyChanged = true;
  }
  
  public XMLEventReader createXMLEventReader(InputStream paramInputStream)
    throws XMLStreamException
  {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramInputStream));
  }
  
  public XMLEventReader createXMLEventReader(Reader paramReader)
    throws XMLStreamException
  {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramReader));
  }
  
  public XMLEventReader createXMLEventReader(Source paramSource)
    throws XMLStreamException
  {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramSource));
  }
  
  public XMLEventReader createXMLEventReader(String paramString, InputStream paramInputStream)
    throws XMLStreamException
  {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramString, paramInputStream));
  }
  
  public XMLEventReader createXMLEventReader(InputStream paramInputStream, String paramString)
    throws XMLStreamException
  {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramInputStream, paramString));
  }
  
  public XMLEventReader createXMLEventReader(String paramString, Reader paramReader)
    throws XMLStreamException
  {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramString, paramReader));
  }
  
  public XMLEventReader createXMLEventReader(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    return new XMLEventReaderImpl(paramXMLStreamReader);
  }
  
  public XMLStreamReader createXMLStreamReader(InputStream paramInputStream)
    throws XMLStreamException
  {
    XMLInputSource localXMLInputSource = new XMLInputSource(null, null, null, paramInputStream, null);
    return getXMLStreamReaderImpl(localXMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(Reader paramReader)
    throws XMLStreamException
  {
    XMLInputSource localXMLInputSource = new XMLInputSource(null, null, null, paramReader, null);
    return getXMLStreamReaderImpl(localXMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(String paramString, Reader paramReader)
    throws XMLStreamException
  {
    XMLInputSource localXMLInputSource = new XMLInputSource(null, paramString, null, paramReader, null);
    return getXMLStreamReaderImpl(localXMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(Source paramSource)
    throws XMLStreamException
  {
    return new XMLStreamReaderImpl(jaxpSourcetoXMLInputSource(paramSource), new PropertyManager(fPropertyManager));
  }
  
  public XMLStreamReader createXMLStreamReader(String paramString, InputStream paramInputStream)
    throws XMLStreamException
  {
    XMLInputSource localXMLInputSource = new XMLInputSource(null, paramString, null, paramInputStream, null);
    return getXMLStreamReaderImpl(localXMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(InputStream paramInputStream, String paramString)
    throws XMLStreamException
  {
    XMLInputSource localXMLInputSource = new XMLInputSource(null, null, null, paramInputStream, paramString);
    return getXMLStreamReaderImpl(localXMLInputSource);
  }
  
  public XMLEventAllocator getEventAllocator()
  {
    return (XMLEventAllocator)getProperty("javax.xml.stream.allocator");
  }
  
  public XMLReporter getXMLReporter()
  {
    return (XMLReporter)fPropertyManager.getProperty("javax.xml.stream.reporter");
  }
  
  public XMLResolver getXMLResolver()
  {
    Object localObject = fPropertyManager.getProperty("javax.xml.stream.resolver");
    return (XMLResolver)localObject;
  }
  
  public void setXMLReporter(XMLReporter paramXMLReporter)
  {
    fPropertyManager.setProperty("javax.xml.stream.reporter", paramXMLReporter);
  }
  
  public void setXMLResolver(XMLResolver paramXMLResolver)
  {
    fPropertyManager.setProperty("javax.xml.stream.resolver", paramXMLResolver);
  }
  
  public XMLEventReader createFilteredReader(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter)
    throws XMLStreamException
  {
    return new EventFilterSupport(paramXMLEventReader, paramEventFilter);
  }
  
  public XMLStreamReader createFilteredReader(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter)
    throws XMLStreamException
  {
    if ((paramXMLStreamReader != null) && (paramStreamFilter != null)) {
      return new XMLStreamFilterImpl(paramXMLStreamReader, paramStreamFilter);
    }
    return null;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Property not supported");
    }
    if (fPropertyManager.containsProperty(paramString)) {
      return fPropertyManager.getProperty(paramString);
    }
    throw new IllegalArgumentException("Property not supported");
  }
  
  public boolean isPropertySupported(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    return fPropertyManager.containsProperty(paramString);
  }
  
  public void setEventAllocator(XMLEventAllocator paramXMLEventAllocator)
  {
    fPropertyManager.setProperty("javax.xml.stream.allocator", paramXMLEventAllocator);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    if ((paramString == null) || (paramObject == null) || (!fPropertyManager.containsProperty(paramString))) {
      throw new IllegalArgumentException("Property " + paramString + " is not supported");
    }
    if ((paramString == "reuse-instance") || (paramString.equals("reuse-instance"))) {
      fReuseInstance = ((Boolean)paramObject).booleanValue();
    } else {
      fPropertyChanged = true;
    }
    fPropertyManager.setProperty(paramString, paramObject);
  }
  
  XMLStreamReader getXMLStreamReaderImpl(XMLInputSource paramXMLInputSource)
    throws XMLStreamException
  {
    if (fTempReader == null)
    {
      fPropertyChanged = false;
      return fTempReader = new XMLStreamReaderImpl(paramXMLInputSource, new PropertyManager(fPropertyManager));
    }
    if ((fReuseInstance) && (fTempReader.canReuse()) && (!fPropertyChanged))
    {
      fTempReader.reset();
      fTempReader.setInputSource(paramXMLInputSource);
      fPropertyChanged = false;
      return fTempReader;
    }
    fPropertyChanged = false;
    return fTempReader = new XMLStreamReaderImpl(paramXMLInputSource, new PropertyManager(fPropertyManager));
  }
  
  XMLInputSource jaxpSourcetoXMLInputSource(Source paramSource)
  {
    if ((paramSource instanceof StreamSource))
    {
      StreamSource localStreamSource = (StreamSource)paramSource;
      String str1 = localStreamSource.getSystemId();
      String str2 = localStreamSource.getPublicId();
      InputStream localInputStream = localStreamSource.getInputStream();
      Reader localReader = localStreamSource.getReader();
      if (localInputStream != null) {
        return new XMLInputSource(str2, str1, null, localInputStream, null);
      }
      if (localReader != null) {
        return new XMLInputSource(str2, str1, null, localReader, null);
      }
      return new XMLInputSource(str2, str1, null);
    }
    throw new UnsupportedOperationException("Cannot create XMLStreamReader or XMLEventReader from a " + paramSource.getClass().getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\XMLInputFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */