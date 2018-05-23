package org.omg.IOP;

import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;

public abstract interface CodecFactoryOperations
{
  public abstract Codec create_codec(Encoding paramEncoding)
    throws UnknownEncoding;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\CodecFactoryOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */