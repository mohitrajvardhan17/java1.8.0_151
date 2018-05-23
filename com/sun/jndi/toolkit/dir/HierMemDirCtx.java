package com.sun.jndi.toolkit.dir;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeModificationException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirStateFactory.Result;
import javax.naming.spi.DirectoryManager;

public class HierMemDirCtx
  implements DirContext
{
  private static final boolean debug = false;
  private static final NameParser defaultParser = new HierarchicalNameParser();
  protected Hashtable<String, Object> myEnv;
  protected Hashtable<Name, Object> bindings;
  protected Attributes attrs;
  protected boolean ignoreCase = false;
  protected NamingException readOnlyEx = null;
  protected NameParser myParser = defaultParser;
  private boolean alwaysUseFactory;
  
  public void close()
    throws NamingException
  {
    myEnv = null;
    bindings = null;
    attrs = null;
  }
  
  public String getNameInNamespace()
    throws NamingException
  {
    throw new OperationNotSupportedException("Cannot determine full name");
  }
  
  public HierMemDirCtx()
  {
    this(null, false, false);
  }
  
  public HierMemDirCtx(boolean paramBoolean)
  {
    this(null, paramBoolean, false);
  }
  
  public HierMemDirCtx(Hashtable<String, Object> paramHashtable, boolean paramBoolean)
  {
    this(paramHashtable, paramBoolean, false);
  }
  
  protected HierMemDirCtx(Hashtable<String, Object> paramHashtable, boolean paramBoolean1, boolean paramBoolean2)
  {
    myEnv = paramHashtable;
    ignoreCase = paramBoolean1;
    init();
    alwaysUseFactory = paramBoolean2;
  }
  
  private void init()
  {
    attrs = new BasicAttributes(ignoreCase);
    bindings = new Hashtable(11, 0.75F);
  }
  
  public Object lookup(String paramString)
    throws NamingException
  {
    return lookup(myParser.parse(paramString));
  }
  
  public Object lookup(Name paramName)
    throws NamingException
  {
    return doLookup(paramName, alwaysUseFactory);
  }
  
  public Object doLookup(Name paramName, boolean paramBoolean)
    throws NamingException
  {
    Object localObject = null;
    paramName = canonizeName(paramName);
    switch (paramName.size())
    {
    case 0: 
      localObject = this;
      break;
    case 1: 
      localObject = bindings.get(paramName);
      break;
    default: 
      HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)bindings.get(paramName.getPrefix(1));
      if (localHierMemDirCtx == null) {
        localObject = null;
      } else {
        localObject = localHierMemDirCtx.doLookup(paramName.getSuffix(1), false);
      }
      break;
    }
    if (localObject == null) {
      throw new NameNotFoundException(paramName.toString());
    }
    if (paramBoolean) {
      try
      {
        return DirectoryManager.getObjectInstance(localObject, paramName, this, myEnv, (localObject instanceof HierMemDirCtx) ? attrs : null);
      }
      catch (NamingException localNamingException1)
      {
        throw localNamingException1;
      }
      catch (Exception localException)
      {
        NamingException localNamingException2 = new NamingException("Problem calling getObjectInstance");
        localNamingException2.setRootCause(localException);
        throw localNamingException2;
      }
    }
    return localObject;
  }
  
  public void bind(String paramString, Object paramObject)
    throws NamingException
  {
    bind(myParser.parse(paramString), paramObject);
  }
  
  public void bind(Name paramName, Object paramObject)
    throws NamingException
  {
    doBind(paramName, paramObject, null, alwaysUseFactory);
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    bind(myParser.parse(paramString), paramObject, paramAttributes);
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    doBind(paramName, paramObject, paramAttributes, alwaysUseFactory);
  }
  
  protected void doBind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean)
    throws NamingException
  {
    if (paramName.isEmpty()) {
      throw new InvalidNameException("Cannot bind empty name");
    }
    if (paramBoolean)
    {
      localObject = DirectoryManager.getStateToBind(paramObject, paramName, this, myEnv, paramAttributes);
      paramObject = ((DirStateFactory.Result)localObject).getObject();
      paramAttributes = ((DirStateFactory.Result)localObject).getAttributes();
    }
    Object localObject = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    ((HierMemDirCtx)localObject).doBindAux(getLeafName(paramName), paramObject);
    if ((paramAttributes != null) && (paramAttributes.size() > 0)) {
      modifyAttributes(paramName, 1, paramAttributes);
    }
  }
  
  protected void doBindAux(Name paramName, Object paramObject)
    throws NamingException
  {
    if (readOnlyEx != null) {
      throw ((NamingException)readOnlyEx.fillInStackTrace());
    }
    if (bindings.get(paramName) != null) {
      throw new NameAlreadyBoundException(paramName.toString());
    }
    if ((paramObject instanceof HierMemDirCtx)) {
      bindings.put(paramName, paramObject);
    } else {
      throw new SchemaViolationException("This context only supports binding objects of it's own kind");
    }
  }
  
  public void rebind(String paramString, Object paramObject)
    throws NamingException
  {
    rebind(myParser.parse(paramString), paramObject);
  }
  
  public void rebind(Name paramName, Object paramObject)
    throws NamingException
  {
    doRebind(paramName, paramObject, null, alwaysUseFactory);
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    rebind(myParser.parse(paramString), paramObject, paramAttributes);
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    doRebind(paramName, paramObject, paramAttributes, alwaysUseFactory);
  }
  
  protected void doRebind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean)
    throws NamingException
  {
    if (paramName.isEmpty()) {
      throw new InvalidNameException("Cannot rebind empty name");
    }
    if (paramBoolean)
    {
      localObject = DirectoryManager.getStateToBind(paramObject, paramName, this, myEnv, paramAttributes);
      paramObject = ((DirStateFactory.Result)localObject).getObject();
      paramAttributes = ((DirStateFactory.Result)localObject).getAttributes();
    }
    Object localObject = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    ((HierMemDirCtx)localObject).doRebindAux(getLeafName(paramName), paramObject);
    if ((paramAttributes != null) && (paramAttributes.size() > 0)) {
      modifyAttributes(paramName, 1, paramAttributes);
    }
  }
  
  protected void doRebindAux(Name paramName, Object paramObject)
    throws NamingException
  {
    if (readOnlyEx != null) {
      throw ((NamingException)readOnlyEx.fillInStackTrace());
    }
    if ((paramObject instanceof HierMemDirCtx)) {
      bindings.put(paramName, paramObject);
    } else {
      throw new SchemaViolationException("This context only supports binding objects of it's own kind");
    }
  }
  
  public void unbind(String paramString)
    throws NamingException
  {
    unbind(myParser.parse(paramString));
  }
  
  public void unbind(Name paramName)
    throws NamingException
  {
    if (paramName.isEmpty()) {
      throw new InvalidNameException("Cannot unbind empty name");
    }
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    localHierMemDirCtx.doUnbind(getLeafName(paramName));
  }
  
  protected void doUnbind(Name paramName)
    throws NamingException
  {
    if (readOnlyEx != null) {
      throw ((NamingException)readOnlyEx.fillInStackTrace());
    }
    bindings.remove(paramName);
  }
  
  public void rename(String paramString1, String paramString2)
    throws NamingException
  {
    rename(myParser.parse(paramString1), myParser.parse(paramString2));
  }
  
  public void rename(Name paramName1, Name paramName2)
    throws NamingException
  {
    if ((paramName2.isEmpty()) || (paramName1.isEmpty())) {
      throw new InvalidNameException("Cannot rename empty name");
    }
    if (!getInternalName(paramName2).equals(getInternalName(paramName1))) {
      throw new InvalidNameException("Cannot rename across contexts");
    }
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName2), false);
    localHierMemDirCtx.doRename(getLeafName(paramName1), getLeafName(paramName2));
  }
  
  protected void doRename(Name paramName1, Name paramName2)
    throws NamingException
  {
    if (readOnlyEx != null) {
      throw ((NamingException)readOnlyEx.fillInStackTrace());
    }
    paramName1 = canonizeName(paramName1);
    paramName2 = canonizeName(paramName2);
    if (bindings.get(paramName2) != null) {
      throw new NameAlreadyBoundException(paramName2.toString());
    }
    Object localObject = bindings.remove(paramName1);
    if (localObject == null) {
      throw new NameNotFoundException(paramName1.toString());
    }
    bindings.put(paramName2, localObject);
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString)
    throws NamingException
  {
    return list(myParser.parse(paramString));
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return localHierMemDirCtx.doList();
  }
  
  protected NamingEnumeration<NameClassPair> doList()
    throws NamingException
  {
    return new FlatNames(bindings.keys());
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString)
    throws NamingException
  {
    return listBindings(myParser.parse(paramString));
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return localHierMemDirCtx.doListBindings(alwaysUseFactory);
  }
  
  protected NamingEnumeration<Binding> doListBindings(boolean paramBoolean)
    throws NamingException
  {
    return new FlatBindings(bindings, myEnv, paramBoolean);
  }
  
  public void destroySubcontext(String paramString)
    throws NamingException
  {
    destroySubcontext(myParser.parse(paramString));
  }
  
  public void destroySubcontext(Name paramName)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    localHierMemDirCtx.doDestroySubcontext(getLeafName(paramName));
  }
  
  protected void doDestroySubcontext(Name paramName)
    throws NamingException
  {
    if (readOnlyEx != null) {
      throw ((NamingException)readOnlyEx.fillInStackTrace());
    }
    paramName = canonizeName(paramName);
    bindings.remove(paramName);
  }
  
  public Context createSubcontext(String paramString)
    throws NamingException
  {
    return createSubcontext(myParser.parse(paramString));
  }
  
  public Context createSubcontext(Name paramName)
    throws NamingException
  {
    return createSubcontext(paramName, null);
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    return createSubcontext(myParser.parse(paramString), paramAttributes);
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    return localHierMemDirCtx.doCreateSubcontext(getLeafName(paramName), paramAttributes);
  }
  
  protected DirContext doCreateSubcontext(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    if (readOnlyEx != null) {
      throw ((NamingException)readOnlyEx.fillInStackTrace());
    }
    paramName = canonizeName(paramName);
    if (bindings.get(paramName) != null) {
      throw new NameAlreadyBoundException(paramName.toString());
    }
    HierMemDirCtx localHierMemDirCtx = createNewCtx();
    bindings.put(paramName, localHierMemDirCtx);
    if (paramAttributes != null) {
      localHierMemDirCtx.modifyAttributes("", 1, paramAttributes);
    }
    return localHierMemDirCtx;
  }
  
  public Object lookupLink(String paramString)
    throws NamingException
  {
    return lookupLink(myParser.parse(paramString));
  }
  
  public Object lookupLink(Name paramName)
    throws NamingException
  {
    return lookup(paramName);
  }
  
  public NameParser getNameParser(String paramString)
    throws NamingException
  {
    return myParser;
  }
  
  public NameParser getNameParser(Name paramName)
    throws NamingException
  {
    return myParser;
  }
  
  public String composeName(String paramString1, String paramString2)
    throws NamingException
  {
    Name localName = composeName(new CompositeName(paramString1), new CompositeName(paramString2));
    return localName.toString();
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    paramName1 = canonizeName(paramName1);
    paramName2 = canonizeName(paramName2);
    Name localName = (Name)paramName2.clone();
    localName.addAll(paramName1);
    return localName;
  }
  
  public Object addToEnvironment(String paramString, Object paramObject)
    throws NamingException
  {
    myEnv = (myEnv == null ? new Hashtable(11, 0.75F) : (Hashtable)myEnv.clone());
    return myEnv.put(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString)
    throws NamingException
  {
    if (myEnv == null) {
      return null;
    }
    myEnv = ((Hashtable)myEnv.clone());
    return myEnv.remove(paramString);
  }
  
  public Hashtable<String, Object> getEnvironment()
    throws NamingException
  {
    if (myEnv == null) {
      return new Hashtable(5, 0.75F);
    }
    return (Hashtable)myEnv.clone();
  }
  
  public Attributes getAttributes(String paramString)
    throws NamingException
  {
    return getAttributes(myParser.parse(paramString));
  }
  
  public Attributes getAttributes(Name paramName)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return localHierMemDirCtx.doGetAttributes();
  }
  
  protected Attributes doGetAttributes()
    throws NamingException
  {
    return (Attributes)attrs.clone();
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString)
    throws NamingException
  {
    return getAttributes(myParser.parse(paramString), paramArrayOfString);
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return localHierMemDirCtx.doGetAttributes(paramArrayOfString);
  }
  
  protected Attributes doGetAttributes(String[] paramArrayOfString)
    throws NamingException
  {
    if (paramArrayOfString == null) {
      return doGetAttributes();
    }
    BasicAttributes localBasicAttributes = new BasicAttributes(ignoreCase);
    Attribute localAttribute = null;
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      localAttribute = attrs.get(paramArrayOfString[i]);
      if (localAttribute != null) {
        localBasicAttributes.put(localAttribute);
      }
    }
    return localBasicAttributes;
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    modifyAttributes(myParser.parse(paramString), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    if ((paramAttributes == null) || (paramAttributes.size() == 0)) {
      throw new IllegalArgumentException("Cannot modify without an attribute");
    }
    NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
    ModificationItem[] arrayOfModificationItem = new ModificationItem[paramAttributes.size()];
    for (int i = 0; (i < arrayOfModificationItem.length) && (localNamingEnumeration.hasMoreElements()); i++) {
      arrayOfModificationItem[i] = new ModificationItem(paramInt, (Attribute)localNamingEnumeration.next());
    }
    modifyAttributes(paramName, arrayOfModificationItem);
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    modifyAttributes(myParser.parse(paramString), paramArrayOfModificationItem);
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    localHierMemDirCtx.doModifyAttributes(paramArrayOfModificationItem);
  }
  
  protected void doModifyAttributes(ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    if (readOnlyEx != null) {
      throw ((NamingException)readOnlyEx.fillInStackTrace());
    }
    applyMods(paramArrayOfModificationItem, attrs);
  }
  
  protected static Attributes applyMods(ModificationItem[] paramArrayOfModificationItem, Attributes paramAttributes)
    throws NamingException
  {
    for (int i = 0; i < paramArrayOfModificationItem.length; i++)
    {
      ModificationItem localModificationItem = paramArrayOfModificationItem[i];
      Attribute localAttribute2 = localModificationItem.getAttribute();
      Attribute localAttribute1;
      NamingEnumeration localNamingEnumeration;
      switch (localModificationItem.getModificationOp())
      {
      case 1: 
        localAttribute1 = paramAttributes.get(localAttribute2.getID());
        if (localAttribute1 == null) {
          paramAttributes.put((Attribute)localAttribute2.clone());
        } else {
          localNamingEnumeration = localAttribute2.getAll();
        }
        break;
      case 2: 
      case 3: 
      default: 
        while (localNamingEnumeration.hasMore())
        {
          localAttribute1.add(localNamingEnumeration.next());
          continue;
          if (localAttribute2.size() == 0)
          {
            paramAttributes.remove(localAttribute2.getID());
          }
          else
          {
            paramAttributes.put((Attribute)localAttribute2.clone());
            break;
            localAttribute1 = paramAttributes.get(localAttribute2.getID());
            if (localAttribute1 != null) {
              if (localAttribute2.size() == 0)
              {
                paramAttributes.remove(localAttribute2.getID());
              }
              else
              {
                localNamingEnumeration = localAttribute2.getAll();
                while (localNamingEnumeration.hasMore()) {
                  localAttribute1.remove(localNamingEnumeration.next());
                }
                if (localAttribute1.size() == 0)
                {
                  paramAttributes.remove(localAttribute2.getID());
                  break;
                  throw new AttributeModificationException("Unknown mod_op");
                }
              }
            }
          }
        }
      }
    }
    return paramAttributes;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    return search(paramString, paramAttributes, null);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    return search(paramName, paramAttributes, null);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    return search(myParser.parse(paramString), paramAttributes, paramArrayOfString);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    SearchControls localSearchControls = new SearchControls();
    localSearchControls.setReturningAttributes(paramArrayOfString);
    return new LazySearchEnumerationImpl(localHierMemDirCtx.doListBindings(false), new ContainmentFilter(paramAttributes), localSearchControls, this, myEnv, false);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls)
    throws NamingException
  {
    DirContext localDirContext = (DirContext)doLookup(paramName, false);
    SearchFilter localSearchFilter = new SearchFilter(paramString);
    return new LazySearchEnumerationImpl(new HierContextEnumerator(localDirContext, paramSearchControls != null ? paramSearchControls.getSearchScope() : 1), localSearchFilter, paramSearchControls, this, myEnv, alwaysUseFactory);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    String str = SearchFilter.format(paramString, paramArrayOfObject);
    return search(paramName, str, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls)
    throws NamingException
  {
    return search(myParser.parse(paramString1), paramString2, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    return search(myParser.parse(paramString1), paramString2, paramArrayOfObject, paramSearchControls);
  }
  
  protected HierMemDirCtx createNewCtx()
    throws NamingException
  {
    return new HierMemDirCtx(myEnv, ignoreCase);
  }
  
  protected Name canonizeName(Name paramName)
    throws NamingException
  {
    Object localObject = paramName;
    if (!(paramName instanceof HierarchicalName))
    {
      localObject = new HierarchicalName();
      int i = paramName.size();
      for (int j = 0; j < i; j++) {
        ((Name)localObject).add(j, paramName.get(j));
      }
    }
    return (Name)localObject;
  }
  
  protected Name getInternalName(Name paramName)
    throws NamingException
  {
    return paramName.getPrefix(paramName.size() - 1);
  }
  
  protected Name getLeafName(Name paramName)
    throws NamingException
  {
    return paramName.getSuffix(paramName.size() - 1);
  }
  
  public DirContext getSchema(String paramString)
    throws NamingException
  {
    throw new OperationNotSupportedException();
  }
  
  public DirContext getSchema(Name paramName)
    throws NamingException
  {
    throw new OperationNotSupportedException();
  }
  
  public DirContext getSchemaClassDefinition(String paramString)
    throws NamingException
  {
    throw new OperationNotSupportedException();
  }
  
  public DirContext getSchemaClassDefinition(Name paramName)
    throws NamingException
  {
    throw new OperationNotSupportedException();
  }
  
  public void setReadOnly(NamingException paramNamingException)
  {
    readOnlyEx = paramNamingException;
  }
  
  public void setIgnoreCase(boolean paramBoolean)
  {
    ignoreCase = paramBoolean;
  }
  
  public void setNameParser(NameParser paramNameParser)
  {
    myParser = paramNameParser;
  }
  
  private abstract class BaseFlatNames<T>
    implements NamingEnumeration<T>
  {
    Enumeration<Name> names;
    
    BaseFlatNames()
    {
      Enumeration localEnumeration;
      names = localEnumeration;
    }
    
    public final boolean hasMoreElements()
    {
      try
      {
        return hasMore();
      }
      catch (NamingException localNamingException) {}
      return false;
    }
    
    public final boolean hasMore()
      throws NamingException
    {
      return names.hasMoreElements();
    }
    
    public final T nextElement()
    {
      try
      {
        return (T)next();
      }
      catch (NamingException localNamingException)
      {
        throw new NoSuchElementException(localNamingException.toString());
      }
    }
    
    public abstract T next()
      throws NamingException;
    
    public final void close()
    {
      names = null;
    }
  }
  
  private final class FlatBindings
    extends HierMemDirCtx.BaseFlatNames<Binding>
  {
    private Hashtable<Name, Object> bds;
    private Hashtable<String, Object> env;
    private boolean useFactory;
    
    FlatBindings(Hashtable<String, Object> paramHashtable, boolean paramBoolean)
    {
      super(paramHashtable.keys());
      env = paramBoolean;
      bds = paramHashtable;
      boolean bool;
      useFactory = bool;
    }
    
    public Binding next()
      throws NamingException
    {
      Name localName = (Name)names.nextElement();
      HierMemDirCtx localHierMemDirCtx = (HierMemDirCtx)bds.get(localName);
      Object localObject = localHierMemDirCtx;
      if (useFactory)
      {
        Attributes localAttributes = localHierMemDirCtx.getAttributes("");
        try
        {
          localObject = DirectoryManager.getObjectInstance(localHierMemDirCtx, localName, HierMemDirCtx.this, env, localAttributes);
        }
        catch (NamingException localNamingException1)
        {
          throw localNamingException1;
        }
        catch (Exception localException)
        {
          NamingException localNamingException2 = new NamingException("Problem calling getObjectInstance");
          localNamingException2.setRootCause(localException);
          throw localNamingException2;
        }
      }
      return new Binding(localName.toString(), localObject);
    }
  }
  
  private final class FlatNames
    extends HierMemDirCtx.BaseFlatNames<NameClassPair>
  {
    FlatNames()
    {
      super(localEnumeration);
    }
    
    public NameClassPair next()
      throws NamingException
    {
      Name localName = (Name)names.nextElement();
      String str = bindings.get(localName).getClass().getName();
      return new NameClassPair(localName.toString(), str);
    }
  }
  
  public class HierContextEnumerator
    extends ContextEnumerator
  {
    public HierContextEnumerator(Context paramContext, int paramInt)
      throws NamingException
    {
      super(paramInt);
    }
    
    protected HierContextEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
      throws NamingException
    {
      super(paramInt, paramString, paramBoolean);
    }
    
    protected NamingEnumeration<Binding> getImmediateChildren(Context paramContext)
      throws NamingException
    {
      return ((HierMemDirCtx)paramContext).doListBindings(false);
    }
    
    protected ContextEnumerator newEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
      throws NamingException
    {
      return new HierContextEnumerator(HierMemDirCtx.this, paramContext, paramInt, paramString, paramBoolean);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\HierMemDirCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */