# Pinned to LTS: weekly 2.590+ dropped commons-lang 2 from core, which
# Templating Engine 2.5.5 still needs (NoClassDefFoundError: StringEscapeUtils).
FROM jenkins/jenkins:2.580.2-lts-jdk21
USER root
RUN apt-get update && apt-get install -y --no-install-recommends python3 python3-venv python3-pip \
    && rm -rf /var/lib/apt/lists/*
RUN curl -fsSL https://get.docker.com | sh
USER jenkins
