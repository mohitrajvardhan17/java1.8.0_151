package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import javax.xml.stream.events.Characters;

public class CharactersEvent
  extends EventBase
  implements Characters
{
  private String _text;
  private boolean isCData = false;
  private boolean isSpace = false;
  private boolean isIgnorable = false;
  private boolean needtoCheck = true;
  
  public CharactersEvent()
  {
    super(4);
  }
  
  public CharactersEvent(String paramString)
  {
    super(4);
    _text = paramString;
  }
  
  public CharactersEvent(String paramString, boolean paramBoolean)
  {
    super(4);
    _text = paramString;
    isCData = paramBoolean;
  }
  
  public String getData()
  {
    return _text;
  }
  
  public void setData(String paramString)
  {
    _text = paramString;
  }
  
  public boolean isCData()
  {
    return isCData;
  }
  
  public String toString()
  {
    if (isCData) {
      return "<![CDATA[" + _text + "]]>";
    }
    return _text;
  }
  
  public boolean isIgnorableWhiteSpace()
  {
    return isIgnorable;
  }
  
  public boolean isWhiteSpace()
  {
    if (needtoCheck)
    {
      checkWhiteSpace();
      needtoCheck = false;
    }
    return isSpace;
  }
  
  public void setSpace(boolean paramBoolean)
  {
    isSpace = paramBoolean;
    needtoCheck = false;
  }
  
  public void setIgnorable(boolean paramBoolean)
  {
    isIgnorable = paramBoolean;
    setEventType(6);
  }
  
  private void checkWhiteSpace()
  {
    if (!Util.isEmptyString(_text))
    {
      isSpace = true;
      for (int i = 0; i < _text.length(); i++) {
        if (!XMLChar.isSpace(_text.charAt(i)))
        {
          isSpace = false;
          break;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\CharactersEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */