package javax.swing.text.html.parser;

class ContentModelState
{
  ContentModel model;
  long value;
  ContentModelState next;
  
  public ContentModelState(ContentModel paramContentModel)
  {
    this(paramContentModel, null, 0L);
  }
  
  ContentModelState(Object paramObject, ContentModelState paramContentModelState)
  {
    this(paramObject, paramContentModelState, 0L);
  }
  
  ContentModelState(Object paramObject, ContentModelState paramContentModelState, long paramLong)
  {
    model = ((ContentModel)paramObject);
    next = paramContentModelState;
    value = paramLong;
  }
  
  public ContentModel getModel()
  {
    ContentModel localContentModel = model;
    for (int i = 0; i < value; i++) {
      if (next != null) {
        localContentModel = next;
      } else {
        return null;
      }
    }
    return localContentModel;
  }
  
  public boolean terminate()
  {
    ContentModel localContentModel;
    int i;
    switch (model.type)
    {
    case 43: 
      if ((value == 0L) && (!model.empty())) {
        return false;
      }
    case 42: 
    case 63: 
      return (next == null) || (next.terminate());
    case 124: 
      for (localContentModel = (ContentModel)model.content; localContentModel != null; localContentModel = next) {
        if (localContentModel.empty()) {
          return (next == null) || (next.terminate());
        }
      }
      return false;
    case 38: 
      localContentModel = (ContentModel)model.content;
      i = 0;
      while (localContentModel != null)
      {
        if (((value & 1L << i) == 0L) && (!localContentModel.empty())) {
          return false;
        }
        i++;
        localContentModel = next;
      }
      return (next == null) || (next.terminate());
    case 44: 
      localContentModel = (ContentModel)model.content;
      i = 0;
      while (i < value)
      {
        i++;
        localContentModel = next;
      }
      while ((localContentModel != null) && (localContentModel.empty())) {
        localContentModel = next;
      }
      if (localContentModel != null) {
        return false;
      }
      return (next == null) || (next.terminate());
    }
    return false;
  }
  
  public Element first()
  {
    switch (model.type)
    {
    case 38: 
    case 42: 
    case 63: 
    case 124: 
      return null;
    case 43: 
      return model.first();
    case 44: 
      ContentModel localContentModel = (ContentModel)model.content;
      int i = 0;
      while (i < value)
      {
        i++;
        localContentModel = next;
      }
      return localContentModel.first();
    }
    return model.first();
  }
  
  public ContentModelState advance(Object paramObject)
  {
    ContentModel localContentModel;
    int i;
    switch (model.type)
    {
    case 43: 
      if (model.first(paramObject)) {
        return new ContentModelState(model.content, new ContentModelState(model, next, value + 1L)).advance(paramObject);
      }
      if (value != 0L)
      {
        if (next != null) {
          return next.advance(paramObject);
        }
        return null;
      }
      break;
    case 42: 
      if (model.first(paramObject)) {
        return new ContentModelState(model.content, this).advance(paramObject);
      }
      if (next != null) {
        return next.advance(paramObject);
      }
      return null;
    case 63: 
      if (model.first(paramObject)) {
        return new ContentModelState(model.content, next).advance(paramObject);
      }
      if (next != null) {
        return next.advance(paramObject);
      }
      return null;
    case 124: 
      for (localContentModel = (ContentModel)model.content; localContentModel != null; localContentModel = next) {
        if (localContentModel.first(paramObject)) {
          return new ContentModelState(localContentModel, next).advance(paramObject);
        }
      }
      break;
    case 44: 
      localContentModel = (ContentModel)model.content;
      i = 0;
      while (i < value)
      {
        i++;
        localContentModel = next;
      }
      if ((localContentModel.first(paramObject)) || (localContentModel.empty()))
      {
        if (next == null) {
          return new ContentModelState(localContentModel, next).advance(paramObject);
        }
        return new ContentModelState(localContentModel, new ContentModelState(model, next, value + 1L)).advance(paramObject);
      }
      break;
    case 38: 
      localContentModel = (ContentModel)model.content;
      i = 1;
      int j = 0;
      while (localContentModel != null)
      {
        if ((value & 1L << j) == 0L)
        {
          if (localContentModel.first(paramObject)) {
            return new ContentModelState(localContentModel, new ContentModelState(model, next, value | 1L << j)).advance(paramObject);
          }
          if (!localContentModel.empty()) {
            i = 0;
          }
        }
        j++;
        localContentModel = next;
      }
      if (i != 0)
      {
        if (next != null) {
          return next.advance(paramObject);
        }
        return null;
      }
      break;
    default: 
      if (model.content == paramObject)
      {
        if ((next == null) && ((paramObject instanceof Element)) && (content != null)) {
          return new ContentModelState(content);
        }
        return next;
      }
      break;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\ContentModelState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */