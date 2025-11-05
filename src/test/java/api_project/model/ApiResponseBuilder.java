package api_project.model;

public class ApiResponseBuilder {
    private int code=1;
    private String type="unknown";
    private String message="test";
    public ApiResponseBuilder code(int code){
        this.code=code;
        return this;
    }
    public ApiResponseBuilder type(String type){
        this.type=type;
        return this;
    }
    public ApiResponseBuilder message(String message){
        this.message=message;
        return this;
    }
    public ApiResponse build(){
        return new ApiResponse(code,type,message);
    }
}
