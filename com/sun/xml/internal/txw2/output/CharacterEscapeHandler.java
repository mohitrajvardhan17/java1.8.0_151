package com.sun.xml.internal.txw2.output;

import java.io.IOException;
import java.io.Writer;

public abstract interface CharacterEscapeHandler
{
  public abstract void escape(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, Writer paramWriter)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\CharacterEscapeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */