package sun.security.krb5;

import java.io.IOException;
import java.security.AccessController;
import java.util.LinkedList;
import sun.security.action.GetBooleanAction;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Realm
  implements Cloneable
{
  public static final boolean AUTODEDUCEREALM = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.autodeducerealm"))).booleanValue();
  private final String realm;
  
  public Realm(String paramString)
    throws RealmException
  {
    realm = parseRealm(paramString);
  }
  
  public static Realm getDefault()
    throws RealmException
  {
    try
    {
      return new Realm(Config.getInstance().getDefaultRealm());
    }
    catch (RealmException localRealmException)
    {
      throw localRealmException;
    }
    catch (KrbException localKrbException)
    {
      throw new RealmException(localKrbException);
    }
  }
  
  public Object clone()
  {
    return this;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Realm)) {
      return false;
    }
    Realm localRealm = (Realm)paramObject;
    return realm.equals(realm);
  }
  
  public int hashCode()
  {
    return realm.hashCode();
  }
  
  public Realm(DerValue paramDerValue)
    throws Asn1Exception, RealmException, IOException
  {
    if (paramDerValue == null) {
      throw new IllegalArgumentException("encoding can not be null");
    }
    realm = new KerberosString(paramDerValue).toString();
    if ((realm == null) || (realm.length() == 0)) {
      throw new RealmException(601);
    }
    if (!isValidRealmString(realm)) {
      throw new RealmException(600);
    }
  }
  
  public String toString()
  {
    return realm;
  }
  
  public static String parseRealmAtSeparator(String paramString)
    throws RealmException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null input name is not allowed");
    }
    String str1 = new String(paramString);
    String str2 = null;
    for (int i = 0; i < str1.length(); i++) {
      if ((str1.charAt(i) == '@') && ((i == 0) || (str1.charAt(i - 1) != '\\')))
      {
        if (i + 1 < str1.length())
        {
          str2 = str1.substring(i + 1, str1.length());
          break;
        }
        throw new IllegalArgumentException("empty realm part not allowed");
      }
    }
    if (str2 != null)
    {
      if (str2.length() == 0) {
        throw new RealmException(601);
      }
      if (!isValidRealmString(str2)) {
        throw new RealmException(600);
      }
    }
    return str2;
  }
  
  public static String parseRealmComponent(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null input name is not allowed");
    }
    String str1 = new String(paramString);
    String str2 = null;
    for (int i = 0; i < str1.length(); i++) {
      if ((str1.charAt(i) == '.') && ((i == 0) || (str1.charAt(i - 1) != '\\')))
      {
        if (i + 1 >= str1.length()) {
          break;
        }
        str2 = str1.substring(i + 1, str1.length());
        break;
      }
    }
    return str2;
  }
  
  protected static String parseRealm(String paramString)
    throws RealmException
  {
    String str = parseRealmAtSeparator(paramString);
    if (str == null) {
      str = paramString;
    }
    if ((str == null) || (str.length() == 0)) {
      throw new RealmException(601);
    }
    if (!isValidRealmString(str)) {
      throw new RealmException(600);
    }
    return str;
  }
  
  protected static boolean isValidRealmString(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if (paramString.length() == 0) {
      return false;
    }
    for (int i = 0; i < paramString.length(); i++) {
      if ((paramString.charAt(i) == '/') || (paramString.charAt(i) == ':') || (paramString.charAt(i) == 0)) {
        return false;
      }
    }
    return true;
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putDerValue(new KerberosString(realm).toDerValue());
    return localDerOutputStream.toByteArray();
  }
  
  public static Realm parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException, RealmException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new Realm(localDerValue2);
  }
  
  public static String[] getRealmsList(String paramString1, String paramString2)
  {
    try
    {
      return parseCapaths(paramString1, paramString2);
    }
    catch (KrbException localKrbException) {}
    return parseHierarchy(paramString1, paramString2);
  }
  
  private static String[] parseCapaths(String paramString1, String paramString2)
    throws KrbException
  {
    Config localConfig = Config.getInstance();
    if (!localConfig.exists(new String[] { "capaths", paramString1, paramString2 })) {
      throw new KrbException("No conf");
    }
    LinkedList localLinkedList = new LinkedList();
    for (String str1 = paramString2;; str1 = (String)localLinkedList.getFirst())
    {
      String str2 = localConfig.getAll(new String[] { "capaths", paramString1, str1 });
      if (str2 == null) {
        break;
      }
      String[] arrayOfString = str2.split("\\s+");
      int i = 0;
      for (int j = arrayOfString.length - 1; j >= 0; j--) {
        if ((!localLinkedList.contains(arrayOfString[j])) && (!arrayOfString[j].equals(".")) && (!arrayOfString[j].equals(paramString1)) && (!arrayOfString[j].equals(paramString2)) && (!arrayOfString[j].equals(str1)))
        {
          i = 1;
          localLinkedList.addFirst(arrayOfString[j]);
        }
      }
      if (i == 0) {
        break;
      }
    }
    localLinkedList.addFirst(paramString1);
    return (String[])localLinkedList.toArray(new String[localLinkedList.size()]);
  }
  
  private static String[] parseHierarchy(String paramString1, String paramString2)
  {
    String[] arrayOfString1 = paramString1.split("\\.");
    String[] arrayOfString2 = paramString2.split("\\.");
    int i = arrayOfString1.length;
    int j = arrayOfString2.length;
    int k = 0;
    j--;
    i--;
    while ((j >= 0) && (i >= 0) && (arrayOfString2[j].equals(arrayOfString1[i])))
    {
      k = 1;
      j--;
      i--;
    }
    LinkedList localLinkedList = new LinkedList();
    for (int m = 0; m <= i; m++) {
      localLinkedList.addLast(subStringFrom(arrayOfString1, m));
    }
    if (k != 0) {
      localLinkedList.addLast(subStringFrom(arrayOfString1, i + 1));
    }
    for (m = j; m >= 0; m--) {
      localLinkedList.addLast(subStringFrom(arrayOfString2, m));
    }
    localLinkedList.removeLast();
    return (String[])localLinkedList.toArray(new String[localLinkedList.size()]);
  }
  
  private static String subStringFrom(String[] paramArrayOfString, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = paramInt; i < paramArrayOfString.length; i++)
    {
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append('.');
      }
      localStringBuilder.append(paramArrayOfString[i]);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\Realm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */