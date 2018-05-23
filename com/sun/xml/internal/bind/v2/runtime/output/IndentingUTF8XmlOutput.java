package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class IndentingUTF8XmlOutput
  extends UTF8XmlOutput
{
  private final Encoded indent8;
  private final int unitLen;
  private int depth = 0;
  private boolean seenText = false;
  
  public IndentingUTF8XmlOutput(OutputStream paramOutputStream, String paramString, Encoded[] paramArrayOfEncoded, CharacterEscapeHandler paramCharacterEscapeHandler)
  {
    super(paramOutputStream, paramArrayOfEncoded, paramCharacterEscapeHandler);
    if (paramString != null)
    {
      Encoded localEncoded = new Encoded(paramString);
      indent8 = new Encoded();
      indent8.ensureSize(len * 8);
      unitLen = len;
      for (int i = 0; i < 8; i++) {
        System.arraycopy(buf, 0, indent8.buf, unitLen * i, unitLen);
      }
    }
    else
    {
      indent8 = null;
      unitLen = 0;
    }
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException
  {
    indentStartTag();
    super.beginStartTag(paramInt, paramString);
  }
  
  public void beginStartTag(Name paramName)
    throws IOException
  {
    indentStartTag();
    super.beginStartTag(paramName);
  }
  
  private void indentStartTag()
    throws IOException
  {
    closeStartTag();
    if (!seenText) {
      printIndent();
    }
    depth += 1;
    seenText = false;
  }
  
  public void endTag(Name paramName)
    throws IOException
  {
    indentEndTag();
    super.endTag(paramName);
  }
  
  public void endTag(int paramInt, String paramString)
    throws IOException
  {
    indentEndTag();
    super.endTag(paramInt, paramString);
  }
  
  private void indentEndTag()
    throws IOException
  {
    depth -= 1;
    if ((!closeStartTagPending) && (!seenText)) {
      printIndent();
    }
    seenText = false;
  }
  
  private void printIndent()
    throws IOException
  {
    write(10);
    int i = depth % 8;
    write(indent8.buf, 0, i * unitLen);
    i >>= 3;
    while (i > 0)
    {
      indent8.write(this);
      i--;
    }
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException
  {
    seenText = true;
    super.text(paramString, paramBoolean);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException
  {
    seenText = true;
    super.text(paramPcdata, paramBoolean);
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    write(10);
    super.endDocument(paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\IndentingUTF8XmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */