package com.bd2.seniorhomeapp.owner.service;

import com.bd2.seniorhomeapp.owner.dto.request.OwnerRequest;
import com.bd2.seniorhomeapp.owner.dto.request.OwnerUpdateRequest;
import com.bd2.seniorhomeapp.owner.entity.Owner;

import java.util.List;

public interface OwnerService {
    List<Owner> getAllOwners(Long id);
    Owner createOwner(OwnerRequest ownerRequest);
    Owner updateOwner(Long id, OwnerUpdateRequest ownerUpdateRequest);
    void deleteOwner(Long id);
}
