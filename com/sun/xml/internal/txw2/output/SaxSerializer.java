package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.util.Stack;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SaxSerializer
  implements XmlSerializer
{
  private final ContentHandler writer;
  private final LexicalHandler lexical;
  private final Stack<String> prefixBindings = new Stack();
  private final Stack<String> elementBindings = new Stack();
  private final AttributesImpl attrs = new AttributesImpl();
  
  public SaxSerializer(ContentHandler paramContentHandler)
  {
    this(paramContentHandler, null, true);
  }
  
  public SaxSerializer(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler)
  {
    this(paramContentHandler, paramLexicalHandler, true);
  }
  
  public SaxSerializer(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      writer = paramContentHandler;
      lexical = paramLexicalHandler;
    }
    else
    {
      IndentingXMLFilter localIndentingXMLFilter = new IndentingXMLFilter(paramContentHandler, paramLexicalHandler);
      writer = localIndentingXMLFilter;
      lexical = localIndentingXMLFilter;
    }
  }
  
  public SaxSerializer(SAXResult paramSAXResult)
  {
    this(paramSAXResult.getHandler(), paramSAXResult.getLexicalHandler());
  }
  
  public void startDocument()
  {
    try
    {
      writer.startDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new TxwException(localSAXException);
    }
  }
  
  public void writeXmlns(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      paramString1 = "";
    }
    if (paramString1.equals("xml")) {
      return;
    }
    prefixBindings.add(paramString2);
    prefixBindings.add(paramString1);
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3)
  {
    elementBindings.add(getQName(paramString3, paramString2));
    elementBindings.add(paramString2);
    elementBindings.add(paramString1);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder)
  {
    attrs.addAttribute(paramString1, paramString2, getQName(paramString3, paramString2), "CDATA", paramStringBuilder.toString());
  }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      while (prefixBindings.size() != 0) {
        writer.startPrefixMapping((String)prefixBindings.pop(), (String)prefixBindings.pop());
      }
      writer.startElement(paramString1, paramString2, getQName(paramString3, paramString2), attrs);
      attrs.clear();
    }
    catch (SAXException localSAXException)
    {
      throw new TxwException(localSAXException);
    }
  }
  
  public void endTag()
  {
    try
    {
      writer.endElement((String)elementBindings.pop(), (String)elementBindings.pop(), (String)elementBindings.pop());
    }
    catch (SAXException localSAXException)
    {
      throw new TxwException(localSAXException);
    }
  }
  
  public void text(StringBuilder paramStringBuilder)
  {
    try
    {
      writer.characters(paramStringBuilder.toString().toCharArray(), 0, paramStringBuilder.length());
    }
    catch (SAXException localSAXException)
    {
      throw new TxwException(localSAXException);
    }
  }
  
  public void cdata(StringBuilder paramStringBuilder)
  {
    if (lexical == null) {
      throw new UnsupportedOperationException("LexicalHandler is needed to write PCDATA");
    }
    try
    {
      lexical.startCDATA();
      text(paramStringBuilder);
      lexical.endCDATA();
    }
    catch (SAXException localSAXException)
    {
      throw new TxwException(localSAXException);
    }
  }
  
  public void comment(StringBuilder paramStringBuilder)
  {
    try
    {
      if (lexical == null) {
        throw new UnsupportedOperationException("LexicalHandler is needed to write comments");
      }
      lexical.comment(paramStringBuilder.toString().toCharArray(), 0, paramStringBuilder.length());
    }
    catch (SAXException localSAXException)
    {
      throw new TxwException(localSAXException);
    }
  }
  
  public void endDocument()
  {
    try
    {
      writer.endDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new TxwException(localSAXException);
    }
  }
  
  public void flush() {}
  
  private static String getQName(String paramString1, String paramString2)
  {
    String str;
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      str = paramString2;
    } else {
      str = paramString1 + ':' + paramString2;
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\SaxSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */