package com.sun.xml.internal.txw2.output;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class IndentingXMLFilter
  extends XMLFilterImpl
  implements LexicalHandler
{
  private LexicalHandler lexical;
  private static final char[] NEWLINE = { '\n' };
  private static final Object SEEN_NOTHING = new Object();
  private static final Object SEEN_ELEMENT = new Object();
  private static final Object SEEN_DATA = new Object();
  private Object state = SEEN_NOTHING;
  private Stack<Object> stateStack = new Stack();
  private String indentStep = "";
  private int depth = 0;
  
  public IndentingXMLFilter() {}
  
  public IndentingXMLFilter(ContentHandler paramContentHandler)
  {
    setContentHandler(paramContentHandler);
  }
  
  public IndentingXMLFilter(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler)
  {
    setContentHandler(paramContentHandler);
    setLexicalHandler(paramLexicalHandler);
  }
  
  public LexicalHandler getLexicalHandler()
  {
    return lexical;
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    lexical = paramLexicalHandler;
  }
  
  /**
   * @deprecated
   */
  public int getIndentStep()
  {
    return indentStep.length();
  }
  
  /**
   * @deprecated
   */
  public void setIndentStep(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    while (paramInt > 0)
    {
      localStringBuilder.append(' ');
      paramInt--;
    }
    setIndentStep(localStringBuilder.toString());
  }
  
  public void setIndentStep(String paramString)
  {
    indentStep = paramString;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    stateStack.push(SEEN_ELEMENT);
    state = SEEN_NOTHING;
    if (depth > 0) {
      writeNewLine();
    }
    doIndent();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    depth += 1;
  }
  
  private void writeNewLine()
    throws SAXException
  {
    super.characters(NEWLINE, 0, NEWLINE.length);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    depth -= 1;
    if (state == SEEN_ELEMENT)
    {
      writeNewLine();
      doIndent();
    }
    super.endElement(paramString1, paramString2, paramString3);
    state = stateStack.pop();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    state = SEEN_DATA;
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (depth > 0) {
      writeNewLine();
    }
    doIndent();
    if (lexical != null) {
      lexical.comment(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (lexical != null) {
      lexical.startDTD(paramString1, paramString2, paramString3);
    }
  }
  
  public void endDTD()
    throws SAXException
  {
    if (lexical != null) {
      lexical.endDTD();
    }
  }
  
  public void startEntity(String paramString)
    throws SAXException
  {
    if (lexical != null) {
      lexical.startEntity(paramString);
    }
  }
  
  public void endEntity(String paramString)
    throws SAXException
  {
    if (lexical != null) {
      lexical.endEntity(paramString);
    }
  }
  
  public void startCDATA()
    throws SAXException
  {
    if (lexical != null) {
      lexical.startCDATA();
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    if (lexical != null) {
      lexical.endCDATA();
    }
  }
  
  private void doIndent()
    throws SAXException
  {
    if (depth > 0)
    {
      char[] arrayOfChar = indentStep.toCharArray();
      for (int i = 0; i < depth; i++) {
        characters(arrayOfChar, 0, arrayOfChar.length);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\IndentingXMLFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */