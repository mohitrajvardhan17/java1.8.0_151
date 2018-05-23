package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.Comment;

public class CommentEvent
  extends EventBase
  implements Comment
{
  private String _text;
  
  public CommentEvent()
  {
    super(5);
  }
  
  public CommentEvent(String paramString)
  {
    this();
    _text = paramString;
  }
  
  public String toString()
  {
    return "<!--" + _text + "-->";
  }
  
  public String getText()
  {
    return _text;
  }
  
  public void setText(String paramString)
  {
    _text = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\CommentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */