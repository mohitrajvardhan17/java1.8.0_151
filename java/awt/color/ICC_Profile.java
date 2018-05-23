package java.awt.color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileActivator;
import sun.java2d.cmm.ProfileDataVerifier;
import sun.java2d.cmm.ProfileDeferralInfo;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ICC_Profile
  implements Serializable
{
  private static final long serialVersionUID = -3938515861990936766L;
  private transient Profile cmmProfile;
  private transient ProfileDeferralInfo deferralInfo;
  private transient ProfileActivator profileActivator;
  private static ICC_Profile sRGBprofile;
  private static ICC_Profile XYZprofile;
  private static ICC_Profile PYCCprofile;
  private static ICC_Profile GRAYprofile;
  private static ICC_Profile LINEAR_RGBprofile;
  public static final int CLASS_INPUT = 0;
  public static final int CLASS_DISPLAY = 1;
  public static final int CLASS_OUTPUT = 2;
  public static final int CLASS_DEVICELINK = 3;
  public static final int CLASS_COLORSPACECONVERSION = 4;
  public static final int CLASS_ABSTRACT = 5;
  public static final int CLASS_NAMEDCOLOR = 6;
  public static final int icSigXYZData = 1482250784;
  public static final int icSigLabData = 1281450528;
  public static final int icSigLuvData = 1282766368;
  public static final int icSigYCbCrData = 1497588338;
  public static final int icSigYxyData = 1501067552;
  public static final int icSigRgbData = 1380401696;
  public static final int icSigGrayData = 1196573017;
  public static final int icSigHsvData = 1213421088;
  public static final int icSigHlsData = 1212961568;
  public static final int icSigCmykData = 1129142603;
  public static final int icSigCmyData = 1129142560;
  public static final int icSigSpace2CLR = 843271250;
  public static final int icSigSpace3CLR = 860048466;
  public static final int icSigSpace4CLR = 876825682;
  public static final int icSigSpace5CLR = 893602898;
  public static final int icSigSpace6CLR = 910380114;
  public static final int icSigSpace7CLR = 927157330;
  public static final int icSigSpace8CLR = 943934546;
  public static final int icSigSpace9CLR = 960711762;
  public static final int icSigSpaceACLR = 1094929490;
  public static final int icSigSpaceBCLR = 1111706706;
  public static final int icSigSpaceCCLR = 1128483922;
  public static final int icSigSpaceDCLR = 1145261138;
  public static final int icSigSpaceECLR = 1162038354;
  public static final int icSigSpaceFCLR = 1178815570;
  public static final int icSigInputClass = 1935896178;
  public static final int icSigDisplayClass = 1835955314;
  public static final int icSigOutputClass = 1886549106;
  public static final int icSigLinkClass = 1818848875;
  public static final int icSigAbstractClass = 1633842036;
  public static final int icSigColorSpaceClass = 1936744803;
  public static final int icSigNamedColorClass = 1852662636;
  public static final int icPerceptual = 0;
  public static final int icRelativeColorimetric = 1;
  public static final int icMediaRelativeColorimetric = 1;
  public static final int icSaturation = 2;
  public static final int icAbsoluteColorimetric = 3;
  public static final int icICCAbsoluteColorimetric = 3;
  public static final int icSigHead = 1751474532;
  public static final int icSigAToB0Tag = 1093812784;
  public static final int icSigAToB1Tag = 1093812785;
  public static final int icSigAToB2Tag = 1093812786;
  public static final int icSigBlueColorantTag = 1649957210;
  public static final int icSigBlueMatrixColumnTag = 1649957210;
  public static final int icSigBlueTRCTag = 1649693251;
  public static final int icSigBToA0Tag = 1110589744;
  public static final int icSigBToA1Tag = 1110589745;
  public static final int icSigBToA2Tag = 1110589746;
  public static final int icSigCalibrationDateTimeTag = 1667329140;
  public static final int icSigCharTargetTag = 1952543335;
  public static final int icSigCopyrightTag = 1668313716;
  public static final int icSigCrdInfoTag = 1668441193;
  public static final int icSigDeviceMfgDescTag = 1684893284;
  public static final int icSigDeviceModelDescTag = 1684890724;
  public static final int icSigDeviceSettingsTag = 1684371059;
  public static final int icSigGamutTag = 1734438260;
  public static final int icSigGrayTRCTag = 1800688195;
  public static final int icSigGreenColorantTag = 1733843290;
  public static final int icSigGreenMatrixColumnTag = 1733843290;
  public static final int icSigGreenTRCTag = 1733579331;
  public static final int icSigLuminanceTag = 1819635049;
  public static final int icSigMeasurementTag = 1835360627;
  public static final int icSigMediaBlackPointTag = 1651208308;
  public static final int icSigMediaWhitePointTag = 2004119668;
  public static final int icSigNamedColor2Tag = 1852009522;
  public static final int icSigOutputResponseTag = 1919251312;
  public static final int icSigPreview0Tag = 1886545200;
  public static final int icSigPreview1Tag = 1886545201;
  public static final int icSigPreview2Tag = 1886545202;
  public static final int icSigProfileDescriptionTag = 1684370275;
  public static final int icSigProfileSequenceDescTag = 1886610801;
  public static final int icSigPs2CRD0Tag = 1886610480;
  public static final int icSigPs2CRD1Tag = 1886610481;
  public static final int icSigPs2CRD2Tag = 1886610482;
  public static final int icSigPs2CRD3Tag = 1886610483;
  public static final int icSigPs2CSATag = 1886597747;
  public static final int icSigPs2RenderingIntentTag = 1886597737;
  public static final int icSigRedColorantTag = 1918392666;
  public static final int icSigRedMatrixColumnTag = 1918392666;
  public static final int icSigRedTRCTag = 1918128707;
  public static final int icSigScreeningDescTag = 1935897188;
  public static final int icSigScreeningTag = 1935897198;
  public static final int icSigTechnologyTag = 1952801640;
  public static final int icSigUcrBgTag = 1650877472;
  public static final int icSigViewingCondDescTag = 1987405156;
  public static final int icSigViewingConditionsTag = 1986618743;
  public static final int icSigChromaticityTag = 1667789421;
  public static final int icSigChromaticAdaptationTag = 1667785060;
  public static final int icSigColorantOrderTag = 1668051567;
  public static final int icSigColorantTableTag = 1668051572;
  public static final int icHdrSize = 0;
  public static final int icHdrCmmId = 4;
  public static final int icHdrVersion = 8;
  public static final int icHdrDeviceClass = 12;
  public static final int icHdrColorSpace = 16;
  public static final int icHdrPcs = 20;
  public static final int icHdrDate = 24;
  public static final int icHdrMagic = 36;
  public static final int icHdrPlatform = 40;
  public static final int icHdrFlags = 44;
  public static final int icHdrManufacturer = 48;
  public static final int icHdrModel = 52;
  public static final int icHdrAttributes = 56;
  public static final int icHdrRenderingIntent = 64;
  public static final int icHdrIlluminant = 68;
  public static final int icHdrCreator = 80;
  public static final int icHdrProfileID = 84;
  public static final int icTagType = 0;
  public static final int icTagReserved = 4;
  public static final int icCurveCount = 8;
  public static final int icCurveData = 12;
  public static final int icXYZNumberX = 8;
  private int iccProfileSerializedDataVersion = 1;
  private transient ICC_Profile resolvedDeserializedProfile;
  
  ICC_Profile(Profile paramProfile)
  {
    cmmProfile = paramProfile;
  }
  
  ICC_Profile(ProfileDeferralInfo paramProfileDeferralInfo)
  {
    deferralInfo = paramProfileDeferralInfo;
    profileActivator = new ProfileActivator()
    {
      public void activate()
        throws ProfileDataException
      {
        activateDeferredProfile();
      }
    };
    ProfileDeferralMgr.registerDeferral(profileActivator);
  }
  
  protected void finalize()
  {
    if (cmmProfile != null) {
      CMSManager.getModule().freeProfile(cmmProfile);
    } else if (profileActivator != null) {
      ProfileDeferralMgr.unregisterDeferral(profileActivator);
    }
  }
  
  public static ICC_Profile getInstance(byte[] paramArrayOfByte)
  {
    Profile localProfile = null;
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
    ProfileDataVerifier.verify(paramArrayOfByte);
    try
    {
      localProfile = CMSManager.getModule().loadProfile(paramArrayOfByte);
    }
    catch (CMMException localCMMException1)
    {
      throw new IllegalArgumentException("Invalid ICC Profile Data");
    }
    Object localObject;
    try
    {
      if ((getColorSpaceType(localProfile) == 6) && (getData(localProfile, 2004119668) != null) && (getData(localProfile, 1800688195) != null)) {
        localObject = new ICC_ProfileGray(localProfile);
      } else if ((getColorSpaceType(localProfile) == 5) && (getData(localProfile, 2004119668) != null) && (getData(localProfile, 1918392666) != null) && (getData(localProfile, 1733843290) != null) && (getData(localProfile, 1649957210) != null) && (getData(localProfile, 1918128707) != null) && (getData(localProfile, 1733579331) != null) && (getData(localProfile, 1649693251) != null)) {
        localObject = new ICC_ProfileRGB(localProfile);
      } else {
        localObject = new ICC_Profile(localProfile);
      }
    }
    catch (CMMException localCMMException2)
    {
      localObject = new ICC_Profile(localProfile);
    }
    return (ICC_Profile)localObject;
  }
  
  public static ICC_Profile getInstance(int paramInt)
  {
    ICC_Profile localICC_Profile = null;
    ProfileDeferralInfo localProfileDeferralInfo;
    switch (paramInt)
    {
    case 1000: 
      synchronized (ICC_Profile.class)
      {
        if (sRGBprofile == null)
        {
          localProfileDeferralInfo = new ProfileDeferralInfo("sRGB.pf", 5, 3, 1);
          sRGBprofile = getDeferredInstance(localProfileDeferralInfo);
        }
        localICC_Profile = sRGBprofile;
      }
      break;
    case 1001: 
      synchronized (ICC_Profile.class)
      {
        if (XYZprofile == null)
        {
          localProfileDeferralInfo = new ProfileDeferralInfo("CIEXYZ.pf", 0, 3, 1);
          XYZprofile = getDeferredInstance(localProfileDeferralInfo);
        }
        localICC_Profile = XYZprofile;
      }
      break;
    case 1002: 
      synchronized (ICC_Profile.class)
      {
        if (PYCCprofile == null) {
          if (standardProfileExists("PYCC.pf"))
          {
            localProfileDeferralInfo = new ProfileDeferralInfo("PYCC.pf", 13, 3, 1);
            PYCCprofile = getDeferredInstance(localProfileDeferralInfo);
          }
          else
          {
            throw new IllegalArgumentException("Can't load standard profile: PYCC.pf");
          }
        }
        localICC_Profile = PYCCprofile;
      }
      break;
    case 1003: 
      synchronized (ICC_Profile.class)
      {
        if (GRAYprofile == null)
        {
          localProfileDeferralInfo = new ProfileDeferralInfo("GRAY.pf", 6, 1, 1);
          GRAYprofile = getDeferredInstance(localProfileDeferralInfo);
        }
        localICC_Profile = GRAYprofile;
      }
      break;
    case 1004: 
      synchronized (ICC_Profile.class)
      {
        if (LINEAR_RGBprofile == null)
        {
          localProfileDeferralInfo = new ProfileDeferralInfo("LINEAR_RGB.pf", 5, 3, 1);
          LINEAR_RGBprofile = getDeferredInstance(localProfileDeferralInfo);
        }
        localICC_Profile = LINEAR_RGBprofile;
      }
      break;
    default: 
      throw new IllegalArgumentException("Unknown color space");
    }
    return localICC_Profile;
  }
  
  private static ICC_Profile getStandardProfile(String paramString)
  {
    (ICC_Profile)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ICC_Profile run()
      {
        ICC_Profile localICC_Profile = null;
        try
        {
          localICC_Profile = ICC_Profile.getInstance(val$name);
        }
        catch (IOException localIOException)
        {
          throw new IllegalArgumentException("Can't load standard profile: " + val$name);
        }
        return localICC_Profile;
      }
    });
  }
  
  public static ICC_Profile getInstance(String paramString)
    throws IOException
  {
    FileInputStream localFileInputStream = null;
    File localFile = getProfileFile(paramString);
    if (localFile != null) {
      localFileInputStream = new FileInputStream(localFile);
    }
    if (localFileInputStream == null) {
      throw new IOException("Cannot open file " + paramString);
    }
    ICC_Profile localICC_Profile = getInstance(localFileInputStream);
    localFileInputStream.close();
    return localICC_Profile;
  }
  
  public static ICC_Profile getInstance(InputStream paramInputStream)
    throws IOException
  {
    if ((paramInputStream instanceof ProfileDeferralInfo)) {
      return getDeferredInstance((ProfileDeferralInfo)paramInputStream);
    }
    byte[] arrayOfByte;
    if ((arrayOfByte = getProfileDataFromStream(paramInputStream)) == null) {
      throw new IllegalArgumentException("Invalid ICC Profile Data");
    }
    return getInstance(arrayOfByte);
  }
  
  static byte[] getProfileDataFromStream(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte2 = new byte[''];
    int j = 128;
    int k = 0;
    int m;
    while (j != 0)
    {
      if ((m = paramInputStream.read(arrayOfByte2, k, j)) < 0) {
        return null;
      }
      k += m;
      j -= m;
    }
    if ((arrayOfByte2[36] != 97) || (arrayOfByte2[37] != 99) || (arrayOfByte2[38] != 115) || (arrayOfByte2[39] != 112)) {
      return null;
    }
    int i = (arrayOfByte2[0] & 0xFF) << 24 | (arrayOfByte2[1] & 0xFF) << 16 | (arrayOfByte2[2] & 0xFF) << 8 | arrayOfByte2[3] & 0xFF;
    byte[] arrayOfByte1 = new byte[i];
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, 128);
    j = i - 128;
    k = 128;
    while (j != 0)
    {
      if ((m = paramInputStream.read(arrayOfByte1, k, j)) < 0) {
        return null;
      }
      k += m;
      j -= m;
    }
    return arrayOfByte1;
  }
  
  static ICC_Profile getDeferredInstance(ProfileDeferralInfo paramProfileDeferralInfo)
  {
    if (!ProfileDeferralMgr.deferring) {
      return getStandardProfile(filename);
    }
    if (colorSpaceType == 5) {
      return new ICC_ProfileRGB(paramProfileDeferralInfo);
    }
    if (colorSpaceType == 6) {
      return new ICC_ProfileGray(paramProfileDeferralInfo);
    }
    return new ICC_Profile(paramProfileDeferralInfo);
  }
  
  void activateDeferredProfile()
    throws ProfileDataException
  {
    final String str = deferralInfo.filename;
    profileActivator = null;
    deferralInfo = null;
    PrivilegedAction local3 = new PrivilegedAction()
    {
      public FileInputStream run()
      {
        File localFile = ICC_Profile.getStandardProfileFile(str);
        if (localFile != null) {
          try
          {
            return new FileInputStream(localFile);
          }
          catch (FileNotFoundException localFileNotFoundException) {}
        }
        return null;
      }
    };
    FileInputStream localFileInputStream;
    if ((localFileInputStream = (FileInputStream)AccessController.doPrivileged(local3)) == null) {
      throw new ProfileDataException("Cannot open file " + str);
    }
    byte[] arrayOfByte;
    ProfileDataException localProfileDataException;
    try
    {
      arrayOfByte = getProfileDataFromStream(localFileInputStream);
      localFileInputStream.close();
    }
    catch (IOException localIOException)
    {
      localProfileDataException = new ProfileDataException("Invalid ICC Profile Data" + str);
      localProfileDataException.initCause(localIOException);
      throw localProfileDataException;
    }
    if (arrayOfByte == null) {
      throw new ProfileDataException("Invalid ICC Profile Data" + str);
    }
    try
    {
      cmmProfile = CMSManager.getModule().loadProfile(arrayOfByte);
    }
    catch (CMMException localCMMException)
    {
      localProfileDataException = new ProfileDataException("Invalid ICC Profile Data" + str);
      localProfileDataException.initCause(localCMMException);
      throw localProfileDataException;
    }
  }
  
  public int getMajorVersion()
  {
    byte[] arrayOfByte = getData(1751474532);
    return arrayOfByte[8];
  }
  
  public int getMinorVersion()
  {
    byte[] arrayOfByte = getData(1751474532);
    return arrayOfByte[9];
  }
  
  public int getProfileClass()
  {
    if (deferralInfo != null) {
      return deferralInfo.profileClass;
    }
    byte[] arrayOfByte = getData(1751474532);
    int i = intFromBigEndian(arrayOfByte, 12);
    int j;
    switch (i)
    {
    case 1935896178: 
      j = 0;
      break;
    case 1835955314: 
      j = 1;
      break;
    case 1886549106: 
      j = 2;
      break;
    case 1818848875: 
      j = 3;
      break;
    case 1936744803: 
      j = 4;
      break;
    case 1633842036: 
      j = 5;
      break;
    case 1852662636: 
      j = 6;
      break;
    default: 
      throw new IllegalArgumentException("Unknown profile class");
    }
    return j;
  }
  
  public int getColorSpaceType()
  {
    if (deferralInfo != null) {
      return deferralInfo.colorSpaceType;
    }
    return getColorSpaceType(cmmProfile);
  }
  
  static int getColorSpaceType(Profile paramProfile)
  {
    byte[] arrayOfByte = getData(paramProfile, 1751474532);
    int i = intFromBigEndian(arrayOfByte, 16);
    int j = iccCStoJCS(i);
    return j;
  }
  
  public int getPCSType()
  {
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
    return getPCSType(cmmProfile);
  }
  
  static int getPCSType(Profile paramProfile)
  {
    byte[] arrayOfByte = getData(paramProfile, 1751474532);
    int i = intFromBigEndian(arrayOfByte, 20);
    int j = iccCStoJCS(i);
    return j;
  }
  
  public void write(String paramString)
    throws IOException
  {
    byte[] arrayOfByte = getData();
    FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
    localFileOutputStream.write(arrayOfByte);
    localFileOutputStream.close();
  }
  
  public void write(OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = getData();
    paramOutputStream.write(arrayOfByte);
  }
  
  public byte[] getData()
  {
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
    PCMM localPCMM = CMSManager.getModule();
    int i = localPCMM.getProfileSize(cmmProfile);
    byte[] arrayOfByte = new byte[i];
    localPCMM.getProfileData(cmmProfile, arrayOfByte);
    return arrayOfByte;
  }
  
  public byte[] getData(int paramInt)
  {
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
    return getData(cmmProfile, paramInt);
  }
  
  static byte[] getData(Profile paramProfile, int paramInt)
  {
    byte[] arrayOfByte;
    try
    {
      PCMM localPCMM = CMSManager.getModule();
      int i = localPCMM.getTagSize(paramProfile, paramInt);
      arrayOfByte = new byte[i];
      localPCMM.getTagData(paramProfile, paramInt, arrayOfByte);
    }
    catch (CMMException localCMMException)
    {
      arrayOfByte = null;
    }
    return arrayOfByte;
  }
  
  public void setData(int paramInt, byte[] paramArrayOfByte)
  {
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
    CMSManager.getModule().setTagData(cmmProfile, paramInt, paramArrayOfByte);
  }
  
  void setRenderingIntent(int paramInt)
  {
    byte[] arrayOfByte = getData(1751474532);
    intToBigEndian(paramInt, arrayOfByte, 64);
    setData(1751474532, arrayOfByte);
  }
  
  int getRenderingIntent()
  {
    byte[] arrayOfByte = getData(1751474532);
    int i = intFromBigEndian(arrayOfByte, 64);
    return 0xFFFF & i;
  }
  
  public int getNumComponents()
  {
    if (deferralInfo != null) {
      return deferralInfo.numComponents;
    }
    byte[] arrayOfByte = getData(1751474532);
    int i = intFromBigEndian(arrayOfByte, 16);
    int j;
    switch (i)
    {
    case 1196573017: 
      j = 1;
      break;
    case 843271250: 
      j = 2;
      break;
    case 860048466: 
    case 1129142560: 
    case 1212961568: 
    case 1213421088: 
    case 1281450528: 
    case 1282766368: 
    case 1380401696: 
    case 1482250784: 
    case 1497588338: 
    case 1501067552: 
      j = 3;
      break;
    case 876825682: 
    case 1129142603: 
      j = 4;
      break;
    case 893602898: 
      j = 5;
      break;
    case 910380114: 
      j = 6;
      break;
    case 927157330: 
      j = 7;
      break;
    case 943934546: 
      j = 8;
      break;
    case 960711762: 
      j = 9;
      break;
    case 1094929490: 
      j = 10;
      break;
    case 1111706706: 
      j = 11;
      break;
    case 1128483922: 
      j = 12;
      break;
    case 1145261138: 
      j = 13;
      break;
    case 1162038354: 
      j = 14;
      break;
    case 1178815570: 
      j = 15;
      break;
    default: 
      throw new ProfileDataException("invalid ICC color space");
    }
    return j;
  }
  
  float[] getMediaWhitePoint()
  {
    return getXYZTag(2004119668);
  }
  
  float[] getXYZTag(int paramInt)
  {
    byte[] arrayOfByte = getData(paramInt);
    float[] arrayOfFloat = new float[3];
    int i = 0;
    for (int j = 8; i < 3; j += 4)
    {
      int k = intFromBigEndian(arrayOfByte, j);
      arrayOfFloat[i] = (k / 65536.0F);
      i++;
    }
    return arrayOfFloat;
  }
  
  float getGamma(int paramInt)
  {
    byte[] arrayOfByte = getData(paramInt);
    if (intFromBigEndian(arrayOfByte, 8) != 1) {
      throw new ProfileDataException("TRC is not a gamma");
    }
    int i = shortFromBigEndian(arrayOfByte, 12) & 0xFFFF;
    float f = i / 256.0F;
    return f;
  }
  
  short[] getTRC(int paramInt)
  {
    byte[] arrayOfByte = getData(paramInt);
    int k = intFromBigEndian(arrayOfByte, 8);
    if (k == 1) {
      throw new ProfileDataException("TRC is not a table");
    }
    short[] arrayOfShort = new short[k];
    int i = 0;
    for (int j = 12; i < k; j += 2)
    {
      arrayOfShort[i] = shortFromBigEndian(arrayOfByte, j);
      i++;
    }
    return arrayOfShort;
  }
  
  static int iccCStoJCS(int paramInt)
  {
    int i;
    switch (paramInt)
    {
    case 1482250784: 
      i = 0;
      break;
    case 1281450528: 
      i = 1;
      break;
    case 1282766368: 
      i = 2;
      break;
    case 1497588338: 
      i = 3;
      break;
    case 1501067552: 
      i = 4;
      break;
    case 1380401696: 
      i = 5;
      break;
    case 1196573017: 
      i = 6;
      break;
    case 1213421088: 
      i = 7;
      break;
    case 1212961568: 
      i = 8;
      break;
    case 1129142603: 
      i = 9;
      break;
    case 1129142560: 
      i = 11;
      break;
    case 843271250: 
      i = 12;
      break;
    case 860048466: 
      i = 13;
      break;
    case 876825682: 
      i = 14;
      break;
    case 893602898: 
      i = 15;
      break;
    case 910380114: 
      i = 16;
      break;
    case 927157330: 
      i = 17;
      break;
    case 943934546: 
      i = 18;
      break;
    case 960711762: 
      i = 19;
      break;
    case 1094929490: 
      i = 20;
      break;
    case 1111706706: 
      i = 21;
      break;
    case 1128483922: 
      i = 22;
      break;
    case 1145261138: 
      i = 23;
      break;
    case 1162038354: 
      i = 24;
      break;
    case 1178815570: 
      i = 25;
      break;
    default: 
      throw new IllegalArgumentException("Unknown color space");
    }
    return i;
  }
  
  static int intFromBigEndian(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  static void intToBigEndian(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    paramArrayOfByte[paramInt2] = ((byte)(paramInt1 >> 24));
    paramArrayOfByte[(paramInt2 + 1)] = ((byte)(paramInt1 >> 16));
    paramArrayOfByte[(paramInt2 + 2)] = ((byte)(paramInt1 >> 8));
    paramArrayOfByte[(paramInt2 + 3)] = ((byte)paramInt1);
  }
  
  static short shortFromBigEndian(byte[] paramArrayOfByte, int paramInt)
  {
    return (short)((paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  static void shortToBigEndian(short paramShort, byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[paramInt] = ((byte)(paramShort >> 8));
    paramArrayOfByte[(paramInt + 1)] = ((byte)paramShort);
  }
  
  private static File getProfileFile(String paramString)
  {
    File localFile = new File(paramString);
    if (localFile.isAbsolute()) {
      return localFile.isFile() ? localFile : null;
    }
    String str1;
    StringTokenizer localStringTokenizer;
    String str2;
    String str3;
    if ((!localFile.isFile()) && ((str1 = System.getProperty("java.iccprofile.path")) != null))
    {
      localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
      while ((localStringTokenizer.hasMoreTokens()) && ((localFile == null) || (!localFile.isFile())))
      {
        str2 = localStringTokenizer.nextToken();
        str3 = str2 + File.separatorChar + paramString;
        localFile = new File(str3);
        if (!isChildOf(localFile, str2)) {
          localFile = null;
        }
      }
    }
    if (((localFile == null) || (!localFile.isFile())) && ((str1 = System.getProperty("java.class.path")) != null))
    {
      localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
      while ((localStringTokenizer.hasMoreTokens()) && ((localFile == null) || (!localFile.isFile())))
      {
        str2 = localStringTokenizer.nextToken();
        str3 = str2 + File.separatorChar + paramString;
        localFile = new File(str3);
      }
    }
    if ((localFile == null) || (!localFile.isFile())) {
      localFile = getStandardProfileFile(paramString);
    }
    if ((localFile != null) && (localFile.isFile())) {
      return localFile;
    }
    return null;
  }
  
  private static File getStandardProfileFile(String paramString)
  {
    String str1 = System.getProperty("java.home") + File.separatorChar + "lib" + File.separatorChar + "cmm";
    String str2 = str1 + File.separatorChar + paramString;
    File localFile = new File(str2);
    return (localFile.isFile()) && (isChildOf(localFile, str1)) ? localFile : null;
  }
  
  private static boolean isChildOf(File paramFile, String paramString)
  {
    try
    {
      File localFile = new File(paramString);
      String str1 = localFile.getCanonicalPath();
      if (!str1.endsWith(File.separator)) {
        str1 = str1 + File.separator;
      }
      String str2 = paramFile.getCanonicalPath();
      return str2.startsWith(str1);
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  private static boolean standardProfileExists(String paramString)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(ICC_Profile.getStandardProfileFile(val$fileName) != null);
      }
    })).booleanValue();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    String str = null;
    if (this == sRGBprofile) {
      str = "CS_sRGB";
    } else if (this == XYZprofile) {
      str = "CS_CIEXYZ";
    } else if (this == PYCCprofile) {
      str = "CS_PYCC";
    } else if (this == GRAYprofile) {
      str = "CS_GRAY";
    } else if (this == LINEAR_RGBprofile) {
      str = "CS_LINEAR_RGB";
    }
    byte[] arrayOfByte = null;
    if (str == null) {
      arrayOfByte = getData();
    }
    paramObjectOutputStream.writeObject(str);
    paramObjectOutputStream.writeObject(arrayOfByte);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    String str = (String)paramObjectInputStream.readObject();
    byte[] arrayOfByte = (byte[])paramObjectInputStream.readObject();
    int i = 0;
    int j = 0;
    if (str != null)
    {
      j = 1;
      if (str.equals("CS_sRGB")) {
        i = 1000;
      } else if (str.equals("CS_CIEXYZ")) {
        i = 1001;
      } else if (str.equals("CS_PYCC")) {
        i = 1002;
      } else if (str.equals("CS_GRAY")) {
        i = 1003;
      } else if (str.equals("CS_LINEAR_RGB")) {
        i = 1004;
      } else {
        j = 0;
      }
    }
    if (j != 0) {
      resolvedDeserializedProfile = getInstance(i);
    } else {
      resolvedDeserializedProfile = getInstance(arrayOfByte);
    }
  }
  
  protected Object readResolve()
    throws ObjectStreamException
  {
    return resolvedDeserializedProfile;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\color\ICC_Profile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */