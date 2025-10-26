# ✅ HOÀN TẤT CẤU HÌNH DUAL DATABASE

## 📊 Tổng Kết Những Gì Đã Làm

### 1. ✅ Dependencies (pom.xml)
- Thêm `spring-boot-starter-data-jpa`
- Thêm `mysql-connector-j`
- Giữ `spring-boot-starter-data-mongodb`

### 2. ✅ Cấu Hình Database (application.yml)
```yaml
# MySQL: jdbc:mysql://localhost:3306/dss_db_mysql
# MongoDB: mongodb://localhost:27017/dss_db_mongo
```

### 3. ✅ Tạo Cấu Trúc Thư Mục Mới

**Entities:**
```
domain/
├── jpa/                 # MySQL Entities
│   ├── Customer.java    ✅ Created
│   ├── Order.java       ✅ Created
│   ├── OrderItem.java   ✅ Created
│   ├── Product.java     ✅ Created
│   └── User.java        ✅ Created
└── mongo/               # MongoDB Documents
    └── (chưa di chuyển)
```

**Repositories:**
```
repository/
├── jpa/                         # MySQL Repositories
│   ├── CustomerRepository.java  ✅ Created
│   ├── OrderRepository.java     ✅ Created
│   ├── ProductRepository.java   ✅ Created
│   └── UserRepository.java      ✅ Created
└── mongo/                       # MongoDB Repositories
    └── RuleCacheRepository.java ✅ Created
```

### 4. ✅ Configuration Classes
- `JpaConfig.java` → Quản lý JPA repositories (package: `com.g5.dss.repository.jpa`)
- `MongoConfig.java` → Cập nhật để quản lý MongoDB repositories (package: `com.g5.dss.repository.mongo`)
- `DssApplication.java` → Thêm `@EnableJpaRepositories`

### 5. ✅ Services Đã Cập Nhật
- `UserService.java` → Đã chuyển sang dùng JPA entities/repositories

---

## 🚨 CẦN LÀM TIẾP

### Bước 1: Cập nhật Password MySQL ⚠️
**File:** `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    password: YOUR_ACTUAL_MYSQL_PASSWORD  # ⚠️ ĐỔI NGAY
```

### Bước 2: Tạo Database MySQL 📀
Chạy lệnh SQL:

```sql
CREATE DATABASE IF NOT EXISTS dss_db_mysql 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### Bước 3: Kiểm Tra & Di Chuyển MongoDB Entities (Optional) 🔄

**Entities MongoDB cũ còn ở:** `com.g5.dss.domain`
- Customer.java
- Order.java
- OrderItem.java
- Product.java
- User.java
- RuleCache.java

**Có thể:**
1. Giữ nguyên (backup)
2. Di chuyển `RuleCache.java` sang `com.g5.dss.domain.mongo`
3. Xóa các entity không dùng nữa (sau khi test)

### Bước 4: Build & Test 🧪

**1. Clean build:**
```bash
mvn clean install
```

**2. Chạy ứng dụng:**
```bash
mvn spring-boot:run
```

**3. Kiểm tra logs:**
- ✅ JPA tạo tables trong MySQL
- ✅ MongoDB kết nối thành công
- ❌ Có lỗi gì không?

---

## 📁 Cấu Trúc Cuối Cùng

### MySQL/JPA (Transactional Data)
| Entity | Repository | Table Name |
|--------|-----------|------------|
| Customer | CustomerRepository | customers |
| Order | OrderRepository | orders |
| OrderItem | (Embedded) | order_items |
| Product | ProductRepository | products |
| User | UserRepository | users |

### MongoDB (Flexible Data)
| Document | Repository | Collection |
|----------|-----------|------------|
| RuleCache | RuleCacheRepository | rule_caches |

---

## 🔍 Cách Sử Dụng Trong Code

### Ví Dụ: Service với JPA (MySQL)
```java
import com.g5.dss.domain.jpa.Customer;
import com.g5.dss.repository.jpa.CustomerRepository;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
```

### Ví Dụ: Service với MongoDB
```java
import com.g5.dss.domain.RuleCache;
import com.g5.dss.repository.mongo.RuleCacheRepository;

@Service
public class CacheService {
    @Autowired
    private RuleCacheRepository ruleCacheRepository;
    
    public RuleCache cacheRule(RuleCache cache) {
        return ruleCacheRepository.save(cache);
    }
}
```

---

## 🎯 Checklist

- [x] Thêm dependencies vào pom.xml
- [x] Cấu hình application.yml
- [x] Tạo JPA entities
- [x] Tạo JPA repositories
- [x] Tạo MongoDB repository
- [x] Tạo JpaConfig & MongoConfig
- [x] Cập nhật UserService
- [ ] **Đổi password MySQL trong application.yml**
- [ ] **Tạo database MySQL**
- [ ] **Build project**
- [ ] **Test kết nối**
- [ ] Di chuyển MongoDB entities (optional)
- [ ] Xóa repository cũ (sau khi test)

---

## ❓ Nếu Gặp Lỗi

### Lỗi: "Multiple DataSource found"
**Giải pháp:** Đã cấu hình xong với `@EnableJpaRepositories` và `@EnableMongoRepositories` ở các package riêng biệt.

### Lỗi: "Could not create connection to database"
**Kiểm tra:**
1. MySQL có đang chạy không?
2. Username/password có đúng không?
3. Database `dss_db_mysql` đã tạo chưa?

### Lỗi: "Failed to configure a DataSource"
**Kiểm tra:** `application.yml` có cấu hình đầy đủ cả MySQL và MongoDB không?

---

## 🚀 Next Steps

**Bạn muốn tôi:**
1. Tạo script SQL để khởi tạo schema MySQL?
2. Tạo script migration dữ liệu từ MongoDB sang MySQL?
3. Kiểm tra tất cả các service/controller cần cập nhật?
4. Tạo integration tests cho dual database?

**Gõ lệnh để tiếp tục:**
- "Tạo SQL schema script"
- "Kiểm tra tất cả services"
- "Tạo migration script"
- "Tạo integration tests"
