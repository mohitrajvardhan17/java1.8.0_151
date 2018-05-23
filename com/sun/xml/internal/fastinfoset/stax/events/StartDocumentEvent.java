package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent
  extends EventBase
  implements StartDocument
{
  protected String _systemId;
  protected String _encoding = "UTF-8";
  protected boolean _standalone = true;
  protected String _version = "1.0";
  private boolean _encodingSet = false;
  private boolean _standaloneSet = false;
  
  public void reset()
  {
    _encoding = "UTF-8";
    _standalone = true;
    _version = "1.0";
    _encodingSet = false;
    _standaloneSet = false;
  }
  
  public StartDocumentEvent()
  {
    this(null, null);
  }
  
  public StartDocumentEvent(String paramString)
  {
    this(paramString, null);
  }
  
  public StartDocumentEvent(String paramString1, String paramString2)
  {
    if (paramString1 != null)
    {
      _encoding = paramString1;
      _encodingSet = true;
    }
    if (paramString2 != null) {
      _version = paramString2;
    }
    setEventType(7);
  }
  
  public String getSystemId()
  {
    return super.getSystemId();
  }
  
  public String getCharacterEncodingScheme()
  {
    return _encoding;
  }
  
  public boolean encodingSet()
  {
    return _encodingSet;
  }
  
  public boolean isStandalone()
  {
    return _standalone;
  }
  
  public boolean standaloneSet()
  {
    return _standaloneSet;
  }
  
  public String getVersion()
  {
    return _version;
  }
  
  public void setStandalone(boolean paramBoolean)
  {
    _standaloneSet = true;
    _standalone = paramBoolean;
  }
  
  public void setStandalone(String paramString)
  {
    _standaloneSet = true;
    if (paramString == null)
    {
      _standalone = true;
      return;
    }
    if (paramString.equals("yes")) {
      _standalone = true;
    } else {
      _standalone = false;
    }
  }
  
  public void setEncoding(String paramString)
  {
    _encoding = paramString;
    _encodingSet = true;
  }
  
  void setDeclaredEncoding(boolean paramBoolean)
  {
    _encodingSet = paramBoolean;
  }
  
  public void setVersion(String paramString)
  {
    _version = paramString;
  }
  
  void clear()
  {
    _encoding = "UTF-8";
    _standalone = true;
    _version = "1.0";
    _encodingSet = false;
    _standaloneSet = false;
  }
  
  public String toString()
  {
    String str = "<?xml version=\"" + _version + "\"";
    str = str + " encoding='" + _encoding + "'";
    if (_standaloneSet)
    {
      if (_standalone) {
        str = str + " standalone='yes'?>";
      } else {
        str = str + " standalone='no'?>";
      }
    }
    else {
      str = str + "?>";
    }
    return str;
  }
  
  public boolean isStartDocument()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StartDocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */