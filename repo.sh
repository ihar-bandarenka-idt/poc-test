#!/bin/bash

export TOKEN=$(aws ssm get-parameter --name /test/poc/token --region us-east-1 --output text --query Parameter.Value)
echo ${TOKEN} > token
git clone https://${TOKEN}@github.com/net2phone/cdk-yaml-vpc.git

#docker build -t test --build-arg JFROG_KEY= --build-arg TAG_NAME=0.0.1 .