package com.g5.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Online Retail data response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineRetailDTO {
    
    private String id;
    private String invoiceNo;
    private String stockCode;
    private String description;
    private Integer quantity;
    private String invoiceDate;
    private Double unitPrice;
    private Integer customerId;
    private String country;
    private Double totalAmount;
}
