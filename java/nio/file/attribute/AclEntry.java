package java.nio.file.attribute;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class AclEntry
{
  private final AclEntryType type;
  private final UserPrincipal who;
  private final Set<AclEntryPermission> perms;
  private final Set<AclEntryFlag> flags;
  private volatile int hash;
  
  private AclEntry(AclEntryType paramAclEntryType, UserPrincipal paramUserPrincipal, Set<AclEntryPermission> paramSet, Set<AclEntryFlag> paramSet1)
  {
    type = paramAclEntryType;
    who = paramUserPrincipal;
    perms = paramSet;
    flags = paramSet1;
  }
  
  public static Builder newBuilder()
  {
    Set localSet1 = Collections.emptySet();
    Set localSet2 = Collections.emptySet();
    return new Builder(null, null, localSet1, localSet2, null);
  }
  
  public static Builder newBuilder(AclEntry paramAclEntry)
  {
    return new Builder(type, who, perms, flags, null);
  }
  
  public AclEntryType type()
  {
    return type;
  }
  
  public UserPrincipal principal()
  {
    return who;
  }
  
  public Set<AclEntryPermission> permissions()
  {
    return new HashSet(perms);
  }
  
  public Set<AclEntryFlag> flags()
  {
    return new HashSet(flags);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof AclEntry))) {
      return false;
    }
    AclEntry localAclEntry = (AclEntry)paramObject;
    if (type != type) {
      return false;
    }
    if (!who.equals(who)) {
      return false;
    }
    if (!perms.equals(perms)) {
      return false;
    }
    return flags.equals(flags);
  }
  
  private static int hash(int paramInt, Object paramObject)
  {
    return paramInt * 127 + paramObject.hashCode();
  }
  
  public int hashCode()
  {
    if (hash != 0) {
      return hash;
    }
    int i = type.hashCode();
    i = hash(i, who);
    i = hash(i, perms);
    i = hash(i, flags);
    hash = i;
    return hash;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(who.getName());
    localStringBuilder.append(':');
    Iterator localIterator = perms.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (AclEntryPermission)localIterator.next();
      localStringBuilder.append(((AclEntryPermission)localObject).name());
      localStringBuilder.append('/');
    }
    localStringBuilder.setLength(localStringBuilder.length() - 1);
    localStringBuilder.append(':');
    if (!flags.isEmpty())
    {
      localIterator = flags.iterator();
      while (localIterator.hasNext())
      {
        localObject = (AclEntryFlag)localIterator.next();
        localStringBuilder.append(((AclEntryFlag)localObject).name());
        localStringBuilder.append('/');
      }
      localStringBuilder.setLength(localStringBuilder.length() - 1);
      localStringBuilder.append(':');
    }
    localStringBuilder.append(type.name());
    return localStringBuilder.toString();
  }
  
  public static final class Builder
  {
    private AclEntryType type;
    private UserPrincipal who;
    private Set<AclEntryPermission> perms;
    private Set<AclEntryFlag> flags;
    
    private Builder(AclEntryType paramAclEntryType, UserPrincipal paramUserPrincipal, Set<AclEntryPermission> paramSet, Set<AclEntryFlag> paramSet1)
    {
      assert ((paramSet != null) && (paramSet1 != null));
      type = paramAclEntryType;
      who = paramUserPrincipal;
      perms = paramSet;
      flags = paramSet1;
    }
    
    public AclEntry build()
    {
      if (type == null) {
        throw new IllegalStateException("Missing type component");
      }
      if (who == null) {
        throw new IllegalStateException("Missing who component");
      }
      return new AclEntry(type, who, perms, flags, null);
    }
    
    public Builder setType(AclEntryType paramAclEntryType)
    {
      if (paramAclEntryType == null) {
        throw new NullPointerException();
      }
      type = paramAclEntryType;
      return this;
    }
    
    public Builder setPrincipal(UserPrincipal paramUserPrincipal)
    {
      if (paramUserPrincipal == null) {
        throw new NullPointerException();
      }
      who = paramUserPrincipal;
      return this;
    }
    
    private static void checkSet(Set<?> paramSet, Class<?> paramClass)
    {
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (localObject == null) {
          throw new NullPointerException();
        }
        paramClass.cast(localObject);
      }
    }
    
    public Builder setPermissions(Set<AclEntryPermission> paramSet)
    {
      if (paramSet.isEmpty())
      {
        paramSet = Collections.emptySet();
      }
      else
      {
        paramSet = EnumSet.copyOf(paramSet);
        checkSet(paramSet, AclEntryPermission.class);
      }
      perms = paramSet;
      return this;
    }
    
    public Builder setPermissions(AclEntryPermission... paramVarArgs)
    {
      EnumSet localEnumSet = EnumSet.noneOf(AclEntryPermission.class);
      for (AclEntryPermission localAclEntryPermission : paramVarArgs)
      {
        if (localAclEntryPermission == null) {
          throw new NullPointerException();
        }
        localEnumSet.add(localAclEntryPermission);
      }
      perms = localEnumSet;
      return this;
    }
    
    public Builder setFlags(Set<AclEntryFlag> paramSet)
    {
      if (paramSet.isEmpty())
      {
        paramSet = Collections.emptySet();
      }
      else
      {
        paramSet = EnumSet.copyOf(paramSet);
        checkSet(paramSet, AclEntryFlag.class);
      }
      flags = paramSet;
      return this;
    }
    
    public Builder setFlags(AclEntryFlag... paramVarArgs)
    {
      EnumSet localEnumSet = EnumSet.noneOf(AclEntryFlag.class);
      for (AclEntryFlag localAclEntryFlag : paramVarArgs)
      {
        if (localAclEntryFlag == null) {
          throw new NullPointerException();
        }
        localEnumSet.add(localAclEntryFlag);
      }
      flags = localEnumSet;
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\AclEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */