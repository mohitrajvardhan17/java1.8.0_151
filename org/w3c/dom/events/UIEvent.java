package org.w3c.dom.events;

import org.w3c.dom.views.AbstractView;

public abstract interface UIEvent
  extends Event
{
  public abstract AbstractView getView();
  
  public abstract int getDetail();
  
  public abstract void initUIEvent(String paramString, boolean paramBoolean1, boolean paramBoolean2, AbstractView paramAbstractView, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\events\UIEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */