package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public abstract interface XMLEntityHandler
{
  public abstract void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void endEntity(String paramString, Augmentations paramAugmentations)
    throws IOException, XNIException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLEntityHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */