package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXEventSerializer
  extends DefaultHandler
  implements LexicalHandler
{
  private Writer _writer;
  private boolean _charactersAreCDATA;
  private StringBuffer _characters;
  private Stack _namespaceStack = new Stack();
  protected List _namespaceAttributes;
  
  public SAXEventSerializer(OutputStream paramOutputStream)
    throws IOException
  {
    _writer = new OutputStreamWriter(paramOutputStream);
    _charactersAreCDATA = false;
  }
  
  public void startDocument()
    throws SAXException
  {
    try
    {
      _writer.write("<sax xmlns=\"http://www.sun.com/xml/sax-events\">\n");
      _writer.write("<startDocument/>\n");
      _writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      _writer.write("<endDocument/>\n");
      _writer.write("</sax>");
      _writer.flush();
      _writer.close();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (_namespaceAttributes == null) {
      _namespaceAttributes = new ArrayList();
    }
    String str = "xmlns" + paramString1;
    AttributeValueHolder localAttributeValueHolder = new AttributeValueHolder(str, paramString1, paramString2, null, null);
    _namespaceAttributes.add(localAttributeValueHolder);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      outputCharacters();
      if (_namespaceAttributes != null)
      {
        arrayOfAttributeValueHolder = new AttributeValueHolder[0];
        arrayOfAttributeValueHolder = (AttributeValueHolder[])_namespaceAttributes.toArray(arrayOfAttributeValueHolder);
        quicksort(arrayOfAttributeValueHolder, 0, arrayOfAttributeValueHolder.length - 1);
        for (i = 0; i < arrayOfAttributeValueHolder.length; i++)
        {
          _writer.write("<startPrefixMapping prefix=\"" + localName + "\" uri=\"" + uri + "\"/>\n");
          _writer.flush();
        }
        _namespaceStack.push(arrayOfAttributeValueHolder);
        _namespaceAttributes = null;
      }
      else
      {
        _namespaceStack.push(null);
      }
      AttributeValueHolder[] arrayOfAttributeValueHolder = new AttributeValueHolder[paramAttributes.getLength()];
      for (int i = 0; i < paramAttributes.getLength(); i++) {
        arrayOfAttributeValueHolder[i] = new AttributeValueHolder(paramAttributes.getQName(i), paramAttributes.getLocalName(i), paramAttributes.getURI(i), paramAttributes.getType(i), paramAttributes.getValue(i));
      }
      quicksort(arrayOfAttributeValueHolder, 0, arrayOfAttributeValueHolder.length - 1);
      i = 0;
      for (int j = 0; j < arrayOfAttributeValueHolder.length; j++) {
        if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
          i++;
        }
      }
      if (i == 0)
      {
        _writer.write("<startElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\"/>\n");
        return;
      }
      _writer.write("<startElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\">\n");
      for (j = 0; j < arrayOfAttributeValueHolder.length; j++) {
        if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
          _writer.write("  <attribute qName=\"" + qName + "\" localName=\"" + localName + "\" uri=\"" + uri + "\" value=\"" + value + "\"/>\n");
        }
      }
      _writer.write("</startElement>\n");
      _writer.flush();
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
      outputCharacters();
      _writer.write("<endElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\"/>\n");
      _writer.flush();
      AttributeValueHolder[] arrayOfAttributeValueHolder = (AttributeValueHolder[])_namespaceStack.pop();
      if (arrayOfAttributeValueHolder != null) {
        for (int i = 0; i < arrayOfAttributeValueHolder.length; i++)
        {
          _writer.write("<endPrefixMapping prefix=\"" + localName + "\"/>\n");
          _writer.flush();
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 == 0) {
      return;
    }
    if (_characters == null) {
      _characters = new StringBuffer();
    }
    _characters.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void outputCharacters()
    throws SAXException
  {
    if (_characters == null) {
      return;
    }
    try
    {
      _writer.write("<characters>" + (_charactersAreCDATA ? "<![CDATA[" : "") + _characters + (_charactersAreCDATA ? "]]>" : "") + "</characters>\n");
      _writer.flush();
      _characters = null;
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      outputCharacters();
      _writer.write("<processingInstruction target=\"" + paramString1 + "\" data=\"" + paramString2 + "\"/>\n");
      _writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {
    _charactersAreCDATA = true;
  }
  
  public void endCDATA()
    throws SAXException
  {
    _charactersAreCDATA = false;
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      outputCharacters();
      _writer.write("<comment>" + new String(paramArrayOfChar, paramInt1, paramInt2) + "</comment>\n");
      _writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  private void quicksort(AttributeValueHolder[] paramArrayOfAttributeValueHolder, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      int i = partition(paramArrayOfAttributeValueHolder, paramInt1, paramInt2);
      quicksort(paramArrayOfAttributeValueHolder, paramInt1, i);
      paramInt1 = i + 1;
    }
  }
  
  private int partition(AttributeValueHolder[] paramArrayOfAttributeValueHolder, int paramInt1, int paramInt2)
  {
    AttributeValueHolder localAttributeValueHolder1 = paramArrayOfAttributeValueHolder[(paramInt1 + paramInt2 >>> 1)];
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    for (;;)
    {
      if (localAttributeValueHolder1.compareTo(paramArrayOfAttributeValueHolder[(--j)]) >= 0)
      {
        while (localAttributeValueHolder1.compareTo(paramArrayOfAttributeValueHolder[(++i)]) > 0) {}
        if (i >= j) {
          break;
        }
        AttributeValueHolder localAttributeValueHolder2 = paramArrayOfAttributeValueHolder[i];
        paramArrayOfAttributeValueHolder[i] = paramArrayOfAttributeValueHolder[j];
        paramArrayOfAttributeValueHolder[j] = localAttributeValueHolder2;
      }
    }
    return j;
  }
  
  public static class AttributeValueHolder
    implements Comparable
  {
    public final String qName;
    public final String localName;
    public final String uri;
    public final String type;
    public final String value;
    
    public AttributeValueHolder(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    {
      qName = paramString1;
      localName = paramString2;
      uri = paramString3;
      type = paramString4;
      value = paramString5;
    }
    
    public int compareTo(Object paramObject)
    {
      try
      {
        return qName.compareTo(qName);
      }
      catch (Exception localException)
      {
        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
      }
    }
    
    public boolean equals(Object paramObject)
    {
      try
      {
        return ((paramObject instanceof AttributeValueHolder)) && (qName.equals(qName));
      }
      catch (Exception localException)
      {
        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
      }
    }
    
    public int hashCode()
    {
      int i = 7;
      i = 97 * i + (qName != null ? qName.hashCode() : 0);
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\SAXEventSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */