# ✅ Sửa Lỗi Trang Segmentation

## 🐛 Lỗi Gốc

**Error**: `TemplateProcessingException` tại dòng 240 trong `segmentation_overview.html`

```
Only variable expressions returning numbers or booleans are allowed in this context, 
any other datatypes are not trusted in the context of this expression
```

**Nguyên nhân**: 
- Thymeleaf không cho phép string trong event handler `th:onclick`
- Code lỗi: `th:onclick="'window.location.href=\'/segmentation/segment/' + ${segment.segmentName} + '\''"`
- Vì lý do bảo mật, Thymeleaf chỉ chấp nhận number/boolean trong onclick, không chấp nhận string concatenation

## 🔧 Giải Pháp

### 1. Sửa Template (segmentation_overview.html)

**Thay đổi từ:**
```html
<div th:each="segment : ${segments}" 
     th:class="'segment-card ' + ${segment.segmentName.toLowerCase()}"
     th:onclick="'window.location.href=\'/segmentation/segment/' + ${segment.segmentName} + '\''">
```

**Sang:**
```html
<div th:each="segment : ${segments}" 
     th:class="'segment-card ' + ${segment.segmentName.toLowerCase()}"
     th:data-segment="${segment.segmentName}"
     style="cursor: pointer;">
```

**Thêm JavaScript handler:**
```javascript
// Add click handlers for segment cards
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.segment-card').forEach(card => {
        card.addEventListener('click', function() {
            const segmentName = this.getAttribute('data-segment');
            window.location.href = '/marketing/segment/' + segmentName;
        });
    });
});
```

### 2. Cập nhật Controller Routes

**File**: `SegmentationController.java`

**Thay đổi mapping từ:**
```java
@Controller
@RequestMapping("/segmentation")
public class SegmentationController {
    @GetMapping  // /segmentation
```

**Sang:**
```java
@Controller
@RequestMapping("/marketing")
public class SegmentationController {
    @GetMapping("/segments")  // /marketing/segments
```

## 📍 URL Mới

| Trang | URL Cũ | URL Mới |
|-------|---------|---------|
| Tổng quan phân khúc | `/segmentation` | `/marketing/segments` |
| Chi tiết phân khúc | `/segmentation/segment/{name}` | `/marketing/segment/{name}` |
| Market Basket | `/segmentation/market-basket` | `/marketing/market-basket` |

## ✅ Kết Quả

1. ✅ Lỗi Thymeleaf đã được sửa
2. ✅ URL đã chuyển sang `/marketing/segments` (phù hợp với sidebar)
3. ✅ Click handler hoạt động bằng JavaScript thuần
4. ✅ An toàn và tuân thủ Thymeleaf security policy

## 🚀 Cách Sử Dụng

```
http://localhost:8080/marketing/segments
```

Hoặc click vào "Marketing Segments" trong sidebar.

## 🎯 Best Practice

**Thymeleaf Event Handlers:**
- ✅ DO: Dùng `data-*` attributes + JavaScript listener
- ❌ DON'T: Dùng string concatenation trong `th:onclick`

**Ví dụ:**
```html
<!-- ❌ WRONG -->
<div th:onclick="'location.href=\'/page/' + ${id} + '\''">

<!-- ✅ CORRECT -->
<div th:data-id="${id}" onclick="goToPage(this)">
<script>
function goToPage(elem) {
    location.href = '/page/' + elem.dataset.id;
}
</script>
```

---

**Sửa bởi**: GitHub Copilot  
**Ngày**: October 27, 2025  
**Liên quan**: SEGMENTATION_GUIDE.md
