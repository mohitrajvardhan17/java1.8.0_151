package com.sun.xml.internal.ws.streaming;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.util.FastInfosetUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

public class SourceReaderFactory
{
  static Class fastInfosetSourceClass;
  static Method fastInfosetSource_getInputStream;
  
  public SourceReaderFactory() {}
  
  public static XMLStreamReader createSourceReader(Source paramSource, boolean paramBoolean)
  {
    return createSourceReader(paramSource, paramBoolean, null);
  }
  
  public static XMLStreamReader createSourceReader(Source paramSource, boolean paramBoolean, String paramString)
  {
    try
    {
      Object localObject1;
      Object localObject2;
      if ((paramSource instanceof StreamSource))
      {
        localObject1 = (StreamSource)paramSource;
        localObject2 = ((StreamSource)localObject1).getInputStream();
        if (localObject2 != null)
        {
          if (paramString != null) {
            return XMLStreamReaderFactory.create(paramSource.getSystemId(), new InputStreamReader((InputStream)localObject2, paramString), paramBoolean);
          }
          return XMLStreamReaderFactory.create(paramSource.getSystemId(), (InputStream)localObject2, paramBoolean);
        }
        Reader localReader = ((StreamSource)localObject1).getReader();
        if (localReader != null) {
          return XMLStreamReaderFactory.create(paramSource.getSystemId(), localReader, paramBoolean);
        }
        return XMLStreamReaderFactory.create(paramSource.getSystemId(), new URL(paramSource.getSystemId()).openStream(), paramBoolean);
      }
      if (paramSource.getClass() == fastInfosetSourceClass) {
        return FastInfosetUtil.createFIStreamReader((InputStream)fastInfosetSource_getInputStream.invoke(paramSource, new Object[0]));
      }
      if ((paramSource instanceof DOMSource))
      {
        localObject1 = new DOMStreamReader();
        ((DOMStreamReader)localObject1).setCurrentNode(((DOMSource)paramSource).getNode());
        return (XMLStreamReader)localObject1;
      }
      if ((paramSource instanceof SAXSource))
      {
        localObject1 = XmlUtil.newTransformer();
        localObject2 = new DOMResult();
        ((Transformer)localObject1).transform(paramSource, (Result)localObject2);
        return createSourceReader(new DOMSource(((DOMResult)localObject2).getNode()), paramBoolean);
      }
      throw new XMLReaderException("sourceReader.invalidSource", new Object[] { paramSource.getClass().getName() });
    }
    catch (Exception localException)
    {
      throw new XMLReaderException(localException);
    }
  }
  
  static
  {
    try
    {
      fastInfosetSourceClass = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
      fastInfosetSource_getInputStream = fastInfosetSourceClass.getMethod("getInputStream", new Class[0]);
    }
    catch (Exception localException)
    {
      fastInfosetSourceClass = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\streaming\SourceReaderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */