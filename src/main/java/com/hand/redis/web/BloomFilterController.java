package com.hand.redis.web;


import com.hand.redis.service.BloomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class BloomFilterController {


    @Autowired
    private BloomFilterService bloomFilterService;

    @RequestMapping("/bloom/idExists")
    public boolean ifExists(int id) {
        return bloomFilterService.userIdExists(id);
    }


}
