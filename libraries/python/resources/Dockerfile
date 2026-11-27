# Default image for Python apps that don't bring their own Dockerfile.
# prepare_dockerfile writes this out and build_image passes the build args.
# Dependencies are installed here, inside the image, from the same lockfile
# the pipeline's Build stage used: nothing is copied from the Jenkins
# workspace's .venv (build_image's .dockerignore excludes it).

ARG PYTHON_VERSION=3.12
FROM python:${PYTHON_VERSION}-slim

ARG PACKAGE_MANAGER=pip
ARG START_COMMAND
ARG PORT=8000

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1 \
    UV_PYTHON_DOWNLOADS=never \
    POETRY_VIRTUALENVS_IN_PROJECT=true \
    PIPENV_VENV_IN_PROJECT=1 \
    PATH=/app/.venv/bin:$PATH \
    PORT=${PORT} \
    START_COMMAND=${START_COMMAND}

WORKDIR /app
COPY . .

# Tool versions match install_dependencies' defaults.
RUN set -eu; \
    case "$PACKAGE_MANAGER" in \
      uv)     pip install uv==0.7.10 && uv sync --locked --no-dev ;; \
      poetry) pip install poetry==1.8.3 && poetry install --no-interaction --only main --no-root ;; \
      pipenv) pip install pipenv==2024.0.1 && \
              if [ -f Pipfile.lock ]; then pipenv sync; else pipenv install; fi ;; \
      pip)    python -m venv .venv && \
              if [ -f requirements.txt ]; then .venv/bin/pip install -r requirements.txt; \
              else .venv/bin/pip install .; fi ;; \
      none)   python -m venv .venv ;; \
      *)      echo "Unknown PACKAGE_MANAGER: $PACKAGE_MANAGER" >&2; exit 1 ;; \
    esac

RUN useradd --create-home --uid 10001 app && chown -R app:app /app
USER app

EXPOSE ${PORT}
# START_COMMAND is read when the container starts, so $PORT inside it works.
CMD ["sh", "-c", "exec $START_COMMAND"]
