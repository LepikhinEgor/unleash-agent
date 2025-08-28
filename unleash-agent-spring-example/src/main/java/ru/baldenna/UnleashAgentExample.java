package ru.baldenna;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UnleashAgentExample {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(UnleashAgentExample.class).run(args);
    }

}

//curl --location --request POST 'http://localhost:4242/api/admin/projects/default/features/feature78/environments/development/strategies' \
//    --header 'Authorization: INSERT_API_KEY' \
//    --header 'Content-Type: application/json' \
//    --data-raw '
//    {
//  "name": "flexibleRollout",
//  "constraints": [],
//  "parameters": {
//    "rollout": "100",
//    "stickiness": "default",
//    "groupId": "feature78"
//  },
//  "variants": [
//    {
//      "stickiness": "default",
//      "name": "variant1",
//      "weight": 333,
//      "payload": {
//        "type": "string",
//        "value": "testFirst"
//      },
//      "weightType": "variable"
//    },
//    {
//      "stickiness": "default",
//      "name": "variant2",
//      "weight": 334,
//      "payload": {
//        "type": "string",
//        "value": "testSecond"
//      },
//      "weightType": "variable"
//    },
//    {
//      "stickiness": "default",
//      "name": "variant3",
//      "weight": 333,
//      "payload": {
//        "type": "string",
//        "value": "testThird"
//      },
//      "weightType": "variable"
//    }
//  ],
//  "segments": [
//    4
//  ],
//  "disabled": false
//}
//
////curl --location --request PUT 'http://localhost:4242/api/admin/projects/default/features/feature78/environments/development/strategies/fcea9977-0d5e-4758-9b8f-5545a747cb63' \
////    --header 'Authorization: INSERT_API_KEY' \
////    --header 'Content-Type: application/json' \
////    --data-raw '
//    {
//  "name": "flexibleRollout",
//  "title": null,
//  "constraints": [
//    {
//      "values": [
//        "123"
//      ],
//      "inverted": false,
//      "operator": "IN",
//      "contextName": "appName",
//      "caseInsensitive": false
//    }
//  ],
//  "parameters": {
//    "rollout": "100",
//    "stickiness": "default",
//    "groupId": "feature78"
//  },
//  "variants": [],
//  "segments": [
//    4
//  ],
//  "disabled": false
//}