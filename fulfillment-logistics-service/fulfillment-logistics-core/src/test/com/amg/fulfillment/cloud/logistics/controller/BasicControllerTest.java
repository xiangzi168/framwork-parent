package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class BasicControllerTest extends TestCase {

 /*   @Autowired
    private WebApplicationContext webApplicationContext ;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetBasicListInfo() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/testGrpc").contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("page", "1").param("row", "10");
        String contentAsString = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();
        Assert.assertTrue(contentAsString.contains("100200")&&contentAsString.contains("请求成功"));
    }


    @Test
    public void testGetBasicListInfo1() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/testGrpcamg").contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("page", "1");
        String content = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();
        Assert.assertTrue(content.contains("100400") && content.contains("请求页码和行数不能为空"));
    }
*/
}