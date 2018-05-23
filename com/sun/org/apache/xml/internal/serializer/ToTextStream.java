package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Messages;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class ToTextStream
  extends ToStream
{
  public ToTextStream() {}
  
  protected void startDocumentInternal()
    throws SAXException
  {
    super.startDocumentInternal();
    m_needToCallStartDocument = false;
  }
  
  public void endDocument()
    throws SAXException
  {
    flushPending();
    flushWriter();
    if (m_tracer != null) {
      super.fireEndDoc();
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (m_tracer != null)
    {
      super.fireStartElem(paramString3);
      firePseudoAttributes();
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (m_tracer != null) {
      super.fireEndElem(paramString3);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    flushPending();
    try
    {
      if (inTemporaryOutputState()) {
        m_writer.write(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, m_lineSepUse);
      }
      if (m_tracer != null) {
        super.fireCharEvent(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void charactersRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, m_lineSepUse);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  void writeNormalizedChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, SAXException
  {
    String str1 = getEncoding();
    Writer localWriter = m_writer;
    int i = paramInt1 + paramInt2;
    int j = 10;
    for (int k = paramInt1; k < i; k++)
    {
      char c = paramArrayOfChar[k];
      if (('\n' == c) && (paramBoolean))
      {
        localWriter.write(m_lineSep, 0, m_lineSepLen);
      }
      else if (m_encodingInfo.isInEncoding(c))
      {
        localWriter.write(c);
      }
      else
      {
        String str3;
        if (Encodings.isHighUTF16Surrogate(c))
        {
          int m = writeUTF16Surrogate(c, paramArrayOfChar, k, i);
          if (m != 0)
          {
            str3 = Integer.toString(m);
            String str4 = Utils.messages.createMessage("ER_ILLEGAL_CHARACTER", new Object[] { str3, str1 });
            System.err.println(str4);
          }
          k++;
        }
        else if (str1 != null)
        {
          localWriter.write(38);
          localWriter.write(35);
          localWriter.write(Integer.toString(c));
          localWriter.write(59);
          String str2 = Integer.toString(c);
          str3 = Utils.messages.createMessage("ER_ILLEGAL_CHARACTER", new Object[] { str2, str1 });
          System.err.println(str3);
        }
        else
        {
          localWriter.write(c);
        }
      }
    }
  }
  
  public void cdata(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, m_lineSepUse);
      if (m_tracer != null) {
        super.fireCDATAEvent(paramArrayOfChar, paramInt1, paramInt2);
      }
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
      writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, m_lineSepUse);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    flushPending();
    if (m_tracer != null) {
      super.fireEscapingEvent(paramString1, paramString2);
    }
  }
  
  public void comment(String paramString)
    throws SAXException
  {
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    comment(m_charsBuff, 0, i);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    flushPending();
    if (m_tracer != null) {
      super.fireCommentEvent(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void entityReference(String paramString)
    throws SAXException
  {
    if (m_tracer != null) {
      super.fireEntityReference(paramString);
    }
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) {}
  
  public void endCDATA()
    throws SAXException
  {}
  
  public void endElement(String paramString)
    throws SAXException
  {
    if (m_tracer != null) {
      super.fireEndElem(paramString);
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (m_needToCallStartDocument) {
      startDocumentInternal();
    }
    if (m_tracer != null)
    {
      super.fireStartElem(paramString3);
      firePseudoAttributes();
    }
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    characters(m_charsBuff, 0, i);
  }
  
  public void addAttribute(String paramString1, String paramString2) {}
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt)
    throws SAXException
  {}
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean)
    throws SAXException
  {
    return false;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void flushPending()
    throws SAXException
  {
    if (m_needToCallStartDocument)
    {
      startDocumentInternal();
      m_needToCallStartDocument = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToTextStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */