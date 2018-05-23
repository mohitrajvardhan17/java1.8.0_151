package sun.security.krb5.internal.rcache;

import java.util.Objects;

public class AuthTimeWithHash
  extends AuthTime
  implements Comparable<AuthTimeWithHash>
{
  final String hash;
  
  public AuthTimeWithHash(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3)
  {
    super(paramString1, paramString2, paramInt1, paramInt2);
    hash = paramString3;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof AuthTimeWithHash)) {
      return false;
    }
    AuthTimeWithHash localAuthTimeWithHash = (AuthTimeWithHash)paramObject;
    return (Objects.equals(hash, hash)) && (Objects.equals(client, client)) && (Objects.equals(server, server)) && (ctime == ctime) && (cusec == cusec);
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { hash });
  }
  
  public String toString()
  {
    return String.format("%d/%06d/%s/%s", new Object[] { Integer.valueOf(ctime), Integer.valueOf(cusec), hash, client });
  }
  
  public int compareTo(AuthTimeWithHash paramAuthTimeWithHash)
  {
    int i = 0;
    if (ctime != ctime) {
      i = Integer.compare(ctime, ctime);
    } else if (cusec != cusec) {
      i = Integer.compare(cusec, cusec);
    } else {
      i = hash.compareTo(hash);
    }
    return i;
  }
  
  public boolean isSameIgnoresHash(AuthTime paramAuthTime)
  {
    return (client.equals(client)) && (server.equals(server)) && (ctime == ctime) && (cusec == cusec);
  }
  
  public byte[] encode(boolean paramBoolean)
  {
    String str1;
    String str2;
    if (paramBoolean)
    {
      str1 = "";
      str2 = String.format("HASH:%s %d:%s %d:%s", new Object[] { hash, Integer.valueOf(client.length()), client, Integer.valueOf(server.length()), server });
    }
    else
    {
      str1 = client;
      str2 = server;
    }
    return encode0(str1, str2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\rcache\AuthTimeWithHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */