package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent
  extends DummyEvent
  implements ProcessingInstruction
{
  private String fName;
  private String fContent;
  
  public ProcessingInstructionEvent()
  {
    init();
  }
  
  public ProcessingInstructionEvent(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public ProcessingInstructionEvent(String paramString1, String paramString2, Location paramLocation)
  {
    init();
    fName = paramString1;
    fContent = paramString2;
    setLocation(paramLocation);
  }
  
  protected void init()
  {
    setEventType(3);
  }
  
  public String getTarget()
  {
    return fName;
  }
  
  public void setTarget(String paramString)
  {
    fName = paramString;
  }
  
  public void setData(String paramString)
  {
    fContent = paramString;
  }
  
  public String getData()
  {
    return fContent;
  }
  
  public String toString()
  {
    if ((fContent != null) && (fName != null)) {
      return "<?" + fName + " " + fContent + "?>";
    }
    if (fName != null) {
      return "<?" + fName + "?>";
    }
    if (fContent != null) {
      return "<?" + fContent + "?>";
    }
    return "<??>";
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(toString());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\ProcessingInstructionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */