package single.practice.beans.response;

public class Response {

    private Object result;
    private String status;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskResponse{" +
                "result=" + result +
                ", status='" + status + '\'' +
                '}';
    }
}
