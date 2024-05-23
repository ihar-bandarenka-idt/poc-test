package com.example.demo;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="test-poc")
public class Pod {

    private String id;
    private String accountId;
    private String token;

    @DynamoDBHashKey(attributeName="Id")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName="accountId")
    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @DynamoDBAttribute(attributeName="token")
    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Pod{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
