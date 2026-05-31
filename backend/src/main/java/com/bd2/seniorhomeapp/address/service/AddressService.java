package com.bd2.seniorhomeapp.address.service;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import com.bd2.seniorhomeapp.address.dto.request.AddressUpdateRequest;
import com.bd2.seniorhomeapp.address.entity.Address;

import java.util.List;

public interface AddressService {
    Address createAddress(AddressRequest addressRequest);
    Address findOrCreateAddress(AddressRequest addressRequest);
    List<Address> findAddresses(Long id, String city);
    Address updateAddress(AddressUpdateRequest addressUpdateRequest, Long id);
    void deleteAddress(Long id);
}
