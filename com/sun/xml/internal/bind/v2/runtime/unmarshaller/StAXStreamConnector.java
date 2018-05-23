package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import java.lang.reflect.Constructor;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class StAXStreamConnector
  extends StAXConnector
{
  private final XMLStreamReader staxStreamReader;
  protected final StringBuilder buffer = new StringBuilder();
  protected boolean textReported = false;
  private final Attributes attributes = new Attributes()
  {
    public int getLength()
    {
      return staxStreamReader.getAttributeCount();
    }
    
    public String getURI(int paramAnonymousInt)
    {
      String str = staxStreamReader.getAttributeNamespace(paramAnonymousInt);
      if (str == null) {
        return "";
      }
      return str;
    }
    
    public String getLocalName(int paramAnonymousInt)
    {
      return staxStreamReader.getAttributeLocalName(paramAnonymousInt);
    }
    
    public String getQName(int paramAnonymousInt)
    {
      String str = staxStreamReader.getAttributePrefix(paramAnonymousInt);
      if ((str == null) || (str.length() == 0)) {
        return getLocalName(paramAnonymousInt);
      }
      return str + ':' + getLocalName(paramAnonymousInt);
    }
    
    public String getType(int paramAnonymousInt)
    {
      return staxStreamReader.getAttributeType(paramAnonymousInt);
    }
    
    public String getValue(int paramAnonymousInt)
    {
      return staxStreamReader.getAttributeValue(paramAnonymousInt);
    }
    
    public int getIndex(String paramAnonymousString1, String paramAnonymousString2)
    {
      for (int i = getLength() - 1; i >= 0; i--) {
        if ((paramAnonymousString2.equals(getLocalName(i))) && (paramAnonymousString1.equals(getURI(i)))) {
          return i;
        }
      }
      return -1;
    }
    
    public int getIndex(String paramAnonymousString)
    {
      for (int i = getLength() - 1; i >= 0; i--) {
        if (paramAnonymousString.equals(getQName(i))) {
          return i;
        }
      }
      return -1;
    }
    
    public String getType(String paramAnonymousString1, String paramAnonymousString2)
    {
      int i = getIndex(paramAnonymousString1, paramAnonymousString2);
      if (i < 0) {
        return null;
      }
      return getType(i);
    }
    
    public String getType(String paramAnonymousString)
    {
      int i = getIndex(paramAnonymousString);
      if (i < 0) {
        return null;
      }
      return getType(i);
    }
    
    public String getValue(String paramAnonymousString1, String paramAnonymousString2)
    {
      int i = getIndex(paramAnonymousString1, paramAnonymousString2);
      if (i < 0) {
        return null;
      }
      return getValue(i);
    }
    
    public String getValue(String paramAnonymousString)
    {
      int i = getIndex(paramAnonymousString);
      if (i < 0) {
        return null;
      }
      return getValue(i);
    }
  };
  private static final Class FI_STAX_READER_CLASS = ;
  private static final Constructor<? extends StAXConnector> FI_CONNECTOR_CTOR = initFastInfosetConnectorClass();
  private static final Class STAX_EX_READER_CLASS = initStAXExReader();
  private static final Constructor<? extends StAXConnector> STAX_EX_CONNECTOR_CTOR = initStAXExConnector();
  
  public static StAXConnector create(XMLStreamReader paramXMLStreamReader, XmlVisitor paramXmlVisitor)
  {
    Class localClass = paramXMLStreamReader.getClass();
    if ((FI_STAX_READER_CLASS != null) && (FI_STAX_READER_CLASS.isAssignableFrom(localClass)) && (FI_CONNECTOR_CTOR != null)) {
      try
      {
        return (StAXConnector)FI_CONNECTOR_CTOR.newInstance(new Object[] { paramXMLStreamReader, paramXmlVisitor });
      }
      catch (Exception localException1) {}
    }
    boolean bool = localClass.getName().equals("com.sun.xml.internal.stream.XMLReaderImpl");
    if (((!getBoolProp(paramXMLStreamReader, "org.codehaus.stax2.internNames")) || (!getBoolProp(paramXMLStreamReader, "org.codehaus.stax2.internNsUris"))) && (!bool) && (!checkImplementaionNameOfSjsxp(paramXMLStreamReader))) {
      paramXmlVisitor = new InterningXmlVisitor(paramXmlVisitor);
    }
    if ((STAX_EX_READER_CLASS != null) && (STAX_EX_READER_CLASS.isAssignableFrom(localClass))) {
      try
      {
        return (StAXConnector)STAX_EX_CONNECTOR_CTOR.newInstance(new Object[] { paramXMLStreamReader, paramXmlVisitor });
      }
      catch (Exception localException2) {}
    }
    return new StAXStreamConnector(paramXMLStreamReader, paramXmlVisitor);
  }
  
  private static boolean checkImplementaionNameOfSjsxp(XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      Object localObject = paramXMLStreamReader.getProperty("http://java.sun.com/xml/stream/properties/implementation-name");
      return (localObject != null) && (localObject.equals("sjsxp"));
    }
    catch (Exception localException) {}
    return false;
  }
  
  private static boolean getBoolProp(XMLStreamReader paramXMLStreamReader, String paramString)
  {
    try
    {
      Object localObject = paramXMLStreamReader.getProperty(paramString);
      if ((localObject instanceof Boolean)) {
        return ((Boolean)localObject).booleanValue();
      }
      return false;
    }
    catch (Exception localException) {}
    return false;
  }
  
  protected StAXStreamConnector(XMLStreamReader paramXMLStreamReader, XmlVisitor paramXmlVisitor)
  {
    super(paramXmlVisitor);
    staxStreamReader = paramXMLStreamReader;
  }
  
  public void bridge()
    throws XMLStreamException
  {
    try
    {
      int i = 0;
      int j = staxStreamReader.getEventType();
      if (j == 7) {
        while (!staxStreamReader.isStartElement()) {
          j = staxStreamReader.next();
        }
      }
      if (j != 1) {
        throw new IllegalStateException("The current event is not START_ELEMENT\n but " + j);
      }
      handleStartDocument(staxStreamReader.getNamespaceContext());
      for (;;)
      {
        switch (j)
        {
        case 1: 
          handleStartElement();
          i++;
          break;
        case 2: 
          i--;
          handleEndElement();
          if (i != 0) {
            break;
          }
          break;
        case 4: 
        case 6: 
        case 12: 
          handleCharacters();
        }
        j = staxStreamReader.next();
      }
      staxStreamReader.next();
      handleEndDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  protected Location getCurrentLocation()
  {
    return staxStreamReader.getLocation();
  }
  
  protected String getCurrentQName()
  {
    return getQName(staxStreamReader.getPrefix(), staxStreamReader.getLocalName());
  }
  
  private void handleEndElement()
    throws SAXException
  {
    processText(false);
    tagName.uri = fixNull(staxStreamReader.getNamespaceURI());
    tagName.local = staxStreamReader.getLocalName();
    visitor.endElement(tagName);
    int i = staxStreamReader.getNamespaceCount();
    for (int j = i - 1; j >= 0; j--) {
      visitor.endPrefixMapping(fixNull(staxStreamReader.getNamespacePrefix(j)));
    }
  }
  
  private void handleStartElement()
    throws SAXException
  {
    processText(true);
    int i = staxStreamReader.getNamespaceCount();
    for (int j = 0; j < i; j++) {
      visitor.startPrefixMapping(fixNull(staxStreamReader.getNamespacePrefix(j)), fixNull(staxStreamReader.getNamespaceURI(j)));
    }
    tagName.uri = fixNull(staxStreamReader.getNamespaceURI());
    tagName.local = staxStreamReader.getLocalName();
    tagName.atts = attributes;
    visitor.startElement(tagName);
  }
  
  protected void handleCharacters()
    throws XMLStreamException, SAXException
  {
    if (predictor.expectText()) {
      buffer.append(staxStreamReader.getTextCharacters(), staxStreamReader.getTextStart(), staxStreamReader.getTextLength());
    }
  }
  
  private void processText(boolean paramBoolean)
    throws SAXException
  {
    if ((predictor.expectText()) && ((!paramBoolean) || (!WhiteSpaceProcessor.isWhiteSpace(buffer)) || (context.getCurrentState().isMixed()))) {
      if (textReported) {
        textReported = false;
      } else {
        visitor.text(buffer);
      }
    }
    buffer.setLength(0);
  }
  
  private static Class initFIStAXReaderClass()
  {
    try
    {
      Class localClass1 = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.FastInfosetStreamReader");
      Class localClass2 = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser");
      if (localClass1.isAssignableFrom(localClass2)) {
        return localClass2;
      }
      return null;
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static Constructor<? extends StAXConnector> initFastInfosetConnectorClass()
  {
    try
    {
      if (FI_STAX_READER_CLASS == null) {
        return null;
      }
      Class localClass = Class.forName("com.sun.xml.internal.bind.v2.runtime.unmarshaller.FastInfosetConnector");
      return localClass.getConstructor(new Class[] { FI_STAX_READER_CLASS, XmlVisitor.class });
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static Class initStAXExReader()
  {
    try
    {
      return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx");
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static Constructor<? extends StAXConnector> initStAXExConnector()
  {
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.bind.v2.runtime.unmarshaller.StAXExConnector");
      return localClass.getConstructor(new Class[] { STAX_EX_READER_CLASS, XmlVisitor.class });
    }
    catch (Throwable localThrowable) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\StAXStreamConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */