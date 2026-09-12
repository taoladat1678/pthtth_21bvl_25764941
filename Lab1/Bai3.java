import java.util.Scanner;

public class Bai3 {
	
	static int tinhTong(int a, int b) {
		return a + b;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhap a: ");
		int a = sc.nextInt();
		System.out.println("Nhap b: ");
		int b = sc.nextInt();
		
		int c = tinhTong(a, b);
		System.out.println("Tong la: "+c);
		sc.close();
		

	}

}
