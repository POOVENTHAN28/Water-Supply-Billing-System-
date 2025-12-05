import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

// API Response wrapper for consistent responses
class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private String status;
    private long timestamp;

    public ApiResponse(boolean success, String message, Object data, String status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
}

// Bill generation progress tracker
class BillGenerationProgress {
    private String billId;
    private int progress;
    private String status;
    private String message;
    private long startTime;

    public BillGenerationProgress(String billId) {
        this.billId = billId;
        this.progress = 0;
        this.status = "in-progress";
        this.message = "Starting bill generation...";
        this.startTime = System.currentTimeMillis();
    }

    public String getBillId() { return billId; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = Math.min(100, Math.max(0, progress)); }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getDuration() { return System.currentTimeMillis() - startTime; }
}

// Connection status tracker
class ConnectionStatus {
    private String connectionId;
    private boolean isOnline;
    private long lastChecked;
    private String errorMessage;

    public ConnectionStatus(String connectionId) {
        this.connectionId = connectionId;
        this.isOnline = true;
        this.lastChecked = System.currentTimeMillis();
        this.errorMessage = null;
    }

    public String getConnectionId() { return connectionId; }
    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { 
        this.isOnline = online;
        this.lastChecked = System.currentTimeMillis();
    }
    public long getLastChecked() { return lastChecked; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String message) { this.errorMessage = message; }
}

// Models
class User {
    private String userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;
    private LocalDateTime registrationDate;
    private boolean isOnline;

    public User(String userId, String username, String password, String email, 
                String phone, String address, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.registrationDate = LocalDateTime.now();
        this.isOnline = false;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public boolean isOnline() { return isOnline; }

    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setOnline(boolean online) { this.isOnline = online; }
}

class WaterConnection {
    private String connectionId;
    private String userId;
    private String connectionType;
    private String meterNumber;
    private double currentReading;
    private double previousReading;
    private LocalDateTime connectionDate;
    private String status;
    private LocalDateTime lastUpdated;
    private boolean isVerified;

    public WaterConnection(String connectionId, String userId, String connectionType, 
                          String meterNumber) {
        this.connectionId = connectionId;
        this.userId = userId;
        this.connectionType = connectionType;
        this.meterNumber = meterNumber;
        this.currentReading = 0;
        this.previousReading = 0;
        this.connectionDate = LocalDateTime.now();
        this.status = "active";
        this.lastUpdated = LocalDateTime.now();
        this.isVerified = true;
    }

    public String getConnectionId() { return connectionId; }
    public String getUserId() { return userId; }
    public String getConnectionType() { return connectionType; }
    public String getMeterNumber() { return meterNumber; }
    public double getCurrentReading() { return currentReading; }
    public double getPreviousReading() { return previousReading; }
    public String getStatus() { return status; }
    public LocalDateTime getConnectionDate() { return connectionDate; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public boolean isVerified() { return isVerified; }

    public void setCurrentReading(double reading) { 
        this.previousReading = this.currentReading;
        this.currentReading = reading;
        this.lastUpdated = LocalDateTime.now();
    }
    public void setStatus(String status) { 
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }
    public void setVerified(boolean verified) { this.isVerified = verified; }
}

class Bill {
    private String billId;
    private String connectionId;
    private String userId;
    private double unitsConsumed;
    private double amount;
    private LocalDateTime billDate;
    private LocalDateTime dueDate;
    private String status;
    private LocalDateTime paymentDate;
    private String generatedBy;

    public Bill(String billId, String connectionId, String userId, 
                double unitsConsumed, double amount) {
        this.billId = billId;
        this.connectionId = connectionId;
        this.userId = userId;
        this.unitsConsumed = unitsConsumed;
        this.amount = amount;
        this.billDate = LocalDateTime.now();
        this.dueDate = billDate.plusDays(30);
        this.status = "pending";
    }

    public String getBillId() { return billId; }
    public String getConnectionId() { return connectionId; }
    public String getUserId() { return userId; }
    public double getUnitsConsumed() { return unitsConsumed; }
    public double getAmount() { return amount; }
    public LocalDateTime getBillDate() { return billDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getGeneratedBy() { return generatedBy; }

    public void setStatus(String status) { this.status = status; }
    public void setPaymentDate(LocalDateTime date) { this.paymentDate = date; }
    public void setGeneratedBy(String admin) { this.generatedBy = admin; }
}

// Data Manager
class DataManager {
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    
    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, WaterConnection> connections = new ConcurrentHashMap<>();
    private Map<String, Bill> bills = new ConcurrentHashMap<>();
    private Map<String, String> sessions = new ConcurrentHashMap<>();
    private Map<String, BillGenerationProgress> billProgress = new ConcurrentHashMap<>();
    private Map<String, ConnectionStatus> connectionStatus = new ConcurrentHashMap<>();

    public DataManager() {
        loadData();
        if (users.isEmpty()) {
            User admin = new User("admin", "admin", "admin123", 
                "admin@water.com", "1234567890", "Admin Office", "admin");
            users.put(admin.getUserId(), admin);
            
            User sampleUser = new User("user1", "john", "john123", 
                "john@example.com", "9876543210", "123 Main St", "user");
            users.put(sampleUser.getUserId(), sampleUser);
            
            WaterConnection conn = new WaterConnection("C001", "user1", "domestic", "MTR001");
            conn.setCurrentReading(100);
            connections.put(conn.getConnectionId(), conn);
            connectionStatus.put("C001", new ConnectionStatus("C001"));
            
            saveData();
        }
    }

    private void loadData() {
        try {
            if (Files.exists(Paths.get("users.json"))) {
                String json = new String(Files.readAllBytes(Paths.get("users.json")));
                Map<String, User> loadedUsers = gson.fromJson(json, 
                    new TypeToken<Map<String, User>>(){}.getType());
                if (loadedUsers != null) users = new ConcurrentHashMap<>(loadedUsers);
            }
            if (Files.exists(Paths.get("connections.json"))) {
                String json = new String(Files.readAllBytes(Paths.get("connections.json")));
                Map<String, WaterConnection> loadedConns = gson.fromJson(json, 
                    new TypeToken<Map<String, WaterConnection>>(){}.getType());
                if (loadedConns != null) connections = new ConcurrentHashMap<>(loadedConns);
            }
            if (Files.exists(Paths.get("bills.json"))) {
                String json = new String(Files.readAllBytes(Paths.get("bills.json")));
                Map<String, Bill> loadedBills = gson.fromJson(json, 
                    new TypeToken<Map<String, Bill>>(){}.getType());
                if (loadedBills != null) bills = new ConcurrentHashMap<>(loadedBills);
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            Files.write(Paths.get("users.json"), gson.toJson(users).getBytes());
            Files.write(Paths.get("connections.json"), gson.toJson(connections).getBytes());
            Files.write(Paths.get("bills.json"), gson.toJson(bills).getBytes());
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public User authenticateUser(String username, String password) {
        return users.values().stream()
            .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
            .findFirst().orElse(null);
    }

    public String createSession(String userId) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, userId);
        User user = users.get(userId);
        if (user != null) {
            user.setOnline(true);
        }
        return sessionId;
    }

    public String getUserIdFromSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public boolean registerUser(User user) {
        if (users.containsKey(user.getUserId()) || 
            users.values().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            return false;
        }
        users.put(user.getUserId(), user);
        saveData();
        return true;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean addConnection(WaterConnection connection) {
        connections.put(connection.getConnectionId(), connection);
        connectionStatus.put(connection.getConnectionId(), new ConnectionStatus(connection.getConnectionId()));
        saveData();
        return true;
    }

    public List<WaterConnection> getUserConnections(String userId) {
        List<WaterConnection> userConns = connections.values().stream()
            .filter(c -> c.getUserId().equals(userId))
            .collect(Collectors.toList());
        System.out.println("[INFO] Getting connections for user: " + userId + ", found: " + userConns.size());
        return userConns;
    }

    public List<WaterConnection> getAllConnections() {
        return new ArrayList<>(connections.values());
    }

    public WaterConnection getConnection(String connectionId) {
        return connections.get(connectionId);
    }

    public boolean addBill(Bill bill) {
        bills.put(bill.getBillId(), bill);
        saveData();
        return true;
    }

    public List<Bill> getUserBills(String userId) {
        return bills.values().stream()
            .filter(b -> b.getUserId().equals(userId))
            .sorted((b1, b2) -> b2.getBillDate().compareTo(b1.getBillDate()))
            .collect(Collectors.toList());
    }

    public List<Bill> getAllBills() {
        return new ArrayList<>(bills.values());
    }

    public Bill getBill(String billId) {
        return bills.get(billId);
    }

    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", users.size());
        analytics.put("onlineUsers", users.values().stream().filter(User::isOnline).count());
        analytics.put("totalConnections", connections.size());
        analytics.put("activeConnections", connections.values().stream()
            .filter(c -> c.getStatus().equals("active")).count());
        analytics.put("totalBills", bills.size());
        analytics.put("pendingBills", bills.values().stream()
            .filter(b -> b.getStatus().equals("pending")).count());
        analytics.put("totalRevenue", bills.values().stream()
            .filter(b -> b.getStatus().equals("paid"))
            .mapToDouble(Bill::getAmount).sum());
        return analytics;
    }

    public void removeSession(String sessionId) {
        String userId = sessions.remove(sessionId);
        if (userId != null) {
            User user = users.get(userId);
            if (user != null) {
                user.setOnline(false);
            }
        }
    }

    public BillGenerationProgress getBillProgress(String billId) {
        return billProgress.get(billId);
    }

    public void setBillProgress(String billId, BillGenerationProgress progress) {
        billProgress.put(billId, progress);
    }

    public ConnectionStatus getConnectionStatus(String connectionId) {
        return connectionStatus.getOrDefault(connectionId, new ConnectionStatus(connectionId));
    }
}

// HTTP Server
public class WaterBillingSystem {
    public static DataManager dataManager;
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        dataManager = new DataManager();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/logout", new LogoutHandler());
        server.createContext("/api/user", new UserHandler());
        server.createContext("/api/connections", new ConnectionHandler());
        server.createContext("/api/bills", new BillHandler());
        server.createContext("/api/admin/users", new AdminUsersHandler());
        server.createContext("/api/admin/analytics", new AnalyticsHandler());
        server.createContext("/api/admin/generate-bill", new GenerateBillHandler());
        server.createContext("/api/bill-progress", new BillProgressHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("\n===========================================");
        System.out.println("Water Billing System Started Successfully!");
        System.out.println("===========================================");
        System.out.println("✓ Access at: http://localhost:" + PORT);
        System.out.println("\nDefault Credentials:");
        System.out.println("  Admin - Username: admin, Password: admin123");
        System.out.println("  User  - Username: john, Password: john123");
        System.out.println("===========================================\n");
    }
}

class LocalDateTimeAdapter implements com.google.gson.JsonSerializer<LocalDateTime>, 
                                      com.google.gson.JsonDeserializer<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public com.google.gson.JsonElement serialize(LocalDateTime date, java.lang.reflect.Type type, 
                                                  com.google.gson.JsonSerializationContext context) {
        return new com.google.gson.JsonPrimitive(date.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type type, 
                                     com.google.gson.JsonDeserializationContext context) {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
