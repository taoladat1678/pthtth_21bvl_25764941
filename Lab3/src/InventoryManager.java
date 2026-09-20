import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private static Path getDataDirectory() {
        if (Files.exists(Path.of("data"))) {
            return Path.of("data");
        } else if (Files.exists(Path.of("..", "data"))) {
            return Path.of("..", "data");
        }
        return Path.of("data");
    }

    /**
     * Ghi danh sách sản phẩm ra tệp CSV sử dụng UTF-8 và try-with-resources.
     */
    public static void saveToCsv(Path filePath, List<Product> products) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                writer.write("ma,ten,donGia,soLuong");
                writer.newLine();
                for (Product p : products) {
                    writer.write(p.toCsvLine());
                    writer.newLine();
                }
            }
            System.out.println("[Thành công] Đã lưu " + products.size() + " sản phẩm vào tệp: " + filePath);
        } catch (IOException e) {
            System.err.println("[Lỗi ghi tệp] Không thể ghi vào tệp " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Đọc tệp CSV và tái tạo danh sách đối tượng Product.
     * Xử lý ngoại lệ tệp không tồn tại, dòng thiếu cột, dữ liệu sai định dạng.
     */
    public static List<Product> readFromCsv(Path filePath) {
        List<Product> products = new ArrayList<>();
        if (!Files.exists(filePath)) {
            System.err.println("[Lỗi] Không tìm thấy tệp dữ liệu: " + filePath.toAbsolutePath());
            return products;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String header = reader.readLine(); // Đọc bỏ qua dòng tiêu đề
            if (header == null) {
                System.out.println("[Cảnh báo] Tệp " + filePath + " rỗng.");
                return products;
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue; // Bỏ qua dòng trống
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    System.err.println("[Lỗi dòng " + lineNumber + "] Tệp " + filePath.getFileName()
                            + ": Dòng thiếu hoặc thừa cột (yêu cầu 4 cột, nhận được " + parts.length
                            + "). Nội dung: \"" + line + "\"");
                    continue;
                }

                String code = parts[0].trim();
                String name = parts[1].trim();
                double price;
                int quantity;

                try {
                    price = Double.parseDouble(parts[2].trim());
                } catch (NumberFormatException e) {
                    System.err
                            .println("[Lỗi dòng " + lineNumber + "] Đơn giá không hợp lệ: \"" + parts[2].trim() + "\"");
                    continue;
                }

                try {
                    quantity = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    System.err.println(
                            "[Lỗi dòng " + lineNumber + "] Số lượng không hợp lệ: \"" + parts[3].trim() + "\"");
                    continue;
                }

                try {
                    Product product = new Product(code, name, price, quantity);
                    products.add(product);
                } catch (IllegalArgumentException e) {
                    System.err
                            .println("[Lỗi dòng " + lineNumber + "] Dữ liệu sản phẩm không hợp lệ: " + e.getMessage());
                }
            }
        } catch (NoSuchFileException e) {
            System.err.println("[Lỗi] Không tìm thấy tệp: " + filePath);
        } catch (IOException e) {
            System.err.println("[Lỗi đọc tệp] Không thể đọc tệp " + filePath + ": " + e.getMessage());
        }

        return products;
    }

    /**
     * Hiển thị toàn bộ sản phẩm và trả về tổng giá trị tồn kho.
     */
    public static double displayProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Danh sách sản phẩm trống.");
            return 0;
        }

        System.out.println("\n" + "=".repeat(85));
        System.out.println(String.format("%-4s | %-8s | %-25s | %-16s | %-6s | %-18s",
                "STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "SL", "Giá trị tồn kho"));
        System.out.println("-".repeat(85));

        double total = 0;
        int index = 1;
        for (Product p : products) {
            System.out.println(String.format("%-4d | %-8s | %-25s | %,12.0f VND | %,6d | %,14.0f VND",
                    index++, p.getCode(), p.getName(), p.getUnitPrice(), p.getQuantity(), p.inventoryValue()));
            total += p.inventoryValue();
        }
        System.out.println("-".repeat(85));
        System.out.printf("TỔNG GIÁ TRỊ TỒN KHO: %,.0f VND (Tổng số mặt hàng: %d)%n", total, products.size());
        System.out.println("=".repeat(85) + "\n");
        return total;
    }

    /**
     * Tìm sản phẩm có giá trị tồn kho cao nhất.
     */
    public static Product findHighestValueProduct(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return null;
        }
        Product maxProduct = products.get(0);
        for (Product p : products) {
            if (p.inventoryValue() > maxProduct.inventoryValue()) {
                maxProduct = p;
            }
        }
        return maxProduct;
    }

    /**
     * Ghi báo cáo tổng hợp vào tệp inventory-report.txt bằng UTF-8.
     */
    public static void writeReport(Path reportPath, List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("[Cảnh báo] Không có dữ liệu để xuất báo cáo.");
            return;
        }

        double totalValue = 0;
        for (Product p : products) {
            totalValue += p.inventoryValue();
        }
        Product maxProduct = findHighestValueProduct(products);

        try {
            if (reportPath.getParent() != null) {
                Files.createDirectories(reportPath.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(reportPath, StandardCharsets.UTF_8)) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                writer.write("=================================================================================");
                writer.newLine();
                writer.write("                       BÁO CÁO TỔNG HỢP TỒN KHO");
                writer.newLine();
                writer.write("               Thời gian xuất báo cáo: " + dtf.format(LocalDateTime.now()));
                writer.newLine();
                writer.write("=================================================================================");
                writer.newLine();
                writer.newLine();

                writer.write(String.format("1. Tổng số mặt hàng tồn kho : %d sản phẩm%n", products.size()));
                writer.write(String.format("2. Tổng giá trị tồn kho     : %,.0f VND%n", totalValue));
                writer.newLine();

                if (maxProduct != null) {
                    writer.write("3. SẢN PHẨM CÓ GIÁ TRỊ TỒN KHO CAO NHẤT:");
                    writer.newLine();
                    writer.write(String.format("   - Mã sản phẩm       : %s%n", maxProduct.getCode()));
                    writer.write(String.format("   - Tên sản phẩm      : %s%n", maxProduct.getName()));
                    writer.write(String.format("   - Đơn giá           : %,.0f VND%n", maxProduct.getUnitPrice()));
                    writer.write(String.format("   - Số lượng tồn      : %,d%n", maxProduct.getQuantity()));
                    writer.write(String.format("   - Giá trị tồn kho   : %,.0f VND%n", maxProduct.inventoryValue()));
                }
                writer.newLine();

                writer.write("4. CHI TIẾT DANH SÁCH TỒN KHO:");
                writer.newLine();
                writer.write(String.format("%-4s | %-8s | %-25s | %-16s | %-6s | %-18s%n",
                        "STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "SL", "Giá trị tồn kho"));
                writer.write("-".repeat(85));
                writer.newLine();

                int idx = 1;
                for (Product p : products) {
                    writer.write(String.format("%-4d | %-8s | %-25s | %,12.0f VND | %,6d | %,14.0f VND%n",
                            idx++, p.getCode(), p.getName(), p.getUnitPrice(), p.getQuantity(), p.inventoryValue()));
                }
                writer.write("=================================================================================");
                writer.newLine();
            }
            System.out.println("[Thành công] Đã xuất báo cáo tổng hợp vào tệp: " + reportPath);
        } catch (IOException e) {
            System.err.println("[Lỗi ghi báo cáo] " + e.getMessage());
        }
    }

    /**
     * Nhập danh sách sản phẩm từ bàn phím với kiểm tra ràng buộc dữ liệu.
     */
    public static List<Product> inputProductsFromConsole(BufferedReader reader) throws IOException {
        List<Product> list = new ArrayList<>();
        System.out.println("\n--- BẮT ĐẦU NHẬP SẢN PHẨM TỪ BÀN PHÍM ---");
        int index = 1;
        while (true) {
            System.out.printf("%n[Nhập sản phẩm thứ %d]%n", index);

            // 1. Nhập mã sản phẩm
            String code;
            while (true) {
                System.out.print("Nhập mã sản phẩm (hoặc gõ 'exit' / để trống để dừng): ");
                code = reader.readLine();
                if (code == null || code.isBlank() || code.equalsIgnoreCase("exit")) {
                    System.out.println("Kết thúc nhập liệu.");
                    return list;
                }
                code = code.trim();
                break;
            }

            // 2. Nhập tên sản phẩm
            String name;
            while (true) {
                System.out.print("Nhập tên sản phẩm: ");
                name = reader.readLine();
                if (name == null || name.isBlank()) {
                    System.out.println("[Lỗi] Tên sản phẩm không được để trống! Vui lòng nhập lại.");
                    continue;
                }
                name = name.trim();
                break;
            }

            // 3. Nhập đơn giá
            double unitPrice;
            while (true) {
                System.out.print("Nhập đơn giá (> 0): ");
                String priceStr = reader.readLine();
                try {
                    unitPrice = Double.parseDouble(priceStr != null ? priceStr.trim() : "0");
                    if (unitPrice <= 0) {
                        System.out.println("[Lỗi] Đơn giá phải lớn hơn 0! Vui lòng nhập lại.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("[Lỗi] Đơn giá phải là số hợp lệ! Vui lòng nhập lại.");
                }
            }

            // 4. Nhập số lượng
            int quantity;
            while (true) {
                System.out.print("Nhập số lượng tồn kho (>= 0): ");
                String qtyStr = reader.readLine();
                try {
                    quantity = Integer.parseInt(qtyStr != null ? qtyStr.trim() : "-1");
                    if (quantity < 0) {
                        System.out.println("[Lỗi] Số lượng không được âm! Vui lòng nhập lại.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("[Lỗi] Số lượng phải là số nguyên hợp lệ! Vui lòng nhập lại.");
                }
            }

            try {
                Product product = new Product(code, name, unitPrice, quantity);
                list.add(product);
                System.out.println("[✓] Đã thêm sản phẩm: " + product.getName());
                index++;
            } catch (IllegalArgumentException e) {
                System.out.println("[Lỗi] " + e.getMessage());
            }

            System.out.print("Bạn có muốn nhập tiếp không? (y/n): ");
            String cont = reader.readLine();
            if (cont == null || !cont.trim().equalsIgnoreCase("y")) {
                break;
            }
        }
        return list;
    }

    /**
     * Chạy tự động các kịch bản kiểm thử theo mục 9.3 của đề bài.
     */
    public static void runTestCases(Path dataDir) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("        CHẠY BỘ KIỂM THỬ TỐI THIỂU (MỤC 9.3)");
        System.out.println("=".repeat(60));

        // Test 1: Hợp lệ & Tiếng Việt
        System.out.println("\n[TEST 1] Dữ liệu hợp lệ & Tiếng Việt (Bàn phím cơ, Chuột không dây, Màn hình Dell)");
        List<Product> validList = new ArrayList<>();
        validList.add(new Product("SP01", "Bàn phím cơ RGB", 1200000, 15));
        validList.add(new Product("SP02", "Chuột không dây Logitech", 450000, 30));
        validList.add(new Product("SP03", "Màn hình Dell 24 inch", 3800000, 8));

        Path csvTest = dataDir.resolve("inventory.csv");
        saveToCsv(csvTest, validList);
        List<Product> readList = readFromCsv(csvTest);
        displayProducts(readList);
        Product maxP = findHighestValueProduct(readList);
        if (maxP != null) {
            System.out.printf("-> Sản phẩm có giá trị tồn kho cao nhất: %s (%s) = %,.0f VND%n",
                    maxP.getName(), maxP.getCode(), maxP.inventoryValue());
        }
        Path reportTest = dataDir.resolve("inventory-report.txt");
        writeReport(reportTest, readList);

        // Test 2: Mã rỗng
        System.out.println("\n[TEST 2] Thử tạo sản phẩm với Mã rỗng:");
        try {
            new Product("", "Chuột Gaming", 500000, 10);
        } catch (IllegalArgumentException e) {
            System.out.println("-> [Đạt yêu cầu] Bị từ chối chính xác: " + e.getMessage());
        }

        // Test 3: Giá âm
        System.out.println("\n[TEST 3] Thử tạo sản phẩm với Đơn giá âm (-1000):");
        try {
            new Product("SP99", "Tai nghe Bluetooth", -1000, 5);
        } catch (IllegalArgumentException e) {
            System.out.println("-> [Đạt yêu cầu] Bị từ chối chính xác: " + e.getMessage());
        }

        // Test 4: CSV lỗi định dạng (Dòng thiếu cột & dữ liệu không phải số)
        System.out.println("\n[TEST 4] Đọc tệp CSV chứa dòng lỗi (thiếu cột, dữ liệu sai kiểu):");
        Path invalidCsv = dataDir.resolve("test_invalid.csv");
        try (BufferedWriter bw = Files.newBufferedWriter(invalidCsv, StandardCharsets.UTF_8)) {
            bw.write("ma,ten,donGia,soLuong\n");
            bw.write("SP01,USB 64GB,150000,20\n");
            bw.write("SP02,Thẻ nhớ MicroSD,80000\n"); // Thiếu 1 cột
            bw.write("SP03,Cáp Type-C,khong_phai_so,10\n"); // Giá không phải số
            bw.write("SP04,Ổ cứng SSD,1200000,5\n");
        } catch (IOException e) {
            System.err.println("Lỗi tạo file test: " + e.getMessage());
        }
        List<Product> parsedFromInvalid = readFromCsv(invalidCsv);
        System.out.printf("-> Số sản phẩm hợp lệ được nạp lại: %d (các dòng lỗi đã được thông báo và bỏ qua)%n",
                parsedFromInvalid.size());
        try {
            Files.deleteIfExists(invalidCsv);
        } catch (IOException ignored) {
        }

        // Test 5: Tệp không tồn tại
        System.out.println("\n[TEST 5] Đọc tệp không tồn tại (file_khong_ton_tai.csv):");
        Path missingFile = dataDir.resolve("file_khong_ton_tai.csv");
        readFromCsv(missingFile);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("             HOÀN THÀNH BỘ KIỂM THỬ");
        System.out.println("=".repeat(60) + "\n");
    }

    public static void main(String[] args) {
        // Cấu hình UTF-8 cho console output và error stream
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        Path dataDir = getDataDirectory();
        Path csvFile = dataDir.resolve("inventory.csv");
        Path reportFile = dataDir.resolve("inventory-report.txt");

        try (BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            List<Product> currentProducts = new ArrayList<>();

            while (true) {
                System.out.println("\n================ QUẢN LÝ TỒN KHO (INVENTORY MANAGER) ================");
                System.out.println("1. Nhập danh sách sản phẩm từ bàn phím và Lưu vào data/inventory.csv");
                System.out.println("2. Đọc tệp data/inventory.csv và Hiển thị danh sách tồn kho");
                System.out.println("3. Tìm sản phẩm có giá trị tồn kho cao nhất");
                System.out.println("4. Ghi báo cáo tổng hợp vào data/inventory-report.txt");
                System.out.println("5. Chạy toàn bộ quy trình kiểm thử tự động");
                System.out.println("0. Thoát chương trình");
                System.out.print("Vui lòng chọn chức năng (0-5): ");

                String choice = consoleReader.readLine();
                if (choice == null || choice.trim().equals("0")) {
                    System.out.println("Cảm ơn bạn đã sử dụng chương trình. Tạm biệt!");
                    break;
                }

                switch (choice.trim()) {
                    case "1" -> {
                        List<Product> inputList = inputProductsFromConsole(consoleReader);
                        if (!inputList.isEmpty()) {
                            // 1. Lưu danh sách vừa nhập từ bàn phím vào data/inventory.csv
                            saveToCsv(csvFile, inputList);

                            // 2. Đọc lại từ tệp inventory.csv để đảm bảo dữ liệu đã vào kho an toàn
                            currentProducts = readFromCsv(csvFile);

                            // 3. Hiển thị danh sách tồn kho
                            System.out.println("\n--- DANH SÁCH TỒN KHO VỪA CẬP NHẬT TỪ TỆP INVENTORY.CSV ---");
                            displayProducts(currentProducts);

                            // 4. Tìm sản phẩm tồn kho cao nhất
                            Product max = findHighestValueProduct(currentProducts);
                            if (max != null) {
                                System.out.printf("-> Sản phẩm có giá trị tồn kho cao nhất: %s (%s) = %,.0f VND%n%n",
                                        max.getName(), max.getCode(), max.inventoryValue());
                            }

                            // 5. Tự động xuất báo cáo tổng hợp vào data/inventory-report.txt
                            writeReport(reportFile, currentProducts);
                        }
                    }
                    case "2" -> {
                        currentProducts = readFromCsv(csvFile);
                        displayProducts(currentProducts);
                    }
                    case "3" -> {
                        currentProducts = readFromCsv(csvFile);
                        Product max = findHighestValueProduct(currentProducts);
                        if (max != null) {
                            System.out.println("\n--- SẢN PHẨM CÓ GIÁ TRỊ TỒN KHO CAO NHẤT ---");
                            System.out.println(max);
                            System.out.printf("=> Giá trị tồn kho: %,.0f VND%n", max.inventoryValue());
                        } else {
                            System.out.println("Chưa có dữ liệu sản phẩm trong kho.");
                        }
                    }
                    case "4" -> {
                        currentProducts = readFromCsv(csvFile);
                        if (!currentProducts.isEmpty()) {
                            writeReport(reportFile, currentProducts);
                        } else {
                            System.out.println(
                                    "[Cảnh báo] Tệp kho hàng trống hoặc chưa tồn tại, vui lòng nhập sản phẩm trước.");
                        }
                    }
                    case "5" -> {
                        runTestCases(dataDir);
                    }
                    default -> System.out.println("Lựa chọn không hợp lệ, vui lòng chọn từ 0 đến 5.");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi vào/ra console: " + e.getMessage());
        }
    }
}
