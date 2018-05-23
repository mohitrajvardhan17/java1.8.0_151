package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public class XMLGrammarPoolImpl
  implements XMLGrammarPool
{
  protected static final int TABLE_SIZE = 11;
  protected Entry[] fGrammars = null;
  protected boolean fPoolIsLocked;
  protected int fGrammarCount = 0;
  private static final boolean DEBUG = false;
  
  public XMLGrammarPoolImpl()
  {
    fGrammars = new Entry[11];
    fPoolIsLocked = false;
  }
  
  public XMLGrammarPoolImpl(int paramInt)
  {
    fGrammars = new Entry[paramInt];
    fPoolIsLocked = false;
  }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString)
  {
    synchronized (fGrammars)
    {
      int i = fGrammars.length;
      Grammar[] arrayOfGrammar1 = new Grammar[fGrammarCount];
      int j = 0;
      for (int k = 0; k < i; k++) {
        for (Entry localEntry = fGrammars[k]; localEntry != null; localEntry = next) {
          if (desc.getGrammarType().equals(paramString)) {
            arrayOfGrammar1[(j++)] = grammar;
          }
        }
      }
      Grammar[] arrayOfGrammar2 = new Grammar[j];
      System.arraycopy(arrayOfGrammar1, 0, arrayOfGrammar2, 0, j);
      return arrayOfGrammar2;
    }
  }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar)
  {
    if (!fPoolIsLocked) {
      for (int i = 0; i < paramArrayOfGrammar.length; i++) {
        putGrammar(paramArrayOfGrammar[i]);
      }
    }
  }
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    return getGrammar(paramXMLGrammarDescription);
  }
  
  public void putGrammar(Grammar paramGrammar)
  {
    if (!fPoolIsLocked) {
      synchronized (fGrammars)
      {
        XMLGrammarDescription localXMLGrammarDescription = paramGrammar.getGrammarDescription();
        int i = hashCode(localXMLGrammarDescription);
        int j = (i & 0x7FFFFFFF) % fGrammars.length;
        for (Entry localEntry = fGrammars[j]; localEntry != null; localEntry = next) {
          if ((hash == i) && (equals(desc, localXMLGrammarDescription)))
          {
            grammar = paramGrammar;
            return;
          }
        }
        localEntry = new Entry(i, localXMLGrammarDescription, paramGrammar, fGrammars[j]);
        fGrammars[j] = localEntry;
        fGrammarCount += 1;
      }
    }
  }
  
  public Grammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    synchronized (fGrammars)
    {
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % fGrammars.length;
      for (Entry localEntry = fGrammars[j]; localEntry != null; localEntry = next) {
        if ((hash == i) && (equals(desc, paramXMLGrammarDescription))) {
          return grammar;
        }
      }
      return null;
    }
  }
  
  public Grammar removeGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    synchronized (fGrammars)
    {
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % fGrammars.length;
      Entry localEntry1 = fGrammars[j];
      Entry localEntry2 = null;
      while (localEntry1 != null)
      {
        if ((hash == i) && (equals(desc, paramXMLGrammarDescription)))
        {
          if (localEntry2 != null) {
            next = next;
          } else {
            fGrammars[j] = next;
          }
          Grammar localGrammar = grammar;
          grammar = null;
          fGrammarCount -= 1;
          return localGrammar;
        }
        localEntry2 = localEntry1;
        localEntry1 = next;
      }
      return null;
    }
  }
  
  public boolean containsGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    synchronized (fGrammars)
    {
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % fGrammars.length;
      for (Entry localEntry = fGrammars[j]; localEntry != null; localEntry = next) {
        if ((hash == i) && (equals(desc, paramXMLGrammarDescription))) {
          return true;
        }
      }
      return false;
    }
  }
  
  public void lockPool()
  {
    fPoolIsLocked = true;
  }
  
  public void unlockPool()
  {
    fPoolIsLocked = false;
  }
  
  public void clear()
  {
    for (int i = 0; i < fGrammars.length; i++) {
      if (fGrammars[i] != null)
      {
        fGrammars[i].clear();
        fGrammars[i] = null;
      }
    }
    fGrammarCount = 0;
  }
  
  public boolean equals(XMLGrammarDescription paramXMLGrammarDescription1, XMLGrammarDescription paramXMLGrammarDescription2)
  {
    return paramXMLGrammarDescription1.equals(paramXMLGrammarDescription2);
  }
  
  public int hashCode(XMLGrammarDescription paramXMLGrammarDescription)
  {
    return paramXMLGrammarDescription.hashCode();
  }
  
  protected static final class Entry
  {
    public int hash;
    public XMLGrammarDescription desc;
    public Grammar grammar;
    public Entry next;
    
    protected Entry(int paramInt, XMLGrammarDescription paramXMLGrammarDescription, Grammar paramGrammar, Entry paramEntry)
    {
      hash = paramInt;
      desc = paramXMLGrammarDescription;
      grammar = paramGrammar;
      next = paramEntry;
    }
    
    protected void clear()
    {
      desc = null;
      grammar = null;
      if (next != null)
      {
        next.clear();
        next = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\XMLGrammarPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */