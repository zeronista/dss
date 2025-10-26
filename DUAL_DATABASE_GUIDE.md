# 🗄️ Hướng Dẫn Cấu Hình Dual Database (MySQL + MongoDB)

## ✅ Đã Hoàn Thành

### 1. Dependencies (pom.xml)
✅ Đã thêm `spring-boot-starter-data-jpa`  
✅ Đã thêm `mysql-connector-j`  
✅ Đã có `spring-boot-starter-data-mongodb`

### 2. Cấu Hình Database (application.yml)
✅ MySQL: `jdbc:mysql://localhost:3306/dss_db_mysql`  
✅ MongoDB: `mongodb://localhost:27017/dss_db_mongo`

### 3. Cấu Trúc Mới

```
src/main/java/com/g5/dss/
├── domain/
│   ├── jpa/              # JPA Entities cho MySQL
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Product.java
│   │   └── User.java
│   └── mongo/            # MongoDB Documents (giữ nguyên entities cũ)
│       └── RuleCache.java (sẽ di chuyển)
│
└── repository/
    ├── jpa/              # JPA Repositories cho MySQL
    │   ├── CustomerRepository.java
    │   ├── OrderRepository.java
    │   ├── ProductRepository.java
    │   └── UserRepository.java
    └── mongo/            # MongoDB Repositories
        └── RuleCacheRepository.java
```

### 4. Phân Chia Database

**MySQL (JPA) - Dữ liệu giao dịch:**
- ✅ Customer (Khách hàng)
- ✅ Order (Đơn hàng)
- ✅ Product (Sản phẩm)
- ✅ User (Người dùng)

**MongoDB - Dữ liệu linh hoạt:**
- ✅ RuleCache (Cache quy tắc)

---

## 🔧 Các Bước Tiếp Theo

### Bước 1: Cập nhật application.yml
Mở `src/main/resources/application.yml` và **thay đổi thông tin kết nối**:

```yaml
spring:
  # MySQL Configuration (JPA)
  datasource:
    url: jdbc:mysql://localhost:3306/dss_db_mysql
    username: root
    password: YOUR_MYSQL_PASSWORD  # ⚠️ Thay đổi password
    
  # MongoDB Configuration
  data:
    mongodb:
      database: dss_db_mongo
      # username: your_username  # Bỏ comment nếu cần
      # password: your_password  # Bỏ comment nếu cần
```

### Bước 2: Tạo Database MySQL
Chạy lệnh SQL để tạo database:

```sql
CREATE DATABASE IF NOT EXISTS dss_db_mysql 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### Bước 3: Tạo Database MongoDB
MongoDB sẽ tự động tạo database khi bạn chạy ứng dụng lần đầu.

### Bước 4: Di chuyển MongoDB entities (nếu cần)
Các entity MongoDB cũ (Customer, Order, Product, User) vẫn còn ở `com.g5.dss.domain`.  
Bạn có thể:
- **Giữ nguyên** nếu muốn backup
- **Di chuyển** sang `com.g5.dss.domain.mongo` để tổ chức tốt hơn

### Bước 5: Cập nhật Services
Các Service hiện tại đang dùng repository cũ. Bạn cần cập nhật imports:

**Ví dụ:**
```java
// CŨ
import com.g5.dss.repository.CustomerRepository;
import com.g5.dss.domain.Customer;

// MỚI - Cho MySQL/JPA
import com.g5.dss.repository.jpa.CustomerRepository;
import com.g5.dss.domain.jpa.Customer;

// HOẶC - Cho MongoDB (nếu cần)
import com.g5.dss.repository.mongo.RuleCacheRepository;
import com.g5.dss.domain.RuleCache;
```

### Bước 6: Build và Test

1. **Build project:**
   ```bash
   mvn clean install
   ```

2. **Chạy ứng dụng:**
   ```bash
   mvn spring-boot:run
   ```

3. **Kiểm tra logs:**
   - JPA sẽ tự động tạo tables trong MySQL (nếu `ddl-auto: update`)
   - MongoDB sẽ kết nối tới collection `rule_caches`

---

## 📋 Danh Sách File Cần Cập Nhật

Tìm kiếm và cập nhật các file Service đang sử dụng repository:

```bash
# Tìm tất cả file đang import repository cũ
grep -r "import com.g5.dss.repository.CustomerRepository" src/
grep -r "import com.g5.dss.repository.OrderRepository" src/
grep -r "import com.g5.dss.repository.ProductRepository" src/
grep -r "import com.g5.dss.repository.UserRepository" src/
```

### Services cần cập nhật (ước tính):
- ✏️ CustomerService
- ✏️ OrderService
- ✏️ ProductService
- ✏️ UserService / UserDetailsService
- ✏️ Các Controller liên quan

---

## ⚠️ Lưu Ý Quan Trọng

### 1. Annotation khác nhau
**JPA Entity:**
```java
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

**MongoDB Document:**
```java
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "rule_caches")
public class RuleCache {
    @Id
    private String id;
}
```

### 2. ID Type khác nhau
- **MySQL/JPA**: `Long id` (auto-increment)
- **MongoDB**: `String id` (ObjectId)

### 3. Relationships
- **JPA**: Dùng `@OneToMany`, `@ManyToOne`, `@JoinColumn`
- **MongoDB**: Dùng embedded documents hoặc DBRef

---

## 🧪 Testing

### Test MySQL Connection
```java
@Autowired
private com.g5.dss.repository.jpa.CustomerRepository customerRepository;

@Test
void testMySQLConnection() {
    Customer customer = new Customer();
    customer.setCustomerId("C001");
    customer.setName("Test Customer");
    customerRepository.save(customer);
    
    assertNotNull(customer.getId());
}
```

### Test MongoDB Connection
```java
@Autowired
private com.g5.dss.repository.mongo.RuleCacheRepository ruleCacheRepository;

@Test
void testMongoDBConnection() {
    RuleCache cache = new RuleCache();
    cache.setRuleType("TEST");
    ruleCacheRepository.save(cache);
    
    assertNotNull(cache.getId());
}
```

---

## 🎯 Next Steps

1. ✅ Cập nhật password MySQL trong `application.yml`
2. ✅ Tạo database `dss_db_mysql`
3. ⏳ Cập nhật tất cả Service classes
4. ⏳ Test kết nối cả 2 databases
5. ⏳ Migrate dữ liệu từ MongoDB sang MySQL (nếu cần)

---

## ❓ Cần Giúp Đỡ?

**Bạn có thể yêu cầu:**
- Tự động cập nhật tất cả Service classes
- Tạo script migration dữ liệu
- Tạo script SQL cho MySQL schema
- Hướng dẫn deploy với dual database

**Gõ lệnh:**
- "Cập nhật tất cả services"
- "Tạo migration script"
- "Tạo SQL schema"
