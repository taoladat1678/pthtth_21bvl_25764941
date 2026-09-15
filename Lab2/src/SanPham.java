public class SanPham {
	private String maSP;
    private String tenSP;
    private double donGia;
    private int soLuong;
	
	
 	public SanPham() {
		super();
	}
	

	
	public SanPham(String maSP, String tenSP, double donGia, int soLuong) {
		super();
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.donGia = donGia;
		this.soLuong = soLuong;
	}



	public String getMaSP() {
		return maSP;
	}
	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}
	public String getTenSP() {
		return tenSP;
	}
	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}
	public double getDonGia() {
		return donGia;
	}
	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	
	public double tinhThanhTien() {
		return this.donGia * this.soLuong;
	}
	
	public int nhapHang(int soLuongNhap) {
		if (soLuongNhap > 0) {
			this.soLuong += soLuongNhap;
		}
		return this.soLuong;
	}
	
	public boolean banHang(int soLuongBan) {
		if (soLuongBan > 0 && soLuongBan <= this.soLuong) {
			this.soLuong -= soLuongBan;
			return true;
		}
		return false;
	}
	
	public void hienThiThongTin() {
		System.out.println("Mã sản phẩm là: "+ this.maSP);
		System.out.println("Tên sản phẩm là: "+ this.tenSP);
		System.out.println("Số lượng: "+ this.soLuong);
		System.out.printf("Thành tiền: %.0f \n",this.tinhThanhTien());
	}
}
