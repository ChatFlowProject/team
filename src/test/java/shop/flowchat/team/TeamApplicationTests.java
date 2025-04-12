package shop.flowchat.team;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev") // "dev" 프로파일에서만 테스트 코드 실행됨
class TeamApplicationTests {

    @Test
    void contextLoads() {
    }

}
