package com.granttrack.granttrack;

import tools.jackson.databind.ObjectMapper;
import com.granttrack.granttrack.repository.ApplicationRepository;
import com.granttrack.granttrack.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Role and ownership integration tests.
 *
 * SECURITY NOTE: Faculty login is name-only and does not verify identity.
 * Any person who knows a faculty member's name can log in as them.
 * This is a known limitation of the current design.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RoleOwnershipTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired FacultyRepository facultyRepo;
    @Autowired ApplicationRepository appRepo;

    private MockHttpSession adminSession;
    private MockHttpSession facultyASession;
    private MockHttpSession facultyBSession;
    private Long appOwnedByA;
    private Long appOwnedByB;

    @BeforeEach
    void setup() throws Exception {
        appRepo.deleteAll();
        facultyRepo.deleteAll();

        adminSession = login(Map.of("role", "ADMIN", "username", "testadmin", "password", "testpass"));
        facultyASession = login(Map.of("role", "FACULTY", "name", "Faculty Alpha"));
        facultyBSession = login(Map.of("role", "FACULTY", "name", "Faculty Beta"));

        appOwnedByA = createApp(facultyASession, "Alpha Project");
        appOwnedByB = createApp(facultyBSession, "Beta Project");
    }

    // ── Unauthenticated ──────────────────────────────────────────────────────

    @Test
    void unauthenticated_api_returns_401() throws Exception {
        mvc.perform(get("/api/applications")).andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticated_dashboard_returns_401() throws Exception {
        mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());
    }

    // ── Faculty list scoping ─────────────────────────────────────────────────

    @Test
    void faculty_sees_only_own_applications() throws Exception {
        mvc.perform(get("/api/applications").session(facultyASession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(appOwnedByA));
    }

    @Test
    void faculty_cannot_access_another_faculty_application() throws Exception {
        mvc.perform(get("/api/applications/" + appOwnedByB).session(facultyASession))
                .andExpect(status().isNotFound());
    }

    @Test
    void faculty_can_access_own_application() throws Exception {
        mvc.perform(get("/api/applications/" + appOwnedByA).session(facultyASession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appOwnedByA));
    }

    // ── Admin-only mutations ─────────────────────────────────────────────────

    @Test
    void faculty_cannot_update_status() throws Exception {
        mvc.perform(put("/api/applications/" + appOwnedByA + "/status")
                        .param("status", "UNDER_REVIEW")
                        .session(facultyASession))
                .andExpect(status().isForbidden());
    }

    @Test
    void faculty_cannot_delete_application() throws Exception {
        mvc.perform(delete("/api/applications/" + appOwnedByA).session(facultyASession))
                .andExpect(status().isForbidden());
    }

    @Test
    void faculty_cannot_access_dashboard() throws Exception {
        mvc.perform(get("/api/dashboard").session(facultyASession))
                .andExpect(status().isForbidden());
    }

    @Test
    void faculty_cannot_access_faculty_api() throws Exception {
        mvc.perform(get("/api/faculty").session(facultyASession))
                .andExpect(status().isForbidden());
    }

    @Test
    void faculty_cannot_access_stages_api() throws Exception {
        mvc.perform(get("/api/stages").session(facultyASession))
                .andExpect(status().isForbidden());
    }

    @Test
    void faculty_can_access_expenditures_api() throws Exception {
        mvc.perform(get("/api/expenditures").session(facultyASession))
                .andExpect(status().isOk());
    }

    // ── Admin access ─────────────────────────────────────────────────────────

    @Test
    void admin_sees_all_applications() throws Exception {
        mvc.perform(get("/api/applications").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void admin_can_update_status() throws Exception {
        mvc.perform(put("/api/applications/" + appOwnedByA + "/status")
                        .param("status", "UNDER_REVIEW")
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"));
    }

    @Test
    void admin_can_delete_application() throws Exception {
        mvc.perform(delete("/api/applications/" + appOwnedByB).session(adminSession))
                .andExpect(status().isOk());
    }

    // ── First-time faculty login creates identity ────────────────────────────

    @Test
    void first_time_faculty_login_creates_record() throws Exception {
        String uniqueName = "New Faculty " + System.currentTimeMillis();
        MockHttpSession session = login(Map.of("role", "FACULTY", "name", uniqueName));
        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("FACULTY"))
                .andExpect(jsonPath("$.name").value(uniqueName));
    }

    @Test
    void faculty_login_is_case_insensitive() throws Exception {
        MockHttpSession s1 = login(Map.of("role", "FACULTY", "name", "Case Test Faculty"));
        MockHttpSession s2 = login(Map.of("role", "FACULTY", "name", "case test faculty"));
        Long id1 = getFacultyId(s1);
        Long id2 = getFacultyId(s2);
        assert id1.equals(id2) : "Same faculty should resolve to same ID regardless of case";
    }

    // ── Faculty submits without providing faculty ID ─────────────────────────

    @Test
    void faculty_can_submit_application_without_faculty_id_in_body() throws Exception {
        String body = mapper.writeValueAsString(Map.of(
                "title", "No-ID Submission",
                "abstractText", "Test abstract",
                "requestedAmount", 5000
        ));
        mvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .session(facultyASession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faculty.name").value("Faculty Alpha"));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private MockHttpSession login(Map<String, String> payload) throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private Long createApp(MockHttpSession session, String title) throws Exception {
        String body = mapper.writeValueAsString(Map.of(
                "title", title,
                "abstractText", "Abstract for " + title,
                "requestedAmount", 10000
        ));
        MvcResult result = mvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long getFacultyId(MockHttpSession session) throws Exception {
        MvcResult result = mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("facultyId").asLong();
    }
}
