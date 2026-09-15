public class MainNguoi {

	public static void main(String[] args) {
		SinhVien sv1 = new SinhVien("Phùng Thanh Độ", 2004, "120 yên lãng","SV01", "CNTT", 9.5);
		sv1.hienThiThongTin();
		System.out.println();
		GiangVien gv1 = new GiangVien("Phùng Thanh Độ", 1990, "36/67 Thanh Hóa","GVXXX", "CNTT", 5670000, 5.67);
		gv1.hienThiThongTin();
	}

}
