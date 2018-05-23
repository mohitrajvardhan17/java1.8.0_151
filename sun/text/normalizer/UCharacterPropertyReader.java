package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class UCharacterPropertyReader
  implements ICUBinary.Authenticate
{
  private static final int INDEX_SIZE_ = 16;
  private DataInputStream m_dataInputStream_;
  private int m_propertyOffset_;
  private int m_exceptionOffset_;
  private int m_caseOffset_;
  private int m_additionalOffset_;
  private int m_additionalVectorsOffset_;
  private int m_additionalColumnsCount_;
  private int m_reservedOffset_;
  private byte[] m_unicodeVersion_ = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID_, this);
  private static final byte[] DATA_FORMAT_ID_ = { 85, 80, 114, 111 };
  private static final byte[] DATA_FORMAT_VERSION_ = { 5, 0, 5, 2 };
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == DATA_FORMAT_VERSION_[0]) && (paramArrayOfByte[2] == DATA_FORMAT_VERSION_[2]) && (paramArrayOfByte[3] == DATA_FORMAT_VERSION_[3]);
  }
  
  protected UCharacterPropertyReader(InputStream paramInputStream)
    throws IOException
  {
    m_dataInputStream_ = new DataInputStream(paramInputStream);
  }
  
  protected void read(UCharacterProperty paramUCharacterProperty)
    throws IOException
  {
    int i = 16;
    m_propertyOffset_ = m_dataInputStream_.readInt();
    i--;
    m_exceptionOffset_ = m_dataInputStream_.readInt();
    i--;
    m_caseOffset_ = m_dataInputStream_.readInt();
    i--;
    m_additionalOffset_ = m_dataInputStream_.readInt();
    i--;
    m_additionalVectorsOffset_ = m_dataInputStream_.readInt();
    i--;
    m_additionalColumnsCount_ = m_dataInputStream_.readInt();
    i--;
    m_reservedOffset_ = m_dataInputStream_.readInt();
    i--;
    m_dataInputStream_.skipBytes(12);
    i -= 3;
    m_maxBlockScriptValue_ = m_dataInputStream_.readInt();
    i--;
    m_maxJTGValue_ = m_dataInputStream_.readInt();
    i--;
    m_dataInputStream_.skipBytes(i << 2);
    m_trie_ = new CharTrie(m_dataInputStream_, null);
    int j = m_exceptionOffset_ - m_propertyOffset_;
    m_dataInputStream_.skipBytes(j * 4);
    j = m_caseOffset_ - m_exceptionOffset_;
    m_dataInputStream_.skipBytes(j * 4);
    j = m_additionalOffset_ - m_caseOffset_ << 1;
    m_dataInputStream_.skipBytes(j * 2);
    if (m_additionalColumnsCount_ > 0)
    {
      m_additionalTrie_ = new CharTrie(m_dataInputStream_, null);
      j = m_reservedOffset_ - m_additionalVectorsOffset_;
      m_additionalVectors_ = new int[j];
      for (int k = 0; k < j; k++) {
        m_additionalVectors_[k] = m_dataInputStream_.readInt();
      }
    }
    m_dataInputStream_.close();
    m_additionalColumnsCount_ = m_additionalColumnsCount_;
    m_unicodeVersion_ = VersionInfo.getInstance(m_unicodeVersion_[0], m_unicodeVersion_[1], m_unicodeVersion_[2], m_unicodeVersion_[3]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\UCharacterPropertyReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */