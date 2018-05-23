package sun.security.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.text.Collator;
import java.util.Locale;
import java.util.ResourceBundle;

public class KeyStoreUtil
{
  private static final String JKS = "jks";
  private static final Collator collator = ;
  
  private KeyStoreUtil() {}
  
  public static boolean isWindowsKeyStore(String paramString)
  {
    return (paramString.equalsIgnoreCase("Windows-MY")) || (paramString.equalsIgnoreCase("Windows-ROOT"));
  }
  
  public static String niceStoreTypeName(String paramString)
  {
    if (paramString.equalsIgnoreCase("Windows-MY")) {
      return "Windows-MY";
    }
    if (paramString.equalsIgnoreCase("Windows-ROOT")) {
      return "Windows-ROOT";
    }
    return paramString.toUpperCase(Locale.ENGLISH);
  }
  
  public static KeyStore getCacertsKeyStore()
    throws Exception
  {
    String str = File.separator;
    File localFile = new File(System.getProperty("java.home") + str + "lib" + str + "security" + str + "cacerts");
    if (!localFile.exists()) {
      return null;
    }
    KeyStore localKeyStore = null;
    FileInputStream localFileInputStream = new FileInputStream(localFile);
    Object localObject1 = null;
    try
    {
      localKeyStore = KeyStore.getInstance("jks");
      localKeyStore.load(localFileInputStream, null);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileInputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localFileInputStream.close();
        }
      }
    }
    return localKeyStore;
  }
  
  public static char[] getPassWithModifier(String paramString1, String paramString2, ResourceBundle paramResourceBundle)
  {
    if (paramString1 == null) {
      return paramString2.toCharArray();
    }
    Object localObject1;
    if (collator.compare(paramString1, "env") == 0)
    {
      localObject1 = System.getenv(paramString2);
      if (localObject1 == null)
      {
        System.err.println(paramResourceBundle.getString("Cannot.find.environment.variable.") + paramString2);
        return null;
      }
      return ((String)localObject1).toCharArray();
    }
    if (collator.compare(paramString1, "file") == 0) {
      try
      {
        localObject1 = null;
        try
        {
          localObject1 = new URL(paramString2);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localObject2 = new File(paramString2);
          if (((File)localObject2).exists())
          {
            localObject1 = ((File)localObject2).toURI().toURL();
          }
          else
          {
            System.err.println(paramResourceBundle.getString("Cannot.find.file.") + paramString2);
            return null;
          }
        }
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(((URL)localObject1).openStream()));
        Object localObject2 = null;
        try
        {
          String str = localBufferedReader.readLine();
          if (str == null)
          {
            arrayOfChar = new char[0];
            return arrayOfChar;
          }
          char[] arrayOfChar = str.toCharArray();
          return arrayOfChar;
        }
        catch (Throwable localThrowable1)
        {
          localObject2 = localThrowable1;
          throw localThrowable1;
        }
        finally
        {
          if (localBufferedReader != null) {
            if (localObject2 != null) {
              try
              {
                localBufferedReader.close();
              }
              catch (Throwable localThrowable4)
              {
                ((Throwable)localObject2).addSuppressed(localThrowable4);
              }
            } else {
              localBufferedReader.close();
            }
          }
        }
        System.err.println(paramResourceBundle.getString("Unknown.password.type.") + paramString1);
      }
      catch (IOException localIOException)
      {
        System.err.println(localIOException);
        return null;
      }
    }
    return null;
  }
  
  static
  {
    collator.setStrength(0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\KeyStoreUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */