package com.sun.org.apache.bcel.internal.classfile;

import java.io.Serializable;

public abstract class AccessFlags
  implements Serializable
{
  protected int access_flags;
  
  public AccessFlags() {}
  
  public AccessFlags(int paramInt)
  {
    access_flags = paramInt;
  }
  
  public final int getAccessFlags()
  {
    return access_flags;
  }
  
  public final int getModifiers()
  {
    return access_flags;
  }
  
  public final void setAccessFlags(int paramInt)
  {
    access_flags = paramInt;
  }
  
  public final void setModifiers(int paramInt)
  {
    setAccessFlags(paramInt);
  }
  
  private final void setFlag(int paramInt, boolean paramBoolean)
  {
    if ((access_flags & paramInt) != 0)
    {
      if (!paramBoolean) {
        access_flags ^= paramInt;
      }
    }
    else if (paramBoolean) {
      access_flags |= paramInt;
    }
  }
  
  public final void isPublic(boolean paramBoolean)
  {
    setFlag(1, paramBoolean);
  }
  
  public final boolean isPublic()
  {
    return (access_flags & 0x1) != 0;
  }
  
  public final void isPrivate(boolean paramBoolean)
  {
    setFlag(2, paramBoolean);
  }
  
  public final boolean isPrivate()
  {
    return (access_flags & 0x2) != 0;
  }
  
  public final void isProtected(boolean paramBoolean)
  {
    setFlag(4, paramBoolean);
  }
  
  public final boolean isProtected()
  {
    return (access_flags & 0x4) != 0;
  }
  
  public final void isStatic(boolean paramBoolean)
  {
    setFlag(8, paramBoolean);
  }
  
  public final boolean isStatic()
  {
    return (access_flags & 0x8) != 0;
  }
  
  public final void isFinal(boolean paramBoolean)
  {
    setFlag(16, paramBoolean);
  }
  
  public final boolean isFinal()
  {
    return (access_flags & 0x10) != 0;
  }
  
  public final void isSynchronized(boolean paramBoolean)
  {
    setFlag(32, paramBoolean);
  }
  
  public final boolean isSynchronized()
  {
    return (access_flags & 0x20) != 0;
  }
  
  public final void isVolatile(boolean paramBoolean)
  {
    setFlag(64, paramBoolean);
  }
  
  public final boolean isVolatile()
  {
    return (access_flags & 0x40) != 0;
  }
  
  public final void isTransient(boolean paramBoolean)
  {
    setFlag(128, paramBoolean);
  }
  
  public final boolean isTransient()
  {
    return (access_flags & 0x80) != 0;
  }
  
  public final void isNative(boolean paramBoolean)
  {
    setFlag(256, paramBoolean);
  }
  
  public final boolean isNative()
  {
    return (access_flags & 0x100) != 0;
  }
  
  public final void isInterface(boolean paramBoolean)
  {
    setFlag(512, paramBoolean);
  }
  
  public final boolean isInterface()
  {
    return (access_flags & 0x200) != 0;
  }
  
  public final void isAbstract(boolean paramBoolean)
  {
    setFlag(1024, paramBoolean);
  }
  
  public final boolean isAbstract()
  {
    return (access_flags & 0x400) != 0;
  }
  
  public final void isStrictfp(boolean paramBoolean)
  {
    setFlag(2048, paramBoolean);
  }
  
  public final boolean isStrictfp()
  {
    return (access_flags & 0x800) != 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\AccessFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */