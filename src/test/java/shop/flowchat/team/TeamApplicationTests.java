package shop.flowchat.team;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@SpringBootTest
@ActiveProfiles("dev") // "dev" 프로파일에서만 테스트 코드 실행됨
class TeamApplicationTests {

    @Test
    void contextLoads() {
    }

}
