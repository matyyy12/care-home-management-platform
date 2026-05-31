package com.bd2.seniorhomeapp.owner.service;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.service.AddressService;
import com.bd2.seniorhomeapp.owner.dto.request.OwnerRequest;
import com.bd2.seniorhomeapp.owner.dto.request.OwnerUpdateRequest;
import com.bd2.seniorhomeapp.owner.entity.Owner;
import com.bd2.seniorhomeapp.owner.repository.OwnerRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final AddressService addressService;
    private final SeniorHomeRepository seniorHomeRepository;

    @Override
    public List<Owner> getAllOwners(Long id) {
        if (id != null) {
            Owner owner = ownerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Owner not found"));
            return List.of(owner);
        }
        return ownerRepository.findAll();
    }

    @Override
    public Owner createOwner(OwnerRequest ownerRequest) {
        Address address = addressService.findOrCreateAddress(ownerRequest.address());
        SeniorHome seniorHome = seniorHomeRepository.findById(ownerRequest.seniorHomeId())
                .orElseThrow(() -> new RuntimeException("SeniorHome not found"));

        Owner owner = new Owner();
        owner.setName(ownerRequest.name());
        owner.setSurname(ownerRequest.surname());
        owner.setSeniorHome(seniorHome);
        owner.setAddress(address);
        return ownerRepository.save(owner);
    }

    @Override
    public Owner updateOwner(Long id, OwnerUpdateRequest ownerUpdateRequest) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (ownerUpdateRequest.name() != null) {
            owner.setName(ownerUpdateRequest.name());
        }
        if (ownerUpdateRequest.surname() != null) {
            owner.setSurname(ownerUpdateRequest.surname());
        }
        if (ownerUpdateRequest.seniorHomeId() != null) {
            SeniorHome seniorHome = seniorHomeRepository.findById(ownerUpdateRequest.seniorHomeId())
                    .orElseThrow(() -> new RuntimeException("SeniorHome not found"));
            owner.setSeniorHome(seniorHome);
        }
        if (ownerUpdateRequest.address() != null) {
            owner.setAddress(addressService.findOrCreateAddress(ownerUpdateRequest.address()));
        }
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteOwner(Long id) {
        ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        ownerRepository.deleteById(id);
    }

}
