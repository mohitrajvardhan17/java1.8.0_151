package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;

public class XMLStreamWriterOutput
  extends XmlOutputAbstractImpl
{
  private final XMLStreamWriter out;
  protected final char[] buf = new char['Ā'];
  private static final Class FI_STAX_WRITER_CLASS = ;
  private static final Constructor<? extends XmlOutput> FI_OUTPUT_CTOR = initFastInfosetOutputClass();
  private static final Class STAXEX_WRITER_CLASS = initStAXExWriterClass();
  private static final Constructor<? extends XmlOutput> STAXEX_OUTPUT_CTOR = initStAXExOutputClass();
  
  public static XmlOutput create(XMLStreamWriter paramXMLStreamWriter, JAXBContextImpl paramJAXBContextImpl)
  {
    Class localClass = paramXMLStreamWriter.getClass();
    if (localClass == FI_STAX_WRITER_CLASS) {
      try
      {
        return (XmlOutput)FI_OUTPUT_CTOR.newInstance(new Object[] { paramXMLStreamWriter, paramJAXBContextImpl });
      }
      catch (Exception localException1) {}
    }
    if ((STAXEX_WRITER_CLASS != null) && (STAXEX_WRITER_CLASS.isAssignableFrom(localClass))) {
      try
      {
        return (XmlOutput)STAXEX_OUTPUT_CTOR.newInstance(new Object[] { paramXMLStreamWriter });
      }
      catch (Exception localException2) {}
    }
    return new XMLStreamWriterOutput(paramXMLStreamWriter);
  }
  
  protected XMLStreamWriterOutput(XMLStreamWriter paramXMLStreamWriter)
  {
    out = paramXMLStreamWriter;
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (!paramBoolean) {
      out.writeStartDocument();
    }
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    if (!paramBoolean)
    {
      out.writeEndDocument();
      out.flush();
    }
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException, XMLStreamException
  {
    out.writeStartElement(nsContext.getPrefix(paramInt), paramString, nsContext.getNamespaceURI(paramInt));
    NamespaceContextImpl.Element localElement = nsContext.getCurrent();
    if (localElement.count() > 0) {
      for (int i = localElement.count() - 1; i >= 0; i--)
      {
        String str = localElement.getNsUri(i);
        if ((str.length() != 0) || (localElement.getBase() != 1)) {
          out.writeNamespace(localElement.getPrefix(i), str);
        }
      }
    }
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException, XMLStreamException
  {
    if (paramInt == -1) {
      out.writeAttribute(paramString1, paramString2);
    } else {
      out.writeAttribute(nsContext.getPrefix(paramInt), nsContext.getNamespaceURI(paramInt), paramString1, paramString2);
    }
  }
  
  public void endStartTag()
    throws IOException, SAXException
  {}
  
  public void endTag(int paramInt, String paramString)
    throws IOException, SAXException, XMLStreamException
  {
    out.writeEndElement();
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    if (paramBoolean) {
      out.writeCharacters(" ");
    }
    out.writeCharacters(paramString);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    if (paramBoolean) {
      out.writeCharacters(" ");
    }
    int i = paramPcdata.length();
    if (i < buf.length)
    {
      paramPcdata.writeTo(buf, 0);
      out.writeCharacters(buf, 0, i);
    }
    else
    {
      out.writeCharacters(paramPcdata.toString());
    }
  }
  
  private static Class initFIStAXWriterClass()
  {
    try
    {
      Class localClass1 = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter");
      Class localClass2 = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer");
      if (localClass1.isAssignableFrom(localClass2)) {
        return localClass2;
      }
      return null;
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static Constructor<? extends XmlOutput> initFastInfosetOutputClass()
  {
    try
    {
      if (FI_STAX_WRITER_CLASS == null) {
        return null;
      }
      Class localClass = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.FastInfosetStreamWriterOutput");
      return localClass.getConstructor(new Class[] { FI_STAX_WRITER_CLASS, JAXBContextImpl.class });
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static Class initStAXExWriterClass()
  {
    try
    {
      return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx");
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static Constructor<? extends XmlOutput> initStAXExOutputClass()
  {
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput");
      return localClass.getConstructor(new Class[] { STAXEX_WRITER_CLASS });
    }
    catch (Throwable localThrowable) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\XMLStreamWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */