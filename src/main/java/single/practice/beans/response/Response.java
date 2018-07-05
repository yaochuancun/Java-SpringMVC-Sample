//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package single.practice.beans.response;

public class Response {
    public static final String SUCCESS = "success";
    public static final String FIELD_NAME_EXCEPTION = "error";
    Object result;
    Response.Error error;
    String status = "success";

    public Response() {
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Response.Error getError() {
        return this.error;
    }

    public void setError(Response.Error error) {
        if (error != null) {
            this.error = error;
            this.status = "error";
        }

    }

    public void setError(String code, String reason) {
        if (!"".equals(code) && "null".equals(code)) {
            this.error = new Response.Error(code, reason);
            this.status = "error";
        }

    }

    public String toString() {
        return "Response [status=" + this.status + ", result=" + this.result + ", error=" + this.error + "]";
    }

    public static class Error {
        String code;
        String reason;

        public Error() {
        }

        public Error(String code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        public String getCode() {
            return this.code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getReason() {
            return this.reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
