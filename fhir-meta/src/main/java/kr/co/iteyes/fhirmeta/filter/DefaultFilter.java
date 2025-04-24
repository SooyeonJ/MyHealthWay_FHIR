package kr.co.iteyes.fhirmeta.filter;

import org.apache.commons.lang3.StringUtils;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.co.iteyes.fhirmeta.dto.AgentStatusDto;
import kr.co.iteyes.fhirmeta.service.AgentStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service("defaultFilter")
public class DefaultFilter implements Filter {

    @Autowired
    private AgentStatusService agentStatusService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String requestURI = req.getRequestURI();
        log.info("Request URI = {}", requestURI);

        // Agent 상태체크(임시, 데몬 Agent의 API로 대체될 예정임)
        String cisn = req.getHeader("cisn");
        if((HttpMethod.POST.name()).equals(req.getMethod())
                && ("/meta/extract").equals(requestURI)
                && StringUtils.isNotBlank(cisn)) {

            AgentStatusDto agentStatusDto = AgentStatusDto.builder()
                    .agentVer("Unknown")
                    .gatewayVer("Unknown")
                    .build();

            agentStatusService.createAgentStatus(cisn, agentStatusDto);
        }

        filterChain.doFilter(req, res);
    }
}
