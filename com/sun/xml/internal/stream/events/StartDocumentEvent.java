package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent
  extends DummyEvent
  implements StartDocument
{
  protected String fSystemId;
  protected String fEncodingScheam;
  protected boolean fStandalone;
  protected String fVersion;
  private boolean fEncodingSchemeSet = false;
  private boolean fStandaloneSet = false;
  private boolean nestedCall = false;
  
  public StartDocumentEvent()
  {
    init("UTF-8", "1.0", true, null);
  }
  
  public StartDocumentEvent(String paramString)
  {
    init(paramString, "1.0", true, null);
  }
  
  public StartDocumentEvent(String paramString1, String paramString2)
  {
    init(paramString1, paramString2, true, null);
  }
  
  public StartDocumentEvent(String paramString1, String paramString2, boolean paramBoolean)
  {
    fStandaloneSet = true;
    init(paramString1, paramString2, paramBoolean, null);
  }
  
  public StartDocumentEvent(String paramString1, String paramString2, boolean paramBoolean, Location paramLocation)
  {
    fStandaloneSet = true;
    init(paramString1, paramString2, paramBoolean, paramLocation);
  }
  
  protected void init(String paramString1, String paramString2, boolean paramBoolean, Location paramLocation)
  {
    setEventType(7);
    fEncodingScheam = paramString1;
    fVersion = paramString2;
    fStandalone = paramBoolean;
    if ((paramString1 != null) && (!paramString1.equals("")))
    {
      fEncodingSchemeSet = true;
    }
    else
    {
      fEncodingSchemeSet = false;
      fEncodingScheam = "UTF-8";
    }
    fLocation = paramLocation;
  }
  
  public String getSystemId()
  {
    if (fLocation == null) {
      return "";
    }
    return fLocation.getSystemId();
  }
  
  public String getCharacterEncodingScheme()
  {
    return fEncodingScheam;
  }
  
  public boolean isStandalone()
  {
    return fStandalone;
  }
  
  public String getVersion()
  {
    return fVersion;
  }
  
  public void setStandalone(boolean paramBoolean)
  {
    fStandaloneSet = true;
    fStandalone = paramBoolean;
  }
  
  public void setStandalone(String paramString)
  {
    fStandaloneSet = true;
    if (paramString == null)
    {
      fStandalone = true;
      return;
    }
    if (paramString.equals("yes")) {
      fStandalone = true;
    } else {
      fStandalone = false;
    }
  }
  
  public boolean encodingSet()
  {
    return fEncodingSchemeSet;
  }
  
  public boolean standaloneSet()
  {
    return fStandaloneSet;
  }
  
  public void setEncoding(String paramString)
  {
    fEncodingScheam = paramString;
  }
  
  void setDeclaredEncoding(boolean paramBoolean)
  {
    fEncodingSchemeSet = paramBoolean;
  }
  
  public void setVersion(String paramString)
  {
    fVersion = paramString;
  }
  
  void clear()
  {
    fEncodingScheam = "UTF-8";
    fStandalone = true;
    fVersion = "1.0";
    fEncodingSchemeSet = false;
    fStandaloneSet = false;
  }
  
  public String toString()
  {
    String str = "<?xml version=\"" + fVersion + "\"";
    str = str + " encoding='" + fEncodingScheam + "'";
    if (fStandaloneSet)
    {
      if (fStandalone) {
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
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(toString());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\StartDocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */