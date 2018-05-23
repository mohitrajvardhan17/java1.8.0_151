package com.sun.org.apache.xml.internal.utils;

import javax.xml.transform.TransformerException;

public abstract interface RawCharacterHandler
{
  public abstract void charactersRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws TransformerException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\RawCharacterHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */