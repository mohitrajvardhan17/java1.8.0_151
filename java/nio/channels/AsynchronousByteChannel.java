package java.nio.channels;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;

public abstract interface AsynchronousByteChannel
  extends AsynchronousChannel
{
  public abstract <A> void read(ByteBuffer paramByteBuffer, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public abstract Future<Integer> read(ByteBuffer paramByteBuffer);
  
  public abstract <A> void write(ByteBuffer paramByteBuffer, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public abstract Future<Integer> write(ByteBuffer paramByteBuffer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\AsynchronousByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */