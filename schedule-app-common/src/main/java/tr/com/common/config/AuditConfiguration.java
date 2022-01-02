package tr.com.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Audit konfigürasyonlarını barındırır.
 *
 * <p> Aktif kullanıcı bilgisi {@link AuditConfiguration#auditorAware} ile sağlanır.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();

    }

}
