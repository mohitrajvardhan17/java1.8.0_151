package com.sun.xml.internal.bind.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.lang.model.SourceVersion;

public abstract interface NameConverter
{
  public static final NameConverter standard = new Standard();
  public static final NameConverter jaxrpcCompatible = new Standard()
  {
    protected boolean isPunct(char paramAnonymousChar)
    {
      return (paramAnonymousChar == '.') || (paramAnonymousChar == '-') || (paramAnonymousChar == ';') || (paramAnonymousChar == '·') || (paramAnonymousChar == '·') || (paramAnonymousChar == '۝') || (paramAnonymousChar == '۞');
    }
    
    protected boolean isLetter(char paramAnonymousChar)
    {
      return (super.isLetter(paramAnonymousChar)) || (paramAnonymousChar == '_');
    }
    
    protected int classify(char paramAnonymousChar)
    {
      if (paramAnonymousChar == '_') {
        return 2;
      }
      return super.classify(paramAnonymousChar);
    }
  };
  public static final NameConverter smart = new Standard()
  {
    public String toConstantName(String paramAnonymousString)
    {
      String str = super.toConstantName(paramAnonymousString);
      if (!SourceVersion.isKeyword(str)) {
        return str;
      }
      return '_' + str;
    }
  };
  
  public abstract String toClassName(String paramString);
  
  public abstract String toInterfaceName(String paramString);
  
  public abstract String toPropertyName(String paramString);
  
  public abstract String toConstantName(String paramString);
  
  public abstract String toVariableName(String paramString);
  
  public abstract String toPackageName(String paramString);
  
  public static class Standard
    extends NameUtil
    implements NameConverter
  {
    public Standard() {}
    
    public String toClassName(String paramString)
    {
      return toMixedCaseName(toWordList(paramString), true);
    }
    
    public String toVariableName(String paramString)
    {
      return toMixedCaseName(toWordList(paramString), false);
    }
    
    public String toInterfaceName(String paramString)
    {
      return toClassName(paramString);
    }
    
    public String toPropertyName(String paramString)
    {
      String str = toClassName(paramString);
      if (str.equals("Class")) {
        str = "Clazz";
      }
      return str;
    }
    
    public String toConstantName(String paramString)
    {
      return super.toConstantName(paramString);
    }
    
    public String toPackageName(String paramString)
    {
      int i = paramString.indexOf(':');
      String str1 = "";
      if (i >= 0)
      {
        str1 = paramString.substring(0, i);
        if ((str1.equalsIgnoreCase("http")) || (str1.equalsIgnoreCase("urn"))) {
          paramString = paramString.substring(i + 1);
        }
      }
      ArrayList localArrayList1 = tokenize(paramString, "/: ");
      if (localArrayList1.size() == 0) {
        return null;
      }
      if (localArrayList1.size() > 1)
      {
        str2 = (String)localArrayList1.get(localArrayList1.size() - 1);
        i = str2.lastIndexOf('.');
        if (i > 0)
        {
          str2 = str2.substring(0, i);
          localArrayList1.set(localArrayList1.size() - 1, str2);
        }
      }
      String str2 = (String)localArrayList1.get(0);
      i = str2.indexOf(':');
      if (i >= 0) {
        str2 = str2.substring(0, i);
      }
      ArrayList localArrayList2 = reverse(tokenize(str2, str1.equals("urn") ? ".-" : "."));
      if (((String)localArrayList2.get(localArrayList2.size() - 1)).equalsIgnoreCase("www")) {
        localArrayList2.remove(localArrayList2.size() - 1);
      }
      localArrayList1.addAll(1, localArrayList2);
      localArrayList1.remove(0);
      for (int j = 0; j < localArrayList1.size(); j++)
      {
        String str3 = (String)localArrayList1.get(j);
        str3 = removeIllegalIdentifierChars(str3);
        if (SourceVersion.isKeyword(str3.toLowerCase())) {
          str3 = '_' + str3;
        }
        localArrayList1.set(j, str3.toLowerCase());
      }
      return combine(localArrayList1, '.');
    }
    
    private static String removeIllegalIdentifierChars(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder(paramString.length() + 1);
      for (int i = 0; i < paramString.length(); i++)
      {
        char c = paramString.charAt(i);
        if ((i == 0) && (!Character.isJavaIdentifierStart(c))) {
          localStringBuilder.append('_');
        }
        if (!Character.isJavaIdentifierPart(c)) {
          localStringBuilder.append('_');
        } else {
          localStringBuilder.append(c);
        }
      }
      return localStringBuilder.toString();
    }
    
    private static ArrayList<String> tokenize(String paramString1, String paramString2)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, paramString2);
      ArrayList localArrayList = new ArrayList();
      while (localStringTokenizer.hasMoreTokens()) {
        localArrayList.add(localStringTokenizer.nextToken());
      }
      return localArrayList;
    }
    
    private static <T> ArrayList<T> reverse(List<T> paramList)
    {
      ArrayList localArrayList = new ArrayList();
      for (int i = paramList.size() - 1; i >= 0; i--) {
        localArrayList.add(paramList.get(i));
      }
      return localArrayList;
    }
    
    private static String combine(List paramList, char paramChar)
    {
      StringBuilder localStringBuilder = new StringBuilder(paramList.get(0).toString());
      for (int i = 1; i < paramList.size(); i++)
      {
        localStringBuilder.append(paramChar);
        localStringBuilder.append(paramList.get(i));
      }
      return localStringBuilder.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\api\impl\NameConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */