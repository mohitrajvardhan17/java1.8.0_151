package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class XSGrammarBucket
{
  Map<String, SchemaGrammar> fGrammarRegistry = new HashMap();
  SchemaGrammar fNoNSGrammar = null;
  
  public XSGrammarBucket() {}
  
  public SchemaGrammar getGrammar(String paramString)
  {
    if (paramString == null) {
      return fNoNSGrammar;
    }
    return (SchemaGrammar)fGrammarRegistry.get(paramString);
  }
  
  public void putGrammar(SchemaGrammar paramSchemaGrammar)
  {
    if (paramSchemaGrammar.getTargetNamespace() == null) {
      fNoNSGrammar = paramSchemaGrammar;
    } else {
      fGrammarRegistry.put(paramSchemaGrammar.getTargetNamespace(), paramSchemaGrammar);
    }
  }
  
  public boolean putGrammar(SchemaGrammar paramSchemaGrammar, boolean paramBoolean)
  {
    SchemaGrammar localSchemaGrammar1 = getGrammar(fTargetNamespace);
    if (localSchemaGrammar1 != null) {
      return localSchemaGrammar1 == paramSchemaGrammar;
    }
    if (!paramBoolean)
    {
      putGrammar(paramSchemaGrammar);
      return true;
    }
    Vector localVector1 = paramSchemaGrammar.getImportedGrammars();
    if (localVector1 == null)
    {
      putGrammar(paramSchemaGrammar);
      return true;
    }
    Vector localVector2 = (Vector)localVector1.clone();
    for (int i = 0; i < localVector2.size(); i++)
    {
      SchemaGrammar localSchemaGrammar2 = (SchemaGrammar)localVector2.elementAt(i);
      SchemaGrammar localSchemaGrammar3 = getGrammar(fTargetNamespace);
      if (localSchemaGrammar3 == null)
      {
        Vector localVector3 = localSchemaGrammar2.getImportedGrammars();
        if (localVector3 != null) {
          for (int j = localVector3.size() - 1; j >= 0; j--)
          {
            localSchemaGrammar3 = (SchemaGrammar)localVector3.elementAt(j);
            if (!localVector2.contains(localSchemaGrammar3)) {
              localVector2.addElement(localSchemaGrammar3);
            }
          }
        }
      }
      else if (localSchemaGrammar3 != localSchemaGrammar2)
      {
        return false;
      }
    }
    putGrammar(paramSchemaGrammar);
    for (i = localVector2.size() - 1; i >= 0; i--) {
      putGrammar((SchemaGrammar)localVector2.elementAt(i));
    }
    return true;
  }
  
  public boolean putGrammar(SchemaGrammar paramSchemaGrammar, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean2) {
      return putGrammar(paramSchemaGrammar, paramBoolean1);
    }
    SchemaGrammar localSchemaGrammar1 = getGrammar(fTargetNamespace);
    if (localSchemaGrammar1 == null) {
      putGrammar(paramSchemaGrammar);
    }
    if (!paramBoolean1) {
      return true;
    }
    Vector localVector1 = paramSchemaGrammar.getImportedGrammars();
    if (localVector1 == null) {
      return true;
    }
    Vector localVector2 = (Vector)localVector1.clone();
    for (int i = 0; i < localVector2.size(); i++)
    {
      SchemaGrammar localSchemaGrammar2 = (SchemaGrammar)localVector2.elementAt(i);
      SchemaGrammar localSchemaGrammar3 = getGrammar(fTargetNamespace);
      if (localSchemaGrammar3 == null)
      {
        Vector localVector3 = localSchemaGrammar2.getImportedGrammars();
        if (localVector3 != null) {
          for (int j = localVector3.size() - 1; j >= 0; j--)
          {
            localSchemaGrammar3 = (SchemaGrammar)localVector3.elementAt(j);
            if (!localVector2.contains(localSchemaGrammar3)) {
              localVector2.addElement(localSchemaGrammar3);
            }
          }
        }
      }
      else
      {
        localVector2.remove(localSchemaGrammar2);
      }
    }
    for (i = localVector2.size() - 1; i >= 0; i--) {
      putGrammar((SchemaGrammar)localVector2.elementAt(i));
    }
    return true;
  }
  
  public SchemaGrammar[] getGrammars()
  {
    int i = fGrammarRegistry.size() + (fNoNSGrammar == null ? 0 : 1);
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[i];
    int j = 0;
    Iterator localIterator = fGrammarRegistry.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      arrayOfSchemaGrammar[(j++)] = ((SchemaGrammar)localEntry.getValue());
    }
    if (fNoNSGrammar != null) {
      arrayOfSchemaGrammar[(i - 1)] = fNoNSGrammar;
    }
    return arrayOfSchemaGrammar;
  }
  
  public void reset()
  {
    fNoNSGrammar = null;
    fGrammarRegistry.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSGrammarBucket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */