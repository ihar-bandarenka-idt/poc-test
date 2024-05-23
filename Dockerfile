FROM python:3.11.2-bullseye
ARG AWS_CDK_VERSION=2.73.0
ARG JFROG_KEY
ARG TAG_NAME
ENV JFROG=${JFROG_KEY}
ENV TAG=${TAG_NAME}
COPY insert_jfrog.sh .
RUN chmod +x /insert_jfrog.sh
RUN ./insert_jfrog.sh
RUN apt update
RUN apt install npm -y

RUN npm install -g npm@latest && npm install -g n && n stable
RUN apt-get install -y git

RUN npm install -g aws-cdk@${AWS_CDK_VERSION}
RUN curl https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip \
  && unzip awscliv2.zip \
  && ./aws/install \
  && rm -rf aws awscliv2.zip
COPY requirements-dev.txt requirements.txt
RUN pip install -r requirements.txt
COPY --from=openjdk:17-jdk-slim /usr/local/openjdk-17 /usr/local/openjdk-17

ENV JAVA_HOME /usr/local/openjdk-17

RUN update-alternatives --install /usr/bin/java java /usr/local/openjdk-17/bin/java 1
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY repo.sh /
RUN chmod +x /repo.sh
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]