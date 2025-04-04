package ai.nory.api.identity;

import ai.nory.api.constant.IdentityConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class IdentityInterceptor implements HandlerInterceptor {
    private final IdentityHeaders identityHeaders;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Intercept the HTTP request and set the incoming identity headers on a POJO that the controllers can easily access

        String locationIdHeader = request.getHeader(IdentityConstant.LOCATION_ID_HEADER);
        if (locationIdHeader != null) {
            identityHeaders.setLocationId(Long.valueOf(locationIdHeader));
        }

        String staffMemberIdHeader = request.getHeader(IdentityConstant.STAFF_MEMBER_ID_HEADER);
        if (staffMemberIdHeader != null) {
            identityHeaders.setStaffMemberId(Long.valueOf(staffMemberIdHeader));
        }

        return true;
    }
}
