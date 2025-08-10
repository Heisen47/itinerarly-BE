package com.example.itinerarly_BE.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(auth.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void startEndpointShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/start"))
                .andExpect(status().isOk())
                .andExpect(content().string("Itinerarly BE is running"));
    }
}

