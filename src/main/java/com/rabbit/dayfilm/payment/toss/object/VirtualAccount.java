package com.rabbit.dayfilm.payment.toss.object;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 가상계좌 결제 정보
 */
@Data
public class VirtualAccount {
    private String accountType;
    private String accountNumber;
    private TossCardCode bank;
    private String customerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime dueDate;
    private RefundStatus refundStatus;
    private Boolean expired;
    private String settlementsStatus;
}
