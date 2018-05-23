package javax.imageio.plugins.jpeg;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class JPEGImageWriteParam
  extends ImageWriteParam
{
  private JPEGQTable[] qTables = null;
  private JPEGHuffmanTable[] DCHuffmanTables = null;
  private JPEGHuffmanTable[] ACHuffmanTables = null;
  private boolean optimizeHuffman = false;
  private String[] compressionNames = { "JPEG" };
  private float[] qualityVals = { 0.0F, 0.3F, 0.75F, 1.0F };
  private String[] qualityDescs = { "Low quality", "Medium quality", "Visually lossless" };
  
  public JPEGImageWriteParam(Locale paramLocale)
  {
    super(paramLocale);
    canWriteProgressive = true;
    progressiveMode = 0;
    canWriteCompressed = true;
    compressionTypes = compressionNames;
    compressionType = compressionTypes[0];
    compressionQuality = 0.75F;
  }
  
  public void unsetCompression()
  {
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    compressionQuality = 0.75F;
  }
  
  public boolean isCompressionLossless()
  {
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    return false;
  }
  
  public String[] getCompressionQualityDescriptions()
  {
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    return (String[])qualityDescs.clone();
  }
  
  public float[] getCompressionQualityValues()
  {
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    return (float[])qualityVals.clone();
  }
  
  public boolean areTablesSet()
  {
    return qTables != null;
  }
  
  public void setEncodeTables(JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2)
  {
    if ((paramArrayOfJPEGQTable == null) || (paramArrayOfJPEGHuffmanTable1 == null) || (paramArrayOfJPEGHuffmanTable2 == null) || (paramArrayOfJPEGQTable.length > 4) || (paramArrayOfJPEGHuffmanTable1.length > 4) || (paramArrayOfJPEGHuffmanTable2.length > 4) || (paramArrayOfJPEGHuffmanTable1.length != paramArrayOfJPEGHuffmanTable2.length)) {
      throw new IllegalArgumentException("Invalid JPEG table arrays");
    }
    qTables = ((JPEGQTable[])paramArrayOfJPEGQTable.clone());
    DCHuffmanTables = ((JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable1.clone());
    ACHuffmanTables = ((JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable2.clone());
  }
  
  public void unsetEncodeTables()
  {
    qTables = null;
    DCHuffmanTables = null;
    ACHuffmanTables = null;
  }
  
  public JPEGQTable[] getQTables()
  {
    return qTables != null ? (JPEGQTable[])qTables.clone() : null;
  }
  
  public JPEGHuffmanTable[] getDCHuffmanTables()
  {
    return DCHuffmanTables != null ? (JPEGHuffmanTable[])DCHuffmanTables.clone() : null;
  }
  
  public JPEGHuffmanTable[] getACHuffmanTables()
  {
    return ACHuffmanTables != null ? (JPEGHuffmanTable[])ACHuffmanTables.clone() : null;
  }
  
  public void setOptimizeHuffmanTables(boolean paramBoolean)
  {
    optimizeHuffman = paramBoolean;
  }
  
  public boolean getOptimizeHuffmanTables()
  {
    return optimizeHuffman;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\plugins\jpeg\JPEGImageWriteParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */