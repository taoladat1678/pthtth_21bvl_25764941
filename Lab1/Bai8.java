import java.io.File;
import java.io.IOException;

public class Bai8 {

	// TH1: Delete thư mục rỗng
	public static boolean deleteEmptyFolder(String source) {
		File folder = new File(source);
		// Kiểm tra nếu folder tồn tại thì xóa
		if (folder.exists()) {
			folder.delete();
			System.out.println("folder ton tai\n xoa folder thanh cong");
			return true;
		} else {
			System.out.println("folder khong ton tai");
			return false;
		}
	}

	// TH2: Delete thư mục chứa Files (không có thư mục con)
	public static boolean deleteListFileInfolder(String source) {
		File folder = new File(source);
		// folder tồn tại
		if (folder.exists()) {
			// danh sách file
			File[] listFile = folder.listFiles();
			if (listFile != null && listFile.length != 0) {
				for (File f : listFile) {
					// file thì xóa
					if (f.isFile()) {
						f.delete();
					}
				}
			}
			folder.delete();
			System.out.println("Delete folder thành công!");
			return true;
		} else {
			System.out.println("folder không tồn tại");
			return false;
		}
	}

	// TH3: Delete thư mục chứa danh sách thư mục con và trong thư mục con chứa danh sách file (đệ quy)
	public static boolean deleteDirRecursive(String source) throws IOException {
		File folder = new File(source);
		// folder tồn tại
		if (folder.exists()) {
			// danh sách file
			File[] listFile = folder.listFiles();
			if (listFile != null && listFile.length != 0) {
				for (File f : listFile) {
					// nếu là file thì delete
					if (f.isFile()) {
						f.delete();
					}
					// nếu là thư mục thì gọi đệ quy lại
					if (f.isDirectory()) {
						deleteDirRecursive(f.getAbsolutePath());
					}
				}
			}
			folder.delete();
			System.out.println("Delete folder thành công!");
			return true;
		} else {
			System.out.println("folder không tồn tại");
			return false;
		}
	}

	public static void main(String[] args) throws IOException {
		String path = "D:\\HocJava\\TestDeleteDir";
		deleteDirRecursive(path);
	}
}
