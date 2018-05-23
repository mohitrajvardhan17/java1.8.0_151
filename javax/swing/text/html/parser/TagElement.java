package javax.swing.text.html.parser;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTML.UnknownTag;

public class TagElement
{
  Element elem;
  HTML.Tag htmlTag;
  boolean insertedByErrorRecovery;
  
  public TagElement(Element paramElement)
  {
    this(paramElement, false);
  }
  
  public TagElement(Element paramElement, boolean paramBoolean)
  {
    elem = paramElement;
    htmlTag = HTML.getTag(paramElement.getName());
    if (htmlTag == null) {
      htmlTag = new HTML.UnknownTag(paramElement.getName());
    }
    insertedByErrorRecovery = paramBoolean;
  }
  
  public boolean breaksFlow()
  {
    return htmlTag.breaksFlow();
  }
  
  public boolean isPreformatted()
  {
    return htmlTag.isPreformatted();
  }
  
  public Element getElement()
  {
    return elem;
  }
  
  public HTML.Tag getHTMLTag()
  {
    return htmlTag;
  }
  
  public boolean fictional()
  {
    return insertedByErrorRecovery;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\TagElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */