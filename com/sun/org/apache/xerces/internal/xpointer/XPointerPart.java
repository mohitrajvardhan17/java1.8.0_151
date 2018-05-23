package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public abstract interface XPointerPart
{
  public static final int EVENT_ELEMENT_START = 0;
  public static final int EVENT_ELEMENT_END = 1;
  public static final int EVENT_ELEMENT_EMPTY = 2;
  
  public abstract void parseXPointer(String paramString)
    throws XNIException;
  
  public abstract boolean resolveXPointer(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, int paramInt)
    throws XNIException;
  
  public abstract boolean isFragmentResolved()
    throws XNIException;
  
  public abstract boolean isChildFragmentResolved()
    throws XNIException;
  
  public abstract String getSchemeName();
  
  public abstract String getSchemeData();
  
  public abstract void setSchemeName(String paramString);
  
  public abstract void setSchemeData(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xpointer\XPointerPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */