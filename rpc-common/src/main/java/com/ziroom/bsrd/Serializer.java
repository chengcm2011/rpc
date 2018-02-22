package com.ziroom.bsrd;


/**
 * 序列化器
 * Tips：模板方法模式：
 * 定义：定义一个操作中算法的骨架（或称为顶级逻辑），将一些步骤（或称为基本方法）的执行延迟到其子类中；
 * 基本方法：抽象方法 + 具体方法final + 钩子方法；
 *
 * @author xuxueli 2015-10-30 21:02:55
 */
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> Object deserialize(byte[] bytes, Class<T> clazz);

//	public enum SerializeEnum {
//		HESSIAN(new HessianSerializer()),
//		JSON(new JacksonSerializer());
//
//		public final Serializer serializer;
//		private SerializeEnum (Serializer serializer) {
//			this.serializer = serializer;
//		}
//		public static SerializeEnum match(String name, SerializeEnum defaultSerializer){
//			for (SerializeEnum item : SerializeEnum.values()) {
//				if (item.name().equals(name)) {
//					return item;
//				}
//			}
//			return defaultSerializer;
//		}
//	}

//	public static void main(String[] args) {
//		Serializer serializer = SerializeEnum.match("HESSIAN", null).serializer;
//		System.out.println(serializer);
//		try {
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("aaa", "111");
//			map.put("bbb", "222");
//			System.out.println(serializer.deserialize(serializer.serialize("ddddddd"), String.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
