package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Comment;

public class CommentEvent
  extends DummyEvent
  implements Comment
{
  private String fText;
  
  public CommentEvent()
  {
    init();
  }
  
  public CommentEvent(String paramString)
  {
    init();
    fText = paramString;
  }
  
  protected void init()
  {
    setEventType(5);
  }
  
  public String toString()
  {
    return "<!--" + getText() + "-->";
  }
  
  public String getText()
  {
    return fText;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write("<!--" + getText() + "-->");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\CommentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */