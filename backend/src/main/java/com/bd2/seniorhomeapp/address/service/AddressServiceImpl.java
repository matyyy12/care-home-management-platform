package com.bd2.seniorhomeapp.address.service;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import com.bd2.seniorhomeapp.address.dto.request.AddressUpdateRequest;
import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.repository.AddressRepository;
import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeRequest;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.repository.PostOfficeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final PostOfficeRepository postOfficeRepository;

    @Override
    public Address createAddress(AddressRequest addressRequest){
        return findOrCreateAddress(addressRequest);
    }

    @Override
    public Address findOrCreateAddress(AddressRequest addressRequest) {
        PostOffice postOffice = findOrCreatePostOffice(addressRequest.postOffice());

        return addressRepository.findByCityAndStreetAndNumberAndPostOffice(
                        addressRequest.city(),
                        addressRequest.street(),
                        addressRequest.number(),
                        postOffice
                )
                .orElseGet(() -> {
                    Address address = new Address();
                    address.setCity(addressRequest.city());
                    address.setNumber(addressRequest.number());
                    address.setStreet(addressRequest.street());
                    address.setPostOffice(postOffice);
                    return addressRepository.save(address);
                });
    }

    @Override
    public List<Address> findAddresses(Long id, String city){
        if(id != null) {
            Address address = addressRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Address not found"));

            return List.of(address);
        }
        if(city != null && !city.isBlank()) {
            return addressRepository.findAllByCity(city);
        }
        return addressRepository.findAll();
    }

    @Override
    public Address updateAddress(AddressUpdateRequest addressUpdateRequest, Long id){
        Address address = addressRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Address not found"));

        if (addressUpdateRequest.city() != null) {
            address.setCity(addressUpdateRequest.city());
        }
        if (addressUpdateRequest.street() != null) {
            address.setStreet(addressUpdateRequest.street());
        }
        if (addressUpdateRequest.number() != null) {
            address.setNumber(addressUpdateRequest.number());
        }
        if (addressUpdateRequest.postOffice() != null) {
            address.setPostOffice(findOrCreatePostOffice(addressUpdateRequest.postOffice()));
        }
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long id){
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found");
        }
        addressRepository.deleteById(id);
    }

    private PostOffice findOrCreatePostOffice(PostOfficeRequest postOfficeRequest) {
        return postOfficeRepository.findByPostalCode(postOfficeRequest.postalCode())
                .orElseGet(() -> {
                    PostOffice postOffice = new PostOffice();
                    postOffice.setPostalCode(postOfficeRequest.postalCode());
                    postOffice.setCity(postOfficeRequest.city());
                    return postOfficeRepository.save(postOffice);
                });
    }

}
