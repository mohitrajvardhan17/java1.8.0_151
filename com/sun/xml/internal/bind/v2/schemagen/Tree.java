package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeDefParticle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

abstract class Tree
{
  Tree() {}
  
  Tree makeOptional(boolean paramBoolean)
  {
    return paramBoolean ? new Optional(this, null) : this;
  }
  
  Tree makeRepeated(boolean paramBoolean)
  {
    return paramBoolean ? new Repeated(this, null) : this;
  }
  
  static Tree makeGroup(GroupKind paramGroupKind, List<Tree> paramList)
  {
    if (paramList.size() == 1) {
      return (Tree)paramList.get(0);
    }
    ArrayList localArrayList = new ArrayList(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Tree localTree = (Tree)localIterator.next();
      if ((localTree instanceof Group))
      {
        Group localGroup = (Group)localTree;
        if (kind == paramGroupKind)
        {
          localArrayList.addAll(Arrays.asList(children));
          continue;
        }
      }
      localArrayList.add(localTree);
    }
    return new Group(paramGroupKind, (Tree[])localArrayList.toArray(new Tree[localArrayList.size()]), null);
  }
  
  abstract boolean isNullable();
  
  boolean canBeTopLevel()
  {
    return false;
  }
  
  protected abstract void write(ContentModelContainer paramContentModelContainer, boolean paramBoolean1, boolean paramBoolean2);
  
  protected void write(TypeDefParticle paramTypeDefParticle)
  {
    if (canBeTopLevel()) {
      write((ContentModelContainer)paramTypeDefParticle._cast(ContentModelContainer.class), false, false);
    } else {
      new Group(GroupKind.SEQUENCE, new Tree[] { this }, null).write(paramTypeDefParticle);
    }
  }
  
  protected final void writeOccurs(Occurs paramOccurs, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {
      paramOccurs.minOccurs(0);
    }
    if (paramBoolean2) {
      paramOccurs.maxOccurs("unbounded");
    }
  }
  
  private static final class Group
    extends Tree
  {
    private final GroupKind kind;
    private final Tree[] children;
    
    private Group(GroupKind paramGroupKind, Tree... paramVarArgs)
    {
      kind = paramGroupKind;
      children = paramVarArgs;
    }
    
    boolean canBeTopLevel()
    {
      return true;
    }
    
    boolean isNullable()
    {
      Tree localTree;
      if (kind == GroupKind.CHOICE)
      {
        for (localTree : children) {
          if (localTree.isNullable()) {
            return true;
          }
        }
        return false;
      }
      for (localTree : children) {
        if (!localTree.isNullable()) {
          return false;
        }
      }
      return true;
    }
    
    protected void write(ContentModelContainer paramContentModelContainer, boolean paramBoolean1, boolean paramBoolean2)
    {
      Particle localParticle = kind.write(paramContentModelContainer);
      writeOccurs(localParticle, paramBoolean1, paramBoolean2);
      for (Tree localTree : children) {
        localTree.write(localParticle, false, false);
      }
    }
  }
  
  private static final class Optional
    extends Tree
  {
    private final Tree body;
    
    private Optional(Tree paramTree)
    {
      body = paramTree;
    }
    
    boolean isNullable()
    {
      return true;
    }
    
    Tree makeOptional(boolean paramBoolean)
    {
      return this;
    }
    
    protected void write(ContentModelContainer paramContentModelContainer, boolean paramBoolean1, boolean paramBoolean2)
    {
      body.write(paramContentModelContainer, true, paramBoolean2);
    }
  }
  
  private static final class Repeated
    extends Tree
  {
    private final Tree body;
    
    private Repeated(Tree paramTree)
    {
      body = paramTree;
    }
    
    boolean isNullable()
    {
      return body.isNullable();
    }
    
    Tree makeRepeated(boolean paramBoolean)
    {
      return this;
    }
    
    protected void write(ContentModelContainer paramContentModelContainer, boolean paramBoolean1, boolean paramBoolean2)
    {
      body.write(paramContentModelContainer, paramBoolean1, true);
    }
  }
  
  static abstract class Term
    extends Tree
  {
    Term() {}
    
    boolean isNullable()
    {
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\Tree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */