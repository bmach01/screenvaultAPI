package com.screenvault.screenvaultAPI.admin;

import java.util.UUID;

public record ManageObjectRequestBody(
        UUID postId,
        UUID commentId
) {
}
