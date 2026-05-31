package com.bd2.seniorhomeapp.owner.dto.request;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record OwnerUpdateRequest(
        @Size(max = 30)
        String name,

        @Size(max = 30)
        String surname,

        Long seniorHomeId,

        @Valid
        AddressRequest address
) {
}
