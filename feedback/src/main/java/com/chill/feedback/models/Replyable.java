package com.chill.feedback.models;

import java.util.UUID;

public interface Replyable {
    UUID getParentId();
    void setParentId(UUID parentId);
}
