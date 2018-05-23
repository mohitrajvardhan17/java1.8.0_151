package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public abstract interface ExtensionsProvider
{
  public abstract boolean functionAvailable(String paramString1, String paramString2)
    throws TransformerException;
  
  public abstract boolean elementAvailable(String paramString1, String paramString2)
    throws TransformerException;
  
  public abstract Object extFunction(String paramString1, String paramString2, Vector paramVector, Object paramObject)
    throws TransformerException;
  
  public abstract Object extFunction(FuncExtFunction paramFuncExtFunction, Vector paramVector)
    throws TransformerException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\ExtensionsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */