package com.sun.org.apache.xerces.internal.xs;

public abstract interface PSVIProvider
{
  public abstract ElementPSVI getElementPSVI();
  
  public abstract AttributePSVI getAttributePSVI(int paramInt);
  
  public abstract AttributePSVI getAttributePSVIByName(String paramString1, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\PSVIProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */