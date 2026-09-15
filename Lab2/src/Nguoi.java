import java.time.LocalDate;


public class Nguoi {
	private String hoTen;
	private int namSinh;
	private String diaChi;
	
	
	public Nguoi() {
		super();
	}


	public Nguoi(String hoTen, int namSinh, String diaChi) {
		super();
		this.hoTen = hoTen;
		this.namSinh = namSinh;
		this.diaChi = diaChi;
	}


	public String getHoTen() {
		return hoTen;
	}


	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}


	public int getNamSinh() {
		return namSinh;
	}


	public void setNamSinh(int namSinh) {
		this.namSinh = namSinh;
	}


	public String getDiaChi() {
		return diaChi;
	}


	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}
	
	public int tinhTuoi() {
		return LocalDate.now().getYear() - namSinh;
	}
	
	public void hienThiThongTin() {
		System.out.println("Họ và Tên: "+ hoTen);
		System.out.println("Năm sinh: "+ namSinh);
		System.out.println("Địa chỉ: "+ diaChi);
		System.out.println("Tuổi: "+tinhTuoi());
	}
	
	
	
}



