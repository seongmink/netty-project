package src.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ClientHandler extends ChannelHandlerAdapter {

  // ChannelboundHandler에 정의된 이벤트
  // 소켓 채널이 최초 활성화 되었을 때 실행
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    System.out.println("Client channelActive!");
    System.out.println("클라이언트 파일 전송");
    String filePath = "C:/test.png";
    File file = new File(filePath);
    System.out.println(file);
    String result = "";
    StringBuilder sb = new StringBuilder();
    InputStream is = null;
    try {
      is = new FileInputStream(file);

      sb.append("CMD:=C\n")
              .append("FILENAME:=").append(file.getName()).append("\n")
              .append("ORGFILENAME:=").append(file.getName()).append("\n")
              .append("FILESIZE:=").append(file.length()).append("\n")
              .append("SAVE_DIR:=").append(file.getAbsolutePath()).append("\n");
      result = sb.toString();

      String code = "FI";
      String length = Integer.toString(result.length() + code.length());
      byte[] arrL = length.getBytes();

      byte[] arrC = code.getBytes();

      byte[] arrD = result.getBytes();

      byte[] arrF = IOUtils.toByteArray(is);

      byte[] request = new byte[arrL.length + arrC.length + arrD.length + arrF.length];
      System.arraycopy(arrL, 0, request, 0, arrL.length);
      System.arraycopy(arrC, 0, request, arrL.length, arrC.length);
      System.arraycopy(arrD, 0, request, arrL.length + arrC.length, arrD.length);
      System.arraycopy(arrF, 0, request, arrL.length + arrC.length + arrD.length, arrF.length);
      ByteBuf byteBuf = Unpooled.wrappedBuffer(request);


//      System.out.println(">>>>>"+new String(request) + "<<<<");


      // 내부적으로 데이터 기록과 전송의 두 가지 메서드 호출
      // write : 채널에 데이터를 기록
      // flush : 채널에 기록된 데이터를 서버로 전송
      ctx.writeAndFlush(byteBuf);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 서버로부터 수신된 데이터가 있을 때 호출
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    System.out.println("Client channelRead!");
    byte[] bytes = (byte[]) msg;

    System.out.println(new String(bytes));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    System.out.println("Client exceptionCaught!");
    cause.printStackTrace();
    ctx.close();
  }
}
