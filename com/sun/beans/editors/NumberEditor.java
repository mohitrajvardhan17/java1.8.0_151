package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public abstract class NumberEditor
  extends PropertyEditorSupport
{
  public NumberEditor() {}
  
  public String getJavaInitializationString()
  {
    Object localObject = getValue();
    return localObject != null ? localObject.toString() : "null";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\NumberEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */