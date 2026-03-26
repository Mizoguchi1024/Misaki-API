package org.mizoguchi.misaki.pojo.dto.admin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AiBalanceResponse {
    @JsonProperty("is_available")
    private boolean isAvailable;

    @JsonProperty("balance_infos")
    private List<BalanceInfo> balanceInfos;

    @Data
    public static class BalanceInfo {

        private String currency;

        @JsonProperty("total_balance")
        private String totalBalance;

        @JsonProperty("granted_balance")
        private String grantedBalance;

        @JsonProperty("topped_up_balance")
        private String toppedUpBalance;
    }
}
