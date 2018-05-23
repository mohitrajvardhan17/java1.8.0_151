package java.lang.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class InfoFromMemberName
  implements MethodHandleInfo
{
  private final MemberName member;
  private final int referenceKind;
  
  InfoFromMemberName(MethodHandles.Lookup paramLookup, MemberName paramMemberName, byte paramByte)
  {
    assert ((paramMemberName.isResolved()) || (paramMemberName.isMethodHandleInvoke()));
    assert (paramMemberName.referenceKindIsConsistentWith(paramByte));
    member = paramMemberName;
    referenceKind = paramByte;
  }
  
  public Class<?> getDeclaringClass()
  {
    return member.getDeclaringClass();
  }
  
  public String getName()
  {
    return member.getName();
  }
  
  public MethodType getMethodType()
  {
    return member.getMethodOrFieldType();
  }
  
  public int getModifiers()
  {
    return member.getModifiers();
  }
  
  public int getReferenceKind()
  {
    return referenceKind;
  }
  
  public String toString()
  {
    return MethodHandleInfo.toString(getReferenceKind(), getDeclaringClass(), getName(), getMethodType());
  }
  
  public <T extends Member> T reflectAs(Class<T> paramClass, MethodHandles.Lookup paramLookup)
  {
    if ((member.isMethodHandleInvoke()) && (!member.isVarargs())) {
      throw new IllegalArgumentException("cannot reflect signature polymorphic method");
    }
    Member localMember = (Member)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Member run()
      {
        try
        {
          return InfoFromMemberName.this.reflectUnchecked();
        }
        catch (ReflectiveOperationException localReflectiveOperationException)
        {
          throw new IllegalArgumentException(localReflectiveOperationException);
        }
      }
    });
    try
    {
      Class localClass = getDeclaringClass();
      byte b = (byte)getReferenceKind();
      paramLookup.checkAccess(b, localClass, convertToMemberName(b, localMember));
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalArgumentException(localIllegalAccessException);
    }
    return (Member)paramClass.cast(localMember);
  }
  
  private Member reflectUnchecked()
    throws ReflectiveOperationException
  {
    byte b = (byte)getReferenceKind();
    Class localClass = getDeclaringClass();
    boolean bool = Modifier.isPublic(getModifiers());
    if (MethodHandleNatives.refKindIsMethod(b))
    {
      if (bool) {
        return localClass.getMethod(getName(), getMethodType().parameterArray());
      }
      return localClass.getDeclaredMethod(getName(), getMethodType().parameterArray());
    }
    if (MethodHandleNatives.refKindIsConstructor(b))
    {
      if (bool) {
        return localClass.getConstructor(getMethodType().parameterArray());
      }
      return localClass.getDeclaredConstructor(getMethodType().parameterArray());
    }
    if (MethodHandleNatives.refKindIsField(b))
    {
      if (bool) {
        return localClass.getField(getName());
      }
      return localClass.getDeclaredField(getName());
    }
    throw new IllegalArgumentException("referenceKind=" + b);
  }
  
  private static MemberName convertToMemberName(byte paramByte, Member paramMember)
    throws IllegalAccessException
  {
    boolean bool;
    if ((paramMember instanceof Method))
    {
      bool = paramByte == 7;
      return new MemberName((Method)paramMember, bool);
    }
    if ((paramMember instanceof Constructor)) {
      return new MemberName((Constructor)paramMember);
    }
    if ((paramMember instanceof Field))
    {
      bool = (paramByte == 3) || (paramByte == 4);
      return new MemberName((Field)paramMember, bool);
    }
    throw new InternalError(paramMember.getClass().getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\InfoFromMemberName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */