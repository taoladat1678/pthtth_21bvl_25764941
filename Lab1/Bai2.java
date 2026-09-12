import java.util.Scanner;

public class Bai2 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Type your name:");
		String myName = sc.nextLine();
		System.out.println("Hi, I'm "+ myName);
		sc.close();
	}

}
