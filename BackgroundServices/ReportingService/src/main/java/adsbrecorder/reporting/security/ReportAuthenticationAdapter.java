package adsbrecorder.reporting.security;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import adsbrecorder.common.auth.ListOfAuthorities;
import adsbrecorder.common.auth.TokenAuthenticationAdapter;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.reporting.ReportingServiceMappings;

@EnableWebSecurity
@Component
public class ReportAuthenticationAdapter extends TokenAuthenticationAdapter implements ListOfAuthorities, ReportingServiceMappings {

    private ReportTokenAuthenticationFilter authenticationFilter;

    @Autowired
    public ReportAuthenticationAdapter(ReportTokenAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = requireNonNull(authenticationFilter);
    }

    @Override
    protected TokenAuthenticationFilter getTokenAuthenticationFilter() {
        return this.authenticationFilter;
    }

    @Override
    protected Collection<String> permitAllURLs() {
        return List.of(CHECK_REPORT_NAME, SEARCH_REPORT_JOBS);
    }

    @Override
    protected Map<String, String> hasAnyAuthorityURLs() {
        return Map.of(
                SIMPLE_DAILY_SUMMARY_REPORT, RUN_SIMPLE_DAILY_SUMMARY_REPORT,
                VIEW_REPORT_OUTPUT, RUN_SIMPLE_DAILY_SUMMARY_REPORT,  // TODO create new authority
                VIEW_RECENT_REPORT_JOBS, RUN_SIMPLE_DAILY_SUMMARY_REPORT,  // TODO create new authority
                GET_REPORT_PROGRESS, VIEW_REPORT_METADATA);
    }
}
