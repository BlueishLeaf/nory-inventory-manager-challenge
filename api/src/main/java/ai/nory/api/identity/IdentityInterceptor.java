package ai.nory.api.identity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class IdentityInterceptor implements HandlerInterceptor {
    private final IdentityHeaders identityHeaders;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String locationIdHeader = request.getHeader("location_id");
        if (locationIdHeader != null) {
            identityHeaders.setLocationId(Long.valueOf(locationIdHeader));
        }

        String staffMemberIdHeader = request.getHeader("staff_member_id");
        if (staffMemberIdHeader != null) {
            identityHeaders.setStaffMemberId(Long.valueOf(staffMemberIdHeader));
        }

        return true;
    }
}
