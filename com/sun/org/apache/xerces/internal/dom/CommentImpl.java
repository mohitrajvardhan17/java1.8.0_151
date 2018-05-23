package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;

public class CommentImpl
  extends CharacterDataImpl
  implements CharacterData, Comment
{
  static final long serialVersionUID = -2685736833408134044L;
  
  public CommentImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString)
  {
    super(paramCoreDocumentImpl, paramString);
  }
  
  public short getNodeType()
  {
    return 8;
  }
  
  public String getNodeName()
  {
    return "#comment";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\CommentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */