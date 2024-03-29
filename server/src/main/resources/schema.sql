DROP TABLE IF EXISTS users, requests, items, bookings, comments;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  description VARCHAR(512) NOT NULL,
  requester_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  available BOOLEAN NOT NULL,
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  request_id BIGINT REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  item_id BIGINT REFERENCES items(id) ON DELETE CASCADE NOT NULL,
  booker_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  start_booking TIMESTAMP WITHOUT TIME ZONE,
  end_booking TIMESTAMP WITHOUT TIME ZONE,
  status VARCHAR
);

CREATE TABLE IF NOT EXISTS comments(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  text VARCHAR(1000),
  author_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  item_id BIGINT REFERENCES items(id) ON DELETE CASCADE NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE
);
