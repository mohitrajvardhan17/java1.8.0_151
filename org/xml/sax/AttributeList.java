package org.xml.sax;

/**
 * @deprecated
 */
public abstract interface AttributeList
{
  public abstract int getLength();
  
  public abstract String getName(int paramInt);
  
  public abstract String getType(int paramInt);
  
  public abstract String getValue(int paramInt);
  
  public abstract String getType(String paramString);
  
  public abstract String getValue(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\AttributeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */