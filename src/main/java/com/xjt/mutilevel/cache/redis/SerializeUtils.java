package com.xjt.mutilevel.cache.redis;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * SerializeUtils.java
 *
 * @author jarlun
 * @version 1.0
 * @创建时间：2015年10月30日 上午11:54:22
 */
@Slf4j
public class SerializeUtils {

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    public static Object deserialize(byte[] bytes) {
        Object result = null;
        if (isEmpty(bytes)) {
            return null;
        }
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
            result = objectInputStream.readObject();
        } catch (Exception e) {
            log.warn("Failed to deserialize", e);
        }
        return result;
    }

    public static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }


    /**
     * 序列化
     *
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        byte[] result = null;
        if (object == null) {
            return new byte[0];
        }
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(SerializeUtils.class.getSimpleName()
                        + " requires a Serializable payload " +
                        "but received an object of type [" + object.getClass().getName() + "]");
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            result = byteStream.toByteArray();
        } catch (Exception e) {
            log.warn("Failed to serialize", e);
        }
        return result;
    }

}
