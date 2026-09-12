import java.util.Scanner;

public class Bai4 {
	
	static String checkChanLe(int a) {
		if (a % 2 == 0) {
			return "Chan";
		} else {
			return "Le";
		}
		
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhap so muon kiem tra");
		int a = sc.nextInt();
		String result = checkChanLe(a);
		System.out.println(result);
		sc.close();
	}

}
