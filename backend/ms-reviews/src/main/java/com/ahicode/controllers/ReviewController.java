package com.ahicode.controllers;

import com.ahicode.storage.entities.Review;
import com.ahicode.storage.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;

    @PostMapping("/test")
    public String test() {
        Review review = new Review();
        review.setUserId("1");
        review.setBookId("1");
        review.setContent("Book");

        reviewRepository.save(review);
        return "saved";
    }
}
