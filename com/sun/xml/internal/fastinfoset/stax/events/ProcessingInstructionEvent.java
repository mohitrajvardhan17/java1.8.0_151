package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent
  extends EventBase
  implements ProcessingInstruction
{
  private String targetName;
  private String _data;
  
  public ProcessingInstructionEvent()
  {
    init();
  }
  
  public ProcessingInstructionEvent(String paramString1, String paramString2)
  {
    targetName = paramString1;
    _data = paramString2;
    init();
  }
  
  protected void init()
  {
    setEventType(3);
  }
  
  public String getTarget()
  {
    return targetName;
  }
  
  public void setTarget(String paramString)
  {
    targetName = paramString;
  }
  
  public void setData(String paramString)
  {
    _data = paramString;
  }
  
  public String getData()
  {
    return _data;
  }
  
  public String toString()
  {
    if ((_data != null) && (targetName != null)) {
      return "<?" + targetName + " " + _data + "?>";
    }
    if (targetName != null) {
      return "<?" + targetName + "?>";
    }
    if (_data != null) {
      return "<?" + _data + "?>";
    }
    return "<??>";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\ProcessingInstructionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */