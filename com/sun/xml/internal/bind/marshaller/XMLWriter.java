package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriter
  extends XMLFilterImpl
{
  private final HashMap<String, String> locallyDeclaredPrefix = new HashMap();
  private final Attributes EMPTY_ATTS = new AttributesImpl();
  private int elementLevel = 0;
  private Writer output;
  private String encoding;
  private boolean writeXmlDecl = true;
  private String header = null;
  private final CharacterEscapeHandler escapeHandler;
  private boolean startTagIsClosed = true;
  
  public XMLWriter(Writer paramWriter, String paramString, CharacterEscapeHandler paramCharacterEscapeHandler)
  {
    init(paramWriter, paramString);
    escapeHandler = paramCharacterEscapeHandler;
  }
  
  public XMLWriter(Writer paramWriter, String paramString)
  {
    this(paramWriter, paramString, DumbEscapeHandler.theInstance);
  }
  
  private void init(Writer paramWriter, String paramString)
  {
    setOutput(paramWriter, paramString);
  }
  
  public void reset()
  {
    elementLevel = 0;
    startTagIsClosed = true;
  }
  
  public void flush()
    throws IOException
  {
    output.flush();
  }
  
  public void setOutput(Writer paramWriter, String paramString)
  {
    if (paramWriter == null) {
      output = new OutputStreamWriter(System.out);
    } else {
      output = paramWriter;
    }
    encoding = paramString;
  }
  
  public void setXmlDecl(boolean paramBoolean)
  {
    writeXmlDecl = paramBoolean;
  }
  
  public void setHeader(String paramString)
  {
    header = paramString;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    locallyDeclaredPrefix.put(paramString1, paramString2);
  }
  
  public void startDocument()
    throws SAXException
  {
    try
    {
      reset();
      if (writeXmlDecl)
      {
        String str = "";
        if (encoding != null) {
          str = " encoding=\"" + encoding + '"';
        }
        writeXmlDecl("<?xml version=\"1.0\"" + str + " standalone=\"yes\"?>");
      }
      if (header != null) {
        write(header);
      }
      super.startDocument();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  protected void writeXmlDecl(String paramString)
    throws IOException
  {
    write(paramString);
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      super.endDocument();
      flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      if (!startTagIsClosed) {
        write(">");
      }
      elementLevel += 1;
      write('<');
      write(paramString3);
      writeAttributes(paramAttributes);
      if (!locallyDeclaredPrefix.isEmpty())
      {
        Iterator localIterator = locallyDeclaredPrefix.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          String str1 = (String)localEntry.getKey();
          String str2 = (String)localEntry.getValue();
          if (str2 == null) {
            str2 = "";
          }
          write(' ');
          if ("".equals(str1))
          {
            write("xmlns=\"");
          }
          else
          {
            write("xmlns:");
            write(str1);
            write("=\"");
          }
          char[] arrayOfChar = str2.toCharArray();
          writeEsc(arrayOfChar, 0, arrayOfChar.length, true);
          write('"');
        }
        locallyDeclaredPrefix.clear();
      }
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
      startTagIsClosed = false;
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      if (startTagIsClosed)
      {
        write("</");
        write(paramString3);
        write('>');
      }
      else
      {
        write("/>");
        startTagIsClosed = true;
      }
      super.endElement(paramString1, paramString2, paramString3);
      elementLevel -= 1;
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      if (!startTagIsClosed)
      {
        write('>');
        startTagIsClosed = true;
      }
      writeEsc(paramArrayOfChar, paramInt1, paramInt2, false);
      super.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      writeEsc(paramArrayOfChar, paramInt1, paramInt2, false);
      super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      if (!startTagIsClosed)
      {
        write('>');
        startTagIsClosed = true;
      }
      write("<?");
      write(paramString1);
      write(' ');
      write(paramString2);
      write("?>");
      if (elementLevel < 1) {
        write('\n');
      }
      super.processingInstruction(paramString1, paramString2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void startElement(String paramString1, String paramString2)
    throws SAXException
  {
    startElement(paramString1, paramString2, "", EMPTY_ATTS);
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    startElement("", paramString, "", EMPTY_ATTS);
  }
  
  public void endElement(String paramString1, String paramString2)
    throws SAXException
  {
    endElement(paramString1, paramString2, "");
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    endElement("", paramString, "");
  }
  
  public void dataElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes, String paramString4)
    throws SAXException
  {
    startElement(paramString1, paramString2, paramString3, paramAttributes);
    characters(paramString4);
    endElement(paramString1, paramString2, paramString3);
  }
  
  public void dataElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    dataElement(paramString1, paramString2, "", EMPTY_ATTS, paramString3);
  }
  
  public void dataElement(String paramString1, String paramString2)
    throws SAXException
  {
    dataElement("", paramString1, "", EMPTY_ATTS, paramString2);
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    try
    {
      if (!startTagIsClosed)
      {
        write('>');
        startTagIsClosed = true;
      }
      char[] arrayOfChar = paramString.toCharArray();
      characters(arrayOfChar, 0, arrayOfChar.length);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  protected final void write(char paramChar)
    throws IOException
  {
    output.write(paramChar);
  }
  
  protected final void write(String paramString)
    throws IOException
  {
    output.write(paramString);
  }
  
  private void writeAttributes(Attributes paramAttributes)
    throws IOException
  {
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      char[] arrayOfChar = paramAttributes.getValue(j).toCharArray();
      write(' ');
      write(paramAttributes.getQName(j));
      write("=\"");
      writeEsc(arrayOfChar, 0, arrayOfChar.length, true);
      write('"');
    }
  }
  
  private void writeEsc(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException
  {
    escapeHandler.escape(paramArrayOfChar, paramInt1, paramInt2, paramBoolean, output);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\marshaller\XMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */