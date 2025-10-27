# [cite_start]DSS PROJECT ASSIGNMENT [cite: 1263]
# [cite_start]USER INTERFACE DESIGN [cite: 1264]

[cite_start]– Hanoi, Jun 2025 – [cite: 1265]

## [cite_start]I. Record of Changes [cite: 1266]

[cite_start]The following table: [cite: 1267]

| Date     | A/M/D* | In charge | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| :------- | :----- | :-------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 08/10/25 | A      | Hiển      | Add 1.2, 2.2, 3.2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| 11/10/25 | A      | Lâm       | Add and finished Lâm’s document                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| 13/10/25 | A      | Huy       | Add 1.1 Tổng Quan Hiệu Suất Kinh Doanh (Customer and Product Performance Review), Add 2.1 Dự Báo Doanh Thu và Rủi Ro Khách Hàng Rời Bỏ (Sales and Customer Churn Forecasting), Add 3.1 Phân Khúc Khách Hàng và Gợi Ý Tiếp Thị Mục Tiêu (Customer Segmentation and Targeted Marketing Simulation)                                                                                                                                                                                                                                                                                                                                                                                       |
| 13/10/25 | A      | Vũ        | Add 1.3 Kiểm Toán Chất Lượng & Hợp Lệ Hóa Đơn – Xây dựng mô hình DSS chấm điểm lỗi dữ liệu hóa đơn (thiếu CustomerID, giá/số lượng không hợp lệ), dashboard hiển thị KPI & danh sách ưu tiên xử lý. Add 2.3 Phát Hiện Giao Dịch Bất Thường – Thiết kế mô hình dự báo dựa trên Z-score cho giá & số lượng, tính điểm bất thường, dashboard Approval Queue cho quyết định phê duyệt/giữ/từ chối. Add 3.3 Chính Sách Kiểm Soát Rủi Ro Trả Hàng – Xây dựng mô hình prescriptive với mô phỏng “What-if”, điều chỉnh ngưỡng Risk Threshold τ, trực quan hóa lợi nhuận kỳ vọng và đề xuất ngưỡng tối ưu. |
| [cite_start]14/10/25 [cite: 1268] | [cite_start]U [cite: 1269] | [cite_start]Hiển [cite: 1270] | [cite_start]Update 1.2, 2.2, 3.2 [cite: 1271]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

[cite_start]*A - Added M - Modified D - Deleted [cite: 1272]

## II. [cite_start]User Interface Design [cite: 1273]

### [cite_start]1. Monthly & Country Sales Overview for Top SKUs (Hiển) [cite: 1274]

#### [cite_start]2.1 Mục tiêu [cite: 1275]
[cite_start]Cung cấp bức tranh mô tả doanh thu theo tháng (YearMonth), theo quốc gia (Country) và Top SKUs theo Revenue để nhà quản trị: [cite: 1276]
* [cite_start]Nhận diện tháng cao/ thấp điểm, [cite: 1277]
* [cite_start]Ưu tiên quốc gia và SKU đóng góp lớn, [cite: 1278]
* [cite_start]Theo dõi mức độ tập trung doanh thu (share Top-N), [cite: 1279]
* [cite_start]Phục vụ quyết định ngân sách/chiến dịch kịp thời. [cite: 1280]

#### [cite_start]2.2 Mô hình [cite: 1281]

##### [cite_start]2.2.1 Chiều mô hình [cite: 1282]
* [cite_start]Loại DSS : Descriptive. [cite: 1283]
* [cite_start]Biểu diễn : KPI Cards + Line chart (Monthly Trend) + Bar chart (Top-N Countries, Top-N SKUs) + 3 bảng xếp hạng/chi tiết. [cite: 1284]

##### [cite_start]2.2.2 Cách thức hoạt động [cite: 1285]
* [cite_start]Làm sạch & chuẩn hóa : loại hóa đơn hủy (nếu bật), loại dòng Quantity ≤ 0 hoặc UnitPrice ≤ 0, tạo Revenue = Quantity×UnitPrice, YearMonth = YYYY-MM . [cite: 1286]
* [cite_start]Tổng hợp : [cite: 1287]
    * [cite_start]Doanh thu theo YearMonth (Monthly Trend). [cite: 1288]
    * [cite_start]Doanh thu theo Country (sort giảm, lấy Top-N). [cite: 1289]
    * [cite_start]Doanh thu theo SKU (sort giảm, lấy Top-N). [cite: 1290]
* [cite_start]KPI : Total Revenue, #Countries Active, %Share Top-N Countries, %Share Top-N SKUs (cảnh báo nếu > 60%). [cite: 1291]
* [cite_start]Drill-down : click Country → lọc theo quốc gia đó; [cite: 1292] [cite_start]click SKU → xem chuỗi doanh thu theo tháng cho SKU. [cite: 1293]

#### [cite_start]2.3 Giao diện người dùng [cite: 1294]
[cite_start]Bố cục chung: [cite: 1295]
* Một trang, Phần nhập liệu ở trên; [cite_start]Kết quả DSS và Phân tích/Gợi ý hiển thị ngay bên dưới [cite: 1296]

##### [cite_start]2.3.1 Nội dung nhập liệu [cite: 1297]

[Image: Bộ lọc dữ liệu UI]

[cite_start]Các trường thông tin: [cite: 1298]

| # | Tên trường         | Loại UI              | Mô tả chi tiết                                                                                                                                                                                                                                          |
| :-- | :----------------- | :------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1 | Date Range         | DatePicker (từ–đến) | [cite_start]Chọn khoảng thời gian phân tích. [cite: 1299] [cite_start]Nếu để trống → dùng toàn bộ dữ liệu hợp lệ. [cite: 1299] [cite_start]Ràng buộc: start ≤ end [cite: 1299]                                                                                                               |
| 2 | Country filter     | Multi-select dropdown | [cite_start]Lọc theo 1 hoặc nhiều quốc gia. [cite: 1299] [cite_start]Mặc định “All”. [cite: 1299] [cite_start]Hỗ trợ search trong danh sách. [cite: 1299]                                                                                                                                       |
| 3 | Top-N              | Slider (min 5, max 50; default 10) | [cite_start]Quy định số lượng quốc gia & SKU hiển thị trong bảng/biểu đồ xếp hạng. [cite: 1299] [cite_start]Áp dụng đồng thời cho Top Countries và Top SKUs. [cite: 1299]                                                                                               |
| 4 | Exclude cancellations | Toggle              | [cite_start]Bật để loại các hóa đơn có InvoiceNo bắt đầu bằng “C”. [cite: 1299] [cite_start]Tooltip giải thích “Loại invoice hủy”. [cite: 1299]                                                                                                                                    |
| 5 | Apply              | Button (primary)     | [cite_start]Áp dụng bộ lọc & tính toán lại toàn bộ kết quả bên dưới (KPI, biểu đồ, bảng). [cite: 1299] [cite_start]Disable nếu đầu vào không hợp lệ. [cite: 1299]                                                                                                                |

##### [cite_start]2.3.2 Kết quả DSS [cite: 1300]

[Image: DSS Results - KPIs and Monthly Trend]
[Image: DSS Results - Top-N Charts and Tables]

[cite_start]Các trường thông tin: [cite: 1301]

| #  | Tên trường                 | Loại UI            | Mô tả chi tiết                                                                                                                                                                                                                               |
| :--- | :------------------------- | :----------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1  | Total Revenue (range)      | KPI Card           | [cite_start]Tổng doanh thu sau lọc. [cite: 1302] [cite_start]Hiển thị số lớn, định dạng tiền tệ; tooltip mô tả công thức SUM(Quantity×UnitPrice). [cite: 1302]                                                                                                         |
| 2  | #Countries active          | KPI Card           | [cite_start]Số quốc gia phát sinh doanh thu trong khoảng thời gian đã chọn. [cite: 1302]                                                                                                                                                                 |
| 3  | %Share Top-N Countries     | KPI Card           | [cite_start]Tỷ trọng doanh thu gộp của Top-N quốc gia so với tổng. [cite: 1302] [cite_start]Cảnh báo màu nếu > 60%. [cite: 1302]                                                                                                                                       |
| 4  | %Share Top-N SKUs          | KPI Card           | [cite_start]Tỷ trọng doanh thu gộp của Top-N SKU so với tổng. [cite: 1302] [cite_start]Cảnh báo màu nếu > 60%. [cite: 1302]                                                                                                                                            |
| 5  | Monthly Revenue Trend      | Line chart         | Trục X: YearMonth; [cite_start]Trục Y: Revenue. [cite: 1302] [cite_start]Đánh dấu điểm cao nhất/thấp nhất; tooltip chi tiết. [cite: 1302]                                                                                                                             |
| 6  | Top-N Countries by Revenue | Bar chart (ngang)  | [cite_start]Xếp hạng quốc gia theo doanh thu; tooltip thêm % share. [cite: 1302] [cite_start]Cho phép click để drill-down. [cite: 1302]                                                                                                                                  |
| 7  | Top-N SKUs by Revenue      | Bar chart (ngang)  | [cite_start]Xếp hạng SKU theo doanh thu; nhãn StockCode – Description (rút gọn). [cite: 1302] [cite_start]Click để drill-down. [cite: 1302]                                                                                                                               |
| 8  | Bảng Country Ranking       | Table              | [cite_start]Cột: Country, Revenue, % tổng, Rank. [cite: 1302] [cite_start]Tìm kiếm/sắp xếp, Export CSV/Excel. [cite: 1302]                                                                                                                                               |
| 9  | Bảng SKU Ranking           | Table              | [cite_start]Cột: StockCode, Description, Revenue, % tổng, Rank. [cite: 1302] [cite_start]Tìm kiếm/sắp xếp, Export. [cite: 1302]                                                                                                                                           |
| 10 | Bảng Monthly Trend         | Table              | [cite_start]Cột: YearMonth, Revenue, MoM%. [cite: 1302]                                                                                                                                                                                                    |

##### [cite_start]2.3.3 Phân tích/Gợi ý [cite: 1303]

[Image: Insights & Recommendations UI]

[cite_start]Các trường thông tin: [cite: 1304]

| # | Tên trường             | Loại UI                         | Mô tả chi tiết                                                                                                                                                                                          |
| :- | :--------------------- | :------------------------------ | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1 | Action Recommendations | Table (SKU/Country, Lý do, Hành động) | [cite_start]Tạo danh sách hành động ngắn gọn: “Đẩy ngân sách UK”, “Bundle 22423 với 23843”, “A/B test giá ở DE”. [cite: 1305]                                                                               |
| 2 | Save Insights          | Button                          | [cite_start]Lưu snapshot kết quả + insights (gắn ngày/giờ, bộ lọc) để tái so sánh lần sau. [cite: 1305]                                                                                                             |

### [cite_start]2. Chính Sách Kiểm Soát Rủi Ro Trả Hàng-Prescriptive Decisions (Vũ) [cite: 1306]

#### [cite_start]2.1 Mục tiêu [cite: 1307]
[cite_start]Hỗ trợ Người quản lý vận hành (Operations Manager) thiết lập chính sách tối ưu cho các đơn hàng có rủi ro trả hàng cao. [cite: 1308]
[cite_start]Người quản lý có thể dùng công cụ phân tích "What-if" để xem trước tác động của việc thay đổi ngưỡng rủi ro đến lợi nhuận và tỷ lệ chuyển đổi. [cite: 1309]

#### [cite_start]2.2 Mô hình [cite: 1310]

##### [cite_start]2.2.1 Chiều mô hình [cite: 1311]
[cite_start]Đây là một mô hình hỗ trợ quyết định mang tính chỉ định (Prescriptive DSS), không chỉ dự báo mà còn gợi ý "hành động tốt nhất nên làm là gì". [cite: 1312]
[cite_start]Sử dụng phương pháp mô phỏng (Simulation) để thực hiện phân tích "What-if". [cite: 1313]

##### [cite_start]2.2.2 Cách thức hoạt động [cite: 1314]
* B1. [cite_start]Tính điểm rủi ro (Return Risk Scoring): Với mỗi đơn hàng mới, DSS tính toán một Return_Risk_Score (0-100) dựa trên các đặc trưng như tỷ lệ trả hàng của khách, của sản phẩm, giá trị đơn hàng, v.v. [cite: 1315]
* B2. [cite_start]Mô phỏng chính sách (Policy Simulation): Người quản lý tương tác với một thanh trượt để thay đổi ngưỡng rủi ro (Risk Threshold τ). [cite: 1316]
* B3. [cite_start]Ước tính lợi nhuận: Với mỗi giá trị τ được chọn, hệ thống sẽ tính toán lại lợi nhuận kỳ vọng trên toàn bộ tập dữ liệu mẫu: [cite: 1317]
    * [cite_start]Nếu Risk_Score < τ (cho qua): $Expected\_Profit=(Revenue−Costs)−(Risk\_Score×ReturnCost)$ [cite: 1318]
    * [cite_start]Nếu Risk_Score ≥ τ (áp dụng chính sách): $Expected\_Profit=(Revenue−Costs)×(1−ConversionRate\_impact)$ [cite: 1319]
* B4. [cite_start]Đề xuất ngưỡng tối ưu: Hệ thống chạy mô phỏng với nhiều giá trị τ khác nhau và đề xuất ngưỡng τ* mang lại tổng lợi nhuận kỳ vọng cao nhất. [cite: 1320]

#### [cite_start]2.3 Giao diện người dùng [cite: 1321]
[cite_start]Bố cục chung: Một Dashboard mô phỏng chính sách (Policy Simulation Dashboard) tương tác cao. [cite: 1322]

##### [cite_start]2.3.1 Nội dung nhập liệu [cite: 1323]

[Image: Simulation Parameters Input UI]

[cite_start]Các trường thông tin: [cite: 1324]

| # | Tên trường                  | Loại UI          | Mô tả chi tiết                                                                                                                                                                                                                                                                                                                           |
| :- | :-------------------------- | :--------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Ngưỡng rủi ro (Risk Threshold τ) | Thanh trượt (Slider) | [cite_start]Đây là công cụ tương tác chính. [cite: 1325] [cite_start]Người quản lý kéo thanh trượt từ 0 đến 100 để thiết lập ngưỡng rủi ro. [cite: 1325] [cite_start]Khi giá trị thay đổi, các biểu đồ và số liệu bên dưới sẽ cập nhật theo thời gian thực. [cite: 1325]                                                                                                |
| 2 | Chi phí xử lý trả hàng       | Textbox (số)     | [cite_start]Cho phép người dùng nhập chi phí ước tính cho một lần xử lý đơn hàng bị trả về (ReturnProcessingCost) để mô phỏng chính xác hơn. [cite: 1325]                                                                                                                                                                                          |
| 3 | Tỷ lệ ảnh hưởng chuyển đổi  | Textbox (số %)   | [cite_start]Cho phép người dùng nhập tỷ lệ chuyển đổi ước tính bị sụt giảm nếu áp dụng chính sách chặn COD (ConversionRate_impact). [cite: 1325]                                                                                                                                                                                                       |

##### [cite_start]2.3.2 Kết quả DSS [cite: 1326]

[Image: DSS Results - Expected Profit Chart and Business Impact Chart]
[Image: DSS Results - System Recommendation KPI]

[cite_start]Các trường thông tin: [cite: 1327]

| # | Tên trường                  | Loại UI                             | Mô tả chi tiết                                                                                                                                                                                                                                                                                       |
| :- | :-------------------------- | :---------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Biểu đồ Lợi nhuận kỳ vọng   | Biểu đồ đường (Line Chart)          | [cite_start]Trục hoành là Ngưỡng rủi ro τ, trục tung là Tổng lợi nhuận kỳ vọng. [cite: 1328] [cite_start]Biểu đồ này cho thấy lợi nhuận thay đổi như thế nào khi người quản lý siết chặt hoặc nới lỏng chính sách. [cite: 1328]                                                                                                   |
| 2 | Biểu đồ Tác động kinh doanh | Biểu đồ cột đôi (Dual-axis Bar Chart) | Hiển thị hai thông tin cùng lúc khi τ thay đổi: (1) Số lượng đơn hàng bị áp dụng chính sách (cột trái); (2) [cite_start]Doanh thu bị ảnh hưởng ước tính (cột phải). [cite: 1328]                                                                                                                                  |
| 3 | Đề xuất của hệ thống       | Thẻ KPI (KPI Card)                  | [cite_start]Hiển thị rõ ràng: ""Ngưỡng rủi ro tối ưu đề xuất (τ*) là 75, ước tính bảo vệ được $X,XXX lợi nhuận mỗi tháng"". [cite: 1328] [cite_start]Con số này được tô đậm và nổi bật. [cite: 1328]                                                                                                                            |

##### [cite_start]2.3.3 Phân tích/Gợi ý [cite: 1329]

[cite_start][Image: Automatic Policy Table UI] [cite: 1331]
[cite_start][Image: Deploy Policy Button UI] [cite: 1330]

[cite_start]Các trường thông tin: [cite: 1333]

| # | Tên trường                  | Loại UI | Mô tả chi tiết                                                                                                                                                                                                                                                                              |
| :- | :-------------------------- | :------ | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1 | Bảng chính sách tự động     | Bảng (Table) | Bảng (Table) \| [cite_start]Một bảng đơn giản, tự động cập nhật để diễn giải ý nghĩa của ngưỡng τ đang được chọn. [cite: 1334] [cite_start]Ví dụ, nếu người dùng chọn τ=75: [cite: 1334] - [cite_start]Risk Score < 75: ""Hành động: Approve Normal"". [cite: 1334] - [cite_start]Risk Score ≥ 75: ""Hành động: Block COD Payment"". [cite: 1334] |
| 2 | Nút "Triển khai chính sách" | Button  | [cite_start]Sau khi đã chọn được ngưỡng τ ưng ý thông qua mô phỏng, người quản lý có thể nhấn nút này để áp dụng quyết định của mình vào hệ thống vận hành thực tế. [cite: 1334]                                                                                                                        |

### [cite_start]3. Phân Khúc Khách Hàng và Gợi Ý Tiếp Thị Mục Tiêu (Customer Segmentation and Targeted Marketing Simulation) ( Huy ) [cite: 1335]

#### [cite_start]3.1.1 Mục tiêu [cite: 1336]
[cite_start]Mục tiêu của tính năng này là nhóm các khách hàng thành những phân khúc riêng biệt dựa trên hành vi mua hàng của họ. [cite: 1337]
[cite_start]Từ đó, hệ thống sẽ đề xuất các hành động marketing cụ thể và được cá nhân hóa cho từng nhóm, trả lời câu hỏi "chúng ta nên làm gì?" [cite: 1338] [cite_start]để tối đa hóa giá trị vòng đời khách hàng và doanh số bán hàng. [cite: 1339]

#### [cite_start]3.1.2 Mô hình [cite: 1340]

##### [cite_start]3.1.2.1 Chiều mô hình [cite: 1341]
[cite_start]Sử dụng chiều mô hình Prescriptive (Chỉ định/Gợi ý). [cite: 1342] [cite_start]Mô hình không chỉ dự đoán mà còn đưa ra các khuyến nghị hành động cụ thể mà doanh nghiệp nên thực hiện để đạt được mục tiêu kinh doanh. [cite: 1342]

##### [cite_start]3.1.2.2 Cách thức hoạt động [cite: 1343]
[cite_start]Hệ thống hoạt động theo một quy trình hai bước: [cite: 1344]
* [cite_start]Bước 1: Phân khúc khách hàng (Customer Segmentation) [cite: 1345]
    * [cite_start]Đầu vào: Sử dụng điểm RFM (Recency, Frequency, Monetary) của mỗi khách hàng được tính toán từ mô hình dự báo và dữ liệu giao dịch cơ bản (CustomerID). [cite: 1346]
    * [cite_start]Phương pháp: Áp dụng thuật toán gom cụm K-Means Clustering trên bộ dữ liệu điểm RFM để tự động tạo ra từ 3-5 phân khúc khách hàng riêng biệt (ví dụ: "Champions," "Loyal Customers," "At-Risk"). [cite: 1347]
    * [cite_start]Đầu ra: Một danh sách các phân khúc khách hàng, mỗi phân khúc bao gồm một nhóm các CustomerID có đặc điểm hành vi tương đồng. [cite: 1348]
* [cite_start]Bước 2: Gợi ý hành động & Sản phẩm (Action & Product Recommendation) [cite: 1349]
    * [cite_start]Đầu vào: Dữ liệu giao dịch chi tiết (InvoiceNo, StockCode) của các khách hàng thuộc một phân khúc cụ thể đã được xác định ở bước 1. [cite: 1350]
    * [cite_start]Phương pháp: Chạy Phân tích Giỏ hàng (Market Basket Analysis) bằng Thuật toán Apriori trên dữ liệu của từng phân khúc để xác định các sản phẩm thường được mua cùng nhau. [cite: 1351]
    * [cite_start]Đầu ra: Một bộ các hành động được đề xuất cho mỗi phân khúc và danh sách các gói sản phẩm (product bundles) gợi ý dựa trên các quy tắc kết hợp đã tìm thấy. [cite: 1352]

#### [cite_start]3.1.3 Giao diện người dùng [cite: 1353]
[cite_start]Bố cục chung: Giao diện bao gồm một khu vực điều khiển để chạy phân tích. [cite: 1354]
[cite_start]Sau khi hoàn tất, kết quả sẽ hiển thị tổng quan về các phân khúc. [cite: 1355]
[cite_start]Người dùng có thể nhấp vào từng phân khúc để xem chi tiết các gợi ý hành động và sản phẩm được đề xuất riêng cho nhóm đó. [cite: 1356]

##### [cite_start]3.1.3.1 Nội dung nhập liệu [cite: 1357]
[cite_start]Giao diện phần nhập liệu và điều khiển [cite: 1358]

[Image: Customer Segmentation & Marketing Input UI - Number of Segments]
[Image: Customer Segmentation & Marketing Input UI - Select Segment]
[Image: Customer Segmentation & Marketing Input UI - Min Support Slider]

| # | Tên thành phần               | Mô tả chi tiết                                                                                                                                                                      |
| :- | :--------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Số lượng phân khúc khách hàng | [cite_start]Cho phép chọn số lượng phân khúc khách hàng mong muốn [cite: 1359]                                                                                                                 |
| 2 | Min support                  | [cite_start]Cho phép người dùng tìm nhiều/ít quy tắc để tìm khả năng mua thêm sản phẩm [cite: 1359]                                                                                                |

##### [cite_start]3.1.3.2 Kết quả DSS [cite: 1360]
[cite_start]Kết quả được trình bày thành hai phần: tổng quan các phân khúc và chi tiết cho từng phân khúc được chọn. [cite: 1361]
[cite_start]Giao diện kết quả phân khúc và gợi ý hành động [cite: 1362]

[Image: Customer Segmentation Results - Overview Table]
[Image: Customer Segmentation Results - Segment Characteristics and Marketing Actions]
[Image: Customer Segmentation Results - Recommended Bundles and Association Rules Table]

| #   | Tên thành phần                          | Mô tả chi tiết                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| :-- | :-------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| (1) | Customer Segments Overview (Tổng quan các phân khúc) | [cite_start]Hiển thị một loạt các ""thẻ"" (cards), mỗi thẻ đại diện cho một phân khúc khách hàng. [cite: 1363] [cite_start]Mỗi thẻ sẽ tóm tắt kích thước (số lượng khách hàng) và giá trị (tổng doanh thu) của nhóm đó. [cite: 1363] [cite_start]Ví dụ: ""Champions"", ""At-Risk"", ""New Customers"". [cite: 1363]                                                                                                                                                                            |
| (2) | Recommended Actions (Hành động gợi ý)   | [cite_start]Khi người dùng chọn một phân khúc, khu vực này sẽ hiển thị các chiến lược marketing được đề xuất, được thiết kế riêng cho nhóm đó. [cite: 1363] [cite_start]• Ví dụ cho nhóm ""At-Risk"": ""Gửi email 'chúng tôi nhớ bạn' kèm theo mã giảm giá 15%."". [cite: 1363]                                                                                                                                                                                                                         |
| (3) | Recommended Product Bundles (Gói sản phẩm gợi ý) | [cite_start]Dựa trên kết quả Phân tích Giỏ hàng, khu vực này sẽ liệt kê các cặp sản phẩm thường được mua cùng nhau bởi khách hàng trong phân khúc được chọn. [cite: 1363] [cite_start]• Ví dụ: ""Gợi ý bán chéo: Khách hàng mua 'WHITE HANGING HEART T-LIGHT HOLDER' thường mua kèm 'PARTY BUNTING'."" [cite: 1363] |

#### [cite_start]3.1.3.3 Phân tích & Gợi ý [cite: 1364]
[cite_start]Phần này cung cấp thêm ngữ cảnh và lý do đằng sau các gợi ý được đưa ra. [cite: 1365]

[Image: Customer Segmentation Analysis - Segment Characteristics]
[Image: Customer Segmentation Analysis - Explanation of Metrics (Support, Confidence, Lift)]

| # | Tên thành phần                  | Mô tả chi tiết                                                                                                                                                                                                                                                                                                                                                        |
| :- | :------------------------------ | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Segment Characteristics (Đặc điểm phân khúc) | [cite_start]Một đoạn mô tả ngắn về hành vi của phân khúc đang được chọn. [cite: 1366] [cite_start]• Ví dụ cho nhóm ""At-Risk"": ""Đây là nhóm khách hàng đã lâu không quay lại mua hàng và có tần suất mua thấp. [cite: 1366] [cite_start]Họ cần một cú hích để tái tương tác."" [cite: 1366]                                                                                                   |
| 2 | Rule Confidence (Độ tin cậy của luật) | [cite_start]Bên cạnh mỗi gói sản phẩm gợi ý, hệ thống sẽ hiển thị chỉ số ""Confidence"" (Độ tin cậy) của luật kết hợp đó. [cite: 1366] [cite_start]• Ví dụ: ""(Confidence: 65%)"" - điều này cho thấy có 65% khả năng khách hàng mua sản phẩm A cũng sẽ mua sản phẩm B, đáp ứng quy tắc nghiệp vụ về độ tin cậy tối thiểu. [cite: 1366] |

### [cite_start]4. Công cụ Bán chéo & Đề xuất Sản phẩm (Lâm) [cite: 1367]

#### [cite_start]2.4.1 Mục tiêu [cite: 1368]
[cite_start]Giúp nhân viên bán hàng tạo ra các đề xuất sản phẩm dựa trên mẫu mua chung và hành vi khách hàng để tăng cơ hội bán chéo và tối ưu hóa chiến lược bố trí sản phẩm. [cite: 1369]

#### [cite_start]2.4.2 Mô hình [cite: 1370]

##### [cite_start]2.4.2.1 Chiều mô hình [cite: 1371]
[cite_start]Sử dụng mô hình Prediction - dự đoán sản phẩm khách hàng có khả năng mua kèm dựa trên các mẫu mua hàng trong quá khứ. [cite: 1372]

##### [cite_start]2.4.2.2 Cách thức hoạt động [cite: 1373]
* [cite_start]Basket Creation: GROUP BY InvoiceNo để tạo market baskets từ transaction data [cite: 1374]
* [cite_start]Association Rules Mining: Tìm frequent co-purchase patterns với minimum support threshold [cite: 1375]
* [cite_start]Metrics Calculation: [cite: 1376]
    * [cite_start]$Support(A,B)$ = Transactions chứa cả A và B / Total transactions [cite: 1377]
    * [cite_start]$Confidence(A→B)$ = Support(A,B) / Support(A) [cite: 1378]
    * [cite_start]$Lift(A→B)$ = Confidence(A→B) / Support(B) [cite: 1379]
* [cite_start]Customer Segmentation: RFM analysis để personalize recommendations based on customer value tier [cite: 1380]
* [cite_start]Ranking: Sắp xếp recommendations theo Confidence × Lift score [cite: 1381]

#### [cite_start]2.4.3 Giao diện người dùng [cite: 1382]
[cite_start]Bố cục chung: Trang với biểu mẫu nhập liệu phía trên, bảng đề xuất ở giữa, và trực quan hóa mạng kết hợp cùng thông tin chi tiết ở dưới. [cite: 1383]

##### [cite_start]2.4.3.1 Nội dung nhập liệu [cite: 1384]

[Image: Cross-sell Recommendations Input UI]

[cite_start]Các trường thông tin: [cite: 1385]

| # | Tên trường          | Loại UI                     | Mô tả chi tiết                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| :- | :------------------ | :-------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Tìm kiếm Sản phẩm | Text Input (Autocomplete)   | [cite_start]Trường nhập văn bản để tìm kiếm sản phẩm: [cite: 1386] - [cite_start]Hướng dẫn: ""Tìm theo mã sản phẩm hoặc tên sản phẩm"" [cite: 1386] - [cite_start]Tự động hoàn thành: Hiển thị danh sách gợi ý khi gõ từ 2 ký tự trở lên, [cite: 1386] - [cite_start]Bắt buộc: Phải có giá trị để tạo đề xuất, [cite: 1386] - [cite_start]Ví dụ: ""85123A"" (Product Search), [cite: 1386] - [cite_start]Validation: Kiểm tra sản phẩm tồn tại trong database [cite: 1386]                                                                                                                                                                                                                                  |
| 2 | Mã Khách hàng       | Text Input (Optional)       | [cite_start]Trường nhập văn bản cho mã khách hàng: [cite: 1386] - [cite_start]Hướng dẫn: ""Nhập mã khách hàng"" (Enter customer ID) [cite: 1386] - [cite_start]Nhãn: ""Customer ID (Optional)"" [cite: 1386] - [cite_start]Tùy chọn: Có nhãn ""(Tùy chọn)"" để người dùng biết không bắt buộc [cite: 1386] - [cite_start]Định dạng: Mã khách hàng 5 chữ số, không kiểm tra vì là tùy chọn [cite: 1386] - [cite_start]Tác dụng: Nếu nhập, recommendation sẽ được cá nhân hóa theo lịch sử khách hàng [cite: 1386]                                                                                                                                                                                                           |
| 3 | Ngưỡng Độ tin cậy   | Slider                      | [cite_start]Thanh trượt để chọn ngưỡng độ tin cậy: [cite: 1386] - [cite_start]Phạm vi: Từ 0.1 đến 1.0, bước 0.05, mặc định 0.5, [cite: 1386] - [cite_start]Hiển thị giá trị: Hiện giá trị hiện tại bên cạnh thanh trượt, [cite: 1386] - [cite_start]Nhãn: ""Confidence Threshold: 0.5"", [cite: 1386] - [cite_start]Tác dụng: Lọc các đề xuất có confidence score dưới ngưỡng này, [cite: 1386] - [cite_start]Giải thích: Giá trị càng cao, đề xuất càng chính xác nhưng số lượng càng ít [cite: 1386]                                                                                                                                                                                                              |
| 4 | Số lượng Đề xuất    | Dropdown List               | [cite_start]Menu thả xuống để chọn số lượng đề xuất: [cite: 1386] - [cite_start]Các tùy chọn: Top 5 (mặc định), Top 10, Top 15 [cite: 1386] - [cite_start]Nhãn: ""Top N Recommendations"" [cite: 1386] - [cite_start]Tác dụng: Giới hạn số kết quả hiển thị trong bảng, [cite: 1386] - [cite_start]Ví dụ: Chọn ""Top 5"" sẽ hiển thị 5 sản phẩm được đề xuất hàng đầu [cite: 1386]                                                                                                                                                                                                                                                                                                                       |
| 5 | Nút Tạo Đề xuất     | Button                      | [cite_start]Nút bấm để tạo đề xuất cross-sell: [cite: 1386] - [cite_start]Văn bản: ""Generate Recommendations"", [cite: 1386] - [cite_start]Trạng thái tải: Hiện biểu tượng xoay khi đang xử lý, [cite: 1386] - [cite_start]Điều kiện: Chỉ hoạt động (enabled) khi đã chọn sản phẩm, [cite: 1386] - [cite_start]Màu sắc: Nền xanh teal (teal background), [cite: 1386] - [cite_start]Action: Gọi API để lấy danh sách sản phẩm được đề xuất dựa trên các tham số đã chọn [cite: 1386]                                                                                                                                                                                                                                  |

##### [cite_start]2.4.3.2 Kết quả DSS [cite: 1387]

[Image: Cross-sell DSS Results - Product Recommendations Table and Target Segments]
[Image: Cross-sell DSS Results - Network Graph Visualization]

[cite_start]Các trường thông tin: [cite: 1388]

| # | Tên trường                              | Loại UI                             | Mô tả chi tiết                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| :- | :-------------------------------------- | :---------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1 | Bảng Đề xuất Sản phẩm                   | Bảng (Table)                        | [cite_start]Bảng hiển thị danh sách sản phẩm được đề xuất: [cite: 1389] - Các cột: Product Code \| Description \| Support \| Confidence \| Lift \| [cite_start]Estimated Impact, [cite: 1389] - [cite_start]Dữ liệu: Top N sản phẩm xếp hạng theo điểm (confidence × lift), [cite: 1389] - [cite_start]Định dạng số: [cite: 1389] + [cite_start]Support/Confidence/Lift hiển thị dạng phần trăm (%) và tỷ lệ với 2 chữ số thập phân [cite: 1389] + [cite_start]Ví dụ: Support: 0.15 (15%), Confidence: 0.45 (45%), Lift: 2.30, [cite: 1389] - [cite_start]Sắp xếp: Mặc định theo Confidence × Lift giảm dần, [cite: 1389] - [cite_start]Tác động ước tính: Dự đoán tăng doanh thu nếu áp dụng đề xuất [cite: 1389]                                                                            |
| 2 | Trực quan hóa Mạng Kết hợp             | Sơ đồ Mạng lưới (Network Graph)      | [cite_start]Biểu đồ mạng lưới hiển thị mối quan hệ giữa các sản phẩm: [cite: 1389] - [cite_start]Loại: Sơ đồ mạng lưới tương tác (Interactive Network Diagram), [cite: 1389] - [cite_start]Nút (Nodes): Các sản phẩm với kích thước tỷ lệ với tần suất xuất hiện, [cite: 1389] - [cite_start]Cạnh (Edges): Liên kết kết hợp với độ dày dựa trên điểm độ tin cậy, [cite: 1389] - [cite_start]Màu sắc: [cite: 1389] + [cite_start]Sản phẩm nguồn (queried product) được làm nổi bật màu xanh dương đậm, [cite: 1389] + [cite_start]Sản phẩm được đề xuất có màu khác (cam, xanh lá), [cite: 1389] - [cite_start]Tương tác: Nhấp vào nút để xem chi tiết sản phẩm và mối quan hệ, [cite: 1389] - [cite_start]Layout: Force-directed graph để tối ưu hiển thị [cite: 1389] |
| 3 | Bảng Thông tin Phân khúc Khách hàng | Panel thông tin (Info Panel)        | [cite_start]Panel hiển thị thông tin khách hàng (chỉ khi có Customer ID): [cite: 1389] - [cite_start]Điều kiện hiển thị: Chỉ xuất hiện khi người dùng nhập mã khách hàng, [cite: 1389] - [cite_start]Thông tin hiển thị: [cite: 1389] + [cite_start]Phân loại khách hàng: High-value/Medium/Low (tính theo AOV - Average Order Value), [cite: 1389] + [cite_start]Tần suất mua hàng: Số đơn hàng trong 12 tháng gần nhất, [cite: 1389] + [cite_start]Số lượng sản phẩm độc nhất đã mua, [cite: 1389] + [cite_start]RFM Score nếu có (Recency, Frequency, Monetary), [cite: 1389] - [cite_start]Định dạng: Card hoặc Panel với icon, [cite: 1389] - [cite_start]Mục đích: Cá nhân hóa đề xuất dựa trên hành vi khách hàng [cite: 1389]                                                            |

##### [cite_start]2.4.3.3 Phân tích/Gợi ý [cite: 1390]

[Image: Cross-sell Strategy Insights UI]

[cite_start]Các trường thông tin: [cite: 1391]

| # | Tên trường                      | Loại UI                     | Mô tả chi tiết                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| :- | :------------------------------ | :-------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Cơ hội Gói sản phẩm (Bundle Opportunity) | Text Box với Icon (Package) | [cite_start]Hiển thị các sản phẩm có mối liên kết mạnh để tạo gói sản phẩm: [cite: 1392] - [cite_start]Nội dung: Phân tích các cặp/nhóm sản phẩm có high confidence & lift để đề xuất bundling, [cite: 1392] - [cite_start]Template: ""Sản phẩm {tên\_sản\_phẩm} có liên kết mạnh với {danh\_sách\_sản\_phẩm\_liên\_quan}. [cite: 1392] [cite_start]Tạo gói quà tặng theo mùa/chủ đề."" [cite: 1392] - [cite_start]Ví dụ: ""White hanging heart holders show strong association with lanterns. [cite: 1392] [cite_start]Create seasonal gift bundles."" [cite: 1392] - [cite_start]Icon: Biểu tượng gói/hộp quà màu xanh dương, [cite: 1392] - [cite_start]Màu nền: Kem nhạt (beige), [cite: 1392] - [cite_start]Mục đích: Gợi ý bundle products để tăng average order value (AOV) [cite: 1392] |
| 2 | Tác động Doanh thu (Revenue Impact) | Text Box với Icon (Revenue) | [cite_start]Ước tính tác động tài chính nếu triển khai các đề xuất: [cite: 1392] - [cite_start]Nội dung: Tính toán và hiển thị potential revenue lift từ cross-sell recommendations, [cite: 1392] - [cite_start]Template: ""Triển khai các đề xuất này có thể tăng giá trị giỏ hàng {min\_percent}-{max\_percent}% cho các phân khúc mục tiêu."" [cite: 1392] - [cite_start]Ví dụ: ""Implementing these recommendations could increase basket size by 15-25% for targeted segments."" [cite: 1392] - [cite_start]Phương pháp tính: Dựa trên historical data về bundle purchase rates và average lift, [cite: 1392] - [cite_start]Icon: Biểu tượng tiền/biểu đồ tăng trưởng màu xanh lá, [cite: 1392] - [cite_start]Màu nền: Kem nhạt (beige), [cite: 1392] - [cite_start]Mục đích: Quantify business impact để justify investment [cite: 1392] |
| 3 | Chiến lược Thời điểm (Timing Strategy) | Text Box với Icon (Calendar) | [cite_start]Đề xuất thời điểm tối ưu để triển khai recommendations: [cite: 1392] - [cite_start]Nội dung: Phân tích seasonal patterns và customer behavior để suggest best timing, [cite: 1392] - [cite_start]Template: ""Triển khai các đề xuất trong {thời\_gian} để đạt hiệu quả tối đa với khách hàng {đặc\_điểm}."" [cite: 1392] - [cite_start]Ví dụ: ""Deploy recommendations during Q4 for maximum effectiveness with gift-oriented customers."" [cite: 1392] - [cite_start]Logic: Dựa trên historical seasonal trends, holiday patterns, customer purchase cycles, [cite: 1392] - [cite_start]Icon: Biểu tượng lịch/đồng hồ màu cam, [cite: 1392] - [cite_start]Màu nền: Kem nhạt (beige), [cite: 1392] - [cite_start]Mục đích: Timing recommendations để maximize conversion rate và effectiveness [cite: 1392] |