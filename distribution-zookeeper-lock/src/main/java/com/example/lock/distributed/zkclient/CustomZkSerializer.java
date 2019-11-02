package com.example.lock.distributed.zkclient;

import com.google.common.base.Preconditions;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.nio.charset.StandardCharsets;

/**
 * <p>自定义实现序列化接口 {@link ZkSerializer}</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-17 14:36
 */
public class CustomZkSerializer implements ZkSerializer {

    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError {
        Preconditions.checkNotNull(data, "【CustomZkSerializer】data不允许为null");
        String d = (String) data;
        return d.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        Preconditions.checkNotNull(bytes, "【CustomZkSerializer】data不允许为null");
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
