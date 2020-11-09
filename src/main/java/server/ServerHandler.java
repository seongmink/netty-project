package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ServerHandler extends ChannelHandlerAdapter {
    private final int LENGTH = 8;
    private final int CODE_LENGTH = 2;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Server channelRead!");

		ByteBuf buf = (ByteBuf) msg;

		// TODO : LENGTH = 첫 8 byte( 2(구분코드) + data의 길이)
		String lengthTemp = "";
		for (int i = 0; i < LENGTH; i++) {
			lengthTemp += (char) buf.readByte();
		}
		int dataLength = Integer.parseInt(lengthTemp.trim()) - 2;
		System.out.println("length = " + dataLength);

		// TODO : 구분 코드
		String code = "";
		for (int i = 0; i < CODE_LENGTH; i++) {
			code += (char) buf.readByte();
		}
		System.out.println("code = " + code);

		// TODO : DATA
		byte[] dataByte = new byte[dataLength];
		for (int i = 0; i < dataLength; i++) {
			dataByte[i] = buf.readByte();
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
		int size = buf.readableBytes();
		byte[] file = new byte[size];
		for (int i = 0; i < size; i++) {
			file[i] = buf.readByte();
		}

		// TODO : Client로 result 전송
		byte[] result = null;
		int resultLength = 0;

		File saveFile = new File(saveDir);
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(saveFile);
			fos.write(file);
			fos.close();

			byte[] resultLengthByte = String.valueOf(CODE_LENGTH + saveDir.length()).getBytes();
			byte[] resultData = saveDir.getBytes();
			resultLength = LENGTH + CODE_LENGTH + saveDir.length();
			result = new byte[resultLength];

			result[8] = 'O';
			result[9] = 'K';
			System.arraycopy(resultLengthByte, 0, result, LENGTH - resultLengthByte.length, resultLengthByte.length);
			System.arraycopy(resultData, 0, result, LENGTH + CODE_LENGTH, resultData.length);
		} catch (Exception e) {

			String exception = e.toString();
			String exceptionName = exception.split("\\(")[0];
			String description = exception.substring(exception.lastIndexOf('(') + 1, exception.length() - 1);

			byte[] resultLengthByte = String.valueOf(CODE_LENGTH + exceptionName.length() + description.getBytes().length).getBytes();
			byte[] resultData = (exceptionName + description).getBytes();
			resultLength = LENGTH + CODE_LENGTH + resultData.length;
			result = new byte[resultLength];

			result[8] = 'N';
			result[9] = 'O';
			System.arraycopy(resultLengthByte, 0, result, LENGTH - resultLengthByte.length, resultLengthByte.length);
			System.arraycopy(resultData, 0, result, LENGTH + CODE_LENGTH, resultData.length);
			e.printStackTrace();
		} finally {
			ByteBuf byteBuf = Unpooled.wrappedBuffer(result);
			System.out.println("byteBuf.toString() = " + byteBuf.toString());
			ctx.writeAndFlush(byteBuf);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server channelReadComplete!");
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("Server exceptionCaught!");
		cause.printStackTrace();
		ctx.close();
	}
}
