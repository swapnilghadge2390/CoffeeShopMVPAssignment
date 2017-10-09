package com.suma.coffeeshopmvpassignment.Models;

import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("status")
    public String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Response() {
    }

    public Response(String status) {
        this.status = status;
    }
}
