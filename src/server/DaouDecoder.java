package src.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class DaouDecoder extends ByteToMessageDecoder {
    private final int LENGTH = 8;
    private final int CODE_LENGTH = 2;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        if(!in.isReadable())
            return;

        String lengthTemp = "";
        for (int i = 0; i < LENGTH; i++) {
            if(in.getByte(i) != 0)
                lengthTemp += (char)in.getByte(i);
        }

        int dataLength = Integer.parseInt(lengthTemp) - 2;

        if(dataLength > in.readableBytes())
            return;
        StringBuffer sf = new StringBuffer();
        for (int i = LENGTH + CODE_LENGTH; i < LENGTH + CODE_LENGTH + dataLength; i++) {
            sf.append((char)in.getByte(i));
        }

        String[] arrD = sf.toString().split("=");
        int fileSize = Integer.parseInt(arrD[4].split("\n")[0]);
        if((in.readableBytes() < LENGTH + CODE_LENGTH + dataLength + fileSize)) {
            in.resetReaderIndex();
            return;
        }

        byte[] file;

        if(in.hasArray()) { // heap 버퍼일 경우
            file = in.array();
        } else { // direct 버퍼일 경우(네티 4.1부터 기본 버퍼타입이 direct)
            file = new byte[in.readableBytes()];
            in.getBytes(in.readerIndex(), file);
        }

        in.clear();
        out.add(file);
    }
}
