package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.xml.internal.stream.writers.XMLDOMWriterImpl;
import com.sun.xml.internal.stream.writers.XMLEventWriterImpl;
import com.sun.xml.internal.stream.writers.XMLStreamWriterImpl;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;

public class XMLOutputFactoryImpl
  extends XMLOutputFactory
{
  private PropertyManager fPropertyManager = new PropertyManager(2);
  private XMLStreamWriterImpl fStreamWriter = null;
  boolean fReuseInstance = false;
  private static final boolean DEBUG = false;
  private boolean fPropertyChanged;
  
  public XMLOutputFactoryImpl() {}
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream)
    throws XMLStreamException
  {
    return createXMLEventWriter(paramOutputStream, null);
  }
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream, String paramString)
    throws XMLStreamException
  {
    return new XMLEventWriterImpl(createXMLStreamWriter(paramOutputStream, paramString));
  }
  
  public XMLEventWriter createXMLEventWriter(Result paramResult)
    throws XMLStreamException
  {
    if (((paramResult instanceof StAXResult)) && (((StAXResult)paramResult).getXMLEventWriter() != null)) {
      return ((StAXResult)paramResult).getXMLEventWriter();
    }
    return new XMLEventWriterImpl(createXMLStreamWriter(paramResult));
  }
  
  public XMLEventWriter createXMLEventWriter(Writer paramWriter)
    throws XMLStreamException
  {
    return new XMLEventWriterImpl(createXMLStreamWriter(paramWriter));
  }
  
  public XMLStreamWriter createXMLStreamWriter(Result paramResult)
    throws XMLStreamException
  {
    if ((paramResult instanceof StreamResult)) {
      return createXMLStreamWriter((StreamResult)paramResult, null);
    }
    if ((paramResult instanceof DOMResult)) {
      return new XMLDOMWriterImpl((DOMResult)paramResult);
    }
    if ((paramResult instanceof StAXResult))
    {
      if (((StAXResult)paramResult).getXMLStreamWriter() != null) {
        return ((StAXResult)paramResult).getXMLStreamWriter();
      }
      throw new UnsupportedOperationException("Result of type " + paramResult + " is not supported");
    }
    if (paramResult.getSystemId() != null) {
      return createXMLStreamWriter(new StreamResult(paramResult.getSystemId()));
    }
    throw new UnsupportedOperationException("Result of type " + paramResult + " is not supported. Supported result types are: DOMResult, StAXResult and StreamResult.");
  }
  
  public XMLStreamWriter createXMLStreamWriter(Writer paramWriter)
    throws XMLStreamException
  {
    return createXMLStreamWriter(toStreamResult(null, paramWriter, null), null);
  }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream)
    throws XMLStreamException
  {
    return createXMLStreamWriter(paramOutputStream, null);
  }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString)
    throws XMLStreamException
  {
    return createXMLStreamWriter(toStreamResult(paramOutputStream, null, null), paramString);
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
  
  public void setProperty(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    if ((paramString == null) || (paramObject == null) || (!fPropertyManager.containsProperty(paramString))) {
      throw new IllegalArgumentException("Property " + paramString + "is not supported");
    }
    if ((paramString == "reuse-instance") || (paramString.equals("reuse-instance")))
    {
      fReuseInstance = ((Boolean)paramObject).booleanValue();
      if (fReuseInstance) {
        throw new IllegalArgumentException("Property " + paramString + " is not supported: XMLStreamWriters are not Thread safe");
      }
    }
    else
    {
      fPropertyChanged = true;
    }
    fPropertyManager.setProperty(paramString, paramObject);
  }
  
  StreamResult toStreamResult(OutputStream paramOutputStream, Writer paramWriter, String paramString)
  {
    StreamResult localStreamResult = new StreamResult();
    localStreamResult.setOutputStream(paramOutputStream);
    localStreamResult.setWriter(paramWriter);
    localStreamResult.setSystemId(paramString);
    return localStreamResult;
  }
  
  XMLStreamWriter createXMLStreamWriter(StreamResult paramStreamResult, String paramString)
    throws XMLStreamException
  {
    try
    {
      if ((fReuseInstance) && (fStreamWriter != null) && (fStreamWriter.canReuse()) && (!fPropertyChanged))
      {
        fStreamWriter.reset();
        fStreamWriter.setOutput(paramStreamResult, paramString);
        return fStreamWriter;
      }
      return fStreamWriter = new XMLStreamWriterImpl(paramStreamResult, paramString, new PropertyManager(fPropertyManager));
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\XMLOutputFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */