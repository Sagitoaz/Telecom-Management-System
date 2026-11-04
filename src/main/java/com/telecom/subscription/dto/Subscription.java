package com.telecom.subscription.dto;

import com.telecom.subscription.model.SubscriptionState;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Subscription(
    UUID subscriptionId,
    UUID subscriberId,
    UUID packageId,
    int version,
    SubscriptionState state,
    boolean autoRenew,
    OffsetDateTime coverageEnd,
    OffsetDateTime cancelEffectiveAt
) {}
