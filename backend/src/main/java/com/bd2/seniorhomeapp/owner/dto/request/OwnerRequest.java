package com.bd2.seniorhomeapp.owner.dto.request;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OwnerRequest (
        @NotBlank
        @Size(max = 30)
        String name,

        @NotBlank
        @Size(max = 30)
        String surname,

        @NotNull
        Long seniorHomeId,

        @Valid
        @NotNull
        AddressRequest address
){
}
