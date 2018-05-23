package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataWriter
  extends XMLWriter
{
  private static final Object SEEN_NOTHING = new Object();
  private static final Object SEEN_ELEMENT = new Object();
  private static final Object SEEN_DATA = new Object();
  private Object state = SEEN_NOTHING;
  private Stack<Object> stateStack = new Stack();
  private String indentStep = "";
  private int depth = 0;
  
  public DataWriter(Writer paramWriter, String paramString, CharacterEscapeHandler paramCharacterEscapeHandler)
  {
    super(paramWriter, paramString, paramCharacterEscapeHandler);
  }
  
  public DataWriter(Writer paramWriter, String paramString)
  {
    this(paramWriter, paramString, DumbEscapeHandler.theInstance);
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
  
  public void reset()
  {
    depth = 0;
    state = SEEN_NOTHING;
    stateStack = new Stack();
    super.reset();
  }
  
  protected void writeXmlDecl(String paramString)
    throws IOException
  {
    super.writeXmlDecl(paramString);
    write('\n');
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    stateStack.push(SEEN_ELEMENT);
    state = SEEN_NOTHING;
    if (depth > 0) {
      super.characters("\n");
    }
    doIndent();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    depth += 1;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    depth -= 1;
    if (state == SEEN_ELEMENT)
    {
      super.characters("\n");
      doIndent();
    }
    super.endElement(paramString1, paramString2, paramString3);
    state = stateStack.pop();
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      write('\n');
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    super.endDocument();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    state = SEEN_DATA;
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\marshaller\DataWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */