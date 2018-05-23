package com.sun.org.apache.xerces.internal.xinclude;

import java.util.Stack;

public class XPointerFramework
{
  XPointerSchema[] fXPointerSchema;
  String[] fSchemaPointerName;
  String[] fSchemaPointerURI;
  String fSchemaPointer;
  String fCurrentSchemaPointer;
  Stack fSchemaNotAvailable;
  int fCountSchemaName = 0;
  int schemaLength = 0;
  XPointerSchema fDefaultXPointerSchema;
  
  public XPointerFramework()
  {
    this(null);
  }
  
  public XPointerFramework(XPointerSchema[] paramArrayOfXPointerSchema)
  {
    fXPointerSchema = paramArrayOfXPointerSchema;
    fSchemaNotAvailable = new Stack();
  }
  
  public void reset()
  {
    fXPointerSchema = null;
    fXPointerSchema = null;
    fCountSchemaName = 0;
    schemaLength = 0;
    fSchemaPointerName = null;
    fSchemaPointerURI = null;
    fDefaultXPointerSchema = null;
    fCurrentSchemaPointer = null;
  }
  
  public void setXPointerSchema(XPointerSchema[] paramArrayOfXPointerSchema)
  {
    fXPointerSchema = paramArrayOfXPointerSchema;
  }
  
  public void setSchemaPointer(String paramString)
  {
    fSchemaPointer = paramString;
  }
  
  public XPointerSchema getNextXPointerSchema()
  {
    int i = fCountSchemaName;
    if (fSchemaPointerName == null) {
      getSchemaNames();
    }
    if (fDefaultXPointerSchema == null) {
      getDefaultSchema();
    }
    if (fDefaultXPointerSchema.getXpointerSchemaName().equalsIgnoreCase(fSchemaPointerName[i]))
    {
      fDefaultXPointerSchema.reset();
      fDefaultXPointerSchema.setXPointerSchemaPointer(fSchemaPointerURI[i]);
      fCountSchemaName = (++i);
      return getDefaultSchema();
    }
    if (fXPointerSchema == null)
    {
      fCountSchemaName = (++i);
      return null;
    }
    int j = fXPointerSchema.length;
    while (fSchemaPointerName[i] != null)
    {
      for (int k = 0; k < j; k++) {
        if (fSchemaPointerName[i].equalsIgnoreCase(fXPointerSchema[k].getXpointerSchemaName()))
        {
          fXPointerSchema[k].setXPointerSchemaPointer(fSchemaPointerURI[i]);
          fCountSchemaName = (++i);
          return fXPointerSchema[k];
        }
      }
      if (fSchemaNotAvailable == null) {
        fSchemaNotAvailable = new Stack();
      }
      fSchemaNotAvailable.push(fSchemaPointerName[i]);
      i++;
    }
    return null;
  }
  
  public XPointerSchema getDefaultSchema()
  {
    if (fDefaultXPointerSchema == null) {
      fDefaultXPointerSchema = new XPointerElementHandler();
    }
    return fDefaultXPointerSchema;
  }
  
  public void getSchemaNames()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i2 = fSchemaPointer.length();
    fSchemaPointerName = new String[5];
    fSchemaPointerURI = new String[5];
    j = fSchemaPointer.indexOf('(');
    if (j <= 0) {
      return;
    }
    fSchemaPointerName[(m++)] = fSchemaPointer.substring(0, j++).trim();
    k = j;
    String str = null;
    i++;
    while (j < i2)
    {
      int i1 = fSchemaPointer.charAt(j);
      if (i1 == 40) {
        i++;
      }
      if (i1 == 41) {
        i--;
      }
      if (i == 0)
      {
        str = fSchemaPointer.substring(k, j).trim();
        fSchemaPointerURI[(n++)] = getEscapedURI(str);
        k = j;
        if ((j = fSchemaPointer.indexOf('(', k)) != -1)
        {
          fSchemaPointerName[(m++)] = fSchemaPointer.substring(k + 1, j).trim();
          i++;
          k = j + 1;
        }
        else
        {
          j = k;
        }
      }
      j++;
    }
    schemaLength = (n - 1);
  }
  
  public String getEscapedURI(String paramString)
  {
    return paramString;
  }
  
  public int getSchemaCount()
  {
    return schemaLength;
  }
  
  public int getCurrentPointer()
  {
    return fCountSchemaName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XPointerFramework.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */