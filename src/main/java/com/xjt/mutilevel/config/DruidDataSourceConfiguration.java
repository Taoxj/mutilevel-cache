package com.xjt.mutilevel.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 阿里的数据库连接池，带监控
 *
 * @author huangjiangnan
 * @version 1.0
 * @email: huangjiangnanjava@163.com
 * @since 2017年2月22日 上午11:37:25
 */
@Slf4j
@Configuration
public class DruidDataSourceConfiguration {

    /**
     * 数据库连接池
     *
     * @return
     */
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    DataSource getDataSource() {
        DruidDataSource master = new DruidDataSource();
        master.setDefaultAutoCommit(true);
        try {
            master.setFilters("stat");
        } catch (SQLException e) {
            log.warn("连接池系统内部异常：{}，{}", e.getMessage(), e);
        }
        return master;
    }

    /**
     * 数据库连接池
     *
     * @return
     */
    @Bean(name = "slaveDataSource")
    @ConditionalOnProperty(prefix = "datasource", name = "more", havingValue = "true", matchIfMissing = false)
    @ConfigurationProperties(prefix = "spring.datasource2")
    DataSource getSlaveDataSource() {
        DruidDataSource master = new DruidDataSource();
        master.setDefaultAutoCommit(true);
        try {
            master.setFilters("stat");
        } catch (SQLException e) {
            log.warn("连接池系统内部异常：{}，{}", e.getMessage(), e);
        }
        return master;
    }

    /**
     * 设定登录账号和密码
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServle() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean =
                new ServletRegistrationBean<StatViewServlet>(new StatViewServlet(),
                        "/druid/*");
        // 控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", "druid");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        servletRegistrationBean.setEnabled(true);
        return servletRegistrationBean;
    }

    /**
     * 过滤
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> statFilter() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean =
                new FilterRegistrationBean<WebStatFilter>(new WebStatFilter());
        // 添加过滤规则
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "/druid/*");
        filterRegistrationBean.addInitParameter("deny", "127.0.0.1");
        filterRegistrationBean.setEnabled(true);
        return filterRegistrationBean;
    }

    @Bean(name = "druid-stat-interceptor")
    public DruidStatInterceptor getDruidStatInterceptor() {
        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
        return druidStatInterceptor;
    }

    @Bean
    public BeanNameAutoProxyCreator getBeanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator bean = new BeanNameAutoProxyCreator();
        bean.setInterceptorNames(new String[]{"druid-stat-interceptor"});
        bean.setBeanNames(new String[]{"*Service", "*Controller"});
        bean.setProxyTargetClass(true);
        return bean;
    }

}
