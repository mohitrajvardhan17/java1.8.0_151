package com.sun.org.apache.xerces.internal.impl.validation;

import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.ArrayList;
import java.util.Locale;

public class ValidationState
  implements ValidationContext
{
  private boolean fExtraChecking = true;
  private boolean fFacetChecking = true;
  private boolean fNormalize = true;
  private boolean fNamespaces = true;
  private EntityState fEntityState = null;
  private NamespaceContext fNamespaceContext = null;
  private SymbolTable fSymbolTable = null;
  private Locale fLocale = null;
  private ArrayList<String> fIdList;
  private ArrayList<String> fIdRefList;
  
  public ValidationState() {}
  
  public void setExtraChecking(boolean paramBoolean)
  {
    fExtraChecking = paramBoolean;
  }
  
  public void setFacetChecking(boolean paramBoolean)
  {
    fFacetChecking = paramBoolean;
  }
  
  public void setNormalizationRequired(boolean paramBoolean)
  {
    fNormalize = paramBoolean;
  }
  
  public void setUsingNamespaces(boolean paramBoolean)
  {
    fNamespaces = paramBoolean;
  }
  
  public void setEntityState(EntityState paramEntityState)
  {
    fEntityState = paramEntityState;
  }
  
  public void setNamespaceSupport(NamespaceContext paramNamespaceContext)
  {
    fNamespaceContext = paramNamespaceContext;
  }
  
  public void setSymbolTable(SymbolTable paramSymbolTable)
  {
    fSymbolTable = paramSymbolTable;
  }
  
  public String checkIDRefID()
  {
    if ((fIdList == null) && (fIdRefList != null)) {
      return (String)fIdRefList.get(0);
    }
    if (fIdRefList != null) {
      for (int i = 0; i < fIdRefList.size(); i++)
      {
        String str = (String)fIdRefList.get(i);
        if (!fIdList.contains(str)) {
          return str;
        }
      }
    }
    return null;
  }
  
  public void reset()
  {
    fExtraChecking = true;
    fFacetChecking = true;
    fNamespaces = true;
    fIdList = null;
    fIdRefList = null;
    fEntityState = null;
    fNamespaceContext = null;
    fSymbolTable = null;
  }
  
  public void resetIDTables()
  {
    fIdList = null;
    fIdRefList = null;
  }
  
  public boolean needExtraChecking()
  {
    return fExtraChecking;
  }
  
  public boolean needFacetChecking()
  {
    return fFacetChecking;
  }
  
  public boolean needToNormalize()
  {
    return fNormalize;
  }
  
  public boolean useNamespaces()
  {
    return fNamespaces;
  }
  
  public boolean isEntityDeclared(String paramString)
  {
    if (fEntityState != null) {
      return fEntityState.isEntityDeclared(getSymbol(paramString));
    }
    return false;
  }
  
  public boolean isEntityUnparsed(String paramString)
  {
    if (fEntityState != null) {
      return fEntityState.isEntityUnparsed(getSymbol(paramString));
    }
    return false;
  }
  
  public boolean isIdDeclared(String paramString)
  {
    if (fIdList == null) {
      return false;
    }
    return fIdList.contains(paramString);
  }
  
  public void addId(String paramString)
  {
    if (fIdList == null) {
      fIdList = new ArrayList();
    }
    fIdList.add(paramString);
  }
  
  public void addIdRef(String paramString)
  {
    if (fIdRefList == null) {
      fIdRefList = new ArrayList();
    }
    fIdRefList.add(paramString);
  }
  
  public String getSymbol(String paramString)
  {
    if (fSymbolTable != null) {
      return fSymbolTable.addSymbol(paramString);
    }
    return paramString.intern();
  }
  
  public String getURI(String paramString)
  {
    if (fNamespaceContext != null) {
      return fNamespaceContext.getURI(paramString);
    }
    return null;
  }
  
  public void setLocale(Locale paramLocale)
  {
    fLocale = paramLocale;
  }
  
  public Locale getLocale()
  {
    return fLocale;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\validation\ValidationState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */