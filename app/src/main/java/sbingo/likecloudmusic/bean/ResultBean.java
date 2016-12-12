package sbingo.likecloudmusic.bean;

import java.io.Serializable;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class ResultBean<T> implements Serializable{

    private String status;

    private String code;

    private String message;

    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return getCode().equals("0");
    }

}
