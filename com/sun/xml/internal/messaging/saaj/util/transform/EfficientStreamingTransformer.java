package com.sun.xml.internal.messaging.saaj.util.transform;

import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

public class EfficientStreamingTransformer
  extends Transformer
{
  private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
  private Transformer m_realTransformer = null;
  private Object m_fiDOMDocumentParser = null;
  private Object m_fiDOMDocumentSerializer = null;
  
  private EfficientStreamingTransformer() {}
  
  private void materialize()
    throws TransformerException
  {
    if (m_realTransformer == null) {
      m_realTransformer = transformerFactory.newTransformer();
    }
  }
  
  public void clearParameters()
  {
    if (m_realTransformer != null) {
      m_realTransformer.clearParameters();
    }
  }
  
  public ErrorListener getErrorListener()
  {
    try
    {
      materialize();
      return m_realTransformer.getErrorListener();
    }
    catch (TransformerException localTransformerException) {}
    return null;
  }
  
  public Properties getOutputProperties()
  {
    try
    {
      materialize();
      return m_realTransformer.getOutputProperties();
    }
    catch (TransformerException localTransformerException) {}
    return null;
  }
  
  public String getOutputProperty(String paramString)
    throws IllegalArgumentException
  {
    try
    {
      materialize();
      return m_realTransformer.getOutputProperty(paramString);
    }
    catch (TransformerException localTransformerException) {}
    return null;
  }
  
  public Object getParameter(String paramString)
  {
    try
    {
      materialize();
      return m_realTransformer.getParameter(paramString);
    }
    catch (TransformerException localTransformerException) {}
    return null;
  }
  
  public URIResolver getURIResolver()
  {
    try
    {
      materialize();
      return m_realTransformer.getURIResolver();
    }
    catch (TransformerException localTransformerException) {}
    return null;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
    throws IllegalArgumentException
  {
    try
    {
      materialize();
      m_realTransformer.setErrorListener(paramErrorListener);
    }
    catch (TransformerException localTransformerException) {}
  }
  
  public void setOutputProperties(Properties paramProperties)
    throws IllegalArgumentException
  {
    try
    {
      materialize();
      m_realTransformer.setOutputProperties(paramProperties);
    }
    catch (TransformerException localTransformerException) {}
  }
  
  public void setOutputProperty(String paramString1, String paramString2)
    throws IllegalArgumentException
  {
    try
    {
      materialize();
      m_realTransformer.setOutputProperty(paramString1, paramString2);
    }
    catch (TransformerException localTransformerException) {}
  }
  
  public void setParameter(String paramString, Object paramObject)
  {
    try
    {
      materialize();
      m_realTransformer.setParameter(paramString, paramObject);
    }
    catch (TransformerException localTransformerException) {}
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    try
    {
      materialize();
      m_realTransformer.setURIResolver(paramURIResolver);
    }
    catch (TransformerException localTransformerException) {}
  }
  
  private InputStream getInputStreamFromSource(StreamSource paramStreamSource)
    throws TransformerException
  {
    InputStream localInputStream = paramStreamSource.getInputStream();
    if (localInputStream != null) {
      return localInputStream;
    }
    if (paramStreamSource.getReader() != null) {
      return null;
    }
    String str1 = paramStreamSource.getSystemId();
    if (str1 != null) {
      try
      {
        Object localObject = str1;
        if (str1.startsWith("file:///"))
        {
          String str2 = str1.substring(7);
          int i = str2.indexOf(":") > 0 ? 1 : 0;
          if (i != 0)
          {
            String str3 = str2.substring(1);
            localObject = str3;
          }
          else
          {
            localObject = str2;
          }
        }
        try
        {
          return new FileInputStream(new File(new URI((String)localObject)));
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw new TransformerException(localURISyntaxException);
        }
        throw new TransformerException("Unexpected StreamSource object");
      }
      catch (IOException localIOException)
      {
        throw new TransformerException(localIOException.toString());
      }
    }
  }
  
  public void transform(Source paramSource, Result paramResult)
    throws TransformerException
  {
    if (((paramSource instanceof StreamSource)) && ((paramResult instanceof StreamResult)))
    {
      try
      {
        StreamSource localStreamSource = (StreamSource)paramSource;
        InputStream localInputStream = getInputStreamFromSource(localStreamSource);
        OutputStream localOutputStream = ((StreamResult)paramResult).getOutputStream();
        if (localOutputStream == null) {
          throw new TransformerException("Unexpected StreamResult object contains null OutputStream");
        }
        Object localObject;
        if (localInputStream != null)
        {
          if (localInputStream.markSupported()) {
            localInputStream.mark(Integer.MAX_VALUE);
          }
          localObject = new byte[' '];
          int i;
          while ((i = localInputStream.read((byte[])localObject)) != -1) {
            localOutputStream.write((byte[])localObject, 0, i);
          }
          if (localInputStream.markSupported()) {
            localInputStream.reset();
          }
          return;
        }
        Reader localReader = localStreamSource.getReader();
        if (localReader != null)
        {
          if (localReader.markSupported()) {
            localReader.mark(Integer.MAX_VALUE);
          }
          localObject = new PushbackReader(localReader, 4096);
          XMLDeclarationParser localXMLDeclarationParser = new XMLDeclarationParser((PushbackReader)localObject);
          try
          {
            localXMLDeclarationParser.parse();
          }
          catch (Exception localException3)
          {
            throw new TransformerException("Unable to run the JAXP transformer on a stream " + localException3.getMessage());
          }
          OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localOutputStream);
          localXMLDeclarationParser.writeTo(localOutputStreamWriter);
          char[] arrayOfChar = new char[' '];
          int j;
          while ((j = ((PushbackReader)localObject).read(arrayOfChar)) != -1) {
            localOutputStreamWriter.write(arrayOfChar, 0, j);
          }
          localOutputStreamWriter.flush();
          if (localReader.markSupported()) {
            localReader.reset();
          }
          return;
        }
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        throw new TransformerException(localIOException.toString());
      }
      throw new TransformerException("Unexpected StreamSource object");
    }
    if ((FastInfosetReflection.isFastInfosetSource(paramSource)) && ((paramResult instanceof DOMResult))) {
      try
      {
        if (m_fiDOMDocumentParser == null) {
          m_fiDOMDocumentParser = FastInfosetReflection.DOMDocumentParser_new();
        }
        FastInfosetReflection.DOMDocumentParser_parse(m_fiDOMDocumentParser, (Document)((DOMResult)paramResult).getNode(), FastInfosetReflection.FastInfosetSource_getInputStream(paramSource));
        return;
      }
      catch (Exception localException1)
      {
        throw new TransformerException(localException1);
      }
    }
    if (((paramSource instanceof DOMSource)) && (FastInfosetReflection.isFastInfosetResult(paramResult))) {
      try
      {
        if (m_fiDOMDocumentSerializer == null) {
          m_fiDOMDocumentSerializer = FastInfosetReflection.DOMDocumentSerializer_new();
        }
        FastInfosetReflection.DOMDocumentSerializer_setOutputStream(m_fiDOMDocumentSerializer, FastInfosetReflection.FastInfosetResult_getOutputStream(paramResult));
        FastInfosetReflection.DOMDocumentSerializer_serialize(m_fiDOMDocumentSerializer, ((DOMSource)paramSource).getNode());
        return;
      }
      catch (Exception localException2)
      {
        throw new TransformerException(localException2);
      }
    }
    materialize();
    m_realTransformer.transform(paramSource, paramResult);
  }
  
  public static Transformer newTransformer()
  {
    return new EfficientStreamingTransformer();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\transform\EfficientStreamingTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */