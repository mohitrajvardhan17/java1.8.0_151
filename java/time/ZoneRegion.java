package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Objects;

final class ZoneRegion
  extends ZoneId
  implements Serializable
{
  private static final long serialVersionUID = 8386373296231747096L;
  private final String id;
  private final transient ZoneRules rules;
  
  static ZoneRegion ofId(String paramString, boolean paramBoolean)
  {
    Objects.requireNonNull(paramString, "zoneId");
    checkName(paramString);
    ZoneRules localZoneRules = null;
    try
    {
      localZoneRules = ZoneRulesProvider.getRules(paramString, true);
    }
    catch (ZoneRulesException localZoneRulesException)
    {
      if (paramBoolean) {
        throw localZoneRulesException;
      }
    }
    return new ZoneRegion(paramString, localZoneRules);
  }
  
  private static void checkName(String paramString)
  {
    int i = paramString.length();
    if (i < 2) {
      throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + paramString);
    }
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if (((k < 97) || (k > 122)) && ((k < 65) || (k > 90)) && ((k != 47) || (j == 0)) && ((k < 48) || (k > 57) || (j == 0)) && ((k != 126) || (j == 0)) && ((k != 46) || (j == 0)) && ((k != 95) || (j == 0)) && ((k != 43) || (j == 0)) && ((k != 45) || (j == 0))) {
        throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + paramString);
      }
    }
  }
  
  ZoneRegion(String paramString, ZoneRules paramZoneRules)
  {
    id = paramString;
    rules = paramZoneRules;
  }
  
  public String getId()
  {
    return id;
  }
  
  public ZoneRules getRules()
  {
    return rules != null ? rules : ZoneRulesProvider.getRules(id, false);
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)7, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  void write(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeByte(7);
    writeExternal(paramDataOutput);
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeUTF(id);
  }
  
  static ZoneId readExternal(DataInput paramDataInput)
    throws IOException
  {
    String str = paramDataInput.readUTF();
    return ZoneId.of(str, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\ZoneRegion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */