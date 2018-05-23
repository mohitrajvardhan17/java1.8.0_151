package org.w3c.dom;

public abstract interface CharacterData
  extends Node
{
  public abstract String getData()
    throws DOMException;
  
  public abstract void setData(String paramString)
    throws DOMException;
  
  public abstract int getLength();
  
  public abstract String substringData(int paramInt1, int paramInt2)
    throws DOMException;
  
  public abstract void appendData(String paramString)
    throws DOMException;
  
  public abstract void insertData(int paramInt, String paramString)
    throws DOMException;
  
  public abstract void deleteData(int paramInt1, int paramInt2)
    throws DOMException;
  
  public abstract void replaceData(int paramInt1, int paramInt2, String paramString)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\CharacterData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */