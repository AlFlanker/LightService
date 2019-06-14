package com.vvvTeam.yuglightservice.IntegrationTests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvTeam.yuglightservice.domain.DTO.OrganizationResponse;
import com.vvvTeam.yuglightservice.domain.DTO.UserAdd;
import com.vvvTeam.yuglightservice.domain.DTO.WorkGroupBaseObjResponse;
import com.vvvTeam.yuglightservice.domain.DTO.registrateDTO.OrganizationRegistrate;
import com.vvvTeam.yuglightservice.domain.DTO.registrateDTO.WorkGroupDTO;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//ToDo: вынести в тестовую БД c данными
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WithUserDetails("admin")
@ActiveProfiles("production")
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    /**
     * @see #getLampFromWorkGroupTest()
     * @throws Exception
     */
    @Disabled
    @Test
    public void pageable() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations/1/lamps")
                .param("page", "0")
                .param("size", "3")
        )       .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        WorkGroupBaseObjResponse value = mapper.readValue(mvcResult.getResponse().getContentAsString(), WorkGroupBaseObjResponse.class);
        Assertions.assertEquals(3,value.getData().size());

    }

    /**
     * @see #getLampFromWorkGroupTest()
     * @throws Exception Если нет организации, то верну 400
     */
    @WithUserDetails("MainDisp")
    @Test
    public void Crashpageable() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations/100/lamps")
                .param("page", "0")
                .param("size", "3")
        )       .andDo(print())
                .andExpect(status().is(404))
                .andReturn();
    }

    /**
     * ToDo: добавить КП для тестов
     * @throws Exception
     */
    @Test
    @WithUserDetails("MainDisp")
    @DisplayName("Тест запроса КП")
    public void getCPFromWorkGroupTest() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations/objects")
                .param("id", "")
                .param("type", "CP")
        )       .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }
    /**
     * ToDo: добавить лампы для тестов
     * @throws Exception
     */
    @WithUserDetails("MainDisp")
    @Test
    @DisplayName("Тест запроса Ламп")
    public void getLampFromWorkGroupTest() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations/objects").param("id", "").param("type", "Lamp")).andDo(print())
                .andExpect(status().is(200))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @WithUserDetails("MainDisp")
    @Test
    @DisplayName("Тест запроса Юзеров")
    public void getUsersFromWorkGroupTest() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations/objects")
                .param("id", "")
                .param("type", "Users")
        )       .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Тест запроса Юзеров по Id  группы")
    public void getUsersFromWorkGroupByIdTest() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations/objects")
                .param("id", "1")
                .param("type", "Users")
        )       .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Transactional
    @WithUserDetails("Disp2")
    @Test
    @DisplayName("Тест запроса от обычного юзера")
    public void getObjectsFromWorkGroupRoleTest() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations/objects")
                .param("id", "")
                .param("type", "Users")
        )       .andDo(print())
                .andExpect(status().is(403))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }
    //ToDo:перенести на тестовую БД и немного переделать логику теста
    @Transactional
    @WithUserDetails("YugSystemAdmin")
    @Test
    @DisplayName("попытка добавления уже существующего пользователя в группу!")
    public void addNewDublUser() throws Exception{
        UserAdd data = new UserAdd();
        data.setUsername("YugSystemAdmin");
        data.setPassword("123");
        data.setEmail("a@dsd.com");
        data.setWg(1);
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/users/addnew")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(data))
        )       .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andReturn();

    }

    @Transactional
    @WithUserDetails("YugSystemAdmin")
    @Test
    @DisplayName("попытка добавления пользователя с пустым паролем в группу!")
    public void addNewErrorUser() throws Exception{
        UserAdd data = new UserAdd();
        data.setUsername("YugSystemA");
        data.setPassword("");
        data.setEmail("a@dsd.com");
        data.setWg(1);
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/users/addnew")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(data))
        )       .andDo(print())
                .andExpect(content().string("{\"passworderror\":\"пароль не может быть пустым\"}"))
                .andReturn();

    }

    @Transactional
    @WithUserDetails("YugSystemAdmin")
    @Test
    @DisplayName("Тест добавления пользователя в группу!")
    public void addNewUser() throws Exception{
        UserAdd data = new UserAdd();
        data.setUsername("YugSystemA");
        data.setPassword("123");
        data.setEmail("a@dsd.com");
        data.setWg(1);
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/users/addnew")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(data))
        )       .andDo(print())
                .andExpect(content().string("{\"status\":\"ok\"}"))
                .andReturn();

    }

    @WithUserDetails("admin")
    @Test
    @DisplayName("Получить организации")
    public void getAllOrganizartion() throws Exception{

        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        OrganizationResponse response = mapper.readValue(content, OrganizationResponse.class);
        Assertions.assertTrue(response.getInfoList().size() > 0);
    }

    @WithUserDetails("MainDisp")
    @Test
    @DisplayName("Попытка получить доступ не Админу")
    public void NotAdminOrganizartion() throws Exception{

        MvcResult mvcResult = mockMvc.perform(get("/globalManager/organizations"))
                .andDo(print())
                .andExpect(status().is(403))
                .andReturn();
    }
    @Transactional
    @WithUserDetails("admin")
    @Test
    @DisplayName("Тест. Попытка добавить дубликат организации")
    public void organizationDublicatieRegistration() throws Exception{
        OrganizationRegistrate registrate = new OrganizationRegistrate();
        registrate.setOrganization("JugSystem");
        registrate.setEmail("2repressian@staliny.ussr");
        registrate.setUsername("YugSystemAdmin");
        registrate.setPassword("p");
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/addOrganization")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(registrate))
        )       .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotAcceptable())
                .andReturn();


        System.out.println(mvcResult.getResponse().getContentAsString());
    }
    @Transactional
    @WithUserDetails("admin")
    @Test
    @DisplayName("Тест. Попытка добавить организацию с некоретными данными")
    public void organizationIncorrectRegistration() throws Exception{
        OrganizationRegistrate registrate = new OrganizationRegistrate();
        registrate.setOrganization("");
        registrate.setEmail("2repressian@staliny.ussr");
        registrate.setUsername("YugSystemAdmin");
        registrate.setPassword("p");
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/addOrganization")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(registrate))
        )       .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json("{\"organizationerror\":\"Имя организации не может быть пустым\"}"))
                .andReturn();

    }


    @Transactional
    @WithUserDetails("admin")
    @Test
    @DisplayName("Тест. добавить организации")
    public void organizationRegistration() throws Exception{
        OrganizationRegistrate registrate = new OrganizationRegistrate();
        registrate.setOrganization("JugEnergo");
        registrate.setEmail("admin@JugEnergo.com");
        registrate.setUsername("JugEnergoAdmin");
        registrate.setPassword("p");
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/addOrganization")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(registrate))
        )       .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"status\":\"ok\"}"))
                .andReturn();

    }

    @Transactional
    @WithUserDetails("YugSystemAdmin")
    @Test
    @DisplayName("Тест. Попытка добавить существующую рабочую группу")
    public void addDublicateWorkGroup() throws Exception{
        WorkGroupDTO groupDTO = new WorkGroupDTO();
        groupDTO.setOrganization("");
        groupDTO.setWorkGroup("JugSystem_default");
        groupDTO.setEmail("a@sad.com");
        groupDTO.setUsername("test_user");
        groupDTO.setPassword("p");
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/addWorkGroup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(groupDTO))
        )       .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotAcceptable())
                .andReturn();

    }

    @Transactional
    @WithUserDetails("YugSystemAdmin")
    @Test
    @DisplayName("Тест. Попытка добавить рабочую группу с существующим админом")
    public void addDublicateUserWorkGroup() throws Exception{
        WorkGroupDTO groupDTO = new WorkGroupDTO();
        groupDTO.setOrganization("");
        groupDTO.setWorkGroup("JugSystem_new");
        groupDTO.setEmail("a@sad.com");
        groupDTO.setUsername("YugSystemAdmin");
        groupDTO.setPassword("p");
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/addWorkGroup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(groupDTO))
        )       .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotAcceptable())
                .andReturn();
    }

    /**
     *
     * @throws Exception
     */
    @Transactional
    @WithUserDetails("YugSystemAdmin")
    @Test
    @DisplayName("Тест. добавить рабочую группу")
    public void addWorkGroup() throws Exception{
        WorkGroupDTO groupDTO = new WorkGroupDTO();
        groupDTO.setOrganization("");
        groupDTO.setWorkGroup("JugSystem_1");
        groupDTO.setEmail("a@sad.com");
        groupDTO.setUsername("Admin_1");
        groupDTO.setPassword("p");
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/globalManager/addWorkGroup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(groupDTO))
        )       .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"status\":\"ok\"}"))
                .andReturn();
    }

    @Transactional
    @WithUserDetails("admin")
    @Test
    @DisplayName("Тест. удалить пользователя")
    public void deleteUserByIdTestAdmin() throws Exception{
        MvcResult mvcResult = mockMvc.perform(delete("/globalManager/users/delete/4","4")
        .with(csrf()).contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaType.APPLICATION_JSON_UTF8)).andDo(print()).andReturn();
        User userById = userService.getUserById(4);
        Assertions.assertTrue(userById.isDeleted());

    }


}
