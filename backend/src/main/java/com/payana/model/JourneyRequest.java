package com.payana.model;

import jakarta.validation.constraints.NotBlank;

public record JourneyRequest(@NotBlank String from, @NotBlank String to, String mode) {}
