package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class MLetParser
{
  private int c;
  private static String tag = "mlet";
  
  public MLetParser() {}
  
  public void skipSpace(Reader paramReader)
    throws IOException
  {
    while ((c >= 0) && ((c == 32) || (c == 9) || (c == 10) || (c == 13))) {
      c = paramReader.read();
    }
  }
  
  public String scanIdentifier(Reader paramReader)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    while (((c >= 97) && (c <= 122)) || ((c >= 65) && (c <= 90)) || ((c >= 48) && (c <= 57)) || (c == 95))
    {
      localStringBuilder.append((char)c);
      c = paramReader.read();
    }
    return localStringBuilder.toString();
  }
  
  public Map<String, String> scanTag(Reader paramReader)
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    skipSpace(paramReader);
    while ((c >= 0) && (c != 62))
    {
      if (c == 60) {
        throw new IOException("Missing '>' in tag");
      }
      String str1 = scanIdentifier(paramReader);
      String str2 = "";
      skipSpace(paramReader);
      if (c == 61)
      {
        int i = -1;
        c = paramReader.read();
        skipSpace(paramReader);
        if ((c == 39) || (c == 34))
        {
          i = c;
          c = paramReader.read();
        }
        StringBuilder localStringBuilder = new StringBuilder();
        while ((c > 0) && (((i < 0) && (c != 32) && (c != 9) && (c != 10) && (c != 13) && (c != 62)) || ((i >= 0) && (c != i))))
        {
          localStringBuilder.append((char)c);
          c = paramReader.read();
        }
        if (c == i) {
          c = paramReader.read();
        }
        skipSpace(paramReader);
        str2 = localStringBuilder.toString();
      }
      localHashMap.put(str1.toLowerCase(), str2);
      skipSpace(paramReader);
    }
    return localHashMap;
  }
  
  public List<MLetContent> parse(URL paramURL)
    throws IOException
  {
    String str1 = "parse";
    String str2 = "<arg type=... value=...> tag requires type parameter.";
    String str3 = "<arg type=... value=...> tag requires value parameter.";
    String str4 = "<arg> tag outside <mlet> ... </mlet>.";
    String str5 = "<mlet> tag requires either code or object parameter.";
    String str6 = "<mlet> tag requires archive parameter.";
    URLConnection localURLConnection = paramURL.openConnection();
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream(), "UTF-8"));
    paramURL = localURLConnection.getURL();
    ArrayList localArrayList1 = new ArrayList();
    Map localMap1 = null;
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    for (;;)
    {
      c = localBufferedReader.read();
      if (c == -1) {
        break;
      }
      if (c == 60)
      {
        c = localBufferedReader.read();
        String str7;
        if (c == 47)
        {
          c = localBufferedReader.read();
          str7 = scanIdentifier(localBufferedReader);
          if (c != 62) {
            throw new IOException("Missing '>' in tag");
          }
          if (str7.equalsIgnoreCase(tag))
          {
            if (localMap1 != null) {
              localArrayList1.add(new MLetContent(paramURL, localMap1, localArrayList2, localArrayList3));
            }
            localMap1 = null;
            localArrayList2 = new ArrayList();
            localArrayList3 = new ArrayList();
          }
        }
        else
        {
          str7 = scanIdentifier(localBufferedReader);
          if (str7.equalsIgnoreCase("arg"))
          {
            Map localMap2 = scanTag(localBufferedReader);
            String str8 = (String)localMap2.get("type");
            if (str8 == null)
            {
              JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str2);
              throw new IOException(str2);
            }
            if (localMap1 != null)
            {
              localArrayList2.add(str8);
            }
            else
            {
              JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str4);
              throw new IOException(str4);
            }
            String str9 = (String)localMap2.get("value");
            if (str9 == null)
            {
              JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str3);
              throw new IOException(str3);
            }
            if (localMap1 != null)
            {
              localArrayList3.add(str9);
            }
            else
            {
              JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str4);
              throw new IOException(str4);
            }
          }
          else if (str7.equalsIgnoreCase(tag))
          {
            localMap1 = scanTag(localBufferedReader);
            if ((localMap1.get("code") == null) && (localMap1.get("object") == null))
            {
              JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str5);
              throw new IOException(str5);
            }
            if (localMap1.get("archive") == null)
            {
              JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str6);
              throw new IOException(str6);
            }
          }
        }
      }
    }
    localBufferedReader.close();
    return localArrayList1;
  }
  
  public List<MLetContent> parseURL(String paramString)
    throws IOException
  {
    URL localURL;
    if (paramString.indexOf(':') <= 1)
    {
      String str1 = System.getProperty("user.dir");
      String str2;
      if ((str1.charAt(0) == '/') || (str1.charAt(0) == File.separatorChar)) {
        str2 = "file:";
      } else {
        str2 = "file:/";
      }
      localURL = new URL(str2 + str1.replace(File.separatorChar, '/') + "/");
      localURL = new URL(localURL, paramString);
    }
    else
    {
      localURL = new URL(paramString);
    }
    return parse(localURL);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\MLetParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */