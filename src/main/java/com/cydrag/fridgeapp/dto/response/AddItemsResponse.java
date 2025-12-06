package com.cydrag.fridgeapp.dto.response;

import java.util.List;

public record AddItemsResponse(
        List<CreatedItemResponse> items
) {}
