package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private Room currentRoom;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Kiểm tra tên client: không được trống và không được chỉ chứa toàn số
            while (true) {
                out.println("Nhập tên của bạn:");
                clientName = in.readLine();
                if (clientName == null) {
                    return;
                }
                clientName = clientName.trim();
                if (clientName.isEmpty()) {
                    out.println("Lỗi: Tên người dùng không được để trống! Vui lòng nhập lại.");
                    continue;
                }
                if (clientName.matches("\\d+")) {
                    out.println("Lỗi: Tên người dùng không được chỉ chứa toàn số! Vui lòng nhập lại.");
                    continue;
                }
                break;
            }

            boolean isRunning = true;
            while (isRunning) {
                sendRoomList();
                out.println("Nhập số thứ tự hoặc tên phòng để tham gia, hoặc lệnh (/exit):");
                String input = in.readLine();

                if (input == null || input.trim().equalsIgnoreCase("/exit")) {
                    break;
                }

                input = input.trim();
                if (input.isEmpty()) {
                    out.println("Lỗi: Lựa chọn không được để trống! Vui lòng chọn lại.");
                    continue;
                }

                Room selectedRoom = null;
                try {
                    int roomIndex = Integer.parseInt(input) - 1;
                    if (roomIndex >= 0 && roomIndex < server.getRooms().size()) {
                        selectedRoom = server.getRooms().get(roomIndex);
                    } else {
                        out.println("Lỗi: Số phòng không tồn tại! Vui lòng chọn lại.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    // Người dùng nhập chữ: kiểm tra xem có khớp tên phòng nào không
                    for (Room room : server.getRooms()) {
                        if (room.getName().equalsIgnoreCase(input)) {
                            selectedRoom = room;
                            break;
                        }
                    }
                    if (selectedRoom == null) {
                        out.println("Lỗi: Tên phòng \"" + input + "\" không tồn tại! Vui lòng chọn lại.");
                        continue;
                    }
                }

                if (selectedRoom != null) {
                    currentRoom = selectedRoom;
                    currentRoom.addClient(this);
                    out.println("Bạn đã vào phòng: " + currentRoom.getName());
                    out.println("Nhập tin nhắn để chat. Gõ /quit để rời phòng, /exit để thoát.");

                    handleChat();

                    if (socket.isClosed()) {
                        isRunning = false;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Kết nối bị lỗi với " + clientName);
        } finally {
            closeConnections();
        }
    }

    private void handleChat() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            if (message.equalsIgnoreCase("/quit")) {
                currentRoom.removeClient(this);
                currentRoom = null;
                out.println("Bạn đã rời phòng.");
                break; // Thoát vòng lặp chat, quay lại chọn phòng
            } else if (message.equalsIgnoreCase("/exit")) {
                currentRoom.removeClient(this);
                currentRoom = null;
                out.println("Đang ngắt kết nối...");
                socket.close();
                break;
            } else {
                currentRoom.broadcast(clientName + ": " + message, this);
            }
        }
        if (message == null) {
            if (currentRoom != null) {
                currentRoom.removeClient(this);
                currentRoom = null;
            }
            socket.close();
        }
    }

    private void sendRoomList() {
        List<Room> rooms = server.getRooms();
        out.println("=== DANH SÁCH PHÒNG ===");
        for (int i = 0; i < rooms.size(); i++) {
            out.println((i + 1) + ". " + rooms.get(i).getName());
        }
        out.println("=======================");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientName() {
        return clientName;
    }

    private void closeConnections() {
        try {
            if (currentRoom != null) {
                currentRoom.removeClient(this);
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println(clientName + " đã ngắt kết nối.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}