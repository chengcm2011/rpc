package com.ziroom.bsrd.rpc.coder;

import com.ziroom.bsrd.rpc.serializer.HessianSerializer;
import com.ziroom.bsrd.rpc.serializer.Serializer;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;
    private Serializer serializer;

    public NettyDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
        this.serializer = new HessianSerializer();
    }

    public NettyDecoder(Class<RpcRequest> rpcRequestClass, Serializer serializer) {
        this.genericClass = rpcRequestClass;
        this.serializer = serializer;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;    // fix 1024k buffer splice limix
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = serializer.deserialize(data, genericClass);
        out.add(obj);
    }
}
