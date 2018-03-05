package com.ziroom.bsrd.rpc.decoder;

import java.io.Serializable;

public interface Serializer extends Serializable {

    <T> byte[] serialize(T obj);

    <T> Object deserialize(byte[] bytes, Class<T> clazz);

}
