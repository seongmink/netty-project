package src.server;

import io.netty.channel.*;

import java.util.Arrays;
import java.util.StringTokenizer;

public class ServerHandler extends ChannelHandlerAdapter {

    private final int LENGTH = 8;
    private final int CODE_LENGTH = 2;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Server channelRead!");

		// TODO : 목표
		// 바이트를 변환 시킴 ( length / 구분코드 / data / file )
		// length = 8 byte( 2(구분코드) + data의 길이)
		// 구분코드 = FI
		// data = '\n'으로 split후 구조 나눠서 진행
		// file = 바이트를 파일로 변환

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
		byte[] dataByte = new byte[length];
		for (int i = LENGTH + CODE_LENGTH; i < LENGTH + CODE_LENGTH + length; i++) {
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

		// TODO : 파일 저장하는 로직

		byte[] file = new byte[Integer.parseInt(fileSize)];
		for (int i = LENGTH + CODE_LENGTH + length; i < bytes.length; i++) {
			file[i-LENGTH-CODE_LENGTH-length] = bytes[i];
		}

//
//		System.out.println("data = " + data.toString());
//
//        ByteBuf in = (ByteBuf) msg;
//        byte[] b = new byte[in.readableBytes()];
//        in.getBytes(0, b, 0, in.readableBytes());
//        String s = new String(b);
//        //System.out.println("Server received: " + ByteBufUtil.hexDump(in));
//        System.out.println("Server received: " + s);
//        ctx.write(in);

//        ByteBuf buf = (ByteBuf) msg;
//
//        int length = buf.readableBytes();
//        System.out.println("length = " + length);
//        byte[] read = new byte[length];
//
//        for (int i = 0; i < read.length; i++) {
//            read[i] = buf.getByte(i);
//        }
//
//        ByteArrayInputStream bis = new ByteArrayInputStream(read);
//        ObjectInputStream ois = new ObjectInputStream(bis);
//        System.out.println(ois.toString());

//        int totalLength = buf.readableBytes();
//
//        byte[] length = new byte[8];
//        for (int i = 0; i < length.length; i++) {
//            length[i] = buf.getByte(i);
//        }
//
//        byte[] code = new byte[2];
//        for (int i = length.length; i < length.length + code.length; i++) {
//            code[i] = buf.getByte(i);
//        }
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server channelReadComplete!");
		//ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("Server exceptionCaught!");
		cause.printStackTrace();
		ctx.close();
	}
}
