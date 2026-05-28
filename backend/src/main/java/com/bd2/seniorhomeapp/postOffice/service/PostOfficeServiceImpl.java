package com.bd2.seniorhomeapp.postOffice.service;

import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeRequest;
import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeUpdateRequest;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.repository.PostOfficeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostOfficeServiceImpl implements PostOfficeService {
    private final PostOfficeRepository postOfficeRepository;

    @Override
    public List<PostOffice> getPostOffices(Long id, String city) {
        if (id != null) {
            PostOffice postOffice = postOfficeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("PostOffice not found"));
            return List.of(postOffice);
        }
        if (city != null && !city.isBlank()) {
            return postOfficeRepository.findAllByCity(city);
        }
        return postOfficeRepository.findAll();
    }

    @Override
    public PostOffice createPostOffice(PostOfficeRequest postOfficeRequest) {
        PostOffice postOffice = new PostOffice();
        postOffice.setCity(postOfficeRequest.city());
        postOffice.setPostalCode(postOfficeRequest.postalCode());
        return postOfficeRepository.save(postOffice);
    }

    @Override
    public PostOffice updatePostOffice(PostOfficeUpdateRequest postOfficeUpdateRequest, Long id) {
        PostOffice postOffice = postOfficeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PostOffice not found"));

        if(postOfficeUpdateRequest.city() != null && !postOfficeUpdateRequest.city().isBlank()) {
            postOffice.setCity(postOfficeUpdateRequest.city());
        }
        if(postOfficeUpdateRequest.postalCode() != null && !postOfficeUpdateRequest.postalCode().isBlank()) {
            postOffice.setPostalCode(postOfficeUpdateRequest.postalCode());
        }
        return postOfficeRepository.save(postOffice);
    }

    @Override
    public void deletePostOffice(Long id) {
        if (!postOfficeRepository.existsById(id)) {
            throw new RuntimeException("PostOffice not found");
        }
        postOfficeRepository.deleteById(id);
    }
}
