package javax.imageio.plugins.jpeg;

import javax.imageio.ImageReadParam;

public class JPEGImageReadParam
  extends ImageReadParam
{
  private JPEGQTable[] qTables = null;
  private JPEGHuffmanTable[] DCHuffmanTables = null;
  private JPEGHuffmanTable[] ACHuffmanTables = null;
  
  public JPEGImageReadParam() {}
  
  public boolean areTablesSet()
  {
    return qTables != null;
  }
  
  public void setDecodeTables(JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2)
  {
    if ((paramArrayOfJPEGQTable == null) || (paramArrayOfJPEGHuffmanTable1 == null) || (paramArrayOfJPEGHuffmanTable2 == null) || (paramArrayOfJPEGQTable.length > 4) || (paramArrayOfJPEGHuffmanTable1.length > 4) || (paramArrayOfJPEGHuffmanTable2.length > 4) || (paramArrayOfJPEGHuffmanTable1.length != paramArrayOfJPEGHuffmanTable2.length)) {
      throw new IllegalArgumentException("Invalid JPEG table arrays");
    }
    qTables = ((JPEGQTable[])paramArrayOfJPEGQTable.clone());
    DCHuffmanTables = ((JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable1.clone());
    ACHuffmanTables = ((JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable2.clone());
  }
  
  public void unsetDecodeTables()
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\plugins\jpeg\JPEGImageReadParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */