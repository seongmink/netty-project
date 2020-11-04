package src.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private final int LENGTH = 8;
    private final int CODE_LENGTH = 2;

//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//			System.out.println("Server channelRead!");
//
//	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Server channelRead0!");

		byte[] bytes = (byte[]) msg;

		// TODO : LENGTH = 첫 8 byte( 2(구분코드) + data의 길이)
		String lengthTemp = "";
		for (int i = 0; i < LENGTH; i++) {
			if((char) bytes[i] != 0)
				lengthTemp += (char) bytes[i];
		}
		int length = Integer.parseInt(lengthTemp);
		System.out.println("length = " + length);

		// TODO : 구분 코드
		String code = "";
		for (int i = LENGTH; i < LENGTH + CODE_LENGTH; i++) {
			code += (char) bytes[i];
		}
		System.out.println("code = " + code);

		// TODO : DATA
		byte[] dataByte = new byte[length-2];
		for (int i = LENGTH + CODE_LENGTH; i < LENGTH + length; i++) {
			dataByte[i-LENGTH-CODE_LENGTH] = bytes[i];
		}
		StringTokenizer st = new StringTokenizer(new String(dataByte), "\n");

		String cmd = st.nextToken().split("=")[1];
		System.out.println("cmd = " + cmd);
		String fileName = st.nextToken().split("=")[1];
		System.out.println("fileName = " + fileName);
		String orgFileName = st.nextToken().split("=")[1];
		System.out.println("orgFileName = " + orgFileName);
		String fileSize = st.nextToken().split("=")[1];
		System.out.println("fileSize = " + fileSize);
		String saveDir = st.nextToken().split("=")[1];
		System.out.println("saveDir = " + saveDir);

		// TODO : 파일 저장
		byte[] file = new byte[Integer.parseInt(fileSize)];
		for (int i = LENGTH + length; i < bytes.length; i++) {
			file[i-LENGTH-length] = bytes[i];
		}

		// TODO : Client로 result 전송
		byte[] result = new byte[LENGTH + CODE_LENGTH + saveDir.length()];
		byte[] resultLength = String.valueOf(saveDir.length() + CODE_LENGTH).getBytes();
		System.arraycopy(resultLength, 0, result, LENGTH - resultLength.length, resultLength.length);

		byte[] resultData = saveDir.getBytes();
		System.arraycopy(resultData, 0, result, LENGTH + CODE_LENGTH, resultData.length);

		File saveFile = new File(saveDir);
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(saveFile);
			fos.write(file);
			fos.close();

			result[8] = 'O';
			result[9] = 'K';
		} catch (Exception e) {
			result[8] = 'N';
			result[9] = 'O';
			e.printStackTrace();
		} finally {
			ByteBuf byteBuf = Unpooled.wrappedBuffer(result);
			ctx.writeAndFlush(byteBuf);
			System.out.println("Arrays.toString(result) = " + Arrays.toString(result));
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server channelReadComplete!");
//		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("Server exceptionCaught!");
		cause.printStackTrace();
		ctx.close();
	}
}
