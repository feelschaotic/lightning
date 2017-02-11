package com.ramo.bt.torrent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * BCode，B编码类，根据编码规则而编写的关于B编码的编码和解码算法，
 * 
 */

public class BCode {
	private BCode() {
	}
	public static Object BDecode(byte abyte0[]) {
		ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(
				abyte0);
		Object obj = BDecodeRecursive(bytearrayinputstream);
		if (bytearrayinputstream.read() != -1)
			throw new IllegalArgumentException("不合法的字节流");
		else
			return obj;
	}

	public static byte[] BEncode(Object obj) {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		BEncodeRecursive(obj, bytearrayoutputstream);
		return bytearrayoutputstream.toByteArray();
	}

	//静态的BDecodeRecursive方法，递归的将一个B编码的字节数组输入流按编成规则进行解码
	static Object BDecodeRecursive(ByteArrayInputStream bytearrayinputstream) {
		//对输入流进行判断，是否支mark/reset，如果不支持就抛出异常
		if (!bytearrayinputstream.markSupported())
			throw new IllegalArgumentException(bytearrayinputstream.getClass()+ " 字节输入流出错");
        //按参数标识的位置，对bytearrayinputstream数据流进行标记
		bytearrayinputstream.mark(2);
		//读取一个字节，存储到i到中
		int i = bytearrayinputstream.read();
		//调用reset方法，将缓冲区的位置重置为标记位置
		bytearrayinputstream.reset();
		//对读取的字符进行判断
		switch (i) {
		//整数105，表示的是字符'i'的ASCII码
		case 105: 
		    //如果此字符以i开头，则调用解码整数数据类型的方法
			return decodeInt(bytearrayinputstream);
        //整数108，表示字符'l'的ASCII码
		case 108: 
		    //如果此字符以l开头，则调用解码List数据类型的方法 
			return decodeList(bytearrayinputstream);
        //整数100，表示字符'd'的ASCII码
		case 100: 
			//如果此字符以d开头，则调用解码字典数据类型的方法 
			return decodeMap(bytearrayinputstream);
		}
		//调用decodeString方法，解码字符串数据
		return decodeString(bytearrayinputstream);
	}
    //静态的BEncodeRecursive方法，递归的将一个数据对象，编码成B编码规则的数据结构
	static void BEncodeRecursive(Object obj,
			ByteArrayOutputStream bytearrayoutputstream) {
		//对数据对象进行判断，属于何种类型的数据，对其中的整数类型数据进行处理
		if ((obj instanceof Number) && !(obj instanceof Float)
				&& !(obj instanceof Double) && !(obj instanceof BigDecimal)) {
			//整个105表示的字符'i'的意思，在字节流中写入字符'i'
			bytearrayoutputstream.write(105);
			//将要写入的数据对象转换成字节数组
			byte abyte0[] = obj.toString().getBytes();
			//将数据内容，写入到bytearrayoutputstream流中
			bytearrayoutputstream.write(abyte0, 0, abyte0.length);
			//写入完成后加入结束标记'e',101是字符'e'的ASCII码
			bytearrayoutputstream.write(101);
		//对字符串类型的数据进行编码
		} else if (obj instanceof String)
			//递归调用
			BEncodeRecursive(((String) obj).getBytes(), bytearrayoutputstream);
		//对字节数组进行编码
		else if (obj instanceof byte[]) {
			//将数据对象强制转换
			byte abyte1[] = (byte[]) obj;
			//取得数据长度
			byte abyte2[] = Integer.toString(abyte1.length).getBytes();
			//写入表示长度的数据
			bytearrayoutputstream.write(abyte2, 0, abyte2.length);
			//写入冒号字符，字符':'的ASCII码是58
			bytearrayoutputstream.write(58);
			//后面接着写入要编码的数据内容
			bytearrayoutputstream.write(abyte1, 0, abyte1.length);
		//对List类型的数据进行编码
		} else if (obj instanceof List) {
			//写入List类型数据编码的开始字符‘l’
			bytearrayoutputstream.write(108);
			//对List中的数据进行遍历，并编码
			for (Iterator iterator = ((List) obj).iterator(); iterator
					.hasNext(); BEncodeRecursive(iterator.next(),
					bytearrayoutputstream));
			//写入完成后加入结束标记'e',101是字符'e'的ASCII码
			bytearrayoutputstream.write(101);
		//对字典类型的数据进行编码
		} else if (obj instanceof Map) {
			//写入字典类型数据编码的开始标记符'd'
			bytearrayoutputstream.write(100);
			//将字典类型（Map）类型中所有的Key值取出，放到一个字符串数组里
			String as[] = (String[]) ((Map) obj).keySet()
					.toArray(new String[0]);
			//对Key值进行排序
			Arrays.sort(as);
			int i = 0;
			//递归调用BEncodeRecursive方法，对字典类型数据进行编码
			for (int j = as.length; i < j; i++) {
				BEncodeRecursive(as[i], bytearrayoutputstream);
				BEncodeRecursive(((Map) obj).get(as[i]), bytearrayoutputstream);
			}
            //一条数据编码结束后加入结束标记'e',101是字符'e'的ASCII码
			bytearrayoutputstream.write(101);
			//捕获并处理异常
		} else {
			throw new IllegalArgumentException("错误: "+ obj.getClass());
		}
	}


    //对整数类型的数据进行解码
	static Number decodeInt(ByteArrayInputStream bytearrayinputstream) {
		int i = bytearrayinputstream.read();
        //判断整数类型的数据是否是以字符'i'开始
		if (i != 105)
			throw new IllegalArgumentException("整数编码错误");
		String s;
		//读取字符串，直到结束标记'e'出现，或是文件读取结束
		for (s = ""; (i = bytearrayinputstream.read()) != 101 && i != -1; s = s	+ (char) i);
		if (i == -1)
			//如果没出现结束标记'e'而文件结束，证明文件不完整，非正常结束
			throw new IllegalArgumentException("非正常结束");
		if (s.length() == 0)
			//如果编码内容的长度为0，说明整数编码内容为空
			throw new IllegalArgumentException("整数编码内容为空");
		else
			return new Long(s);
	}
     //对列表（List）类型的数据进行解码
	static List decodeList(ByteArrayInputStream bytearrayinputstream) {
		int i = bytearrayinputstream.read();
        //判断列表类型的数据是否是以字符'l'开始，否则抛出错误
		if (i != 108)
			throw new IllegalArgumentException("列表编码错误");
        //新建一个ArrayList对象
		ArrayList arraylist = new ArrayList();
		//对数据应该按参数设定的值进行标记
		bytearrayinputstream.mark(2);
		//遍历并读取数据流中的数据
		while ((i = bytearrayinputstream.read()) != 101 && i != -1) {
			bytearrayinputstream.reset();
			//调用BDecodeRecursive方法，递归的对列表数据编码
			arraylist.add(BDecodeRecursive(bytearrayinputstream));
			bytearrayinputstream.mark(2);
		}
		//判断文件是否正常结束
		if (i == -1)
			throw new IllegalArgumentException("列表内容非正常结束");
		else
			return arraylist;
	}
    //对字典（Map）类型的数据进行解码
	static Map decodeMap(ByteArrayInputStream bytearrayinputstream) {
		int i = bytearrayinputstream.read();
        //判断字典类型的数据是否是以字符'd'开始，否则抛出错误
		if (i != 100)
			throw new IllegalArgumentException("字典编码错误");
		//新建一个Hashtable对象
		Hashtable hashtable = new Hashtable();
		String s = null;
		bytearrayinputstream.mark(2);
		//遍历并读取数据流中的数据
		while ((i = bytearrayinputstream.read()) != 101 && i != -1) {
			bytearrayinputstream.reset();
            //对字典内部的字符串数据还需要进行编码
			String s1 = new String(decodeString(bytearrayinputstream));
			if (s != null && s.compareTo(s1) >= 0)
				throw new IllegalArgumentException("错误");
			//调用BDecodeRecursive方法，递归的对字典型数据编码
			Object obj = BDecodeRecursive(bytearrayinputstream);
			//将编码后的数据，存入到Hashtable中
			hashtable.put(s1, obj);
			bytearrayinputstream.mark(2);
		}
		//判断文件是否正常结束
		if (i == -1)
			throw new IllegalArgumentException("字典编码非正常结束");
		else
			return hashtable;
	}
     //对字符串（String）类型的数据进行解码，返回字节数组
	static byte[] decodeString(ByteArrayInputStream bytearrayinputstream) {
		int i;
		String s;
        //遍历字符串，直到读到冒号“：”、或文件结束
		for (s = ""; (i = bytearrayinputstream.read()) != 58 && i != -1; s = s + (char) i);
		//如果没有出现冒号，证明编码不符合规则
		if (i == -1)
			throw new IllegalArgumentException("字符串解析错误");
		//如查字符串长度为0，说明编码内容不存在
		if (s.length() == 0)
			throw new IllegalArgumentException("字符串内容不存在");
		//将字符串类型的数据转换成整型
		int j = Integer.parseInt(s);
		byte abyte0[] = new byte[j];
		if (bytearrayinputstream.read(abyte0, 0, j) != j)
			throw new IllegalArgumentException("");
		else
			return abyte0;
	}
}