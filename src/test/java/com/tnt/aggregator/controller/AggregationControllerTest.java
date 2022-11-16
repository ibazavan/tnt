package com.tnt.aggregator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnt.aggregator.service.AggregationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AggregationController.class})
class AggregationControllerTest {
    private final String AGGREGATION_ENDPOINT = "/aggregation";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private AggregationService aggregationService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void whenParametersCorrectReturn200() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                .get(AGGREGATION_ENDPOINT + "?pricing=EN&track=109347264&shipments=109347264")).andReturn().getResponse();
        Assertions.assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @Test
    void whenParametersAreMissingReturn400() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                .get(AGGREGATION_ENDPOINT + "?pricing=XxxX&track=10923232347264")).andReturn().getResponse();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),response.getStatus());
    }

}