# Use official PostgreSQL image from the Docker Hub
FROM postgres:15

# Set environment variables for PostgreSQL
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=orderdb

# Expose PostgreSQL port
EXPOSE 5432

# No additional commands needed, default entrypoint will start PostgreSQL
