package sun.security.krb5.internal.ccache;

import java.io.ByteArrayOutputStream;

public class Tag
{
  int length;
  int tag;
  int tagLen;
  Integer time_offset;
  Integer usec_offset;
  
  public Tag(int paramInt1, int paramInt2, Integer paramInteger1, Integer paramInteger2)
  {
    tag = paramInt2;
    tagLen = 8;
    time_offset = paramInteger1;
    usec_offset = paramInteger2;
    length = (4 + tagLen);
  }
  
  public Tag(int paramInt)
  {
    tag = paramInt;
    tagLen = 0;
    length = (4 + tagLen);
  }
  
  public byte[] toByteArray()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localByteArrayOutputStream.write(length);
    localByteArrayOutputStream.write(tag);
    localByteArrayOutputStream.write(tagLen);
    if (time_offset != null) {
      localByteArrayOutputStream.write(time_offset.intValue());
    }
    if (usec_offset != null) {
      localByteArrayOutputStream.write(usec_offset.intValue());
    }
    return localByteArrayOutputStream.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ccache\Tag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */