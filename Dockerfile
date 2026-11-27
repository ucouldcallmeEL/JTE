# Pinned to LTS: weekly 2.590+ dropped commons-lang 2 from core, which
# Templating Engine 2.5.5 still needs (NoClassDefFoundError: StringEscapeUtils).
FROM jenkins/jenkins:2.580.2-lts-jdk21
USER root
RUN apt-get update && apt-get install -y --no-install-recommends python3 python3-venv python3-pip \
    && rm -rf /var/lib/apt/lists/*
RUN curl -fsSL https://get.docker.com | sh
RUN apt-get update && apt-get install -y --no-install-recommends wget gnupg \
    && wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | gpg --dearmor -o /usr/share/keyrings/trivy.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/trivy.gpg] https://aquasecurity.github.io/trivy-repo/deb generic main" > /etc/apt/sources.list.d/trivy.list \
    && apt-get update && apt-get install -y trivy \
    && rm -rf /var/lib/apt/lists/*
USER jenkins
