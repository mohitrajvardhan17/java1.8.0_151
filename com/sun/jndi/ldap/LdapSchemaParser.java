package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.ConfigurationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;

final class LdapSchemaParser
{
  private static final boolean debug = false;
  static final String OBJECTCLASSDESC_ATTR_ID = "objectClasses";
  static final String ATTRIBUTEDESC_ATTR_ID = "attributeTypes";
  static final String SYNTAXDESC_ATTR_ID = "ldapSyntaxes";
  static final String MATCHRULEDESC_ATTR_ID = "matchingRules";
  static final String OBJECTCLASS_DEFINITION_NAME = "ClassDefinition";
  private static final String[] CLASS_DEF_ATTRS = { "objectclass", "ClassDefinition" };
  static final String ATTRIBUTE_DEFINITION_NAME = "AttributeDefinition";
  private static final String[] ATTR_DEF_ATTRS = { "objectclass", "AttributeDefinition" };
  static final String SYNTAX_DEFINITION_NAME = "SyntaxDefinition";
  private static final String[] SYNTAX_DEF_ATTRS = { "objectclass", "SyntaxDefinition" };
  static final String MATCHRULE_DEFINITION_NAME = "MatchingRule";
  private static final String[] MATCHRULE_DEF_ATTRS = { "objectclass", "MatchingRule" };
  private static final char SINGLE_QUOTE = '\'';
  private static final char WHSP = ' ';
  private static final char OID_LIST_BEGIN = '(';
  private static final char OID_LIST_END = ')';
  private static final char OID_SEPARATOR = '$';
  private static final String NUMERICOID_ID = "NUMERICOID";
  private static final String NAME_ID = "NAME";
  private static final String DESC_ID = "DESC";
  private static final String OBSOLETE_ID = "OBSOLETE";
  private static final String SUP_ID = "SUP";
  private static final String PRIVATE_ID = "X-";
  private static final String ABSTRACT_ID = "ABSTRACT";
  private static final String STRUCTURAL_ID = "STRUCTURAL";
  private static final String AUXILARY_ID = "AUXILIARY";
  private static final String MUST_ID = "MUST";
  private static final String MAY_ID = "MAY";
  private static final String EQUALITY_ID = "EQUALITY";
  private static final String ORDERING_ID = "ORDERING";
  private static final String SUBSTR_ID = "SUBSTR";
  private static final String SYNTAX_ID = "SYNTAX";
  private static final String SINGLE_VAL_ID = "SINGLE-VALUE";
  private static final String COLLECTIVE_ID = "COLLECTIVE";
  private static final String NO_USER_MOD_ID = "NO-USER-MODIFICATION";
  private static final String USAGE_ID = "USAGE";
  private static final String SCHEMA_TRUE_VALUE = "true";
  private boolean netscapeBug;
  
  LdapSchemaParser(boolean paramBoolean)
  {
    netscapeBug = paramBoolean;
  }
  
  static final void LDAP2JNDISchema(Attributes paramAttributes, LdapSchemaCtx paramLdapSchemaCtx)
    throws NamingException
  {
    Attribute localAttribute1 = null;
    Attribute localAttribute2 = null;
    Attribute localAttribute3 = null;
    Attribute localAttribute4 = null;
    localAttribute1 = paramAttributes.get("objectClasses");
    if (localAttribute1 != null) {
      objectDescs2ClassDefs(localAttribute1, paramLdapSchemaCtx);
    }
    localAttribute2 = paramAttributes.get("attributeTypes");
    if (localAttribute2 != null) {
      attrDescs2AttrDefs(localAttribute2, paramLdapSchemaCtx);
    }
    localAttribute3 = paramAttributes.get("ldapSyntaxes");
    if (localAttribute3 != null) {
      syntaxDescs2SyntaxDefs(localAttribute3, paramLdapSchemaCtx);
    }
    localAttribute4 = paramAttributes.get("matchingRules");
    if (localAttribute4 != null) {
      matchRuleDescs2MatchRuleDefs(localAttribute4, paramLdapSchemaCtx);
    }
  }
  
  private static final DirContext objectDescs2ClassDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx)
    throws NamingException
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    localBasicAttributes.put(CLASS_DEF_ATTRS[0], CLASS_DEF_ATTRS[1]);
    LdapSchemaCtx localLdapSchemaCtx = paramLdapSchemaCtx.setup(2, "ClassDefinition", localBasicAttributes);
    NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
    while (localNamingEnumeration.hasMore())
    {
      String str2 = (String)localNamingEnumeration.next();
      try
      {
        Object[] arrayOfObject = desc2Def(str2);
        String str1 = (String)arrayOfObject[0];
        Attributes localAttributes = (Attributes)arrayOfObject[1];
        localLdapSchemaCtx.setup(6, str1, localAttributes);
      }
      catch (NamingException localNamingException) {}
    }
    return localLdapSchemaCtx;
  }
  
  private static final DirContext attrDescs2AttrDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx)
    throws NamingException
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    localBasicAttributes.put(ATTR_DEF_ATTRS[0], ATTR_DEF_ATTRS[1]);
    LdapSchemaCtx localLdapSchemaCtx = paramLdapSchemaCtx.setup(3, "AttributeDefinition", localBasicAttributes);
    NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
    while (localNamingEnumeration.hasMore())
    {
      String str2 = (String)localNamingEnumeration.next();
      try
      {
        Object[] arrayOfObject = desc2Def(str2);
        String str1 = (String)arrayOfObject[0];
        Attributes localAttributes = (Attributes)arrayOfObject[1];
        localLdapSchemaCtx.setup(7, str1, localAttributes);
      }
      catch (NamingException localNamingException) {}
    }
    return localLdapSchemaCtx;
  }
  
  private static final DirContext syntaxDescs2SyntaxDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx)
    throws NamingException
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    localBasicAttributes.put(SYNTAX_DEF_ATTRS[0], SYNTAX_DEF_ATTRS[1]);
    LdapSchemaCtx localLdapSchemaCtx = paramLdapSchemaCtx.setup(4, "SyntaxDefinition", localBasicAttributes);
    NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
    while (localNamingEnumeration.hasMore())
    {
      String str2 = (String)localNamingEnumeration.next();
      try
      {
        Object[] arrayOfObject = desc2Def(str2);
        String str1 = (String)arrayOfObject[0];
        Attributes localAttributes = (Attributes)arrayOfObject[1];
        localLdapSchemaCtx.setup(8, str1, localAttributes);
      }
      catch (NamingException localNamingException) {}
    }
    return localLdapSchemaCtx;
  }
  
  private static final DirContext matchRuleDescs2MatchRuleDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx)
    throws NamingException
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    localBasicAttributes.put(MATCHRULE_DEF_ATTRS[0], MATCHRULE_DEF_ATTRS[1]);
    LdapSchemaCtx localLdapSchemaCtx = paramLdapSchemaCtx.setup(5, "MatchingRule", localBasicAttributes);
    NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
    while (localNamingEnumeration.hasMore())
    {
      String str2 = (String)localNamingEnumeration.next();
      try
      {
        Object[] arrayOfObject = desc2Def(str2);
        String str1 = (String)arrayOfObject[0];
        Attributes localAttributes = (Attributes)arrayOfObject[1];
        localLdapSchemaCtx.setup(9, str1, localAttributes);
      }
      catch (NamingException localNamingException) {}
    }
    return localLdapSchemaCtx;
  }
  
  private static final Object[] desc2Def(String paramString)
    throws NamingException
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    Attribute localAttribute = null;
    int[] arrayOfInt = { 1 };
    int i = 1;
    localAttribute = readNumericOID(paramString, arrayOfInt);
    String str = (String)localAttribute.get(0);
    localBasicAttributes.put(localAttribute);
    skipWhitespace(paramString, arrayOfInt);
    while (i != 0)
    {
      localAttribute = readNextTag(paramString, arrayOfInt);
      localBasicAttributes.put(localAttribute);
      if (localAttribute.getID().equals("NAME")) {
        str = (String)localAttribute.get(0);
      }
      skipWhitespace(paramString, arrayOfInt);
      if (arrayOfInt[0] >= paramString.length() - 1) {
        i = 0;
      }
    }
    return new Object[] { str, localBasicAttributes };
  }
  
  private static final int findTrailingWhitespace(String paramString, int paramInt)
  {
    for (int i = paramInt; i > 0; i--) {
      if (paramString.charAt(i) != ' ') {
        return i + 1;
      }
    }
    return 0;
  }
  
  private static final void skipWhitespace(String paramString, int[] paramArrayOfInt)
  {
    for (int i = paramArrayOfInt[0]; i < paramString.length(); i++) {
      if (paramString.charAt(i) != ' ')
      {
        paramArrayOfInt[0] = i;
        return;
      }
    }
  }
  
  private static final Attribute readNumericOID(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    String str = null;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramArrayOfInt[0];
    int j = paramString.indexOf(' ', i);
    if ((j == -1) || (j - i < 1)) {
      throw new InvalidAttributeValueException("no numericoid found: " + paramString);
    }
    str = paramString.substring(i, j);
    paramArrayOfInt[0] += str.length();
    return new BasicAttribute("NUMERICOID", str);
  }
  
  private static final Attribute readNextTag(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    BasicAttribute localBasicAttribute = null;
    String str = null;
    String[] arrayOfString = null;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramString.indexOf(' ', paramArrayOfInt[0]);
    if (i < 0) {
      str = paramString.substring(paramArrayOfInt[0], paramString.length() - 1);
    } else {
      str = paramString.substring(paramArrayOfInt[0], i);
    }
    arrayOfString = readTag(str, paramString, paramArrayOfInt);
    if (arrayOfString.length < 0) {
      throw new InvalidAttributeValueException("no values for attribute \"" + str + "\"");
    }
    localBasicAttribute = new BasicAttribute(str, arrayOfString[0]);
    for (int j = 1; j < arrayOfString.length; j++) {
      localBasicAttribute.add(arrayOfString[j]);
    }
    return localBasicAttribute;
  }
  
  private static final String[] readTag(String paramString1, String paramString2, int[] paramArrayOfInt)
    throws NamingException
  {
    paramArrayOfInt[0] += paramString1.length();
    skipWhitespace(paramString2, paramArrayOfInt);
    if (paramString1.equals("NAME")) {
      return readQDescrs(paramString2, paramArrayOfInt);
    }
    if (paramString1.equals("DESC")) {
      return readQDString(paramString2, paramArrayOfInt);
    }
    if ((paramString1.equals("EQUALITY")) || (paramString1.equals("ORDERING")) || (paramString1.equals("SUBSTR")) || (paramString1.equals("SYNTAX"))) {
      return readWOID(paramString2, paramArrayOfInt);
    }
    if ((paramString1.equals("OBSOLETE")) || (paramString1.equals("ABSTRACT")) || (paramString1.equals("STRUCTURAL")) || (paramString1.equals("AUXILIARY")) || (paramString1.equals("SINGLE-VALUE")) || (paramString1.equals("COLLECTIVE")) || (paramString1.equals("NO-USER-MODIFICATION"))) {
      return new String[] { "true" };
    }
    if ((paramString1.equals("SUP")) || (paramString1.equals("MUST")) || (paramString1.equals("MAY")) || (paramString1.equals("USAGE"))) {
      return readOIDs(paramString2, paramArrayOfInt);
    }
    return readQDStrings(paramString2, paramArrayOfInt);
  }
  
  private static final String[] readQDString(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    int i = paramString.indexOf('\'', paramArrayOfInt[0]) + 1;
    int j = paramString.indexOf('\'', i);
    if ((i == -1) || (j == -1) || (i == j)) {
      throw new InvalidAttributeIdentifierException("malformed QDString: " + paramString);
    }
    if (paramString.charAt(i - 1) != '\'') {
      throw new InvalidAttributeIdentifierException("qdstring has no end mark: " + paramString);
    }
    paramArrayOfInt[0] = (j + 1);
    return new String[] { paramString.substring(i, j) };
  }
  
  private static final String[] readQDStrings(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    return readQDescrs(paramString, paramArrayOfInt);
  }
  
  private static final String[] readQDescrs(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    skipWhitespace(paramString, paramArrayOfInt);
    switch (paramString.charAt(paramArrayOfInt[0]))
    {
    case '(': 
      return readQDescrList(paramString, paramArrayOfInt);
    case '\'': 
      return readQDString(paramString, paramArrayOfInt);
    }
    throw new InvalidAttributeValueException("unexpected oids string: " + paramString);
  }
  
  private static final String[] readQDescrList(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    Vector localVector = new Vector(5);
    paramArrayOfInt[0] += 1;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramArrayOfInt[0];
    int j = paramString.indexOf(')', i);
    if (j == -1) {
      throw new InvalidAttributeValueException("oidlist has no end mark: " + paramString);
    }
    while (i < j)
    {
      arrayOfString = readQDString(paramString, paramArrayOfInt);
      localVector.addElement(arrayOfString[0]);
      skipWhitespace(paramString, paramArrayOfInt);
      i = paramArrayOfInt[0];
    }
    paramArrayOfInt[0] = (j + 1);
    String[] arrayOfString = new String[localVector.size()];
    for (int k = 0; k < arrayOfString.length; k++) {
      arrayOfString[k] = ((String)localVector.elementAt(k));
    }
    return arrayOfString;
  }
  
  private static final String[] readWOID(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    skipWhitespace(paramString, paramArrayOfInt);
    if (paramString.charAt(paramArrayOfInt[0]) == '\'') {
      return readQDString(paramString, paramArrayOfInt);
    }
    int i = paramArrayOfInt[0];
    int j = paramString.indexOf(' ', i);
    if ((j == -1) || (i == j)) {
      throw new InvalidAttributeIdentifierException("malformed OID: " + paramString);
    }
    paramArrayOfInt[0] = (j + 1);
    return new String[] { paramString.substring(i, j) };
  }
  
  private static final String[] readOIDs(String paramString, int[] paramArrayOfInt)
    throws NamingException
  {
    skipWhitespace(paramString, paramArrayOfInt);
    if (paramString.charAt(paramArrayOfInt[0]) != '(') {
      return readWOID(paramString, paramArrayOfInt);
    }
    String str = null;
    Vector localVector = new Vector(5);
    paramArrayOfInt[0] += 1;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramArrayOfInt[0];
    int k = paramString.indexOf(')', i);
    int j = paramString.indexOf('$', i);
    if (k == -1) {
      throw new InvalidAttributeValueException("oidlist has no end mark: " + paramString);
    }
    if ((j == -1) || (k < j)) {}
    for (j = k; (j < k) && (j > 0); j = paramString.indexOf('$', i))
    {
      m = findTrailingWhitespace(paramString, j - 1);
      str = paramString.substring(i, m);
      localVector.addElement(str);
      paramArrayOfInt[0] = (j + 1);
      skipWhitespace(paramString, paramArrayOfInt);
      i = paramArrayOfInt[0];
    }
    int m = findTrailingWhitespace(paramString, k - 1);
    str = paramString.substring(i, m);
    localVector.addElement(str);
    paramArrayOfInt[0] = (k + 1);
    String[] arrayOfString = new String[localVector.size()];
    for (int n = 0; n < arrayOfString.length; n++) {
      arrayOfString[n] = ((String)localVector.elementAt(n));
    }
    return arrayOfString;
  }
  
  private final String classDef2ObjectDesc(Attributes paramAttributes)
    throws NamingException
  {
    StringBuffer localStringBuffer = new StringBuffer("( ");
    Attribute localAttribute = null;
    int i = 0;
    localAttribute = paramAttributes.get("NUMERICOID");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeNumericOID(localAttribute));
      i++;
    }
    else
    {
      throw new ConfigurationException("Class definition doesn'thave a numeric OID");
    }
    localAttribute = paramAttributes.get("NAME");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDescrs(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("DESC");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDString(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("OBSOLETE");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("SUP");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeOIDs(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("ABSTRACT");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("STRUCTURAL");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("AUXILIARY");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("MUST");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeOIDs(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("MAY");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeOIDs(localAttribute));
      i++;
    }
    if (i < paramAttributes.size())
    {
      String str = null;
      NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
      while (localNamingEnumeration.hasMoreElements())
      {
        localAttribute = (Attribute)localNamingEnumeration.next();
        str = localAttribute.getID();
        if ((!str.equals("NUMERICOID")) && (!str.equals("NAME")) && (!str.equals("SUP")) && (!str.equals("MAY")) && (!str.equals("MUST")) && (!str.equals("STRUCTURAL")) && (!str.equals("DESC")) && (!str.equals("AUXILIARY")) && (!str.equals("ABSTRACT")) && (!str.equals("OBSOLETE"))) {
          localStringBuffer.append(writeQDStrings(localAttribute));
        }
      }
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  private final String attrDef2AttrDesc(Attributes paramAttributes)
    throws NamingException
  {
    StringBuffer localStringBuffer = new StringBuffer("( ");
    Attribute localAttribute = null;
    int i = 0;
    localAttribute = paramAttributes.get("NUMERICOID");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeNumericOID(localAttribute));
      i++;
    }
    else
    {
      throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    }
    localAttribute = paramAttributes.get("NAME");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDescrs(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("DESC");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDString(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("OBSOLETE");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("SUP");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeWOID(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("EQUALITY");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeWOID(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("ORDERING");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeWOID(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("SUBSTR");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeWOID(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("SYNTAX");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeWOID(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("SINGLE-VALUE");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("COLLECTIVE");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("NO-USER-MODIFICATION");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("USAGE");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDString(localAttribute));
      i++;
    }
    if (i < paramAttributes.size())
    {
      String str = null;
      NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
      while (localNamingEnumeration.hasMoreElements())
      {
        localAttribute = (Attribute)localNamingEnumeration.next();
        str = localAttribute.getID();
        if ((!str.equals("NUMERICOID")) && (!str.equals("NAME")) && (!str.equals("SYNTAX")) && (!str.equals("DESC")) && (!str.equals("SINGLE-VALUE")) && (!str.equals("EQUALITY")) && (!str.equals("ORDERING")) && (!str.equals("SUBSTR")) && (!str.equals("NO-USER-MODIFICATION")) && (!str.equals("USAGE")) && (!str.equals("SUP")) && (!str.equals("COLLECTIVE")) && (!str.equals("OBSOLETE"))) {
          localStringBuffer.append(writeQDStrings(localAttribute));
        }
      }
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  private final String syntaxDef2SyntaxDesc(Attributes paramAttributes)
    throws NamingException
  {
    StringBuffer localStringBuffer = new StringBuffer("( ");
    Attribute localAttribute = null;
    int i = 0;
    localAttribute = paramAttributes.get("NUMERICOID");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeNumericOID(localAttribute));
      i++;
    }
    else
    {
      throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    }
    localAttribute = paramAttributes.get("DESC");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDString(localAttribute));
      i++;
    }
    if (i < paramAttributes.size())
    {
      String str = null;
      NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
      while (localNamingEnumeration.hasMoreElements())
      {
        localAttribute = (Attribute)localNamingEnumeration.next();
        str = localAttribute.getID();
        if ((!str.equals("NUMERICOID")) && (!str.equals("DESC"))) {
          localStringBuffer.append(writeQDStrings(localAttribute));
        }
      }
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  private final String matchRuleDef2MatchRuleDesc(Attributes paramAttributes)
    throws NamingException
  {
    StringBuffer localStringBuffer = new StringBuffer("( ");
    Attribute localAttribute = null;
    int i = 0;
    localAttribute = paramAttributes.get("NUMERICOID");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeNumericOID(localAttribute));
      i++;
    }
    else
    {
      throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    }
    localAttribute = paramAttributes.get("NAME");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDescrs(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("DESC");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeQDString(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("OBSOLETE");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeBoolean(localAttribute));
      i++;
    }
    localAttribute = paramAttributes.get("SYNTAX");
    if (localAttribute != null)
    {
      localStringBuffer.append(writeWOID(localAttribute));
      i++;
    }
    else
    {
      throw new ConfigurationException("Attribute type doesn'thave a syntax OID");
    }
    if (i < paramAttributes.size())
    {
      String str = null;
      NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
      while (localNamingEnumeration.hasMoreElements())
      {
        localAttribute = (Attribute)localNamingEnumeration.next();
        str = localAttribute.getID();
        if ((!str.equals("NUMERICOID")) && (!str.equals("NAME")) && (!str.equals("SYNTAX")) && (!str.equals("DESC")) && (!str.equals("OBSOLETE"))) {
          localStringBuffer.append(writeQDStrings(localAttribute));
        }
      }
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  private final String writeNumericOID(Attribute paramAttribute)
    throws NamingException
  {
    if (paramAttribute.size() != 1) {
      throw new InvalidAttributeValueException("A class definition must have exactly one numeric OID");
    }
    return (String)paramAttribute.get() + ' ';
  }
  
  private final String writeWOID(Attribute paramAttribute)
    throws NamingException
  {
    if (netscapeBug) {
      return writeQDString(paramAttribute);
    }
    return paramAttribute.getID() + ' ' + paramAttribute.get() + ' ';
  }
  
  private final String writeQDString(Attribute paramAttribute)
    throws NamingException
  {
    if (paramAttribute.size() != 1) {
      throw new InvalidAttributeValueException(paramAttribute.getID() + " must have exactly one value");
    }
    return paramAttribute.getID() + ' ' + '\'' + paramAttribute.get() + '\'' + ' ';
  }
  
  private final String writeQDStrings(Attribute paramAttribute)
    throws NamingException
  {
    return writeQDescrs(paramAttribute);
  }
  
  private final String writeQDescrs(Attribute paramAttribute)
    throws NamingException
  {
    switch (paramAttribute.size())
    {
    case 0: 
      throw new InvalidAttributeValueException(paramAttribute.getID() + "has no values");
    case 1: 
      return writeQDString(paramAttribute);
    }
    StringBuffer localStringBuffer = new StringBuffer(paramAttribute.getID());
    localStringBuffer.append(' ');
    localStringBuffer.append('(');
    NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
    while (localNamingEnumeration.hasMore())
    {
      localStringBuffer.append(' ');
      localStringBuffer.append('\'');
      localStringBuffer.append((String)localNamingEnumeration.next());
      localStringBuffer.append('\'');
      localStringBuffer.append(' ');
    }
    localStringBuffer.append(')');
    localStringBuffer.append(' ');
    return localStringBuffer.toString();
  }
  
  private final String writeOIDs(Attribute paramAttribute)
    throws NamingException
  {
    switch (paramAttribute.size())
    {
    case 0: 
      throw new InvalidAttributeValueException(paramAttribute.getID() + "has no values");
    case 1: 
      if (!netscapeBug) {
        return writeWOID(paramAttribute);
      }
      break;
    }
    StringBuffer localStringBuffer = new StringBuffer(paramAttribute.getID());
    localStringBuffer.append(' ');
    localStringBuffer.append('(');
    NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
    localStringBuffer.append(' ');
    localStringBuffer.append(localNamingEnumeration.next());
    while (localNamingEnumeration.hasMore())
    {
      localStringBuffer.append(' ');
      localStringBuffer.append('$');
      localStringBuffer.append(' ');
      localStringBuffer.append((String)localNamingEnumeration.next());
    }
    localStringBuffer.append(' ');
    localStringBuffer.append(')');
    localStringBuffer.append(' ');
    return localStringBuffer.toString();
  }
  
  private final String writeBoolean(Attribute paramAttribute)
    throws NamingException
  {
    return paramAttribute.getID() + ' ';
  }
  
  final Attribute stringifyObjDesc(Attributes paramAttributes)
    throws NamingException
  {
    BasicAttribute localBasicAttribute = new BasicAttribute("objectClasses");
    localBasicAttribute.add(classDef2ObjectDesc(paramAttributes));
    return localBasicAttribute;
  }
  
  final Attribute stringifyAttrDesc(Attributes paramAttributes)
    throws NamingException
  {
    BasicAttribute localBasicAttribute = new BasicAttribute("attributeTypes");
    localBasicAttribute.add(attrDef2AttrDesc(paramAttributes));
    return localBasicAttribute;
  }
  
  final Attribute stringifySyntaxDesc(Attributes paramAttributes)
    throws NamingException
  {
    BasicAttribute localBasicAttribute = new BasicAttribute("ldapSyntaxes");
    localBasicAttribute.add(syntaxDef2SyntaxDesc(paramAttributes));
    return localBasicAttribute;
  }
  
  final Attribute stringifyMatchRuleDesc(Attributes paramAttributes)
    throws NamingException
  {
    BasicAttribute localBasicAttribute = new BasicAttribute("matchingRules");
    localBasicAttribute.add(matchRuleDef2MatchRuleDesc(paramAttributes));
    return localBasicAttribute;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapSchemaParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */