package com.cydrag.fridgeapp.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TokenRefreshRequest {

    private String refreshToken;
}
