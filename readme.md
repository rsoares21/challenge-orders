

docker build -t postgres-orders .
docker run -d --name local-postgres-db -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=orderdb postgres-orders:latest

-------------------------------------------------------------------------------------------------------------------------------------------------------------------

docker-compose up --build