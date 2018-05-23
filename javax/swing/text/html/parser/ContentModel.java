package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.Vector;

public final class ContentModel
  implements Serializable
{
  public int type;
  public Object content;
  public ContentModel next;
  private boolean[] valSet;
  private boolean[] val;
  
  public ContentModel() {}
  
  public ContentModel(Element paramElement)
  {
    this(0, paramElement, null);
  }
  
  public ContentModel(int paramInt, ContentModel paramContentModel)
  {
    this(paramInt, paramContentModel, null);
  }
  
  public ContentModel(int paramInt, Object paramObject, ContentModel paramContentModel)
  {
    type = paramInt;
    content = paramObject;
    next = paramContentModel;
  }
  
  public boolean empty()
  {
    ContentModel localContentModel;
    switch (type)
    {
    case 42: 
    case 63: 
      return true;
    case 43: 
    case 124: 
      for (localContentModel = (ContentModel)content; localContentModel != null; localContentModel = next) {
        if (localContentModel.empty()) {
          return true;
        }
      }
      return false;
    case 38: 
    case 44: 
      for (localContentModel = (ContentModel)content; localContentModel != null; localContentModel = next) {
        if (!localContentModel.empty()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public void getElements(Vector<Element> paramVector)
  {
    switch (type)
    {
    case 42: 
    case 43: 
    case 63: 
      ((ContentModel)content).getElements(paramVector);
      break;
    case 38: 
    case 44: 
    case 124: 
      for (ContentModel localContentModel = (ContentModel)content; localContentModel != null; localContentModel = next) {
        localContentModel.getElements(paramVector);
      }
      break;
    default: 
      paramVector.addElement((Element)content);
    }
  }
  
  public boolean first(Object paramObject)
  {
    Object localObject;
    switch (type)
    {
    case 42: 
    case 43: 
    case 63: 
      return ((ContentModel)content).first(paramObject);
    case 44: 
      for (localObject = (ContentModel)content; localObject != null; localObject = next)
      {
        if (((ContentModel)localObject).first(paramObject)) {
          return true;
        }
        if (!((ContentModel)localObject).empty()) {
          return false;
        }
      }
      return false;
    case 38: 
    case 124: 
      localObject = (Element)paramObject;
      if ((valSet == null) || (valSet.length <= Element.getMaxIndex()))
      {
        valSet = new boolean[Element.getMaxIndex() + 1];
        val = new boolean[valSet.length];
      }
      if (valSet[index] != 0) {
        return val[index];
      }
      for (ContentModel localContentModel = (ContentModel)content; localContentModel != null; localContentModel = next) {
        if (localContentModel.first(paramObject))
        {
          val[index] = true;
          break;
        }
      }
      valSet[index] = true;
      return val[index];
    }
    return content == paramObject;
  }
  
  public Element first()
  {
    switch (type)
    {
    case 38: 
    case 42: 
    case 63: 
    case 124: 
      return null;
    case 43: 
    case 44: 
      return ((ContentModel)content).first();
    }
    return (Element)content;
  }
  
  public String toString()
  {
    switch (type)
    {
    case 42: 
      return content + "*";
    case 63: 
      return content + "?";
    case 43: 
      return content + "+";
    case 38: 
    case 44: 
    case 124: 
      char[] arrayOfChar = { ' ', (char)type, ' ' };
      String str = "";
      for (ContentModel localContentModel = (ContentModel)content; localContentModel != null; localContentModel = next)
      {
        str = str + localContentModel;
        if (next != null) {
          str = str + new String(arrayOfChar);
        }
      }
      return "(" + str + ")";
    }
    return content.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\ContentModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */