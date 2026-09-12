import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Bai13 {

	// Ghi file ảnh
	public static void saveFile(File path, String tfile, byte[] bfile) {
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bfile));
			ImageIO.write(img, tfile, path);
			System.out.println("Luu file anh thanh cong: " + path.getAbsolutePath());
		} catch (IOException ex) {
			Logger.getLogger(Bai13.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// Đọc file ảnh
	public static byte[] readFile(File path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			byte[] buf = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
			fis.close();
			return bos.toByteArray();
		} catch (IOException ex) {
			Logger.getLogger(Bai13.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static void main(String[] args) {
		File sourceImage = new File("D:\\image_input.png");
		File destImage = new File("D:\\image_output.png");

		if (sourceImage.exists()) {
			byte[] data = readFile(sourceImage);
			if (data != null) {
				saveFile(destImage, "png", data);
			}
		} else {
			System.out.println("Khong tim thay file anh: " + sourceImage.getAbsolutePath());
		}
	}
}
