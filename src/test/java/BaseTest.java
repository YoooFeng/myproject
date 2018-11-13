import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


// 配置spring和junit整合，junit启动时加载springIOC容器 spring-test,junit
@RunWith(SpringJUnit4ClassRunner.class)
// 通知junit spring的配置文件在哪
@ContextConfiguration({"classpath: spring/spring-dao.xml", "classpath: spring/spring-server.xml"})
public class BaseTest {

}
