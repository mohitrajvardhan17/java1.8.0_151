package javax.swing.text.html.parser;

import java.util.BitSet;

final class TagStack
  implements DTDConstants
{
  TagElement tag;
  Element elem;
  ContentModelState state;
  TagStack next;
  BitSet inclusions;
  BitSet exclusions;
  boolean net;
  boolean pre;
  
  TagStack(TagElement paramTagElement, TagStack paramTagStack)
  {
    tag = paramTagElement;
    elem = paramTagElement.getElement();
    next = paramTagStack;
    Element localElement = paramTagElement.getElement();
    if (localElement.getContent() != null) {
      state = new ContentModelState(localElement.getContent());
    }
    if (paramTagStack != null)
    {
      inclusions = inclusions;
      exclusions = exclusions;
      pre = pre;
    }
    if (paramTagElement.isPreformatted()) {
      pre = true;
    }
    if (inclusions != null) {
      if (inclusions != null)
      {
        inclusions = ((BitSet)inclusions.clone());
        inclusions.or(inclusions);
      }
      else
      {
        inclusions = inclusions;
      }
    }
    if (exclusions != null) {
      if (exclusions != null)
      {
        exclusions = ((BitSet)exclusions.clone());
        exclusions.or(exclusions);
      }
      else
      {
        exclusions = exclusions;
      }
    }
  }
  
  public Element first()
  {
    return state != null ? state.first() : null;
  }
  
  public ContentModel contentModel()
  {
    if (state == null) {
      return null;
    }
    return state.getModel();
  }
  
  boolean excluded(int paramInt)
  {
    return (exclusions != null) && (exclusions.get(elem.getIndex()));
  }
  
  boolean advance(Element paramElement)
  {
    if ((exclusions != null) && (exclusions.get(paramElement.getIndex()))) {
      return false;
    }
    if (state != null)
    {
      ContentModelState localContentModelState = state.advance(paramElement);
      if (localContentModelState != null)
      {
        state = localContentModelState;
        return true;
      }
    }
    else if (elem.getType() == 19)
    {
      return true;
    }
    return (inclusions != null) && (inclusions.get(paramElement.getIndex()));
  }
  
  boolean terminate()
  {
    return (state == null) || (state.terminate());
  }
  
  public String toString()
  {
    return next + " <" + tag.getElement().getName() + ">";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\TagStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */