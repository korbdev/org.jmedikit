package org.jmedikit.lib.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class ConstantHelper {
	public static void printBufferedImageConstants(){
		System.out.println("-BufferedImageConstants---------------------");
		System.out.println("TYPE_3BYTE_BGR  = "+BufferedImage.TYPE_3BYTE_BGR);
		System.out.println("TYPE_4BYTE_ABGR  = "+BufferedImage.TYPE_4BYTE_ABGR);
		System.out.println("TYPE_4BYTE_ABGR_PRE  = "+BufferedImage.TYPE_4BYTE_ABGR_PRE);
		System.out.println("TYPE_BYTE_BINARY  = "+BufferedImage.TYPE_BYTE_BINARY);
		System.out.println("TYPE_BYTE_GRAY  = "+BufferedImage.TYPE_BYTE_GRAY);
		System.out.println("TYPE_BYTE_INDEXED  = "+BufferedImage.TYPE_BYTE_INDEXED);
		System.out.println("TYPE_CUSTOM  = "+BufferedImage.TYPE_CUSTOM);
		System.out.println("TYPE_INT_ARGB  = "+BufferedImage.TYPE_INT_ARGB);
		System.out.println("TYPE_INT_ARGB_PRE  = "+BufferedImage.TYPE_INT_ARGB_PRE);
		System.out.println("TYPE_INT_BGR  = "+BufferedImage.TYPE_INT_BGR);
		System.out.println("TYPE_INT_RGB  = "+BufferedImage.TYPE_INT_RGB);
		System.out.println("TYPE_USHORT_555_RGB  = "+BufferedImage.TYPE_USHORT_555_RGB);
		System.out.println("TYPE_USHORT_565_RGB  = "+BufferedImage.TYPE_USHORT_565_RGB);
		System.out.println("TYPE_USHORT_GRAY  = "+BufferedImage.TYPE_USHORT_GRAY);
		System.out.println("-------------------------------------------");
	}
	
	public static void printDataBufferConstants(){
		System.out.println("--DataBufferConstants----------------------");
		System.out.println("TYPE_BYTE  = "+DataBuffer.TYPE_BYTE);
		System.out.println("TYPE_SHORT  = "+DataBuffer.TYPE_SHORT);
		System.out.println("TYPE_USHORT  = "+DataBuffer.TYPE_USHORT);
		System.out.println("TYPE_INT  = "+DataBuffer.TYPE_INT);
		System.out.println("TYPE_FLOAT  = "+DataBuffer.TYPE_FLOAT);
		System.out.println("TYPE_UNDEFINED  = "+DataBuffer.TYPE_UNDEFINED);
		System.out.println("TYPE_DOUBLE  = "+DataBuffer.TYPE_DOUBLE);
		System.out.println("-------------------------------------------");
	}
}
