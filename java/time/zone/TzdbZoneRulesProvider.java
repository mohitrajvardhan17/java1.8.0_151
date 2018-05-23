package java.time.zone;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

final class TzdbZoneRulesProvider
  extends ZoneRulesProvider
{
  private List<String> regionIds;
  private String versionId;
  private final Map<String, Object> regionToRules = new ConcurrentHashMap();
  
  public TzdbZoneRulesProvider()
  {
    try
    {
      String str = System.getProperty("java.home") + File.separator + "lib";
      DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(str, "tzdb.dat"))));
      Object localObject1 = null;
      try
      {
        load(localDataInputStream);
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localDataInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localDataInputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localDataInputStream.close();
          }
        }
      }
    }
    catch (Exception localException)
    {
      throw new ZoneRulesException("Unable to load TZDB time-zone rules", localException);
    }
  }
  
  protected Set<String> provideZoneIds()
  {
    return new HashSet(regionIds);
  }
  
  protected ZoneRules provideRules(String paramString, boolean paramBoolean)
  {
    Object localObject = regionToRules.get(paramString);
    if (localObject == null) {
      throw new ZoneRulesException("Unknown time-zone ID: " + paramString);
    }
    try
    {
      if ((localObject instanceof byte[]))
      {
        byte[] arrayOfByte = (byte[])localObject;
        DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
        localObject = Ser.read(localDataInputStream);
        regionToRules.put(paramString, localObject);
      }
      return (ZoneRules)localObject;
    }
    catch (Exception localException)
    {
      throw new ZoneRulesException("Invalid binary time-zone data: TZDB:" + paramString + ", version: " + versionId, localException);
    }
  }
  
  protected NavigableMap<String, ZoneRules> provideVersions(String paramString)
  {
    TreeMap localTreeMap = new TreeMap();
    ZoneRules localZoneRules = getRules(paramString, false);
    if (localZoneRules != null) {
      localTreeMap.put(versionId, localZoneRules);
    }
    return localTreeMap;
  }
  
  private void load(DataInputStream paramDataInputStream)
    throws Exception
  {
    if (paramDataInputStream.readByte() != 1) {
      throw new StreamCorruptedException("File format not recognised");
    }
    String str1 = paramDataInputStream.readUTF();
    if (!"TZDB".equals(str1)) {
      throw new StreamCorruptedException("File format not recognised");
    }
    int i = paramDataInputStream.readShort();
    for (int j = 0; j < i; j++) {
      versionId = paramDataInputStream.readUTF();
    }
    j = paramDataInputStream.readShort();
    String[] arrayOfString = new String[j];
    for (int k = 0; k < j; k++) {
      arrayOfString[k] = paramDataInputStream.readUTF();
    }
    regionIds = Arrays.asList(arrayOfString);
    k = paramDataInputStream.readShort();
    Object[] arrayOfObject = new Object[k];
    for (int m = 0; m < k; m++)
    {
      byte[] arrayOfByte = new byte[paramDataInputStream.readShort()];
      paramDataInputStream.readFully(arrayOfByte);
      arrayOfObject[m] = arrayOfByte;
    }
    for (m = 0; m < i; m++)
    {
      int n = paramDataInputStream.readShort();
      regionToRules.clear();
      for (int i1 = 0; i1 < n; i1++)
      {
        String str2 = arrayOfString[paramDataInputStream.readShort()];
        Object localObject = arrayOfObject[(paramDataInputStream.readShort() & 0xFFFF)];
        regionToRules.put(str2, localObject);
      }
    }
  }
  
  public String toString()
  {
    return "TZDB[" + versionId + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\zone\TzdbZoneRulesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */