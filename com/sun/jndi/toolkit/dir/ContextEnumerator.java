package com.sun.jndi.toolkit.dir;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class ContextEnumerator
  implements NamingEnumeration<Binding>
{
  private static boolean debug = false;
  private NamingEnumeration<Binding> children = null;
  private Binding currentChild = null;
  private boolean currentReturned = false;
  private Context root;
  private ContextEnumerator currentChildEnum = null;
  private boolean currentChildExpanded = false;
  private boolean rootProcessed = false;
  private int scope = 2;
  private String contextName = "";
  
  public ContextEnumerator(Context paramContext)
    throws NamingException
  {
    this(paramContext, 2);
  }
  
  public ContextEnumerator(Context paramContext, int paramInt)
    throws NamingException
  {
    this(paramContext, paramInt, "", paramInt != 1);
  }
  
  protected ContextEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
    throws NamingException
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("null context passed");
    }
    root = paramContext;
    if (paramInt != 0) {
      children = getImmediateChildren(paramContext);
    }
    scope = paramInt;
    contextName = paramString;
    rootProcessed = (!paramBoolean);
    prepNextChild();
  }
  
  protected NamingEnumeration<Binding> getImmediateChildren(Context paramContext)
    throws NamingException
  {
    return paramContext.listBindings("");
  }
  
  protected ContextEnumerator newEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
    throws NamingException
  {
    return new ContextEnumerator(paramContext, paramInt, paramString, paramBoolean);
  }
  
  public boolean hasMore()
    throws NamingException
  {
    return (!rootProcessed) || ((scope != 0) && (hasMoreDescendants()));
  }
  
  public boolean hasMoreElements()
  {
    try
    {
      return hasMore();
    }
    catch (NamingException localNamingException) {}
    return false;
  }
  
  public Binding nextElement()
  {
    try
    {
      return next();
    }
    catch (NamingException localNamingException)
    {
      throw new NoSuchElementException(localNamingException.toString());
    }
  }
  
  public Binding next()
    throws NamingException
  {
    if (!rootProcessed)
    {
      rootProcessed = true;
      return new Binding("", root.getClass().getName(), root, true);
    }
    if ((scope != 0) && (hasMoreDescendants())) {
      return getNextDescendant();
    }
    throw new NoSuchElementException();
  }
  
  public void close()
    throws NamingException
  {
    root = null;
  }
  
  private boolean hasMoreChildren()
    throws NamingException
  {
    return (children != null) && (children.hasMore());
  }
  
  private Binding getNextChild()
    throws NamingException
  {
    Binding localBinding1 = (Binding)children.next();
    Binding localBinding2 = null;
    if ((localBinding1.isRelative()) && (!contextName.equals("")))
    {
      NameParser localNameParser = root.getNameParser("");
      Name localName = localNameParser.parse(contextName);
      localName.add(localBinding1.getName());
      if (debug) {
        System.out.println("ContextEnumerator: adding " + localName);
      }
      localBinding2 = new Binding(localName.toString(), localBinding1.getClassName(), localBinding1.getObject(), localBinding1.isRelative());
    }
    else
    {
      if (debug) {
        System.out.println("ContextEnumerator: using old binding");
      }
      localBinding2 = localBinding1;
    }
    return localBinding2;
  }
  
  private boolean hasMoreDescendants()
    throws NamingException
  {
    if (!currentReturned)
    {
      if (debug) {
        System.out.println("hasMoreDescendants returning " + (currentChild != null));
      }
      return currentChild != null;
    }
    if ((currentChildExpanded) && (currentChildEnum.hasMore()))
    {
      if (debug) {
        System.out.println("hasMoreDescendants returning true");
      }
      return true;
    }
    if (debug) {
      System.out.println("hasMoreDescendants returning hasMoreChildren");
    }
    return hasMoreChildren();
  }
  
  private Binding getNextDescendant()
    throws NamingException
  {
    if (!currentReturned)
    {
      if (debug) {
        System.out.println("getNextDescedant: simple case");
      }
      currentReturned = true;
      return currentChild;
    }
    if ((currentChildExpanded) && (currentChildEnum.hasMore()))
    {
      if (debug) {
        System.out.println("getNextDescedant: expanded case");
      }
      return currentChildEnum.next();
    }
    if (debug) {
      System.out.println("getNextDescedant: next case");
    }
    prepNextChild();
    return getNextDescendant();
  }
  
  private void prepNextChild()
    throws NamingException
  {
    if (hasMoreChildren())
    {
      try
      {
        currentChild = getNextChild();
        currentReturned = false;
      }
      catch (NamingException localNamingException)
      {
        if (debug) {
          System.out.println(localNamingException);
        }
        if (debug) {
          localNamingException.printStackTrace();
        }
      }
    }
    else
    {
      currentChild = null;
      return;
    }
    if ((scope == 2) && ((currentChild.getObject() instanceof Context)))
    {
      currentChildEnum = newEnumerator((Context)currentChild.getObject(), scope, currentChild.getName(), false);
      currentChildExpanded = true;
      if (debug) {
        System.out.println("prepNextChild: expanded");
      }
    }
    else
    {
      currentChildExpanded = false;
      currentChildEnum = null;
      if (debug) {
        System.out.println("prepNextChild: normal");
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\ContextEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */