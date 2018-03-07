package com.ziroom.bsrd.rpc.coder;

import com.ziroom.bsrd.rpc.serializer.HessianSerializer;
import com.ziroom.bsrd.rpc.serializer.Serializer;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;
    private Serializer serializer;

    public NettyEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
        serializer = new HessianSerializer();
    }

    public NettyEncoder(Class<RpcResponse> rpcResponseClass, Serializer serializer) {
        this.genericClass = rpcResponseClass;
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = serializer.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
