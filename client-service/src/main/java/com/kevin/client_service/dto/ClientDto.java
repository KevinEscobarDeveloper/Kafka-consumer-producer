package com.kevin.client_service.dto;

import java.util.List;

public record ClientDto(String clientId,
                        String name,
                        String address,
                        Boolean isActive,
                        List<String> purchaseHistory
) {
}
