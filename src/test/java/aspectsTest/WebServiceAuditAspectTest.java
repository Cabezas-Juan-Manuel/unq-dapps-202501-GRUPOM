package aspectsTest;

import ar.edu.unq.pronostico.deportivo.PronosticoDeportivoApplication;
import ar.edu.unq.pronostico.deportivo.aspects.WebServiceAuditAspect;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(classes = PronosticoDeportivoApplication.class)
@AutoConfigureMockMvc
class WebServiceAuditAspectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser("carlos") // Simula un usuario autenticado
    void testAspectLogsEndpointCallCorrectly() throws Exception {

        LogCaptor logCaptor = LogCaptor.forClass(WebServiceAuditAspect.class);

        String teamName = "milan";
        String expectedMethodName = "getFuturesMatches";

        mockMvc.perform(get("/pronosticoDeportivo/team/{teamName}/matches", teamName))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));

        // el assert that simplifica la busqueda de los logs, no deberian de tener un orden
        assertThat(logCaptor.getInfoLogs()).anySatisfy(log -> {
            assertThat(log).contains("User: 'carlos'");
            assertThat(log).contains("Method: '" + expectedMethodName + "'");
            assertThat(log).contains("Parameters: [" + teamName + "]");
            assertThat(log).contains("Execution started.");
        });

        assertThat(logCaptor.getInfoLogs()).anySatisfy(log -> {
            assertThat(log).contains("User: 'carlos'");
            assertThat(log).contains("Method: '" + expectedMethodName + "'");
            assertThat(log).contains("Execution finished in");
            assertThat(log).contains("ms.");
        });
    }

}

