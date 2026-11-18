package com.example.comicplatform_springbootweb.common;

/**
 * 通用返回结果
 * @param <T>
 *
 *查漫画返回 Comic 对象，查列表返回 List<Comic> 。
 * success() 静态方法让你在 Controller 里一行return Result.success(comic); 就搞定
 * 放回结果的样式：
 * {
 *   "code": 200,
 *   "msg": "成功",
 *   "data": { "id": 1, "comicName": "XX漫画" }
 * }
 */

public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    private Result(Integer code,String msg,T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    // 成功返回
    public static <T> Result<T> success(T data) {
        return new Result<>(200,"成功",data);
    }
    //无数据成功返回
    public static <T> Result<T> success() {
        return new Result<>(200,"成功",null);
    }

    // 失败返回
    public static <T> Result<T> error(Integer code,String msg) {
        return new Result<>(code,msg,null);
    }
}
