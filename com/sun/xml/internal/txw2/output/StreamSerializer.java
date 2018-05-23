package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class StreamSerializer
  implements XmlSerializer
{
  private final SaxSerializer serializer;
  private final XMLWriter writer;
  
  public StreamSerializer(OutputStream paramOutputStream)
  {
    this(createWriter(paramOutputStream));
  }
  
  public StreamSerializer(OutputStream paramOutputStream, String paramString)
    throws UnsupportedEncodingException
  {
    this(createWriter(paramOutputStream, paramString));
  }
  
  public StreamSerializer(Writer paramWriter)
  {
    this(new StreamResult(paramWriter));
  }
  
  public StreamSerializer(StreamResult paramStreamResult)
  {
    final OutputStream[] arrayOfOutputStream = new OutputStream[1];
    if (paramStreamResult.getWriter() != null)
    {
      writer = createWriter(paramStreamResult.getWriter());
    }
    else if (paramStreamResult.getOutputStream() != null)
    {
      writer = createWriter(paramStreamResult.getOutputStream());
    }
    else if (paramStreamResult.getSystemId() != null)
    {
      String str = paramStreamResult.getSystemId();
      str = convertURL(str);
      try
      {
        FileOutputStream localFileOutputStream = new FileOutputStream(str);
        arrayOfOutputStream[0] = localFileOutputStream;
        writer = createWriter(localFileOutputStream);
      }
      catch (IOException localIOException)
      {
        throw new TxwException(localIOException);
      }
    }
    else
    {
      throw new IllegalArgumentException();
    }
    serializer = new SaxSerializer(writer, writer, false)
    {
      public void endDocument()
      {
        super.endDocument();
        if (arrayOfOutputStream[0] != null)
        {
          try
          {
            arrayOfOutputStream[0].close();
          }
          catch (IOException localIOException)
          {
            throw new TxwException(localIOException);
          }
          arrayOfOutputStream[0] = null;
        }
      }
    };
  }
  
  private StreamSerializer(XMLWriter paramXMLWriter)
  {
    writer = paramXMLWriter;
    serializer = new SaxSerializer(paramXMLWriter, paramXMLWriter, false);
  }
  
  private String convertURL(String paramString)
  {
    paramString = paramString.replace('\\', '/');
    paramString = paramString.replaceAll("//", "/");
    paramString = paramString.replaceAll("//", "/");
    if (paramString.startsWith("file:/")) {
      if (paramString.substring(6).indexOf(":") > 0) {
        paramString = paramString.substring(6);
      } else {
        paramString = paramString.substring(5);
      }
    }
    return paramString;
  }
  
  public void startDocument()
  {
    serializer.startDocument();
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3)
  {
    serializer.beginStartTag(paramString1, paramString2, paramString3);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder)
  {
    serializer.writeAttribute(paramString1, paramString2, paramString3, paramStringBuilder);
  }
  
  public void writeXmlns(String paramString1, String paramString2)
  {
    serializer.writeXmlns(paramString1, paramString2);
  }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3)
  {
    serializer.endStartTag(paramString1, paramString2, paramString3);
  }
  
  public void endTag()
  {
    serializer.endTag();
  }
  
  public void text(StringBuilder paramStringBuilder)
  {
    serializer.text(paramStringBuilder);
  }
  
  public void cdata(StringBuilder paramStringBuilder)
  {
    serializer.cdata(paramStringBuilder);
  }
  
  public void comment(StringBuilder paramStringBuilder)
  {
    serializer.comment(paramStringBuilder);
  }
  
  public void endDocument()
  {
    serializer.endDocument();
  }
  
  public void flush()
  {
    serializer.flush();
    try
    {
      writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new TxwException(localIOException);
    }
  }
  
  private static XMLWriter createWriter(Writer paramWriter)
  {
    DataWriter localDataWriter = new DataWriter(new BufferedWriter(paramWriter));
    localDataWriter.setIndentStep("  ");
    return localDataWriter;
  }
  
  private static XMLWriter createWriter(OutputStream paramOutputStream, String paramString)
    throws UnsupportedEncodingException
  {
    XMLWriter localXMLWriter = createWriter(new OutputStreamWriter(paramOutputStream, paramString));
    localXMLWriter.setEncoding(paramString);
    return localXMLWriter;
  }
  
  private static XMLWriter createWriter(OutputStream paramOutputStream)
  {
    try
    {
      return createWriter(paramOutputStream, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new Error(localUnsupportedEncodingException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\StreamSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */