package com.synpulse.transaction.persentation;

import io.confluent.ksql.api.client.*;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.StreamedQueryResult;

public class KsqlDBQuery {
    public static void main(String[] args) {
        // Set the ksqlDB server endpoint
        String serverEndpoint = "localhost";

        // Create ksqlDB client options
        ClientOptions options = ClientOptions.create()
                .setHost(serverEndpoint)
                .setPort(8088);

        // Create ksqlDB client
        Client client = Client.create(options);
        System.out.println("21e12eqwdqwd");

        client.streamQuery("CREATE TABLE transaction_table (id STRING PRIMARY KEY, amount DECIMAL(18, 2),currency STRING,accountIban STRING,valueDate STRING,description STRING) WITH (kafka_topic = 'query_transaction_v1',value_format = 'json');")
                .thenAccept(streamedQueryResult -> {
                    System.out.println("Query has started. Query ID: " + streamedQueryResult.queryID());

                }).exceptionally(e -> {
                    System.out.println("Request failed: " + e);
                    return null;
                });
    }
}