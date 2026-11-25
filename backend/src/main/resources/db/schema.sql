create table if not exists stops (
  id varchar(80) primary key,
  name varchar(180) not null,
  latitude double precision not null,
  longitude double precision not null,
  mode varchar(30) not null
);
create table if not exists routes (
  id varchar(80) primary key,
  service_number varchar(50) not null,
  origin varchar(180) not null,
  destination varchar(180) not null,
  mode varchar(30) not null
);
create table if not exists knowledge_documents (
  id serial primary key,
  title varchar(200) not null,
  content text not null,
  embedding_key varchar(120)
);
