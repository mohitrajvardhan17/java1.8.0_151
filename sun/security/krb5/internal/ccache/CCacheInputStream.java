package sun.security.krb5.internal.ccache;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import sun.misc.IOUtils;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.AuthorizationDataEntry;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.util.KrbDataInputStream;

public class CCacheInputStream
  extends KrbDataInputStream
  implements FileCCacheConstants
{
  private static boolean DEBUG = Krb5.DEBUG;
  
  public CCacheInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public Tag readTag()
    throws IOException
  {
    char[] arrayOfChar = new char['Ѐ'];
    int j = -1;
    Integer localInteger1 = null;
    Integer localInteger2 = null;
    int i = read(2);
    if (i < 0) {
      throw new IOException("stop.");
    }
    if (i > arrayOfChar.length) {
      throw new IOException("Invalid tag length.");
    }
    while (i > 0)
    {
      j = read(2);
      int k = read(2);
      switch (j)
      {
      case 1: 
        localInteger1 = new Integer(read(4));
        localInteger2 = new Integer(read(4));
        break;
      }
      i -= 4 + k;
    }
    return new Tag(i, j, localInteger1, localInteger2);
  }
  
  public PrincipalName readPrincipal(int paramInt)
    throws IOException, RealmException
  {
    Object localObject = null;
    int i;
    if (paramInt == 1281) {
      i = 0;
    } else {
      i = read(4);
    }
    int j = readLength4();
    ArrayList localArrayList = new ArrayList();
    if (paramInt == 1281) {
      j--;
    }
    for (int m = 0; m <= j; m++)
    {
      int k = readLength4();
      byte[] arrayOfByte = IOUtils.readFully(this, k, true);
      localArrayList.add(new String(arrayOfByte));
    }
    if (localArrayList.isEmpty()) {
      throw new IOException("No realm or principal");
    }
    if (isRealm((String)localArrayList.get(0)))
    {
      String str = (String)localArrayList.remove(0);
      if (localArrayList.isEmpty()) {
        throw new IOException("No principal name components");
      }
      return new PrincipalName(i, (String[])localArrayList.toArray(new String[localArrayList.size()]), new Realm(str));
    }
    try
    {
      return new PrincipalName(i, (String[])localArrayList.toArray(new String[localArrayList.size()]), Realm.getDefault());
    }
    catch (RealmException localRealmException) {}
    return null;
  }
  
  boolean isRealm(String paramString)
  {
    try
    {
      Realm localRealm = new Realm(paramString);
    }
    catch (Exception localException)
    {
      return false;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      for (int i = 0; i < str.length(); i++) {
        if (str.charAt(i) >= '') {
          return false;
        }
      }
    }
    return true;
  }
  
  EncryptionKey readKey(int paramInt)
    throws IOException
  {
    int i = read(2);
    if (paramInt == 1283) {
      read(2);
    }
    int j = readLength4();
    byte[] arrayOfByte = IOUtils.readFully(this, j, true);
    return new EncryptionKey(arrayOfByte, i, new Integer(paramInt));
  }
  
  long[] readTimes()
    throws IOException
  {
    long[] arrayOfLong = new long[4];
    arrayOfLong[0] = (read(4) * 1000L);
    arrayOfLong[1] = (read(4) * 1000L);
    arrayOfLong[2] = (read(4) * 1000L);
    arrayOfLong[3] = (read(4) * 1000L);
    return arrayOfLong;
  }
  
  boolean readskey()
    throws IOException
  {
    return read() != 0;
  }
  
  HostAddress[] readAddr()
    throws IOException, KrbApErrException
  {
    int i = readLength4();
    if (i > 0)
    {
      ArrayList localArrayList = new ArrayList();
      for (int m = 0; m < i; m++)
      {
        int j = read(2);
        int k = readLength4();
        if ((k != 4) && (k != 16))
        {
          if (DEBUG) {
            System.out.println("Incorrect address format.");
          }
          return null;
        }
        byte[] arrayOfByte = new byte[k];
        for (int n = 0; n < k; n++) {
          arrayOfByte[n] = ((byte)read(1));
        }
        localArrayList.add(new HostAddress(j, arrayOfByte));
      }
      return (HostAddress[])localArrayList.toArray(new HostAddress[localArrayList.size()]);
    }
    return null;
  }
  
  AuthorizationDataEntry[] readAuth()
    throws IOException
  {
    int i = readLength4();
    if (i > 0)
    {
      ArrayList localArrayList = new ArrayList();
      byte[] arrayOfByte = null;
      for (int m = 0; m < i; m++)
      {
        int j = read(2);
        int k = readLength4();
        arrayOfByte = IOUtils.readFully(this, k, true);
        localArrayList.add(new AuthorizationDataEntry(j, arrayOfByte));
      }
      return (AuthorizationDataEntry[])localArrayList.toArray(new AuthorizationDataEntry[localArrayList.size()]);
    }
    return null;
  }
  
  byte[] readData()
    throws IOException
  {
    int i = readLength4();
    if (i == 0) {
      return null;
    }
    return IOUtils.readFully(this, i, true);
  }
  
  boolean[] readFlags()
    throws IOException
  {
    boolean[] arrayOfBoolean = new boolean[32];
    int i = read(4);
    if ((i & 0x40000000) == 1073741824) {
      arrayOfBoolean[1] = true;
    }
    if ((i & 0x20000000) == 536870912) {
      arrayOfBoolean[2] = true;
    }
    if ((i & 0x10000000) == 268435456) {
      arrayOfBoolean[3] = true;
    }
    if ((i & 0x8000000) == 134217728) {
      arrayOfBoolean[4] = true;
    }
    if ((i & 0x4000000) == 67108864) {
      arrayOfBoolean[5] = true;
    }
    if ((i & 0x2000000) == 33554432) {
      arrayOfBoolean[6] = true;
    }
    if ((i & 0x1000000) == 16777216) {
      arrayOfBoolean[7] = true;
    }
    if ((i & 0x800000) == 8388608) {
      arrayOfBoolean[8] = true;
    }
    if ((i & 0x400000) == 4194304) {
      arrayOfBoolean[9] = true;
    }
    if ((i & 0x200000) == 2097152) {
      arrayOfBoolean[10] = true;
    }
    if ((i & 0x100000) == 1048576) {
      arrayOfBoolean[11] = true;
    }
    if (DEBUG)
    {
      String str = ">>> CCacheInputStream: readFlags() ";
      if (arrayOfBoolean[1] == 1) {
        str = str + " FORWARDABLE;";
      }
      if (arrayOfBoolean[2] == 1) {
        str = str + " FORWARDED;";
      }
      if (arrayOfBoolean[3] == 1) {
        str = str + " PROXIABLE;";
      }
      if (arrayOfBoolean[4] == 1) {
        str = str + " PROXY;";
      }
      if (arrayOfBoolean[5] == 1) {
        str = str + " MAY_POSTDATE;";
      }
      if (arrayOfBoolean[6] == 1) {
        str = str + " POSTDATED;";
      }
      if (arrayOfBoolean[7] == 1) {
        str = str + " INVALID;";
      }
      if (arrayOfBoolean[8] == 1) {
        str = str + " RENEWABLE;";
      }
      if (arrayOfBoolean[9] == 1) {
        str = str + " INITIAL;";
      }
      if (arrayOfBoolean[10] == 1) {
        str = str + " PRE_AUTH;";
      }
      if (arrayOfBoolean[11] == 1) {
        str = str + " HW_AUTH;";
      }
      System.out.println(str);
    }
    return arrayOfBoolean;
  }
  
  Credentials readCred(int paramInt)
    throws IOException, RealmException, KrbApErrException, Asn1Exception
  {
    PrincipalName localPrincipalName1 = null;
    try
    {
      localPrincipalName1 = readPrincipal(paramInt);
    }
    catch (Exception localException1) {}
    if (DEBUG) {
      System.out.println(">>>DEBUG <CCacheInputStream>  client principal is " + localPrincipalName1);
    }
    PrincipalName localPrincipalName2 = null;
    try
    {
      localPrincipalName2 = readPrincipal(paramInt);
    }
    catch (Exception localException2) {}
    if (DEBUG) {
      System.out.println(">>>DEBUG <CCacheInputStream> server principal is " + localPrincipalName2);
    }
    EncryptionKey localEncryptionKey = readKey(paramInt);
    if (DEBUG) {
      System.out.println(">>>DEBUG <CCacheInputStream> key type: " + localEncryptionKey.getEType());
    }
    long[] arrayOfLong = readTimes();
    KerberosTime localKerberosTime1 = new KerberosTime(arrayOfLong[0]);
    KerberosTime localKerberosTime2 = arrayOfLong[1] == 0L ? null : new KerberosTime(arrayOfLong[1]);
    KerberosTime localKerberosTime3 = new KerberosTime(arrayOfLong[2]);
    KerberosTime localKerberosTime4 = arrayOfLong[3] == 0L ? null : new KerberosTime(arrayOfLong[3]);
    if (DEBUG)
    {
      System.out.println(">>>DEBUG <CCacheInputStream> auth time: " + localKerberosTime1.toDate().toString());
      System.out.println(">>>DEBUG <CCacheInputStream> start time: " + (localKerberosTime2 == null ? "null" : localKerberosTime2.toDate().toString()));
      System.out.println(">>>DEBUG <CCacheInputStream> end time: " + localKerberosTime3.toDate().toString());
      System.out.println(">>>DEBUG <CCacheInputStream> renew_till time: " + (localKerberosTime4 == null ? "null" : localKerberosTime4.toDate().toString()));
    }
    boolean bool = readskey();
    boolean[] arrayOfBoolean = readFlags();
    TicketFlags localTicketFlags = new TicketFlags(arrayOfBoolean);
    HostAddress[] arrayOfHostAddress = readAddr();
    HostAddresses localHostAddresses = null;
    if (arrayOfHostAddress != null) {
      localHostAddresses = new HostAddresses(arrayOfHostAddress);
    }
    AuthorizationDataEntry[] arrayOfAuthorizationDataEntry = readAuth();
    AuthorizationData localAuthorizationData = null;
    if (arrayOfAuthorizationDataEntry != null) {
      localAuthorizationData = new AuthorizationData(arrayOfAuthorizationDataEntry);
    }
    byte[] arrayOfByte1 = readData();
    byte[] arrayOfByte2 = readData();
    if ((localPrincipalName1 == null) || (localPrincipalName2 == null)) {
      return null;
    }
    try
    {
      return new Credentials(localPrincipalName1, localPrincipalName2, localEncryptionKey, localKerberosTime1, localKerberosTime2, localKerberosTime3, localKerberosTime4, bool, localTicketFlags, localHostAddresses, localAuthorizationData, arrayOfByte1 != null ? new Ticket(arrayOfByte1) : null, arrayOfByte2 != null ? new Ticket(arrayOfByte2) : null);
    }
    catch (Exception localException3) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ccache\CCacheInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */