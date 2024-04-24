CREATE TABLE IF NOT EXISTS users (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS notes (
    id SERIAL PRIMARY KEY,
    userid INT,
    title VARCHAR(100),
    body TEXT,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT null,
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT null
--    FOREIGN KEY (userid) REFERENCES users(userid)
);