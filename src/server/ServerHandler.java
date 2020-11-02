package src.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.apache.commons.io.IOUtils;
import src.util.IntByteConvert;

import java.io.*;
import java.nio.ByteOrder;

public class ServerHandler extends ChannelHandlerAdapter {

    private final int LENGTH = 8;
    private final int CODE_LENGTH = 2;
    private final String CODE = "FI";

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Server channelRead!");

		// 바이트를 변환 시킴 ( length / 구분코드 / data / file )
		// length = 8 byte( 2(구분코드) + data의 길이)
		// 구분코드 = FI
		// data = '\n'으로 split후 구조 나눠서 진행
		// file = 바이트를 파일로 변환

		byte[] bytes = (byte[]) msg;

		// 첫 8 byte( 2(구분코드) + data의 길이)
		byte[] lengthByte = new byte[LENGTH];
		for (int i = 0; i < LENGTH; i++) {
			lengthByte[i] = bytes[i];
			System.out.printf("[%02X]", lengthByte[i]);
		}
		int dataLength = IntByteConvert.byteToInt(lengthByte, ByteOrder.LITTLE_ENDIAN);
		System.out.println(dataLength);

		// 구분 코드
		byte[] codeByte = new byte[CODE.length()];
		for (int i = LENGTH; i < LENGTH + CODE_LENGTH; i++) {
			codeByte[i-LENGTH] = bytes[i];
		}
		System.out.println("codeByte = " + codeByte);

		// data
		byte[] data = new byte[dataLength];
		for (int i = LENGTH + CODE_LENGTH; i < data.length; i++) {
			data[i-LENGTH-CODE_LENGTH] = bytes[i];
		}

		System.out.println("data = " + data.toString());

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
