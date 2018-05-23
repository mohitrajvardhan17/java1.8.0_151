package sun.misc;

import java.io.FileDescriptor;

public abstract interface JavaIOFileDescriptorAccess
{
  public abstract void set(FileDescriptor paramFileDescriptor, int paramInt);
  
  public abstract int get(FileDescriptor paramFileDescriptor);
  
  public abstract void setHandle(FileDescriptor paramFileDescriptor, long paramLong);
  
  public abstract long getHandle(FileDescriptor paramFileDescriptor);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JavaIOFileDescriptorAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */