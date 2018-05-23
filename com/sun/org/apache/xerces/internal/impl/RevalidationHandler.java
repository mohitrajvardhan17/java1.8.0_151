package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public abstract interface RevalidationHandler
  extends XMLDocumentFilter
{
  public abstract boolean characterData(String paramString, Augmentations paramAugmentations);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\RevalidationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */