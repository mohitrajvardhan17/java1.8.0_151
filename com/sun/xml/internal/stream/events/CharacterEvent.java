package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Characters;

public class CharacterEvent
  extends DummyEvent
  implements Characters
{
  private String fData;
  private boolean fIsCData;
  private boolean fIsIgnorableWhitespace;
  private boolean fIsSpace = false;
  private boolean fCheckIfSpaceNeeded = true;
  
  public CharacterEvent()
  {
    fIsCData = false;
    init();
  }
  
  public CharacterEvent(String paramString)
  {
    fIsCData = false;
    init();
    fData = paramString;
  }
  
  public CharacterEvent(String paramString, boolean paramBoolean)
  {
    init();
    fData = paramString;
    fIsCData = paramBoolean;
  }
  
  public CharacterEvent(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    init();
    fData = paramString;
    fIsCData = paramBoolean1;
    fIsIgnorableWhitespace = paramBoolean2;
  }
  
  protected void init()
  {
    setEventType(4);
  }
  
  public String getData()
  {
    return fData;
  }
  
  public void setData(String paramString)
  {
    fData = paramString;
    fCheckIfSpaceNeeded = true;
  }
  
  public boolean isCData()
  {
    return fIsCData;
  }
  
  public String toString()
  {
    if (fIsCData) {
      return "<![CDATA[" + getData() + "]]>";
    }
    return fData;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    if (fIsCData) {
      paramWriter.write("<![CDATA[" + getData() + "]]>");
    } else {
      charEncode(paramWriter, fData);
    }
  }
  
  public boolean isIgnorableWhiteSpace()
  {
    return fIsIgnorableWhitespace;
  }
  
  public boolean isWhiteSpace()
  {
    if (fCheckIfSpaceNeeded)
    {
      checkWhiteSpace();
      fCheckIfSpaceNeeded = false;
    }
    return fIsSpace;
  }
  
  private void checkWhiteSpace()
  {
    if ((fData != null) && (fData.length() > 0))
    {
      fIsSpace = true;
      for (int i = 0; i < fData.length(); i++) {
        if (!XMLChar.isSpace(fData.charAt(i)))
        {
          fIsSpace = false;
          break;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\CharacterEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */