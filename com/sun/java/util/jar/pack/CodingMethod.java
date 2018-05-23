package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract interface CodingMethod
{
  public abstract void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract byte[] getMetaCoding(Coding paramCoding);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\CodingMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */