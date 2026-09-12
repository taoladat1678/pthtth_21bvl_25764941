import java.io.File;

public class Bai7 {
	
	static void checkAndDeleteFile(String source) {
		File file = new File(source);
		
		if (file.exists()) {
			file.delete();
		} else {
			System.out.println("ko thấy file");
		}
	}

	public static void main(String[] args) {
		
		String source = "C:\\Users\\hlua7\\OneDrive\\Desktop\\New Text Document (2).txt";
		checkAndDeleteFile(source);
		
		}

}
