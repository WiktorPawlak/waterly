# Base image with Jenkins
FROM jenkins/jenkins:lts

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
RUN apt-get update \
    && apt-get install -y \
        curl \
        wget \
        unzip \
        sudo \
        git \
        software-properties-common

# Install Node.js 18.x
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - \
    && apt-get install -y nodejs

# Install Maven
ARG MAVEN_VERSION=3.8.3
ARG USER_HOME_DIR="/root"
RUN wget --no-verbose -O /tmp/apache-maven.tar.gz https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar xzf /tmp/apache-maven.tar.gz -C /opt/ \
    && ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven \
    && ln -s /opt/maven/bin/mvn /usr/local/bin \
    && rm -f /tmp/apache-maven.tar.gz

# Install Docker
RUN curl -fsSL https://get.docker.com -o get-docker.sh && \
    sh get-docker.sh && \
    rm get-docker.sh

# Start the Docker daemon
RUN service docker start

# Install Java 17
RUN apt-get install -y openjdk-17-jdk
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64

# Install Payara Server
ARG PAYARA_VERSION=6.2023.3
RUN wget --no-verbose -O /tmp/payara.zip https://repo.maven.apache.org/maven2/fish/payara/distributions/payara/${PAYARA_VERSION}/payara-${PAYARA_VERSION}.zip \
    && unzip -qq /tmp/payara.zip -d /opt/ \
    && rm -f /tmp/payara.zip
ENV PAYARA_HOME /opt/payara6

# Set permissions for Jenkins to access Payara
USER root
RUN chown -R jenkins:jenkins /opt/payara6/glassfish/domains

# Install PNPM
RUN npm install -g pnpm
ENV npm_config_user=root

ARG USERNAME=jenkins
RUN echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Expose the Jenkins server port
EXPOSE 2137

USER jenkins

# Start Jenkins with the specified port
CMD ["--httpPort=2137"]
