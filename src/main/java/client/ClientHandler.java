package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCounted;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
@Slf4j
public class ClientHandler extends ChannelHandlerAdapter {

  // ChannelboundHandler에 정의된 이벤트
  // 소켓 채널이 최초 활성화 되었을 때 실행
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    System.out.println("Client channelActive!");
    System.out.println("클라이언트 파일 전송");
    String filePath = "C:/test412321123125.exe";
    File file = new File(filePath);
    if(file.length() == 0) {
      System.out.println("해당 파일이 없습니다.");
      
      return;
    }
    String result = "";
    StringBuilder sb = new StringBuilder();
    InputStream is = null;
    try {
      is = new FileInputStream(file);

      // TODO: DATA 생성
      sb.append("CMD:=C\n")
              .append("FILENAME:=").append(file.getName()).append("\n")
              .append("ORGFILENAME:=").append(file.getName()).append("\n")
              .append("FILESIZE:=").append(file.length()).append("\n")
              .append("SAVE_DIR:=").append("C:/test4123215.exe").append("\n");
      result = sb.toString();

      String code = "FI";

      String length = Integer.toString(result.length() + code.length());
      // TODO: 고정 8bytes Length
      byte[] arrL = new byte[8];
      byte[] tmpL = length.getBytes();
      System.arraycopy(tmpL, 0, arrL, arrL.length - tmpL.length, tmpL.length);
      System.out.println(Arrays.toString(arrL));

      // TODO: 구분코드 2bytes
      byte[] arrC = code.getBytes();

      // TODO: Data
      byte[] arrD = result.getBytes();

      // TODO: File
      byte[] arrF = IOUtils.toByteArray(is);

      // TODO: 생성된 byte 배열 병합
      byte[] request = new byte[arrL.length + arrC.length + arrD.length + arrF.length];
      System.arraycopy(arrL, 0, request, 0, arrL.length);
      System.arraycopy(arrC, 0, request, arrL.length, arrC.length);
      System.arraycopy(arrD, 0, request, arrL.length + arrC.length, arrD.length);
      System.arraycopy(arrF, 0, request, arrL.length + arrC.length + arrD.length, arrF.length);
      ByteBuf byteBuf = Unpooled.wrappedBuffer(request);

      // 내부적으로 데이터 기록과 전송의 두 가지 메서드 호출
      // write : 채널에 데이터를 기록
      // flush : 채널에 기록된 데이터를 서버로 전송
      ctx.writeAndFlush(byteBuf);
    } catch (Exception e) {
      int idx = e.toString().lastIndexOf('(');
      String tmpE = e.toString().split(":")[0];
      String exception = e.toString().substring(idx);
      System.out.println(tmpE + " " + exception);
    }
  }

  // 서버로부터 수신된 데이터가 있을 때 호출
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    System.out.println("Client channelRead!");
    byte[] bytes = (byte[]) msg;
    ByteBuf buf = Unpooled.wrappedBuffer(bytes);
//      ByteBuf buf = (ByteBuf) msg;
    if (bytes.length < 10) {

      System.out.println("서버의 응답 형식이 잘못되었습니다.");
    }
    // 앞 8bytes 의 데이터 길이
    String length = "";
    for (int i = 0; i < 8; i++) {
      int chk = buf.readByte();
      if(chk != 0) {
        length += (char) chk;
      }
//      if((char) bytes[i] != 0) {
//        length += (char) bytes[i];
//      }
    }
    System.out.println("Length : " + length);


    // 구분코드 출력
//    byte[] code = {bytes[8], bytes[9]};
    byte[] code = new byte[2];
    for (int i = 0; i < 2; i++) {
      code[i] = buf.readByte();
    }
    System.out.println("구분코드 : " + new String(code));


    // 데이터 출력
    int tmp = Integer.parseInt(length) - 2;
    byte[] data = new byte[tmp];
    for (int i = 0; i < tmp; i++) {
      data[i] = buf.readByte();
//      data[i] = bytes[i+10];
    }
    System.out.println("Data : " + new String(data));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    System.out.println("Client exceptionCaught!");
    cause.printStackTrace();
    ctx.close();
  }
}