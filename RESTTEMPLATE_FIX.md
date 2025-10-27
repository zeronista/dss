# RestTemplate Configuration - Fix Guide

## ❌ Lỗi ban đầu

```
Parameter 2 of constructor in com.g5.dss.service.ReturnRiskService 
required a bean of type 'org.springframework.web.client.RestTemplate' 
that could not be found.
```

## ✅ Giải pháp

Đã tạo `RestTemplateConfig.java` để định nghĩa bean RestTemplate.

### File: `config/RestTemplateConfig.java`

```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30 seconds
        factory.setReadTimeout(30000);    // 30 seconds
        
        return new RestTemplate(factory);
    }
}
```

## 🎯 Chức năng

RestTemplate được sử dụng để:
- Gọi Model Service API (Python FastAPI)
- HTTP requests đến external services
- Timeout: 30 seconds (connect & read)

## 🔧 Sử dụng

Trong các Service classes, inject RestTemplate:

```java
@Service
public class ReturnRiskService {
    
    private final RestTemplate restTemplate;
    
    public ReturnRiskService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void callModelService() {
        String url = "http://localhost:8000/predict";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    }
}
```

## 📝 Configuration trong application.yml

```yaml
model-service:
  base-url: http://localhost:8000
  timeout: 30000
```

## ✅ Hoàn tất

Chạy lại application:
```bash
mvn spring-boot:run
```

Lỗi đã được fix! 🎉
