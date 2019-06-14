package com.vvvTeam.yuglightservice.IntegrationTests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvTeam.yuglightservice.service.request.and.response.report.ReportResponce;
import jdk.nashorn.internal.runtime.regexp.joni.MatcherFactory;
import org.hamcrest.text.IsEmptyString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.regex.Matcher;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("production")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("MainDisp")
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Все филды объектов, необходимые для отчета")
    public void reportTypeTest() throws Exception{

        final String res = "[{\"type\":\"lamp\",\"fields\":[\"v_ac\",\"i_ac\",\"temperature\",\"vdcboard\",\"latitude\",\"longitude\",\"brightness\",\"state\",\"date_of_changed\",\"id\",\"object_name\",\"cp_id\"]},{\"type\":\"controlpoint\",\"fields\":[\"id\",\"objectName\"]}]";
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(get("/report/type"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(200))
                .andExpect(content().string(res));
    }

    @Test
    public void testObjects() throws Exception{
      mockMvc.perform(get("/report/objects")).andDo(print());

    }


    @Test
    @Disabled
    public void reportTest() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(get("/report/allstates")
                .param("datefrom", "2019-03-04T16:12:00Z")
                .param("dateto", "2019-03-04T17:00:00Z")
                .param("objectsid", "4")
                .param("tags", "v_ac,i_ac,date_of_changed")
                .param("limit","100")
        )       .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
        ReportResponce value = mapper.readValue(mvcResult.getResponse().getContentAsString(), ReportResponce.class);
        Assertions.assertEquals(value.getBody().get(0).getStates().size(),100l);
    }

    /**
     * Incorrect date_of_changed order
     * @throws Exception
     */
    @Test
    @DisplayName("Некорректный порядок дат")
    public void DateCrash() throws Exception{

        mockMvc.perform(get("/report/allstates")
                .param("datefrom", "2018-02-01T00:00:00Z")
                .param("dateto", "2018-01-01T00:00:00Z")
                .param("objectsid", "1,2")
                .param("tags", "v_ac,i_ac")
        ).andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(400))
                .andExpect(content().string("date_of_changed wrong order"));

    }

    @Test
    @DisplayName("Путой id")
    public void IdsCrash() throws Exception{

        mockMvc.perform(get("/report/allstates")
                .param("datefrom", "2018-01-01T00:00:00Z")
                .param("dateto", "2018-02-01T00:00:00Z")
                .param("objectsid", "")
                .param("tags", "v_ac,i_ac")
        ).andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(400))
                .andExpect(content().string("the field(tags and id) cannot be empty"));




    }

    /**
     * если нет таких тэгов - вернет "400 Bad Request"
     * @throws Exception
     */
    @Test
    public void TagsCrash() throws Exception{
        mockMvc.perform(get("/report/allstates")
                .param("datefrom", "2018-01-01T00:00:00Z")
                .param("dateto", "2018-02-01T00:00:00Z")
                .param("objectsid", "1,2,3")
                .param("tags", "vac")
        ).andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(400));

    }


    /**
     *
     * @throws Exception
     */
    @WithUserDetails("MainDisp")
    @Test
    public void getObj() throws Exception{
        mockMvc.perform(get("/report/objects"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }
}
