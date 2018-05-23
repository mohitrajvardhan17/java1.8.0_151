package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class NameSpaceSymbTable
{
  private static final String XMLNS = "xmlns";
  private static final SymbMap initialMap = new SymbMap();
  private SymbMap symb = (SymbMap)initialMap.clone();
  private List<SymbMap> level = new ArrayList();
  private boolean cloned = true;
  
  public NameSpaceSymbTable() {}
  
  public void getUnrenderedNodes(Collection<Attr> paramCollection)
  {
    Iterator localIterator = symb.entrySet().iterator();
    while (localIterator.hasNext())
    {
      NameSpaceSymbEntry localNameSpaceSymbEntry = (NameSpaceSymbEntry)localIterator.next();
      if ((!rendered) && (n != null))
      {
        localNameSpaceSymbEntry = (NameSpaceSymbEntry)localNameSpaceSymbEntry.clone();
        needsClone();
        symb.put(prefix, localNameSpaceSymbEntry);
        lastrendered = uri;
        rendered = true;
        paramCollection.add(n);
      }
    }
  }
  
  public void outputNodePush()
  {
    push();
  }
  
  public void outputNodePop()
  {
    pop();
  }
  
  public void push()
  {
    level.add(null);
    cloned = false;
  }
  
  public void pop()
  {
    int i = level.size() - 1;
    Object localObject = level.remove(i);
    if (localObject != null)
    {
      symb = ((SymbMap)localObject);
      if (i == 0) {
        cloned = false;
      } else {
        cloned = (level.get(i - 1) != symb);
      }
    }
    else
    {
      cloned = false;
    }
  }
  
  final void needsClone()
  {
    if (!cloned)
    {
      level.set(level.size() - 1, symb);
      symb = ((SymbMap)symb.clone());
      cloned = true;
    }
  }
  
  public Attr getMapping(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = symb.get(paramString);
    if (localNameSpaceSymbEntry == null) {
      return null;
    }
    if (rendered) {
      return null;
    }
    localNameSpaceSymbEntry = (NameSpaceSymbEntry)localNameSpaceSymbEntry.clone();
    needsClone();
    symb.put(paramString, localNameSpaceSymbEntry);
    rendered = true;
    lastrendered = uri;
    return n;
  }
  
  public Attr getMappingWithoutRendered(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = symb.get(paramString);
    if (localNameSpaceSymbEntry == null) {
      return null;
    }
    if (rendered) {
      return null;
    }
    return n;
  }
  
  public boolean addMapping(String paramString1, String paramString2, Attr paramAttr)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry1 = symb.get(paramString1);
    if ((localNameSpaceSymbEntry1 != null) && (paramString2.equals(uri))) {
      return false;
    }
    NameSpaceSymbEntry localNameSpaceSymbEntry2 = new NameSpaceSymbEntry(paramString2, paramAttr, false, paramString1);
    needsClone();
    symb.put(paramString1, localNameSpaceSymbEntry2);
    if (localNameSpaceSymbEntry1 != null)
    {
      lastrendered = lastrendered;
      if ((lastrendered != null) && (lastrendered.equals(paramString2))) {
        rendered = true;
      }
    }
    return true;
  }
  
  public Node addMappingAndRender(String paramString1, String paramString2, Attr paramAttr)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry1 = symb.get(paramString1);
    if ((localNameSpaceSymbEntry1 != null) && (paramString2.equals(uri)))
    {
      if (!rendered)
      {
        localNameSpaceSymbEntry1 = (NameSpaceSymbEntry)localNameSpaceSymbEntry1.clone();
        needsClone();
        symb.put(paramString1, localNameSpaceSymbEntry1);
        lastrendered = paramString2;
        rendered = true;
        return n;
      }
      return null;
    }
    NameSpaceSymbEntry localNameSpaceSymbEntry2 = new NameSpaceSymbEntry(paramString2, paramAttr, true, paramString1);
    lastrendered = paramString2;
    needsClone();
    symb.put(paramString1, localNameSpaceSymbEntry2);
    if ((localNameSpaceSymbEntry1 != null) && (lastrendered != null) && (lastrendered.equals(paramString2)))
    {
      rendered = true;
      return null;
    }
    return n;
  }
  
  public int getLevel()
  {
    return level.size();
  }
  
  public void removeMapping(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = symb.get(paramString);
    if (localNameSpaceSymbEntry != null)
    {
      needsClone();
      symb.put(paramString, null);
    }
  }
  
  public void removeMappingIfNotRender(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = symb.get(paramString);
    if ((localNameSpaceSymbEntry != null) && (!rendered))
    {
      needsClone();
      symb.put(paramString, null);
    }
  }
  
  public boolean removeMappingIfRender(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = symb.get(paramString);
    if ((localNameSpaceSymbEntry != null) && (rendered))
    {
      needsClone();
      symb.put(paramString, null);
    }
    return false;
  }
  
  static
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = new NameSpaceSymbEntry("", null, true, "xmlns");
    lastrendered = "";
    initialMap.put("xmlns", localNameSpaceSymbEntry);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\NameSpaceSymbTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */