package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public abstract interface PrimitiveTypeContentHandler
{
  public abstract void booleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void bytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void shorts(short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void ints(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void longs(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void floats(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void doubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void uuids(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\PrimitiveTypeContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */