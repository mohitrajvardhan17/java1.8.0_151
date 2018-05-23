package sun.security.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.URIParameter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.security.auth.AuthPermission;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.Configuration.Parameters;
import javax.security.auth.login.ConfigurationSpi;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import sun.security.util.PropertyExpander.ExpandException;
import sun.security.util.ResourcesMgr;

public final class ConfigFile
  extends Configuration
{
  private final Spi spi = new Spi();
  
  public ConfigFile() {}
  
  public AppConfigurationEntry[] getAppConfigurationEntry(String paramString)
  {
    return spi.engineGetAppConfigurationEntry(paramString);
  }
  
  public synchronized void refresh()
  {
    spi.engineRefresh();
  }
  
  public static final class Spi
    extends ConfigurationSpi
  {
    private URL url;
    private boolean expandProp = true;
    private Map<String, List<AppConfigurationEntry>> configuration;
    private int linenum;
    private StreamTokenizer st;
    private int lookahead;
    private static Debug debugConfig = Debug.getInstance("configfile");
    private static Debug debugParser = Debug.getInstance("configparser");
    
    public Spi()
    {
      try
      {
        init();
      }
      catch (IOException localIOException)
      {
        throw new SecurityException(localIOException);
      }
    }
    
    public Spi(URI paramURI)
    {
      try
      {
        url = paramURI.toURL();
        init();
      }
      catch (IOException localIOException)
      {
        throw new SecurityException(localIOException);
      }
    }
    
    public Spi(final Configuration.Parameters paramParameters)
      throws IOException
    {
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Void run()
            throws IOException
          {
            if (paramParameters == null)
            {
              ConfigFile.Spi.this.init();
            }
            else
            {
              if (!(paramParameters instanceof URIParameter)) {
                throw new IllegalArgumentException("Unrecognized parameter: " + paramParameters);
              }
              URIParameter localURIParameter = (URIParameter)paramParameters;
              url = localURIParameter.getURI().toURL();
              ConfigFile.Spi.this.init();
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((IOException)localPrivilegedActionException.getException());
      }
    }
    
    private void init()
      throws IOException
    {
      int i = 0;
      String str1 = Security.getProperty("policy.expandProperties");
      if (str1 == null) {
        str1 = System.getProperty("policy.expandProperties");
      }
      if ("false".equals(str1)) {
        expandProp = false;
      }
      HashMap localHashMap = new HashMap();
      if (url != null)
      {
        if (debugConfig != null) {
          debugConfig.println("reading " + url);
        }
        init(url, localHashMap);
        configuration = localHashMap;
        return;
      }
      String str2 = Security.getProperty("policy.allowSystemProperty");
      if ("true".equalsIgnoreCase(str2))
      {
        String str3 = System.getProperty("java.security.auth.login.config");
        if (str3 != null)
        {
          int k = 0;
          if (str3.startsWith("="))
          {
            k = 1;
            str3 = str3.substring(1);
          }
          try
          {
            str3 = PropertyExpander.expand(str3);
          }
          catch (PropertyExpander.ExpandException localExpandException1)
          {
            throw ioException("Unable.to.properly.expand.config", new Object[] { str3 });
          }
          URL localURL = null;
          try
          {
            localURL = new URL(str3);
          }
          catch (MalformedURLException localMalformedURLException)
          {
            File localFile = new File(str3);
            if (localFile.exists()) {
              localURL = localFile.toURI().toURL();
            } else {
              throw ioException("extra.config.No.such.file.or.directory.", new Object[] { str3 });
            }
          }
          if (debugConfig != null) {
            debugConfig.println("reading " + localURL);
          }
          init(localURL, localHashMap);
          i = 1;
          if (k != 0)
          {
            if (debugConfig != null) {
              debugConfig.println("overriding other policies!");
            }
            configuration = localHashMap;
            return;
          }
        }
      }
      String str4;
      for (int j = 1; (str4 = Security.getProperty("login.config.url." + j)) != null; j++) {
        try
        {
          str4 = PropertyExpander.expand(str4).replace(File.separatorChar, '/');
          if (debugConfig != null) {
            debugConfig.println("\tReading config: " + str4);
          }
          init(new URL(str4), localHashMap);
          i = 1;
        }
        catch (PropertyExpander.ExpandException localExpandException2)
        {
          throw ioException("Unable.to.properly.expand.config", new Object[] { str4 });
        }
      }
      if ((i == 0) && (j == 1) && (str4 == null))
      {
        if (debugConfig != null) {
          debugConfig.println("\tReading Policy from ~/.java.login.config");
        }
        str4 = System.getProperty("user.home");
        String str5 = str4 + File.separatorChar + ".java.login.config";
        if (new File(str5).exists()) {
          init(new File(str5).toURI().toURL(), localHashMap);
        }
      }
      configuration = localHashMap;
    }
    
    private void init(URL paramURL, Map<String, List<AppConfigurationEntry>> paramMap)
      throws IOException
    {
      try
      {
        InputStreamReader localInputStreamReader = new InputStreamReader(getInputStream(paramURL), "UTF-8");
        Object localObject1 = null;
        try
        {
          readConfig(localInputStreamReader, paramMap);
        }
        catch (Throwable localThrowable2)
        {
          localObject1 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localInputStreamReader != null) {
            if (localObject1 != null) {
              try
              {
                localInputStreamReader.close();
              }
              catch (Throwable localThrowable3)
              {
                ((Throwable)localObject1).addSuppressed(localThrowable3);
              }
            } else {
              localInputStreamReader.close();
            }
          }
        }
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        if (debugConfig != null) {
          debugConfig.println(localFileNotFoundException.toString());
        }
        throw new IOException(ResourcesMgr.getString("Configuration.Error.No.such.file.or.directory", "sun.security.util.AuthResources"));
      }
    }
    
    public AppConfigurationEntry[] engineGetAppConfigurationEntry(String paramString)
    {
      List localList = null;
      synchronized (configuration)
      {
        localList = (List)configuration.get(paramString);
      }
      if ((localList == null) || (localList.size() == 0)) {
        return null;
      }
      ??? = new AppConfigurationEntry[localList.size()];
      Iterator localIterator = localList.iterator();
      for (int i = 0; localIterator.hasNext(); i++)
      {
        AppConfigurationEntry localAppConfigurationEntry = (AppConfigurationEntry)localIterator.next();
        ???[i] = new AppConfigurationEntry(localAppConfigurationEntry.getLoginModuleName(), localAppConfigurationEntry.getControlFlag(), localAppConfigurationEntry.getOptions());
      }
      return (AppConfigurationEntry[])???;
    }
    
    public synchronized void engineRefresh()
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(new AuthPermission("refreshLoginConfiguration"));
      }
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          try
          {
            ConfigFile.Spi.this.init();
          }
          catch (IOException localIOException)
          {
            throw new SecurityException(localIOException.getLocalizedMessage(), localIOException);
          }
          return null;
        }
      });
    }
    
    private void readConfig(Reader paramReader, Map<String, List<AppConfigurationEntry>> paramMap)
      throws IOException
    {
      linenum = 1;
      if (!(paramReader instanceof BufferedReader)) {
        paramReader = new BufferedReader(paramReader);
      }
      st = new StreamTokenizer(paramReader);
      st.quoteChar(34);
      st.wordChars(36, 36);
      st.wordChars(95, 95);
      st.wordChars(45, 45);
      st.wordChars(42, 42);
      st.lowerCaseMode(false);
      st.slashSlashComments(true);
      st.slashStarComments(true);
      st.eolIsSignificant(true);
      lookahead = nextToken();
      while (lookahead != -1) {
        parseLoginEntry(paramMap);
      }
    }
    
    private void parseLoginEntry(Map<String, List<AppConfigurationEntry>> paramMap)
      throws IOException
    {
      LinkedList localLinkedList = new LinkedList();
      String str1 = st.sval;
      lookahead = nextToken();
      if (debugParser != null) {
        debugParser.println("\tReading next config entry: " + str1);
      }
      match("{");
      while (!peek("}"))
      {
        String str2 = match("module class name");
        String str3 = match("controlFlag").toUpperCase(Locale.ENGLISH);
        Object localObject1 = str3;
        int i = -1;
        switch (((String)localObject1).hashCode())
        {
        case 389487519: 
          if (((String)localObject1).equals("REQUIRED")) {
            i = 0;
          }
          break;
        case -810754599: 
          if (((String)localObject1).equals("REQUISITE")) {
            i = 1;
          }
          break;
        case -848090850: 
          if (((String)localObject1).equals("SUFFICIENT")) {
            i = 2;
          }
          break;
        case 703609696: 
          if (((String)localObject1).equals("OPTIONAL")) {
            i = 3;
          }
          break;
        }
        AppConfigurationEntry.LoginModuleControlFlag localLoginModuleControlFlag;
        switch (i)
        {
        case 0: 
          localLoginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
          break;
        case 1: 
          localLoginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
          break;
        case 2: 
          localLoginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
          break;
        case 3: 
          localLoginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
          break;
        default: 
          throw ioException("Configuration.Error.Invalid.control.flag.flag", new Object[] { str3 });
        }
        localObject1 = new HashMap();
        Object localObject2;
        while (!peek(";"))
        {
          localObject2 = match("option key");
          match("=");
          try
          {
            ((Map)localObject1).put(localObject2, expand(match("option value")));
          }
          catch (PropertyExpander.ExpandException localExpandException)
          {
            throw new IOException(localExpandException.getLocalizedMessage());
          }
        }
        lookahead = nextToken();
        if (debugParser != null)
        {
          debugParser.println("\t\t" + str2 + ", " + str3);
          localObject2 = ((Map)localObject1).keySet().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            String str4 = (String)((Iterator)localObject2).next();
            debugParser.println("\t\t\t" + str4 + "=" + (String)((Map)localObject1).get(str4));
          }
        }
        localLinkedList.add(new AppConfigurationEntry(str2, localLoginModuleControlFlag, (Map)localObject1));
      }
      match("}");
      match(";");
      if (paramMap.containsKey(str1)) {
        throw ioException("Configuration.Error.Can.not.specify.multiple.entries.for.appName", new Object[] { str1 });
      }
      paramMap.put(str1, localLinkedList);
    }
    
    private String match(String paramString)
      throws IOException
    {
      String str = null;
      switch (lookahead)
      {
      case -1: 
        throw ioException("Configuration.Error.expected.expect.read.end.of.file.", new Object[] { paramString });
      case -3: 
      case 34: 
        if ((paramString.equalsIgnoreCase("module class name")) || (paramString.equalsIgnoreCase("controlFlag")) || (paramString.equalsIgnoreCase("option key")) || (paramString.equalsIgnoreCase("option value")))
        {
          str = st.sval;
          lookahead = nextToken();
        }
        else
        {
          throw ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Object[] { new Integer(linenum), paramString, st.sval });
        }
        break;
      case 123: 
        if (paramString.equalsIgnoreCase("{")) {
          lookahead = nextToken();
        } else {
          throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(linenum), paramString, st.sval });
        }
        break;
      case 59: 
        if (paramString.equalsIgnoreCase(";")) {
          lookahead = nextToken();
        } else {
          throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(linenum), paramString, st.sval });
        }
        break;
      case 125: 
        if (paramString.equalsIgnoreCase("}")) {
          lookahead = nextToken();
        } else {
          throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(linenum), paramString, st.sval });
        }
        break;
      case 61: 
        if (paramString.equalsIgnoreCase("=")) {
          lookahead = nextToken();
        } else {
          throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(linenum), paramString, st.sval });
        }
        break;
      default: 
        throw ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Object[] { new Integer(linenum), paramString, st.sval });
      }
      return str;
    }
    
    private boolean peek(String paramString)
    {
      switch (lookahead)
      {
      case 44: 
        return paramString.equalsIgnoreCase(",");
      case 59: 
        return paramString.equalsIgnoreCase(";");
      case 123: 
        return paramString.equalsIgnoreCase("{");
      case 125: 
        return paramString.equalsIgnoreCase("}");
      }
      return false;
    }
    
    private int nextToken()
      throws IOException
    {
      int i;
      while ((i = st.nextToken()) == 10) {
        linenum += 1;
      }
      return i;
    }
    
    private InputStream getInputStream(URL paramURL)
      throws IOException
    {
      if ("file".equalsIgnoreCase(paramURL.getProtocol())) {
        try
        {
          return paramURL.openStream();
        }
        catch (Exception localException)
        {
          String str = paramURL.getPath();
          if (paramURL.getHost().length() > 0) {
            str = "//" + paramURL.getHost() + str;
          }
          if (debugConfig != null) {
            debugConfig.println("cannot read " + paramURL + ", try " + str);
          }
          return new FileInputStream(str);
        }
      }
      return paramURL.openStream();
    }
    
    private String expand(String paramString)
      throws PropertyExpander.ExpandException, IOException
    {
      if (paramString.isEmpty()) {
        return paramString;
      }
      if (!expandProp) {
        return paramString;
      }
      String str = PropertyExpander.expand(paramString);
      if ((str == null) || (str.length() == 0)) {
        throw ioException("Configuration.Error.Line.line.system.property.value.expanded.to.empty.value", new Object[] { new Integer(linenum), paramString });
      }
      return str;
    }
    
    private IOException ioException(String paramString, Object... paramVarArgs)
    {
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString(paramString, "sun.security.util.AuthResources"));
      return new IOException(localMessageFormat.format(paramVarArgs));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\ConfigFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */