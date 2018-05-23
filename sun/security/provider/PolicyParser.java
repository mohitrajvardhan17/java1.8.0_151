package sun.security.provider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import sun.net.www.ParseUtil;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import sun.security.util.PropertyExpander.ExpandException;
import sun.security.util.ResourcesMgr;

public class PolicyParser
{
  private static final String EXTDIRS_PROPERTY = "java.ext.dirs";
  private static final String OLD_EXTDIRS_EXPANSION = "${java.ext.dirs}";
  static final String EXTDIRS_EXPANSION = "${{java.ext.dirs}}";
  private Vector<GrantEntry> grantEntries = new Vector();
  private Map<String, DomainEntry> domainEntries;
  private static final Debug debug = Debug.getInstance("parser", "\t[Policy Parser]");
  private StreamTokenizer st;
  private int lookahead;
  private boolean expandProp = false;
  private String keyStoreUrlString = null;
  private String keyStoreType = null;
  private String keyStoreProvider = null;
  private String storePassURL = null;
  
  private String expand(String paramString)
    throws PropertyExpander.ExpandException
  {
    return expand(paramString, false);
  }
  
  private String expand(String paramString, boolean paramBoolean)
    throws PropertyExpander.ExpandException
  {
    if (!expandProp) {
      return paramString;
    }
    return PropertyExpander.expand(paramString, paramBoolean);
  }
  
  public PolicyParser() {}
  
  public PolicyParser(boolean paramBoolean)
  {
    this();
  }
  
  public void read(Reader paramReader)
    throws PolicyParser.ParsingException, IOException
  {
    if (!(paramReader instanceof BufferedReader)) {
      paramReader = new BufferedReader(paramReader);
    }
    st = new StreamTokenizer(paramReader);
    st.resetSyntax();
    st.wordChars(97, 122);
    st.wordChars(65, 90);
    st.wordChars(46, 46);
    st.wordChars(48, 57);
    st.wordChars(95, 95);
    st.wordChars(36, 36);
    st.wordChars(160, 255);
    st.whitespaceChars(0, 32);
    st.commentChar(47);
    st.quoteChar(39);
    st.quoteChar(34);
    st.lowerCaseMode(false);
    st.ordinaryChar(47);
    st.slashSlashComments(true);
    st.slashStarComments(true);
    lookahead = st.nextToken();
    GrantEntry localGrantEntry = null;
    while (lookahead != -1)
    {
      if (peek("grant"))
      {
        localGrantEntry = parseGrantEntry();
        if (localGrantEntry != null) {
          add(localGrantEntry);
        }
      }
      else if ((peek("keystore")) && (keyStoreUrlString == null))
      {
        parseKeyStoreEntry();
      }
      else if ((peek("keystorePasswordURL")) && (storePassURL == null))
      {
        parseStorePassURL();
      }
      else if ((localGrantEntry == null) && (keyStoreUrlString == null) && (storePassURL == null) && (peek("domain")))
      {
        if (domainEntries == null) {
          domainEntries = new TreeMap();
        }
        DomainEntry localDomainEntry = parseDomainEntry();
        if (localDomainEntry != null)
        {
          String str = localDomainEntry.getName();
          if (!domainEntries.containsKey(str))
          {
            domainEntries.put(str, localDomainEntry);
          }
          else
          {
            MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.domain.name"));
            Object[] arrayOfObject = { str };
            throw new ParsingException(localMessageFormat.format(arrayOfObject));
          }
        }
      }
      match(";");
    }
    if ((keyStoreUrlString == null) && (storePassURL != null)) {
      throw new ParsingException(ResourcesMgr.getString("keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore"));
    }
  }
  
  public void add(GrantEntry paramGrantEntry)
  {
    grantEntries.addElement(paramGrantEntry);
  }
  
  public void replace(GrantEntry paramGrantEntry1, GrantEntry paramGrantEntry2)
  {
    grantEntries.setElementAt(paramGrantEntry2, grantEntries.indexOf(paramGrantEntry1));
  }
  
  public boolean remove(GrantEntry paramGrantEntry)
  {
    return grantEntries.removeElement(paramGrantEntry);
  }
  
  public String getKeyStoreUrl()
  {
    try
    {
      if ((keyStoreUrlString != null) && (keyStoreUrlString.length() != 0)) {
        return expand(keyStoreUrlString, true).replace(File.separatorChar, '/');
      }
    }
    catch (PropertyExpander.ExpandException localExpandException)
    {
      if (debug != null) {
        debug.println(localExpandException.toString());
      }
      return null;
    }
    return null;
  }
  
  public void setKeyStoreUrl(String paramString)
  {
    keyStoreUrlString = paramString;
  }
  
  public String getKeyStoreType()
  {
    return keyStoreType;
  }
  
  public void setKeyStoreType(String paramString)
  {
    keyStoreType = paramString;
  }
  
  public String getKeyStoreProvider()
  {
    return keyStoreProvider;
  }
  
  public void setKeyStoreProvider(String paramString)
  {
    keyStoreProvider = paramString;
  }
  
  public String getStorePassURL()
  {
    try
    {
      if ((storePassURL != null) && (storePassURL.length() != 0)) {
        return expand(storePassURL, true).replace(File.separatorChar, '/');
      }
    }
    catch (PropertyExpander.ExpandException localExpandException)
    {
      if (debug != null) {
        debug.println(localExpandException.toString());
      }
      return null;
    }
    return null;
  }
  
  public void setStorePassURL(String paramString)
  {
    storePassURL = paramString;
  }
  
  public Enumeration<GrantEntry> grantElements()
  {
    return grantEntries.elements();
  }
  
  public Collection<DomainEntry> getDomainEntries()
  {
    return domainEntries.values();
  }
  
  public void write(Writer paramWriter)
  {
    PrintWriter localPrintWriter = new PrintWriter(new BufferedWriter(paramWriter));
    Enumeration localEnumeration = grantElements();
    localPrintWriter.println("/* AUTOMATICALLY GENERATED ON " + new Date() + "*/");
    localPrintWriter.println("/* DO NOT EDIT */");
    localPrintWriter.println();
    if (keyStoreUrlString != null) {
      writeKeyStoreEntry(localPrintWriter);
    }
    if (storePassURL != null) {
      writeStorePassURL(localPrintWriter);
    }
    while (localEnumeration.hasMoreElements())
    {
      GrantEntry localGrantEntry = (GrantEntry)localEnumeration.nextElement();
      localGrantEntry.write(localPrintWriter);
      localPrintWriter.println();
    }
    localPrintWriter.flush();
  }
  
  private void parseKeyStoreEntry()
    throws PolicyParser.ParsingException, IOException
  {
    match("keystore");
    keyStoreUrlString = match("quoted string");
    if (!peek(",")) {
      return;
    }
    match(",");
    if (peek("\"")) {
      keyStoreType = match("quoted string");
    } else {
      throw new ParsingException(st.lineno(), ResourcesMgr.getString("expected.keystore.type"));
    }
    if (!peek(",")) {
      return;
    }
    match(",");
    if (peek("\"")) {
      keyStoreProvider = match("quoted string");
    } else {
      throw new ParsingException(st.lineno(), ResourcesMgr.getString("expected.keystore.provider"));
    }
  }
  
  private void parseStorePassURL()
    throws PolicyParser.ParsingException, IOException
  {
    match("keyStorePasswordURL");
    storePassURL = match("quoted string");
  }
  
  private void writeKeyStoreEntry(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("keystore \"");
    paramPrintWriter.print(keyStoreUrlString);
    paramPrintWriter.print('"');
    if ((keyStoreType != null) && (keyStoreType.length() > 0)) {
      paramPrintWriter.print(", \"" + keyStoreType + "\"");
    }
    if ((keyStoreProvider != null) && (keyStoreProvider.length() > 0)) {
      paramPrintWriter.print(", \"" + keyStoreProvider + "\"");
    }
    paramPrintWriter.println(";");
    paramPrintWriter.println();
  }
  
  private void writeStorePassURL(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("keystorePasswordURL \"");
    paramPrintWriter.print(storePassURL);
    paramPrintWriter.print('"');
    paramPrintWriter.println(";");
    paramPrintWriter.println();
  }
  
  private GrantEntry parseGrantEntry()
    throws PolicyParser.ParsingException, IOException
  {
    GrantEntry localGrantEntry = new GrantEntry();
    LinkedList localLinkedList = null;
    int i = 0;
    match("grant");
    Object localObject1;
    Object localObject3;
    Object localObject2;
    while (!peek("{")) {
      if (peekAndMatch("Codebase"))
      {
        if (codeBase != null) {
          throw new ParsingException(st.lineno(), ResourcesMgr.getString("multiple.Codebase.expressions"));
        }
        codeBase = match("quoted string");
        peekAndMatch(",");
      }
      else if (peekAndMatch("SignedBy"))
      {
        if (signedBy != null) {
          throw new ParsingException(st.lineno(), ResourcesMgr.getString("multiple.SignedBy.expressions"));
        }
        signedBy = match("quoted string");
        localObject1 = new StringTokenizer(signedBy, ",", true);
        int k = 0;
        int m = 0;
        while (((StringTokenizer)localObject1).hasMoreTokens())
        {
          localObject3 = ((StringTokenizer)localObject1).nextToken().trim();
          if (((String)localObject3).equals(",")) {
            m++;
          } else if (((String)localObject3).length() > 0) {
            k++;
          }
        }
        if (k <= m) {
          throw new ParsingException(st.lineno(), ResourcesMgr.getString("SignedBy.has.empty.alias"));
        }
        peekAndMatch(",");
      }
      else if (peekAndMatch("Principal"))
      {
        if (localLinkedList == null) {
          localLinkedList = new LinkedList();
        }
        if (peek("\""))
        {
          localObject1 = "PolicyParser.REPLACE_NAME";
          localObject2 = match("principal type");
        }
        else
        {
          if (peek("*"))
          {
            match("*");
            localObject1 = "WILDCARD_PRINCIPAL_CLASS";
          }
          else
          {
            localObject1 = match("principal type");
          }
          if (peek("*"))
          {
            match("*");
            localObject2 = "WILDCARD_PRINCIPAL_NAME";
          }
          else
          {
            localObject2 = match("quoted string");
          }
          if ((((String)localObject1).equals("WILDCARD_PRINCIPAL_CLASS")) && (!((String)localObject2).equals("WILDCARD_PRINCIPAL_NAME")))
          {
            if (debug != null) {
              debug.println("disallowing principal that has WILDCARD class but no WILDCARD name");
            }
            throw new ParsingException(st.lineno(), ResourcesMgr.getString("can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name"));
          }
        }
        try
        {
          localObject2 = expand((String)localObject2);
          if ((((String)localObject1).equals("javax.security.auth.x500.X500Principal")) && (!((String)localObject2).equals("WILDCARD_PRINCIPAL_NAME")))
          {
            X500Principal localX500Principal = new X500Principal(new X500Principal((String)localObject2).toString());
            localObject2 = localX500Principal.getName();
          }
          localLinkedList.add(new PrincipalEntry((String)localObject1, (String)localObject2));
        }
        catch (PropertyExpander.ExpandException localExpandException3)
        {
          if (debug != null) {
            debug.println("principal name expansion failed: " + (String)localObject2);
          }
          i = 1;
        }
        peekAndMatch(",");
      }
      else
      {
        throw new ParsingException(st.lineno(), ResourcesMgr.getString("expected.codeBase.or.SignedBy.or.Principal"));
      }
    }
    if (localLinkedList != null) {
      principals = localLinkedList;
    }
    match("{");
    while (!peek("}")) {
      if (peek("Permission"))
      {
        try
        {
          localObject1 = parsePermissionEntry();
          localGrantEntry.add((PermissionEntry)localObject1);
        }
        catch (PropertyExpander.ExpandException localExpandException1)
        {
          if (debug != null) {
            debug.println(localExpandException1.toString());
          }
          skipEntry();
        }
        match(";");
      }
      else
      {
        throw new ParsingException(st.lineno(), ResourcesMgr.getString("expected.permission.entry"));
      }
    }
    match("}");
    try
    {
      if (signedBy != null) {
        signedBy = expand(signedBy);
      }
      if (codeBase != null)
      {
        if (codeBase.equals("${java.ext.dirs}")) {
          codeBase = "${{java.ext.dirs}}";
        }
        int j;
        if ((j = codeBase.indexOf("${{java.ext.dirs}}")) < 0)
        {
          codeBase = expand(codeBase, true).replace(File.separatorChar, '/');
        }
        else
        {
          localObject2 = parseExtDirs(codeBase, j);
          if ((localObject2 != null) && (localObject2.length > 0)) {
            for (int n = 0; n < localObject2.length; n++)
            {
              localObject3 = (GrantEntry)localGrantEntry.clone();
              codeBase = localObject2[n];
              add((GrantEntry)localObject3);
              if (debug != null) {
                debug.println("creating policy entry for expanded java.ext.dirs path:\n\t\t" + localObject2[n]);
              }
            }
          }
          i = 1;
        }
      }
    }
    catch (PropertyExpander.ExpandException localExpandException2)
    {
      if (debug != null) {
        debug.println(localExpandException2.toString());
      }
      return null;
    }
    return i == 1 ? null : localGrantEntry;
  }
  
  private PermissionEntry parsePermissionEntry()
    throws PolicyParser.ParsingException, IOException, PropertyExpander.ExpandException
  {
    PermissionEntry localPermissionEntry = new PermissionEntry();
    match("Permission");
    permission = match("permission type");
    if (peek("\"")) {
      name = expand(match("quoted string"));
    }
    if (!peek(",")) {
      return localPermissionEntry;
    }
    match(",");
    if (peek("\""))
    {
      action = expand(match("quoted string"));
      if (!peek(",")) {
        return localPermissionEntry;
      }
      match(",");
    }
    if (peekAndMatch("SignedBy")) {
      signedBy = expand(match("quoted string"));
    }
    return localPermissionEntry;
  }
  
  private DomainEntry parseDomainEntry()
    throws PolicyParser.ParsingException, IOException
  {
    int i = 0;
    String str = null;
    Object localObject = new HashMap();
    match("domain");
    str = match("domain name");
    while (!peek("{")) {
      localObject = parseProperties("{");
    }
    match("{");
    DomainEntry localDomainEntry = new DomainEntry(str, (Map)localObject);
    while (!peek("}"))
    {
      match("keystore");
      str = match("keystore name");
      if (!peek("}")) {
        localObject = parseProperties(";");
      }
      match(";");
      localDomainEntry.add(new KeyStoreEntry(str, (Map)localObject));
    }
    match("}");
    return i == 1 ? null : localDomainEntry;
  }
  
  private Map<String, String> parseProperties(String paramString)
    throws PolicyParser.ParsingException, IOException
  {
    HashMap localHashMap = new HashMap();
    while (!peek(paramString))
    {
      String str1 = match("property name");
      match("=");
      String str2;
      try
      {
        str2 = expand(match("quoted string"));
      }
      catch (PropertyExpander.ExpandException localExpandException)
      {
        throw new IOException(localExpandException.getLocalizedMessage());
      }
      localHashMap.put(str1.toLowerCase(Locale.ENGLISH), str2);
    }
    return localHashMap;
  }
  
  static String[] parseExtDirs(String paramString, int paramInt)
  {
    String str1 = System.getProperty("java.ext.dirs");
    String str2 = paramInt > 0 ? paramString.substring(0, paramInt) : "file:";
    int i = paramInt + "${{java.ext.dirs}}".length();
    String str3 = i < paramString.length() ? paramString.substring(i) : (String)null;
    String[] arrayOfString = null;
    if (str1 != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
      int j = localStringTokenizer.countTokens();
      arrayOfString = new String[j];
      for (int k = 0; k < j; k++)
      {
        File localFile = new File(localStringTokenizer.nextToken());
        arrayOfString[k] = ParseUtil.encodePath(localFile.getAbsolutePath());
        if (!arrayOfString[k].startsWith("/")) {
          arrayOfString[k] = ("/" + arrayOfString[k]);
        }
        String str4 = str3 == null ? "/*" : arrayOfString[k].endsWith("/") ? "*" : str3;
        arrayOfString[k] = (str2 + arrayOfString[k] + str4);
      }
    }
    return arrayOfString;
  }
  
  private boolean peekAndMatch(String paramString)
    throws PolicyParser.ParsingException, IOException
  {
    if (peek(paramString))
    {
      match(paramString);
      return true;
    }
    return false;
  }
  
  private boolean peek(String paramString)
  {
    boolean bool = false;
    switch (lookahead)
    {
    case -3: 
      if (paramString.equalsIgnoreCase(st.sval)) {
        bool = true;
      }
      break;
    case 44: 
      if (paramString.equalsIgnoreCase(",")) {
        bool = true;
      }
      break;
    case 123: 
      if (paramString.equalsIgnoreCase("{")) {
        bool = true;
      }
      break;
    case 125: 
      if (paramString.equalsIgnoreCase("}")) {
        bool = true;
      }
      break;
    case 34: 
      if (paramString.equalsIgnoreCase("\"")) {
        bool = true;
      }
      break;
    case 42: 
      if (paramString.equalsIgnoreCase("*")) {
        bool = true;
      }
      break;
    case 59: 
      if (paramString.equalsIgnoreCase(";")) {
        bool = true;
      }
      break;
    }
    return bool;
  }
  
  private String match(String paramString)
    throws PolicyParser.ParsingException, IOException
  {
    String str = null;
    switch (lookahead)
    {
    case -2: 
      throw new ParsingException(st.lineno(), paramString, ResourcesMgr.getString("number.") + String.valueOf(st.nval));
    case -1: 
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("expected.expect.read.end.of.file."));
      Object[] arrayOfObject = { paramString };
      throw new ParsingException(localMessageFormat.format(arrayOfObject));
    case -3: 
      if (paramString.equalsIgnoreCase(st.sval))
      {
        lookahead = st.nextToken();
      }
      else if (paramString.equalsIgnoreCase("permission type"))
      {
        str = st.sval;
        lookahead = st.nextToken();
      }
      else if (paramString.equalsIgnoreCase("principal type"))
      {
        str = st.sval;
        lookahead = st.nextToken();
      }
      else if ((paramString.equalsIgnoreCase("domain name")) || (paramString.equalsIgnoreCase("keystore name")) || (paramString.equalsIgnoreCase("property name")))
      {
        str = st.sval;
        lookahead = st.nextToken();
      }
      else
      {
        throw new ParsingException(st.lineno(), paramString, st.sval);
      }
      break;
    case 34: 
      if (paramString.equalsIgnoreCase("quoted string"))
      {
        str = st.sval;
        lookahead = st.nextToken();
      }
      else if (paramString.equalsIgnoreCase("permission type"))
      {
        str = st.sval;
        lookahead = st.nextToken();
      }
      else if (paramString.equalsIgnoreCase("principal type"))
      {
        str = st.sval;
        lookahead = st.nextToken();
      }
      else
      {
        throw new ParsingException(st.lineno(), paramString, st.sval);
      }
      break;
    case 44: 
      if (paramString.equalsIgnoreCase(",")) {
        lookahead = st.nextToken();
      } else {
        throw new ParsingException(st.lineno(), paramString, ",");
      }
      break;
    case 123: 
      if (paramString.equalsIgnoreCase("{")) {
        lookahead = st.nextToken();
      } else {
        throw new ParsingException(st.lineno(), paramString, "{");
      }
      break;
    case 125: 
      if (paramString.equalsIgnoreCase("}")) {
        lookahead = st.nextToken();
      } else {
        throw new ParsingException(st.lineno(), paramString, "}");
      }
      break;
    case 59: 
      if (paramString.equalsIgnoreCase(";")) {
        lookahead = st.nextToken();
      } else {
        throw new ParsingException(st.lineno(), paramString, ";");
      }
      break;
    case 42: 
      if (paramString.equalsIgnoreCase("*")) {
        lookahead = st.nextToken();
      } else {
        throw new ParsingException(st.lineno(), paramString, "*");
      }
      break;
    case 61: 
      if (paramString.equalsIgnoreCase("=")) {
        lookahead = st.nextToken();
      } else {
        throw new ParsingException(st.lineno(), paramString, "=");
      }
      break;
    default: 
      throw new ParsingException(st.lineno(), paramString, new String(new char[] { (char)lookahead }));
    }
    return str;
  }
  
  private void skipEntry()
    throws PolicyParser.ParsingException, IOException
  {
    while (lookahead != 59)
    {
      switch (lookahead)
      {
      case -2: 
        throw new ParsingException(st.lineno(), ";", ResourcesMgr.getString("number.") + String.valueOf(st.nval));
      case -1: 
        throw new ParsingException(ResourcesMgr.getString("expected.read.end.of.file."));
      }
      lookahead = st.nextToken();
    }
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    FileReader localFileReader = new FileReader(paramArrayOfString[0]);
    Object localObject1 = null;
    try
    {
      FileWriter localFileWriter = new FileWriter(paramArrayOfString[1]);
      Object localObject2 = null;
      try
      {
        PolicyParser localPolicyParser = new PolicyParser(true);
        localPolicyParser.read(localFileReader);
        localPolicyParser.write(localFileWriter);
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally {}
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileReader != null) {
        if (localObject1 != null) {
          try
          {
            localFileReader.close();
          }
          catch (Throwable localThrowable6)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable6);
          }
        } else {
          localFileReader.close();
        }
      }
    }
  }
  
  static class DomainEntry
  {
    private final String name;
    private final Map<String, String> properties;
    private final Map<String, PolicyParser.KeyStoreEntry> entries;
    
    DomainEntry(String paramString, Map<String, String> paramMap)
    {
      name = paramString;
      properties = paramMap;
      entries = new HashMap();
    }
    
    String getName()
    {
      return name;
    }
    
    Map<String, String> getProperties()
    {
      return properties;
    }
    
    Collection<PolicyParser.KeyStoreEntry> getEntries()
    {
      return entries.values();
    }
    
    void add(PolicyParser.KeyStoreEntry paramKeyStoreEntry)
      throws PolicyParser.ParsingException
    {
      String str = paramKeyStoreEntry.getName();
      if (!entries.containsKey(str))
      {
        entries.put(str, paramKeyStoreEntry);
      }
      else
      {
        MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.name"));
        Object[] arrayOfObject = { str };
        throw new PolicyParser.ParsingException(localMessageFormat.format(arrayOfObject));
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("\ndomain ").append(name);
      Iterator localIterator;
      Object localObject;
      if (properties != null)
      {
        localIterator = properties.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject = (Map.Entry)localIterator.next();
          localStringBuilder.append("\n        ").append((String)((Map.Entry)localObject).getKey()).append('=').append((String)((Map.Entry)localObject).getValue());
        }
      }
      localStringBuilder.append(" {\n");
      if (entries != null)
      {
        localIterator = entries.values().iterator();
        while (localIterator.hasNext())
        {
          localObject = (PolicyParser.KeyStoreEntry)localIterator.next();
          localStringBuilder.append(localObject).append("\n");
        }
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
  
  public static class GrantEntry
  {
    public String signedBy;
    public String codeBase;
    public LinkedList<PolicyParser.PrincipalEntry> principals;
    public Vector<PolicyParser.PermissionEntry> permissionEntries;
    
    public GrantEntry()
    {
      principals = new LinkedList();
      permissionEntries = new Vector();
    }
    
    public GrantEntry(String paramString1, String paramString2)
    {
      codeBase = paramString2;
      signedBy = paramString1;
      principals = new LinkedList();
      permissionEntries = new Vector();
    }
    
    public void add(PolicyParser.PermissionEntry paramPermissionEntry)
    {
      permissionEntries.addElement(paramPermissionEntry);
    }
    
    public boolean remove(PolicyParser.PrincipalEntry paramPrincipalEntry)
    {
      return principals.remove(paramPrincipalEntry);
    }
    
    public boolean remove(PolicyParser.PermissionEntry paramPermissionEntry)
    {
      return permissionEntries.removeElement(paramPermissionEntry);
    }
    
    public boolean contains(PolicyParser.PrincipalEntry paramPrincipalEntry)
    {
      return principals.contains(paramPrincipalEntry);
    }
    
    public boolean contains(PolicyParser.PermissionEntry paramPermissionEntry)
    {
      return permissionEntries.contains(paramPermissionEntry);
    }
    
    public Enumeration<PolicyParser.PermissionEntry> permissionElements()
    {
      return permissionEntries.elements();
    }
    
    public void write(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print("grant");
      if (signedBy != null)
      {
        paramPrintWriter.print(" signedBy \"");
        paramPrintWriter.print(signedBy);
        paramPrintWriter.print('"');
        if (codeBase != null) {
          paramPrintWriter.print(", ");
        }
      }
      if (codeBase != null)
      {
        paramPrintWriter.print(" codeBase \"");
        paramPrintWriter.print(codeBase);
        paramPrintWriter.print('"');
        if ((principals != null) && (principals.size() > 0)) {
          paramPrintWriter.print(",\n");
        }
      }
      Object localObject2;
      if ((principals != null) && (principals.size() > 0))
      {
        localObject1 = principals.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          paramPrintWriter.print("      ");
          localObject2 = (PolicyParser.PrincipalEntry)((Iterator)localObject1).next();
          ((PolicyParser.PrincipalEntry)localObject2).write(paramPrintWriter);
          if (((Iterator)localObject1).hasNext()) {
            paramPrintWriter.print(",\n");
          }
        }
      }
      paramPrintWriter.println(" {");
      Object localObject1 = permissionEntries.elements();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject2 = (PolicyParser.PermissionEntry)((Enumeration)localObject1).nextElement();
        paramPrintWriter.write("  ");
        ((PolicyParser.PermissionEntry)localObject2).write(paramPrintWriter);
      }
      paramPrintWriter.println("};");
    }
    
    public Object clone()
    {
      GrantEntry localGrantEntry = new GrantEntry();
      codeBase = codeBase;
      signedBy = signedBy;
      principals = new LinkedList(principals);
      permissionEntries = new Vector(permissionEntries);
      return localGrantEntry;
    }
  }
  
  static class KeyStoreEntry
  {
    private final String name;
    private final Map<String, String> properties;
    
    KeyStoreEntry(String paramString, Map<String, String> paramMap)
    {
      name = paramString;
      properties = paramMap;
    }
    
    String getName()
    {
      return name;
    }
    
    Map<String, String> getProperties()
    {
      return properties;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("\n    keystore ").append(name);
      if (properties != null)
      {
        Iterator localIterator = properties.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          localStringBuilder.append("\n        ").append((String)localEntry.getKey()).append('=').append((String)localEntry.getValue());
        }
      }
      localStringBuilder.append(";");
      return localStringBuilder.toString();
    }
  }
  
  public static class ParsingException
    extends GeneralSecurityException
  {
    private static final long serialVersionUID = -4330692689482574072L;
    private String i18nMessage;
    
    public ParsingException(String paramString)
    {
      super();
      i18nMessage = paramString;
    }
    
    public ParsingException(int paramInt, String paramString)
    {
      super();
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("line.number.msg"));
      Object[] arrayOfObject = { new Integer(paramInt), paramString };
      i18nMessage = localMessageFormat.format(arrayOfObject);
    }
    
    public ParsingException(int paramInt, String paramString1, String paramString2)
    {
      super();
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("line.number.expected.expect.found.actual."));
      Object[] arrayOfObject = { new Integer(paramInt), paramString1, paramString2 };
      i18nMessage = localMessageFormat.format(arrayOfObject);
    }
    
    public String getLocalizedMessage()
    {
      return i18nMessage;
    }
  }
  
  public static class PermissionEntry
  {
    public String permission;
    public String name;
    public String action;
    public String signedBy;
    
    public PermissionEntry() {}
    
    public PermissionEntry(String paramString1, String paramString2, String paramString3)
    {
      permission = paramString1;
      name = paramString2;
      action = paramString3;
    }
    
    public int hashCode()
    {
      int i = permission.hashCode();
      if (name != null) {
        i ^= name.hashCode();
      }
      if (action != null) {
        i ^= action.hashCode();
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof PermissionEntry)) {
        return false;
      }
      PermissionEntry localPermissionEntry = (PermissionEntry)paramObject;
      if (permission == null)
      {
        if (permission != null) {
          return false;
        }
      }
      else if (!permission.equals(permission)) {
        return false;
      }
      if (name == null)
      {
        if (name != null) {
          return false;
        }
      }
      else if (!name.equals(name)) {
        return false;
      }
      if (action == null)
      {
        if (action != null) {
          return false;
        }
      }
      else if (!action.equals(action)) {
        return false;
      }
      if (signedBy == null)
      {
        if (signedBy != null) {
          return false;
        }
      }
      else if (!signedBy.equals(signedBy)) {
        return false;
      }
      return true;
    }
    
    public void write(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print("permission ");
      paramPrintWriter.print(permission);
      if (name != null)
      {
        paramPrintWriter.print(" \"");
        paramPrintWriter.print(name.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\""));
        paramPrintWriter.print('"');
      }
      if (action != null)
      {
        paramPrintWriter.print(", \"");
        paramPrintWriter.print(action);
        paramPrintWriter.print('"');
      }
      if (signedBy != null)
      {
        paramPrintWriter.print(", signedBy \"");
        paramPrintWriter.print(signedBy);
        paramPrintWriter.print('"');
      }
      paramPrintWriter.println(";");
    }
  }
  
  public static class PrincipalEntry
    implements Principal
  {
    public static final String WILDCARD_CLASS = "WILDCARD_PRINCIPAL_CLASS";
    public static final String WILDCARD_NAME = "WILDCARD_PRINCIPAL_NAME";
    public static final String REPLACE_NAME = "PolicyParser.REPLACE_NAME";
    String principalClass;
    String principalName;
    
    public PrincipalEntry(String paramString1, String paramString2)
    {
      if ((paramString1 == null) || (paramString2 == null)) {
        throw new NullPointerException(ResourcesMgr.getString("null.principalClass.or.principalName"));
      }
      principalClass = paramString1;
      principalName = paramString2;
    }
    
    boolean isWildcardName()
    {
      return principalName.equals("WILDCARD_PRINCIPAL_NAME");
    }
    
    boolean isWildcardClass()
    {
      return principalClass.equals("WILDCARD_PRINCIPAL_CLASS");
    }
    
    boolean isReplaceName()
    {
      return principalClass.equals("PolicyParser.REPLACE_NAME");
    }
    
    public String getPrincipalClass()
    {
      return principalClass;
    }
    
    public String getPrincipalName()
    {
      return principalName;
    }
    
    public String getDisplayClass()
    {
      if (isWildcardClass()) {
        return "*";
      }
      if (isReplaceName()) {
        return "";
      }
      return principalClass;
    }
    
    public String getDisplayName()
    {
      return getDisplayName(false);
    }
    
    public String getDisplayName(boolean paramBoolean)
    {
      if (isWildcardName()) {
        return "*";
      }
      if (paramBoolean) {
        return "\"" + principalName + "\"";
      }
      return principalName;
    }
    
    public String getName()
    {
      return principalName;
    }
    
    public String toString()
    {
      if (!isReplaceName()) {
        return getDisplayClass() + "/" + getDisplayName();
      }
      return getDisplayName();
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof PrincipalEntry)) {
        return false;
      }
      PrincipalEntry localPrincipalEntry = (PrincipalEntry)paramObject;
      return (principalClass.equals(principalClass)) && (principalName.equals(principalName));
    }
    
    public int hashCode()
    {
      return principalClass.hashCode();
    }
    
    public void write(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print("principal " + getDisplayClass() + " " + getDisplayName(true));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\PolicyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */