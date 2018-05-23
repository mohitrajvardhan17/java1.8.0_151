package javax.imageio.plugins.jpeg;

import java.util.Arrays;

public class JPEGHuffmanTable
{
  private static final short[] StdDCLuminanceLengths = { 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
  private static final short[] StdDCLuminanceValues = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
  private static final short[] StdDCChrominanceLengths = { 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
  private static final short[] StdDCChrominanceValues = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
  private static final short[] StdACLuminanceLengths = { 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125 };
  private static final short[] StdACLuminanceValues = { 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, 129, 145, 161, 8, 35, 66, 177, 193, 21, 82, 209, 240, 36, 51, 98, 114, 130, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250 };
  private static final short[] StdACChrominanceLengths = { 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119 };
  private static final short[] StdACChrominanceValues = { 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, 129, 8, 20, 66, 145, 161, 177, 193, 9, 35, 51, 82, 240, 21, 98, 114, 209, 10, 22, 36, 52, 225, 37, 241, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 130, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 226, 227, 228, 229, 230, 231, 232, 233, 234, 242, 243, 244, 245, 246, 247, 248, 249, 250 };
  public static final JPEGHuffmanTable StdDCLuminance = new JPEGHuffmanTable(StdDCLuminanceLengths, StdDCLuminanceValues, false);
  public static final JPEGHuffmanTable StdDCChrominance = new JPEGHuffmanTable(StdDCChrominanceLengths, StdDCChrominanceValues, false);
  public static final JPEGHuffmanTable StdACLuminance = new JPEGHuffmanTable(StdACLuminanceLengths, StdACLuminanceValues, false);
  public static final JPEGHuffmanTable StdACChrominance = new JPEGHuffmanTable(StdACChrominanceLengths, StdACChrominanceValues, false);
  private short[] lengths;
  private short[] values;
  
  public JPEGHuffmanTable(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    if ((paramArrayOfShort1 == null) || (paramArrayOfShort2 == null) || (paramArrayOfShort1.length == 0) || (paramArrayOfShort2.length == 0) || (paramArrayOfShort1.length > 16) || (paramArrayOfShort2.length > 256)) {
      throw new IllegalArgumentException("Illegal lengths or values");
    }
    for (int i = 0; i < paramArrayOfShort1.length; i++) {
      if (paramArrayOfShort1[i] < 0) {
        throw new IllegalArgumentException("lengths[" + i + "] < 0");
      }
    }
    for (i = 0; i < paramArrayOfShort2.length; i++) {
      if (paramArrayOfShort2[i] < 0) {
        throw new IllegalArgumentException("values[" + i + "] < 0");
      }
    }
    lengths = Arrays.copyOf(paramArrayOfShort1, paramArrayOfShort1.length);
    values = Arrays.copyOf(paramArrayOfShort2, paramArrayOfShort2.length);
    validate();
  }
  
  private void validate()
  {
    int i = 0;
    for (int j = 0; j < lengths.length; j++) {
      i += lengths[j];
    }
    if (i != values.length) {
      throw new IllegalArgumentException("lengths do not correspond to length of value table");
    }
  }
  
  private JPEGHuffmanTable(short[] paramArrayOfShort1, short[] paramArrayOfShort2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      lengths = Arrays.copyOf(paramArrayOfShort1, paramArrayOfShort1.length);
      values = Arrays.copyOf(paramArrayOfShort2, paramArrayOfShort2.length);
    }
    else
    {
      lengths = paramArrayOfShort1;
      values = paramArrayOfShort2;
    }
  }
  
  public short[] getLengths()
  {
    return Arrays.copyOf(lengths, lengths.length);
  }
  
  public short[] getValues()
  {
    return Arrays.copyOf(values, values.length);
  }
  
  public String toString()
  {
    String str = System.getProperty("line.separator", "\n");
    StringBuilder localStringBuilder = new StringBuilder("JPEGHuffmanTable");
    localStringBuilder.append(str).append("lengths:");
    for (int i = 0; i < lengths.length; i++) {
      localStringBuilder.append(" ").append(lengths[i]);
    }
    localStringBuilder.append(str).append("values:");
    for (i = 0; i < values.length; i++) {
      localStringBuilder.append(" ").append(values[i]);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\plugins\jpeg\JPEGHuffmanTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */