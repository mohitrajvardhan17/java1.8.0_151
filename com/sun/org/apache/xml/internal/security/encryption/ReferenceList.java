package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;

public abstract interface ReferenceList
{
  public static final int DATA_REFERENCE = 1;
  public static final int KEY_REFERENCE = 2;
  
  public abstract void add(Reference paramReference);
  
  public abstract void remove(Reference paramReference);
  
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract Iterator<Reference> getReferences();
  
  public abstract Reference newDataReference(String paramString);
  
  public abstract Reference newKeyReference(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\ReferenceList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */