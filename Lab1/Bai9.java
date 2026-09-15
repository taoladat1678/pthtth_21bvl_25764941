import java.io.File;

public class Bai9 {

	public static void finFile(String source, String key) {
		File file = new File(source);
		if (file.exists()) {
			if (file.isFile()) {
				if (file.getName().endsWith(key)) {
					System.out.println(file.getAbsolutePath());
				}
			}
			File[] listFile = file.listFiles();
			if (listFile != null) {
				for (File f : listFile) {
					finFile(f.getAbsolutePath(), key);
				}
			}
		} else {
			System.out.println("source không tồn tại");
		}
	}

	public static void main(String[] args) {
		String source = "D:\\HocJava";
		String key = ".txt";
		finFile(source, key);
	}
}
