package com.bd2.seniorhomeapp.seniorHome.service;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.service.AddressService;
import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeRequest;
import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeUpdateRequest;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeniorHomeServiceImpl implements SeniorHomeService {

    private final SeniorHomeRepository seniorHomeRepository;
    private final AddressService addressService;

    @Override
    public List<SeniorHome> findHomes(Long id) {
        if (id != null) {
            SeniorHome seniorHome = seniorHomeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Senior home not found"));
            return List.of(seniorHome);
        }
        return seniorHomeRepository.findAll();
    }

    @Override
    public SeniorHome createHome(SeniorHomeRequest seniorHomeRequest) {
        Address address = addressService.findOrCreateAddress(seniorHomeRequest.address());
        SeniorHome seniorHome = new SeniorHome();

        seniorHome.setName(seniorHomeRequest.name());
        seniorHome.setEmail(seniorHomeRequest.email());
        seniorHome.setPhoneNumber(seniorHomeRequest.phoneNumber());
        seniorHome.setWebsite(seniorHomeRequest.website());
        seniorHome.setEstablishedDate(seniorHomeRequest.establishedDate());
        seniorHome.setAddress(address);
        return seniorHomeRepository.save(seniorHome);
    }

    @Override
    public SeniorHome updateHome(SeniorHomeUpdateRequest seniorHomeUpdateRequest, Long id) {
        SeniorHome seniorHome = seniorHomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Senior home not found"));

        if (seniorHomeUpdateRequest.name() != null) {
            seniorHome.setName(seniorHomeUpdateRequest.name());
        }
        if (seniorHomeUpdateRequest.email() != null) {
            seniorHome.setEmail(seniorHomeUpdateRequest.email());
        }
        if (seniorHomeUpdateRequest.phoneNumber() != null) {
            seniorHome.setPhoneNumber(seniorHomeUpdateRequest.phoneNumber());
        }
        if (seniorHomeUpdateRequest.website() != null) {
            seniorHome.setWebsite(seniorHomeUpdateRequest.website());
        }
        if (seniorHomeUpdateRequest.establishedDate() != null) {
            seniorHome.setEstablishedDate(seniorHomeUpdateRequest.establishedDate());
        }
        if (seniorHomeUpdateRequest.address() != null) {
            Address address = addressService.findOrCreateAddress(seniorHomeUpdateRequest.address());
            seniorHome.setAddress(address);
        }

        return seniorHomeRepository.save(seniorHome);
    }

    @Override
    public void deleteHome(Long id) {
        if (!seniorHomeRepository.existsById(id)) {
            throw new RuntimeException("Senior home not found");
        }
        seniorHomeRepository.deleteById(id);
    }
}
