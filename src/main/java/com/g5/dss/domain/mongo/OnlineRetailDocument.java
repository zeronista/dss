package com.g5.dss.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * MongoDB Document cho Online Retail data
 * Collection: DSS
 */
@Document(collection = "DSS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineRetailDocument {
    
    @Id
    private String id;
    
    @Field("InvoiceNo")
    private String invoiceNo;
    
    @Field("StockCode")
    private String stockCode;
    
    @Field("Description")
    private String description;
    
    @Field("Quantity")
    private Integer quantity;
    
    @Field("InvoiceDate")
    private String invoiceDate; // hoặc LocalDateTime nếu đã parse
    
    @Field("UnitPrice")
    private Double unitPrice;
    
    @Field("CustomerID")
    private Integer customerId;
    
    @Field("Country")
    private String country;
    
    // Computed property
    public Double getTotalAmount() {
        if (quantity != null && unitPrice != null) {
            return quantity * unitPrice;
        }
        return 0.0;
    }
}
