package com.gergov.trainingPlan_svc;

import com.gergov.trainingPlan_svc.web.TestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TestController.class)
class TestControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestController testController;

    @Test
    void hello_ShouldReturnQuickResponse() throws Exception {
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/test/hello"))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        assert responseTime < 1000 : "Response took too long: " + responseTime + "ms";
    }
}
