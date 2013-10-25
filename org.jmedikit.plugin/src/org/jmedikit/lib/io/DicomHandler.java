package org.jmedikit.lib.io;



public class DicomHandler {
	
	/*public static final int UNSIGNED = 0;
	public static final int SIGNED = 1;
	
	public DicomHandler(String path) throws IOException{
		System.out.println(path);
		File f = new File(path);
		
		DicomInputStream dis = new DicomInputStream(f);
		DicomObject obj = dis.readDicomObject();
		
		double slope = obj.getDouble(Tag.RescaleSlope);
		double intercept = obj.getDouble(Tag.RescaleIntercept);
		
		System.out.println("("+obj.getString(Tag.BitsAllocated)+"," +
				obj.getString(Tag.BitsStored)+","+
				obj.getString(Tag.HighBit)+")"+
				"PixelRep = "+obj.getString(Tag.PixelRepresentation)+", Slope "+
				slope+", Intercept "+
				intercept);
		
		//J2KImageReader dir = (J2KImageReader) ImageIO.getImageReadersByFormatName("jpeg2000").next();
		DicomImageReader dir = (DicomImageReader) ImageIO.getImageReadersByFormatName("dicom").next();
		ImageInputStream iis = ImageIO.createImageInputStream(f);
		DicomImageReadParam param = (DicomImageReadParam) dir.getDefaultReadParam();
		//J2KImageReadParam param = (J2KImageReadParam) dir.getDefaultReadParam();
		dir.setInput(iis);
		System.out.println(obj.getString(Tag.NumberOfFrames));
		
		Raster r = dir.readRaster(0, param);
		//System.out.println(r.getHeight());
		int width = r.getWidth();
		int height = r.getHeight();
		
		System.out.println("Image "+width+" x "+height+", "+obj.getString(Tag.PixelRepresentation));
		
		BufferedImage bufferImg = dir.read(0, param);
		DataBuffer buffer = r.getDataBuffer();
		
		System.out.println(bufferImg.getType()+" "+ buffer.getClass().getName());
		
		PrintWriter pWriter = new PrintWriter(new FileWriter("Samples/test.txt"));
        
		VectorUInt32 idx = new VectorUInt32(3);

		Image img = new Image(width, height, PixelIDValueEnum.sitkUInt16);
		
		System.out.println(Short.MIN_VALUE+" - "+Short.MAX_VALUE);
		System.out.println(AbstractImage.unsignedShortToSignedShort(42000));
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				idx.set(0, x);
				idx.set(1, y);
				
				short pixel = AbstractImage.unsignedShortToSignedShort(buffer.getElem(y * width + x));
				int intPixel = buffer.getElem(y * width + x);

				int value = (int) (pixel * slope+intercept);
				img.setPixelAsUInt16(idx, value);
				int itkPixel = 0;
				itkPixel = img.getPixelAsUInt16(idx);
				pWriter.print("("+pixel+","+intPixel+","+value+")");
			}
			pWriter.println("");
		}
		pWriter.flush();
		
		ImageFileWriter fw = new ImageFileWriter();
		fw.setFileName("Samples/out.dcm");
		fw.execute(img);
	}*/
}
