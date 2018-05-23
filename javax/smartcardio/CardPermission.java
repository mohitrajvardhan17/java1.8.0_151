package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Permission;

public class CardPermission
  extends Permission
{
  private static final long serialVersionUID = 7146787880530705613L;
  private static final int A_CONNECT = 1;
  private static final int A_EXCLUSIVE = 2;
  private static final int A_GET_BASIC_CHANNEL = 4;
  private static final int A_OPEN_LOGICAL_CHANNEL = 8;
  private static final int A_RESET = 16;
  private static final int A_TRANSMIT_CONTROL = 32;
  private static final int A_ALL = 63;
  private static final int[] ARRAY_MASKS = { 63, 1, 2, 4, 8, 16, 32 };
  private static final String S_CONNECT = "connect";
  private static final String S_EXCLUSIVE = "exclusive";
  private static final String S_GET_BASIC_CHANNEL = "getBasicChannel";
  private static final String S_OPEN_LOGICAL_CHANNEL = "openLogicalChannel";
  private static final String S_RESET = "reset";
  private static final String S_TRANSMIT_CONTROL = "transmitControl";
  private static final String S_ALL = "*";
  private static final String[] ARRAY_STRINGS = { "*", "connect", "exclusive", "getBasicChannel", "openLogicalChannel", "reset", "transmitControl" };
  private transient int mask;
  private volatile String actions;
  
  public CardPermission(String paramString1, String paramString2)
  {
    super(paramString1);
    if (paramString1 == null) {
      throw new NullPointerException();
    }
    mask = getMask(paramString2);
  }
  
  private static int getMask(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("actions must not be empty");
    }
    for (int i = 0; i < ARRAY_STRINGS.length; i++) {
      if (paramString == ARRAY_STRINGS[i]) {
        return ARRAY_MASKS[i];
      }
    }
    if (paramString.endsWith(",")) {
      throw new IllegalArgumentException("Invalid actions: '" + paramString + "'");
    }
    i = 0;
    String[] arrayOfString1 = paramString.split(",");
    label201:
    for (String str : arrayOfString1)
    {
      for (int m = 0; m < ARRAY_STRINGS.length; m++) {
        if (ARRAY_STRINGS[m].equalsIgnoreCase(str))
        {
          i |= ARRAY_MASKS[m];
          break label201;
        }
      }
      throw new IllegalArgumentException("Invalid action: '" + str + "'");
    }
    return i;
  }
  
  private static String getActions(int paramInt)
  {
    if (paramInt == 63) {
      return "*";
    }
    int i = 1;
    StringBuilder localStringBuilder = new StringBuilder();
    for (int j = 0; j < ARRAY_MASKS.length; j++)
    {
      int k = ARRAY_MASKS[j];
      if ((paramInt & k) == k)
      {
        if (i == 0) {
          localStringBuilder.append(",");
        } else {
          i = 0;
        }
        localStringBuilder.append(ARRAY_STRINGS[j]);
      }
    }
    return localStringBuilder.toString();
  }
  
  public String getActions()
  {
    if (actions == null) {
      actions = getActions(mask);
    }
    return actions;
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof CardPermission)) {
      return false;
    }
    CardPermission localCardPermission = (CardPermission)paramPermission;
    if ((mask & mask) != mask) {
      return false;
    }
    String str = getName();
    if (str.equals("*")) {
      return true;
    }
    return str.equals(localCardPermission.getName());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CardPermission)) {
      return false;
    }
    CardPermission localCardPermission = (CardPermission)paramObject;
    return (getName().equals(localCardPermission.getName())) && (mask == mask);
  }
  
  public int hashCode()
  {
    return getName().hashCode() + 31 * mask;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (actions == null) {
      getActions();
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    mask = getMask(actions);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\CardPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */