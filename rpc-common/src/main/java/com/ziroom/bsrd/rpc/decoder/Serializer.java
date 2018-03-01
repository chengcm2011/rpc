package com.ziroom.bsrd.rpc.decoder;

public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> Object deserialize(byte[] bytes, Class<T> clazz);

}
