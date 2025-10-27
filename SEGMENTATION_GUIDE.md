# 🎯 Hướng Dẫn Sử Dụng Phân Khúc Khách Hàng & Market Basket

## ✅ Đã Triển Khai

Tôi đã tạo đầy đủ tính năng phân khúc khách hàng (RFM Analysis) và Market Basket Analysis (gợi ý gói sản phẩm) cho ứng dụng DSS của bạn, tương tự như code Python Streamlit bạn cung cấp.

### 📁 Files Đã Tạo

#### 1. DTOs (Data Transfer Objects)
- `RFMCustomerDTO.java` - DTO cho phân tích RFM (Recency, Frequency, Monetary)
- `MarketBasketRuleDTO.java` - DTO cho quy tắc kết hợp sản phẩm
- `CustomerSegmentSummaryDTO.java` - DTO tổng hợp phân khúc

#### 2. Services (Business Logic)
- `CustomerSegmentationService.java` - Phân tích RFM và phân khúc khách hàng
- `MarketBasketService.java` - Phân tích Market Basket (Association Rules)

#### 3. Controllers
- `SegmentationApiController.java` - REST API cho phân khúc
- `SegmentationController.java` - Web controller

#### 4. Templates (HTML)
- `segmentation_overview.html` - Trang tổng quan phân khúc

## 🚀 Tính Năng

### 1. **Phân Khúc Khách Hàng (RFM Analysis)**

**5 Phân khúc Tự động:**

1. **🏆 Champions** - Khách hàng VIP
   - Recency thấp (mới mua gần đây)
   - Frequency cao (mua thường xuyên)
   - Monetary cao (chi tiêu nhiều)
   - **Marketing**: Ưu đãi VIP, early access, giới thiệu bạn bè

2. **💎 Loyal** - Khách hàng Trung thành
   - Recency trung bình-thấp
   - Frequency trung bình-cao
   - **Marketing**: Tích điểm, upsell, ưu đãi sinh nhật

3. **⚠️ At-Risk** - Có Nguy Cơ Rời Bỏ
   - Recency cao (lâu không mua)
   - Frequency thấp
   - **Marketing**: Email nhắc nhở + mã giảm 15%, reactivation bundle

4. **😴 Hibernating** - Ngủ Đông
   - Recency rất cao
   - Frequency thấp
   - **Marketing**: Remarketing, giảm phí vận chuyển, flash sale

5. **👥 Regulars** - Khách hàng Thường
   - Các metrics ở mức trung bình
   - **Marketing**: Khuyến mãi định kỳ, cross-sell

### 2. **Market Basket Analysis (Gợi ý Gói Sản phẩm)**

**Metrics:**
- **Support**: Tỷ lệ giao dịch có cả sản phẩm A và B
- **Confidence**: Xác suất mua B khi đã mua A (%)
- **Lift**: Mức độ liên kết (>1 là tốt, >2 là rất mạnh)

**Ứng dụng:**
- Tạo bundle khuyến mãi
- Gợi ý "Mua kèm" trên website
- Chiến dịch email marketing

## 📊 API Endpoints

### Phân Khúc Khách Hàng

```http
GET /api/segmentation/rfm
```
Tính toán RFM cho tất cả khách hàng

```http
GET /api/segmentation/summary
```
Lấy tổng quan các phân khúc

**Response:**
```json
[
  {
    "segmentName": "Champions",
    "customerCount": 150,
    "totalValue": 500000,
    "avgRecency": 15.5,
    "avgFrequency": 8.2,
    "avgMonetary": 3333.33,
    "percentageOfTotal": 10.5,
    "description": "🏆 Nhóm khách hàng VIP nhất!...",
    "marketingActions": [
      "Ưu đãi VIP/early access",
      "Chương trình giới thiệu bạn bè"
    ]
  }
]
```

```http
GET /api/segmentation/at-risk
```
Lấy danh sách khách hàng có nguy cơ rời bỏ

**Response:**
```json
{
  "atRiskCustomers": [...],
  "totalCustomers": 1000,
  "atRiskCount": 120,
  "atRiskPercentage": 12.0,
  "potentialValue": 150000
}
```

```http
GET /api/segmentation/segment/{segmentName}
```
Lấy danh sách khách hàng trong một phân khúc

**Params:**
- `segmentName`: Champions, Loyal, At-Risk, Hibernating, Regulars

```http
GET /api/segmentation/stats
```
Lấy thống kê tổng quan

### Market Basket Analysis

```http
POST /api/segmentation/market-basket
```
Phân tích market basket cho một phân khúc

**Params:**
- `segment` (optional): Tên phân khúc
- `minSupport` (default: 0.01): Ngưỡng support tối thiểu
- `minConfidence` (default: 30): Ngưỡng confidence tối thiểu (%)
- `maxRules` (default: 10): Số lượng quy tắc tối đa

**Response:**
```json
[
  {
    "productACode": "85123A",
    "productAName": "WHITE HANGING HEART T-LIGHT HOLDER",
    "productBCode": "22423",
    "productBName": "REGENCY CAKESTAND 3 TIER",
    "support": 0.025,
    "confidence": 65.5,
    "lift": 2.3,
    "transactionCount": 150,
    "recommendation": "🔥 Bundle mạnh - Tạo combo khuyến mãi"
  }
]
```

```http
GET /api/segmentation/product-recommendations/{stockCode}
```
Lấy gợi ý sản phẩm cho 1 sản phẩm

**Params:**
- `stockCode`: Mã sản phẩm
- `segment` (optional): Lọc theo phân khúc
- `topN` (default: 5): Số lượng gợi ý

## 🌐 Web Pages

### 1. Trang Tổng Quan Phân Khúc

**URL:** `http://localhost:8080/segmentation`

**Hiển thị:**
- Thống kê tổng quan (tổng khách, số phân khúc, khách rủi ro)
- Các thẻ phân khúc với metrics chi tiết
- Biểu đồ phân bổ khách hàng (doughnut chart)
- Link đến Market Basket Analysis

**Tính năng:**
- Click vào thẻ phân khúc để xem chi tiết
- Hover effect đẹp mắt
- Màu sắc riêng cho từng phân khúc

### 2. Trang Chi Tiết Phân Khúc

**URL:** `http://localhost:8080/segmentation/segment/{segmentName}`

**Hiển thị:**
- Danh sách khách hàng trong phân khúc
- Thống kê chi tiết (Recency, Frequency, Monetary)
- Gợi ý hành động marketing cụ thể

### 3. Trang Market Basket

**URL:** `http://localhost:8080/segmentation/market-basket`

**Hiển thị:**
- Bộ lọc theo phân khúc
- Danh sách quy tắc kết hợp (A → B)
- Metrics: Support, Confidence, Lift
- Gợi ý marketing cho từng quy tắc

## 🎯 Cách Sử Dụng

### Bước 1: Khởi động ứng dụng

```powershell
mvnw spring-boot:run
```

### Bước 2: Truy cập Dashboard

```
http://localhost:8080/dashboard
```

Click vào link "Phân khúc Khách hàng" (hoặc trực tiếp truy cập `/segmentation`)

### Bước 3: Xem Phân Khúc

1. Xem tổng quan 5 phân khúc
2. Click vào phân khúc quan tâm (ví dụ: Champions)
3. Xem danh sách khách hàng và gợi ý marketing

### Bước 4: Phân Tích Market Basket

1. Từ trang tổng quan, click "Phân tích Market Basket"
2. Chọn phân khúc (hoặc All)
3. Xem các cặp sản phẩm thường được mua cùng
4. Sử dụng kết quả để:
   - Tạo bundle khuyến mãi
   - Thiết lập "Mua kèm" trên web
   - Thiết kế email marketing

## 💡 Use Cases Thực Tế

### Use Case 1: Giữ Chân Khách Hàng At-Risk

**Mục tiêu:** Ngăn chặn khách hàng rời bỏ

**Steps:**
1. Truy cập `/segmentation`
2. Xem số khách hàng "At-Risk" (màu đỏ)
3. Click vào card "At-Risk"
4. Export danh sách khách hàng (API: `/api/segmentation/segment/At-Risk`)
5. Tạo chiến dịch email:
   - Subject: "🎁 [Tên], chúng tôi nhớ bạn!"
   - Content: Mã giảm 15% + gợi ý sản phẩm họ đã mua
   - CTA: "Quay lại mua ngay"

### Use Case 2: Upsell cho Champions

**Mục tiêu:** Tăng giá trị từ khách VIP

**Steps:**
1. Lấy danh sách Champions: `/api/segmentation/segment/Champions`
2. Phân tích sản phẩm họ hay mua
3. Tạo market basket rules cho Champions
4. Thiết kế bundle cao cấp dựa trên quy tắc có Lift > 2.0
5. Gửi email độc quyền với ưu đãi VIP

### Use Case 3: Cross-sell với Market Basket

**Mục tiêu:** Tăng AOV (Average Order Value)

**Steps:**
1. Phân tích market basket: `/api/segmentation/market-basket?segment=Loyal`
2. Tìm cặp sản phẩm có Confidence > 60% và Lift > 1.5
3. Triển khai "Mua kèm" trên website:
   - Khi khách thêm sản phẩm A vào giỏ
   - Hiển thị: "Khách hàng cũng mua: [Sản phẩm B]"
4. Đo lường conversion rate

### Use Case 4: Reactivate Hibernating

**Mục tiêu:** Đánh thức khách hàng ngủ đông

**Steps:**
1. Lấy Hibernating customers: `/api/segmentation/segment/Hibernating`
2. Sắp xếp theo Monetary giảm dần (ưu tiên khách có giá trị cao)
3. Thiết kế chiến dịch remarketing:
   - Google Ads retargeting
   - Facebook Custom Audience
   - Email với flash sale 24h
4. Track conversion trong 1 tuần

## 🔧 Tùy Chỉnh

### Điều Chỉnh Ngưỡng Phân Khúc

Mở file `CustomerSegmentationService.java`, tìm method `determineSegment()`:

```java
// Champions: Low recency, high frequency, high monetary
if (recency <= recencyQ25 && frequency >= frequencyQ75 && monetary >= monetaryQ75) {
    return "Champions";
}
```

Thay đổi ngưỡng theo nhu cầu (Q25, Q50, Q75).

### Thêm Phân Khúc Mới

1. Thêm logic vào `determineSegment()`
2. Thêm description vào `getSegmentDescription()`
3. Thêm actions vào `getMarketingActions()`
4. Cập nhật màu sắc trong CSS

### Tùy Chỉnh Market Basket

Điều chỉnh tham số trong `MarketBasketService`:

```java
// Số sản phẩm phổ biến để phân tích
.limit(100)  // Tăng lên 200 cho nhiều quy tắc hơn

// Ngưỡng confidence
if (confidence >= minConfidence)  // Giảm xuống 20 để tìm nhiều quy tắc hơn
```

## 📈 Metrics Quan Trọng

### RFM Metrics

- **Recency < 30 ngày**: Khách hàng active
- **Frequency > 5 đơn**: Khách hàng trung thành
- **Monetary > TB**: Khách hàng giá trị cao

### Market Basket Metrics

- **Support > 2%**: Quy tắc phổ biến
- **Confidence > 50%**: Quy tắc mạnh
- **Lift > 1.5**: Có liên kết tốt
- **Lift > 2.0**: Liên kết rất mạnh 🔥

## 🎨 Thiết Kế

**Màu sắc Phân khúc:**
- Champions: Vàng gold (#FFD700)
- Loyal: Xanh lá (#4CAF50)
- At-Risk: Đỏ (#FF5722)
- Hibernating: Xám (#9E9E9E)
- Regulars: Xanh dương (#2196F3)

**Biểu tượng:**
- 🏆 Champions
- 💎 Loyal
- ⚠️ At-Risk
- 😴 Hibernating
- 👥 Regulars

## 🚧 Còn Cần Tạo

Do giới hạn token, bạn cần tạo thêm:

1. **Templates còn thiếu:**
   - `segment_detail.html` - Trang chi tiết phân khúc
   - `market_basket.html` - Trang market basket analysis

2. **Tính năng bổ sung:**
   - Export CSV cho từng phân khúc
   - Tích hợp với email marketing
   - Dashboard analytics cho từng phân khúc

## 📚 Tài Liệu Tham Khảo

- RFM Analysis: https://en.wikipedia.org/wiki/RFM_(market_research)
- Association Rules: https://en.wikipedia.org/wiki/Association_rule_learning
- Market Basket Analysis: https://www.investopedia.com/terms/m/market-basket.asp

---

**Tạo bởi:** GitHub Copilot  
**Ngày:** October 27, 2025  
**Dựa trên:** Python Streamlit app.py
