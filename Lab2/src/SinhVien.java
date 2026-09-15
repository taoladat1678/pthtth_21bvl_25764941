public class SinhVien extends Nguoi {
	private String maSinhVien;
	private String nganhHoc;
	private double diemTrungBinh;
	
	


	public SinhVien(String hoTen, int namSinh, String diaChi, String maSinhVien, String nganhHoc, double diemTrungBinh) {
		super(hoTen, namSinh, diaChi);
		this.maSinhVien = maSinhVien;
		this.nganhHoc = nganhHoc;
		this.diemTrungBinh = diemTrungBinh;
	}
	
	public String xepLoai(double n) {
	    if (n >= 8.5) return "Giỏi";
	    if (n >= 7.0) return "Khá";
	    if (n >= 5.0) return "Trung bình";
	    return "Quá yếu đuối";
	}

	@Override
	public void hienThiThongTin() {
		super.hienThiThongTin();
		System.out.println("Mã sinh viên: "+ maSinhVien);
		System.out.println("Ngành học: "+ nganhHoc);
		System.out.println("Điểm trung bình: "+ diemTrungBinh);
		System.out.println("Xếp loại: "+ xepLoai(diemTrungBinh));
		
	}
	

	
	
	
}
