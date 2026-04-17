package domain.model;

public class AuthResponse {
    private final boolean success;
    private final AuthFailureReason failureReason;

    public AuthResponse(boolean success, AuthFailureReason authFailureReason){
        this.success = success;
        this.failureReason = authFailureReason;
    }

    public boolean isSuccessful(){
        return success;
    }
    public AuthFailureReason getFailureReason() {
        return failureReason;
    }
}
