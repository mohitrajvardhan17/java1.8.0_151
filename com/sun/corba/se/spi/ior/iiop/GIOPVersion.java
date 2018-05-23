package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class GIOPVersion
{
  public static final GIOPVersion V1_0 = new GIOPVersion((byte)1, (byte)0);
  public static final GIOPVersion V1_1 = new GIOPVersion((byte)1, (byte)1);
  public static final GIOPVersion V1_2 = new GIOPVersion((byte)1, (byte)2);
  public static final GIOPVersion V1_3 = new GIOPVersion((byte)1, (byte)3);
  public static final GIOPVersion V13_XX = new GIOPVersion((byte)13, (byte)1);
  public static final GIOPVersion DEFAULT_VERSION = V1_2;
  public static final int VERSION_1_0 = 256;
  public static final int VERSION_1_1 = 257;
  public static final int VERSION_1_2 = 258;
  public static final int VERSION_1_3 = 259;
  public static final int VERSION_13_XX = 3329;
  private byte major = 0;
  private byte minor = 0;
  
  public GIOPVersion() {}
  
  public GIOPVersion(byte paramByte1, byte paramByte2)
  {
    major = paramByte1;
    minor = paramByte2;
  }
  
  public GIOPVersion(int paramInt1, int paramInt2)
  {
    major = ((byte)paramInt1);
    minor = ((byte)paramInt2);
  }
  
  public byte getMajor()
  {
    return major;
  }
  
  public byte getMinor()
  {
    return minor;
  }
  
  public boolean equals(GIOPVersion paramGIOPVersion)
  {
    return (major == major) && (minor == minor);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof GIOPVersion))) {
      return equals((GIOPVersion)paramObject);
    }
    return false;
  }
  
  public int hashCode()
  {
    return 37 * major + minor;
  }
  
  public boolean lessThan(GIOPVersion paramGIOPVersion)
  {
    if (major < major) {
      return true;
    }
    return (major == major) && (minor < minor);
  }
  
  public int intValue()
  {
    return major << 8 | minor;
  }
  
  public String toString()
  {
    return major + "." + minor;
  }
  
  public static GIOPVersion getInstance(byte paramByte1, byte paramByte2)
  {
    switch (paramByte1 << 8 | paramByte2)
    {
    case 256: 
      return V1_0;
    case 257: 
      return V1_1;
    case 258: 
      return V1_2;
    case 259: 
      return V1_3;
    case 3329: 
      return V13_XX;
    }
    return new GIOPVersion(paramByte1, paramByte2);
  }
  
  public static GIOPVersion parseVersion(String paramString)
  {
    int i = paramString.indexOf('.');
    if ((i < 1) || (i == paramString.length() - 1)) {
      throw new NumberFormatException("GIOP major, minor, and decimal point required: " + paramString);
    }
    int j = Integer.parseInt(paramString.substring(0, i));
    int k = Integer.parseInt(paramString.substring(i + 1, paramString.length()));
    return getInstance((byte)j, (byte)k);
  }
  
  public static GIOPVersion chooseRequestVersion(ORB paramORB, IOR paramIOR)
  {
    GIOPVersion localGIOPVersion1 = paramORB.getORBData().getGIOPVersion();
    IIOPProfile localIIOPProfile = paramIOR.getProfile();
    GIOPVersion localGIOPVersion2 = localIIOPProfile.getGIOPVersion();
    ORBVersion localORBVersion = localIIOPProfile.getORBVersion();
    if ((!localORBVersion.equals(ORBVersionFactory.getFOREIGN())) && (localORBVersion.lessThan(ORBVersionFactory.getNEWER()))) {
      return V1_0;
    }
    int i = localGIOPVersion2.getMajor();
    int j = localGIOPVersion2.getMinor();
    int k = localGIOPVersion1.getMajor();
    int m = localGIOPVersion1.getMinor();
    if (k < i) {
      return localGIOPVersion1;
    }
    if (k > i) {
      return localGIOPVersion2;
    }
    if (m <= j) {
      return localGIOPVersion1;
    }
    return localGIOPVersion2;
  }
  
  public boolean supportsIORIIOPProfileComponents()
  {
    return (getMinor() > 0) || (getMajor() > 1);
  }
  
  public void read(InputStream paramInputStream)
  {
    major = paramInputStream.read_octet();
    minor = paramInputStream.read_octet();
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_octet(major);
    paramOutputStream.write_octet(minor);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\iiop\GIOPVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */