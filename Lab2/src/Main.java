
public class Main {

	public static void main(String[] args) {
		System.out.println("Sản phẩm 1:");
		SanPham sp1 = new SanPham();
		sp1.setMaSP("IP18");
		sp1.setTenSP("Iporn 18 Pro Ultra Max");
		sp1.setSoLuong(100);
		sp1.setDonGia(100000);;
		sp1.hienThiThongTin();
		sp1.nhapHang(10);
		System.out.println("Số lượng khi thêm: "+ sp1.getSoLuong());
		sp1.banHang(20);
		System.out.println("Số lượng sau khi bán: "+ sp1.getSoLuong());
		
		System.out.println();
		
		System.out.println("Sản phẩm 2:");
		SanPham sp2 = new SanPham();
		sp2.setMaSP("IP19");
		sp2.setTenSP("Iporn 19 Pro Ultra Max");
		sp2.setSoLuong(200);
		sp2.setDonGia(100000);;
		sp2.hienThiThongTin();
		sp2.nhapHang(10);
		System.out.println("Số lượng khi thêm: "+ sp2.getSoLuong());
		sp2.banHang(20);
		System.out.println("Số lượng sau khi bán: "+ sp2.getSoLuong());
	}

}
