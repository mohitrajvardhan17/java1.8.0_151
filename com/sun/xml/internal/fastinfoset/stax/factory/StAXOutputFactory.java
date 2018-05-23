package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;
import com.sun.xml.internal.fastinfoset.stax.events.StAXEventWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class StAXOutputFactory
  extends XMLOutputFactory
{
  private StAXManager _manager = null;
  
  public StAXOutputFactory() {}
  
  public XMLEventWriter createXMLEventWriter(Result paramResult)
    throws XMLStreamException
  {
    return new StAXEventWriter(createXMLStreamWriter(paramResult));
  }
  
  public XMLEventWriter createXMLEventWriter(Writer paramWriter)
    throws XMLStreamException
  {
    return new StAXEventWriter(createXMLStreamWriter(paramWriter));
  }
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream)
    throws XMLStreamException
  {
    return new StAXEventWriter(createXMLStreamWriter(paramOutputStream));
  }
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream, String paramString)
    throws XMLStreamException
  {
    return new StAXEventWriter(createXMLStreamWriter(paramOutputStream, paramString));
  }
  
  public XMLStreamWriter createXMLStreamWriter(Result paramResult)
    throws XMLStreamException
  {
    Object localObject1;
    if ((paramResult instanceof StreamResult))
    {
      localObject1 = (StreamResult)paramResult;
      if (((StreamResult)localObject1).getWriter() != null) {
        return createXMLStreamWriter(((StreamResult)localObject1).getWriter());
      }
      if (((StreamResult)localObject1).getOutputStream() != null) {
        return createXMLStreamWriter(((StreamResult)localObject1).getOutputStream());
      }
      if (((StreamResult)localObject1).getSystemId() != null)
      {
        FileWriter localFileWriter = null;
        int j = 1;
        try
        {
          localFileWriter = new FileWriter(new File(((StreamResult)localObject1).getSystemId()));
          XMLStreamWriter localXMLStreamWriter2 = createXMLStreamWriter(localFileWriter);
          j = 0;
          XMLStreamWriter localXMLStreamWriter4 = localXMLStreamWriter2;
          return localXMLStreamWriter4;
        }
        catch (IOException localIOException2)
        {
          throw new XMLStreamException(localIOException2);
        }
        finally
        {
          if ((j != 0) && (localFileWriter != null)) {
            try
            {
              localFileWriter.close();
            }
            catch (IOException localIOException5) {}
          }
        }
      }
    }
    else
    {
      localObject1 = null;
      int i = 1;
      try
      {
        localObject1 = new FileWriter(new File(paramResult.getSystemId()));
        XMLStreamWriter localXMLStreamWriter1 = createXMLStreamWriter((Writer)localObject1);
        i = 0;
        XMLStreamWriter localXMLStreamWriter3 = localXMLStreamWriter1;
        return localXMLStreamWriter3;
      }
      catch (IOException localIOException1)
      {
        throw new XMLStreamException(localIOException1);
      }
      finally
      {
        if ((i != 0) && (localObject1 != null)) {
          try
          {
            ((FileWriter)localObject1).close();
          }
          catch (IOException localIOException6) {}
        }
      }
    }
    throw new UnsupportedOperationException();
  }
  
  public XMLStreamWriter createXMLStreamWriter(Writer paramWriter)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException();
  }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream)
    throws XMLStreamException
  {
    return new StAXDocumentSerializer(paramOutputStream, new StAXManager(_manager));
  }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString)
    throws XMLStreamException
  {
    StAXDocumentSerializer localStAXDocumentSerializer = new StAXDocumentSerializer(paramOutputStream, new StAXManager(_manager));
    localStAXDocumentSerializer.setEncoding(paramString);
    return localStAXDocumentSerializer;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { null }));
    }
    if (_manager.containsProperty(paramString)) {
      return _manager.getProperty(paramString);
    }
    throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { paramString }));
  }
  
  public boolean isPropertySupported(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    return _manager.containsProperty(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    _manager.setProperty(paramString, paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\factory\StAXOutputFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */