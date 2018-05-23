package sun.misc;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

public class ExtensionInfo
{
  public static final int COMPATIBLE = 0;
  public static final int REQUIRE_SPECIFICATION_UPGRADE = 1;
  public static final int REQUIRE_IMPLEMENTATION_UPGRADE = 2;
  public static final int REQUIRE_VENDOR_SWITCH = 3;
  public static final int INCOMPATIBLE = 4;
  public String title;
  public String name;
  public String specVersion;
  public String specVendor;
  public String implementationVersion;
  public String vendor;
  public String vendorId;
  public String url;
  private static final ResourceBundle rb = ResourceBundle.getBundle("sun.misc.resources.Messages");
  
  public ExtensionInfo() {}
  
  public ExtensionInfo(String paramString, Attributes paramAttributes)
    throws NullPointerException
  {
    String str1;
    if (paramString != null) {
      str1 = paramString + "-";
    } else {
      str1 = "";
    }
    String str2 = str1 + Attributes.Name.EXTENSION_NAME.toString();
    name = paramAttributes.getValue(str2);
    if (name != null) {
      name = name.trim();
    }
    str2 = str1 + Attributes.Name.SPECIFICATION_TITLE.toString();
    title = paramAttributes.getValue(str2);
    if (title != null) {
      title = title.trim();
    }
    str2 = str1 + Attributes.Name.SPECIFICATION_VERSION.toString();
    specVersion = paramAttributes.getValue(str2);
    if (specVersion != null) {
      specVersion = specVersion.trim();
    }
    str2 = str1 + Attributes.Name.SPECIFICATION_VENDOR.toString();
    specVendor = paramAttributes.getValue(str2);
    if (specVendor != null) {
      specVendor = specVendor.trim();
    }
    str2 = str1 + Attributes.Name.IMPLEMENTATION_VERSION.toString();
    implementationVersion = paramAttributes.getValue(str2);
    if (implementationVersion != null) {
      implementationVersion = implementationVersion.trim();
    }
    str2 = str1 + Attributes.Name.IMPLEMENTATION_VENDOR.toString();
    vendor = paramAttributes.getValue(str2);
    if (vendor != null) {
      vendor = vendor.trim();
    }
    str2 = str1 + Attributes.Name.IMPLEMENTATION_VENDOR_ID.toString();
    vendorId = paramAttributes.getValue(str2);
    if (vendorId != null) {
      vendorId = vendorId.trim();
    }
    str2 = str1 + Attributes.Name.IMPLEMENTATION_URL.toString();
    url = paramAttributes.getValue(str2);
    if (url != null) {
      url = url.trim();
    }
  }
  
  public int isCompatibleWith(ExtensionInfo paramExtensionInfo)
  {
    if ((name == null) || (name == null)) {
      return 4;
    }
    if (name.compareTo(name) == 0)
    {
      if ((specVersion == null) || (specVersion == null)) {
        return 0;
      }
      int i = compareExtensionVersion(specVersion, specVersion);
      if (i < 0)
      {
        if ((vendorId != null) && (vendorId != null) && (vendorId.compareTo(vendorId) != 0)) {
          return 3;
        }
        return 1;
      }
      if ((vendorId != null) && (vendorId != null))
      {
        if (vendorId.compareTo(vendorId) != 0) {
          return 3;
        }
        if ((implementationVersion != null) && (implementationVersion != null))
        {
          i = compareExtensionVersion(implementationVersion, implementationVersion);
          if (i < 0) {
            return 2;
          }
        }
      }
      return 0;
    }
    return 4;
  }
  
  public String toString()
  {
    return "Extension : title(" + title + "), name(" + name + "), spec vendor(" + specVendor + "), spec version(" + specVersion + "), impl vendor(" + vendor + "), impl vendor id(" + vendorId + "), impl version(" + implementationVersion + "), impl url(" + url + ")";
  }
  
  private int compareExtensionVersion(String paramString1, String paramString2)
    throws NumberFormatException
  {
    paramString1 = paramString1.toLowerCase();
    paramString2 = paramString2.toLowerCase();
    return strictCompareExtensionVersion(paramString1, paramString2);
  }
  
  private int strictCompareExtensionVersion(String paramString1, String paramString2)
    throws NumberFormatException
  {
    if (paramString1.equals(paramString2)) {
      return 0;
    }
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString1, ".,");
    StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString2, ".,");
    int i = 0;
    int j = 0;
    int k = 0;
    if (localStringTokenizer1.hasMoreTokens()) {
      i = convertToken(localStringTokenizer1.nextToken().toString());
    }
    if (localStringTokenizer2.hasMoreTokens()) {
      j = convertToken(localStringTokenizer2.nextToken().toString());
    }
    if (i > j) {
      return 1;
    }
    if (j > i) {
      return -1;
    }
    int m = paramString1.indexOf(".");
    int n = paramString2.indexOf(".");
    if (m == -1) {
      m = paramString1.length() - 1;
    }
    if (n == -1) {
      n = paramString2.length() - 1;
    }
    return strictCompareExtensionVersion(paramString1.substring(m + 1), paramString2.substring(n + 1));
  }
  
  private int convertToken(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return 0;
    }
    int i = 0;
    int j = 0;
    int k = 0;
    int m = paramString.length();
    int n = m;
    Object[] arrayOfObject = { name };
    MessageFormat localMessageFormat = new MessageFormat(rb.getString("optpkg.versionerror"));
    String str1 = localMessageFormat.format(arrayOfObject);
    int i1 = paramString.indexOf("-");
    int i2 = paramString.indexOf("_");
    if ((i1 == -1) && (i2 == -1)) {
      try
      {
        return Integer.parseInt(paramString) * 100;
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        System.out.println(str1);
        return 0;
      }
    }
    int i3;
    if (i2 != -1)
    {
      try
      {
        i3 = Integer.parseInt(paramString.substring(0, i2));
        char c = paramString.charAt(m - 1);
        if (Character.isLetter(c))
        {
          i = Character.getNumericValue(c);
          n = m - 1;
          k = Integer.parseInt(paramString.substring(i2 + 1, n));
          if ((i >= Character.getNumericValue('a')) && (i <= Character.getNumericValue('z')))
          {
            j = k * 100 + i;
          }
          else
          {
            j = 0;
            System.out.println(str1);
          }
        }
        else
        {
          k = Integer.parseInt(paramString.substring(i2 + 1, n));
        }
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        System.out.println(str1);
        return 0;
      }
      return i3 * 100 + (k + j);
    }
    try
    {
      i3 = Integer.parseInt(paramString.substring(0, i1));
    }
    catch (NumberFormatException localNumberFormatException3)
    {
      System.out.println(str1);
      return 0;
    }
    String str2 = paramString.substring(i1 + 1);
    String str3 = "";
    int i4 = 0;
    if (str2.indexOf("ea") != -1)
    {
      str3 = str2.substring(2);
      i4 = 50;
    }
    else if (str2.indexOf("alpha") != -1)
    {
      str3 = str2.substring(5);
      i4 = 40;
    }
    else if (str2.indexOf("beta") != -1)
    {
      str3 = str2.substring(4);
      i4 = 30;
    }
    else if (str2.indexOf("rc") != -1)
    {
      str3 = str2.substring(2);
      i4 = 20;
    }
    if ((str3 == null) || (str3.equals(""))) {
      return i3 * 100 - i4;
    }
    try
    {
      return i3 * 100 - i4 + Integer.parseInt(str3);
    }
    catch (NumberFormatException localNumberFormatException4)
    {
      System.out.println(str1);
    }
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\ExtensionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */