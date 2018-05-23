package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.dir.HierMemDirCtx;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;

final class LdapSchemaCtx
  extends HierMemDirCtx
{
  private static final boolean debug = false;
  private static final int LEAF = 0;
  private static final int SCHEMA_ROOT = 1;
  static final int OBJECTCLASS_ROOT = 2;
  static final int ATTRIBUTE_ROOT = 3;
  static final int SYNTAX_ROOT = 4;
  static final int MATCHRULE_ROOT = 5;
  static final int OBJECTCLASS = 6;
  static final int ATTRIBUTE = 7;
  static final int SYNTAX = 8;
  static final int MATCHRULE = 9;
  private SchemaInfo info = null;
  private boolean setupMode = true;
  private int objectType;
  
  static DirContext createSchemaTree(Hashtable<String, Object> paramHashtable, String paramString, LdapCtx paramLdapCtx, Attributes paramAttributes, boolean paramBoolean)
    throws NamingException
  {
    try
    {
      LdapSchemaParser localLdapSchemaParser = new LdapSchemaParser(paramBoolean);
      SchemaInfo localSchemaInfo = new SchemaInfo(paramString, paramLdapCtx, localLdapSchemaParser);
      LdapSchemaCtx localLdapSchemaCtx = new LdapSchemaCtx(1, paramHashtable, localSchemaInfo);
      LdapSchemaParser.LDAP2JNDISchema(paramAttributes, localLdapSchemaCtx);
      return localLdapSchemaCtx;
    }
    catch (NamingException localNamingException)
    {
      paramLdapCtx.close();
      throw localNamingException;
    }
  }
  
  private LdapSchemaCtx(int paramInt, Hashtable<String, Object> paramHashtable, SchemaInfo paramSchemaInfo)
  {
    super(paramHashtable, true);
    objectType = paramInt;
    info = paramSchemaInfo;
  }
  
  public void close()
    throws NamingException
  {
    info.close();
  }
  
  public final void bind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    if (!setupMode)
    {
      if (paramObject != null) {
        throw new IllegalArgumentException("obj must be null");
      }
      addServerSchema(paramAttributes);
    }
    LdapSchemaCtx localLdapSchemaCtx = (LdapSchemaCtx)super.doCreateSubcontext(paramName, paramAttributes);
  }
  
  protected final void doBind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean)
    throws NamingException
  {
    if (!setupMode) {
      throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()");
    }
    super.doBind(paramName, paramObject, paramAttributes, false);
  }
  
  public final void rebind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    try
    {
      doLookup(paramName, false);
      throw new SchemaViolationException("Cannot replace existing schema object");
    }
    catch (NameNotFoundException localNameNotFoundException)
    {
      bind(paramName, paramObject, paramAttributes);
    }
  }
  
  protected final void doRebind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean)
    throws NamingException
  {
    if (!setupMode) {
      throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()");
    }
    super.doRebind(paramName, paramObject, paramAttributes, false);
  }
  
  protected final void doUnbind(Name paramName)
    throws NamingException
  {
    if (!setupMode) {
      try
      {
        LdapSchemaCtx localLdapSchemaCtx = (LdapSchemaCtx)doLookup(paramName, false);
        deleteServerSchema(attrs);
      }
      catch (NameNotFoundException localNameNotFoundException)
      {
        return;
      }
    }
    super.doUnbind(paramName);
  }
  
  protected final void doRename(Name paramName1, Name paramName2)
    throws NamingException
  {
    if (!setupMode) {
      throw new SchemaViolationException("Cannot rename a schema object");
    }
    super.doRename(paramName1, paramName2);
  }
  
  protected final void doDestroySubcontext(Name paramName)
    throws NamingException
  {
    if (!setupMode) {
      try
      {
        LdapSchemaCtx localLdapSchemaCtx = (LdapSchemaCtx)doLookup(paramName, false);
        deleteServerSchema(attrs);
      }
      catch (NameNotFoundException localNameNotFoundException)
      {
        return;
      }
    }
    super.doDestroySubcontext(paramName);
  }
  
  final LdapSchemaCtx setup(int paramInt, String paramString, Attributes paramAttributes)
    throws NamingException
  {
    try
    {
      setupMode = true;
      LdapSchemaCtx localLdapSchemaCtx1 = (LdapSchemaCtx)super.doCreateSubcontext(new CompositeName(paramString), paramAttributes);
      objectType = paramInt;
      setupMode = false;
      LdapSchemaCtx localLdapSchemaCtx2 = localLdapSchemaCtx1;
      return localLdapSchemaCtx2;
    }
    finally
    {
      setupMode = false;
    }
  }
  
  protected final DirContext doCreateSubcontext(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    if ((paramAttributes == null) || (paramAttributes.size() == 0)) {
      throw new SchemaViolationException("Must supply attributes describing schema");
    }
    if (!setupMode) {
      addServerSchema(paramAttributes);
    }
    LdapSchemaCtx localLdapSchemaCtx = (LdapSchemaCtx)super.doCreateSubcontext(paramName, paramAttributes);
    return localLdapSchemaCtx;
  }
  
  private static final Attributes deepClone(Attributes paramAttributes)
    throws NamingException
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
    while (localNamingEnumeration.hasMore()) {
      localBasicAttributes.put((Attribute)((Attribute)localNamingEnumeration.next()).clone());
    }
    return localBasicAttributes;
  }
  
  protected final void doModifyAttributes(ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    if (setupMode)
    {
      super.doModifyAttributes(paramArrayOfModificationItem);
    }
    else
    {
      Attributes localAttributes = deepClone(attrs);
      applyMods(paramArrayOfModificationItem, localAttributes);
      modifyServerSchema(attrs, localAttributes);
      attrs = localAttributes;
    }
  }
  
  protected final HierMemDirCtx createNewCtx()
  {
    LdapSchemaCtx localLdapSchemaCtx = new LdapSchemaCtx(0, myEnv, info);
    return localLdapSchemaCtx;
  }
  
  private final void addServerSchema(Attributes paramAttributes)
    throws NamingException
  {
    Attribute localAttribute;
    switch (objectType)
    {
    case 2: 
      localAttribute = info.parser.stringifyObjDesc(paramAttributes);
      break;
    case 3: 
      localAttribute = info.parser.stringifyAttrDesc(paramAttributes);
      break;
    case 4: 
      localAttribute = info.parser.stringifySyntaxDesc(paramAttributes);
      break;
    case 5: 
      localAttribute = info.parser.stringifyMatchRuleDesc(paramAttributes);
      break;
    case 1: 
      throw new SchemaViolationException("Cannot create new entry under schema root");
    default: 
      throw new SchemaViolationException("Cannot create child of schema object");
    }
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    localBasicAttributes.put(localAttribute);
    info.modifyAttributes(myEnv, 1, localBasicAttributes);
  }
  
  private final void deleteServerSchema(Attributes paramAttributes)
    throws NamingException
  {
    Attribute localAttribute;
    switch (objectType)
    {
    case 2: 
      localAttribute = info.parser.stringifyObjDesc(paramAttributes);
      break;
    case 3: 
      localAttribute = info.parser.stringifyAttrDesc(paramAttributes);
      break;
    case 4: 
      localAttribute = info.parser.stringifySyntaxDesc(paramAttributes);
      break;
    case 5: 
      localAttribute = info.parser.stringifyMatchRuleDesc(paramAttributes);
      break;
    case 1: 
      throw new SchemaViolationException("Cannot delete schema root");
    default: 
      throw new SchemaViolationException("Cannot delete child of schema object");
    }
    ModificationItem[] arrayOfModificationItem = new ModificationItem[1];
    arrayOfModificationItem[0] = new ModificationItem(3, localAttribute);
    info.modifyAttributes(myEnv, arrayOfModificationItem);
  }
  
  private final void modifyServerSchema(Attributes paramAttributes1, Attributes paramAttributes2)
    throws NamingException
  {
    Attribute localAttribute2;
    Attribute localAttribute1;
    switch (objectType)
    {
    case 6: 
      localAttribute2 = info.parser.stringifyObjDesc(paramAttributes1);
      localAttribute1 = info.parser.stringifyObjDesc(paramAttributes2);
      break;
    case 7: 
      localAttribute2 = info.parser.stringifyAttrDesc(paramAttributes1);
      localAttribute1 = info.parser.stringifyAttrDesc(paramAttributes2);
      break;
    case 8: 
      localAttribute2 = info.parser.stringifySyntaxDesc(paramAttributes1);
      localAttribute1 = info.parser.stringifySyntaxDesc(paramAttributes2);
      break;
    case 9: 
      localAttribute2 = info.parser.stringifyMatchRuleDesc(paramAttributes1);
      localAttribute1 = info.parser.stringifyMatchRuleDesc(paramAttributes2);
      break;
    default: 
      throw new SchemaViolationException("Cannot modify schema root");
    }
    ModificationItem[] arrayOfModificationItem = new ModificationItem[2];
    arrayOfModificationItem[0] = new ModificationItem(3, localAttribute2);
    arrayOfModificationItem[1] = new ModificationItem(1, localAttribute1);
    info.modifyAttributes(myEnv, arrayOfModificationItem);
  }
  
  private static final class SchemaInfo
  {
    private LdapCtx schemaEntry;
    private String schemaEntryName;
    LdapSchemaParser parser;
    private String host;
    private int port;
    private boolean hasLdapsScheme;
    
    SchemaInfo(String paramString, LdapCtx paramLdapCtx, LdapSchemaParser paramLdapSchemaParser)
    {
      schemaEntryName = paramString;
      schemaEntry = paramLdapCtx;
      parser = paramLdapSchemaParser;
      port = port_number;
      host = hostname;
      hasLdapsScheme = hasLdapsScheme;
    }
    
    synchronized void close()
      throws NamingException
    {
      if (schemaEntry != null)
      {
        schemaEntry.close();
        schemaEntry = null;
      }
    }
    
    private LdapCtx reopenEntry(Hashtable<?, ?> paramHashtable)
      throws NamingException
    {
      return new LdapCtx(schemaEntryName, host, port, paramHashtable, hasLdapsScheme);
    }
    
    synchronized void modifyAttributes(Hashtable<?, ?> paramHashtable, ModificationItem[] paramArrayOfModificationItem)
      throws NamingException
    {
      if (schemaEntry == null) {
        schemaEntry = reopenEntry(paramHashtable);
      }
      schemaEntry.modifyAttributes("", paramArrayOfModificationItem);
    }
    
    synchronized void modifyAttributes(Hashtable<?, ?> paramHashtable, int paramInt, Attributes paramAttributes)
      throws NamingException
    {
      if (schemaEntry == null) {
        schemaEntry = reopenEntry(paramHashtable);
      }
      schemaEntry.modifyAttributes("", paramInt, paramAttributes);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapSchemaCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */