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
# Gitleaks: release binary (needs 8.19+ for "gitleaks git"), checked against
# the release's checksums. Picks x64 or arm64 to match the host.
ARG GITLEAKS_VERSION=8.24.0
RUN set -eu; \
    case "$(dpkg --print-architecture)" in \
      amd64) arch=x64 ;; \
      arm64) arch=arm64 ;; \
      *) echo "No Gitleaks build for $(dpkg --print-architecture)" >&2; exit 1 ;; \
    esac; \
    base="https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}"; \
    file="gitleaks_${GITLEAKS_VERSION}_linux_${arch}.tar.gz"; \
    cd /tmp; \
    curl -fsSLO "${base}/${file}"; \
    curl -fsSL "${base}/gitleaks_${GITLEAKS_VERSION}_checksums.txt" | grep " ${file}\$" | sha256sum -c -; \
    tar -xzf "${file}" -C /usr/local/bin gitleaks; \
    rm -f "${file}"; \
    gitleaks version
USER jenkins
