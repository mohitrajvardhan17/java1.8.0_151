package com.sun.xml.internal.txw2.output;

import java.util.Stack;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class IndentingXMLStreamWriter
  extends DelegatingXMLStreamWriter
{
  private static final Object SEEN_NOTHING = new Object();
  private static final Object SEEN_ELEMENT = new Object();
  private static final Object SEEN_DATA = new Object();
  private Object state = SEEN_NOTHING;
  private Stack<Object> stateStack = new Stack();
  private String indentStep = "  ";
  private int depth = 0;
  
  public IndentingXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter)
  {
    super(paramXMLStreamWriter);
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
  
  private void onStartElement()
    throws XMLStreamException
  {
    stateStack.push(SEEN_ELEMENT);
    state = SEEN_NOTHING;
    if (depth > 0) {
      super.writeCharacters("\n");
    }
    doIndent();
    depth += 1;
  }
  
  private void onEndElement()
    throws XMLStreamException
  {
    depth -= 1;
    if (state == SEEN_ELEMENT)
    {
      super.writeCharacters("\n");
      doIndent();
    }
    state = stateStack.pop();
  }
  
  private void onEmptyElement()
    throws XMLStreamException
  {
    state = SEEN_ELEMENT;
    if (depth > 0) {
      super.writeCharacters("\n");
    }
    doIndent();
  }
  
  private void doIndent()
    throws XMLStreamException
  {
    if (depth > 0) {
      for (int i = 0; i < depth; i++) {
        super.writeCharacters(indentStep);
      }
    }
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {
    super.writeStartDocument();
    super.writeCharacters("\n");
  }
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    super.writeStartDocument(paramString);
    super.writeCharacters("\n");
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    super.writeStartDocument(paramString1, paramString2);
    super.writeCharacters("\n");
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    onStartElement();
    super.writeStartElement(paramString);
  }
  
  public void writeStartElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    onStartElement();
    super.writeStartElement(paramString1, paramString2);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    onStartElement();
    super.writeStartElement(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    onEmptyElement();
    super.writeEmptyElement(paramString1, paramString2);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    onEmptyElement();
    super.writeEmptyElement(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    onEmptyElement();
    super.writeEmptyElement(paramString);
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    onEndElement();
    super.writeEndElement();
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    state = SEEN_DATA;
    super.writeCharacters(paramString);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    state = SEEN_DATA;
    super.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    state = SEEN_DATA;
    super.writeCData(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\IndentingXMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */