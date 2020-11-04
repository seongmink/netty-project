package src.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class DaouDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if(!in.isReadable())
            return;
        in.markReaderIndex();
        String length = "";
        for (int i = 0; i < 8; i++) {
            if(in.getByte(i) != 0)
                length += (char)in.getByte(i);
        }
        int len = Integer.parseInt(length) - 2;
        if(len > in.readableBytes())
            return;
        String data = "";
        for (int i = 10; i < len + 10; i++) {
            data += (char)in.getByte(i);
        }
//        System.out.println("data = " + data);

        String[] arrD = data.split("=");
        int fileSize = Integer.parseInt(arrD[4].split("\n")[0]);
//        System.out.println("in.readableBytes() = " + in.readableBytes());
//        System.out.println("in.readableBytes() = " + in.readableBytes());
//        System.out.println("fileSize = " + (fileSize + len + 10));
        if((in.readableBytes() < fileSize + len + 10)) {
            in.resetReaderIndex();
            return;
        }

        byte[] msg = new byte[fileSize + len + 10];
        for (int i = 0; i < fileSize + len + 10; i++) {
            msg[i] = in.getByte(i);
        }
        in.clear();
//        in.readerIndex(in.readerIndex() + in.readableBytes());
        out.add(msg);
    }
}
