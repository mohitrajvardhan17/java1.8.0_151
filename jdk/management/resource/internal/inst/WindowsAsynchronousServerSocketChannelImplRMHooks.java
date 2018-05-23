package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.net.InetSocketAddress;
import jdk.internal.instrumentation.InstrumentationTarget;

@InstrumentationTarget("sun.nio.ch.WindowsAsynchronousServerSocketChannelImpl")
public class WindowsAsynchronousServerSocketChannelImplRMHooks
{
  protected final FileDescriptor fd = null;
  protected volatile InetSocketAddress localAddress = null;
  
  public WindowsAsynchronousServerSocketChannelImplRMHooks() {}
  
  /* Error */
  @jdk.internal.instrumentation.InstrumentationMethod
  void implClose()
    throws java.io.IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 62	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:implClose	()V
    //   4: aload_0
    //   5: getfield 56	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   8: invokestatic 60	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/io/FileDescriptor;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   11: astore_1
    //   12: aload_1
    //   13: ifnull +25 -> 38
    //   16: getstatic 54	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   19: aload_0
    //   20: getfield 56	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   23: invokevirtual 59	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   26: astore_2
    //   27: aload_2
    //   28: ldc2_w 25
    //   31: aload_1
    //   32: invokeinterface 63 4 0
    //   37: pop2
    //   38: aload_0
    //   39: getfield 57	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   42: ifnull +30 -> 72
    //   45: aload_0
    //   46: getfield 57	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   49: invokestatic 61	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   52: astore_1
    //   53: getstatic 55	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   56: aload_0
    //   57: invokevirtual 59	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   60: astore_2
    //   61: aload_2
    //   62: ldc2_w 25
    //   65: aload_1
    //   66: invokeinterface 63 4 0
    //   71: pop2
    //   72: goto +83 -> 155
    //   75: astore_3
    //   76: aload_0
    //   77: getfield 56	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   80: invokestatic 60	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/io/FileDescriptor;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   83: astore 4
    //   85: aload 4
    //   87: ifnull +28 -> 115
    //   90: getstatic 54	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   93: aload_0
    //   94: getfield 56	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   97: invokevirtual 59	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   100: astore 5
    //   102: aload 5
    //   104: ldc2_w 25
    //   107: aload 4
    //   109: invokeinterface 63 4 0
    //   114: pop2
    //   115: aload_0
    //   116: getfield 57	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   119: ifnull +34 -> 153
    //   122: aload_0
    //   123: getfield 57	jdk/management/resource/internal/inst/WindowsAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   126: invokestatic 61	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   129: astore 4
    //   131: getstatic 55	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   134: aload_0
    //   135: invokevirtual 59	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   138: astore 5
    //   140: aload 5
    //   142: ldc2_w 25
    //   145: aload 4
    //   147: invokeinterface 63 4 0
    //   152: pop2
    //   153: aload_3
    //   154: athrow
    //   155: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	156	0	this	WindowsAsynchronousServerSocketChannelImplRMHooks
    //   11	55	1	localResourceIdImpl1	jdk.management.resource.internal.ResourceIdImpl
    //   26	36	2	localResourceRequest1	jdk.management.resource.ResourceRequest
    //   75	79	3	localObject	Object
    //   83	63	4	localResourceIdImpl2	jdk.management.resource.internal.ResourceIdImpl
    //   100	41	5	localResourceRequest2	jdk.management.resource.ResourceRequest
    // Exception table:
    //   from	to	target	type
    //   0	4	75	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\WindowsAsynchronousServerSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */