package com.vss;

import com.vss.repo.SlotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SlotRepository slots;

    private String firstSlotId() throws Exception {
        MvcResult result = mockMvc.perform(get("/slots"))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse().getContentAsString();
        // Seed data guarantees at least one Book link of the form /slots/{id}/book
        int idx = html.indexOf("/slots/");
        String sub = html.substring(idx + "/slots/".length());
        return sub.substring(0, sub.indexOf('/'));
    }

    @Test
    void volunteerRegistersAndDuplicateIsRejected() throws Exception {
        String email = "webflow" + System.nanoTime() + "@example.com";
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "Web Flow")
                        .param("email", email)
                        .param("phone", "9876543210")
                        .param("skills", "teaching"))
                .andExpect(status().is3xxRedirection());

        // Same e-mail again -> registration page with error, no redirect
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "Web Flow")
                        .param("email", email)
                        .param("phone", "9876543210"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("already registered")));
    }

    @Test
    void slotsPageShowsSeededShifts() throws Exception {
        mockMvc.perform(get("/slots"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Morning Food Drive")));
    }

    @Test
    void bookingConfirmCancelFlow() throws Exception {
        String email = "flow" + System.nanoTime() + "@example.com";
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "Flow User")
                        .param("email", email)
                        .param("phone", "9876543210"))
                .andExpect(status().is3xxRedirection());

        String slotId = firstSlotId();

        MvcResult bookingResult = mockMvc.perform(post("/slots/" + slotId + "/book")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String ref = bookingResult.getResponse().getRedirectedUrl();
        ref = ref.substring(ref.indexOf("ref=") + 4);

        // Status tracking by reference shows PENDING
        mockMvc.perform(get("/status").param("ref", ref))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(ref)))
                .andExpect(content().string(containsString("PENDING")));

        // Coordinator confirms
        mockMvc.perform(post("/coordinator/bookings/" + ref + "/confirm"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/status").param("ref", ref))
                .andExpect(content().string(containsString("CONFIRMED")));

        // Volunteer cancels; seat released
        mockMvc.perform(post("/bookings/" + ref + "/cancel"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/status").param("ref", ref))
                .andExpect(content().string(containsString("CANCELLED")));
    }

    @Test
    void coordinatorDashboardListsBookings() throws Exception {
        mockMvc.perform(get("/coordinator"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Coordinator dashboard")));
    }
}
