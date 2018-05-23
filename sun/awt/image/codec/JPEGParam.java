package sun.awt.image.codec;

import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGHuffmanTable;
import com.sun.image.codec.jpeg.JPEGQTable;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.util.Enumeration;
import java.util.Vector;

public class JPEGParam
  implements JPEGEncodeParam, Cloneable
{
  private static int[] defComponents = { -1, 1, 3, 3, 4, 3, 4, 4, 4, 4, 4, 4 };
  private static int[][] stdCompMapping = { { 0, 0, 0, 0 }, { 0 }, { 0, 0, 0 }, { 0, 1, 1 }, { 0, 0, 0, 0 }, { 0, 1, 1 }, { 0, 0, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 1, 1, 0 } };
  private static int[][] stdSubsample = { { 1, 1, 1, 1 }, { 1 }, { 1, 1, 1 }, { 1, 2, 2 }, { 1, 1, 1, 1 }, { 1, 2, 2 }, { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, { 1, 2, 2, 1 }, { 1, 2, 2, 1 } };
  private int width;
  private int height;
  private int encodedColorID;
  private int numComponents;
  private byte[][][] appMarkers;
  private byte[][] comMarker;
  private boolean imageInfoValid;
  private boolean tableInfoValid;
  private int[] horizontalSubsampling;
  private int[] verticalSubsampling;
  private JPEGQTable[] qTables;
  private int[] qTableMapping;
  private JPEGHuffmanTable[] dcHuffTables;
  private int[] dcHuffMapping;
  private JPEGHuffmanTable[] acHuffTables;
  private int[] acHuffMapping;
  private int restartInterval;
  private static final int app0Length = 14;
  
  public JPEGParam(int paramInt)
  {
    this(paramInt, defComponents[paramInt]);
  }
  
  public JPEGParam(JPEGDecodeParam paramJPEGDecodeParam)
  {
    this(paramJPEGDecodeParam.getEncodedColorID(), paramJPEGDecodeParam.getNumComponents());
    copy(paramJPEGDecodeParam);
  }
  
  public JPEGParam(JPEGEncodeParam paramJPEGEncodeParam)
  {
    this(paramJPEGEncodeParam.getEncodedColorID(), paramJPEGEncodeParam.getNumComponents());
    copy(paramJPEGEncodeParam);
  }
  
  public JPEGParam(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != 0) && (paramInt2 != defComponents[paramInt1])) {
      throw new IllegalArgumentException("NumComponents not in sync with COLOR_ID");
    }
    qTables = new JPEGQTable[4];
    acHuffTables = new JPEGHuffmanTable[4];
    dcHuffTables = new JPEGHuffmanTable[4];
    for (int i = 0; i < 4; i++)
    {
      qTables[i] = null;
      dcHuffTables[i] = null;
      acHuffTables[i] = null;
    }
    comMarker = ((byte[][])null);
    appMarkers = new byte[16][][];
    numComponents = paramInt2;
    setDefaults(paramInt1);
  }
  
  private void copy(JPEGDecodeParam paramJPEGDecodeParam)
  {
    if (getEncodedColorID() != paramJPEGDecodeParam.getEncodedColorID()) {
      throw new IllegalArgumentException("Argument to copy must match current COLOR_ID");
    }
    if (getNumComponents() != paramJPEGDecodeParam.getNumComponents()) {
      throw new IllegalArgumentException("Argument to copy must match in number of components");
    }
    setWidth(paramJPEGDecodeParam.getWidth());
    setHeight(paramJPEGDecodeParam.getHeight());
    for (int i = 224; i < 239; i++) {
      setMarkerData(i, copyArrays(paramJPEGDecodeParam.getMarkerData(i)));
    }
    setMarkerData(254, copyArrays(paramJPEGDecodeParam.getMarkerData(254)));
    setTableInfoValid(paramJPEGDecodeParam.isTableInfoValid());
    setImageInfoValid(paramJPEGDecodeParam.isImageInfoValid());
    setRestartInterval(paramJPEGDecodeParam.getRestartInterval());
    for (i = 0; i < 4; i++)
    {
      setDCHuffmanTable(i, paramJPEGDecodeParam.getDCHuffmanTable(i));
      setACHuffmanTable(i, paramJPEGDecodeParam.getACHuffmanTable(i));
      setQTable(i, paramJPEGDecodeParam.getQTable(i));
    }
    for (i = 0; i < paramJPEGDecodeParam.getNumComponents(); i++)
    {
      setDCHuffmanComponentMapping(i, paramJPEGDecodeParam.getDCHuffmanComponentMapping(i));
      setACHuffmanComponentMapping(i, paramJPEGDecodeParam.getACHuffmanComponentMapping(i));
      setQTableComponentMapping(i, paramJPEGDecodeParam.getQTableComponentMapping(i));
      setHorizontalSubsampling(i, paramJPEGDecodeParam.getHorizontalSubsampling(i));
      setVerticalSubsampling(i, paramJPEGDecodeParam.getVerticalSubsampling(i));
    }
  }
  
  private void copy(JPEGEncodeParam paramJPEGEncodeParam)
  {
    copy(paramJPEGEncodeParam);
  }
  
  protected void setDefaults(int paramInt)
  {
    encodedColorID = paramInt;
    restartInterval = 0;
    int i = 0;
    switch (numComponents)
    {
    case 1: 
      if ((encodedColorID == 1) || (encodedColorID == 0)) {
        i = 1;
      }
      break;
    case 3: 
      if (encodedColorID == 3) {
        i = 1;
      }
      break;
    case 4: 
      if (encodedColorID == 4) {
        i = 1;
      }
      break;
    }
    if (i != 0) {
      addMarkerData(224, createDefaultAPP0Marker());
    }
    setTableInfoValid(true);
    setImageInfoValid(true);
    dcHuffTables[0] = JPEGHuffmanTable.StdDCLuminance;
    dcHuffTables[1] = JPEGHuffmanTable.StdDCChrominance;
    dcHuffMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[encodedColorID], 0, dcHuffMapping, 0, getNumComponents());
    acHuffTables[0] = JPEGHuffmanTable.StdACLuminance;
    acHuffTables[1] = JPEGHuffmanTable.StdACChrominance;
    acHuffMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[encodedColorID], 0, acHuffMapping, 0, getNumComponents());
    qTables[0] = JPEGQTable.StdLuminance.getScaledInstance(0.5F, true);
    qTables[1] = JPEGQTable.StdChrominance.getScaledInstance(0.5F, true);
    qTableMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[encodedColorID], 0, qTableMapping, 0, getNumComponents());
    horizontalSubsampling = new int[getNumComponents()];
    System.arraycopy(stdSubsample[encodedColorID], 0, horizontalSubsampling, 0, getNumComponents());
    verticalSubsampling = new int[getNumComponents()];
    System.arraycopy(stdSubsample[encodedColorID], 0, verticalSubsampling, 0, getNumComponents());
  }
  
  public Object clone()
  {
    JPEGParam localJPEGParam = new JPEGParam(getEncodedColorID(), getNumComponents());
    localJPEGParam.copy(this);
    return localJPEGParam;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public void setWidth(int paramInt)
  {
    width = paramInt;
  }
  
  public void setHeight(int paramInt)
  {
    height = paramInt;
  }
  
  public int getHorizontalSubsampling(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getNumComponents())) {
      throw new IllegalArgumentException("Component must be between 0 and number of components");
    }
    return horizontalSubsampling[paramInt];
  }
  
  public int getVerticalSubsampling(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getNumComponents())) {
      throw new IllegalArgumentException("Component must be between 0 and number of components");
    }
    return verticalSubsampling[paramInt];
  }
  
  public void setHorizontalSubsampling(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= getNumComponents())) {
      throw new IllegalArgumentException("Component must be between 0 and number of components: " + paramInt1);
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("SubSample factor must be positive: " + paramInt2);
    }
    horizontalSubsampling[paramInt1] = paramInt2;
  }
  
  public void setVerticalSubsampling(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= getNumComponents())) {
      throw new IllegalArgumentException("Component must be between 0 and number of components");
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("SubSample factor must be positive.");
    }
    verticalSubsampling[paramInt1] = paramInt2;
  }
  
  public JPEGQTable getQTable(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("tableNum must be between 0 and 3.");
    }
    return qTables[paramInt];
  }
  
  public JPEGQTable getQTableForComponent(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= qTableMapping.length)) {
      throw new IllegalArgumentException("Component must be between 0 and number of components");
    }
    return getQTable(qTableMapping[paramInt]);
  }
  
  public JPEGHuffmanTable getDCHuffmanTable(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("tableNum must be 0-3.");
    }
    return dcHuffTables[paramInt];
  }
  
  public JPEGHuffmanTable getDCHuffmanTableForComponent(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= dcHuffMapping.length)) {
      throw new IllegalArgumentException("Component must be between 0 and number of components");
    }
    return getDCHuffmanTable(dcHuffMapping[paramInt]);
  }
  
  public JPEGHuffmanTable getACHuffmanTable(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("tableNum must be 0-3.");
    }
    return acHuffTables[paramInt];
  }
  
  public JPEGHuffmanTable getACHuffmanTableForComponent(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= acHuffMapping.length)) {
      throw new IllegalArgumentException("Component must be between 0 and number of components");
    }
    return getACHuffmanTable(acHuffMapping[paramInt]);
  }
  
  public void setQTable(int paramInt, JPEGQTable paramJPEGQTable)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("tableNum must be between 0 and 3.");
    }
    qTables[paramInt] = paramJPEGQTable;
  }
  
  public void setDCHuffmanTable(int paramInt, JPEGHuffmanTable paramJPEGHuffmanTable)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("tableNum must be 0, 1, 2, or 3.");
    }
    dcHuffTables[paramInt] = paramJPEGHuffmanTable;
  }
  
  public void setACHuffmanTable(int paramInt, JPEGHuffmanTable paramJPEGHuffmanTable)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("tableNum must be 0, 1, 2, or 3.");
    }
    acHuffTables[paramInt] = paramJPEGHuffmanTable;
  }
  
  public int getDCHuffmanComponentMapping(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getNumComponents())) {
      throw new IllegalArgumentException("Requested Component doesn't exist.");
    }
    return dcHuffMapping[paramInt];
  }
  
  public int getACHuffmanComponentMapping(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getNumComponents())) {
      throw new IllegalArgumentException("Requested Component doesn't exist.");
    }
    return acHuffMapping[paramInt];
  }
  
  public int getQTableComponentMapping(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getNumComponents())) {
      throw new IllegalArgumentException("Requested Component doesn't exist.");
    }
    return qTableMapping[paramInt];
  }
  
  public void setDCHuffmanComponentMapping(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= getNumComponents())) {
      throw new IllegalArgumentException("Given Component doesn't exist.");
    }
    if ((paramInt2 < 0) || (paramInt2 >= 4)) {
      throw new IllegalArgumentException("Tables must be 0, 1, 2, or 3.");
    }
    dcHuffMapping[paramInt1] = paramInt2;
  }
  
  public void setACHuffmanComponentMapping(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= getNumComponents())) {
      throw new IllegalArgumentException("Given Component doesn't exist.");
    }
    if ((paramInt2 < 0) || (paramInt2 >= 4)) {
      throw new IllegalArgumentException("Tables must be 0, 1, 2, or 3.");
    }
    acHuffMapping[paramInt1] = paramInt2;
  }
  
  public void setQTableComponentMapping(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= getNumComponents())) {
      throw new IllegalArgumentException("Given Component doesn't exist.");
    }
    if ((paramInt2 < 0) || (paramInt2 >= 4)) {
      throw new IllegalArgumentException("Tables must be 0, 1, 2, or 3.");
    }
    qTableMapping[paramInt1] = paramInt2;
  }
  
  public boolean isImageInfoValid()
  {
    return imageInfoValid;
  }
  
  public void setImageInfoValid(boolean paramBoolean)
  {
    imageInfoValid = paramBoolean;
  }
  
  public boolean isTableInfoValid()
  {
    return tableInfoValid;
  }
  
  public void setTableInfoValid(boolean paramBoolean)
  {
    tableInfoValid = paramBoolean;
  }
  
  public boolean getMarker(int paramInt)
  {
    byte[][] arrayOfByte = (byte[][])null;
    if (paramInt == 254) {
      arrayOfByte = comMarker;
    } else if ((paramInt >= 224) && (paramInt <= 239)) {
      arrayOfByte = appMarkers[(paramInt - 224)];
    } else {
      throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
    }
    if (arrayOfByte == null) {
      return false;
    }
    return arrayOfByte.length != 0;
  }
  
  public byte[][] getMarkerData(int paramInt)
  {
    if (paramInt == 254) {
      return comMarker;
    }
    if ((paramInt >= 224) && (paramInt <= 239)) {
      return appMarkers[(paramInt - 224)];
    }
    throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
  }
  
  public void setMarkerData(int paramInt, byte[][] paramArrayOfByte)
  {
    if (paramInt == 254) {
      comMarker = paramArrayOfByte;
    } else if ((paramInt >= 224) && (paramInt <= 239)) {
      appMarkers[(paramInt - 224)] = paramArrayOfByte;
    } else {
      throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
    }
  }
  
  public void addMarkerData(int paramInt, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return;
    }
    if (paramInt == 254) {
      comMarker = appendArray(comMarker, paramArrayOfByte);
    } else if ((paramInt >= 224) && (paramInt <= 239)) {
      appMarkers[(paramInt - 224)] = appendArray(appMarkers[(paramInt - 224)], paramArrayOfByte);
    } else {
      throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
    }
  }
  
  public int getEncodedColorID()
  {
    return encodedColorID;
  }
  
  public int getNumComponents()
  {
    return numComponents;
  }
  
  public static int getNumComponents(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 12)) {
      throw new IllegalArgumentException("Invalid JPEGColorID.");
    }
    return defComponents[paramInt];
  }
  
  public int getRestartInterval()
  {
    return restartInterval;
  }
  
  public void setRestartInterval(int paramInt)
  {
    restartInterval = paramInt;
  }
  
  public int getDensityUnit()
  {
    if (!getMarker(224)) {
      throw new IllegalArgumentException("No APP0 marker present");
    }
    byte[] arrayOfByte = findAPP0();
    if (arrayOfByte == null) {
      throw new IllegalArgumentException("Can't understand APP0 marker that is present");
    }
    return arrayOfByte[7];
  }
  
  public int getXDensity()
  {
    if (!getMarker(224)) {
      throw new IllegalArgumentException("No APP0 marker present");
    }
    byte[] arrayOfByte = findAPP0();
    if (arrayOfByte == null) {
      throw new IllegalArgumentException("Can't understand APP0 marker that is present");
    }
    int i = arrayOfByte[8] << 8 | arrayOfByte[9] & 0xFF;
    return i;
  }
  
  public int getYDensity()
  {
    if (!getMarker(224)) {
      throw new IllegalArgumentException("No APP0 marker present");
    }
    byte[] arrayOfByte = findAPP0();
    if (arrayOfByte == null) {
      throw new IllegalArgumentException("Can't understand APP0 marker that is present");
    }
    int i = arrayOfByte[10] << 8 | arrayOfByte[11] & 0xFF;
    return i;
  }
  
  public void setDensityUnit(int paramInt)
  {
    byte[] arrayOfByte = null;
    if (!getMarker(224))
    {
      arrayOfByte = createDefaultAPP0Marker();
      addMarkerData(224, arrayOfByte);
    }
    else
    {
      arrayOfByte = findAPP0();
      if (arrayOfByte == null) {
        throw new IllegalArgumentException("Can't understand APP0 marker that is present");
      }
    }
    arrayOfByte[7] = ((byte)paramInt);
  }
  
  public void setXDensity(int paramInt)
  {
    byte[] arrayOfByte = null;
    if (!getMarker(224))
    {
      arrayOfByte = createDefaultAPP0Marker();
      addMarkerData(224, arrayOfByte);
    }
    else
    {
      arrayOfByte = findAPP0();
      if (arrayOfByte == null) {
        throw new IllegalArgumentException("Can't understand APP0 marker that is present");
      }
    }
    arrayOfByte[8] = ((byte)(paramInt >>> 8 & 0xFF));
    arrayOfByte[9] = ((byte)(paramInt & 0xFF));
  }
  
  public void setYDensity(int paramInt)
  {
    byte[] arrayOfByte = null;
    if (!getMarker(224))
    {
      arrayOfByte = createDefaultAPP0Marker();
      addMarkerData(224, arrayOfByte);
    }
    else
    {
      arrayOfByte = findAPP0();
      if (arrayOfByte == null) {
        throw new IllegalArgumentException("Can't understand APP0 marker that is present");
      }
    }
    arrayOfByte[10] = ((byte)(paramInt >>> 8 & 0xFF));
    arrayOfByte[11] = ((byte)(paramInt & 0xFF));
  }
  
  public void setQuality(float paramFloat, boolean paramBoolean)
  {
    double d = paramFloat;
    if (d <= 0.01D) {
      d = 0.01D;
    }
    if (d > 1.0D) {
      d = 1.0D;
    }
    if (d < 0.5D) {
      d = 0.5D / d;
    } else {
      d = 2.0D - d * 2.0D;
    }
    qTableMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[encodedColorID], 0, qTableMapping, 0, getNumComponents());
    JPEGQTable localJPEGQTable = JPEGQTable.StdLuminance;
    qTables[0] = localJPEGQTable.getScaledInstance((float)d, paramBoolean);
    localJPEGQTable = JPEGQTable.StdChrominance;
    qTables[1] = localJPEGQTable.getScaledInstance((float)d, paramBoolean);
    qTables[2] = null;
    qTables[3] = null;
  }
  
  byte[] findAPP0()
  {
    byte[][] arrayOfByte = (byte[][])null;
    arrayOfByte = getMarkerData(224);
    if (arrayOfByte == null) {
      return null;
    }
    for (int i = 0; i < arrayOfByte.length; i++) {
      if ((arrayOfByte[i] != null) && (checkAPP0(arrayOfByte[i]))) {
        return arrayOfByte[i];
      }
    }
    return null;
  }
  
  static boolean checkAPP0(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 14) {
      return false;
    }
    if ((paramArrayOfByte[0] != 74) || (paramArrayOfByte[1] != 70) || (paramArrayOfByte[2] != 73) || (paramArrayOfByte[3] != 70) || (paramArrayOfByte[4] != 0)) {
      return false;
    }
    return paramArrayOfByte[5] >= 1;
  }
  
  static byte[] createDefaultAPP0Marker()
  {
    byte[] arrayOfByte = new byte[14];
    arrayOfByte[0] = 74;
    arrayOfByte[1] = 70;
    arrayOfByte[2] = 73;
    arrayOfByte[3] = 70;
    arrayOfByte[4] = 0;
    arrayOfByte[5] = 1;
    arrayOfByte[6] = 1;
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 1;
    arrayOfByte[10] = 0;
    arrayOfByte[11] = 1;
    arrayOfByte[12] = 0;
    arrayOfByte[13] = 0;
    return arrayOfByte;
  }
  
  static byte[] copyArray(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    return arrayOfByte;
  }
  
  static byte[][] copyArrays(byte[][] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return (byte[][])null;
    }
    byte[][] arrayOfByte = new byte[paramArrayOfByte.length][];
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      if (paramArrayOfByte[i] != null) {
        arrayOfByte[i] = copyArray(paramArrayOfByte[i]);
      }
    }
    return arrayOfByte;
  }
  
  static byte[][] appendArray(byte[][] paramArrayOfByte, byte[] paramArrayOfByte1)
  {
    int i = 0;
    if (paramArrayOfByte != null) {
      i = paramArrayOfByte.length;
    }
    byte[][] arrayOfByte = new byte[i + 1][];
    for (int j = 0; j < i; j++) {
      arrayOfByte[j] = paramArrayOfByte[j];
    }
    if (paramArrayOfByte1 != null) {
      arrayOfByte[i] = copyArray(paramArrayOfByte1);
    }
    return arrayOfByte;
  }
  
  static byte[][] buildArray(Vector paramVector)
  {
    if (paramVector == null) {
      return (byte[][])null;
    }
    int i = 0;
    byte[][] arrayOfByte = new byte[paramVector.size()][];
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements())
    {
      byte[] arrayOfByte1 = (byte[])localEnumeration.nextElement();
      if (arrayOfByte1 != null) {
        arrayOfByte[(i++)] = copyArray(arrayOfByte1);
      }
    }
    return arrayOfByte;
  }
  
  public static int getDefaultColorId(ColorModel paramColorModel)
  {
    boolean bool = paramColorModel.hasAlpha();
    ColorSpace localColorSpace1 = paramColorModel.getColorSpace();
    ColorSpace localColorSpace2 = null;
    switch (localColorSpace1.getType())
    {
    case 6: 
      return 1;
    case 5: 
      if (bool) {
        return 7;
      }
      return 3;
    case 3: 
      if (localColorSpace2 == null) {
        try
        {
          localColorSpace2 = ColorSpace.getInstance(1002);
        }
        catch (IllegalArgumentException localIllegalArgumentException) {}
      }
      if (localColorSpace1 == localColorSpace2) {
        return bool ? 10 : 5;
      }
      return bool ? 7 : 3;
    case 9: 
      return 4;
    }
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\codec\JPEGParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */