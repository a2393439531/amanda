package com.timeyang.amanda.config;

import com.timeyang.amanda.authority.UserAuthority;
import com.timeyang.amanda.authority.util.AuthorityUtils;
import com.timeyang.amanda.blog.domain.Article;
import com.timeyang.amanda.blog.domain.Category;
import com.timeyang.amanda.blog.domain.Comment;
import com.timeyang.amanda.blog.repository.ArticleRepository;
import com.timeyang.amanda.blog.repository.CommentRepository;
import com.timeyang.amanda.blog.service.CategoryService;
import com.timeyang.amanda.user.domain.Profile;
import com.timeyang.amanda.user.domain.User;
import com.timeyang.amanda.user.service.ProfileService;
import com.timeyang.amanda.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * 初始化数据
 *
 * @author chaokunyang
 * @create 2017-04-18
 */
@Service
@org.springframework.context.annotation.Profile("init")
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private AmandaProperties amandaProperties;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CommentRepository commentRepository;

    public void populate() {
        deleteAll();

        List<UserAuthority> userAuthorityList = AuthorityUtils.createAuthorityList(
                "VIEW_ARTICLES", "VIEW_ARTICLE", "CREATE_ARTICLE", "EDIT_ARTICLE", "DELETE_ARTICLE", "DELETE_ANY_ARTICLE",
                "VIEW_COMMENTS", "VIEW_COMMENT", "CREATE_COMMENT", "EDIT_COMMENT", "DELETE_COMMENT", "DELETE_ANY_COMMENT",
                "VIEW_ATTACHMENT", "DELETE_ATTACHMENT", "DELETE_ANY_ATTACHMENT",
                "VIEW_USER_SESSIONS",
                "DELETE_ANY_USER",
                "VIEW_FILE", "VIEW_PRIVATE_FILE", "UPLOAD_FILE", "UPLOAD_PRIVATE_FILE", "DELETE_FILE", "DELETE_ANY_FILE",
                "ACTUATOR");
        Set<UserAuthority> authorities = new HashSet<>(userAuthorityList);
        User user = User.builder().username(amandaProperties.getUsername()).name(amandaProperties.getName()).hashedPassword(userService.getHashedPassword(amandaProperties.getPassword())).authorities(authorities).accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).build();
        userService.save(user);

        addCategories();

        addArticles(user);

        addProfile();
    }

    public void deleteAll() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        categoryService.deleteAll();
        categoryService.deleteAll();
        profileService.deleteAll();
        userService.deleteAll();
    }

    private void addCategories() {
        categoryService.deleteAll();

        Category root = new Category("Category", "分类", -1, 0, new ArrayList<>());

        Category lang = new Category("Programing Languages", "编程语言", 0, 0, null);

        List<Category> langs = Arrays.asList(
                new Category("Java", 1, 0, null),
                new Category("Scala", 1, 1, null),
                new Category("Clojure", 1, 2, null),
                new Category("Groovy", 1, 3, null),
                new Category("Python", 1, 4, null),
                new Category("EcmaScript", 1, 5, null)
        );
        lang.setChildren(langs);

        Category backend = new Category("Development", "开发", 0, 1, null);
        List<Category> backendEntries = Arrays.asList(
                new Category("log", "日志", 1, 0, null),
                new Category("Search", "搜索", 1, 1, null),
                new Category("Message Queue", "消息队列", 1, 2, null)
        );
        backend.setChildren(backendEntries);

        Category microservices = new Category("MicroServices", "微服务", 0, 2, null);
        List<Category> microservicesList = Arrays.asList(
                new Category("Event Sourcing", 1, 0, null),
                new Category("CQRS", 1, 1, null),
                new Category("Message-Driven MicroServices","消息驱动的微服务", 1, 2, null)
        );
        microservices.setChildren(microservicesList);

        Category reactive = new Category("Reactive","响应式编程", 0, 3, null);
        List<Category> reactives = Arrays.asList(
                new Category("Akka", 1, 0, null),
                new Category("Spring Reactor", 1, 1, null),
                new Category("Vert.x", 1, 2, null)
        );
        reactive.setChildren(reactives);

        Category cloudComputing = new Category("Cloud Computing", "云计算", 0, 4, null);
        List<Category> cloudComputingList = Arrays.asList(
                new Category("Docker", 1, 0, null),
                new Category("Kubernetes", 1, 1, null),
                new Category("Mesos", 1, 2, null)
        );
        cloudComputing.setChildren(cloudComputingList);

        Category bigData = new Category("Big Data", "大数据", 0, 5, null);
        List<Category> bigDataList = Arrays.asList(
                new Category("Hadoop", 1, 0, null),
                new Category("Spark", 1, 1, null),
                new Category("Flink", 1, 2, null),
                new Category("Storm", 1, 3, null),
                new Category("Beam", 1, 4, null)
        );
        bigData.setChildren(bigDataList);

        Category devOps = new Category("DevOps", 0, 6, null);
        List<Category> devOpsList = Arrays.asList(
                new Category("DevOps Practice", "DevOps实践", 1, 0, null),
                new Category("SRE Practice", "SRE实践", 1, 1, null)
        );
        devOps.setChildren(devOpsList);

        List<Category> categories = Arrays.asList(
                lang,
                microservices,
                new Category("Streaming", 0, 2, null),
                reactive,
                backend,
                cloudComputing,
                bigData,
                devOps,
                new Category("Build/Delivery", "构建交付", 0, 7, null)
        );

        root.getChildren().addAll(categories);
        categoryService.save(root);
    }

    private void addArticles(User user) {
        Category category1 = categoryService.getCategoryByName("Development");
        Article amanda = Article.builder()
                .user(user)
                .title("我为什么开发Amanda？")
                .keywords("Amanda、 Spring Boot、Spring Data JPA、 Hibernate Search")
                .abstractContent("Amanda关注于使用最好和最合适的工具解决问题")
                .mdBody("Amanda关注于使用最好和最合适的工具解决问题")
                .htmlBody("Amanda关注于使用最好和最合适的工具解决问题")
                .category(category1).categoryName(category1.getName())
                .published(true)
                .publishedDate(Instant.now())
                .build();

        articleRepository.save(amanda);

        Comment comment = new Comment(amanda.getId(), "Amanda设计不错, 我很喜欢", null);
        commentRepository.save(comment);

        Category category2 = categoryService.getCategoryByName("log");
        Article logPlatform = Article.builder()
                .user(user)
                .title("基于Elastic Stack + Fluentd搭建实时日志平台？")
                .keywords("Elastic Stack、Fluentd、ElasticSearch、Logstash、Beats、filebeat、metricbeat、 Kibana")
                .abstractContent("基于Elastic Stack + Fluentd搭建实时日志平台")
                .mdBody("基于Elastic Stack + Fluentd搭建实时日志平台")
                .htmlBody("基于Elastic Stack + Fluentd搭建实时日志平台")
                .category(category2).categoryName(category2.getName())
                .published(true)
                .publishedDate(Instant.now())
                .build();
        articleRepository.save(logPlatform);

        Category category3 = categoryService.getCategoryByName("MicroServices");
        Article eventSourcing = Article.builder()
                .user(user)
                .title("Microservices Event Sourcing")
                .keywords("spring-boot\n" +
                        "springcloud\n" +
                                "event-driven\n" +
                                "event-sourcing\n" +
                                "angularjs\n" +
                                "ecommerce\n" +
                                "mongodb\n" +
                                "neo4j\n" +
                                "microservice\n" +
                                "oauth2\n" +
                                "eventually-consistent")
                .abstractContent("Microservices Event Sourcing 是一个微服务架构的在线购物平台，使用Spring Boot、Spring Cloud、Spring Reactor、OAuth2、CQRS 构建，实现了基于Event Sourcing的最终一致性，提供了构建端到端微服务的最佳实践")
                .mdBody("Microservices Event Sourcing 是一个微服务架构的在线购物平台，使用Spring Boot、Spring Cloud、Spring Reactor、OAuth2、CQRS 构建，实现了基于Event Sourcing的最终一致性，提供了构建端到端微服务的最佳实践")
                .htmlBody("Microservices Event Sourcing 是一个微服务架构的在线购物平台，使用Spring Boot、Spring Cloud、Spring Reactor、OAuth2、CQRS 构建，实现了基于Event Sourcing的最终一致性，提供了构建端到端微服务的最佳实践")
                .category(category3).categoryName(category3.getName())                                   .published(true)
                .publishedDate(Instant.now())
                .build();

        articleRepository.save(eventSourcing);
    }

    private void addProfile() {
        User user = userService.getUserByUsername(amandaProperties.getUsername());

        Profile profile = Profile.builder()
                .name("杨朝坤")
                .user(user)
                .email("chaokunyang@qq.com")
                .biography("在微服务、Spring Cloud、Elastic Stack、搜索、Kafka、Spark、机器学习、深度学习等技术有一定的实践经验")
                .url("http://timeyang.com")
                .githubUrl("https://github.com/chaokunyang")
                .twitterUrl("https://twitter.com/chaokunyang")
                .weiboUrl("http://weibo.com/u/3153324577")
                .location("广东 深圳")
                .mdBody("我叫杨朝坤，我是一名软件工程师。\n" +
                        "\n" +
                        "热爱大数据与机器学习相关技术，在微服务、Spring Cloud、Elastic Stack、搜索、Kafka、Spark、机器学习、深度学习等领域有一定的实践经验。\n" +
                        "\n" +
                        "在空余时间，我喜欢运动、读书、旅行、看电影")
                .htmlBody("<p>我叫杨朝坤，是一名软件工程师。</p>\n<p>热爱大数据与机器学习相关技术，在微服务、Spring Cloud、Elastic Stack、搜索、Kafka、Spark、机器学习、深度学习等领域有一定的实践经验。</p>\n<p>在空余时间，我喜欢运动、读书、旅行、看电影</p>")
                .build();
        profileService.save(profile);
    }

    @Override
    public void run(String... args) throws Exception {
        populate();
    }
}
