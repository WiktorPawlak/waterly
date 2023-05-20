# Base image with CentOS and Jenkins
FROM centos:7

# Set the Jenkins home directory
ENV JENKINS_HOME=/var/jenkins_home

# Set deployment envs
# DB_SERVER_ADDRESS - needs to be changed to local gateway
ENV DB_SERVER_ADDRESS=192.168.1.112
ENV DB_ADMIN_PASS=12345
ENV DB_AUTH_PASS=12345
ENV DB_MOK_PASS=12345
ENV DB_MOL_PASS=12345

# Switch to root user for installation
USER root

# Install necessary dependencies
RUN yum update -y && \
    yum install -y \
        curl \
        wget \
        unzip \
        sudo \
        git \
        epel-release

# Install Node.js 18.x
RUN curl -fsSL https://rpm.nodesource.com/setup_14.x | sudo bash - \
    && yum install -y nodejs

# Install Maven
ARG MAVEN_VERSION=3.8.3
ARG USER_HOME_DIR="/root"
RUN wget --no-verbose -O /tmp/apache-maven.tar.gz https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.3/apache-maven-3.8.3-bin.tar.gz \
    && tar xzf /tmp/apache-maven.tar.gz -C /opt/ \
    && ln -s /opt/apache-maven-3.8.3 /opt/maven \
    && ln -s /opt/maven/bin/mvn /usr/local/bin \
    && rm -f /tmp/apache-maven.tar.gz

# Install Docker
RUN curl -fsSL https://get.docker.com -o get-docker.sh && \
    sh get-docker.sh && \
    rm get-docker.sh

# Install Java 17
RUN wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz \
    && tar xvf openjdk-17.0.2_linux-x64_bin.tar.gz \
    && mv jdk-17.0.2/ /opt/jdk-17/
ENV JAVA_HOME /opt/jdk-17

# Install Payara Server
ARG PAYARA_VERSION=6.2023.3
RUN curl -fsSL https://repo.maven.apache.org/maven2/fish/payara/distributions/payara/${PAYARA_VERSION}/payara-${PAYARA_VERSION}.zip -o /tmp/payara.zip \
    && unzip -qq /tmp/payara.zip -d /opt/ \
    && rm -f /tmp/payara.zip
ENV PAYARA_HOME /opt/payara6

# Install PNPM
RUN npm install -g pnpm
ENV npm_config_user=root

ARG USERNAME=jenkins
ARG USER_UID=1000
ARG USER_GID=1000

# Create Jenkins user and assign ownership
RUN groupadd -g ${USER_GID} ${USERNAME} \
    && useradd -u ${USER_UID} -g ${USER_GID} -m -s /bin/bash ${USERNAME} \
    && chown -R ${USER_UID}:${USER_GID} /opt/payara6/glassfish/domains

ARG USERNAME=jenkins
RUN echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Set permissions for Jenkins to access Payara
RUN chown -R jenkins:jenkins /opt/payara6/glassfish/domains

# Expose the Jenkins server port
EXPOSE 2137

USER jenkins
