package src.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class DaouDecoder extends ByteToMessageDecoder {
    private final int LENGTH = 8;
    private final int CODE_LENGTH = 2;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        if(!in.isReadable() || in.readableBytes() < LENGTH)
            return;

        String lengthTemp = "";
        for (int i = 0; i < LENGTH; i++) {
            char nowByte = (char) in.readByte();
            if (nowByte != 0)
                lengthTemp += nowByte;
        }

        if(CODE_LENGTH > in.readableBytes()){
            in.resetReaderIndex();
            return;
        } else {
            in.skipBytes(2); // 구분 코드
        }

        int dataLength = Integer.parseInt(lengthTemp) - 2;
        if (dataLength > in.readableBytes()) {
            in.resetReaderIndex();
            return;
        }

        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < dataLength; i++) {
            sf.append((char)in.readByte());
        }

        int fileSize = Integer.parseInt(sf.toString().split("\n")[3].split("=")[1]);
        if(in.readableBytes() < fileSize) {
            in.resetReaderIndex();
            return;
        }

        byte[] msg;
        if(in.hasArray()) { // heap 버퍼일 경우
            msg = in.array();
        } else { // direct 버퍼일 경우(네티 4.1부터 기본 버퍼타입이 direct)
            msg = new byte[in.readableBytes()];
            in.getBytes(0, msg);
        }

        in.clear();
        out.add(msg);
    }
}
