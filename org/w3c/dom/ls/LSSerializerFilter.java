package org.w3c.dom.ls;

import org.w3c.dom.traversal.NodeFilter;

public abstract interface LSSerializerFilter
  extends NodeFilter
{
  public abstract int getWhatToShow();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ls\LSSerializerFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */