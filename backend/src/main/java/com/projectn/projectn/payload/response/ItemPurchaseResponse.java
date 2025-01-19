package com.projectn.projectn.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemPurchaseResponse {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("shop_item_id")
    private UUID shopItemId;

    @JsonProperty("purchase_at")
    private String purchaseAt;
}
