package com.bd2.seniorhomeapp.postOffice.service;

import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeRequest;
import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeUpdateRequest;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;

import java.util.List;

public interface PostOfficeService {
    List<PostOffice> getPostOffices(Long id, String city);
    PostOffice createPostOffice(PostOfficeRequest postOfficeRequest);
    PostOffice updatePostOffice(PostOfficeUpdateRequest postOfficeUpdateRequest, Long id);
    void deletePostOffice(Long id);
}
