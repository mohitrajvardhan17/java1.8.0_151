package javax.management.loading;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MLetContent
{
  private Map<String, String> attributes;
  private List<String> types;
  private List<String> values;
  private URL documentURL;
  private URL baseURL;
  
  public MLetContent(URL paramURL, Map<String, String> paramMap, List<String> paramList1, List<String> paramList2)
  {
    documentURL = paramURL;
    attributes = Collections.unmodifiableMap(paramMap);
    types = Collections.unmodifiableList(paramList1);
    values = Collections.unmodifiableList(paramList2);
    String str1 = getParameter("codebase");
    if (str1 != null)
    {
      if (!str1.endsWith("/")) {
        str1 = str1 + "/";
      }
      try
      {
        baseURL = new URL(documentURL, str1);
      }
      catch (MalformedURLException localMalformedURLException1) {}
    }
    if (baseURL == null)
    {
      String str2 = documentURL.getFile();
      int i = str2.lastIndexOf('/');
      if ((i >= 0) && (i < str2.length() - 1)) {
        try
        {
          baseURL = new URL(documentURL, str2.substring(0, i + 1));
        }
        catch (MalformedURLException localMalformedURLException2) {}
      }
    }
    if (baseURL == null) {
      baseURL = documentURL;
    }
  }
  
  public Map<String, String> getAttributes()
  {
    return attributes;
  }
  
  public URL getDocumentBase()
  {
    return documentURL;
  }
  
  public URL getCodeBase()
  {
    return baseURL;
  }
  
  public String getJarFiles()
  {
    return getParameter("archive");
  }
  
  public String getCode()
  {
    return getParameter("code");
  }
  
  public String getSerializedObject()
  {
    return getParameter("object");
  }
  
  public String getName()
  {
    return getParameter("name");
  }
  
  public String getVersion()
  {
    return getParameter("version");
  }
  
  public List<String> getParameterTypes()
  {
    return types;
  }
  
  public List<String> getParameterValues()
  {
    return values;
  }
  
  private String getParameter(String paramString)
  {
    return (String)attributes.get(paramString.toLowerCase());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\MLetContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */