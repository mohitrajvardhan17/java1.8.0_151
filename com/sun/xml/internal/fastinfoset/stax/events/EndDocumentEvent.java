package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.EndDocument;

public class EndDocumentEvent
  extends EventBase
  implements EndDocument
{
  public EndDocumentEvent()
  {
    super(8);
  }
  
  public String toString()
  {
    return "<? EndDocument ?>";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EndDocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */