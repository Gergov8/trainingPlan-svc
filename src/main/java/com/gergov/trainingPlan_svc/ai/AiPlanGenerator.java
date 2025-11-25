package com.gergov.trainingPlan_svc.ai;

import com.gergov.trainingPlan_svc.plan.model.PlanLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AiPlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(AiPlanGenerator.class);
    private final ChatClient chatClient;

    public AiPlanGenerator(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generatePlan(double distanceKm, int daysPerWeek, PlanLevel planLevel) {
        try {
            String prompt = buildPrompt(distanceKm, daysPerWeek, planLevel);

            log.info("Generating AI plan for distance: {}km, days: {}, level: {}",
                    distanceKm, daysPerWeek, planLevel);

            ChatResponse response = chatClient.prompt()
                    .user(prompt)
                    .call().chatResponse();

            assert response != null;
            String text = response.getResult().getOutput().getText();


            log.debug("AI plan generated successfully");
            return text;

        } catch (Exception e) {
            log.error("Failed to generate AI plan", e);
            throw new RuntimeException("AI service error: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(double distanceKm, int daysPerWeek, PlanLevel planLevel) {
        return """
        You are an experienced running coach who follows Frank Shorter's philosophy:
        - Aerobic base building
        - One weekly long run
        - Two quality session (tempo or intervals)
        - As much easy mileage as the body can handle between the hard runs
        - Progressive overload

        Create a structured 4-week running plan.

        Constraints:
        - Target distance: %s km
        - Training days per week: %s
        - Level: %s
        - Weekly mileage, pace and hard sessions should be considered with the level of the user
        - Output *ONLY VALID JSON* in this exact schema:
        - If training days per week equal 7 then schedule 1 recovery run(very easy pace and relatively short)

        {
          "distanceKm": number,
          "daysPerWeek": integer,
          "planLevel": "RECREATIONAL" | "COMPETITIVE" | "ELITE",
          "weeks": [
            {
              "week": 1,
              "days": [
                { "dayOfWeek": "Mon", "type": "Easy", "description": "...", "durationMin": 45 }
              ]
            }
          ]
        }

        No explanation. No commentary. Only JSON.
        """.formatted(distanceKm, daysPerWeek, planLevel);
    }
}